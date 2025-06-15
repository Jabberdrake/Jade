package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class SettlementTransferCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementTransferCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!player.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't transfer the settlement to yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + player.getName() + "</highlight>) is not a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole leaderRole = focus.getLeaderRole();
        focus.setPlayerRole(target.getUniqueId(), leaderRole);
        focus.setPlayerRole(player.getUniqueId(), focus.getRoleBelow(leaderRole));

        player.sendMessage(success("Transferred leadership of " + focus.getDisplayNameAsString() + "<normal> to <highlight>" + target.getName() + "</highlight>!"));
        focus.tell((Player) target, "You have been demoted to " + focus.getRoleBelow(leaderRole).getDisplayAsString() + "<normal>!");

        focus.broadcast("A high official has <highlight>transferred ownership</highlight> of this settlement: <gold>" + target.getName() + " </gold>is the new <highlight>leader</highlight>!");
        if (target.isOnline()) {
            focus.tell((Player) target, "You have been promoted to " + leaderRole.getDisplayAsString() + "<normal>! Congratulations!");
        }
        return Command.SINGLE_SUCCESS;
    }
}

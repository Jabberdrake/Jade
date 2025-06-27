package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementKickCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementKickCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());
        if (senderRole == null) {
            player.sendMessage(error("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderRole.canKick()) {
            player.sendMessage(error("You are not allowed to kick members from <highlight>" + focus.getDisplayName() + "<normal>!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't kick yourself!"));
            player.sendMessage(info("To leave the settlement, do <highlight>/settlement leave"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is not a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.getRoleFromMember(target.getUniqueId()).getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) has equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.removeMember(target.getUniqueId());

        focus.broadcast("<highlight>" + target.getName() + "</highlight> has been kicked from the settlement!");
        if (target.isOnline()) {
            ((Player) target).sendMessage(info("You have been kicked from " + focus.getDisplayName() + "<normal>!"));
        }
        return Command.SINGLE_SUCCESS;
    }
}

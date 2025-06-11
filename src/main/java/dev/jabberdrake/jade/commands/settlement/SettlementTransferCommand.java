package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SettlementTransferCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllPlayersInSettlement)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementTransferCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't transfer the settlement to yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player is not a member of your focus settlement!."));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole leaderRole = focus.getLeaderRole();
        focus.setPlayerRole(target.getUniqueId(), leaderRole);
        focus.setPlayerRole(player.getUniqueId(), focus.getRoleBelow(leaderRole));

        player.sendMessage(TextUtils.composeSuccessText("Transferred leadership of ")
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText(" to "))
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText("!"))
        );

        if (target.isOnline()) {
            target.sendMessage(TextUtils.composeSimpleInfoMessage("You are now the ")
                    .append(TextUtils.composeInfoHighlight("leader"))
                    .append(TextUtils.composeInfoText(" of "))
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeInfoText("!"))
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SettlementKickCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllPlayersInSettlement)
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
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderRole.canKick()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You do not have permission to kick members from ")
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        SettlementRole settlementRole = focus.getRoleFromMember(targetUUID);
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't kick yourself! To leave the settlement, do /settlement leave."));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player is not a member of your focus settlement."));
            return Command.SINGLE_SUCCESS;
        } else if (settlementRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player has a higher authority title than you!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.removeMember(targetUUID);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" has been kicked from "))
                .append(focus.getDisplayName())
                .append((TextUtils.composeSuccessHighlight("!")))
        );

        if (target.isOnline()) {
            target.sendMessage(TextUtils.composeSimpleInfoMessage("You have been kicked from ")
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeInfoText("!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.CharterTitle;
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

        if (!performBasicChecks(player, focus)) { return Command.SINGLE_SUCCESS; }

        CharterTitle senderTitle = focus.getTitleFromMember(player.getUniqueId());
        if (senderTitle == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find a matching title for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderTitle.canKick()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not have the permission to kick a member."));
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        CharterTitle charterTitle = focus.getTitleFromMember(targetUUID);
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You can't kick yourself! To leave the settlement, do /settlement leave."));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("The specified player is not a member of your focus settlement."));
            return Command.SINGLE_SUCCESS;
        } else if (charterTitle.getAuthority() >= senderTitle.getAuthority()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("The specified player has a higher authority title than you!."));
            return Command.SINGLE_SUCCESS;
        }

        JadePlayer jadeTarget = PlayerManager.asJadePlayer(targetUUID);
        jadeTarget.removeSettlement(focus);
        focus.removeMember(targetUUID);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" has been kicked from "))
                .append(focus.getDisplayName())
                .append((TextUtils.composeSuccessHighlight("!")))
        );

        target.sendMessage(TextUtils.composePlainInfoMessage("You have been kicked from ")
                .append(focus.getDisplayName())
                .append(TextUtils.composeInfoText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }

    public static boolean performBasicChecks(Player player, Settlement settlement) {

        if (settlement == null) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not focusing on any settlement."));
            return false;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }

        return true;
    }
}

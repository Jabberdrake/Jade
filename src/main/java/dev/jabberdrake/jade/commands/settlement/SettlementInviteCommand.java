package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.CharterTitle;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SettlementInviteCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonArgumentSuggestions::suggestAllOnlinePlayers)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementInviteCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();

        if (!performBasicChecks(player, focus)) { return Command.SINGLE_SUCCESS; }

        CharterTitle senderTitle = focus.getTitleFromMember(player.getUniqueId());
        if (senderTitle == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find a matching title for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderTitle.canInvite()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not have the permission to invite a player."));
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You can't invite yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("The specified player is already a member of your focus settlement!."));
            return Command.SINGLE_SUCCESS;
        }
        // TODO: Check if at max capacity if we decide to implement that

        RealmManager.registerInviteToSettlement(target, focus);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" has been invited to "))
                .append(focus.getDisplayName())
                .append((TextUtils.composeSuccessHighlight("!")))
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

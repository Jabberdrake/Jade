package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.SettlementRole;
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
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());
        if (senderRole == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderRole.canInvite()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You do not have permission to invite players to ")
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeSuccessText("!"))
            );
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't invite yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player is already a member of your focus settlement!."));
            return Command.SINGLE_SUCCESS;
        }
        // TODO: Check if at max capacity if we decide to implement that

        boolean successFlag = RealmManager.registerInviteToSettlement(target, focus);
        if (successFlag) {
            player.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessHighlight(targetName))
                    .append(TextUtils.composeSuccessText(" has been invited to "))
                    .append(focus.getDisplayName())
                    .append((TextUtils.composeSuccessHighlight("!")))
            );
        } else {
            player.sendMessage(TextUtils.composeErrorPrefix()
                    .append(TextUtils.composeErrorHighlight(targetName))
                    .append(TextUtils.composeErrorText(" has already been invited to another nation!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }
}

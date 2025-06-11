package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.NationCommand;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class NationJoinCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationJoinCommand::runCommandWithoutArgs)
                .then(Commands.argument("nation", StringArgumentType.string())
                        .suggests(CommonNationSuggestions::suggestAllNations)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationJoinCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (focus.isInNation()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This settlement is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation inviter = RealmManager.getWhoInvitedSettlement(focus);
        if (inviter == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You do not have any pending invites."));
            return Command.SINGLE_SUCCESS;
        }

        inviter.addSettlement(focus);
        focus.setNation(inviter);
        RealmManager.clearInviteToNation(focus);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("The settlement "))
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText(" is now a member of the nation "))
                .append(inviter.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (focus.isInNation()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This settlement is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        String inviterAsString = StringArgumentType.getString(context, "nation");
        Nation inviterArg = RealmManager.getNation(inviterAsString);

        Nation inviter = RealmManager.getWhoInvitedSettlement(focus);
        if (inviterArg == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a nation with that name."));
            return Command.SINGLE_SUCCESS;
        } else if (!inviter.equals(inviterArg)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You do not have a pending invite from ")
                    .append(TextUtils.composeErrorHighlight(inviterArg.getName()))
                    .append(TextUtils.composeErrorText("!"))
            );
        }

        inviter.addSettlement(focus);
        focus.setNation(inviter);
        RealmManager.clearInviteToNation(focus);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("The settlement "))
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText(" is now a member of the nation "))
                .append(inviter.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

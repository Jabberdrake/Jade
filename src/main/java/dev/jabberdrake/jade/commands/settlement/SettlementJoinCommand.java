package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementJoinCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementJoinCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementJoinCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement inviter = RealmManager.getWhoInvitedPlayer(player);
        if (inviter == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not have any pending invites."));
            return Command.SINGLE_SUCCESS;
        }

        inviter.addMember(player.getUniqueId(), inviter.getDefaultRole());
        PlayerManager.asJadePlayer(player.getUniqueId()).addSettlement(inviter);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("You are now a member of "))
                .append(inviter.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String settlementAsString = StringArgumentType.getString(context, "stmArg");
        Settlement stmArg = RealmManager.getSettlement(settlementAsString);
        Settlement stmInviter = RealmManager.getWhoInvitedPlayer(player);
        if (stmArg == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find a stmArg with that name."));
            return Command.SINGLE_SUCCESS;
        } else if (!stmArg.equals(stmInviter)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not have a pending invite from ")
                    .append(TextUtils.composeErrorHighlight(stmInviter.getName()))
                    .append(TextUtils.composeErrorText("!"))
            );
        }

        stmArg.addMember(player.getUniqueId(), stmArg.getDefaultRole());
        PlayerManager.asJadePlayer(player.getUniqueId()).addSettlement(stmArg);
        RealmManager.clearInviteToSettlement(player);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("You are now a member of "))
                .append(stmArg.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

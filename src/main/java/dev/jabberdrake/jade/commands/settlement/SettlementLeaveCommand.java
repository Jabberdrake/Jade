package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementLeaveCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementLeaveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementLeaveCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(TextUtils.composeSimpleInfoMessage("To avoid accidental usages of this command, you need to explicitly specify which settlement you want to leave."));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String settlementAsString = StringArgumentType.getString(context, "stmArg");
        Settlement settlement = RealmManager.getSettlement(settlementAsString);

        if (settlement == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a settlement with that name."));
            return Command.SINGLE_SUCCESS;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return Command.SINGLE_SUCCESS;
        } else if (settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't leave a settlement you own! If you want to disband the settlement, do ")
                    .append(TextUtils.composeErrorHighlight("/settlement disband <settlement>"))
            );
            return Command.SINGLE_SUCCESS;
        }

        settlement.removeMember(player.getUniqueId());

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("You have left ")
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!")));
        return Command.SINGLE_SUCCESS;
    }
}

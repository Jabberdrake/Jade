package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.commands.settlement.CommonSettlementSuggestions;
import dev.jabberdrake.jade.commands.settlement.SettlementLeaveCommand;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class NationLeaveCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationLeaveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationLeaveCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(TextUtils.composeSimpleInfoMessage("To avoid accidental usages of this command, you must explicitly state the settlement you want to leave its nation."));
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
        } else if (!settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not the leader of this settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation nation = settlement.getNation();
        if (nation == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This settlement is not part of a nation!"));
            return Command.SINGLE_SUCCESS;
        } else if (nation.getCapital().equals(settlement)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Capital settlements cannot leave their nations!"));

            player.sendMessage(TextUtils.composeSimpleInfoMessage("If you want to dissolve the nation, do ")
                    .append(TextUtils.composeErrorHighlight("/nation dissolve <capital_settlement>"))
            );
            player.sendMessage(TextUtils.composeSimpleInfoMessage("Alternatively, if you want to transfer leadership of the nation to another settlement, do ")
                    .append(TextUtils.composeInfoHighlight("/nation transfer <other_settlement>"))
            );
            return Command.SINGLE_SUCCESS;
        }

        settlement.getNation().removeSettlement(settlement);
        settlement.leaveNation();

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText(" has left the nation of "))
                .append(nation.getDisplayName())
                .append((TextUtils.composeSuccessText("!")))
        );
        return Command.SINGLE_SUCCESS;
    }
}

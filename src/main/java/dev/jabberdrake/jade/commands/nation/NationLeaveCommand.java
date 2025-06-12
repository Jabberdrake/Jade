package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.CommonSettlementSuggestions;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class NationLeaveCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationLeaveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
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
        String settlementArgument = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(settlementArgument);

        if (settlement == null) {
            player.sendMessage(error("Could not find a settlement named <highlight>" + settlementArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            player.sendMessage(error("You are not a member of <highlight>" + settlement.getName() + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not the leader of this settlement (<highlight>" + settlement.getName() + "</highlight>)!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation nation = settlement.getNation();
        if (nation == null) {
            player.sendMessage(error("This settlement (<highlight>" + settlement.getName() + "</highlight>) is not part of a nation!"));
            return Command.SINGLE_SUCCESS;
        } else if (nation.getCapital().equals(settlement)) {
            player.sendMessage(error("Capital settlements cannot leave their nations!"));

            player.sendMessage(info("If you want to dissolve the nation, do <highlight>/nation dissolve <i><capital_settlement></i>"));
            player.sendMessage(info("Alternatively, if you want to transfer leadership of the nation to another settlement, do <highlight>/nation transfer <i><other_settlement></i>"));
            return Command.SINGLE_SUCCESS;
        }

        settlement.getNation().removeSettlement(settlement);
        settlement.leaveNation();

        settlement.broadcast("We have left the nation of " + nation.getDisplayNameAsString() + " !");
        nation.broadcast("The settlement of " + settlement.getDisplayNameAsString() + " has left the nation!");
        return Command.SINGLE_SUCCESS;
    }
}

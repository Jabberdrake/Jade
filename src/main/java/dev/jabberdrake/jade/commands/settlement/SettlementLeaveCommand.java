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

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementLeaveCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementLeaveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementLeaveCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("To avoid accidental usages of this command, you need to explicitly specify which settlement you want to leave."));
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
        } else if (settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You can't leave a settlement you own!"));
            player.sendMessage(info("If you want to disband the settlement, do <highlight>/settlement disband <i><settlement>"));
            return Command.SINGLE_SUCCESS;
        }

        settlement.removeMember(player.getUniqueId());

        settlement.broadcast("<highlight>" + player.getName() + "</highlight> has left the settlement!");
        player.sendMessage(info("You have left the settlement of " + settlement.getDisplayNameAsString() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}

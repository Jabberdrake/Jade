package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementDisbandCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementDisbandCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementDisbandCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("To avoid accidental usages of this command, you need to explicitly specify which settlement you want to disband."));
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
        }

        SettlementRole role = settlement.getRoleFromMember(player.getUniqueId());
        if (role == null) {
            player.sendMessage(error("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!role.isLeader()) {
            player.sendMessage(error("You are not the leader of this settlement (<highlight>" + settlement.getName() + "</highlight>)!"));
            return Command.SINGLE_SUCCESS;
        }

        RealmManager.deleteSettlement(settlement);

        settlement.broadcast("A high official has <red>disbanded</red> the settlement. Goodbye...");
        Bukkit.broadcast(info("The settlement of " + settlement.getDisplayNameAsString() + "<normal> has been disbanded!"));
        return Command.SINGLE_SUCCESS;
    }
}

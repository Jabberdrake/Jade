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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class NationDissolveCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationDissolveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationDissolveCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("To avoid accidental usages of this command, you must explicitly state the capital settlement of the nation you wish to dissolve."));
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
        } else if (!nation.getCapital().equals(settlement)) {
            player.sendMessage(error("Only capital settlements can dissolve their nations!"));
            return Command.SINGLE_SUCCESS;
        }

        RealmManager.deleteNation(nation);

        nation.broadcast("A high official has <red>dissolved</red> the nation. Goodbye...");
        Bukkit.broadcast(info("The nation of " + nation.getDisplayNameAsString() + " has been dissolved!"));
        return Command.SINGLE_SUCCESS;
    }
}

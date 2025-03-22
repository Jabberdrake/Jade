package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;

import java.util.UUID;

public class SettlementListCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(SettlementListCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        int maxID = RealmManager.getSettlementCount();
        for (int i = 1; i <= maxID; i++) {
            Settlement settlement = RealmManager.getSettlement(i);
            context.getSource().getSender().sendPlainMessage("Found settlement with an ID of " + settlement.getId() + "!");
            context.getSource().getSender().sendPlainMessage("    Settlement name: " + settlement.getName());
            context.getSource().getSender().sendPlainMessage("    Settlement description: " + settlement.getDescription());
            for (UUID uuid : settlement.getPopulation().keySet()) {
                String citizenName = Bukkit.getOfflinePlayer(uuid).getName();
                context.getSource().getSender().sendPlainMessage("    Member: " + citizenName + " | Rank: " + settlement.getPopulation().get(uuid).getName());
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

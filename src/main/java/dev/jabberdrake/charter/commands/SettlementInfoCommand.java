package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SettlementInfoCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(SettlementInfoCommand::buildSettlementSuggestions)
                        .executes(SettlementInfoCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        String stmString = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(stmString);
        context.getSource().getSender().sendPlainMessage("Found settlement with an ID of " + settlement.getId() + "!");
        context.getSource().getSender().sendPlainMessage("    Settlement name: " + settlement.getName());
        context.getSource().getSender().sendPlainMessage("    Settlement description: " + settlement.getDescription());
        Charter.getPlugin(Charter.class).getLogger().info(settlement.getPopulation().toString());
        for (UUID uuid : settlement.getPopulation().keySet()) {
            String citizenName = Bukkit.getOfflinePlayer(uuid).getName();
            context.getSource().getSender().sendPlainMessage("    Member: " + citizenName + " | Rank: " + settlement.getPopulation().get(uuid).getName());
        }
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> buildSettlementSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final List<String> stmNames = new ArrayList<>();
        for (int i = 1; i <= RealmManager.getSettlementCount(); i++) {
            stmNames.add(RealmManager.getSettlement(i).getName());
        }

        stmNames.stream()
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

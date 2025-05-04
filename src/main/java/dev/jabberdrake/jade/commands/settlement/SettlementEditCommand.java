package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SettlementEditCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(SettlementEditCommand::buildSettlementSuggestions)
                        .then(Commands.argument("attribute", StringArgumentType.word())
                                .suggests(SettlementEditCommand::buildStmFieldSuggestions)
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(SettlementEditCommand::runCommand)
                                )
                        )
                )
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        String stmString = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(stmString);

        String attr = StringArgumentType.getString(context, "attribute");
        String value = StringArgumentType.getString(context, "value");

        switch (attr) {
            case "name":
                settlement.setName(value);
                context.getSource().getSender().sendPlainMessage("Settlement name updated!");
                break;
            case "style":
                settlement.setDisplayName(value);
                context.getSource().getSender().sendPlainMessage("Settlement decorated name updated!");
                break;
            case "description":
                settlement.setDescription(value);
                context.getSource().getSender().sendPlainMessage("Settlement description updated!");
                break;
            default:
                context.getSource().getSender().sendPlainMessage("Unknown settlement field!");
        }

        //RealmManager.storeSettlement(settlement);
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

    public static CompletableFuture<Suggestions> buildStmFieldSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest("name");
        builder.suggest("style");
        builder.suggest("description");
        return builder.buildFuture();
    }
}

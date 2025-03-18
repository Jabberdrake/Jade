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
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SettlementClaimCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(SettlementClaimCommand::buildSettlementSuggestions)
                        .executes(SettlementClaimCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player)) {
            Charter.getPlugin(Charter.class).getLogger().warning("[SettlementClaimCommand::runCommand] Only players can run this command!");
            return Command.SINGLE_SUCCESS;
        }

        Player player = (Player) context.getSource().getSender();
        Chunk currentChunk = player.getLocation().getChunk();
        String stmString = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(stmString);
        if (settlement == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find settlement with name \"" + stmString + "\"..."));
        }

        if (RealmManager.claimChunk(settlement, currentChunk)) {
            player.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You have successfully claimed this chunk for "))
                    .append(TextUtils.composeSuccessHighlight(settlement.getName()))
                    .append(TextUtils.composeSuccessText("!"))
            );
        } else {
            player.sendMessage(TextUtils.composePlainErrorMessage("This chunk is already claimed by " + RealmManager.getChunkOwner(currentChunk).getName() + "!"));
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

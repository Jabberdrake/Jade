package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class CommonTitleSuggestions {

    public static CompletableFuture<Suggestions> suggestOwnedTitles(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        PlayerManager.asJadePlayer(player.getUniqueId()).getOwnedTitles()
                .stream()
                .map(JadeTitle::getName)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAvailableTitles(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        PlayerManager.asJadePlayer(player.getUniqueId()).getAvailableTitles()
                .stream()
                .map(JadeTitle::getName)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

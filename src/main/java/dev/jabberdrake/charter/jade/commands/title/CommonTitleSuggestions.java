package dev.jabberdrake.charter.jade.commands.title;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.jade.titles.NamedTitle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class CommonTitleSuggestions {

    public static CompletableFuture<Suggestions> suggestOwnedTitles(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        PlayerManager.fetchProfile(player.getUniqueId()).getAvailableTitles()
                .stream()
                .filter(title -> title.getOwner().equals(player.getUniqueId()))
                .map(NamedTitle::getName)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAvailableTitles(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        PlayerManager.fetchProfile(player.getUniqueId()).getAvailableTitles()
                .stream()
                .map(NamedTitle::getName)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

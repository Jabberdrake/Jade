package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class CommonArgumentSuggestions {
    public static CompletableFuture<Suggestions> suggestAllOnlinePlayers(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName)
                .filter(playerName -> playerName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

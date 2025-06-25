package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.utils.JadeTextColor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
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

    public static CompletableFuture<Suggestions> suggestAllItemKeys(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        // Add all vanilla item keys to suggestions builder
        RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ITEM)
                .stream().filter(registryKey -> registryKey.getKey().getKey().startsWith(builder.getRemainingLowerCase()))
                .forEach(registryKey -> {
                    builder.suggest(registryKey.getKey().toString());
                });

        // Add all Jade item keys to suggestions builder
        //TODO: actually do this lmao

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestVanillaTextColors(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        JadeTextColor.VANILLA_NAMES
                .stream().filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()) || name.equals(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestExistingGravesForPlayer(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        PlayerManager.getAllGravesForPlayer(player.getUniqueId()).stream().map(Grave::getID).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAllGraves(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        PlayerManager.getAllGraves().stream().map(Grave::getID).forEach(builder::suggest);
        return builder.buildFuture();
    }
}

package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;

public class CommonNationSuggestions {

    public static CompletableFuture<Suggestions> suggestAllSettlementsInNation(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        focus.getNation().getMembers().stream().map(Settlement::getName)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAllNations(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        RealmManager.getAllNations().stream().map(Nation::getName)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

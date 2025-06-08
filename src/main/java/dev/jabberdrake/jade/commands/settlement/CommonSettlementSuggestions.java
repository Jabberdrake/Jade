package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommonSettlementSuggestions {

    public static CompletableFuture<Suggestions> buildSuggestionsForAllSettlements(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final List<String> stmNames = new ArrayList<>();

        RealmManager.getAllSettlements().forEach(settlement -> {
            stmNames.add(settlement.getName());
        });

        stmNames.stream()
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForSettlementsWithPlayer(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final List<String> stmNames = new ArrayList<>();
        Player player = (Player) context.getSource().getSender();

        RealmManager.getSettlementsForPlayer(player).forEach(settlement -> {
            stmNames.add(settlement.getName());
        });

        stmNames.stream()
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForAllPlayersInSettlement(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (focus.getRoleFromMember(player.getUniqueId()) == null) {
            return builder.buildFuture();
        }

        Set<UUID> memberSet = focus.getPopulationAsIDSet();
        memberSet.stream().map(uuid -> Bukkit.getPlayer(uuid).getName())
                .filter(playerName -> playerName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAllRolesInSettlement(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (focus.getRoleFromMember(player.getUniqueId()) == null) {
            return builder.buildFuture();
        }

        Set<String> roleNames = focus.getRoles().stream().map(SettlementRole::getName).collect(Collectors.toSet());
        roleNames.stream()
                .filter(roleName -> roleName.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

}

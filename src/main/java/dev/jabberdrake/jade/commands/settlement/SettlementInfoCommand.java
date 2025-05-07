package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SettlementInfoCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(SettlementInfoCommand::buildSettlementSuggestions)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementInfoCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        String stmString = StringArgumentType.getString(context, "settlement");
        Player player = (Player) context.getSource().getSender();
        Settlement settlement = RealmManager.getSettlement(stmString);
        if (settlement == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Couldn't find the specified settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composePlainInfoMessage("Information on settlement ").append(settlement.getDisplayName()));
        player.sendMessage(Component.text("    ").append(settlement.getDescription()));
        player.sendMessage(Component.text());
        player.sendMessage(Component.text("    Nation: ").color(TextUtils.LIGHT_BRASS)
                .append(Component.text(settlement.isInNation() ? settlement.getNation().getName() : "None").color(TextUtils.ZORBA)));
        player.sendMessage(Component.text("    Members:").color(TextUtils.LIGHT_BRASS));
        settlement.getPopulation().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEach(entry -> {
                    player.sendMessage(Component.text("    - ").color(TextUtils.LIGHT_BRASS)
                            .append(entry.getValue().getDisplayAsComponent())
                            .append(Component.space())
                            .append(Component.text(Bukkit.getPlayer(entry.getKey()).getName()).color(TextUtils.ZORBA))
                    );
                });
        if (settlement.getPopulation().size() > 10) {
            player.sendMessage(Component.text("    - ").color(TextUtils.BRASS)
                .append(Component.text("...").color(TextUtils.ZORBA))
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> buildSettlementSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {

        RealmManager.getAllSettlements().stream()
                .filter(entry -> entry.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(entry -> builder.suggest(entry.getName()));

        return builder.buildFuture();
    }
}

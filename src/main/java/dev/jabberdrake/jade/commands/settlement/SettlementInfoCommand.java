package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SettlementInfoCommand {

    private static final String INDENT = "       ";

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
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Couldn't find the specified settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composeSimpleInfoMessage("Settlement info:"));
        player.sendMessage(Component.text(INDENT).append(settlement.asTextComponent()));
        player.sendMessage(Component.text(INDENT).append(settlement.getDescription()));
        player.sendMessage(Component.text()); //evil \n

        player.sendMessage(Component.text(INDENT + "Food: ", TextUtils.LIGHT_BRASS)
                .append(Component.text(settlement.getFood() + "/" + settlement.getFoodCapacity(), TextUtils.LIVINGMETAL)));
        if (settlement.isInNation()) {
            player.sendMessage(Component.text(INDENT + "Nation: ", TextUtils.LIGHT_BRASS)
                    .append(settlement.getNation().getDisplayName()));
        } else {
            player.sendMessage(Component.text(INDENT + "Nation: ", TextUtils.LIGHT_BRASS)
                    .append(Component.text("None", TextUtils.ZORBA)));
        }

        player.sendMessage(Component.text(INDENT + "Members:", TextUtils.LIGHT_BRASS));
        settlement.getPopulation().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEach(entry -> {
                    player.sendMessage(Component.text(INDENT + "- ", TextUtils.LIGHT_BRASS)
                            .append(entry.getValue().getDisplayAsComponent())
                            .append(Component.space())
                            .append(Component.text(Bukkit.getPlayer(entry.getKey()).getName(), TextUtils.LIGHT_ZORBA))
                    );
                });
        if (settlement.getPopulation().size() > 10) {
            player.sendMessage(Component.text(INDENT + "- ", TextUtils.LIGHT_BRASS)
                .append(Component.text("...").color(TextUtils.ZORBA))
            );
        }
        player.sendMessage(Component.text()); //evil \n
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> buildSettlementSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {

        RealmManager.getAllSettlements().stream()
                .filter(entry -> entry.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(entry -> builder.suggest(entry.getName()));

        return builder.buildFuture();
    }
}

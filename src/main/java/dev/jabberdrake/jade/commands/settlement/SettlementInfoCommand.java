package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Map;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementInfoCommand {

    private static final String INDENT = "       ";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementInfoCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        String settlementArgument = StringArgumentType.getString(context, "settlement");
        Player player = (Player) context.getSource().getSender();
        Settlement settlement = RealmManager.getSettlement(settlementArgument);
        if (settlement == null) {
            player.sendMessage(error("Could not find a settlement named <highlight>" + settlementArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(info("Settlement info:"));
        player.sendMessage(Component.text(INDENT).append(settlement.asTextComponent()));
        player.sendMessage(Component.text(INDENT).append(settlement.getDescriptionAsComponent()));
        player.sendMessage(Component.text()); //evil \n

        player.sendMessage(Component.text(INDENT + "Food: ", TextUtils.LIGHT_BRASS)
                .append(Component.text(settlement.getFood() + "/" + settlement.getFoodCapacity(), TextUtils.LIVINGMETAL)));
        if (settlement.isInNation()) {
            player.sendMessage(Component.text(INDENT + "Nation: ", TextUtils.LIGHT_BRASS)
                    .append(settlement.getNation().getDisplayNameAsComponent()));
        } else {
            player.sendMessage(Component.text(INDENT + "Nation: ", TextUtils.LIGHT_BRASS)
                    .append(Component.text("None", TextUtils.DARK_ZORBA)));
        }

        player.sendMessage(Component.text(INDENT + "Members:", TextUtils.LIGHT_BRASS));
        settlement.getPopulation().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEach(entry -> {
                    player.sendMessage(Component.text(INDENT + "— ", TextUtils.LIGHT_BRASS)
                            .append(entry.getValue().getDisplayAsComponent())
                            .append(Component.space())
                            .append(Component.text(Bukkit.getOfflinePlayer(entry.getKey()).getName(), TextUtils.LIGHT_ZORBA))
                    );
                });
        if (settlement.getPopulation().size() > 10) {
            player.sendMessage(Component.text(INDENT + "— ", TextUtils.LIGHT_BRASS)
                .append(Component.text("...").color(TextUtils.ZORBA))
            );
        }
        player.sendMessage(Component.text()); //evil \n
        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class NationInfoCommand {

    private static final String INDENT = "       ";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("nation", StringArgumentType.word())
                        .suggests(CommonNationSuggestions::suggestAllNations)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationInfoCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        String nationArgument = StringArgumentType.getString(context, "nation");
        Player player = (Player) context.getSource().getSender();
        Nation nation = RealmManager.getNation(nationArgument);
        if (nation == null) {
            player.sendMessage(error("Could not find a nation named <highlight>" + nationArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        // TODO: Replace this with calls to the new text formatters
        player.sendMessage(TextUtils.composeSimpleInfoMessage("Nation info:"));
        player.sendMessage(Component.text(INDENT).append(nation.asTextComponent()));
        player.sendMessage(Component.text(INDENT).append(nation.getDescriptionAsComponent()));
        player.sendMessage(Component.text()); //evil \n

        player.sendMessage(Component.text(INDENT + "Capital: ", TextUtils.LIGHT_BRASS)
                .append(nation.getCapital().asTextComponent()));

        player.sendMessage(Component.text(INDENT + "Members:", TextUtils.LIGHT_BRASS));
        nation.getMembers().stream()
                .limit(10)
                .forEach(entry -> {
                    player.sendMessage(Component.text(INDENT + "— ", TextUtils.LIGHT_BRASS)
                            .append(entry.asTextComponent())
                    );
                });
        if (nation.getMembers().size() > 10) {
            player.sendMessage(Component.text(INDENT + "— ", TextUtils.LIGHT_BRASS)
                    .append(Component.text("...").color(TextUtils.ZORBA))
            );
        }
        player.sendMessage(Component.text()); //evil \n
        return Command.SINGLE_SUCCESS;
    }
}

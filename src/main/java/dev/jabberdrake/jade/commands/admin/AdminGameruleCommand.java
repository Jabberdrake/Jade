package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class AdminGameruleCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("gamerule", StringArgumentType.word())
                        .suggests(AdminGameruleCommand::suggestJadeGamerules)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(AdminGameruleCommand::runCommand)
                        )
                )
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String gameruleArg = StringArgumentType.getString(context, "gamerule");
        Boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeSettings.setGamerule(gameruleArg, valueArg);
        if (result == true) {
            player.sendMessage(TextUtils.composeSimpleSuccessMessage("Gamerule ")
                    .append(TextUtils.composeSuccessHighlight(gameruleArg))
                    .append(TextUtils.composeSuccessText(" is now set to "))
                    .append(TextUtils.composeSuccessHighlight(valueArg.toString()).decorate(TextDecoration.ITALIC))
            );
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("That gamerule does not exist!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> suggestJadeGamerules(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest("preventCoralFading");

        return builder.buildFuture();
    }

}

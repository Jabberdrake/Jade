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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.concurrent.CompletableFuture;

public class AdminGameruleCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.literal("preventCoralFading")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(AdminGameruleCommand::runCommandForCoralFading)
                        )
                )
                .build();
    }

    public static int runCommandForCoralFading(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        Boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeSettings.setGamerule("preventCoralFading", valueArg);
        if (result == true) {
            Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("An administrator has set gamerule ")
                    .append(TextUtils.composeOperatorHighlight("preventCoralFading"))
                    .append(TextUtils.composeOperatorText(" to "))
                    .append(TextUtils.composeSuccessHighlight(valueArg.toString().toUpperCase()).decorate(TextDecoration.ITALIC))
            );
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

}

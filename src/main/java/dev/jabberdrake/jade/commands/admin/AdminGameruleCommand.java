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
                                .suggests(AdminGameruleCommand::suggestRealmProtectionLevels)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(AdminGameruleCommand::runCommandForCoralFading)
                        )
                )
                .then(Commands.literal("realmProtectionLevel")
                        .then(Commands.argument("level", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(AdminGameruleCommand::runCommandForRealmProtectionLevel)
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

    public static int runCommandForRealmProtectionLevel(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String levelArg = StringArgumentType.getString(context, "level");

        boolean result = JadeSettings.setGamerule("preventCoralFading", levelArg);
        if (result == true) {
            Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("An administrator has set gamerule ")
                    .append(TextUtils.composeOperatorHighlight("realmProtectionLevel"))
                    .append(TextUtils.composeOperatorText(" to "))
                    .append(TextUtils.composeSuccessHighlight(levelArg).decorate(TextDecoration.ITALIC))
            );
            switch (levelArg) {
                case JadeSettings.NONE -> Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("Realm protection is now disabled."));
                case JadeSettings.CONTAINER -> Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("Block protection in claimed chunks is now restricted to container blocks."));
                case JadeSettings.NORMAL -> Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("Block protection in claimed chunks now applies to all blocks."));
                default -> player.sendMessage(TextUtils.composeSimpleErrorMessage("Unrecognized realm protection level! Please report this to a developer!"));
            }
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("An error occurred while altering gamerules! Please report this to the developer!"));
        }

        return Command.SINGLE_SUCCESS;
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

    public static CompletableFuture<Suggestions> suggestRealmProtectionLevels(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest(JadeSettings.NONE);
        builder.suggest(JadeSettings.CONTAINER);
        builder.suggest(JadeSettings.NORMAL);

        return builder.buildFuture();
    }

}

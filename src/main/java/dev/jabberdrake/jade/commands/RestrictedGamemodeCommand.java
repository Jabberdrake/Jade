package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class RestrictedGamemodeCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("gamemode", StringArgumentType.word())
                        .suggests(RestrictedGamemodeCommand::suggestAllGamemodes)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .requires(sender -> sender.getSender().hasPermission("jade.gamemode"))
                        .executes(RestrictedGamemodeCommand::runCommand))
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> buildShorthandForSurvival(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .requires(sender -> sender.getSender().hasPermission("jade.gamemode"))
                .executes(RestrictedGamemodeCommand::runShortcutForSurvival)
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> buildShorthandForCreative(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .requires(sender -> sender.getSender().hasPermission("jade.gamemode"))
                .executes(RestrictedGamemodeCommand::runShortcutForCreative)
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> buildShorthandForAdventure(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .requires(sender -> sender.getSender().hasPermission("jade.gamemode"))
                .executes(RestrictedGamemodeCommand::runShortcutForAdventure)
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> buildShorthandForSpectator(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .requires(sender -> sender.getSender().hasPermission("jade.gamemode"))
                .executes(RestrictedGamemodeCommand::runShortcutForSpectator)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String gamemodeArgument = StringArgumentType.getString(context, "gamemode");
        switch (gamemodeArgument) {
            case "survival":
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.sendMessage(error("You are already in <highlight>survival mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(info("You are now in <highlight>Survival Mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                }
            case "creative":
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(error("You are already in <highlight>creative mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                } else {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(info("You are now in <highlight>Creative Mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                }
            case "adventure":
                if (player.getGameMode() == GameMode.ADVENTURE) {
                    player.sendMessage(error("You are already in <highlight>adventure mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                } else {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(info("You are now in <highlight>Adventure Mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                }
            case "spectator":
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    player.sendMessage(error("You are already in <highlight>spectator mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(info("You are now in <highlight>Spectator Mode</highlight>!"));
                    return Command.SINGLE_SUCCESS;
                }
            default:
                player.sendMessage(error("Could not find a valid gamemode named <highlight>" + gamemodeArgument + "</highlight>!"));
                return Command.SINGLE_SUCCESS;
        }
    }

    public static int runShortcutForSurvival(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.sendMessage(error("You are already in <highlight>survival mode</highlight>!"));
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(info("You are now in <highlight>Survival Mode</highlight>!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runShortcutForCreative(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(error("You are already in <highlight>creative mode</highlight>!"));
        } else {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(info("You are now in <highlight>Creative Mode</highlight>!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runShortcutForAdventure(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            player.sendMessage(error("You are already in <highlight>adventure mode</highlight>!"));
        } else {
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(info("You are now in <highlight>Adventure Mode</highlight>!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runShortcutForSpectator(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(error("You are already in <highlight>spectator mode</highlight>!"));
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(info("You are now in <highlight>Spectator Mode</highlight>!"));
        }
        return Command.SINGLE_SUCCESS;
    }


    public static CompletableFuture<Suggestions> suggestAllGamemodes(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest("survival");
        builder.suggest("creative");
        builder.suggest("adventure");
        builder.suggest("spectator");
        return builder.buildFuture();
    }
}

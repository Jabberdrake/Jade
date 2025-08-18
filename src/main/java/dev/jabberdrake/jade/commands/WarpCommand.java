package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.building.BuildingManager;
import dev.jabberdrake.jade.building.Warp;
import dev.jabberdrake.jade.commands.title.TitleEditCommand;
import dev.jabberdrake.jade.menus.implementations.GraveListMenu;
import dev.jabberdrake.jade.menus.implementations.WarpListMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

import java.io.IOException;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class WarpCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(WarpCommand::runCommandForList)
                .then(Commands.literal("list")
                        .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.list"))
                        .executes(WarpCommand::runCommandForList)
                )
                .then(Commands.literal("tp")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.teleport"))
                                .suggests(CommonArgumentSuggestions::suggestAllWarps)
                                .executes(WarpCommand::runCommandForTeleport)
                        )
                )
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.create"))
                                .executes(WarpCommand::runCommandForCreate)
                        )
                )
                .then(Commands.literal("edit")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.edit"))
                                .suggests(CommonArgumentSuggestions::suggestAllWarps)
                                .then(Commands.literal("description")
                                        .then(Commands.argument("description", StringArgumentType.greedyString())
                                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.edit"))
                                                .executes(WarpCommand::runCommandForDescription)
                                        )
                                )
                                .then(Commands.literal("icon")
                                        .then(Commands.argument("icon", ArgumentTypes.namespacedKey())
                                                .suggests(CommonArgumentSuggestions::suggestAllItemKeys)
                                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.edit"))
                                                .executes(WarpCommand::runCommandForIcon)
                                        )
                                )
                        )
                )
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player && sender.getSender().hasPermission("jade.warps.delete"))
                                .suggests(CommonArgumentSuggestions::suggestAllWarps)
                                .executes(WarpCommand::runCommandForDelete)
                        )
                )
                .build();
    }

    public static int runCommandForList(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        new WarpListMenu().open(player);
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTeleport(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String warpArgument = StringArgumentType.getString(context, "name");
        Warp warp = BuildingManager.getWarp(warpArgument);

        if (warp == null) {
            player.sendMessage(error("Could not find a warp with the name of <highlight>" + warpArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        player.teleportAsync(warp.getLocation());
        player.sendMessage(info("Warped to <coral>" + warp.getName() + "<normal>!"));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForCreate(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String warpArgument = StringArgumentType.getString(context, "name");
        if (warpArgument.equalsIgnoreCase("warp") || warpArgument.equalsIgnoreCase("warps")) {
            player.sendMessage(error("Cannot create a warp with that name!"));
            return Command.SINGLE_SUCCESS;
        }

        Warp warp = BuildingManager.getWarp(warpArgument);
        if (!(warp == null)) {
            player.sendMessage(error("That name (<highlight>" + warpArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        Warp newWarp = BuildingManager.createWarp(warpArgument, player);
        player.sendMessage(info("Created the warp of <coral>" + newWarp.getName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDescription(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String warpArgument = StringArgumentType.getString(context, "name");
        Warp warp = BuildingManager.getWarp(warpArgument);

        if (warp == null) {
            player.sendMessage(error("Could not find a warp with the name of <highlight>" + warpArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        String descArgument = StringArgumentType.getString(context, "description");
        if (!descArgument.startsWith("<")) {
            descArgument = "<white>" + descArgument;
        }

        warp.setDescription(descArgument);
        try {
            warp.store();
        } catch (IOException e) {
            player.sendMessage(error("An unexpected error occurred while saving your edits. Please report this to a developer!"));
            Jade.getInstance().getLogger().warning("[WarpCommand::runCommandForDescription] Caught IOException while saving warp " + warp.getName() + ":");
            e.printStackTrace();
        }
        player.sendMessage(success("Changed <highlight>description</highlight> of warp <coral>" + warp.getName() + "<normal> to: " + warp.getDescriptionAsString()));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIcon(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String warpArgument = StringArgumentType.getString(context, "name");
        Warp warp = BuildingManager.getWarp(warpArgument);

        if (warp == null) {
            player.sendMessage(error("Could not find a warp with the name of <highlight>" + warpArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        NamespacedKey iconArgument = context.getArgument("icon", NamespacedKey.class);
        switch (iconArgument.getNamespace()) {
            case "minecraft":
                ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(iconArgument);
                if (dummy == null) {
                    player.sendMessage(error("Please provide a valid item key!"));
                    return Command.SINGLE_SUCCESS;
                }
                break;
            case "jade":
                // TODO: implement this
                player.sendMessage(error("This feature has not been implemented yet!"));
                return Command.SINGLE_SUCCESS;
            default:
                player.sendMessage(error("Please provide a valid namespace!"));
                return Command.SINGLE_SUCCESS;
        }

        if (iconArgument.asString().equals(warp.getIconAsString())) {
            player.sendMessage(error("This warp already has that icon!"));
            return Command.SINGLE_SUCCESS;
        }

        warp.setIcon(iconArgument);
        try {
            warp.store();
        } catch (IOException e) {
            player.sendMessage(error("An unexpected error occurred while saving your edits. Please report this to a developer!"));
            Jade.getInstance().getLogger().warning("[WarpCommand::runCommandForIcon] Caught IOException while saving warp " + warp.getName() + ":");
            e.printStackTrace();
        }
        player.sendMessage(success("Changed the <highlight>icon</highlight> of warp <coral>" + warp.getName() + "<normal> to <highlight>" + warp.getIconAsString() + "</highlight>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDelete(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String warpArgument = StringArgumentType.getString(context, "name");
        Warp warp = BuildingManager.getWarp(warpArgument);

        if (warp == null) {
            player.sendMessage(error("Could not find a warp with the name of <highlight>" + warpArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        BuildingManager.deleteWarp(warp);
        Bukkit.broadcast(info("The warp of <coral>" + warp.getName() + "<normal> has been <red>deleted</red>!"), "jade.warps.teleport");
        return Command.SINGLE_SUCCESS;
    }
}

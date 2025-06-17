package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.menus.implementations.AreasListMenu;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class SettlementAreasCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementAreasCommand::runCommandForList)
                .then(Commands.literal("wand")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementAreasCommand::runCommandForWand)
                )
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementAreasCommand::runCommandForCreate))
                )
                .then(Commands.literal("edit")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .then(Commands.literal("name")
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementAreasCommand::runCommandForName)
                                        )
                                )
                                .then(Commands.literal("display")
                                        .then(Commands.argument("display_name", StringArgumentType.greedyString())
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementAreasCommand::runCommandForDisplay)
                                        )
                                )
                                .then(Commands.literal("icon")
                                        .then(Commands.argument("icon", ArgumentTypes.namespacedKey())
                                                .suggests(CommonArgumentSuggestions::suggestAllItemKeys)
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementAreasCommand::runCommandForIcon)
                                        )
                                )
                                .then(Commands.literal("pos1")
                                        .then(Commands.argument("pos1", ArgumentTypes.blockPosition())
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementAreasCommand::runCommandForPos1)
                                        )
                                )
                                .then(Commands.literal("pos2")
                                        .then(Commands.argument("pos2", ArgumentTypes.blockPosition())
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementAreasCommand::runCommandForPos2)
                                        )
                                )
                        )
                )
                .then(Commands.literal("trust")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementAreasCommand::runCommandForTrust)
                                )
                        )
                )
                .then(Commands.literal("untrust")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementAreasCommand::runCommandForUntrust)
                                )
                        )
                )
                .then(Commands.literal("list")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementAreasCommand::runCommandForList)
                )
                .then(Commands.literal("viewall")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementAreasCommand::runCommandForViewAll)
                )
                .then(Commands.literal("view")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementAreasCommand::runCommandForView)
                        )
                )
                .then(Commands.literal("transfer")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementAreasCommand::runCommandForTransfer)
                                )
                        )
                )
                .then(Commands.literal("delete")
                        .then(Commands.argument("area", StringArgumentType.word())
                                .suggests(CommonSettlementSuggestions::suggestAllAreasInSettlement)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementAreasCommand::runCommandForDelete)
                        )
                )
                .build();
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        return SettlementCommand.validateFocusLeadership(player, settlement);
    }

    public static int runCommandForWand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        //ItemStack wand = ItemUtils.asDisplayItem(NamespacedKey.minecraft("golden_hoe"));
        player.sendMessage(error("Not implemented yet!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForCreate(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!focus.isUniqueAreaName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use by another area!"));
            return Command.SINGLE_SUCCESS;
        }

        String treatedName = nameArgument.substring(0, 1).toUpperCase() + nameArgument.substring(1);

        Area newArea = new Area(treatedName, focus, player);
        focus.addArea(newArea);

        focus.broadcast( "<highlight>" + player.getName() + "</highlight> has created a <highlight>new area</highlight> named " + newArea.getDisplayName() + "<normal>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForName(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to edit attributes for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!focus.isUniqueAreaName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use by another area!"));
            return Command.SINGLE_SUCCESS;
        }

        String treatedName = nameArgument.substring(0, 1).toUpperCase() + nameArgument.substring(1);

        String oldDisplay = area.asDisplayString();
        area.setName(treatedName);
        focus.broadcast("<gold>" + player.getName() + "</gold> has changed the <highlight>reference name</highlight> of the area <highlight>" + oldDisplay + "<normal> to <highlight><i>" + area.getName() + "</i></highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDisplay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to edit attributes for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        }

        String displayArgument = StringArgumentType.getString(context, "display_name");

        String oldDisplay = area.asDisplayString();
        area.setDisplayName(displayArgument);
        focus.broadcast("<gold>" + player.getName() + "</gold> has changed the <highlight>display name</highlight> of the area <highlight>" + oldDisplay + "<normal> to <highlight>" + area.getDisplayName() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIcon(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.getHolderUniqueID().equals(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to edit attributes for the <highlight>" + areaArgument + "</highlight> area!"));
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

        area.setIcon(iconArgument);
        focus.broadcast("<gold>" + player.getName() + "</gold> has changed the <highlight>icon</highlight> of the area <highlight>" + area.asDisplayString() + "<normal> to <highlight>" + area.getIconAsString() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForPos1(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to edit attributes for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        } else if (!player.getWorld().equals(focus.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + focus.getName() + "</highlight>) cannot hold areas in the world you're currently in!"));
            return Command.SINGLE_SUCCESS;
        }

        try {
            final BlockPositionResolver blockPositionResolver = context.getArgument("pos1", BlockPositionResolver.class);
            final BlockPosition blockPosition = blockPositionResolver.resolve(context.getSource());

            if (!focus.isInTerritory(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ())) {
                player.sendMessage(error("This position (<highlight>" + blockPosition.blockX() + "," + blockPosition.blockY() + "," + blockPosition.blockZ() + "</highlight>) is outside the bounds of this settlement (<highlight>" + focus.getName() + "</highlight>)!"));
                return Command.SINGLE_SUCCESS;
            }

            area.setPos1(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ());

        } catch (CommandSyntaxException e) {
            player.sendMessage(error("Could not parse that position!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.tell(player, "Changed the <highlight>pos1</highlight> attribute of the area <highlight>" + area.asDisplayString() + "<normal> to <highlight>" + area.displayPos1() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForPos2(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to edit attributes for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        } else if (!player.getWorld().equals(focus.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + focus.getName() + "</highlight>) cannot hold areas in the world you're currently in!"));
            return Command.SINGLE_SUCCESS;
        }

        try {
            final BlockPositionResolver blockPositionResolver = context.getArgument("pos2", BlockPositionResolver.class);
            final BlockPosition blockPosition = blockPositionResolver.resolve(context.getSource());

            if (!focus.isInTerritory(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ())) {
                player.sendMessage(error("This position (<highlight>" + blockPosition.blockX() + "," + blockPosition.blockY() + "," + blockPosition.blockZ() + "</highlight>) is outside the bounds of this settlement (<highlight>" + focus.getName() + "</highlight>)!"));
                return Command.SINGLE_SUCCESS;
            }

            area.setPos2(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ());

        } catch (CommandSyntaxException e) {
            player.sendMessage(error("Could not parse that position!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.tell(player, "Changed the <highlight>pos2</highlight> attribute of the area <highlight>" + area.asDisplayString() + "<normal> to <highlight>" + area.displayPos2() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForList(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        new AreasListMenu(focus).open(player);
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForViewAll(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (!player.getWorld().equals(focus.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + focus.getName() + "</highlight>) does not hold any areas in the world you're currently in!"));
            return Command.SINGLE_SUCCESS;
        }

        boolean result = jadePlayer.toggleAreaView(focus);
        if (result) {
            player.sendMessage(info("You are now viewing all nearby areas in <highlight>" + focus.getDisplayName() + "<normal>!"));
        } else {
            player.sendMessage(info("You are no longer viewing nearby areas in <highlight>" + focus.getDisplayName() + "<normal>!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForView(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (!player.getWorld().equals(focus.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + focus.getName() + "</highlight>) does not hold any areas in the world you're currently in!"));
            return Command.SINGLE_SUCCESS;
        }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        boolean result = jadePlayer.toggleAreaView(area);
        if (result) {
            player.sendMessage(info("You are now viewing borders for the <highlight>" + area.getDisplayName() + "<normal> area!"));
        } else {
            player.sendMessage(info("You are no longer viewing the <highlight>" + area.getDisplayName() + "<normal> area!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTrust(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to manage members for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't trust yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (area.isMember(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + targetArgument + "</highlight>) is already trusted in the <highlight>" + area.getName() + "</highlight> area)!"));
            return Command.SINGLE_SUCCESS;
        }

        area.addMember(target.getUniqueId());

        focus.tell(player, "<highlight>" + target.getName() + "</highlight> is now trusted in the <highlight>" + area.getDisplayName() + "<normal> area!");
        if (target.isOnline()) {
            focus.tell((Player) target, "You are now trusted in the <highlight>" + area.getDisplayName() + "<normal> area!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForUntrust(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId()) && !focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not allowed to manage members for the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't untrust yourself!"));
            player.sendMessage(info("To delete the area, do <highlight>/settlement areas delete <i><area>"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isMember(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + targetArgument + "</highlight>) is already not trusted in the <highlight>" + area.getName() + "</highlight> area)!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.getRoleFromMember(target.getUniqueId()).isLeader()) {
            player.sendMessage(error("This player (<highlight>" + targetArgument + "</highlight>) is the <highlight>leader</highlight>, and cannot be removed from any area!"));
            return Command.SINGLE_SUCCESS;
        }

        area.removeMember(target.getUniqueId());

        focus.tell(player, "<highlight>" + target.getName() + "</highlight> is no longer trusted in the <highlight>" + area.getDisplayName() + "<normal> area!");
        if (target.isOnline()) {
            focus.tell((Player) target, "You are no longer trusted in the <highlight>" + area.getDisplayName() + "<normal> area!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDelete(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("Only the <highlight>leader</highlight> is allowed to delete areas!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.removeArea(area);

        focus.broadcast("A high official has <red>deleted</red> the <highlight>" + area.getDisplayName() + "<normal> area!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTransfer(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        Settlement focus = jadePlayer.getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        String areaArgument = StringArgumentType.getString(context, "area");
        Area area = focus.getAreaByName(areaArgument);
        if (area == null) {
            player.sendMessage(error("Could not find an area named <highlight>" + areaArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isHolder(player.getUniqueId())) {
            player.sendMessage(error("You are not the rightful holder of the <highlight>" + areaArgument + "</highlight> area!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't transfer an area to yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!area.isMember(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + targetArgument + "</highlight>) is not a member of the <highlight>" + area.getName() + "</highlight> area)!"));
            return Command.SINGLE_SUCCESS;
        }

        area.setHolder(target.getUniqueId());

        focus.broadcast("<gold>" + player.getName() + " has <highlight>transferred holdership</highlight> of the <highlight>" + area.getDisplayName() + "<normal> area to <gold>" + target.getName() + " </gold>!");
        if (target.isOnline()) {
            focus.tell((Player) target, "You are now the <highlight>holder</highlight> of the " + area.getDisplayName() + "<normal> area! Congratulations!");
        }
        return Command.SINGLE_SUCCESS;
    }
}

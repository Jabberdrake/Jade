package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.menus.implementations.SettlementManageMenu;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class SettlementManageCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementManageCommand::runCommandWithoutArgs)
                .then(Commands.argument("role", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllRolesInSettlement)
                        .then(Commands.literal("create")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementManageCommand::runCommandForCreate)
                        )
                        .then(Commands.literal("authority")
                                .then(Commands.literal("increase")
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementManageCommand::runCommandForAuthorityIncrease)
                                )
                                .then(Commands.literal("decrease")
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementManageCommand::runCommandForAuthorityDecrease)
                                )
                        )
                        .then(Commands.literal("name")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementManageCommand::runCommandForNameChange)
                                )
                        )
                        .then(Commands.literal("color")
                                .then(Commands.argument("named_color", StringArgumentType.word())
                                        .suggests(CommonArgumentSuggestions::suggestVanillaTextColors)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementManageCommand::runCommandForColorChange)
                                )
                        )
                        .then(Commands.literal("icon")
                                .then(Commands.argument("icon", ArgumentTypes.namespacedKey())
                                        .suggests(SettlementManageCommand::suggestRoleIcons)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(SettlementManageCommand::runCommandForIconChange)
                                )
                        )
                        .then(Commands.literal("permissions")
                                .then(Commands.argument("permission_node", StringArgumentType.word())
                                        .suggests(SettlementManageCommand::suggestRolePermissionNodes)
                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .requires(sender -> sender.getExecutor() instanceof Player)
                                                .executes(SettlementManageCommand::runCommandForPermissionChange)
                                        )
                                )
                        )
                        .then(Commands.literal("makedefault")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementManageCommand::runCommandForMakeDefault)
                        )
                        .then(Commands.literal("delete")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementManageCommand::runCommandForDelete)
                        )
                )
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        new SettlementManageMenu(focus).open(player);

        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canManage()) {
            player.sendMessage(error("You are not allowed to manage roles for <highlight>" + settlement.getDisplayName() + "<normal>!"));
            return false;
        }
        return true;
    }

    public static boolean validateRoleArgument(String roleArgument, Player player, Settlement settlement) {
        if (settlement.getRoleFromName(roleArgument) == null) {
            player.sendMessage(error("Could not find a role named <highlight>" + roleArgument + "</highlight> in " + settlement.getDisplayName() + "<normal>!"));
            return false;
        }
        return true;
    }


    public static boolean validateAuthorityDynamic(Player player, Settlement settlement, SettlementRole targetRole) {
        return validateAuthorityDynamic(player, settlement, targetRole, 0);
    }

    public static boolean validateAuthorityDynamic(Player player, Settlement settlement, SettlementRole targetRole, int authorityOffset) {
        SettlementRole senderRole = settlement.getRoleFromMember(player.getUniqueId());
        if (senderRole.isLeader()) return true;
        if (senderRole.getAuthority() <= targetRole.getAuthority() + authorityOffset) {
            player.sendMessage(error("You don't have enough authority to manage that role!"));
            return false;
        }
        return true;
    }

    public static int runCommandForCreate(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (!focus.isUniqueRoleName(roleArgument)) {
            player.sendMessage(error("That name (<highlight>" + roleArgument + "</highlight>) is already in use by another role!"));
            return Command.SINGLE_SUCCESS;
        }

        int targetAuthority = focus.getLowestUnassignedAuthority();
        if (targetAuthority > focus.getRoleFromMember(player.getUniqueId()).getAuthority()) {
            player.sendMessage(error("There are no free role slots for authority levels below that of your current role!"));
            return Command.SINGLE_SUCCESS;
        }

        String treatedName = roleArgument.substring(0, 1).toUpperCase() + roleArgument.substring(1);

        SettlementRole newRole = new SettlementRole(treatedName, NamedTextColor.GRAY, focus, targetAuthority, SettlementRole.Type.NORMAL, NamespacedKey.minecraft("leather_helmet"));

        focus.addRole(newRole);

        focus.tell(player, "Created a <highlight>new role</highlight> named " + newRole.getDisplayAsString() + "<normal>! Its current authority level is <light_amethyst>" + newRole.getAuthority() + "</light_amethyst>.");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForAuthorityIncrease(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role, 1)) { return Command.SINGLE_SUCCESS; }

        if (role.getAuthority() == SettlementRole.MAX_AUTHORITY) {
            player.sendMessage(error("Cannot edit authority for the <highlight>leader role</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (role.getAuthority() == (SettlementRole.MAX_AUTHORITY - 1)) {
            player.sendMessage(error("Only the <highlight>leader role</highlight> can be at maximum authority!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole roleAbove = focus.getRoleAbove(role);
        if (roleAbove != null && roleAbove.getAuthority() == role.getAuthority() + 1) {
            player.sendMessage(error("Another role is occupying the authority level above!"));
            return Command.SINGLE_SUCCESS;
        }

        role.increaseAuthority();

        focus.tell(player, "<green>Increased</green> authority of role " + role.getDisplayAsString() + "<normal>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForAuthorityDecrease(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        if (role.getAuthority() == SettlementRole.MAX_AUTHORITY) {
            player.sendMessage(error("Cannot edit authority for the <highlight>leader role</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (role.getAuthority() == SettlementRole.MIN_AUTHORITY) {
            player.sendMessage(error("That role is already at the minimum authority level!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole roleBelow = focus.getRoleBelow(role);
        if (roleBelow != null && roleBelow.getAuthority() == role.getAuthority() - 1) {
            player.sendMessage(error("Another role is occupying the authority level below!"));
            return Command.SINGLE_SUCCESS;
        }

        role.decreaseAuthority();

        focus.tell(player, "<red>Decreased</red> authority of role " + role.getDisplayAsString() + "<normal>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForNameChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String nameArg = StringArgumentType.getString(context, "name");
        if (nameArg.equals(role.getName())) {
            player.sendMessage(error("That role already has that name..."));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.isUniqueRoleName(nameArg)) {
            player.sendMessage(error("That name (<highlight>" + roleArgument + "</highlight>) is already in use by another role!"));
            return Command.SINGLE_SUCCESS;
        }

        String oldDisplay = role.getDisplayAsString();
        role.setName(nameArg.substring(0, 1).toUpperCase() + nameArg.substring(1));

        focus.tell(player, "Changed the name of role " + oldDisplay + "<normal> to " + role.getDisplayAsString() + "<normal>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForColorChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String colorArgument = StringArgumentType.getString(context, "named_color");
        TextColor color;
        if (NamedTextColor.NAMES.keys().contains(colorArgument)) {
            color = NamedTextColor.NAMES.value(colorArgument);
        } else {
            player.sendMessage(error("Could not find a color named <highlight>" + colorArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        if (color.equals(role.getColor())) {
            player.sendMessage(error("That role already has that color!"));
            return Command.SINGLE_SUCCESS;
        }

        role.setColor(color);

        focus.tell(player, "Changed color of role " + role.getDisplayAsString() + "<normal> to <" + colorArgument.toLowerCase() + ">" + colorArgument.toUpperCase() + "</" + colorArgument.toLowerCase() + ">!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIconChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        NamespacedKey iconArgument = context.getArgument("icon", NamespacedKey.class);
        String namespace = iconArgument.getNamespace();
        if (namespace.equals("minecraft")) {
            ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(iconArgument);
            if (dummy == null) {
                player.sendMessage(error("Please provide a valid item key!"));
                return Command.SINGLE_SUCCESS;
            }
        } else if (namespace.equals("jade")) {
            // TODO: implement this
            player.sendMessage(error("This feature has not been implemented yet!"));
            return Command.SINGLE_SUCCESS;
        } else {
            player.sendMessage(error("Please provide a valid namespace!"));
            return Command.SINGLE_SUCCESS;
        }

        role.setIcon(iconArgument);

        focus.tell(player, "Changed icon of role " + role.getDisplayAsString() + " to <highlight>" + iconArgument.asString() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForPermissionChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String permArgument = StringArgumentType.getString(context, "permission_node");
        boolean valueArgument = BoolArgumentType.getBool(context, "value");
        if (role.setPermission(permArgument, valueArgument)) {
            if (valueArgument) {
                focus.tell(player, "Changed permission <light_amethyst>" + permArgument + "</light_amethyst> to <green>TRUE</green> for role " + role.getDisplayAsString() + "<normal>!");

            } else {
                focus.tell(player, "Changed permission <light_amethyst>" + permArgument + "</light_amethyst> to <red>FALSE</red> for role " + role.getDisplayAsString() + "<normal>!");
            }

        } else {
            player.sendMessage(error("Could not find a permission node named <highlight>" + permArgument + "</highlight>!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMakeDefault(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        if (!focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("Only the <highlight>leader</highlight> may change the default role!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isDefault()) {
            player.sendMessage(error("That role is already the default role, silly!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setDefaultRole(role);

        focus.broadcast("A high official has made " + role.getDisplayAsString() + "<normal> the <highlight>default role</highlight>! Newcomers will automatically be assigned this role when they join!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDelete(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArgument = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArgument, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArgument);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        if (!focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("Only the <highlight>leader</highlight> may delete roles!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isLeader()) {
            player.sendMessage(error("The <highlight>leader role</highlight> cannot be deleted!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isDefault()) {
            player.sendMessage(error("The <highlight>default role</highlight> cannot be deleted!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.removeRole(role);

        focus.broadcast("A high official has <red>deleted</red> the role of " + role.getDisplayAsString() + "!");
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> suggestRolePermissionNodes(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        List<String> permissionNodes = new ArrayList<>();
        permissionNodes.add("canInvite");
        permissionNodes.add("canKick");
        permissionNodes.add("canClaim");
        permissionNodes.add("canUnclaim");
        permissionNodes.add("canPromote");
        permissionNodes.add("canDemote");
        permissionNodes.add("canEdit");
        permissionNodes.add("canManage");

        permissionNodes.stream().filter(str -> str.toLowerCase().contains(builder.getRemainingLowerCase())).forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestRoleIcons(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        List<String> permissionNodes = new ArrayList<>();
        permissionNodes.add("minecraft:netherite_helmet");
        permissionNodes.add("minecraft:diamond_helmet");
        permissionNodes.add("minecraft:golden_helmet");
        permissionNodes.add("minecraft:chainmail_helmet");
        permissionNodes.add("minecraft:turtle_helmet");
        permissionNodes.add("minecraft:iron_helmet");
        permissionNodes.add("minecraft:leather_helmet");

        permissionNodes.stream().filter(str -> str.toLowerCase().contains(builder.getRemainingLowerCase())).forEach(builder::suggest);

        return builder.buildFuture();
    }


}

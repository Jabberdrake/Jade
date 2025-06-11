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
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
                                        .suggests(CommonArgumentSuggestions::suggestNamedTextColors)
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

        //player.sendMessage(TextUtils.composeSimpleErrorMessage("Settlement role management GUI has not been implemented yet!"));

        new SettlementManageMenu(focus).open(player);

        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canManage()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not allowed to manage roles for ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }
        return true;
    }

    public static boolean validateRoleArgument(String roleArgument, Player player, Settlement settlement) {
        if (settlement.getRoleFromName(roleArgument) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("There is no \"")
                    .append(TextUtils.composeErrorHighlight(roleArgument))
                    .append(TextUtils.composeErrorText("\" role in "))
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }
        return true;
    }


    public static boolean validateAuthorityDynamic(Player player, Settlement settlement, SettlementRole targetRole) {
        return validateAuthorityDynamic(player, settlement, targetRole, 0);
    }

    public static boolean validateAuthorityDynamic(Player player, Settlement settlement, SettlementRole targetRole, int authorityOffset) {
        SettlementRole senderRole = settlement.getRoleFromMember(player.getUniqueId());
        if (senderRole.getAuthority() <= targetRole.getAuthority() + authorityOffset) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Insufficient authority!"));
            return false;
        }
        return true;
    }

    public static int runCommandForCreate(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        //if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        if (!focus.isUniqueRoleName(roleArg)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified name is already in use by another role in that settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        int targetAuthority = focus.getLowestUnassignedAuthority();
        if (targetAuthority > focus.getRoleFromMember(player.getUniqueId()).getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("There are no free role slots for authority levels below that of your current role!"));
            return Command.SINGLE_SUCCESS;
        }

        String processedName = roleArg.substring(0, 1).toUpperCase() + roleArg.substring(1);

        SettlementRole newRole = new SettlementRole(processedName, NamedTextColor.GRAY, focus, targetAuthority, SettlementRole.Type.NORMAL, NamespacedKey.fromString("leather_helmet"));

        focus.addRole(newRole);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Created role ")
                .append(newRole.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText(" in the settlement "))
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForAuthorityIncrease(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role, 1)) { return Command.SINGLE_SUCCESS; }

        if (role.getAuthority() == SettlementRole.MAX_AUTHORITY) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Cannot increase authority for the leader role!"));
            return Command.SINGLE_SUCCESS;
        } else if (role.getAuthority() == (SettlementRole.MAX_AUTHORITY - 1)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Only the leader role can be at maximum authority!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole roleAbove = focus.getRoleAbove(role);
        if (roleAbove != null && roleAbove.getAuthority() == role.getAuthority() + 1) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Another role is occupying the target authority level!"));
            return Command.SINGLE_SUCCESS;
        }
        role.increaseAuthority();

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Increased authority of role ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText("!")));

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
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Cannot decrease authority for the leader role!"));
            return Command.SINGLE_SUCCESS;
        } else if (role.getAuthority() == SettlementRole.MIN_AUTHORITY) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Target role is already at minimum authority!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole roleBelow = focus.getRoleBelow(role);
        if (roleBelow != null && roleBelow.getAuthority() == role.getAuthority() - 1) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Another role is occupying the target authority level!"));
            return Command.SINGLE_SUCCESS;
        }

        role.decreaseAuthority();

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Decreased authority of role ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForNameChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String nameArg = StringArgumentType.getString(context, "name");
        if (nameArg.equals(role.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Target role already has that name..."));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.isUniqueRoleName(nameArg)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Another role in ")
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeErrorText(" already has that name!")));
            return Command.SINGLE_SUCCESS;
        }

        role.setName(nameArg.substring(0, 1).toUpperCase() + nameArg.substring(1));

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Renamed target role to ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForColorChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String colorArg = StringArgumentType.getString(context, "named_color");
        TextColor color;
        if (NamedTextColor.NAMES.keys().contains(colorArg)) {
            color = NamedTextColor.NAMES.value(colorArg);
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not recognize the specified color!"));
            return Command.SINGLE_SUCCESS;
        }

        if (color.equals(role.getColor())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Target role already has that color!"));
            return Command.SINGLE_SUCCESS;
        }

        role.setColor(color);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed color of role ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText(" to "))
                .append(Component.text(colorArg.toUpperCase()).color(color))
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIconChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        NamespacedKey iconArgument = context.getArgument("icon", NamespacedKey.class);
        String namespace = iconArgument.getNamespace();
        if (namespace.equals("minecraft")) {
            ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(iconArgument);
            if (dummy == null) {
                player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid item key!"));
                return Command.SINGLE_SUCCESS;
            }
        } else if (namespace.equals("jade")) {
            // TODO: implement this
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This feature has not been implemented yet!"));
            return Command.SINGLE_SUCCESS;
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid namespace!"));
            return Command.SINGLE_SUCCESS;
        }

        role.setIcon(iconArgument);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed icon of role ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText(" to "))
                .append(TextUtils.composeSuccessHighlight(iconArgument.asString()))
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForPermissionChange(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        String permArg = StringArgumentType.getString(context, "permission_node");
        boolean valueArg = BoolArgumentType.getBool(context, "value");
        if (role.setPermission(permArg, valueArg)) {
            if (valueArg) {
                player.sendMessage(TextUtils.composeSimpleSuccessMessage("Set permission ")
                        .append(Component.text(permArg).color(TextUtils.LIGHT_AMETHYST))
                        .append(TextUtils.composeSuccessText(" to "))
                        .append(Component.text("TRUE", NamedTextColor.GREEN))
                        .append(TextUtils.composeSuccessText(" for role "))
                        .append(role.getDisplayAsComponent())
                        .append(TextUtils.composeSuccessText("!")));

            } else {
                player.sendMessage(TextUtils.composeSimpleSuccessMessage("Set permission ")
                        .append(Component.text(permArg).color(TextUtils.LIGHT_AMETHYST))
                        .append(TextUtils.composeSuccessText(" to "))
                        .append(Component.text("FALSE", NamedTextColor.RED))
                        .append(TextUtils.composeSuccessText(" for role "))
                        .append(role.getDisplayAsComponent())
                        .append(TextUtils.composeSuccessText("!")));
            }

        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Invalid permission node specified!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMakeDefault(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        if (!focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Only the leader may change the default role!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isDefault()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This role is already the default role, silly!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setDefaultRole(role);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Made ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText(" the default role of settlement "))
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDelete(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        String roleArg = StringArgumentType.getString(context, "role");

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementManageCommand.validateRoleArgument(roleArg, player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole role = focus.getRoleFromName(roleArg);
        if (!SettlementManageCommand.validateAuthorityDynamic(player, focus, role)) { return Command.SINGLE_SUCCESS; }

        if (!focus.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Only the leader may delete roles!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The leader role cannot be deleted!"));
            return Command.SINGLE_SUCCESS;
        }

        if (role.isDefault()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The default role cannot be deleted!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.removeRole(role);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Deleted role ")
                .append(role.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText(" in the settlement "))
                .append(focus.getDisplayName())
                .append(TextUtils.composeSuccessText("!")));

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

        permissionNodes.stream().filter(str -> str.startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);

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

        permissionNodes.stream().filter(str -> str.startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);

        return builder.buildFuture();
    }


}

package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.SimpleJadeMenu;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.text;

public class SettlementRoleEditMenu extends SimpleJadeMenu {

    private SettlementRole role;
    private Map<String, String> inputs;

    public SettlementRoleEditMenu(SettlementRole role, Map<String, String> inputs) {
        super("Editing " + role.getName(), Rows.SIX);
        this.role = role;
        this.inputs = inputs;
    }

    @Override
    public void composeMenu() {

    }

    public MenuItem getInfoAsMenuItem(String subject) {
        ItemStack infoItem = ItemStack.of(Material.PAPER);
        switch (subject) {
            case "NAME":
                infoItem.setData(DataComponentTypes.CUSTOM_NAME, text("Role Name", TextUtils.ZORBA));
                infoItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                                .addLine(text("Every role must have", TextUtils.DARK_ZORBA)
                                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                .addLine(text("a unique name. To edit", TextUtils.DARK_ZORBA)
                                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                .addLine(text("this role's name, click", TextUtils.DARK_ZORBA)
                                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                .addLine(text("the item below.", TextUtils.DARK_ZORBA)
                                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .build());
                return new MenuItem(infoItem, (Consumer<Player>) null);
            case "COLOR":
                infoItem.setData(DataComponentTypes.CUSTOM_NAME, text("Role Color", TextUtils.ZORBA));
                infoItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                        .addLine(text("To edit this role's display", TextUtils.DARK_ZORBA)
                                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .addLine(text("color, click the item below.", TextUtils.DARK_ZORBA)
                                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .build());
                return new MenuItem(infoItem, (Consumer<Player>) null);
            default:
                return null;
        }
    }

    public MenuItem getButtonAsMenuItem(String subject) {
        Map<ClickType, Consumer<Player>> buttonActions = new HashMap<>();
        switch (subject) {
            case "NAME":
                ItemStack nameEditItem = ItemStack.of(Material.NAME_TAG);
                nameEditItem.setData(DataComponentTypes.CUSTOM_NAME, text("Current name: ", TextUtils.ZORBA).append(text(this.role.getName(), TextUtils.AMETHYST)));
                if (inputs.containsKey("NAME")) {
                    String inputName = inputs.get("NAME");
                    nameEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                            .addLine(text("Will be changed to: ", TextUtils.DARK_ZORBA).append(text(inputName, TextUtils.LIGHT_AMETHYST))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                            .addLine(Component.empty())
                            .addLine(text()
                                    .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to enter a new name", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build()
                            )
                            .addLine(text()
                                    .content("Right Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to confirm new name", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build())
                            .build());
                    buttonActions.put(ClickType.LEFT, p -> {
                        p.sendMessage(text("Not implemented yet!", NamedTextColor.RED));
                        p.closeInventory();
                    });
                    buttonActions.put(ClickType.RIGHT, p -> {
                        p.performCommand("settlement manage " + this.role.getName() + " name " + inputName);
                        p.closeInventory();
                    });
                    return new MenuItem(nameEditItem, buttonActions);
                } else {
                    nameEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                            //.addLine(text("aaa", TextUtils.DARK_ZORBA)
                            //        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                            .addLine(Component.empty())
                            .addLine(text()
                                    .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to enter a new name", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build()
                            )
                            .build());
                    buttonActions.put(ClickType.LEFT, p -> {
                        p.sendMessage(text("Not implemented yet!", NamedTextColor.RED));
                        p.closeInventory();
                    });
                    return new MenuItem(nameEditItem, buttonActions);
                }
            case "COLOR":
                TextColor currentColor = this.role.getColor();

                final String roleColorHex = inputs.containsKey("COLOR") ? NamedTextColor.NAMES.value(inputs.get("COLOR")).asHexString() : currentColor.asHexString();
                ItemStack colorEditItem;

                if (roleColorHex.equals(NamedTextColor.AQUA.asHexString())) {
                    colorEditItem = ItemStack.of(Material.PRISMARINE_CRYSTALS);
                } else if (roleColorHex.equals(NamedTextColor.DARK_AQUA.asHexString())) {
                    colorEditItem = ItemStack.of(Material.PRISMARINE_SHARD);
                }  else if (roleColorHex.equals(NamedTextColor.BLUE.asHexString())) {
                    colorEditItem = ItemStack.of(Material.LIGHT_BLUE_DYE);
                } else if (roleColorHex.equals(NamedTextColor.DARK_BLUE.asHexString())) {
                    colorEditItem = ItemStack.of(Material.BLUE_DYE);
                } else if (roleColorHex.equals(NamedTextColor.DARK_PURPLE.asHexString())) {
                    colorEditItem = ItemStack.of(Material.PURPLE_DYE);
                }  else if (roleColorHex.equals(NamedTextColor.LIGHT_PURPLE.asHexString())) {
                    colorEditItem = ItemStack.of(Material.PINK_DYE);
                } else if (roleColorHex.equals(NamedTextColor.GREEN.asHexString())) {
                    colorEditItem = ItemStack.of(Material.LIME_DYE);
                } else if (roleColorHex.equals(NamedTextColor.DARK_GREEN.asHexString())) {
                    colorEditItem = ItemStack.of(Material.GREEN_DYE);
                } else if (roleColorHex.equals(NamedTextColor.YELLOW.asHexString())) {
                    colorEditItem = ItemStack.of(Material.YELLOW_DYE);
                } else if (roleColorHex.equals(NamedTextColor.GOLD.asHexString())) {
                    colorEditItem = ItemStack.of(Material.RAW_GOLD);
                } else if (roleColorHex.equals(NamedTextColor.RED.asHexString())) {
                    colorEditItem = ItemStack.of(Material.RED_DYE);
                } else if (roleColorHex.equals(NamedTextColor.DARK_RED.asHexString())) {
                    colorEditItem = ItemStack.of(Material.REDSTONE);
                } else if (roleColorHex.equals(NamedTextColor.GRAY.asHexString())) {
                    colorEditItem = ItemStack.of(Material.LIGHT_GRAY_DYE);
                } else if (roleColorHex.equals(NamedTextColor.DARK_GRAY.asHexString())) {
                    colorEditItem = ItemStack.of(Material.GRAY_DYE);
                } else if (roleColorHex.equals(NamedTextColor.BLACK.asHexString())) {
                    colorEditItem = ItemStack.of(Material.BLACK_DYE);
                } else if (roleColorHex.equals(NamedTextColor.WHITE.asHexString())) {
                    colorEditItem = ItemStack.of(Material.WHITE_DYE);
                } else {
                    colorEditItem = ItemStack.of(Material.BARRIER);
                }
                colorEditItem.setData(DataComponentTypes.CUSTOM_NAME, text("Current color: ", TextUtils.ZORBA).append(text(NamedTextColor.NAMES.key((NamedTextColor) currentColor), currentColor)));
                if (inputs.containsKey("COLOR")) {
                    String inputColor = inputs.get("COLOR");
                    colorEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                            .addLine(text("Will be changed to: ", TextUtils.DARK_ZORBA).append(text(inputColor, TextColor.fromHexString(inputColor))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)))
                            .addLine(Component.empty())
                            .addLine(text()
                                    .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to cycle through available colors", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build()
                            )
                            .addLine(text()
                                    .content("Right Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to confirm chosen color", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build())
                            .build());
                    buttonActions.put(ClickType.LEFT, p -> {
                        List<String> namedTextColorsAsStrings = NamedTextColor.NAMES.keys().stream().toList();
                        inputs.remove("COLOR");
                        inputs.put("COLOR", namedTextColorsAsStrings.get((namedTextColorsAsStrings.indexOf(inputColor) + 1) % namedTextColorsAsStrings.size()));
                        new SettlementRoleEditMenu(this.role, this.inputs).open(p);
                        p.closeInventory();
                    });
                    buttonActions.put(ClickType.RIGHT, p -> {
                        p.performCommand("settlement manage " + this.role.getName() + " color " + inputColor);
                        p.closeInventory();
                    });
                    return new MenuItem(colorEditItem, buttonActions);
                } else {
                    colorEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                            //.addLine(text("aaa", TextUtils.DARK_ZORBA)
                            //        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                            .addLine(Component.empty())
                            .addLine(text()
                                    .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                    .append(text(" to cycle through available colors", NamedTextColor.DARK_GREEN))
                                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .build())
                            .build());
                    buttonActions.put(ClickType.LEFT, p -> {
                        List<String> namedTextColorsAsStrings = NamedTextColor.NAMES.keys().stream().toList();
                        inputs.remove("COLOR");
                        inputs.put("COLOR", namedTextColorsAsStrings.getFirst());
                        new SettlementRoleEditMenu(this.role, this.inputs).open(p);
                        p.closeInventory();
                    });
                    return new MenuItem(colorEditItem, buttonActions);
                }
            default:
                return null;
        }
    }

    public MenuItem getPermissionAsMenuItem(String permissionNode, Map<String, String> inputs) {
        ItemStack permEditItem;
        Map<ClickType, Consumer<Player>> buttonActions = new HashMap<>();
        if (inputs.containsKey(permissionNode)) {
            String permValueAsString = inputs.get(permissionNode);
            boolean permValue = permValueAsString.equals("TRUE");
            if (permValue) {
                permEditItem = new ItemStack(Material.GREEN_WOOL);
            } else {
                permEditItem = new ItemStack(Material.RED_WOOL);
            }

            permEditItem.setData(DataComponentTypes.CUSTOM_NAME, text("Permission node: ", TextUtils.LIGHT_ROSEMETAL).append(text(SettlementRole.getPermissionAsDisplayString(permissionNode), TextUtils.LIGHT_CHROME)));
            permEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .addLine(text("Current value: ", TextUtils.DARK_ZORBA).append(text(this.role.hasPermission(permissionNode) ? "TRUE" : "FALSE", this.role.hasPermission(permissionNode) ? NamedTextColor.GREEN : NamedTextColor.RED))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .addLine(text("Will be changed to: ", TextUtils.DARK_ZORBA).append(text(permValueAsString, permValue ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .addLine(Component.empty())
                    .addLine(text()
                        .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .append(text(" to flip permission value", NamedTextColor.DARK_GREEN))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build())
                    .addLine(text()
                        .content("Right Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .append(text(" to confirm options", NamedTextColor.DARK_GREEN))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build())
                    .build());
            buttonActions.put(ClickType.LEFT, p -> {
                inputs.remove(permissionNode);
                inputs.put(permissionNode, permValue ? "FALSE" : "TRUE");
                new SettlementRoleEditMenu(this.role, this.inputs).open(p);
                p.closeInventory();
            });
            buttonActions.put(ClickType.RIGHT, p -> {
                p.performCommand("settlement manage " + this.role.getName() + " permissions " + permissionNode + " " + permValueAsString);
                p.closeInventory();
            });
            return new MenuItem(permEditItem, buttonActions);
        } else {
            boolean permValue = this.role.hasPermission(permissionNode);
            if (permValue) {
                permEditItem = new ItemStack(Material.GREEN_WOOL);
            } else {
                permEditItem = new ItemStack(Material.RED_WOOL);
            }

            permEditItem.setData(DataComponentTypes.CUSTOM_NAME, text("Permission node: ", TextUtils.LIGHT_ROSEMETAL).append(text(permissionNode, TextUtils.LIGHT_CHROME)));
            permEditItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .addLine(text("Current value: ", TextUtils.DARK_ZORBA).append(text(permValue ? "TRUE" : "FALSE", permValue ? NamedTextColor.GREEN : NamedTextColor.RED))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .addLine(Component.empty())
                    .addLine(text()
                            .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .append(text(" to flip permission value", NamedTextColor.DARK_GREEN))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .build())
                    .build());
            buttonActions.put(ClickType.LEFT, p -> {
                inputs.put(permissionNode, permValue ? "FALSE" : "TRUE");
                new SettlementRoleEditMenu(this.role, this.inputs).open(p);
                p.closeInventory();
            });
            return new MenuItem(permEditItem, buttonActions);
        }
    }
}

package dev.jabberdrake.jade.jade.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface JadeMenu extends InventoryHolder {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    void click(Player player, int slot);

    void setItem(int slot, ItemStack item);
    void setItem(int slot, ItemStack item, Consumer<Player> action);

    void composeMenu(Player player);
    void composeErrorMenu(Player player, String message);

    default void open(Player player) {
        composeMenu(player);
        player.openInventory(getInventory());
    }

    default void reopen(Player player) {
        player.closeInventory();
        composeMenu(player);
        player.openInventory(getInventory());
    }

    default void error(Player player, String errorMessage) {
        player.closeInventory();
        composeErrorMenu(player, errorMessage);
        player.openInventory(getInventory());
    }
}

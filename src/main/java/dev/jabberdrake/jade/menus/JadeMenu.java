package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public interface JadeMenu extends InventoryHolder {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    void click(Player player, int slot);

    void setItem(int slot, MenuItem item);

    void composeMenu(Player player);

    default void open(Player player) {
        composeMenu(player);
        player.openInventory(getInventory());
    }

    /*
    default void reopen(Player player) {
        player.closeInventory();
        composeMenu(player);
        player.openInventory(getInventory());
    }*/

    void update();

    default void error(Player player, String errorMessage) {
        player.closeInventory();
        player.sendMessage(TextUtils.composeSimpleErrorMessage("An error occurred while using a menu! Details: ")
                .append(Component.text(errorMessage, TextUtils.DARK_ZORBA)));
    }

    Map<Integer, MenuItem> getItems();
}

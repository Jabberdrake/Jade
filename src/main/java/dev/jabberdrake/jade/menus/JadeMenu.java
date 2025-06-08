package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public interface JadeMenu extends InventoryHolder {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    void click(Player player, int slot, ClickType clickType);

    void setItem(int slot, MenuItem item);

    void composeMenu();

    void open(Player player);

    void update();

    default void error(String errorMessage) {
        getPlayer().closeInventory();
        getPlayer().sendMessage(TextUtils.composeSimpleErrorMessage("An error occurred while using a menu! Details: ")
                .append(Component.text(errorMessage, TextUtils.DARK_ZORBA)));
    }

    Map<Integer, MenuItem> getItems();

    Player getPlayer();
}

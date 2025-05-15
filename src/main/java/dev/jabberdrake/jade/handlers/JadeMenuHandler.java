package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.menus.JadeMenu;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class JadeMenuHandler implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(inventory.getHolder(false) instanceof JadeMenu menu)) { return; }

        event.setCancelled(true);
        menu.click((Player) event.getWhoClicked(), event.getSlot());
    }
}

package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.menus.JadeMenu;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class JadeMenuHandler implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(inventory.getHolder(false) instanceof JadeMenu menu)) { return; }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(!menu.canClick(player, event.getSlot(), event.getClick(), event.getAction()));
        menu.click(player, event.getSlot(), event.getClick(), event.getAction());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof JadeMenu menu)) { return; }

        menu.close((Player) event.getPlayer());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof JadeMenu menu)) { return; }

        event.setCancelled(true);
    }
}

package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.VanillaItem;
import dev.jabberdrake.jade.menus.JadeMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VanillaItemHandler implements Listener {

    // Credit to YouHaveTrouble for the design of this implementation!
    // (https://github.com/YouHaveTrouble/MendingBeGone/blob/master/src/main/java/me/youhavetrouble/mendingbegone/MendingBeGone.java)

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        processVanillaItem(event.getItem().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder(false) instanceof JadeMenu) return;

        for (ItemStack itemStack : inventory.getContents()) {
            processVanillaItem(itemStack);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent event) {
        for (ItemStack itemStack : event.getPlayer().getInventory()) {
            processVanillaItem(itemStack);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null || inventory.getHolder(false) instanceof JadeMenu) { return; }

        processVanillaItem(event.getCurrentItem());
        processVanillaItem(event.getCursor());
    }

    public void processVanillaItem(ItemStack source) {
        if (source == null) return;
        VanillaItem.convert(source);
    }
}

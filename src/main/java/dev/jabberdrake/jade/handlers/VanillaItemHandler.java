package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.VanillaItem;
import dev.jabberdrake.jade.items.VanillaItemRegistry;
import dev.jabberdrake.jade.menus.JadeMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemEnchant(EnchantItemEvent event) {
        processVanillaItem(event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftingResult(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        processVanillaItem(event.getRecipe().getResult());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAnvilRename(PrepareAnvilEvent event) {
        if (event.getResult() == null) return;
        renameVanillaItem(event.getResult(), event.getView().getRenameText());
    }

    public void processVanillaItem(ItemStack source) {
        if (source == null) return;

        if (source.getPersistentDataContainer().has(JadeItem.JADE_ITEM_KEY)) {
            VanillaItem.update(source);
        } else {
            VanillaItem.convert(source);
        }
    }

    public void renameVanillaItem(ItemStack source, String newName) {
        if (source.getPersistentDataContainer().has(JadeItem.JADE_ITEM_KEY)) {
            VanillaItem template = VanillaItemRegistry.getVanillaItem(source.getType().getKey().getKey());

            if (newName.equalsIgnoreCase("")) {
                JadeItem.rename(source, template.getName(), false);
            } else {
                JadeItem.rename(source, newName, true);
            }
        }
    }
}

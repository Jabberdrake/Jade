package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.VanillaItem;
import dev.jabberdrake.jade.items.VanillaItemRegistry;
import dev.jabberdrake.jade.menus.JadeMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
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
    public void onItemPickupAttempt(PlayerAttemptPickupItemEvent event) {
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
        ItemStack result = event.getItem();
        processVanillaItem(result);
        event.setItem(result);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftingPrepare(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        processVanillaItem(event.getRecipe().getResult());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        if (event.getResult() == null) return;
        ItemStack result = event.getResult();
        renameVanillaItem(result, event.getView().getRenameText());
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSmithingPrepare(PrepareSmithingEvent event) {
        if (event.getResult() == null) return;
        ItemStack result = event.getResult();
        resetJadeData(result);
        VanillaItem.convert(result);
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSmithingComplete(SmithItemEvent event) {
        resetJadeData(event.getInventory().getResult());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPotionBrew(BrewEvent event) {
        for (ItemStack result : event.getResults()) {
            processVanillaItem(result);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        for (ItemStack drop : event.getItems().stream().map(Item::getItemStack).toList()) {
            processVanillaItem(drop);
        }
    }

    public void processVanillaItem(ItemStack source) {
        if (source == null) return;

        if (source.getPersistentDataContainer().has(JadeItem.JADE_ITEM_KEY)) {
            VanillaItem.update(source);
        } else {
            VanillaItem.convert(source);
        }
    }

    public void resetJadeData(ItemStack source) {
        if (source == null) return;

        source.editPersistentDataContainer(pdc -> pdc.remove(JadeItem.JADE_ITEM_KEY));
    }

    public void renameVanillaItem(ItemStack source, String newName) {
        if (source.getPersistentDataContainer().has(JadeItem.JADE_ITEM_KEY)) {
            VanillaItem template = VanillaItemRegistry.getVanillaItem(source.getType().getKey().getKey());
            if (template == null) return;

            if (newName.equalsIgnoreCase("")) {
                JadeItem.rename(source, template.getName(), false);
            } else {
                JadeItem.rename(source, newName, true);
            }
            VanillaItem.update(source);
        }
    }
}

package dev.jabberdrake.jade.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ExpBottleHandler implements Listener {

    public static final int XP_UNIT = 20;

    @EventHandler
    public void onBottleBreak(ExpBottleEvent event) {
        event.setExperience(XP_UNIT);
    }

    @EventHandler
    public void onTableInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != Material.GLASS_BOTTLE) return;

        int totalXP = player.calculateTotalExperiencePoints();
        if (totalXP < XP_UNIT) return;

        event.setCancelled(true);
        player.give(ItemStack.of(Material.EXPERIENCE_BOTTLE, 1));
        player.setExperienceLevelAndProgress(totalXP - XP_UNIT);

        int heldAmount = heldItem.getAmount();
        if (heldAmount == 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            heldItem.setAmount(heldAmount - 1);
            //player.getInventory().setItemInMainHand();
        }
    }
}

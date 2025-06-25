package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.JadeItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GadgetItemHandler implements Listener {



    @EventHandler
    public void onFrameBreak(HangingBreakEvent event) {
        Hanging hangingEntity = event.getEntity();
        if (!(hangingEntity instanceof ItemFrame frame)) return;

        if (!frame.isVisible()) {
            event.setCancelled(true);
            World world = Jade.getInstance().getServer().getWorld(frame.getWorld().getUID());
            if (world == null) return;

            ItemStack heldItem = frame.getItem();
            if (heldItem.getType() != Material.AIR) {
                Item heldItemEntity = world.dropItem(frame.getLocation(), heldItem);
                heldItemEntity.setVelocity(new Vector(0, 0.4, 0));
                world.playSound(frame.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 0.65F, 1F);
            }

            Item frameItemEntity = world.dropItem(frame.getLocation(), JadeItemRegistry.PHANTOM_ITEM_FRAME.getItem());
            frameItemEntity.setVelocity(new Vector(0, 0.2, 0));
            world.playSound(frame.getLocation(), Sound.ENTITY_ITEM_FRAME_BREAK, 0.65F, 1F);
            frame.remove();
        }
    }
}

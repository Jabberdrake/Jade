package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.settings.BlockProtectionSetting;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class RealmProtectionHandler implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlockPlaced().getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;

        Area area = owner.getHighestPriorityAreaForLocation(player.getLocation());
        if (area != null) {
            if (!area.isMember(player.getUniqueId())) {
                player.sendMessage(error("You are not trusted in the <highlight>" + area.getName() + "</highlight> area!"));
            }
        } else {
            if (owner.containsPlayer(player.getUniqueId())) return;

            String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
            switch (protectionLevel) {
                case BlockProtectionSetting.REGULAR, BlockProtectionSetting.ADVENTURE:
                    player.sendMessage(error("You are not a member of the <highlight>" + owner.getName() + "</highlight> settlement!"));
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;

        Area area = owner.getHighestPriorityAreaForLocation(player.getLocation());
        if (area != null) {
            if (!area.isMember(player.getUniqueId())) {
                player.sendMessage(error("You are not trusted in the <highlight>" + area.getName() + "</highlight> area!"));
            }
        } else {
            if (owner.containsPlayer(player.getUniqueId())) return;

            String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
            switch (protectionLevel) {
                case BlockProtectionSetting.REGULAR, BlockProtectionSetting.ADVENTURE:
                    player.sendMessage(error("You are not a member of the <highlight>" + owner.getName() + "</highlight> settlement!"));
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        Player player = event.getPlayer();
        Chunk chunk = block.getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;

        Area area = owner.getHighestPriorityAreaForLocation(player.getLocation());
        if (area != null) {
            if (!area.isMember(player.getUniqueId())) {
                player.sendMessage(error("You are not trusted in the <highlight>" + area.getName() + "</highlight> area!"));
            }
        } else {
            if (owner.containsPlayer(player.getUniqueId())) return;

            String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
            switch (protectionLevel) {
                case BlockProtectionSetting.REGULAR -> event.setCancelled(true);
                case BlockProtectionSetting.ADVENTURE -> {
                    if (block.getState() instanceof Container || block.getBlockData() instanceof Openable || block.getBlockData() instanceof Powerable) {
                        event.setCancelled(false);
                    } else {
                        player.sendMessage(error("You are not a member of the <highlight>" + owner.getName() + "</highlight> settlement!"));
                        event.setCancelled(true);
                    }
                }
                case BlockProtectionSetting.NONE -> event.setCancelled(false);
            }
        }
    }
}

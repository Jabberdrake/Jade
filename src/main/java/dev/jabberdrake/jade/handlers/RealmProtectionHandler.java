package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.realms.RealmManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RealmProtectionHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        switch (JadeSettings.realmProtectionLevel) {
            case JadeSettings.CONTAINER:
                if (!RealmManager.isUnclaimedChunk(block.getChunk())
                        && block.getState() instanceof Container
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            case JadeSettings.NORMAL:
                if (!RealmManager.isUnclaimedChunk(chunk)
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        switch (JadeSettings.realmProtectionLevel) {
            case JadeSettings.CONTAINER:
                if (!RealmManager.isUnclaimedChunk(block.getChunk())
                        && block.getState() instanceof Container
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            case JadeSettings.NORMAL:
                if (!RealmManager.isUnclaimedChunk(chunk)
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        Player player = event.getPlayer();
        Chunk chunk = block.getChunk();
        switch (JadeSettings.realmProtectionLevel) {
            case JadeSettings.CONTAINER:
                if (!RealmManager.isUnclaimedChunk(block.getChunk())
                        && block.getState() instanceof Container
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            case JadeSettings.NORMAL:
                if (!RealmManager.isUnclaimedChunk(chunk)
                        && !RealmManager.getChunkOwner(chunk).containsPlayer(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                break;
            default:
                break;
        }
    }
}

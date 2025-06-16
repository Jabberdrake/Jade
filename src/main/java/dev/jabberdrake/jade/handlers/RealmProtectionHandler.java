package dev.jabberdrake.jade.handlers;

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

public class RealmProtectionHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlockPlaced().getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;
        if (owner.containsPlayer(player.getUniqueId())) return;

        String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
        switch (protectionLevel) {
            case BlockProtectionSetting.REGULAR, BlockProtectionSetting.ADVENTURE -> event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;
        if (owner.containsPlayer(player.getUniqueId())) return;

        String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
        switch (protectionLevel) {
            case BlockProtectionSetting.REGULAR, BlockProtectionSetting.ADVENTURE -> event.setCancelled(true);
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

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;
        if (owner.containsPlayer(player.getUniqueId())) return;

        String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
        switch (protectionLevel) {
            case BlockProtectionSetting.REGULAR -> event.setCancelled(true);
            case BlockProtectionSetting.ADVENTURE -> {
                if (block.getState() instanceof Container || block.getBlockData() instanceof Openable || block.getBlockData() instanceof Powerable) {
                    event.setCancelled(false);
                } else event.setCancelled(true);
            }
            case BlockProtectionSetting.NONE -> event.setCancelled(false);
        }
    }
}

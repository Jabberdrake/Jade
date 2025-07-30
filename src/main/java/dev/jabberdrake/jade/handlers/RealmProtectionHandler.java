package dev.jabberdrake.jade.handlers;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.settings.BlockProtectionSetting;
import dev.jabberdrake.jade.utils.TextUtils;
import dev.jabberdrake.jade.utils.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class RealmProtectionHandler implements Listener {

    private static final NamespacedKey HORSE_RIDER_KEY = Jade.key("last_rider");

    @EventHandler(priority = EventPriority.LOW)
    public void onPreMobSpawn(PreCreatureSpawnEvent event) {
        Chunk chunk = event.getSpawnLocation().getChunk();
        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;

        if (event.getReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMobSpawn(CreatureSpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) return;

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHorseMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Chunk chunk = player.getLocation().getChunk();

        Settlement owner = RealmManager.getChunkOwner(chunk);
        if (owner == null) {
            // Register the player as the horse's last rider
            event.getMount().getPersistentDataContainer().set(HORSE_RIDER_KEY, new UUIDDataType(), player.getUniqueId());
            return;
        }

        Area area = owner.getHighestPriorityAreaForLocation(player.getLocation());
        if (area != null) {
            if (!area.isMember(player.getUniqueId())) {

                // If the player trying to mount the horse is the last registered rider, then override claim protection
                if (event.getMount().getPersistentDataContainer().has(HORSE_RIDER_KEY) && event.getMount().getPersistentDataContainer().get(HORSE_RIDER_KEY, new UUIDDataType()).equals(player.getUniqueId())) {
                    event.setCancelled(false);
                    return;
                }


                player.sendMessage(error("You are not trusted in the <highlight>" + area.getName() + "</highlight> area!"));
            }
        } else {
            if (owner.containsPlayer(player.getUniqueId())) {
                // Register the player as the horse's last rider
                event.getMount().getPersistentDataContainer().set(HORSE_RIDER_KEY, new UUIDDataType(), player.getUniqueId());
                return;
            }

            String protectionLevel = owner.getSetting(BlockProtectionSetting.class).getValue();
            switch (protectionLevel) {
                case BlockProtectionSetting.REGULAR, BlockProtectionSetting.ADVENTURE:

                    // If the player trying to mount the horse is the last registered rider, then override claim protection
                    if (event.getMount().getPersistentDataContainer().has(HORSE_RIDER_KEY) && event.getMount().getPersistentDataContainer().get(HORSE_RIDER_KEY, new UUIDDataType()).equals(player.getUniqueId())) {
                        event.setCancelled(false);
                        return;
                    }

                    player.sendMessage(error("You are not a member of the <highlight>" + owner.getName() + "</highlight> settlement!"));
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // We deal with horse mounting in a separate event handler
        if (event.getRightClicked() instanceof AbstractHorse) return;

        Player player = event.getPlayer();
        Chunk chunk = event.getRightClicked().getChunk();

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
                    if (block.getState() instanceof Container || block.getBlockData() instanceof Openable || block.getBlockData() instanceof Powerable || block.getType() == Material.VAULT || block.getBlockData() instanceof Bed) {
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;
        Player player = (Player) event.getRemover();
        Chunk chunk = player.getChunk();

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
}

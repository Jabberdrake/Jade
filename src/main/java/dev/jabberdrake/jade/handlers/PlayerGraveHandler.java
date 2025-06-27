package dev.jabberdrake.jade.handlers;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.jabberdrake.jade.JadeConfig;
import dev.jabberdrake.jade.menus.implementations.GraveOpenMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.PositionUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class PlayerGraveHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGraveInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) {
            return;
        }

        // Check if the block we clicked was a grave
        Grave grave = PlayerManager.getGraveAt(block.getLocation());
        if (grave == null) return;

        Player player = event.getPlayer();
        UUID viewerUUID = PlayerManager.getGraveViewer(grave);
        if (viewerUUID != null) {
            if (viewerUUID.equals(player.getUniqueId())) {
                // weird shit, ignore
            } else if (grave.getPlayerID().equals(viewerUUID)) {
                player.sendMessage(error("Cannot open someone's grave while its rightful owner already has it open!"));
                return;
            } else if (grave.getPlayerID().equals(player.getUniqueId())) {
                Player viewerPlayer = Bukkit.getPlayer(viewerUUID);
                if (viewerPlayer != null) {
                    viewerPlayer.closeInventory();
                }
            }
        }

        event.setCancelled(true);

        if (!grave.getPlayerID().equals(player.getUniqueId())) {
            player.sendMessage(info("This grave does not belong to you... The rightful owner has been notified."));
            OfflinePlayer owner = Bukkit.getOfflinePlayer(grave.getPlayerID());
            if (owner.isOnline()) {
                ((Player) owner).sendMessage(info("<sunsteel>" + player.getName() + "</sunsteel> has opened your grave, <coral>" + grave.getID() + "</coral>..."));
            }
        }

        PlayerManager.openGraveMenu(player, grave);
    }

    @EventHandler
    public void onBlockDestroy(BlockDestroyEvent event) {
        Grave grave = PlayerManager.getGraveAt(event.getBlock().getLocation());
        if (grave != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Grave grave = PlayerManager.getGraveAt(event.getToBlock().getLocation());
        if (grave != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> PlayerManager.getGraveAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> PlayerManager.getGraveAt(block.getLocation()) != null);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!JadeConfig.enablePlayerGraves) return;
        if (event.getKeepInventory()) return;

        Player player = event.getPlayer();
        Location deathLocation = player.getLocation().getBlock().getLocation();
        boolean unrecoverable = false;

        if (event.getDamageSource().getDamageType().equals(DamageType.OUT_OF_WORLD)) { unrecoverable = true; }
        else if (isObstructed(deathLocation)) {
            deathLocation.add(0, 1, 0);
            if (isObstructed(deathLocation)) {
                unrecoverable = true;
            }
        }

        List<ItemStack> items = new ArrayList<>(event.getDrops());
        if (player.getKiller() != null) {
            ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
            head.setData(DataComponentTypes.CUSTOM_NAME, Component.text(player.getName() + "'s Head", JadeTextColor.YELLOW));
            head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(player.getUniqueId()).build());
            items.add(head);
        }
        items.removeIf(itemstack -> itemstack == null || itemstack.getType().isAir() || itemstack.getAmount() <= 0);

        if (items.isEmpty()) return;

        if (unrecoverable) {
            PlayerManager.registerGrave(player, items, null);
            player.sendMessage(info("Since you died in an obstructed or otherwise unreachable location, your items were deposited in a <highlight>virtual grave</highlight>."));
            player.sendMessage(info("To see your virtual graves, do <highlight>/grave list"));
        } else {
            PlayerManager.registerGrave(player, items, deathLocation);

            Block deathBlock = deathLocation.getBlock();
            deathBlock.setType(Material.PLAYER_HEAD);
            Skull skull = (Skull) deathBlock.getState();
            skull.setOwningPlayer(player);
            skull.update(true, false);

            player.sendMessage(info("Your items were stored in a grave at <coral>" + PositionUtils.asString(deathLocation, false) + "</coral>!"));
        }

        event.getDrops().clear();
    }

    public boolean isObstructed(Location location) {
        if (location.getY() < location.getWorld().getMinHeight() || location.getY() > location.getWorld().getMaxHeight()) return true;
        return !location.getBlock().getType().isAir() && !location.getBlock().isLiquid();
    }
}

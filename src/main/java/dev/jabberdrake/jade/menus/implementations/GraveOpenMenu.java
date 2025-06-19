package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

import java.util.List;
import java.util.function.Consumer;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class GraveOpenMenu extends PagedJadeMenu {
    private Grave grave;

    public GraveOpenMenu(Grave grave) {
        super(Bukkit.getOfflinePlayer(grave.getPlayerID()).getName() + "'s grave (" + grave.getID() + ")", Rows.SIX);

        this.grave = grave;
    }

    @Override
    public boolean canClick(Player player, int slot, ClickType clickType, InventoryAction inventoryAction) {
        if (isSafeSlot(slot)) {
            return this.getInventory().getItem(slot) != null &&
                    (inventoryAction == InventoryAction.PICKUP_ALL || inventoryAction == InventoryAction.HOTBAR_SWAP || inventoryAction == InventoryAction.PLACE_ALL || inventoryAction == InventoryAction.MOVE_TO_OTHER_INVENTORY);
        } else {
            return false;
        }
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, InventoryAction inventoryAction) {
        super.click(player, slot, clickType, inventoryAction);

        if (this.canClick(player, slot, clickType, inventoryAction)) {
            final int index = slot + this.getCurrentPage() * getInventory().getSize();
            this.getItems().remove(index);
        }
    }

    @Override
    public void close(Player player) {
        grave.setItems(this.getItemStacksInSafeArea());
        if (this.getItemStacksInSafeArea().isEmpty()) {
            if (!grave.getPlayerID().equals(getPlayer().getUniqueId())) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(grave.getPlayerID());
                if (owner.isOnline()) {
                    ((Player) owner).sendMessage(info("<sunsteel>" + player.getName() + "</sunsteel> has <red>emptied</red your grave, <coral>" + grave.getID() + "</coral>!"));
                }
            }
            PlayerManager.clearGrave(grave);
        }
    }

    @Override
    public void composeMenu() {
        List<MenuItem> graveContents = grave.getItems().stream().map(itemstack -> new MenuItem(itemstack, (Consumer<Player>) null)).toList();
        addAll(graveContents);
    }
}

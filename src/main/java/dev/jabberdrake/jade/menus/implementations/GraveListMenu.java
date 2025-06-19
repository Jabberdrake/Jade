package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class GraveListMenu extends PagedJadeMenu {

    public GraveListMenu() {
        super("Your Graves", Rows.SIX);
    }

    @Override
    public void composeMenu() {
        List<MenuItem> graveItems = new ArrayList<>();
        for (Grave grave : PlayerManager.getAllGravesForPlayer(this.getPlayer().getUniqueId())) {
            graveItems.add(new MenuItem(grave.asDisplayItem(), grave.isVirtual() ? p -> {
                p.performCommand("grave open " + grave.getID());
            } : null));
        }

        addAll(graveItems);
    }
}

package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.building.BuildingManager;
import dev.jabberdrake.jade.building.Warp;
import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;

import java.util.ArrayList;
import java.util.List;

public class WarpListMenu extends PagedJadeMenu {

    public WarpListMenu() {
        super("Warps", Rows.SIX);
    }

    @Override
    public void composeMenu() {
        List<MenuItem> warpItems = new ArrayList<>();
        for (Warp warp : BuildingManager.getAllWarps()) {
            warpItems.add(new MenuItem(warp.asDisplayItem("warp"), p -> {
                p.performCommand("warp tp " + warp.getName());
            }));
        }

        addAll(warpItems);
    }
}

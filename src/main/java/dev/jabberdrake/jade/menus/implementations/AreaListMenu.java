package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.menus.SimpleJadeMenu;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.Settlement;

import java.util.ArrayList;
import java.util.List;

public class AreaListMenu extends PagedJadeMenu {

    private final Settlement settlement;

    public AreaListMenu(Settlement settlement) {
        super("Areas in " + settlement.getName(), SimpleJadeMenu.Rows.SIX);
        this.settlement = settlement;
    }

    @Override
    public void composeMenu() {
        List<MenuItem> areasAsMenuItems = new ArrayList<>();
        for (Area area : settlement.getAreaList()) {
            areasAsMenuItems.add(new MenuItem(area.asDisplayItem("VIEW"), p -> {
                p.performCommand("settlement areas view " + area.getName());
                p.closeInventory();
            }));
        }

        addAll(areasAsMenuItems);
    }
}

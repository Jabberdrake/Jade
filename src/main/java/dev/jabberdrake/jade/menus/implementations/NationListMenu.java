package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;

import java.util.ArrayList;
import java.util.List;

public class NationListMenu extends PagedJadeMenu {

    public NationListMenu() {
        super("Nation List", Rows.SIX);
    }

    @Override
    public void composeMenu() {
        List<MenuItem> nationsAsMenuItems = new ArrayList<>();
        for (Nation nation : RealmManager.getAllNations()) {
            nationsAsMenuItems.add(new MenuItem(nation.asDisplayItem("INFO"), p -> {
                p.performCommand("nation info " + nation.getName());
                p.closeInventory();
            }));
        }

        addAll(nationsAsMenuItems);
    }
}

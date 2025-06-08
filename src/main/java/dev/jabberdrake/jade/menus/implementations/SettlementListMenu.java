package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SettlementListMenu extends PagedJadeMenu {

    public SettlementListMenu() {
        super("Settlement List", Rows.SIX);
    }

    @Override
    public void composeMenu() {
        List<MenuItem> settlementsAsMenuItems = new ArrayList<>();
        for (Settlement settlement : RealmManager.getAllSettlements()) {
            settlementsAsMenuItems.add(new MenuItem(settlement.asDisplayItem("INFO"), p -> {
                p.performCommand("settlement info " + settlement.getName());
                p.closeInventory();
            }));
        }

        addAll(settlementsAsMenuItems);
    }
}

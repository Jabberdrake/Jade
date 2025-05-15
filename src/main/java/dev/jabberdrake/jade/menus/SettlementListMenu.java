package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettlementListMenu extends PagedJadeMenu {

    public SettlementListMenu() {
        super("Settlement List", Rows.SIX);
    }

    @Override
    public void composeMenu(Player player) {
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

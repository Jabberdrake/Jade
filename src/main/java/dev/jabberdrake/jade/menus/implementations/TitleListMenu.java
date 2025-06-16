package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.JadeTitle;

import java.util.ArrayList;
import java.util.List;

public class TitleListMenu extends PagedJadeMenu {

    public TitleListMenu() {
        super("Your Titles", Rows.SIX);
    }

    @Override
    public void composeMenu() {
        List<MenuItem> titlesAsMenuItems = new ArrayList<>();
        for (JadeTitle title : PlayerManager.asJadePlayer(this.getPlayer().getUniqueId()).getAvailableTitles()) {
            titlesAsMenuItems.add(new MenuItem(title.asDisplayItem("USE"), p -> {
                p.performCommand("title use " + title.getName());
                p.closeInventory();
            }));
        }

        addAll(titlesAsMenuItems);
    }
}

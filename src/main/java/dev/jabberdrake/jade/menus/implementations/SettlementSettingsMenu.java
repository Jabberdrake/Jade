package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.PagedJadeMenu;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.AbstractSetting;
import dev.jabberdrake.jade.utils.JadeTextColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.text;

public class SettlementSettingsMenu extends PagedJadeMenu {

    private final Settlement settlement;

    public SettlementSettingsMenu(Settlement settlement) {
        super("Settings", Rows.SIX);
        this.settlement = settlement;
    }

    @Override
    public void composeMenu() {
        List<MenuItem> settingsAsMenuItems = new ArrayList<>();

        SettlementRole viewerRole = this.settlement.getRoleFromMember(this.getPlayer().getUniqueId());
        settlement.getSettings().keySet().stream().toList().forEach(clazz -> {
            settingsAsMenuItems.add(getSettingAsMenuItem(clazz, viewerRole));
        });

        addAll(settingsAsMenuItems);
    }

    public <T extends AbstractSetting<?>> MenuItem getSettingAsMenuItem(Class<T> clazz, SettlementRole viewerRole) {
        T setting = settlement.getSetting(clazz);
        ItemStack settingItem = setting.getIconAsItem();
        Map<ClickType, Consumer<Player>> menuActions = new HashMap<>();

        settingItem.setData(DataComponentTypes.CUSTOM_NAME, text()
                .content(setting.displayName()).color(JadeTextColor.CHROME)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());

        ItemLore.Builder loreBuilder = ItemLore.lore();
        setting.description().forEach(loreBuilder::addLine);

        loreBuilder.addLine(Component.empty());
        setting.buildValueLore(loreBuilder);
        loreBuilder.addLine(Component.empty());

        if (viewerRole.isLeader()) {
            loreBuilder.addLine(text()
                    .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                    .append(text(" to change value", NamedTextColor.DARK_GREEN))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build()
            );
            menuActions.put(ClickType.LEFT, p -> {
                settlement.cycleSettingValue(clazz);
                new SettlementSettingsMenu(settlement).open(p);
            });
        }

        settingItem.setData(DataComponentTypes.LORE, loreBuilder.build());
        return new MenuItem(settingItem, menuActions);
    }
}

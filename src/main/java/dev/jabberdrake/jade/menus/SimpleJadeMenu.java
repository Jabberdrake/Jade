package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SimpleJadeMenu implements JadeMenu {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    private Map<Integer, MenuItem> items = new HashMap<>();
    private Inventory inventory;
    private int rows;

    public SimpleJadeMenu(String title, Rows rows) {
        this.rows = rows.getValue();
        this.inventory = Bukkit.createInventory(this, rows.getSize(), Component.text(title));
    }

    @Override
    public void click(Player player, int slot) {
        final Consumer<Player> action = this.items.get(slot).getAction();

        if (action != null) { action.accept(player); }
    }

    @Override
    public void setItem(int slot, MenuItem menuItem) {
        this.items.put(slot, menuItem);
        getInventory().setItem(slot, menuItem.getItem());
    }

    @Override
    public abstract void composeMenu(Player player);

    @Override
    public void update() {
        getInventory().clear();

        for (int i = 0; i < getInventory().getSize(); i++) {
            final ItemStack item = getItems().get(i).getItem();
            if (item != null) {
                getInventory().setItem(i, item);
            }
        }
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        return this.items;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public enum Rows {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6);

        private final int value;
        private final int size;

        Rows(int rows) {
            this.value = rows;
            this.size = rows * 9;
        }

        public int getValue() {
            return this.value;
        }

        public int getSize() {
            return this.size;
        }
    }
}

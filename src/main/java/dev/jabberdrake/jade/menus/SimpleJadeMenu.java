package dev.jabberdrake.jade.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SimpleJadeMenu implements JadeMenu {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    private Map<Integer, MenuItem> items = new HashMap<>();
    private Player player;
    private Inventory inventory;
    private int rows;

    public SimpleJadeMenu(String title, Rows rows) {
        this.rows = rows.getValue();
        this.inventory = Bukkit.createInventory(this, rows.getSize(), Component.text(title));
    }

    @Override
    public void click(Player player, int slot, ClickType clickType) {

        final MenuItem clickedItem = this.getItems().get(slot);
        if (clickedItem != null) {
            final Consumer<Player> action = clickedItem.getAction(clickType);
            if (action != null) {
                action.accept(player);
            }
        }
    }

    @Override
    public void setItem(int slot, MenuItem menuItem) {
        this.items.put(slot, menuItem);
        getInventory().setItem(slot, menuItem.getItemStack());
    }

    @Override
    public abstract void composeMenu();

    @Override
    public void open(Player player) {
        this.player = player;
        composeMenu();
        player.openInventory(getInventory());
    }

    @Override
    public void update() {
        getInventory().clear();

        for (int i = 0; i < getInventory().getSize(); i++) {
            final ItemStack item = getItems().get(i).getItemStack();
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
    public Player getPlayer() {
        return this.player;
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

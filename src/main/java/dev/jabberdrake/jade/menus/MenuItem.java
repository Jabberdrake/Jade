package dev.jabberdrake.jade.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {
    private ItemStack item;
    private Consumer<Player> action;

    public MenuItem(ItemStack item, Consumer<Player> action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Consumer<Player> getAction() {
        return action;
    }

    public void setAction(Consumer<Player> action) {
        this.action = action;
    }
}

package dev.jabberdrake.jade.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuItem {
    private ItemStack item;
    private Map<ClickType, Consumer<Player>> actions;

    public MenuItem(ItemStack item, Consumer<Player> action) {
        this.item = item;
        this.actions = new HashMap<>();
        this.actions.put(ClickType.LEFT, action);
    }

    public MenuItem(ItemStack item, Map<ClickType, Consumer<Player>> actions) {
        this.item = item;
        this.actions = actions;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    public Map<ClickType, Consumer<Player>> getActions() {
        return actions;
    }

    public Consumer<Player> getAction(ClickType clickType) {
        return this.getActions().get(clickType);
    }

    public void setAction(ClickType clickType, Consumer<Player> action) {
        this.getActions().put(clickType, action);
    }
}

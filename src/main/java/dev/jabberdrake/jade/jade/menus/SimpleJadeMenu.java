package dev.jabberdrake.jade.jade.menus;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SimpleJadeMenu implements JadeMenu {

    // Credit to SMCode (https://github.com/SimpleMineCode/) for this implementation!
    private Map<Integer, Consumer<Player>> actions = new HashMap<>();
    private Inventory inventory;
    private int rows;

    public SimpleJadeMenu(String title, Rows rows) {
        this.rows = rows.getValue();
        this.inventory = Bukkit.createInventory(this, rows.getSize(), Component.text(title));
    }

    @Override
    public void click(Player player, int slot) {
        final Consumer<Player> action = this.actions.get(slot);

        if (action != null) { action.accept(player); }
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        this.setItem(slot, item, player -> {});
    }

    @Override
    public void setItem(int slot, ItemStack item, Consumer<Player> action) {
        this.actions.put(slot, action);
        getInventory().setItem(slot, item);
    }

    @Override
    public abstract void composeMenu(Player player);

    @Override
    public void composeErrorMenu(Player player, String message) {
        ItemStack errorItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta errorItemMeta = errorItem.getItemMeta();

        errorItemMeta.displayName(Component.text("An error has occured!", NamedTextColor.RED));
        errorItemMeta.lore(List.of(
                Component.text("If you are seeing this, please", TextUtils.ZORBA),
                Component.text("contact an administrator!", TextUtils.ZORBA),
                Component.newline(),
                Component.text("Details:", TextUtils.ZORBA),
                Component.text("— " + message, TextUtils.DARK_ZORBA)
        ));
        errorItem.setItemMeta(errorItemMeta);


        this.inventory = Bukkit.createInventory(this, 9, Component.text("Error menu"));
        this.inventory.setItem(4, errorItem);
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

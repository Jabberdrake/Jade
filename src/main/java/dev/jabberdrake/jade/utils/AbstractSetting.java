package dev.jabberdrake.jade.utils;

import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public abstract class AbstractSetting<T> {
    private T value;

    public AbstractSetting(T value) {
        this.value = value;
    }

    public abstract String name();
    public abstract String displayName();
    public abstract List<Component> description();
    public abstract void buildValueLore(ItemLore.Builder loreBuilder);
    public abstract NamespacedKey iconKey();
    public abstract T defaultValue();
    public abstract boolean isValidValue(T value);
    public abstract T cycleValue();

    public ItemStack getIconAsItem() { return ItemUtils.asDisplayItemBase(iconKey()); }
    public T getValue() { return this.value; };

    @SuppressWarnings("unchecked")
    public boolean setValue(Object value) {
        if (value != null && isValidValue((T) value)) {
            this.value = (T) value;
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        return name() + "=" + getValue().toString();
    }
}

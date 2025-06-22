package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.Jade;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public abstract class AbstractSetting<T> {
    protected String name;
    protected NamespacedKey key;
    protected T defaultValue;
    protected T[] possibleValues;

    @SafeVarargs
    protected AbstractSetting(String name, T defaultValue, T... possibleValues) {
        this.name = name;
        this.key = Jade.key(name);
        this.defaultValue = defaultValue;
        this.possibleValues = possibleValues;
    }

    public String getName() { return this.name; }
    public NamespacedKey getKey() { return this.key; }
    public T getDefaultValue() { return this.defaultValue; }
    public T[] getPossibleValues() { return this.possibleValues; }

    public abstract PersistentDataType<?, ?> getValueType();

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractSetting) {
            return this.getKey().equals(((AbstractSetting<?>) o).getKey());
        } else return false;
    }
}

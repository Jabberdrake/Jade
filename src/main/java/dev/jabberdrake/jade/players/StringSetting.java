package dev.jabberdrake.jade.players;

import org.bukkit.persistence.PersistentDataType;

public class StringSetting extends AbstractSetting<String> {

    protected StringSetting(String keyString, String defaultValue, String... possibleValues) {
        super(keyString, defaultValue, possibleValues);
    }

    @Override
    public PersistentDataType<?, ?> getValueType() {
        return PersistentDataType.STRING;
    }
}

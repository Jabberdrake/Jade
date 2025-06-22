package dev.jabberdrake.jade.players;

import org.bukkit.persistence.PersistentDataType;

public class BooleanSetting extends AbstractSetting<Boolean> {

    public BooleanSetting(String keyString, boolean defaultValue) {
        super(keyString, defaultValue, true, false);
    }

    @Override
    public PersistentDataType<?, ?> getValueType() {
        return PersistentDataType.BOOLEAN;
    }

}

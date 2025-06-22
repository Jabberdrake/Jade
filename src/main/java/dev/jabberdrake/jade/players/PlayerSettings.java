package dev.jabberdrake.jade.players;

import java.util.ArrayList;
import java.util.List;

public class PlayerSettings {

    public static final List<AbstractSetting<?>> SETTINGS = new ArrayList<>();

    public static final BooleanSetting MUTE_RANDOM_ADVICE = registerSetting("muteRandomAdvice", false);

    public static BooleanSetting registerSetting(String keyString, boolean defaultValue) {
        BooleanSetting setting = new BooleanSetting(keyString, defaultValue);
        SETTINGS.add(setting);
        return setting;
    }

    public static List<AbstractSetting<?>> getAllSettings() {
        return SETTINGS;
    }
}

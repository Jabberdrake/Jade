package dev.jabberdrake.jade.players;

import java.util.ArrayList;
import java.util.List;

public class PlayerSettings {

    public static final List<AbstractSetting<?>> SETTINGS = new ArrayList<>();

    public static final BooleanSetting MUTE_RANDOM_ADVICE = registerSetting("muteRandomAdvice", false);
    public static final StringSetting ANNOUNCE_SETTLEMENT_FORMAT = registerSetting("announceSettlementFormat", "TITLE", "TITLE", "CHAT");

    public static BooleanSetting registerSetting(String keyString, boolean defaultValue) {
        BooleanSetting setting = new BooleanSetting(keyString, defaultValue);
        SETTINGS.add(setting);
        return setting;
    }

    public static StringSetting registerSetting(String keyString, String defaultValue, String... possibleValues) {
        StringSetting setting = new StringSetting(keyString, defaultValue, possibleValues);
        SETTINGS.add(setting);
        return setting;
    }

    public static List<AbstractSetting<?>> getAllSettings() {
        return SETTINGS;
    }
}

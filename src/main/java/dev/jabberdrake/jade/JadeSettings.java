package dev.jabberdrake.jade;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JadeSettings {
    private static Jade jade;
    private static File file;
    private static FileConfiguration config;
    public static final String FILENAME = "settings.yml";

    // GENERIC
    public static String database = "jade.db";
    public static List<String> gameworlds = List.of("world");
    public static int chunkCost = 20;

    // GAMERULES
    public static boolean preventCoralFade = true;


    public static void load(Jade jade) {
        JadeSettings.jade = jade;
        JadeSettings.file = new File(jade.getDataFolder(), "settings.yml");
        JadeSettings.config = YamlConfiguration.loadConfiguration(file);

        // Load generics
        database = config.getString("generic.database");
        chunkCost = config.getInt("generic.chunkCost");
        gameworlds = config.getStringList("generic.gameworlds");

        // Load gamerules
        preventCoralFade = config.getBoolean("gamerules.preventCoralFade");
    }

    public static boolean setGamerule(String gamerule, boolean value) {
        switch (gamerule) {
            case "preventCoralFade":
                preventCoralFade = value;
                config.set("gamerules.preventCoralFade", value);
                JadeSettings.save();
                return true;
            default:
                return false;
        }
    }

    public static void save() {
        try {
            JadeSettings.config.save(JadeSettings.file);

            jade.getLogger().info("[JadeSettings::save] Successfully saved plugin settings!");
        } catch (IOException e) {
            jade.getLogger().info("[JadeSettings::save] Caught IOException while attempting to save plugin settings! Continuing...");
            e.printStackTrace();
        }
    }
}

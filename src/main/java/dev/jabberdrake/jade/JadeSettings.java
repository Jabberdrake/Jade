package dev.jabberdrake.jade;

import dev.jabberdrake.jade.utils.Road;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public static boolean enablePlayerGraves = true;
    public static boolean enableSpeedRoads = true;

    // OTHER
    public static List<Road> roads = new ArrayList<>();

    public static void load(Jade jade) {
        JadeSettings.jade = jade;
        JadeSettings.file = new File(jade.getDataFolder(), "settings.yml");
        JadeSettings.config = YamlConfiguration.loadConfiguration(file);

        // Load generics
        database = config.getString("generic.database", "jade.db");
        chunkCost = config.getInt("generic.chunkCost", 20);
        gameworlds = config.getStringList("generic.gameworlds");

        // Load gamerules
        preventCoralFade = config.getBoolean("gamerules.preventCoralFade", true);
        enablePlayerGraves = config.getBoolean("gamerules.enablePlayerGraves", true);
        enableSpeedRoads = config.getBoolean("gamerules.enableSpeedRoads", true);

        // Load roads
        List<String> roadEntries = config.getStringList("roads");
        outer: for (String entry : roadEntries) {
            String[] parts = entry.split(";");
            if (parts.length != 3 && parts.length != 4) {
                jade.getLogger().info("[JadeSettings::load] Invalid format for road entry in \"" + entry + "\". Skipping...");
                continue;
            }

            Material top = parseRoadMaterial(parts[0]);
            Material middle = parseRoadMaterial(parts[1]);
            Material bottom = parseRoadMaterial(parts[2]);

            if (top == null && middle == null && bottom == null) {
                jade.getLogger().info("[JadeSettings::load] Invalid content for road entry in \"" + entry + "\". Skipping...");
                continue;
            }

            Road newRoad;
            if (parts.length == 4) {
                double speed;
                try {
                    speed = Double.parseDouble(parts[3]);
                    if (speed <= 0.0) {
                        jade.getLogger().info("[JadeSettings::load] Found illegal argument while parsing explicit road speed modifier in \"" + entry + "\". Skipping...");
                        break;
                    }
                } catch (NumberFormatException e) {
                    jade.getLogger().info("[JadeSettings::load] Caught NumberFormatException while parsing explicit road speed modifier in \"" + entry + "\". Defaulting to 1...");
                    speed = 1.0;
                }
                newRoad = new Road(speed, top, middle, bottom);
            } else {
                newRoad = new Road(top, middle, bottom);
            }
            for (Road road : roads) {
                if (road.equals(newRoad)) {
                    jade.getLogger().info("[JadeSettings::load] Found duplicate road entry in \"" + entry + "\". Skipping...");
                    continue outer;
                }
            }

            jade.getLogger().info("[JadeSettings::load] Adding new road: " + newRoad);
            roads.add(newRoad);
        }
    }

    public static boolean setGamerule(String gamerule, boolean value) {
        switch (gamerule) {
            case "preventCoralFade":
                preventCoralFade = value;
                config.set("gamerules.preventCoralFade", value);
                JadeSettings.save();
                return true;
            case "enablePlayerGraves":
                enablePlayerGraves = value;
                config.set("gamerules.enablePlayerGraves", value);
                JadeSettings.save();
                return true;
            case "enableSpeedRoads":
                enableSpeedRoads = value;
                config.set("gamerules.enableSpeedRoads", value);
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

    public static Material parseRoadMaterial(String entry) {
        if (entry.equalsIgnoreCase("ANY") || entry.equalsIgnoreCase("NULL") || entry.equalsIgnoreCase("*") || entry.equalsIgnoreCase("EMPTY")) {
            return null;
        }

        Material aux = Material.matchMaterial(entry);
        if (aux == null) {
            jade.getLogger().info("[JadeSettings::parseRoadMaterial] Found unknown road material in settings: \"" + entry + "\". Defaulting to ANY...");
            return null;
        }

        return aux;
    }
}

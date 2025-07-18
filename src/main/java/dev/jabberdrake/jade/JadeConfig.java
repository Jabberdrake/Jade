package dev.jabberdrake.jade;

import dev.jabberdrake.jade.utils.Road;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JadeConfig {
    private static Jade jade;
    private static Logger logger;
    private static File file;
    private static FileConfiguration config;
    public static final String FILENAME = "config.yml";

    // GENERIC
    public static String database = "jade.db";
    public static List<String> gameworlds = List.of("world");
    public static int chunkCost = 20;

    // GAMERULES
    public static boolean preventCoralFading = true;
    public static boolean enablePlayerGraves = true;
    public static boolean enableSpeedRoads = true;
    public static boolean sayRandomAdvice = true;
    public static boolean doMonsterGriefing = false;

    // OTHER
    public static List<Road> roads = new ArrayList<>();

    public static void load(Jade jade) {
        JadeConfig.logger = Jade.getInstance().getLogger();
        JadeConfig.file = new File(jade.getDataFolder(), FILENAME);
        JadeConfig.config = YamlConfiguration.loadConfiguration(file);

        // Load generics
        database = config.getString("generic.database", "jade.db");
        chunkCost = config.getInt("generic.chunkCost", 20);
        gameworlds = config.getStringList("generic.gameworlds");
        for (String gameworld : gameworlds) {
            logger.info("[JadeConfig::load] Adding new gameworld \"" + gameworld + "\"");
        }

        // Load gamerules
        preventCoralFading = config.getBoolean("gamerules.preventCoralFading", true);
        enablePlayerGraves = config.getBoolean("gamerules.enablePlayerGraves", true);
        enableSpeedRoads = config.getBoolean("gamerules.enableSpeedRoads", true);
        sayRandomAdvice = config.getBoolean("gamerules.sayRandomAdvice", true);
        doMonsterGriefing = config.getBoolean("gamerules.doMonsterGriefing", false);

        // Load roads
        List<String> roadEntries = config.getStringList("roads");
        outer: for (String entry : roadEntries) {
            String[] parts = entry.split(";");
            if (parts.length != 3 && parts.length != 4) {
                logger.info("[JadeConfig::load] Invalid format for road entry in \"" + entry + "\". Skipping...");
                continue;
            }

            Material top = parseRoadMaterial(parts[0]);
            Material middle = parseRoadMaterial(parts[1]);
            Material bottom = parseRoadMaterial(parts[2]);

            if (top == null && middle == null && bottom == null) {
                logger.info("[JadeConfig::load] Invalid content for road entry in \"" + entry + "\". Skipping...");
                continue;
            }

            Road newRoad;
            if (parts.length == 4) {
                double speed;
                try {
                    speed = Double.parseDouble(parts[3]);
                    if (speed <= 0.0) {
                        logger.info("[JadeConfig::load] Found illegal argument while parsing explicit road speed modifier in \"" + entry + "\". Skipping...");
                        break;
                    }
                } catch (NumberFormatException e) {
                    logger.info("[JadeConfig::load] Caught NumberFormatException while parsing explicit road speed modifier in \"" + entry + "\". Defaulting to 1...");
                    speed = 1.0;
                }
                newRoad = new Road(speed, top, middle, bottom);
            } else {
                newRoad = new Road(top, middle, bottom);
            }
            for (Road road : roads) {
                if (road.equals(newRoad)) {
                    logger.info("[JadeConfig::load] Found duplicate road entry in \"" + entry + "\". Skipping...");
                    continue outer;
                }
            }

            logger.info("[JadeConfig::load] Adding new road: " + newRoad);
            roads.add(newRoad);
        }
    }

    public static boolean setGamerule(String gamerule, boolean value) {
        switch (gamerule) {
            case "preventCoralFading":
                preventCoralFading = value;
                config.set("gamerules.preventCoralFading", value);
                JadeConfig.save();
                return true;
            case "enablePlayerGraves":
                enablePlayerGraves = value;
                config.set("gamerules.enablePlayerGraves", value);
                JadeConfig.save();
                return true;
            case "enableSpeedRoads":
                enableSpeedRoads = value;
                config.set("gamerules.enableSpeedRoads", value);
                JadeConfig.save();
                return true;
            case "sayRandomAdvice":
                sayRandomAdvice = value;
                config.set("gamerules.sayRandomAdvice", value);
                JadeConfig.save();
                return true;
            case "doMonsterGriefing":
                doMonsterGriefing = value;
                config.set("gamerules.doMonsterGriefing", value);
                JadeConfig.save();
                return true;
            default:
                return false;
        }
    }

    public static void save() {
        try {
            JadeConfig.config.save(JadeConfig.file);

            logger.info("[JadeConfig::save] Successfully saved plugin settings!");
        } catch (IOException e) {
            logger.info("[JadeConfig::save] Caught IOException while attempting to save plugin settings! Continuing...");
            e.printStackTrace();
        }
    }

    public static Material parseRoadMaterial(String entry) {
        if (entry.equalsIgnoreCase("ANY") || entry.equalsIgnoreCase("NULL") || entry.equalsIgnoreCase("*") || entry.equalsIgnoreCase("EMPTY")) {
            return null;
        }

        Material aux = Material.matchMaterial(entry);
        if (aux == null) {
            logger.info("[JadeConfig::parseRoadMaterial] Found unknown road material in settings: \"" + entry + "\". Defaulting to ANY...");
            return null;
        }

        return aux;
    }
}

package dev.jabberdrake.jade.building;

import de.bluecolored.bluemap.api.WebApp;
import dev.jabberdrake.jade.Jade;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BuildingManager {

    private static Jade plugin;
    private static Logger logger;

    private static Map<String, Warp> WARPS = new HashMap<>();

    public static void initialize() {
        plugin = Jade.getInstance();
        logger = Jade.getInstance().getLogger();

        File warpsDir = new File(plugin.getDataFolder(), "warps");
        if (!warpsDir.exists()) {
            logger.warning("[BuildingManager::initialize] Could not find warps directory! Halting initialization process...");
            return;
        }

        int warpsLoaded = 0;
        for (File warpFile : warpsDir.listFiles()) {
            Warp newWarp = Warp.load(warpFile);
            WARPS.put(newWarp.getName(), newWarp);
            warpsLoaded = warpsLoaded + 1;
        }
        logger.info("[BuildingManager::initialize] Loaded " + warpsLoaded + " warps!");
    }

    public static void shutdown() {
        for (Warp warp : WARPS.values()) {
            try {
                warp.store();
            } catch (IOException e) {
                logger.warning("[BuildingManager::shutdown] Failed to store warp " + warp.getName() + ":");
                e.printStackTrace();
            }
        }
    }

    public static Warp getWarp(String name) {
        for (String storedName : WARPS.keySet()) {
            if (storedName.equalsIgnoreCase(name)) {
                return WARPS.get(storedName);
            }
        }
        return null;
    }

    public static List<Warp> getAllWarps() {
        return WARPS.values().stream().toList();
    }

    public static Warp createWarp(String name, Player player) {
        Warp warp = new Warp(name, player);

        try {
            warp.store();
        } catch (IOException e) {
            logger.warning("[BuildingManager::createWarp] Caught IOException while attempting to store newly created warp:");
            e.printStackTrace();
        }
        WARPS.put(warp.getName(), warp);
        return warp;
    }

    public static void deleteWarp(Warp warp) {
        warp.delete();
        WARPS.remove(warp.getName());
    }

}

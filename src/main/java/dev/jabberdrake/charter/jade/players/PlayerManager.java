package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.Charter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PlayerManager {

    private static final Charter plugin = Charter.getPlugin(Charter.class);
    private static final Logger logger = plugin.getLogger();

    private static final String PROFILE_FOLDER = "/profiles/";

    private static Map<UUID, JadePlayer> cache = new HashMap<>();

    public static void initialize() {

    }

    public static void shutdown() {

    }

    public static JadePlayer parsePlayer(UUID uuid) {
        return cache.getOrDefault(uuid, null);
    }

    public static JadeProfile fetchProfile(UUID uuid) {
        return cache.getOrDefault(uuid, null).getProfile();
    }

    public static String getProfileFilepath(UUID uuid) {
        return Charter.getPlugin(Charter.class).getDataFolder() + PROFILE_FOLDER + uuid.toString() + ".yml";
    }

    public static boolean loadProfile(UUID uuid) {

        // Profile files should always be in '/Charter/profiles/<uuid>.yml'
        String pathname = getProfileFilepath(uuid);
        File profileFile = new File(pathname);

        if (!profileFile.exists()) {
            // If no matching profile file was found, it's probably the first login.
            JadeProfile profile = new JadeProfile(uuid);
            JadePlayer jadePlayer = new JadePlayer(uuid, profile);

            PlayerManager.cache.put(uuid, jadePlayer);
            logger.info("[PlayerManager::loadProfile] Created a new profile for uuid=" + uuid + "!");
            return true;
        }

        // If a matching profile file was found, compose the profile object from the data therein.
        FileConfiguration data = YamlConfiguration.loadConfiguration(profileFile);
        JadeProfile profile = JadeProfile.load(data, "profile");

        JadePlayer jadePlayer = new JadePlayer(uuid, profile);
        PlayerManager.cache.put(uuid, jadePlayer);
        return true;
    }

    public static boolean storeProfile(UUID uuid) {
        String pathname = getProfileFilepath(uuid);
        File profileFile = new File(pathname);
        FileConfiguration data = YamlConfiguration.loadConfiguration(profileFile);

        JadeProfile profile = PlayerManager.fetchProfile(uuid);
        JadeProfile.store(profile, data, "profile");

        try {
            data.save(profileFile);
        } catch (IOException e) {
            logger.warning("[PlayerManager::storeProfile] Failed to save data for player profile (UUID=" + uuid + ")!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

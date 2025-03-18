package dev.jabberdrake.charter.jade.titles;

import dev.jabberdrake.charter.Charter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class TitleManager {

    private static final Charter plugin = Charter.getPlugin(Charter.class);
    private static final Logger logger = plugin.getLogger();

    private static final String TITLE_FOLDER = "/titles/";

    private static Map<String, JadeTitle> cache = new HashMap<>();

    public static void initialize() {

    }

    public static void shutdown() {

    }

    public static JadeTitle fetchTitle(String serializedTitle) {
        // Check the cache
        if (cache.containsKey(serializedTitle)) {
            return cache.get(serializedTitle);
        } else {
            // If it's not in the cache, it must be in the disk
            // Otherwise, it doesn't exist.
            TitleManager.loadTitle(serializedTitle);
            return cache.getOrDefault(serializedTitle, null);
        }
    }

    public static String getTitleFilepath(String serializedTitle) {
        String[] parts = serializedTitle.split("@");
        String filename = parts[0].toUpperCase(Locale.ROOT) + "@" + parts[1];
        return Charter.getPlugin(Charter.class).getDataFolder() + TITLE_FOLDER + filename + ".yml";
    }

    public static boolean loadTitle(String serializedTitle) {

        // Title files should always be in '/Charter/titles/<title_name>@<owner_uuid>.yml'
        String filepath = getTitleFilepath(serializedTitle);
        File titleFile = new File(filepath);
        if (!titleFile.exists()) {
            logger.warning("[TitleManager::loadTitle] Attempted to load non-existing title: " + serializedTitle);
            return false;
        }

        // Compose JadeTitle object from file data
        FileConfiguration data = YamlConfiguration.loadConfiguration(titleFile);
        JadeTitle matchingTitle = JadeTitle.load(data, "title");

        // Cache it to avoid having to read the file again
        cache.put(serializedTitle, matchingTitle);
        return true;
    }

    public static boolean storeTitle(JadeTitle title) {
        String filepath = getTitleFilepath(title.serialize());
        File titleFile = new File(filepath);
        FileConfiguration data = YamlConfiguration.loadConfiguration(titleFile);

        JadeTitle.store(title, data, "title");

        try {
            data.save(titleFile);
        } catch (IOException e) {
            logger.warning("[TitleManager::storeTitle] Failed to save data for vanity title: (" + title.serialize() +")");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

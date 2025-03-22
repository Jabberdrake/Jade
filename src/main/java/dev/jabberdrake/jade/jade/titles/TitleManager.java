package dev.jabberdrake.jade.jade.titles;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.jade.players.JadePlayer;
import dev.jabberdrake.jade.jade.players.JadeProfile;
import dev.jabberdrake.jade.jade.players.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TitleManager {

    private static final Jade plugin = Jade.getPlugin(Jade.class);
    private static final Logger logger = plugin.getLogger();

    private static final String TITLE_FOLDER = "/titles/";

    private static Map<String, JadeTitle> cache = new HashMap<>();

    // a "dirty" object is an object that has been modified during runtime,
    // and should therefore be written to the files during shutdown
    private static List<JadeTitle> dirtyTitles = new ArrayList<>();

    public static void initialize() {
        // Nothing to do on startup, as profile loading is handled by the PlayerManager

        // Plus, there is no point in loading *every single title*, as we will only need
        // to handle those available to online players.
    }

    public static void shutdown() {
        logger.info("[TitleManager::shutdown] Storing all edited titles to disk...");
        for (JadeTitle dirtyTitle : dirtyTitles) {
            TitleManager.storeTitle(dirtyTitle);
        }
    }

    public static JadeTitle createTitle(String name, TextColor color, UUID owner) {
        Component titleComponent = Component.text(name, color);
        JadeTitle title = new JadeTitle(name, JadeTitle.serializeTitle(titleComponent), owner);

        JadeProfile profile = PlayerManager.fetchProfile(owner);
        profile.addTitle(title);

        TitleManager.cache.put(name, title);
        TitleManager.markAsDirty(title);
        return title;
    }

    public static void deleteTitle(JadeTitle title) {
        String titleName = title.getName();
        TitleManager.cache.remove(titleName);

        for (JadePlayer jadePlayer : PlayerManager.getAllOnlinePlayers()) {
            jadePlayer.getProfile().removeTitle(title);
        }

        String matchingFilepath = TitleManager.getTitleFilepath(title.serialize());
        File matchingFile = new File(matchingFilepath);
        if (!matchingFile.delete()) {
            logger.warning("[TitleManager::deleteTitle] Failed to delete file " + matchingFilepath);
        }
    }

    public static boolean allowUseOfTitle(JadeTitle title, UUID owner, UUID target) {
        JadeProfile targetProfile = PlayerManager.fetchProfile(target);
        if (!title.getOwner().equals(owner)) {
            return false;
        } else if (targetProfile.canUseTitle(title)) {
            return false;
        }

        title.addUser(target);
        targetProfile.addTitle(title);
        return true;
    }

    public static boolean disallowUseOfTitle(JadeTitle title, UUID owner, UUID target) {
        JadeProfile targetProfile = PlayerManager.fetchProfile(target);
        if (!title.getOwner().equals(owner)) {
            return false;
        } else if (!targetProfile.canUseTitle(title)) {
            return false;
        }

        title.removeUser(target);
        targetProfile.removeTitle(title);
        return true;
    }

    public static void renameTitle(JadeTitle title, String newName, String newTitle) {
        if (!newName.equals(title.getName())) {
            String matchingFilepath = TitleManager.getTitleFilepath(title.serialize());
            File matchingFile = new File(matchingFilepath);
            if (!matchingFile.delete()) {
                logger.warning("[TitleManager::renameTitle] Failed to delete file " + matchingFilepath);
            }
        }

        title.setName(newName);
        title.setTitle(newTitle);

        TitleManager.markAsDirty(title);
    }

    public static boolean isUniqueName(UUID owner, String potentialName) {
        for (JadeTitle title : PlayerManager.fetchProfile(owner).getAvailableTitles()) {
            if (title.getName().equals(potentialName)) {
                return false;
            }
        }
        return true;
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
        return Jade.getPlugin(Jade.class).getDataFolder() + TITLE_FOLDER + filename + ".yml";
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

    public static void markAsDirty(JadeTitle title) {
        if (!TitleManager.dirtyTitles.contains(title)) {
            TitleManager.dirtyTitles.add(title);
        }
    }
}

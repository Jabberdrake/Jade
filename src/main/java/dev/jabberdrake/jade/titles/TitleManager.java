package dev.jabberdrake.jade.titles;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private static Map<Integer, JadeTitle> cache = new HashMap<>();

    public static void initialize() {
        // Nothing to do on startup, as profile loading is handled by the PlayerManager

        // Plus, there is no point in loading *every single title*, as we will only need
        // to handle those available to online players.
    }

    public static void shutdown() {
        // Nothing to do here.
    }

    public static JadeTitle createTitle(String name, TextColor color, UUID owner) {
        Component titleComponent = Component.text(name, color);
        JadeTitle title = new JadeTitle(name, JadeTitle.serializeTitle(titleComponent), owner, NamedTextColor.WHITE, "minecraft:book");

        cache.put(title.getId(), title);
        return title;
    }

    public static JadeTitle getTitle(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            JadeTitle fetchedTitle = DatabaseManager.fetchTitleById(id);
            if (fetchedTitle != null) {
                cache.put(id, fetchedTitle);
                return fetchedTitle;
            } else {
                plugin.getLogger().warning("[TitleManager::getTitle] Attempted to fetch unknown title for ID=" + id);
                return null;
            }
        }
    }

    public static void deleteTitle(JadeTitle title) {
        DatabaseManager.deleteTitle(title.getId());
        PlayerManager.unsetTitleForAllPlayers(title);

        cache.remove(title.getId());
    }

    public static List<JadeTitle> getAllTitlesOwnedBy(UUID ownerID) {
        List<Integer> titleIDList = DatabaseManager.fetchAllTitlesOwnedBy(ownerID);
        List<JadeTitle> titleList = new ArrayList<>(titleIDList.size());

        for (int id : titleIDList) {
            titleList.add(TitleManager.getTitle(id));
        }
        return titleList;
    }

    public static List<JadeTitle> getAllTitlesAvailableTo(UUID playerID) {
        List<Integer> titleIDList = DatabaseManager.fetchAllTitlesAvailableTo(playerID);

        List<JadeTitle> titleList = new ArrayList<>(titleIDList.size());

        for (int id : titleIDList) {
            titleList.add(TitleManager.getTitle(id));
        }
        return titleList;
    }

    public static List<JadeTitle> getAllUniversalTitles() {
        List<Integer> titleIDList = DatabaseManager.fetchAllUniversalTitles();

        List<JadeTitle> titleList = new ArrayList<>(titleIDList.size());

        for (int id : titleIDList) {
            titleList.add(TitleManager.getTitle(id));
        }
        return titleList;
    }

    public static boolean allowUseOfTitle(JadeTitle title, UUID owner, UUID target) {
        JadePlayer targetPlayer = PlayerManager.asJadePlayer(target);
        if (!title.getOwner().equals(owner)) {
            return false;
        } else if (targetPlayer.canUseTitle(title)) {
            return false;
        }

        title.addUser(target);
        return true;
    }

    public static boolean disallowUseOfTitle(JadeTitle title, UUID owner, UUID target) {
        JadePlayer targetPlayer = PlayerManager.asJadePlayer(target);
        if (!title.getOwner().equals(owner)) {
            return false;
        } else if (!targetPlayer.canUseTitle(title)) {
            return false;
        }

        title.removeUser(target);
        return true;
    }

    public static boolean isUniqueName(String potentialName) {
        return DatabaseManager.checkForUniqueTitleName(potentialName);
    }
}

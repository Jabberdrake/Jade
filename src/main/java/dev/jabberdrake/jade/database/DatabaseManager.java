package dev.jabberdrake.jade.database;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.database.daos.NationDataObject;
import dev.jabberdrake.jade.database.daos.PlayerDataObject;
import dev.jabberdrake.jade.database.daos.SettlementDataObject;
import dev.jabberdrake.jade.database.daos.TitleDataObject;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.JadeTitle;
import net.kyori.adventure.text.format.TextColor;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {
    private static Jade plugin;
    public static SQLiteDataSource source = null;

    private static Database database = null;
    private static Map<Class<?>, DatabaseObject<?, ?>> dataObjectRegistry = new HashMap<>();

    public static void initialize(Jade plugin) {
        DatabaseManager.plugin = plugin;
        DatabaseManager.source = new SQLiteDataSource();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File dataFile = new File(dataFolder, JadeSettings.database);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        DatabaseManager.source.setUrl("jdbc:sqlite:" + dataFile);
        try {
            database = new Database(source.getConnection());
            plugin.getLogger().info("[DatabaseManager::initialize] Connected to database at " + JadeSettings.database + "!");
        } catch (SQLException e) {
            plugin.getLogger().severe("[DatabaseManager::initialize] Severe error occured during database initialization:\n\t" + e.getMessage());
            e.printStackTrace();
        }

        dataObjectRegistry.put(JadePlayer.class, new PlayerDataObject(plugin, database));
        dataObjectRegistry.put(JadeTitle.class, new TitleDataObject(plugin, database));
        dataObjectRegistry.put(Settlement.class, new SettlementDataObject(plugin, database));
        dataObjectRegistry.put(Nation.class, new NationDataObject(plugin, database));

        plugin.getLogger().info("[DatabaseManager::initialize] Finished database initialization!");
    }

    // DATA OBJECT GETTERS
    public static PlayerDataObject getPlayerDao() {
        return (PlayerDataObject) dataObjectRegistry.get(PlayerDataObject.class);
    }

    public static TitleDataObject getTitleDao() {
        return (TitleDataObject) dataObjectRegistry.get(JadeTitle.class);
    }

    // PLAYER METHODS
    public static void createPlayer(JadePlayer player) {
        getPlayerDao().create(player);
    }

    public static JadePlayer fetchPlayerByUUID(UUID id) {
        return getPlayerDao().fetch(id);
    }

    public static void savePlayer(JadePlayer player) {
        getPlayerDao().save(player);
    }

    public static void unsetTitleForAllPlayers(JadeTitle title) {
        getPlayerDao().unsetTitleForAllPlayers(title);
    }

    // TITLE METHODS
    public static int createTitle(JadeTitle title) {
        return getTitleDao().create(title);
    }

    public static JadeTitle fetchTitleById(int id) {
        return getTitleDao().fetch(id);
    }

    public static void saveTitle(JadeTitle title) {
        getTitleDao().save(title);
    }

    public static void makeTitleUniversal(JadeTitle title) {
        getTitleDao().makeTitleUniversal(title.getId());
    }

    public static List<JadeTitle> fetchAllTitlesOwnedBy(UUID ownerID) {
        return getTitleDao().fetchAllTitlesOwnedBy(ownerID);
    }

    public static List<JadeTitle> fetchAllTitlesAvailableTo(UUID playerID) {
        return getTitleDao().fetchAllTitlesAvailableTo(playerID);
    }

    public static List<JadeTitle> fetchAllUniversalTitles() {
        return getTitleDao().fetchAllUniversalTitles();
    }

    public static boolean checkForUniqueTitleName(String name) {
        return getTitleDao().checkForUniqueName(name);
    }

    public static void addPlayerToTitle(JadeTitle title, UUID playerID) {
        getTitleDao().addPlayerToTitle(title, playerID);
    }

    public static void removePlayerFromTitle(JadeTitle title, UUID playerID) {
        getTitleDao().removePlayerFromTitle(title, playerID);
    }

    public static void deleteTitle(int id) {
        getTitleDao().delete(id);
    }

    public static void shutdown() {
        try {
            if (source.getConnection() != null && !source.getConnection().isClosed()) {
                source.getConnection().close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[DatabaseManager::shutdown] " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

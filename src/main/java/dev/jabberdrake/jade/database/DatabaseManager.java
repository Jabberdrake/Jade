package dev.jabberdrake.jade.database;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.database.daos.*;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.realms.*;
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

    public static void initialize() {
        DatabaseManager.plugin = Jade.getInstance();
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
        dataObjectRegistry.put(Area.class, new AreaDataObject(plugin, database));
        dataObjectRegistry.put(Grave.class, new GraveDataObject(plugin, database));

        plugin.getLogger().info("[DatabaseManager::initialize] Finished database initialization!");
    }

    // DATA OBJECT GETTERS
    public static PlayerDataObject getPlayerDao() {
        return (PlayerDataObject) dataObjectRegistry.get(JadePlayer.class);
    }

    public static TitleDataObject getTitleDao() {
        return (TitleDataObject) dataObjectRegistry.get(JadeTitle.class);
    }

    public static SettlementDataObject getSettlementDao() {
        return (SettlementDataObject) dataObjectRegistry.get(Settlement.class);
    }

    public static NationDataObject getNationDao() {
        return (NationDataObject) dataObjectRegistry.get(Nation.class);
    }

    public static AreaDataObject getAreaDao() {
        return (AreaDataObject) dataObjectRegistry.get(Area.class);
    }

    public static GraveDataObject getGraveDao() {
        return (GraveDataObject) dataObjectRegistry.get(Grave.class);
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

    public static List<Integer> fetchAllTitlesOwnedBy(UUID ownerID) {
        return getTitleDao().fetchAllTitlesOwnedBy(ownerID);
    }

    public static List<Integer> fetchAllTitlesAvailableTo(UUID playerID) {
        return getTitleDao().fetchAllTitlesAvailableTo(playerID);
    }

    public static List<Integer> fetchAllUniversalTitles() {
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

    // SETTLEMENT METHODS
    public static int createSettlement(Settlement settlement) {
        return getSettlementDao().create(settlement);
    }

    public static Settlement fetchSettlementById(int id) {
        return getSettlementDao().fetch(id);
    }

    public static Settlement fetchSettlementByName(String name) {
        return getSettlementDao().fetchByName(name);
    }

    public static List<Settlement> fetchAllSettlements() {
        return getSettlementDao().fetchAll();
    }

    public static void saveSettlement(Settlement settlement) {
        getSettlementDao().save(settlement);
    }

    public static void deleteSettlement(int id) {
        getSettlementDao().delete(id);
    }

    public static int createSettlementRole(SettlementRole role) {
        return getSettlementDao().createSettlementRole(role);
    }

    public static void saveSettlementRole(SettlementRole role) {
        getSettlementDao().saveSettlementRole(role);
    }

    public static void deleteSettlementRole(SettlementRole role) {
        getSettlementDao().deleteSettlementRole(role);
    }

    public static void addPlayerToSettlement(UUID playerID, Settlement settlement, SettlementRole role) {
        getSettlementDao().addPlayerToSettlement(playerID, settlement, role);
    }

    public static void removePlayerFromSettlement(UUID playerID, Settlement settlement) {
        getSettlementDao().removePlayerFromSettlement(playerID, settlement);
    }

    public static void alterPlayerForSettlement(UUID playerID, Settlement settlement, SettlementRole role) {
        getSettlementDao().alterPlayerForSettlement(playerID, settlement, role);
    }

    public static void addChunkToSettlement(ChunkAnchor anchor, Settlement settlement) {
        getSettlementDao().addChunkToSettlement(anchor, settlement);
    }

    public static void removeChunkFromSettlement(ChunkAnchor anchor, Settlement settlement) {
        getSettlementDao().removeChunkFromSettlement(anchor, settlement);
    }

    public static Map<ChunkAnchor, Settlement> fetchTerritoryMap(List<Settlement> settlements) {
        return getSettlementDao().fetchTerritoryMap(settlements);
    }

    // NATION METHODS
    public static int createNation(Nation nation) {
        return getNationDao().create(nation);
    }

    public static Nation fetchNationById(int id) {
        return getNationDao().fetch(id);
    }

    public static Nation fetchNationByName(String name) {
        return getNationDao().fetchByName(name);
    }

    public static List<Nation> fetchAllNations() {
        return getNationDao().fetchAll();
    }

    public static void saveNation(Nation nation) {
        getNationDao().save(nation);
    }

    public static void deleteNation(int id) {
        getNationDao().delete(id);
    }

    public static void addMemberToNation(Settlement settlement, Nation nation) {
        getNationDao().addMemberToNation(settlement, nation);
    }

    public static void removeMemberFromNation(Settlement settlement, Nation nation) {
        getNationDao().removeMemberFromNation(settlement, nation);
    }

    public static void alterCapitalForNation(Settlement settlement, Nation nation) {
        getNationDao().alterCapitalForNation(settlement, nation);
    }

    // AREA METHODS
    public static int createArea(Area area) {
        return getAreaDao().create(area);
    }

    public static Area fetchAreaByID(int id) {
        return getAreaDao().fetch(id);
    }

    public static List<Area> fetchAllAreas() {
        return getAreaDao().fetchAll();
    }

    public static void saveArea(Area area) {
        getAreaDao().save(area);
    }

    public static void deleteArea(int id) {
        getAreaDao().delete(id);
    }

    public static void addMemberToArea(UUID memberID, Area area) {
        getAreaDao().addMemberToArea(memberID, area);
    }

    public static void removeMemberFromArea(UUID memberID, Area area) {
        getAreaDao().removeMemberFromArea(memberID, area);
    }

    // GRAVE METHODS
    public static void createGrave(Grave grave) {
        getGraveDao().create(grave);
    }

    public static Grave fetchGraveByID(String id) {
        return getGraveDao().fetch(id);
    }

    public static List<Grave> fetchAllGraves() {
        return getGraveDao().fetchAll();
    }

    public static void saveGrave(Grave grave) {
        getGraveDao().save(grave);
    }

    public static void deleteGrave(String id) {
        getGraveDao().delete(id);
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

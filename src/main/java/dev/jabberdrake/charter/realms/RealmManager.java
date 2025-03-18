package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class RealmManager {

    private static final Charter plugin = Charter.getPlugin(Charter.class);
    private static final Logger logger = plugin.getLogger();

    private static final String SETTLEMENT_FOLDER = "/settlements/";
    private static final String SETTLEMENT_FILE_PREFIX = "stm-";
    private static final String NATION_FOLDER = "/nations/";
    private static final String NATION_FILE_PREFIX = "nat-";

    private static final String MANIFEST_FILENAME = "/manifest.yml";

    private static int settlementCount;
    private static int nationCount;

    // a "dirty" realm is a realm that has been edited, and whose edits have not been written on the disk
    private static List<Settlement> dirtySettlements;

    private static Map<Integer, Settlement> settlementMap = new HashMap<>();
    // ADD NATION MAP

    private static Map<ChunkAnchor, Settlement> territoryMap = new HashMap<>();

    public static void initialize() {
        if (loadManifest()) {
            logger.info("[RealmManager::initialize] Successfully loaded " + settlementCount + " settlements and " + nationCount + " nations!");
        } else {
            logger.warning("[RealmManager::initialize] Manifest loading failed! No realms were loaded...");
        }
    }

    public static void shutdown() {
        logger.info("[RealmManager::shutdown] Storing all edited realms to disk...");
        for (Settlement dirtySettlement : dirtySettlements) {
            RealmManager.storeSettlement(dirtySettlement);
        }

        storeManifest();
    }

    public static int getSettlementCount() {
        return settlementCount;
    }

    public static int getNationCount() {
        return nationCount;
    }

    public static int incrementSettlementCount() {
        return settlementCount++;
    }

    public static int incrementNationCount() {
        return nationCount++;
    }

    public static String getSettlementFilepath(int id) {
        return Charter.getPlugin(Charter.class).getDataFolder() + SETTLEMENT_FOLDER + SETTLEMENT_FILE_PREFIX + String.valueOf(id) + ".yml";
    }

    public static String trimSettlementPrefix(String filename) {
        if (filename.startsWith(SETTLEMENT_FILE_PREFIX)) {
            return filename.substring(SETTLEMENT_FILE_PREFIX.length());
        } else {
            return filename;
        }
    }

    public static Settlement loadSettlement(int id) {
        // Get matching filepath for ID in argument, and attempt to read the file
        // If no matching file is found, quit
        String pathname = getSettlementFilepath(id);
        File settlementFile = new File(pathname);
        if (!settlementFile.exists()) { return null; }

        // Start building Settlement object, starting with basic info
        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        int stmID = data.getInt("settlement.id");
        String stmName = data.getString("settlement.name");
        String stmStyle = data.getString("settlement.style");
        String stmDescription = data.getString("settlement.description");

        Settlement stm = new Settlement(stmID, stmName, stmStyle, stmDescription);

        // Build title list for Settlement
        List<String> readTitles = data.getStringList("settlement.titles");
        for (String readTitle : readTitles) {
            stm.addTitle(CharterTitle.fromString(readTitle, stm));
        }

        // Build population map for Settlement
        List<String> readPopulation = data.getStringList("settlement.population");
        for (String readCitizen : readPopulation) {
            String[] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            CharterTitle fetchedTitle = stm.getTitleFromName(parts[1]);

            stm.addCitizen(convertedUUID, fetchedTitle);
        }

        // Build chunk anchor set for Settlement
        List<String> readTerritory = data.getStringList("settlement.territory");
        for (String readChunk: readTerritory) {
            String[] parts = readChunk.split(";");
            int readX = Integer.parseInt(parts[0]);
            int readZ = Integer.parseInt(parts[1]);
            ChunkAnchor anchor = new ChunkAnchor(readX, readZ);
            stm.addTerritory(anchor);
            if (RealmManager.territoryMap.containsKey(anchor)) {
                Settlement owner = RealmManager.territoryMap.get(anchor);
                if (!owner.equals(stm)) {
                    logger.warning("[RealmManager::loadSettlementFromID] Chunk at [x=" + readX + ", z=" + readZ + "] claimed by both " + owner.getName() + " and " + stm.getName() + "!");
                }
            } else {
                RealmManager.territoryMap.put(anchor, stm);
            }
        }

        return stm;
    }

    public static void storeSettlement(Settlement settlement) {
        int ID = settlement.getId();
        String name = settlement.getName();
        String style = settlement.getStyle();
        String description = settlement.getDescription();

        logger.info("[RealmManager::storeSettlement] Storing settlement ID=" + ID + " (" + name + ")...");

        String pathname = getSettlementFilepath(ID);
        File settlementFile = new File(pathname);

        List<String> preparedTitleStrings = new ArrayList<>();
        for (CharterTitle title : settlement.getTitles()) {
            preparedTitleStrings.add(title.toDataString());
        }

        List<String> preparedPopulationStrings = new ArrayList<>();
        for (UUID uuid : settlement.getPopulation().keySet()) {
            preparedPopulationStrings.add(uuid.toString() + ";" + settlement.getPopulation().get(uuid).getName());
        }

        List<String> preparedTerritoryStrings = new ArrayList<>();
        for (ChunkAnchor chunk : settlement.getTerritory()) {
            logger.info(chunk.toString());
            preparedTerritoryStrings.add(chunk.getX() + ";" + chunk.getZ());
        }

        for (String str : preparedTitleStrings) {
            Charter.getPlugin(Charter.class).getLogger().info(str);
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        data.set("settlement.id", ID);
        data.set("settlement.name", name);
        data.set("settlement.style", style);
        data.set("settlement.description", description);
        data.set("settlement.titles", preparedTitleStrings);
        data.set("settlement.population", preparedPopulationStrings);
        data.set("settlement.territory", preparedTerritoryStrings);
        try {
            data.save(settlementFile);
            logger.info("[RealmManager::storeSettlement] Successfully stored settlement ID=" + ID + " (" + name + ")!");
        } catch (IOException e) {
            logger.warning("[RealmManager::storeSettlement] Failed to save data for settlement ID=" + ID + " (" + name + ")!");
            e.printStackTrace();
        }
    }

    public static String getNationFilepath(int id) {
        return Charter.getPlugin(Charter.class).getDataFolder() + NATION_FOLDER + NATION_FILE_PREFIX + String.valueOf(id) + ".yml";
    }

    public static String trimNationPrefix(String filename) {
        if (filename.startsWith(NATION_FILE_PREFIX)) {
            return filename.substring(NATION_FILE_PREFIX.length());
        } else {
            return filename;
        }
    }

    public static String getManifestFilepath() {
        return Charter.getPlugin(Charter.class).getDataFolder() + MANIFEST_FILENAME;
    }

    public static boolean loadManifest() {
        String pathname = getManifestFilepath();
        File manifestFile = new File(pathname);
        if (!manifestFile.exists()) {
            logger.warning("[RealmManager::loadManifest] Failed to find the manifest file! Has the manifest file been moved?");
            logger.warning("[RealmManager::loadManifest] Expected filepath: " + pathname);
            return false;
        }

        logger.info("[RealmManager::loadManifest] Found realm data manifest at '" + pathname + "'! Proceeding...");
        FileConfiguration data = YamlConfiguration.loadConfiguration(manifestFile);
        RealmManager.settlementCount = data.getInt("manifest.settlements.count");
        RealmManager.nationCount = data.getInt("manifest.nations.count");

        List<String> readSettlements = data.getStringList("manifest.settlements.map");
        for (String readSettlement : readSettlements) {
            String[] parts = readSettlement.split(";");
            int readID = Integer.parseInt(parts[0]);
            RealmManager.settlementMap.put(readID, loadSettlement(readID));
        }

        // ADD NATIONS LATER
        return true;
    }

    public static boolean storeManifest() {
        String pathname = getManifestFilepath();
        File manifestFile = new File(pathname);
        if (!manifestFile.exists()) {
            logger.warning("[RealmManager::storeManifest] Failed to find the manifest file! Has the manifest file been moved?");
            logger.warning("[RealmManager::storeManifest] Creating new manifest file at: " + pathname);
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(manifestFile);
        data.set("manifest.settlements.count", RealmManager.getSettlementCount());

        List<String> stmMap = new ArrayList<>(RealmManager.getSettlementCount());
        for (int stmID : settlementMap.keySet()) {
            String stmString = stmID + ";" + settlementMap.get(stmID).getName();
            stmMap.add(stmString);
        }
        data.set("manifest.settlements.map", stmMap);

        // ADD NATIONS LATER

        try {
            data.save(manifestFile);
        } catch (IOException e) {
            logger.warning("[RealmManager::storeManifest] Failed to save data for realm manifest!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Settlement getSettlement(int id) {
        return settlementMap.get(id);
    }

    public static Settlement getSettlement(String name) {
        for (int ID : settlementMap.keySet()) {
            if (name.equals(settlementMap.get(ID).getName())) {
                return settlementMap.get(ID);
            }
        }
        return null;
    }

    public static void markAsDirty(String realmType, Object realm) {
        switch (realmType) {
            case "settlement":
                dirtySettlements.add((Settlement) realm);
                break;
            case "nation":
                // ADD NATION HERE
                break;
            default:
                logger.warning("[RealmManager::markAsDirty] Invalid realm type: " + realmType);
        }
    }

    public static Settlement getChunkOwner(Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
        for (ChunkAnchor ca : territoryMap.keySet()) {
            if (anchor.equals(ca)) {
                return territoryMap.get(ca);
            }
        }
        return null;
    }

    public static boolean claimChunk(Settlement settlement, Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
        if (getChunkOwner(chunk) == null) {
            territoryMap.put(anchor, settlement);
            settlement.addTerritory(anchor);
            return true;
        } else return false;
    }

    public static boolean unclaimChunk(Settlement settlement, Chunk chunk) {
       ChunkAnchor anchor = new ChunkAnchor(chunk);
       if (Objects.equals(getChunkOwner(chunk), settlement)) {
           territoryMap.remove(anchor);
           settlement.removeTerritory(anchor);
           return true;
       } else return false;
    }
}

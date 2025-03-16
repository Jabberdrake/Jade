package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.management.PlayerTitle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class RealmManager {

    private static final String SETTLEMENT_FOLDER = "/settlements/";
    private static final String SETTLEMENT_FILE_PREFIX = "stm-";
    private static final String NATION_FOLDER = "/nations/";
    private static final String NATION_FILE_PREFIX = "nat-";

    private static final String MANIFEST_FILENAME = "/manifest.yml";

    private static int settlementCount;
    private static int nationCount;

    private static Logger logger = Charter.getPlugin(Charter.class).getLogger();

    private static List<Settlement> dirtySettlements;   // a "dirty" realm is a realm that has been edited, and whose edits have not been written on permanent memory

    private static Map<Integer, Settlement> settlementMap;
    // ADD NATION MAP

    public static void initialize() {
        if (loadManifest() == 0) {
            logger.info("[RealmManager::initialize] Successfully loaded " + settlementCount + " settlements and " + nationCount + " nations!");
        } else {
            logger.warning("[RealmManager::initialize] Manifest loading failed! No realms were loaded...");
        }

        RealmManager.dirtySettlements = new ArrayList<>();
    }

    public static void shutdown() {
        logger.info("[RealmManager::shutdown] Storing all edited realms to disk...");
        for (Settlement dirtySettlement : dirtySettlements) {
            RealmManager.storeSettlement(dirtySettlement);
        }
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

    public static Settlement loadSettlementFromID(int id) {
        // Get matching filepath for ID in argument, and attempt to read the file
        // If no matching file is found, quit
        String pathname = getSettlementFilepath(id);
        File settlementFile = new File(pathname);
        if (!settlementFile.exists()) { return null; }

        // Start building Settlement object, starting with basic info
        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        int stmID = data.getInt("settlement.id");
        String stmName = data.getString("settlement.name");
        String stmDecoratedName = data.getString("settlement.decoratedName");
        String stmDescription = data.getString("settlement.description");

        Settlement stm = new Settlement(stmID, stmName, stmDecoratedName, stmDescription);

        // Build title list for Settlement
        List<String> readTitles = data.getStringList("settlement.titles");
        for (String readTitle : readTitles) {
            stm.addTitle(PlayerTitle.fromString(readTitle, stm));
        }

        // Build population map for Settlement
        List<String> readPopulation = data.getStringList("settlement.population");
        for (String readCitizen : readPopulation) {
            String [] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            PlayerTitle fetchedTitle = stm.getTitleFromName(parts[1]);

            stm.addCitizen(convertedUUID, fetchedTitle);
        }

        return stm;
    }

    public static void storeSettlement(Settlement settlement) {
        int ID = settlement.getId();
        String name = settlement.getName();
        String decoratedName = settlement.getDecoratedName();
        String description = settlement.getDescription();

        logger.info("[RealmManager::storeSettlement] Storing settlement ID=" + ID + " (" + name + ")!");

        String pathname = getSettlementFilepath(ID);
        File settlementFile = new File(pathname);
        if (!settlementFile.exists()) { return; }

        List<String> preparedTitleStrings = new ArrayList<>();
        for (PlayerTitle title : settlement.getTitles()) {
            preparedTitleStrings.add(title.toString());
        }

        List<String> preparedPopulationStrings = new ArrayList<>();
        for (UUID uuid : settlement.getPopulation().keySet()) {
            preparedPopulationStrings.add(uuid.toString() + ";" + settlement.getPopulation().get(uuid).getName());
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        data.set("settlement.id", ID);
        data.set("settlement.name", name);
        data.set("settlement.decoratedName", decoratedName);
        data.set("settlement.description", description);
        data.set("settlement.titles", preparedTitleStrings);
        data.set("settlement.population", preparedPopulationStrings);
        try {
            data.save(settlementFile);
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

    public static int loadManifest() {
        String pathname = getManifestFilepath();
        File manifestFile = new File(pathname);
        if (!manifestFile.exists()) {
            logger.warning("[RealmManager::loadManifest] Failed to find the manifest file! Has the manifest file been moved?");
            logger.warning("[RealmManager::loadManifest] Expected filepath: " + pathname);
            return -1;
        }

        logger.info("[RealmManager::loadManifest] Found realm data manifest at '" + pathname + "'! Proceeding...");
        FileConfiguration data = YamlConfiguration.loadConfiguration(manifestFile);
        RealmManager.settlementCount = data.getInt("manifest.settlements.count");
        RealmManager.nationCount = data.getInt("manifest.nations.count");

        settlementMap = new HashMap<>(RealmManager.getSettlementCount());
        List<String> readSettlements = data.getStringList("manifest.settlements.map");
        for (String readSettlement : readSettlements) {
            String[] parts = readSettlement.split(";");
            int readID = Integer.parseInt(parts[0]);
            RealmManager.settlementMap.put(readID, loadSettlementFromID(readID));
        }

        // ADD NATIONS LATER
        return 0;
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
}

package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.management.PlayerTitle;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RealmManager {

    private static final String SETTLEMENT_DATA_PATH = "/settlements/";
    private static final String SETTLEMENT_FILE_PREFIX = "stm-";
    private static final String NATION_DATA_PATH = "/nations/";
    private static final String NATION_FILE_PREFIX = "nat-";

    private static int settlementCount;
    private static int nationCount;

    public static void initialize(int settlementCount, int nationCount) {
        RealmManager.settlementCount = settlementCount;
        RealmManager.nationCount = nationCount;
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




    public static String getSettlementFolderPath() {
        return Charter.getPlugin(Charter.class).getDataFolder() + SETTLEMENT_DATA_PATH;
    }

    public static String getSettlementFilepath(int id) {
        return getSettlementFolderPath() + SETTLEMENT_FILE_PREFIX + String.valueOf(id) + ".yml";
    }

    public static String trimSettlementPrefix(String filename) {
        if (filename.startsWith(SETTLEMENT_FILE_PREFIX)) {
            return filename.substring(SETTLEMENT_FILE_PREFIX.length());
        } else {
            return filename;
        }
    }

    public static Settlement loadSettlementFromID(int id) {
        String pathname = getSettlementFilepath(id);
        File settlementFile = new File(getSettlementFilepath(id));
        if (!settlementFile.exists()) { return null; }

        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        int stmID = data.getInt("settlement.id");
        String stmName = data.getString("settlement.name");
        String stmDescription = data.getString("settlement.description");
        Settlement stm = new Settlement(stmID, stmName, stmDescription);

        List<String> readTitles = data.getStringList("settlement.titles");
        for (String readTitle : readTitles) {
            String[] parts = readTitle.split(";");
            stm.addTitle(new PlayerTitle(stm, parts[1], Integer.parseInt(parts[2])));
        }

        Charter.getPlugin(Charter.class).getLogger().info(stm.getTitles().get(0).getName());

        List<String> readPopulation = data.getStringList("settlement.population");
        for (String readCitizen : readPopulation) {
            String [] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            Charter.getPlugin(Charter.class).getLogger().info(convertedUUID.toString());
            PlayerTitle fetchedTitle = stm.getTitleFromName(parts[1]);
            Charter.getPlugin(Charter.class).getLogger().info(parts[1]);
            stm.addCitizen(convertedUUID, fetchedTitle);
        }

        return stm;
    }






    public static String getNationFolderPath() {
        return Charter.getPlugin(Charter.class).getDataFolder() + SETTLEMENT_DATA_PATH;
    }

    public static String getNationFilepath(int id) {
        return getNationFolderPath() + NATION_FILE_PREFIX + String.valueOf(id) + ".yml";
    }

    public static String trimNationPrefix(String filename) {
        if (filename.startsWith(NATION_FILE_PREFIX)) {
            return filename.substring(NATION_FILE_PREFIX.length());
        } else {
            return filename;
        }
    }
}

package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
    private static List<Settlement> dirtySettlements = new ArrayList<>();

    private static Map<Integer, Settlement> cache = new HashMap<>();
    private static Map<UUID, Settlement> activeStmInvites = new HashMap<>();
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
        // Since this is called when we do '/charter admin rebuild', maybe we should explicitly
        // wipe all internal data maps/caches?

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

    // This should only be used when loading settlements.
    public static Map<ChunkAnchor, Settlement> getTerritoryMap() {
        return RealmManager.territoryMap;
    }

    public static List<Settlement> getAllSettlements() {
        return new ArrayList<>(cache.values());
    }

    public static boolean isUniqueSettlementName(String potentialStmName) {
        for (Settlement settlement : getAllSettlements()) {
            if (settlement.getName().equalsIgnoreCase(potentialStmName)) {
                return false;
            }
        }
        return true;
    }

    public static Settlement createSettlement(String name, Player leader, ChunkAnchor land) {
        Settlement settlement = new Settlement(name, leader, land);
        RealmManager.cache.put(settlement.getId(), settlement);
        RealmManager.markAsDirty(settlement);
        return settlement;
    }

    public static void deleteSettlement(Settlement settlement) {
        int stmID = settlement.getId();
        RealmManager.cache.remove(stmID);

        settlement.getPopulation().keySet()
                .stream().map(PlayerManager::parsePlayer)
                .forEach(jadePlayer -> jadePlayer.removeSettlement(settlement));

        String matchingFilepath = RealmManager.getSettlementFilepath(stmID);
        File matchingFile = new File(matchingFilepath);
        if (!matchingFile.delete()) {
            logger.warning("[RealmManager::deleteSettlement] Failed to delete file " + matchingFilepath);
        }
    }

    public static Settlement getWhoInvitedPlayer(Player player) {
        UUID playerID = player.getUniqueId();
        if (!RealmManager.activeStmInvites.containsKey(playerID)) { return null; }

        return RealmManager.activeStmInvites.get(playerID);
    }

    public static void registerInviteToSettlement(Player player, Settlement settlement) {
        UUID playerID = player.getUniqueId();
        if (!RealmManager.activeStmInvites.containsKey(playerID)) {
            RealmManager.activeStmInvites.put(playerID, settlement);
        }

        RealmManager.markAsDirty(settlement);
    }

    public static void clearInviteToSettlement(Player player) {
        UUID playerID = player.getUniqueId();
        if (!RealmManager.activeStmInvites.containsKey(playerID)) { return; }

        Settlement inviter = RealmManager.activeStmInvites.remove(playerID);
        RealmManager.markAsDirty(inviter);
    }

    public static List<Settlement> getSettlementsForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        List<Settlement> result = new ArrayList<>();

        for (Settlement stm : cache.values()) {
            if (stm.containsPlayer(uuid)) {
                result.add(stm);
            }
        }
        return result;
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
        if (!settlementFile.exists()) {
            logger.warning("[RealmManager::loadSettlement] Attempted to load non-existing settlement for id=" + id + "!");
            return null;
        }

        // Compose Settlement object from file data
        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);
        Settlement settlement = Settlement.load(data, "settlement");

        // Cache it to avoid having to read the file again
        RealmManager.cache.put(id, settlement);

        return settlement;
    }

    public static void storeSettlement(Settlement settlement) {
        String pathname = getSettlementFilepath(settlement.getId());
        File settlementFile = new File(pathname);
        FileConfiguration data = YamlConfiguration.loadConfiguration(settlementFile);

        Settlement.store(settlement, data, "settlement");

        try {
            data.save(settlementFile);
            logger.info("[RealmManager::storeSettlement] Successfully stored settlement ID=" + settlement.getId() + " (" + settlement.getName() + ")!");
        } catch (IOException e) {
            logger.warning("[RealmManager::storeSettlement] Failed to save data for settlement ID=" + settlement.getId() + " (" + settlement.getName() + ")!");
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

        FileConfiguration data = YamlConfiguration.loadConfiguration(manifestFile);
        RealmManager.settlementCount = data.getInt("manifest.settlements.count");
        RealmManager.nationCount = data.getInt("manifest.nations.count");

        List<String> readSettlements = data.getStringList("manifest.settlements.map");
        for (String readSettlement : readSettlements) {
            String[] parts = readSettlement.split(";");
            int readID = Integer.parseInt(parts[0]);

            RealmManager.loadSettlement(readID);
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

        List<String> stmMap = new ArrayList<>();
        for (int stmID : cache.keySet()) {
            String stmString = stmID + ";" + cache.get(stmID).getName();
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

    public static Settlement getSettlement(int id) { return cache.getOrDefault(id, null); }

    public static Settlement getSettlement(String name) {
        for (int ID : cache.keySet()) {
            if (name.equals(cache.get(ID).getName())) {
                return cache.get(ID);
            }
        }
        return null;
    }

    public static void markAsDirty(Settlement settlement) {
        if (!RealmManager.dirtySettlements.contains(settlement)) {
            RealmManager.dirtySettlements.add(settlement);
        }
    }

    public static Settlement getChunkOwner(Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
        return getChunkOwner(anchor);
    }

    public static Settlement getChunkOwner(ChunkAnchor anchor) {
        for (ChunkAnchor ca : territoryMap.keySet()) {
            if (anchor.equals(ca)) {
                return territoryMap.get(ca);
            }
        }
        return null;
    }

    public static boolean isUnclaimedChunk(Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
        return RealmManager.isUnclaimedChunk(anchor);
    }

    public static boolean isUnclaimedChunk(ChunkAnchor anchor) {
        return RealmManager.getChunkOwner(anchor) == null;
    }

    public static boolean claimChunk(Settlement settlement, Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
        return RealmManager.claimChunk(settlement, anchor);
    }

    public static boolean claimChunk(Settlement settlement, ChunkAnchor anchor) {
        if (RealmManager.getChunkOwner(anchor) == null) {
            settlement.addTerritory(anchor);

            territoryMap.put(anchor, settlement);
            RealmManager.markAsDirty(settlement);
            return true;
        } else return false;
    }

    public static boolean unclaimChunk(Settlement settlement, Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
       return RealmManager.unclaimChunk(settlement, anchor);
    }

    public static boolean unclaimChunk(Settlement settlement, ChunkAnchor anchor) {
        if (Objects.equals(getChunkOwner(anchor), settlement)) {
            settlement.removeTerritory(anchor);

            territoryMap.remove(anchor);
            RealmManager.markAsDirty(settlement);
            return true;
        } else return false;
    }

    // used for SettlementClaimCommand::runCommandForFill ('/settlement claim fill')
    public static Set<ChunkAnchor> prepareRecursiveChunkClaim(Settlement settlement, ChunkAnchor anchor) {
        final int MAX_CHUNKS_TO_CLAIM = 50;

        Set<ChunkAnchor> chunksToClaim = new LinkedHashSet<>();
        Queue<ChunkAnchor> queue = new LinkedList<>();
        queue.add(anchor);
        while (!queue.isEmpty() && chunksToClaim.size() < MAX_CHUNKS_TO_CLAIM) {
            ChunkAnchor head = queue.poll();
            if (!isUnclaimedChunk(head) || chunksToClaim.contains(head)) { continue; }

            chunksToClaim.add(head);
            enqueueIfUnclaimed(chunksToClaim, queue, head.getRelativeChunk(0, 1));
            enqueueIfUnclaimed(chunksToClaim, queue, head.getRelativeChunk(0, -1));
            enqueueIfUnclaimed(chunksToClaim, queue, head.getRelativeChunk(1, 0));
            enqueueIfUnclaimed(chunksToClaim, queue, head.getRelativeChunk(-1, 0));
        }

        if (chunksToClaim.size() >= MAX_CHUNKS_TO_CLAIM) {
            return null;
        } else return chunksToClaim;
    }

    // auxiliary function of RealmManager::prepareRecursiveChunkClaim
    public static void enqueueIfUnclaimed(Set<ChunkAnchor> set, Queue<ChunkAnchor> queue, ChunkAnchor anchor) {
        if (RealmManager.isUnclaimedChunk(anchor) && !set.contains(anchor) && !queue.contains(anchor)) {
            queue.add(anchor);
        }
    }
}

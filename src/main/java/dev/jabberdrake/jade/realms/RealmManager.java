package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.players.PlayerManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class RealmManager {

    private static final Jade plugin = Jade.getPlugin(Jade.class);
    private static final Logger logger = plugin.getLogger();

    private static Map<Integer, Settlement> settlementCache = new HashMap<>();
    private static Map<UUID, Settlement> activeSettlementInvites = new HashMap<>(); // <invited player UUID, Settlement>

    private static Map<Integer, Nation> nationCache = new HashMap<>();
    private static Map<Integer, Nation> activeNationInvites = new HashMap<>(); // <invited settlement ID, Nation>

    private static Map<ChunkAnchor, Settlement> territoryMap = new HashMap<>();

    public static void initialize() {
        List<Settlement> settlements = DatabaseManager.fetchAllSettlements();
        for (Settlement settlement : settlements) {
            settlementCache.put(settlement.getId(), settlement);
        }

        List<Nation> nations = DatabaseManager.fetchAllNations();
        for (Nation nation : nations) {
            nationCache.put(nation.getId(), nation);
        }

        logger.info("[RealmManager::initialize] Successfully loaded " + settlements.size() + " settlements and " + nations.size() + " nations!");


        RealmManager.territoryMap = DatabaseManager.fetchTerritoryMap(settlements);
        logger.info("[RealmManager::initialize] Successfully loaded the territory map!");
    }

    public static void shutdown() {
        // Nothing
    }

    public static void reload() {
        settlementCache.clear();
        activeSettlementInvites.clear();
        nationCache.clear();
        activeNationInvites.clear();

        List<Settlement> settlements = DatabaseManager.fetchAllSettlements();
        // List<Nation> nations = DatabaseManager.fetchAllNations();
        territoryMap = DatabaseManager.fetchTerritoryMap(settlements);
        logger.info("[RealmManager::initialize] Successfully reloaded internal data structures for realms!");
    }

    public static int getSettlementCount() {
        return settlementCache.values().size();
    }

    public static int getNationCount() {
        return nationCache.values().size();
    }

    // This should only be used when loading settlements.
    public static Map<ChunkAnchor, Settlement> getTerritoryMap() {
        return RealmManager.territoryMap;
    }

    public static List<Settlement> getAllSettlements() {
        return new ArrayList<>(settlementCache.values());
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

        settlementCache.put(settlement.getId(), settlement);
        return settlement;
    }

    public static Settlement getSettlement(int id) {
        // There are certain cases where caller methods will intentionally try to call for
        // a settlement with ID=0 or ID=-1. The intended behaviour here is to return null,
        // so as to reply that "there is no such settlement". By catching this early, we avoid
        // having to do unnecessary map lookups or (worse even) database queries.
        if (id <= 0) return null;

        if (settlementCache.containsKey(id)) {
            return settlementCache.get(id);
        } else {
            Settlement settlement = DatabaseManager.fetchSettlementById(id);
            if (settlement != null) {
                settlementCache.put(settlement.getId(), settlement);
                return settlement;
            } else {
                plugin.getLogger().warning("[RealmManager::getSettlement(id)] Attempted to fetch unknown settlement for ID=" + id);
                return null;
            }
        }
    }

    public static Settlement getSettlement(String name) {
        // Check for match in data cache
        for (int ID : settlementCache.keySet()) {
            if (name.equals(settlementCache.get(ID).getName())) {
                return settlementCache.get(ID);
            }
        }

        Settlement settlement = DatabaseManager.fetchSettlementByName(name);
        if (settlement != null) {
            settlementCache.put(settlement.getId(), settlement);
            return settlement;
        } else {
            plugin.getLogger().warning("[RealmManager::getSettlement(name)] Attempted to fetch unknown settlement for name=" + name);
            return null;
        }

    }

    public static void deleteSettlement(Settlement settlement) {
        // Remove settlement from cache list
        settlementCache.remove(settlement.getId());

        // Remove all active invites to the deleted settlement
        List<UUID> invitesToRemove = new ArrayList<>();
        for (UUID playerID : activeSettlementInvites.keySet()) {
            if (activeSettlementInvites.get(playerID).equals(settlement)) {
                invitesToRemove.add(playerID);
            }
        }
        for (UUID playerID : invitesToRemove) activeSettlementInvites.remove(playerID);

        // Remove all territory map entries regarding the deleted settlement
        List<ChunkAnchor> chunkEntriesToRemove = new ArrayList<>();
        for (ChunkAnchor anchor : territoryMap.keySet()) {
            if (territoryMap.get(anchor).equals(settlement)) {
                chunkEntriesToRemove.add(anchor);
            }
        }
        for (ChunkAnchor anchor : chunkEntriesToRemove) territoryMap.remove(anchor);

        DatabaseManager.deleteSettlement(settlement.getId());
    }

    public static Settlement getWhoInvitedPlayer(Player player) {
        UUID playerID = player.getUniqueId();
        return RealmManager.activeSettlementInvites.get(playerID);
    }

    public static boolean registerInviteToSettlement(Player player, Settlement settlement) {
        UUID playerID = player.getUniqueId();
        if (!RealmManager.activeSettlementInvites.containsKey(playerID)) {
            RealmManager.activeSettlementInvites.put(playerID, settlement);
            return true;
        } else return false;
    }

    public static void clearInviteToSettlement(Player player) {
        UUID playerID = player.getUniqueId();
        Settlement inviter = RealmManager.activeSettlementInvites.remove(playerID);
    }

    public static List<Settlement> getSettlementsForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        List<Settlement> result = new ArrayList<>();

        for (Settlement stm : settlementCache.values()) {
            if (stm.containsPlayer(uuid)) {
                result.add(stm);
            }
        }
        return result;
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
        if (RealmManager.getChunkOwner(anchor) == null && settlement.getFood() >= JadeSettings.chunkCost) {
            settlement.addChunk(anchor);

            territoryMap.put(anchor, settlement);
            return true;
        } else return false;
    }

    public static boolean unclaimChunk(Settlement settlement, Chunk chunk) {
        ChunkAnchor anchor = new ChunkAnchor(chunk);
       return RealmManager.unclaimChunk(settlement, anchor);
    }

    public static boolean unclaimChunk(Settlement settlement, ChunkAnchor anchor) {
        if (Objects.equals(getChunkOwner(anchor), settlement)) {
            settlement.removeChunk(anchor);

            territoryMap.remove(anchor);
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

    public static List<Nation> getAllNations() {
        return new ArrayList<>(nationCache.values());
    }

    public static boolean isUniqueNationName(String potentialNatName) {
        for (Nation nation : getAllNations()) {
            if (nation.getName().equalsIgnoreCase(potentialNatName)) {
                return false;
            }
        }
        return true;
    }

    public static Nation createNation(String name, Settlement capital) {
        Nation nation = new Nation(name, capital);
        capital.setNation(nation);

        nationCache.put(nation.getId(), nation);
        return nation;
    }

    // Used by DatabaseManager when composing settlement objects from persistent data
    public static Nation getNation(int id) {
        if (id == 0) return null;

        if (nationCache.containsKey(id)) {
            return nationCache.get(id);
        } else {
            Nation nation = DatabaseManager.fetchNationById(id);
            if (nation != null) {
                nationCache.put(id, nation);
                return nation;
            } else {
                plugin.getLogger().warning("[RealmManager::getNation(id)] Attempted to fetch unknown nation for ID=" + id);
                return null;
            }
        }
    }

    public static Nation getNation(String name) {
        // Check for match in data cache
        for (int ID : nationCache.keySet()) {
            if (name.equals(nationCache.get(ID).getName())) {
                return nationCache.get(ID);
            }
        }

        Nation nation = DatabaseManager.fetchNationByName(name);
        if (nation != null) {
            nationCache.put(nation.getId(), nation);
            return nation;
        } else {
            plugin.getLogger().warning("[RealmManager::getNation(name)] Attempted to fetch unknown nation for name=" + name);
            return null;
        }
    }

    public static void deleteNation(Nation nation) {
        // Remove nation from cache list
        nationCache.remove(nation.getId());

        // Remove all active invites to the deleted nation
        List<Integer> invitesToRemove = new ArrayList<>();
        for (int stmID : activeNationInvites.keySet()) {
            if (activeNationInvites.get(stmID).equals(nation)) {
                invitesToRemove.add(stmID);
            }
        }
        for (int stmID : invitesToRemove) activeNationInvites.remove(stmID);

        // Make all member settlements leave deleted nation
        for (Settlement settlement : getAllSettlements()) {
            Nation nationToCheck = settlement.getNation();
            if (nationToCheck != null && nationToCheck.equals(nation)) {
                settlement.leaveNation();
            }
        }

        DatabaseManager.deleteNation(nation.getId());
    }

    public static Nation getWhoInvitedSettlement(Settlement settlement) {
        return RealmManager.activeNationInvites.get(settlement.getId());
    }

    public static boolean registerInviteToNation(Settlement settlement, Nation nation) {
        if (!RealmManager.activeNationInvites.containsKey(settlement.getId())) {
            RealmManager.activeNationInvites.put(settlement.getId(), nation);
            return true;
        } else return false;
    }

    public static void clearInviteToNation(Settlement settlement) {
        Nation inviter = RealmManager.activeNationInvites.remove(settlement.getId());
    }
}

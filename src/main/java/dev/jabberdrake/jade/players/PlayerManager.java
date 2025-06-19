package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;

public class PlayerManager {

    private static final Jade plugin = Jade.getPlugin(Jade.class);
    private static final Logger logger = plugin.getLogger();

    private static Map<UUID, JadePlayer> playerCache = new HashMap<>();
    private static Map<String, Grave> graveCache = new HashMap<>();

    public static void initialize() {
        List<Grave> graves = DatabaseManager.fetchAllGraves();
        for (Grave grave : graves) {
            graveCache.put(grave.getID(), grave);
        }

        logger.info("[PlayerManager::initialize] Initialization finished!");
    }

    public static void shutdown() {
        for (String graveID : graveCache.keySet()) {
            DatabaseManager.saveGrave(graveCache.get(graveID));
        }
    }

    public static JadePlayer asJadePlayer(UUID uuid) {
        return playerCache.getOrDefault(uuid, null);
    }

    public static List<JadePlayer> getAllOnlinePlayers() { return new ArrayList<>(playerCache.values()); }

    public static void handleLogin(UUID uuid) {
        JadePlayer player = DatabaseManager.fetchPlayerByUUID(uuid);
        if (player == null) {
            player = new JadePlayer(uuid);
            DatabaseManager.createPlayer(player);
        }

        playerCache.put(uuid, player);

        logger.info("[PlayerManager::handleLogin] Successfully composed internal data structures for player " + plugin.getServer().getPlayer(uuid).getName() + "!");
    }

    public static void handleLogout(UUID uuid) {
        playerCache.get(uuid).clearViewTasks();
        playerCache.remove(uuid);

        for (Grave grave : getAllGravesForPlayer(uuid)) {
            DatabaseManager.saveGrave(grave);
        }
    }

    public static void unsetTitleForAllPlayers(JadeTitle title) {
        // Handle online players
        for (JadePlayer player : getAllOnlinePlayers()) {
            if (player.getTitleInUse().equals(title)) {
                player.setTitleInUse(DefaultJadeTitle.PEASANT);
            }
        }

        // Handle offline players + register changes in DB
        DatabaseManager.unsetTitleForAllPlayers(title);
    }

    public static boolean isUniqueGraveID(String identifier) {
        return !PlayerManager.graveCache.containsKey(identifier);
    }

    public static Grave getGraveAt(Location location) {
        for (String graveID : graveCache.keySet()) {
            Grave grave = graveCache.get(graveID);
            if (!grave.isVirtual() && grave.getChestLocation().equals(location)) {
                return grave;
            }
        }
        return null;
    }

    public static void registerGrave(Player player, List<ItemStack> items) {
        Grave grave = new Grave(player.getUniqueId(), items);
        graveCache.put(grave.getID(), grave);
        DatabaseManager.createGrave(grave);
    }

    public static void registerGrave(Player player, List<ItemStack> items, Location deathLocation) {
        Grave grave = new Grave(player.getUniqueId(), items, deathLocation);
        graveCache.put(grave.getID(), grave);
        DatabaseManager.createGrave(grave);
    }

    public static void clearGrave(Grave grave) {
        if (!grave.isVirtual()) {
            grave.getChestLocation().getBlock().setType(Material.AIR);
        }

        graveCache.remove(grave.getID());
        DatabaseManager.deleteGrave(grave.getID());
    }

    public static List<Grave> getAllGravesForPlayer(UUID playerID) {
        return graveCache.values().stream().filter(grave -> grave.getPlayerID().equals(playerID)).toList();
    }

    public static List<Grave> getAllGraves() {
        return graveCache.values().stream().toList();
    }
}

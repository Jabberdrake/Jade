package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.menus.implementations.GraveOpenMenu;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Logger;

public class PlayerManager {

    private static final Jade plugin = Jade.getPlugin(Jade.class);
    private static final Logger logger = plugin.getLogger();

    private static Map<UUID, JadePlayer> playerCache = new HashMap<>();
    private static Map<String, Grave> graveCache = new HashMap<>();

    public static final Map<UUID, String> openGraves = new HashMap<>();

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

        // Fetch all settings
        PersistentDataContainer pdc = player.asPlayer().getPersistentDataContainer();
        for (AbstractSetting<?> setting : PlayerSettings.getAllSettings()) {
            boolean useDefaults = !pdc.has(setting.getKey());

            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;
                boolean booleanValue = useDefaults ? booleanSetting.getDefaultValue() : pdc.get(setting.getKey(), PersistentDataType.BOOLEAN);
                player.setSetting(booleanSetting, booleanValue);
            }
            // add more setting types
        }

        // Set focus settlement if player spawned in trusted settlement
        Settlement owner = RealmManager.getChunkOwner(player.asPlayer().getChunk());
        if (owner != null && owner.containsPlayer(uuid)) {
            player.setFocusSettlement(owner);
        }

        playerCache.put(uuid, player);

        logger.info("[PlayerManager::handleLogin] Successfully composed internal data structures for player " + plugin.getServer().getPlayer(uuid).getName() + "!");
    }

    public static void handleLogout(UUID uuid) {
        playerCache.get(uuid).clearViewTasks();
        playerCache.remove(uuid);

        // was originally gonna save all edited settings to player PDCs here but those aren't accessible during PlayerQuitEvent so oh well

        // Save all graves
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

    public static void registerGrave(Player player, List<ItemStack> items, Location deathLocation) {
        Grave grave = new Grave(player, items, deathLocation);
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

    public static void registerSettingChange(Player player, AbstractSetting<?> setting) {
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting) setting;
            pdc.set(setting.getKey(), PersistentDataType.BOOLEAN, jadePlayer.getSetting(booleanSetting));
        }
        // add more setting types

    }

    public static List<Grave> getAllGravesForPlayer(UUID playerID) {
        return graveCache.values().stream().filter(grave -> grave.getPlayerID().equals(playerID)).toList();
    }

    public static List<Grave> getAllGraves() {
        return graveCache.values().stream().toList();
    }

    public static void openGraveMenu(Player player, Grave grave) {
        openGraves.put(player.getUniqueId(), grave.getID());
        new GraveOpenMenu(grave).open(player);
    }

    public static void closeGraveMenu(Player player, boolean force) {
        openGraves.remove(player.getUniqueId());
        if (force) player.closeInventory();
    }

    public static UUID getGraveViewer(Grave grave) {
        for (UUID viewerID : PlayerManager.openGraves.keySet()) {
            if (openGraves.get(viewerID).equalsIgnoreCase(grave.getID())) return viewerID;
        }
        return null;
    }
}

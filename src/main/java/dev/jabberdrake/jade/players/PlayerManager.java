package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;

import java.util.*;
import java.util.logging.Logger;

public class PlayerManager {

    private static final Jade plugin = Jade.getPlugin(Jade.class);
    private static final Logger logger = plugin.getLogger();

    private static Map<UUID, JadePlayer> cache = new HashMap<>();

    public static void initialize() {
        // Nothing
    }

    public static void shutdown() {
        // Nothing
    }

    public static JadePlayer asJadePlayer(UUID uuid) {
        return cache.getOrDefault(uuid, null);
    }

    public static List<JadePlayer> getAllOnlinePlayers() { return new ArrayList<>(cache.values()); }

    public static void handleLogin(UUID uuid) {
        JadePlayer player = DatabaseManager.fetchPlayerByUUID(uuid);
        if (player == null) {
            player = new JadePlayer(uuid);
            DatabaseManager.createPlayer(player);
        }

        cache.put(uuid, player);

        logger.info("[PlayerManager::handleLogin] Successfully composed internal data structures for player " + plugin.getServer().getPlayer(uuid).getName() + "!");
    }

    public static void handleLogout(UUID uuid) {
        cache.remove(uuid);
    }


    //public static JadeProfile fetchProfile(UUID uuid) {
    //    return cache.getOrDefault(uuid, null).getProfile();
    //}

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
}

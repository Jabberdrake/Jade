package dev.jabberdrake.jade.chat;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.recipes.FurnaceRecipes;
import org.bukkit.Server;

import java.util.logging.Logger;

public class ChatManager {

    private static Server server;
    private static Logger logger;

    public static void initialize() {
        server = Jade.getInstance().getServer();
        logger = Jade.getInstance().getLogger();

        new RandomAdviceTask().runTaskTimer(Jade.getInstance(), 1200L, 6000L);

        logger.info("[ChatManager::initialize] Finished initializing!");
    }
}

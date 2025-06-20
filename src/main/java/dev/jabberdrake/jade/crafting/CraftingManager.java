package dev.jabberdrake.jade.crafting;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.recipes.FurnaceRecipes;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;

import java.util.logging.Logger;

public class CraftingManager {

    private static Server server;
    private static Logger logger;

    public static void initialize() {
        server = Jade.getInstance().getServer();
        logger = Jade.getInstance().getLogger();

        for (Recipe recipe : FurnaceRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + FurnaceRecipes.getAllRecipes().size() + " furnace recipes!");
    }
}

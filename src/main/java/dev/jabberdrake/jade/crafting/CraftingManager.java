package dev.jabberdrake.jade.crafting;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.recipes.FurnaceRecipes;
import dev.jabberdrake.jade.crafting.recipes.StonecutterRecipes;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;

import java.util.logging.Logger;

public class CraftingManager {

    private static Server server;
    private static Logger logger;

    public static void initialize() {
        server = Jade.getInstance().getServer();
        logger = Jade.getInstance().getLogger();

        // add all custom furnace recipes
        for (Recipe recipe : FurnaceRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + FurnaceRecipes.getAllRecipes().size() + " furnace recipes!");

        // add all custom stonecutter recipes
        for (Recipe recipe : StonecutterRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + StonecutterRecipes.getAllRecipes().size() + " stonecutter recipes!");
    }
}

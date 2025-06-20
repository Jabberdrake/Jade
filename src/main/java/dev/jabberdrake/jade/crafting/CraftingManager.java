package dev.jabberdrake.jade.crafting;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.recipes.CookingRecipes;
import dev.jabberdrake.jade.crafting.recipes.ShapedRecipes;
import dev.jabberdrake.jade.crafting.recipes.ShapelessRecipes;
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

        // add all custom shaped crafting recipes
        for (Recipe recipe : ShapedRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + ShapedRecipes.getAllRecipes().size() + " shaped crafting recipes!");

        // add all custom shapeless crafting recipes
        for (Recipe recipe : ShapelessRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + ShapelessRecipes.getAllRecipes().size() + " shapeless crafting recipes!");

        // add all custom furnace recipes
        for (Recipe recipe : CookingRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + CookingRecipes.getAllRecipes().size() + " cooking recipes!");

        // add all custom stonecutter recipes
        for (Recipe recipe : StonecutterRecipes.getAllRecipes().values()) {
            server.addRecipe(recipe);
        }
        logger.info("[CraftingManager::initialize] Added " + StonecutterRecipes.getAllRecipes().size() + " stonecutter recipes!");
    }
}

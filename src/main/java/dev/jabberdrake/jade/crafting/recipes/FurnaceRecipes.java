package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class FurnaceRecipes {

    private static final Map<NamespacedKey, FurnaceRecipe> RECIPES = new LinkedHashMap<>();

    public static final FurnaceRecipe ROTTEN_FLESH_TO_LEATHER = registerRecipe("recipe_rottenFlesh_to_leather", ItemStack.of(Material.LEATHER), Material.ROTTEN_FLESH, 0.2f, 200);

    private static FurnaceRecipe registerRecipe(String keyString, ItemStack result, Material source, float experience, int cookingTime) {
        NamespacedKey key = Jade.key(keyString);
        FurnaceRecipe recipe = new FurnaceRecipe(key, result, source, experience, cookingTime);
        RECIPES.put(key, recipe);
        return recipe;
    }

    public static Map<NamespacedKey, FurnaceRecipe> getAllRecipes() {
        return RECIPES;
    }
}

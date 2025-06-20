package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class StonecutterRecipes {

    private static final Map<NamespacedKey, StonecuttingRecipe> RECIPES = new LinkedHashMap<>();

    public static final StonecuttingRecipe COBBLESTONE_TO_GRAVEL = registerRecipe("recipe_cobblestone_to_gravel", ItemStack.of(Material.GRAVEL), Material.COBBLESTONE);
    public static final StonecuttingRecipe GRAVEL_TO_SAND = registerRecipe("recipe_gravel_to_sand", ItemStack.of(Material.SAND), Material.GRAVEL);

    private static StonecuttingRecipe registerRecipe(String keyString, ItemStack result, Material source) {
        NamespacedKey key = Jade.key(keyString);
        StonecuttingRecipe recipe = new StonecuttingRecipe(key, result, source);
        RECIPES.put(key, recipe);
        return recipe;
    }

    public static Map<NamespacedKey, StonecuttingRecipe> getAllRecipes() {
        return RECIPES;
    }
}

package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.CauldronRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class CauldronRecipes {

    private static final Map<NamespacedKey, CauldronRecipe> RECIPES = new LinkedHashMap<>();

    static {
        registerRecipe("dirt_to_mud", ItemStack.of(Material.MUD), Material.DIRT);
        registerRecipe("coarse_dirt_to_mud", ItemStack.of(Material.MUD), Material.COARSE_DIRT);
        registerRecipe("rooted_dirt_to_mud", ItemStack.of(Material.MUD), Material.ROOTED_DIRT);
        registerRecipe("gravel_to_clay", ItemStack.of(Material.CLAY), Material.GRAVEL);
        registerRecipe("magma_cream_to_slimeball", ItemStack.of(Material.SLIME_BALL), Material.MAGMA_CREAM);
        registerRecipe("white_concrete_powder_to_white_concrete", ItemStack.of(Material.WHITE_CONCRETE), Material.WHITE_CONCRETE_POWDER);
        registerRecipe("light_gray_concrete_powder_to_light_gray_concrete", ItemStack.of(Material.LIGHT_GRAY_CONCRETE), Material.LIGHT_GRAY_CONCRETE_POWDER);
        registerRecipe("gray_concrete_powder_to_gray_concrete", ItemStack.of(Material.GRAY_CONCRETE), Material.GRAY_CONCRETE_POWDER);
        registerRecipe("black_concrete_powder_to_black_concrete", ItemStack.of(Material.BLACK_CONCRETE), Material.BLACK_CONCRETE_POWDER);
        registerRecipe("brown_concrete_powder_to_brown_concrete", ItemStack.of(Material.BROWN_CONCRETE), Material.BROWN_CONCRETE_POWDER);
        registerRecipe("red_concrete_powder_to_red_concrete", ItemStack.of(Material.RED_CONCRETE), Material.RED_CONCRETE_POWDER);
        registerRecipe("orange_concrete_powder_to_orange_concrete", ItemStack.of(Material.ORANGE_CONCRETE), Material.ORANGE_CONCRETE_POWDER);
        registerRecipe("yellow_concrete_powder_to_yellow_concrete", ItemStack.of(Material.YELLOW_CONCRETE), Material.YELLOW_CONCRETE_POWDER);
        registerRecipe("lime_concrete_powder_to_lime_concrete", ItemStack.of(Material.LIME_CONCRETE), Material.LIME_CONCRETE_POWDER);
        registerRecipe("green_concrete_powder_to_green_concrete", ItemStack.of(Material.GREEN_CONCRETE), Material.GREEN_CONCRETE_POWDER);
        registerRecipe("cyan_concrete_powder_to_cyan_concrete", ItemStack.of(Material.CYAN_CONCRETE), Material.CYAN_CONCRETE_POWDER);
        registerRecipe("light_blue_concrete_powder_to_light_blue_concrete", ItemStack.of(Material.LIGHT_BLUE_CONCRETE), Material.LIGHT_BLUE_CONCRETE_POWDER);
        registerRecipe("blue_concrete_powder_to_blue_concrete", ItemStack.of(Material.BLUE_CONCRETE), Material.BLUE_CONCRETE_POWDER);
        registerRecipe("purple_concrete_powder_to_purple_concrete", ItemStack.of(Material.PURPLE_CONCRETE), Material.PURPLE_CONCRETE_POWDER);
        registerRecipe("magenta_concrete_powder_to_magenta_concrete", ItemStack.of(Material.MAGENTA_CONCRETE), Material.MAGENTA_CONCRETE_POWDER);
        registerRecipe("pink_concrete_powder_to_pink_concrete", ItemStack.of(Material.PINK_CONCRETE), Material.PINK_CONCRETE_POWDER);
    }

    private static void registerRecipe(String keyString, ItemStack result, Material source) {
        NamespacedKey key = Jade.key("cauldronRecipe_" + keyString);
        CauldronRecipe recipe = new CauldronRecipe(key, result, source);
        RECIPES.put(key, recipe);
    }

    public static Map<NamespacedKey, CauldronRecipe> getAllRecipes() {
        return RECIPES;
    }
}

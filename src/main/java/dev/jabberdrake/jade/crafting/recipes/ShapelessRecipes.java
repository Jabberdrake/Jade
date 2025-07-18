package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

import static dev.jabberdrake.jade.utils.MaterialUtils.*;

public class ShapelessRecipes {

    private static final Map<NamespacedKey, ShapelessRecipe> RECIPES = new LinkedHashMap<>();

    static {
        registerRecipe("wool_to_string", ItemStack.of(Material.STRING, 4), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL)));
        registerRecipe("nether_wart_block_to_nether_wart", ItemStack.of(Material.NETHER_WART, 9), Ingredient.of(Material.NETHER_WART_BLOCK));

        registerRecipe("OAK_slabs_to_planks", ItemStack.of(Material.OAK_PLANKS), Ingredient.of(Material.OAK_SLAB, 2));
        registerRecipe("BIRCH_slabs_to_planks", ItemStack.of(Material.BIRCH_PLANKS), Ingredient.of(Material.BIRCH_SLAB, 2));
        registerRecipe("SPRUCE_slabs_to_planks", ItemStack.of(Material.SPRUCE_PLANKS), Ingredient.of(Material.SPRUCE_SLAB, 2));
        registerRecipe("JUNGLE_slabs_to_planks", ItemStack.of(Material.JUNGLE_PLANKS), Ingredient.of(Material.JUNGLE_SLAB, 2));
        registerRecipe("ACACIA_slabs_to_planks", ItemStack.of(Material.ACACIA_PLANKS), Ingredient.of(Material.ACACIA_SLAB, 2));
        registerRecipe("DARK_OAK_slabs_to_planks", ItemStack.of(Material.DARK_OAK_PLANKS), Ingredient.of(Material.DARK_OAK_SLAB, 2));
        registerRecipe("CRIMSON_slabs_to_planks", ItemStack.of(Material.CRIMSON_PLANKS), Ingredient.of(Material.CRIMSON_SLAB, 2));
        registerRecipe("WARPED_slabs_to_planks", ItemStack.of(Material.WARPED_PLANKS), Ingredient.of(Material.WARPED_SLAB, 2));
        registerRecipe("MANGROVE_slabs_to_planks", ItemStack.of(Material.MANGROVE_PLANKS), Ingredient.of(Material.MANGROVE_SLAB, 2));
        registerRecipe("PALE_OAK_slabs_to_planks", ItemStack.of(Material.PALE_OAK_PLANKS), Ingredient.of(Material.PALE_OAK_SLAB, 2));

        registerRecipe("CONCRETE_any_to_white", ItemStack.of(Material.WHITE_CONCRETE), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_CONCRETE), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_gray", ItemStack.of(Material.GRAY_CONCRETE), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_black", ItemStack.of(Material.BLACK_CONCRETE), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_brown", ItemStack.of(Material.BROWN_CONCRETE), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_red", ItemStack.of(Material.RED_CONCRETE), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_orange", ItemStack.of(Material.ORANGE_CONCRETE), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_yellow", ItemStack.of(Material.YELLOW_CONCRETE), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_lime", ItemStack.of(Material.LIME_CONCRETE), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_green", ItemStack.of(Material.GREEN_CONCRETE), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_cyan", ItemStack.of(Material.CYAN_CONCRETE), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_CONCRETE), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_blue", ItemStack.of(Material.BLUE_CONCRETE), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_purple", ItemStack.of(Material.PURPLE_CONCRETE), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_magenta", ItemStack.of(Material.MAGENTA_CONCRETE), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));
        registerRecipe("CONCRETE_any_to_pink", ItemStack.of(Material.PINK_CONCRETE), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE)));

        registerRecipe("TERRACOTTA_any_to_white", ItemStack.of(Material.WHITE_TERRACOTTA), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_TERRACOTTA), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_gray", ItemStack.of(Material.GRAY_TERRACOTTA), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_black", ItemStack.of(Material.BLACK_TERRACOTTA), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_brown", ItemStack.of(Material.BROWN_TERRACOTTA), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_red", ItemStack.of(Material.RED_TERRACOTTA), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_orange", ItemStack.of(Material.ORANGE_TERRACOTTA), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_yellow", ItemStack.of(Material.YELLOW_TERRACOTTA), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_lime", ItemStack.of(Material.LIME_TERRACOTTA), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_green", ItemStack.of(Material.GREEN_TERRACOTTA), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_cyan", ItemStack.of(Material.CYAN_TERRACOTTA), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_TERRACOTTA), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_blue", ItemStack.of(Material.BLUE_TERRACOTTA), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_purple", ItemStack.of(Material.PURPLE_TERRACOTTA), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_magenta", ItemStack.of(Material.MAGENTA_TERRACOTTA), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));
        registerRecipe("TERRACOTTA_any_to_pink", ItemStack.of(Material.PINK_TERRACOTTA), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA)));

        registerRecipe("GLAZED_TERRACOTTA_any_to_white", ItemStack.of(Material.WHITE_GLAZED_TERRACOTTA), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_GLAZED_TERRACOTTA), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_gray", ItemStack.of(Material.GRAY_GLAZED_TERRACOTTA), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_black", ItemStack.of(Material.BLACK_GLAZED_TERRACOTTA), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_brown", ItemStack.of(Material.BROWN_GLAZED_TERRACOTTA), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_red", ItemStack.of(Material.RED_GLAZED_TERRACOTTA), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_orange", ItemStack.of(Material.ORANGE_GLAZED_TERRACOTTA), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_yellow", ItemStack.of(Material.YELLOW_GLAZED_TERRACOTTA), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_lime", ItemStack.of(Material.LIME_GLAZED_TERRACOTTA), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_green", ItemStack.of(Material.GREEN_GLAZED_TERRACOTTA), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_cyan", ItemStack.of(Material.CYAN_GLAZED_TERRACOTTA), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_GLAZED_TERRACOTTA), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_blue", ItemStack.of(Material.BLUE_GLAZED_TERRACOTTA), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_purple", ItemStack.of(Material.PURPLE_GLAZED_TERRACOTTA), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_magenta", ItemStack.of(Material.MAGENTA_GLAZED_TERRACOTTA), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));
        registerRecipe("GLAZED_TERRACOTTA_any_to_pink", ItemStack.of(Material.PINK_GLAZED_TERRACOTTA), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA)));

        registerRecipe("STAINED_GLASS_any_to_white", ItemStack.of(Material.WHITE_STAINED_GLASS), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_gray", ItemStack.of(Material.GRAY_STAINED_GLASS), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_black", ItemStack.of(Material.BLACK_STAINED_GLASS), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_brown", ItemStack.of(Material.BROWN_STAINED_GLASS), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_red", ItemStack.of(Material.RED_STAINED_GLASS), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_orange", ItemStack.of(Material.ORANGE_STAINED_GLASS), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_yellow", ItemStack.of(Material.YELLOW_STAINED_GLASS), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_lime", ItemStack.of(Material.LIME_STAINED_GLASS), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_green", ItemStack.of(Material.GREEN_STAINED_GLASS), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_cyan", ItemStack.of(Material.CYAN_STAINED_GLASS), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_STAINED_GLASS), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_blue", ItemStack.of(Material.BLUE_STAINED_GLASS), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_purple", ItemStack.of(Material.PURPLE_STAINED_GLASS), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_magenta", ItemStack.of(Material.MAGENTA_STAINED_GLASS), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));
        registerRecipe("STAINED_GLASS_any_to_pink", ItemStack.of(Material.PINK_STAINED_GLASS), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS)));

        registerRecipe("STAINED_GLASS_PANE_any_to_white", ItemStack.of(Material.WHITE_STAINED_GLASS_PANE), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_gray", ItemStack.of(Material.GRAY_STAINED_GLASS_PANE), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_black", ItemStack.of(Material.BLACK_STAINED_GLASS_PANE), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_brown", ItemStack.of(Material.BROWN_STAINED_GLASS_PANE), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_red", ItemStack.of(Material.RED_STAINED_GLASS_PANE), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_orange", ItemStack.of(Material.ORANGE_STAINED_GLASS_PANE), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_yellow", ItemStack.of(Material.YELLOW_STAINED_GLASS_PANE), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_lime", ItemStack.of(Material.LIME_STAINED_GLASS_PANE), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_green", ItemStack.of(Material.GREEN_STAINED_GLASS_PANE), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_cyan", ItemStack.of(Material.CYAN_STAINED_GLASS_PANE), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_blue", ItemStack.of(Material.BLUE_STAINED_GLASS_PANE), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_purple", ItemStack.of(Material.PURPLE_STAINED_GLASS_PANE), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_magenta", ItemStack.of(Material.MAGENTA_STAINED_GLASS_PANE), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));
        registerRecipe("STAINED_GLASS_PANE_any_to_pink", ItemStack.of(Material.PINK_STAINED_GLASS_PANE), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE)));

        registerRecipe("CANDLE_any_to_white", ItemStack.of(Material.WHITE_CANDLE), Ingredient.of(Material.WHITE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_CANDLE), Ingredient.of(Material.LIGHT_GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_gray", ItemStack.of(Material.GRAY_CANDLE), Ingredient.of(Material.GRAY_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_black", ItemStack.of(Material.BLACK_CANDLE), Ingredient.of(Material.BLACK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_brown", ItemStack.of(Material.BROWN_CANDLE), Ingredient.of(Material.BROWN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_red", ItemStack.of(Material.RED_CANDLE), Ingredient.of(Material.RED_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_orange", ItemStack.of(Material.ORANGE_CANDLE), Ingredient.of(Material.ORANGE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_yellow", ItemStack.of(Material.YELLOW_CANDLE), Ingredient.of(Material.YELLOW_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_lime", ItemStack.of(Material.LIME_CANDLE), Ingredient.of(Material.LIME_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_green", ItemStack.of(Material.GREEN_CANDLE), Ingredient.of(Material.GREEN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_cyan", ItemStack.of(Material.CYAN_CANDLE), Ingredient.of(Material.CYAN_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_CANDLE), Ingredient.of(Material.LIGHT_BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_blue", ItemStack.of(Material.BLUE_CANDLE), Ingredient.of(Material.BLUE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_purple", ItemStack.of(Material.PURPLE_CANDLE), Ingredient.of(Material.PURPLE_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_magenta", ItemStack.of(Material.MAGENTA_CANDLE), Ingredient.of(Material.MAGENTA_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
        registerRecipe("CANDLE_any_to_pink", ItemStack.of(Material.PINK_CANDLE), Ingredient.of(Material.PINK_DYE), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE)));
    }

    private static class Ingredient {
        private RecipeChoice recipeChoice = null;
        private Material material = null;
        private int amount = 1;

        private Ingredient(Material material, int amount) {
            this.material = material;
            this.amount = amount;
        }

        private Ingredient(RecipeChoice choice) {
            this.recipeChoice = choice;
        }

        private static Ingredient of(Material material) {
            return new Ingredient(material, 1);
        }

        private static Ingredient of(Material material, int amount) {
            return new Ingredient(material, amount);
        }

        private static Ingredient of(RecipeChoice choice) {
            return new Ingredient(choice);
        }

        private RecipeChoice getRecipeChoice() { return this.recipeChoice; }
        private Material getMaterial() { return this.material; }
        private int getAmount() { return this.amount; }
    }

    private static void registerRecipe(String keyString, ItemStack result, Ingredient... ingredients) {
        NamespacedKey key = Jade.key("shapelessRecipe_" + keyString);
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        for (Ingredient ingredient : ingredients) {
            if (ingredient.getMaterial() == null) {
                recipe.addIngredient(ingredient.getRecipeChoice());
            } else {
                recipe.addIngredient(ingredient.getAmount(), ingredient.getMaterial());
            }
        }
        RECIPES.put(key, recipe);
    }

    public static Map<NamespacedKey, ShapelessRecipe> getAllRecipes() {
        return RECIPES;
    }
}

package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static dev.jabberdrake.jade.utils.MaterialUtils.*;

public class ShapedRecipes {

    private static final Map<NamespacedKey, ShapedRecipe> RECIPES = new LinkedHashMap<>();

    static {
        registerRecipe("CONCRETE_any_to_white", ItemStack.of(Material.WHITE_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_gray", ItemStack.of(Material.GRAY_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_black", ItemStack.of(Material.BLACK_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_brown", ItemStack.of(Material.BROWN_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_red", ItemStack.of(Material.RED_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_orange", ItemStack.of(Material.ORANGE_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_yellow", ItemStack.of(Material.YELLOW_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_lime", ItemStack.of(Material.LIME_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_green", ItemStack.of(Material.GREEN_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_cyan", ItemStack.of(Material.CYAN_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_blue", ItemStack.of(Material.BLUE_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_purple", ItemStack.of(Material.PURPLE_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_magenta", ItemStack.of(Material.MAGENTA_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));
        registerRecipe("CONCRETE_any_to_pink", ItemStack.of(Material.PINK_CONCRETE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CONCRETE), 'A'));

        registerRecipe("TERRACOTTA_any_to_white", ItemStack.of(Material.WHITE_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_gray", ItemStack.of(Material.GRAY_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_black", ItemStack.of(Material.BLACK_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_brown", ItemStack.of(Material.BROWN_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_red", ItemStack.of(Material.RED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_orange", ItemStack.of(Material.ORANGE_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_yellow", ItemStack.of(Material.YELLOW_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_lime", ItemStack.of(Material.LIME_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_green", ItemStack.of(Material.GREEN_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_cyan", ItemStack.of(Material.CYAN_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_blue", ItemStack.of(Material.BLUE_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_purple", ItemStack.of(Material.PURPLE_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_magenta", ItemStack.of(Material.MAGENTA_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));
        registerRecipe("TERRACOTTA_any_to_pink", ItemStack.of(Material.PINK_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_TERRACOTTA), 'A'));

        registerRecipe("GLAZED_TERRACOTTA_any_to_white", ItemStack.of(Material.WHITE_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_gray", ItemStack.of(Material.GRAY_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_black", ItemStack.of(Material.BLACK_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_brown", ItemStack.of(Material.BROWN_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_red", ItemStack.of(Material.RED_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_orange", ItemStack.of(Material.ORANGE_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_yellow", ItemStack.of(Material.YELLOW_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_lime", ItemStack.of(Material.LIME_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_green", ItemStack.of(Material.GREEN_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_cyan", ItemStack.of(Material.CYAN_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_blue", ItemStack.of(Material.BLUE_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_purple", ItemStack.of(Material.PURPLE_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_magenta", ItemStack.of(Material.MAGENTA_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));
        registerRecipe("GLAZED_TERRACOTTA_any_to_pink", ItemStack.of(Material.PINK_GLAZED_TERRACOTTA, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_GLAZED_TERRACOTTA), 'A'));

        registerRecipe("STAINED_GLASS_any_to_white", ItemStack.of(Material.WHITE_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_gray", ItemStack.of(Material.GRAY_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_black", ItemStack.of(Material.BLACK_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_brown", ItemStack.of(Material.BROWN_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_red", ItemStack.of(Material.RED_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_orange", ItemStack.of(Material.ORANGE_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_yellow", ItemStack.of(Material.YELLOW_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_lime", ItemStack.of(Material.LIME_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_green", ItemStack.of(Material.GREEN_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_cyan", ItemStack.of(Material.CYAN_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_blue", ItemStack.of(Material.BLUE_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_purple", ItemStack.of(Material.PURPLE_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_magenta", ItemStack.of(Material.MAGENTA_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));
        registerRecipe("STAINED_GLASS_any_to_pink", ItemStack.of(Material.PINK_STAINED_GLASS, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS), 'A'));

        registerRecipe("STAINED_GLASS_PANE_any_to_white", ItemStack.of(Material.WHITE_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_gray", ItemStack.of(Material.GRAY_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_black", ItemStack.of(Material.BLACK_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_brown", ItemStack.of(Material.BROWN_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_red", ItemStack.of(Material.RED_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_orange", ItemStack.of(Material.ORANGE_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_yellow", ItemStack.of(Material.YELLOW_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_lime", ItemStack.of(Material.LIME_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_green", ItemStack.of(Material.GREEN_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_cyan", ItemStack.of(Material.CYAN_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_blue", ItemStack.of(Material.BLUE_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_purple", ItemStack.of(Material.PURPLE_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_magenta", ItemStack.of(Material.MAGENTA_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));
        registerRecipe("STAINED_GLASS_PANE_any_to_pink", ItemStack.of(Material.PINK_STAINED_GLASS_PANE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_STAINED_GLASS_PANE), 'A'));

        registerRecipe("WOOL_any_to_white", ItemStack.of(Material.WHITE_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_gray", ItemStack.of(Material.GRAY_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_black", ItemStack.of(Material.BLACK_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_brown", ItemStack.of(Material.BROWN_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_red", ItemStack.of(Material.RED_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_orange", ItemStack.of(Material.ORANGE_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_yellow", ItemStack.of(Material.YELLOW_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_lime", ItemStack.of(Material.LIME_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_green", ItemStack.of(Material.GREEN_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_cyan", ItemStack.of(Material.CYAN_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_blue", ItemStack.of(Material.BLUE_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_purple", ItemStack.of(Material.PURPLE_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_magenta", ItemStack.of(Material.MAGENTA_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));
        registerRecipe("WOOL_any_to_pink", ItemStack.of(Material.PINK_WOOL, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_WOOL), 'A'));

        registerRecipe("CARPET_any_to_white", ItemStack.of(Material.WHITE_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_gray", ItemStack.of(Material.GRAY_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_black", ItemStack.of(Material.BLACK_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_brown", ItemStack.of(Material.BROWN_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_red", ItemStack.of(Material.RED_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_orange", ItemStack.of(Material.ORANGE_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_yellow", ItemStack.of(Material.YELLOW_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_lime", ItemStack.of(Material.LIME_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_green", ItemStack.of(Material.GREEN_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_cyan", ItemStack.of(Material.CYAN_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_blue", ItemStack.of(Material.BLUE_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_purple", ItemStack.of(Material.PURPLE_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_magenta", ItemStack.of(Material.MAGENTA_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        registerRecipe("CARPET_any_to_pink", ItemStack.of(Material.PINK_CARPET, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CARPET), 'A'));
        
        registerRecipe("CANDLE_any_to_white", ItemStack.of(Material.WHITE_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.WHITE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_light_gray", ItemStack.of(Material.LIGHT_GRAY_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_gray", ItemStack.of(Material.GRAY_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GRAY_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_black", ItemStack.of(Material.BLACK_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLACK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_brown", ItemStack.of(Material.BROWN_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BROWN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_red", ItemStack.of(Material.RED_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.RED_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_orange", ItemStack.of(Material.ORANGE_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.ORANGE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_yellow", ItemStack.of(Material.YELLOW_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.YELLOW_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_lime", ItemStack.of(Material.LIME_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIME_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_green", ItemStack.of(Material.GREEN_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.GREEN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_cyan", ItemStack.of(Material.CYAN_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.CYAN_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_light_blue", ItemStack.of(Material.LIGHT_BLUE_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.LIGHT_BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_blue", ItemStack.of(Material.BLUE_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.BLUE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_purple", ItemStack.of(Material.PURPLE_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PURPLE_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_magenta", ItemStack.of(Material.MAGENTA_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.MAGENTA_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
        registerRecipe("CANDLE_any_to_pink", ItemStack.of(Material.PINK_CANDLE, 8), "AAA;ABA;AAA", Ingredient.of(Material.PINK_DYE, 'B'), Ingredient.of(new RecipeChoice.MaterialChoice(ALL_CANDLE), 'A'));
    }

    private static class Ingredient {
        private RecipeChoice recipeChoice = null;
        private Material material = null;
        private char key = 'A';

        private Ingredient(Material material, char key) {
            this.material = material;
            this.key = key;
        }

        private Ingredient(RecipeChoice choice, char key) {
            this.recipeChoice = choice;
            this.key = key;
        }

        private static Ingredient of(Material material, char key) {
            return new Ingredient(material, key);
        }

        private static Ingredient of(RecipeChoice choice, char key) {
            return new Ingredient(choice, key);
        }

        private RecipeChoice getRecipeChoice() { return this.recipeChoice; }
        private Material getMaterial() { return this.material; }
        private char getKey() { return this.key; }
    }

    private static void registerRecipe(String keyString, ItemStack result, String shape, Ingredient... ingredients) {
        NamespacedKey key = Jade.key("shapedRecipe_" + keyString);
        String[] shapeLines = shape.split(";");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(shapeLines[0], shapeLines[1], shapeLines[2]);
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getMaterial() == null) {
                recipe.setIngredient(ingredient.getKey(), ingredient.getRecipeChoice());
            } else {
                recipe.setIngredient(ingredient.getKey(), ingredient.getMaterial());
            }
        }
        RECIPES.put(key, recipe);
    }

    public static Map<NamespacedKey, ShapedRecipe> getAllRecipes() {
        return RECIPES;
    }
}

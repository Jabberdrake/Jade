package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class CookingRecipes {

    private static final Map<NamespacedKey, CookingRecipe<?>> RECIPES = new LinkedHashMap<>();

    static {
        registerRecipe("rottenFlesh_to_leather", ItemStack.of(Material.LEATHER), Material.ROTTEN_FLESH, 0.2f, 200, CookingStations.FURNACE, CookingStations.SMOKER, CookingStations.CAMPFIRE);
        registerRecipe("rawCopperBlock_to_copperBlock", ItemStack.of(Material.COPPER_BLOCK), Material.RAW_COPPER_BLOCK, 6.3f, 400, CookingStations.FURNACE, CookingStations.BLAST_FURNACE);
        registerRecipe("rawIronBlock_to_ironBlock", ItemStack.of(Material.IRON_BLOCK), Material.RAW_IRON_BLOCK, 6.3f, 400, CookingStations.FURNACE, CookingStations.BLAST_FURNACE);
        registerRecipe("rawGoldBlock_to_goldBlock", ItemStack.of(Material.GOLD_BLOCK), Material.RAW_GOLD_BLOCK, 9f, 400, CookingStations.FURNACE, CookingStations.BLAST_FURNACE);

        registerRecipe("dehydrate_tube_coral_block", ItemStack.of(Material.DEAD_TUBE_CORAL_BLOCK), Material.TUBE_CORAL_BLOCK, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_brain_coral_block", ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK), Material.BRAIN_CORAL_BLOCK, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_bubble_coral_block", ItemStack.of(Material.DEAD_BUBBLE_CORAL_BLOCK), Material.BUBBLE_CORAL_BLOCK, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_fire_coral_block", ItemStack.of(Material.DEAD_FIRE_CORAL_BLOCK), Material.FIRE_CORAL_BLOCK, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_horn_coral_block", ItemStack.of(Material.DEAD_HORN_CORAL_BLOCK), Material.HORN_CORAL_BLOCK, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_tube_coral", ItemStack.of(Material.DEAD_TUBE_CORAL), Material.TUBE_CORAL, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_brain_coral", ItemStack.of(Material.DEAD_BRAIN_CORAL), Material.BRAIN_CORAL, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_bubble_coral", ItemStack.of(Material.DEAD_BUBBLE_CORAL), Material.BUBBLE_CORAL, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_fire_coral", ItemStack.of(Material.DEAD_FIRE_CORAL), Material.FIRE_CORAL, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_horn_coral", ItemStack.of(Material.DEAD_HORN_CORAL), Material.HORN_CORAL, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_tube_coral_fan", ItemStack.of(Material.DEAD_TUBE_CORAL_FAN), Material.TUBE_CORAL_FAN, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_brain_coral_fan", ItemStack.of(Material.DEAD_BRAIN_CORAL_FAN), Material.BRAIN_CORAL_FAN, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_bubble_coral_fan", ItemStack.of(Material.DEAD_BUBBLE_CORAL_FAN), Material.BUBBLE_CORAL_FAN, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_fire_coral_fan", ItemStack.of(Material.DEAD_FIRE_CORAL_FAN), Material.FIRE_CORAL_FAN, 0.2f, 200, CookingStations.FURNACE);
        registerRecipe("dehydrate_horn_coral_fan", ItemStack.of(Material.DEAD_HORN_CORAL_FAN), Material.HORN_CORAL_FAN, 0.2f, 200, CookingStations.FURNACE);
    }

    private enum CookingStations {
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE
    }

    private static void registerRecipe(String baseKeyString, ItemStack result, Material source, float experience, int cookingTime, CookingStations... stations) {
        for (CookingStations station : stations) {
            CookingRecipe<?> recipe = null;
            NamespacedKey key = null;
            switch (station) {
                case FURNACE:
                    key = Jade.key("furnaceRecipe_" + baseKeyString);
                    recipe = new FurnaceRecipe(key, result, source, experience, cookingTime);
                    break;
                case BLAST_FURNACE:
                    key = Jade.key("blastingRecipe_" + baseKeyString);
                    recipe = new BlastingRecipe(key, result, source, experience, cookingTime);
                    break;
                case SMOKER:
                    key = Jade.key("smokingRecipe_" + baseKeyString);
                    recipe = new SmokingRecipe(key, result, source, experience, cookingTime);
                    break;
                case CAMPFIRE:
                    key = Jade.key("campfireRecipe_" + baseKeyString);
                    recipe = new CampfireRecipe(key, result, source, experience, cookingTime);
                    break;
            }

            if (key == null) continue;
            RECIPES.put(key, recipe);
        }
    }

    public static Map<NamespacedKey, CookingRecipe<?>> getAllRecipes() {
        return RECIPES;
    }
}

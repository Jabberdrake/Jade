package dev.jabberdrake.jade.crafting.recipes;

import dev.jabberdrake.jade.Jade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class StonecutterRecipes {

    private static final Map<NamespacedKey, StonecuttingRecipe> RECIPES = new LinkedHashMap<>();

    static {
        registerRecipe("cobblestone_to_gravel", ItemStack.of(Material.GRAVEL), Material.COBBLESTONE);
        registerRecipe("cobbledDeepslate_to_gravel", ItemStack.of(Material.GRAVEL), Material.COBBLED_DEEPSLATE);
        registerRecipe("gravel_to_sand", ItemStack.of(Material.SAND), Material.GRAVEL);

        // OAK
        registerRecipe("OAK_log_to_strippedLog", ItemStack.of(Material.STRIPPED_OAK_LOG), Material.OAK_LOG);
        registerRecipe("OAK_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_OAK_WOOD), Material.OAK_WOOD);
        registerRecipe("OAK_planks_to_stairs", ItemStack.of(Material.OAK_STAIRS), Material.OAK_PLANKS);
        registerRecipe("OAK_planks_to_slabs", ItemStack.of(Material.OAK_SLAB, 2), Material.OAK_PLANKS);
        registerRecipe("OAK_planks_to_trapdoors", ItemStack.of(Material.OAK_TRAPDOOR, 2), Material.OAK_PLANKS);

        // BIRCH
        registerRecipe("BIRCH_log_to_strippedLog", ItemStack.of(Material.STRIPPED_BIRCH_LOG), Material.BIRCH_LOG);
        registerRecipe("BIRCH_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_BIRCH_WOOD), Material.BIRCH_WOOD);
        registerRecipe("BIRCH_planks_to_stairs", ItemStack.of(Material.BIRCH_STAIRS), Material.BIRCH_PLANKS);
        registerRecipe("BIRCH_planks_to_slabs", ItemStack.of(Material.BIRCH_SLAB, 2), Material.BIRCH_PLANKS);
        registerRecipe("BIRCH_planks_to_trapdoors", ItemStack.of(Material.BIRCH_TRAPDOOR, 2), Material.BIRCH_PLANKS);

        // SPRUCE
        registerRecipe("SPRUCE_log_to_strippedLog", ItemStack.of(Material.STRIPPED_SPRUCE_LOG), Material.SPRUCE_LOG);
        registerRecipe("SPRUCE_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_SPRUCE_WOOD), Material.SPRUCE_WOOD);
        registerRecipe("SPRUCE_planks_to_stairs", ItemStack.of(Material.SPRUCE_STAIRS), Material.SPRUCE_PLANKS);
        registerRecipe("SPRUCE_planks_to_slabs", ItemStack.of(Material.SPRUCE_SLAB, 2), Material.SPRUCE_PLANKS);
        registerRecipe("SPRUCE_planks_to_trapdoors", ItemStack.of(Material.SPRUCE_TRAPDOOR, 2), Material.SPRUCE_PLANKS);

        // JUNGLE
        registerRecipe("JUNGLE_log_to_strippedLog", ItemStack.of(Material.STRIPPED_JUNGLE_LOG), Material.JUNGLE_LOG);
        registerRecipe("JUNGLE_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_JUNGLE_WOOD), Material.JUNGLE_WOOD);
        registerRecipe("JUNGLE_planks_to_stairs", ItemStack.of(Material.JUNGLE_STAIRS), Material.JUNGLE_PLANKS);
        registerRecipe("JUNGLE_planks_to_slabs", ItemStack.of(Material.JUNGLE_SLAB, 2), Material.JUNGLE_PLANKS);
        registerRecipe("JUNGLE_planks_to_trapdoors", ItemStack.of(Material.JUNGLE_TRAPDOOR, 2), Material.JUNGLE_PLANKS);

        // ACACIA
        registerRecipe("ACACIA_log_to_strippedLog", ItemStack.of(Material.STRIPPED_ACACIA_LOG), Material.ACACIA_LOG);
        registerRecipe("ACACIA_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_ACACIA_WOOD), Material.ACACIA_WOOD);
        registerRecipe("ACACIA_planks_to_stairs", ItemStack.of(Material.ACACIA_STAIRS), Material.ACACIA_PLANKS);
        registerRecipe("ACACIA_planks_to_slabs", ItemStack.of(Material.ACACIA_SLAB, 2), Material.ACACIA_PLANKS);
        registerRecipe("ACACIA_planks_to_trapdoors", ItemStack.of(Material.ACACIA_TRAPDOOR, 2), Material.ACACIA_PLANKS);

        // DARK OAK
        registerRecipe("DARK_OAK_log_to_strippedLog", ItemStack.of(Material.STRIPPED_DARK_OAK_LOG), Material.DARK_OAK_LOG);
        registerRecipe("DARK_OAK_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_DARK_OAK_WOOD), Material.DARK_OAK_WOOD);
        registerRecipe("DARK_OAK_planks_to_stairs", ItemStack.of(Material.DARK_OAK_STAIRS), Material.DARK_OAK_PLANKS);
        registerRecipe("DARK_OAK_planks_to_slabs", ItemStack.of(Material.DARK_OAK_SLAB, 2), Material.DARK_OAK_PLANKS);
        registerRecipe("DARK_OAK_planks_to_trapdoors", ItemStack.of(Material.DARK_OAK_TRAPDOOR, 2), Material.DARK_OAK_PLANKS);

        // MANGROVE
        registerRecipe("MANGROVE_log_to_strippedLog", ItemStack.of(Material.STRIPPED_MANGROVE_LOG), Material.MANGROVE_LOG);
        registerRecipe("MANGROVE_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_MANGROVE_WOOD), Material.MANGROVE_WOOD);
        registerRecipe("MANGROVE_planks_to_stairs", ItemStack.of(Material.MANGROVE_STAIRS), Material.MANGROVE_PLANKS);
        registerRecipe("MANGROVE_planks_to_slabs", ItemStack.of(Material.MANGROVE_SLAB, 2), Material.MANGROVE_PLANKS);
        registerRecipe("MANGROVE_planks_to_trapdoors", ItemStack.of(Material.MANGROVE_TRAPDOOR, 2), Material.MANGROVE_PLANKS);

        // BAMBOO
        registerRecipe("BAMBOO_block_to_strippedBlock", ItemStack.of(Material.STRIPPED_BAMBOO_BLOCK), Material.BAMBOO_BLOCK);
        registerRecipe("BAMBOO_planks_to_mosaicPlanks", ItemStack.of(Material.BAMBOO_MOSAIC), Material.BAMBOO_PLANKS);
        registerRecipe("BAMBOO_planks_to_stairs", ItemStack.of(Material.BAMBOO_STAIRS), Material.BAMBOO_PLANKS);
        registerRecipe("BAMBOO_planks_to_slabs", ItemStack.of(Material.BAMBOO_SLAB, 2), Material.BAMBOO_PLANKS);
        registerRecipe("BAMBOO_planks_to_trapdoors", ItemStack.of(Material.BAMBOO_TRAPDOOR, 2), Material.BAMBOO_PLANKS);
        registerRecipe("BAMBOO_mosaicPlanks_to_planks", ItemStack.of(Material.BAMBOO_PLANKS), Material.BAMBOO_MOSAIC);
        registerRecipe("BAMBOO_mosaicPlanks_to_mosaicSlabs", ItemStack.of(Material.BAMBOO_MOSAIC_SLAB), Material.BAMBOO_MOSAIC);
        registerRecipe("BAMBOO_mosaicPlanks_to_mosaicStairs", ItemStack.of(Material.BAMBOO_MOSAIC_STAIRS), Material.BAMBOO_MOSAIC);

        // PALE OAK
        registerRecipe("PALE_OAK_log_to_strippedLog", ItemStack.of(Material.STRIPPED_PALE_OAK_LOG), Material.PALE_OAK_LOG);
        registerRecipe("PALE_OAK_wood_to_strippedWood", ItemStack.of(Material.STRIPPED_PALE_OAK_WOOD), Material.PALE_OAK_WOOD);
        registerRecipe("PALE_OAK_planks_to_stairs", ItemStack.of(Material.PALE_OAK_STAIRS), Material.PALE_OAK_PLANKS);
        registerRecipe("PALE_OAK_planks_to_slabs", ItemStack.of(Material.PALE_OAK_SLAB, 2), Material.PALE_OAK_PLANKS);
        registerRecipe("PALE_OAK_planks_to_trapdoors", ItemStack.of(Material.PALE_OAK_TRAPDOOR, 2), Material.PALE_OAK_PLANKS);
    }

    private static void registerRecipe(String keyString, ItemStack result, Material source) {
        NamespacedKey key = Jade.key("stonecuttingRecipe_" + keyString);
        StonecuttingRecipe recipe = new StonecuttingRecipe(key, result, source);
        RECIPES.put(key, recipe);
    }

    public static Map<NamespacedKey, StonecuttingRecipe> getAllRecipes() {
        return RECIPES;
    }
}

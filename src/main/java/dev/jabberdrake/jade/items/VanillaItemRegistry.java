package dev.jabberdrake.jade.items;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class VanillaItemRegistry {

    private static final Map<String, VanillaItem> VANILLA_ITEMS = new HashMap<>();

    // UNCOMMON ITEMS
    public static final VanillaItem NAUTILUS_SHELL = registerItem(VanillaItem.builder()
            .data("Nautilus Shell", Material.NAUTILUS_SHELL, Rarity.UNCOMMON)
            .build());


    // RARE ITEMS
    public static final VanillaItem NETHERITE_HELMET = registerItem(VanillaItem.builder()
            .data("Netherite Helmet", Material.NETHERITE_HELMET, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Netherite Chestplate", Material.NETHERITE_CHESTPLATE, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Netherite Leggings", Material.NETHERITE_LEGGINGS, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_BOOTS = registerItem(VanillaItem.builder()
            .data("Netherite Boots", Material.NETHERITE_BOOTS, Rarity.RARE)
            .build());

    public static final VanillaItem NETHERITE_SWORD = registerItem(VanillaItem.builder()
            .data("Netherite Sword", Material.NETHERITE_SWORD, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_PICKAXE = registerItem(VanillaItem.builder()
            .data("Netherite Pickaxe", Material.NETHERITE_PICKAXE, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_AXE = registerItem(VanillaItem.builder()
            .data("Netherite Axe", Material.NETHERITE_AXE, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_SHOVEL = registerItem(VanillaItem.builder()
            .data("Netherite Shovel", Material.NETHERITE_SHOVEL, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_HOE = registerItem(VanillaItem.builder()
            .data("Netherite Hoe", Material.NETHERITE_HOE, Rarity.RARE)
            .build());

    public static final VanillaItem NETHERITE_INGOT = registerItem(VanillaItem.builder()
            .data("Netherite Ingot", Material.NETHERITE_INGOT, Rarity.RARE)
            .build());
    public static final VanillaItem NETHERITE_BLOCK = registerItem(VanillaItem.builder()
            .data("Block of Netherite", Material.NETHERITE_BLOCK, Rarity.RARE)
            .build());

    public static final VanillaItem TRIDENT = registerItem(VanillaItem.builder()
            .data("Trident", Material.TRIDENT, Rarity.RARE)
            .build());

    // LEGENDARY ITEMS
    public static final VanillaItem DRAGON_EGG = registerItem(VanillaItem.builder()
            .data("Dragon Egg", Material.DRAGON_EGG, Rarity.LEGENDARY)
            .build());

    // MYTHIC ITEMS
    public static final VanillaItem NETHER_STAR = registerItem(VanillaItem.builder()
            .data("Nether Star", Material.NETHER_STAR, Rarity.MYTHIC)
            .build());

    public static VanillaItem registerItem(VanillaItem item) {
        VANILLA_ITEMS.put(item.getKey(), item);
        return item;
    }

    public static VanillaItem getVanillaItem(String itemKey) {
        return VANILLA_ITEMS.get(itemKey);
    }
}

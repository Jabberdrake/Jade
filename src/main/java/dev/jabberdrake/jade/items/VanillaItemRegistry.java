package dev.jabberdrake.jade.items;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaItemRegistry {

    private static final Map<String, VanillaItem> VANILLA_ITEMS = new HashMap<>();

    // COMMON ITEMS
    public static final VanillaItem CHAINMAIL_HELMET = registerItem(VanillaItem.builder()
            .data("Chainmail Helmet", Material.CHAINMAIL_HELMET, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem CHAINMAIL_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Chainmail Chestplate", Material.CHAINMAIL_CHESTPLATE, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem CHAINMAIL_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Chainmail Leggings", Material.CHAINMAIL_LEGGINGS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem CHAINMAIL_BOOTS = registerItem(VanillaItem.builder()
            .data("Chainmail Boots", Material.CHAINMAIL_BOOTS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());

    public static final VanillaItem IRON_HELMET = registerItem(VanillaItem.builder()
            .data("Iron Helmet", Material.IRON_HELMET, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem IRON_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Iron Chestplate", Material.IRON_CHESTPLATE, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem IRON_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Iron Leggings", Material.IRON_LEGGINGS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem IRON_BOOTS = registerItem(VanillaItem.builder()
            .data("Iron Boots", Material.IRON_BOOTS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem IRON_SWORD = registerItem(VanillaItem.builder()
            .data("Iron Sword", Material.IRON_SWORD, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem IRON_PICKAXE = registerItem(VanillaItem.builder()
            .data("Iron Pickaxe", Material.IRON_PICKAXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem IRON_AXE = registerItem(VanillaItem.builder()
            .data("Iron Axe", Material.IRON_AXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem IRON_SHOVEL = registerItem(VanillaItem.builder()
            .data("Iron Shovel", Material.IRON_SHOVEL, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem IRON_HOE = registerItem(VanillaItem.builder()
            .data("Iron Hoe", Material.IRON_HOE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());

    public static final VanillaItem DIAMOND_HELMET = registerItem(VanillaItem.builder()
            .data("Diamond Helmet", Material.DIAMOND_HELMET, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem DIAMOND_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Diamond Chestplate", Material.DIAMOND_CHESTPLATE, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem DIAMOND_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Diamond Leggings", Material.DIAMOND_LEGGINGS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem DIAMOND_BOOTS = registerItem(VanillaItem.builder()
            .data("Diamond Boots", Material.DIAMOND_BOOTS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem DIAMOND_SWORD = registerItem(VanillaItem.builder()
            .data("Diamond Sword", Material.DIAMOND_SWORD, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem DIAMOND_PICKAXE = registerItem(VanillaItem.builder()
            .data("Diamond Pickaxe", Material.DIAMOND_PICKAXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem DIAMOND_AXE = registerItem(VanillaItem.builder()
            .data("Diamond Axe", Material.DIAMOND_AXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem DIAMOND_SHOVEL = registerItem(VanillaItem.builder()
            .data("Diamond Shovel", Material.DIAMOND_SHOVEL, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem DIAMOND_HOE = registerItem(VanillaItem.builder()
            .data("Diamond Hoe", Material.DIAMOND_HOE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());

    public static final VanillaItem LEATHER_HELMET = registerItem(VanillaItem.builder()
            .data("Leather Cap", Material.LEATHER_HELMET, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem LEATHER_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Leather Tunic", Material.LEATHER_CHESTPLATE, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem LEATHER_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Leather Pants", Material.LEATHER_LEGGINGS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem LEATHER_BOOTS = registerItem(VanillaItem.builder()
            .data("Leather Boots", Material.LEATHER_BOOTS, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());

    public static final VanillaItem WOODEN_SWORD = registerItem(VanillaItem.builder()
            .data("Wooden Sword", Material.WOODEN_SWORD, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem WOODEN_PICKAXE = registerItem(VanillaItem.builder()
            .data("Wooden Pickaxe", Material.WOODEN_PICKAXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem WOODEN_AXE = registerItem(VanillaItem.builder()
            .data("Wooden Axe", Material.WOODEN_AXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem WOODEN_SHOVEL = registerItem(VanillaItem.builder()
            .data("Wooden Shovel", Material.WOODEN_SHOVEL, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem WOODEN_HOE = registerItem(VanillaItem.builder()
            .data("Wooden Hoe", Material.WOODEN_HOE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());

    public static final VanillaItem STONE_SWORD = registerItem(VanillaItem.builder()
            .data("Stone Sword", Material.STONE_SWORD, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem STONE_PICKAXE = registerItem(VanillaItem.builder()
            .data("Stone Pickaxe", Material.STONE_PICKAXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem STONE_AXE = registerItem(VanillaItem.builder()
            .data("Stone Axe", Material.STONE_AXE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem STONE_SHOVEL = registerItem(VanillaItem.builder()
            .data("Stone Shovel", Material.STONE_SHOVEL, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem STONE_HOE = registerItem(VanillaItem.builder()
            .data("Stone Hoe", Material.STONE_HOE, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());

    public static final VanillaItem TURTLE_HELMET = registerItem(VanillaItem.builder()
            .data("Turtle Shell", Material.TURTLE_HELMET, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());

    public static final VanillaItem LEATHER_HORSE_ARMOR = registerItem(VanillaItem.builder()
            .data("Leather Horse Armor", Material.LEATHER_HORSE_ARMOR, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem IRON_HORSE_ARMOR = registerItem(VanillaItem.builder()
            .data("Iron Horse Armor", Material.IRON_HORSE_ARMOR, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem GOLDEN_HORSE_ARMOR = registerItem(VanillaItem.builder()
            .data("Golden Horse Armor", Material.GOLDEN_HORSE_ARMOR, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem DIAMOND_HORSE_ARMOR = registerItem(VanillaItem.builder()
            .data("Diamond Horse Armor", Material.DIAMOND_HORSE_ARMOR, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem WOLF_ARMOR = registerItem(VanillaItem.builder()
            .data("Wolf Armor", Material.WOLF_ARMOR, Rarity.COMMON)
            .tags(ItemTag.ARMOR)
            .build());

    public static final VanillaItem BOW = registerItem(VanillaItem.builder()
            .data("Bow", Material.BOW, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem CROSSBOW = registerItem(VanillaItem.builder()
            .data("Crossbow", Material.CROSSBOW, Rarity.COMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem TIPPED_ARROW = registerItem(VanillaItem.builder()
            .data("Tipped Arrow", Material.TIPPED_ARROW, Rarity.COMMON)
            .tags(ItemTag.CONSUMABLE)
            .build());

    public static final VanillaItem FISHING_ROD = registerItem(VanillaItem.builder()
            .data("Fishing Rod", Material.FISHING_ROD, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem SHEARS = registerItem(VanillaItem.builder()
            .data("Shears", Material.SHEARS, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem BRUSH = registerItem(VanillaItem.builder()
            .data("Brush", Material.BRUSH, Rarity.COMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem POTION = registerItem(VanillaItem.builder()
            .data("Uncraftable Potion", Material.POTION, Rarity.COMMON)
            .tags(ItemTag.CONSUMABLE)
            .build());
    public static final VanillaItem SPLASH_POTION = registerItem(VanillaItem.builder()
            .data("Splash Uncraftable Potion", Material.SPLASH_POTION, Rarity.COMMON)
            .tags(ItemTag.CONSUMABLE)
            .build());
    public static final VanillaItem LINGERING_POTION = registerItem(VanillaItem.builder()
            .data("Lingering Uncraftable Potion", Material.LINGERING_POTION, Rarity.COMMON)
            .tags(ItemTag.CONSUMABLE)
            .build());

    // UNCOMMON ITEMS
    public static final VanillaItem CREEPER_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Creeper Banner Pattern", Material.CREEPER_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem PIGLIN_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Snout Banner Pattern", Material.PIGLIN_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SKULL_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Skull Banner Pattern", Material.SKULL_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MOJANG_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Thing Banner Pattern", Material.MOJANG_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem FLOW_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Flow Banner Pattern", Material.FLOW_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem GUSTER_BANNER_PATTERN = registerItem(VanillaItem.builder()
            .data("Guster Banner Pattern", Material.GUSTER_BANNER_PATTERN, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem SNIFFER_EGG = registerItem(VanillaItem.builder()
            .data("Sniffer Egg", Material.SNIFFER_EGG, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem RECOVERY_COMPASS = registerItem(VanillaItem.builder()
            .data("Recovery Compass", Material.RECOVERY_COMPASS, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem DISC_FRAGMENT_5 = registerItem(VanillaItem.builder()
            .data("Disc Fragment 5", Material.DISC_FRAGMENT_5, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ECHO_SHARD = registerItem(VanillaItem.builder()
            .data("Echo Shard", Material.ECHO_SHARD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem GOAT_HORN = registerItem(VanillaItem.builder()
            .data("Goat Horn", Material.GOAT_HORN, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ANGLER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Angler Pottery Sherd", Material.ANGLER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ARCHER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Archer Pottery Sherd", Material.ARCHER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ARMS_UP_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Arms Up Pottery Sherd", Material.ARMS_UP_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem BLADE_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Blade Pottery Sherd", Material.BLADE_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem BREWER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Brewer Pottery Sherd", Material.BREWER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem BURN_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Burn Pottery Sherd", Material.BURN_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem DANGER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Danger Pottery Sherd", Material.DANGER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem EXPLORER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Explorer Pottery Sherd", Material.EXPLORER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem FLOW_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Flow Pottery Sherd", Material.FLOW_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem FRIEND_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Friend Pottery Sherd", Material.FRIEND_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem GUSTER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Guster Pottery Sherd", Material.GUSTER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem HEART_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Heart Pottery Sherd", Material.HEART_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem HEARTBREAK_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Heartbreak Pottery Sherd", Material.HEARTBREAK_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem HOWL_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Howl Pottery Sherd", Material.HOWL_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MINER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Miner Pottery Sherd", Material.MINER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MOURNER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Mourner Pottery Sherd", Material.MOURNER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem PLENTY_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Plenty Pottery Sherd", Material.PLENTY_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem PRIZE_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Prize Pottery Sherd", Material.PRIZE_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SCRAPE_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Scrape Pottery Sherd", Material.SCRAPE_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SHEAF_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Sheaf Pottery Sherd", Material.SHEAF_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SHELTER_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Shelter Pottery Sherd", Material.SHELTER_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SKULL_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Skull Pottery Sherd", Material.SKULL_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SNORT_POTTERY_SHERD = registerItem(VanillaItem.builder()
            .data("Snort Pottery Sherd", Material.SNORT_POTTERY_SHERD, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem OMINOUS_BOTTLE = registerItem(VanillaItem.builder()
            .data("Ominous Bottle", Material.OMINOUS_BOTTLE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem NETHERITE_UPGRADE_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Netherite Upgrade", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Sentry Armor Trim", Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem DUNE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Dune Armor Trim", Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem COAST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Coast Armor Trim", Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem WILD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Wild Armor Trim", Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem TIDE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Tide Armor Trim", Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Snout Armor Trim", Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem RIB_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Rib Armor Trim", Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Wayfinder Armor Trim", Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Shaper Armor Trim", Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem RAISER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Raiser Armor Trim", Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem HOST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Host Armor Trim", Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Flow Armor Trim", Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Bolt Armor Trim", Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem WARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Ward Armor Trim", Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem EYE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Eye Armor Trim", Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem VEX_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Vex Armor Trim", Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Spire Armor Trim", Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(VanillaItem.builder()
            .data("Silence Armor Trim", Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem NAUTILUS_SHELL = registerItem(VanillaItem.builder()
            .data("Nautilus Shell", Material.NAUTILUS_SHELL, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem TRIDENT = registerItem(VanillaItem.builder()
            .data("Trident", Material.TRIDENT, Rarity.UNCOMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem HEART_OF_THE_SEA = registerItem(VanillaItem.builder()
            .data("Heart of the Sea", Material.HEART_OF_THE_SEA, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem CONDUIT = registerItem(VanillaItem.builder()
            .data("Conduit", Material.CONDUIT, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem DRAGON_BREATH = registerItem(VanillaItem.builder()
            .data("Dragon's Breath", Material.DRAGON_BREATH, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ZOMBIE_HEAD = registerItem(VanillaItem.builder()
            .data("Zombie Head", Material.ZOMBIE_HEAD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem CREEPER_HEAD = registerItem(VanillaItem.builder()
            .data("Creeper Head", Material.CREEPER_HEAD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem SKELETON_SKULL = registerItem(VanillaItem.builder()
            .data("Skeleton Skull", Material.SKELETON_SKULL, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem WITHER_SKELETON_SKULL = registerItem(VanillaItem.builder()
            .data("Wither Skeleton Skull", Material.WITHER_SKELETON_SKULL, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem PIGLIN_HEAD = registerItem(VanillaItem.builder()
            .data("Piglin Head", Material.PIGLIN_HEAD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem DRAGON_HEAD = registerItem(VanillaItem.builder()
            .data("Dragon Head", Material.DRAGON_HEAD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem TOTEM_OF_UNDYING = registerItem(VanillaItem.builder()
            .data("Totem of Undying", Material.TOTEM_OF_UNDYING, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem NETHER_STAR = registerItem(VanillaItem.builder()
            .data("Nether Star", Material.NETHER_STAR, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem BEACON = registerItem(VanillaItem.builder()
            .data("Beacon", Material.BEACON, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ELYTRA = registerItem(VanillaItem.builder()
            .data("Elytra", Material.ELYTRA, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ENCHANTED_GOLDEN_APPLE = registerItem(VanillaItem.builder()
            .data("Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem ENCHANTED_BOOK = registerItem(VanillaItem.builder()
            .data("Enchanted Book", Material.ENCHANTED_BOOK, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem MUSIC_DISC_13 = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_13, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_CAT = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_CAT, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_BLOCKS = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_BLOCKS, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_CHIRP = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_CHIRP, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_FAR = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_FAR, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_MALL = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_MALL, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_MELLOHI = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_MELLOHI, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_STAL = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_STAL, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_STRAD = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_STRAD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_WARD = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_WARD, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_11 = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_11, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_WAIT = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_WAIT, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_5 = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_5, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_RELIC = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_RELIC, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_CREATOR = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_CREATOR, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_CREATOR_MUSIC_BOX = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_CREATOR_MUSIC_BOX, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_PRECIPICE = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_PRECIPICE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_TEARS = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_TEARS, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_PIGSTEP = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_PIGSTEP, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_OTHERSIDE = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_OTHERSIDE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MUSIC_DISC_LAVA_CHICKEN = registerItem(VanillaItem.builder()
            .data("Music Disc", Material.MUSIC_DISC_LAVA_CHICKEN, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem HEAVY_CORE = registerItem(VanillaItem.builder()
            .data("Heavy Core", Material.HEAVY_CORE, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem MACE = registerItem(VanillaItem.builder()
            .data("Mace", Material.MACE, Rarity.UNCOMMON)
            .build());

    public static final VanillaItem ANCIENT_DEBRIS = registerItem(VanillaItem.builder()
            .data("Ancient Debris", Material.ANCIENT_DEBRIS, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem NETHERITE_INGOT = registerItem(VanillaItem.builder()
            .data("Netherite Ingot", Material.NETHERITE_INGOT, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem NETHERITE_BLOCK = registerItem(VanillaItem.builder()
            .data("Block of Netherite", Material.NETHERITE_BLOCK, Rarity.UNCOMMON)
            .build());
    public static final VanillaItem NETHERITE_HELMET = registerItem(VanillaItem.builder()
            .data("Netherite Helmet", Material.NETHERITE_HELMET, Rarity.UNCOMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem NETHERITE_CHESTPLATE = registerItem(VanillaItem.builder()
            .data("Netherite Chestplate", Material.NETHERITE_CHESTPLATE, Rarity.UNCOMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem NETHERITE_LEGGINGS = registerItem(VanillaItem.builder()
            .data("Netherite Leggings", Material.NETHERITE_LEGGINGS, Rarity.UNCOMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem NETHERITE_BOOTS = registerItem(VanillaItem.builder()
            .data("Netherite Boots", Material.NETHERITE_BOOTS, Rarity.UNCOMMON)
            .tags(ItemTag.ARMOR)
            .build());
    public static final VanillaItem NETHERITE_SWORD = registerItem(VanillaItem.builder()
            .data("Netherite Sword", Material.NETHERITE_SWORD, Rarity.UNCOMMON)
            .tags(ItemTag.WEAPON)
            .build());
    public static final VanillaItem NETHERITE_PICKAXE = registerItem(VanillaItem.builder()
            .data("Netherite Pickaxe", Material.NETHERITE_PICKAXE, Rarity.UNCOMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem NETHERITE_AXE = registerItem(VanillaItem.builder()
            .data("Netherite Axe", Material.NETHERITE_AXE, Rarity.UNCOMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem NETHERITE_SHOVEL = registerItem(VanillaItem.builder()
            .data("Netherite Shovel", Material.NETHERITE_SHOVEL, Rarity.UNCOMMON)
            .tags(ItemTag.TOOL)
            .build());
    public static final VanillaItem NETHERITE_HOE = registerItem(VanillaItem.builder()
            .data("Netherite Hoe", Material.NETHERITE_HOE, Rarity.UNCOMMON)
            .tags(ItemTag.TOOL)
            .build());


    // RARE ITEMS

    // LEGENDARY ITEMS
    public static final VanillaItem DRAGON_EGG = registerItem(VanillaItem.builder()
            .data("Dragon Egg", Material.DRAGON_EGG, Rarity.LEGENDARY)
            .build());

    // MYTHIC ITEMS

    public static VanillaItem registerItem(VanillaItem item) {
        VANILLA_ITEMS.put(item.getKey(), item);
        return item;
    }

    public static VanillaItem getVanillaItem(String itemKey) {
        return VANILLA_ITEMS.get(itemKey);
    }
}

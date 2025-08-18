package dev.jabberdrake.jade.items;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class JadeItemRegistry {

    private static final Map<String, JadeItem> ITEMS = new HashMap<>();

    // GADGET ITEMS
    public static final JadeItem PHANTOM_ITEM_FRAME = registerJadeItem(GadgetItem.builder()
            .data("Phantom Item Frame", "phantom_item_frame", Rarity.UNCOMMON)
            .item("minecraft:item_frame[minecraft:entity_data={id:\"minecraft:item_frame\",Invisible:true}]")
            .build());
    public static final JadeItem BUILDERS_WAND = registerJadeItem(GadgetItem.builder()
            .data("Builder's Wand", "builders_wand", Rarity.UNCOMMON)
            .item(Material.GOLDEN_SWORD)
            .build());

    // INGREDIENT ITEMS
    public static final JadeItem ENDER_SHARD = registerJadeItem(IngredientItem.builder()
            .data("Ender Shard", "ender_shard", Rarity.COMMON)
            .item(Material.MUSIC_DISC_CAT)
            .build());

    public static JadeItem registerJadeItem(JadeItem item) {
        ITEMS.put(item.getKey(), item);
        return item;
    }

    public static JadeItem getJadeItem(String key) {
        return ITEMS.get(key);
    }
}

package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class VanillaItem extends JadeItem {

    public VanillaItem(VanillaItem.Builder builder) {
        super(builder);
    }

    public static void convert(ItemStack source) {
        if (source == null || source.getPersistentDataContainer().has(JADE_ITEM_KEY)) return;

        String sourceKey = source.getType().getKey().getKey();
        VanillaItem template = VanillaItemRegistry.getVanillaItem(sourceKey);
        if (template == null) return;

        // Set name
        source.setData(DataComponentTypes.ITEM_NAME, Component.text(template.getName(), template.getRarity().getColor()));

        // Set tooltip style
        source.setData(DataComponentTypes.TOOLTIP_STYLE, template.getRarity().getTooltipKey());

        // Apply key data
        source.editPersistentDataContainer(pdc -> {
            pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, template.getKey());
        });

        // Hide vanilla attributes
        ItemUtils.hideAttributes(source);

        // Apply lore
        JadeItem.relore(source, template);
    }

    public static void update(ItemStack source) {
        if (source == null || !source.getPersistentDataContainer().has(JADE_ITEM_KEY)) return;

        String sourceKey = source.getType().getKey().getKey();
        VanillaItem template = VanillaItemRegistry.getVanillaItem(sourceKey);
        if (template == null) return;

        // Re-apply lore
        JadeItem.relore(source, template);
    }

    public static VanillaItem.Builder builder() {
        return new VanillaItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {
        public Builder data(String name, Material material, Rarity rarity) {
            super.data(name, material.getKey().getKey(), rarity, ItemGroup.VANILLA);
            super.item(material);
            return this;
        }

        public Builder lore(List<String> loreLines) {
            super.lore(loreLines);
            return this;
        }

        public Builder tags(ItemTag... tags) {
            super.tags(tags);
            return this;
        }

        @Override
        public VanillaItem build() {
            try {
                mount();
            } catch (IllegalStateException e) {
                Jade.error("Could not finish building VanillaItem " + this.key + " due to: <red>" + e.getMessage());
            }
            return new VanillaItem(this);
        }
    }


}

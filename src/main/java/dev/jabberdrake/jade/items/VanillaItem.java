package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.utils.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

        // Apply key data
        source.editPersistentDataContainer(pdc -> {
            pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, template.getKey());
        });

        // Apply custom name
        source.setData(DataComponentTypes.CUSTOM_NAME, Component.text(template.getName(), template.getRarity().getColor())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        // Apply custom tooltip
        source.setData(DataComponentTypes.TOOLTIP_STYLE, template.getRarity().getTooltipKey());

        // Apply lore
        source.setData(DataComponentTypes.LORE, template.template.getData(DataComponentTypes.LORE));

        // Hide attributes
        ItemUtils.hideAttributes(source);
    }

    public static void update(ItemStack source) {
        if (source == null || !source.getPersistentDataContainer().has(JADE_ITEM_KEY)) return;

        String sourceKey = source.getType().getKey().getKey();
        VanillaItem template = VanillaItemRegistry.getVanillaItem(sourceKey);
        if (template == null) return;

        // Re-apply lore
        JadeItem.setLore(source, template.getLoreLines());
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
            this.loreLines = loreLines;
            return this;
        }

        @Override
        public VanillaItem build() {
            if (this.template == null) return null;

            VanillaItem item = new VanillaItem(this);
            item.setKeyData();
            item.setCustomName();
            item.setTooltipStyle();
            item.hideAttributes();
            item.setLore();

            return item;
        }
    }


}

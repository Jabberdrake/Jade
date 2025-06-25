package dev.jabberdrake.jade.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class VanillaItem extends JadeItem {

    public VanillaItem(VanillaItem.Builder builder) {
        super(builder);
    }

    public static void convert(ItemStack source) {
        Bukkit.broadcast(Component.text("trying to convert " + source.getType().name() + " " + source.getType().getKey().getKey()));
        if (source == null || source.getPersistentDataContainer().has(JADE_ITEM_KEY)) return;

        String sourceKey = source.getType().getKey().getKey();
        JadeItem jadeItem = JadeItemRegistry.getJadeItem(sourceKey);
        if (!(jadeItem instanceof VanillaItem)) return;

        VanillaItem template = (VanillaItem) jadeItem;

        // Apply key data
        source.editPersistentDataContainer(pdc -> {
            pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, template.getKey());
        });

        // Apply custom name
        source.setData(DataComponentTypes.CUSTOM_NAME, Component.text(template.getName(), template.getRarity().getColor())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        // Apply custom tooltip
        source.setData(DataComponentTypes.TOOLTIP_STYLE, template.getRarity().getTooltipKey());
    }

    public static VanillaItem.Builder builder() {
        return new VanillaItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {
        public Builder data(String name, Material material, Rarity rarity) {
            super.data(name, material.getKey().getKey(), rarity, Group.VANILLA);
            super.item(material);
            return this;
        }

        @Override
        public VanillaItem build() {
            setKeyData();
            setCustomName();
            setTooltipStyle();
            return new VanillaItem(this);
        }
    }


}

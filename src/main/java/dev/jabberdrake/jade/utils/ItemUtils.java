package dev.jabberdrake.jade.utils;

import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.JadeItemRegistry;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.checkerframework.checker.units.qual.A;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static NamespacedKey parseKey(String namespacedKeyAsString) {
        String[] parts = namespacedKeyAsString.split(":");
        if (parts.length != 2) return null;

        String namespace = parts[0];
        String key = parts[1];

        if (!namespace.equals("minecraft") && !namespace.equals("jade")) return null;
        return new NamespacedKey(namespace, key);
    }

    public static ItemStack asDisplayItemBase(NamespacedKey itemKey) {
        ItemStack iconItem;
        switch (itemKey.getNamespace()) {
            case "minecraft":
                // Check if the provided key matches a valid Minecraft item
                ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(itemKey);
                if (dummy == null) {
                    return null;
                }

                // Fetch the item material and return a matching itemstack
                iconItem = ItemStack.of(Material.matchMaterial(itemKey.asString()));
                if (iconItem.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
                    iconItem.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                }
                if (iconItem.hasData(DataComponentTypes.FOOD)) {
                    iconItem.unsetData(DataComponentTypes.FOOD);
                }
                return iconItem;
            case "jade":
                iconItem = JadeItemRegistry.getJadeItem(itemKey.getKey()).getItem();
                if (iconItem.hasData(DataComponentTypes.TOOLTIP_STYLE)) {
                    iconItem.unsetData(DataComponentTypes.TOOLTIP_STYLE);
                }
                if (iconItem.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
                    iconItem.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                }
                if (iconItem.hasData(DataComponentTypes.FOOD)) {
                    iconItem.unsetData(DataComponentTypes.FOOD);
                }
                return iconItem;
            default:
                return ItemStack.of(Material.BARRIER);
        }
    }

    public static void hideAttributes(ItemStack item) {
        /*
        List<AttributeUtils.ItemAttributeEntry> entries = new ArrayList<>();

        ItemAttributeModifiers modifiers = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            entries.add(new AttributeUtils.ItemAttributeEntry(entry.attribute(), entry.modifier(), entry.getGroup(), AttributeModifierDisplay.hidden()));
        }

        ItemAttributeModifiers.Builder hiddenAttrBuilder = ItemAttributeModifiers.itemAttributes();
        for (AttributeUtils.ItemAttributeEntry hiddenEntry : entries) {
            hiddenAttrBuilder.addModifier(hiddenEntry.getAttribute(), hiddenEntry.getModifier(), hiddenEntry.getSlotGroup(), hiddenEntry.getDisplay());
        }
        ItemAttributeModifiers hiddenAttrModifiers = hiddenAttrBuilder.build();

        item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, hiddenAttrModifiers);
        */

        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .addHiddenComponents(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .build());
        }


}

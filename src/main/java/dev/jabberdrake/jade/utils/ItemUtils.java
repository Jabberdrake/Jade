package dev.jabberdrake.jade.utils;

import com.mojang.brigadier.Command;
import dev.jabberdrake.jade.Jade;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.Arrays;

public class ItemUtils {

    public static NamespacedKey parseKey(String namespacedKeyAsString) {
        String[] parts = namespacedKeyAsString.split(":");
        if (parts.length != 2) return null;

        String namespace = parts[0];
        String key = parts[1];

        if (!namespace.equals("minecraft") && !namespace.equals("jade")) return null;
        return new NamespacedKey(namespace, key);
    }

    public static ItemStack asDisplayItem(NamespacedKey itemKey) {
        switch (itemKey.getNamespace()) {
            case "minecraft":
                // Check if the provided key matches a valid Minecraft item
                ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(itemKey);
                if (dummy == null) {
                    return null;
                }

                // Fetch the item material and return a matching itemstack
                ItemStack iconItem = ItemStack.of(Material.matchMaterial(itemKey.asString()));
                if (iconItem.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
                    iconItem.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                }
                return iconItem;
            case "jade":
                // TODO: IMPLEMENT ME
                return ItemStack.of(Material.STRUCTURE_VOID);
            default:
                return ItemStack.of(Material.BARRIER);
        }
    }
}

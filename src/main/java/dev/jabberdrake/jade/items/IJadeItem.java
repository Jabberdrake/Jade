package dev.jabberdrake.jade.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface IJadeItem {

    String getName();
    String getKey();
    NamespacedKey getNamespacedKey(boolean includeGroup);
    Rarity getRarity();
    ItemStack getItem();
    ItemStack getItem(int amount);
    ItemGroup getGroup();


}

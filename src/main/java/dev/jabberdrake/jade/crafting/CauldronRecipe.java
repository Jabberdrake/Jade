package dev.jabberdrake.jade.crafting;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class CauldronRecipe {
    private NamespacedKey key;
    private ItemStack result;
    private Material source;

    public CauldronRecipe(NamespacedKey key, ItemStack result, Material source) {
        this.key = key;
        this.result = result;
        this.source = source;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public Material getSource() {
        return this.source;
    }

    public boolean matchSource(ItemStack input) {
        return input.getType().equals(this.getSource());
    }
}

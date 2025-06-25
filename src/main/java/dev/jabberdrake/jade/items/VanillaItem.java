package dev.jabberdrake.jade.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaItem extends JadeItem {

    public VanillaItem(VanillaItem.Builder builder) {
        super(builder);
    }

    public static VanillaItem.Builder builder() {
        return new VanillaItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {
        public Builder data(Material material) {
            this.template = ItemStack.of(material);
            super.data(material.name(), material.getKey().getKey(), resolveVanillaRarity(), Group.VANILLA);
            return this;
        }

        private Rarity resolveVanillaRarity() {
            if (this.template == null) return null;

            // for specific cases, add logic here

            // If we're not dealing with a special case, then we simply
            // translate Minecraft rarities to Jade rarities
            return switch (this.template.getItemMeta().getRarity()) {
                case UNCOMMON -> Rarity.UNCOMMON;
                case RARE, EPIC -> Rarity.RARE;
                default -> Rarity.COMMON;
            };
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

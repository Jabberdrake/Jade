package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import io.papermc.paper.datacomponent.DataComponentTypes;

public class IngredientItem extends JadeItem {

    public IngredientItem(Builder builder) { super(builder); }

    public static IngredientItem.Builder builder() {
        return new IngredientItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {

        public IngredientItem.Builder data(String name, String key, Rarity rarity) {
            super.data(name, key, rarity, ItemGroup.INGREDIENT);

            return this;
        }

        @Override
        public IngredientItem build() {
            try {
                removeComponent(DataComponentTypes.JUKEBOX_PLAYABLE);
                stackSize(64);
                model();
                mount();
            } catch (IllegalStateException e) {
                Jade.error("Could not finish building IngredientItem " + this.key + " due to: <red>" + e.getMessage());
            }
            return new IngredientItem(this);
        }
    }
}

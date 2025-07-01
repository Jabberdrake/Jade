package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;

public class GadgetItem extends JadeItem {

    public GadgetItem(Builder builder) {
        super(builder);
    }

    public static GadgetItem.Builder builder() {
        return new GadgetItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {

        public Builder data(String name, String key, Rarity rarity) {
            super.data(name, key, rarity, ItemGroup.GADGET);

            return this;
        }

        @Override
        public GadgetItem build() {
            try {
                model();
                mount();
            } catch (IllegalStateException e) {
                Jade.error("Could not finish building GadgetItem " + this.key + " due to: <red>" + e.getMessage());
            }
            return new GadgetItem(this);
        }
    }
}

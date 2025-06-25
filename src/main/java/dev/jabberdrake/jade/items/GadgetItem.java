package dev.jabberdrake.jade.items;

public class GadgetItem extends JadeItem {

    public GadgetItem(Builder builder) {
        super(builder);
    }

    public static GadgetItem.Builder builder() {
        return new GadgetItem.Builder();
    }

    public static class Builder extends JadeItem.Builder {

        public Builder data(String name, String key, Rarity rarity) {
            super.data(name, key, rarity, Group.GADGET);

            return this;
        }

        @Override
        public GadgetItem build() {
            setKeyData();
            setCustomName();
            setTooltipStyle();
            setModelData(key);
            return new GadgetItem(this);
        }
    }
}

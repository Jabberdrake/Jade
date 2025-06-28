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
            super.data(name, key, rarity, ItemGroup.GADGET);

            return this;
        }

        @Override
        public GadgetItem build() {
            if (this.template == null) return null;
            GadgetItem item = new GadgetItem(this);

            item.setKeyData();
            item.setCustomName();
            item.setTooltipStyle();
            item.setModelData(this.key);
            item.hideAttributes();
            item.setLore();

            return item;
        }
    }
}

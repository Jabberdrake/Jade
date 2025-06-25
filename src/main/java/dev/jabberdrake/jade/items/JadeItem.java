package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public abstract class JadeItem {
    public static final NamespacedKey JADE_ITEM_KEY = Jade.key("item");

    protected final String name;
    protected final String key;
    protected final Rarity rarity;
    protected final ItemStack template;
    protected final Group group;

    protected enum Group {
        VANILLA,
        GADGET;
    }

    protected JadeItem(String name, String key, Rarity rarity, Material material, Group group) {
        this.name = name;
        this.key = key;
        this.rarity = rarity;
        this.template = ItemStack.of(material);
        this.group = group;
    }

    protected JadeItem(JadeItem.Builder builder) {
        this.name = builder.name;
        this.key = builder.key;
        this.rarity = builder.rarity;
        this.template = builder.template;
        this.group = builder.group;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getNamespacedKey(boolean includeGroup) {
        return "jade:" + (includeGroup ? getItemGroup().name() + "/" : "") + getKey();
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public ItemStack getItem() {
        return this.template.clone();
    }

    public ItemStack getItem(int amount) {
        ItemStack clone = this.getItem();
        clone.setAmount(amount);
        return clone;
    }

    public JadeItem.Group getItemGroup() {
        return this.group;
    }

    public void setCustomName() {
        this.template.setData(DataComponentTypes.CUSTOM_NAME, Component.text(name, rarity.getColor()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    public void setModelData(String path) {
        this.template.setData(DataComponentTypes.ITEM_MODEL, Jade.key(path));
    }

    public void setKeyData() {
        this.template.editPersistentDataContainer(pdc -> {
            pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, key);
        });
    }

    public void setTooltipStyle() {
        this.template.setData(DataComponentTypes.TOOLTIP_STYLE, rarity.getTooltipKey());
    }

    static abstract class Builder {
        protected String name;
        protected String key;
        protected Rarity rarity;
        protected ItemStack template;
        protected Group group;

        protected Builder data(String name, String key, Rarity rarity, Group group) {
            this.name = name;
            this.key = key;
            this.rarity = rarity;
            this.group = group;
            return this;
        }

        public Builder item(Material material) {
            this.template = ItemStack.of(material);
            return this;
        }

        public Builder item(String inputString) {
            try {
                this.template = Jade.getInstance().getServer().getItemFactory().createItemStack(inputString);
            } catch (IllegalArgumentException e) {
                this.template = ItemStack.of(Material.BARRIER);
            }
            return this;
        }

        public void setCustomName() {
            this.template.setData(DataComponentTypes.CUSTOM_NAME, Component.text(name, rarity.getColor()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        public void setModelData(String path) {
            this.template.setData(DataComponentTypes.ITEM_MODEL, Jade.key(path));
        }

        public void setKeyData() {
            this.template.editPersistentDataContainer(pdc -> {
                pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, key);
            });
        }

        public void setTooltipStyle() {
            this.template.setData(DataComponentTypes.TOOLTIP_STYLE, rarity.getTooltipKey());
        }

        abstract JadeItem build();
    }

}

package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.items.decorators.*;
import dev.jabberdrake.jade.utils.AttributeUtils;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class JadeItem {
    public static final NamespacedKey JADE_ITEM_KEY = Jade.key("item");

    protected final String name;
    protected final String key;
    protected final Rarity rarity;
    protected final ItemStack template;
    protected List<String> lore;
    protected final ItemGroup group;
    protected final ItemTag[] tags;

    protected JadeItem(JadeItem.Builder builder) {
        this.name = builder.name;
        this.key = builder.key;
        this.rarity = builder.rarity;
        this.template = builder.template;
        this.lore = builder.lore;
        this.group = builder.group;
        this.tags = builder.tags;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getFullKey() {
        return "jade:" + getItemGroup().name() + "/" + getKey();
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

    public List<String> getLore() {
        return this.lore;
    }

    public ItemGroup getItemGroup() {
        return this.group;
    }

    public ItemTag[] getItemTags() {
        return this.tags;
    }

    public static void rename(ItemStack item, String name, boolean italic) {
        TextColor nameColor = Rarity.COMMON.getColor();
        if (item.hasData(DataComponentTypes.ITEM_NAME)) {
            Component itemName = item.getData(DataComponentTypes.ITEM_NAME);
            if (itemName.style().color() != null) {
                nameColor = itemName.style().color();
            }

        }

        if (italic) {
            item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(name, nameColor));
        } else {
            item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(name, nameColor).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
    }

    public static void relore(ItemStack item, JadeItem template) {
        if (item == null) return;

        ItemTag tag = template.getItemTags() == null ? ItemTag.MATERIAL : template.getItemTags()[0];
        switch (tag) {
            case WEAPON,TOOL -> WeaponDecorator.weaponDecorator().decorate(item, template.getLore(), template.getItemGroup());
            case ARMOR -> ArmorDecorator.armorDecorator().decorate(item, template.getLore(), template.getItemGroup());
            case CONSUMABLE -> ConsumableDecorator.consumableDecorator().decorate(item, template.getLore(), template.getItemGroup());
            default -> MaterialDecorator.materialDecorator().decorate(item, template.getLore(), template.getItemGroup());
        }
    }

    public static class Builder {
        protected String name;
        protected String key;
        protected Rarity rarity;
        protected ItemStack template;
        protected ItemGroup group;
        protected List<String> lore;
        protected ItemTag[] tags;

        public ItemStack getTemplate() {
            return this.template;
        }

        public List<String> getLore() {
            return this.lore;
        }

        public ItemTag[] getItemTags() {
            return this.tags;
        }

        public ItemGroup getItemGroup() { return this.group; }

        protected Builder data(String name, String key, Rarity rarity, ItemGroup group) {
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

        public Builder lore(List<String> loreLines) {
            this.lore = loreLines;
            return this;
        }

        public Builder tags(ItemTag... tags) {
            this.tags = tags;
            return this;
        }

        public Builder model() throws IllegalStateException {
            if (this.template == null) throw new IllegalStateException("Missing itemstack!");

            this.template.setData(DataComponentTypes.ITEM_MODEL, Jade.key(this.key));
            return this;
        }

        public Builder mount() throws IllegalStateException {
            if (this.template == null) throw new IllegalStateException("Missing itemstack!");

            // Set name
            this.template.setData(DataComponentTypes.ITEM_NAME, Component.text(this.name, this.rarity.getColor()));
            //this.template.setData(DataComponentTypes.CUSTOM_NAME, Component.text(this.name, this.rarity.getColor())
            //        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            // Set tooltip style
            this.template.setData(DataComponentTypes.TOOLTIP_STYLE, rarity.getTooltipKey());

            // Add key to PDC
            this.template.editPersistentDataContainer(pdc -> {
                pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, this.key);
            });

            // Add lore
            ItemTag tag = this.tags == null ? ItemTag.MATERIAL : this.tags[0];
            switch (tag) {
                case WEAPON,TOOL -> WeaponDecorator.weaponDecorator().decorate(this);
                case ARMOR -> ArmorDecorator.armorDecorator().decorate(this);
                case CONSUMABLE -> ConsumableDecorator.consumableDecorator().decorate(this);
                default -> MaterialDecorator.materialDecorator().decorate(this);
            }

            // Hide vanilla attribute display
            ItemUtils.hideAttributes(this.template);

            return this;
        }

        public JadeItem build() {
            // This is dogshit
            return null;
        }
    }

}

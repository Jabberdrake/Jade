package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.AttributeUtils;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public abstract class JadeItem {
    public static final NamespacedKey JADE_ITEM_KEY = Jade.key("item");

    protected final String name;
    protected final String key;
    protected final Rarity rarity;
    protected final ItemStack template;
    protected final Group group;
    protected List<String> loreLines;

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
        this.loreLines = builder.loreLines;
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

    public List<String> getLoreLines() {
        return this.loreLines;
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
        protected List<String> loreLines;

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

        public Builder lore(List<String> loreLines) {
            this.loreLines = loreLines;
            return this;
        }

        public void setCustomName() {
            if (this.template == null) return;
            this.template.setData(DataComponentTypes.CUSTOM_NAME, Component.text(name, rarity.getColor()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        public void setModelData(String path) {
            if (this.template == null) return;
            this.template.setData(DataComponentTypes.ITEM_MODEL, Jade.key(path));
        }

        public void setKeyData() {
            if (this.template == null) return;
            this.template.editPersistentDataContainer(pdc -> {
                pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, key);
            });
        }

        public void setTooltipStyle() {
            if (this.template == null) return;
            this.template.setData(DataComponentTypes.TOOLTIP_STYLE, rarity.getTooltipKey());
        }

        public void setLore() {
            if (this.template == null) return;

            ItemLore.Builder lore = ItemLore.lore();

            List<String> baseAttributeLore = new ArrayList<>();
            List<String> idenAttributeLore = new ArrayList<>();

            List<ItemAttributeModifiers.Entry> modifiers = this.template.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
            for (ItemAttributeModifiers.Entry entry : modifiers) {
                Attribute entryAttr = entry.attribute();
                double amount = entry.modifier().getAmount();
                if (entryAttr == Attribute.ATTACK_DAMAGE) {
                    baseAttributeLore.add("<gold>\uD83D\uDDE1 Damage: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_DAMAGE + amount));
                }
                else if (entryAttr == Attribute.ATTACK_SPEED) {
                    baseAttributeLore.add("<copper_red>⌛ Speed: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_SPEED + amount));
                }
                else if (entryAttr == Attribute.ARMOR) {
                    baseAttributeLore.add("<cyan>⛨ Defense: " + TextUtils.DF.format(amount));
                }
                else if (entryAttr == Attribute.ARMOR_TOUGHNESS) {
                    idenAttributeLore.add("<lime>+" + TextUtils.DF.format(amount) + " <zorba>Armor Toughness");
                }
                else if (entryAttr == Attribute.KNOCKBACK_RESISTANCE) {
                    idenAttributeLore.add("<lime>+" + TextUtils.DF.format(amount * 100) + "% <zorba>Knockback Resistance");
                }

            }

            if (!baseAttributeLore.isEmpty()) baseAttributeLore.add("");
            if (this.loreLines != null) idenAttributeLore.add("");

            // Apply attribute lore
            TextUtils.lore(lore, baseAttributeLore);
            TextUtils.lore(lore, idenAttributeLore);

            // Apply actual lore
            if (this.loreLines != null) {
                TextUtils.lore(lore, this.loreLines);
            }

            this.template.setData(DataComponentTypes.LORE, lore.build());
        }

        abstract JadeItem build();
    }

}

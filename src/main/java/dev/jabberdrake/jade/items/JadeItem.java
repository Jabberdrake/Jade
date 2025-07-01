package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.items.decorators.JadeItemDecorator;
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
import org.apache.maven.model.Build;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
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
        if (item.hasData(DataComponentTypes.CUSTOM_NAME)) {
            Component itemName = item.getData(DataComponentTypes.CUSTOM_NAME);
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
    
    public void setLore() {
        JadeItem.setLore(this.template, this.lore);
    }

    public static void setLore(ItemStack item, List<String> flavorText) {
        if (item == null) return;

        ItemLore.Builder lore = ItemLore.lore();

        // TEMP
        if (item.getType().equals(Material.POTION)) {
            TextUtils.lore(lore, JadeItemDecorator.parseEffects(item));
            item.setData(DataComponentTypes.LORE, lore.build());
            return;
        }

        List<String> baseAttributeLore = new LinkedList<>();
        List<String> idenAttributeLore = new LinkedList<>();
        List<Component> enchantmentsLore = new LinkedList<>();

        Map<Enchantment, Integer> enchantments = item.getData(DataComponentTypes.ENCHANTMENTS).enchantments();
        List<ItemAttributeModifiers.Entry> modifiers = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            Attribute entryAttr = entry.attribute();
            double amount = entry.modifier().getAmount();
            if (amount == 0) continue;
            if (entryAttr == Attribute.ATTACK_DAMAGE) {
                StringBuilder damageLoreStr = new StringBuilder("<gold>\uD83D\uDDE1 Damage: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_DAMAGE + amount));

                for (Enchantment enchantment : enchantments.keySet()) {
                    if (enchantment.getKey().getKey().equalsIgnoreCase("sharpness")) {
                        int sharpnessVal = enchantments.get(enchantment);
                        if (sharpnessVal <= 0) break;

                        double sharpnessBuff = 0.5 + (sharpnessVal * 0.5);
                        damageLoreStr.append(" <light_purple>(+ ").append(TextUtils.DF.format(sharpnessBuff)).append(")");
                    }
                }

                baseAttributeLore.add(damageLoreStr.toString());
            }
            else if (entryAttr == Attribute.ATTACK_SPEED) {
                baseAttributeLore.add("<copper_red>⌛ Attack Speed: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_SPEED + amount));
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

        if (!idenAttributeLore.isEmpty()) idenAttributeLore.addFirst("");
        if (!enchantments.isEmpty()) idenAttributeLore.add("");

        for (Enchantment ench : enchantments.keySet()) {
            int magnitude = enchantments.get(ench);
            enchantmentsLore.add(ench.displayName(magnitude).color(NamedTextColor.LIGHT_PURPLE));
        }

        // Apply attribute lore
        TextUtils.lore(lore, baseAttributeLore);
        TextUtils.lore(lore, idenAttributeLore);
        TextUtils.lore(lore, enchantmentsLore, "✧ ", NamedTextColor.LIGHT_PURPLE);

        // Apply actual lore
        if (flavorText != null) {
            lore.addLine(Component.empty());
            TextUtils.lore(lore, flavorText);
        }

        item.setData(DataComponentTypes.LORE, lore.build());
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

        public ItemTag[] getItemTags() {
            return this.tags;
        }

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

        public Builder model() throws IllegalStateException {
            if (this.template == null) throw new IllegalStateException("Missing itemstack!");

            this.template.setData(DataComponentTypes.ITEM_MODEL, Jade.key(this.key));
            return this;
        }

        public Builder mount() throws IllegalStateException {
            if (this.template == null) throw new IllegalStateException("Missing itemstack!");

            // Set name
            this.template.getItemMeta().itemName(Component.text(this.name, this.rarity.getColor()));

            // Set tooltip style
            this.template.setData(DataComponentTypes.TOOLTIP_STYLE, rarity.getTooltipKey());

            // Add key to PDC
            this.template.editPersistentDataContainer(pdc -> {
                pdc.set(JadeItem.JADE_ITEM_KEY, PersistentDataType.STRING, this.key);
            });

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

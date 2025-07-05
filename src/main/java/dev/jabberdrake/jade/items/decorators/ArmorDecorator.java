package dev.jabberdrake.jade.items.decorators;

import dev.jabberdrake.jade.items.ItemGroup;
import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.utils.AttributeUtils;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorDecorator extends JadeItemDecorator {

    private static final ArmorDecorator INSTANCE = new ArmorDecorator();

    public static ArmorDecorator armorDecorator() {
        return INSTANCE;
    }

    @Override
    public void decorate(JadeItem.Builder builder) {
        decorate(builder.getTemplate(), builder.getLore(), builder.getItemGroup());
    }

    @Override
    public void decorate(ItemStack template, List<String> lore, ItemGroup group) {
        List<String> primaryAttrLore = parsePrimaryAttributes(template);
        if (primaryAttrLore == null) throw new IllegalStateException("[ArmorDecorator] Invalid primary attributes in armor item!");

        ItemLore.Builder loreBuilder = ItemLore.lore();
        TextUtils.lore(loreBuilder, primaryAttrLore);

        List<String> secondaryAttrLore = parseSecondaryAttributes(template);
        if (!secondaryAttrLore.isEmpty()) {
            TextUtils.lore(loreBuilder, secondaryAttrLore);
        }

        List<String> enchLore = parseEnchantments(template);
        if (!enchLore.isEmpty()) {
            TextUtils.lore(loreBuilder, enchLore);
        }

        if (group == ItemGroup.VANILLA) {
            List<String> trimLore = parseTrim(template);
            if (!trimLore.isEmpty()) TextUtils.lore(loreBuilder, trimLore);
        }

        applyLore(loreBuilder, lore);
        template.setData(DataComponentTypes.LORE, loreBuilder);
    }

    @Override
    List<String> parsePrimaryAttributes(ItemStack template) {
        if (!template.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
            return null;
        }

        /*
        Map<Enchantment, Integer> enchantments = null;
        if (template.hasData(DataComponentTypes.ENCHANTMENTS)) {
            enchantments = template.getData(DataComponentTypes.ENCHANTMENTS).enchantments();
        }
        */

        String healthLore = "";
        String defenseLore = "";
        List<ItemAttributeModifiers.Entry> modifiers = template.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            Attribute entryAttr = entry.attribute();
            double amount = entry.modifier().getAmount();
            //if (amount == 0) continue;
            if (entryAttr == Attribute.MAX_HEALTH) {
                StringBuilder healthLoreBuilder = new StringBuilder("<dark_red>❤ Health: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_DAMAGE + amount));
                healthLore = healthLoreBuilder.toString();
            }
            else if (entryAttr == Attribute.ARMOR) {
                StringBuilder defenseLoreBuilder = new StringBuilder("<cyan>⛨ Defense: " + TextUtils.DF.format(amount));
                defenseLore = defenseLoreBuilder.toString();
            }
        }

        if (defenseLore.equalsIgnoreCase("")) return null;
        if (healthLore.equalsIgnoreCase("")) {
            return List.of(defenseLore);
        } else {
            return List.of(healthLore, defenseLore);
        }
    }

    @Override
    List<String> parseSecondaryAttributes(ItemStack template) {
        List<String> attrLore = new ArrayList<>();

        // We assume that the item has attribute modifiers to read, since this method is only ever called after parsePrimaryAttributes,
        // which throws an exception if this data component cannot be found.
        List<ItemAttributeModifiers.Entry> modifiers = template.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .modifiers()
                .stream().filter(entry -> entry.modifier().getAmount() > 0.1 && entry.attribute() != Attribute.ARMOR && entry.attribute() != Attribute.MAX_HEALTH)
                .toList();

        if (modifiers.isEmpty()) {
            return attrLore;
        } else attrLore.add("");
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            Attribute entryAttr = entry.attribute();
            double amount = entry.modifier().getAmount();
            if (amount == 0) continue;
            if (entryAttr == Attribute.ATTACK_DAMAGE) {
                attrLore.add("<lime>+" + TextUtils.DF.format(amount) + " <zorba>Damage");
            }
            else if (entryAttr == Attribute.ATTACK_SPEED) {
                attrLore.add("<lime>+" + TextUtils.DF.format(amount) + " <zorba>Attack Speed");
            }
            else if (entryAttr == Attribute.ARMOR_TOUGHNESS) {
                attrLore.add("<lime>+" + TextUtils.DF.format(amount) + " <zorba>Armor Toughness");
            }
            else if (entryAttr == Attribute.KNOCKBACK_RESISTANCE) {
                attrLore.add("<lime>+" + TextUtils.DF.format(amount * 100) + "% <zorba>Knockback Resistance");
            }
        }

        return attrLore;
    }
}

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeaponDecorator extends JadeItemDecorator {

    private static final WeaponDecorator INSTANCE = new WeaponDecorator();

    public static WeaponDecorator weaponDecorator() {
        return INSTANCE;
    }

    @Override
    public void decorate(JadeItem.Builder builder) {
        decorate(builder.getTemplate(), builder.getLore(), builder.getItemGroup());
    }

    @Override
    public void decorate(ItemStack template, List<String> lore, ItemGroup itemGroup) {
        ItemLore.Builder loreBuilder = ItemLore.lore();

        List<String> primaryAttrLore = parsePrimaryAttributes(template);
        if (primaryAttrLore != null && !primaryAttrLore.isEmpty()) {
            TextUtils.lore(loreBuilder, primaryAttrLore);
        }
        List<String> secondaryAttrLore = parseSecondaryAttributes(template);
        if (!secondaryAttrLore.isEmpty()) {
            TextUtils.lore(loreBuilder, secondaryAttrLore);
        }

        List<String> enchLore = parseEnchantments(template);
        if (!enchLore.isEmpty()) {
            TextUtils.lore(loreBuilder, enchLore);
        }

        applyLore(loreBuilder, lore);
        template.setData(DataComponentTypes.LORE, loreBuilder);
    }

    @Override
    List<String> parsePrimaryAttributes(ItemStack template) {
        if (!template.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
            return null;
        }

        Map<Enchantment, Integer> enchantments = null;
        if (template.hasData(DataComponentTypes.ENCHANTMENTS)) {
            enchantments = template.getData(DataComponentTypes.ENCHANTMENTS).enchantments();
        }

        String damageLore = "";
        String atkSpeedLore = "";
        List<ItemAttributeModifiers.Entry> modifiers = template.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            Attribute entryAttr = entry.attribute();
            double amount = entry.modifier().getAmount();
            //if (amount == 0) continue;
            if (entryAttr == Attribute.ATTACK_DAMAGE) {
                StringBuilder damageLoreBuilder = new StringBuilder("<gold>\uD83D\uDDE1 Damage: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_DAMAGE + amount));

                if (enchantments != null) {
                    for (Enchantment enchantment : enchantments.keySet()) {
                        if (enchantment.getKey().getKey().equalsIgnoreCase("sharpness")) {
                            int sharpnessVal = enchantments.get(enchantment);
                            if (sharpnessVal <= 0) break;

                            double sharpnessBuff = 0.5 + (sharpnessVal * 0.5);
                            damageLoreBuilder.append(" <light_purple>(+").append(TextUtils.DF.format(sharpnessBuff)).append(")");
                        }
                    }
                }
                damageLore = damageLoreBuilder.toString();
            }
            else if (entryAttr == Attribute.ATTACK_SPEED) {
                StringBuilder speedLoreBuilder = new StringBuilder("<copper>⌛ Attack Speed: " + TextUtils.DF.format(AttributeUtils.BASE_ATTACK_SPEED + amount));
                atkSpeedLore = speedLoreBuilder.toString();
            }
        }

        if (damageLore.equalsIgnoreCase("") || atkSpeedLore.equalsIgnoreCase("")) return null;
        return List.of(damageLore, atkSpeedLore);
    }

    @Override
    List<String> parseSecondaryAttributes(ItemStack template) {
        List<String> attrLore = new ArrayList<>();

        // We assume that the item has attribute modifiers to read, since this method is only ever called after parsePrimaryAttributes,
        // which throws an exception if this data component cannot be found.
        List<ItemAttributeModifiers.Entry> modifiers = template.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .modifiers()
                .stream().filter(entry -> entry.modifier().getAmount() > 0 && entry.attribute() != Attribute.ATTACK_DAMAGE && entry.attribute() != Attribute.ATTACK_SPEED)
                .toList();
        if (modifiers.isEmpty()) {
            return attrLore;
        } else attrLore.add("");
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            Attribute entryAttr = entry.attribute();
            double amount = entry.modifier().getAmount();
            if (entryAttr == Attribute.ARMOR) {
                attrLore.add("<lime>+" + TextUtils.DF.format(amount) + " <zorba>Defense");
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

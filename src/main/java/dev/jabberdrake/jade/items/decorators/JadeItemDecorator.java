package dev.jabberdrake.jade.items.decorators;

import dev.jabberdrake.jade.items.ItemGroup;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import dev.jabberdrake.jade.items.JadeItem;
import io.papermc.paper.datacomponent.item.PotionContents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class JadeItemDecorator {

    abstract void decorate(JadeItem.Builder builder);
    abstract void decorate(ItemStack template, List<String> lore, ItemGroup group);

    abstract List<String> parsePrimaryAttributes(ItemStack template);
    abstract List<String> parseSecondaryAttributes(ItemStack template);

    public List<String> parseEnchantments(ItemStack template) {
        List<String> enchantmentLore = new ArrayList<>();
        if (!template.hasData(DataComponentTypes.ENCHANTMENTS)) return enchantmentLore;

        Map<Enchantment, Integer> enchantments = template.getData(DataComponentTypes.ENCHANTMENTS).enchantments();
        if (!enchantments.isEmpty()) {
            enchantmentLore.add("");
        }

        for (Enchantment ench : enchantments.keySet()) {
            int magnitude = enchantments.get(ench);
            String enchStr = MiniMessage.miniMessage().serialize(ench.displayName(magnitude));
            enchantmentLore.add("<light_purple>♢ " + enchStr.replace("<gray>", ""));
        }

        return enchantmentLore;
    }

    public static List<String> parseEffects(ItemStack template) {
        List<String> effectLore = new ArrayList<>();

        if (template.hasData(DataComponentTypes.POTION_CONTENTS)) {
            effectLore.add("<dark_amethyst>Effects:");
        } else {
            return null;
        }

        PotionContents pContents = template.getData(DataComponentTypes.POTION_CONTENTS);
        PotionType pType = pContents.potion();
        if (pType == PotionType.AWKWARD || pType == PotionType.MUNDANE || pType == PotionType.THICK || pType == PotionType.WATER) {
            return List.of("<zorba>No effects");
        }

        for (PotionEffect pEffect : pContents.allEffects()) {
            StringBuilder effectStr = new StringBuilder("<dark_amethyst>— ");
            effectStr.append(pEffect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL) ? "<red>" : "<light_amethyst>");
            effectStr.append(MiniMessage.miniMessage().serialize(Component.translatable(pEffect.getType())));
            effectStr.append(" " + TextUtils.toRomanNumeral(1 + pEffect.getAmplifier()));
            int durationAsSeconds = pEffect.getDuration() / 20;
            if (durationAsSeconds > 0) effectStr.append(" <zorba>(" + String.format("%02d:%02d", durationAsSeconds / 60, durationAsSeconds % 60) + ")");
            effectLore.add(effectStr.toString());
        }

        return effectLore;
    }

    public static List<String> parseTrim(ItemStack template) {
        List<String> trimLore = new ArrayList<>();
        if (!template.hasData(DataComponentTypes.TRIM)) return trimLore;

        trimLore.add("");
        ArmorTrim trim = template.getData(DataComponentTypes.TRIM).armorTrim();
        trimLore.add(TextUtils.getTrimAsString(trim));
        return trimLore;
    }

    public static void applyLore(ItemLore.Builder loreBuilder, List<String> lore) {
        if (lore == null || lore.isEmpty()) return;

        loreBuilder.addLine(Component.empty());
        for (String loreLine : lore) {
            loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>" + loreLine)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
    }
}

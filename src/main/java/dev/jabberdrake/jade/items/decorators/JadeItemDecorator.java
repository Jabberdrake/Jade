package dev.jabberdrake.jade.items.decorators;

import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import dev.jabberdrake.jade.items.JadeItem;
import io.papermc.paper.datacomponent.item.PotionContents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class JadeItemDecorator {

    abstract void decorate(JadeItem.Builder builder);

    abstract List<String> parsePrimaryAttributes(ItemLore.Builder loreBuilder, JadeItem.Builder itemBuilder);
    abstract List<String> parseSecondaryAttributes(ItemLore.Builder loreBuilder, JadeItem.Builder itemBuilder);

    public List<String> parseEnchantments(ItemLore.Builder loreBuilder, JadeItem.Builder itemBuilder) {
        List<String> enchantmentLore = new ArrayList<>();

        Map<Enchantment, Integer> enchantments = itemBuilder.getTemplate().getData(DataComponentTypes.ENCHANTMENTS).enchantments();
        for (Enchantment ench : enchantments.keySet()) {
            int magnitude = enchantments.get(ench);
            String enchStr = MiniMessage.miniMessage().serialize(ench.displayName(magnitude));
            enchantmentLore.add("<light_purple>✧ " + enchStr);
        }

        return enchantmentLore;
    }

    public static List<String> parseEffects(ItemStack template) {//JadeItem.Builder itemBuilder) {
        List<String> effectLore = new ArrayList<>();



        if (template.hasData(DataComponentTypes.POTION_CONTENTS)) {
            effectLore.add("<dark_amethyst>Effects:");
        } else {
            return null;
        }

        PotionContents pContents = template.getData(DataComponentTypes.POTION_CONTENTS);
        for (PotionEffect pEffect : pContents.allEffects()) {
            StringBuilder effectStr = new StringBuilder("<dark_amethyst>— ");

            effectStr.append(pEffect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL) ? "<red>" : "<light_amethyst>");
            effectStr.append(MiniMessage.miniMessage().serialize(Component.translatable(pEffect.getType())));
            effectStr.append(" " + TextUtils.toRomanNumeral(1 + pEffect.getAmplifier()));
            int durationAsSeconds = pEffect.getDuration() / 20;
            effectStr.append(" <zorba>(" + String.format("%02d:%02d", durationAsSeconds / 60, durationAsSeconds % 60) + ")");

            effectLore.add(effectStr.toString());
        }

        return effectLore;
    }
}

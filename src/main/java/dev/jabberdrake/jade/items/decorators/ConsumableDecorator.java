package dev.jabberdrake.jade.items.decorators;

import dev.jabberdrake.jade.items.ItemGroup;
import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConsumableDecorator extends JadeItemDecorator {

    private static final ConsumableDecorator INSTANCE = new ConsumableDecorator();

    public static ConsumableDecorator consumableDecorator() {
        return INSTANCE;
    }

    @Override
    public void decorate(JadeItem.Builder builder) {
        decorate(builder.getTemplate(), builder.getLore(), builder.getItemGroup());
    }

    @Override
    public void decorate(ItemStack template, List<String> lore, ItemGroup group) {
        ItemLore.Builder loreBuilder = ItemLore.lore();

        List<String> effectLore = parseEffects(template);
        if (effectLore != null && !effectLore.isEmpty()) {
            TextUtils.lore(loreBuilder, effectLore);
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
        throw new UnsupportedOperationException("Consumables do not have any primary attributes!");
    }

    @Override
    List<String> parseSecondaryAttributes(ItemStack template) {
        throw new UnsupportedOperationException("Consumables do not have any secondary attributes!");
    }
}

package dev.jabberdrake.jade.items.decorators;

import dev.jabberdrake.jade.items.ItemGroup;
import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MaterialDecorator extends JadeItemDecorator {

    private static final MaterialDecorator INSTANCE = new MaterialDecorator();

    public static MaterialDecorator materialDecorator() {
        return INSTANCE;
    }

    @Override
    public void decorate(JadeItem.Builder builder) {
        decorate(builder.getTemplate(), builder.getLore(), builder.getItemGroup());
    }

    @Override
    public void decorate(ItemStack template, List<String> lore, ItemGroup itemGroup) {
        ItemLore.Builder loreBuilder = ItemLore.lore();
        List<String> enchLore = parseEnchantments(template);
        if (!enchLore.isEmpty()) {
            TextUtils.lore(loreBuilder, enchLore);
        }

        applyLore(loreBuilder, lore);
        template.setData(DataComponentTypes.LORE, loreBuilder);
    }

    @Override
    List<String> parsePrimaryAttributes(ItemStack template) {
        throw new UnsupportedOperationException("Materials do not have any primary attributes!");
    }

    @Override
    List<String> parseSecondaryAttributes(ItemStack template) {
        throw new UnsupportedOperationException("Materials do not have any secondary attributes!");
    }
}

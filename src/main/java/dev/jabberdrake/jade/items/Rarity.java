package dev.jabberdrake.jade.items;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;

public enum Rarity {

    COMMON("common", NamedTextColor.WHITE),
    UNCOMMON("uncommon", NamedTextColor.GREEN),
    RARE("rare", NamedTextColor.DARK_PURPLE),
    LEGENDARY("legendary", NamedTextColor.GOLD),
    MYTHIC("mythic", JadeTextColor.MYTHIC_BLUE);

    private String name;
    private TextColor color;
    private NamespacedKey tooltipKey;

    Rarity(String name, TextColor color) {
        this.name = name;
        this.color = color;
        this.tooltipKey = Jade.key(name);
    }

    public String getName() {
        return this.name;
    }

    public TextColor getColor() {
        return this.color;
    }

    public NamespacedKey getTooltipKey() {
        return this.tooltipKey;
    }


}

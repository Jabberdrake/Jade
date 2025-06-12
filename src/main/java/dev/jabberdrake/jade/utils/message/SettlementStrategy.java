package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class SettlementStrategy implements JadeMessageStrategy {
    private Settlement settlement;

    public SettlementStrategy(Settlement settlement) {
        this.settlement = settlement;
    }

    @Override
    public TextColor normalColor() {
        return JadeTextColor.DARK_MANA;
    }

    @Override
    public TextColor highlightColor() {
        return JadeTextColor.LIGHT_MANA;
    }

    @Override
    public Component prefix() {
        return Component.text()
                .content("(").color(highlightColor())
                .append(settlement.getDisplayName())
                .append(Component.text(") [⚐] > "))
                .build();
    }
}

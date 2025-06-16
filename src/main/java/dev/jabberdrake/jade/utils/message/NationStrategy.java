package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class NationStrategy implements JadeMessageStrategy {
    private Nation nation;

    public NationStrategy(Nation nation) {
        this.nation = nation;
    }

    @Override
    public TextColor normalColor() {
        return JadeTextColor.DARK_BRASS;
    }

    @Override
    public TextColor highlightColor() {
        return JadeTextColor.LIGHT_BRASS;
    }

    @Override
    public Component prefix() {
        return Component.text()
                .content("(").color(highlightColor())
                .append(nation.getDisplayNameAsComponent())
                .append(Component.text(") [⁂] > "))
                .build();
    }
}

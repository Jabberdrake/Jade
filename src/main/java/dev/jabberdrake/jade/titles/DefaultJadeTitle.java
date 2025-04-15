package dev.jabberdrake.jade.titles;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class DefaultJadeTitle extends JadeTitle {

    private static final Component PEASANT_COMPONENT = Component.text("Peasant", TextUtils.DARK_ZORBA);

    public static final DefaultJadeTitle PEASANT = new DefaultJadeTitle("Peasant", JadeTitle.serializeTitle(PEASANT_COMPONENT));

    private DefaultJadeTitle(final String name, final String title) {
        super(1, name, title, null, NamedTextColor.WHITE, "minecraft:wheat");
        this.makeUniversal();
    }
}

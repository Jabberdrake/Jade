package dev.jabberdrake.jade.titles;

import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;

public class DefaultJadeTitle extends JadeTitle {

    private static final Component PEASANT_COMPONENT = Component.text("Peasant", JadeTextColor.LIGHT_BRASS);

    public static final DefaultJadeTitle PEASANT = new DefaultJadeTitle("Peasant", JadeTitle.serializeDisplay(PEASANT_COMPONENT));

    private DefaultJadeTitle(final String name, final String title) {
        super(1, name, title, null, NamedTextColor.WHITE, NamespacedKey.minecraft("wheat"));
    }
}

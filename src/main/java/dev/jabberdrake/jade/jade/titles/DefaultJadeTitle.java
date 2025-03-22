package dev.jabberdrake.jade.jade.titles;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class DefaultJadeTitle extends JadeTitle {

    private static final Component PEASANT_COMPONENT = Component.text("Peasant", TextUtils.DARK_ZORBA);

    public static final DefaultJadeTitle PEASANT = new DefaultJadeTitle("Peasant", JadeTitle.serializeTitle(PEASANT_COMPONENT));

    private DefaultJadeTitle(final String name, final String title) {
        super(name, title, UUID.fromString("0a9f50b0-b8f2-4044-baa2-0bfa185115ca"));
        this.makeUniversal();
    }
}

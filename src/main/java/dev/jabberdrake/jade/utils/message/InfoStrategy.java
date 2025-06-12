package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class InfoStrategy implements JadeMessageStrategy {
    @Override
    public TextColor normalColor() {
        return JadeTextColor.ZORBA;
    }

    @Override
    public TextColor highlightColor() {
        return JadeTextColor.LIGHT_ZORBA;
    }

    @Override
    public Component prefix() {
        return Component.text("[!] > ", highlightColor());
    }
}

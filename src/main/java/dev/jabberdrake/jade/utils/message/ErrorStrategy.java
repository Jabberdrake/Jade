package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ErrorStrategy implements JadeMessageStrategy {
    @Override
    public TextColor normalColor() {
        return JadeTextColor.RED;
    }

    @Override
    public TextColor highlightColor() {
        return JadeTextColor.CARMINE;
    }

    @Override
    public Component prefix() {
        return Component.text("[⚡] > ", highlightColor());
    }
}

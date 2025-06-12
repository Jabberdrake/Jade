package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class SuccessStrategy implements JadeMessageStrategy {
    @Override
    public TextColor normalColor() {
        return JadeTextColor.DARK_LAUREL;
    }

    @Override
    public TextColor highlightColor() {
        return JadeTextColor.LIGHT_LAUREL;
    }

    @Override
    public Component prefix() {
        return Component.text("[❊] > ", highlightColor());
    }
}

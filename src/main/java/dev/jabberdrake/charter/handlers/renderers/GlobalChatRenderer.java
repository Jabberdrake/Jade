package dev.jabberdrake.charter.handlers.renderers;

import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class GlobalChatRenderer implements ChatRenderer {
    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience audience) {
        return Component.text()
                .append(Component.text("Developer").color(TextUtils.LIGHT_MANA).decorate(TextDecoration.BOLD))
                .append(Component.text(" "))
                .append(sourceDisplayName.color(TextUtils.LIGHT_MANA).append(Component.text(": ")))
                .append(message.color(TextUtils.DARK_MANA))
                .build();
    }
}

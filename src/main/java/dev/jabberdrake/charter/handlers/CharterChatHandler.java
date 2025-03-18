package dev.jabberdrake.charter.handlers;

import dev.jabberdrake.charter.players.CharterProfile;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CharterChatHandler implements Listener, ChatRenderer {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this);
    }

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

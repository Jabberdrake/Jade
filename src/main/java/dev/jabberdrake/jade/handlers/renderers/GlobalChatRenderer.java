package dev.jabberdrake.jade.handlers.renderers;

import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class GlobalChatRenderer implements ChatRenderer {
    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience audience) {
        JadeTitle titleInUse = PlayerManager.asJadePlayer(source.getUniqueId()).getTitleInUse();
        return Component.text()
                .append(titleInUse.getDisplayAsComponent())
                .append(Component.text(" "))
                .append(sourceDisplayName.color(titleInUse.getSenderColor()).append(Component.text(": ")))
                .append(message.color(NamedTextColor.GRAY))
                .build();
    }
}

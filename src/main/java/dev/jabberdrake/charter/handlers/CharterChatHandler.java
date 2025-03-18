package dev.jabberdrake.charter.handlers;

import dev.jabberdrake.charter.handlers.renderers.GlobalChatRenderer;
import dev.jabberdrake.charter.handlers.renderers.RoleplayChatRenderer;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CharterChatHandler implements Listener{

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (PlayerManager.parsePlayer(player.getUniqueId()).isInRoleplay()) {
            event.renderer(new RoleplayChatRenderer());
        } else {
            event.renderer(new GlobalChatRenderer());
        }
    }
}

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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CharterChatHandler implements Listener{

    private static final int MAX_DISTANCE_SQUARED = 250;

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        String originalMessage = MiniMessage.miniMessage().serialize(event.originalMessage());
        if (handleChannelSwitching(sender, originalMessage)) {
            event.setCancelled(true);
            return;
        }

        if (PlayerManager.parsePlayer(sender.getUniqueId()).isInRoleplay()) {
            // Roleplay channel acts as a 'local' chat channel of sorts, in the sense that only
            // players within a set distance of you receive your chat messages.
            //
            // Unless I've been looking in the wrong place, you can only really restrict who
            // sees what in the listener, so here goes.
            event.viewers().removeIf(viewer ->
                    viewer instanceof Player &&
                    ((Player) viewer).getWorld().equals(sender.getWorld()) &&
                    ((Player) viewer).getLocation().distanceSquared(sender.getLocation())
                            > CharterChatHandler.MAX_DISTANCE_SQUARED
            );
            event.renderer(new RoleplayChatRenderer());
        } else {
            event.renderer(new GlobalChatRenderer());
        }
    }

    public boolean handleChannelSwitching(Player sender, String originalMessage) {
        if (originalMessage.startsWith("g:")) {

            // Replace this with proper chat channel logic if it ever gets implemented
            if (!PlayerManager.parsePlayer(sender.getUniqueId()).isInRoleplay()) {
                sender.sendMessage(TextUtils.composePlainErrorMessage("You are already speaking in this channel!"));

                return true;
            }
            PlayerManager.parsePlayer(sender.getUniqueId()).toggleRoleplay();


            sender.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You are now speaking in the "))
                    .append(TextUtils.composeSuccessHighlight("Global").decorate(TextDecoration.BOLD))
                    .append(TextUtils.composeSuccessText(" channel!"))
            );
            return true;
        } else if (originalMessage.startsWith("rp:")) {
            // Replace this with proper chat channel logic if it ever gets implemented
            if (PlayerManager.parsePlayer(sender.getUniqueId()).isInRoleplay()) {
                sender.sendMessage(TextUtils.composePlainErrorMessage("You are already speaking in this channel!"));
                return true;
            }
            PlayerManager.parsePlayer(sender.getUniqueId()).toggleRoleplay();

            sender.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You are now speaking in the "))
                    .append(Component.text("Roleplay", TextUtils.ROSEMETAL).decorate(TextDecoration.BOLD))
                    .append(TextUtils.composeSuccessText(" channel!"))
            );

            return true;
        }

        return false;
    }


}

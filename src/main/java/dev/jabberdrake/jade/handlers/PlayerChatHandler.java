package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.handlers.renderers.GlobalChatRenderer;
import dev.jabberdrake.jade.handlers.renderers.RoleplayChatRenderer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatHandler implements Listener{

    private static final int MAX_DISTANCE_SQUARED = 250;

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        String originalMessage = MiniMessage.miniMessage().serialize(event.originalMessage());
        if (handleChannelSwitching(sender, originalMessage)) {
            event.setCancelled(true);
            return;
        }

        if (PlayerManager.asJadePlayer(sender.getUniqueId()).isInRoleplay()) {
            // Roleplay channel acts as a 'local' chat channel of sorts, in the sense that only
            // players within a set distance of you receive your chat messages.
            //
            // Unless I've been looking in the wrong place, you can only really restrict who
            // sees what in the listener, so here goes.
            event.viewers().removeIf(viewer ->
                    viewer instanceof Player &&
                    ((Player) viewer).getWorld().equals(sender.getWorld()) &&
                    ((Player) viewer).getLocation().distanceSquared(sender.getLocation())
                            > PlayerChatHandler.MAX_DISTANCE_SQUARED
            );
            event.renderer(new RoleplayChatRenderer());
        } else {
            event.renderer(new GlobalChatRenderer());
        }
    }

    public boolean handleChannelSwitching(Player sender, String originalMessage) {
        if (originalMessage.startsWith("g:")) {

            // Replace this with proper chat channel logic if it ever gets implemented
            if (!PlayerManager.asJadePlayer(sender.getUniqueId()).isInRoleplay()) {
                sender.sendMessage(TextUtils.composePlainErrorMessage("You are already speaking in this channel!"));

                return true;
            }
            PlayerManager.asJadePlayer(sender.getUniqueId()).toggleRoleplay();


            sender.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You are now speaking in the "))
                    .append(TextUtils.composeSuccessHighlight("Global").decorate(TextDecoration.BOLD))
                    .append(TextUtils.composeSuccessText(" channel!"))
            );
            return true;
        } else if (originalMessage.startsWith("rp:")) {
            // Replace this with proper chat channel logic if it ever gets implemented
            if (PlayerManager.asJadePlayer(sender.getUniqueId()).isInRoleplay()) {
                sender.sendMessage(TextUtils.composePlainErrorMessage("You are already speaking in this channel!"));
                return true;
            }
            PlayerManager.asJadePlayer(sender.getUniqueId()).toggleRoleplay();

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

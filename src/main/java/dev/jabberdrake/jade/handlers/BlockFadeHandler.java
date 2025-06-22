package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

public class BlockFadeHandler implements Listener {

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        // Prevents living coral from fading into dead coral if the relevant gamerule is enabled.
        if (JadeConfig.preventCoralFading && event.getBlock().getType().toString().contains("CORAL")) {
            event.setCancelled(true);
        }
    }
}

package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class MobExplodeHandler implements Listener {

    @EventHandler
    public void onMobExplode(EntityExplodeEvent event) {
        if (!JadeConfig.doMobExplosion) {
            event.blockList().clear();
        }
    }
}

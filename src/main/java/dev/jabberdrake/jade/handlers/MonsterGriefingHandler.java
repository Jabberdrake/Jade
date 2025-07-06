package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class MonsterGriefingHandler implements Listener {

    @EventHandler
    public void onMobExplode(EntityExplodeEvent event) {
        if (!JadeConfig.doMonsterGriefing) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onEndermanBreak(EntityChangeBlockEvent event) {
        if (!JadeConfig.doMonsterGriefing && event.getEntity().getType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onZombieBreakDoor(EntityBreakDoorEvent event) {
        if (!JadeConfig.doMonsterGriefing) {
            event.setCancelled(true);
        }
    }
}

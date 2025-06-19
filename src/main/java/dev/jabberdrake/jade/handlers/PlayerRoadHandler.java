package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.utils.Road;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class PlayerRoadHandler implements Listener {
    private static final NamespacedKey ROAD_MODIFIER_KEY = new NamespacedKey("jade", "road_speed_modifier");
    private static final AttributeModifier.Operation ROAD_MODIFIER_OP = AttributeModifier.Operation.ADD_SCALAR;
    private static final double ROAD_MODIFIER_TRANSITION = 0.25D;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        if (!JadeSettings.enableSpeedRoads) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (!hasChangedBlockPosition(from, to)) return; // hasChangedPosition() is too precise, and is triggered way too often

        Player player = event.getPlayer();
        if (player.isFlying() || player.isSwimming() || player.isJumping() || player.isDead() || player.getFallDistance() > 1) return;



        Block top = to.getBlock().getType() == Material.DIRT_PATH ? to.getBlock() : to.getBlock().getRelative(0, -1, 0);
        Block mid = top.getRelative(0, -1, 0);
        Block bot = mid.getRelative(0, -1, 0);

        double speedModifier = 0;
        for (Road road : JadeSettings.roads) {
            if (road.match(top.getType(), mid.getType(), bot.getType())) {
                speedModifier = road.speedModifier();
            }
        }

        AttributeInstance movementSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        double targetSpeed = movementSpeed.getBaseValue() * speedModifier;
        double currentSpeed = movementSpeed.getValue();
        if (currentSpeed == targetSpeed || currentSpeed == movementSpeed.getBaseValue()) return;

        movementSpeed.removeModifier(ROAD_MODIFIER_KEY);

        double finalSpeedModifier;
        // Gradually transition from current speed to target speed
        if (targetSpeed >= currentSpeed) {
            // Speed up until we've hit the target speed
            finalSpeedModifier = Math.min(currentSpeed + ROAD_MODIFIER_TRANSITION, targetSpeed) - movementSpeed.getBaseValue();
        } else {
            // Slow down until we get back to base speed
            finalSpeedModifier = Math.max(currentSpeed - ROAD_MODIFIER_TRANSITION, movementSpeed.getBaseValue()) - movementSpeed.getBaseValue();
        }
        movementSpeed.addModifier(new AttributeModifier(ROAD_MODIFIER_KEY, finalSpeedModifier, ROAD_MODIFIER_OP));
    }

    public boolean hasChangedBlockPosition(Location from, Location to) {
        return !(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ());
    }
}

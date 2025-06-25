package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.crafting.CauldronRecipe;
import dev.jabberdrake.jade.crafting.recipes.CauldronRecipes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CauldronCraftHandler implements Listener {

    private static final int MAXIMUM_DETECT_ITERATIONS = 10;
    private static final int MAXIMUM_CRAFT_ITERATIONS = 3;
    private static final int CRAFT_ITERATION_INTERVAL = 30;

    private static final Map<UUID, BukkitTask> ongoingCraftTasks = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        Item itemEntity = event.getItemDrop();
        doCauldronCrafting(itemEntity);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemMerge(ItemMergeEvent event) {
        Item aggregate = event.getTarget();
        Item piece = event.getEntity();

        if (stopCauldronCrafting(aggregate) || stopCauldronCrafting(piece)) {
            if (aggregate.getThrower() == null) {
                aggregate.setThrower(piece.getThrower());
            }

            doCauldronCrafting(aggregate);
        }
    }

    public boolean stopCauldronCrafting(Item source) {
        BukkitTask stoppedTask = ongoingCraftTasks.remove(source.getUniqueId());
        if (stoppedTask != null) {
            stoppedTask.cancel();
            return true;
        } else return false;
    }

    public void doCauldronCrafting(Item source) {
        final CauldronRecipe[] recipe = {null};
        final int[] detectIterations = { 0 };
        final int[] craftIterations = { 0 };

        Bukkit.getScheduler().runTaskTimer(Jade.getInstance(), task -> {
            ongoingCraftTasks.put(source.getUniqueId(), task);

            // If the item has been picked up, wiped, etc..., stop the process
            if (!source.isValid()) {
                ongoingCraftTasks.remove(source.getUniqueId());
                task.cancel();
                return;
            }

            // Detect whether the dropped item is actually in a cauldron with water
            // If it isn't, stop the process
            Block cauldron = source.getLocation().getBlock();
            if (cauldron.getType() != Material.WATER_CAULDRON) {
                detectIterations[0] = detectIterations[0] + 1;

                if (detectIterations[0] >= MAXIMUM_DETECT_ITERATIONS) {
                    ongoingCraftTasks.remove(source.getUniqueId());
                    task.cancel();
                }

                return;
            }

            // Find the recipe to use by matching the thrown itemstack material with every registered CauldronRecipe
            // If no matching recipe is found, stop the process
            if (recipe[0] == null) {
                for (CauldronRecipe potentialRecipe : CauldronRecipes.getAllRecipes().values()) {
                    if (potentialRecipe.matchSource(source.getItemStack())) {
                        recipe[0] = potentialRecipe;
                        break;
                    }
                }

                if (recipe[0] == null) {
                    ongoingCraftTasks.remove(source.getUniqueId());
                    task.cancel();
                    return;
                }
            }

            // If we've reached this point, we can freely assume that:
            //  - the item still exists;
            //  - the item is in a cauldron with water;
            //  - the item matches a registered CauldronRecipe;
            // On every "craft iteration", play a sound and spawn some particles
            // Then, check whether we've done enough iterations and, if we have, spit out the result item
            source.setPickupDelay(CRAFT_ITERATION_INTERVAL);
            cauldron.getWorld().playSound(cauldron.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 0.75f, 1f);

            double waterHeight = 0.9 - (0.1875 * (3 - ((Levelled) cauldron.getBlockData()).getLevel()));
            cauldron.getWorld().spawnParticle(Particle.SPLASH, cauldron.getLocation().getBlockX() + 0.5, cauldron.getLocation().getBlockY() + waterHeight, cauldron.getLocation().getBlockZ() + 0.5, 8, 0.15, 0.05, 0.15);

            if (craftIterations[0] >= MAXIMUM_CRAFT_ITERATIONS) {
                cauldron.getWorld().playSound(cauldron.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.65f, 1.25f);
                cauldron.getWorld().spawnParticle(Particle.POOF, cauldron.getLocation().getBlockX() + 0.5, cauldron.getLocation().getBlockY() + 1.0, cauldron.getLocation().getBlockZ() + 0.5, 3, 0.1, 0.0, 0.1, 0.03);

                ItemStack stack = source.getItemStack();
                stack = stack.withType(recipe[0].getResult().getType());
                source.setItemStack(stack);

                source.setVelocity(new Vector(0, 0.3, 0));
                source.setPickupDelay(10);

                if (!(cauldron.getBlockData() instanceof Levelled levelled)) {
                    ongoingCraftTasks.remove(source.getUniqueId());
                    task.cancel();
                    return;
                }

                int level = levelled.getLevel() - 1;
                if (level <= 0) {
                    cauldron.setType(Material.CAULDRON);
                } else {
                    levelled.setLevel(level);
                    cauldron.setBlockData(levelled);
                }

                ongoingCraftTasks.remove(source.getUniqueId());
                task.cancel();
            } else {
                craftIterations[0] = craftIterations[0] + 1;
            }
        }, CRAFT_ITERATION_INTERVAL, CRAFT_ITERATION_INTERVAL);
    }
}

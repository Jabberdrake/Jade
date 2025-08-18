package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.events.BuilderWandEvent;
import dev.jabberdrake.jade.items.JadeItem;
import dev.jabberdrake.jade.items.JadeItemRegistry;
import dev.jabberdrake.jade.utils.TextUtils;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.util.Vector;

import java.util.*;

public class GadgetItemHandler implements Listener {

    public static final Map<UUID, Pair<Location, String>> WAND_MAP = new HashMap<>();

    @EventHandler
    public void onFrameBreak(HangingBreakEvent event) {
        Hanging hangingEntity = event.getEntity();
        if (!(hangingEntity instanceof ItemFrame frame)) return;

        if (!frame.isVisible()) {
            event.setCancelled(true);
            World world = Jade.getInstance().getServer().getWorld(frame.getWorld().getUID());
            if (world == null) return;

            ItemStack heldItem = frame.getItem();
            if (heldItem.getType() != Material.AIR) {
                Item heldItemEntity = world.dropItem(frame.getLocation(), heldItem);
                heldItemEntity.setVelocity(new Vector(0, 0.4, 0));
                world.playSound(frame.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 0.65F, 1F);
            }

            Item frameItemEntity = world.dropItem(frame.getLocation(), JadeItemRegistry.PHANTOM_ITEM_FRAME.getItem());
            frameItemEntity.setVelocity(new Vector(0, 0.2, 0));
            world.playSound(frame.getLocation(), Sound.ENTITY_ITEM_FRAME_BREAK, 0.65F, 1F);
            frame.remove();
        }
    }

    @EventHandler
    public void onBuilderWand(BuilderWandEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) {
            return;
        }
        Player player = event.getPlayer();

        // Get editable properties
        List<String> properties = new ArrayList<>();

        if (block.getBlockData() instanceof Directional) {
            properties.add("facing");
        }
        if (block.getBlockData() instanceof Bisected) {
            properties.add("half");
        }
        if (block.getBlockData() instanceof Snowable) {
            properties.add("snowy");
        }
        if (block.getBlockData() instanceof Stairs) {
            properties.add("shape");
        }
        if (block.getBlockData() instanceof Wall) {
            properties.add("north");
            properties.add("east");
            properties.add("south");
            properties.add("west");
            properties.add("up");
        }

        // Handle previous data
        if (WAND_MAP.containsKey(player.getUniqueId())) {

            // If the last record pertains to a different block or mentions an invalid property, then clear the record
            if (!WAND_MAP.get(player.getUniqueId()).key().equals(block.getLocation()) ||
                    !properties.contains(WAND_MAP.get(player.getUniqueId()).right())) {
                WAND_MAP.remove(player.getUniqueId());
                if (!properties.isEmpty()) {
                    WAND_MAP.put(player.getUniqueId(), Pair.of(block.getLocation(), properties.getFirst()));
                } else {
                    player.sendActionBar(TextUtils.deserialize("<red>This block has no editable properties..."));
                    return;
                }
            }

            String savedProperty = WAND_MAP.get(player.getUniqueId()).right();
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                int idx = properties.indexOf(savedProperty);
                if (idx < 0 || idx + 1 == properties.size()) {
                    savedProperty = properties.getFirst();
                } else {
                    savedProperty = properties.get(idx + 1);
                }

                WAND_MAP.remove(player.getUniqueId());
                WAND_MAP.put(player.getUniqueId(), Pair.of(block.getLocation(), savedProperty));

                player.sendActionBar(composeWandPropertiesBar(savedProperty, properties));
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                switch (savedProperty) {
                    case "facing":
                        Directional directional = (Directional) block.getBlockData();
                        List<BlockFace> faces = directional.getFaces().stream().sorted().toList();
                        BlockFace face;
                        int fIdx = faces.indexOf(directional.getFacing());
                        if (fIdx < 0 || fIdx + 1 == faces.size()) {
                            face = faces.getFirst();
                        } else {
                            face = faces.get(fIdx + 1);
                        }
                        directional.setFacing(face);
                        block.setBlockData(directional, false);
                        player.sendActionBar(composeStringPropertyBar("facing", faces.stream().map(BlockFace::name).toList(), face.name()));
                        break;
                    case "half":
                        Bisected bisected = (Bisected) block.getBlockData();
                        if (bisected.getHalf() == Bisected.Half.BOTTOM) {
                            bisected.setHalf(Bisected.Half.TOP);
                            block.setBlockData(bisected, false);
                            player.sendActionBar(composeStringPropertyBar("half", List.of("upper", "lower"), "upper"));
                        } else {
                            bisected.setHalf(Bisected.Half.BOTTOM);
                            block.setBlockData(bisected, false);
                            player.sendActionBar(composeStringPropertyBar("half", List.of("upper", "lower"), "lower"));
                        }
                        break;
                    case "snowy":
                        Snowable snowable = (Snowable) block.getBlockData();
                        if (snowable.isSnowy()) {
                            snowable.setSnowy(false);
                            block.setBlockData(snowable, false);
                            player.sendActionBar(composeBooleanPropertyBar("snowy", false));
                        } else {
                            snowable.setSnowy(true);
                            block.setBlockData(snowable, false);
                            player.sendActionBar(composeBooleanPropertyBar("snowy", true));
                        }
                        break;
                    case "shape":
                        Stairs stairs = (Stairs) block.getBlockData();
                        Stairs.Shape shape = stairs.getShape();
                        List<Stairs.Shape> shapes = Arrays.stream(Stairs.Shape.values()).sorted().toList();
                        int sIdx = shapes.indexOf(shape);
                        if (sIdx < 0 || sIdx + 1 == shapes.size()) {
                            shape = shapes.getFirst();
                        } else {
                            shape = shapes.get(sIdx + 1);
                        }

                        stairs.setShape(shape);
                        block.setBlockData(stairs, false);
                        player.sendActionBar(composeStringPropertyBar("shape", shapes.stream().map(Stairs.Shape::name).toList(), shape.name()));
                        break;
                    case "up":
                        Wall wall = (Wall) block.getBlockData();
                        if (wall.isUp()) {
                            wall.setUp(false);
                            block.setBlockData(wall, false);
                            player.sendActionBar(composeBooleanPropertyBar("up", false));
                        } else {
                            wall.setUp(true);
                            block.setBlockData(wall, false);
                            player.sendActionBar(composeBooleanPropertyBar("up", true));
                        }
                        break;
                    case "north":
                    case "east":
                    case "south":
                    case "west":
                        BlockFace dFace = BlockFace.valueOf(savedProperty.toUpperCase(Locale.ROOT));
                        Wall dWall = (Wall) block.getBlockData();
                        List<Wall.Height> heights = Arrays.stream(Wall.Height.values()).sorted().toList();
                        Wall.Height height;
                        int hIdx = heights.indexOf(dWall.getHeight(dFace));
                        if (hIdx < 0 || hIdx + 1 == heights.size()) {
                            height = heights.getFirst();
                        } else {
                            height = heights.get(hIdx + 1);
                        }

                        dWall.setHeight(dFace, height);
                        block.setBlockData(dWall);
                        player.sendActionBar(composeStringPropertyBar(savedProperty, heights.stream().map(Wall.Height::name).toList(), height.name()));
                        break;
                    default:
                        player.sendActionBar(TextUtils.deserialize("<red>Unknown property!"));
                        break;
                }
            }
        } else {
            if (!properties.isEmpty()) {
                WAND_MAP.put(player.getUniqueId(), Pair.of(block.getLocation(), properties.getFirst()));
            } else {
                player.sendActionBar(TextUtils.deserialize("<red>This block has no editable properties..."));
                return;
            }
            return;
        }
    }

    public Component composeWandPropertiesBar(String highlightedProperty, List<String> properties) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<light_amethyst>Properties: ");
        for (String property : properties) {
            if (property.equalsIgnoreCase(highlightedProperty)) {
                stringBuilder.append("<light_chrome><bold>" + property);
            } else {
                stringBuilder.append("<chrome>" + property);
            }

            if (!properties.getLast().equalsIgnoreCase(property)) {
                stringBuilder.append(" <reset><light_amethyst>| ");
            }
        }
        return TextUtils.deserialize(stringBuilder.toString());
    }

    public Component composeStringPropertyBar(String property, List<String> possibleValues, String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<amethyst><bold>(" + property + "): <reset>");
        for (String possibleValue : possibleValues) {
            if (possibleValue.equalsIgnoreCase(value)) {
                stringBuilder.append("<light_chrome><bold>" + possibleValue);
            } else {
                stringBuilder.append("<chrome>" + possibleValue);
            }

            if (!possibleValues.getLast().equalsIgnoreCase(possibleValue)) {
                stringBuilder.append(" <reset><amethyst>| ");
            }
        }
        return TextUtils.deserialize(stringBuilder.toString());
    }

    public Component composeBooleanPropertyBar(String property, boolean value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<amethyst><bold>(" + property + "): <reset>");
        if (value) {
            stringBuilder.append(" <green>TRUE <amethyst>| <zorba>false");
        } else {
            stringBuilder.append(" <zorba>true <amethyst>| <red>FALSE");
        }
        return TextUtils.deserialize(stringBuilder.toString());
    }
}

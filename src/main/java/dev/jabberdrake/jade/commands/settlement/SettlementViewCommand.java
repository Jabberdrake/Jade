package dev.jabberdrake.jade.commands.settlement;

import com.destroystokyo.paper.ParticleBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.Area;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementViewCommand {
    private static final int CLAIM_VIEW_RANGE = 2;
    private static final int AREA_VIEW_RANGE = 1;

    private static Map<UUID, BukkitTask> ongoingViewTasks = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementViewCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());

        jadePlayer.toggleBorderview();

        if (jadePlayer.isViewingBorders()) {
            player.sendMessage(info("You are now viewing settlement borders around you!"));

            Bukkit.getScheduler().runTaskTimer(Jade.getPlugin(Jade.class), task -> {
                ongoingViewTasks.put(player.getUniqueId(), task);
                if (!player.isOnline() || player.isDead()) { task.cancel(); }

                drawClaimBorders(player, new ChunkAnchor(player.getChunk()));
            }, 0, 30);
        } else {
            ongoingViewTasks.remove(player.getUniqueId()).cancel();
            player.sendMessage(info("You are no longer viewing settlement borders!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static ParticleBuilder buildClaimParticle(TextColor mapColor, Player player) {
        Particle.DustOptions claimBorderOptions = new Particle.DustOptions(
                Color.fromRGB(mapColor.red(), mapColor.green(), mapColor.blue()
                ), 10F);

        return new ParticleBuilder(Particle.DUST)
                .data(claimBorderOptions).count(5).clone().receivers(player);
    }

    public static ParticleBuilder buildAreaParticle(Player player) {
        return new ParticleBuilder(Particle.FLAME).count(0).clone().receivers(player);
    }

    public static void drawClaimBorders(Player player, ChunkAnchor anchor) {
        World world = player.getWorld();
        double y = player.getY();

        Set<Area> areas = new HashSet<>();

        Set<ChunkAnchor> processed = new LinkedHashSet<>();
        Queue<ChunkAnchor> queue = new LinkedList<>();
        queue.add(anchor);
        while (!queue.isEmpty()) {
            ChunkAnchor head = queue.poll();
            if (getMaxDistance(anchor, head) > CLAIM_VIEW_RANGE) { break; }

            Settlement owner = RealmManager.getChunkOwner(head);

            ChunkAnchor north = head.getRelativeChunk(0, -1);
            ChunkAnchor south = head.getRelativeChunk(0, 1);
            ChunkAnchor west = head.getRelativeChunk(-1, 0);
            ChunkAnchor east = head.getRelativeChunk(1, 0);

            if (owner == null) {
                processed.add(head);
                enqueueIfValid(processed, queue, anchor, north);
                enqueueIfValid(processed, queue, anchor, south);
                enqueueIfValid(processed, queue, anchor, west);
                enqueueIfValid(processed, queue, anchor, east);
                continue;
            }
            owner.getAreaList().forEach(area -> {
                if (area.intersectsChunk(head)) {
                    areas.add(area);
                }
            });

            Settlement northOwner = RealmManager.getChunkOwner(north);
            Settlement southOwner = RealmManager.getChunkOwner(south);
            Settlement westOwner = RealmManager.getChunkOwner(west);
            Settlement eastOwner = RealmManager.getChunkOwner(east);
            if (northOwner == null || !northOwner.equals(owner)) {
                drawNorthEdge(buildClaimParticle(owner.getMapColor(), player), world, head, y);
            }

            if (southOwner == null || !southOwner.equals(owner)) {
                drawSouthEdge(buildClaimParticle(owner.getMapColor(), player), world, head, y);
            }

            if (westOwner == null || !westOwner.equals(owner)) {
                drawWestEdge(buildClaimParticle(owner.getMapColor(), player), world, head, y);
            }

            if (eastOwner == null || !eastOwner.equals(owner)) {
                drawEastEdge(buildClaimParticle(owner.getMapColor(), player), world, head, y);
            }

            processed.add(head);
            enqueueIfValid(processed, queue, anchor, north);
            enqueueIfValid(processed, queue, anchor, south);
            enqueueIfValid(processed, queue, anchor, west);
            enqueueIfValid(processed, queue, anchor, east);
        }

        if (!areas.isEmpty()) {
            ParticleBuilder areaParticle = buildAreaParticle(player);
            areas.forEach(area -> {
                for (Location point : area.getWireframe(false)) {
                    areaParticle.location(point);
                    areaParticle.spawn();
                }
            });
        }
    }

    public static int getMaxDistance(ChunkAnchor reference, ChunkAnchor anchor) {
        int dX = Math.abs(anchor.getX() - reference.getX());
        int dZ = Math.abs(anchor.getZ() - reference.getZ());
        return Math.max(dX, dZ);
    }

    public static void enqueueIfValid(Set<ChunkAnchor> processed, Queue<ChunkAnchor> queue, ChunkAnchor reference, ChunkAnchor anchor) {
        if (getMaxDistance(reference, anchor) <= CLAIM_VIEW_RANGE && !processed.contains(anchor) && !queue.contains(anchor)) {
            queue.add(anchor);
        }
    }

    public static void drawNorthEdge(ParticleBuilder particle, World world, ChunkAnchor anchor, double y) {
        for (int i = 0; i <= 15; i++) {
            particle = particle.location(new Location(world, (anchor.getX() << 4) + i, y, (anchor.getZ() << 4)));
            particle.spawn();
        }
    }

    public static void drawSouthEdge(ParticleBuilder particle, World world, ChunkAnchor anchor, double y) {
        for (int i = 0; i <= 15; i++) {
            particle = particle.location(new Location(world, (anchor.getX() << 4) + i, y, (anchor.getZ() << 4) + 16));
            particle.spawn();
        }
    }

    public static void drawWestEdge(ParticleBuilder particle, World world, ChunkAnchor anchor, double y) {
        for (int i = 0; i <= 15; i++) {
            particle = particle.location(new Location(world, (anchor.getX() << 4), y, (anchor.getZ() << 4) + i));
            particle.spawn();
        }
    }

    public static void drawEastEdge(ParticleBuilder particle, World world, ChunkAnchor anchor, double y) {
        for (int i = 0; i <= 15; i++) {
            particle = particle.location(new Location(world, (anchor.getX() << 4) + 16, y, (anchor.getZ() << 4) + i));
            particle.spawn();
        }
    }
}

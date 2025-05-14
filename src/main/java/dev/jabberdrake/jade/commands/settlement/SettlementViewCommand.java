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
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SettlementViewCommand {
    private static final int CLAIM_VIEW_RANGE = 2;
    private static final int AREA_VIEW_RANGE = 1;

    private static Map<UUID, BukkitTask> ongoingViewTasks = new HashMap<>();

    private static Particle.DustOptions claimBorderOptions = new Particle.DustOptions(Color.YELLOW, 10F);
    private static ParticleBuilder claimParticleTemplate = new ParticleBuilder(Particle.DUST)
            .data(claimBorderOptions).count(10);

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementViewCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        ParticleBuilder borderParticle = claimParticleTemplate.clone().receivers(player);

        jadePlayer.toggleBorderview();

        if (jadePlayer.isViewingBorders()) {
            player.sendMessage(TextUtils.composeSimpleSuccessMessage("You are now viewing settlement borders!"));

            Bukkit.getScheduler().runTaskTimer(Jade.getPlugin(Jade.class), task -> {
                ongoingViewTasks.put(player.getUniqueId(), task);
                if (!player.isOnline() || player.isDead()) { task.cancel(); }

                drawClaimBorders(borderParticle, player, new ChunkAnchor(player.getChunk()));
            }, 0, 60);
        } else {
            ongoingViewTasks.remove(player.getUniqueId()).cancel();
            player.sendMessage(TextUtils.composeSimpleSuccessMessage("You are no longer viewing settlement borders!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static void drawClaimBorders(ParticleBuilder particle, Player player, ChunkAnchor anchor) {
        World world = player.getWorld();
        double y = player.getY() - 2;

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

            Settlement northOwner = RealmManager.getChunkOwner(north);
            Settlement southOwner = RealmManager.getChunkOwner(south);
            Settlement westOwner = RealmManager.getChunkOwner(west);
            Settlement eastOwner = RealmManager.getChunkOwner(east);
            if (northOwner == null || !northOwner.equals(owner)) {
                drawNorthEdge(particle, world, head, y);
            }

            if (southOwner == null || !southOwner.equals(owner)) {
                drawSouthEdge(particle, world, head, y);
            }

            if (westOwner == null || !westOwner.equals(owner)) {
                drawWestEdge(particle, world, head, y);
            }

            if (eastOwner == null || !eastOwner.equals(owner)) {
                drawEastEdge(particle, world, head, y);
            }

            processed.add(head);
            enqueueIfValid(processed, queue, anchor, north);
            enqueueIfValid(processed, queue, anchor, south);
            enqueueIfValid(processed, queue, anchor, west);
            enqueueIfValid(processed, queue, anchor, east);

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

package dev.jabberdrake.jade.utils;

import dev.jabberdrake.jade.realms.ChunkAnchor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class CuboidSelection {
    private final World world;

    private double maxX;
    private double maxY;
    private double maxZ;

    private double minX;
    private double minY;
    private double minZ;

    private Location pos1;
    private Location pos2;

    // cached frames
    private List<Location> pos1Frame;
    private List<Location> pos2Frame;
    private List<Location> wireframe;

    private Set<ChunkAnchor> chunks = new HashSet<>();

    public CuboidSelection(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public Location getPos1() {
        return this.pos1;
    }

    public boolean hasPos1() {
        return pos1 != null;
    }

    public void setPos1(Location pos1) {
        if (!pos1.getWorld().equals(this.world)) return;
        this.pos1 = pos1;
        this.pos1Frame = generateFrameForLocation(this.pos1);
        if (this.pos2 != null) {
            this.updateInternals();
        }
    }

    public Location getPos2() {
        return this.pos2;
    }

    public boolean hasPos2() {
        return pos2 != null;
    }

    public void setPos2(Location pos2) {
        if (!pos2.getWorld().equals(this.world)) return;
        this.pos2 = pos2;
        this.pos2Frame = generateFrameForLocation(this.pos2);
        if (this.pos1 != null) {
            this.updateInternals();
        }
    }

    private void updateInternals() {
        if (pos1 == null || pos2 == null) return;

        // Setting X coordinates
        if (pos1.getX() > pos2.getX()) {
            this.maxX = pos1.getX();
            this.minX = pos2.getX();
        } else {
            this.maxX = pos2.getX();
            this.minX = pos1.getX();
        }

        // Setting Y coordinates
        if (pos1.getY() > pos2.getY()) {
            this.maxY = pos1.getY();
            this.minY = pos2.getY();
        } else {
            this.maxY = pos2.getY();
            this.minY = pos1.getY();
        }

        // Setting Z coordinates
        if (pos1.getZ() > pos2.getZ()) {
            this.maxZ = pos1.getZ();
            this.minZ = pos2.getZ();
        } else {
            this.maxZ = pos2.getZ();
            this.minZ = pos1.getZ();
        }

        // Setting the chunk set
        for (double x = minX; x <= maxX; x += 16) {
            for (double z = minZ; z <= maxZ; z += 16) {
                Chunk chunk = new Location(world, x, 70, z).getChunk();
                chunks.add(new ChunkAnchor(chunk));
            }
        }

        // Recalculating frames
        pos1Frame = generateFrameForLocation(pos1);
        pos2Frame = generateFrameForLocation(pos2);

        wireframe = generateWireframe();
    }

    public int getVolume() {
        if (this.hasPos1() && this.hasPos2()) {
            return (int) (Math.abs(maxX - minX) * Math.abs(maxY - minY) * Math.abs(maxZ - minZ));
        } else return -1;
    }

    public List<Location> generateFrameForLocation(Location location) {
        double x = location.x();
        double y = location.y();
        double z = location.z();
        List<Location> frame = new ArrayList<>();
        frame.addAll(PositionUtils.traceNorthEdge(world, x, x+1, y, z, 0.25, true));
        frame.addAll(PositionUtils.traceNorthEdge(world, x, x+1, y, z, 0.25, false));
        frame.addAll(PositionUtils.traceWestEdge(world, x, y, z, z+1, 0.25, true));
        frame.addAll(PositionUtils.traceWestEdge(world, x, y, z, z+1, 0.25, false));
        frame.addAll(PositionUtils.traceEastEdge(world, x, y, z, z+1, 0.25, true));
        frame.addAll(PositionUtils.traceEastEdge(world, x, y, z, z+1, 0.25, false));
        frame.addAll(PositionUtils.traceSouthEdge(world, x, x+1, y, z, 0.25, true));
        frame.addAll(PositionUtils.traceSouthEdge(world, x, x+1, y, z, 0.25, false));
        frame.addAll(PositionUtils.traceNorthwestColumn(world, x, y, y+1, z, 0.25));
        frame.addAll(PositionUtils.traceNortheastColumn(world, x, y, y+1, z, 0.25));
        frame.addAll(PositionUtils.traceSouthwestColumn(world, x, y, y+1, z, 0.25));
        frame.addAll(PositionUtils.traceSoutheastColumn(world, x, y, y+1, z, 0.25));

        return frame;
    }

    public List<Location> getFrameForPos1() {
        return this.pos1Frame;
    }

    public List<Location> getFrameForPos2() {
        return this.pos2Frame;
    }

    public List<Location> generateWireframe() {
        List<Location> wireframe = new ArrayList<>();
        wireframe.addAll(PositionUtils.traceNorthEdge(world, minX, maxX+1, maxY, minZ, 0.25, true));
        wireframe.addAll(PositionUtils.traceNorthEdge(world, minX, maxX+1, minY, minZ, 0.25, false));
        wireframe.addAll(PositionUtils.traceWestEdge(world, minX, maxY, minZ, maxZ+1, 0.25, true));
        wireframe.addAll(PositionUtils.traceWestEdge(world, minX, minY, minZ, maxZ+1, 0.25, false));
        wireframe.addAll(PositionUtils.traceEastEdge(world, maxX, maxY, minZ, maxZ+1, 0.25, true));
        wireframe.addAll(PositionUtils.traceEastEdge(world, maxX, minY, minZ, maxZ+1, 0.25, false));
        wireframe.addAll(PositionUtils.traceSouthEdge(world, minX, maxX+1, maxY, maxZ, 0.25, true));
        wireframe.addAll(PositionUtils.traceSouthEdge(world, minX, maxX+1, minY, maxZ, 0.25, false));
        wireframe.addAll(PositionUtils.traceNorthwestColumn(world, maxX, minY, maxY+1, maxZ, 0.25));
        wireframe.addAll(PositionUtils.traceNortheastColumn(world, minX, minY, maxY+1, maxZ, 0.25));
        wireframe.addAll(PositionUtils.traceSouthwestColumn(world, maxX, minY, maxY+1, minZ, 0.25));
        wireframe.addAll(PositionUtils.traceSoutheastColumn(world, minX, minY, maxY+1, minZ, 0.25));

        return wireframe;
    }

    public List<Location> getWireframe() {
        return this.wireframe;
    }

    public boolean intersectsChunk(ChunkAnchor anchor) {
        for (ChunkAnchor chunk : chunks) {
            if (chunk.equals(anchor)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPosition(double x, double y, double z) {
        if (this.hasPos1() && this.hasPos2()) {
            return (x >= minX && x <= maxX) && (y >= minY && y <= maxY) && (z >= minZ && z <= maxZ);
        } else return false;
    }

    public int getShortestChunkDistanceTo(ChunkAnchor anchor) {
        int minDistance = 99;
        for (ChunkAnchor chunk : chunks) {
            int manhattanDistance = Math.abs(chunk.getX() - anchor.getX()) + Math.abs(chunk.getZ() - anchor.getZ());
            if (manhattanDistance < minDistance) {
                minDistance = manhattanDistance;
            }
        }
        return minDistance;
    }
}

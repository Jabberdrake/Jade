package dev.jabberdrake.jade.utils;

import dev.jabberdrake.jade.realms.ChunkAnchor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Stream;

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
        if (this.pos2 != null) {
            this.setAnchors();
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
        if (this.pos1 != null) {
            this.setAnchors();
        }
    }

    private void setAnchors() {
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
    }

    public List<Location> getFrameForLocation(Location location) {
        return List.of(
                location.offset(1, 1, 1).toLocation(world),
                location.offset(1, 1, 0).toLocation(world),
                location.offset(0, 1, 1).toLocation(world),
                location.offset(0, 1, 0).toLocation(world),
                location.offset(1, 0, 1).toLocation(world),
                location.offset(1, 0, 0).toLocation(world),
                location.offset(0, 0, 1).toLocation(world),
                location.offset(0, 0, 0).toLocation(world)
        );
    }

    public List<Location> getFrameForPos1() {
        return this.getFrameForLocation(this.pos1);
    }

    public List<Location> getFrameForPos2() {
        return this.getFrameForLocation(this.pos2);
    }

    public List<Location> getWireframe(boolean trimEnds) {
        if (hasPos1() && hasPos2()) {
            List<Location> wireframe = new ArrayList<>();

            wireframe.addAll(this.traceEdge("HIGH", "MAX -> E", trimEnds));
            wireframe.addAll(this.traceEdge("HIGH", "MAX -> W", trimEnds));
            wireframe.addAll(this.traceEdge("HIGH", "E -> MIN", trimEnds));
            wireframe.addAll(this.traceEdge("HIGH", "W -> MIN", trimEnds));
            wireframe.addAll(this.traceEdge("LOW", "MAX -> E", trimEnds));
            wireframe.addAll(this.traceEdge("LOW", "MAX -> W", trimEnds));
            wireframe.addAll(this.traceEdge("LOW", "E -> MIN", trimEnds));
            wireframe.addAll(this.traceEdge("LOW", "W -> MIN", trimEnds));
            wireframe.addAll(this.traceColumn("MAX", trimEnds));
            wireframe.addAll(this.traceColumn("E", trimEnds));
            wireframe.addAll(this.traceColumn("W", trimEnds));
            wireframe.addAll(this.traceColumn("MIN", trimEnds));

            return wireframe;
        } else return null;
    }

    public List<Location> traceEdge(String yPlane, String line, boolean trimEnds) {
        List<Location> edge = new ArrayList<>();
        double y = yPlane.equals("HIGH") ? maxY + 1 : minY;
        double dx = 0;
        double dz = 0;

        double trim = trimEnds ? 1 : 0;

        switch (line) {
            case "MAX -> E":
                // MAX = (maxX, ---, maxZ)
                // E = (minX, ---, maxZ)
                while (minX + trim + dx <= maxX + 1 - trim) {
                    edge.add(new Location(world, minX + dx, y, maxZ + 1));
                    dx += 0.25;
                }
                return edge;
            case "MAX -> W":
                // MAX = (maxX, ---, maxZ)
                // W = (maxX, ---, minZ)
                while (minZ + trim + dz <= maxZ + 1 - trim) {
                    edge.add(new Location(world, maxX + 1, y, minZ + dz));
                    dz += 0.25;
                }
                return edge;
            case "E -> MIN":
                // E = (minX, ---, maxZ)
                // MIN = (minX, ---, minZ)
                while (minZ + trim + dz <= maxZ + 1 - trim) {
                    edge.add(new Location(world, minX, y, minZ + dz));
                    dz += 0.25;
                }
                return edge;
            case "W -> MIN":
                // W = (maxX, ---, minZ)
                // MIN = (minX, ---, minZ)
                while (minX + trim + dx <= maxX + 1 - trim) {
                    edge.add(new Location(world, minX + dx, y, minZ));
                    dx += 0.25;
                }
                return edge;
            default:
                return null;
        }
    }

    public List<Location> traceColumn(String vertex, boolean trimEnds) {
        List<Location> column = new ArrayList<>();
        double x = 0;
        double z = 0;

        double trim = trimEnds ? 1 : 0;

        switch (vertex) {
            case "MAX":
                x = maxX + 1;
                z = maxZ + 1;
                break;
            case "W":
                x = maxX + 1;
                z = minZ;
                break;
            case "E":
                x = minX;
                z = maxZ + 1;
                break;
            case "MIN":
                x = minX;
                z = minZ;
                break;
            default:
                return null;
        }
        for (double dy = trim; minY + dy <= maxY + 1 - trim; dy += 0.25) {
            column.add(new Location(world, x, minY + dy, z));
        }
        return column;
    }

    public boolean intersectsChunk(ChunkAnchor anchor) {
        for (ChunkAnchor chunk : chunks) {
            if (chunk.equals(anchor)) {
                return true;
            }
        }
        return false;
    }
}

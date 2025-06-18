package dev.jabberdrake.jade.realms;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public record ChunkAnchor(World world, int x, int z) {

    public ChunkAnchor(String world, int x, int z) {
        this(Bukkit.getWorld(world), x, z);
    }

    public ChunkAnchor(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public ChunkAnchor(Chunk chunk) {
        this(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ChunkAnchor getRelativeChunk(int dx, int dz) {
        return new ChunkAnchor(this.world, this.x + dx, this.z + dz);
    }

    public String serialize() {
        return this.world.getName() + ";" + this.x + ";" + this.z;
    }

    public int getManhattanDistanceTo(ChunkAnchor other) {
        return Math.abs(this.getX() - other.getX()) + Math.abs(this.getZ() - other.getZ());
    }

    public static ChunkAnchor deserialize(String serializedAnchor) {
        String[] split = serializedAnchor.split(";");
        String world = split[0];
        int x = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        return new ChunkAnchor(world, x, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkAnchor) {
            ChunkAnchor other = (ChunkAnchor) obj;
            return this.world.equals(other.world) && this.x == other.x && this.z == other.z;
        } else return false;
    }

    @Override
    public String toString() {
        return "ChunkAnchor{world=" + this.world.getName() + "x=" + this.x + ", z=" + this.z + "}";
    }
}

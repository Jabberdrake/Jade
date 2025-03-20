package dev.jabberdrake.charter.realms;

import org.bukkit.Chunk;

public record ChunkAnchor(int x, int z) {

    public ChunkAnchor(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkAnchor(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ChunkAnchor getRelativeChunk(int dx, int dz) {
        return new ChunkAnchor(this.x + dx, this.z + dz);
    }

    public String serialize() {
        return this.x + ";" + this.z;
    }

    public static ChunkAnchor deserialize(String serializedAnchor) {
        String[] split = serializedAnchor.split(";");
        int x = Integer.parseInt(split[0]);
        int z = Integer.parseInt(split[1]);
        return new ChunkAnchor(x, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkAnchor) {
            ChunkAnchor other = (ChunkAnchor) obj;
            return this.x == other.x && this.z == other.z;
        } else return false;
    }

    @Override
    public String toString() {
        return "ChunkAnchor{x=" + this.x + ", z=" + this.z + "}";
    }
}

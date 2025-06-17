package dev.jabberdrake.jade.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class PositionUtils {

    public static List<Location> traceNorthEdge(World world, double fromX, double toX, double blockY, double blockZ, double increment, boolean nudgeY) {
        double y = nudgeY ? blockY + 1 : blockY;
        List<Location> edge = new ArrayList<>();
        for (double x = fromX; x <= toX; x += increment) {
            edge.add(new Location(world, x, y, blockZ));
        }
        return edge;
    }

    public static List<Location> traceWestEdge(World world, double blockX, double blockY, double fromZ, double toZ, double increment, boolean nudgeY) {
        double y = nudgeY ? blockY + 1 : blockY;
        List<Location> edge = new ArrayList<>();
        for (double z = fromZ; z <= toZ; z += increment) {
            edge.add(new Location(world, blockX, y, z));
        }
        return edge;
    }

    public static List<Location> traceSouthEdge(World world, double fromX, double toX, double blockY, double blockZ, double increment, boolean nudgeY) {
        double y = nudgeY ? blockY + 1 : blockY;
        List<Location> edge = new ArrayList<>();
        for (double x = fromX; x <= toX; x += increment) {
            edge.add(new Location(world, x, y, blockZ + 1));
        }
        return edge;
    }

    public static List<Location> traceEastEdge(World world, double blockX, double blockY, double fromZ, double toZ, double increment, boolean nudgeY) {
        double y = nudgeY ? blockY + 1 : blockY;
        List<Location> edge = new ArrayList<>();
        for (double z = fromZ; z <= toZ; z += increment) {
            edge.add(new Location(world, blockX + 1, y, z));
        }
        return edge;
    }

    public static List<Location> traceNorthwestColumn(World world, double blockX, double fromY, double toY, double blockZ, double increment) {
        List<Location> column = new ArrayList<>();
        for (double y = fromY; y <= toY; y += increment) {
            column.add(new Location(world, blockX + 1, y, blockZ + 1));
        }
        return column;
    }

    public static List<Location> traceNortheastColumn(World world, double blockX, double fromY, double toY, double blockZ, double increment) {
        List<Location> column = new ArrayList<>();
        for (double y = fromY; y <= toY; y += increment) {
            column.add(new Location(world, blockX, y, blockZ + 1));
        }
        return column;
    }

    public static List<Location> traceSouthwestColumn(World world, double blockX, double fromY, double toY, double blockZ, double increment) {
        List<Location> column = new ArrayList<>();
        for (double y = fromY; y <= toY; y += increment) {
            column.add(new Location(world, blockX + 1, y, blockZ));
        }
        return column;
    }

    public static List<Location> traceSoutheastColumn(World world, double blockX, double fromY, double toY, double blockZ, double increment) {
        List<Location> column = new ArrayList<>();
        for (double y = fromY; y <= toY; y += increment) {
            column.add(new Location(world, blockX, y, blockZ));
        }
        return column;
    }
}

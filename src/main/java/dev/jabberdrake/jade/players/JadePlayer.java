package dev.jabberdrake.jade.players;

import com.destroystokyo.paper.ParticleBuilder;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Stream;

public class JadePlayer {
    private static final int VIEW_RANGE = 2;


    private UUID uuid;

    // For general stuff
    private String roleplayName;
    private JadeTitle titleInUse;
    private boolean inRoleplay = false;

    // For claim stuff
    private int stmFocus = -1;
    private int natFocus = -1;
    private boolean autoclaim = false;
    private boolean borderview = false;

    private static Map<String, BukkitTask> ongoingViewTasks = new HashMap<>();

    public JadePlayer(UUID uuid) {
        this.uuid = uuid;
        this.roleplayName = Bukkit.getPlayer(uuid).getName();
        this.titleInUse = DefaultJadeTitle.PEASANT;
    }

    public JadePlayer(UUID uuid, String roleplayName, JadeTitle titleInUse) {
        this.uuid = uuid;
        this.roleplayName = roleplayName;
        this.titleInUse = titleInUse;
    }

    public UUID getUniqueID() {
        return this.uuid;
    }

    public OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.getUniqueID());
    }
    public String getRoleplayName() { return this.roleplayName; }

    public void setRoleplayName(String roleplayName) {
        this.roleplayName = roleplayName;
        DatabaseManager.savePlayer(this);
    }

    public JadeTitle getTitleInUse() {
        if (!this.titleInUse.isAvailableTo(this.uuid)) {
            this.setTitleInUse(DefaultJadeTitle.PEASANT);
        }
        return this.titleInUse;
    }

    public void setTitleInUse(JadeTitle title) {
        this.titleInUse = title;
        DatabaseManager.savePlayer(this);
    }

    public List<JadeTitle> getAvailableTitles() {
        return Stream.concat(TitleManager.getAllTitlesAvailableTo(this.uuid).stream(), TitleManager.getAllUniversalTitles().stream()).toList();
    }

    public List<JadeTitle> getOwnedTitles() {
        return TitleManager.getAllTitlesOwnedBy(this.uuid);
    }

    public JadeTitle getTitleFromName(String titleName) {
        for (JadeTitle title : this.getAvailableTitles()) {
            if (title.getName().equalsIgnoreCase(titleName)) {
                return title;
            }
        }
        return null;
    }

    public boolean canUseTitle(JadeTitle title) {
        return this.getAvailableTitles().contains(title);
    }

    public boolean isInRoleplay() {
        return this.inRoleplay;
    }

    public void toggleRoleplay() {
        this.inRoleplay = !this.inRoleplay;
    }

    public boolean isAutoclaiming() {
        return this.autoclaim;
    }

    public void toggleAutoclaim() {
        this.autoclaim = !this.autoclaim;
    }

    public <T> String getViewTaskKey(T value) {
        if (value instanceof Settlement) {
            Settlement stm = (Settlement) value;
            return "settlement." + stm.getName();
        } else if (value instanceof Area) {
            Area area = (Area) value;
            return "area." + area.getSettlement().getName() + "." + area.getName();
        } else return "all";
    }

    public boolean isViewingBorders() {
        return this.borderview;
    }

    public void toggleBorderview() {
        this.borderview = !this.borderview;

        String taskKey = getViewTaskKey("all");
        OfflinePlayer offlinePlayer = this.asOfflinePlayer();
        if (this.borderview) {
            Bukkit.getScheduler().runTaskTimer(Jade.getInstance(), task -> {
                ongoingViewTasks.put(taskKey, task);
                if (!offlinePlayer.isOnline() || ((Player) offlinePlayer).isDead()) { task.cancel(); }

                Player player = (Player) offlinePlayer;
                drawNearbyBorders(player, new ChunkAnchor(player.getChunk()));
            }, 0, 30);
        } else {
            ongoingViewTasks.remove(taskKey).cancel();
        }
    }

    public boolean toggleAreaView(Settlement settlement) {
        String taskKey = getViewTaskKey(settlement);

        // If we are already viewing all areas in a settlement, then stop doing that
        BukkitTask viewTask = ongoingViewTasks.remove(taskKey);
        if (viewTask != null) {
            viewTask.cancel();
            return false;
        }

        // If we are specifically viewing certain areas, then stop viewing those...
        List<String> entriesToRemove = new ArrayList<>();
        for (String targetTaskKey : ongoingViewTasks.keySet()) {
            if (targetTaskKey.startsWith("area." + settlement.getName())) {
                entriesToRemove.add(targetTaskKey);
            }
        }
        for (String entryToRemove : entriesToRemove) {
            ongoingViewTasks.remove(entryToRemove).cancel();
        }

        // ...and then, start viewing all areas in that settlement
        Bukkit.getScheduler().runTaskTimer(Jade.getPlugin(Jade.class), task -> {
            ongoingViewTasks.put(taskKey, task);

            OfflinePlayer offlinePlayer = this.asOfflinePlayer();
            if (!offlinePlayer.isOnline() || ((Player) offlinePlayer).isDead()) { task.cancel(); }

            Player player = (Player) offlinePlayer;
            drawNearbyAreas(player, new ChunkAnchor(player.getChunk()));
        }, 0, 30);
        return true;
    }

    public boolean toggleAreaView(Area area) {
        String taskKey = getViewTaskKey(area);

        // If we are already viewing that area, then stop viewing it
        BukkitTask viewTask = ongoingViewTasks.remove(taskKey);
        if (viewTask != null) {
            viewTask.cancel();
            return false;
        }

        // If we are already viewing all areas in a given settlement, then we cancel that...
        String allAreasTaskKey = getViewTaskKey(area.getSettlement());
        BukkitTask allAreasViewTask = ongoingViewTasks.remove(allAreasTaskKey);
        if (allAreasViewTask != null) {
            allAreasViewTask.cancel();
        }

        // ... and then, we start specifically viewing the selected area
        Bukkit.getScheduler().runTaskTimer(Jade.getPlugin(Jade.class), task -> {
             ongoingViewTasks.put(taskKey, task);

             OfflinePlayer offlinePlayer = this.asOfflinePlayer();
             if (!offlinePlayer.isOnline() || ((Player) offlinePlayer).isDead()) { task.cancel(); }

             Player player = (Player) offlinePlayer;
             drawAreaBorders(player, new ChunkAnchor(player.getChunk()), area);
        }, 0, 30);
        return true;
    }

    public void clearViewTasks() {
        for (BukkitTask task : ongoingViewTasks.values()) {
            task.cancel();
        }
        ongoingViewTasks.clear();
    }

    public Settlement getFocusSettlement() {
        if (this.stmFocus == -1) {
            return null;
        } else return RealmManager.getSettlement(this.stmFocus);
    }

    public void setFocusSettlement(Settlement settlement) {
        this.stmFocus = settlement.getId();
    }

    public static ParticleBuilder buildClaimParticle(TextColor mapColor, Player player) {
        Particle.DustOptions claimBorderOptions = new Particle.DustOptions(
                Color.fromRGB(mapColor.red(), mapColor.green(), mapColor.blue()
                ), 10F);

        return new ParticleBuilder(Particle.DUST)
                .data(claimBorderOptions).count(5).clone().receivers(player);
    }

    public static ParticleBuilder buildAreaParticle(Player player) {
        return new ParticleBuilder(Particle.WAX_OFF).count(0).clone().receivers(player);
    }

    public static ParticleBuilder buildAreaPos1Particle(Player player) {
        return new ParticleBuilder(Particle.SOUL_FIRE_FLAME).count(0).clone().receivers(player);
    }

    public static ParticleBuilder buildAreaPos2Particle(Player player) {
        return new ParticleBuilder(Particle.FLAME).count(0).clone().receivers(player);
    }

    public static void drawNearbyBorders(Player player, ChunkAnchor anchor) {
        World world = player.getWorld();
        double y = player.getY();

        Set<ChunkAnchor> processed = new LinkedHashSet<>();
        Queue<ChunkAnchor> queue = new LinkedList<>();
        queue.add(anchor);
        while (!queue.isEmpty()) {
            ChunkAnchor head = queue.poll();
            if (getMaxDistance(anchor, head) > VIEW_RANGE) { break; }

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
    }

    public static void drawNearbyAreas(Player player, ChunkAnchor anchor) {
        Set<Area> areas = new HashSet<>();

        Set<ChunkAnchor> processed = new LinkedHashSet<>();
        Queue<ChunkAnchor> queue = new LinkedList<>();
        queue.add(anchor);
        while (!queue.isEmpty()) {
            ChunkAnchor head = queue.poll();
            if (getMaxDistance(anchor, head) > VIEW_RANGE) { break; }

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

            processed.add(head);
            enqueueIfValid(processed, queue, anchor, north);
            enqueueIfValid(processed, queue, anchor, south);
            enqueueIfValid(processed, queue, anchor, west);
            enqueueIfValid(processed, queue, anchor, east);
        }

        if (!areas.isEmpty()) {
            ParticleBuilder areaParticle = buildAreaParticle(player);
            areas.forEach(area -> {
                for (Location point : area.getWireframe()) {
                    areaParticle.location(point);
                    areaParticle.spawn();
                }
            });
        }
    }

    public static void drawAreaBorders(Player player, ChunkAnchor anchor, Area area) {
        if (area.hasPos1()) {
            ParticleBuilder pos1Particle = buildAreaPos1Particle(player);
            for (Location point : area.getFrameForPos1()) {
                pos1Particle.location(point);
                pos1Particle.spawn();
            }
        }

        if (area.hasPos2()) {
            ParticleBuilder pos2Particle = buildAreaPos2Particle(player);
            for (Location point : area.getFrameForPos2()) {
                pos2Particle.location(point);
                pos2Particle.spawn();
            }
        }

        int chunkDistance = area.getShortestChunkDistanceTo(anchor);
        if (chunkDistance > VIEW_RANGE) return;

        if (area.hasPos1() && area.hasPos2()) {
            ParticleBuilder areaParticle = buildAreaParticle(player);
            for (Location point : area.getWireframe()) {
                areaParticle.location(point);
                areaParticle.spawn();
            }
        }
    }

    public static int getMaxDistance(ChunkAnchor reference, ChunkAnchor anchor) {
        int dX = Math.abs(anchor.getX() - reference.getX());
        int dZ = Math.abs(anchor.getZ() - reference.getZ());
        return Math.max(dX, dZ);
    }

    public static void enqueueIfValid(Set<ChunkAnchor> processed, Queue<ChunkAnchor> queue, ChunkAnchor reference, ChunkAnchor anchor) {
        if (getMaxDistance(reference, anchor) <= VIEW_RANGE && !processed.contains(anchor) && !queue.contains(anchor)) {
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

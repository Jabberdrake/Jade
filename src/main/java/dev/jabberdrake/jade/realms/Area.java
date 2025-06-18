package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.utils.CuboidSelection;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Area {

    private static final String DEFAULT_NAME_DECORATION = "<gold>";

    private int id;
    private String name;
    private String displayName;
    private Settlement settlement;
    private NamespacedKey icon;
    private CuboidSelection selection;
    private UUID holder;
    private List<UUID> members = new ArrayList<>();

    // Used by DatabaseManager when composing runtime object from persistent data
    public Area(int id, String name, String displayName, Settlement settlement, NamespacedKey icon, String serializedPos1, String serializedPos2, UUID holder) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.settlement = settlement;
        this.icon = icon;
        this.selection = new CuboidSelection(settlement.getWorld());
        this.holder = holder;

        Location pos1 = deserializeLocation(serializedPos1);
        if (pos1 != null) this.selection.setPos1(pos1);

        Location pos2 = deserializeLocation(serializedPos2);
        if (pos2 != null) this.selection.setPos2(pos2);
    }

    // Used by SettlementAreasCommand when creating new area
    public Area(String name, Settlement settlement, Player founder) {
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.settlement = settlement;
        this.icon = NamespacedKey.minecraft("oak_fence_gate");
        this.selection = new CuboidSelection(settlement.getWorld());
        this.holder = founder.getUniqueId();
    }

    public int getId() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        DatabaseManager.saveArea(this);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Component getDisplayNameAsComponent() {
        return TextUtils.deserialize(this.getDisplayName());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveArea(this);
    }

    public Component asTextComponent() {
        return Component.text()
                .append(this.getDisplayNameAsComponent())
                .append(Component.text(" (" + this.getName() + ")", TextUtils.LIGHT_ZORBA))
                .build();
    }

    public String asDisplayString() {
        return this.getDisplayName() + "<normal> <light_zorba>(" + this.getName() + ")</light_zorba>";
    }

    public Settlement getSettlement() {
        return this.settlement;
    }

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
        DatabaseManager.saveArea(this);
    }

    public String getIconAsString() {
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItem(this.icon);
    }

    public int getVolume() {
        return this.selection.getVolume();
    }

    public boolean hasPos1() {
        return this.selection.hasPos1();
    }

    public boolean hasPos2() {
        return this.selection.hasPos2();
    }

    public Location getPos1() {
        return this.selection.getPos1();
    }

    public Location getPos2() {
        return this.selection.getPos2();
    }

    public String displayPos1() {
        Location pos1 = this.getPos1();
        return "(" + pos1.getBlockX() + "," + pos1.getBlockY() + "," + pos1.getBlockZ() + ")";
    }

    public String displayPos2() {
        Location pos2 = this.getPos2();
        return "(" + pos2.getBlockX() + "," + pos2.getBlockY() + "," + pos2.getBlockZ() + ")";
    }

    public void setPos1(double x, double y, double z) {
        this.selection.setPos1(new Location(this.selection.getWorld(), x, y, z));
        DatabaseManager.saveArea(this);
    }

    public void setPos2(double x, double y, double z) {
        this.selection.setPos2(new Location(this.selection.getWorld(), x, y, z));
        DatabaseManager.saveArea(this);
    }

    public boolean hasFinishedSelection() {
        return this.selection.hasPos1() && this.selection.hasPos2();
    }

    public UUID getHolderUniqueID() {
        return this.holder;
    }

    public boolean isHolder(UUID playerID) {
        return this.holder.equals(playerID);
    }

    public void setHolder(UUID playerID) {
        this.holder = playerID;
        DatabaseManager.saveArea(this);
    }

    public List<UUID> getMemberList() {
        return this.members;
    }

    // Used by DatabaseManager
    public void setMemberList(List<UUID> memberList) {
        this.members = memberList;
    }

    public boolean isMember(UUID playerID) {
        return this.getMemberList().contains(playerID);
    }

    public void addMember(UUID playerID) {
        if (!this.getMemberList().contains(playerID)) {
            this.getMemberList().add(playerID);
            DatabaseManager.addMemberToArea(playerID, this);
        }
    }

    public void removeMember(UUID playerID) {
        this.getMemberList().remove(playerID);
        DatabaseManager.removeMemberFromArea(playerID, this);
    }

    public List<Location> getFrameForPos1() {
        return this.selection.getFrameForPos1();
    }

    public List<Location> getFrameForPos2() {
        return this.selection.getFrameForPos2();
    }

    public List<Location> getWireframe() {
        return this.selection.getWireframe();
    }

    public boolean intersectsChunk(ChunkAnchor anchor) {
        return this.selection.intersectsChunk(anchor);
    }

    public boolean containsPosition(double x, double y, double z) {
        return this.selection.containsPosition(x, y, z);
    }

    public int getShortestChunkDistanceTo(ChunkAnchor anchor) {
        return this.selection.getShortestChunkDistanceTo(anchor);
    }

    private Location deserializeLocation(String serializedLocation) {
        if (this.settlement == null) return null;

        if (serializedLocation.equalsIgnoreCase("NONE")) return null;

        String[] parts = serializedLocation.split(";");
        Location loc;
        try {
            loc = new Location(settlement.getWorld(), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
        return loc;
    }

    private String serializeLocation(Location location) {
        return ((int) location.getX()) + ";" + ((int) location.getY()) + ";" + ((int) location.getZ());
    }

    public String serializePos1() {
        if (!hasPos1()) return "NONE";

        return serializeLocation(getPos1());
    }

    public String serializePos2() {
        if (!hasPos2()) return "NONE";

        return serializeLocation(getPos2());
    }

    public ItemStack asDisplayItem() {
        return this.asDisplayItem(null);
    }

    public ItemStack asDisplayItem(String addon) {
        ItemStack item = this.getIconAsItem();
        item.setData(DataComponentTypes.CUSTOM_NAME, text()
                .append(this.asTextComponent())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore();

        loreBuilder.addLine(text("Anchors: ", JadeTextColor.LIGHT_BRASS)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        loreBuilder.addLine(text()
                        .content("— ").color(JadeTextColor.LIGHT_BRASS)
                .append(this.getPos1() == null ? text("Not set.", JadeTextColor.DARK_ZORBA) : text(displayPos1(), JadeTextColor.LIGHT_BLUE))
                .append(text(" (Position 1)", JadeTextColor.DARK_ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );
        loreBuilder.addLine(text()
                .content("— ").color(JadeTextColor.LIGHT_BRASS)
                .append(this.getPos2() == null ? text("Not set.", JadeTextColor.DARK_ZORBA) : text(displayPos2(), JadeTextColor.GOLD))
                .append(text(" (Position 2)", JadeTextColor.DARK_ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );
        loreBuilder.addLine(Component.empty());
        loreBuilder.addLine(text("Holder: ", JadeTextColor.LIGHT_BRASS)
                .append(getSettlement().getMemberAsComponent(holder))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        loreBuilder.addLine(text("Members: ", JadeTextColor.LIGHT_BRASS)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        if (this.getMemberList().size() == 1) {
            loreBuilder.addLine(Component.text().content("— ").color(JadeTextColor.LIGHT_BRASS)
                    .append(Component.text("None...", JadeTextColor.DARK_ZORBA))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        this.getMemberList().stream().filter(id -> !id.equals(holder))
                .limit(10)
                .forEach(member -> {
                    loreBuilder.addLine(Component.text().content("— ").color(JadeTextColor.LIGHT_BRASS)
                            .append(getSettlement().getMemberAsComponent(member))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                });

        if (this.getMemberList().size() > 10) {
            loreBuilder.addLine(Component.text().content("— ").color(JadeTextColor.LIGHT_BRASS)
                    .append(Component.text("...", JadeTextColor.LIGHT_ZORBA))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        if (addon == null) {
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
        } else {
            loreBuilder.addLine(Component.empty());
            switch (addon) {
                case "VIEW":
                    loreBuilder.addLine(text()
                            .append(text("Left Click", NamedTextColor.GREEN))
                            .append(text(" to view this area", NamedTextColor.DARK_GREEN))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .build());
                default:
                    item.setData(DataComponentTypes.LORE, loreBuilder.build());
            }
        }

        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Area other) {
            return this.getId() == other.getId();
        } else return false;
    }
}

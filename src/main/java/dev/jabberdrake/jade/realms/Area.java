package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.utils.CuboidSelection;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    public Area(int id, String name, String displayName, Settlement settlement, NamespacedKey icon, String pos1, String pos2, UUID holder) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.settlement = settlement;
        this.icon = icon;
        this.selection = new CuboidSelection(settlement.getWorld());
        this.selection.setPos1(deserializeLocation(pos1));
        this.selection.setPos2(deserializeLocation(pos2));
        this.holder = holder;
    }

    // Used by Settlement when creating new area
    public Area(String name, Settlement settlement, Player founder) {
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.settlement = settlement;
        this.icon = NamespacedKey.minecraft("oak_fence_gate");
        this.selection = new CuboidSelection(settlement.getWorld());
        this.holder = founder.getUniqueId();
        this.members.add(founder.getUniqueId());
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        //DatabaseManager.saveSettlementArea(this);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Component getDisplayNameAsComponent() {
        return TextUtils.deserialize(this.getDisplayName());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        //DatabaseManager.saveSettlementArea(this);
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

    public void setIcon(NamespacedKey icon) { this.icon = icon; }

    public String getIconAsString() {
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItem(this.icon);
    }

    public Location getPos1() {
        return this.selection.getPos1();
    }

    public String getPos1AsString() {
        Location pos1 = this.getPos1();
        return "(" + pos1.getBlockX() + "," + pos1.getBlockY() + "," + pos1.getBlockZ() + ")";
    }

    public String getPos2AsString() {
        Location pos2 = this.getPos2();
        return "(" + pos2.getBlockX() + "," + pos2.getBlockY() + "," + pos2.getBlockZ() + ")";
    }

    public Location getPos2() {
        return this.selection.getPos2();
    }

    public void setPos1(double x, double y, double z) {
        this.selection.setPos1(new Location(this.selection.getWorld(), x, y, z));
    }

    public void setPos2(double x, double y, double z) {
        this.selection.setPos2(new Location(this.selection.getWorld(), x, y, z));
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
    }

    public List<UUID> getMemberList() {
        return this.members;
    }

    public void setMemberList(List<UUID> memberList) {
        this.members = memberList;
    }

    public boolean isMember(UUID playerID) {
        return this.getMemberList().contains(playerID);
    }

    public void addMember(UUID playerID) {
        if (!this.getMemberList().contains(playerID)) {
            this.getMemberList().add(playerID);
        }
    }

    public List<Location> getFrameForPos1() {
        return this.selection.getFrameForPos1();
    }

    public List<Location> getFrameForPos2() {
        return this.selection.getFrameForPos2();
    }

    public List<Location> getWireframe(boolean trimEnds) {
        return this.selection.getWireframe(trimEnds);
    }

    public boolean intersectsChunk(ChunkAnchor anchor) {
        return this.selection.intersectsChunk(anchor);
    }

    private Location deserializeLocation(String serializedLocation) {
        if (this.settlement == null) return null;

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
        return location.getX() + ";" + location.getY() + ";" + location.getZ();
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
                .append(this.getPos1() == null ? text("Not set.", JadeTextColor.DARK_ZORBA) : text(getPos1AsString(), JadeTextColor.LIGHT_BLUE))
                .append(text(" (Position 1)", JadeTextColor.DARK_ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );
        loreBuilder.addLine(text()
                .content("— ").color(JadeTextColor.LIGHT_BRASS)
                .append(this.getPos2() == null ? text("Not set.", JadeTextColor.DARK_ZORBA) : text(getPos2AsString(), JadeTextColor.GOLD))
                .append(text(" (Position 2)", JadeTextColor.DARK_ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );
        loreBuilder.addLine(Component.empty());
        loreBuilder.addLine(text("WIP!", JadeTextColor.RED));

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
}

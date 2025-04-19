package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class Nation {

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private String icon;
    private long creationTime;

    // Normally, just storing 'capital' would do, but due to the way Settlements *also*
    // reference capitals, and fetchNation is called during fetchSettlement, if we ever try to
    // load a capital settlement from database, we'll be stuck in a loop. That way, whenever a nation
    // object is loaded, it stores simply the capital identifier; then, if it is ever necessary to
    // resolve this identifier (get the matching Settlement object), we'll quickly ask the RealmManager
    // to work it out for us and consequently cache the result for future use (check getNation()).
    private int capitalId;
    private Settlement capital;

    // Same thing here.
    private List<Integer> memberIds;
    private List<Settlement> members;

    // Used by DatabaseManager when composing runtime object from persistent data
    public Nation(int id, String name, String displayName, String description, TextColor mapColor, String icon, long creationTime, int capitalId) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.icon = icon;
        this.creationTime = creationTime;
        this.capitalId = capitalId;
        this.capital = null;
        this.memberIds = null;
        this.members = null;
    }

    // Used by RealmManager when creating new nation
    public Nation(String name, TextColor mapColor, Settlement capital) {
        this.name = name;
        this.displayName = "<gold>" + name;
        this.mapColor = mapColor;
        this.icon = "minecraft:barrel";
        this.creationTime = System.currentTimeMillis() / 1000L;

        this.capitalId = capital.getId();
        this.capital = capital;

        this.memberIds = new ArrayList<>(List.of(capital.getId()));
        this.members = new ArrayList<>(List.of(capital));

        this.id = DatabaseManager.createNation(this);
    }

    public int getId() { return this.id; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getDisplayNameAsString() { return this.displayName; }

    public Component getDisplayName() { return MiniMessage.miniMessage().deserialize(this.displayName); }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescriptionAsString() {
        return this.description;
    }

    public Component getDescription() {
        return MiniMessage.miniMessage().deserialize(this.getDescriptionAsString());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TextColor getMapColor() { return this.mapColor; }

    public void setMapColor(TextColor mapColor) { this.mapColor = mapColor; }

    public String getIconAsString() { return this.icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public long getCreationTimeAsLong() { return this.creationTime; }

    public String getCreationTimeAsString() {
        ZonedDateTime dateTime = Instant.ofEpochMilli(this.creationTime).atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        return dateTime.format(formatter);
    }

    public Settlement getCapital() {
        if (this.capital == null) {
            this.capital = RealmManager.getSettlement(this.capitalId);
        }
        return this.capital;
    }

    public void setCapital(Settlement capital) {
        this.capital = capital;
        this.capitalId = capital.getId();
    }

    public List<Settlement> getMembers() {
        if (this.members == null) {
            List<Settlement> members = new ArrayList<>();
            for (int memberId : this.memberIds) {
                members.add(RealmManager.getSettlement(memberId));
            }
            this.members = members;
        }
        return this.members;
    }

    // Used by DatabaseManager
    public void setMemberList(List<Integer> memberIds) {
        this.memberIds = memberIds;
    }

    public void addSettlement(Settlement settlement) {
        if (!this.members.contains(settlement)) {
            this.members.add(settlement);
        }
    }

    public void removeSettlement(Settlement settlement) {
        this.members.remove(settlement);
    }

    public boolean containsSettlement(Settlement settlement) {
        return this.members.contains(settlement);
    }

    public static Nation load(FileConfiguration data, String root) {
        return null;
    }

    public static void store(Nation nation, FileConfiguration data, String root) {

    }


}

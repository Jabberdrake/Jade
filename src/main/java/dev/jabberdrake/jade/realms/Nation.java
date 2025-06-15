package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.TextUtils;
import dev.jabberdrake.jade.utils.message.NationStrategy;
import dev.jabberdrake.jade.utils.message.SettlementStrategy;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Nation {

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private NamespacedKey icon;
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

    private NationStrategy broadcastFormatter = new NationStrategy(this);

    // Used by DatabaseManager when composing runtime object from persistent data
    public Nation(int id, String name, String displayName, String description, TextColor mapColor, NamespacedKey icon, long creationTime, int capitalId) {
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
    public Nation(String name, Settlement capital) {
        this.name = name;
        this.displayName = "<gold>" + name;
        this.description = "<green>Preparing to take over the world...";
        this.mapColor = NamedTextColor.GOLD;
        this.icon = NamespacedKey.minecraft("barrel");
        this.creationTime = System.currentTimeMillis() / 1000L;

        this.capitalId = capital.getId();
        this.capital = capital;

        this.memberIds = new ArrayList<>(List.of(capital.getId()));
        this.members = new ArrayList<>(List.of(capital));

        this.id = DatabaseManager.createNation(this);
        DatabaseManager.addMemberToNation(capital, this);
    }

    public int getId() { return this.id; }

    public String getName() { return this.name; }

    public void setName(String name) {
        this.name = name;
        DatabaseManager.saveNation(this);
    }

    public String getDisplayNameAsString() { return this.displayName; }

    public Component getDisplayName() { return MiniMessage.miniMessage().deserialize(this.displayName); }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveNation(this);
    }

    public String getDescriptionAsString() {
        return this.description;
    }

    public Component getDescription() {
        return MiniMessage.miniMessage().deserialize(this.getDescriptionAsString());
    }

    public void setDescription(String description) {
        this.description = description;
        DatabaseManager.saveNation(this);
    }

    public TextColor getMapColor() { return this.mapColor; }

    public void setMapColor(TextColor mapColor) {
        this.mapColor = mapColor;
        DatabaseManager.saveNation(this);
    }

    public String getIconAsString() { return this.icon.asString(); }

    public ItemStack getIconAsItem() { return ItemUtils.asDisplayItem(this.icon); }

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
        DatabaseManager.saveNation(this);
    }

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
        DatabaseManager.saveNation(this);
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
        if (!this.getMembers().contains(settlement)) {
            this.getMembers().add(settlement);
            DatabaseManager.addMemberToNation(settlement, this);
        }
    }

    public void removeSettlement(Settlement settlement) {
        this.getMembers().remove(settlement);
        DatabaseManager.removeMemberFromNation(settlement, this);
    }

    public boolean containsSettlement(Settlement settlement) {
        return this.getMembers().contains(settlement);
    }

    public Component asTextComponent() {
        return Component.text()
                .append(this.getDisplayName())
                .append(Component.text(" (" + this.getName() + ")", TextUtils.LIGHT_ZORBA))
                .build();
    }

    public ItemStack asDisplayItem() {
        return this.asDisplayItem(null);
    }

    public ItemStack asDisplayItem(String addon) {
        final int maxMembersToDisplay = 10;

        ItemStack item = this.getIconAsItem();
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text()
                .append(this.asTextComponent())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore()
                .addLine(Component.text().append(this.getDescription())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text(""))
                .addLine(Component.text().content("Capital: ").color(TextUtils.LIGHT_BRASS)
                        .append(this.getCapital().getDisplayName())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text("Members: ", TextUtils.LIGHT_BRASS)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        List<Settlement> membersToDisplay = this.getMembers().stream().limit(maxMembersToDisplay).toList();

        for (Settlement member : membersToDisplay) {
            loreBuilder.addLine(Component.text("— ", TextUtils.LIGHT_BRASS)
                    .append(member.asTextComponent())
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        if (this.getMembers().size() > maxMembersToDisplay) {
            loreBuilder.addLine(Component.text("— ", TextUtils.LIGHT_BRASS)
                    .append(Component.text("...").color(TextUtils.ZORBA))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        if (addon == null) {
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
        } else {
            loreBuilder = loreBuilder.addLine(Component.text(""));
            switch (addon) {
                case "INFO":
                    loreBuilder = loreBuilder.addLine(Component.text()
                            .append(Component.text("Left Click", NamedTextColor.GREEN))
                            .append(Component.text(" to read more", NamedTextColor.DARK_GREEN))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .build());
                default:
                    item.setData(DataComponentTypes.LORE, loreBuilder.build());
            }
        }

        return item;
    }

    public void broadcast(String message, TagResolver... resolvers) {
        for (Settlement settlement : this.getMembers()) {
            for (UUID memberID : settlement.getPopulation().keySet()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberID);
                if (offlinePlayer.isOnline()) {
                    ((Player) offlinePlayer).sendMessage(this.broadcastFormatter.process(message, resolvers));
                }
            }
        }
    }

}

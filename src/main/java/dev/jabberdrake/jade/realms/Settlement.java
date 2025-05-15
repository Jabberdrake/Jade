package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

public class Settlement {

    private static final String DEFAULT_NAME_DECORATION = "<gold><bold>";
    private static final String DEFAULT_DESC = "<green>Settlement of <gold>";
    private static final TextColor DEFAULT_MAP_COLOR = TextUtils.ZORBA;
    private static final int DEFAULT_MAX_FOOD = 1000;

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private String icon;
    private int food;
    private int foodCapacity;
    private long creationTime;
    private Nation nation;
    private List<SettlementRole> roles;
    private Map<UUID, SettlementRole> population;
    private Set<ChunkAnchor> territory;

    // Used by DatabaseManager when composing runtime object from persistent data
    public Settlement(int id, String name, String displayName, String description, TextColor mapColor, String icon, int food, long creationTime, Nation nation) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.icon = icon;
        this.food = food;
        this.creationTime = creationTime;
        this.nation = nation;
        this.roles = new ArrayList<>();
        this.population = new HashMap<>();
        this.territory = new HashSet<>();

        calculateFoodCapacity();
    }

    // Used by RealmManager when creating new settlement
    public Settlement(String name, Player leader, ChunkAnchor startingChunk) {
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.description = DEFAULT_DESC + leader.getName();
        this.mapColor = DEFAULT_MAP_COLOR;
        this.icon = "minecraft:oak_planks";
        this.food = 20;
        this.calculateFoodCapacity();
        this.creationTime = System.currentTimeMillis() / 1000L;
        this.nation = null;

        this.id = DatabaseManager.createSettlement(this);

        this.roles = new ArrayList<>();
        this.addRole(DefaultSettlementRole.leader(this));
        this.addRole(DefaultSettlementRole.officer(this));
        this.addRole(DefaultSettlementRole.member(this));

        this.population = new HashMap<>();
        this.addMember(leader.getUniqueId(), this.getLeaderRole());

        this.territory = new LinkedHashSet<>();
        RealmManager.claimChunk(this, startingChunk); //This will call Settlement::addChunk
    }

    /*
    public Settlement(int id, String name, String displayName, String description, TextColor mapColor) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.roles = new ArrayList<>();
        this.population = new HashMap<>();
        this.territory = new HashSet<>();
    }*/

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        DatabaseManager.saveSettlement(this);
    }

    public String getDisplayNameAsString() {
        return this.displayName;
    }

    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(this.getDisplayNameAsString());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveSettlement(this);
    }

    public String getDescriptionAsString() {
        return this.description;
    }

    public Component getDescription() {
        return MiniMessage.miniMessage().deserialize(this.getDescriptionAsString());
    }

    public void setDescription(String description) {
        this.description = description;
        DatabaseManager.saveSettlement(this);
    }

    public TextColor getMapColor() {
        return this.mapColor;
    }

    public void setMapColor(TextColor color) {
        this.mapColor = color;
        DatabaseManager.saveSettlement(this);
    }

    public String getIconAsString() {
        return this.icon;
    }

    public ItemStack getIconAsMaterial() {
        if (this.getIconAsString() == null) {
            return ItemStack.of(Material.BARRIER);
        } else if (this.getIconAsString().startsWith("minecraft:")) {
            return ItemStack.of(Material.matchMaterial(this.getIconAsString()));
        } else if (this.getIconAsString().startsWith("jade:")) {
            return ItemStack.of(Material.BARRIER);
        } else return null;
    }

    public void setIcon(String icon) {
        this.icon = icon;
        DatabaseManager.saveSettlement(this);
    }

    public int getFood() {
        return this.food;
    }

    public void setFood(int food) {
        this.food = food;
        DatabaseManager.saveSettlement(this);
    }

    public boolean hasFood() {
        return this.hasFood(0);
    }

    public boolean hasFood(int threshold) {
        return this.getFood() >= threshold;
    }

    public boolean canHandleFoodDeposit(int amount) {
        return this.getFoodCapacity() - this.getFood() >= amount;
    }

    public void addFood(int amount) {
        this.setFood(this.getFood() + amount);
    }

    public int getFoodCapacity() {
        return this.foodCapacity;
    }

    public void calculateFoodCapacity() {
        //TODO: make this not dumb as shit
        this.foodCapacity = DEFAULT_MAX_FOOD;
    }

    public long getCreationTimeAsLong() { return this.creationTime; }

    public String getCreationTimeAsString() {
        ZonedDateTime dateTime = Instant.ofEpochMilli(this.creationTime).atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        return dateTime.format(formatter);
    }

    public Nation getNation() { return nation; }

    public void setNation(Nation nation) {
        this.nation = nation;
        DatabaseManager.saveSettlement(this);
    }

    public boolean isInNation() { return this.nation != null; }

    public boolean isMemberOfNation(Nation nation) { return nation.containsSettlement(this); }

    public List<SettlementRole> getRoles() {
        return this.roles;
    }

    public void setRoles(List<SettlementRole> roles) {
        this.roles = roles;
    }

    public SettlementRole getRoleForId(int id) {
        for (SettlementRole role : this.getRoles()) {
            if (role.getId() == id) {
                return role;
            }
        }
        return null;
    }

    public SettlementRole getLeaderRole() {
        for (SettlementRole role : this.getRoles()) {
            if (role.isLeader()) {
                return role;
            }
        }
        return null;
    }

    public SettlementRole getDefaultRole() {
        for (SettlementRole role : this.getRoles()) {
            if (role.isDefault()) {
                return role;
            }
        }
        return null;
    }

    public boolean setDefaultRole(SettlementRole defaultRole) {
        for (SettlementRole role : this.roles) {
            if (role.equals(defaultRole)) {
                // If the specified role exists in this settlement,
                // set the current default role to normal
                // and set the specified role as default.
                this.getDefaultRole().setToNormal();
                DatabaseManager.saveSettlementRole(this.getDefaultRole());

                role.setToDefault();
                DatabaseManager.saveSettlementRole(role);
                return true;
            }
        }
        return false;
    }

    public Map<UUID, SettlementRole> getPopulation() {
        return this.population;
    }

    public Set<UUID> getPopulationAsIDSet() { return this.population.keySet(); }

    // Used by DatabaseManager
    public void setPopulation(Map<UUID, SettlementRole> population) {
        this.population = population;
    }

    public boolean containsPlayer(UUID uuid) {
        return this.population.containsKey(uuid);
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    // Used by DatabaseManager
    public void setTerritory(Set<ChunkAnchor> territory) {
        this.territory = territory;
    }

    public void addRole(SettlementRole role) {
        this.roles.add(role);
        int roleID = DatabaseManager.createSettlementRole(role);
        role.setId(roleID);
    }

    public void removeRole(SettlementRole role) {
        if (this.roles.contains(role)) {
            this.roles.remove(role);
            DatabaseManager.deleteSettlementRole(role);

            for (UUID playerID : this.getPopulation().keySet()) {
                if (role.equals(this.getPopulation().get(playerID))) {
                    this.setPlayerRole(playerID, this.getDefaultRole());
                }
            }
        }
    }

    public void addMember(UUID playerID, SettlementRole role) {
        this.population.put(playerID, role);
        DatabaseManager.addPlayerToSettlement(playerID, this, role);
    }

    public void removeMember(UUID playerID) {
        if (this.containsPlayer(playerID)) {
            this.population.remove(playerID);
            DatabaseManager.removePlayerFromSettlement(playerID, this);
        }
    }

    // Called by RealmManager during claimChunk()
    public void addChunk(ChunkAnchor anchor) {
        this.territory.add(anchor);

        this.food -= JadeSettings.chunkCost;

        DatabaseManager.addChunkToSettlement(anchor, this);
    }

    // Called by RealmManager during unclaimChunk()
    public void removeChunk(ChunkAnchor anchor) {
        this.territory.remove(anchor);
        DatabaseManager.removeChunkFromSettlement(anchor, this);
    }

    public SettlementRole getRoleFromName(String roleName) {
        for (SettlementRole role : this.roles) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }

    public SettlementRole getRoleFromMember(UUID memberID) {
        return this.population.getOrDefault(memberID, null);
    }

    public SettlementRole getRoleForAuthority(int authority) {
        for (SettlementRole role : this.roles) {
            if (role.getAuthority() == authority) {
                return role;
            }
        }
        return null;
    }

    public SettlementRole getRoleAbove(SettlementRole role) {
        int referenceAuthority = role.getAuthority();
        for (int a = referenceAuthority + 1; a < SettlementRole.MAX_AUTHORITY; a++) {
            SettlementRole roleAtAuthority = this.getRoleForAuthority(a);
            if (roleAtAuthority != null) { return roleAtAuthority; }
        }
        return null;
    }

    public SettlementRole getRoleBelow(SettlementRole role) {
        int referenceAuthority = role.getAuthority();
        for (int a = referenceAuthority - 1; a >= SettlementRole.MIN_AUTHORITY; a--) {
            SettlementRole roleAtAuthority = this.getRoleForAuthority(a);
            if (roleAtAuthority != null) { return roleAtAuthority; }
        }
        return null;
    }

    public boolean canManageRole(SettlementRole reference, SettlementRole target) {
        return reference.getAuthority() > target.getAuthority();
    }

    public boolean setPlayerRole(UUID playerID, SettlementRole role) {
        if (!this.containsPlayer(playerID)) { return false; }

        this.getPopulation().remove(playerID);
        this.getPopulation().put(playerID, role);
        DatabaseManager.alterPlayerForSettlement(playerID, this, role);

        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Settlement) {
            Settlement other = (Settlement) object;
            return this.getId() == other.getId();
        } else return false;
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

        ItemStack item = this.getIconAsMaterial();
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text()
                .append(this.asTextComponent())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore()
                .addLine(Component.text().append(this.getDescription()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text(""))
                .addLine(Component.text().append(this.presentFoodAsComponent()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text().append(this.presentNationAsComponent()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text("Members: ", TextUtils.LIGHT_BRASS).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        List<Map.Entry<UUID, SettlementRole>> populationToDisplay = this.getPopulation().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(maxMembersToDisplay).toList();

        for (Map.Entry<UUID, SettlementRole> member : populationToDisplay) {
            loreBuilder.addLine(Component.text("— ", TextUtils.LIGHT_BRASS)
                    .append(member.getValue().getDisplayAsComponent())
                    .append(Component.space())
                    .append(Component.text(Bukkit.getPlayer(member.getKey()).getName(), TextUtils.LIGHT_ZORBA))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        if (this.getPopulation().size() > maxMembersToDisplay) {
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

    public Component presentFoodAsComponent() {
        return Component.text().content("Food: ").color(TextUtils.LIGHT_BRASS)
                .append(Component.text(this.getFood() + "/" + this.getFoodCapacity(), TextUtils.LIVINGMETAL))
                .build();
    }

    public Component presentNationAsComponent() {
        if (this.getNation() == null) {
            return Component.text().content("Nation: ").color(TextUtils.LIGHT_BRASS)
                    .append(Component.text("None", TextUtils.DARK_ZORBA))
                    .build();
        } else {
            return Component.text().content("Nation: ").color(TextUtils.LIGHT_BRASS)
                    .append(this.getNation().getDisplayName())
                    .build();
        }
    }

    @Override
    public String toString() {
        return "Settlement{id=" + this.getId() + ";name=" + this.getName() + "}";
    }
}

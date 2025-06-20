package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.realms.settings.BlockProtectionSetting;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.AbstractSetting;
import dev.jabberdrake.jade.utils.TextUtils;
import dev.jabberdrake.jade.utils.message.SettlementStrategy;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static dev.jabberdrake.jade.utils.JadeTextColor.*;

public class Settlement {

    private static final String DEFAULT_NAME_DECORATION = "<gold>";
    private static final String DEFAULT_DESC = "<green>Settlement of <gold>";
    private static final TextColor DEFAULT_MAP_COLOR = ZORBA;
    private static final int DEFAULT_MAX_FOOD = 1000;

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private NamespacedKey icon;
    private final World world;
    private int food;
    private int foodCapacity;
    private final long creationTime;
    private Nation nation;
    private List<SettlementRole> roles = new ArrayList<>();
    private Map<UUID, SettlementRole> population = new HashMap<>();
    private Set<ChunkAnchor> territory = new HashSet<>();
    private List<Area> areas = new ArrayList<>();
    private final Map<Class<? extends AbstractSetting<?>>, AbstractSetting<?>> settings = new HashMap<>();

    private final SettlementStrategy broadcastFormatter = new SettlementStrategy(this);

    // Used by DatabaseManager when composing runtime object from persistent data
    public Settlement(int id, String name, String displayName, String description, TextColor mapColor, NamespacedKey icon, World world, int food, long creationTime, Nation nation, String settings) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.icon = icon;
        this.world = world;
        this.food = food;
        this.creationTime = creationTime;
        this.nation = nation;

        deserializeSettings(settings);
        calculateFoodCapacity();
    }

    // Used by RealmManager when creating new settlement
    public Settlement(String name, Player leader, ChunkAnchor startingChunk) {
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.description = DEFAULT_DESC + leader.getName();
        this.mapColor = DEFAULT_MAP_COLOR;
        this.icon = NamespacedKey.minecraft("oak_planks");
        this.world = startingChunk.getWorld();
        this.food = 20;
        this.calculateFoodCapacity();
        this.creationTime = System.currentTimeMillis() / 1000L;
        this.nation = null;
        this.defaultSettings();

        this.id = DatabaseManager.createSettlement(this);

        this.addRole(DefaultSettlementRole.leader(this));
        this.addRole(DefaultSettlementRole.officer(this));
        this.addRole(DefaultSettlementRole.member(this));

        this.addMember(leader.getUniqueId(), this.getLeaderRole());

        RealmManager.claimChunk(this, startingChunk); //This will call Settlement::addChunk
    }

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

    public String getDisplayName() {
        return this.displayName;
    }

    public Component getDisplayNameAsComponent() {
        return TextUtils.deserialize(this.getDisplayName());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveSettlement(this);
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

    public String getDescription() {
        return this.description;
    }

    public Component getDescriptionAsComponent() {
        return TextUtils.deserialize(this.getDescription());
    }

    public void setDescription(String description) {
        this.description = description;
        DatabaseManager.saveSettlement(this);
    }

    public TextColor getMapColor() {
        if (this.isInNation()) {
            return this.getNation().getMapColor();
        } else return this.mapColor;
    }

    public void setMapColor(TextColor color) {
        this.mapColor = color;
        DatabaseManager.saveSettlement(this);
    }

    public String getIconAsString() {
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItemBase(this.icon);
    }

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
        DatabaseManager.saveSettlement(this);
    }

    public World getWorld() { return this.world; }

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

        // sanity check
        if (this.getFood() > this.getFoodCapacity()) {
            this.setFood(this.getFoodCapacity());
        }
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

    public void leaveNation() {
        this.nation = null;
    }

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

                role.setToDefault();
                return true;
            }
        }
        return false;
    }

    public boolean isUniqueRoleName(String potentialRoleName) {
        for (SettlementRole role: this.getRoles()) {
            if (role.getName().equalsIgnoreCase(potentialRoleName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isUniqueAreaName(String potentialAreaName) {
        for (Area area: this.getAreaList()) {
            if (area.getName().equalsIgnoreCase(potentialAreaName)) {
                return false;
            }
        }
        return true;
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

    public Component getMemberAsComponent(UUID uuid) {
        if (this.containsPlayer(uuid)) {
            return this.getRoleFromMember(uuid).getDisplayAsComponent()
                    .append(Component.text(" " + Bukkit.getOfflinePlayer(uuid).getName(), LIGHT_ZORBA));
        }
        return null;
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    public boolean isInTerritory(double x, double y, double z) {
        Location loc = new Location(this.getWorld(), x, y, z);
        ChunkAnchor target = new ChunkAnchor(loc.getChunk());
        for (ChunkAnchor chunk : this.getTerritory()) {
            if (target.equals(chunk)) return true;
        }
        return false;
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

    public UUID getLeaderUUID() {
        for (UUID memberID : this.getPopulation().keySet()) {
            SettlementRole role = this.getPopulation().get(memberID);
            if (role.isLeader()) return memberID;
        }
        return null;
    }

    public void addMember(UUID playerID, SettlementRole role) {
        this.population.put(playerID, role);
        DatabaseManager.addPlayerToSettlement(playerID, this, role);
    }

    public void removeMember(UUID playerID) {
        if (this.containsPlayer(playerID)) {
            this.population.remove(playerID);

            for (Area area : this.getAreaList()) {

                if (area.isHolder(playerID)) {
                    area.setHolder(getLeaderUUID());
                }

                if (area.isMember(playerID)) {
                    area.removeMember(playerID);
                }
            }

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
        for (int a = referenceAuthority + 1; a <= SettlementRole.MAX_AUTHORITY; a++) {
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

    public int getLowestUnassignedAuthority() {
        for (int authority = SettlementRole.MIN_AUTHORITY; authority <= SettlementRole.MAX_AUTHORITY; authority++) {
            if (this.getRoleForAuthority(authority) == null) return authority;
        }
        return -1;
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
                .addLine(Component.text().append(this.getDescriptionAsComponent())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text(""))
                .addLine(Component.text().append(this.presentFoodAsComponent())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text().append(this.presentNationAsComponent())
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build())
                .addLine(Component.text("Members: ", TextUtils.LIGHT_BRASS)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        List<Map.Entry<UUID, SettlementRole>> populationToDisplay = this.getPopulation().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(maxMembersToDisplay).toList();

        for (Map.Entry<UUID, SettlementRole> member : populationToDisplay) {
            loreBuilder.addLine(Component.text("— ", TextUtils.LIGHT_BRASS)
                    .append(member.getValue().getDisplayAsComponent())
                    .append(Component.space())
                    .append(Component.text(Bukkit.getOfflinePlayer(member.getKey()).getName(), TextUtils.LIGHT_ZORBA))
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
            loreBuilder.addLine(Component.empty());
            switch (addon) {
                case "INFO":
                    loreBuilder.addLine(Component.text()
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
                    .append(this.getNation().getDisplayNameAsComponent())
                    .build();
        }
    }

    public List<Area> getAreaList() {
        return this.areas;
    }

    public Area getAreaByName(String name) {
        for (Area area : this.getAreaList()) {
            if (area.getName().equalsIgnoreCase(name)) {
                return area;
            }
        }
        return null;
    }

    public boolean hasAreaInChunk(ChunkAnchor anchor) {
        for (Area area : this.getAreaList()) {
            if (area.intersectsChunk(anchor)) return true;
        }
        return false;
    }

    public Area getHighestPriorityAreaForLocation(Location location) {
        Area result = null;
        int lowestVolume = 999;
        for (Area area : this.getAreaList()) {
            if (area.containsPosition(location.getX(), location.getY(), location.getZ())) {
                int thisVolume = area.getVolume();
                if (thisVolume != -1 && thisVolume < lowestVolume) {
                    result = area;
                    lowestVolume = thisVolume;
                }
            }
        }
        return result;
    }

    // Used by RealmManager
    public void registerArea(Area area) {
        this.areas.add(area);
    }

    public void addArea(Area area) {
        this.areas.add(area);
        int areaID = DatabaseManager.createArea(area);
        area.setID(areaID);
        area.addMember(area.getHolderUniqueID());
    }

    public void removeArea(Area area) {
        if (this.areas.contains(area)) {
            this.areas.remove(area);
            DatabaseManager.deleteArea(area.getId());
        }
    }

    public void defaultSettings() {
        this.settings.put(BlockProtectionSetting.class, new BlockProtectionSetting());
        // more settings...
    }

    public void deserializeSettings(String serializedSettings) {
        String[] chunks = serializedSettings.split(";");
        // I'm blindingly trusting that the serialization was done well, which is bound to end in shit but oh well

        this.settings.put(BlockProtectionSetting.class, BlockProtectionSetting.deserialize(chunks[0]));
        // more settings...
    }

    public String serializeSettings() {
        String serialized = "";
        serialized += this.getSetting(BlockProtectionSetting.class).toString();
        serialized += ";";
        // more settings...

        return serialized;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractSetting<?>> T getSetting(Class<T> clazz) {
        return this.settings.get(clazz) == null ? null : (T) this.settings.get(clazz);
    }

    public Map<Class<? extends AbstractSetting<?>>, AbstractSetting<?>> getSettings() {
        return this.settings;
    }

    public <T extends AbstractSetting<?>> boolean cycleSettingValue(Class<T> clazz) {
        T setting = this.getSetting(clazz);
        Object value = setting.cycleValue();
        if (setting.setValue(value)) {
            this.broadcast("Changed value of setting <highlight>" + setting.displayName() + "</highlight> to <light_amethyst>" + value.toString());
            DatabaseManager.saveSettlement(this);
            return true;
        } else return false;
    }

    public void broadcast(String message, TagResolver... resolvers) {
        for (UUID memberID : this.getPopulation().keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberID);
            if (offlinePlayer.isOnline()) {
                ((Player) offlinePlayer).sendMessage(this.broadcastFormatter.process(message, resolvers));
            }
        }
    }

    public void tell(Player player, String message, TagResolver... resolvers) {
        // This method assumes that:
        //  - player matches a valid known player;
        //  - player is online;
        player.sendMessage(this.broadcastFormatter.process(message, resolvers));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Settlement) {
            Settlement other = (Settlement) object;
            return this.getId() == other.getId();
        } else return false;
    }

    @Override
    public String toString() {
        return "Settlement{id=" + this.getId() + ";name=" + this.getName() + "}";
    }
}

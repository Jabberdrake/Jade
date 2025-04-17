package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Settlement {

    private static final String DEFAULT_NAME_DECORATION = "<gold><bold>";
    private static final String DEFAULT_DESC = "<green>Settlement of <gold>";
    private static final TextColor DEFAULT_MAP_COLOR = TextUtils.ZORBA;

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private String icon;
    private long creationTime;
    private Nation nation;
    private List<SettlementRole> roles;
    private Map<UUID, SettlementRole> population;
    private Set<ChunkAnchor> territory;

    // Used by DatabaseManager when composing runtime object from persistent data
    public Settlement(int id, String name, String displayName, String description, TextColor mapColor, String icon, long creationTime, Nation nation) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.icon = icon;
        this.creationTime = creationTime;
        this.nation = nation;
        this.roles = new ArrayList<>();
        this.population = new HashMap<>();
        this.territory = new HashSet<>();
    }

    // Used by RealmManager when creating new settlement
    public Settlement(String name, Player leader, ChunkAnchor startingChunk) {
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.description = DEFAULT_DESC + leader.getName();
        this.mapColor = DEFAULT_MAP_COLOR;
        this.nation = null;
        this.icon = "minecraft:oak_planks";
        this.creationTime = System.currentTimeMillis() / 1000L;

        this.id = RealmManager.incrementSettlementCount(); //TODO

        this.roles = new ArrayList<>();
        roles.add(DefaultSettlementRole.leader(this));
        roles.add(DefaultSettlementRole.officer(this));
        roles.add(DefaultSettlementRole.peasant(this));

        this.population = new HashMap<>();
        population.put(leader.getUniqueId(), roles.getFirst());

        this.territory = new LinkedHashSet<>();
        territory.add(startingChunk);
    }

    public Settlement(int id, String name, String displayName, String description, TextColor mapColor) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.roles = new ArrayList<>();
        this.population = new HashMap<>();
        this.territory = new HashSet<>();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayNameAsString() {
        return this.displayName;
    }

    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(this.getDisplayNameAsString());
    }

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

    public TextColor getMapColor() {
        return this.mapColor;
    }

    public void setMapColor(TextColor color) { this.mapColor = color; }

    public String getIconAsString() { return this.icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public long getCreationTimeAsLong() { return this.creationTime; }

    public String getCreationTimeAsString() {
        ZonedDateTime dateTime = Instant.ofEpochMilli(this.creationTime).atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        return dateTime.format(formatter);
    }

    public Nation getNation() { return nation; }

    public void setNation(Nation nation) { this.nation = nation; }

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

    public Map<UUID, SettlementRole> getPopulation() {
        return this.population;
    }

    public Set<UUID> getPopulationAsIDSet() { return this.population.keySet(); }

    public void setPopulation(Map<UUID, SettlementRole> population) {
        this.population = population;
    }

    public boolean containsPlayer(UUID uuid) {
        return this.population.containsKey(uuid);
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    public void setTerritory(Set<ChunkAnchor> territory) {
        this.territory = territory;
    }

    public void addRole(SettlementRole role) {
        this.roles.add(role);
    }

    public void addMember(UUID playerID, SettlementRole role) {
        this.population.put(playerID, role);
    }

    public void removeMember(UUID playerID) {
        if (this.containsPlayer(playerID)) {
            this.population.remove(playerID);
        }
    }

    public void addTerritory(ChunkAnchor anchor) {
        this.territory.add(anchor);
    }

    public void removeTerritory(ChunkAnchor anchor) {
        this.territory.remove(anchor);
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

    public boolean setPlayerTitle(UUID playerID, SettlementRole role) {
        if (!this.containsPlayer(playerID)) { return false; }

        this.getPopulation().remove(playerID);
        this.getPopulation().put(playerID, role);

        RealmManager.markAsDirty(this);

        return true;
    }

    /*
    public static Settlement load(FileConfiguration data, String root) {
        // Obtaining basic attributes
        int stmID = data.getInt(root + ".id");
        String stmName = data.getString(root + ".name");
        String stmDisplayName = data.getString(root + ".displayName");
        String stmDescription = data.getString(root + ".description");
        String stmMapColorAsString = data.getString(root + ".mapColor");
        assert stmMapColorAsString != null;
        TextColor stmMapColor = TextColor.fromHexString(stmMapColorAsString);

        Settlement stm = new Settlement(stmID, stmName, stmDisplayName, stmDescription, stmMapColor);

        // Parsing nations
        int nationID = data.getInt(root + ".nation");
        if (nationID > 0) {
            Nation nation = RealmManager.getNation(nationID);
            if (nation == null) {
                Jade.getPlugin(Jade.class).getLogger().warning("[Settlement::load] Invalid nation ID detected while loading settlement ID=" + stmID + " (" + stmName + ")!");
                stm.setNation(null);
            } else stm.setNation(nation);
        } else stm.setNation(null);


        // Building title list
        List<String> readTitles = data.getStringList(root + ".titles");
        for (String readTitle : readTitles) {
            stm.addTitle(SettlementRole.fromString(readTitle, stm));
        }

        // Build population map
        List<String> readPopulation = data.getStringList(root + ".population");
        for (String readCitizen : readPopulation) {
            String[] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            SettlementRole fetchedTitle = stm.getRoleFromName(parts[1]);

            stm.addMember(convertedUUID, fetchedTitle);
        }

        // Build chunk anchor set for Settlement
        List<String> readTerritory = data.getStringList(root + ".territory");
        for (String readChunk: readTerritory) {
            String[] parts = readChunk.split(";");
            int readX = Integer.parseInt(parts[0]);
            int readZ = Integer.parseInt(parts[1]);
            ChunkAnchor anchor = new ChunkAnchor(readX, readZ);
            stm.addTerritory(anchor);
            if (RealmManager.getTerritoryMap().containsKey(anchor)) {
                Settlement owner = RealmManager.getTerritoryMap().get(anchor);
                if (!owner.equals(stm)) {
                    Jade.getPlugin(Jade.class).getLogger().warning("[Settlement::load] Chunk at [x=" + readX + ", z=" + readZ + "] claimed by both " + owner.getName() + " and " + stm.getName() + "!");
                }
            } else {
                RealmManager.getTerritoryMap().put(anchor, stm);
            }
        }

        return stm;
    }

    public static void store(Settlement settlement, FileConfiguration data, String root) {
        // Storing basic attributes
        data.set(root + ".id", settlement.getId());
        data.set(root + ".name", settlement.getName());
        data.set(root + ".displayName", settlement.getDisplayNameAsString());
        data.set(root + ".description", settlement.getDescriptionAsString());
        data.set(root + ".mapColor", settlement.getMapColor().asHexString());

        // Storing nation ID
        data.set(root + ".nation", settlement.isInNation() ? -1 : settlement.getNation().getId());

        // Storing all internal titles
        List<String> preparedTitleStrings = new ArrayList<>();
        for (SettlementRole title : settlement.getTitles()) {
            preparedTitleStrings.add(title.serialize());
        }
        data.set(root + ".titles", preparedTitleStrings);

        // Storing population map
        List<String> preparedPopulationStrings = new ArrayList<>();
        for (UUID uuid : settlement.getPopulation().keySet()) {
            preparedPopulationStrings.add(uuid.toString() + ";" + settlement.getPopulation().get(uuid).getName());
        }
        data.set(root + ".population", preparedPopulationStrings);

        // Storing territory map
        List<String> preparedTerritoryStrings = new ArrayList<>();
        for (ChunkAnchor chunk : settlement.getTerritory()) {
            preparedTerritoryStrings.add(chunk.getX() + ";" + chunk.getZ());
        }
        data.set(root + ".territory", preparedTerritoryStrings);
    }*/

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

package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Settlement {

    private static final String DEFAULT_NAME_DECORATION = "<gold><bold> ";
    private static final String DEFAULT_DESC = "<green>Settlement of <gold>";
    private static final TextColor DEFAULT_COLOR = TextUtils.ZORBA;

    private int id;
    private String name;
    private String displayName;
    private String description;
    private TextColor mapColor;
    private Nation nation;
    private List<CharterTitle> titles;
    private Map<UUID, CharterTitle> population;
    private Set<ChunkAnchor> territory;

    public Settlement(int id, String name, String displayName, String description, TextColor mapColor, Nation nation, List<CharterTitle> titles, Map<UUID, CharterTitle> population, Set<ChunkAnchor> territory) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.nation = nation;
        this.titles = titles;
        this.population = population;
        this.territory = territory;
    }

    public Settlement(String name, Player leader, ChunkAnchor startingChunk) {
        this.id = RealmManager.incrementSettlementCount();
        this.name = name;
        this.displayName = DEFAULT_NAME_DECORATION + name;
        this.description = DEFAULT_DESC + leader.getName();
        this.mapColor = DEFAULT_COLOR;
        this.nation = null;

        this.titles = new ArrayList<>();
        titles.add(DefaultCharterTitle.leader(this));
        titles.add(DefaultCharterTitle.officer(this));
        titles.add(DefaultCharterTitle.peasant(this));

        this.population = new HashMap<>();
        population.put(leader.getUniqueId(), titles.getFirst());

        this.territory = new LinkedHashSet<>();
        territory.add(startingChunk);
    }

    public Settlement(int id, String name, String displayName, String description, TextColor mapColor) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.mapColor = mapColor;
        this.titles = new ArrayList<>();
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

    public Nation getNation() { return nation; }

    public void setNation(Nation nation) { this.nation = nation; }

    public boolean isInNation() { return this.nation != null; }

    public boolean isMemberOfNation(Nation nation) { return nation.containsSettlement(this); }

    public List<CharterTitle> getTitles() {
        return this.titles;
    }

    public CharterTitle getDefaultTitle() {
        for (CharterTitle title : this.getTitles()) {
            if (title.isLeader()) {
                return title;
            }
        }
        return null;
    }

    public boolean setDefaultTitle(CharterTitle defaultTitle) {
        for (CharterTitle title : this.titles) {
            if (title.equals(defaultTitle)) {
                // If the specified title exists in this settlement,
                // set the current default title to normal
                // and set the specified title as default.
                this.getDefaultTitle().setToNormal();
                title.setToDefault();
                return true;
            }
        }
        return false;
    }

    public Map<UUID, CharterTitle> getPopulation() {
        return this.population;
    }

    public Set<UUID> getPopulationAsIDSet() { return this.population.keySet(); }

    public boolean containsPlayer(UUID uuid) {
        return this.population.containsKey(uuid);
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    public void addTitle(CharterTitle title) {
        this.titles.add(title);
    }

    public void addMember(UUID playerID, CharterTitle title) {
        this.population.put(playerID, title);
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

    public CharterTitle getTitleFromName(String titleName) {
        for (CharterTitle title : this.titles) {
            if (title.getName().equals(titleName)) {
                return title;
            }
        }
        return null;
    }

    public CharterTitle getTitleFromMember(UUID memberID) {
        return this.population.getOrDefault(memberID, null);
    }

    public CharterTitle getTitleForAuthority(int authority) {
        for (CharterTitle title : this.titles) {
            if (title.getAuthority() == authority) {
                return title;
            }
        }
        return null;
    }

    public CharterTitle getTitleAbove(CharterTitle title) {
        int referenceAuthority = title.getAuthority();
        for (int a = referenceAuthority + 1; a < CharterTitle.MAX_AUTHORITY; a++) {
            CharterTitle titleAtAuthority = this.getTitleForAuthority(a);
            if (titleAtAuthority != null) { return titleAtAuthority; }
        }
        return null;
    }

    public CharterTitle getTitleBelow(CharterTitle title) {
        int referenceAuthority = title.getAuthority();
        for (int a = referenceAuthority - 1; a >= CharterTitle.MIN_AUTHORITY; a--) {
            CharterTitle titleAtAuthority = this.getTitleForAuthority(a);
            if (titleAtAuthority != null) { return titleAtAuthority; }
        }
        return null;
    }

    public boolean canManageTitle(CharterTitle reference, CharterTitle target) {
        return reference.getAuthority() > target.getAuthority();
    }

    public boolean setPlayerTitle(UUID playerID, CharterTitle title) {
        if (!this.containsPlayer(playerID)) { return false; }

        this.getPopulation().remove(playerID);
        this.getPopulation().put(playerID, title);

        RealmManager.markAsDirty(this);

        return true;
    }

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
            stm.addTitle(CharterTitle.fromString(readTitle, stm));
        }

        // Build population map
        List<String> readPopulation = data.getStringList(root + ".population");
        for (String readCitizen : readPopulation) {
            String[] parts = readCitizen.split(";");
            UUID convertedUUID = UUID.fromString(parts[0]);
            CharterTitle fetchedTitle = stm.getTitleFromName(parts[1]);

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
        for (CharterTitle title : settlement.getTitles()) {
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

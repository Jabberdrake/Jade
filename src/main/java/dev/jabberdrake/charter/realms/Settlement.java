package dev.jabberdrake.charter.realms;

import java.util.*;

public class Settlement {

    private static final String DEFAULT_DESC = "Settlement of ";

    private int id;
    private String name;
    private String style;
    private String description;
    private List<CharterTitle> titles;
    private CharterTitle defaultTitle;
    private Map<UUID, CharterTitle> population;
    private Set<ChunkAnchor> territory;

    public Settlement(int id, String name, String style, String description, List<CharterTitle> titles, CharterTitle defaultTitle, Map<UUID, CharterTitle> population, Set<ChunkAnchor> territory) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.description = description;
        this.titles = titles;
        this.defaultTitle = defaultTitle;
        this.population = population;
        this.territory = territory;
    }

    public Settlement(String name, List<CharterTitle> titles, CharterTitle defaultTitle, Map<UUID, CharterTitle> population, Set<ChunkAnchor> territory) {
        this.id = RealmManager.incrementSettlementCount();
        this.name = name;
        this.style = name;
        this.description = DEFAULT_DESC;
        this.titles = titles;
        this.defaultTitle = defaultTitle;
        this.population = population;
        this.territory = territory;
    }

    public Settlement(int id, String name, String style, String description) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.description = description;
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

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CharterTitle> getTitles() {
        return this.titles;
    }

    public CharterTitle getDefaultTitle() {
        return this.defaultTitle;
    }

    public int setDefaultTitle(CharterTitle defaultTitle) {
        for (CharterTitle title : this.titles) {
            if (title.equals(defaultTitle)) {
                this.defaultTitle = defaultTitle;
                return 0;
            }
        }
        return -1;
    }

    public Map<UUID, CharterTitle> getPopulation() {
        return this.population;
    }

    public Set<ChunkAnchor> getTerritory() {
        return this.territory;
    }

    public void addTitle(CharterTitle title) {
        this.titles.add(title);
    }

    public void addCitizen(UUID playerID, CharterTitle title) {
        this.population.put(playerID, title);
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof Settlement) {
            Settlement other = (Settlement) object;
            return this.id == other.id
                    && this.name.equals(other.name)
                    && this.getStyle().equals(other.getStyle())
                    && this.getDescription().equals(other.getDescription())
                    && this.getTitles().equals(other.getTitles())
                    && this.getDefaultTitle().equals(other.getDefaultTitle())
                    && this.getPopulation().equals(other.getPopulation());
        } else return false;
    }

    @Override
    public String toString() {
        return "Settlement{id=" + this.getId() + ";name=" + this.getName() + "}";
    }
}

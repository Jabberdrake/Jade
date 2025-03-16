package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.realms.management.PlayerTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Settlement {

    private static final String DEFAULT_DESC = "Settlement of ";

    private int id;
    private String name;
    private String decoratedName;
    private String description;
    private List<PlayerTitle> titles;
    private Map<UUID, PlayerTitle> population;

    public Settlement(int id, String name, String decoratedName, String description, List<PlayerTitle> titles, Map<UUID, PlayerTitle> population) {
        this.id = id;
        this.name = name;
        this.decoratedName = decoratedName;
        this.description = description;
        this.titles = titles;
        this.population = population;
    }

    public Settlement(String name, List<PlayerTitle> titles, Map<UUID, PlayerTitle> population) {
        this.id = RealmManager.incrementSettlementCount();
        this.name = name;
        this.decoratedName = name;
        this.description = DEFAULT_DESC;
        this.titles = titles;
        this.population = population;
    }

    public Settlement(int id, String name, String decoratedName, String description) {
        this.id = id;
        this.name = name;
        this.decoratedName = decoratedName;
        this.description = description;
        this.titles = new ArrayList<>();
        this.population = new HashMap<>();
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

    public String getDecoratedName() {
        return this.decoratedName;
    }

    public void setDecoratedName(String decoratedName) {
        this.decoratedName = decoratedName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PlayerTitle> getTitles() {
        return this.titles;
    }

    public Map<UUID, PlayerTitle> getPopulation() {
        return this.population;
    }

    public void addTitle(PlayerTitle title) {
        this.titles.add(title);
    }

    public void addCitizen(UUID playerID, PlayerTitle title) {
        this.population.put(playerID, title);
    }

    public PlayerTitle getTitleFromName(String name) {
        for (PlayerTitle title : this.titles) {
            if (title.getName().equals(name)) {
                return title;
            }
        }
        return null;
    }
}

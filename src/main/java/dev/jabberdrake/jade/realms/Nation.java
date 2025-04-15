package dev.jabberdrake.jade.realms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Nation {

    private int id;
    private String name;
    private String title;
    private TextColor mapColor;
    private List<Settlement> settlements;
    private Settlement capital;

    public Nation(int id, String name, String title, TextColor mapColor, List<Settlement> settlements, Settlement capital) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.mapColor = mapColor;
        this.settlements = settlements;
        this.capital = capital;
    }

    public Nation(String name, TextColor mapColor, Settlement capital) {
        this.id = RealmManager.incrementNationCount();
        this.name = name;
        this.title = "<gold>" + name;
        this.mapColor = mapColor;
        this.settlements = new ArrayList<>(List.of(capital));
        this.capital = capital;
    }

    public int getId() { return this.id; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getTitleAsString() { return this.title; }

    public Component getTitleAsComponent() { return MiniMessage.miniMessage().deserialize(this.title); }

    public TextColor getMapColor() { return this.mapColor; }

    public void setMapColor(TextColor mapColor) { this.mapColor = mapColor; }

    public List<Settlement> getSettlements() { return this.settlements; }

    public void addSettlement(Settlement settlement) {
        if (!this.settlements.contains(settlement)) {
            this.settlements.add(settlement);
        }
    }

    public void removeSettlement(Settlement settlement) {
        this.settlements.remove(settlement);
    }

    public boolean containsSettlement(Settlement settlement) {
        return this.settlements.contains(settlement);
    }

    public Settlement getCapital() { return this.capital; }

    public void setCapital(Settlement capital) { this.capital = capital; }

    public static Nation load(FileConfiguration data, String root) {
        return null;
    }

    public static void store(Nation nation, FileConfiguration data, String root) {

    }


}

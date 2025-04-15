package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JadeProfile {

    private UUID uuid;
    private String roleplayName;
    private List<JadeTitle> availableTitles;
    private JadeTitle titleInUse;

    public JadeProfile(UUID uuid, String roleplayName, List<JadeTitle> availableTitles, JadeTitle titleInUse) {
        this.uuid = uuid;
        this.roleplayName = roleplayName;
        this.availableTitles = availableTitles;
        this.titleInUse = titleInUse;
    }

    public JadeProfile(UUID uuid) {
        this.uuid = uuid;
        this.roleplayName = Bukkit.getPlayer(uuid).getName();
        this.availableTitles = List.of(DefaultJadeTitle.PEASANT);
        this.titleInUse = availableTitles.getFirst();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getRoleplayName() {
        return this.roleplayName;
    }

    public void setRoleplayName(String roleplayName) {
        this.roleplayName = roleplayName;
    }

    public void addTitle(JadeTitle title) {
        this.availableTitles.add(title);
    }

    public void removeTitle(JadeTitle title) {
        if (this.availableTitles.contains(title)) {
            this.availableTitles.remove(title);
            if (this.titleInUse.equals(title)) {
                this.titleInUse = this.availableTitles.getFirst();
            }
        }
    }

    public List<JadeTitle> getAvailableTitles() {
        return this.availableTitles;
    }

    public JadeTitle getTitleFromName(String titleName) {
        for (JadeTitle title : this.availableTitles) {
            if (title.getName().equalsIgnoreCase(titleName)) {
                return title;
            }
        }
        return null;
    }

    public JadeTitle getTitleInUse() { return this.titleInUse; }

    public boolean setTitle(JadeTitle titleToUse) {
        for (JadeTitle title : this.availableTitles) {
            if (title.equals(titleToUse)) {
                this.titleInUse = titleToUse;
                return true;
            }
        }
        return false;
    }

    public boolean canUseTitle(JadeTitle title) { return this.availableTitles.contains(title); }

}

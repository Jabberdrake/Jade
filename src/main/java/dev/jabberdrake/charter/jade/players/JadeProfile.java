package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.charter.jade.titles.JadeTitle;
import dev.jabberdrake.charter.jade.titles.TitleManager;
import dev.jabberdrake.charter.realms.CharterTitle;
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

    public static JadeProfile load(UUID playerID, FileConfiguration data, String root) {
        // Obtain matching player's unique ID
        String uuidAsString = data.getString(root + ".uuid");
        UUID uuid = UUID.fromString(uuidAsString);

        // Obtain roleplay name
        String roleplayName = data.getString(root + ".roleplayName");

        // Obtain available Jade titles
        List<JadeTitle> titles = new ArrayList<>();
        List<String> titlesAsStrings = data.getStringList(root + ".availableTitles");
        for (String titleAsString : titlesAsStrings) {
            JadeTitle title = TitleManager.fetchTitle(titleAsString);
            if (title != null && title.isAvailableTo(playerID)) { titles.add(title); }
        }

        // Obtain title in use
        JadeTitle titleInUse = null;
        String titleInUseAsString = data.getString(root + ".titleInUse");
        for (JadeTitle auxTitle : titles) {
            if (auxTitle.getName().equals(titleInUseAsString)) {
                titleInUse = auxTitle;
            }
        }
        if (titleInUse == null) { titleInUse = titles.getFirst(); }

        return new JadeProfile(uuid, roleplayName, titles, titleInUse);
    }

    public static void store(JadeProfile profile, FileConfiguration data, String root) {
        // Storing basic attributes
        data.set(root + ".uuid", profile.getUUID().toString());
        data.set(root + ".roleplayName", profile.getRoleplayName());

        // Storing available titles
        List<String> titlesAsStrings = new ArrayList<>();
        for (JadeTitle availableTitle : profile.getAvailableTitles()) {
            titlesAsStrings.add(availableTitle.serialize());
        }
        data.set(root + ".availableTitles", titlesAsStrings);

        // Storing title in use
        data.set(root + ".titleInUse", profile.getTitleInUse().serialize());
    }

}

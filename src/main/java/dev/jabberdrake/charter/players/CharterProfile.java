package dev.jabberdrake.charter.players;

import dev.jabberdrake.charter.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.charter.jade.titles.JadeTitle;
import dev.jabberdrake.charter.jade.titles.TitleManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CharterProfile {

    private UUID uuid;
    private String roleplayName;
    private List<JadeTitle> availableTitles;

    public CharterProfile(UUID uuid, String roleplayName, List<JadeTitle> availableTitles) {
        this.uuid = uuid;
        this.roleplayName = roleplayName;
        this.availableTitles = availableTitles;
    }

    public CharterProfile(UUID uuid) {
        this.uuid = uuid;
        this.roleplayName = "";
        this.availableTitles = List.of(DefaultJadeTitle.PEASANT);
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
        this.availableTitles.remove(title);
    }

    public List<JadeTitle> getAvailableTitles() {
        return this.availableTitles;
    }

    public static CharterProfile load(FileConfiguration data, String root) {
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
        }

        return new CharterProfile(uuid, roleplayName, titles);
    }

    public static void store(CharterProfile profile, FileConfiguration data, String root) {
        // Storing simple variables
        data.set(root + ".uuid", profile.getUUID().toString());
        data.set(root + ".roleplayName", profile.getRoleplayName());

        // Storing available titles
        List<String> titlesAsStrings = new ArrayList<>();
        for (JadeTitle availableTitle : profile.getAvailableTitles()) {
            titlesAsStrings.add(availableTitle.serialize());
        }
        data.set(root + ".availableTitles", titlesAsStrings);
    }

}

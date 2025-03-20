package dev.jabberdrake.charter.jade.titles;

import dev.jabberdrake.charter.Charter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JadeTitle extends NamedTitle {

    private TextColor senderColor;
    private UUID owner;
    private List<UUID> users;

    public JadeTitle(String name, String title, UUID owner) {
        super(name, title);

        this.senderColor = NamedTextColor.WHITE;
        this.owner = owner;
        this.users = new ArrayList<>();
        users.add(owner);
    }

    public TextColor getSenderColor() {
        return this.senderColor;
    }

    public boolean setSenderColor(TextColor senderColor) {
        if (this.senderColor != senderColor) {
            this.senderColor = senderColor;
            return true;
        } else {
            return false;
        }
    }

    public UUID getOwner() {
        return this.owner;
    }

    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }

    public List<UUID> getUsers() {
        return this.users;
    }

    public boolean addUser(UUID uuid) {
        if (this.users.contains(uuid)) {
            return false;
        } else {
            this.users.add(uuid);
            return true;
        }
    }

    public boolean removeUser(UUID uuid) {
        if (this.users.contains(uuid)) {
            this.users.remove(uuid);
            return true;
        } else {
            return false;
        }
    }

    public void makeUniversal() {
        // If the users list is null, then everyone can use it.
        this.users = null;
    }

    public boolean isUniversal() {
        // If the users list is null, then everyone can use it.
        return this.users == null;
    }

    public boolean isAvailableTo(UUID user) {
        if (this.users == null) {
            return true;
        } else return this.users.contains(user);
    }

    public String serialize() {
        return this.getName() + "@" + this.getOwner().toString();
    }

    public static String serializeTitle(Component title) {
        return MiniMessage.miniMessage().serialize(title);
    }

    public static JadeTitle load(FileConfiguration data, String root) {
        // Obtaining NamedTitle variables
        String titleName = data.getString(root + ".name");
        String titleTitle = data.getString(root + ".title");

        // Obtaining sender color
        String colorAsString = data.getString(root + ".senderColor");
        TextColor titleSenderColor = TextColor.fromHexString(colorAsString);

        // Obtaining owner
        String ownerAsString = data.getString(root + ".owner");
        UUID titleOwner = UUID.fromString(ownerAsString);

        JadeTitle title = new JadeTitle(titleName, titleTitle, titleOwner);
        title.setSenderColor(titleSenderColor);

        // Obtaining users
        boolean isUniversal = data.getBoolean(root + ".universal");
        if (!isUniversal) {
            List<String> usersAsStrings = data.getStringList(root + ".users");
            for (String userAsString : usersAsStrings) {
                title.addUser(UUID.fromString(userAsString));
            }
        } else {
            title.makeUniversal();
        }

        return title;
    }

    public static void store(JadeTitle title, FileConfiguration data, String root) {
        // Storing NamedTitle attributes
        data.set(root + ".name", title.getName());
        data.set(root + ".title", title.getTitleAsString());

        // Storing sender color and owner
        data.set(root + ".senderColor", title.getSenderColor().asHexString());
        data.set(root + ".owner", title.getOwner().toString());

        // Storing users
        if (title.isUniversal()) {
            data.set(root + ".universal", true);
        } else {
            data.set(root + ".universal", false);

            List<String> usersAsStrings = new ArrayList<>();
            for (UUID user : title.getUsers()) {
                usersAsStrings.add(user.toString());
            }
            data.set(root + ".users", usersAsStrings);
        }
    }

    @Override
    public String toString() {
        return "JadeTitle{name=" + this.getName() + ", owner=" + this.getOwner() + "}";
    }
}

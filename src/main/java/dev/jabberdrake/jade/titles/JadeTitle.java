package dev.jabberdrake.jade.titles;

import dev.jabberdrake.jade.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JadeTitle {

    private int id;
    private String name;
    private String displayName;
    private UUID owner;
    private TextColor senderColor;
    private String icon;
    private List<UUID> users;

    // Used by DatabaseManager when composing runtime object from persistent data
    public JadeTitle(int id, String name, String displayName, UUID owner, TextColor senderColor, String iconAsString) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.owner = owner;
        this.senderColor = senderColor;
        this.icon = iconAsString;
        if (this.owner != null) {
            this.users = new ArrayList<>();
            this.users.add(owner);
        } else {
            this.users = null;
        }
    }

    // Used by TitleManager when creating new title
    public JadeTitle(String name, String displayName, UUID owner, TextColor senderColor, String iconAsString) {
        this.name = name;
        this.displayName = displayName;
        this.owner = owner;
        this.senderColor = senderColor;
        this.icon = iconAsString;

        this.id = DatabaseManager.createTitle(this);

        if (this.owner != null) {
            this.users = new ArrayList<>();
            this.addUser(owner);
        } else {
            this.users = null;
        }
    }

    public int getId() { return this.id; }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        DatabaseManager.saveTitle(this);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Component getDisplayAsComponent() {
        return MiniMessage.miniMessage().deserialize(this.displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveTitle(this);
    }

    public UUID getOwner() {
        return this.owner;
    }

    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }

    public TextColor getSenderColor() {
        return this.senderColor;
    }

    public boolean setSenderColor(TextColor senderColor) {
        if (this.senderColor != senderColor) {
            this.senderColor = senderColor;
            DatabaseManager.saveTitle(this);
            return true;
        } else {
            return false;
        }
    }

    public String getIconAsString() {
        return this.icon;
    }

    public boolean setIcon(String iconAsString) {
        if (!this.icon.equals(iconAsString)) {
            this.icon = iconAsString;
            DatabaseManager.saveTitle(this);
            return true;
        } else {
            return false;
        }
    }

    public List<UUID> getUserList() {
        return this.users;
    }

    public boolean addUser(UUID uuid) {
        if (this.users.contains(uuid)) {
            return false;
        } else {
            this.users.add(uuid);
            DatabaseManager.addPlayerToTitle(this, uuid);
            return true;
        }
    }

    public boolean removeUser(UUID uuid) {
        if (this.users.contains(uuid)) {
            this.users.remove(uuid);
            DatabaseManager.removePlayerFromTitle(this, uuid);
            return true;
        } else {
            return false;
        }
    }

    public void makeUniversal() {
        // If the users list is null, then everyone can use it.
        this.users = null;
        this.owner = null;
        DatabaseManager.makeTitleUniversal(this);
    }

    public boolean isUniversal() {
        // If the users list is null, then everyone can use it.
        return this.owner == null;
    }

    public boolean isAvailableTo(UUID user) {
        if (this.owner == null) {
            return true;
        } else return this.users.contains(user);
    }

    public String serialize() {
        return this.getName() + "@" + this.getOwner().toString();
    }

    public static String serializeDisplay(Component display) {
        return MiniMessage.miniMessage().serialize(display);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof JadeTitle) {
            JadeTitle other = (JadeTitle) object;
            return this.getName().equals(other.getName());
        } else return false;
    }

    @Override
    public String toString() {
        return "JadeTitle{name=" + this.getName() + ", owner=" + this.getOwner() + "}";
    }
}

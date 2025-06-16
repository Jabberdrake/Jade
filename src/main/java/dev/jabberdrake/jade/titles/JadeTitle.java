package dev.jabberdrake.jade.titles;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class JadeTitle {

    private int id;
    private String name;
    private String displayName;
    private UUID owner;
    private TextColor senderColor;
    private NamespacedKey icon;
    private List<UUID> users;

    // Used by DatabaseManager when composing runtime object from persistent data
    public JadeTitle(int id, String name, String displayName, UUID owner, TextColor senderColor, NamespacedKey icon) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.owner = owner;
        this.senderColor = senderColor;
        this.icon = icon;
        if (this.owner != null) {
            this.users = new ArrayList<>();
            this.users.add(owner);
        } else {
            this.users = null;
        }
    }

    // Used by TitleManager when creating new title
    public JadeTitle(String name, String displayName, UUID owner, TextColor senderColor, NamespacedKey icon) {
        this.name = name;
        this.displayName = displayName;
        this.owner = owner;
        this.senderColor = senderColor;
        this.icon = icon;

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
        return TextUtils.deserialize(this.displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        DatabaseManager.saveTitle(this);
    }

    public Component asTextComponent() {
        return text()
                .append(this.getDisplayAsComponent())
                .append(text(" (" + this.getName() + ")", TextUtils.LIGHT_ZORBA))
                .build();
    }

    public String asDisplayString() {
        return this.getDisplayName() + "<normal> <light_zorba>(" + this.getName() + ")</light_zorba>";
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
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItem(this.icon);
    }

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
        DatabaseManager.saveTitle(this);
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

    public ItemStack asDisplayItem() {
        return this.asDisplayItem(null);
    }

    public ItemStack asDisplayItem(String addon) {

        ItemStack item = this.getIconAsItem();
        item.setData(DataComponentTypes.CUSTOM_NAME, text()
                .append(this.asTextComponent())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore();

        if (this.isUniversal()) {
            loreBuilder.addLine(text("Universal Title", JadeTextColor.LIVINGMETAL)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        } else {
            loreBuilder.addLine(text()
                    .content("Owned by ").color(JadeTextColor.ZORBA)
                    .append(text(Bukkit.getOfflinePlayer(this.getOwner()).getName(), JadeTextColor.LIGHT_ZORBA))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build()
            );
        }

        if (addon == null) {
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
        } else {
            loreBuilder.addLine(Component.empty());
            switch (addon) {
                case "USE":
                    loreBuilder.addLine(text()
                            .append(text("Left Click", NamedTextColor.GREEN))
                            .append(text(" to use this title", NamedTextColor.DARK_GREEN))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .build());
                default:
                    item.setData(DataComponentTypes.LORE, loreBuilder.build());
            }
        }

        return item;
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

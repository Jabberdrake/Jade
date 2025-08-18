package dev.jabberdrake.jade.building;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.PositionUtils;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Warp {

    private static final String DEFAULT_DESC = "<light_zorba>The best place this side of the Yaruga!";

    private String name;
    private String description;
    private Location location;
    private UUID creatorID;
    private NamespacedKey icon;

    public Warp(String name, Player player) {
        this.name = name;
        this.description = DEFAULT_DESC;
        this.location = player.getLocation();
        this.creatorID = player.getUniqueId();
        this.icon = NamespacedKey.minecraft("compass");
    }

    public Warp(String name, String description, Location location, UUID creatorID, NamespacedKey icon) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.creatorID = creatorID;
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptionAsString() {
        return this.description;
    }

    public Component getDescriptionAsComponent() {
        return TextUtils.deserialize(this.getDescriptionAsString());
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCreatorID() {
        return this.creatorID;
    }

    public OfflinePlayer getCreatorAsPlayer() {
        return Bukkit.getOfflinePlayer(creatorID);
    }

    public Location getLocation() {
        return this.location;
    }

    public String getIconAsString() {
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItemBase(this.icon);
    }

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
    }

    public ItemStack asDisplayItem() {
        return this.asDisplayItem("");
    }

    public ItemStack asDisplayItem(String addon) {
        ItemStack item = this.getIconAsItem();
        item.setData(DataComponentTypes.CUSTOM_NAME, text()
                .content(this.getName()).color(JadeTextColor.CORAL)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore()
                .addLine(Component.text().append(this.getDescriptionAsComponent())
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build());

        loreBuilder.addLine(Component.empty());

        loreBuilder.addLine(text()
                .content("Created by: ").color(JadeTextColor.LIGHT_BRASS)
                .append(text(this.getCreatorAsPlayer().getName(), JadeTextColor.ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );

        loreBuilder.addLine(text("Location: ", JadeTextColor.LIGHT_BRASS)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        loreBuilder.addLine(text()
                .content("— World: ").color(JadeTextColor.LIGHT_BRASS)
                .append(text(this.getLocation().getWorld().getName(), JadeTextColor.CORAL))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );

        loreBuilder.addLine(text()
                .content("— Position: ").color(JadeTextColor.LIGHT_BRASS)
                .append(text(PositionUtils.asString(this.getLocation(), false), JadeTextColor.CORAL))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build()
        );

        if (addon.equalsIgnoreCase("warp")) {
            loreBuilder.addLine(Component.empty());
            loreBuilder.addLine(text()
                    .append(text("Left Click", NamedTextColor.GREEN))
                    .append(text(" to teleport to this location", NamedTextColor.DARK_GREEN))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }
        item.setData(DataComponentTypes.LORE, loreBuilder.build());

        return item;
    }

    public boolean delete() {
        File warpFile = new File(Jade.getInstance().getDataFolder(), "warps/" + this.getName() + ".yml");
        return warpFile.delete();
    }

    public void store() throws IOException {
        File warpFile = new File(Jade.getInstance().getDataFolder(), "warps/" + this.getName() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(warpFile);
        data.set("warp.name", this.getName());
        data.set("warp.description", this.getDescriptionAsString());
        data.set("warp.creator", this.getCreatorID().toString());
        data.set("warp.location", PositionUtils.asString(this.location, true));
        data.set("warp.icon", this.getIconAsString());
        data.save(warpFile);
    }

    public static Warp load(File warpFile) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(warpFile);
        return new Warp(
                data.getString("warp.name"),
                data.getString("warp.description"),
                PositionUtils.fromString(data.getString("warp.location")),
                UUID.fromString(data.getString("warp.creator")),
                ItemUtils.parseKey(data.getString("warp.icon"))
        );
    }

}

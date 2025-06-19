package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.PositionUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Grave {
    private String id;
    private UUID playerID;
    private List<ItemStack> inventory;
    private boolean virtual = true;

    private Location chestLocation = null;

    public Grave(String id, UUID playerID, List<ItemStack> inventory, Location chestLocation) {
        this.id = id;
        this.playerID = playerID;
        this.inventory = inventory;
        if (chestLocation != null) {
            this.virtual = false;
            this.chestLocation = chestLocation;
        }
    }

    // Used for unrecoverable graves (e.g.: caused by dying to the void
    public Grave(UUID playerID, List<ItemStack> inventory) {
        this(playerID, inventory, true);
    }

    // Used for recoverable graves (e.g.: caused by dying to a zombie on the surface)
    public Grave(UUID playerID, List<ItemStack> inventory, Location chestLocation) {
        this(playerID, inventory, false);

        this.chestLocation = chestLocation;
    }

    // Common base constructor
    private Grave(UUID playerID, List<ItemStack> inventory, boolean virtual) {
        this.id = generateID();
        this.playerID = playerID;
        this.inventory = inventory;
        this.virtual = virtual;
    }

    public String getID() {
        return this.id;
    }

    public UUID getPlayerID() {
        return this.playerID;
    }

    public List<ItemStack> getItems() {
        return this.inventory;
    }

    public void setItems(List<ItemStack> items) {
        this.inventory = items;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setVirtual(boolean value) {
        this.virtual = value;
    }

    public Location getChestLocation() {
        return this.chestLocation;
    }

    public String generateID() {
        List<String> adjectives = Arrays.asList(
                "embarrassing",
                "shameful",
                "sad",
                "joyous",
                "atrocious",
                "poignant",
                "dastardly",
                "inspiring",
                "disgraceful",
                "shabby",
                "unwise",
                "depressing",
                "usual",
                "common",
                "cooked",
                "unlucky",
                "undeserved",
                "avoidable",
                "ridiculous",
                "agonizing",
                "annoying",
                "ominous",
                "foretold",
                "predictable",
                "uncommon",
                "worrisome",
                "troublesome",
                "albanian",
                "lovely",
                "despicable",
                "fated"
        );

        List<String> nouns = Arrays.asList(
                "Story",
                "Tale",
                "Day",
                "Experience",
                "Occurrence",
                "Demise",
                "Fate",
                "Failure",
                "Event",
                "Development",
                "Disgrace",
                "Noob",
                "Tomfoolery",
                "Shenanigan",
                "Misfortune",
                "Casualty",
                "Moment",
                "Expedition",
                "Effort",
                "Affair",
                "Chronicle",
                "Aftermath",
                "Woe",
                "Troubles",
                "Remainder",
                "Remains",
                "Outcome"
        );

        String identifier;
        do {
            int adjectiveIndex = (int) (Math.random() * adjectives.size());
            int nounIndex = (int) (Math.random() * nouns.size());

            identifier = adjectives.get(adjectiveIndex) + nouns.get(nounIndex);
        }
        while (!PlayerManager.isUniqueGraveID(identifier));
        return identifier;
    }

    public ItemStack asDisplayItem() {
        return this.asDisplayItem(null);
    }

    public ItemStack asDisplayItem(String addon) {
        ItemStack item = ItemUtils.asDisplayItem(NamespacedKey.minecraft(this.isVirtual() ? "cyan_candle" : "red_candle"));
        item.setData(DataComponentTypes.CUSTOM_NAME, text()
                .content(this.getID()).color(JadeTextColor.ZORBA)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore();

        loreBuilder.addLine(text("Items: ", JadeTextColor.LIGHT_BRASS)
                .append(text(this.getItems().size(), JadeTextColor.ZORBA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        if (!this.isVirtual()) {
            loreBuilder.addLine(text("Location: ", JadeTextColor.LIGHT_BRASS)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            loreBuilder.addLine(text()
                    .content("— World: ").color(JadeTextColor.LIGHT_BRASS)
                    .append(text(this.getChestLocation().getWorld().getName(), JadeTextColor.CORAL))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build()
            );
            loreBuilder.addLine(text()
                    .content("— Position: ").color(JadeTextColor.LIGHT_BRASS)
                    .append(text(PositionUtils.asString(this.getChestLocation(), false), JadeTextColor.CORAL))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build()
            );
        }

        if (!this.isVirtual()) {
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
        } else {
            loreBuilder.addLine(Component.empty());
            loreBuilder.addLine(text()
                    .append(text("Left Click", NamedTextColor.GREEN))
                    .append(text(" to open this grave", NamedTextColor.DARK_GREEN))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
        }

        return item;
    }
}

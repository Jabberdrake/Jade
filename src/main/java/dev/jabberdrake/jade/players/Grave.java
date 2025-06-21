package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.utils.ItemUtils;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.PositionUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Grave {
    private String id;
    private UUID playerID;
    private List<ItemStack> inventory;
    private String cause;
    private Location chestLocation = null;

    // Used by DatabaseManager
    public Grave(String id, UUID playerID, List<ItemStack> inventory, String cause, Location chestLocation) {
        this.id = id;
        this.playerID = playerID;
        this.inventory = inventory;
        this.cause = cause;
        if (chestLocation != null) {
            this.chestLocation = chestLocation;
        }
    }

    // Common base constructor
    public Grave(Player player, List<ItemStack> inventory, Location chestLocation) {
        this.id = generateID();
        this.playerID = player.getUniqueId();
        this.inventory = inventory;
        this.cause = formatCause(player);
        this.chestLocation = chestLocation;
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

    public String getCause() {
        return this.cause;
    }

    public boolean isVirtual() {
        return (this.chestLocation == null);
    }

    public void makeVirtual() {
        this.chestLocation = null;
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

    public static String resolveEntityName(Entity entity) {
        return entity.getName();
    }

    public static String resolveDamageCause(EntityDamageEvent.DamageCause cause) {
        String causeString;
        switch (cause) {
            case FALL -> causeString = "falling";
            case FIRE, FIRE_TICK -> causeString = "burning";
            case KILL -> causeString = "evil admin machinations";
            case LAVA -> causeString = "swimming in lava";
            case VOID -> causeString = "falling into the void";
            case MAGIC -> causeString = "foul magic";
            case CUSTOM -> causeString = "...something";
            case DRYOUT -> causeString = "being a fish";
            case FREEZE -> causeString = "freezing";
            case POISON -> causeString = "poison";
            case THORNS -> causeString = "thorns";
            case WITHER -> causeString = "withering";
            case CONTACT -> causeString = "harmful block";
            case MELTING -> causeString = "being a snowman";
            case SUICIDE -> causeString = "suicide";
            case CAMPFIRE -> causeString = "spitroasting";
            case CRAMMING -> causeString = "cramming";
            case DROWNING -> causeString = "drowning";
            case HOT_FLOOR -> causeString = "not having shoes";
            case LIGHTNING -> causeString = "Zeus";
            case PROJECTILE -> causeString = "a projectile";
            case SONIC_BOOM -> causeString = "a sonic boom from a Warden";
            case STARVATION -> causeString = "starving";
            case SUFFOCATION -> causeString = "suffocating in a wall";
            case WORLD_BORDER -> causeString = "THE FOG";
            case ENTITY_ATTACK -> causeString = "Dotto";
            case FALLING_BLOCK -> causeString = "a falling anvil";
            case FLY_INTO_WALL -> causeString = "roleplaying as a pidgeon";
            case BLOCK_EXPLOSION -> causeString = "TNT";
            case ENTITY_EXPLOSION -> causeString = "Creeper";
            case ENTITY_SWEEP_ATTACK -> causeString = "getting molly-whopped";
            case DRAGON_BREATH -> causeString = "getting spat on";
            default -> causeString = "...I don't know.";
        }
        return causeString;
    }

    public static String formatCause(Player player) {
        String cause = "";
        if (player.getKiller() != null) {
            cause = player.getKiller().getName();
        } else if (player.getLastDamageCause() != null) {
            EntityDamageEvent damageEvent = player.getLastDamageCause();

            if (damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                damageEvent instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent) damageEvent;
                cause = resolveEntityName(entityDamageEvent.getDamager());
            } else {
                cause = resolveDamageCause(damageEvent.getCause());
            }
        }

        return "Death by " + cause;
    }

    public ItemStack asDisplayItem() {
        return this.asDisplayItem(null);
    }

    public ItemStack asDisplayItem(String addon) {
        ItemStack item = ItemUtils.asDisplayItemBase(NamespacedKey.minecraft(this.isVirtual() ? "cyan_candle" : "red_candle"));
        item.setData(DataComponentTypes.CUSTOM_NAME, text()
                .content(this.getID()).color(JadeTextColor.ZORBA)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        ItemLore.Builder loreBuilder = ItemLore.lore();

        loreBuilder.addLine(text(this.getCause(), JadeTextColor.DARK_ZORBA).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        loreBuilder.addLine(Component.empty());

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

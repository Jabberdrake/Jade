package dev.jabberdrake.jade.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.HSVLike;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.stream.Collectors;

import static dev.jabberdrake.jade.utils.ItemUtils.parseKey;
import static net.kyori.adventure.text.format.TextColor.color;

public class JadeTextColor implements TextColor {
    // A massive thank you to the developers of the Adventure API for the basis of this code!
    // (Source: https://github.com/KyoriPowered/adventure/blob/main/4/api/src/main/java/net/kyori/adventure/text/format/NamedTextColor.java)

    // STANDARD MINECRAFT COLORS
    public static final JadeTextColor BLACK = new JadeTextColor("black", NamedTextColor.BLACK.value(), "minecraft:black_dye");
    public static final JadeTextColor BLUE = new JadeTextColor("blue", NamedTextColor.DARK_BLUE.value(), "minecraft:blue_dye");
    public static final JadeTextColor GREEN = new JadeTextColor("green", NamedTextColor.DARK_GREEN.value(), "minecraft:green_dye");
    public static final JadeTextColor CYAN = new JadeTextColor("cyan", NamedTextColor.DARK_AQUA.value(), "minecraft:cyan_dye");
    public static final JadeTextColor CARMINE = new JadeTextColor("carmine", NamedTextColor.DARK_RED.value(), "minecraft:redstone");
    public static final JadeTextColor PURPLE = new JadeTextColor("purple", NamedTextColor.DARK_PURPLE.value(), "minecraft:purple_dye");
    public static final JadeTextColor GOLD = new JadeTextColor("gold", NamedTextColor.GOLD.value(), "minecraft:raw_gold");
    public static final JadeTextColor LIGHT_GRAY = new JadeTextColor("light_gray", NamedTextColor.GRAY.value(), "minecraft:light_gray_dye");
    public static final JadeTextColor GRAY = new JadeTextColor("gray", NamedTextColor.DARK_GRAY.value(), "minecraft:gunpowder"); // I think gray dye is too light for this color
    public static final JadeTextColor LIGHT_BLUE = new JadeTextColor("light_blue", NamedTextColor.BLUE.value(), "minecraft:light_blue_dye");
    public static final JadeTextColor LIME = new JadeTextColor("lime", NamedTextColor.GREEN.value(), "minecraft:lime_dye");
    public static final JadeTextColor AQUA = new JadeTextColor("aqua", NamedTextColor.AQUA.value(), "minecraft:prismarine_crystals");
    public static final JadeTextColor RED = new JadeTextColor("red", NamedTextColor.RED.value(), "minecraft:red_dye");
    public static final JadeTextColor MAGENTA = new JadeTextColor("magenta", NamedTextColor.LIGHT_PURPLE.value(), "minecraft:magenta_dye");
    public static final JadeTextColor YELLOW = new JadeTextColor("yellow", NamedTextColor.YELLOW.value(), "minecraft:yellow_dye");
    public static final JadeTextColor WHITE = new JadeTextColor("white", NamedTextColor.WHITE.value(), "minecraft:white_dye");


    // JADE COLORS
    public static final JadeTextColor DARK_ZORBA = new JadeTextColor("dark_zorba", color(0x4b4047), "minecraft:flint");
    public static final JadeTextColor ZORBA = new JadeTextColor("zorba", color(0x9f918d), "minecraft:dead_tube_coral");
    public static final JadeTextColor LIGHT_ZORBA = new JadeTextColor("light_zorba", color(0xdadada), "minecraft:clay");
    public static final JadeTextColor DARK_LAUREL = new JadeTextColor("dark_laurel", color(0x00a800), "minecraft:sugar_cane");
    public static final JadeTextColor LAUREL = new JadeTextColor("laurel", color(0x97ca97), "minecraft:sugar_cane");
    public static final JadeTextColor LIGHT_LAUREL = new JadeTextColor("light_laurel", color(0x54fc54), "minecraft:sugar_cane");
    public static final JadeTextColor DARK_BRASS = new JadeTextColor("dark_brass", color(0xb49f3e), "minecraft:gold_ingot");
    public static final JadeTextColor LIGHT_BRASS = new JadeTextColor("light_brass", color(0xd9ca81), "minecraft:gold_ingot");
    public static final JadeTextColor SUNSTEEL = new JadeTextColor("sunsteel", color(0xfce143), "minecraft:gold_ingot");
    public static final JadeTextColor DARK_AMETHYST = new JadeTextColor("dark_amethyst", color(0x9a58fc), "minecraft:amethyst_shard");
    public static final JadeTextColor AMETHYST = new JadeTextColor("amethyst", color(0xaa76dd), "minecraft:amethyst_shard");
    public static final JadeTextColor LIGHT_AMETHYST = new JadeTextColor("light_amethyst", color(0xc49ffc), "minecraft:amethyst_shard");
    public static final JadeTextColor CHROME = new JadeTextColor("chrome", color(0x8b9db3), "minecraft:iron_ingot");
    public static final JadeTextColor LIGHT_CHROME = new JadeTextColor("light_chrome", color(0xd1e7e5), "minecraft:iron_ingot");
    public static final JadeTextColor DARK_MANA = new JadeTextColor("dark_mana", color(0x5d9da8), "minecraft:heart_of_the_sea");
    public static final JadeTextColor LIGHT_MANA = new JadeTextColor("light_mana", color(0x84e1e1), "minecraft:heart_of_the_sea");
    public static final JadeTextColor ROSEMETAL = new JadeTextColor("rosemetal", color(0xfc7c96), "minecraft:armadillo_scute");
    public static final JadeTextColor LIGHT_ROSEMETAL = new JadeTextColor("light_rosemetal", color(0xf5afaf), "minecraft:armadillo_scute");
    public static final JadeTextColor LIVINGMETAL = new JadeTextColor("livingmetal", color(0x5afc9f), "minecraft:music_disc_creator");
    public static final JadeTextColor MYTHIC_BLUE = new JadeTextColor("mythic_blue", color(0x30eeb4), "minecraft:light_blue_candle");
    public static final JadeTextColor AUDALAD_CYAN = new JadeTextColor("audalad_cyan", color(0x53b5b5), "minecraft:tide_armor_trim_smithing_template");
    public static final JadeTextColor BEER_YELLOW = new JadeTextColor("beer_yellow", color(0xf4ac5e), "minecraft:wheat");
    public static final JadeTextColor WINE_RED = new JadeTextColor("wine_red", color(0x7b0323), "minecraft:sweet_berries");
    public static final JadeTextColor TYRIAN_PURPLE = new JadeTextColor("tyrian_purple", color(0x66023c), "minecraft:nautilus_shell");
    public static final JadeTextColor CORAL = new JadeTextColor("coral", color(0xff7f50), "minecraft:fire_coral");
    public static final JadeTextColor SALMON = new JadeTextColor("salmon", color(0xfa8072), "minecraft:salmon");
    public static final JadeTextColor ORANGE = new JadeTextColor("orange", color(0xffa500), "minecraft:orange_dye");
    public static final JadeTextColor COPPER = new JadeTextColor("copper", color(0xb4684d), "minecraft:copper_ingot");
    public static final JadeTextColor DIAMOND = new JadeTextColor("diamond", color(0x6eecd2), "minecraft:diamond");
    public static final JadeTextColor EMERALD = new JadeTextColor("emerald", color(0x11a036), "minecraft:emerald");
    public static final JadeTextColor IRON = new JadeTextColor("iron", color(0xececec), "minecraft:iron_ingot");
    public static final JadeTextColor LAPIS = new JadeTextColor("lapis", color(0x416e97), "minecraft:lapis_lazuli");
    public static final JadeTextColor QUARTZ = new JadeTextColor("quartz", color(0xe3d4c4), "minecraft:quartz");
    public static final JadeTextColor NETHERITE = new JadeTextColor("netherite", color(0x625859), "minecraft:netherite_ingot");
    public static final JadeTextColor REDSTONE = new JadeTextColor("redstone", color(0x971607), "minecraft:redstone");
    public static final JadeTextColor RESIN = new JadeTextColor("resin", color(0xfc7812), "minecraft:resin_brick");

    private static final List<JadeTextColor> VANILLA_VALUES = List.of(
            BLACK, BLUE, GREEN, CYAN, CARMINE, PURPLE, GOLD, LIGHT_GRAY, GRAY, LIGHT_BLUE, LIME, AQUA, RED, MAGENTA, YELLOW, WHITE
    );

    public static final List<String> VANILLA_NAMES = VANILLA_VALUES.stream().map(JadeTextColor::toString).toList();

    private static final List<JadeTextColor> VALUES = List.of(
            BLACK, BLUE, GREEN, CYAN, CARMINE, PURPLE, GOLD, LIGHT_GRAY, GRAY, LIGHT_BLUE, LIME, AQUA, RED, MAGENTA, YELLOW, WHITE,
            DARK_ZORBA, ZORBA, LIGHT_ZORBA,
            DARK_LAUREL, LAUREL, LIGHT_LAUREL,
            DARK_BRASS, LIGHT_BRASS, SUNSTEEL,
            DARK_AMETHYST, AMETHYST, LIGHT_AMETHYST,
            CHROME, LIGHT_CHROME,
            DARK_MANA, LIGHT_MANA,
            ROSEMETAL, LIGHT_ROSEMETAL,
            LIVINGMETAL, MYTHIC_BLUE, AUDALAD_CYAN,
            BEER_YELLOW, WINE_RED, TYRIAN_PURPLE,
            CORAL, SALMON, ORANGE,
            COPPER, DIAMOND, EMERALD, IRON, LAPIS, QUARTZ, NETHERITE, REDSTONE, RESIN
    );

    public static final List<String> NAMES = VALUES.stream().map(JadeTextColor::toString).toList();

    public static String toMessageTag(TextColor color) {
        return "<color:" + color.asHexString().toUpperCase(Locale.ROOT) + ">";
    }

    public static JadeTextColor nearestTo(final TextColor any) {
        if (any instanceof JadeTextColor) {
            return (JadeTextColor) any;
        }

        return TextColor.nearestColorTo(VALUES, any);
    }

    public static TextColor fromNameOrHexstring(String arg, boolean restrictToDefaultColors) {
        // If the string starts with a #, then we'll try to parse it into a valid TextColor.
        // Note that fromHexString() returns null if it can't successfully parse it, so no need
        // to implement extra error handling here.
        if (arg.startsWith("#")) {
            return TextColor.fromHexString(arg);
        }

        // Otherwise, we'll assume that the string matches a named color:
        List<JadeTextColor> values = (restrictToDefaultColors) ? VANILLA_VALUES : VALUES;
        for (JadeTextColor jadeColor : values) {
            if (jadeColor.toString().equals(arg)) {
                return jadeColor;
            }
        }

        // If the input is not formatted like a hexstring, and does not match any known named color,
        // then just return null so we can display an error message
        return null;
    }

    public static List<JadeTextColor> getAllColors() {
        return VALUES;
    }

    public static JadeTextColor matchName(String name) {
        for (JadeTextColor color : getAllColors()) {
            if (color.name.equalsIgnoreCase(name)) return color;
        }

        return JadeTextColor.WHITE; //default
    }

    public static TagResolver asAdventureTags() {
        List<TagResolver> stylings = VALUES.stream().map(color -> Placeholder.styling(color.name, color(color.value))).collect(Collectors.toList());

        stylings.add(Placeholder.unparsed("tag_open", "<"));
        stylings.add(Placeholder.unparsed("tag_close", ">"));
        return TagResolver.resolver(stylings);
    }

    private final String name;
    private final int value;
    private final HSVLike hsv;
    private final NamespacedKey icon;

    public JadeTextColor(final String name, final int value) {
        this.name = name;
        this.value = value;
        this.hsv = HSVLike.fromRGB(this.red(), this.green(), this.blue());
        this.icon = NamespacedKey.minecraft("barrier");
    }

    public JadeTextColor(final String name, final int value, final String iconKey) {
        this.name = name;
        this.value = value;  // This is very stupid
        this.hsv = HSVLike.fromRGB(this.red(), this.green(), this.blue());
        this.icon = parseKey(iconKey);
    }

    public JadeTextColor(final String name, final TextColor color, final String iconKey) {
        this.name = name;
        this.value = color.value();     // The only reason I ever use this is that the MC-Dev plugin only previews color if a call to TextColor is made.
        this.hsv = HSVLike.fromRGB(this.red(), this.green(), this.blue());
        this.icon = parseKey(iconKey);
    }

    @Override
    public int value() {
        return this.value;
    }

    @Override
    public HSVLike asHSV() {
        return this.hsv;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

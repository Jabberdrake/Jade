package dev.jabberdrake.jade.utils;

import dev.jabberdrake.jade.utils.message.ErrorStrategy;
import dev.jabberdrake.jade.utils.message.InfoStrategy;
import dev.jabberdrake.jade.utils.message.SuccessStrategy;
import dev.jabberdrake.jade.utils.message.SystemStrategy;
import io.papermc.paper.datacomponent.item.ItemLore;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final InfoStrategy INFO_STRATEGY = new InfoStrategy();
    private static final ErrorStrategy ERROR_STRATEGY = new ErrorStrategy();
    private static final SuccessStrategy SUCCESS_STRATEGY = new SuccessStrategy();
    private static final SystemStrategy SYSTEM_STRATEGY = new SystemStrategy();

    public static final DecimalFormat DF = new DecimalFormat("#.#");
    private static final Map<String, Integer> ROMAN_CONVERSION_MAP = new LinkedHashMap<>();
    private static final Map<TrimMaterial, Pair<String, TextColor>> TRIM_MATERIAL_MAP = new LinkedHashMap<>();
    private static final Map<TrimPattern, String> TRIM_PATTERN_MAP = new LinkedHashMap<>();

    static {
        ROMAN_CONVERSION_MAP.put("M", 1000);
        ROMAN_CONVERSION_MAP.put("CM", 900);
        ROMAN_CONVERSION_MAP.put("D", 500);
        ROMAN_CONVERSION_MAP.put("CD", 400);
        ROMAN_CONVERSION_MAP.put("C", 100);
        ROMAN_CONVERSION_MAP.put("XC", 90);
        ROMAN_CONVERSION_MAP.put("L", 50);
        ROMAN_CONVERSION_MAP.put("XL", 40);
        ROMAN_CONVERSION_MAP.put("X", 10);
        ROMAN_CONVERSION_MAP.put("IX", 9);
        ROMAN_CONVERSION_MAP.put("V", 5);
        ROMAN_CONVERSION_MAP.put("IV", 4);
        ROMAN_CONVERSION_MAP.put("I", 1);

        TRIM_MATERIAL_MAP.put(TrimMaterial.AMETHYST, Pair.of("Amethyst", JadeTextColor.AMETHYST));
        TRIM_MATERIAL_MAP.put(TrimMaterial.COPPER, Pair.of("Copper", JadeTextColor.COPPER));
        TRIM_MATERIAL_MAP.put(TrimMaterial.DIAMOND, Pair.of("Diamond", JadeTextColor.DIAMOND));
        TRIM_MATERIAL_MAP.put(TrimMaterial.EMERALD, Pair.of("Emerald", JadeTextColor.EMERALD));
        TRIM_MATERIAL_MAP.put(TrimMaterial.GOLD , Pair.of("Golden", JadeTextColor.GOLD));
        TRIM_MATERIAL_MAP.put(TrimMaterial.IRON, Pair.of("Iron", JadeTextColor.IRON));
        TRIM_MATERIAL_MAP.put(TrimMaterial.LAPIS, Pair.of("Lapis", JadeTextColor.LAPIS));
        TRIM_MATERIAL_MAP.put(TrimMaterial.NETHERITE, Pair.of("Netherite", JadeTextColor.NETHERITE));
        TRIM_MATERIAL_MAP.put(TrimMaterial.QUARTZ, Pair.of("Quartz", JadeTextColor.QUARTZ));
        TRIM_MATERIAL_MAP.put(TrimMaterial.REDSTONE, Pair.of("Redstone", JadeTextColor.REDSTONE));
        TRIM_MATERIAL_MAP.put(TrimMaterial.RESIN, Pair.of("Resin", JadeTextColor.RESIN));

        TRIM_PATTERN_MAP.put(TrimPattern.BOLT, "Bolt");
        TRIM_PATTERN_MAP.put(TrimPattern.COAST, "Coast");
        TRIM_PATTERN_MAP.put(TrimPattern.DUNE, "Dune");
        TRIM_PATTERN_MAP.put(TrimPattern.EYE, "Eye");
        TRIM_PATTERN_MAP.put(TrimPattern.FLOW, "Flow");
        TRIM_PATTERN_MAP.put(TrimPattern.HOST, "Host");
        TRIM_PATTERN_MAP.put(TrimPattern.RAISER, "Raiser");
        TRIM_PATTERN_MAP.put(TrimPattern.RIB, "Rib");
        TRIM_PATTERN_MAP.put(TrimPattern.SENTRY, "Sentry");
        TRIM_PATTERN_MAP.put(TrimPattern.SHAPER, "Shaper");
        TRIM_PATTERN_MAP.put(TrimPattern.SILENCE, "Silence");
        TRIM_PATTERN_MAP.put(TrimPattern.SNOUT, "Snout");
        TRIM_PATTERN_MAP.put(TrimPattern.SPIRE, "Spire");
        TRIM_PATTERN_MAP.put(TrimPattern.TIDE, "Tide");
        TRIM_PATTERN_MAP.put(TrimPattern.VEX, "Vex");
        TRIM_PATTERN_MAP.put(TrimPattern.WARD, "Ward");
        TRIM_PATTERN_MAP.put(TrimPattern.WAYFINDER, "Wayfinder");
        TRIM_PATTERN_MAP.put(TrimPattern.WILD, "Wild");
    }

    public static final TextColor DARK_ZORBA = TextColor.color(0x4b4047);
    public static final TextColor ZORBA = TextColor.color(0x9f918d);
    public static final TextColor LIGHT_ZORBA = TextColor.color(0xdadada);

    public static final TextColor DARK_LAUREL = TextColor.color(0x00a800);
    public static final TextColor LAUREL = TextColor.color(0x97ca97);
    public static final TextColor LIGHT_LAUREL = TextColor.color(0x54fc54);

    public static final TextColor DARK_BRASS = TextColor.color(0xb49f3e);
    public static final TextColor BRASS = TextColor.color(0xd9ca81);
    public static final TextColor LIGHT_BRASS = TextColor.color(0xfce143);

    public static final TextColor DARK_AMETHYST = TextColor.color(0x9a58fc);
    public static final TextColor AMETHYST = TextColor.color(0xaa76dd);
    public static final TextColor LIGHT_AMETHYST = TextColor.color(0xc49ffc);

    public static final TextColor DARK_CHROME = TextColor.color(0x8b9db3);
    public static final TextColor LIGHT_CHROME = TextColor.color(0xd1e7e5);

    public static final TextColor DARK_MANA = TextColor.color(0x5d9da8);
    public static final TextColor LIGHT_MANA = TextColor.color(0x84e1e1);

    public static final TextColor LIGHT_ROSEMETAL = TextColor.color(0xf5afaf);
    public static final TextColor ROSEMETAL = TextColor.color(0xfc7c96);

    public static final TextColor LIVINGMETAL = TextColor.color(0x5afc9f);

    public static final TextColor MYTHIC_BLUE = TextColor.color(0x30eeb4);
    public static final TextColor LEGENDARY_ORANGE = NamedTextColor.GOLD;
    public static final TextColor RARE_PURPLE = NamedTextColor.DARK_PURPLE;
    public static final TextColor UNCOMMON_GREEN = NamedTextColor.GREEN;
    public static final TextColor COMMON_WHITE = NamedTextColor.WHITE;

    public static final Component SYMBOL_ACCEPTED = Component.text("✔", NamedTextColor.GREEN);
    public static final Component SYMBOL_DENIED = Component.text("❌", NamedTextColor.RED);

    public static Component composeInfoPrefix() {
        return Component.text("[!] > ", LIGHT_ZORBA);
    }

    public static Component composeInfoText(String content) {
        return Component.text(content, ZORBA);
    }

    public static Component composeInfoHighlight(String content) {
        return Component.text(content, LIGHT_ZORBA);
    }

    public static Component composeSimpleInfoMessage(String content) {
        return Component.text()
                .append(composeInfoPrefix())
                .append(composeInfoText(content))
                .build();
    }

    public static Component composeErrorPrefix() {
        return Component.text("[⚠] > ", NamedTextColor.RED);
    }

    public static Component composeErrorText(String content) {
        return Component.text(content, NamedTextColor.DARK_RED);
    }

    public static Component composeErrorHighlight(String content) { return Component.text(content, NamedTextColor.RED); }

    public static Component composeSimpleErrorMessage(String content) {
        return Component.text()
                .append(composeErrorPrefix())
                .append(composeErrorText(content))
                .build();
    }

    public static Component composeSuccessPrefix() {
        return Component.text("[❊] > ", LIGHT_LAUREL);
    }

    public static Component composeSuccessText(String content) {
        return Component.text(content, LAUREL);
    }

    public static Component composeSuccessHighlight(String content) {
        return Component.text(content, DARK_LAUREL);
    }

    public static Component composeSimpleSuccessMessage(String content) {
        return Component.text()
                .append(composeSuccessPrefix())
                .append(composeSuccessText(content))
                .build();
    }

    public static Component composeOperatorPrefix() {
        return Component.text("[※] > ", LIGHT_CHROME);
    }

    public static Component composeOperatorText(String content) {
        return Component.text(content, DARK_CHROME);
    }

    public static Component composeOperatorHighlight(String content) {
        return Component.text(content, LIGHT_CHROME);
    }

    public static Component composeSimpleOperatorMessage(String content) {
        return Component.text()
                .append(composeOperatorPrefix())
                .append(composeOperatorText(content))
                .build();
    }

    public static Component composeAcceptedRequirement(String content) {
        return Component.text()
                .append(TextUtils.SYMBOL_ACCEPTED)
                .append(Component.text(" " + content, TextUtils.LIGHT_CHROME))
                .build();
    }

    public static Component composeDeniedRequirement(String content) {
        return Component.text()
                .append(TextUtils.SYMBOL_DENIED)
                .append(Component.text(" " + content, TextUtils.DARK_CHROME))
                .build();
    }

    public static Component info(String message) {
        return INFO_STRATEGY.process(message);
    }

    public static Component error(String message) {
        return ERROR_STRATEGY.process(message);
    }

    public static Component success(String message) {
        return SUCCESS_STRATEGY.process(message);
    }

    public static Component system(String message) {
        return SYSTEM_STRATEGY.process(message);
    }

    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(preprocess(text), JadeTextColor.asAdventureTags());
    }

    public static String translateGradients(String raw) {
        Pattern pattern = Pattern.compile("<gradient:([a-z_]+):([a-z_]+)>");
        Matcher matcher = pattern.matcher(raw);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String replacement = "<gradient:" + JadeTextColor.matchName(matcher.group(1)).asHexString() + ":" + JadeTextColor.matchName(matcher.group(2)).asHexString() + ">";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static String preprocess(String raw) {
        String result = translateGradients(raw);
        // other steps...
        return result;
    }

    public static void lore(ItemLore.Builder loreBuilder, List<Component> loreLines, String prefixString, TextColor prefixColor) {
        for (Component line : loreLines) {
            loreBuilder.addLine(Component.text().content(prefixString).color(prefixColor)
                    .append(line)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }
    }

    public static void lore(ItemLore.Builder loreBuilder, List<String> loreLines) {
        for (String line : loreLines) {
            if (line.equalsIgnoreCase("")) {
                loreBuilder.addLine(Component.empty());
                continue;
            }
            loreBuilder.addLine(deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
    }

    public static String toRomanNumeral(int arabicInt) {
        StringBuilder romanNumeral = new StringBuilder();
        int remainder = arabicInt;
        for (Map.Entry<String, Integer> numeralPair : ROMAN_CONVERSION_MAP.entrySet()) {
            while (remainder >= numeralPair.getValue()) {
                romanNumeral.append(numeralPair.getKey());
                remainder -= numeralPair.getValue();
            }
        }
        return romanNumeral.toString();
    }

    public static String getTrimAsString(ArmorTrim trim) {
        String pattern = TRIM_PATTERN_MAP.get(trim.getPattern());
        String material = TRIM_MATERIAL_MAP.get(trim.getMaterial()).left();
        String color = "<" + TRIM_MATERIAL_MAP.get(trim.getMaterial()).right().asHexString() + ">";

        return "<zorba>Trim: " + color + material + " " + pattern;
    }
}

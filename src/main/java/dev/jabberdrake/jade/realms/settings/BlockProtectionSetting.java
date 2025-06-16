package dev.jabberdrake.jade.realms.settings;

import dev.jabberdrake.jade.utils.AbstractSetting;
import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class BlockProtectionSetting extends AbstractSetting<String> {

    // valid values
    public static final String NONE = "NONE";
    public static final String ADVENTURE = "ADVENTURE";
    public static final String REGULAR = "REGULAR";
    public static final List<String> VALUES = List.of(NONE, ADVENTURE, REGULAR);

    // static attributes
    private static final String NAME = "settlement.blockProtection";
    private static final String DISPLAY = "Block Protection";
    private static final NamespacedKey ICON = NamespacedKey.minecraft("grass_block");
    private static final String DEFAULT_VALUE = REGULAR;

    // default
    public static BlockProtectionSetting DEFAULT = new BlockProtectionSetting(DEFAULT_VALUE);

    public BlockProtectionSetting(String value) {
        super(value);
    }

    public BlockProtectionSetting() {
        super(DEFAULT_VALUE);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String displayName() {
        return DISPLAY;
    }

    @Override
    public List<Component> description() {
        return List.of(
                text("Defines how foreigners may break", JadeTextColor.ZORBA)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                text("or interact with blocks within", JadeTextColor.ZORBA)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                text("this settlement's territory.", JadeTextColor.ZORBA)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        );
    }

    @Override
    public void buildValueLore(ItemLore.Builder loreBuilder) {
        loreBuilder.addLine(text()
                .content("Values: ").color(JadeTextColor.LIGHT_AMETHYST)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        loreBuilder.addLine(text()
                .content("— ").color(JadeTextColor.LIGHT_AMETHYST)
                .append(text(NONE, getValue().equals(NONE) ? JadeTextColor.LIGHT_CHROME : JadeTextColor.CHROME))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        loreBuilder.addLine(text()
                .content("— ").color(JadeTextColor.LIGHT_AMETHYST)
                .append(text(ADVENTURE, getValue().equals(ADVENTURE) ? JadeTextColor.LIGHT_CHROME : JadeTextColor.CHROME))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        loreBuilder.addLine(text()
                .content("— ").color(JadeTextColor.LIGHT_AMETHYST)
                .append(text(REGULAR, getValue().equals(REGULAR) ? JadeTextColor.LIGHT_CHROME : JadeTextColor.CHROME))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());
        loreBuilder.addLine(Component.empty());
        switch (this.getValue()) {
            case NONE:
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>Foreigners may break and interact<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>with <zorba>ALL</zorba> blocks.<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                break;
            case ADVENTURE:
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>Foreigners may <zorba>ONLY</zorba> interact with<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>blocks, similarly to <zorba>Adventure Mode</zorba>.<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                break;
            case REGULAR:
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>Foreigners may not break nor<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                loreBuilder.addLine(TextUtils.deserialize("<dark_zorba>interact with <zorba>ANY</zorba> blocks.<dark_zorba>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                break;
            default:
                loreBuilder.addLine(TextUtils.deserialize("<red>An error has occured!</red>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                loreBuilder.addLine(TextUtils.deserialize("<red>Please report this to a developer.</red>")
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
    }

    @Override
    public NamespacedKey iconKey() {
        return ICON;
    }

    @Override
    public String defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public boolean isValidValue(String value) {
        return VALUES.contains(value);
    }

    @Override
    public String cycleValue() {
        int currentIndex = VALUES.indexOf(this.getValue());
        return VALUES.get((currentIndex + 1) % VALUES.size());
    }


    public static BlockProtectionSetting deserialize(String serializedString) {
        String[] parts = serializedString.split("=");
        if (parts.length != 2) return DEFAULT;

        String name = parts[0];
        String value = parts[1];
        if (!DEFAULT.name().equals(name) || !DEFAULT.isValidValue(value)) return DEFAULT;

        return new BlockProtectionSetting(value);
    }
}

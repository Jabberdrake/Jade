package dev.jabberdrake.jade.commands.profile;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class ProfileSettingsCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("setting", StringArgumentType.word())
                        .suggests(ProfileSettingsCommand::suggestAllSettings)
                        .then(Commands.argument("value", StringArgumentType.word())
                                .suggests(ProfileSettingsCommand::suggestPossibleValues)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(ProfileSettingsCommand::runCommand)
                        )
                )
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String settingArgument = StringArgumentType.getString(context, "setting");
        AbstractSetting<?> setting = ProfileSettingsCommand.validateSettingArgument(settingArgument);
        if (setting == null) {
            player.sendMessage(error("Could not find a setting named <highlight>" + settingArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        String valueArgument = StringArgumentType.getString(context, "value");
        if (!ProfileSettingsCommand.validateValueArgument(setting, valueArgument)) {
            player.sendMessage(error("That value (<highlight>" + valueArgument + "</highlight>) is not valid for the <highlight>" + setting.getName() + "</highlight> setting!"));
            return Command.SINGLE_SUCCESS;
        }

        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting) setting;
            boolean booleanValue = Boolean.parseBoolean(valueArgument);

            jadePlayer.setSetting(booleanSetting, booleanValue);

            if (booleanValue) {
                player.sendMessage(system("Changed setting <highlight>" + booleanSetting.getName() + "</highlight> to <green>TRUE</green>!"));
            } else {
                player.sendMessage(system("Changed setting <highlight>" + booleanSetting.getName() + "</highlight> to <red>FALSE</red>!"));
            }
        } else if (setting instanceof StringSetting) {
            StringSetting stringSetting = (StringSetting) setting;
            String stringValue = valueArgument.toUpperCase(Locale.ROOT);

            jadePlayer.setSetting(stringSetting, stringValue);

            player.sendMessage(system("Changed setting <highlight>" + stringSetting.getName() + "</highlight> to <highlight>" + stringValue + "</highlight>!"));
        } else {
            player.sendMessage(error("Could not find the matching class type for the specified setting <highlight>" + settingArgument + "</highlight>. Please report this to a developer!"));
        }

        PlayerManager.registerSettingChange(player, setting);
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> suggestAllSettings(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        PlayerSettings.getAllSettings().stream().map(AbstractSetting::getName).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestPossibleValues(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        AbstractSetting<?> setting = validateSettingArgument(StringArgumentType.getString(context, "setting"));
        if (setting == null) return builder.buildFuture();

        for (Object value : setting.getPossibleValues()) {
            builder.suggest(String.valueOf(value));
        }
        return builder.buildFuture();
    }

    public static AbstractSetting<?> validateSettingArgument(String settingArgument) {
        for (AbstractSetting<?> setting : PlayerSettings.getAllSettings()) {
            if (setting.getName().equalsIgnoreCase(settingArgument)) {
                return setting;
            }
        }
        return null;
    }

    public static boolean validateValueArgument(AbstractSetting<?> setting, String valueArgument) {
        for (Object object : setting.getPossibleValues()) {
            if (valueArgument.equalsIgnoreCase(object.toString())) {
                return true;
            }
        }

        return false;
    }
}

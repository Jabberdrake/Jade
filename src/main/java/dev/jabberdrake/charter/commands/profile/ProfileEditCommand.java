package dev.jabberdrake.charter.commands.profile;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ProfileEditCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("attribute", StringArgumentType.word())
                        .suggests(ProfileEditCommand::buildProfileFieldSuggestions)
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes(ProfileEditCommand::runCommand)
                        )
                )
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player)) {
            Charter.getPlugin(Charter.class).getLogger().warning("[ProfileEditCommand::runCommand] Only players can run this command!");
        }

        Player player = (Player) sender;
        String attr = StringArgumentType.getString(context, "attribute");
        String value = StringArgumentType.getString(context, "value");

        switch (attr) {
            case "roleplayName":
                PlayerManager.fetchProfile(player.getUniqueId()).setRoleplayName(value);
                player.sendMessage(TextUtils.composeSuccessPrefix()
                        .append(TextUtils.composeSuccessText("Your roleplay name is now \""))
                        .append(Component.text(value, TextUtils.LIGHT_BRASS))
                        .append(TextUtils.composeSuccessText("\"!"))
                );
                break;
            default:
                player.sendMessage(TextUtils.composePlainErrorMessage("Unknown profile attribute!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> buildProfileFieldSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        builder.suggest("roleplayName");
        return builder.buildFuture();
    }
}

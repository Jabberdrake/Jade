package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.NamedTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;

public class TitleEditCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("title", StringArgumentType.word())
                        .suggests(CommonTitleSuggestions::suggestOwnedTitles)
                        .then(Commands.literal("name")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(TitleEditCommand::runCommandForName)))
                        .then(Commands.literal("display")
                                .then(Commands.argument("display", StringArgumentType.string()))
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(TitleEditCommand::runCommandForDisplay))
                )
                .build();
    }

    public static int runCommandForName(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleAsString = StringArgumentType.getString(context, "title");
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        JadeTitle title = jadePlayer.getTitleFromName(titleAsString);
        String newName = StringArgumentType.getString(context, "name");

        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        } else if (title.isUniversal()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You cannot edit universal titles!"));
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        } else if (!TitleManager.isUniqueName(newName)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("There is already a title with this reference name!"));
            return Command.SINGLE_SUCCESS;
        }

        Component oldTitleComponent = title.getTitleAsComponent()
                .append(Component.text(" "))
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(title.getName(), NamedTextColor.GRAY))
                .append(Component.text(")", NamedTextColor.GRAY));

        Component newTitleComponent = title.getTitleAsComponent()
                .append(Component.text(" "))
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(newName, NamedTextColor.GRAY))
                .append(Component.text(")", NamedTextColor.GRAY));

        title.setName(newName);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully edit the ")
                .append(oldTitleComponent)
                .append(TextUtils.composeSuccessText(" title to "))
                .append(newTitleComponent)
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDisplay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleAsString = StringArgumentType.getString(context, "title");
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        JadeTitle title = jadePlayer.getTitleFromName(titleAsString);
        String display = StringArgumentType.getString(context, "display");

        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        Component newDisplay = MiniMessage.miniMessage().deserialize(display);

        Component oldTitleComponent = title.getTitleAsComponent()
                .append(Component.text(" "))
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(title.getName(), NamedTextColor.GRAY))
                .append(Component.text(")", NamedTextColor.GRAY));

        Component newTitleComponent = newDisplay
                .append(Component.text(" "))
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(title.getName(), NamedTextColor.GRAY))
                .append(Component.text(")", NamedTextColor.GRAY));

        title.setTitle(display);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully edit the ")
                .append(oldTitleComponent)
                .append(TextUtils.composeSuccessText(" title to "))
                .append(newTitleComponent)
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

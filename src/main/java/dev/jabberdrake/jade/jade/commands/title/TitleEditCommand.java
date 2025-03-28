package dev.jabberdrake.jade.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.jade.players.JadeProfile;
import dev.jabberdrake.jade.jade.players.PlayerManager;
import dev.jabberdrake.jade.jade.titles.JadeTitle;
import dev.jabberdrake.jade.jade.titles.NamedTitle;
import dev.jabberdrake.jade.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
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
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .executes(TitleEditCommand::runCommandForName))))
                .build();
    }

    public static int runCommandForName(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleAsString = StringArgumentType.getString(context, "title");

        JadeProfile profile = PlayerManager.fetchProfile(player.getUniqueId());
        JadeTitle title = profile.getTitleFromName(titleAsString);
        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        Component oldTitleAsComponent = title.getTitleAsComponent();
        String newName = StringArgumentType.getString(context, "name");
        newName = newName.replace(" ", "_");
        Component newNameAsComponent = MiniMessage.miniMessage().deserialize(newName);
        String referenceName = PlainTextComponentSerializer.plainText().serialize(newNameAsComponent);

        List<String> ownedTitles = profile.getAvailableTitles().stream()
                .filter(jtitle -> !jtitle.equals(title) && jtitle.getOwner().equals(player.getUniqueId()))
                .map(NamedTitle::getName).toList();

        for (String titleName : ownedTitles) {
            if (titleName.equalsIgnoreCase(referenceName)) {
                player.sendMessage(TextUtils.composePlainErrorMessage("You already own a title with the specified name!"));
                return Command.SINGLE_SUCCESS;
            }
        }

        TitleManager.renameTitle(title, referenceName, newName);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully renamed the ")
                .append(oldTitleAsComponent)
                .append(TextUtils.composeSuccessText(" title to "))
                .append(newNameAsComponent)
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

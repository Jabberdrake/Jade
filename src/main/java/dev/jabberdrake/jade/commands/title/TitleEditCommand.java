package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static dev.jabberdrake.jade.utils.TextUtils.*;

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
        String titleArgument = StringArgumentType.getString(context, "title");

        JadeTitle title = PlayerManager.asJadePlayer(player.getUniqueId()).getTitleFromName(titleArgument);
        String nameArgument = StringArgumentType.getString(context, "name");
        if (title == null) {
            player.sendMessage(error("Could not find a title named <highlight>" + titleArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (title.isUniversal()) {
            player.sendMessage(error("You cannot edit <highlight>universal titles</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(error("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        } else if (!TitleManager.isUniqueName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        String oldTitle = title.getDisplayName() + "<normal> <gray>(" + title.getName() + ")</gray>";
        title.setName(nameArgument);

        for (UUID userID : title.getUserList()) {
            if (userID.equals(player.getUniqueId())) continue;

            OfflinePlayer oUser = Bukkit.getOfflinePlayer(userID);
            if (oUser.isOnline()) {
                Player user = (Player) oUser;
                user.sendMessage(info("The title of " + title.getDisplayName() + "<normal> has been edited: <highlight>reference name</highlight> is now <gray><bold>" + title.getName()));
            }
        }
        player.sendMessage(success("Changed the <highlight>reference name</highlight> of the title " + oldTitle + " to <highlight>" + title.getName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDisplay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleArgument = StringArgumentType.getString(context, "title");

        JadeTitle title = PlayerManager.asJadePlayer(player.getUniqueId()).getTitleFromName(titleArgument);
        String display = StringArgumentType.getString(context, "display");
        if (title == null) {
            player.sendMessage(error("Could not find a title named <highlight>" + titleArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (title.isUniversal()) {
            player.sendMessage(error("You cannot edit <highlight>universal titles</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(error("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        String oldTitle = title.getDisplayName() + "<normal> <gray>(" + title.getName() + ")</gray>";
        title.setDisplayName(display);

        for (UUID userID : title.getUserList()) {
            if (userID.equals(player.getUniqueId())) continue;

            OfflinePlayer oUser = Bukkit.getOfflinePlayer(userID);
            if (oUser.isOnline()) {
                Player user = (Player) oUser;
                user.sendMessage(info("The title of " + title.getDisplayName() + "<normal> has been edited: <highlight>display name</highlight> is now " + title.getDisplayName()));
            }
        }
        player.sendMessage(success("Changed the <highlight>display name</highlight> of the title " + oldTitle + " to <highlight>" + title.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}

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

public class TitleDeleteCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("title", StringArgumentType.word())
                        .suggests(CommonTitleSuggestions::suggestOwnedTitles)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(TitleDeleteCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleArgument = StringArgumentType.getString(context, "title");

        JadeTitle title = PlayerManager.asJadePlayer(player.getUniqueId()).getTitleFromName(titleArgument);
        if (title == null) {
            player.sendMessage(error("Could not find a title named <highlight>" + titleArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (title.isUniversal()) {
            player.sendMessage(error("You cannot delete <highlight>universal</highlight> titles!"));
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(error("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        TitleManager.deleteTitle(title);

        for (UUID userID : title.getUserList()) {
            if (userID.equals(player.getUniqueId())) continue;

            OfflinePlayer oUser = Bukkit.getOfflinePlayer(userID);
            if (oUser.isOnline()) {
                Player user = (Player) oUser;
                user.sendMessage(info("The title of " + title.getDisplayName() + "<normal> has been <red>deleted</red> by its owner!"));
            }
        }
        player.sendMessage(success("<red>Deleted</red> the title of " + title.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}

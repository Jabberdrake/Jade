package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class TitleRevokeCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("title", StringArgumentType.word())
                        .suggests(CommonTitleSuggestions::suggestOwnedTitles)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(CommonArgumentSuggestions::suggestAllOnlinePlayers)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(TitleRevokeCommand::runCommand)))
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
            player.sendMessage(error("You cannot revoke access to <highlight>universal titles</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(error("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't revoke yourself!"));
            player.sendMessage(info("To delete the title, do <highlight>/title delete <i><reference_name>"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.isAvailableTo(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + targetArgument + "</highlight>) is not allowed to use this title!"));
            return Command.SINGLE_SUCCESS;
        }

        TitleManager.revokeUseOfTitle(title, player.getUniqueId(), target.getUniqueId());

        player.sendMessage(success("<highlight>" + target.getName() + "</highlight> is no longer allowed to use to the title of " + title.getDisplayName() + "<normal>!"));
        if (target.isOnline()) {
            ((Player) target).sendMessage(info("<highlight>" + player.getName() + "</highlight> has <red>revoked</red> your use of the title of " + title.getDisplayName() + "<normal>!"));
        }
        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        String titleAsString = StringArgumentType.getString(context, "title");

        JadeTitle title = PlayerManager.asJadePlayer(player.getUniqueId()).getTitleFromName(titleAsString);
        if (title == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        } else if (title.isUniversal()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You cannot revoke access to universal titles!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't revoke yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.isAvailableTo(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This player is already not allowed to use this title!"));
            return Command.SINGLE_SUCCESS;
        }

        TitleManager.revokeUseOfTitle(title, player.getUniqueId(), targetUUID);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessHighlight(player.getName()))
                .append(TextUtils.composeSuccessText(" can no longer use the "))
                .append(title.getTitleAsComponent())
                .append(TextUtils.composeSuccessText(" title!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

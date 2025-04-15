package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

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
        String titleAsString = StringArgumentType.getString(context, "title");

        JadeTitle title = PlayerManager.asJadePlayer(player.getUniqueId()).getTitleFromName(titleAsString);
        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You do not own this title!"));
            return Command.SINGLE_SUCCESS;
        }

        TitleManager.deleteTitle(title);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully deleted the ")
                .append(title.getTitleAsComponent())
                .append(TextUtils.composeSuccessText(" title!"))
        );


        return Command.SINGLE_SUCCESS;
    }
}

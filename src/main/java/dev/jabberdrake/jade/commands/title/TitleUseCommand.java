package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class TitleUseCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("title", StringArgumentType.word())
                        .suggests(CommonTitleSuggestions::suggestAvailableTitles)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(TitleUseCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleArgument = StringArgumentType.getString(context, "title");

        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        JadeTitle title = jadePlayer.getTitleFromName(titleArgument);
        if (title == null) {
            player.sendMessage(error("Could not find a title named <highlight>" + titleArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        if (jadePlayer.getTitleInUse().equals(title)) {
            player.sendMessage(error("You are already using that title!"));
            return Command.SINGLE_SUCCESS;
        }

        jadePlayer.setTitleInUse(title);

        player.sendMessage(info("Now using the title of " + title.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}

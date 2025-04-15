package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

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
        String titleAsString = StringArgumentType.getString(context, "title");

        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        JadeTitle title = jadePlayer.getTitleFromName(titleAsString);
        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        }

        if (jadePlayer.getTitleInUse().equals(title)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You are already using that title!"));
            return Command.SINGLE_SUCCESS;
        }

        jadePlayer.setTitleInUse(title);

        player.sendMessage(TextUtils.composePlainInfoMessage("Now using the ")
                .append(title.getTitleAsComponent())
                .append(TextUtils.composeInfoText(" title!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

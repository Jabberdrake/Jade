package dev.jabberdrake.charter.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.charter.jade.players.JadeProfile;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.jade.titles.JadeTitle;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class TitleUseCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("title", StringArgumentType.word())
                        .suggests(CommonTitleSuggestions::suggestOwnedTitles)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(TitleUseCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String titleAsString = StringArgumentType.getString(context, "title");

        JadeProfile profile = PlayerManager.fetchProfile(player.getUniqueId());
        JadeTitle title = profile.getTitleFromName(titleAsString);
        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified title!"));
            return Command.SINGLE_SUCCESS;
        }

        if (!profile.setTitle(title)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Failed to set title! Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composePlainInfoMessage("Now using the ")
                .append(title.getTitleAsComponent())
                .append(TextUtils.composeInfoText(" title!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

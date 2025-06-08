package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class TitleCreateCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests(CommonArgumentSuggestions::suggestNamedTextColors)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(TitleCreateCommand::runCommand)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String nameArgument = StringArgumentType.getString(context, "name");
        nameArgument = nameArgument.replace(" ", "_");
        if (!TitleManager.isUniqueName(nameArgument)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("There is already a title with that name!"));
            return Command.SINGLE_SUCCESS;
        }

        String colorArgument = StringArgumentType.getString(context, "color");
        TextColor color = null;
        if (colorArgument.startsWith("#")) {
            color = TextColor.fromHexString(colorArgument);
            if (color == null) {
                player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not parse the specified color!"));
                return Command.SINGLE_SUCCESS;
            }
        } else if (NamedTextColor.NAMES.keys().contains(colorArgument)) {
            color = NamedTextColor.NAMES.value(colorArgument);
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not parse the specified color!"));
            return Command.SINGLE_SUCCESS;
        }

        JadeTitle createdTitle = TitleManager.createTitle(nameArgument, color, player.getUniqueId());
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Successfully created the ")
                .append(createdTitle.getTitleAsComponent())
                .append(TextUtils.composeSuccessText(" title!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

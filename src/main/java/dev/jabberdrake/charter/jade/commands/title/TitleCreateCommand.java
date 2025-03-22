package dev.jabberdrake.charter.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.jade.titles.JadeTitle;
import dev.jabberdrake.charter.jade.titles.TitleManager;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TitleCreateCommand {



    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests(TitleCreateCommand::suggestNamedTextColors)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(TitleCreateCommand::runCommand)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String nameArgument = StringArgumentType.getString(context, "name");
        nameArgument = nameArgument.replace(" ", "_");
        if (!TitleManager.isUniqueName(player.getUniqueId(), nameArgument)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You already own a title with that name!"));
            return Command.SINGLE_SUCCESS;
        }

        String colorArgument = StringArgumentType.getString(context, "color");
        TextColor color = null;
        if (colorArgument.startsWith("#")) {
            color = TextColor.fromHexString(colorArgument);
            if (color == null) {
                player.sendMessage(TextUtils.composePlainErrorMessage("Could not parse the specified color!"));
                return Command.SINGLE_SUCCESS;
            }
        } else if (NamedTextColor.NAMES.keys().contains(colorArgument)) {
            color = NamedTextColor.NAMES.value(colorArgument);
        } else {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not parse the specified color!"));
            return Command.SINGLE_SUCCESS;
        }

        JadeTitle createdTitle = TitleManager.createTitle(nameArgument, color, player.getUniqueId());
        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully created the ")
                .append(createdTitle.getTitleAsComponent())
                .append(TextUtils.composeSuccessText(" title!"))
        );

        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> suggestNamedTextColors(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {

        NamedTextColor.NAMES.keys().forEach(builder::suggest);

        return builder.buildFuture();
    }
}

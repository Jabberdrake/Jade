package dev.jabberdrake.jade.commands.title;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.titles.JadeTitle;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.utils.JadeTextColor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class TitleCreateCommand {

    private static final int MAX_NAME_LENGTH = 20;

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests(CommonArgumentSuggestions::suggestVanillaTextColors)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(TitleCreateCommand::runCommand)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String nameArgument = StringArgumentType.getString(context, "name");
        nameArgument = nameArgument.replace(" ", "_");
        if (!TitleManager.isUniqueName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        } else if (nameArgument.length() > 20) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is too long! Title names should not be longer than 20 characters."));
            return Command.SINGLE_SUCCESS;
        }

        String colorArgument = StringArgumentType.getString(context, "color");
        TextColor color = JadeTextColor.fromNameOrHexstring(colorArgument, true);
        if (color == null) {
            player.sendMessage(error("Please provide a valid color name or hexstring!"));
            player.sendMessage(info("A hexstring is composed of a <highlight>#</highlight> and six <highlight>hexadecimal</highlight> characters (0-9 and a-f), e.g.: <gold>#ffaa00"));
            player.sendMessage(info("This input recognizes all default Minecraft colors. For a full list of valid names, see this " +
                    "<hover:show_text:'<zorba>Clicking this text will open </zorba><aqua>https://minecraft.wiki/w/Formatting_codes#Color_codes</aqua>'>" +
                    "<click:open_url:https://minecraft.wiki/w/Formatting_codes#Color_codes>webpage</click>" +
                    "</hover>."));
            return Command.SINGLE_SUCCESS;
        }

        JadeTitle createdTitle = TitleManager.createTitle(nameArgument, color, player.getUniqueId());
        player.sendMessage(success("Created the title of " + createdTitle.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}

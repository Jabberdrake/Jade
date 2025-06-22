package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.title.*;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.JadeTitle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class TitleCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .requires(sender -> sender.getExecutor() instanceof Player)
                    .executes(TitleCommand::runCommand)
                .then(TitleCreateCommand.buildCommand("create"))
                .then(TitleDeleteCommand.buildCommand("delete"))
                .then(TitleEditCommand.buildCommand("edit"))
                .then(TitleListCommand.buildCommand("list"))
                .then(TitleUseCommand.buildCommand("use"))
                .then(TitleAllowCommand.buildCommand("allow"))
                .then(TitleRevokeCommand.buildCommand("revoke"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("The help menu for this command is still under development, sorry! In the meantime, if you have any questions, please ask the developer!"));
        return Command.SINGLE_SUCCESS;
    }
}

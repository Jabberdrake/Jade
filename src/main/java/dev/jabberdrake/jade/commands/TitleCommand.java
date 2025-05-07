package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.title.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class TitleCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(TitleCommand::runCommand)
                .then(TitleCreateCommand.buildCommand("create"))
                .then(TitleDeleteCommand.buildCommand("delete"))
                .then(TitleEditCommand.buildCommand("edit"))
                .then(TitleUseCommand.buildCommand("use"))
                .then(TitleAllowCommand.buildCommand("allow"))
                .then(TitleRevokeCommand.buildCommand("revoke"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("debi is a homo");
        return Command.SINGLE_SUCCESS;
    }
}

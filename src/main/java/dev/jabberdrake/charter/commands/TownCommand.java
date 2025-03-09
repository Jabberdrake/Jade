package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class TownCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(TownCommand::runCommand)
                .then(TownCreateCommand.buildCommand("create"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("you did /town or /plugin town!");
        return Command.SINGLE_SUCCESS;
    }
}

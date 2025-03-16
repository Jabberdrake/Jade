package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class SettlementCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(SettlementCommand::runCommand)
                .then(SettlementListCommand.buildCommand("list"))
                .then(SettlementInfoCommand.buildCommand("info"))
                .then(SettlementEditCommand.buildCommand("edit"))
                .then(SettlementCreateCommand.buildCommand("create"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("dotto is a homo");
        return Command.SINGLE_SUCCESS;
    }
}

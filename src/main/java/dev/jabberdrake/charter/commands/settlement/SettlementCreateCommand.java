package dev.jabberdrake.charter.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class SettlementCreateCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(SettlementCreateCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("creating big boy towns...");
        return Command.SINGLE_SUCCESS;
    }
}

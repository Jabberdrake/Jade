package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.commands.settlement.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class SettlementCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(SettlementCommand::runCommand)
                .then(SettlementListCommand.buildCommand("list"))
                .then(SettlementInfoCommand.buildCommand("info"))
                .then(SettlementEditCommand.buildCommand("edit"))
                .then(SettlementManageCommand.buildCommand("manage"))
                .then(SettlementClaimCommand.buildCommand("claim"))
                .then(SettlementMapCommand.buildCommand("map"))
                .then(SettlementViewCommand.buildCommand("view"))
                .then(SettlementFocusCommand.buildCommand("focus"))
                .then(SettlementJoinCommand.buildCommand("join"))
                .then(SettlementCreateCommand.buildCommand("create"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("dotto is a homo");
        return Command.SINGLE_SUCCESS;
    }
}

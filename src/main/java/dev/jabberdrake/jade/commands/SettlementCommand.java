package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class SettlementCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(SettlementCommand::runCommand)
                .then(SettlementListCommand.buildCommand("list")) //NEEDS WORK
                .then(SettlementInfoCommand.buildCommand("info"))
                .then(SettlementCreateCommand.buildCommand("create"))
                .then(SettlementInviteCommand.buildCommand("invite"))
                .then(SettlementKickCommand.buildCommand("kick"))
                .then(SettlementJoinCommand.buildCommand("join"))
                .then(SettlementClaimCommand.buildCommand("claim"))
                .then(SettlementUnclaimCommand.buildCommand("unclaim"))
                .then(SettlementManageCommand.buildCommand("manage")) //NEEDS WORK
                .then(SettlementPromoteCommand.buildCommand("promote"))
                .then(SettlementDemoteCommand.buildCommand("demote"))
                .then(SettlementEditCommand.buildCommand("edit"))
                .then(SettlementDisbandCommand.buildCommand("disband"))
                .then(SettlementMapCommand.buildCommand("map"))
                .then(SettlementFoodCommand.buildCommand("food"))
                .then(SettlementViewCommand.buildCommand("view"))
                .then(SettlementFocusCommand.buildCommand("focus"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("dotto is a homo");
        return Command.SINGLE_SUCCESS;
    }
}

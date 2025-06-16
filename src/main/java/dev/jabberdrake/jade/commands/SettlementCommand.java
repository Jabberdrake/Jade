package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.*;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class SettlementCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(SettlementCommand::runCommand)
                .then(SettlementListCommand.buildCommand("list"))
                .then(SettlementInfoCommand.buildCommand("info"))
                .then(SettlementCreateCommand.buildCommand("create"))
                .then(SettlementInviteCommand.buildCommand("invite"))
                .then(SettlementKickCommand.buildCommand("kick"))
                .then(SettlementJoinCommand.buildCommand("join"))
                .then(SettlementLeaveCommand.buildCommand("leave"))
                .then(SettlementClaimCommand.buildCommand("claim"))
                .then(SettlementUnclaimCommand.buildCommand("unclaim"))
                .then(SettlementManageCommand.buildCommand("manage"))
                .then(SettlementTransferCommand.buildCommand("transfer"))
                .then(SettlementPromoteCommand.buildCommand("promote"))
                .then(SettlementDemoteCommand.buildCommand("demote"))
                .then(SettlementEditCommand.buildCommand("edit"))
                .then(SettlementDisbandCommand.buildCommand("disband"))
                .then(SettlementMapCommand.buildCommand("map"))
                .then(SettlementFoodCommand.buildCommand("food"))
                .then(SettlementViewCommand.buildCommand("view"))
                .then(SettlementFocusCommand.buildCommand("focus"))
                .then(SettlementSettingsCommand.buildCommand("settings"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("dotto is a homo");
        return Command.SINGLE_SUCCESS;
    }

    // Auxiliary methods for child command nodes
    public static boolean validateFocusSettlement(Player player, Settlement settlement) {
        if (settlement == null) {
            player.sendMessage(error("You are not focusing on any settlement."));
            return false;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(error("You are not a member of " + settlement.getDisplayName() + "!"));
            return false;
        }
        return true;
    }

    public static boolean validateFocusLeadership(Player player, Settlement settlement) {
        // IMPORTANT: This method assumes that validateFocusSettlement has been run beforehand, and that:
        //      1. settlement is NOT null;
        //      2. settlement contains player;
        if (!settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(error("You are not the leader of this settlement!"));
            return false;
        } else return true;
    }
}

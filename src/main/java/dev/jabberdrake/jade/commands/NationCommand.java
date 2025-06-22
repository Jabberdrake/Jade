package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.nation.*;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class NationCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationCommand::runCommand)
                .then(NationInfoCommand.buildCommand("info"))
                .then(NationListCommand.buildCommand("list"))
                .then(NationProclaimCommand.buildCommand("proclaim"))
                .then(NationEditCommand.buildCommand("edit"))
                .then(NationInviteCommand.buildCommand("invite"))
                .then(NationKickCommand.buildCommand("kick"))
                .then(NationJoinCommand.buildCommand("join"))
                .then(NationLeaveCommand.buildCommand("leave"))
                .then(NationTransferCommand.buildCommand("transfer"))
                .then(NationDissolveCommand.buildCommand("dissolve"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("The help menu for this command is still under development, sorry! In the meantime, if you have any questions, please ask the developer!"));
        return Command.SINGLE_SUCCESS;
    }

    // Auxiliary methods for child command nodes
    public static boolean validateCapitalStatus(Player player, Settlement settlement) {
        if (!settlement.isInNation()) {
            player.sendMessage(error("This settlement is not part of a nation!"));
            return false;
        } else if (settlement.getNation().getCapital().getId() != settlement.getId()) {
            player.sendMessage(error("This settlement is not the capital of a nation!"));
            return false;
        } else return true;
    }
}

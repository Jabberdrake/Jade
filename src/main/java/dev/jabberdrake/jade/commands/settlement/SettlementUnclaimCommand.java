package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.success;

public class SettlementUnclaimCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementUnclaimCommand::runCommandWithoutArgs)
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Chunk currentChunk = player.getLocation().getChunk();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (RealmManager.unclaimChunk(focus, currentChunk)) {
            player.sendMessage(success("Unclaimed this chunk for " + focus.getDisplayNameAsString() + "<normal>!"));
        } else {
            player.sendMessage(error("This chunk is not claimed by your settlement (<highlight>" + focus.getDisplayNameAsString() + "<normal>)!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canUnclaim()) {
            player.sendMessage(error("You are not allowed to unclaim chunks for <highlight>" + settlement.getDisplayNameAsString() + "<normal>!"));
            return false;
        }
        return true;
    }
}

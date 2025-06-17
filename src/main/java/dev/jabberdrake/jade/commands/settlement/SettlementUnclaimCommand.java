package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

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

        if (focus.getTerritory().size() <= 1) {
            player.sendMessage(error("This settlement (<highlight>" + focus.getName() + "</highlight>) only has one chunk of territory left!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.hasAreaInChunk(new ChunkAnchor(currentChunk))) {
            player.sendMessage(error("There is an area in this settlement (<highlight>" + focus.getName() + "</highlight>) occupying this chunk!"));
            return Command.SINGLE_SUCCESS;
        }

        if (RealmManager.unclaimChunk(focus, currentChunk)) {
            player.sendMessage(success("Unclaimed this chunk for " + focus.getDisplayName() + "<normal>!"));
        } else {
            player.sendMessage(error("This chunk is not claimed by your settlement (<highlight>" + focus.getDisplayName() + "<normal>)!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canUnclaim()) {
            player.sendMessage(error("You are not allowed to unclaim chunks for <highlight>" + settlement.getDisplayName() + "<normal>!"));
            return false;
        }
        return true;
    }
}

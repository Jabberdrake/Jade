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

public class SettlementUnclaimCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementClaimCommand::runCommandWithoutArgs)
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Chunk currentChunk = player.getLocation().getChunk();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (RealmManager.unclaimChunk(focus, currentChunk)) {
            player.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You have successfully unclaimed this chunk for "))
                    .append(focus.getDisplayName())
                    .append(TextUtils.composeSuccessText("!"))
            );
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This chunk is not claimed by ")
                    .append(TextUtils.composeErrorHighlight(RealmManager.getChunkOwner(currentChunk).getName()))
                    .append(TextUtils.composeErrorText("!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canUnclaim()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not allowed to unclaim chunks for ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }
        return true;
    }
}

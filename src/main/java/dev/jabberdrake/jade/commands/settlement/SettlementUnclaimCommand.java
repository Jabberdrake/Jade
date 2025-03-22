package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.jade.players.PlayerManager;
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

        Settlement settlement = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }

        if (RealmManager.unclaimChunk(settlement, currentChunk)) {
            player.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You have successfully unclaimed this chunk for "))
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeSuccessText("!"))
            );
        } else {
            player.sendMessage(TextUtils.composePlainErrorMessage("This chunk is not claimed by ")
                    .append(TextUtils.composeErrorHighlight(RealmManager.getChunkOwner(currentChunk).getName()))
                    .append(TextUtils.composeErrorText("!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public static boolean performCommonChecks(Player player, Settlement settlement) {

        if (settlement == null) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not focusing on any settlement."));
            return false;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        } else if (!settlement.getTitleFromMember(player.getUniqueId()).canUnclaim()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not allowed to unclaim chunks for ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }

        return true;
    }
}

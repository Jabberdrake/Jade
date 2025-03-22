package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.suggestions.CommonSettlementSuggestions;
import dev.jabberdrake.jade.jade.players.JadePlayer;
import dev.jabberdrake.jade.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Set;

public class SettlementClaimCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementClaimCommand::runCommandWithoutArgs)
                .then(Commands.literal("square")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 5))
                            .suggests(CommonSettlementSuggestions::buildSuggestionsForSettlementsWithPlayer)
                            .requires(sender -> sender.getExecutor() instanceof Player)
                            .executes(SettlementClaimCommand::runCommandForSquare)))
                .then(Commands.literal("auto")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementClaimCommand::runCommandForAuto))
                .then(Commands.literal("fill")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementClaimCommand::runCommandForFill))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Chunk currentChunk = player.getLocation().getChunk();

        Settlement settlement = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }

        if (RealmManager.claimChunk(settlement, currentChunk)) {
            player.sendMessage(TextUtils.composeSuccessPrefix()
                    .append(TextUtils.composeSuccessText("You have successfully claimed this chunk for "))
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeSuccessText("!"))
            );
        } else {
            player.sendMessage(TextUtils.composePlainErrorMessage("This chunk is already claimed by ")
                    .append(TextUtils.composeErrorHighlight(RealmManager.getChunkOwner(currentChunk).getName()))
                    .append(TextUtils.composeErrorText("!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForSquare(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        Settlement settlement = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }

        Chunk currentChunk = player.getLocation().getChunk();

        int cX = currentChunk.getX();
        int cZ = currentChunk.getZ();

        int value = IntegerArgumentType.getInteger(context, "value");
        int claimedCount = 0;
        for (int aX = -value; aX <= value; aX++) {
            for (int aZ = -value; aZ <= value; aZ++) {
                ChunkAnchor aux = new ChunkAnchor(cX + aX, cZ + aZ);
                Settlement auxOwner = RealmManager.getChunkOwner(aux);

                if (!(auxOwner == null)) { continue;}
                RealmManager.claimChunk(settlement, aux);
                claimedCount++;
            }
        }

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("You have successfully claimed "))
                .append(TextUtils.composeSuccessHighlight(String.valueOf(claimedCount)))
                .append(TextUtils.composeSuccessText(" chunks for "))
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForAuto(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        Settlement settlement = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }


        JadePlayer jadePlayer = PlayerManager.parsePlayer(player.getUniqueId());

        jadePlayer.toggleAutoclaim();

        if (jadePlayer.isAutoclaiming()) {
            player.sendMessage(TextUtils.composePlainSuccessMessage("Toggled autoclaim ")
                    .append(TextUtils.composeSuccessHighlight("ON").decorate(TextDecoration.BOLD))
                    .append(TextUtils.composeSuccessText("! Keep walking to claim chunks for "))
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeSuccessText("!"))
            );

            RealmManager.claimChunk(settlement, player.getChunk());

        } else {
            player.sendMessage(TextUtils.composePlainSuccessMessage("Toggled autoclaim ")
                    .append(TextUtils.composeSuccessHighlight("OFF").decorate(TextDecoration.BOLD))
                    .append(TextUtils.composeSuccessText("!"))
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForFill(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ChunkAnchor anchor = new ChunkAnchor(player.getChunk());

        Settlement settlement = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }

        Set<ChunkAnchor> chunksToClaim = RealmManager.prepareRecursiveChunkClaim(settlement, anchor);
        if (chunksToClaim == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Too many chunks to claim at once! Are you sure the border is closed?"));
            return Command.SINGLE_SUCCESS;
        }

        int chunkCount = 0;
        for (ChunkAnchor chunk : chunksToClaim) {
            RealmManager.claimChunk(settlement, chunk);
            chunkCount++;
        }

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("You have successfully claimed "))
                .append(TextUtils.composeSuccessHighlight(String.valueOf(chunkCount)))
                .append(TextUtils.composeSuccessText(" chunks for "))
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
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
        } else if (!settlement.getTitleFromMember(player.getUniqueId()).canClaim()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not allowed to claim chunks for ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }

        return true;
    }
}

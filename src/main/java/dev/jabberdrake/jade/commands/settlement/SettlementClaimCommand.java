package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.JadeConfig;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class SettlementClaimCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementClaimCommand::runCommandWithoutArgs)
                .then(Commands.literal("square")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 5))
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
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateGameworld(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (!validateWorldspawnDistance(player, new ChunkAnchor(currentChunk))) {
            player.sendMessage(error("This chunk is too close to the worldspawn!"));
            return Command.SINGLE_SUCCESS;
        }

        if (RealmManager.claimChunk(focus, currentChunk)) {
            player.sendMessage(success("Claimed this chunk for <highlight>" + focus.getDisplayName() + "<normal>!"));
        } else if (!RealmManager.isUnclaimedChunk(currentChunk)) {
            player.sendMessage(error("This chunk is already claimed by <highlight>" + RealmManager.getChunkOwner(currentChunk).getName() + "<normal>!"));
        } else if (focus.getFood() < JadeConfig.chunkCost) {
            player.sendMessage(error("Not enough food!"));
            player.sendMessage(info("Remember: each chunk costs <highlight>" + JadeConfig.chunkCost + "</highlight> to claim!"));
            player.sendMessage(info("To check how much food you have stored, do <highlight>/settlement food</highlight> or <highlight>/settlement info</highlight>!"));
        } else {
            player.sendMessage(error("oepsie woepsie! de plugin is stukkie wukkie!!!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForSquare(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateGameworld(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        Chunk currentChunk = player.getLocation().getChunk();

        World cWorld = currentChunk.getWorld();
        int cX = currentChunk.getX();
        int cZ = currentChunk.getZ();

        boolean worldspawnFlag = false;

        int value = IntegerArgumentType.getInteger(context, "value");
        Set<ChunkAnchor> chunksToClaim = new HashSet<>();
        for (int aX = -value; aX <= value; aX++) {
            for (int aZ = -value; aZ <= value; aZ++) {
                ChunkAnchor aux = new ChunkAnchor(cWorld, cX + aX, cZ + aZ);
                Settlement auxOwner = RealmManager.getChunkOwner(aux);

                if (auxOwner != null) { continue; }
                if (!validateWorldspawnDistance(player, aux)) {
                    worldspawnFlag = true;
                    continue;
                }
                chunksToClaim.add(aux);
            }
        }

        if (focus.getFood() < chunksToClaim.size() * JadeConfig.chunkCost) {
            player.sendMessage(error("You do not have enough food to claim <highlight>" + chunksToClaim.size() + "</highlight> chunks!"));
            player.sendMessage(info("Remember: each chunk costs <highlight>" + JadeConfig.chunkCost + "</highlight> to claim!"));
            player.sendMessage(info("To check how much food you have stored, do <highlight>/settlement food</highlight> or <highlight>/settlement info</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        if (worldspawnFlag) {
            player.sendMessage(error("Some of the chunks you tried to claim were far too close to the worldspawn!"));
        }

        for (ChunkAnchor chunkToClaim : chunksToClaim) {
            RealmManager.claimChunk(focus, chunkToClaim);
        }

        player.sendMessage(success("Claimed <highlight>" + chunksToClaim.size() + "</highlight> chunks for " + focus.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForAuto(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateGameworld(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        jadePlayer.toggleAutoclaim();

        if (jadePlayer.isAutoclaiming()) {
            player.sendMessage(info("Toggled autoclaim <green><bold>ON</bold></green>! Keep walking to claim chunks for " + focus.getDisplayName() + "<normal>!"));
            if (validateWorldspawnDistance(player, new ChunkAnchor(player.getChunk()))) {
                RealmManager.claimChunk(focus, player.getChunk());
            }

        } else {
            player.sendMessage(info("Toggled autoclaim <red><bold>OFF</bold></red>!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForFill(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ChunkAnchor anchor = new ChunkAnchor(player.getChunk());
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateGameworld(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        Set<ChunkAnchor> chunksToClaim = RealmManager.prepareRecursiveChunkClaim(focus, anchor);
        if (chunksToClaim == null) {
            player.sendMessage(error("Too many chunks to claim at once! Are you sure the border is closed?"));
            return Command.SINGLE_SUCCESS;
        } else if (chunksToClaim.size() * JadeConfig.chunkCost > focus.getFood()) {
            player.sendMessage(error("You do not have enough food to claim <highlight>" + chunksToClaim.size() + "</highlight> chunks!"));
            player.sendMessage(info("Remember: each chunk costs <highlight>" + JadeConfig.chunkCost + "</highlight> to claim!"));
            player.sendMessage(info("To check how much food you have stored, do <highlight>/settlement food</highlight> or <highlight>/settlement info</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        int chunkCount = 0;
        for (ChunkAnchor chunk : chunksToClaim) {
            RealmManager.claimChunk(focus, chunk);
            chunkCount++;
        }

        player.sendMessage(success("Claimed <highlight>" + chunkCount + "</highlight> chunks for " + focus.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canClaim()) {
            player.sendMessage(error("You are not allowed to claim chunks for <highlight>" + settlement.getName() + "<normal>!"));
            return false;
        }
        return true;
    }

    public static boolean validateGameworld(Player player, Settlement settlement) {
        if (!player.getLocation().getWorld().equals(settlement.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + settlement.getDisplayName() + "</highlight>) can only hold territory in the <highlight>" + settlement.getWorld().getName() + "</highlight> world!"));
            return false;
        }
        return true;
    }

    public static boolean validateWorldspawnDistance(Player player, ChunkAnchor anchor) {
        if (player.isOp()) {
            return true;
        } else return anchor.getManhattanDistanceTo(new ChunkAnchor(player.getWorld().getSpawnLocation().getChunk())) > 3;
    }
}

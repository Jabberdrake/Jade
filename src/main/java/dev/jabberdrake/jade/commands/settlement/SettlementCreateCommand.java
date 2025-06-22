package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.JadeConfig;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class SettlementCreateCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.word())
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementCreateCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ChunkAnchor anchor = new ChunkAnchor(player.getChunk());

        if (!JadeConfig.gameworlds.contains(player.getWorld().getName())) {
            player.sendMessage(error("Cannot create a settlement in this world (<highlight>" + player.getWorld().getName() + "</highlight>)! If you believe this is in error, please contact an administrator!"));
            return Command.SINGLE_SUCCESS;
        }

        if (!RealmManager.isUnclaimedChunk(anchor)) {
            player.sendMessage(error("Cannot create a settlement on a claimed chunk!"));
            return Command.SINGLE_SUCCESS;
        } else if (!SettlementClaimCommand.validateWorldspawnDistance(player, anchor)) {
            player.sendMessage(error("Cannot create a settlement on chunk so close to the worldspawn!"));
            return Command.SINGLE_SUCCESS;
        }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueSettlementName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        Settlement settlement = RealmManager.createSettlement(nameArgument, player, anchor);

        PlayerManager.asJadePlayer(player.getUniqueId()).setFocusSettlement(settlement);

        player.sendMessage(success("Created the settlement of " + settlement.getDisplayName() + "<normal>!"));
        Bukkit.broadcast(info("<highlight>" + player.getName() + "</highlight> has created the settlement of " + settlement.getDisplayName() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateGameworld(Player player, Settlement settlement) {
        if (!player.getLocation().getWorld().equals(settlement.getWorld())) {
            player.sendMessage(error("This settlement (<highlight>" + settlement.getDisplayName() + "</highlight>) can only hold territory in the <highlight>" + settlement.getWorld().getName() + "</highlight> world!"));
            return false;
        }
        return true;
    }
}
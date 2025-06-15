package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
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

        if (!RealmManager.isUnclaimedChunk(anchor)) {
            player.sendMessage(error("Cannot create a settlement on a claimed chunk!"));
            return Command.SINGLE_SUCCESS;
        }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueSettlementName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        Settlement settlement = RealmManager.createSettlement(nameArgument, player, anchor);

        PlayerManager.asJadePlayer(player.getUniqueId()).setFocusSettlement(settlement);

        player.sendMessage(success("Created the settlement of " + settlement.getDisplayNameAsString() + "!"));
        Bukkit.broadcast(info("<highlight>" + player.getName() + "</highlight> has created the settlement of " + settlement.getDisplayNameAsString() + "<normal>!"));
        return Command.SINGLE_SUCCESS;
    }
}
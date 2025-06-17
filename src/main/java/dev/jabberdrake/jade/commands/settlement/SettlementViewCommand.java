package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementViewCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementViewCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());

        jadePlayer.toggleBorderview();

        if (jadePlayer.isViewingBorders()) {
            player.sendMessage(info("You are now viewing settlement borders around you!"));
        } else {
            player.sendMessage(info("You are no longer viewing settlement borders!"));
        }

        return Command.SINGLE_SUCCESS;
    }
}

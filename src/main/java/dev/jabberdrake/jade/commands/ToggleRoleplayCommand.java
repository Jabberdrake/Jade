package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.success;

public class ToggleRoleplayCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ToggleRoleplayCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        jadePlayer.toggleRoleplay();

        if (jadePlayer.isInRoleplay()) {
            player.sendMessage(success("You are now in roleplay mode!"));
        } else {
            player.sendMessage(success("You have left roleplay mode!"));
        }
        return Command.SINGLE_SUCCESS;
    }
}

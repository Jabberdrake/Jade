package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleRoleplayCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(ToggleRoleplayCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player)) {
            Jade.getPlugin(Jade.class).getLogger().warning("[ToggleRoleplayCommand::runCommand] Only players can run this command!");
        }

        Player player = (Player) sender;
        JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
        jadePlayer.toggleRoleplay();

        if (jadePlayer.isInRoleplay()) {
            player.sendMessage(TextUtils.composeSimpleSuccessMessage("You are now in roleplay mode!"));
        } else {
            player.sendMessage(TextUtils.composeSimpleSuccessMessage("You have left roleplay mode!"));
        }
        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.commands.profile.ProfileEditCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class ProfileCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ProfileCommand::runCommand)
                .then(ProfileEditCommand.buildCommand("edit"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String roleplayName = PlayerManager.asJadePlayer(player.getUniqueId()).getRoleplayName();

        player.sendMessage(info("Roleplay name: \"<light_brass>" + roleplayName + "</light_brass>\"!"));
        return Command.SINGLE_SUCCESS;
    }
}

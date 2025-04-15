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

public class ProfileCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(ProfileCommand::runCommand)
                .then(ProfileEditCommand.buildCommand("edit"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player)) {
            Jade.getPlugin(Jade.class).getLogger().warning("[ProfileEditCommand::runCommand] Only players can run this command!");
        }

        Player player = (Player) sender;
        String roleplayName = PlayerManager.asJadePlayer(player.getUniqueId()).getRoleplayName();
        player.sendMessage(TextUtils.composeInfoPrefix()
                .append(TextUtils.composeInfoText("Roleplay name: \""))
                .append(Component.text(roleplayName, TextUtils.LIGHT_BRASS))
                .append(TextUtils.composeInfoText("\"!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

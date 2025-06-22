package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.profile.ProfileSettingsCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class JadeCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .requires(sender -> sender.getExecutor() instanceof Player)
                    .executes(HelpCommand::runCommand)
                .then(AdminCommand.buildCommand("admin"))
                .then(HelpCommand.buildCommand("help"))
                .then(ProfileCommand.buildCommand("profile"))
                .then(SettlementCommand.buildCommand("settlement"))
                .then(NationCommand.buildCommand("nation"))
                .then(GraveCommand.buildCommand("grave"))
                .then(ToggleRoleplayCommand.buildCommand("toggleroleplay"))
                .build();
    }
}

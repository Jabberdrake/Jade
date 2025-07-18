package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.admin.AdminBackupCommand;
import dev.jabberdrake.jade.commands.admin.AdminDumpCommand;
import dev.jabberdrake.jade.commands.admin.AdminGameruleCommand;
import dev.jabberdrake.jade.commands.admin.AdminMakeVirtualCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class AdminCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .requires(sender -> sender.getExecutor() instanceof Player)
                    .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                    .executes(AdminCommand::runCommand)
                .then(AdminGameruleCommand.buildCommand("gamerule"))
                .then(AdminDumpCommand.buildCommand("dump"))
                .then(AdminMakeVirtualCommand.buildCommand("makevirtual"))
                .then(AdminBackupCommand.buildCommand("backup"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("The help menu for this command is still under development, sorry! In the meantime, if you have any questions, please ask the developer!"));
        return Command.SINGLE_SUCCESS;
    }
}

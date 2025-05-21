package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.admin.AdminDumpCommand;
import dev.jabberdrake.jade.commands.admin.AdminGameruleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class AdminCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(AdminCommand::runCommand)
                .then(AdminGameruleCommand.buildCommand("gamerule"))
                .then(AdminDumpCommand.buildCommand("dump"))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("adminstuff!");
        return Command.SINGLE_SUCCESS;
    }
}

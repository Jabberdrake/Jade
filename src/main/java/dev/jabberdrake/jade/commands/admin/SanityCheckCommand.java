package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.database.DatabaseManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.system;

public class SanityCheckCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                .executes(SanityCheckCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {

        Bukkit.broadcast(system("An operator has triggered the sanity check routine. This may cause some lag..."));
        DatabaseManager.sanityCheck();
        Bukkit.broadcast(system("Sanity check routine finished!"));

        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BuildworldCommand {

    private static final String BUILDWORLD = "build";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {

        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(BuildworldCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        World world = player.getWorld();

        if (world.getName().contains(BUILDWORLD)) {
            player.performCommand("world group teleport unowned");
        } else {
            player.performCommand("world group teleport build");
        }
        return Command.SINGLE_SUCCESS;
    }
}

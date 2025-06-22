package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class HelpCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {

        return Commands.literal(label)
                    .requires(sender -> sender.getExecutor() instanceof Player)
                    .executes(HelpCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(info("The help menu for this command is still under development, sorry! In the meantime, if you have any questions, please ask the developer!"));
        return Command.SINGLE_SUCCESS;
    }
}

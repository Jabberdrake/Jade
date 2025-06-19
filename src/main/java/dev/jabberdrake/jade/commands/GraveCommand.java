package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.menus.implementations.GraveListMenu;
import dev.jabberdrake.jade.menus.implementations.GraveOpenMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class GraveCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(GraveCommand::runCommandForList)
                .then(Commands.literal("list")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(GraveCommand::runCommandForList)
                )
                .then(Commands.literal("open")
                        .then(Commands.argument("graveID", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(GraveCommand::runCommandForOpen)
                        )
                )
                .build();
    }

    public static int runCommandForList(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        new GraveListMenu().open(player);
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForOpen(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String graveArgument = StringArgumentType.getString(context, "graveID");
        Grave grave = null;
        for (Grave potentialGrave : PlayerManager.getAllGravesForPlayer(player.getUniqueId())) {
            if (potentialGrave.getID().equalsIgnoreCase(graveArgument)) {
                grave = potentialGrave;
                break;
            }
        }

        if (grave == null) {
            player.sendMessage(error("Could not find a grave with the ID of <highlight>" + graveArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!grave.isVirtual()) {
            player.sendMessage(error("This grave is not missing or obstructed, so it cannot be opened via commands!"));
            return Command.SINGLE_SUCCESS;
        }

        new GraveOpenMenu(grave).open(player);
        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.GraveCommand;
import dev.jabberdrake.jade.menus.implementations.GraveOpenMenu;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.players.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.system;

public class AdminMakeVirtualCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(AdminMakeVirtualCommand::runCommandWithoutArgs)
                .then(Commands.argument("graveID", StringArgumentType.word())
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .suggests(CommonArgumentSuggestions::suggestAllGraves)
                        .executes(AdminMakeVirtualCommand::runCommandWithArgs)
                )
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(system("Please specify a grave ID!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
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
        } else if (grave.isVirtual()) {
            player.sendMessage(error("This grave is already virtual!"));
            return Command.SINGLE_SUCCESS;
        }

        grave.makeVirtual();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(grave.getPlayerID());
        if (owner.isOnline())
            ((Player) owner).sendMessage(system("An operator has made your grave <highlight>" + grave.getID() + "</highlight> virtual. You can now access it by doing <highlight>/grave open " + grave.getID() + "</highlight>!"));
        player.sendMessage(system("Grave <highlight>" + grave.getID() + "</highlight> is now virtual!"));
        return Command.SINGLE_SUCCESS;
    }
}

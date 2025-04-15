package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdminSmiteCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(AdminSmiteCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        UUID goblinID = UUID.fromString("c6753bfe-2922-4074-9cc2-78d1f3028e8b");

        if (Bukkit.getPlayer(goblinID) != null) {
            Player goblin = Bukkit.getPlayer(goblinID);
            Bukkit.broadcast(Component.text(goblin.getAddress().toString()));
        } else {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Target is not online."));
        }
        
        return Command.SINGLE_SUCCESS;
    }
}

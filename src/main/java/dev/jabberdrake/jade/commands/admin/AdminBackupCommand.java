package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.DatabaseManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.system;

public class AdminBackupCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(AdminBackupCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (!player.hasPermission("jade.admin")) {
            player.sendMessage(error("Insufficient permission!"));
        }

        File backupsDir = new File(Jade.getInstance().getDataFolder(), "backups");
        if (!backupsDir.exists() || !backupsDir.isDirectory()) {
            player.sendMessage(error("Backups folder could not be found..."));
            return Command.SINGLE_SUCCESS;
        }

        Bukkit.broadcast(system("An operator has triggered the backup routine..."));
        String backup = DatabaseManager.backup();
        Bukkit.broadcast(system("Backup routine finished!"));
        if (backup == null) {
            Bukkit.broadcast(error("A severe error occurred during the backup routine. Please check the server console."), "jade.admin");
        } else {
            Bukkit.broadcast(system("The newest backup has been saved to <highlight>.../plugins/Jade/backups/" + backup + "</highlight>!"), "jade.admin");
        }

        if (backupsDir.listFiles().length > 100) {
            Bukkit.broadcast(system("<highlight>Warning:</highlight> There are now more than 100 backup files in the backup folder. Please consider cleaning up the folder and migrating the oldest backups elsewhere."), "jade.admin");
        }

        return Command.SINGLE_SUCCESS;
    }
}

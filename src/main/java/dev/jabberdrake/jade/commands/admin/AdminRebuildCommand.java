package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AdminRebuildCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(AdminRebuildCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Bukkit.broadcast(TextUtils.composeSimpleOperatorMessage("An administrator is now rebuilding internal data structures. This might lag for a bit..."));

        sender.sendMessage(TextUtils.composeSimpleOperatorMessage("Rebuilding realm data from database..."));
        RealmManager.reload();

        int settlementCount = RealmManager.getSettlementCount();
        int nationCount = RealmManager.getNationCount();
        sender.sendMessage(
                TextUtils.composeSimpleSuccessMessage("Successfully loaded ")
                        .append(TextUtils.composeSuccessHighlight(String.valueOf(settlementCount)))
                        .append(TextUtils.composeSuccessText(" settlements and "))
                        .append(TextUtils.composeSuccessHighlight(String.valueOf(nationCount)))
                        .append(TextUtils.composeSuccessText(" nations!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

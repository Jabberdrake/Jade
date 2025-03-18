package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

public class AdminRebuildCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(AdminRebuildCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        sender.sendMessage(TextUtils.composePlainOperatorMessage("Rebuilding realm data from manifest..."));
        if (!RealmManager.loadManifest()) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Could not load manifest! Quiting..."));
            return Command.SINGLE_SUCCESS;
        }

        int settlementCount = RealmManager.getSettlementCount();
        int nationCount = RealmManager.getNationCount();
        sender.sendMessage(
                TextUtils.composePlainSuccessMessage("Successfully loaded ")
                        .append(TextUtils.composeSuccessHighlight(String.valueOf(settlementCount)))
                        .append(TextUtils.composeSuccessText(" settlements and "))
                        .append(TextUtils.composeSuccessHighlight(String.valueOf(nationCount)))
                        .append(TextUtils.composeSuccessText(" nations!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

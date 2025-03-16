package dev.jabberdrake.charter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.RealmManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class AdminRebuildCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(AdminRebuildCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendPlainMessage("Rebuilding realm data from manifest...");
        RealmManager.loadManifest();
        int settlementCount = RealmManager.getSettlementCount();
        int nationCount = RealmManager.getNationCount();
        context.getSource().getSender().sendPlainMessage("Successfully loaded " + settlementCount + " settlements and " + nationCount + " nations!");
        return Command.SINGLE_SUCCESS;
    }
}

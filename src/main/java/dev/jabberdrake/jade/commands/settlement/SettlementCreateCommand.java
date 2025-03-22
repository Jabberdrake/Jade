package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementCreateCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementCreateCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ChunkAnchor anchor = new ChunkAnchor(player.getChunk());

        if (!RealmManager.isUnclaimedChunk(anchor)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Cannot create a settlement on a claimed chunk!"));
            return Command.SINGLE_SUCCESS;
        }

        String name = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueSettlementName(name)) {
            player.sendMessage(TextUtils.composePlainErrorMessage("There is already a settlement with that name!"));
            return Command.SINGLE_SUCCESS;
        }

        Settlement settlement = RealmManager.createSettlement(name, player, anchor);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully created ")
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}
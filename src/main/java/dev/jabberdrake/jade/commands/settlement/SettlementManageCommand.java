package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.menus.SettlementTitleMenu;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementManageCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .executes(SettlementManageCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement settlement = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!performBasicChecks(player, settlement)) { return Command.SINGLE_SUCCESS; }

        new SettlementTitleMenu(settlement).open(player);

        return Command.SINGLE_SUCCESS;
    }

    public static boolean performBasicChecks(Player player, Settlement settlement) {

        if (settlement == null) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not focusing on any settlement."));
            return false;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }

        return true;
    }
}

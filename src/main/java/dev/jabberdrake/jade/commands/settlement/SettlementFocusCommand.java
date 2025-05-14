package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementFocusCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementFocusCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForSettlementsWithPlayer)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementFocusCommand::runCommand))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (focus == null) {
            player.sendMessage(TextUtils.composeSimpleInfoMessage("You are not focusing on any settlement..."));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composeSimpleInfoMessage("You are focusing on: ")
                .append(focus.asTextComponent())
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String settlementAsString = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(settlementAsString);
        if (settlement == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a settlement with that name."));
            return Command.SINGLE_SUCCESS;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerManager.asJadePlayer(player.getUniqueId()).setFocusSettlement(settlement);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("Now focusing on "))
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

}

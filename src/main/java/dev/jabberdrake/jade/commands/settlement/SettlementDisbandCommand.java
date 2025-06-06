package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementDisbandCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementDisbandCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementDisbandCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(TextUtils.composeSimpleInfoMessage("To avoid accidental usages of this command, you need to explicitly specify which settlement you want to disband."));
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String settlementAsString = StringArgumentType.getString(context, "stmArg");
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

        SettlementRole role = settlement.getRoleFromMember(player.getUniqueId());
        if (role == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!role.isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Only the leader can disband a settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        RealmManager.deleteSettlement(settlement);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("You have disbanded ")
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.CommonSettlementSuggestions;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class NationDissolveCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationDissolveCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationDissolveCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(TextUtils.composeSimpleInfoMessage("To avoid accidental usages of this command, you must explicitly state the capital settlement of the nation you wish to dissolve."));
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
        } else if (!settlement.getRoleFromMember(player.getUniqueId()).isLeader()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not the leader of this settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation nation = settlement.getNation();
        if (nation == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("This settlement is not part of a nation!"));
            return Command.SINGLE_SUCCESS;
        } else if (!nation.getCapital().equals(settlement)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Only capital settlements can dissolve their nations!"));
            return Command.SINGLE_SUCCESS;
        }

        RealmManager.deleteNation(nation);

        player.sendMessage(TextUtils.composeSimpleSuccessMessage("You have dissolved the nation of ")
                .append(nation.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

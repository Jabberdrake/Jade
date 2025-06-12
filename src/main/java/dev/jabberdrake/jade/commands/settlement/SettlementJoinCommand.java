package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.success;

public class SettlementJoinCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementJoinCommand::runCommandWithoutArgs)
                .then(Commands.argument("settlement", StringArgumentType.string())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementJoinCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        Settlement inviter = RealmManager.getWhoInvitedPlayer(player);
        if (inviter == null) {
            player.sendMessage(error("You do not have any pending settlement invites."));
            return Command.SINGLE_SUCCESS;
        }

        inviter.addMember(player.getUniqueId(), inviter.getDefaultRole());
        RealmManager.clearInviteToSettlement(player);

        player.sendMessage(success("You have joined the settlement of " + inviter.getDisplayNameAsString() + "!"));
        inviter.broadcast("<highlight>" + player.getName() + " has joined the settlement!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String settlementArgument = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(settlementArgument);

        Settlement inviter = RealmManager.getWhoInvitedPlayer(player);
        if (settlement == null) {
            player.sendMessage(error("Could not find a settlement named <highlight>" + settlementArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!settlement.equals(inviter)) {
            player.sendMessage(error("You do not have a pending nation invite from <highlight>" + settlementArgument + "</highlight>!"));
        }

        settlement.addMember(player.getUniqueId(), settlement.getDefaultRole());
        RealmManager.clearInviteToSettlement(player);

        player.sendMessage(success("You have joined the settlement of " + inviter.getDisplayNameAsString() + "!"));
        inviter.broadcast("<highlight>" + player.getName() + " has joined the settlement!");
        return Command.SINGLE_SUCCESS;
    }
}

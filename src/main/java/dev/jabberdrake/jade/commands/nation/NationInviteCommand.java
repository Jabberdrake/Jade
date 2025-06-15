package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.NationCommand;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.commands.settlement.CommonSettlementSuggestions;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class NationInviteCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationInviteCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        Nation focusNation = focus.getNation();

        String targetArgument = StringArgumentType.getString(context, "settlement");
        Settlement target = RealmManager.getSettlement(targetArgument);
        if (target == null) {
            player.sendMessage(error("Could not find a settlement named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (target.equals(focus)) {
            player.sendMessage(error("...but why? What were you hoping to achieve here?"));
            return Command.SINGLE_SUCCESS;
        } else if (target.isInNation()) {
            player.sendMessage(error("This settlement (<highlight>" + targetArgument + "</highlight>) is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        boolean successFlag = RealmManager.registerInviteToNation(target, focusNation);
        if (successFlag) {
            focusNation.broadcast("The settlement of " + target.getDisplayNameAsString() + "<normal> has been invited to the nation!");
            target.broadcast("We have been invited to join the nation of " + focusNation.getDisplayNameAsString() + "<normal>!");
        } else {
            player.sendMessage(error("This settlement (<highlight>" + targetArgument + "<normal>) already has a pending nation invite!"));
        }

        return Command.SINGLE_SUCCESS;
    }
}

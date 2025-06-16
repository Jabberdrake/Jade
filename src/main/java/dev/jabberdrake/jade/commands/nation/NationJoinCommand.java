package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class NationJoinCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(NationJoinCommand::runCommandWithoutArgs)
                .then(Commands.argument("nation", StringArgumentType.string())
                        .suggests(CommonNationSuggestions::suggestAllNations)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationJoinCommand::runCommandWithArgs))
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (focus.isInNation()) {
            player.sendMessage(error("This settlement is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation inviter = RealmManager.getWhoInvitedSettlement(focus);
        if (inviter == null) {
            player.sendMessage(error("You do not have any pending nation invites."));
            return Command.SINGLE_SUCCESS;
        }

        inviter.addSettlement(focus);
        focus.setNation(inviter);
        RealmManager.clearInviteToNation(focus);

        focus.broadcast("We have joined the nation of " + inviter.getDisplayName() + "<normal>!");
        inviter.broadcast("The settlement of " + focus.getDisplayName() + "<normal> has joined the nation!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandWithArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (focus.isInNation()) {
            player.sendMessage(error("This settlement is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        String nationArgument = StringArgumentType.getString(context, "nation");
        Nation nation = RealmManager.getNation(nationArgument);

        Nation inviter = RealmManager.getWhoInvitedSettlement(focus);
        if (nation == null) {
            player.sendMessage(error("Could not find a nation named <highlight>" + nationArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (!inviter.equals(nation)) {
            player.sendMessage(error("You do not have a pending nation invite from <highlight>" + nationArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        inviter.addSettlement(focus);
        focus.setNation(inviter);
        RealmManager.clearInviteToNation(focus);

        focus.broadcast("We have joined the nation of " + inviter.getDisplayName() + "<normal>!");
        inviter.broadcast("The settlement of " + focus.getDisplayName() + "<normal> has joined the nation!");
        return Command.SINGLE_SUCCESS;
    }
}

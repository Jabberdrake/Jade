package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.NationCommand;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class NationTransferCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.word())
                        .suggests(CommonNationSuggestions::suggestAllSettlementsInNation)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationTransferCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        Nation focusNation = focus.getNation();

        String targetName = StringArgumentType.getString(context, "settlement");
        Settlement target = RealmManager.getSettlement(targetName);
        if (target == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified settlement!"));
            return Command.SINGLE_SUCCESS;
        } else if (target.equals(focus)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("...but why? What were you hoping to achieve here?"));
            return Command.SINGLE_SUCCESS;
        } else if (!target.isInNation()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified settlement is not part of a nation!"));
            return Command.SINGLE_SUCCESS;
        } else if (!target.getNation().equals(focusNation)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified settlement is not part of your nation!"));
            return Command.SINGLE_SUCCESS;
        }

        focusNation.setCapital(target);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(target.getDisplayName())
                .append(TextUtils.composeSuccessText(" is now the capital of "))
                .append(focusNation.getDisplayName())
                .append(TextUtils.composeSuccessHighlight("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

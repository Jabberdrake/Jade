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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class NationProclaimCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("name", StringArgumentType.word())
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(NationProclaimCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }

        if (focus.isInNation()) {
            player.sendMessage(error("This settlement is already part of a nation!"));
            return Command.SINGLE_SUCCESS;
        }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueNationName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        Nation nation = RealmManager.createNation(nameArgument, focus);

        player.sendMessage(success("Created the nation of " + nation.getDisplayNameAsString() + "!"));
        Bukkit.broadcast(info("<highlight>" + player.getName() + "</highlight> has created the nation of " + nation.getDisplayNameAsString() + "!"));
        return Command.SINGLE_SUCCESS;
    }
}

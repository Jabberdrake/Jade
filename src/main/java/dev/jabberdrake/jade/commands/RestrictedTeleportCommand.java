package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.SettlementInviteCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.info;
import static dev.jabberdrake.jade.utils.TextUtils.error;

public class RestrictedTeleportCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonArgumentSuggestions::suggestAllOnlinePlayers)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .requires(sender -> sender.getSender().hasPermission("jade.teleport"))
                        .executes(RestrictedTeleportCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't teleport to yourself!"));
            return Command.SINGLE_SUCCESS;
        }

        Player onlineTarget = (Player) target;
        if (!onlineTarget.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is in another world!"));
            return Command.SINGLE_SUCCESS;
        }

        player.teleport(onlineTarget);
        player.sendMessage(info("Teleported to <highlight>" + onlineTarget.getName() + "</highlight>!"));
        return Command.SINGLE_SUCCESS;
    }
}

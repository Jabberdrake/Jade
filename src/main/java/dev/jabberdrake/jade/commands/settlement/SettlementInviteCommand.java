package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static dev.jabberdrake.jade.utils.TextUtils.*;

public class SettlementInviteCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonArgumentSuggestions::suggestAllOnlinePlayers)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementInviteCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());
        if (senderRole == null) {
            player.sendMessage(error("Could not find a matching role for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!senderRole.canInvite()) {
            player.sendMessage(error("You are not allowed to invite members to <highlight>" + focus.getDisplayNameAsString() + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't invite yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!target.isOnline()) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is not online!"));
            return Command.SINGLE_SUCCESS;
        } else if (focus.containsPlayer(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is already a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        // TODO: Check if at max capacity if we decide to implement that

        Player onlineTarget = (Player) target;
        boolean successFlag = RealmManager.registerInviteToSettlement(onlineTarget, focus);
        if (successFlag) {
            player.sendMessage(success("Invited <highlight>" + target.getName() + "</highlight> to " + focus.getDisplayNameAsString() + "!"));
            focus.tell(onlineTarget, "<highlight>" + target.getName() + "</highlight> has invited you to join " + focus.getDisplayNameAsString() + "!");
            onlineTarget.sendMessage(info("To accept a settlement invite, do <highlight>/settlement join"));
        } else {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) already has a pending settlement invite!"));
        }

        return Command.SINGLE_SUCCESS;
    }
}

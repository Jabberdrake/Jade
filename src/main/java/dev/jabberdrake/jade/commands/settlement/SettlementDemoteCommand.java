package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.success;

public class SettlementDemoteCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementDemoteCommand::runCommand)
                        .then(Commands.argument("role", StringArgumentType.string())
                                .suggests(SettlementDemoteCommand::buildSuggestionsForRolesBelow)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementDemoteCommand::runCommandForTitle)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't demote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is not a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());

        SettlementRole fromRole = focus.getRoleFromMember(target.getUniqueId());
        if (fromRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) has equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole toRole = focus.getRoleBelow(fromRole);
        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("You can only demote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(target.getUniqueId(), toRole);

        player.sendMessage(success("Demoted <highlight>" + target.getName() + "</highlight> to the role of " + toRole.getDisplayAsString() + "<normal>!"));
        if (target.isOnline()) {
            focus.tell((Player) target, "You have been demoted to " + toRole.getDisplayAsString() + "<normal>!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTitle(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetArgument);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't demote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(target.getUniqueId())) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is not a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());

        SettlementRole fromRole = focus.getRoleFromMember(target.getUniqueId());
        if (fromRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) has equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        String roleArgument = StringArgumentType.getString(context, "role");

        SettlementRole toRole = focus.getRoleFromName(roleArgument);
        if (toRole == null) {
            player.sendMessage(error("Could not find a role named <highlight>" + roleArgument + "</highlight> in " + focus.getDisplayName() + "<normal>!"));
            return Command.SINGLE_SUCCESS;
        }

        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can only demote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(target.getUniqueId(), toRole);

        player.sendMessage(success("Demoted <highlight>" + target.getName() + "</highlight> to the role of " + toRole.getDisplayAsString() + "<normal>!"));
        if (target.isOnline()) {
            focus.tell((Player) target, "You have been demoted to " + toRole.getDisplayAsString() + "!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canDemote()) {
            player.sendMessage(error("You are not allowed to demote members in <highlight>" + settlement.getDisplayName() + "<normal>!"));
            return false;
        }
        return true;
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForRolesBelow(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        if (!focus.containsPlayer(player.getUniqueId())) { return builder.buildFuture(); }

        SettlementRole playerRole = focus.getRoleFromMember(player.getUniqueId());
        focus.getRoles().stream()
                .filter(role -> role.getAuthority() < playerRole.getAuthority())
                .map(SettlementRole::getName)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

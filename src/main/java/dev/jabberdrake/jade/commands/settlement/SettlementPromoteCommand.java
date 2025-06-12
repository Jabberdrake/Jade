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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.success;

public class SettlementPromoteCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::suggestAllPlayersInSettlement)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementPromoteCommand::runCommand)
                        .then(Commands.argument("role", StringArgumentType.string())
                                .suggests(SettlementPromoteCommand::buildSuggestionsForRolesAbove)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementPromoteCommand::runCommandForTitle)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetArgument = StringArgumentType.getString(context, "player");
        OfflinePlayer target = Bukkit.getPlayer(targetArgument);
        UUID targetUUID = target.getUniqueId();
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't promote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) is not a member of your settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());
        SettlementRole fromRole = focus.getRoleFromMember(targetUUID);
        if (fromRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("This player (<highlight>" + target.getName() + "</highlight>) has equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole toRole = focus.getRoleAbove(fromRole);
        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("You can only promote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(targetUUID, toRole);

        player.sendMessage(success("Promoted <highlight>" + target.getName() + "</highlight> to the role of " + toRole.getDisplayAsString() + "!"));
        if (target.isOnline()) {
            focus.tell((Player) target, "You have been promoted to " + toRole.getDisplayAsString() + "! Congratulations!");
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
        if (!target.hasPlayedBefore()) {
            player.sendMessage(error("Could not find a player named <highlight>" + targetArgument + "</highlight>!"));
            return Command.SINGLE_SUCCESS;
        } else if (targetArgument.equals(player.getName())) {
            player.sendMessage(error("You can't promote yourself!"));
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
            player.sendMessage(error("Could not find a role named <highlight>" + roleArgument + "</highlight> in " + focus.getDisplayNameAsString() + "!"));
            return Command.SINGLE_SUCCESS;
        }

        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(error("You can only promote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(target.getUniqueId(), toRole);

        player.sendMessage(success("Promoted <highlight>" + target.getName() + "</highlight> to the role of " + toRole.getDisplayAsString() + "!"));
        if (target.isOnline()) {
            focus.tell((Player) target, "You have been promoted to " + toRole.getDisplayAsString() + "! Congratulations!");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canPromote()) {
            player.sendMessage(error("You are not allowed to promote members in <highlight>" + settlement.getDisplayNameAsString() + "</highlight>!"));
            return false;
        }
        return true;
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForRolesAbove(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        if (!focus.containsPlayer(player.getUniqueId())) { return builder.buildFuture(); }

        SettlementRole playerRole = focus.getRoleFromMember(player.getUniqueId());
        focus.getRoles().stream()
                .filter(role -> role.getAuthority() > playerRole.getAuthority())
                .map(SettlementRole::getName)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

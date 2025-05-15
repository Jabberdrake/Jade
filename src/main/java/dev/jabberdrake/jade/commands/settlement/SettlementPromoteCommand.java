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
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SettlementPromoteCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllPlayersInSettlement)
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

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't promote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player is not a member of your focus settlement!."));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());

        SettlementRole fromRole = focus.getRoleFromMember(targetUUID);
        if (fromRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Target player is of equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole toRole = focus.getRoleAbove(fromRole);
        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can only promote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(targetUUID, toRole);

        player.sendMessage(TextUtils.composeSuccessText("You have promoted ")
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" to the role of "))
                .append(toRole.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTitle(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(player.getName())) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can't promote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("The specified player is not a member of your focus settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderRole = focus.getRoleFromMember(player.getUniqueId());

        SettlementRole fromRole = focus.getRoleFromMember(targetUUID);
        if (fromRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Target player is of equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        String titleArgument = StringArgumentType.getString(context, "role");

        SettlementRole toRole = focus.getRoleFromName(titleArgument);
        if (toRole == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Could not find specified role!"));
            return Command.SINGLE_SUCCESS;
        }

        if (toRole.getAuthority() >= senderRole.getAuthority()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You can only promote members to roles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerRole(targetUUID, toRole);

        player.sendMessage(TextUtils.composeSuccessText("You have promoted ")
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" to the title of "))
                .append(toRole.getDisplayAsComponent())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canPromote()) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("You are not allowed to promote members in ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
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

package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.NamedTitle;
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
                        .then(Commands.argument("title", StringArgumentType.string())
                                .suggests(SettlementPromoteCommand::buildSuggestionsForTitlesBelow)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementPromoteCommand::runCommandForTitle)))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player sender = (Player) context.getSource().getSender();

        Settlement focus = PlayerManager.asJadePlayer(sender.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(sender, focus)) { return Command.SINGLE_SUCCESS; }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(sender.getName())) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("You can't promote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("The specified player is not a member of your focus settlement!."));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderTitle = focus.getRoleFromMember(sender.getUniqueId());

        SettlementRole fromTitle = focus.getRoleFromMember(targetUUID);
        if (fromTitle.getAuthority() >= senderTitle.getAuthority()) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Target player is of equal or higher authority than you!."));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole toTitle = focus.getRoleAbove(fromTitle);
        if (toTitle.getAuthority() >= senderTitle.getAuthority()) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("You can only promote members to titles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerTitle(targetUUID, toTitle);

        sender.sendMessage(TextUtils.composeSuccessText("You have promoted ")
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" to the title of "))
                .append(toTitle.getTitleAsComponent())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTitle(CommandContext<CommandSourceStack> context) {
        Player sender = (Player) context.getSource().getSender();

        Settlement focus = PlayerManager.asJadePlayer(sender.getUniqueId()).getFocusSettlement();
        if (!performCommonChecks(sender, focus)) { return Command.SINGLE_SUCCESS; }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = target.getUniqueId();
        if (Bukkit.getPlayer(targetName) == null) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Could not find the specified player."));
            return Command.SINGLE_SUCCESS;
        } else if (targetName.equals(sender.getName())) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("You can't promote yourself!"));
            return Command.SINGLE_SUCCESS;
        } else if (!focus.containsPlayer(targetUUID)) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("The specified player is not a member of your focus settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        SettlementRole senderTitle = focus.getRoleFromMember(sender.getUniqueId());

        SettlementRole fromTitle = focus.getRoleFromMember(targetUUID);
        if (fromTitle.getAuthority() >= senderTitle.getAuthority()) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Target player is of equal or higher authority than you!"));
            return Command.SINGLE_SUCCESS;
        }

        String titleArgument = StringArgumentType.getString(context, "title");

        SettlementRole toTitle = focus.getRoleFromName(titleArgument);
        if (toTitle == null) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("Could not find specified title!"));
            return Command.SINGLE_SUCCESS;
        }

        if (toTitle.getAuthority() >= senderTitle.getAuthority()) {
            sender.sendMessage(TextUtils.composePlainErrorMessage("You can only promote members to titles of lower authority than yours!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setPlayerTitle(targetUUID, toTitle);

        sender.sendMessage(TextUtils.composeSuccessText("You have promoted ")
                .append(TextUtils.composeSuccessHighlight(targetName))
                .append(TextUtils.composeSuccessText(" to the title of "))
                .append(toTitle.getTitleAsComponent())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }

    public static boolean performCommonChecks(Player player, Settlement settlement) {

        if (settlement == null) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not focusing on any settlement."));
            return false;
        } else if (!settlement.containsPlayer(player.getUniqueId())) {
            // NOTE: Since it just uses whichever settlement you're focusing on, this shouldn't ever happen.
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not a member of ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        } else if (!settlement.getRoleFromMember(player.getUniqueId()).canPromote()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("You are not allowed to promote members in ")
                    .append(settlement.getDisplayName())
                    .append(TextUtils.composeErrorText("!"))
            );
            return false;
        }

        return true;
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForTitlesBelow(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();
        if (!focus.containsPlayer(player.getUniqueId())) { return builder.buildFuture(); }

        SettlementRole playerTitle = focus.getRoleFromMember(player.getUniqueId());
        focus.getRoles().stream()
                .filter(title -> title.getAuthority() < playerTitle.getAuthority())
                .map(NamedTitle::getName)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

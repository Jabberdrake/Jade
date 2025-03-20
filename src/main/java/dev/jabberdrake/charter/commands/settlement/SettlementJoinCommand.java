package dev.jabberdrake.charter.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.commands.suggestions.CommonSettlementSuggestions;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class SettlementJoinCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.argument("settlement", StringArgumentType.greedyString())
                        .suggests(CommonSettlementSuggestions::buildSuggestionsForAllSettlements)
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementJoinCommand::runCommand))
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String settlementAsString = StringArgumentType.getString(context, "settlement");
        Settlement settlement = RealmManager.getSettlement(settlementAsString);
        if (settlement == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find a settlement with that name."));
            return Command.SINGLE_SUCCESS;
        }

        settlement.addCitizen(player.getUniqueId(), settlement.getDefaultTitle());
        PlayerManager.parsePlayer(player.getUniqueId()).addSettlement(settlement);

        player.sendMessage(TextUtils.composeSuccessPrefix()
                .append(TextUtils.composeSuccessText("You are now a member of "))
                .append(settlement.getDisplayName())
                .append(TextUtils.composeSuccessText("!"))
        );
        return Command.SINGLE_SUCCESS;
    }
}

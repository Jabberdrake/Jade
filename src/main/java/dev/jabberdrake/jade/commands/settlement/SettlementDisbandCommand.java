package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.CharterTitle;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class SettlementDisbandCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementDisbandCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.parsePlayer(player.getUniqueId()).getFocusSettlement();

        CharterTitle title = focus.getTitleFromMember(player.getUniqueId());
        if (title == null) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Could not find a matching title for command sender. Please report this to a developer!"));
            return Command.SINGLE_SUCCESS;
        } else if (!title.isLeader()) {
            player.sendMessage(TextUtils.composePlainErrorMessage("Only the leader can disband a settlement!"));
            return Command.SINGLE_SUCCESS;
        }

        RealmManager.deleteSettlement(focus);

        player.sendMessage(TextUtils.composePlainSuccessMessage("Successfully disbanded ")
                .append(focus.getDisplayName())
                .append(TextUtils.composePlainSuccessMessage("!"))
        );

        return Command.SINGLE_SUCCESS;
    }
}

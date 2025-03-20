package dev.jabberdrake.charter.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.ChunkAnchor;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SettlementMapCommand {
    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementMapCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        int currentX = player.getChunk().getX();
        int currentZ = player.getChunk().getZ();

        final int mapRange = 5;
        Component mapAsText = Component.text("");
        for (int cZ = -mapRange; cZ <= mapRange; cZ++) {
            for (int cX = -mapRange; cX <= mapRange; cX++) {
                Settlement owner = RealmManager.getChunkOwner(new ChunkAnchor(currentX + cX, currentZ + cZ));
                if (owner == null) {
                    mapAsText = mapAsText.append(Component.text("■", TextUtils.LAUREL)
                            .hoverEvent(HoverEvent.showText(Component.text("Wilderness", TextUtils.LAUREL))));
                } else {
                    mapAsText = mapAsText.append(Component.text("■", owner.getMapColor())
                            .hoverEvent(HoverEvent.showText(owner.getDisplayName())));
                }
            }
            if (cZ != mapRange) {
                mapAsText = mapAsText.appendNewline();
            }
        }
        player.sendMessage(Component.text("===========", TextUtils.DARK_ZORBA));
        player.sendMessage(mapAsText);
        player.sendMessage(Component.text("===========", TextUtils.DARK_ZORBA));

        return Command.SINGLE_SUCCESS;
    }
}

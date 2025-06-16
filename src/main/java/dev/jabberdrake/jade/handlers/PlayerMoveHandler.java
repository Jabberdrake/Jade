package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveHandler implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        ChunkAnchor fromAnchor = new ChunkAnchor(player.getWorld(), from.getBlockX() >> 4, from.getBlockZ() >> 4);
        ChunkAnchor toAnchor = new ChunkAnchor(player.getWorld(), to.getBlockX() >> 4, to.getBlockZ() >> 4);

        if (fromAnchor.equals(toAnchor)) { return; }

        Settlement owner = RealmManager.getChunkOwner(toAnchor);
        if (owner == null) {
            // Handle autoclaiming
            JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
            if (jadePlayer.isAutoclaiming()) {
                if (RealmManager.claimChunk(jadePlayer.getFocusSettlement(), toAnchor)) {
                    player.clearTitle();
                    player.sendActionBar(TextUtils.composeSuccessText("Claimed this chunk for ")
                            .append(jadePlayer.getFocusSettlement().getDisplayNameAsComponent())
                            .append(TextUtils.composeSuccessText("!"))
                    );
                } else if (jadePlayer.getFocusSettlement().getFood() < JadeSettings.chunkCost) {
                    player.sendActionBar(TextUtils.composeErrorText("Not enough food!"));
                }

            } else {
                // Handle title/actionbar messages when moving to unclaimed chunks
                if (RealmManager.getChunkOwner(fromAnchor) == null) {
                    return;
                }

                // This happens when the player moves from a claimed chunk to an unclaimed chunk.
                // To inform them that they have left a claimed region, show them that they are
                // now in 'Wilderness', like in Factions:
                player.clearTitle();
                player.sendActionBar(Component.text("Wilderness", NamedTextColor.DARK_GREEN));
            }
            return;
        } else {
            // Handle autoclaiming
            JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
            if (jadePlayer.isAutoclaiming()) {
                player.clearTitle();
                player.sendActionBar(TextUtils.composeErrorText("This chunk is already claimed!"));
            } else {
                // Handle title/actionbar messages when moving to claimed chunks
                if (RealmManager.getChunkOwner(fromAnchor) != null && RealmManager.getChunkOwner(fromAnchor).equals(owner)) {
                    return;
                }

                // This happens when the player moves:
                // - from an unclaimed chunk to a claimed chunk
                // - from a chunk claimed by A to a chunk claimed by B
                // When this happens, present the settlement name (decorated) and description:
                Component mainTitle = owner.getDisplayNameAsComponent();
                Component subTitle = owner.getDescriptionAsComponent();

                final Title stmTitle = Title.title(mainTitle, subTitle);

                player.clearTitle();
                player.showTitle(stmTitle);
                return;
            }
        }


    }
}

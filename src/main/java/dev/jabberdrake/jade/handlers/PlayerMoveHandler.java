package dev.jabberdrake.jade.handlers;

import dev.jabberdrake.jade.JadeSettings;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.utils.JadeTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.success;
import static net.kyori.adventure.text.Component.text;

public class PlayerMoveHandler implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        ChunkAnchor fromAnchor = new ChunkAnchor(player.getWorld(), from.getBlockX() >> 4, from.getBlockZ() >> 4);
        ChunkAnchor toAnchor = new ChunkAnchor(player.getWorld(), to.getBlockX() >> 4, to.getBlockZ() >> 4);

        Settlement owner = RealmManager.getChunkOwner(toAnchor);
        if (owner == null) {
            if (fromAnchor.equals(toAnchor)) { return; } // We only care about in-chunk movements when displaying area info, and we're in unclaimed chunks so...

            /// Player has moved to an unclaimed chunk

            // Handle autoclaiming
            JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
            if (jadePlayer.isAutoclaiming()) {
                if (RealmManager.claimChunk(jadePlayer.getFocusSettlement(), toAnchor)) {
                    player.clearTitle();
                    player.sendActionBar(success("Claimed this chunk for <highlight>" + jadePlayer.getFocusSettlement().getDisplayName() + "<normal>!"));
                } else if (jadePlayer.getFocusSettlement().getFood() < JadeSettings.chunkCost) {
                    player.sendActionBar(error("Not enough food!"));
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
                player.sendActionBar(text("Wilderness", NamedTextColor.DARK_GREEN));
            }
            return;
        } else {
            // Check if we have entered an area within a settlement
            // If so, send the appropriate action bar
            Area areaFrom = owner.getHighestPriorityAreaForLocation(from);
            Area areaTo = owner.getHighestPriorityAreaForLocation(to);
            if (areaTo != null && (areaFrom == null || !areaFrom.equals(areaTo))) {
                player.sendActionBar(text()
                        .append(areaTo.getDisplayNameAsComponent())
                        .append(text(" ・ ", JadeTextColor.LIGHT_ZORBA))
                        .append(owner.getDisplayNameAsComponent())
                        .build()
                );
            }

            if (fromAnchor.equals(toAnchor)) { return; } // From this point on, we only deal with movements from one chunk to another
            /// Player has moved to a claimed chunk

            // Handle autoclaiming
            JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
            if (jadePlayer.isAutoclaiming()) {
                player.clearTitle();
                player.sendActionBar(error("This chunk is already claimed!"));
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

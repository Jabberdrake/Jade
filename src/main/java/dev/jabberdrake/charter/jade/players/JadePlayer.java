package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.players.CharterProfile;
import dev.jabberdrake.charter.realms.Settlement;

import java.util.UUID;

public class JadePlayer {
    private UUID uuid;
    private CharterProfile profile;

    // For realm commands
    private Settlement stmFocus = null;
    // private Polity focus = null;

    public JadePlayer(UUID uuid, CharterProfile profile) {
        this.uuid = uuid;
        this.profile = profile;
    }

    public UUID getUniqueID() {
        return this.uuid;
    }

    public CharterProfile getProfile() {
        return this.profile;
    }

    public Settlement getSettlementFocus() {
        return this.stmFocus;
    }

    public int getSettlementFocusID() {
        if (this.stmFocus == null) {
            return -1;
        } else {
            return this.stmFocus.getId();
        }
    }
}

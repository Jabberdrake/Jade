package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.realms.Settlement;

import java.util.UUID;

public class JadePlayer {
    private UUID uuid;
    private JadeProfile profile;

    // For Jade stuff
    private boolean inRoleplay = false;

    // For realm commands
    private Settlement stmFocus = null;
    // private Polity focus = null;

    public JadePlayer(UUID uuid, JadeProfile profile) {
        this.uuid = uuid;
        this.profile = profile;
    }

    public UUID getUniqueID() {
        return this.uuid;
    }

    public JadeProfile getProfile() {
        return this.profile;
    }

    public boolean isInRoleplay() {
        return this.inRoleplay;
    }

    public void toggleRoleplay() {
        this.inRoleplay = !this.inRoleplay;
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

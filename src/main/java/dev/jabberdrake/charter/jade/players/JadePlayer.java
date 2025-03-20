package dev.jabberdrake.charter.jade.players;

import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JadePlayer {
    private UUID uuid;
    private JadeProfile profile;

    // For Jade stuff
    private boolean inRoleplay = false;

    // For Charter stuff
    private boolean autoclaim = false;
    private List<Integer> settlements = new ArrayList<>();
    private int stmFocus = -1;
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

    public boolean isAutoclaiming() {
        return this.autoclaim;
    }

    public void toggleAutoclaim() {
        this.autoclaim = !this.autoclaim;
    }

    public Settlement getFocusSettlement() {
        if (this.stmFocus == -1) {
            return null;
        } else return RealmManager.getSettlement(this.stmFocus);
    }

    public int getFocusSettlementID() {
        return this.stmFocus;
    }

    public void setFocusSettlement(Settlement settlement) {
        this.stmFocus = settlement.getId();
    }

    public void setFocusSettlement(int settlementID) {
        this.stmFocus = settlementID;
    }

    public List<Settlement> getSettlements() {
        List<Settlement> stmList = new ArrayList<>();
        for (int settlementID : settlements) {
            stmList.add(RealmManager.getSettlement(settlementID));
        }
        return stmList;
    }

    public List<Integer> getSettlementsAsIntegerList() {
        return this.settlements;
    }

    public boolean isInSettlement(Settlement settlement) {
        for (int stmID : settlements) {
            if (stmID == settlement.getId()) {
                return true;
            }
        }
        return false;
    }

    public void addSettlement(Settlement settlement) {
        this.settlements.add(settlement.getId());
    }

    public void addSettlement(int settlementID) {
        this.settlements.add(settlementID);
    }

    public void removeSettlement(Settlement settlement) {
        int stmID = settlement.getId();
        if (this.settlements.contains(stmID)) {
            this.settlements.remove(stmID);
        }
    }

    public void removeSettlement(int settlementID) {
        if (this.settlements.contains(settlementID)) {
            this.settlements.remove(settlementID);
        }
    }
}

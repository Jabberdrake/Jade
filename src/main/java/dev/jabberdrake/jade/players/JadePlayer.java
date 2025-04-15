package dev.jabberdrake.jade.players;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class JadePlayer {
    private UUID uuid;

    // For Jade stuff
    private String roleplayName;
    private JadeTitle titleInUse;
    private boolean inRoleplay = false;

    // For Charter stuff
    private List<Integer> settlements = new ArrayList<>();
    private int stmFocus = -1;
    private int natFocus = -1;
    private boolean autoclaim = false;
    private boolean borderview = false;

    public JadePlayer(UUID uuid) {
        this.uuid = uuid;
        this.roleplayName = Bukkit.getPlayer(uuid).getName();
        this.titleInUse = DefaultJadeTitle.PEASANT;
    }

    public JadePlayer(UUID uuid, String roleplayName, JadeTitle titleInUse) {
        this.uuid = uuid;
        this.roleplayName = roleplayName;
        this.titleInUse = titleInUse;
    }

    public UUID getUniqueID() {
        return this.uuid;
    }

    public String getRoleplayName() { return this.roleplayName; }

    public void setRoleplayName(String roleplayName) {
        this.roleplayName = roleplayName;
        DatabaseManager.savePlayer(this);
    }

    public JadeTitle getTitleInUse() { return this.titleInUse; }

    public void setTitleInUse(JadeTitle title) {
        this.titleInUse = title;
        DatabaseManager.savePlayer(this);
    }

    public List<JadeTitle> getAvailableTitles() {
        return Stream.concat(DatabaseManager.fetchAllTitlesAvailableTo(this.uuid).stream(), DatabaseManager.fetchAllUniversalTitles()).toList();
        return DatabaseManager.fetchAllTitlesAvailableTo(this.uuid);
    }

    public List<JadeTitle> getOwnedTitles() {
        return DatabaseManager.fetchAllTitlesOwnedBy(this.uuid);
    }

    public JadeTitle getTitleFromName(String titleName) {
        for (JadeTitle title : this.getAvailableTitles()) {
            if (title.getName().equalsIgnoreCase(titleName)) {
                return title;
            }
        }
        return null;
    }

    public boolean canUseTitle(JadeTitle title) {
        return this.getAvailableTitles().contains(title);
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

    public boolean isViewingBorders() {
        return this.borderview;
    }

    public void toggleBorderview() {
        this.borderview = !this.borderview;
    }

    public Settlement getFocusSettlement() {
        if (this.stmFocus == -1) {
            return null;
        } else return RealmManager.getSettlement(this.stmFocus);
    }

    public int getFocusSettlementAsID() {
        return this.stmFocus;
    }

    public void setFocusSettlement(Settlement settlement) {
        this.setFocusSettlement(settlement.getId());
    }

    public void setFocusSettlement(int settlementID) {
        if (this.settlements.contains(settlementID)) {
            this.stmFocus = settlementID;
        }
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
        this.removeSettlement(stmID);
    }

    public void removeSettlement(int settlementID) {
        if (this.settlements.contains(settlementID)) {
            this.settlements.remove(settlementID);
        }

        if (this.stmFocus == settlementID) {
            if (this.settlements.isEmpty()) {
                this.stmFocus = -1;
            } else {
                this.stmFocus = this.settlements.getFirst();
            }
        }
    }
}

package dev.jabberdrake.charter.realms.management;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;

public class PlayerTitle {

    private Settlement settlement;
    private String name;
    private String style;
    private int authority;
    private boolean canInvite = false;
    private boolean canKick = false;
    private boolean canClaim = false;
    private boolean canUnclaim = false;
    private boolean canPromote = false;
    private boolean canDemote = false;
    private boolean canEdit = false;
    private boolean canDisband = false;

    public PlayerTitle(Settlement settlement, String name, String style, int authority) {
        this.settlement = settlement;
        this.name = name;
        this.style = style;
        this.authority = authority;

        generatePermissionsFromAuthority(authority);
    }

    public PlayerTitle(Settlement settlement, DefaultTitle defaultTitle) {
        this.name = defaultTitle.name;
        this.style = defaultTitle.name;
        this.settlement = settlement;
        this.authority = defaultTitle.authority;

        generatePermissionsFromAuthority(authority);
    }

    public String getName() {
        return this.name;
    }

    public String getStyle() {
        return this.style;
    }

    public int getAuthority() {
        return this.authority;
    }

    public boolean canInvite() {
        return this.canInvite;
    }

    public boolean canKick() {
        return this.canKick;
    }

    public boolean canClaim() {
        return this.canClaim;
    }

    public boolean canUnclaim() {
        return this.canUnclaim;
    }

    public boolean canPromote() {
        return this.canPromote;
    }

    public boolean canDemote() {
        return this.canDemote;
    }

    public boolean canEdit() {
        return this.canEdit;
    }

    public boolean canDisband() {
        return this.canDisband;
    }

    public enum DefaultTitle {
        LEADER("Leader", 60),
        OFFICER("Officer", 40),
        MEMBER("Member", 20);

        public final String name;
        public final int authority;
        DefaultTitle(String name, int authority) {
            this.name = name;
            this.authority = authority;
        }
    }

    public void generatePermissionsFromAuthority(int authority) {

        if (authority == 60) {  // Leader
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = true;
            this.canUnclaim = true;
            this.canPromote = true;
            this.canDemote = true;
            this.canEdit = true;
            this.canDisband = true;
        } else if (authority >= 40) { // Officer, or above
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
            this.canDisband = false;
        } else {
            this.canInvite = false;
            this.canKick = false;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
            this.canDisband = false;
        }
    }

    public void setPermission(String permissionName, boolean value) {
        switch (permissionName) {
            case "canInvite":
                this.canInvite = value;
                break;
            case "canKick":
                this.canKick = value;
                break;
            case "canClaim":
                this.canClaim = value;
                break;
            case "canUnclaim":
                this.canUnclaim = value;
                break;
            case "canPromote":
                this.canPromote = value;
                break;
            case "canDemote":
                this.canDemote = value;
                break;
            case "canRestyle":
                this.canEdit = value;
                break;
            case "canDisband":
                this.canDisband = value;
                break;
            default:
                Charter.getPlugin(Charter.class).getLogger().warning("[PlayerTitle::setPermission] Unknown permission key: " + permissionName);
        }
    }

    public static PlayerTitle fromString(String str, Settlement settlement) {
        String[] parts = str.split(";");
        PlayerTitle title = new PlayerTitle(settlement, parts[0], parts[1], Integer.parseInt(parts[2]));
        title.setPermission("canInvite", Boolean.parseBoolean(parts[3]));
        title.setPermission("canKick", Boolean.parseBoolean(parts[4]));
        title.setPermission("canClaim", Boolean.parseBoolean(parts[5]));
        title.setPermission("canUnclaim", Boolean.parseBoolean(parts[6]));
        title.setPermission("canPromote", Boolean.parseBoolean(parts[7]));
        title.setPermission("canDemote", Boolean.parseBoolean(parts[8]));
        title.setPermission("canEdit", Boolean.parseBoolean(parts[9]));
        title.setPermission("canDisband", Boolean.parseBoolean(parts[10]));
        return title;
    }

    @Override
    public String toString() {
        return this.getName() + ";" + this.getStyle() + ";" + this.getAuthority()
                + ";" + this.canInvite() + ";" + this.canKick()
                + ";" + this.canClaim() + ";" + this.canUnclaim()
                + ";" + this.canPromote() + ";" + this.canDemote()
                + ";" + this.canEdit() + ";" + this.canDisband();
    }
}

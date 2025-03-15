package dev.jabberdrake.charter.realms.management;

import dev.jabberdrake.charter.realms.Settlement;

import java.util.Locale;

public class PlayerTitle {

    private Settlement settlement;
    private String name;
    private String decoratedName;
    private int authority;
    private boolean canInvite = false;
    private boolean canKick = false;
    private boolean canClaim = false;
    private boolean canUnclaim = false;
    private boolean canPromote = false;
    private boolean canDemote = false;
    private boolean canRestyle = false;
    private boolean canDisband = false;

    public PlayerTitle(Settlement settlement, String decoratedName, int authority) {
        this.settlement = settlement;
        this.name = decoratedName.toUpperCase(Locale.ENGLISH);
        this.decoratedName = decoratedName;
        this.authority = authority;

        generatePermissionsFromAuthority(authority);
    }

    public PlayerTitle(Settlement settlement, DefaultTitle defaultTitle) {
        this.name = defaultTitle.name;
        this.decoratedName = defaultTitle.name;
        this.settlement = settlement;
        this.authority = defaultTitle.authority;

        generatePermissionsFromAuthority(authority);
    }

    public String getName() {
        return this.name;
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
            this.canRestyle = true;
            this.canDisband = true;
        } else if (authority >= 40) { // Officer, or above
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canRestyle = false;
            this.canDisband = false;
        } else {
            this.canInvite = false;
            this.canKick = false;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canRestyle = false;
            this.canDisband = false;
        }
    }

    public int setPermission(String permissionName, boolean value) {
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
                this.canRestyle = value;
                break;
            case "canDisband":
                this.canDisband = value;
                break;
            default:
                return -1;
        }
        return 0;
    }
}

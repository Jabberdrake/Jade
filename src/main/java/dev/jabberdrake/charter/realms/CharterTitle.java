package dev.jabberdrake.charter.realms;

import dev.jabberdrake.charter.Charter;
import dev.jabberdrake.charter.jade.titles.NamedTitle;
import org.bukkit.configuration.file.FileConfiguration;

public class CharterTitle extends NamedTitle {

    public static final int MAX_AUTHORITY = 8;

    private NamedTitle title;
    private Settlement settlement;
    private int authority;
    private boolean canInvite = false;
    private boolean canKick = false;
    private boolean canClaim = false;
    private boolean canUnclaim = false;
    private boolean canPromote = false;
    private boolean canDemote = false;
    private boolean canEdit = false;
    private boolean canDisband = false;

    public CharterTitle(String name, String title, Settlement settlement, int authority) {
        super(name, title);
        this.settlement = settlement;
        this.authority = authority;

        generatePermissionsFromAuthority(authority);
    }

    // used during settlement creation
    public CharterTitle(Settlement settlement, DefaultCharterTitle defaultCharterTitle) {
        super(defaultCharterTitle.getName(), defaultCharterTitle.getTitleAsString());
        this.settlement = settlement;
        this.authority = defaultCharterTitle.getAuthority();

        generatePermissionsFromAuthority(authority);
    }

    public Settlement getSettlement() { return this.settlement; }

    public int getAuthority() {
        return this.authority;
    }

    public boolean isLeader() { return this.authority == 8; }

    public boolean isDefault() { return this.equals(this.getSettlement().getDefaultTitle()); }

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

    public void generatePermissionsFromAuthority(int authority) {

        if (authority == MAX_AUTHORITY) {  // Leader
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = true;
            this.canUnclaim = true;
            this.canPromote = true;
            this.canDemote = true;
            this.canEdit = true;
            this.canDisband = true;
        } else if (authority >= MAX_AUTHORITY / 2) { // Officer, or above
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
            case "canEdit":
                this.canEdit = value;
                break;
            case "canDisband":
                this.canDisband = value;
                break;
            default:
                Charter.getPlugin(Charter.class).getLogger().warning("[PlayerTitle::setPermission] Unknown permission key: " + permissionName);
        }
    }

    public static CharterTitle fromString(String str, Settlement settlement) {
        String[] parts = str.split(";");
        CharterTitle title = new CharterTitle(parts[0], parts[1], settlement, Integer.parseInt(parts[2]));
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
    public boolean equals(Object object) {
        if (object instanceof CharterTitle) {
            CharterTitle other = (CharterTitle) object;
            return this.getName().equals(other.getName()) &&
                    this.getSettlement().getName().equals(other.getSettlement().getName());
        } else return false;
    }

    @Override
    public String serialize() {
        return this.getName() + ";" + this.getTitleAsString() + ";" + this.getAuthority()
                + ";" + this.canInvite() + ";" + this.canKick()
                + ";" + this.canClaim() + ";" + this.canUnclaim()
                + ";" + this.canPromote() + ";" + this.canDemote()
                + ";" + this.canEdit() + ";" + this.canDisband();
    }

    @Override
    public String toString() {
        return "PlayerTitle{" + this.serialize() + "}";
    }
}

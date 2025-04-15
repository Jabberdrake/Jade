package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.titles.NamedTitle;

public class CharterTitle extends NamedTitle {

    public static final int MIN_AUTHORITY = 0;
    public static final int MAX_AUTHORITY = 8;

    private NamedTitle title;
    private Settlement settlement;
    private int authority;
    private Type type;
    private boolean canInvite = false;
    private boolean canKick = false;
    private boolean canClaim = false;
    private boolean canUnclaim = false;
    private boolean canPromote = false;
    private boolean canDemote = false;
    private boolean canEdit = false;

    protected enum Type {
        LEADER,
        DEFAULT,
        NORMAL
    }

    protected CharterTitle(String name, String title, Settlement settlement, Type type, int authority) {
        super(name, title);
        this.settlement = settlement;
        this.authority = authority;
        this.type = type;

        generatePermissionsFromAuthority(authority);
    }

    // used during settlement creation
    public CharterTitle(Settlement settlement, DefaultCharterTitle defaultCharterTitle) {
        super(defaultCharterTitle.getName(), defaultCharterTitle.getTitleAsString());
        this.settlement = settlement;
        this.authority = defaultCharterTitle.getAuthority();
        this.type = defaultCharterTitle.getType();

        generatePermissionsFromAuthority(authority);
    }

    public Settlement getSettlement() { return this.settlement; }

    public int getAuthority() {
        return this.authority;
    }

    protected Type getType() { return this.type; }

    protected static Type parseFromString(String typeAsString) {
        return switch (typeAsString) {
            case "LEADER" -> Type.LEADER;
            case "DEFAULT" -> Type.DEFAULT;
            default -> Type.NORMAL;
        };
    }

    public boolean isLeader() { return this.type == Type.LEADER; }

    public boolean isDefault() { return this.type == Type.DEFAULT; }

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

    public void generatePermissionsFromAuthority(int authority) {

        if (authority == MAX_AUTHORITY) {  // Leader
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = true;
            this.canUnclaim = true;
            this.canPromote = true;
            this.canDemote = true;
            this.canEdit = true;
        } else if (authority >= MAX_AUTHORITY / 2) { // Officer, or above
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
        } else {
            this.canInvite = false;
            this.canKick = false;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
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
            default:
                Jade.getPlugin(Jade.class).getLogger().warning("[PlayerTitle::setPermission] Unknown permission key: " + permissionName);
        }
    }

    public void setToDefault() {
        this.type = Type.DEFAULT;
    }

    public void setToNormal() {
        this.type = Type.NORMAL;
    }

    public static CharterTitle fromString(String str, Settlement settlement) {
        String[] parts = str.split(";");
        CharterTitle title = new CharterTitle(parts[0], parts[1], settlement, CharterTitle.parseFromString(parts[3]), Integer.parseInt(parts[2]));
        title.setPermission("canInvite", Boolean.parseBoolean(parts[4]));
        title.setPermission("canKick", Boolean.parseBoolean(parts[5]));
        title.setPermission("canClaim", Boolean.parseBoolean(parts[6]));
        title.setPermission("canUnclaim", Boolean.parseBoolean(parts[7]));
        title.setPermission("canPromote", Boolean.parseBoolean(parts[8]));
        title.setPermission("canDemote", Boolean.parseBoolean(parts[9]));
        title.setPermission("canEdit", Boolean.parseBoolean(parts[10]));
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
                + ";" + this.canEdit();
    }

    @Override
    public String toString() {
        return "CharterTitle{" + this.serialize() + "}";
    }
}

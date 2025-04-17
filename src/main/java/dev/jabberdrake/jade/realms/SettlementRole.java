package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.titles.NamedTitle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class SettlementRole {

    public static final int MIN_AUTHORITY = 0;
    public static final int MAX_AUTHORITY = 8;

    private int id;
    private String name;
    private TextColor color;
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

    public enum Type {
        LEADER,
        DEFAULT,
        NORMAL
    }

    // Used by DatabaseManager when composing runtime object from persistent data
    public SettlementRole(int id, String name, TextColor color, Settlement settlement, int authority, Type type,
        boolean canInvite, boolean canKick, boolean canClaim, boolean canUnclaim, boolean canPromote, boolean canDemote, boolean canEdit) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.settlement = settlement;
        this.authority = authority;
        this.type = type;

        // I should probably see if I can make this a bit more elegant...
        this.canInvite = canInvite;
        this.canKick = canKick;
        this.canClaim = canClaim;
        this.canUnclaim = canUnclaim;
        this.canPromote = canPromote;
        this.canDemote = canDemote;
        this.canEdit = canEdit;
    }

    // Used by DefaultSettlementRole to generate preset roles
    protected SettlementRole(String name, TextColor color, Settlement settlement, int authority, Type type) {
        this.name = name;
        this.color = color;
        this.settlement = settlement;
        this.authority = authority;
        this.type = type;

        generatePermissionsFromAuthority(authority);
    }

    /*
    public SettlementRole(Settlement settlement, DefaultSettlementRole defaultSettlementRole) {
        super(defaultCharterTitle.getName(), defaultCharterTitle.getTitleAsString());
        this.settlement = settlement;
        this.authority = defaultCharterTitle.getAuthority();
        this.type = defaultCharterTitle.getType();

        generatePermissionsFromAuthority(authority);
    }*/

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public String getDisplayAsString() {
        return "<" + this.getColor().asHexString() + ">" + this.getName();
    }

    public Component getDisplayAsComponent() {
        return Component.text(this.getName(), this.getColor());
    }

    public Settlement getSettlement() { return this.settlement; }

    public int getAuthority() {
        return this.authority;
    }

    public Type getType() { return this.type; }

    public String getTypeAsString() {
        return switch (this.type) {
            case LEADER -> "LEADER";
            case DEFAULT -> "DEFAULT";
            default -> "NORMAL";
        };
    }

    public static Type parseTypeFromString(String typeAsString) {
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
                Jade.getPlugin(Jade.class).getLogger().warning("[SettlementRole::setPermission] Unknown permission key: " + permissionName);
        }
    }

    public void setToDefault() {
        this.type = Type.DEFAULT;
    }

    public void setToNormal() {
        this.type = Type.NORMAL;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SettlementRole) {
            SettlementRole other = (SettlementRole) object;
            return this.getName().equals(other.getName()) &&
                    this.getSettlement().getName().equals(other.getSettlement().getName());
        } else return false;
    }

    @Override
    public String toString() {
        return "SettlementRole{" + this.getName() + "@" + this.getSettlement().getName() + "}";
    }
}

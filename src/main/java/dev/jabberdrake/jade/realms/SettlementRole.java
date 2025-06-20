package dev.jabberdrake.jade.realms;

import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettlementRole implements Comparable<SettlementRole> {

    public static final int MIN_AUTHORITY = 0;
    public static final int MAX_AUTHORITY = 8;

    private int id;
    private String name;
    private TextColor color;
    private Settlement settlement;
    private int authority;
    private Type type;
    private NamespacedKey icon;
    private boolean canInvite = false;
    private boolean canKick = false;
    private boolean canClaim = false;
    private boolean canUnclaim = false;
    private boolean canPromote = false;
    private boolean canDemote = false;
    private boolean canEdit = false;
    private boolean canManage = false;

    @Override
    public int compareTo(@NotNull SettlementRole o) {
        return Integer.compare(this.authority, o.authority);
    }

    public enum Type {
        LEADER,
        DEFAULT,
        NORMAL
    }

    // Used by DatabaseManager when composing runtime object from persistent data
    public SettlementRole(int id, String name, TextColor color, Settlement settlement, int authority, Type type, NamespacedKey icon,
        boolean canInvite, boolean canKick, boolean canClaim, boolean canUnclaim, boolean canPromote, boolean canDemote, boolean canEdit, boolean canManage) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.settlement = settlement;
        this.authority = authority;
        this.type = type;
        this.icon = icon;

        // I should probably see if I can make this a bit more elegant...
        this.canInvite = canInvite;
        this.canKick = canKick;
        this.canClaim = canClaim;
        this.canUnclaim = canUnclaim;
        this.canPromote = canPromote;
        this.canDemote = canDemote;
        this.canEdit = canEdit;
        this.canManage = canManage;
    }

    // Used by DefaultSettlementRole to generate preset roles
    // Used by SettlementManageCommand to create new roles
    public SettlementRole(String name, TextColor color, Settlement settlement, int authority, Type type, NamespacedKey icon) {
        this.name = name;
        this.color = color;
        this.settlement = settlement;
        this.authority = authority;
        this.type = type;
        this.icon = icon;

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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        DatabaseManager.saveSettlementRole(this);
    }

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(TextColor color) {
        this.color = color;
        DatabaseManager.saveSettlementRole(this);
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

    public void increaseAuthority() {
        // To clarify, we only change things if the current authority is 2 units lower than
        // the maximum because:
        // 1. if current authority is 0 units lower (8), then we're handling the leader role;
        // 2. if current authority is 1 unit lower (7), then after promotion, this role would equal the leader role
        //    in authority, which shouldn't ever happen;
        if (this.authority < MAX_AUTHORITY - 1) {
            this.authority++;
        }
        DatabaseManager.saveSettlementRole(this);
    }

    public void decreaseAuthority() {
        if (this.authority > MIN_AUTHORITY) {
            this.authority--;
        }
        DatabaseManager.saveSettlementRole(this);
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

    public void setIcon(NamespacedKey icon) {
        this.icon = icon;
        DatabaseManager.saveSettlementRole(this);
    }

    public String getIconAsString() {
        return this.icon.asString();
    }

    public ItemStack getIconAsItem() {
        return ItemUtils.asDisplayItemBase(this.icon);
    }

    public boolean isLeader() { return this.type == Type.LEADER; }

    public boolean isDefault() { return this.type == Type.DEFAULT; }

    public static String getPermissionAsDisplayString(String permissionNode) {
        switch (permissionNode) {
            case "canInvite":
                return "Can Invite";
            case "canKick":
                return "Can Kick";
            case "canClaim":
                return "Can Claim";
            case "canUnclaim":
                return "Can Unclaim";
            case "canPromote":
                return "Can Promote";
            case "canDemote":
                return "Can Demote";
            case "canEdit":
                return "Can Edit";
            case "canManage":
                return "Can Manage";
            default:
                return "Unknown permission";
        }
    }

    public boolean hasPermission(String permissionNode) {
        switch (permissionNode) {
            case "canInvite":
            case "Can Invite":
                return this.canInvite();
            case "canKick":
            case "Can Kick":
                return this.canKick();
            case "canClaim":
            case "Can Claim":
                return this.canClaim();
            case "canUnclaim":
            case "Can Unclaim":
                return this.canUnclaim();
            case "canPromote":
            case "Can Promote":
                return this.canPromote();
            case "canDemote":
            case "Can Demote":
                return this.canDemote();
            case "canEdit":
            case "Can Edit":
                return this.canEdit();
            case "canManage":
            case "Can Manage":
                return this.canManage();
            default:
                return false;
        }
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

    public boolean canManage() { return this.canManage; }

    public void generatePermissionsFromAuthority(int authority) {

        if (authority == MAX_AUTHORITY) {  // Leader
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = true;
            this.canUnclaim = true;
            this.canPromote = true;
            this.canDemote = true;
            this.canEdit = true;
            this.canManage = true;
        } else if (authority >= MAX_AUTHORITY / 2) { // Officer, or above
            this.canInvite = true;
            this.canKick = true;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
            this.canManage = false;
        } else {
            this.canInvite = false;
            this.canKick = false;
            this.canClaim = false;
            this.canUnclaim = false;
            this.canPromote = false;
            this.canDemote = false;
            this.canEdit = false;
            this.canManage = false;
        }
    }

    public boolean setPermission(String permissionName, boolean value) {
        switch (permissionName) {
            case "canInvite":
                this.canInvite = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canKick":
                this.canKick = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canClaim":
                this.canClaim = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canUnclaim":
                this.canUnclaim = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canPromote":
                this.canPromote = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canDemote":
                this.canDemote = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canEdit":
                this.canEdit = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            case "canManage":
                this.canManage = value;
                DatabaseManager.saveSettlementRole(this);
                break;
            default:
                return false;
        }
        return true;
    }

    public void setToDefault() {
        this.type = Type.DEFAULT;
        DatabaseManager.saveSettlementRole(this);
    }

    public void setToNormal() {
        this.type = Type.NORMAL;
        DatabaseManager.saveSettlementRole(this);
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

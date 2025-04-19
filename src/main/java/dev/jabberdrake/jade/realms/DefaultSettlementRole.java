package dev.jabberdrake.jade.realms;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class DefaultSettlementRole extends SettlementRole {

    private static final String LEADER_NAME = "Leader";
    private static final String OFFICER_NAME = "Officer";
    private static final String MEMBER_NAME = "Member";

    private static final TextColor LEADER_COLOR = NamedTextColor.GOLD;
    private static final TextColor OFFICER_COLOR = NamedTextColor.LIGHT_PURPLE;
    private static final TextColor MEMBER_COLOR = NamedTextColor.YELLOW;

    public DefaultSettlementRole(String name, TextColor color, Settlement settlement, int authority, Type type) {
        super(name, color, settlement, authority, type);
    }

    public static DefaultSettlementRole leader(Settlement settlement) {
        return new DefaultSettlementRole(LEADER_NAME, LEADER_COLOR, settlement, 8, Type.LEADER);
    }

    public static DefaultSettlementRole officer(Settlement settlement) {
        return new DefaultSettlementRole(OFFICER_NAME, OFFICER_COLOR, settlement, 4, Type.NORMAL);
    }

    public static DefaultSettlementRole member(Settlement settlement) {
        return new DefaultSettlementRole(MEMBER_NAME, MEMBER_COLOR, settlement, 0, Type.DEFAULT);
    }
}

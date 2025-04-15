package dev.jabberdrake.jade.realms;

public class DefaultCharterTitle extends CharterTitle {

    private static final String LEADER_NAME = "Leader";
    private static final String OFFICER_NAME = "Officer";
    private static final String MEMBER_NAME = "Member";

    private static final String LEADER_TITLE = "Leader";
    private static final String OFFICER_TITLE = "Officer";
    private static final String MEMBER_TITLE = "Member";

    public DefaultCharterTitle(String name, String title, Settlement settlement, Type type, int authority) {
        super(name, title, settlement, type, authority);
    }

    public static DefaultCharterTitle leader(Settlement settlement) {
        return new DefaultCharterTitle(LEADER_NAME, LEADER_TITLE, settlement, Type.LEADER, 8);
    }

    public static DefaultCharterTitle officer(Settlement settlement) {
        return new DefaultCharterTitle(OFFICER_NAME, OFFICER_TITLE, settlement, Type.NORMAL, 4);
    }

    public static DefaultCharterTitle peasant(Settlement settlement) {
        return new DefaultCharterTitle(MEMBER_NAME, MEMBER_TITLE, settlement, Type.DEFAULT, 0);
    }
}

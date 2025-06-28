package dev.jabberdrake.jade.items;

public enum ItemGroup {
    VANILLA("vanilla"),
    GADGET("gadget");

    private final String key;

    ItemGroup(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

package dev.jabberdrake.jade.items;

public enum ItemGroup {
    VANILLA("vanilla"),
    GADGET("gadget"),
    ARTIFACT("artifact");

    private final String key;

    ItemGroup(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

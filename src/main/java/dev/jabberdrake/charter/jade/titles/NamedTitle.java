package dev.jabberdrake.charter.jade.titles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public abstract class NamedTitle {
    private String name;
    private String title;

    public NamedTitle(String name) {
        this.name = name;
        this.title = name;
    }

    public NamedTitle(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return this.name;
    }

    public String getTitleAsString() {
        return this.title;
    }

    public Component getTitleAsComponent() {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(this.title);
    }

    public String serialize() {
        return this.name + ";" + this.title;
    }

    @Override
    public String toString() {
        return "NamedTitle{name=" + this.name + ", title=" + this.title + "}";
    }
}

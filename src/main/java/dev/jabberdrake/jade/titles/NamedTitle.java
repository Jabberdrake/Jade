package dev.jabberdrake.jade.titles;

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

    public void setName(String name) { this.name = name; }

    public String getDisplayName() {
        return this.title;
    }

    public Component getDisplayAsComponent() {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(this.title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String serialize() {
        return this.name + ";" + this.title;
    }

    @Override
    public String toString() {
        return "NamedTitle{name=" + this.name + ", title=" + this.title + "}";
    }
}

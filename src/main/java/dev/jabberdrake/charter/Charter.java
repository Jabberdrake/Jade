package dev.jabberdrake.charter;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Charter extends JavaPlugin {

    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("Starting up Charter!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Shutting down Charter!");
    }
}

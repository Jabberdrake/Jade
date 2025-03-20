package dev.jabberdrake.charter;

import dev.jabberdrake.charter.commands.CharterCommand;
import dev.jabberdrake.charter.commands.ProfileCommand;
import dev.jabberdrake.charter.commands.SettlementCommand;
import dev.jabberdrake.charter.commands.ToggleRoleplayCommand;
import dev.jabberdrake.charter.handlers.PlayerChatHandler;
import dev.jabberdrake.charter.handlers.PlayerMoveHandler;
import dev.jabberdrake.charter.handlers.PlayerSessionHandler;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.jade.titles.TitleManager;
import dev.jabberdrake.charter.realms.RealmManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Charter extends JavaPlugin {
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("Starting up Charter!");

        registerCommands();
        registerHandlers();

        RealmManager.initialize();
        TitleManager.initialize();
        PlayerManager.initialize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Shutting down Charter!");

        RealmManager.shutdown();
        TitleManager.shutdown();
        PlayerManager.shutdown();
    }

    public void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CharterCommand.buildCommand("charter"), "An all-purpose command for the Charter plugin!");
            commands.registrar().register(SettlementCommand.buildCommand("settlement"), "Manages settlement interactions!");
            commands.registrar().register(ProfileCommand.buildCommand("profile"), "Manage profile interactions!");
            commands.registrar().register(ToggleRoleplayCommand.buildCommand("toggleroleplay"), "Toggle roleplay mode!");
        });
    }

    public void registerHandlers() {
        this.getServer().getPluginManager().registerEvents(new PlayerSessionHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveHandler(), this);
    }
}

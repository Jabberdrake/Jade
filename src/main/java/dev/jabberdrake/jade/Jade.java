package dev.jabberdrake.jade;

import com.flowpowered.math.vector.Vector2i;
import com.technicjelle.BMUtils.Cheese;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import dev.jabberdrake.jade.chat.ChatManager;
import dev.jabberdrake.jade.commands.*;
import dev.jabberdrake.jade.crafting.CraftingManager;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.handlers.*;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.titles.TitleManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Jade extends JavaPlugin {
    private static Jade instance;
    private final Logger logger = this.getLogger();

    public static Jade getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Jade.instance = this;
        logger.info("Preparing to take over the world!");

        if (!getDataFolder().exists()) {
            logger.info("[Jade::onEnable] Data folder not found! Creating a new one...");
            getDataFolder().mkdirs();
        }


        saveResource(JadeConfig.FILENAME, false);
        logger.info("[Jade::onEnable] Loading settings...");
        JadeConfig.load(this);


        registerCommands();
        registerHandlers();

        DatabaseManager.initialize();
        RealmManager.initialize();
        TitleManager.initialize();
        PlayerManager.initialize();
        ChatManager.initialize();
        CraftingManager.initialize();

        logger.info("[Jade::onEnable] Looking for dependencies...");
        BlueMapAPI.onEnable(api -> {
            logger.info("[Jade::onEnable] Bluemap found! Integrating...");

            // sync realm claims every minute
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> syncRealms(api), 0, 60 * 20);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("I'll be back...");

        DatabaseManager.shutdown();
        RealmManager.shutdown();
        TitleManager.shutdown();
        PlayerManager.shutdown();
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(JadeCommand.buildCommand("jade"), "An all-purpose command for the Jade plugin!");
            commands.registrar().register(SettlementCommand.buildCommand("settlement"), "Manages settlement interactions!");
            commands.registrar().register(NationCommand.buildCommand("nation"), "Manages nation interactions!");
            commands.registrar().register(TitleCommand.buildCommand("title"), "Manages vanity titles!");
            commands.registrar().register(ProfileCommand.buildCommand("profile"), "Manage profile interactions!");
            commands.registrar().register(GraveCommand.buildCommand("grave"), "Manage existing deathchests!");
            commands.registrar().register(ToggleRoleplayCommand.buildCommand("toggleroleplay"), "Toggle roleplay mode!");
        });
    }

    private void registerHandlers() {
        this.getServer().getPluginManager().registerEvents(new JadeMenuHandler(), this);

        this.getServer().getPluginManager().registerEvents(new PlayerSessionHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatHandler(), this);

        this.getServer().getPluginManager().registerEvents(new GadgetItemHandler(), this);
        this.getServer().getPluginManager().registerEvents(new VanillaItemHandler(), this);

        this.getServer().getPluginManager().registerEvents(new BlockFadeHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGraveHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRoadHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ExpBottleHandler(), this);

        this.getServer().getPluginManager().registerEvents(new CauldronCraftHandler(), this);

        this.getServer().getPluginManager().registerEvents(new RealmProtectionHandler(), this);
    }

    private void syncRealms(BlueMapAPI api) {
        Map<String, BlueMapWorld> blueMapWorlds = new HashMap<>();
        for (BlueMapWorld world : api.getWorlds()) {
            String worldName = world.getId().split("#")[0];
            blueMapWorlds.put(worldName, world);
        }

        for (String gameworldName : JadeConfig.gameworlds) {
            BlueMapWorld gameworld = blueMapWorlds.get(gameworldName);
            Collection<BlueMapMap> blueMapMaps = gameworld.getMaps();

            MarkerSet settlementSet = MarkerSet.builder().label("Settlements").build();

            for (Settlement settlement : RealmManager.getAllSettlements()) {

                TextColor referenceColor = settlement.getMapColor();
                Color outlineColor = new Color(referenceColor.red(), referenceColor.green(), referenceColor.blue());
                Color mainColor = new Color((int) (outlineColor.getRed() * 0.9), (int) (outlineColor.getGreen() * 0.9), (int) (outlineColor.getBlue() * 0.9), 0.2f);

                Vector2i[] coordinates = settlement.getTerritory().stream()
                        .map(anchor -> new Vector2i(anchor.getX(), anchor.getZ()))
                        .toArray(Vector2i[]::new);

                Collection<Cheese> platter = Cheese.createPlatterFromChunks(coordinates);
                int i = 0;
                for (Cheese cheese : platter) {
                    final String[] membersAsString = {"<br>Members:"};
                    settlement.getPopulation().entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(10)
                            .forEach(entry -> {
                                membersAsString[0] += "<br>— <i>" + entry.getValue().getDisplayAsString().replaceAll("^(<.*>)", "") + "</i> " + Bukkit.getOfflinePlayer(entry.getKey()).getName();
                            });
                    if (settlement.getPopulation().size() > 10) {
                        membersAsString[0] += "<br>— ...";
                    }

                    ShapeMarker stmMarker = ShapeMarker.builder()
                            .label(settlement.getName())
                            .detail("<h1>" + settlement.getName() + "</h1>"
                                    + settlement.getDescription()
                                    + "<br>"
                                    + "<br>Food: " + settlement.getFood() + "/" + settlement.getFoodCapacity()
                                    + "<br>Nation: " + (settlement.isInNation() ? "<b>" + settlement.getNation().getName() + "</b>" : "None")
                                    + membersAsString[0]
                            )
                            .fillColor(mainColor)
                            .lineColor(outlineColor)
                            .lineWidth(2)
                            .depthTestEnabled(false)
                            .shape(cheese.getShape(), 62)
                            .holes(cheese.getHoles().toArray(Shape[]::new))
                            .centerPosition()
                            .build();

                    stmMarker.setMinDistance(50);
                    settlementSet.put("jade.settlements." + settlement.getName().toLowerCase() + ".segment-" + i++, stmMarker);
                }
            }

            for (BlueMapMap map : blueMapMaps) {
                map.getMarkerSets().put("settlements", settlementSet);
            }
        }
    }

    public static NamespacedKey key(String value) {
        return new NamespacedKey(getInstance(), value);
    }
}

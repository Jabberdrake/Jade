package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.players.Grave;
import dev.jabberdrake.jade.utils.PositionUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GraveDataObject implements DatabaseObject<Grave, String> {

    private Jade plugin;
    private Database database;

    public GraveDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public Grave fetch(String id) {
        Grave[] result = {null};
        String sql = "SELECT player_id, position, items FROM graves WHERE id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, id);
            }, resultSet -> {
                if (resultSet.next()) {
                    String playerID = resultSet.getString("player_id");
                    String position = resultSet.getString("position");
                    List<ItemStack> items = List.of(ItemStack.deserializeItemsFromBytes(resultSet.getBytes("items")));
                    Location location = PositionUtils.fromString(position);
                    if (location == null) {
                        result[0] = new Grave(UUID.fromString(playerID), items);
                    } else {
                        result[0] = new Grave(UUID.fromString(playerID), items, location);
                    }
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::fetch] Caught SQLException while fetching grave object: ");
            e.printStackTrace();
        }

        return result[0];
    }

    @Override
    public List<Grave> fetchAll() {
        List<Grave> graves = new ArrayList<>();
        String sql = "SELECT id, player_id, position, items FROM graves;";
        try {
            database.query(sql, resultSet -> {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String playerID = resultSet.getString("player_id");
                    String position = resultSet.getString("position");
                    List<ItemStack> items = List.of(ItemStack.deserializeItemsFromBytes(resultSet.getBytes("items")));
                    Location location = PositionUtils.fromString(position);
                    Grave grave = new Grave(id, UUID.fromString(playerID), items, location);

                    graves.add(grave);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::fetchAll] Caught SQLException while fetching all registered grave objects!");
            e.printStackTrace();
        }
        return graves;
    }

    @Override
    public void save(Grave grave) {
        String sql = "UPDATE graves SET position = ?, items = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, PositionUtils.asString(grave.getChestLocation(), true));
                stmt.setBytes(2, ItemStack.serializeItemsAsBytes(grave.getItems()));
                stmt.setString(3, grave.getID());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::save] Caught SQLException while saving data object for grave " + grave.getID() + ": ");
            e.printStackTrace();
        }
    }

    @Override
    public String create(Grave grave) {
        String sql = "INSERT INTO graves (id, player_id, position, items) VALUES (?, ?, ?, ?);";
        try {
            database.create(sql, stmt -> {
                stmt.setString(1, grave.getID());
                stmt.setString(2, grave.getPlayerID().toString());
                if (grave.isVirtual()) {
                    stmt.setString(3, "NONE");
                } else {
                    stmt.setString(3, PositionUtils.asString(grave.getChestLocation(), true));
                }
                stmt.setBytes(4, ItemStack.serializeItemsAsBytes(grave.getItems()));
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::create] Caught SQLException while creating grave object for " + grave.getID() + ": ");
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM graves WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[GraveDataObject::delete] Caught SQLException while deleting area object for ID=" + id + ": ");
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        String SQL_INIT_GRAVES =
                """
                CREATE TABLE IF NOT EXISTS graves (
                  id STRING PRIMARY KEY,
                  player_id TEXT(36) NOT NULL,
                  position TEXT NOT NULL,
                  items BLOB NOT NULL
                );
                """;

        database.execute(SQL_INIT_GRAVES);
        plugin.getLogger().info("[GraveDataObject::initialize] Initialized data table 'graves'!");
    }
}

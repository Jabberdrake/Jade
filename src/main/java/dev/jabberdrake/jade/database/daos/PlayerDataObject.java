package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseManager;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.titles.JadeTitle;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PlayerDataObject implements DatabaseObject<JadePlayer, UUID> {

    private Jade plugin;
    private Database database;

    public PlayerDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[PlayerDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public JadePlayer fetch(UUID id) {
        String sql = "SELECT roleplay_name, title_id FROM players WHERE uuid = ?;";
        final JadePlayer[] player = {null};
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, id.toString());
            }, resultSet -> {
                if (resultSet.next()) {
                    String roleplay_name = resultSet.getString("roleplay_name");
                    int title_id = resultSet.getInt("title_id");

                    player[0] = new JadePlayer(id, roleplay_name, DatabaseManager.fetchTitleById(title_id));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[PlayerDataObject::fetch] Caught SQLException while fetching by UUID: ");
            e.printStackTrace();
        }
        return player[0];
    }

    @Override
    public List<JadePlayer> fetchAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(JadePlayer jadePlayer) {
        String sql = "UPDATE players SET roleplay_name = ?, title_id = ? WHERE uuid = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, jadePlayer.getRoleplayName());
                stmt.setInt(2, jadePlayer.getTitleInUse().getId());
                stmt.setString(3, jadePlayer.getUniqueID().toString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[PlayerDataObject::save] Caught SQLException while updating player record: ");
            e.printStackTrace();
        }
    }

    @Override
    public UUID create(JadePlayer jadePlayer) {
        String sql = "INSERT INTO players (uuid, roleplay_name, title_id) VALUES (?, ?, ?);";
        try {
            // Not using database.create() here because I'm not leaving the identifier up to the database
            database.execute(sql, stmt -> {
                stmt.setString(1, jadePlayer.getUniqueID().toString());
                stmt.setString(2, jadePlayer.getRoleplayName());
                stmt.setInt(3, 1);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[PlayerDataObject::create] Caught SQLException while creating player record: ");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("This is not supported and will never be.");
    }

    public void unsetTitleForAllPlayers(JadeTitle title) {
        String sql = "UPDATE players SET title_id = ? WHERE title_id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, 1);
                stmt.setInt(2, title.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[PlayerDataObject::unsetTitleForAllPlayers] Caught SQLException while resetting title in use for all players: ");
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        String SQL_INIT_TABLE =
                """
                CREATE TABLE IF NOT EXISTS players (
                  uuid TEXT(36) PRIMARY KEY,
                  roleplay_name TEXT NOT NULL,
                  title_id INTEGER NOT NULL,
                    CONSTRAINT profiles_title_id_fk
                      FOREIGN KEY (title_id) REFERENCES titles (id)
                );
                """;

        database.execute(SQL_INIT_TABLE);
        plugin.getLogger().info("[PlayerDataObject::initialize] Initialized data table 'players'!");
    }
}

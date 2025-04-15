package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.titles.DefaultJadeTitle;
import dev.jabberdrake.jade.titles.JadeTitle;
import net.kyori.adventure.text.format.TextColor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TitleDataObject implements DatabaseObject<JadeTitle, Integer> {

    private Jade plugin;
    private Database database;

    public TitleDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public JadeTitle fetch(Integer id) {
        final JadeTitle[] title = {null};
        String sql = "SELECT name, title, owner_id, sender_color, icon FROM titles WHERE id = ?;";

        // Fetch title object
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, id);
            }, resultSet -> {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String serializedTitle = resultSet.getString("title");
                    String ownerId = resultSet.getString("owner_id");
                    String senderColor = resultSet.getString("sender_color");
                    String icon = resultSet.getString("icon");
                    if (ownerId.equals("universal")) {
                        title[0] = new JadeTitle(id, name, serializedTitle, null, TextColor.fromHexString(senderColor), icon);
                    } else {
                        title[0] = new JadeTitle(id, name, serializedTitle, UUID.fromString(ownerId), TextColor.fromHexString(senderColor), icon);
                    }
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::fetch] Caught SQLException while fetching title object: ");
            e.printStackTrace();
        }

        // Fetch user list
        if (title[0] != null) {
            String sql2 = "SELECT player_id FROM title_users WHERE title_id = ?;";
            try {
                database.query(sql2, stmt -> {
                    stmt.setInt(1, id);
                }, resultSet -> {
                    while (resultSet.next()) {
                        String userID = resultSet.getString("player_id");
                        title[0].getUserList().add(UUID.fromString(userID));
                    }
                });
            } catch (SQLException e) {
                plugin.getLogger().warning("[TitleDataObject::fetch] Caught SQLException while fetching title user list: ");
                e.printStackTrace();
            }
        }

        return title[0];
    }

    @Override
    public List<JadeTitle> fetchAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(JadeTitle jadeTitle) {
        String sql = """
                INSERT INTO titles (id, name, title, owner_id, sender_color, icon)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO UPDATE SET
                        name = excluded.name,
                        title = excluded.title,
                        owner_id = excluded.owner_id,
                        sender_color = excluded.sender_color,
                        icon = excluded.icon;
                """;
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, jadeTitle.getId());
                stmt.setString(2, jadeTitle.getName());
                stmt.setString(3, jadeTitle.getTitleAsString());
                if (jadeTitle.isUniversal()) {
                    stmt.setString(4, "universal");
                } else {
                    stmt.setString(4, jadeTitle.getOwner().toString());
                }
                stmt.setString(5, jadeTitle.getSenderColor().asHexString());
                stmt.setString(6, jadeTitle.getIconAsString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::save] Caught SQLException while saving title object: ");
            e.printStackTrace();
        }

        /*
        Note for self:
            Due to how I *think* I've designed this, there's no point in saving the title's userlist, because
            that process is already done whenever the userlist is changed (e.g.: through /title allow or /title revoke).
            If we find, during testing, that this doesn't actually work the way I think it will, I will cry.
         */
    }

    @Override
    public Integer create(JadeTitle title) {
        final Integer[] id = {null};
        String sql = "INSERT INTO titles (name, title, owner_id, sender_color, icon) VALUES (?, ?, ?, ?, ?)";
        try {
            id[0] = Math.toIntExact(database.create(sql, stmt -> {
                stmt.setString(1, title.getName());
                stmt.setString(2, title.getTitleAsString());
                stmt.setString(3, title.getOwner().toString());
                stmt.setString(4, title.getSenderColor().asHexString());
                stmt.setString(5, title.getIconAsString());
            }));
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::create] Caught SQLException while creating title object: ");
            e.printStackTrace();
        }
        return id[0];
    }

    @Override
    public void delete(Integer id) {
        // Deleting title object
        String sql = "DELETE FROM titles WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::delete] Caught SQLException while deleting title object: ");
            e.printStackTrace();
        }

        // Deleting user list, if applicable
        String sql2 = "DELETE FROM title_users WHERE title_id = ?;";
        try {
            database.execute(sql2, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::delete] Caught SQLException while deleting title user list: ");
            e.printStackTrace();
        }
    }

    public void makeTitleUniversal(int id) {
        String sql1 = "UPDATE titles SET owner_id = universal WHERE id = ?;";
        try {
            database.execute(sql1, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::makeTitleUniversal] Caught SQLException while editing owner_id column: ");
            e.printStackTrace();
        }

        String sql2 = "DELETE FROM title_users WHERE title_id = ?;";
        try {
            database.execute(sql2, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::makeTitleUniversal] Caught SQLException while clearing obsolete entries in title_users: ");
            e.printStackTrace();
        }
    }

    public List<Integer> fetchAllTitlesOwnedBy(UUID ownerID) {
        List<Integer> titleIDList = new ArrayList<>();
        String sql = "SELECT id FROM titles WHERE owner_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, ownerID.toString());
            }, resultSet -> {
                while (resultSet.next()) {
                    titleIDList.add(resultSet.getInt("id"));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::fetchAllTitlesOwnedBy] Caught SQLException while fetching titles: ");
            e.printStackTrace();
        }
        return titleIDList;
    }

    public List<Integer> fetchAllTitlesAvailableTo(UUID playerID) {
        List<Integer> titleIDList = new ArrayList<>();
        String sql = "SELECT title_id FROM title_users WHERE player_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, playerID.toString());
            }, resultSet -> {
                while (resultSet.next()) {
                    titleIDList.add(resultSet.getInt("title_id"));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::fetchAllTitlesOwnedBy] Caught SQLException while fetching titles: ");
            e.printStackTrace();
        }
        return titleIDList;
    }

    public List<Integer> fetchAllUniversalTitles() {
        List<Integer> titleIDList = new ArrayList<>();
        String sql = "SELECT id FROM titles WHERE owner_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, "universal");
            }, resultSet -> {
                while (resultSet.next()) {
                    titleIDList.add(resultSet.getInt("id"));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::fetchAllUniversalTitles] Caught SQLException while fetching all universal titles: ");
            e.printStackTrace();
        }

        return titleIDList;
    }

    public boolean checkForUniqueName(String name) {
        boolean[] result = {false};
        String sql = "SELECT COUNT(1) AS name_count FROM titles WHERE name = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, name);
            }, resultSet -> {
                if (resultSet.next()) {
                    result[0] = resultSet.getInt("name_count") == 0;
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::checkForUniqueName] Caught SQLException while counting name occurences: ");
            e.printStackTrace();
        }
        return result[0];
    }

    public void addPlayerToTitle(JadeTitle title, UUID playerID) {
        try {
            String sql = "INSERT INTO title_users (title_id, player_id) VALUES (?, ?);";
            database.execute(sql, stmt -> {
                stmt.setInt(1, title.getId());
                stmt.setString(2, playerID.toString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::addPlayerToTitle] Caught SQLException while allowing title access to player: ");
            e.printStackTrace();
        }
    }

    public void removePlayerFromTitle(JadeTitle title, UUID playerID) {
        try {
            String sql = "DELETE FROM title_users WHERE title_id = ? AND player_ID = ?;";
            database.execute(sql, stmt -> {
                stmt.setInt(1, title.getId());
                stmt.setString(2, playerID.toString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[TitleDataObject::removePlayerFromTitle] Caught SQLException while revoking title access from player: ");
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        String SQL_INIT_TITLES =
                """
                CREATE TABLE IF NOT EXISTS titles (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  title TEXT NOT NULL,
                  owner_id TEXT(36) NOT NULL,
                  sender_color TEXT(7) DEFAULT '#FFFFFF' NOT NULL,
                  icon TEXT NOT NULL DEFAULT "minecraft:book"
                );
                """;
        String SQL_INIT_TITLE_USERS =
                """
                CREATE TABLE IF NOT EXISTS title_users (
                  title_id INTEGER NOT NULL,
                  player_id TEXT(36) NOT NULL,
                    CONSTRAINT titles_users_pk
                      PRIMARY KEY (title_id, player_id),
                    CONSTRAINT titleusers_title_id_fk
                      FOREIGN KEY (title_id) REFERENCES titles (id)
                      ON DELETE CASCADE
                );
                """;

        database.execute(SQL_INIT_TITLES);
        plugin.getLogger().info("[TitleDataObject::initialize] Initialized data table 'titles'!");
        database.execute(SQL_INIT_TITLE_USERS);
        plugin.getLogger().info("[TitleDataObject::initialize] Initialized data table 'title_users'!");

        JadeTitle firstTitle = this.fetch(1);
        if (firstTitle == null) {
            this.save(DefaultJadeTitle.PEASANT);
            plugin.getLogger().info("[TitleDataObject::initialize] Could not find default universal title. Creating!");
        }
    }
}

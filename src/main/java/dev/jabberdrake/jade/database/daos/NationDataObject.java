package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.Settlement;
import net.kyori.adventure.text.format.TextColor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dev.jabberdrake.jade.utils.ItemUtils.parseKey;

public class NationDataObject implements DatabaseObject<Nation, Integer> {

    private Jade plugin;
    private Database database;

    public NationDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public Nation fetch(Integer id) {
        Nation[] result = {null};
        String sql = "SELECT name, display_name, description, map_color, icon, creation_time, capital_id FROM nations WHERE id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, id);
            }, resultSet -> {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    String description = resultSet.getString("description");
                    String mapColor = resultSet.getString("map_color");
                    String icon = resultSet.getString("icon");
                    long creationTime = resultSet.getLong("creation_time");
                    int capitalId = resultSet.getInt("capital_id");

                    result[0] = new Nation(id, name, displayName, description, TextColor.fromHexString(mapColor), parseKey(icon), creationTime, capitalId);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::fetch] Caught SQLException while fetching nation object: ");
            e.printStackTrace();
        }

        // If there is no matching record in the main nation table, quit;
        // otherwise, fetch remaining attributes from auxiliary tables
        if (result[0] == null) {
            return null;
        }

        result[0].setMemberList(fetchMemberIDList(result[0]));

        return result[0];
    }

    public Nation fetchByName(String name) {
        Nation[] result = {null};
        String sql = "SELECT id, display_name, description, map_color, icon, creation_time, capital_id FROM nations WHERE name = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setString(1, name);
            }, resultSet -> {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String displayName = resultSet.getString("display_name");
                    String description = resultSet.getString("description");
                    String mapColor = resultSet.getString("map_color");
                    String icon = resultSet.getString("icon");
                    long creationTime = resultSet.getLong("creation_time");
                    int capitalId = resultSet.getInt("capital_id");

                    result[0] = new Nation(id, name, displayName, description, TextColor.fromHexString(mapColor), parseKey(icon), creationTime, capitalId);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::fetchByName] Caught SQLException while fetching nation object: ");
            e.printStackTrace();
        }

        // If there is no matching record in the main nation table, quit;
        // otherwise, fetch remaining attributes from auxiliary tables
        if (result[0] == null) {
            return null;
        }

        result[0].setMemberList(fetchMemberIDList(result[0]));

        return result[0];
    }

    public List<Integer> fetchMemberIDList(Nation nation) {
        List<Integer> memberIDList = new ArrayList<>();
        String sql = "SELECT settlement_id FROM nation_members WHERE nation_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, nation.getId());
            }, resultSet -> {
                while (resultSet.next()) {
                    int settlementId = resultSet.getInt("settlement_id");
                    memberIDList.add(settlementId);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::fetchMemberIDList] Caught SQLException while fetching member settlement IDs for nation" + nation.getName() + ": ");
            e.printStackTrace();
        }
        return memberIDList;
    }

    @Override
    public List<Nation> fetchAll() {
        List<Nation> nations = new ArrayList<>();
        String sql = "SELECT id, name, display_name, description, map_color, icon, creation_time, capital_id FROM nations";
        try {
            database.query(sql, resultSet -> {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    String description = resultSet.getString("description");
                    String mapColor = resultSet.getString("map_color");
                    String icon = resultSet.getString("icon");
                    long creationTime = resultSet.getLong("creation_time");
                    int capitalId = resultSet.getInt("capital_id");

                    Nation nation = new Nation(id, name, displayName, description, TextColor.fromHexString(mapColor), parseKey(icon), creationTime, capitalId);
                    nation.setMemberList(fetchMemberIDList(nation));

                    nations.add(nation);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetchAll] Caught SQLException while fetching all registered nation objects!");
            e.printStackTrace();
        }
        return nations;
    }

    @Override
    public void save(Nation nation) {
        String sql = "UPDATE nations SET name = ?, display_name = ?, description = ?, map_color = ?, icon = ?, creation_time = ?, capital_id = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, nation.getName());
                stmt.setString(2, nation.getDisplayName());
                stmt.setString(3, nation.getDescription());
                stmt.setString(4, nation.getMapColor().asHexString());
                stmt.setString(5, nation.getIconAsString());
                stmt.setLong(6, nation.getCreationTimeAsLong());
                stmt.setInt(7, nation.getCapital().getId());
                stmt.setInt(8, nation.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::save] Caught SQLException while saving data object for nation " + nation.getName() + ": ");
            e.printStackTrace();
        }
    }

    @Override
    public Integer create(Nation nation) {
        final Integer[] id = {null};
        String sql = "INSERT INTO nations (name, display_name, description, map_color, icon, creation_time, capital_id) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            id[0] = Math.toIntExact(database.create(sql, stmt -> {
                stmt.setString(1, nation.getName());
                stmt.setString(2, nation.getDisplayName());
                stmt.setString(3, nation.getDescription());
                stmt.setString(4, nation.getMapColor().asHexString());
                stmt.setString(5, nation.getIconAsString());
                stmt.setLong(6, nation.getCreationTimeAsLong());
                stmt.setInt(7, nation.getCapital().getId());
            }));
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::create] Caught SQLException while creating nation object for " + nation.getName() + ": ");
            e.printStackTrace();
        }

        return id[0];
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM nations WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::delete] Caught SQLException while deleting nation object for ID=" + id + ": ");
            e.printStackTrace();
        }
    }

    public void addMemberToNation(Settlement settlement, Nation nation) {
        String sql = "INSERT INTO nation_members (nation_id, settlement_id) VALUES (?, ?);";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, nation.getId());
                stmt.setInt(2, settlement.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::addMemberToNation] Caught SQLException while adding settlement " + settlement.getName() + " to nation " + nation.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void removeMemberFromNation(Settlement settlement, Nation nation) {
        String sql = "DELETE FROM nation_members WHERE nation_id = ? AND settlement_id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, nation.getId());
                stmt.setInt(2, settlement.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::removeMemberFromNation] Caught SQLException while remove settlement " + settlement.getName() + " from nation " + nation.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void alterCapitalForNation(Settlement settlement, Nation nation) {
        String sql = "UPDATE nations SET capital_id = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
                stmt.setInt(2, nation.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[NationDataObject::alterCapitalForNation] Caught SQLException while making settlement " + settlement.getName() + " the capital of nation " + nation.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        String SQL_INIT_NATIONS =
                """
                CREATE TABLE IF NOT EXISTS nations (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  display_name TEXT NOT NULL,
                  description TEXT NOT NULL,
                  map_color TEXT(6) DEFAULT 'FFAA00' NOT NULL,
                  icon TEXT DEFAULT "minecraft:barrel" NOT NULL,
                  creation_time INTEGER DEFAULT CURRENT_TIMESTAMP NOT NULL,
                  capital_id INTEGER NOT NULL,
                    CONSTRAINT nations_capital_id_fk
                      FOREIGN KEY (capital_id) REFERENCES settlements (id)
                      ON DELETE CASCADE
                );
                """;

        String SQL_INIT_NATION_MEMBERS =
                """
                CREATE TABLE IF NOT EXISTS nation_members (
                    nation_id INTEGER NOT NULL,
                    settlement_id INTEGER NOT NULL,
                      CONSTRAINT nation_members_pk
                        PRIMARY KEY (nation_id, settlement_id),
                      CONSTRAINT natmembers_nation_id_fk
                        FOREIGN KEY (nation_id) REFERENCES nations (id)
                        ON DELETE CASCADE,
                      CONSTRAINT natmembers_settlement_id_fk
                        FOREIGN KEY (settlement_id) REFERENCES settlements (id)
                        ON DELETE CASCADE
                );
                """;

        database.execute(SQL_INIT_NATIONS);
        plugin.getLogger().info("[NationDataObject::initialize] Initialized data table 'nations'!");
        database.execute(SQL_INIT_NATION_MEMBERS);
        plugin.getLogger().info("[NationDataObject::initialize] Initialized data table 'nation_members'!");
    }
}

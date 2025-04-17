package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class SettlementDataObject implements DatabaseObject<Settlement, Integer> {

    private Jade plugin;
    private Database database;

    public SettlementDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public Settlement fetch(Integer id) {
        Settlement[] result = {null};
        String sql = "SELECT name, display_name, description, map_color, icon, creation_time, nation_id FROM settlements WHERE id = ?";
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
                    int nationId = resultSet.getInt("nation_id");
                    result[0] = new Settlement(id, name, displayName, description, TextColor.fromHexString(mapColor), icon, creationTime, RealmManager.getNation(nationId));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetch] Caught SQLException while fetching settlement object: ");
            e.printStackTrace();
        }

        // If there is no matching record in the main settlement table, quit;
        // otherwise, fetch remaining attributes from auxiliary tables
        if (result[0] == null) {
            return null;
        }

        result[0].setRoles(this.fetchRoles(result[0]));
        result[0].setPopulation(this.fetchPopulation(result[0]));
        result[0].setTerritory(this.fetchTerritory(result[0]));

        return result[0];
    }

    public List<SettlementRole> fetchRoles(Settlement settlement) {
        List<SettlementRole> roles = new ArrayList<>();
        String sql = "SELECT id, name, color, authority, type, can_invite, can_kick, can_claim, can_unclaim, can_promote, can_demote, can_edit FROM settlement_roles WHERE settlement_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
            }, resultSet -> {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String color = resultSet.getString("color");
                    int authority = resultSet.getInt("authority");
                    String type = resultSet.getString("type");
                    boolean canInvite = resultSet.getBoolean("can_invite");
                    boolean canKick = resultSet.getBoolean("can_kick");
                    boolean canClaim = resultSet.getBoolean("can_claim");
                    boolean canUnclaim = resultSet.getBoolean("can_unclaim");
                    boolean canPromote = resultSet.getBoolean("can_promote");
                    boolean canDemote = resultSet.getBoolean("can_demote");
                    boolean canEdit = resultSet.getBoolean("can_edit");

                    SettlementRole role = new SettlementRole(id, name, TextColor.fromHexString(color), settlement, authority, SettlementRole.parseTypeFromString(type), canInvite, canKick, canClaim, canUnclaim, canPromote, canDemote, canEdit);
                    roles.add(role);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetchRoles] Caught SQLException while fetching roles for settlement" + settlement.getName() + ": ");
            e.printStackTrace();
        }

        return roles;
    }

    public Map<UUID, SettlementRole> fetchPopulation(Settlement settlement) {
        Map<UUID, SettlementRole> population = new HashMap<>();
        String sql = "SELECT player_id, role_id FROM settlement_members WHERE settlement_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
            }, resultSet -> {
                while (resultSet.next()) {
                    String playerUUID = resultSet.getString("player_id");
                    int roleId = resultSet.getInt("role_id");
                    population.put(UUID.fromString(playerUUID), settlement.getRoleForId(roleId));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetchPopulation] Caught SQLException while fetching members for settlement" + settlement.getName() + ": ");
            e.printStackTrace();
        }

        return population;
    }

    public Set<ChunkAnchor> fetchTerritory(Settlement settlement) {
        Set<ChunkAnchor> territory = new HashSet<>();
        String sql = "SELECT chunk_x, chunk_z FROM settlement_chunks WHERE settlement_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
            }, resultSet -> {
                while (resultSet.next()) {
                    int chunkX = resultSet.getInt("chunk_x");
                    int chunkZ = resultSet.getInt("chunk_z");
                    territory.add(new ChunkAnchor(chunkX, chunkZ));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetchTerritory] Caught SQLException while fetching chunks for settlement" + settlement.getName() + ": ");
            e.printStackTrace();
        }

        return territory;
    }

    @Override
    public List<Settlement> fetchAll() {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT id, name, display_name, description, map_color, icon, creation_time, nation_id FROM settlements";
        try {
            database.query(sql,resultSet -> {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    String description = resultSet.getString("description");
                    String mapColor = resultSet.getString("map_color");
                    String icon = resultSet.getString("icon");
                    long creationTime = resultSet.getLong("creation_date");
                    int nationId = resultSet.getInt("nation_id");

                    Settlement stm = new Settlement(id, name, displayName, description, TextColor.fromHexString(mapColor), icon, creationTime, RealmManager.getNation(nationId));

                    stm.setRoles(this.fetchRoles(stm));
                    stm.setPopulation(this.fetchPopulation(stm));
                    stm.setTerritory(this.fetchTerritory(stm));

                    settlements.add(stm);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::fetchAll] Caught SQLException while fetching all registered settlement objects: ");
            e.printStackTrace();
        }

        return settlements;
    }

    @Override
    public void save(Settlement settlement) {
        String sql = "UPDATE settlements SET name = ?, display_name = ?, description = ?, map_color = ?, icon = ?, creation_time = ?, nation_id = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, settlement.getName());
                stmt.setString(2, settlement.getDisplayNameAsString());
                stmt.setString(3, settlement.getDescriptionAsString());
                stmt.setString(4, settlement.getMapColor().asHexString());
                stmt.setString(5, settlement.getIconAsString());
                stmt.setLong(6, settlement.getCreationTimeAsLong());
                if (settlement.getNation() == null) {
                    stmt.setNull(7, Types.INTEGER);
                } else {
                    stmt.setInt(7, settlement.getNation().getId());
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::save] Caught SQLException while saving data object for settlement" + settlement.getName() + ": ");
            e.printStackTrace();
        }

        // As per the way I've designed this, any state changes to roles, population and/or territory are saved
        // at different moments, and therefore do not need to be repeated here. This method is solely for saving
        // changes made to the main settlement attributes.

        // For example, if someone joins my settlement, they will be registered as a member in the database
        // whenever the join event happens by way of DatabaseManager#addPlayerToSettlement, which calls a specialized
        // method for the job here.
    }

    @Override
    public Integer create(Settlement settlement) {
        final Integer[] id = {null};
        String sql = "INSERT INTO settlements (name, display_name, description, map_color, icon, creation_time, nation_id) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            id[0] = Math.toIntExact(database.create(sql, stmt -> {
                stmt.setString(1, settlement.getName());
                stmt.setString(2, settlement.getDisplayNameAsString());
                stmt.setString(3, settlement.getDescriptionAsString());
                stmt.setString(4, settlement.getMapColor().asHexString());
                stmt.setString(5, settlement.getIconAsString());
                stmt.setLong(6, settlement.getCreationTimeAsLong());
                stmt.setNull(7, Types.INTEGER);     // This code is run whenever the settlement is created, so it's safe to assume that it doesn't belong to a nation yet.
            }));
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::create] Caught SQLException while creating settlement object for " + settlement.getName() + ": ");
            e.printStackTrace();
        }

        // Note to self:
        // I initially thought that I had to create all the necessary DB entries for the default/preset roles,
        // the starting chunk and the founder player, but then I realized that all that can be done through the same
        // methods as those used to generically add new roles, chunks and members.
        // ...That being said, I haven't put that realization up to the test quite yet. I hope it works.
        return id[0];
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM settlements WHERE id = ?;";
        try {
            database.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::delete] Caught SQLException while deleting settlement object for ID=" + id + ": ");
            e.printStackTrace();
        }

        // In principle, due to the ON CASCADE effect, I *shouldn't* have to bother manually
        // deleting all auxiliary entries. But I dunno

    }

    public Integer createSettlementRole(SettlementRole role) {
        Integer[] result = {null};
        String sql = "INSERT INTO settlement_roles (settlement_id, name, color, authority, type, can_invite, can_kick, can_claim, can_unclaim, can_promote, can_demote, can_edit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            result[0] = Math.toIntExact(database.create(sql, stmt -> {
                stmt.setInt(1, role.getSettlement().getId());
                stmt.setString(2, role.getName());
                stmt.setString(3, role.getColor().asHexString());
                stmt.setInt(4, role.getAuthority());
                stmt.setString(5, role.getTypeAsString());
                stmt.setBoolean(6, role.canInvite());
                stmt.setBoolean(7, role.canKick());
                stmt.setBoolean(8, role.canClaim());
                stmt.setBoolean(9, role.canUnclaim());
                stmt.setBoolean(10, role.canPromote());
                stmt.setBoolean(11, role.canDemote());
                stmt.setBoolean(12, role.canEdit());
            }));
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::createSettlementRole] Caught SQLException while creating settlement role object for " + role.toString() + ": ");
            e.printStackTrace();
        }
        return result[0];
    }

    public void saveSettlementRole(SettlementRole role) {
        String sql = "UPDATE settlement_roles SET name = ?, color = ?, authority = ?, type = ?, can_invite = ?, can_kick = ?, can_claim = ?, can_unclaim = ?, can_promote = ?, can_demote = ?, can_edit = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, role.getName());
                stmt.setString(2, role.getColor().asHexString());
                stmt.setInt(3, role.getAuthority());
                stmt.setString(4, role.getTypeAsString());
                stmt.setBoolean(5, role.canInvite());
                stmt.setBoolean(6, role.canKick());
                stmt.setBoolean(7, role.canClaim());
                stmt.setBoolean(8, role.canUnclaim());
                stmt.setBoolean(9, role.canPromote());
                stmt.setBoolean(10, role.canDemote());
                stmt.setBoolean(11, role.canEdit());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::saveSettlementRole] Caught SQLException while saving settlement role object for " + role.toString() + ": ");
            e.printStackTrace();
        }
    }

    public void deleteSettlementRole(SettlementRole role) {
        String sql1 = "DELETE FROM settlement_roles WHERE id = ?;";
        try {
            database.execute(sql1, stmt -> {
                stmt.setInt(1, role.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::deleteSettlementRole] Caught SQLException while deleting settlement role object for " + role.toString() + ": ");
            e.printStackTrace();
        }

        // Reassign every player who had the role we just deleted to the matching settlement's default role
        String sql2 = "UPDATE settlement_members SET role_id = ? WHERE settlement_id = ? AND role_id = ?;";
        try {
            database.execute(sql2, stmt -> {
                stmt.setInt(1, role.getSettlement().getDefaultRole().getId());  // We can be reasonably safe that we don't need to fetch this again
                stmt.setInt(2, role.getSettlement().getId());
                stmt.setInt(3, role.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::deleteSettlementRole] Caught SQLException while resetting member role assignments after deleting role object for " + role.toString() + ": ");
            e.printStackTrace();
        }
    }

    public void addPlayerToSettlement(UUID playerId, Settlement settlement, SettlementRole role) {
        String sql = "INSERT INTO settlement_members (settlement_id, player_id, role_id) VALUES (?, ?, ?);";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
                stmt.setString(2, playerId.toString());
                stmt.setInt(3, role.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::addPlayerToSettlement] Caught SQLException while adding player " + playerId.toString() + " to settlement " + settlement.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void removePlayerFromSettlement(UUID playerId, Settlement settlement) {
        String sql = "DELETE FROM settlement_members WHERE player_id = ? AND settlement_id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, playerId.toString());
                stmt.setInt(2, settlement.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::removePlayerFromSettlement] Caught SQLException while removing player " + playerId.toString() + " from settlement " + settlement.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void alterPlayerForSettlement(UUID playerId, Settlement settlement, SettlementRole role) {
        String sql = "UPDATE settlement_members SET role_id = ? WHERE player_id = ? AND settlement_id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, role.getId());
                stmt.setString(2, playerId.toString());
                stmt.setInt(3, settlement.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::alterPlayerForSettlement] Caught SQLException while altering player " + playerId.toString() + " for settlement " + settlement.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void addChunkToSettlement(ChunkAnchor anchor, Settlement settlement) {
        String sql = "INSERT INTO settlement_chunks (settlement_id, chunk_x, chunk_z) VALUES (?, ?, ?);";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, settlement.getId());
                stmt.setInt(2, anchor.getX());
                stmt.setInt(3, anchor.getZ());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::addChunkToSettlement] Caught SQLException while adding " + anchor.toString() + " to settlement " + settlement.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void removeChunkFromSettlement(ChunkAnchor anchor, Settlement settlement) {
        String sql = "DELETE FROM settlement_chunks WHERE chunk_x = ? AND chunk_z = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, anchor.getX());
                stmt.setInt(2, anchor.getZ());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[SettlementDataObject::removeChunkFromSettlement] Caught SQLException while removing " + anchor.toString() + " from settlement " + settlement.getName() + ": ");
            e.printStackTrace();
        }
    }

    public Map<ChunkAnchor, Settlement> fetchTerritoryMap(List<Settlement> settlements) {
        Map<ChunkAnchor, Settlement> map = new HashMap<>();
        for (Settlement settlement : settlements) {
            String sql = "SELECT chunk_x, chunk_z FROM settlement_chunks WHERE settlement_id = ?;";
            try {
                database.query(sql, stmt -> {
                    stmt.setInt(1, settlement.getId());
                }, resultSet -> {
                    while (resultSet.next()) {
                        int chunkX = resultSet.getInt("chunk_x");
                        int chunkZ = resultSet.getInt("chunk_z");
                        map.put(new ChunkAnchor(chunkX, chunkZ), settlement);
                    }
                });
            } catch (SQLException e) {
                plugin.getLogger().warning("[SettlementDataObject::fetchTerritoryMap] Caught SQLException while composing global territory map: ");
                e.printStackTrace();
            }
        }
        return map;
    }

    public void initialize() throws SQLException {
        String SQL_INIT_SETTLEMENTS =
                """
                CREATE TABLE IF NOT EXISTS settlements (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  display_name TEXT NOT NULL,
                  description TEXT NOT NULL,
                  map_color TEXT(7) DEFAULT 'FFAA00' NOT NULL,
                  icon TEXT NOT NULL,
                  creation_time INTEGER DEFAULT CURRENT_TIMESTAMP NOT NULL,
                  nation_id INTEGER DEFAULT NULL,
                    CONSTRAINT stms_nation_id_fk
                      FOREIGN KEY (nation_id) REFERENCES nations (id)
                      ON DELETE SET NULL
                );
                """;
        String SQL_INIT_SETTLEMENT_ROLES =
                """
                CREATE TABLE IF NOT EXISTS settlement_roles (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  settlement_id INTEGER NOT NULL,
                  name TEXT NOT NULL,
                  color TEXT(7) NOT NULL,
                  authority INTEGER DEFAULT 0 NOT NULL CHECK (authority BETWEEN 0 AND 8),
                  type TEXT DEFAULT 'NONE' NOT NULL,
                  can_invite BOOLEAN DEFAULT FALSE NOT NULL,
                  can_kick BOOLEAN DEFAULT FALSE NOT NULL,
                  can_claim BOOLEAN DEFAULT FALSE NOT NULL,
                  can_unclaim BOOLEAN DEFAULT FALSE NOT NULL,
                  can_promote BOOLEAN DEFAULT FALSE NOT NULL,
                  can_demote BOOLEAN DEFAULT FALSE NOT NULL,
                  can_edit BOOLEAN DEFAULT FALSE NOT NULL,
                    CONSTRAINT stmroles_settlement_id_fk
                      FOREIGN KEY (settlement_id) REFERENCES settlements (id)
                      ON DELETE CASCADE
                );
                """;
        String SQL_INIT_SETTLEMENT_MEMBERS =
                """
                CREATE TABLE IF NOT EXISTS settlement_members (
                  settlement_id INTEGER NOT NULL,
                  player_id TEXT(36) NOT NULL,
                  role_id INTEGER NOT NULL,
                    CONSTRAINT settlement_members_pk
                      PRIMARY KEY (settlement_id, player_id),
                    CONSTRAINT settlement_id_fk
                      FOREIGN KEY (settlement_id) REFERENCES settlements (id)
                      ON DELETE CASCADE,
                    CONSTRAINT stmpopulation_role_id_fk
                      FOREIGN KEY (role_id) REFERENCES settlement_roles (id)
                      ON DELETE DO NOTHING
                );
                """;
        String SQL_INIT_SETTLEMENT_CHUNKS =
                """
                CREATE TABLE IF NOT EXISTS settlement_chunks (
                  settlement_id INTEGER NOT NULL,
                  chunk_x INTEGER NOT NULL,
                  chunk_z INTEGER NOT NULL,
                    CONSTRAINT settlement_chunks_pk
                      PRIMARY KEY (chunk_x, chunk_z),
                    CONSTRAINT stmchunks_settlement_id_fk
                      FOREIGN KEY (settlement_id) REFERENCES settlements (id)
                      ON DELETE CASCADE
                );
                """;

        database.execute(SQL_INIT_SETTLEMENTS);
        plugin.getLogger().info("[SettlementDataObject::initialize] Initialized data table 'settlements'!");
        database.execute(SQL_INIT_SETTLEMENT_ROLES);
        plugin.getLogger().info("[SettlementDataObject::initialize] Initialized data table 'settlement_roles'!");
        database.execute(SQL_INIT_SETTLEMENT_MEMBERS);
        plugin.getLogger().info("[SettlementDataObject::initialize] Initialized data table 'settlement_members'!");
        database.execute(SQL_INIT_SETTLEMENT_CHUNKS);
        plugin.getLogger().info("[SettlementDataObject::initialize] Initialized data table 'settlement_chunks'!");
    }
}

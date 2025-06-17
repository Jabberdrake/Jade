package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.realms.Area;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.jabberdrake.jade.utils.ItemUtils.parseKey;

public class AreaDataObject implements DatabaseObject<Area, Integer> {

    private Jade plugin;
    private Database database;

    public AreaDataObject(Jade plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        try {
            initialize();
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::initialize] Caught SQLException while initializing: ");
            e.printStackTrace();
        }
    }

    @Override
    public Area fetch(Integer id) {
        Area[] result = {null};
        String sql = "SELECT settlement_id, name, display_name, icon, holder_id, pos1, pos2 FROM areas WHERE id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, id);
            }, resultSet -> {
                if (resultSet.next()) {
                    int settlementID = resultSet.getInt("settlement_id");
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    String icon = resultSet.getString("icon");
                    String holderID = resultSet.getString("holder_id");
                    String pos1 = resultSet.getString("pos1");
                    String pos2 = resultSet.getString("pos2");

                    result[0] = new Area(id, name, displayName, RealmManager.getSettlement(settlementID), parseKey(icon), pos1, pos2, UUID.fromString(holderID));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::fetch] Caught SQLException while fetching area object: ");
            e.printStackTrace();
        }

        // If there is no matching record in the main nation table, quit;
        // otherwise, fetch remaining attributes from auxiliary tables
        if (result[0] == null) {
            return null;
        }

        result[0].setMemberList(fetchMemberList(result[0]));

        return result[0];
    }

    public List<UUID> fetchMemberList(Area area) {
        List<UUID> memberList = new ArrayList<>();
        String sql = "SELECT player_id FROM area_members WHERE area_id = ?;";
        try {
            database.query(sql, stmt -> {
                stmt.setInt(1, area.getId());
            }, resultSet -> {
                while (resultSet.next()) {
                    String memberID = resultSet.getString("player_id");
                    memberList.add(UUID.fromString(memberID));
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::fetchMemberList] Caught SQLException while fetching member UUIDs for area " + area.getName() + ": ");
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public List<Area> fetchAll() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT id, settlement_id, name, display_name, icon, holder_id, pos1, pos2 FROM areas;";
        try {
            database.query(sql, resultSet -> {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int settlementID = resultSet.getInt("settlement_id");
                    String name = resultSet.getString("name");
                    String displayName = resultSet.getString("display_name");
                    String icon = resultSet.getString("icon");
                    String holderID = resultSet.getString("holder_id");
                    String pos1 = resultSet.getString("pos1");
                    String pos2 = resultSet.getString("pos2");

                    Area area = new Area(id, name, displayName, RealmManager.getSettlement(settlementID), parseKey(icon), pos1, pos2, UUID.fromString(holderID));
                    area.setMemberList(fetchMemberList(area));

                    areas.add(area);
                }
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::fetchAll] Caught SQLException while fetching all registered area objects!");
            e.printStackTrace();
        }
        return areas;
    }

    @Override
    public void save(Area area) {
        String sql = "UPDATE areas SET name = ?, display_name = ?, icon = ?, holder_id = ?, pos1 = ?, pos2 = ? WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setString(1, area.getName());
                stmt.setString(2, area.getDisplayName());
                stmt.setString(3, area.getIconAsString());
                stmt.setString(4, area.getHolderUniqueID().toString());
                stmt.setString(5, area.serializePos1());
                stmt.setString(6, area.serializePos2());
                stmt.setInt(7, area.getId());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::save] Caught SQLException while saving data object for area " + area.getName() + ": ");
            e.printStackTrace();
        }
    }

    @Override
    public Integer create(Area area) {
        final Integer[] id = {null};
        String sql = "INSERT INTO areas (settlement_id, name, display_name, icon, holder_id, pos1, pos2) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try {
            id[0] = Math.toIntExact(database.create(sql, stmt -> {
                stmt.setInt(1, area.getSettlement().getId());
                stmt.setString(2, area.getName());
                stmt.setString(3, area.getDisplayName());
                stmt.setString(4, area.getIconAsString());
                stmt.setString(5, area.getHolderUniqueID().toString());
                stmt.setString(6, area.serializePos1());
                stmt.setString(7, area.serializePos2());
            }));
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::create] Caught SQLException while creating area object for " + area.getName() + ": ");
            e.printStackTrace();
        }

        return id[0];
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM areas WHERE id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, id);
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::delete] Caught SQLException while deleting area object for ID=" + id + ": ");
            e.printStackTrace();
        }
    }

    public void addMemberToArea(UUID memberID, Area area) {
        String sql = "INSERT INTO area_members (area_id, player_id) VALUES (?, ?);";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, area.getId());
                stmt.setString(2, memberID.toString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::addMemberToArea] Caught SQLException while adding player " + Bukkit.getOfflinePlayer(memberID).getName() + " to area " + area.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void removeMemberFromArea(UUID memberID, Area area) {
        String sql = "DELETE FROM area_members WHERE area_id = ? AND player_id = ?;";
        try {
            database.execute(sql, stmt -> {
                stmt.setInt(1, area.getId());
                stmt.setString(2, memberID.toString());
            });
        } catch (SQLException e) {
            plugin.getLogger().warning("[AreaDataObject::removeMemberFromArea] Caught SQLException while removing player " + Bukkit.getOfflinePlayer(memberID).getName() + " from area " + area.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        String SQL_INIT_AREAS =
                """
                CREATE TABLE IF NOT EXISTS areas (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  settlement_id INETEGER NOT NULL,
                  name TEXT NOT NULL,
                  display_name TEXT NOT NULL,
                  icon TEXT DEFAULT "minecraft:oak_fence_gate" NOT NULL,
                  holder_id TEXT(36) NOT NULL,
                  pos1 TEXT NOT NULL,
                  pos2 TEXT NOT NULL,
                    CONSTRAINT areas_settlement_id_fk
                      FOREIGN KEY (settlement_id) REFERENCES settlements (id)
                      ON DELETE CASCADE
                );
                """;

        String SQL_INIT_AREAS_MEMBERS =
                """
                CREATE TABLE IF NOT EXISTS area_members (
                    area_id INTEGER NOT NULL,
                    player_id TEXT(36) NOT NULL,
                      CONSTRAINT area_members_pk
                        PRIMARY KEY (area_id, player_id),
                      CONSTRAINT areamembers_area_id_fk
                        FOREIGN KEY (area_id) REFERENCES areas (id)
                        ON DELETE CASCADE
                );
                """;

        database.execute(SQL_INIT_AREAS);
        plugin.getLogger().info("[AreaDataObject::initialize] Initialized data table 'areas'!");
        database.execute(SQL_INIT_AREAS_MEMBERS);
        plugin.getLogger().info("[AreaDataObject::initialize] Initialized data table 'area_members'!");
    }
}

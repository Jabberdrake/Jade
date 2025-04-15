package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.realms.Settlement;

import java.sql.SQLException;
import java.util.List;

public class SettlementDataObject implements DatabaseObject<Settlement, Integer> {

    private Jade plugin;
    private Database database;

    public SettlementDataObject(Jade plugin, Database database) {
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
        return null;
    }

    @Override
    public List<Settlement> fetchAll() {
        return List.of();
    }

    @Override
    public void save(Settlement settlement) {

    }

    @Override
    public Integer create(Settlement settlement) {
        return 0;
    }

    @Override
    public void delete(Integer id) {

    }

    public void initialize() throws SQLException {
        String SQL_INIT_SETTLEMENTS =
                """
                CREATE TABLE IF NOT EXISTS settlements (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  display_name TEXT NOT NULL,
                  description TEXT NOT NULL,
                  map_color TEXT(6) DEFAULT 'FFAA00' NOT NULL,
                  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
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
                  display_name TEXT NOT NULL,
                  authority INTEGER DEFAULT 0 NOT NULL CHECK (authority BETWEEN 0 AND 8),
                  type TEXT DEFAULT 'NONE' NOT NULL,
                  can_invite BOOLEAN DEFAULT FALSE NOT NULL,
                  can_kick BOOLEAN DEFAULT FALSE NOT NULL,
                  can_claim BOOLEAN DEFAULT FALSE NOT NULL,
                  can_unclaim BOOLEAN DEFAULT FALSE NOT NULL,
                  can_promote BOOLEAN DEFAULT FALSE NOT NULL,
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
                      ON DELETE CASCADE
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

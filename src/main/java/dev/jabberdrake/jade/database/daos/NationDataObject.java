package dev.jabberdrake.jade.database.daos;

import dev.jabberdrake.jade.Jade;
import dev.jabberdrake.jade.database.Database;
import dev.jabberdrake.jade.database.DatabaseObject;
import dev.jabberdrake.jade.realms.Nation;

import java.sql.SQLException;
import java.util.List;

public class NationDataObject implements DatabaseObject<Nation, Integer> {

    private Jade plugin;
    private Database database;

    public NationDataObject(Jade plugin, Database database) {
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
        return null;
    }

    @Override
    public List<Nation> fetchAll() {
        return List.of();
    }

    @Override
    public void save(Nation nation) {

    }

    @Override
    public Integer create(Nation nation) {
        return 0;
    }

    @Override
    public void delete(Integer id) {

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
                  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
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

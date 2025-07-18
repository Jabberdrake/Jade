package dev.jabberdrake.jade.database;

import dev.jabberdrake.jade.Jade;

import java.io.File;
import java.sql.*;

public class Database {

    // Massive thank you to orang3i for this code!
    private Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    @FunctionalInterface
    public interface StatementCallback {
        void accept(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface QueryCallback {
        void accept(ResultSet stmt) throws SQLException;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void execute(String sql, StatementCallback callback) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            callback.accept(stmt);
            stmt.executeUpdate();
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void execute(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    public void backup(String absolutePath) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("BACKUP to " + absolutePath);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void query(String sql, StatementCallback paramSetter, QueryCallback resultHandler) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            paramSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                resultHandler.accept(rs);
            }
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void query(String sql, QueryCallback resultHandler) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            resultHandler.accept(rs);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public long create(String sql, StatementCallback callback) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            callback.accept(stmt);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getLong(1);
            } else {
                return -1;
            }
        }
    }

}

package dev.oklookat.sethome.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.oklookat.sethome.Main;

public class Database {
    private static volatile Database instance;
    public Connection conn;

    public Database() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        var connStr = "jdbc:sqlite:%s";
        connStr = String.format(connStr, Main.self.getDataFolder() + File.separator + "homes.db");
        conn = DriverManager.getConnection(connStr);

        // create tables
        try (final var stmt = conn.prepareStatement(PlayerDB.table)) {
            stmt.executeUpdate();
        }
        try (final var stmt = conn.prepareStatement(WorldDB.table)) {
            stmt.executeUpdate();
        }
        try (final var stmt = conn.prepareStatement(HomeDB.table)) {
            stmt.executeUpdate();
        }
    }

    public static Database get() throws SQLException, ClassNotFoundException {
        Database localInstance = instance;
        if (localInstance != null) {
            return localInstance;
        }
        synchronized (Database.class) {
            localInstance = instance;
            if (localInstance == null) {
                instance = localInstance = new Database();
            }
        }
        return localInstance;
    }

    public void destroy() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } finally {
            instance = null;
        }

    }
}

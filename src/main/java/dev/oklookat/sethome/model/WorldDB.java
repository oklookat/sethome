package dev.oklookat.sethome.model;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import dev.oklookat.sethome.err.WorldNotExists;

public class WorldDB {
    public static final String table = """
            CREATE TABLE IF NOT EXISTS world(
                id INTEGER NOT NULL PRIMARY KEY,
                name TXT NOT NULL,
                UNIQUE(id, name) ON CONFLICT ROLLBACK
            );
                """;
    public Integer id;
    public String name;

    public static WorldDB create(String name) throws SQLException, ClassNotFoundException {
        final var world = new WorldDB();
        world.name = name;

        final var query = "INSERT INTO world (name) VALUES (?) RETURNING id;";
        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            stmt.setString(1, world.name);
            final var rs = stmt.executeQuery();
            while (rs.next()) {
                world.id = rs.getInt("id");
                return world;
            }
        }

        throw new SQLException("ResultSet empty?");
    }

    public static WorldDB findByName(String name) throws WorldNotExists, SQLException, ClassNotFoundException {
        final var query = "SELECT * FROM world WHERE name = ? LIMIT 1;";

        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            final var world = new WorldDB();
            world.name = name;

            stmt.setString(1, world.name);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                world.id = rs.getInt("id");
                return world;
            }
        }

        throw new WorldNotExists(name);
    }

    public static WorldDB findById(Integer id) throws WorldNotExists, SQLException, ClassNotFoundException {
        final var query = "SELECT * FROM world WHERE id = ? LIMIT 1;";

        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            final var world = new WorldDB();
            world.id = id;

            stmt.setInt(1, world.id);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                world.name = rs.getString("name");
                if(Bukkit.getWorld(world.name) != null) {
                    return world;
                }
                break;
            }
        }

        throw new WorldNotExists("id " + id);
    }
}

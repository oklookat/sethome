package dev.oklookat.sethome.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import dev.oklookat.sethome.Utils;
import dev.oklookat.sethome.err.HomeAlreadyExists;
import dev.oklookat.sethome.err.HomeNotExists;
import dev.oklookat.sethome.err.PlayerNotExists;
import dev.oklookat.sethome.err.WorldNotExists;

public class HomeDB {
    public static final String table = """
            CREATE TABLE IF NOT EXISTS home(
                id INTEGER UNIQUE NOT NULL PRIMARY KEY,
                name TXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL,
                player_id INTEGER NOT NULL,
                world_id INTEGER NOT NULL,
                FOREIGN KEY(player_id) REFERENCES player(player_id) ON DELETE CASCADE,
                FOREIGN KEY(world_id) REFERENCES world(world_id)
            );
                """;
    public Integer id;
    public String name;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public Integer playerId;
    public Integer worldId;

    /**
     * Add new player home.
     * 
     * @throws Exception
     * @throws HomeAlreadyExists
     */
    public static HomeDB setHome(Player player, String homeName) throws HomeAlreadyExists, SQLException, ClassNotFoundException {
        homeName = Utils.formatHomeName(homeName);

        var home = new HomeDB();
        home.name = homeName;

        // check is home exists
        var isHomeExistsBefore = false;
        try {
            home = find(player, home.name);
            // if home with default name 'home', need to replace
            if(!home.name.equalsIgnoreCase("home")) {
                throw new HomeAlreadyExists(home.name);
            }
            isHomeExistsBefore = true;
        } catch (HomeNotExists e) {
        }

        // world id
        final var worldName = player.getWorld().getName();
        WorldDB world;
        try {
            world = WorldDB.findByName(worldName);
        } catch (WorldNotExists e) {
            world = WorldDB.create(worldName);
        }
        home.worldId = world.id;

        // player id
        final var uuid = player.getUniqueId().toString();
        PlayerDB playerDb;
        try {
            playerDb = PlayerDB.find(uuid);
        } catch(PlayerNotExists e) {
            playerDb = PlayerDB.create(uuid);
        }
        home.playerId = playerDb.id;

        // location
        final var loc = player.getLocation();
        home.x = loc.getX();
        home.y = loc.getY();
        home.z = loc.getZ();
        home.yaw = loc.getYaw();
        home.pitch = loc.getPitch();

        // add
        if(!isHomeExistsBefore) {
            insert(home);
        } else {
            update(home);
        }
        return home;
    }

    /**
     * Get player home.
     * 
     * @return Player home
     */
    public static HomeDB find(Player player, String homeName) throws HomeNotExists, SQLException, ClassNotFoundException {
        homeName = Utils.formatHomeName(homeName);
        
        PlayerDB playerDb;
        final var uuid = player.getUniqueId().toString();
        try {
            playerDb = PlayerDB.find(uuid);
        } catch (PlayerNotExists e) {
            playerDb = PlayerDB.create(uuid);
        }

        final var query = "SELECT * FROM home WHERE player_id = ? AND name = ? LIMIT 1;";
        try (var stmt = Database.get().conn.prepareStatement(query)) {
            stmt.setInt(1, playerDb.id);
            stmt.setString(2, homeName);
            final var rs = stmt.executeQuery();
            final var home = new HomeDB();
            while (rs.next()) {
                fillResultSet(home, rs);
                return home;
            }
        }

        throw new HomeNotExists(homeName);
    }

    /** Update home in DB */
    private static void update(HomeDB instance) throws SQLException, ClassNotFoundException {
        final var query = "UPDATE home SET name = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world_id = ? WHERE player_id = ?;";
        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            fillStatement(instance, stmt);
            stmt.executeUpdate();
        }
    }

    public static void deleteById(HomeDB instance) throws SQLException, ClassNotFoundException {
        final var query = "DELETE FROM home WHERE id = ?;";
        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            stmt.setInt(1, instance.id);
            stmt.executeUpdate();
        }
    }

    /** New home in DB */
    private static void insert(HomeDB instance) throws SQLException, ClassNotFoundException {
        final var query = """
                    INSERT INTO home (name, x, y, z, yaw, pitch, world_id, player_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;
                """;
        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            fillStatement(instance, stmt);
            final var rs = stmt.executeQuery();
            while (rs.next()) {
                instance.id = rs.getInt("id");
                return;
            }
        }

        throw new SQLException("ResultSet empty?");
    }

    /** Fill data for DB ops. */
    private static void fillStatement(HomeDB instance, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, instance.name);
        stmt.setDouble(2, instance.x);
        stmt.setDouble(3, instance.y);
        stmt.setDouble(4, instance.z);
        stmt.setFloat(5, instance.yaw);
        stmt.setFloat(6, instance.pitch);
        stmt.setInt(7, instance.worldId);
        stmt.setInt(8, instance.playerId);
    }

    /** Fill instance by DB result. */
    private static void fillResultSet(HomeDB instance, ResultSet rs) throws SQLException {
        instance.id = rs.getInt("id");
        instance.name = rs.getString("name");
        instance.x = rs.getDouble("x");
        instance.y = rs.getDouble("y");
        instance.z = rs.getDouble("z");
        instance.yaw = rs.getFloat("yaw");
        instance.pitch = rs.getFloat("pitch");
        instance.worldId = rs.getInt("world_id");
        instance.playerId = rs.getInt("player_id");
    }
}

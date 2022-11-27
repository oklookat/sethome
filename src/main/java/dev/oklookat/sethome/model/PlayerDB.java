package dev.oklookat.sethome.model;

import java.sql.SQLException;

import dev.oklookat.sethome.err.PlayerNotExists;

// player in DB
public class PlayerDB {
    public static final String table = """
            CREATE TABLE IF NOT EXISTS player(
                id INTEGER NOT NULL PRIMARY KEY,
                uuid TEXT NOT NULL,
                UNIQUE(id, uuid) ON CONFLICT ROLLBACK
            );
            """;
    public Integer id;
    public String uuid;

    /** Find player by UUID. */
    public static PlayerDB find(String uuid) throws SQLException, ClassNotFoundException, PlayerNotExists {
        final var player = new PlayerDB();
        player.uuid = uuid;

        final var query = "SELECT * FROM player WHERE uuid = ? LIMIT 1;";
        try (final var stmt = Database.get().conn.prepareStatement(query)) {
            stmt.setString(1, player.uuid);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                player.id = rs.getInt("id");
                return player;
            }
        }

        throw new PlayerNotExists(player.uuid);
    }

    /** Create player.
     * @return Player || NULL if error
     */
    public static PlayerDB create(String uuid) throws SQLException, ClassNotFoundException {
        final var player = new PlayerDB();
        player.uuid = uuid;

        var query = "INSERT INTO player (uuid) VALUES (?) RETURNING id;";
        try (var stmt = Database.get().conn.prepareStatement(query)) {
            stmt.setString(1, uuid);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                player.id = rs.getInt("id");
                return player;
            }
        }

        throw new SQLException("ResultSet empty?");
    }

}

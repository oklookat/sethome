package dev.oklookat.sethome.err;

/** Player not exists in DB. */
public class PlayerNotExists extends Exception {
    public PlayerNotExists(String uuid) {
        super("Player with uuid '" + uuid + "' not exists.");
    }
}

package dev.oklookat.sethome.err;

/** The house exists, but the world does not */
public class WorldNotExists extends Exception {
    public WorldNotExists(String worldName) {
        super("World '" + worldName + "' not exists.");
    }
}

package dev.oklookat.sethome.err;

/** Home already exists */
public class HomeAlreadyExists extends Exception {
    public HomeAlreadyExists(String homeName) {
        super("Home '" + homeName + "' already exists.");
    }
}

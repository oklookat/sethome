package dev.oklookat.sethome.err;

/** Home not exists */
public class HomeNotExists extends Exception {
    public HomeNotExists(String homeName) {
        super("Home '" + homeName + "' not exists.");
    }
}

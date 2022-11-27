package dev.oklookat.sethome;

public class Directory {

    /** create plugin dir if not exists */
    public static void init() throws Exception {
        var dir = Main.self.getDataFolder();
        if(!dir.exists() && !dir.mkdir()) {
            throw new Exception("Failed to create plugin directory.");
        }
    }

}

package dev.oklookat.sethome;

public class Utils {
    public static String formatHomeName(String homeName) {
        if(homeName == null || homeName.length() < 1) {
            homeName = "home";
        }
        homeName = homeName.toLowerCase();
        return homeName;
    }

    public static boolean validateHomeName(String homeName) {
        if(homeName == null) {
            return true;
        }
        if(homeName.length() > 8) {
            return false;
        }
        return true;
    }
}

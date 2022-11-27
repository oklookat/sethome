package dev.oklookat.sethome;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import dev.oklookat.sethome.cmd.DelHome;
import dev.oklookat.sethome.cmd.Home;
import dev.oklookat.sethome.cmd.SetHome;
import dev.oklookat.sethome.model.Database;

public class Main extends JavaPlugin {
    // TODO: add validators, config file with regex, messages, homes list, permissions
    public static Main self;

    private SetHome set;
    private DelHome del;
    private Home home;

    @Override
    public void onEnable() {
        self = this;
        try {
            Directory.init();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return;
        }
        set = new SetHome();
        del = new DelHome();
        home = new Home();
    }

    @Override
    public void onDisable() {
        try {
            Database.get().destroy();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        } finally {
            self = null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        return home.serve(sender, command, args) || set.serve(sender, command, args)
                || del.serve(sender, command, args);
    }

}

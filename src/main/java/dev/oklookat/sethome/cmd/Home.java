package dev.oklookat.sethome.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.oklookat.sethome.Main;
import dev.oklookat.sethome.err.HomeNotExists;
import dev.oklookat.sethome.model.HomeDB;
import dev.oklookat.sethome.model.WorldDB;
import net.kyori.adventure.text.Component;

public class Home {

    public Home() {
        Main.self.getCommand("home").setExecutor(Main.self);
    }

    public boolean serve(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        var player = (Player) sender;
        if (!command.getName().equalsIgnoreCase("home")) {
            return false;
        }

        var homeName = "";
        if (args.length > 0 && args[0].trim().length() > 0) {
            homeName = args[0];
        }

        try {
            final var home = HomeDB.find(player, homeName);
            final var world = WorldDB.findById(home.worldId);
            final var loc = new Location(Bukkit.getWorld(world.name), home.x, home.y, home.z, home.yaw, home.pitch);
            player.teleport(loc);
        } catch (HomeNotExists e) {
            player.sendMessage(Component.text("Home not exists"));
        } catch (Exception e) {
            player.sendMessage(Component.text("Server error :("));
            e.printStackTrace();
        }

        return true;
    }

}

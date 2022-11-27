package dev.oklookat.sethome.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.oklookat.sethome.Main;
import dev.oklookat.sethome.err.HomeAlreadyExists;
import dev.oklookat.sethome.model.HomeDB;
import net.kyori.adventure.text.Component;

public class SetHome {

    public SetHome() {
        Main.self.getCommand("sethome").setExecutor(Main.self);
    }

    public boolean serve(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        var player = (Player) sender;
        if (!command.getName().equalsIgnoreCase("sethome")) {
            return false;
        }

        var homeName = "";
        if (args.length > 0 && args[0].trim().length() > 0) {
            homeName = args[0];
        }

        try {
            HomeDB.setHome(player, homeName);
        } catch (HomeAlreadyExists e) {
            player.sendMessage(Component.text("Home already exists."));
        } catch (Exception e) {
            player.sendMessage(Component.text(e.getMessage()));
            e.printStackTrace();
        }

        return true;
    }

}

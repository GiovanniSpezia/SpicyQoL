package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.HomeManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public SetHomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission(Perms.SETHOME)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        String name = (args.length >= 1) ? args[0] : "home";
        homeManager.setHome(p, name);
        return true;
    }
}
package it.giospezia.spicyqol.sit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SitCommand implements CommandExecutor {

    private final SitManager sitManager;

    public SitCommand(SitManager sitManager) {
        this.sitManager = sitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        sitManager.toggleSit(p);
        return true;
    }
}
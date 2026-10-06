package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.BackManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    private final BackManager backManager;

    public BackCommand(BackManager backManager) {
        this.backManager = backManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission(Perms.BACK)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        backManager.back(p);
        return true;
    }
}
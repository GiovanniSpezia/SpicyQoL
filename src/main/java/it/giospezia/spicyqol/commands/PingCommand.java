package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }

        if (!p.hasPermission(Perms.PING)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        int ping = p.getPing();
        Chat.send(p, "&7Il tuo ping: &e" + ping + "ms");
        return true;
    }
}
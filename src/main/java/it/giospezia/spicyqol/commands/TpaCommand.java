package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.SpicyQoL;
import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpaCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }
        Player from = (Player) sender;

        if (!from.hasPermission(Perms.TPA)) {
            Chat.send(from, "&cNon hai permesso.");
            return true;
        }

        if (args.length < 1) {
            Chat.send(from, "&cUso: &e/tpa <player>");
            return true;
        }

        Player to = Bukkit.getPlayerExact(args[0]);
        if (to == null || !to.isOnline()) {
            Chat.send(from, "&cPlayer non trovato.");
            return true;
        }

        if (to.getUniqueId().equals(from.getUniqueId())) {
            Chat.send(from, "&cNon puoi chiedere la TPA a te stesso.");
            return true;
        }

        int timeout = SpicyQoL.get().getConfig().getInt("tpa.timeout-seconds", 60);
        tpaManager.sendRequest(from, to, timeout);
        return true;
    }
}
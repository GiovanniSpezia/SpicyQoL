package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpAcceptCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }
        Player target = (Player) sender;

        if (!target.hasPermission(Perms.TPA)) {
            Chat.send(target, "&cNon hai permesso.");
            return true;
        }

        tpaManager.accept(target);
        return true;
    }
}
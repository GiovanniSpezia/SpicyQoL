package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.gui.TpaRequestGui;
import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpMenuCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpMenuCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }

        if (tpaManager.getRequestFor(p) == null) {
            Chat.send(p, "&cNon hai richieste di teleport.");
            return true;
        }

        new TpaRequestGui(p, tpaManager).open();
        return true;
    }
}
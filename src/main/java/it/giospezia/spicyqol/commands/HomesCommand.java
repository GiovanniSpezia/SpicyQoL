package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.gui.HomesGui;
import it.giospezia.spicyqol.managers.HomeManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public HomesCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }

        if (!p.hasPermission(Perms.HOMES)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        new HomesGui(p, homeManager).open();
        return true;
    }
}
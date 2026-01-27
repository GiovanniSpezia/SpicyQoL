package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QolCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // (se in futuro vuoi subcomandi, li gestisci qui)
        Chat.send(sender, "&e&lSpicyQoL &7- comandi disponibili");
        Chat.send(sender, "&8• &e/spawn &7- vai allo spawn");
        Chat.send(sender, "&8• &e/setspawn &7- imposta lo spawn (op)");
        Chat.send(sender, "&8• &e/home [nome] &7- vai a una home");
        Chat.send(sender, "&8• &e/sethome [nome] &7- imposta una home");
        Chat.send(sender, "&8• &e/delhome <nome> &7- elimina una home");
        Chat.send(sender, "&8• &e/homes &7- GUI delle home");
        Chat.send(sender, "&8• &e/tpa <player> &7- richiesta di teleport");
        Chat.send(sender, "&8• &e/tpaccept &7- accetta richiesta");
        Chat.send(sender, "&8• &e/tpdeny &7- rifiuta richiesta");
        Chat.send(sender, "&8• &e/tpamenu &7- apre GUI TPA (se hai richiesta)");
        Chat.send(sender, "&8• &e/back &7- torna indietro");
        Chat.send(sender, "&8• &e/rtp &7- teleport random");
        Chat.send(sender, "&8• &e/ping &7- mostra ping");

        return true;
    }
}
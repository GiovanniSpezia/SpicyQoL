package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QolCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Chat.send(sender, "&8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Chat.send(sender, "&e&lSpicyQoL &7- comandi disponibili");
        Chat.send(sender, "&8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Chat.send(sender, "&8• &e/qolmenu &7- apre il menu giocatore");
        Chat.send(sender, "&8• &e/day &7/ &e/night &7- proponi un voto per l'orario");
        Chat.send(sender, "&8• &e/timevote si|no &7- vota una proposta attiva");

        Chat.send(sender, "&6📍 Teleport &7(viaggi)");
        Chat.send(sender, "&8• &e/spawn &7- vai allo spawn");
        Chat.send(sender, "&8• &e/setspawn &7- imposta lo spawn &8(op)");
        Chat.send(sender, "&8• &e/back &7- torna all'ultima posizione");
        Chat.send(sender, "&8• &e/rtp &7- menu random teleport (cooldown 20 min)");

        Chat.send(sender, "");
        Chat.send(sender, "&6🏠 Home");
        Chat.send(sender, "&8• &e/home <nome> &7- vai a una home");
        Chat.send(sender, "&8• &e/sethome <nome> &7- imposta una home");
        Chat.send(sender, "&8• &e/delhome <nome> &7- elimina una home");
        Chat.send(sender, "&8• &e/homes &7- apre la GUI delle home");

        Chat.send(sender, "");
        Chat.send(sender, "&6📩 Teleport tra player");
        Chat.send(sender, "&8• &e/tpa <player> &7- richiesta di teleport");
        Chat.send(sender, "&8• &e/tpaccept &7- accetta richiesta");
        Chat.send(sender, "&8• &e/tpdeny &7- rifiuta richiesta");
        Chat.send(sender, "&8• &e/tpamenu &7- apre GUI accetta/rifiuta");

        Chat.send(sender, "");
        Chat.send(sender, "&6🪑 Azioni QoL");
        Chat.send(sender, "&8• &e/sit &7- siediti");

        Chat.send(sender, "");
        Chat.send(sender, "&6ℹ️ Altro");
        Chat.send(sender, "&8• &e/ping &7- mostra il tuo ping");

        Chat.send(sender, "&8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return true;
    }
}

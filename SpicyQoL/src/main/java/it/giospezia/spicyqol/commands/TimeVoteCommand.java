package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.TimeVoteManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeVoteCommand implements CommandExecutor {
    private final TimeVoteManager timeVoteManager;

    public TimeVoteCommand(TimeVoteManager timeVoteManager) {
        this.timeVoteManager = timeVoteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.send(sender, "&cSolo i giocatori possono votare.");
            return true;
        }
        if (args.length != 1) {
            Chat.send(player, "&7Usa &a/timevote si &7oppure &c/timevote no&7.");
            return true;
        }
        String answer = args[0].toLowerCase(java.util.Locale.ROOT);
        if (answer.equals("si") || answer.equals("sì") || answer.equals("yes") || answer.equals("accetta")) {
            timeVoteManager.castVote(player, true);
        } else if (answer.equals("no") || answer.equals("rifiuta")) {
            timeVoteManager.castVote(player, false);
        } else {
            Chat.send(player, "&7Usa &a/timevote si &7oppure &c/timevote no&7.");
        }
        return true;
    }
}

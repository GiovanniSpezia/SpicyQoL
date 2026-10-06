package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.TimeVoteManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {
    private final TimeVoteManager timeVoteManager;
    private final boolean day;

    public TimeCommand(TimeVoteManager timeVoteManager, boolean day) {
        this.timeVoteManager = timeVoteManager;
        this.day = day;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.send(sender, "&cSolo i giocatori possono chiedere un cambio dell'orario.");
            return true;
        }
        if (!player.hasPermission("spicyqol.timevote")) {
            Chat.send(player, "&cNon hai il permesso per avviare una votazione.");
            return true;
        }
        timeVoteManager.startVote(player, day);
        return true;
    }
}

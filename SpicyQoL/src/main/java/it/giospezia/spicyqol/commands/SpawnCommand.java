package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.managers.SpawnManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SpawnCommand(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission(Perms.SPAWN)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        spawnManager.teleportToSpawn(p);
        return true;
    }
}
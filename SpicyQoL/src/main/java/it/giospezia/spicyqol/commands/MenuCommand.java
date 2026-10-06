package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.gui.MenuGui;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {
    private final RtpCommand rtpCommand;

    public MenuCommand(RtpCommand rtpCommand) { this.rtpCommand = rtpCommand; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.send(sender, "&cSolo i giocatori possono aprire il menu.");
            return true;
        }
        new MenuGui(player, MenuGui.Type.MAIN, rtpCommand).open();
        return true;
    }
}

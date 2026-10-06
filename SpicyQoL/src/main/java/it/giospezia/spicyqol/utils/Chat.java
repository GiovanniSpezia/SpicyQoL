package it.giospezia.spicyqol.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Chat {
    private static String prefix = "&6&lSpicy&fQoL &8» &f";

    public static void init(JavaPlugin plugin) {
        prefix = plugin.getConfig().getString("prefix", prefix);
    }

    public static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(CommandSender sender, String msg) {
        sender.sendMessage(c(prefix + msg));
    }
}
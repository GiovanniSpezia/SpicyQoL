package it.giospezia.spicyqol.managers;

import it.giospezia.spicyqol.utils.Chat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    public static class Request {
        public final UUID from;
        public final UUID to;
        public final long expireAt;

        public Request(UUID from, UUID to, long expireAt) {
            this.from = from;
            this.to = to;
            this.expireAt = expireAt;
        }
    }

    private final JavaPlugin plugin;
    private final Map<UUID, Request> requestsByTarget = new HashMap<>();

    public TpaManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player from, Player to, int timeoutSeconds) {
        long expireAt = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        requestsByTarget.put(to.getUniqueId(), new Request(from.getUniqueId(), to.getUniqueId(), expireAt));

        Chat.send(from, "&aRichiesta inviata a &e" + to.getName() + "&a!");
        Chat.send(to, "&e" + from.getName() + " &7vuole teletrasportarsi da te.");

        // Chat buttons (Spigot - Bungee components)
        TextComponent base = new TextComponent("Clicca: ");
        base.setColor(ChatColor.GRAY);

        TextComponent accept = new TextComponent("[ACCETTA]");
        accept.setColor(ChatColor.GREEN);
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        accept.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Accetta la richiesta").color(ChatColor.GREEN).create()
        ));

        TextComponent deny = new TextComponent("[RIFIUTA]");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        deny.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Rifiuta la richiesta").color(ChatColor.RED).create()
        ));

        TextComponent gui = new TextComponent("[APRI GUI]");
        gui.setColor(ChatColor.YELLOW);
        gui.setBold(true);
        gui.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpamenu"));
        gui.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Apri il menu con ACCETTA/RIFIUTA").color(ChatColor.YELLOW).create()
        ));

        TextComponent space = new TextComponent(" ");

        base.addExtra(space);
        base.addExtra(accept);
        base.addExtra(space);
        base.addExtra(deny);
        base.addExtra(space);
        base.addExtra(gui);

        to.spigot().sendMessage(base);

        // timeout request
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Request r = requestsByTarget.get(to.getUniqueId());
            if (r != null && r.from.equals(from.getUniqueId()) && System.currentTimeMillis() >= r.expireAt) {
                requestsByTarget.remove(to.getUniqueId());
                Player f = Bukkit.getPlayer(r.from);
                Player t = Bukkit.getPlayer(r.to);
                if (f != null) Chat.send(f, "&cLa richiesta a &e" + (t != null ? t.getName() : "quel player") + " &cè scaduta.");
                if (t != null) Chat.send(t, "&7La richiesta di &e" + (f != null ? f.getName() : "quel player") + " &7è scaduta.");
            }
        }, timeoutSeconds * 20L);
    }

    public Request getRequestFor(Player target) {
        Request r = requestsByTarget.get(target.getUniqueId());
        if (r == null) return null;

        if (System.currentTimeMillis() >= r.expireAt) {
            requestsByTarget.remove(target.getUniqueId());
            return null;
        }
        return r;
    }

    public void accept(Player target) {
        Request r = getRequestFor(target);
        if (r == null) {
            Chat.send(target, "&cNon hai richieste di teleport.");
            return;
        }

        Player from = Bukkit.getPlayer(r.from);
        if (from == null) {
            requestsByTarget.remove(target.getUniqueId());
            Chat.send(target, "&cIl player non è online.");
            return;
        }

        requestsByTarget.remove(target.getUniqueId());
        from.teleport(target.getLocation());
        Chat.send(from, "&aRichiesta accettata! Teletrasporto...");
        Chat.send(target, "&aHai accettato la richiesta.");
    }

    public void deny(Player target) {
        Request r = getRequestFor(target);
        if (r == null) {
            Chat.send(target, "&cNon hai richieste di teleport.");
            return;
        }

        Player from = Bukkit.getPlayer(r.from);
        requestsByTarget.remove(target.getUniqueId());

        if (from != null) Chat.send(from, "&cRichiesta rifiutata da &e" + target.getName() + "&c.");
        Chat.send(target, "&aRichiesta rifiutata.");
    }
}
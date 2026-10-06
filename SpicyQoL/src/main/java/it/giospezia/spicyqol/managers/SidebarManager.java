package it.giospezia.spicyqol.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SidebarManager implements Listener {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ITALIAN);
    private final JavaPlugin plugin;
    private final Map<UUID, Scoreboard> previousBoards = new HashMap<>();
    private final Map<UUID, Scoreboard> pluginBoards = new HashMap<>();

    public SidebarManager(JavaPlugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) attach(player);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateAll, 20L, 40L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> attach(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        restore(event.getPlayer());
    }

    private void attach(Player player) {
        Scoreboard original = player.getScoreboard();
        previousBoards.put(player.getUniqueId(), original);

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        copyTeams(original, board);
        Objective objective = board.registerNewObjective("spicy_sidebar", "dummy", color("&6&lSPICY &fQOL"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        pluginBoards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
        update(player);
    }

    private void copyTeams(Scoreboard source, Scoreboard target) {
        for (Team sourceTeam : source.getTeams()) {
            Team team = target.registerNewTeam(sourceTeam.getName());
            team.setDisplayName(sourceTeam.getDisplayName());
            team.setPrefix(sourceTeam.getPrefix());
            team.setSuffix(sourceTeam.getSuffix());
            team.setColor(sourceTeam.getColor());
            for (String entry : sourceTeam.getEntries()) team.addEntry(entry);
        }
    }

    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) update(player);
    }

    private void update(Player player) {
        Scoreboard board = pluginBoards.get(player.getUniqueId());
        if (board == null || player.getScoreboard() != board) return;
        Objective objective = board.getObjective("spicy_sidebar");
        if (objective == null) return;

        Team roleTeam = previousBoards.get(player.getUniqueId()) == null ? null
                : previousBoards.get(player.getUniqueId()).getEntryTeam(player.getName());
        String role = roleTeam == null ? "Utente" : org.bukkit.ChatColor.stripColor(
                color(roleTeam.getPrefix() + roleTeam.getDisplayName() + roleTeam.getSuffix()));
        if (role == null || role.trim().isEmpty()) role = roleTeam == null ? "Utente" : roleTeam.getName();
        role = trim(role.trim(), 18);

        String world = trim(player.getWorld().getName(), 22);
        String[] lines = {
                color("&7" + LocalDate.now().format(DATE)),
                color("&f" + trim(player.getName(), 22)),
                color("&7Ruolo: &f" + role),
                color("&6&l◆ &ePROFILO"),
                color("&7Mondo: &f" + world),
                color("&7Pos: &f" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ()),
                color("&7Livello: &f" + player.getLevel()),
                color("&7Online: &f" + Bukkit.getOnlinePlayers().size()),
                color("&7Ping: &f" + player.getPing() + " ms"),
                color("&e/spicyqol")
        };

        for (int i = 0; i < lines.length; i++) {
            String entry = ChatColor.values()[i].toString();
            board.resetScores(entry);
            Team lineTeam = board.getTeam("spq_line" + i);
            if (lineTeam == null) lineTeam = board.registerNewTeam("spq_line" + i);
            lineTeam.addEntry(entry);
            lineTeam.setPrefix(trim(lines[i], 64));
            objective.getScore(entry).setScore(lines.length - i);
        }
    }

    private String color(String text) { return ChatColor.translateAlternateColorCodes('&', text); }

    private String trim(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max);
    }

    private void restore(Player player) {
        Scoreboard original = previousBoards.remove(player.getUniqueId());
        pluginBoards.remove(player.getUniqueId());
        if (original != null && player.isOnline()) player.setScoreboard(original);
    }

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) restore(player);
    }
}

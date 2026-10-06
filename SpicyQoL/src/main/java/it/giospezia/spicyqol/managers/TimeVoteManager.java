package it.giospezia.spicyqol.managers;

import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class TimeVoteManager implements Listener {
    private final JavaPlugin plugin;
    private Vote activeVote;

    public TimeVoteManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasActiveVote() {
        return activeVote != null;
    }

    public void startVote(Player initiator, boolean day) {
        if (!initiator.hasPermission("spicyqol.timevote")) {
            Chat.send(initiator, "&cNon hai il permesso per avviare una votazione.");
            return;
        }
        if (activeVote != null) {
            Chat.send(initiator, "&cC'è già una votazione in corso. &7(" + activeVote.label() + " in " + activeVote.world.getName() + ")");
            return;
        }

        World world = initiator.getWorld();
        Set<UUID> eligible = new LinkedHashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) eligible.add(player.getUniqueId());

        if (eligible.size() <= 1) {
            applyTime(world, day);
            Chat.send(initiator, "&aHai impostato " + (day ? "il giorno" : "la notte") + " nel mondo &f" + world.getName() + "&a.");
            return;
        }

        activeVote = new Vote(initiator.getUniqueId(), world, day, eligible);
        activeVote.yesVotes.add(initiator.getUniqueId());
        int needed = votesNeeded(eligible.size());

        for (Player player : Bukkit.getOnlinePlayers()) {
            Chat.send(player, "&e" + initiator.getName() + " propone di impostare " + (day ? "il &fGIORNO" : "la &fNOTTE")
                    + " &enel mondo &f" + world.getName() + "&e.");
            Chat.send(player, "&7Vota con &a/timevote si &7o &c/timevote no&7. Servono &f" + needed + " voti favorevoli.");
        }

        long timeout = Math.max(10L, plugin.getConfig().getLong("time-vote.timeout-seconds", 45L));
        activeVote.expiryTask = Bukkit.getScheduler().runTaskLater(plugin,
                () -> finish(false, "&cVotazione scaduta senza abbastanza voti."), timeout * 20L);
    }

    public void castVote(Player player, boolean approve) {
        if (activeVote == null) {
            Chat.send(player, "&cNon c'è nessuna votazione in corso.");
            return;
        }
        if (!activeVote.eligible.contains(player.getUniqueId())) {
            Chat.send(player, "&cNon eri online quando è iniziata questa votazione.");
            return;
        }
        if (activeVote.yesVotes.contains(player.getUniqueId()) || activeVote.noVotes.contains(player.getUniqueId())) {
            Chat.send(player, "&eHai già votato in questa votazione.");
            return;
        }

        if (approve) activeVote.yesVotes.add(player.getUniqueId());
        else activeVote.noVotes.add(player.getUniqueId());
        Chat.send(player, approve ? "&aHai votato a favore." : "&cHai votato contro.");
        evaluateVote();
        if (activeVote != null) {
            broadcast("&7Voti: &a" + activeVote.yesVotes.size() + " sì &8/ &c" + activeVote.noVotes.size()
                    + " no &8— &7ne servono &f" + votesNeeded(activeVote.eligible.size()) + "&7.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (activeVote == null || !activeVote.eligible.remove(event.getPlayer().getUniqueId())) return;
        if (event.getPlayer().getUniqueId().equals(activeVote.initiator)) {
            finish(false, "&cVotazione annullata: chi l'ha avviata è uscito.");
            return;
        }
        activeVote.yesVotes.remove(event.getPlayer().getUniqueId());
        activeVote.noVotes.remove(event.getPlayer().getUniqueId());
        evaluateVote();
    }

    private void evaluateVote() {
        if (activeVote == null) return;
        int needed = votesNeeded(activeVote.eligible.size());
        if (activeVote.yesVotes.size() >= needed) {
            finish(true, "&aLa votazione è passata: " + activeVote.label() + " nel mondo &f" + activeVote.world.getName() + "&a.");
            return;
        }
        int undecided = activeVote.eligible.size() - activeVote.yesVotes.size() - activeVote.noVotes.size();
        if (activeVote.yesVotes.size() + undecided < needed) {
            finish(false, "&cLa votazione è stata respinta.");
        }
    }

    private int votesNeeded(int players) {
        return players / 2 + 1;
    }

    private void finish(boolean passed, String message) {
        if (activeVote == null) return;
        Vote vote = activeVote;
        activeVote = null;
        if (vote.expiryTask != null) vote.expiryTask.cancel();
        if (passed) applyTime(vote.world, vote.day);
        broadcast(message);
    }

    private void applyTime(World world, boolean day) {
        world.setTime(day ? 1000L : 13000L);
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) Chat.send(player, message);
    }

    public void shutdown() {
        if (activeVote != null && activeVote.expiryTask != null) activeVote.expiryTask.cancel();
        activeVote = null;
    }

    private static final class Vote {
        private final UUID initiator;
        private final World world;
        private final boolean day;
        private final Set<UUID> eligible;
        private final Set<UUID> yesVotes = new LinkedHashSet<>();
        private final Set<UUID> noVotes = new LinkedHashSet<>();
        private BukkitTask expiryTask;

        private Vote(UUID initiator, World world, boolean day, Set<UUID> eligible) {
            this.initiator = initiator;
            this.world = world;
            this.day = day;
            this.eligible = eligible;
        }

        private String label() { return day ? "giorno" : "notte"; }
    }
}

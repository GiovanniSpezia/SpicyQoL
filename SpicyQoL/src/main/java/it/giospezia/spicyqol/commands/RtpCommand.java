package it.giospezia.spicyqol.commands;

import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
import it.giospezia.spicyqol.managers.DataManager;
import it.giospezia.spicyqol.gui.MenuGui;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class RtpCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final DataManager data;
    private final Random random = new Random();

    public RtpCommand(JavaPlugin plugin, DataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            Chat.send(sender, "&cSolo i player possono usare questo comando.");
            return true;
        }

        if (!p.hasPermission(Perms.RTP)) {
            Chat.send(p, "&cNon hai permesso.");
            return true;
        }

        new MenuGui(p, MenuGui.Type.RTP, this).open();
        return true;
    }

    public void teleport(Player p) {
        if (!p.hasPermission(Perms.RTP)) {
            Chat.send(p, "&cNon hai il permesso per usare il teletrasporto casuale.");
            return;
        }

        long cooldownMillis = Math.max(0L, plugin.getConfig().getLong("rtp.cooldown-seconds", 1200L)) * 1000L;
        long remaining = cooldownMillis - (System.currentTimeMillis() - data.getRtpLastUse(p));
        if (!p.hasPermission("spicyqol.rtp.bypass") && remaining > 0L) {
            long seconds = (remaining + 999L) / 1000L;
            Chat.send(p, "&cPotrai usare di nuovo /rtp tra &e" + (seconds / 60) + "m " + (seconds % 60) + "s&c.");
            return;
        }

        String worldName = plugin.getConfig().getString("rtp.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Chat.send(p, "&cMondo RTP non trovato in config: &e" + worldName);
            return;
        }

        int radius = Math.max(1, plugin.getConfig().getInt("rtp.radius", 5000));
        int minDist = Math.max(0, Math.min(radius, plugin.getConfig().getInt("rtp.min-distance-from-0", 200)));
        int attempts = Math.max(1, plugin.getConfig().getInt("rtp.attempts", 15));
        boolean safe = plugin.getConfig().getBoolean("rtp.safe-check", true);

        Location loc = findRandomLocation(world, radius, minDist, attempts, safe);
        if (loc == null) {
            Chat.send(p, "&cImpossibile trovare una posizione sicura. Riprova.");
            return;
        }

        if (p.teleport(loc)) {
            data.setRtpLastUse(p, System.currentTimeMillis());
            Chat.send(p, "&aRTP completato! &7(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        } else {
            Chat.send(p, "&cTeletrasporto annullato.");
        }
    }

    private Location findRandomLocation(World world, int radius, int minDist, int attempts, boolean safe) {
        for (int i = 0; i < attempts; i++) {
            int x = randomCoord(radius, minDist);
            int z = randomCoord(radius, minDist);

            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (!safe) return loc;

            Block feet = loc.getBlock();
            Block head = loc.clone().add(0, 1, 0).getBlock();
            Block ground = loc.clone().add(0, -1, 0).getBlock();

            boolean airOk = feet.getType().isAir() && head.getType().isAir();
            boolean groundOk = ground.getType().isSolid()
                    && ground.getType() != Material.LAVA
                    && ground.getType() != Material.MAGMA_BLOCK;

            // evita spawn su acqua
            boolean notWater = ground.getType() != Material.WATER;

            if (airOk && groundOk && notWater) return loc;
        }
        return null;
    }

    private int randomCoord(int radius, int minDist) {
        if (minDist == 0) return random.nextInt(radius * 2 + 1) - radius;
        int val;
        do { val = random.nextInt(radius * 2 + 1) - radius; }
        while (Math.abs(val) < minDist && radius > minDist);
        return val;
    }
}

package it.giospezia.spicyqol.managers;

import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.LocUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnManager {

    private final JavaPlugin plugin;
    private final DataManager data;

    public SpawnManager(JavaPlugin plugin, DataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    public void setSpawn(Player p) {
        Location loc = p.getLocation();

        // sovrascrivi sezione spawn pulita
        data.cfg().set("spawn", null);
        LocUtil.writeLocation(data.cfg().createSection("spawn"), loc);

        data.markDirty(); // ✅ al posto di data.save()
        Chat.send(p, "&aSpawn impostato!");
    }

    public Location getSpawn() {
        return LocUtil.readLocation(data.cfg().getConfigurationSection("spawn"));
    }

    public void teleportToSpawn(Player p) {
        Location spawn = getSpawn();
        if (spawn == null) {
            Chat.send(p, "&cLo spawn non è stato impostato. Usa /setspawn (admin).");
            return;
        }

        p.teleport(spawn);

        if (plugin.getConfig().getBoolean("spawn.teleport-sound", true)) {
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            } catch (Exception ignored) {}
        }

        Chat.send(p, "&aTeletrasportato allo spawn!");
    }
}
package it.giospezia.spicyqol.managers;

import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.LocUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BackManager {

    private final JavaPlugin plugin;
    private final DataManager data;

    public BackManager(JavaPlugin plugin, DataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    private String path(Player p) {
        return "players." + p.getUniqueId() + ".back";
    }

    public void setBack(Player p, Location loc) {
        if (loc == null) return;
        LocUtil.writeLocation(data.cfg().createSection(path(p)), loc);
        data.markDirty(); // ✅ non salva subito
    }

    public Location getBack(Player p) {
        return LocUtil.readLocation(data.cfg().getConfigurationSection(path(p)));
    }

    public void back(Player p) {
        Location loc = getBack(p);
        if (loc == null) {
            Chat.send(p, "&cNon hai una posizione &e/back &csalvata.");
            return;
        }
        p.teleport(loc);
        Chat.send(p, "&aTeletrasportato alla tua ultima posizione!");
    }
}
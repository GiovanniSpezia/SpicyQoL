package it.giospezia.spicyqol.managers;

import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.LocUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class HomeManager {

    private final JavaPlugin plugin;
    private final DataManager data;

    public HomeManager(JavaPlugin plugin, DataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    private String base(Player p) {
        return "players." + p.getUniqueId() + ".homes";
    }

    public int getMaxHomes(Player p) {
        return plugin.getConfig().getInt("homes.max-homes", 3);
    }

    public Set<String> listHomes(Player p) {
        ConfigurationSection sec = data.cfg().getConfigurationSection(base(p));
        if (sec == null) return new LinkedHashSet<>();
        return sec.getKeys(false);
    }

    public boolean setHome(Player p, String name) {
        name = name.toLowerCase(Locale.ROOT);

        Set<String> homes = listHomes(p);
        if (!homes.contains(name) && homes.size() >= getMaxHomes(p)) {
            Chat.send(p, "&cHai raggiunto il limite di home (&e" + getMaxHomes(p) + "&c).");
            return false;
        }

        ConfigurationSection sec = data.cfg().getConfigurationSection(base(p));
        if (sec == null) sec = data.cfg().createSection(base(p));

        // se esiste già, sovrascriviamo pulito
        sec.set(name, null);

        ConfigurationSection homeSec = sec.createSection(name);
        LocUtil.writeLocation(homeSec, p.getLocation());

        data.markDirty(); // ✅ al posto di data.save()
        Chat.send(p, "&aHome &e" + name + " &aimpostata!");
        return true;
    }

    public boolean delHome(Player p, String name) {
        name = name.toLowerCase(Locale.ROOT);
        String path = base(p) + "." + name;

        if (!data.cfg().contains(path)) {
            Chat.send(p, "&cQuesta home non esiste.");
            return false;
        }

        data.cfg().set(path, null);
        data.markDirty(); // ✅ al posto di data.save()

        Chat.send(p, "&aHome &e" + name + " &aeliminata!");
        return true;
    }

    public Location getHome(Player p, String name) {
        name = name.toLowerCase(Locale.ROOT);
        return LocUtil.readLocation(data.cfg().getConfigurationSection(base(p) + "." + name));
    }

    public void teleportHome(Player p, String name) {
        Location loc = getHome(p, name);
        if (loc == null) {
            Chat.send(p, "&cHome non trovata. Usa &e/homes&c per vedere la lista.");
            return;
        }
        p.teleport(loc);
        Chat.send(p, "&aTeletrasportato alla home &e" + name + "&a!");
    }
}
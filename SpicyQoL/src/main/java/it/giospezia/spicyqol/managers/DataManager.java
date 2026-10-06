package it.giospezia.spicyqol.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.io.File;

public class DataManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration cfg;

    private boolean dirty = false;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        load();
    }

    private void load() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            if (!file.exists()) file.createNewFile();
            cfg = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Impossibile caricare data.yml");
            e.printStackTrace();
        }
    }

    public FileConfiguration cfg() {
        return cfg;
    }

    /** Chiamalo quando modifichi data.yml (set, createSection, remove ecc.) */
    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public long getRtpLastUse(Player player) {
        return cfg.getLong("players." + player.getUniqueId() + ".rtp-last-use", 0L);
    }

    public void setRtpLastUse(Player player, long timestamp) {
        cfg.set("players." + player.getUniqueId() + ".rtp-last-use", timestamp);
        markDirty();
        saveNow();
    }

    public void saveIfDirty() {
        if (!dirty) return;
        saveNow();
    }

    public void saveNow() {
        try {
            cfg.save(file);
            dirty = false;
        } catch (Exception e) {
            plugin.getLogger().severe("Impossibile salvare data.yml");
            e.printStackTrace();
        }
    }
}

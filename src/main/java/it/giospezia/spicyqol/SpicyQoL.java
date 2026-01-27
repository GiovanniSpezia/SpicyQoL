package it.giospezia.spicyqol;

import it.giospezia.spicyqol.commands.*;
import it.giospezia.spicyqol.listeners.DeathBackListener;
import it.giospezia.spicyqol.listeners.GuiListener;
import it.giospezia.spicyqol.listeners.TeleportTrackListener;
import it.giospezia.spicyqol.managers.*;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.plugin.java.JavaPlugin;

public class SpicyQoL extends JavaPlugin {

    private static SpicyQoL instance;

    private DataManager dataManager;
    private SpawnManager spawnManager;
    private HomeManager homeManager;
    private BackManager backManager;
    private TpaManager tpaManager;

    public static SpicyQoL get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Chat.init(this);

        dataManager = new DataManager(this);
        spawnManager = new SpawnManager(this, dataManager);
        homeManager = new HomeManager(this, dataManager);
        backManager = new BackManager(this, dataManager);
        tpaManager = new TpaManager(this);

        // Commands
        getCommand("qol").setExecutor(new QolCommand());

        getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(spawnManager));

        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
        getCommand("delhome").setExecutor(new DelHomeCommand(homeManager));
        getCommand("homes").setExecutor(new HomesCommand(homeManager)); // GUI

        getCommand("tpa").setExecutor(new TpaCommand(tpaManager));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpaManager));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(tpaManager));
        getCommand("tpamenu").setExecutor(new TpMenuCommand(tpaManager)); // GUI accept/deny

        getCommand("back").setExecutor(new BackCommand(backManager));
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("ping").setExecutor(new PingCommand());

        // Listeners
        getServer().getPluginManager().registerEvents(new DeathBackListener(backManager), this);
        getServer().getPluginManager().registerEvents(new TeleportTrackListener(backManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(homeManager, tpaManager), this);

        // ✅ Autosave ogni 60 secondi (riduce lag e corruzioni)
        getServer().getScheduler().runTaskTimer(this, () -> {
            dataManager.saveIfDirty();
        }, 20L * 60, 20L * 60);

        getLogger().info("SpicyQoL enabled (optimized)!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveNow(); // salva sempre alla chiusura
    }
}
package it.giospezia.spicyqol;

import it.giospezia.spicyqol.commands.*;
import it.giospezia.spicyqol.listeners.DeathBackListener;
import it.giospezia.spicyqol.listeners.GuiListener;
import it.giospezia.spicyqol.listeners.HotbarMenuListener;
import it.giospezia.spicyqol.listeners.TeleportTrackListener;
import it.giospezia.spicyqol.managers.*;
import it.giospezia.spicyqol.sit.SitCommand;
import it.giospezia.spicyqol.sit.SitListener;
import it.giospezia.spicyqol.sit.SitManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.plugin.java.JavaPlugin;

public class SpicyQoL extends JavaPlugin {

    private static SpicyQoL instance;

    private DataManager dataManager;
    private SpawnManager spawnManager;
    private HomeManager homeManager;
    private BackManager backManager;
    private TpaManager tpaManager;

    // /sit
    private SitManager sitManager;
    private SidebarManager sidebarManager;
    private RtpCommand rtpCommand;
    private TimeVoteManager timeVoteManager;

    public static SpicyQoL get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Chat.init(this);

        // Managers
        dataManager = new DataManager(this);
        spawnManager = new SpawnManager(this, dataManager);
        homeManager = new HomeManager(this, dataManager);
        backManager = new BackManager(this, dataManager);
        tpaManager = new TpaManager(this);
        rtpCommand = new RtpCommand(this, dataManager);
        timeVoteManager = new TimeVoteManager(this);

        sitManager = new SitManager();

        // Commands
        getCommand("qol").setExecutor(new QolCommand());

        getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(spawnManager));

        getCommand("home").setExecutor(new HomeCommand(homeManager));
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
        getCommand("delhome").setExecutor(new DelHomeCommand(homeManager));
        getCommand("homes").setExecutor(new HomesCommand(homeManager));

        getCommand("tpa").setExecutor(new TpaCommand(tpaManager));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpaManager));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(tpaManager));
        getCommand("tpamenu").setExecutor(new TpMenuCommand(tpaManager));

        getCommand("back").setExecutor(new BackCommand(backManager));
        getCommand("rtp").setExecutor(rtpCommand);
        getCommand("qolmenu").setExecutor(new MenuCommand(rtpCommand));
        getCommand("day").setExecutor(new TimeCommand(timeVoteManager, true));
        getCommand("night").setExecutor(new TimeCommand(timeVoteManager, false));
        getCommand("timevote").setExecutor(new TimeVoteCommand(timeVoteManager));
        getCommand("ping").setExecutor(new PingCommand());

        // Sit
        getCommand("sit").setExecutor(new SitCommand(sitManager));

        // Listeners
        getServer().getPluginManager().registerEvents(new DeathBackListener(backManager), this);
        getServer().getPluginManager().registerEvents(new TeleportTrackListener(backManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(homeManager, tpaManager, spawnManager, backManager, rtpCommand, timeVoteManager), this);
        getServer().getPluginManager().registerEvents(timeVoteManager, this);
        getServer().getPluginManager().registerEvents(new HotbarMenuListener(rtpCommand), this);

        if (getConfig().getBoolean("scoreboard.enabled", true)) {
            sidebarManager = new SidebarManager(this);
            getServer().getPluginManager().registerEvents(sidebarManager, this);
        }

        getServer().getPluginManager().registerEvents(new SitListener(sitManager), this);

        // Autosave data.yml ogni 60 secondi (se dirty)
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (dataManager != null) dataManager.saveIfDirty();
        }, 20L * 60, 20L * 60);

        getLogger().info("SpicyQoL enabled!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveNow();
        if (sitManager != null) sitManager.cleanupAll();
        if (sidebarManager != null) sidebarManager.shutdown();
        if (timeVoteManager != null) timeVoteManager.shutdown();

        getLogger().info("SpicyQoL disabled!");
    }
}

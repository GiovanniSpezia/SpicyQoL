package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.managers.BackManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathBackListener implements Listener {

    private final BackManager backManager;

    public DeathBackListener(BackManager backManager) {
        this.backManager = backManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // salva la location della morte
        backManager.setBack(e.getEntity(), e.getEntity().getLocation());
    }
}
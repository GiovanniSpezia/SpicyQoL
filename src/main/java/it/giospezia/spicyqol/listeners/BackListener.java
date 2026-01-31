package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.managers.BackManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class BackListener implements Listener {

    private final BackManager backManager;

    public BackListener(BackManager backManager) {
        this.backManager = backManager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        // salva la location di morte come back
        if (e.getPlayer().getLastDamageCause() != null) {
            // la location attuale subito dopo morte è ok come "morte"
            backManager.setBack(e.getPlayer(), e.getPlayer().getLocation());
        }
    }
}
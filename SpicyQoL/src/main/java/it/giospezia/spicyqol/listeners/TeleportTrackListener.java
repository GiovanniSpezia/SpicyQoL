package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.managers.BackManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportTrackListener implements Listener {

    private final BackManager backManager;

    public TeleportTrackListener(BackManager backManager) {
        this.backManager = backManager;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        // salva da dove sei partito, così /back torna lì
        if (e.getFrom() != null) {
            backManager.setBack(e.getPlayer(), e.getFrom());
        }
    }
}
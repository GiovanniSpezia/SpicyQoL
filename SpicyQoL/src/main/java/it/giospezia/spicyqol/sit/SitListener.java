package it.giospezia.spicyqol.sit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SitListener implements Listener {

    private final SitManager sitManager;

    public SitListener(SitManager sitManager) {
        this.sitManager = sitManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!sitManager.isSeated(e.getPlayer())) return;

        if (e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            sitManager.standUp(e.getPlayer());
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (sitManager.isSeated(e.getPlayer()) && e.isSneaking()) {
            sitManager.standUp(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        sitManager.cleanup(e.getPlayer());
    }
}
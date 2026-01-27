package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.gui.GuiKeys;
import it.giospezia.spicyqol.gui.HomesGui;
import it.giospezia.spicyqol.gui.TpaRequestGui;
import it.giospezia.spicyqol.managers.HomeManager;
import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GuiListener implements Listener {

    private final HomeManager homeManager;
    private final TpaManager tpaManager;

    public GuiListener(HomeManager homeManager, TpaManager tpaManager) {
        this.homeManager = homeManager;
        this.tpaManager = tpaManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getInventory().getHolder() == null) return;

        // Homes GUI
        if (e.getInventory().getHolder() instanceof HomesGui) {
            e.setCancelled(true);

            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;
            if (!item.hasItemMeta()) return;

            String homeName = item.getItemMeta()
                    .getPersistentDataContainer()
                    .get(GuiKeys.HOME_NAME(), PersistentDataType.STRING);

            if (homeName == null) return;

            p.closeInventory();
            homeManager.teleportHome(p, homeName);
            return;
        }

        // TPA GUI
        if (e.getInventory().getHolder() instanceof TpaRequestGui) {
            e.setCancelled(true);

            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;

            if (item.getType() == Material.LIME_WOOL) {
                p.closeInventory();
                tpaManager.accept(p);
                return;
            }

            if (item.getType() == Material.RED_WOOL) {
                p.closeInventory();
                tpaManager.deny(p);
                return;
            }
        }
    }
}
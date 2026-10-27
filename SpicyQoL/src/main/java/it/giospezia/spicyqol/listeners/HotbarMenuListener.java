package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.gui.GuiKeys;
import it.giospezia.spicyqol.gui.MenuGui;
import it.giospezia.spicyqol.commands.RtpCommand;
import it.giospezia.spicyqol.SpicyQoL;
import it.giospezia.spicyqol.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HotbarMenuListener implements Listener {
    private static final int MENU_SLOT = 8;
    private final RtpCommand rtpCommand;

    public HotbarMenuListener(RtpCommand rtpCommand) {
        this.rtpCommand = rtpCommand;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(SpicyQoL.get(), () -> ensureMenuItem(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(SpicyQoL.get(), () -> ensureMenuItem(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (!isMenuItem(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);
        new MenuGui(event.getPlayer(), MenuGui.Type.MAIN, rtpCommand).open();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!isMenuItemEnabled()) return; // slot 9 is a normal slot when the menu item is disabled
        if (isMenuItem(event.getCurrentItem()) || isMenuItem(event.getCursor()) || event.getHotbarButton() == MENU_SLOT) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() instanceof PlayerInventory && event.getSlot() == MENU_SLOT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!isMenuItemEnabled()) return;
        int menuRawSlot = event.getView().getTopInventory().getSize() + 27 + MENU_SLOT;
        if (isMenuItem(event.getOldCursor()) || event.getRawSlots().contains(menuRawSlot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack())) event.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (isMenuItem(event.getMainHandItem()) || isMenuItem(event.getOffHandItem())) event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(this::isMenuItem);
    }

    private void ensureMenuItem(Player player) {
        if (!isMenuItemEnabled()) {
            removeMenuItems(player);
            return;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack current = inventory.getItem(MENU_SLOT);
        if (isMenuItem(current)) {
            refreshMenuItemText(current);
            return;
        }

        int existingMenuSlot = -1;
        ItemStack[] contents = inventory.getStorageContents();
        for (int slot = 0; slot < contents.length; slot++) {
            if (isMenuItem(contents[slot])) {
                existingMenuSlot = slot;
                break;
            }
        }

        if (existingMenuSlot >= 0) {
            refreshMenuItemText(contents[existingMenuSlot]);
            inventory.setItem(existingMenuSlot, current == null || current.getType().isAir() ? null : current);
        } else if (current != null && !current.getType().isAir()) {
            int emptyStorageSlot = -1;
            for (int slot = 0; slot < 36; slot++) {
                if (slot == MENU_SLOT) continue;
                if (inventory.getItem(slot) == null || inventory.getItem(slot).getType().isAir()) {
                    emptyStorageSlot = slot;
                    break;
                }
            }
            if (emptyStorageSlot < 0) {
                player.sendMessage(Lang.t("§eLibera uno slot nell'inventario per ricevere la bussola del menu."));
                return;
            }
            inventory.setItem(emptyStorageSlot, current);
        }
        inventory.setItem(MENU_SLOT, createMenuItem());
    }

    // Whether the hotbar menu compass is enabled in the config.
    private boolean isMenuItemEnabled() {
        return SpicyQoL.get().getConfig().getBoolean("menu-item.enabled", true);
    }

    // Removes any menu compass from the player's inventory (used when the
    // menu-item feature is disabled in the config).
    private void removeMenuItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (isMenuItem(inventory.getItem(slot))) inventory.setItem(slot, null);
        }
    }

    private ItemStack createMenuItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(GuiKeys.MENU_ITEM(), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        refreshMenuItemText(item);
        return item;
    }

    // Re-applies the localized name/lore to an existing menu item, so items
    // created before a language change get updated too.
    private void refreshMenuItemText(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Lang.t("§6§lMenu giocatore"));
        meta.setLore(java.util.List.of(Lang.t("§7Tasto destro per aprire le funzioni")));
        item.setItemMeta(meta);
    }

    private boolean isMenuItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer()
                .has(GuiKeys.MENU_ITEM(), PersistentDataType.BYTE);
    }
}

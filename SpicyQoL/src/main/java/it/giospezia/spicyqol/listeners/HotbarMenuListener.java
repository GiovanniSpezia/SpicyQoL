package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.gui.GuiKeys;
import it.giospezia.spicyqol.gui.MenuGui;
import it.giospezia.spicyqol.commands.RtpCommand;
import it.giospezia.spicyqol.SpicyQoL;
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
        PlayerInventory inventory = player.getInventory();
        ItemStack current = inventory.getItem(MENU_SLOT);
        if (isMenuItem(current)) return;

        int existingMenuSlot = -1;
        ItemStack[] contents = inventory.getStorageContents();
        for (int slot = 0; slot < contents.length; slot++) {
            if (isMenuItem(contents[slot])) {
                existingMenuSlot = slot;
                break;
            }
        }

        if (existingMenuSlot >= 0) {
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
                player.sendMessage("§eLibera uno slot nell'inventario per ricevere la bussola del menu.");
                return;
            }
            inventory.setItem(emptyStorageSlot, current);
        }
        inventory.setItem(MENU_SLOT, createMenuItem());
    }

    private ItemStack createMenuItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lMenu giocatore");
        meta.setLore(java.util.List.of("§7Tasto destro per aprire le funzioni"));
        meta.getPersistentDataContainer().set(GuiKeys.MENU_ITEM(), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isMenuItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer()
                .has(GuiKeys.MENU_ITEM(), PersistentDataType.BYTE);
    }
}

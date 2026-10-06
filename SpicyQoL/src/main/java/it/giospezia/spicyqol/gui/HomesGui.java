package it.giospezia.spicyqol.gui;

import it.giospezia.spicyqol.managers.HomeManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomesGui implements InventoryHolder {

    private final Player player;
    private final HomeManager homeManager;
    private final Inventory inv;

    public HomesGui(Player player, HomeManager homeManager) {
        this.player = player;
        this.homeManager = homeManager;

        Set<String> homes = homeManager.listHomes(player);

        this.inv = Bukkit.createInventory(this, 54, Chat.c("&8✦ &6Le tue home"));

        build(homes);
    }

    private void build(Set<String> homes) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int slot = 0; slot < inv.getSize(); slot++) inv.setItem(slot, filler);

        if (homes.isEmpty()) {
            ItemStack no = new ItemStack(Material.BOOK);
            ItemMeta meta = no.getItemMeta();
            meta.setDisplayName(Chat.c("&e&lNessuna home salvata"));
            List<String> lore = new ArrayList<>();
            lore.add(Chat.c("&7Usa &e/sethome <nome>"));
            meta.setLore(lore);
            no.setItemMeta(meta);
            inv.setItem(22, no);
        } else {
            int[] slots = {10, 12, 14, 16, 19, 21, 23, 25};
            int i = 0;
            for (String name : homes) {
                if (i >= slots.length) break;
                Location loc = homeManager.getHome(player, name);
                ItemStack it = new ItemStack(Material.OAK_DOOR);
                ItemMeta meta = it.getItemMeta();

                meta.setDisplayName(Chat.c("&e&l" + name));
                List<String> lore = new ArrayList<>();
                if (loc != null) {
                    lore.add(Chat.c("&7Mondo: &f" + loc.getWorld().getName()));
                    lore.add(Chat.c("&7Coordinate: &f" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
                }
                lore.add(Chat.c(""));
                lore.add(Chat.c("&aClick per teletrasportarti"));
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(GuiKeys.HOME_NAME(), PersistentDataType.STRING, name.toLowerCase());
                it.setItemMeta(meta);
                inv.setItem(slots[i++], it);
            }
        }
        ItemStack menu = new ItemStack(Material.COMPASS);
        ItemMeta menuMeta = menu.getItemMeta();
        menuMeta.setDisplayName(Chat.c("&6&lMenu giocatore"));
        menuMeta.setLore(List.of(Chat.c("&7Torna al menu SpicyQoL")));
        menuMeta.getPersistentDataContainer().set(GuiKeys.ACTION(), PersistentDataType.STRING, "main");
        menu.setItemMeta(menuMeta);
        inv.setItem(53, menu);
    }

    public void open() {
        player.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}

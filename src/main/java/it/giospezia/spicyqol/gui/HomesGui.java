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

        int size = calcSize(homes.size());
        this.inv = Bukkit.createInventory(this, size, Chat.c("&6&lLe tue Home"));

        build(homes);
    }

    private int calcSize(int count) {
        int rows = Math.max(1, (int) Math.ceil(count / 9.0));
        rows = Math.min(6, rows);
        return rows * 9;
    }

    private void build(Set<String> homes) {
        if (homes.isEmpty()) {
            ItemStack no = new ItemStack(Material.BARRIER);
            ItemMeta meta = no.getItemMeta();
            meta.setDisplayName(Chat.c("&cNessuna home trovata"));
            List<String> lore = new ArrayList<>();
            lore.add(Chat.c("&7Usa &e/sethome <nome>"));
            meta.setLore(lore);
            no.setItemMeta(meta);
            inv.setItem(4, no);
            return;
        }

        int i = 0;
        for (String name : homes) {
            Location loc = homeManager.getHome(player, name);
            ItemStack it = new ItemStack(Material.OAK_DOOR);
            ItemMeta meta = it.getItemMeta();

            meta.setDisplayName(Chat.c("&e&l" + name));

            List<String> lore = new ArrayList<>();
            if (loc != null) {
                lore.add(Chat.c("&7Mondo: &f" + loc.getWorld().getName()));
                lore.add(Chat.c("&7X: &f" + loc.getBlockX() + " &8| &7Y: &f" + loc.getBlockY() + " &8| &7Z: &f" + loc.getBlockZ()));
            }
            lore.add(Chat.c(""));
            lore.add(Chat.c("&aClick per teletrasportarti"));
            meta.setLore(lore);

            // salva il nome home nell'item
            meta.getPersistentDataContainer().set(GuiKeys.HOME_NAME(), PersistentDataType.STRING, name.toLowerCase());
            it.setItemMeta(meta);

            inv.setItem(i++, it);
            if (i >= inv.getSize()) break;
        }
    }

    public void open() {
        player.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
package it.giospezia.spicyqol.gui;

import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TpaRequestGui implements InventoryHolder {

    private final Player player;
    private final TpaManager tpaManager;
    private final Inventory inv;

    public TpaRequestGui(Player player, TpaManager tpaManager) {
        this.player = player;
        this.tpaManager = tpaManager;
        this.inv = Bukkit.createInventory(this, 27, Chat.c("&e&lRichiesta TPA"));

        build();
    }

    private void build() {
        // filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // accetta (verde)
        ItemStack accept = new ItemStack(Material.LIME_WOOL);
        ItemMeta am = accept.getItemMeta();
        am.setDisplayName(Chat.c("&a&lACCETTA"));
        am.setLore(List.of(
                Chat.c("&7Accetta la richiesta di teleport"),
                Chat.c(""),
                Chat.c("&aClick per accettare")
        ));
        accept.setItemMeta(am);

        // rifiuta (rosso)
        ItemStack deny = new ItemStack(Material.RED_WOOL);
        ItemMeta dm = deny.getItemMeta();
        dm.setDisplayName(Chat.c("&c&lRIFIUTA"));
        dm.setLore(List.of(
                Chat.c("&7Rifiuta la richiesta di teleport"),
                Chat.c(""),
                Chat.c("&cClick per rifiutare")
        ));
        deny.setItemMeta(dm);

        inv.setItem(11, accept);
        inv.setItem(15, deny);

        // info centrale
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName(Chat.c("&eInfo"));
        TpaManager.Request r = tpaManager.getRequestFor(player);
        String fromName = (r != null && Bukkit.getPlayer(r.from) != null) ? Bukkit.getPlayer(r.from).getName() : "Sconosciuto";
        im.setLore(List.of(
                Chat.c("&7Richiesta da: &f" + fromName),
                Chat.c("&7Usa i bottoni sotto per decidere")
        ));
        info.setItemMeta(im);
        inv.setItem(13, info);
    }

    public void open() {
        player.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
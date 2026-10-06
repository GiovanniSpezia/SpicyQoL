package it.giospezia.spicyqol.gui;

import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class TimeVoteGui implements InventoryHolder {
    private final Player player;
    private final Inventory inventory;

    public TimeVoteGui(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, Chat.c("&8✦ &6Vota l'orario"));
        build();
    }

    private void build() {
        ItemStack filler = item(Material.BLACK_STAINED_GLASS_PANE, " ", List.of(), null);
        for (int slot = 0; slot < inventory.getSize(); slot++) inventory.setItem(slot, filler);

        inventory.setItem(11, item(Material.YELLOW_CONCRETE, "&e&lRichiedi il giorno",
                List.of("&7Avvia una votazione per impostare", "&7il giorno nel tuo mondo.", "", "&aClick per proporlo"), "time-day"));
        inventory.setItem(15, item(Material.BLUE_CONCRETE, "&9&lRichiedi la notte",
                List.of("&7Avvia una votazione per impostare", "&7la notte nel tuo mondo.", "", "&aClick per proporla"), "time-night"));
        inventory.setItem(13, item(Material.CLOCK, "&6&lVotazione condivisa",
                List.of("&7Da soli il cambio è immediato.", "&7Con più giocatori serve la", "&7maggioranza dei voti favorevoli.", "", "&f/timevote si &8| &f/timevote no"), null));
        inventory.setItem(26, item(Material.COMPASS, "&e&lTorna al menu",
                List.of("&7Ritorna alle funzioni SpicyQoL"), "main"));
    }

    private ItemStack item(Material material, String name, List<String> lore, String action) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Chat.c(name));
        if (!lore.isEmpty()) meta.setLore(lore.stream().map(Chat::c).toList());
        if (action != null) meta.getPersistentDataContainer().set(GuiKeys.ACTION(), PersistentDataType.STRING, action);
        stack.setItemMeta(meta);
        return stack;
    }

    public void open() { player.openInventory(inventory); }
    @Override public Inventory getInventory() { return inventory; }
}

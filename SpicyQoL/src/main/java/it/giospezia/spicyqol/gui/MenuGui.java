package it.giospezia.spicyqol.gui;

import it.giospezia.spicyqol.commands.RtpCommand;
import it.giospezia.spicyqol.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MenuGui implements InventoryHolder {
    public enum Type { MAIN, COMMANDS, RTP }

    private final Player player;
    private final Type type;
    private final RtpCommand rtpCommand;
    private final Inventory inventory;

    public MenuGui(Player player, Type type, RtpCommand rtpCommand) {
        this.player = player;
        this.type = type;
        this.rtpCommand = rtpCommand;
        String title = type == Type.RTP ? "&8✦ &6Teletrasporto casuale"
                : type == Type.COMMANDS ? "&8✦ &6Comandi utili" : "&8✦ &6Menu SpicyQoL";
        this.inventory = Bukkit.createInventory(this, 27, Chat.c(title));
        build();
    }

    private void build() {
        ItemStack filler = item(Material.GRAY_STAINED_GLASS_PANE, " ", List.of(), null);
        for (int slot = 0; slot < inventory.getSize(); slot++) inventory.setItem(slot, filler);

        if (type == Type.MAIN) {
            inventory.setItem(10, item(Material.OAK_DOOR, "&e&lLe mie home", List.of("&7Visualizza e raggiungi le tue home"), "homes"));
            inventory.setItem(12, item(Material.RESPAWN_ANCHOR, "&a&lSpawn", List.of("&7Torna al punto di ritrovo"), "spawn"));
            inventory.setItem(14, item(Material.ENDER_PEARL, "&b&lRandom teleport", List.of("&7Scegli una destinazione casuale", "&7Cooldown: 20 minuti"), "rtp"));
            inventory.setItem(16, item(Material.COMPASS, "&d&lUltima posizione", List.of("&7Torna al punto precedente"), "back"));
            inventory.setItem(26, item(Material.COMPASS, "&6&lComandi e info", List.of("&7Apri la guida rapida"), "commands"));
        } else if (type == Type.COMMANDS) {
            inventory.setItem(10, item(Material.PAPER, "&e&lHome", List.of("&f/sethome <nome>", "&f/home <nome>", "&f/homes", "&f/delhome <nome>"), null));
            inventory.setItem(12, item(Material.ENDER_PEARL, "&b&lTeleport", List.of("&f/rtp &7- casuale, cooldown 20 min", "&f/spawn", "&f/back"), null));
            inventory.setItem(14, item(Material.PLAYER_HEAD, "&d&lGiocatori", List.of("&f/tpa <giocatore>", "&f/tpaccept", "&f/tpdeny", "&f/tpamenu"), null));
            inventory.setItem(16, item(Material.FEATHER, "&a&lUtilità", List.of("&f/ping", "&f/sit", "&f/qolmenu"), null));
            inventory.setItem(26, item(Material.COMPASS, "&e&lTorna al menu", List.of("&7Ritorna alle funzioni"), "main"));
        } else {
            inventory.setItem(13, item(Material.MAP, "&6&lDestinazione casuale", List.of("&7Ti porterà in un luogo sicuro", "&7del mondo configurato.", "", "&eCooldown: 20 minuti"), null));
            inventory.setItem(22, item(Material.LIME_CONCRETE, "&a&lTeletrasportami", List.of("&7Clicca per cercare una destinazione"), "rtp-confirm"));
            inventory.setItem(26, item(Material.COMPASS, "&e&lTorna al menu", List.of("&7Ritorna alle funzioni"), "main"));
        }
    }

    private ItemStack item(Material material, String name, List<String> lore, String action) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Chat.c(name));
        if (!lore.isEmpty()) {
            List<String> colored = new ArrayList<>();
            for (String line : lore) colored.add(Chat.c(line));
            meta.setLore(colored);
        }
        if (action != null) meta.getPersistentDataContainer().set(GuiKeys.ACTION(), PersistentDataType.STRING, action);
        stack.setItemMeta(meta);
        return stack;
    }

    public void open() { player.openInventory(inventory); }
    public Type getType() { return type; }
    public RtpCommand getRtpCommand() { return rtpCommand; }
    @Override public Inventory getInventory() { return inventory; }
}

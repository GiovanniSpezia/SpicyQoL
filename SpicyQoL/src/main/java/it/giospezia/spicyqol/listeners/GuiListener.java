package it.giospezia.spicyqol.listeners;

import it.giospezia.spicyqol.commands.RtpCommand;
import it.giospezia.spicyqol.gui.GuiKeys;
import it.giospezia.spicyqol.gui.HomesGui;
import it.giospezia.spicyqol.gui.MenuGui;
import it.giospezia.spicyqol.gui.TimeVoteGui;
import it.giospezia.spicyqol.gui.TpaRequestGui;
import it.giospezia.spicyqol.managers.BackManager;
import it.giospezia.spicyqol.managers.HomeManager;
import it.giospezia.spicyqol.managers.SpawnManager;
import it.giospezia.spicyqol.managers.TpaManager;
import it.giospezia.spicyqol.managers.TimeVoteManager;
import it.giospezia.spicyqol.utils.Chat;
import it.giospezia.spicyqol.utils.Perms;
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
    private final SpawnManager spawnManager;
    private final BackManager backManager;
    private final RtpCommand rtpCommand;
    private final TimeVoteManager timeVoteManager;

    public GuiListener(HomeManager homeManager, TpaManager tpaManager, SpawnManager spawnManager,
                       BackManager backManager, RtpCommand rtpCommand, TimeVoteManager timeVoteManager) {
        this.homeManager = homeManager;
        this.tpaManager = tpaManager;
        this.spawnManager = spawnManager;
        this.backManager = backManager;
        this.rtpCommand = rtpCommand;
        this.timeVoteManager = timeVoteManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getHolder() == null) return;
        Object holder = event.getView().getTopInventory().getHolder();

        if (holder instanceof HomesGui || holder instanceof TpaRequestGui || holder instanceof MenuGui || holder instanceof TimeVoteGui) {
            event.setCancelled(true);
            if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;
        } else return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        if (holder instanceof HomesGui) {
            String home = item.getItemMeta().getPersistentDataContainer()
                    .get(GuiKeys.HOME_NAME(), PersistentDataType.STRING);
            if (home != null) {
                player.closeInventory();
                homeManager.teleportHome(player, home);
            } else if ("main".equals(item.getItemMeta().getPersistentDataContainer()
                    .get(GuiKeys.ACTION(), PersistentDataType.STRING))) {
                new MenuGui(player, MenuGui.Type.MAIN, rtpCommand).open();
            }
            return;
        }

        if (holder instanceof TpaRequestGui) {
            String action = item.getItemMeta().getPersistentDataContainer()
                    .get(GuiKeys.ACTION(), PersistentDataType.STRING);
            if ("main".equals(action)) {
                new MenuGui(player, MenuGui.Type.MAIN, rtpCommand).open();
                return;
            }
            if (item.getType() == Material.LIME_WOOL) {
                player.closeInventory();
                tpaManager.accept(player);
            } else if (item.getType() == Material.RED_WOOL) {
                player.closeInventory();
                tpaManager.deny(player);
            }
            return;
        }

        String action = item.getItemMeta().getPersistentDataContainer()
                .get(GuiKeys.ACTION(), PersistentDataType.STRING);
        if (action == null) return;
        if (holder instanceof TimeVoteGui) {
            switch (action) {
                case "main" -> new MenuGui(player, MenuGui.Type.MAIN, rtpCommand).open();
                case "time-day", "time-night" -> {
                    player.closeInventory();
                    timeVoteManager.startVote(player, action.equals("time-day"));
                }
                default -> { }
            }
            return;
        }
        if (!(holder instanceof MenuGui)) return;
        switch (action) {
            case "main" -> new MenuGui(player, MenuGui.Type.MAIN, rtpCommand).open();
            case "commands" -> new MenuGui(player, MenuGui.Type.COMMANDS, rtpCommand).open();
            case "time-menu" -> new TimeVoteGui(player).open();
            case "homes" -> {
                if (player.hasPermission(Perms.HOMES)) new HomesGui(player, homeManager).open();
                else Chat.send(player, "&cNon hai il permesso per usare le home.");
            }
            case "spawn" -> {
                if (player.hasPermission(Perms.SPAWN)) { player.closeInventory(); spawnManager.teleportToSpawn(player); }
                else Chat.send(player, "&cNon hai il permesso per andare allo spawn.");
            }
            case "rtp" -> new MenuGui(player, MenuGui.Type.RTP, rtpCommand).open();
            case "rtp-confirm" -> { player.closeInventory(); rtpCommand.teleport(player); }
            case "back" -> {
                if (player.hasPermission(Perms.BACK)) { player.closeInventory(); backManager.back(player); }
                else Chat.send(player, "&cNon hai il permesso per usare /back.");
            }
            default -> { }
        }
    }
}

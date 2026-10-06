package it.giospezia.spicyqol.gui;

import it.giospezia.spicyqol.SpicyQoL;
import org.bukkit.NamespacedKey;

public class GuiKeys {
    public static NamespacedKey HOME_NAME() {
        return new NamespacedKey(SpicyQoL.get(), "home_name");
    }

    public static NamespacedKey ACTION() {
        return new NamespacedKey(SpicyQoL.get(), "menu_action");
    }

    public static NamespacedKey MENU_ITEM() {
        return new NamespacedKey(SpicyQoL.get(), "player_menu_item");
    }
}

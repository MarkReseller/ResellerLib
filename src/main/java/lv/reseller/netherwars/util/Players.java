package lv.reseller.netherwars.util;

import org.bukkit.entity.Player;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class Players {

    public static void clear(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
    }
}

package lv.reseller.netherwars.logic;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum TeamColor {

    WHITE(ChatColor.WHITE, (byte)0, Color.WHITE),
    ORANGE(ChatColor.GOLD, (byte)1, Color.ORANGE),

    AQUA(ChatColor.AQUA, (byte)3, Color.AQUA),
    YELLOW(ChatColor.YELLOW, (byte)4, Color.YELLOW),
    LIME(ChatColor.GREEN, (byte)5, Color.LIME),
    PINK(ChatColor.LIGHT_PURPLE, (byte)6, Color.FUCHSIA),
    GRAY(ChatColor.DARK_GRAY, (byte)7, Color.GRAY),
    SILVER(ChatColor.GRAY, (byte)8, Color.SILVER),
    TEAL(ChatColor.DARK_AQUA, (byte)9, Color.TEAL),
    PURPLE(ChatColor.DARK_PURPLE, (byte)10, Color.PURPLE),
    BLUE(ChatColor.BLUE, (byte)11, Color.BLUE),

    GREEN(ChatColor.DARK_GREEN, (byte)13, Color.GREEN),
    RED(ChatColor.RED, (byte)14, Color.RED),
    BLACK(ChatColor.BLACK, (byte)15, Color.BLACK);

    private final ChatColor chatColor;
    private final byte blockData;
    private final Color armorColor;

    TeamColor(ChatColor chatColor, byte blockData, Color armorColor) {
        this.chatColor = chatColor;
        this.blockData = blockData;
        this.armorColor = armorColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public byte getBlockData() {
        return blockData;
    }

    public Color getArmorColor() {
        return armorColor;
    }
}

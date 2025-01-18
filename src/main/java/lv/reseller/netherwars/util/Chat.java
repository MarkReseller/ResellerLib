package lv.reseller.netherwars.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;

public class Chat {

    public static Color translateChatColorToColor(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case DARK_AQUA:
                return Color.TEAL;
            case DARK_BLUE:
                return Color.BLUE;
            case DARK_GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_PURPLE:
                return Color.PURPLE;
            case DARK_RED:
                return Color.RED;
            case GOLD:
                return Color.YELLOW;
            case GRAY:
                return Color.GRAY;
            case GREEN:
                return Color.GREEN;
            case LIGHT_PURPLE:
                return Color.PURPLE;
            case RED:
                return Color.RED;
            case WHITE:
                return Color.WHITE;
            case YELLOW:
                return Color.YELLOW;
            default:
                break;
        }
        return null;
    }

    public static ChatColor translateColorToChatColor(Color color)
    {
        if(color == Color.AQUA)
            return ChatColor.AQUA;
        if(color == Color.BLACK)
            return ChatColor.BLACK;
        if(color == Color.BLUE)
            return ChatColor.BLUE;
        if(color == Color.FUCHSIA)
            return ChatColor.LIGHT_PURPLE;
        if(color == Color.GRAY)
            return ChatColor.DARK_GRAY;
        if(color == Color.GREEN)
            return ChatColor.DARK_GREEN;
        if(color == Color.LIME)
            return ChatColor.GREEN;
        if(color == Color.MAROON)
            return ChatColor.DARK_RED;
        if(color == Color.NAVY)
            return ChatColor.DARK_BLUE;
        if(color == Color.OLIVE)
            return ChatColor.DARK_GREEN;
        if(color == Color.ORANGE)
            return ChatColor.GOLD;
        if(color == Color.PURPLE)
            return ChatColor.DARK_PURPLE;
        if(color == Color.RED)
            return ChatColor.RED;
        if(color == Color.SILVER)
            return ChatColor.GRAY;
        if(color == Color.TEAL)
            return ChatColor.DARK_AQUA;
        if(color == Color.WHITE)
            return ChatColor.WHITE;
        if (color == Color.YELLOW)
            return ChatColor.YELLOW;
        return null;
    }

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<String> colorize(List<String> list) {
        list.replaceAll(Chat::colorize);
        return list;
    }

    public static String leftPadIgnoreColor(String str, int length) {
        for(char c : str.toCharArray()) {
            if(c == 167) length += 2;
        }
        return String.format("%-" + length + "s", str);
    }

    public static String rightPadIgnoreColor(String str, int length) {
        for(char c : str.toCharArray()) {
            if(c == 167) length += 2;
        }
        return String.format("%" + length + "s", str);
    }

}

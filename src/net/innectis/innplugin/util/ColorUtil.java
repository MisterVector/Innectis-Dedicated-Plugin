package net.innectis.innplugin.util;

import net.innectis.innplugin.player.chat.ChatColor;

/**
 * Utilities to convert Innectis colors into colors of
 * other APIs, such as Bukkit or Bungee Chat colors
 *
 * @author AlphaBlend
 */
public class ColorUtil {

    /**
     * Converts an IDP color into a bungee chat color
     * @param color
     * @return
     */
    public static net.md_5.bungee.api.ChatColor idpColorToBungee(ChatColor color) {
        switch (color) {
            case BLACK:
                return net.md_5.bungee.api.ChatColor.BLACK;
            case DARK_BLUE:
                return net.md_5.bungee.api.ChatColor.DARK_BLUE;
            case DARK_GREEN:
                return net.md_5.bungee.api.ChatColor.DARK_GREEN;
            case DARK_AQUA:
                return net.md_5.bungee.api.ChatColor.DARK_AQUA;
            case DARK_RED:
                return net.md_5.bungee.api.ChatColor.DARK_RED;
            case DARK_PURPLE:
                return net.md_5.bungee.api.ChatColor.DARK_PURPLE;
            case GOLD:
                return net.md_5.bungee.api.ChatColor.GOLD;
            case GRAY:
                return net.md_5.bungee.api.ChatColor.GRAY;
            case DARK_GRAY:
                return net.md_5.bungee.api.ChatColor.DARK_GRAY;
            case BLUE:
                return net.md_5.bungee.api.ChatColor.BLUE;
            case GREEN:
                return net.md_5.bungee.api.ChatColor.GREEN;
            case AQUA:
                return net.md_5.bungee.api.ChatColor.AQUA;
            case RED:
                return net.md_5.bungee.api.ChatColor.RED;
            case LIGHT_PURPLE:
                return net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;
            case YELLOW:
                return net.md_5.bungee.api.ChatColor.YELLOW;
            case WHITE:
                return net.md_5.bungee.api.ChatColor.WHITE;
            case EFFECT_MAGIC:
                return net.md_5.bungee.api.ChatColor.MAGIC;
            case EFFECT_BOLD:
                return net.md_5.bungee.api.ChatColor.BOLD;
            case EFFECT_STRIKE_THROUGH:
                return net.md_5.bungee.api.ChatColor.STRIKETHROUGH;
            case EFFECT_UNDERLINED:
                return net.md_5.bungee.api.ChatColor.UNDERLINE;
            case EFFECT_ITALIC:
                return net.md_5.bungee.api.ChatColor.ITALIC;
        }

        // Bad color argument
        throw new IllegalArgumentException("Color not supported: " + color.toString());
    }
    
}

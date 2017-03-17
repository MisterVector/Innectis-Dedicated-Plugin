package net.innectis.innplugin.util;

import net.innectis.innplugin.player.chat.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A util class for various chat functions
 *
 * @author AlphaBlend
 */
public class ChatUtil {

    /**
     * Creates a text component from a series of messages, defaulting
     * the color to dark green
     * @param messages
     * @return
     */
    public static TextComponent createTextComponent(String... messages) {
        return createTextComponent(ChatColor.DARK_GREEN, messages);
    }

    /**
     * Creates a text component from a series of messages, with a defined
     * color for each message
     * @param color
     * @param messages
     * @return
     */
    public static TextComponent createTextComponent(ChatColor color, String... messages) {
        TextComponent finalComponent = null;

        for (String msg : messages) {
            TextComponent component = new TextComponent(msg);
            component.setColor(ColorUtil.idpColorToBungee(color));

            if (finalComponent != null) {
                finalComponent.addExtra(component);
            } else {
                finalComponent = new TextComponent(component);
            }
        }

        return finalComponent;
    }

    /**
     * Creates a clickable URL from its title
     * @param text
     * @param URL
     * @return
     */
    public static TextComponent createHTMLLink(String text, String URL) {
        TextComponent component = new TextComponent(text);
        component.setColor(ColorUtil.idpColorToBungee(ChatColor.WHITE));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));

        return component;
    }

    /**
     * Creates a component that will run a command when clicking text
     * @param text
     * @param command
     * @return
     */
    public static TextComponent createCommandLink(String text, String command) {
        TextComponent component = new TextComponent(text);
        component.setColor(ColorUtil.idpColorToBungee(ChatColor.WHITE));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        return component;
    }

}

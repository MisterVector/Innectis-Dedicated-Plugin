package net.innectis.innplugin.player.chat;

/**
 *
 * @author Hret
 */
public class Prefix {

    public static final int PreGroupPrefixLocation = 1;
    public static final int GroupPrefixLocation = 2;
    public static final int PostGroupPrefixLocation = 3;

    private String text;
    private ChatColor surroundColor;
    private ChatColor textColor;

    public Prefix(String text, ChatColor surroundColor, ChatColor textColor) {
        this.text = text;
        this.surroundColor = surroundColor;
        this.textColor = textColor;
    }

    public Prefix(String text, ChatColor color) {
        this.text = text;
        this.surroundColor = ChatColor.WHITE;
        this.textColor = color;
    }

    /**
     * Gets the prefix text
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the color that is used to color the brackets
     * @return surroundColor
     */
    public ChatColor getSurroundColor() {
        return surroundColor;
    }

    /**
     * Gets the color of the text
     * @return textColor
     */
    public ChatColor getTextColor() {
        return textColor;
    }

    /**
     * Returns the prefix with the colors in the right place
     * [TEXT]
     * @return prefix text
     */
    public String getFullPrefix() {
        return surroundColor + "[" + textColor + text + surroundColor + "]";
    }
    
}

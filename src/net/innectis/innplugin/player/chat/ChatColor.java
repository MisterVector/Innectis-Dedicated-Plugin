package net.innectis.innplugin.player.chat;

import java.awt.Color;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.util.StringUtil;

/**
 * All supported color values for chat
 */
public enum ChatColor {

    /**
     * Represents black
     */
    BLACK("0", "000000", "black", "blk"),
    /**
     * Represents dark blue
     */
    DARK_BLUE("1", "0000BF", "darkblue", "dark_blue"),
    /**
     * Represents dark green
     */
    DARK_GREEN("2", "00BF00", "darkgreen", "dark_green"),
    /**
     * Represents dark blue (aqua)
     */
    DARK_AQUA("3", "00BFBF", "darkaqua", "dark_aqua", "darkcyan", "dark_cyan"),
    /**
     * Represents dark red
     */
    DARK_RED("4", "BF0000", "darkred", "dark_red"),
    /**
     * Represents dark purple
     */
    DARK_PURPLE("5", "BF00BF", "darkpurple", "dark_purple"),
    /**
     * Represents gold
     */
    GOLD("6", "BFBF00", "gold"),
    /**
     * Represents gray
     */
    GRAY("7", "BFBFBF", "gray"),
    /**
     * Represents dark gray
     */
    DARK_GRAY("8", "404040", "darkgray", "dark_gray"),
    /**
     * Represents blue
     */
    BLUE("9", "4040FF", "blue"),
    /**
     * Represents green
     */
    GREEN("a", "49FF40", "green"),
    /**
     * Represents aqua
     */
    AQUA("b", "40FFFF", "aqua", "cyan"),
    /**
     * Represents red
     */
    RED("c", "FF4040", "red"),
    /**
     * Represents light purple
     */
    LIGHT_PURPLE("d", "FF40FF", "lightpurple", "light_purple", "pink"),
    /**
     * Represents yellow
     */
    YELLOW("e", "FFFF40", "yellow"),
    /**
     * Represents white
     */
    WHITE("f", "FFFFFF", "white"),
    //--------------------------------------------------
    // Effects
    //--------------------------------------------------
    /**
     * Clears any effect and sets the colour to white
     */
    EFFECT_CLEAR("g", "FFFFFF", "clear"),
    /**
     * Represents "magic" (text that spazes out, rapidly changing jibberish)
     */
    EFFECT_MAGIC("k", "FFFFFF", "magic"),
    /** Shows given text in bold */
    EFFECT_BOLD("l", "FFFFFF", "bold"),
    /** Strike through text */
    EFFECT_STRIKE_THROUGH("m", "FFFFFF", "strikethrough"),
    /** Underlines text
     * <p/>
     * Note: this makes the row higher then fits in the chat rows.
     * This causes the line to write on the other lines.
     */
    EFFECT_UNDERLINED("n", "FFFFFF", "underlined", "underline"),
    /** Sets the text in italic */
    EFFECT_ITALIC("o", "FFFFFF", "italic");
    //
    private final String code;
    private final String htmlcolor;
    private String[] argList = null;
    private static final Map<String, ChatColor> colors = new HashMap<String, ChatColor>(values().length);

    private ChatColor(final String code, final String htmlcolor, String... argList) {
        this.code = code;
        this.htmlcolor = htmlcolor;
        this.argList = argList;
    }

    /**
     * Gets the data value associated with this color
     *
     * @return An integer value of this color code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the HTML color code associated with this color
     *
     * @return An string value of the HTML color
     */
    public String getHTMLColor() {
        return htmlcolor;
    }

    /**
     * Gets the list of color names associated with this color
     * @return
     */
    public String[] getArgList() {
        return argList;
    }

    /**
     * Prints out the chatcolor as the MC string value.
     * @return MC colour code (full code)
     */
    @Override
    public String toString() {
        return String.format("\u00A7%s", code);
    }

    /**
     * Gets the color represented by the specified color code
     *
     * @param code Code to check
     * @return Associative {@link Color} with the given code, or null if it doesn't exist
     */
    public static ChatColor getByCode(String code) {
        if (StringUtil.stringIsNullOrEmpty(code)) {
            return null;
        }

        return colors.get(code.toLowerCase());
    }

    /**
     * Gets the color represented by the specified color code or name.
     * <p/>
     * This method will suffer no (noticible) penalty if used with a colour code
     * in contract to <b>getByCode</b>.
     * <p/>
     * However, when used with a string (or code that doesn't exist) it will
     * check all values present in the enum. Even though this wouldn't be a
     * massive overhead, its still something to take into account.
     *
     * @param codeOrString Code or name to check
     * @return Associative {@link Color} with the given code, or null if it doesn't exist
     */
    public static ChatColor getByCodeOrString(String codeOrString) {
        ChatColor codeColour = getByCode(codeOrString);

        if (codeColour != null) {
            return codeColour;
        }

        for (ChatColor color : ChatColor.values()) {
            for (String arg : color.getArgList()) {
                if (arg.equalsIgnoreCase(codeOrString)) {
                    return color;
                }
            }
        }

        return null;
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return input.replaceAll("(?i)\u00A7[0-F]", "");
    }

    /**
     * Converts a string of text by converting all %color% to its &<code> variant
     * @param text
     * @return
     */
    public static String convertLongColors(String text) {
        for (ChatColor clr : values()) {
            for (String arg : clr.getArgList()) {
                text = text.replace("%" + arg + "%", "&" + clr.getCode());
            }
        }

        return text;
    }

    /**
     * Parses all colors in the given line,
     * but also checks if the player got the permission to use them
     */
    public static String parseSignColor(String line, IdpPlayer player) {
        String colourCodes = "0"; //black
        Formatter fmt = new Formatter();

        // Check for colour levels
        if (player.hasPermission(Permission.special_sign_colour_lvl3)) {
            colourCodes = "0-9a-fA-F"; // all colors
        } else if (player.hasPermission(Permission.special_sign_colour_lvl2)) {
            colourCodes = "0-8fF"; // all dark colors, gold, white
        } else if (player.hasPermission(Permission.special_sign_colour_lvl1)) {
            colourCodes = "078"; // gray, dark grey
        }

        // Extra effects perm
        if (player.hasPermission(Permission.special_sign_colour_any)) {
            colourCodes += "gGk-oK-O"; // Effects
        } else if (player.hasPermission(Permission.special_sign_colour_effects)) {
            colourCodes += "gGl-oL-O"; // Effects
        }

        String regex = StringUtil.format("&(?<!&&)(?=[{0}])", colourCodes);

        line = line.replaceAll("\u00A7", ""); //prevent cheating somehow
        for (ChatColor clr : ChatColor.values()) {
            line = line.replaceAll(fmt.format(regex, clr.getCode()).toString(), "\u00A7");
        }
        return line.replace("&&", "&");
    }

    /**
     * Parses all colors in the text
     */
    public static String parseChatColor(String text) {
        if (StringUtil.stringIsNullOrEmpty(text)) {
            return null;
        }
        for (ChatColor clr : ChatColor.values()) {
            for (String str : clr.getArgList()) {
                text = text.replaceAll("(?i)%" + str.toLowerCase() + "%", "\u00A7" + clr.getCode());
            }
        }
        return text.replace("&&", "&");
    }

    static {
        for (ChatColor color : ChatColor.values()) {
            colors.put(color.getCode(), color);
        }
    }
    
}

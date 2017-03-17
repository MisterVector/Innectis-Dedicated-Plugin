package net.innectis.innplugin.holiday;

import java.util.Random;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.Prefix;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * @author Hret/Alphablend
 *
 * Module for christmas related events
 */
class ChristmasModule extends HolidayModule {

    // For random prefix of a reindeer
    private static final String[] PREFIX_NAMES = new String[]{"Dasher", "Dancer", "Prancer", "Vixen",
        "Comet", "Cupid", "Donner", "Blitzen", "Rudolf"};
    // For christmas-like colors
    private static final ChatColor[] PREFIX_COLOURS = new ChatColor[]{ChatColor.DARK_RED, ChatColor.RED, ChatColor.WHITE};
    private final String cheertext;
    private Random rand;

    public ChristmasModule() {
        super(HolidayType.CHRISTMAS);

        // build the cheertext
        cheertext = "" + ChatColor.DARK_RED + ChatColor.EFFECT_UNDERLINED + "Merry Christmas"
                + ChatColor.EFFECT_CLEAR + ChatColor.WHITE + " from "
                + ChatColor.AQUA + "Innectis" + ChatColor.WHITE + "!";
    }

    @Override
    public void onPlayerJoin(IdpPlayer player) {
        Prefix prefix = getPrefix(player.getName());
        player.getSession().setPrefix(Prefix.PostGroupPrefixLocation, prefix, false);

        player.printInfo(cheertext);
    }

    /**
     * Returns an randomized prefix.
     * @param name
     * @return
     */
    private Prefix getPrefix(String name) {
        rand = new Random(name.hashCode());
        return new Prefix(makeChristmasText(PREFIX_NAMES[rand.nextInt(PREFIX_NAMES.length)]), ChatColor.AQUA);
    }

    /**
     * Makes the text alter between specified colours
     * @param originalText
     * @return
     */
    private static String makeChristmasText(String originalText) {
        StringBuilder resultString = new StringBuilder(originalText.length() * 2);
        int idx = 0;

        char ch;
        for (int i = 0; i < originalText.length(); i++) {
            ch = originalText.charAt(i);

            if (!Character.isWhitespace(ch)) {
                resultString.append(PREFIX_COLOURS[idx]);
                if (++idx >= PREFIX_COLOURS.length) {
                    idx = 0;
                }
            }
            resultString.append(ch);
        }

        return resultString.toString();
    }

}
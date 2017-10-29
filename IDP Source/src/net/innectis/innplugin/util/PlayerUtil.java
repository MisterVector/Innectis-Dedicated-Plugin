package net.innectis.innplugin.util;

import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;

/**
 * Util class that's based on players
 *
 * @author AlphaBlend
 */
public class PlayerUtil {

    /**
     * Returns the colored equivalent of the non-colored player name. This
     * should not be used for any special names like [SYSTEM] or [UNASSIGNED]
     * @param playerName
     * @return
     */
    public static String getColoredName(String playerName) {
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);
        return getColoredName(credentials);
    }

    /**
     * Returns the colored equivalent of the name given by the
     * given credentials
     * @param credentials
     * @return
     */
    public static String getColoredName(PlayerCredentials credentials) {
        UUID uniqueId = credentials.getUniqueId();
        String coloredName = null;

        if (uniqueId == Configuration.EVERYONE_IDENTIFIER) {
            coloredName = Configuration.EVERYONE_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.LOT_ASSIGNABLE_IDENTIFIER) {
            coloredName = Configuration.LOT_ASSIGNABLE_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.LOT_ACCESS_IDENTIFIER) {
            coloredName = Configuration.LOT_ACCESS_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.SERVER_GENERATED_IDENTIFIER) {
            coloredName = Configuration.SERVER_GENERATED_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.OTHER_IDENTIFIER) {
            coloredName = Configuration.OTHER_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.UNASSIGNED_IDENTIFIER) {
            coloredName = Configuration.UNASSIGNED_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.AUTOMATIC_IDENTIFIER) {
            coloredName = Configuration.AUTOMATIC_CREDENTIALS.getName();
        } else if (uniqueId == Configuration.SYSTEM_IDENTIFIER) {
            coloredName = Configuration.SYSTEM_CREDENTIALS.getName();
        }

        if (coloredName != null) {
            return ChatColor.DARK_GRAY + coloredName;
        } else {
            PlayerGroup group = PlayerGroup.getGroupOfPlayerById(credentials.getUniqueId());
            return group.getPrefix().getTextColor() + credentials.getName();
        }
    }
    
}

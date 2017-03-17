package net.innectis.innplugin.objects;

import net.innectis.innplugin.player.PlayerGroup;

/**
 * Class to handle spoofed names
 *
 * @author AlphaBlend
 */
public class SpoofObject {

    private String spoofName;
    private PlayerGroup spoofGroup;

    /**
     * Construct a new spoof object
     *
     * @param spoofName
     * @param group
     */
    public SpoofObject(String spoofName, PlayerGroup spoofGroup) {
        this.spoofName = spoofName;
        this.spoofGroup = spoofGroup;
    }

    /**
     * Returns if the player is hidden
     *
     * @return
     */
    public boolean isHidden() {
        return (spoofName.isEmpty());
    }

    /**
     * Get the spoofed username
     *
     * @return
     */
    public String getSpoofName() {
        return spoofName;
    }

    /**
     * Gets the colored equivalent of the spoofed name
     *
     * @return
     */
    public String getSpoofNameColor() {
        if (spoofName.length() > 0) {
            return spoofGroup.getPrefix().getTextColor() + spoofName;
        }

        return "";
    }

}

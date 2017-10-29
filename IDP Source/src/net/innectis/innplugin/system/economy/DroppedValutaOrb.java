package net.innectis.innplugin.system.economy;

import org.bukkit.entity.ExperienceOrb;

/**
 * A class holding a dropped valuta orb
 *
 * @author AlphaBlend
 */
public class DroppedValutaOrb {

    private String coloredPlayerName;
    private int amount;

    public DroppedValutaOrb(String coloredPlayerName, int amount) {
        this.coloredPlayerName = coloredPlayerName;
        this.amount = amount;
    }

    /**
     * Gets the name of the player that dropped this valuta orb
     * @return
     */
    public String getColoredPlayerName() {
        return coloredPlayerName;
    }

    /**
     * Gets the amount of dropped valutas
     * @return
     */
    public int getAmount() {
        return amount;
    }

}

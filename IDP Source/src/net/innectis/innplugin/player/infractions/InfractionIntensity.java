package net.innectis.innplugin.player.infractions;

/**
 *
 * @author Hret
 *
 * Enum to represent certain levels of intensity for infractions.
 * There are 4 levels listed in this enum to make it easier to assign a level to a player when an
 * infraction is needed.
 */
public enum InfractionIntensity {

    /**
     * Large offence that is just below bannable.
     */
    HIGH(5),
    /**
     * Medium offence that needs to be recorded.
     */
    MIDDLE(3),
    /**
     * Small offence that will gets recorded after the player.
     * Use this for a small but noticable offence.
     */
    LOW(1),
    /**
     * Only notice the infraction to a player but do not increase their level.
     */
    NOTICE(0);
    //
    private final int intlevel;

    private InfractionIntensity(int intlevel) {
        this.intlevel = intlevel;
    }

    /**
     * A numirical value to represent the intensity of the infraction.
     * @return will always return a positive number;
     */
    public int getIntensityLevel() {
        return intlevel;
    }
    
}

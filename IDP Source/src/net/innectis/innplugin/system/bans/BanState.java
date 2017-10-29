package net.innectis.innplugin.system.bans;

/**
 * Lists all the states of a ba
 *
 * @author AlphaBlend
 */
public enum BanState {

    USERBAN_INDEFINITE(1, "Indefinite ban"),
    USERBAN_TIMED(2, "Timed Ban"),
    IPBAN_INDEFINITE(3, "Indefinite IP Ban"),
    IPBAN_TIMED(4, "Timed IP Ban"),
    EXPIRED(5, "Expired");

    private final int stateNumber;
    private final String title;

    private BanState(int stateNumber, String title) {
        this.stateNumber = stateNumber;
        this.title = title;
    }

    /**
     * Returns the number representing this ban state
     * @return
     */
    public int getStateNumber() {
        return stateNumber;
    }

    /**
     * Gets the title of this ban state
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets a ban state from its number
     * @param stateNumber
     * @return
     */
    public static BanState fromStateNumber(int stateNumber) {
        for (BanState state : values()) {
            if (state.getStateNumber() == stateNumber) {
                return state;
            }
        }

        return null;
    }
    
}

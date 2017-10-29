package net.innectis.innplugin.system.game;

/**
 *
 * @author Hret
 *
 * Interface for a game with a timer
 */
public interface GameTimer {

    /**
     * This method is called when a timer ticks.
     */
    public void onInterval();

    /**
     * The ID of the game where this timer is tied to.
     * @return the ID of the game
     */
    public int getId();

}

package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Nosliw
 */
public class InnPlayerQuitEvent extends APlayerEvent {

    private String quitMessage;

    public InnPlayerQuitEvent(IdpPlayer player, String quitmessage) {
        super(player, InnEventType.PLAYER_QUIT);

        this.quitMessage = quitmessage;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }

    /**
     * <b>This event cannot be terminated!</b>
     * @param terminate
     */
    @Override
    public void setTerminate(boolean terminate) {
        throw new UnsupportedOperationException("This event cannot be set to terminate!");
    }

    /**
     * <b>This event cannot be terminated!</b>
     * @param terminate
     */
    @Override
    public boolean shouldTerminate() {
        return false;
    }
    
}

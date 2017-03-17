package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Nosliw
 */
public class InnPlayerKickedEvent extends APlayerEvent implements InnEventCancellable {

    private String leaveMessage;
    private String reason;
    private boolean cancel;

    public InnPlayerKickedEvent(IdpPlayer player, String leaveMessage, String reason) {
        super(player, InnEventType.PLAYER_QUIT);

        this.leaveMessage = leaveMessage;
        this.reason = reason;
        this.cancel = false;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}

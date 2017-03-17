package net.innectis.innplugin.player.request;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * Request to change the lotflag of a lot
 */
public class GenericRequest extends Request {

    public GenericRequest(InnPlugin plugin, IdpPlayer player, IdpPlayer requester, Long currentTime, Long timeout) {
        super(plugin, player, requester, currentTime, timeout);
    }

    @Override
    public void onReject() {
        IdpPlayer locRequester = getRequester();
        if (locRequester != null) {
            locRequester.printInfo(getPlayer().getColoredName(), " has denied your request!");
        }
    }

    @Override
    public void onTimeout() {
        IdpPlayer locRequester = getRequester();
        if (getPlayer() != null) {
            if (locRequester == null) {
                getPlayer().printInfo("The request has timed out.");
            } else {
                getPlayer().printInfo("The request from ", locRequester.getColoredName(), " has timed out.");
            }
        }
        if (locRequester != null) {
            locRequester.printInfo("Your request has timed out.");
        }
    }

    @Override
    public void onAccept() {
        IdpPlayer locRequester = getRequester();

        if (locRequester != null) {
            locRequester.printInfo(getPlayer().getColoredName(), " has accepted your request");
        }
    }

    @Override
    public String getDescription() {
        // String from /accept "You have accepted the "
        IdpPlayer requestplayer = getRequester();
        if (requestplayer != null) {
            return "Request from " + requestplayer.getColoredName();
        } else {
            return "Request from " + requester;
        }
    }
    
}

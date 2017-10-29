package net.innectis.innplugin.player.request;

import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * This is a request to remove the lot of a player.
 */
public class LotRemovalRequest extends Request {

    private int lotId;

    public LotRemovalRequest(InnPlugin plugin, IdpPlayer lotOwner, IdpPlayer staffIssuer, int lotid) {
        super(plugin, lotOwner, staffIssuer, System.currentTimeMillis(), Configuration.LOT_REMOVAL_REQUEST_TIMEOUT);
        this.lotId = lotid;
    }

    @Override
    public void onReject() {
        getRequester().printInfo(getPlayer().getColoredName(), " has denied the lot removal request!");
    }

    @Override
    public void onTimeout() {
        IdpPlayer locRequester = getRequester();
        if (getPlayer() != null) {
            if (locRequester == null) {
                getPlayer().printInfo("The lot removal request has timed out.");
            } else {
                getPlayer().printInfo("The lot removal request from ", locRequester.getColoredName(), " has timed out.");
            }
        }
        if (locRequester != null) {
            locRequester.printInfo("Your lot removal request has timed out.");
        }
    }

    @Override
    public void onAccept() {
        IdpPlayer staffMember = getRequester();
        IdpPlayer lotOwner = getPlayer();

        if (staffMember == null && lotOwner != null) {
            lotOwner.printError("Player not found, the staff member must be online!");
            return;
        } else if (lotOwner == null && staffMember != null) {
            staffMember.printError("Player not found, lot owner must be online!");
            return;
        } else if (staffMember == null || lotOwner == null) {
            return;
        }
        // Log
        InnPlugin.logInfo(lotOwner.getColoredDisplayName(), " has confirmed to allow ", staffMember.getColoredDisplayName(),
                " to remove lot #" + lotId + "!");

        // Send info to owner
        lotOwner.printInfo("You confirmed the request from " + staffMember.getColoredDisplayName(), " to remove lot #" + lotId + "!");

        // Print password to staffmember
        staffMember.printInfo(lotOwner.getColoredName(), " has confirmed the lot removal request!");

        InnectisLot lot = LotHandler.getLot(lotId);

        if (lot != null) {
            staffMember.printInfo("The password for the removal is '", ChatColor.AQUA + lot.getPassword(), "'.");
        } else {
            staffMember.printError("The lot you requested to be removed was not found!");
        }
    }

    @Override
    public String getDescription() {
        // String from /accept "You have accepted the "
        return "Lot removal request for lot #" + lotId;
    }

}

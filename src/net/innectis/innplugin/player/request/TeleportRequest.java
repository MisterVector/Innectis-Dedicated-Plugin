package net.innectis.innplugin.player.request;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.ValutaSinkManager;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.location.IdpSpawnFinder;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import org.bukkit.Location;

/**
 *
 * @author Hret
 */
public class TeleportRequest extends Request {

    private int tpCost;
    private boolean isTpr;
    private boolean isStaffRequest;

    public TeleportRequest(InnPlugin plugin, IdpPlayer player, IdpPlayer requester, int tpCost, boolean isTpr) {
        this(plugin, player, requester, tpCost, isTpr, false);
    }

    public TeleportRequest(InnPlugin plugin, IdpPlayer player, IdpPlayer requester, int tpCost, boolean isTpr, boolean isStaffRequest) {
        super(plugin, player, requester, System.currentTimeMillis(), Configuration.PLAYER_TELEPORT_REQUEST_TIMEOUT);
        this.isTpr = isTpr;
        this.tpCost = tpCost;
        this.isStaffRequest = isStaffRequest;
    }

    @Override
    public void onReject() {
        IdpPlayer locRequester = getRequester();
        if (locRequester != null) {
            locRequester.printInfo(getPlayer().getColoredName(), " has denied your teleport request!");
        }
    }

    @Override
    public void onTimeout() {
        IdpPlayer locRequester = getRequester();
        if (getPlayer() != null) {
            if (locRequester == null) {
                getPlayer().printInfo("The teleport request has timed out.");
            } else {
                getPlayer().printInfo("The teleport request from ", locRequester.getColoredName(), " has timed out.");
            }
        }
        if (locRequester != null) {
            locRequester.printInfo("Your teleport request has timed out.");
        }
    }

    /**
     * Checks if the command was a tpr command
     *
     * @return true is the command was /tpr
     */
    public boolean isTprCommand() {
        return isTpr;
    }

    @Override
    public void onAccept() {
        IdpPlayer locRequester = getRequester();
        IdpPlayer locPlayer = getPlayer();

        if (locRequester == null && locPlayer != null) {
            locPlayer.printError("Player not found, teleport canceled!");
            return;
        } else if (locPlayer == null && locRequester != null) {
            locRequester.printError("Player not found, teleport canceled!");
            return;
        } else if (locRequester == null || locPlayer == null) {
            return;
        }

        if (isTpr) {
            if (!isStaffRequest && locPlayer.getSession().isInDamageState()) {
                locRequester.printError(locPlayer.getDisplayName() + " took damage since the request was made. Unable to teleport them to you!");
                locPlayer.printError("You took damage since the teleport request was made. Unable to teleport!");

                return;
            }

            // Make sure the person who requested the teleport is not in mid-air
            // to prevent abuse of this request
            Location loc = locRequester.getLocation();
            IdpSpawnFinder finder = new IdpSpawnFinder(loc);
            finder.findClosestSpawn(false);

            if (finder.getHeightDifference() > 3) {
                IdpWorldType worldType = IdpWorldFactory.getWorld(loc.getWorld().getName()).getActingWorldType();

                if (worldType != IdpWorldType.CREATIVEWORLD) {
                    locRequester.printError("Request could not be made as you are at a dangerous height.");
                    locPlayer.printError("The requester is at a dangerous height, request cancelled.");
                }

                return;
            }
        }

        if (tpCost > 0) {
            TransactionObject transaction = TransactionHandler.getTransactionObject(locRequester);
            int balance = transaction.getValue(TransactionType.VALUTAS);

            if (balance < tpCost) {
                locPlayer.printError("You are unable to accept the " + getDescription());
                locRequester.printError(locPlayer.getName() + " was unable to accept your teleport request.");
                return;
            } else {
                transaction.subtractValue(tpCost, TransactionType.VALUTAS);
            }
        }

        IdpPlayer p1 = null, p2 = null;

        if (isTpr) {
            p1 = locPlayer;
            p2 = locRequester;
        } else {
            p1 = locRequester;
            p2 = locPlayer;
        }

        boolean success = false;

        List<TeleportType> teleportTypes = new ArrayList<TeleportType>();
        teleportTypes.add(TeleportType.USE_SPAWN_FINDER);
        teleportTypes.add(TeleportType.PVP_IMMUNITY);

        if (!isStaffRequest) {
            teleportTypes.add(TeleportType.RESTRICT_IF_NETHER);
            teleportTypes.add(TeleportType.RESTRICT_IF_NOESCAPE);
        }

        if (p1.teleport(p2, teleportTypes.toArray(new TeleportType[teleportTypes.size()]))) {
            locRequester.printInfo(locPlayer.getColoredName(), " has accepted your teleport request!");
            success = true;
        } else {
            // Give back what was taken from the player previously
            if (tpCost > 0) {
                TransactionObject transaction = TransactionHandler.getTransactionObject(locRequester);
                transaction.addValue(tpCost, TransactionType.VALUTAS);
                locPlayer.printError("You are unable to accept the " + getDescription());
                locRequester.printError(locPlayer.getName() + " was unable to accept your teleport request.");
            }
        }

        // Make sure the valutas were actually taken before we log the valuta sink
        if (success) {
            ValutaSinkManager.addToSink(tpCost);
        }
    }

    @Override
    public String getDescription() {
        // String from /accept "You have accepted the "
        IdpPlayer requestplayer = getRequester();
        if (requestplayer != null) {
            return "teleport request from " + requestplayer.getColoredName();
        } else {
            return "teleport request from " + requester;
        }
    }
    
}

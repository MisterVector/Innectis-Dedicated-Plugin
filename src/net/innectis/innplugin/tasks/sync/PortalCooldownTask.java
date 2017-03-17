package net.innectis.innplugin.tasks.sync;

import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.objects.PortalDestinationResult;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Location;

/**
 * A task to teleport a player after a period of time of
 * being in a portal
 *
 * @author AlphaBlend
 */
public class PortalCooldownTask extends LimitedTask {

    private IdpPlayer player;
    private Location destinationLocation = null;

    public PortalCooldownTask(IdpPlayer player, Location destination, long delay) {
        super(RunBehaviour.SYNCED, delay, 1);

        this.player = player;
        this.destinationLocation = destination;
    }

    @Override
    public void run() {
        player.getSession().setPortalCooldownTaskId(-1);

        PortalDestinationResult result = LocationUtil.getPlayerPortalLocation(player);

        // Double check that the location is still the same from where the
        // player is, in case they moved out of the portal
        if (result == null || !result.getLocation().equals(destinationLocation)) {
            return;
        }

        // The material of the target location's block
        IdpMaterial targetMat = IdpMaterial.fromBlock(destinationLocation.getBlock());

        // Cannot warp into a portal
        if (targetMat == IdpMaterial.PORTAL) {
            player.printError("Cannot warp into a portal!");
        } else {
            // Cannot warp to destination, so warp to spawn instead
            if (!player.teleport(destinationLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY)) {
                player.printInfo("Unable to warp to destination. Warping to spawn instead.");

                player.teleport(WarpHandler.getSpawn(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
            }
        }
    }
    
}

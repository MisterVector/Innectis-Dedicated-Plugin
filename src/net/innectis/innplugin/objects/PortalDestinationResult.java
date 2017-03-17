package net.innectis.innplugin.objects;

import org.bukkit.Location;

/**
 * Contains the destination of a portal and
 * whether it can be teleported to instantly
 *
 * @author AlphaBlend
 */
public class PortalDestinationResult {

    private Location location = null;
    private boolean instant = false;

    public PortalDestinationResult(Location location, boolean instant) {
        this.location = location;
        this.instant = instant;
    }

    /**
     * Gets the destination of the portal
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets if the location warped to should
     * be instant
     * @return
     */
    public boolean isInstant() {
        return instant;
    }

}

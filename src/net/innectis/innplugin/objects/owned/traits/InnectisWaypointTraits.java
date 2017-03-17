package net.innectis.innplugin.objects.owned.traits;

import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import org.bukkit.Location;

/**
 * Traits for a waypoint
 *
 * @author AlphaBlend
 */
public class InnectisWaypointTraits extends InnectisOwnedObjectTraits {

    private Location destination;
    InnectisWaypoint.CostType costType;

    public InnectisWaypointTraits(InnectisWaypoint waypoint) {
        super(waypoint);

        this.destination = waypoint.getDestination();
        this.costType = waypoint.getCostType();
    }

    /**
     * Applies this waypoint's traits to the target
     * @param waypoint
     */
    public void applyTraits(InnectisWaypoint waypoint) {
        super.applyTraits(waypoint);

        waypoint.setDestination(destination);
        waypoint.setCostType(costType);
    }

}

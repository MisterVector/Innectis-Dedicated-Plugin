package net.innectis.innplugin.objects.owned.traits;

import java.util.List;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisOwnedObject;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.player.PlayerCredentials;

/**
 * The base class for owned object traits
 *
 * @author AlphaBlend
 */
public class InnectisOwnedObjectTraits {

    private PlayerCredentials owner;
    private List<PlayerCredentials> members;
    private List<PlayerCredentials> operators;
    private long flags;

    public InnectisOwnedObjectTraits(InnectisOwnedObject obj) {
        this.owner = obj.getOwnerCredentials();
        this.members = obj.getMembers();
        this.operators = obj.getOperators();
        this.flags = obj.getFlags();
    }

    /**
     * Applies this owned object's traits to the target
     * @param obj
     */
    public void applyTraits(InnectisOwnedObject obj) {
        obj.setOwner(owner);
        obj.setMembers(members);
        obj.setOperators(operators);
        obj.setFlags(flags);
    }

    /**
     * Gets an owned object traits object from an owned object
     * @param obj
     * @return
     */
    public static InnectisOwnedObjectTraits getTraits(InnectisOwnedObject obj) {
        if (obj instanceof InnectisBookcase) {
            return new InnectisBookcaseTraits((InnectisBookcase) obj);
        } else if (obj instanceof InnectisChest) {
            return new InnectisChestTraits((InnectisChest) obj);
        } else if (obj instanceof InnectisLot) {
            return new InnectisLotTraits((InnectisLot) obj);
        } else if (obj instanceof InnectisWaypoint) {
            return new InnectisWaypointTraits((InnectisWaypoint) obj);
        }

        return new InnectisOwnedObjectTraits(obj);
    }

}

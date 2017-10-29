package net.innectis.innplugin.objects.owned;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.PlayerCredentials;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Lynxy
 */
public class InnectisWaypoint extends InnectisOwnedObject {

    public enum CostType {
        /**
         * No cost needed to make this waypoint
         */
        NO_COST(1),

        /**
         * This waypoint was made using 3 lapis blocks
         */
        LAPIS_COST(2),

        /**
         * This waypoint was made using 30 valutas
         */
        VALUTA_COST(3);

        private final int typeId;

        private CostType(int typeId) {
            this.typeId = typeId;
        }

        /**
         * Gets the ID of this cost type
         * @return
         */
        public int getTypeId() {
            return typeId;
        }

        /**
         * Gets a cost type from its ID
         * @param id
         * @return
         */
        public static CostType getCostTypeFromId(int id) {
            for (CostType ct : values()) {
                if (ct.getTypeId() == id) {
                    return ct;
                }
            }

            return null;
        }

    }

    private Block waypoint;
    private Location destination;
    private CostType type;

    public InnectisWaypoint(World world, Block waypoint, Location destination, int id, PlayerCredentials ownerCredentials, List<PlayerCredentials> members, List<PlayerCredentials> operators, long flags, CostType type) {
        super(world, waypoint.getLocation().toVector(), waypoint.getLocation().toVector(), id, ownerCredentials, members, operators, flags);
        this.waypoint = waypoint;
        this.destination = destination;
        this.type = type;
    }

    public InnectisWaypoint(Block waypoint, Location destination, int id, PlayerCredentials ownerCredentials, List<PlayerCredentials> members, List<PlayerCredentials> operators, long flags, CostType type) {
        this(waypoint.getWorld(), waypoint, destination, id, ownerCredentials, members, operators, flags, type);
    }

    @Override
    protected Class<? extends FlagType> getEnumClass() {
        return WaypointFlagType.class;
    }

    public Block getWaypoint() {
        return this.waypoint;
    }

    public void setWaypoint(Block waypoint) {
        this.waypoint = waypoint;
    }

    public Location getDestination() {
        return this.destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    /**
     * Returns true if location is within the area of this object
     * @param location
     */
    @Override
    public boolean isAtLocation(Location location) {
        if (waypoint != null
                && waypoint.getWorld().equals(location.getWorld())
                && waypoint.getLocation().getBlockX() == location.getBlockX()
                && waypoint.getLocation().getBlockY() == location.getBlockY()
                && waypoint.getLocation().getBlockZ() == location.getBlockZ()) {
            return true;
        }

        return false;
    }

    /**
     * Returns whether or not the block specified for this InnectisWaypoint is actually a Waypoint
     */
    public boolean isValid() {
        if (waypoint != null && IdpMaterial.fromBlock(waypoint) != IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
            return false;
        }
        return !(waypoint == null);
    }

    /**
     * Returns true if the player is the owner or a member, or if this
     * Waypoint's owner is % or contains member %, or if this Waypoint has member
     * @ and player is a member of the Lot this waypoint is on
     * @param playerName
     */
    @Override
    public boolean canPlayerAccess(String playerName) {
        if (super.canPlayerAccess(playerName)) {
            return true;
        }

        if (containsMember("@")) { //allow lot members
            InnectisLot lot = LotHandler.getLot(waypoint.getLocation());
            if (lot != null && (lot.containsMember(playerName) || lot.containsOperator(playerName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the cost type of this waypoint's creation
     * @return
     */
    public CostType getCostType() {
        return type;
    }

    /**
     * Sets the cost type of this waypoint
     * @param type
     */
    public void setCostType(CostType type) {
        this.type = type;
    }

    private boolean createWaypointInDB() {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("REPLACE INTO waypoints "
                    + "(owner_id, world, locx, locy, locz, tworld, tlocx, tlocy, tlocz, tyaw, flags, cost_type)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(2, super.getWorld().getName());
            statement.setInt(3, getWaypoint().getX());
            statement.setInt(4, getWaypoint().getY());
            statement.setInt(5, getWaypoint().getZ());
            statement.setString(6, getDestination().getWorld().getName());
            statement.setInt(7, getDestination().getBlockX());
            statement.setInt(8, getDestination().getBlockY());
            statement.setInt(9, getDestination().getBlockZ());
            statement.setFloat(10, getDestination().getYaw());
            statement.setLong(11, super.getFlags());
            statement.setInt(12, type.getTypeId());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                super.setId(result.getInt(1));
                super.setUpdated(false);
            } else {
                InnPlugin.logError("New waypoint not found in the database!");
                return false;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save new waypoint!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    @Override
    public boolean save() {
        if (super.getId() == -1) {
            return createWaypointInDB();
        }

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE waypoints SET "
                    + "owner_id = ?, world = ?, locx = ?, "
                    + "locy = ?, locz = ?, tworld = ?, "
                    + "tlocx = ?, tlocy = ?, tlocz = ?, "
                    + "tyaw = ?, flags = ?, cost_type = ? "
                    + "WHERE waypointid = ?;");
            statement.setString(1, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(2, super.getWorld().getName());
            statement.setInt(3, getWaypoint().getX());
            statement.setInt(4, getWaypoint().getY());
            statement.setInt(5, getWaypoint().getZ());
            statement.setString(6, getDestination().getWorld().getName());
            statement.setInt(7, getDestination().getBlockX());
            statement.setInt(8, getDestination().getBlockY());
            statement.setInt(9, getDestination().getBlockZ());
            statement.setFloat(10, getDestination().getYaw());
            statement.setLong(11, super.getFlags());
            statement.setInt(12, type.getTypeId());
            statement.setInt(13, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM waypoints_members WHERE waypointid = ?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            for (PlayerCredentials pc : getMembers()) {
                statement = DBManager.prepareStatement("INSERT INTO waypoints_members (waypointid, player_id, isop) VALUES (?, ?, 0);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            for (PlayerCredentials pc : getOperators()) {
                statement = DBManager.prepareStatement("INSERT INTO waypoints_members (waypointid, player_id, isop) VALUES (?, ?, 1);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            super.setUpdated(false);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save waypoint #" + getId() + "!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    @Override
    public OwnedObjectType getType() {
        return OwnedObjectType.WAYPOINT;
    }

}

package net.innectis.innplugin.objects.owned.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.objects.owned.InnectisWaypoint.CostType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * @author Lynxy
 */
public class WaypointHandler {

    private static HashMap<Integer, InnectisWaypoint> _waypoints = new HashMap<Integer, InnectisWaypoint>();

    public static synchronized HashMap<Integer, InnectisWaypoint> getWaypoints() {
        return _waypoints;
    }

    public static synchronized void setWaypoints(HashMap<Integer, InnectisWaypoint> waypoints) {
        _waypoints = waypoints;
    }

    public static boolean loadWaypoints() {
        getWaypoints().clear();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM waypoints;");
            result = statement.executeQuery();

            //load all waypoints into memory
            while (result.next()) {
                InnectisWaypoint waypoint = getWaypointFromResultSet(result);

                if (waypoint != null) {
                    getWaypoints().put(result.getInt("waypointid"), waypoint);
                }
            }

            saveWaypoints(); //save any waypoint that may have been modified
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET WAYPOINTS FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    public static void saveWaypoints() {
        InnectisWaypoint waypoint;
        int exceptionCt = 0;
        Exception exception = null;
        for (Iterator<InnectisWaypoint> it = getWaypoints().values().iterator(); it.hasNext();) {
            waypoint = it.next();
            if (waypoint.getUpdated()) {
                try {
                    waypoint.save();
                } catch (Exception ex) {
                    if (exception == null) {
                        exception = ex;

                        InnPlugin.logError("################### ERROR SAVING DOOR #" + ChatColor.GOLD + waypoint.getId() + ChatColor.RED + " ###################", ex);
                    }
                    exceptionCt++;
                }
            }
        }
        if (exceptionCt > 0) {
            InnPlugin.getPlugin().sendAdminMessage("savewaypointexception ", "There were " + exceptionCt + " error(s) saving waypoints! Please check log.");
        }
    }

    public static List<InnectisWaypoint> getAllWaypoints() {
        return new ArrayList<InnectisWaypoint>(_waypoints.values());
    }

    public static InnectisWaypoint getWaypoint(int waypointId) {
        return getWaypoints().get(waypointId);
    }

    public static InnectisWaypoint getWaypoint(Location location) {
        return getWaypoint(location, true);
    }

    public static InnectisWaypoint getWaypoint(Location location, boolean allowRemove) {
        InnectisWaypoint waypoint;
        for (Iterator<InnectisWaypoint> it = _waypoints.values().iterator(); it.hasNext();) {
            waypoint = it.next();
            if (waypoint.isAtLocation(location)) {
                if (waypoint.isValid()) {
                    return waypoint;
                } else if (allowRemove) {
                    if (removeWaypointForcibly(location.getBlock(), waypoint.getId(), false)) {
                        it.remove();
                    }
                }
            }
        }
        return null;
    }

    public static List<InnectisWaypoint> getWaypoints(String playerName) {
        List<InnectisWaypoint> waypoints = new ArrayList<InnectisWaypoint>();
        InnectisWaypoint waypoint;

        for (Iterator<InnectisWaypoint> it = getWaypoints().values().iterator(); it.hasNext();) {
            waypoint = it.next();

            if (waypoint.getOwner().equalsIgnoreCase(playerName)) {
                waypoints.add(waypoint);
            }
        }
        return waypoints;
    }

    private static InnectisWaypoint getWaypointFromResultSet(ResultSet result) throws SQLException {
        // Don't load waypoints in unloaded worlds
        World world = Bukkit.getWorld(result.getString("world"));
        if (world == null) {
            return null;
        }

        // Don't load waypoints with an invalid destination
        World destworld = Bukkit.getWorld(result.getString("tworld"));
        if (destworld == null) {
            return null;
        }

        int x = result.getInt("locx"), y = result.getInt("locy"), z = result.getInt("locz");
        Vector dest = new Vector(result.getInt("tlocx"), result.getInt("tlocy"), result.getInt("tlocz"));
        Block waypoint = world.getBlockAt(x, y, z);
        Location destination = new Location(destworld, dest.getBlockX(), dest.getBlockY(), dest.getBlockZ());
        destination.setYaw(result.getFloat("tyaw"));

        if (IdpMaterial.fromBlock(waypoint) != IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
            return null;
        }

        String ownerIdString = result.getString("owner_id");
        UUID ownerId = UUID.fromString(ownerIdString);
        PlayerCredentials ownerCredentials = null;

        if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
            ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
        } else {
            ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
        }

        int waypointid = result.getInt("waypointid");
        PreparedStatement statement = DBManager.prepareStatement("SELECT player_id, isop FROM waypoints_members WHERE waypointid = ?;");
        statement.setInt(1, waypointid);
        ResultSet result2 = statement.executeQuery();

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();

        while (result2.next()) {
            String memberIdString = result2.getString("player_id");
            UUID memberId = UUID.fromString(memberIdString);

            if (memberId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                members.add(Configuration.EVERYONE_CREDENTIALS);
            } else if (memberId.equals(Configuration.LOT_ACCESS_IDENTIFIER)) {
                members.add(Configuration.LOT_ACCESS_CREDENTIALS);
            } else {
                boolean isOp = result2.getBoolean("isop");

                PlayerCredentials memberCredentials = PlayerCredentialsManager.getByUniqueId(memberId, true);

                if (isOp) {
                    operators.add(memberCredentials);
                } else {
                    members.add(memberCredentials);
                }
            }
        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        return new InnectisWaypoint(world, waypoint, destination, result.getInt("waypointid"), ownerCredentials, members, operators, result.getLong("flags"), InnectisWaypoint.CostType.getCostTypeFromId(result.getInt("cost_type")));
    }

    public static boolean hasWaypoint(String playerName) {
        InnectisWaypoint waypoint;
        for (Iterator<InnectisWaypoint> it = getWaypoints().values().iterator(); it.hasNext();) {
            waypoint = it.next();

            if (waypoint.getOwner().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOwnWaypoint(String playerName, Location location) {
        InnectisWaypoint waypoint = getWaypoint(location);

        if (waypoint == null) {
            return false;
        }

        if (waypoint.getOwner().equalsIgnoreCase(playerName)) {
            return true;
        }

        return false;
    }

    public static InnectisWaypoint createWaypoint(PlayerCredentials ownerCredentials, Block block, InnectisWaypoint.CostType costType) throws SQLException {
        Location loc = block.getLocation();
        InnectisWaypoint existingWaypoint = getWaypoint(loc);

        // Allow placement, original owner is assumed
        if (existingWaypoint != null) {
            return existingWaypoint;
        }

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        members.add(Configuration.EVERYONE_CREDENTIALS);

        InnectisWaypoint innwaypoint = new InnectisWaypoint(block.getWorld(), block, loc, -1, ownerCredentials, members, null, 0, costType);
        innwaypoint.save();
        WaypointHandler.getWaypoints().put(innwaypoint.getId(), innwaypoint);

        BlockHandler.setBlock(block, IdpMaterial.LAPIS_LAZULI_OREBLOCK);

        return innwaypoint;
    }

    public static InnectisWaypoint removeWaypoint(IdpPlayer player, Block block) throws SQLException {
        if (IdpMaterial.fromBlock(block) != IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
            return null;
        }

        Location loc = block.getLocation();
        InnectisWaypoint waypoint = getWaypoint(loc);

        if (waypoint == null) {
            return null;
        }

        InnectisLot lot = LotHandler.getLot(loc);
        //you can remove a waypoint if you own it or if its on your lot
        if (waypoint.getOwner().equalsIgnoreCase(player.getName())
                || (lot != null && lot.getOwner().equalsIgnoreCase(player.getName()))
                || player.hasPermission(Permission.owned_object_override)) {
            removeWaypointForcibly(block, waypoint.getId(), true);
            return waypoint;
        } else {
            player.printError("You cannot destroy that waypoint!");
        }
        return null;
    }

    /**
     * Returns true if the block was the last one in the set
     */
    public static boolean removeWaypointForcibly(Block block, int id, boolean allowRemovalFromMainVariable) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM waypoints WHERE waypointid = ?;");
            statement.setInt(1, id);
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM waypoints_members WHERE waypointid = ?;");
            statement.setInt(1, id);
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            if (allowRemovalFromMainVariable) {
                _waypoints.remove(id);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Could not remove waypoint!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

}

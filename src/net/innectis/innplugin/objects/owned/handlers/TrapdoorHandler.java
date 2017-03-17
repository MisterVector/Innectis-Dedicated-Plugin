package net.innectis.innplugin.objects.owned.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.InnectisTrapdoor;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Handler for all owned trapdoors
 *
 * @author AlphaBlend
 */
public class TrapdoorHandler {

    private static HashMap<Integer, InnectisTrapdoor> trapdoors = new HashMap<Integer, InnectisTrapdoor>();

    public TrapdoorHandler() {
    }

    /**
     * Adds a new trapdoor to the list of trapdoors
     * @param world
     * @param loc
     * @param ownerCredentials
     * @return The owned Trapdoor object
     * @throws SQLException
     */
    public static InnectisTrapdoor createTrapdoor(World world, Location loc, PlayerCredentials ownerCredentials) throws SQLException {
        // Allow placement, original owner is assumed
        if (isTrapdoorAtLocation(loc)) {
            return null;
        }

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        members.add(Configuration.EVERYONE_CREDENTIALS);

        InnectisTrapdoor trapdoor = new InnectisTrapdoor(world, loc, 0, ownerCredentials, members, null, 0);
        trapdoor.save();
        trapdoors.put(trapdoor.getId(), trapdoor);
        return trapdoor;
    }

    /**
     * Removes a trapdoor by its location
     * @param id
     * @return
     */
    public static InnectisTrapdoor removeTrapdoor(Location loc) throws SQLException {
        for (InnectisTrapdoor trapdoor : trapdoors.values()) {
            if (trapdoor.isAtLocation(loc)) {
                removeTrapdoor(trapdoor.getId());
                return trapdoor;
            }
        }

        return null;
    }

    /**
     * Removes a trapdoor by its id
     * @param id
     */
    public static void removeTrapdoor(int id) throws SQLException {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM trapdoors WHERE trapdoorid = ?");
            statement.setInt(1, id);
            statement.execute();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM trapdoors_members WHERE trapdoorid = ?");
            statement.setInt(1, id);
            statement.execute();
            DBManager.closePreparedStatement(statement);

            trapdoors.remove(id);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to remove trapdoor!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Gets a trapdoor by its ID
     * @param id
     * @return
     */
    public static InnectisTrapdoor getTrapdoor(int id) {
        return trapdoors.get(id);
    }

    /**
     * Gets a trapdoor by its location
     * @param loc
     * @return
     */
    public static InnectisTrapdoor getTrapdoor(Location loc) {
        for (InnectisTrapdoor trapdoor : trapdoors.values()) {
            if (trapdoor.isAtLocation(loc)) {
                return trapdoor;
            }
        }

        return null;
    }

    /**
     * Gets the trapdoors owned by the specified owner
     * @param playerName
     * @return
     */
    public static List<InnectisTrapdoor> getTrapdoors(String playerName) {
        List<InnectisTrapdoor> tdlist = new ArrayList<InnectisTrapdoor>();

        for (InnectisTrapdoor trapdoor : trapdoors.values()) {
            if (trapdoor.getOwner().equalsIgnoreCase(playerName)) {
                tdlist.add(trapdoor);
            }
        }

        return tdlist;
    }

    /**
     * Returns if a trapdoor is at the specified location
     * @param loc
     * @return
     */
    public static boolean isTrapdoorAtLocation(Location loc) {
        for (InnectisTrapdoor trapdoor : trapdoors.values()) {
            if (trapdoor.getLocation().equals(loc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Loads all trapdoors into memory
     */
    public static boolean loadTrapdoors() {
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        ResultSet set = null;
        ResultSet set2 = null;

        try {
            // First, load all the trapdoors into memory
            statement = DBManager.prepareStatement("SELECT * FROM trapdoors");
            set = statement.executeQuery();

            while (set.next()) {
                World world = Bukkit.getWorld(set.getString("world"));

                if (world != null) {
                    int id = set.getInt("trapdoorid");

                    String ownerIdString = set.getString("owner_id");
                    UUID ownerId = UUID.fromString(ownerIdString);
                    PlayerCredentials ownerCredentials = null;

                    if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
                        ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
                    } else {
                        ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
                    }

                    int x = set.getInt("locx");
                    int y = set.getInt("locy");
                    int z = set.getInt("locz");
                    Location loc = new Location(world, x, y, z);

                    long flags = set.getLong("flags");

                    // Load the members of the trapdoor
                    statement2 = DBManager.prepareStatement("SELECT player_id, isop FROM trapdoors_members WHERE trapdoorid = ?;");
                    statement2.setInt(1, id);
                    set2 = statement2.executeQuery();

                    List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
                    List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();

                    while (set2.next()) {
                        String memberIdString = set2.getString("player_id");
                        UUID memberId = UUID.fromString(memberIdString);

                        if (memberId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                            members.add(Configuration.EVERYONE_CREDENTIALS);
                        } else if (memberId.equals(Configuration.LOT_ACCESS_IDENTIFIER)) {
                            members.add(Configuration.LOT_ACCESS_CREDENTIALS);
                        } else {
                            boolean isOp = set2.getBoolean("isop");

                            PlayerCredentials memberCredentials = PlayerCredentialsManager.getByUniqueId(memberId, true);

                            if (isOp) {
                                operators.add(memberCredentials);
                            } else {
                                members.add(memberCredentials);
                            }
                        }
                    }

                    DBManager.closeResultSet(set2);
                    DBManager.closePreparedStatement(statement2);

                    trapdoors.put(id, new InnectisTrapdoor(world, loc, id, ownerCredentials, members, operators, flags));
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET TRAPDOORS FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closeResultSet(set2);
            DBManager.closePreparedStatement(statement);
            DBManager.closePreparedStatement(statement2);
        }

        return true;
    }
    
}

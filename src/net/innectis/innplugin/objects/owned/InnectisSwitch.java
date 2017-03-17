package net.innectis.innplugin.objects.owned;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 *
 * A switch in the IDP which can be owned and linked.
 */
public class InnectisSwitch extends InnectisOwnedObject {

    private static final Map<Integer, InnectisSwitch> switchChache = Collections.synchronizedMap(new HashMap<Integer, InnectisSwitch>());
    private List<Integer> links;

    public InnectisSwitch(Location loc, PlayerCredentials ownerCredentials) {
        this(loc.getWorld(), loc.toVector(), ownerCredentials);
        super.setUpdated(true);
    }

    public InnectisSwitch(World world, Vector loc, PlayerCredentials ownerCredentials) {
        super(world, loc, loc, -1, ownerCredentials, null, null, 0);
        links = new ArrayList<Integer>();
        super.setUpdated(true);
    }

    private InnectisSwitch(Integer id, PlayerCredentials ownerCredentials, World world, Vector loc, long flags, List<Integer> links) {
        super(world, loc, loc, id, ownerCredentials, null, null, flags);
        this.links = links;
    }

    /**
     * Not supported
     */
    @Override
    public List<PlayerCredentials> getMembers() {
        return Collections.emptyList();
    }

    /**
     * Not supported
     */
    @Override
    public List<PlayerCredentials> getOperators() {
        return Collections.emptyList();
    }

    /**
     * Not supported
     */
    @Override
    public void clearMembersAndOperators() {
        // Do Nothing
    }

    /**
     * Not supported
     */
    @Override
    public boolean containsMember(String playerName) {
        return false; // No members
    }

    /**
     * Not supported
     */
    @Override
    public boolean containsOperator(String playerName) {
        return false; // No ops
    }

    /**
     * Not supported
     */
    @Override
    public boolean addMember(PlayerCredentials credentials) {
        return false; // No members
    }

    /**
     * Not supported
     */
    @Override
    public boolean addOperator(PlayerCredentials credentials) {
        return false;
    }

    /**
     * Not supported
     */
    @Override
    public String getMembersString(ChatColor userColor, ChatColor opColor, ChatColor customGroupColor, ChatColor groupColor) {
        return "";
    }

    /**
     * Not supported
     */
    @Override
    public boolean removeMember(String playerName) {
        return super.removeMember(playerName);
    }

    /**
     * Not supported
     */
    @Override
    public boolean removeOperator(String playerName) {
        return super.removeOperator(playerName);
    }

    /**
     * The class of the flags the InnectisSwitch uses.
     * @return
     */
    @Override
    protected Class<? extends FlagType> getEnumClass() {
        return SwitchFlagType.class;
    }

    /**
     * Checks if the given switch is at the given location
     * @param location
     * @return
     */
    @Override
    public boolean isAtLocation(Location location) {
        return super.getPos1().equals(location.toVector());
    }

    /**
     * This will add a link between two switches.
     * This method will do this recursively for all of the connected switches.
     * @param switchid
     */
    public boolean addLink(int switchid) {
        if (!links.contains(switchid) && switchid != super.getId()) {
            // Add link both directions
            links.add(switchid);
            createDBLink(getId(), switchid);

            InnectisSwitch temp = getSwitch(switchid);

            // Update this switch and its links
            updateLinks(temp.links);
            // Share the new link with all switches that are already linked
            for (int id : links) {
                temp = getSwitch(id);
                if (temp != null) {
                    temp.updateLinks(links);
                }
            }

            // Update other switch and their links
            temp.updateLinks(links);
            // Share the new link with all switches that are already linked
            for (int id : temp.links) {
                temp = getSwitch(id);
                if (temp != null) {
                    temp.updateLinks(links);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Updates the link ID this switch has.
     * @param newLinks
     */
    public void updateLinks(List<Integer> newLinks) {
        for (int id : newLinks) {
            if (!links.contains(id) && id != super.getId()) {
                links.add(id);
                createDBLink(id, super.getId());
            }
        }
    }

    /**
     * This will create a link in the database between these switches
     * @param linkid1
     * @param linkid2
     */
    private void createDBLink(int linkid1, int linkid2) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("INSERT IGNORE INTO switches_links (switcha, switchb) VALUES (?,?) ;");
            statement.setInt(1, Math.min(linkid1, linkid2));
            statement.setInt(2, Math.max(linkid2, linkid1));
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Could not link switches in DB!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Checks if the state of the swich is ON
     * @return TRUE if ON
     */
    public boolean getOnState() {
        byte dat = BlockHandler.getBlockData(super.getPos1Location().getBlock());
        return dat > 8;
    }

    /**
     * Toggles this switch and all coppled switches
     */
    public void toggleAll() {
        toggleSwitch(super.getPos1Location());
        toggleLinkedSwitches();
    }

    /**
     * This will toggle all switches this switch is connected to.
     */
    public void toggleLinkedSwitches() {
        InnectisSwitch temp;
        // Share the new link with all switches that are already linked
        for (int id : links) {
            temp = getSwitch(id);
            if (temp != null) {
                toggleSwitch(temp.getPos1Location());
            }
        }
    }

    /**
     * Toggles a switch at a given location.
     * This method does not check if the targetted block is actually a switch!
     *
     * @param vec - The location of a switch
     */
    private static void toggleSwitch(Location loc) {
        Block block = loc.getBlock();
        byte data = BlockHandler.getBlockData(block);
        data += (data > 8 ? -8 : 8);

        BlockHandler.setBlockData(block, data);
    }

    /**
     * This method will try to find a switch at the given location, or create one if not.
     * @param location
     * @param credentials
     * @return the existing or new switch
     */
    public static InnectisSwitch createOrGetSwitch(Location location, PlayerCredentials credentials) {
        InnectisSwitch sw = getSwitch(location);

        if (sw == null) {
            sw = new InnectisSwitch(location, credentials);

            if (!sw.save()) {
                InnPlugin.logError("Could not create switch on location: " + location + " for " + credentials + "!");
            }
        }
        return sw;
    }

    /**
     * Creates a switch at the given location, or gets the switch on the given
     * location and returns the ID of this switch.
     * @param location
     * @param credentials
     * @return
     */
    public static int createOrGetSwitchId(Location location, PlayerCredentials credentials) {
        return createOrGetSwitch(location, credentials).getId();
    }

    /**
     * Gets the switch with the given ID.
     * @param id
     * @return
     */
    public static InnectisSwitch getSwitch(int id) {
        InnectisSwitch swch;
        synchronized (switchChache) {
            swch = switchChache.get(id);
        }
        if (swch == null) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement("SELECT switchid, owner_id, locx, locy, locz, world, flags FROM switches WHERE switchid = ? ");
                statement.setInt(1, id);
                result = statement.executeQuery();

                if (result.next()) {
                    swch = fromResultSet(result);
                }
            } catch (SQLException ex) {
                InnPlugin.logError("Cannot load switch ::ID query", ex);
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }
        return swch;
    }

    /**
     * Looks up all the switches owned by the given player.
     * @param playerName
     * @return List of the switches or null is none.
     */
    public static List<InnectisSwitch> getSwitches(String playerName) {
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT switchid, owner_id, locx, locy, locz, world, flags FROM switches WHERE owner_id = ? ");
            statement.setString(1, credentials.getUniqueId().toString());
            set = statement.executeQuery();

            List<InnectisSwitch> switches = new ArrayList<InnectisSwitch>(set.getFetchSize());
            while (set.next()) {
                switches.add(fromResultSet(set));
            }

            return switches;
        } catch (SQLException ex) {
            InnPlugin.logError("Cant get switch! ", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * This will extract the Innectisswitch from the next row in the resultset.
     * @param result
     * @return
     * @throws SQLException
     */
    private static InnectisSwitch fromResultSet(ResultSet result) throws SQLException {
        World world = Bukkit.getWorld(result.getString("world"));

        // Don't load switches on unloaded worlds
        if (world == null) {
            return null;
        }

        // Check if we got an result
        int switchid = result.getInt("switchid");

        Vector loc = new Vector(result.getInt("locx"), result.getInt("locy"), result.getInt("locz"));
        long flags = result.getLong("flags");

        String ownerIdString = result.getString("owner_id");
        UUID ownerId = UUID.fromString(ownerIdString);
        PlayerCredentials ownerCredentials = null;

        if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
            ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
        } else {
            ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
        }

        // Load the links
        PreparedStatement statement = DBManager.prepareStatement("SELECT switcha, switchb FROM switches_links WHERE switcha = ? OR switchb = ? ");
        statement.setInt(1, switchid);
        statement.setInt(2, switchid);
        ResultSet result2 = statement.executeQuery();

        // Filter the links
        List<Integer> links = new LinkedList<Integer>();
        int idA, idB;
        while (result2.next()) {
            idA = result2.getInt("switcha");
            idB = result2.getInt("switchb");

            if (idA != switchid && !links.contains(idA)) {
                links.add(idA);
            }

            if (idB != switchid && !links.contains(idB)) {
                links.add(idB);
            }
        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        DBManager.closePreparedStatement(statement);
        DBManager.closeResultSet(result2);

        // Create the switch
        InnectisSwitch returnvalue = new InnectisSwitch(switchid, ownerCredentials, world, loc, flags, links);

        // Add to chache
        synchronized (switchChache) {
            switchChache.put(switchid, returnvalue);
        }

        return returnvalue;
    }

    /**
     * Returns the switch on the given location
     * @param location
     * @return
     */
    public static InnectisSwitch getSwitch(Location location) {
        synchronized (switchChache) {
            for (InnectisSwitch sw : switchChache.values()) {
                if (sw.isAtLocation(location)) {
                    return sw;
                }
            }
        }

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT switchid, owner_id, locx, locy, locz, world, flags FROM switches "
                    + " WHERE locx = ? AND locy = ? AND locz = ? AND world = ? ");
            statement.setInt(1, location.getBlockX());
            statement.setInt(2, location.getBlockY());
            statement.setInt(3, location.getBlockZ());
            statement.setString(4, location.getWorld().getName());
            result = statement.executeQuery();

            if (result.next()) {
                return fromResultSet(result);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot load switch ::Loc query", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * This will update the given switch in the database or create it if it doesn't exist.
     */
    @Override
    public boolean save() {
        if (super.getUpdated()) {
            if (super.getId() < 0) {
                return createInDB();
            } else {
                return updateInDB();
            }
        } else {
            return true;
        }
    }

    /**
     * Creates a new switch in the database.
     * This will automaticly fill the ID of the current switch with the auto generated value.
     * @throws SQLException
     */
    private boolean createInDB() {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("INSERT INTO switches "
                    + " (owner_id, locx, locy, locz, world, flags) VALUES (?, ?, ?, ?, ?, ?) ");
            statement.setString(1, super.getOwnerCredentials().getUniqueId().toString());
            statement.setInt(2, super.getPos1().getBlockX());
            statement.setInt(3, super.getPos1().getBlockY());
            statement.setInt(4, super.getPos1().getBlockZ());
            statement.setString(5, super.getWorld().getName());
            statement.setLong(6, super.getFlags());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                super.setId(result.getInt(1));
                super.setUpdated(false);
            } else {
                InnPlugin.logError("New switch not found in the database!");
                return false;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to create switch in database!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * Updates the current switch in the database.
     * This will not affect the id, owner or location values
     * @throws SQLException
     */
    private boolean updateInDB() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE switches SET flags = ? WHERE switchid = ?;");
            statement.setLong(1, super.getFlags());
            statement.setInt(2, super.getId());
            statement.executeUpdate();

            setUpdated(false);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save switch!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * This will remove the link with the switch of the given ID.
     * @param id
     */
    public void unlinkAll() {
        PreparedStatement statement = null;

        try {
            // Remove the DB links
            statement = DBManager.prepareStatement(" DELETE FROM switches_links WHERE switcha = ? OR switchb = ? ");
            statement.setInt(1, super.getId());
            statement.setInt(2, super.getId());
            statement.executeUpdate();

            // Remove the link of other switches
            InnectisSwitch tmp;
            for (int id : links) {
                tmp = getSwitch(id);
                if (tmp != null) {
                    tmp.removeFromLinksList(super.getId());
                }
            }

            // Clear own links
            links.clear();
        } catch (SQLException ex) {
            Logger.getLogger(InnectisSwitch.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * This will remove the given ID from the links list
     * @param id
     * @return true if the ID was in the list.
     */
    private boolean removeFromLinksList(int id) {
        if (links.contains(id)) {
            for (Iterator<Integer> it = links.iterator(); it.hasNext();) {
                Integer currid = it.next();
                if (currid == id) {
                    it.remove();
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Deletes a switch
     */
    public void delete() {
        if (super.getId() <= 0) {
            return;
        }

        PreparedStatement statement = null;

        try {
            // Remove the switch
            statement = DBManager.prepareStatement(" DELETE FROM switches WHERE switchid = ? ");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            // Remove all links
            statement = DBManager.prepareStatement(" DELETE FROM switches_links WHERE switcha = ? OR switchb = ? ");
            statement.setInt(1, super.getId());
            statement.setInt(2, super.getId());
            statement.executeUpdate();

            // Remove from chache
            synchronized (switchChache) {
                switchChache.remove(super.getId());
            }

            // Unlink their links
            InnectisSwitch tmp;
            for (int id : links) {
                tmp = getSwitch(id);
                if (tmp != null) {
                    tmp.removeFromLinksList(super.getId());
                }
            }

            super.setId(-1);
        } catch (SQLException sqle) {
            InnPlugin.logError("Could not remove Switch", sqle);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    @Override
    public OwnedObjectType getType() {
        return OwnedObjectType.SWITCH;
    }
    
}

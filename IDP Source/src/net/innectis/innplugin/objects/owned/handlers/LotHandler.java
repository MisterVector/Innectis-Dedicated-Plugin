package net.innectis.innplugin.objects.owned.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.objects.owned.LotTag;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.system.shop.ChestShopLotDetails;
import net.innectis.innplugin.system.shop.ChestShopLotManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author Lynxy
 *
 * 02-07-2012 - Hret Added some documentation
 */
public final class LotHandler {

    private LotHandler() {
    }
    /**
     * The lotcache. <b>Do not call directly!</b>
     */
    private static HashMap<Integer, InnectisLot> _lots = new HashMap<Integer, InnectisLot>();
    /**
     * A 'virtual' lot that acts as the global parent lot.
     */
    private static InnectisLot mainLot = null;
    /**
     * The name used for assignable lots
     */
    public static final String ASSIGNABLE_LOT_OWNER = "#";

    // Cached lot comparator for ascending lots
    public static Comparator<InnectisLot> lotComparatorAscending = null;

    // Cached lot comparator for descending lots
    public static Comparator<InnectisLot> lotComparatorDescending = null;

    // Cached lot number comparator
    private static Comparator<InnectisLot> lotNumberComparator = null;

    /**
     * Returns the lots that are loaded in the cache as a hashmap.
     *
     * @return the lotcache
     */
    public static synchronized HashMap<Integer, InnectisLot> getLots() {
        return _lots;
    }

    /**
     * Sets that lot cache to the given hashmap
     *
     * @param lots - the new lotcache
     */
    public static synchronized void setLots(HashMap<Integer, InnectisLot> lots) {
        _lots = lots;
    }

    /**
     * Returns the main 'virtual' lot.
     *
     * @return
     */
    public static synchronized InnectisLot getMainLot() {
        return mainLot;
    }

    public static synchronized void setMainLot(InnectisLot lot) {
        mainLot = lot;
    }

    //<editor-fold defaultstate="collapsed" desc="Lot cache loading/saving">
    /**
     * Clears the cache and reload all lots from the database. <br/>
     * <b>Additionally it will check for disabled lots and fix lot nr's.</b>
     */
    public static boolean loadLots() {
        getLots().clear();

        mainLot = new InnectisLot(0, null, null, null, null, null, null, null, null, null, null, 0, 0, null, null, null, "", "", 0, 0, 0, true, false);
        PreparedStatement statement = null;
        ResultSet result = null;
        InnectisLot lot;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM lots WHERE deleted=0 ORDER BY lotid ASC;");
            result = statement.executeQuery();

            //first load all lots into memory
            while (result.next()) {
                lot = getLotFromResultSet(result);

                if (lot != null && !lot.getDeleted()) {
                    getLots().put(result.getInt("lotid"), lot);
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET LOTS FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        //now go through and find sublots
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            setupParentAndSublotsForLot(lot);
        }

        //go through and check max lot count/size for every lot
        cleanLots();

        //sort sublots based on total area, so we can correctly get which lot a player is on
        Collections.sort(mainLot.getSublots(), getLotComparatorDescending());
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            Collections.sort(lot.getSublots(), getLotComparatorDescending());
        }

        fixLotNumbers();
        saveLots(); //save any lot that may have been modified
        return true;
    }

    /**
     * This method constructs an InnectsLot from an resultset object.
     * @param result
     * The resultset to check.
     * @return
     * @throws SQLException
     */
    private static InnectisLot getLotFromResultSet(ResultSet result) throws SQLException {
        // Don't load lots in unloaded worlds
        World world = Bukkit.getWorld(result.getString("world"));

        if (world == null) {
            return null;
        }

        int lotId = result.getInt("lotid");
        Vector point1 = new Vector(result.getInt("x1"), result.getInt("y1"), result.getInt("z1"));
        Vector point2 = new Vector(result.getInt("x2"), result.getInt("y2"), result.getInt("z2"));

        Location spawn = new Location(world, result.getInt("sx"), result.getInt("sy"), result.getInt("sz"), result.getInt("yaw"), 0);

        String ownerIdString = result.getString("owner_id");
        UUID ownerId = UUID.fromString(ownerIdString);
        PlayerCredentials ownerCredentials = null;

        // Owner credentials are different if this lot is assignable
        if (ownerId.equals(Configuration.LOT_ASSIGNABLE_IDENTIFIER)) {
            ownerCredentials = Configuration.LOT_ASSIGNABLE_CREDENTIALS;
        } else if (ownerId.equals(Configuration.OTHER_IDENTIFIER)) {
            ownerCredentials = Configuration.OTHER_CREDENTIALS;
        } else if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
            ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
        } else if (ownerId.equals(Configuration.SYSTEM_IDENTIFIER)) {
            ownerCredentials = Configuration.SYSTEM_CREDENTIALS;
        } else if (ownerId.equals(Configuration.GAME_IDENTIFIER)) {
            ownerCredentials = Configuration.GAME_CREDENTIALS;
        } else {
            ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
        }

        String creatorIdString = result.getString("creator_id");
        UUID creatorId = UUID.fromString(creatorIdString);
        PlayerCredentials creatorCredentials = PlayerCredentialsManager.getByUniqueId(creatorId, true);

        PreparedStatement statement = DBManager.prepareStatement("SELECT player_id, isop FROM lot_members WHERE lotid = ?;");
        statement.setInt(1, lotId);
        ResultSet result2 = statement.executeQuery();

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();

        while (result2.next()) {
            String memberIdString = result2.getString("player_id");
            UUID memberId = UUID.fromString(memberIdString);

            if (memberId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                members.add(Configuration.EVERYONE_CREDENTIALS);
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

        statement = DBManager.prepareStatement("SELECT * FROM lot_safelist WHERE lotid = ?;");
        statement.setInt(1, lotId);
        result2 = statement.executeQuery();
        List<PlayerCredentials> safelist = new ArrayList<PlayerCredentials>();

        while(result2.next()) {
            String playerIdString = result2.getString("player_id");
            UUID playerId = UUID.fromString(playerIdString);
            PlayerCredentials playerCredentials = PlayerCredentialsManager.getByUniqueId(playerId, true);
            safelist.add(playerCredentials);
        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        statement = DBManager.prepareStatement("SELECT player_id, timeout FROM lot_banned WHERE lotid = ?;");
        statement.setInt(1, lotId);
        result2 = statement.executeQuery();
        Map<PlayerCredentials, Long> banned = new HashMap<PlayerCredentials, Long>();

        while (result2.next()) {
            String playerIdString = result2.getString("player_id");
            UUID playerId = UUID.fromString(playerIdString);
            PlayerCredentials playerCredentials = null;

            if (playerId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                playerCredentials = Configuration.EVERYONE_CREDENTIALS;
            } else {
                playerCredentials = PlayerCredentialsManager.getByUniqueId(playerId);
            }

            long timeout = result2.getLong("timeout");

            banned.put(playerCredentials, timeout);
        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        LotTag tag = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM lot_tags WHERE lot_id = ?;");
            statement.setInt(1, lotId);
            result2 = statement.executeQuery();

            if (result2.next()) {
                String tagName = result2.getString("tag");
                boolean publicTag = result2.getBoolean("public_tag");
                tag = new LotTag(tagName, publicTag);
            }
        } catch (SQLException ex) {

        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        return new InnectisLot(lotId, world, point1, point2, spawn, ownerCredentials, result.getString("lotname"), members, operators, banned, safelist, result.getInt("lotnr"), result.getLong("flags"), tag, mainLot, creatorCredentials, result.getString("enter_msg"), result.getString("exit_msg"), result.getLong("last_owner_edit"), result.getLong("last_member_edit"), result.getInt("warp_count"), result.getBoolean("hidden"), result.getBoolean("deleted"));
    }

    /**
     * This method will get a list all owners with lots and fix the lotnumbers so they are ordered correctly.
     */
    private static void fixLotNumbers() {
        List<PlayerCredentials> owners = new ArrayList<PlayerCredentials>();

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            InnectisLot lot = it.next();
            PlayerCredentials ownerCredentials = lot.getOwnerCredentials();

            if (!owners.contains(ownerCredentials)) {
                owners.add(ownerCredentials);
            }
        }
        for (PlayerCredentials pc : owners) {
            fixLotNumber(pc.getName());
        }
        saveLots();
    }

    /**
     * This method will save all lot that have been marked as changed. <br/>
     * Unchanged lot's will be ignored.
     */
    public static void saveLots() {
        InnectisLot lot;
        int exceptionCt = 0;
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            if (lot.getUpdated()) {
                try {
                    lot.save();
                } catch (Exception ex) {
                    InnPlugin.logError("################### ERROR SAVING LOT #" + ChatColor.GOLD + lot.getId() + ChatColor.RED + " ###################", ex);
                    exceptionCt++;
                }
            }
        }

        if (exceptionCt > 0) {
            InnPlugin.getPlugin().sendAdminMessage("savelotexception ", "There were " + exceptionCt + " error(s) saving lots! Please check log.");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Lot disable">
    /**
     * This method will check the given lots and remove lots from guests who havn't used them.
     * Furthermore, it will disable lots of players who got to many or the lots that are to big.
     * @param lots
     */
    private static void cleanLots(Collection<InnectisLot> lots) {
        InnectisLot lot;
        HashMap<PlayerCredentials, Integer> lotCount = new HashMap<PlayerCredentials, Integer>();
        int count;

        Date targetdate = new Date(System.currentTimeMillis() - 1209600000);

        for (Iterator<InnectisLot> it = lots.iterator(); it.hasNext();) {
            lot = it.next();

            // Check if its a top lot (not a sublot)
            if (lot.getParent() == null || lot.getParent() == mainLot) {
                PlayerCredentials ownerCredentials = lot.getOwnerCredentials();
                UUID playerId = ownerCredentials.getUniqueId();
                String name = ownerCredentials.getName();

                PlayerGroup group = PlayerGroup.getGroupOfPlayerById(playerId);

                // Check for inactive lots of guest players who left
                if (lot.getLastOwnerEdit() == 0 && group == PlayerGroup.GUEST) {
                    PlayerSession session = PlayerSession.getSession(playerId, name, InnPlugin.getPlugin(), true);

                    if (session != null) {
                        // Check date
                        if (session.getLastLogin().before(targetdate)) {
                            InnPlugin.logInfo("Lot #" + lot.getId() + " (" + lot.getOwner() + ") set to assignable; player inactive, never editted lot!");
                            lot.setOwner(Configuration.LOT_ASSIGNABLE_CREDENTIALS);
                        }
                    }
                }

                // Dont disable lots whose player names are invalid (I.E. #, %, or ~)
                if (group != PlayerGroup.NONE) {
                    count = (lotCount.get(ownerCredentials) == null ? 0 : lotCount.get(ownerCredentials));

                    if (count + 1 > group.getMaxLots()) {
                        lot.setDisabled(true);
                        InnPlugin.logInfo("Lot #" + lot.getId() + " (" + lot.getOwner() + ") disabled; too many lots!");
                    } else if (lot.getWidth() - 2 >= group.getMaxLotSize() * 1.15 || lot.getLength() - 2 >= group.getMaxLotSize() * 1.15) { //-2 because of how lots are stored, and *1.15 for 15% extra padding
                        lot.setDisabled(true);
                        InnPlugin.logInfo("Lot #" + lot.getId() + " (" + lot.getOwner() + ") disabled; width/length too large! (" + lot.getWidth() + " or " + lot.getLength() + ")>" + group.getMaxLotSize());
                    } else {
                        lotCount.put(ownerCredentials, ++count);
                    }
                }

                // Check all bans.
                lot.reloadBanned();
            }
        }
    }

    /**
     * This method will check all lots and remove lots from guests who havn't used them.
     * Furthermore, it will disable lots of players who got to many or the lots that are to big.
     */
    private static void cleanLots() {
        cleanLots(getLots().values());
    }

    /**
     * This method will check all lots of the given player by ID and remove lots if he's a guests that
     * hasn't used them in a while. It will also check if the size and amount of lots for this
     * player are still accepted.
     */
    public static void cleanLots(String playerName) {
        cleanLots(getLots(playerName));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Lot">
    /**
     * This will return the lot with the given ID.
     * @param lotId
     * @return null if no lot with that ID.
     */
    public static InnectisLot getLot(int lotId) {
        return getLots().get(lotId);
    }

    /**
     * Get the first lot at the given location.
     * <p/>
     * This method will not check for hidden lots.
     * @param location
     * @return
     */
    public static InnectisLot getLot(Location location) {
        return getLot(location, false);
    }

    /**
     * Get the first lot at the given location.
     * @param location
     * @param allowHidden
     * True if this should also check for hidden lots
     * @return
     */
    public static InnectisLot getLot(Location location, boolean allowHidden) {
        List<InnectisLot> lots = getLots(location, allowHidden);
        if (lots == null) {
            return null;
        }

        return lots.get(0);
    }

    /**
     * This method will look for a lot with the given string as the owner aswell as the lotnumber
     * @param playerName
     * The ID of the owner
     * @param lotNumber
     * The lotnumber to look for.
     * @return
     */
    public static InnectisLot getLot(String playerName, int lotNumber) {
        InnectisLot lot = null, partialMatchStart = null, partialMatchAny = null;

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();

            if (lot.getLotNumber() == lotNumber) {

                // Check for exact match
                if (lot.getOwner().equalsIgnoreCase(playerName)) {
                    return lot;
                }

                if (partialMatchStart == null && lot.getOwner().length() >= playerName.length()
                        && lot.getOwner().toLowerCase().substring(0, playerName.length()).equalsIgnoreCase(playerName.toLowerCase())) {
                    partialMatchStart = lot;
                } else if (partialMatchAny == null && lot.getOwner().length() >= playerName.length()
                        && lot.getOwner().toLowerCase().contains(playerName.toLowerCase())) {
                    partialMatchAny = lot;
                }
            }
        }

        if (partialMatchStart != null) {
            return partialMatchStart;
        } else {
            return partialMatchAny;
        }
    }

    /**
     * This will look for a lot with the given owner by ID and the given lotname
     * @param playerName
     * The owner's ID
     * @param lotName
     * The exact name of the lot
     * @return
     */
    public static InnectisLot getLot(String playerName, String lotName) {
        InnectisLot lot = null, partialMatchStart = null, partialMatchAny = null;

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();

            if (lot.getLotName().equalsIgnoreCase(lotName)) {
                // Check exact match
                if (lot.getOwner().equalsIgnoreCase(playerName)) {
                    return lot;
                }

                if (partialMatchStart == null && lot.getOwner().length() >= playerName.length()
                        && lot.getOwner().toLowerCase().substring(0, playerName.length()).equalsIgnoreCase(playerName.toLowerCase())) {
                    partialMatchStart = lot;
                } else if (partialMatchAny == null && lot.getOwner().length() >= playerName.length()
                        && lot.getOwner().toLowerCase().contains(playerName.toLowerCase())) {
                    partialMatchAny = lot;
                }
            }
        }

        if (partialMatchStart != null) {
            return partialMatchStart;
        } else {
            return partialMatchAny;
        }
    }

    /**
     * Tries to find the first lot that matches the given region. <br/>
     * This will not check for the YAxis
     * @param region
     * @return
     */
    public static InnectisLot getLot(IdpWorldRegion region) {
        return getLot(region, false);
    }

    /**
     * Tries to find the first lot that matches the given region.
     * @param region
     * @return
     */
    public static InnectisLot getLot(IdpWorldRegion region, boolean useYAxis) {
        InnectisLot returnLot = null;

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            InnectisLot lot = it.next();

            if (lot.equals(region, useYAxis)) {
                returnLot = lot;
                break;
            }
        }

        return returnLot;
    }

    /**
     * Does a database lookup to find the lot with the given name.
     * </p>
     * It will lookup the lot with the given lotname.
     * If multiple lots have the same name the lot that set the name first will be given.
     * @param lotName
     * @return
     */
    public static InnectisLot getLot(String lotName) {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT lotid FROM lot_names WHERE lotname=? ORDER BY time ASC;");
            statement.setString(1, lotName);
            result = statement.executeQuery();

            while (result.next()) {
                int lotid = result.getInt("lotid");
                InnectisLot lot = getLot(lotid);

                if (lot != null) {
                    return lot;
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Error getting lot based on name ", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Gets all lots by the specified tag
     * @param tag
     * @return
     */
    public static List<InnectisLot> getByTag(String tag) {
        List<InnectisLot> foundLots = new ArrayList<InnectisLot>();

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            InnectisLot lot = it.next();
            LotTag objTag = lot.getTag();

            if (objTag != null && objTag.getTag().equalsIgnoreCase(tag)) {
                foundLots.add(lot);
            }
        }

        return foundLots;
    }

    /**
     * Gets all lots with the specified flags that are not inherited
     * @param flags
     * @return
     */
    public static List<InnectisLot> getLots(List<LotFlagType> flags) {
        List<InnectisLot> foundLots = new ArrayList<InnectisLot>();

        for (InnectisLot lot : _lots.values()) {
            boolean found = true;

            for (LotFlagType flag : flags) {
                if (!lot.isFlagSetNoInheritance(flag)) {
                    found = false;
                    break;
                }
            }

            if (found) {
                foundLots.add(lot);
            }
        }

        return foundLots;
    }

    /**
     * This will return a list with all lots that are owned by the player with the given name.
     * @param playerName
     * @return
     */
    public static List<InnectisLot> getLots(String playerName) {
        List<InnectisLot> lots = new ArrayList<InnectisLot>();
        InnectisLot lot;

        // Find all lots that match the given name (partial or exact).
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            if (lot.getOwner().equalsIgnoreCase(playerName)) {
                lots.add(lot);
            }
        }

        // Sort if bigger then 1
        if (lots.size() > 1) {
            Collections.sort(lots, getLotNumberComparator());
        }

        return lots;
    }

    /**
     * This will do a resursive search for lots that are on the given location.
     * <p/>
     * This will ignore hidden lots (will show non-hidden sublots).
     * @param location
     * The location to check.
     * @return
     * A list of lots that exist on the given location, or null if none.
     */
    public static List<InnectisLot> getLots(Location location) {
        return getLots(location, false);
    }

    /**
     * This will do a search for lots that are on the given location.
     * @param location
     * The location to check.
     * @param returnHidden
     * True if also hidden lots should be added to the list.
     * @return
     * A list of lots that exist on the given location, or null if none.
     */
    public static List<InnectisLot> getLots(Location location, boolean returnHidden) {
        return getLotsRecursive(location, returnHidden, mainLot.getSublots(), new ArrayList<InnectisLot>());
    }

    /**
     * This will do a resursive search for lots that are on the given location.
     * @param location
     * The location to check.
     * @param returnHidden
     * True if also hidden lots should be added to the list.
     * @param checkLots
     * The lots to check.
     * @param foundLots
     * The list that will be used to add lots that have been found.
     * @return
     * A list of lots that exist on the given location, or null if none.
     */
    private static List<InnectisLot> getLotsRecursive(Location location, boolean returnHidden, List<InnectisLot> checkLots, List<InnectisLot> foundLots) {
        // Nothing to check...
        if (checkLots == null) {
            return null;
        }

        // Check all lots
        for (InnectisLot lot : checkLots) {
            // Check if lot in same world, and if the location is inside the lot
            if (lot.getWorld().equals(location.getWorld())
                    && lot.contains(location)) {

                // Check if the lot can be added
                if (returnHidden || !lot.getHidden()) {
                    foundLots.add(lot);
                }

                // Check the sublots (if any)
                if (!lot.getSublots().isEmpty()) {
                    getLotsRecursive(location, returnHidden, lot.getSublots(), foundLots);
                }
            }
        }

        // Return null if nothing was found
        if (foundLots.isEmpty()) {
            return null;
        }

        // Sort
        if (foundLots.size() > 1) {
            Collections.sort(foundLots, getLotComparatorAscending());
        }
        return foundLots;
    }

    /**
     * This will do a resursive search for lots that are on the given region.
     * <p/>
     * This method will ignore hidden lots
     * @param region
     * The region to check.
     * <p/>
     * Note: it will check if the full lot is inside this region.
     * @return
     * A list of lots that exist inside the given region, or null if none.
     */
    public static List<InnectisLot> getLots(IdpWorldRegion region) {
        return getLots(region, false);
    }

    /**
     * This will do a resursive search for lots that are on the given region.
     * @param region
     * The region to check.
     * <p/>
     * Note: it will check if the full lot is inside this region.
     *
     * @param returnHidden
     * True if also hidden lots should be added to the list.
     * @return
     * A list of lots that exist inside the given region, or null if none.
     */
    public static List<InnectisLot> getLots(IdpWorldRegion region, boolean returnHidden) {
        return getLotsRecursive(region, returnHidden, mainLot.getSublots(), new ArrayList<InnectisLot>());
    }

    /**
     * This will do a resursive search for lots that are on the given region.
     * @param region
     * The region to check.
     * <p/>
     * Note: it will check if the full lot is inside this region.
     *
     * @param returnHidden
     * True if also hidden lots should be added to the list.
     * @param checkLots
     * The lots to check.
     * @param foundLots
     * The list that will be used to add lots that have been found.
     * @return
     * A list of lots that exist inside the given region, or null if none.
     */
    private static List<InnectisLot> getLotsRecursive(IdpWorldRegion region, boolean returnHidden, List<InnectisLot> checkLots, List<InnectisLot> foundLots) {
        if (checkLots == null) {
            return null;
        }

        // Check lots
        for (InnectisLot lot : checkLots) {
            if (lot.getWorld().equals(region.getWorld())
                    && region.contains(lot)) {


                if (returnHidden || !lot.getHidden()) {
                    foundLots.add(lot);
                }

                // Check sublots (if any)
                if (!lot.getSublots().isEmpty()) {
                    getLotsRecursive(region, returnHidden, lot.getSublots(), foundLots);
                }
            }
        }

        if (foundLots.isEmpty()) {
            return null;
        }
        if (foundLots.size() > 1) {
            Collections.sort(foundLots, getLotComparatorAscending());
        }
        return foundLots;
    }

    /**
     *
     * @param region
     * @param includeHidden
     * @return
     */
    public static List<InnectisLot> getLotsOverlapping(IdpWorldRegion region, boolean includeHidden) {
        List<InnectisLot> foundLots = new ArrayList<InnectisLot>();
        InnectisLot lot;
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            if (includeHidden) {
                if (lot.getWorld().equals(region.getWorld()) && region.intersects(lot, 0)) {
                    foundLots.add(lot);
                }
            } else if (lot.getWorld().equals(region.getWorld()) && !lot.getHidden() && region.intersects(lot, 0)) {
                foundLots.add(lot);
            }
        }
        if (foundLots.isEmpty()) {
            return null;
        }
        if (foundLots.size() > 1) {
            Collections.sort(foundLots, getLotComparatorAscending());
        }
        return foundLots;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Lot removal">
    /**
     * Removes the lot with the given id from the database.
     * <p/>
     * This will not remove sublots that are not owned by the owner of the removed lot.
     * @param lotId
     * @return
     * @throws SQLException
     */
    public static int removeLot(int lotId) throws SQLException {
        return removeLot(getLot(lotId));
    }

    /**
     * Removes the given lot from the database.
     * <p/>
     * This will not remove sublots that are not owned by the owner of the removed lot.
     * @param lot
     * @return
     * @throws SQLException
     */
    public static int removeLot(InnectisLot lot) throws SQLException {
        return removeLot(lot, false);
    }

    /**
     * This will remove the given lot from the database
     * @param lot
     * The lot to remove
     * @param override
     * This boolean checks if the sublots not owned by the owner of the remvoed lot should be removed.
     * <p/>
     * So when true, all sublots get removed. When false, only lots also owned by the lotowner will
     * be removed.
     * @return
     * @throws SQLException
     */
    public static int removeLot(InnectisLot lot, boolean override) throws SQLException {
        if (lot != null) {
            int remCount = 0;
            List<InnectisLot> sublots = new ArrayList<InnectisLot>(lot.getSublots());
            for (InnectisLot sublot : sublots) {
                if (lot.getOwner().equalsIgnoreCase(sublot.getOwner()) || override) {
                    remCount += removeLot(sublot, override);
                } else {
                    //getLot(sublot.getId()).setParent(null);
                    sublot.setParent(null);
                    // No need to save, it will auto set it right on startup
                }
            }
            lot.setDeleted(true);
            lot.save();

            lot.setParent(mainLot);
            mainLot.getSublots().remove(lot);
            getLots().remove(lot.getId());

            ChestShopLotDetails details = ChestShopLotManager.getChestShopLot(lot.getId());

            if (details != null) {
                ChestShopLotManager.deleteChestShopLot(details);
            }

            remCount++;

            List<InnectisLot> lots = getLots(lot.getOwner());
            if (!lots.isEmpty()) {
                for (InnectisLot l : lots) {
                    if (l.getLotNumber() > lot.getLotNumber()) {
                        l.setLotNumber(l.getLotNumber() - 1);
                        l.save();
                    }
                }
            }

            return remCount;
        }

        return 0;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Lot Assigning">
    /**
     * Randomly assigns a lot the the given player
     *
     * @param ownerCredentials credentials of the player the lot will be assigned to
     * @return InnectisLot - the newly created lot
     * @throws SQLException - Is thrown when something happens when saving the
     * lot in the database.
     */
    public static InnectisLot assignLot(PlayerCredentials ownerCredentials) throws SQLException {
        return assignLot(ownerCredentials, getLot(ASSIGNABLE_LOT_OWNER, 1));
    }

    /**
     * Assigns the given lot the player with the given name
     *
     * @param ownerCredentials - name of the player
     * @param lot - The lot that needs to be assigned to the player
     * @return InnectisLot - the newly created lot
     * @throws SQLException - Is thrown when something happens when saving the
     * lot in the database.
     */
    public static InnectisLot assignLot(PlayerCredentials ownerCredentials, InnectisLot lot) throws SQLException {
        if (lot == null) {
            return null;
        }

        String name = ownerCredentials.getName();
        lot.setOwner(ownerCredentials);
        lot.setLotNumber(0);
        lot.save();
        InnPlugin.logInfo("Assigning lot #" + lot.getId() + " to " + name);
        LotHandler.cleanLots(ownerCredentials.getName());

        fixLotNumber(ASSIGNABLE_LOT_OWNER); //reorder lotnr for available lots

        IdpPlayer target = InnPlugin.getPlugin().getPlayer(ownerCredentials.getUniqueId());

        if (target != null) {
            name = target.getName();
            target.printInfo("You have been assigned a lot!");
            target.printInfo("Type " + ChatColor.AQUA + "/mylot" + (lot.getLotNumber() > 1 ? " " + lot.getLotNumber() : "") + ChatColor.GREEN + " to go to it.");
            target.teleport(lot.getSpawn(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        }

        InnPlugin.getPlugin().sendModeratorMessage(null, "Lot #" + lot.getId() + " assigned to " + name);

        return lot;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get lotcount">
    /**
     * Gets the amount of lots the given player has. <b>This will not count
     * sublots.</b>
     *
     * @param playerName
     * @return The amount of lots the given player by ID has
     */
    public static int getLotCount(String playerName) {
        return getLotCount(playerName, 0);
    }

    /**
     * Gets the amount of lots the given player by ID has. <b>This will not count
     * sublots.</b>
     *
     * @param playerName
     * @param sublotCount - acts as an output parameter. This value will be set
     * to the amount of sublots the player by ID has. Doesn't matter if the 'name' is
     * the owner of the parent or not.
     * @return The amount of lots the given name has
     */
    public static int getLotCount(String playerName, int sublotCount) {
        int count = 0;
        sublotCount = 0;

        List<InnectisLot> lots = getLots(playerName);

        if (!lots.isEmpty()) {
            for (InnectisLot lot : lots) {
                if (lot.getParent() == null || lot.getParent().getHidden()) {
                    count++;
                } else {
                    sublotCount++;
                }
            }
        }
        return count;
    }

    //</editor-fold>
    /**
     * This will find set the parent lot of this lot and set the sublots correctly.
     * @param lot
     */
    private static void setupParentAndSublotsForLot(InnectisLot lot) {
        List<InnectisLot> lots;

        //find parent
        lots = getLots(lot.getCenter(), true);
        if (lots != null) {
            for (InnectisLot l : lots) {
                if (lot.setParent(l)) { //isSublotWithinLot() performed in here
                    break;
                }
            }
        }

        // Find sublots
        lots = getLotsRecursive(lot, true, new ArrayList(getLots().values()), new ArrayList()); //get lots within this lot
        if (lots != null) {
            for (InnectisLot l : lots) {
                if (l.getParent() == null || l.getParent().getArea() > lot.getArea()) {
                    //the existing parent (if any) has a larger area than new found parent,
                    //so we will use the new one instead
                    l.setParent(lot); //isSublotWithinLot() performed in here
                }
            }
        }
    }

    /**
     * Checks if the child lot is within the region of the given parentlot
     * @param child
     * @param parent
     * @return
     */
    public static boolean isSublotWithinLot(InnectisLot child, InnectisLot parent) {
        if (parent == mainLot) {
            return true;
        }
        if (parent != null && child != null) {
            return parent.contains(child);
        }
        return false;
    }

    /**
     * This will recount the lotnumbers and set the correctly.
     * @param playerName
     * The owner of the lots
     */
    private static void fixLotNumber(String playerName) {
        int curI = 1;

        //number non-dynamic primary lots first
        curI = fixLotNumbersByType(curI, playerName, false, false);

        //number dynamic primary lots second
        curI = fixLotNumbersByType(curI, playerName, false, true);

        //number non-dynamic sublots third
        curI = fixLotNumbersByType(curI, playerName, true, false);

        //number dynamic sublots fourth
        fixLotNumbersByType(curI, playerName, true, true);
    }

    /**
     * Fixes lot numbers by type, whether the lots are sublots
     * or if the worlds are loaded dynamically
     * @param sublots
     * @param dynamic
     */
    private static int fixLotNumbersByType(int curI, String playerName, boolean sublots, boolean dynamic) {
        List<InnectisLot> lots = getLots(playerName);

            for (int i = 0; i < lots.size(); i++) {
                InnectisLot lot = lots.get(i);
                boolean prerequisite = (!sublots ? lot.getParent() == null || lot.getParent().getHidden()
                        : lot.getParentNotHidden() != null);

                if (prerequisite) {
                    prerequisite = (!dynamic || lot.isDynamicLot());
                }

                if (prerequisite) {
                    if (lot.getLotNumber() != curI) {
                        lot.setLotNumber(curI);
                    }

                    curI++;
                }
            }

        return curI;
    }

    /**
     * This will create a lot on the given location.
     * It will auto check if the created lot needs to be a shoplot
     * @param world
     * @param point1
     * @param point2
     * @param ownerCredentials
     * @param creatorCredentials
     * @return
     * @throws SQLException
     */
    public static InnectisLot addLot(World world, Vector point1, Vector point2, PlayerCredentials ownerCredentials, PlayerCredentials creatorCredentials) throws SQLException {
        InnectisLot lot = new InnectisLot(world, point1, point2, ownerCredentials, creatorCredentials, mainLot, null);

        lot.save();
        LotHandler.getLots().put(lot.getId(), lot);
        setupParentAndSublotsForLot(lot);
        return lot;
    }

    /**
     * Changes the lotnr of the lot with given ID.<br/> The lotnr will change
     * the way its shown in /listlots and will define the order in which /mylot
     * finds it.
     *
     * @param lotid - The ID of the lot that needs to be changed
     * @param lotnr - The NR it needs to be changed to
     * @return boolean that shows if it has succeeded or failed.
     *
     */
    public static boolean editLotNr(int lotid, int lotnr) {
        InnectisLot targetlot = getLot(lotid);
        if (targetlot == null) {
            return false;
        }

        List<InnectisLot> foundLots = new ArrayList<InnectisLot>();

        InnectisLot lot;
        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            lot = it.next();
            if (lot.getOwner().equalsIgnoreCase(targetlot.getOwner())) {
                if (lot != targetlot) {
                    if (lot.getLotNumber() >= lotnr) {
                        foundLots.add(lot);
                    }
                }
            }
        }

        Collections.sort(foundLots, getLotNumberComparator());

        int lastNr = lotnr;
        List<InnectisLot> fixLots = new ArrayList<InnectisLot>();
        if (foundLots.isEmpty()) {
            targetlot.setLotNumber(lotnr);
            saveLots();
        } else {
            for (InnectisLot l : foundLots) {
                if (l.getLotNumber() > lastNr + 1) { //gap
                    break;
                }
                lastNr = l.getLotNumber();
                l.setLotNumber(0 - (l.getLotNumber() + 1));
                fixLots.add((l));
            }

            saveLots();
            targetlot.setLotNumber(lotnr);

            if (!targetlot.save()) {
                InnPlugin.logError("Cannot save lot with lotid #" + lotid + " however should be picked up later! (Lothandler::EditLotNr)");
            }

            for (InnectisLot l : fixLots) {
                l.setLotNumber(Math.abs(l.getLotNumber()));
            }

            saveLots();
        }

        return true;
    }

    /**
     * Gets a map of all lots that have tags
     * @return
     */
    public static HashMap<String, List<InnectisLot>> getAllTaggedLots() {
        HashMap<String, List<InnectisLot>> taggedLots = new HashMap<String, List<InnectisLot>>();

        for (Iterator<InnectisLot> it = getLots().values().iterator(); it.hasNext();) {
            InnectisLot lot = it.next();
            LotTag tag = lot.getTag();

            if (tag != null) {
                String tagName = tag.getTag();
                List<InnectisLot> lots = taggedLots.get(tagName);

                if (lots == null) {
                    lots = new ArrayList<InnectisLot>();
                    taggedLots.put(tagName, lots);
                }

                lots.add(lot);
            }
        }

        return taggedLots;
    }

    /**
     * Gets the cached comparator for ascending lots
     * @return
     */
    private static Comparator<InnectisLot> getLotComparatorAscending() {
        if (lotComparatorAscending == null) {
            lotComparatorAscending = new Comparator<InnectisLot>() {
                public int compare(InnectisLot lot1, InnectisLot lot2) {
                    int area1 = Math.abs(lot1.getPos1().getBlockX() - lot1.getPos2().getBlockX())
                            * Math.abs(lot1.getPos1().getBlockY() - lot1.getPos2().getBlockY())
                            * Math.abs(lot1.getPos1().getBlockZ() - lot1.getPos2().getBlockZ());
                    int area2 = Math.abs(lot2.getPos1().getBlockX() - lot2.getPos2().getBlockX())
                            * Math.abs(lot2.getPos1().getBlockY() - lot2.getPos2().getBlockY())
                            * Math.abs(lot2.getPos1().getBlockZ() - lot2.getPos2().getBlockZ());
                    return (area1 - area2);
                }
            };
        }

        return lotComparatorAscending;
    }

    /**
     * Gets the cached comparator for descending lots
     * @return
     */
    private static Comparator<InnectisLot> getLotComparatorDescending() {
        if (lotComparatorDescending == null) {
            lotComparatorDescending = new Comparator<InnectisLot>() {
                public int compare(InnectisLot lot1, InnectisLot lot2) {
                    int area1 = Math.abs(lot1.getPos1().getBlockX() - lot1.getPos2().getBlockX())
                            * Math.abs(lot1.getPos1().getBlockY() - lot1.getPos2().getBlockY())
                            * Math.abs(lot1.getPos1().getBlockZ() - lot1.getPos2().getBlockZ());
                    int area2 = Math.abs(lot2.getPos1().getBlockX() - lot2.getPos2().getBlockX())
                            * Math.abs(lot2.getPos1().getBlockY() - lot2.getPos2().getBlockY())
                            * Math.abs(lot2.getPos1().getBlockZ() - lot2.getPos2().getBlockZ());
                    return (area2 - area1);
                }
            };
        }

        return lotComparatorDescending;
    }

    /**
     * Gets the cached comparator for lot numbers
     * @return
     */
    private static Comparator<InnectisLot> getLotNumberComparator() {
        if (lotNumberComparator == null) {
            lotNumberComparator = new Comparator<InnectisLot>() {
                public int compare(InnectisLot lot1, InnectisLot lot2) {
                    return (lot1.getLotNumber() - lot2.getLotNumber());
                }
            };
        }

        return lotNumberComparator;
    }

}

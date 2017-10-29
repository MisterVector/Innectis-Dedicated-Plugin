package net.innectis.innplugin.objects.owned;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpSpawnFinder;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.LotEnterLeaveTime;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.util.DateUtil;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * @author Lynxy
 */
public class InnectisLot extends InnectisOwnedObject {

    private int lotnr;
    private String lotName;
    private int timesWarpUsed;
    private Map<PlayerCredentials, Long> banned;
    private List<PlayerCredentials> safelist;
    private Location spawn;
    private InnectisLot parent;
    private PlayerCredentials creatorCredentials;
    private String enterMsg;
    private String exitMsg;
    private long lastOwnerEdit;
    private long lastMemberEdit;
    private boolean hidden;
    private boolean deleted;
    private boolean disabled;
    private List<InnectisLot> sublots;
    private LotTag tag;

    public InnectisLot(World world, Vector point1, Vector point2, PlayerCredentials ownerCredentials, PlayerCredentials creatorCredentials, InnectisLot parent, LotTag tag) {
        this(-1, world, point1, point2, point1, ownerCredentials, null, null, new HashMap<PlayerCredentials, Long>(), new ArrayList<PlayerCredentials>(), 0, 0, tag, parent, creatorCredentials, 0, 0);
    }

    public InnectisLot(int id, World world, Vector point1, Vector point2, Vector spawn, PlayerCredentials ownerCredentials, List<PlayerCredentials> members, List<PlayerCredentials> operators, Map<PlayerCredentials, Long> banned, List<PlayerCredentials> safelist, int lotnr, long flags, LotTag tag, InnectisLot parent, PlayerCredentials creatorCredentials, long lastOwnerEdit, long lastMemberEdit) {
        this(id, world, point1, point2, new Location(world, point1.getBlockX(), point1.getBlockY(), point1.getBlockZ()), ownerCredentials, "", members, operators, banned, safelist, lotnr, flags, tag, parent, creatorCredentials, "", "", lastOwnerEdit, lastMemberEdit, 0, false, false);
    }

    public InnectisLot(int id, World world, Vector point1, Vector point2, Location spawn, PlayerCredentials ownerCredentials, String lotname, List<PlayerCredentials> members, List<PlayerCredentials> operators, Map<PlayerCredentials, Long> banned, List<PlayerCredentials> safelist, int lotnr, long flags, LotTag tag, InnectisLot parent, PlayerCredentials creatorCredentials, String enterMsg, String exitMsg, long lastOwnerEdit, long lastMemberEdit, int timesWarpUsed, boolean hidden, boolean deleted) {
        super(world, point1, point2, id, ownerCredentials, members, operators, flags);
        this.lotName = lotname;
        this.banned = banned;
        this.safelist = safelist;
        this.spawn = spawn;
        this.lotnr = lotnr;
        this.sublots = new ArrayList<InnectisLot>();
        this.creatorCredentials = creatorCredentials;
        this.enterMsg = enterMsg;
        this.exitMsg = exitMsg;
        this.lastOwnerEdit = lastOwnerEdit;
        this.lastMemberEdit = lastMemberEdit;
        this.timesWarpUsed = timesWarpUsed;
        this.hidden = hidden;
        this.deleted = deleted;
        this.disabled = false;
        this.tag = tag;
        setParent(parent);
    }

    /**
     * Checks if this lot was loaded with a dynamic world
     * @return
     */
    public boolean isDynamicLot() {
        IdpWorld world = IdpWorldFactory.getWorld(super.getWorld().getName());
        return (world.getWorldType() == IdpWorldType.DYNAMIC);
    }

    /**
     * Returns true if location is within the area of this object
     *
     * @param location
     */
    @Override
    public boolean isAtLocation(Location location) {
        return contains(location);
    }

    @Override
    protected Class<? extends FlagType> getEnumClass() {
        return LotFlagType.class;
    }

    /**
     * Returns true if this lot or any of its parents have a flag bit set. If
     * both child and parent lot have the flag set, it is negated.
     *
     * @param flag
     * @return
     */
    @Override
    public boolean isFlagSet(FlagType flag) {
        InnectisLot curLot = this;
        boolean isSet = false;

        while (curLot != null && curLot != LotHandler.getMainLot()) {
            if (curLot.isFlagSetNoInheritance(flag)) {
                isSet = !isSet;
            }

            curLot = curLot.getParentAllowHidden();
        }

        return isSet;
    }

    public int getLotNumber() {
        return lotnr;
    }

    public void setLotNumber(int lotnr) {
        this.lotnr = lotnr;
        super.setUpdated(true);
    }

    public String getLotName() {
        return lotName;
    }

    /**
     * Instantly changes the lot name in the database, as well as in memory
     *
     * @param lotName
     */
    public boolean setLotName(String lotName) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM lot_names WHERE lotid=?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            if (!lotName.equalsIgnoreCase("")) {
                statement = DBManager.prepareStatement("INSERT INTO lot_names (lotname, lotid, time) VALUES (?, ?, ?);");
                statement.setString(1, lotName);
                statement.setInt(2, super.getId());
                statement.setLong(3, Calendar.getInstance().getTimeInMillis());
                statement.executeUpdate();
            }

            this.lotName = lotName;
            super.setUpdated(true);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to set name of lot #" + getId() + "!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * Returns a List of banned usernames
     */
    public List<String> getBannedList() {
        reloadBanned();

        List<String> bannedPlayers = new ArrayList<String>(banned.size());

        for (PlayerCredentials pc : banned.keySet()) {
            bannedPlayers.add(pc.getName());
        }

        return bannedPlayers;
    }

    /**
     * Returns a String of banned usernames, seperated by a space. If banned
     * list is empty, returns "none"
     */
    public String getBannedString(ChatColor colour) {
        StringBuilder sb = new StringBuilder("" + colour);

        for (PlayerCredentials pc : banned.keySet()) {
            long timeout = banned.get(pc) - System.currentTimeMillis();
            String name = pc.getName();

            if (timeout > 0) {
                sb.append("[").append(name).append(":").append(DateUtil.getTimeString(timeout, false)).append(colour).append("]");
            } else {
                sb.append(name);
            }

            sb.append(", ");
        }

        if (sb.length() == 0) {
            return "none";
        }

        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Adds the credentials of the specified player to this lot's safelist
     *
     * @param credentials
     * @return true if added, false if already added
     */
    public boolean addSafelist(PlayerCredentials credentials) {
        if (containsSafelist(credentials.getName())) {
            return false;
        }

        if (isBanned(credentials.getName())) {
            banned.remove(credentials);
        }

        safelist.add(credentials);
        super.setUpdated(true);
        return true;
    }

    /**
     * Removes the specified player from the safelist
     *
     * @param playerName
     * @return true if removed, false otherwise
     */
    public boolean removeSafelist(String playerName) {
        boolean removed = false;

        for (Iterator<PlayerCredentials> it = safelist.iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            super.setUpdated(true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player represented by their ID is on this lot's safelist
     *
     * @param playerName
     * @return
     */
    public boolean containsSafelist(String playerName) {
        for (PlayerCredentials pc : safelist) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Clears the safelist
     */
    public void clearSafelist() {
        safelist.clear();
        super.setUpdated(true);
    }

    /**
     * Returns an unmodifiable list of the safelist
     *
     * @return
     */
    public List<PlayerCredentials> getSafelist() {
        return Collections.unmodifiableList(safelist);
    }

    /**
     * Sets the safelist of this lot
     * @param safelist
     */
    public void setSafelist(List<PlayerCredentials> safelist) {
        this.safelist = safelist;
    }

    /**
     * Returns the entire safelist as a string
     *
     * @return
     */
    public String getSafelistString() {
        StringBuilder sb = new StringBuilder();
        int idx = 0;

        for (PlayerCredentials pc : safelist) {
            String name = pc.getName();

            if (idx > 0) {
                sb.append(", ");
            }

            sb.append(name);
            idx++;
        }

        return sb.toString();
    }

    /**
     * Adds the credentials of a player to this lot's ban list If they are
     * already banned, returns false
     *
     * @param credentials
     */
    public boolean banUser(PlayerCredentials credentials) {
        return banUser(credentials, 0L);
    }

    /**
     * Adds a username to the banned list until the entered time and returns
     * true. If username is already banned, returns false. If time is put as '0'
     * then perm.
     *
     * @param credentials
     * @param timeout
     * @return
     */
    public boolean banUser(PlayerCredentials credentials, long timeout) {
        String name = credentials.getName();

        if (isBanned(name)) {
            return false;
        }

        if (containsSafelist(name)) {
            removeSafelist(name);
        }

        if (super.containsOperator(name)) {
            super.removeOperator(name);
        }

        if (super.containsMember(name)) {
            super.removeMember(name);
        }

        banned.put(credentials, timeout);
        super.setUpdated(true);
        return true;
    }

    /**
     * Removes the player from this lot's ban list not banned
     *
     * @param playerName
     */
    public boolean unbanUser(String playerName) {
        boolean removed = false;

        for (Iterator<PlayerCredentials> it = banned.keySet().iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            super.setUpdated(true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if a player is banned, false otherwise
     *
     * @param playerName
     * @return
     */
    public boolean isBanned(String playerName) {
        for (PlayerCredentials pc : banned.keySet()) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Clears the banned user list of this lot
     */
    public void clearBanned() {
        banned.clear();
        super.setUpdated(true);
    }

    /**
     * Checks through all lot bans, and removes them if they have timed out.
     */
    public void reloadBanned() {
        for (PlayerCredentials pc : banned.keySet()) {
            long banTime = banned.get(pc);

            if (banTime > 0 && banTime < System.currentTimeMillis()) {
                String name = pc.getName();

                // Log auto-unbans.
                IdpPlayer player = InnPlugin.getPlugin().getPlayer(getOwner(), true);

                if (player != null) {
                    player.printInfo(name + "'s ban has automatically expired on lot #" + getId());
                }

                InnPlugin.logInfo(name + "'s ban has automatically expired on lot #" + getId() + " (" + getOwner() + ")");
                banned.remove(pc);
            }
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        super.setUpdated(true);
    }

    /**
     * Returns the parent lot. If no parent, or parent is hidden, returns null
     */
    public InnectisLot getParent() {
        if (parent == LotHandler.getMainLot()
                || (parent != null && parent.getHidden())) {
            return null;
        }

        return parent;
    }

    /**
     * Returns the parent lot. If no parent, returns null
     */
    public InnectisLot getParentAllowHidden() {
        if (parent == LotHandler.getMainLot()) {
            return null;
        }

        return parent;
    }

    /**
     * Returns the parent lot. If no parent, or parent is hidden, returns null
     */
    public InnectisLot getParentNotHidden() {
        InnectisLot par = getParent();

        if (par == null) {
            return null;
        }

        if (par.getHidden()) {
            return null;
        }

        return par;
    }

    /**
     * If parent is null, sets the parent to the main lot. Otherwise checks to
     * make sure this lot is within the new parent, removes this object from the
     * current parent (if any), and sets the new parent. Returns true if all
     * went well, false if not
     *
     * @param parent
     */
    public final boolean setParent(InnectisLot parent) {
        if (parent == null) {
            parent = LotHandler.getMainLot();

            if (parent == null) {
                return false;
            }
        }

        if (parent == this) {
            return false;
        }

        if (!LotHandler.isSublotWithinLot(this, parent)) {
            return false;
        }

        if (this.parent != null) {
            this.parent.removeSublot(this);
            this.parent = null;
        }

        if (parent.addSublot(this)) {
            this.parent = parent;
        }

        super.setUpdated(true);
        return true;
    }

    /**
     * Returns the MAIN lot that this lot is in, if any. Will not return
     * sublots, and will not return the virtual main lot
     */
    public InnectisLot getParentTop() {
        return getParentTop(this);
    }

    private InnectisLot getParentTop(InnectisLot checkLot) {
        if (checkLot.parent == LotHandler.getMainLot()
                || checkLot.parent == null
                || (checkLot.parent != null && checkLot.parent.getHidden())) {
            return checkLot;
        }

        return getParentTop(checkLot.parent);
    }

    public List<InnectisLot> getSublots() {
        if (sublots.contains(this)) { //sanity check
            sublots.remove(this);
        }
        return sublots;
    }

    /**
     * If the new sublot isn't already an existing sublot, adds it and returns
     * true
     *
     * @param sublot
     */
    private boolean addSublot(InnectisLot sublot) {
        if (this != sublot && !sublots.contains(sublot)) {
            if (LotHandler.isSublotWithinLot(sublot, this)) {
                sublots.add(sublot);
                return true;
            }
        }
        return false;
    }

    /**
     * If the sublot exists, removes it and returns true
     *
     * @param sublot
     */
    private boolean removeSublot(InnectisLot sublot) {
        if (sublots.contains(sublot)) {
            sublots.remove(sublot);
            return true;
        }
        return false;
    }

    public PlayerCredentials getCreatorCredentials() {
        return creatorCredentials;
    }

    public String getCreator() {
        return creatorCredentials.getName();
    }

    public void setCreator(PlayerCredentials creatorCredentials) {
        this.creatorCredentials = creatorCredentials;
        super.setUpdated(true);
    }

    public long getLastOwnerEdit() {
        return lastOwnerEdit;
    }

    public String getLastOwnerEditString() {
        if (lastOwnerEdit == 0) {
            return "Never";
        }

        long timeDiff = (System.currentTimeMillis() - lastOwnerEdit) / 1000;
        return DateUtil.getTimeDifferenceString(timeDiff, DateUtil.DEFAULT_CONSTANTS) + " ago";
    }

    public void setLastOwnerEdit(long lastEdit) {
        lastOwnerEdit = lastEdit;
        super.setUpdated(true);
    }

    public long getLastMemberEdit() {
        return lastMemberEdit;
    }

    public String getLastMemberEditString() {
        if (lastMemberEdit == 0) {
            return "Never";
        }

        long timeDiff = (System.currentTimeMillis() - lastMemberEdit) / 1000;
        return DateUtil.getTimeDifferenceString(timeDiff, DateUtil.DEFAULT_CONSTANTS) + " ago";
    }

    public void setLastMemberEdit(long lastEdit) {
        lastMemberEdit = lastEdit;
        super.setUpdated(true);
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        super.setUpdated(true);
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        super.setUpdated(true);
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        //setUpdated(true); //does not save to DB! don't uncomment this
    }

    /**
     * Checks if this lot is assignable
     *
     * @return
     */
    public boolean isAssignable() {
        return getOwner().equals("#");
    }

    /**
     * Checks to see if this lot's region is equivalent to the specified region
     *
     * @param region
     * @param yaxis whether the region to compare is a Y-axis lot
     * @return
     */
    public boolean equals(IdpWorldRegion region, boolean yaxis) {
        if (super.getLowestX() == region.getLowestX()
                && super.getLowestZ() == region.getLowestZ()
                && super.getHighestX() == region.getHighestX()
                && super.getHighestZ() == region.getHighestZ()
                && (!yaxis || (super.getLowestY() == region.getLowestY()
                && super.getHighestY() == region.getHighestY()))
                && super.getWorld().getName().equalsIgnoreCase(region.getWorld().getName())) {
            return true;
        }

        return false;
    }

    /**
     * Simulates a leave event for all users on the lot
     */
    public void simulateLeave() {
        for (IdpPlayer p : InnPlugin.getPlugin().getOnlinePlayers()) {
            InnectisLot tempLot = LotHandler.getLot(p.getLocation());

            if (tempLot != null && tempLot == this) {
                onLeave(p, this, false);
            }
        }
    }

    /**
     * Simulates a join event for all users on the lot
     */
    public void simulateJoin() {
        for (IdpPlayer p : InnPlugin.getPlugin().getOnlinePlayers()) {
            InnectisLot tempLot = LotHandler.getLot(p.getLocation());

            if (tempLot != null && tempLot == this) {
                onEnter(p, this, false);
            }
        }
    }

    /**
     * Sets the flag on the lot, then sends leave/enter events for every player
     * on the lot
     *
     * @param flag
     * @param disable
     */
    public void setLotFlag(FlagType flag, boolean disable) {
        //before we update the flag, we need ot simulate every user on the lot leaving it
        simulateLeave();

        setFlag(flag.getFlagBit(), disable);

        //now that lot exit event has occured we can simulate player re-entering the lot with the updated flag
        simulateJoin();
    }

    /**
     * Gets the next highest lotnr available for the current owner. Does not
     * take gaps into consideration
     *
     * @throws SQLException
     */
    private int getFreeLotNumber() throws SQLException {
        int highestLotNr = 0;
        List<InnectisLot> lots = LotHandler.getLots(getOwner());

        if (lots.isEmpty()) {
            return 1;
        }

        for (InnectisLot lot : lots) {
            if (lot.getLotNumber() > highestLotNr) {
                highestLotNr = lot.getLotNumber();
            }
        }

        return highestLotNr + 1;
    }

    private boolean createLotInDB() {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            if (lotnr == 0) {
                lotnr = getFreeLotNumber();
            }

            statement = DBManager.prepareStatementWithAutoGeneratedKeys("INSERT INTO lots "
                    + "(owner_id, lotnr, world, lotname, x1, y1, z1, x2, y2, z2, sx, sy, sz, yaw, flags, creator_id, enter_msg, exit_msg, last_owner_edit, last_member_edit, warp_count, hidden, deleted)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, super.getOwnerCredentials().getUniqueId().toString());
            statement.setInt(2, lotnr);
            statement.setString(3, super.getWorld().getName());
            statement.setString(4, lotName);
            statement.setInt(5, super.getPos1().getBlockX());
            statement.setInt(6, super.getPos1().getBlockY());
            statement.setInt(7, super.getPos1().getBlockZ());
            statement.setInt(8, super.getPos2().getBlockX());
            statement.setInt(9, super.getPos2().getBlockY());
            statement.setInt(10, super.getPos2().getBlockZ());
            statement.setInt(11, spawn.getBlockX());
            statement.setInt(12, spawn.getBlockY());
            statement.setInt(13, spawn.getBlockZ());
            statement.setInt(14, Math.round(spawn.getYaw()));
            statement.setLong(15, super.getFlags());
            statement.setString(16, creatorCredentials.getUniqueId().toString());
            statement.setString(17, enterMsg);
            statement.setString(18, exitMsg);
            statement.setLong(19, lastOwnerEdit);
            statement.setLong(20, lastMemberEdit);
            statement.setInt(21, timesWarpUsed);
            statement.setBoolean(22, hidden);
            statement.setBoolean(23, deleted);
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                super.setId(result.getInt(1));
                super.setUpdated(false);
            } else {
                InnPlugin.logError("New lot not found in the database!");
                return false;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to create lot!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    @Override
    public boolean save() {
        if (super.getId() == 0) { //main lot (virtual)
            super.setUpdated(false);
            return true;
        }

        if (super.getId() == -1) {
            return createLotInDB();
        }

        PreparedStatement statement = null;

        try {
            if (lotnr == 0) {
                lotnr = getFreeLotNumber();
            }

            statement = DBManager.prepareStatement("UPDATE lots SET "
                    + "owner_id = ?, lotnr = ?, world = ?, "
                    + "lotname = ?, x1 = ?, y1 = ?, "
                    + "z1 = ?, x2 = ?, y2 = ?, "
                    + "z2 = ?, sx = ?, sy = ?, "
                    + "sz = ?, yaw = ?, flags = ?, "
                    + "creator_id = ?, enter_msg = ?, exit_msg = ?, "
                    + "last_owner_edit = ?, last_member_edit = ?, warp_count = ?, "
                    + "hidden = ?, deleted = ? WHERE lotid = ?;");
            statement.setString(1, super.getOwnerCredentials().getUniqueId().toString());
            statement.setInt(2, lotnr);
            statement.setString(3, super.getWorld().getName());
            statement.setString(4, lotName);
            statement.setInt(5, super.getPos1().getBlockX());
            statement.setInt(6, super.getPos1().getBlockY());
            statement.setInt(7, super.getPos1().getBlockZ());
            statement.setInt(8, super.getPos2().getBlockX());
            statement.setInt(9, super.getPos2().getBlockY());
            statement.setInt(10, super.getPos2().getBlockZ());
            statement.setInt(11, spawn.getBlockX());
            statement.setInt(12, spawn.getBlockY());
            statement.setInt(13, spawn.getBlockZ());
            statement.setInt(14, Math.round(spawn.getYaw()));
            statement.setLong(15, super.getFlags());
            statement.setString(16, creatorCredentials.getUniqueId().toString());
            statement.setString(17, enterMsg);
            statement.setString(18, exitMsg);
            statement.setLong(19, lastOwnerEdit);
            statement.setLong(20, lastMemberEdit);
            statement.setInt(21, timesWarpUsed);
            statement.setBoolean(22, hidden);
            statement.setBoolean(23, deleted);
            statement.setInt(24, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM lot_members WHERE lotid = ?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM lot_banned WHERE lotid = ?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM lot_safelist WHERE lotid = ?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            for (PlayerCredentials pc : super.getMembers()) {
                statement = DBManager.prepareStatement("INSERT INTO lot_members (lotid, player_id, isop) VALUES (?, ?, 0);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            for (PlayerCredentials pc : super.getOperators()) {
                statement = DBManager.prepareStatement("INSERT INTO lot_members (lotid, player_id, isop) VALUES (?, ?, 1);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            for (PlayerCredentials pc : banned.keySet()) {
                long time = banned.get(pc);

                statement = DBManager.prepareStatement("INSERT INTO lot_banned (lotid, player_id, timeout) VALUES (?, ?, ?);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.setLong(3, time);
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            for (PlayerCredentials pc : getSafelist()) {
                statement = DBManager.prepareStatement("INSERT INTO lot_safelist (lotid, player_id) VALUES (?, ?);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            statement = DBManager.prepareStatement("DELETE FROM lot_tags WHERE lot_id = ?;");
            statement.setInt(1, super.getId());
            statement.execute();
            DBManager.closePreparedStatement(statement);

            if (tag != null) {
                statement = DBManager.prepareStatement("INSERT INTO lot_tags VALUES (?, ?, ?);");
                statement.setInt(1, super.getId());
                statement.setString(2, tag.getTag());
                statement.setBoolean(3, tag.isPublic());
                statement.execute();
                DBManager.closePreparedStatement(statement);
            }

            super.setUpdated(false);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save lot #" + super.getId() + "!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * Gets the location one block diagonally from the final location calculated
     * from the most minimum point from the location passed in on the lot
     *
     * @param lot
     * @param loc
     * @return
     */
    public Location getClosestCorner(Location loc) {
        InnectisLot parentLot = getParent();
        InnectisLot finalLot = this;

        // Get the topmost parent lot
        while (parentLot != null) {
            finalLot = parentLot;
            parentLot = parentLot.getParent();
        }

        // Represents the final location to teleport the player to
        int finalX = 0;
        int finalY = loc.getBlockY();
        int finalZ = 0;

        // These values will mimic the end blockface that teleports the player diagonally
        // once the final location to reach the end of the lot has been calculated
        // (they will either be -1 or 1)
        int modX = 0;
        int modZ = 0;

        // Get minimum X/Z and maximum X/Z coordinates
        int minX = finalLot.getLowestX();
        int minZ = finalLot.getLowestZ();
        int maxX = finalLot.getHighestX();
        int maxZ = finalLot.getHighestZ();

        // Calculate the distance of lowest and highest X/Z coordinates in
        // conjunction with the player's location
        int diffX1 = loc.getBlockX() - minX;
        int diffX2 = maxX - loc.getBlockX();
        int diffZ1 = loc.getBlockZ() - minZ;
        int diffZ2 = maxZ - loc.getBlockZ();

        // Determines the minimum distance of all four corners of the lot from
        // the player's current location
        int score = 0;

        // Grab the first coordinates (min X / min Z) and add to score
        score = diffX1 + diffZ1;
        finalX = minX;
        finalZ = minZ;
        modX = -1;
        modZ = -1;

        // See if min X / max Z is smaller than previous score
        if ((diffX1 + diffZ2) < score) {
            score = diffX1 + diffZ2;
            finalX = minX;
            finalZ = maxZ;
            modX = -1;
            modZ = 1;
        }

        // See if max X / min Z is smaller than previous score
        if ((diffX2 + diffZ1) < score) {
            score = diffX2 + diffZ1;
            finalX = maxX;
            finalZ = minZ;
            modX = 1;
            modZ = -1;
        }

        // See if max X / max Z is smaller than previous score
        if ((diffX2 + diffZ2) < score) {
            finalX = maxX;
            finalZ = maxZ;
            modX = 1;
            modZ = 1;
        }

        // Calculate the end location, and the block to teleport to
        Location modifiedLocation = new Location(loc.getWorld(), finalX + modX, finalY, finalZ + modZ, loc.getYaw(), loc.getPitch());

        IdpSpawnFinder finder = new IdpSpawnFinder(modifiedLocation);
        Location endLocation = finder.findClosestSpawn(false);

        // No destination? Get spawn instead
        if (endLocation == null) {
            endLocation = WarpHandler.getSpawn();
        }

        return endLocation;

    }

    public String getEnterMsg() {
        return enterMsg;
    }

    public void setEnterMsg(String msg) {
        enterMsg = msg;
        super.setUpdated(true);
    }

    public void setExitMsg(String msg) {
        exitMsg = msg;
        super.setUpdated(true);
    }

    public String getExitMsg() {
        return exitMsg;
    }

    /**
     * Removes every trait from this lot (this saves when done)
     *
     * @return
     */
    @Override
    public void clearTraits() {
        simulateLeave();
        super.clearTraits();
        simulateJoin();
        clearBanned();
        clearSafelist();
        setEnterMsg("");
        setExitMsg("");
        setDisabled(false);
        setLotName("");
        setTag(null);

        super.setUpdated(true);
    }

    public void addWarpTimeUsed() {
        timesWarpUsed++;
        super.setUpdated(true);
    }

    public int getTimesWarpUsed() {
        return timesWarpUsed;
    }

    public void resetWarpTimesUsed() {
        timesWarpUsed = 0;
        super.setUpdated(true);
    }

    /**
     * Called when a player enters this lot
     *
     * @param player
     * @param previousLot
     * @param verbose
     * @return
     */
    public boolean onEnter(IdpPlayer player, InnectisLot previousLot, boolean verbose) {
        if (isFlagSet(LotFlagType.INVISIBLE)) {
            for (IdpPlayer onlinePlayer : InnPlugin.getPlugin().getOnlinePlayers()) {
                onlinePlayer.getHandle().hidePlayer(player.getHandle());
            }
        }

        if (isFlagSet(LotFlagType.NONAMETAG)) {
            player.getSession().setShowNametag(false);
        }

        if (isFlagSet(LotFlagType.BLINDNESS)) {
            player.getSession().setLastBlindnessEffect(System.currentTimeMillis());
            PlayerEffect.BLINDNESS.applySpecial(player, 20000, 10);
        }

        if (isFlagSet(LotFlagType.NOJUMP)) {
            player.getSession().setLastJumpEffect(System.currentTimeMillis());
            PlayerEffect.JUMP_BOOST.applySpecial(player, 20000, 190);
        }

        if (isFlagSet(LotFlagType.ETERNALDAY)) {
            player.getHandle().setPlayerTime(6000, false);
        } else if (isFlagSet(LotFlagType.ETERNALNIGHT)) {
            player.getHandle().setPlayerTime(14000, false);
        } else {
            player.getHandle().resetPlayerTime(); //restore default time
        }

        if (isFlagSet(LotFlagType.NOPOTION)) {
            for (PotionEffect effect : player.getHandle().getActivePotionEffects()) {
                player.getHandle().removePotionEffect(effect.getType());
            }
        }

        if (isFlagSet(LotFlagType.NOHUNGER)) {
            double oldHealthLevel = player.getHealth();
            int oldFoodLevel = player.getFoodLevel();

            player.getSession().setOldHealthLevel(oldHealthLevel);
            player.getSession().setOldFoodLevel(oldFoodLevel);

            player.setFoodLevel(20);
        }

        if (isFlagSet(LotFlagType.NOWEATHER)) {
            if (getWorld().hasStorm())
            player.getHandle().setPlayerWeather(WeatherType.CLEAR);
        } else if (isFlagSet(LotFlagType.ETERNALWEATHER)) {
            if (!getWorld().hasStorm())
            player.getHandle().setPlayerWeather(WeatherType.DOWNFALL);
        } else if (player.getHandle().getPlayerWeather() != null) {
            player.getHandle().resetPlayerWeather();
        }

        if (isFlagSet(LotFlagType.NOSAVEINVENTORY)) {
            player.saveInventory();
            player.setInventory(InventoryType.NO_SAVE);
        }

        if (isFlagSet(LotFlagType.ANTICOLLISION)) {
            player.getHandle().setCollidable(false);
        }

        //ALL VERBOSE MESSAGES SHOULD GO IN HERE
        //THIS SHOULD BE AT THE END OF THE METHOD
        if (verbose) {
            boolean showPvP = (isFlagSet(LotFlagType.PVP) && (previousLot == null || !previousLot.isFlagSet(LotFlagType.PVP)));
            boolean showEnterMsg = (enterMsg != null && !enterMsg.isEmpty());

            if (showPvP || showEnterMsg) {
                LotEnterLeaveTime enterLeaveTime = player.getSession().getLotEnterLeave();
                InnectisLot lastLot = enterLeaveTime.getLot();

                if (lastLot != this) {
                    enterLeaveTime.setLot(this);
                }

                long lotEnterTime = enterLeaveTime.getEnterTime();

                if (lotEnterTime > 0) {
                    long diff = (System.currentTimeMillis() - lotEnterTime);

                    if (diff < 7500) {
                        showPvP = false;
                        showEnterMsg = false;
                    }
                }

                if (showPvP || showEnterMsg) {
                    if (showPvP) {
                        player.sendTitle("", ChatColor.DARK_RED + "You have entered a PvP zone.");
                    }

                    if (showEnterMsg) {
                        player.printInfo(ChatColor.YELLOW + enterMsg.replaceAll(" ", ChatColor.YELLOW + " "));
                    }

                    enterLeaveTime.setEnterTime(System.currentTimeMillis());
                }
            }
        }

        return true;
    }

    /**
     * Sets the owner of this lot and all of its children
     *
     * @param credentials
     */
    public void setOwnerWithSublots(PlayerCredentials credentials) {
        setOwner(credentials);

        for (InnectisLot sublot : sublots) {
            sublot.setOwnerWithSublots(credentials);
        }
    }

    /**
     * Called when a player leaves this lot
     *
     * @param player
     * @param nextLot
     * @param verbose
     * @return
     */
    public boolean onLeave(IdpPlayer player, InnectisLot nextLot, boolean verbose) {
        if (isFlagSet(LotFlagType.INVISIBLE)) {
            for (IdpPlayer onlinePlayer : InnPlugin.getPlugin().getOnlinePlayers()) {
                onlinePlayer.getHandle().showPlayer(player.getHandle());
            }
        }

        if (isFlagSet(LotFlagType.NONAMETAG)) {
            player.getSession().setShowNametag(true);
        }

        if (isFlagSet(LotFlagType.BLINDNESS)) {
            player.getSession().setLastBlindnessEffect(0);
            PlayerEffect.BLINDNESS.removeSpecial(player);
        }

        if (isFlagSet(LotFlagType.NOJUMP)) {
            player.getSession().setLastJumpEffect(0);
            PlayerEffect.JUMP_BOOST.removeSpecial(player);
        }

        if (isFlagSet(LotFlagType.ETERNALDAY) || isFlagSet(LotFlagType.ETERNALNIGHT)) {
            player.getHandle().resetPlayerTime(); //restore default time
        }

        if ((isFlagSet(LotFlagType.NOWEATHER) || isFlagSet(LotFlagType.ETERNALWEATHER))
                && player.getHandle().getPlayerWeather() != null) {
                player.getHandle().resetPlayerWeather(); // restore normal weather
        }

        if (isFlagSet(LotFlagType.NOHUNGER)) {
            double oldHealthLevel = player.getSession().getOldHealthLevel();
            int oldFoodLevel = player.getSession().getOldFoodLevel();

            player.setHealth(oldHealthLevel);
            player.setFoodLevel(oldFoodLevel);
        }

        if (isFlagSet(LotFlagType.NOSAVEINVENTORY)) {
            player.setInventory(player.getWorld().getSettings().getInventoryType());
        }

        if (isFlagSet(LotFlagType.ANTICOLLISION)) {
            player.getHandle().setCollidable(true);
        }

        LotEnterLeaveTime lotEnterLeave = player.getSession().getLotEnterLeave();

        //ALL VERBOSE MESSAGES SHOULD GO IN HERE
        //THIS SHOULD BE AT THE END OF THE METHOD
        if (verbose) {
            boolean showPvP = (isFlagSet(LotFlagType.PVP) && (nextLot == null || !nextLot.isFlagSet(LotFlagType.PVP)));
            boolean showLeaveMsg = (exitMsg != null && !exitMsg.isEmpty());

            if (showPvP || showLeaveMsg) {
                LotEnterLeaveTime enterLeaveTime = player.getSession().getLotEnterLeave();
                long lastLeaveTime = enterLeaveTime.getLeaveTime();

                if (lastLeaveTime > 0) {
                    long diff = (System.currentTimeMillis() - lastLeaveTime);

                    if (diff < 7500) {
                        showPvP = false;
                        showLeaveMsg = false;
                    }
                }

                if (showPvP || showLeaveMsg) {
                    if (showPvP) {
                        player.sendTitle("", ChatColor.DARK_RED + "You have left a PvP zone.");
                    }

                    if (showLeaveMsg) {
                        player.printInfo(ChatColor.YELLOW + exitMsg.replaceAll(" ", ChatColor.YELLOW + " "));
                    }

                    lotEnterLeave.setLeaveTime(System.currentTimeMillis());
                }
            }
        }

        return true;
    }

    /**
     * Gets an unmodifiable map of the banned players of this lot
     * @return
     */
    public Map<PlayerCredentials, Long> getBanned() {
        return Collections.unmodifiableMap(banned);
    }

    /**
     * Sets the banned players of this lot
     * @param banned
     */
    public void setBanned(Map<PlayerCredentials, Long> banned) {
        this.banned = banned;
    }

    /**
     * Gets the tag of this owned object
     * @return
     */
    public LotTag getTag() {
        return tag;
    }

    /**
     * Sets the tag of this owned object
     * @param tag
     */
    public void setTag(LotTag tag) {
        this.tag = tag;
        super.setUpdated(true);
    }

    @Override
    public OwnedObjectType getType() {
        return OwnedObjectType.LOT;
    }

}

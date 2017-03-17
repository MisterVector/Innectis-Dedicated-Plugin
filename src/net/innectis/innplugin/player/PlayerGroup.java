package net.innectis.innplugin.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.Prefix;

/**
 *
 * @author Hret
 */
public enum PlayerGroup {

    /**
     * Super Administrator
     * This is admin group for special commands that could crash the server if not used properly
     */
    SADMIN(1, 10, "Admin", ChatColor.DARK_RED, 100, 2000, 20000),
    /**
     * Administrator group
     */
    ADMIN(2, 9, "Admin", ChatColor.DARK_RED, 100, 2000, 20000),
    /**
     * Rainbow Moderator group
     */
    RAINBOW_MODERATOR(3, 8, "Rainbow Mod", ChatColor.GREEN, 100, 500, 20000),
    /**
     * Moderator group
     */
    MODERATOR(4, 7, "Moderator", ChatColor.DARK_GREEN, 100, 500, 20000),
    /**
     * Diamond group
     */
    DIAMOND(5, 6, "Diamond", ChatColor.AQUA, 100, 300, 50000),
    /**
     * Goldy group
     */
    GOLDY(6, 5, "Goldy", ChatColor.GOLD, 100, 300, 20000),
    /**
     * Super VIP group
     */
    SUPER_VIP(7, 4, "Super VIP", ChatColor.DARK_PURPLE, 5, 150, 10000),
    /**
     * VIP group
     */
    VIP(8, 3, "VIP", ChatColor.DARK_AQUA, 3, 100, 5000),
    /**
     * Super user group
     * This rank is given when players completed the intoduction
     */
    USER(9, 2, "User", ChatColor.YELLOW, 1, 50, 500),
    /**
     * Guest rank, given when somebody joins the server
     */
    GUEST(10, 1, "Guest", ChatColor.YELLOW, 1, 50, 100),
    //
    /**
     * This is not a rank that a player can have!
     * Use this group if nobody should have access to a permission.
     */
    NONE(-1, -1, "None", ChatColor.WHITE, 0, 0, 0);
    /**
     * Caching of the most used data
     */
    /** Groupid */
    public final int id;
    /**
     * ID that is <b>Increasing</b> when the groups get higher.<br/>
     * Also they dont have to be in order, there can be a gap between them. <br/>
     * SAdmin got the highest.<br />
     * Guest got the lowest <br />
     * None got <b>-1</b> which isn't used<br />
     **/
    private final int orderID;
    /** Groupname */
    public final String name;
    /** GroupColour */
    public final ChatColor color;
    /** number of lots they can have (excludes sublots) */
    private final int maxLots;
    /** max lot size they can have */
    private final int maxLotSize;
    /** number of refferal points recieved */
    private final int referralPoints;

    private PlayerGroup(int id, int orderid, String name, ChatColor color, int maxLots, int maxLotSize, int refferalPoints) {
        this.id = id;
        this.orderID = orderid;
        this.name = name;
        this.color = color;
        this.maxLots = maxLots;
        this.maxLotSize = maxLotSize;
        this.referralPoints = refferalPoints;
    }

    /**
     * Retuns a list with all permission that are given to this group
     * @param id
     * @return
     */
    public Set<Permission> getPermissions() {
        return permissions.get(id);
    }

    /**
     * Returns a prefix object for the given group
     * @return
     */
    public Prefix getPrefix() {
        return new Prefix(name, ChatColor.WHITE, color);
    }

    /**
     * Returns the maximum  number of lots this group can have
     * @return
     */
    public int getMaxLots() {
        return maxLots;
    }

    /**
     * Returns the number of referral points earned for this group.
     * @return
     */
    public int getReferralPoints() {
        return referralPoints;
    }

    /**
     * Returns the maximum lot size this group can have
     * @return
     */
    public int getMaxLotSize() {
        return maxLotSize;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if the current group is part of the staff.
     *
     * @return true if the group is a staff group
     */
    public boolean isStaffGroup() {
        return equalsOrInherits(PlayerGroup.MODERATOR);
    }


    /**
     * Checks if the current group2 is lower then the current group.<br/>
     * So the <b>Guest</b> rank will be lower then the <b>Moderator</b> rank.<br/>
     * <br/>
     * <i>PlayerGroup.NONE will <u>always</u> return false.</i>
     *
     * @param group2
     * @return true if its the lower ranked group
     */
    public boolean inherits(PlayerGroup testGroup) {
        return equalsOrInherits(testGroup) && this != testGroup;
    }

    /**
     * Checks if the current group2 is the same or lower then the current group.<br/>
     * So the <b>Guest</b> rank will be lower then the <b>Moderator</b> rank.<br/>
     * <br/>
     * <i>PlayerGroup.NONE will <u>always</u> return false.</i>
     *
     * @param group2
     * @return true if its the same or lower ranked group
     */
    public boolean equalsOrInherits(PlayerGroup testGroup) {
        if (orderID < 0 || testGroup.orderID < 0) {
            return false;
        }
        return (orderID >= testGroup.orderID);
    }

    /* -------------------------------------------------------
     * Statics
     * ------------------------------------------------------- */
    /** Sets of permissions for every group. */
    protected static final HashMap<Integer, HashSet<Permission>> permissions = new HashMap<Integer, HashSet<Permission>>();
    /** Quick lookup map for groups with an ID. */
    protected static final HashMap<Integer, PlayerGroup> groups = new HashMap<Integer, PlayerGroup>();

    static {
        int i = 0;
        for (PlayerGroup group : values()) {
            i = Math.max(i, group.id);
            groups.put(group.id, group);
            permissions.put(group.id, new HashSet<Permission>());
        }
    }

    private static boolean PermissionInitialized = false;

    /**
     * Register permission, cant be done in static block as permission are not initialized
     */
    public static void registerPermissions() {
        InnPlugin.logCustom(ChatColor.AQUA, "Registering permissions...");

        // Avoid it being done twice
        if (PermissionInitialized) {
            return;
        }
        PermissionInitialized = true;
        // Setup inheritance
        // Register Permissions
        for (Permission perm : Permission.values()) {
            if (perm.group != NONE && Permission.getPermission(perm.id) != Permission.NONE) {
                permissions.get(perm.group.id).add(perm);
            }
        }

        for (PlayerGroup group : values()) {
            if (group == NONE) {
                continue;
            }
            for (PlayerGroup group2 : values()) {
                if (group == group2) {
                    continue;
                }
                if (group.equalsOrInherits(group2)) {
                    permissions.get(group.id).addAll(permissions.get(group2.id));
                }
            }
        }
    }
    /* -------------------------------------------------------
     * LOOKUPS
     * ------------------------------------------------------- */

    /**
     * Retuns a list with all permission that is given to this group
     * @param playergroup
     * @return
     */
    public static Set<Permission> getPermissions(PlayerGroup group) {
        return permissions.get(group.id);
    }

    /**
     * Retuns the group with the given key
     * @param id
     * @return The group or PlayerGroup.NONE if not found
     */
    public static PlayerGroup getGroup(int id) {
        PlayerGroup group = groups.get(id);
        if (group == null) {
            group = PlayerGroup.NONE;
        }
        return group;
    }

    /**
     * Retuns the group with the given key.     *
     * @param name of group (you can use '_' as space)
     * @return The group or PlayerGroup.NONE if not found
     */
    public static PlayerGroup getGroup(String groupName) {
        for (PlayerGroup group : values()) {
            if (group.name.equalsIgnoreCase(groupName.replace('_', ' '))) {
                return group;
            }
        }
        return PlayerGroup.NONE;
    }

    /**
     * This method will look in the database for the group of the player with the given ID.<br />
     * <b>If the player is online dont use this method, but look in their session object.</b>
     * @param playerName
     * @return PlayerGroup or PlayerGroup.NONE if not found
     */
    public static PlayerGroup getGroupOfPlayerById(UUID playerId) {
        PlayerGroup group = PlayerGroup.NONE;

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT playergroup FROM players WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();

            if (result.next()) {
                int groupId = result.getInt("playergroup");
                group = PlayerGroup.getGroup(groupId);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Error getting group of player with ID: " + playerId, ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return group;
    }

}

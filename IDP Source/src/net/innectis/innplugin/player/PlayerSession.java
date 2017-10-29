package net.innectis.innplugin.player;

import com.google.common.collect.Lists;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.iplogging.Playerinfo;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler.PermissionType;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.handlers.WorldHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.objects.EditSignWand;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.objects.ModifiablePermissions;
import net.innectis.innplugin.objects.SpoofObject;
import net.innectis.innplugin.objects.ViewedPlayerInventoryData;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.system.mail.MailMessage;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.objects.LotEnterLeaveTime;
import net.innectis.innplugin.player.channel.ChatChannel;
import net.innectis.innplugin.player.channel.ChatChannelGroup;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.channel.MemberDetails;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatInjector;
import net.innectis.innplugin.player.chat.Prefix;
import net.innectis.innplugin.player.request.Request;
import net.innectis.innplugin.player.tinywe.IdpEditSession;
import net.innectis.innplugin.player.tinywe.RegionClipboard;
import net.innectis.innplugin.player.tools.InformationTool.InformationToolType;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.StringUtil;
import net.minecraft.server.v1_11_R1.PacketPlayOutCamera;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 */
public class PlayerSession {

    // <editor-fold defaultstate="collapsed" desc="Static methods and objects">
    private static Map<UUID, PlayerSession> sessions = Collections.synchronizedMap(new HashMap<UUID, PlayerSession>());
    private static final Object _synclock = new Object();

    /**
     * Checks if there is a session for the player
     *
     * @param playerName
     * @return
     */
    public static boolean hasSession(String playerName) {
        for (PlayerSession session : sessions.values()) {
            if (session.getRealName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if there is a session for the player by ID
     *
     * @param playerName
     * @return
     */
    public static boolean hasSession(UUID playerId) {
        for (PlayerSession session : sessions.values()) {
            if (session.getUniqueId().equals(playerId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Cleans up expired sessions
     *
     * @param now
     */
    public static void cleanup(long now) {
        for (PlayerSession session : getSessions()) {
            if (session.getExpireTime() > 0 && now > session.getExpireTime()) {
                session.destroy();
            } else if (session.getExpireTime() == 0) {
                // Check if session got stuck.
                if (InnPlugin.getPlugin().getPlayer(session.playerName) == null) {
                    session.expireSession(5);
                }
            }
        }
    }

    /**
     * Returns an active session. This will be null if the session had already
     * expired.
     *
     * @param uniqueId
     * @return
     */
    public static PlayerSession getActiveSession(UUID uniqueId) {
        return sessions.get(uniqueId);
    }

    /**
     * Gets a session from the specified player ID
     *
     * @param playerName
     * @return
     */
    public static PlayerSession getSession_(UUID playerId) {
        for (PlayerSession session : sessions.values()) {
            if (session.getUniqueId().equals(playerId)) {
                return session;
            }
        }

        return null;
    }

    /**
     * Gets a session from the specified player name
     *
     * @param playerName
     * @return
     */
    public static PlayerSession getSession_(String playerName) {
        for (PlayerSession session : sessions.values()) {
            if (session.getDisplayName().equalsIgnoreCase(playerName)) {
                return session;
            }
        }

        return null;
    }

    /**
     * Retuns the session of the player represented by name and ID. If there is
     * no session for the player, a new one is created. If the player of the
     * session is not online, it will expire automaticly in 10 minutes
     *
     * @param playerId
     * @param playerName
     * @param server
     * @return session for the given player
     */
    public static PlayerSession getSession(UUID playerId, String playerName, InnPlugin server) {
        return getSession(playerId, playerName, server, false);
    }

    /**
     * Retuns the session of the player represented by name and ID. If there is
     * no session for the player, a new one is created. If the player of the
     * session is not online, it will expire automaticly in 10 minutes
     *
     * @param playerId
     * @param playerName
     * @param server
     * @param isFixedName if true, then this name is fixed and does not need to
     * be updated from the database
     * @return session for the given player
     */
    public static PlayerSession getSession(UUID playerId, String playerName, InnPlugin server, boolean isFixedName) {
        synchronized (_synclock) {
            // Get the session
            PlayerSession session = sessions.get(playerId);

            if (session != null) {
                // Check if object was about to expire
                if (session.getExpireTime() > 0) {
                    // Check if player is still online
                    if (server.getPlayer(playerId) == null) {
                        // Reset expire time
                        session.expireSession(10);
                    } else {
                        // Reactivate.
                        session.reactivate();
                    }
                }
            } else {
                if (InnPlugin.isDebugEnabled()) {
                    InnPlugin.logDebug("Creating new session for " + playerName + ".");
                }

                // If this name is not fixed (according to the player's chatname setting)
                // then get the fixed name from the database
                if (!isFixedName) {
                    String testName = getFixedPlayerName(playerId);

                    // Player might be new, so there is no name in the database yet
                    if (testName != null) {
                        playerName = testName;
                    }
                }

                // Create new session
                session = new PlayerSession(playerId, playerName, server);
                // Register the session!
                sessions.put(playerId, session);
                // Expire session in 10 minutes if player not online
                if (server.getPlayer(playerId) == null) {
                    session.expireSession(10);
                }
            }

            return session;
        }
    }

    /**
     * Returns a list with all sessions Note: some session can contain players
     * that are offline!
     *
     * @return all existing session (including the ones that are expired)
     */
    public static List<PlayerSession> getSessions() {
        List<PlayerSession> list = new ArrayList<PlayerSession>();
        list.addAll(sessions.values());
        return list;
    }

    /**
     * Sets the new fixed player name of this player
     *
     * @param playerName
     * @return true if successful, false otherwise
     */
    public boolean setFixedPlayerName(String playerName) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players SET name = ? WHERE player_id = ?;");
            statement.setString(1, playerName);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save " + this.playerName + "'s name!", ex);

            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        this.playerName = playerName;

        return true;
    }

    /**
     * Looks up the username from the database.
     *
     * @param playerId
     * @return The username as in the database
     */
    private static String getFixedPlayerName(UUID playerId) {
        String fixedName = null;
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            // Check the username for caps
            statement = DBManager.prepareStatement("SELECT name FROM players WHERE player_id = ?");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                fixedName = set.getString("name");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot get the fixed username from the database.", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return fixedName;
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Idp Specific Enums">

    /**
     * The status of a player
     */
    public enum PlayerStatus {

        ALIVE_PLAYER,
        DEAD_PLAYER
    }
    //</editor-fold>
//
    /**
     * The ID of the owner of this session
     */
    protected UUID playerId;

    /**
     * The playername of the owner of this session
     */
    protected String playerName;

    private InnPlugin server;
    /**
     * The type of inventory the player has
     */
    protected InventoryType invType;

    /**
     * @param playerId
     * @param server
     */
    private PlayerSession(UUID playerId, String playerName, InnPlugin server) {
        // Reset session time.
        sessionStart = System.currentTimeMillis();

        this.playerId = playerId;
        this.playerName = playerName;
        this.server = server;

        loadSettings();

        // Checks if the player is new
        isNewPlayer = (getLastLogin() == null);

        // Default type to none;
        invType = InventoryType.NONE;

        // Name colour always of GroupPrefixLocation?
        this.nameColor = getGroup().color;

        // Creating seesion, assume player is dead, otherwise it should be overriden by the method
        _playerStatus = PlayerStatus.DEAD_PLAYER;

        // Load all ignored players
        loadIgnoredPlayersFromDB();

        // Extra debugMode permission
        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Registering debug permissions...");

            ModifiablePermissions perms = getModifiablePermissions();
            perms.addPermissionNoSave(Permission.command_cheat_time.getId(), PermissionType.ADDITIONAL);
            perms.addPermissionNoSave(Permission.command_cheat_weather.getId(), PermissionType.ADDITIONAL);
        }

        // Reactive it
        reactivate();
    }
//

    /**
     * Handles a player login. <br/>
     * Do not call, call IdpPlayer::login() instead!
     */
    protected void login() {
        logintime = _savedlogintime = System.currentTimeMillis();
    }

    /**
     * Handles a player logout. <br/>
     * Do not call, call IdpPlayer::logout() instead!
     */
    protected void logout() {
        expireSession(Configuration.PLAYERSESSION_EXPIRETIME);

        // Save online ticks
        saveOnlineTime();
        logintime = Integer.MIN_VALUE;
        _savedlogintime = Integer.MIN_VALUE;

        //clear the inventoryType so it doesnt have a chance to be incorrect upon next login
        invType = InventoryType.NONE;

        leaveAllChatChannels();
        saveChatSoundSettings();

        PreparedStatement statement = null;

        // This is not inside a method as it should never be fired for any other reason...
        try {
            // Set the logout time.
            statement = DBManager.prepareStatement("UPDATE ip_log SET logouttime = CURRENT_TIMESTAMP WHERE player_id = ? AND logouttime IS NULL ORDER BY logtime DESC LIMIT 1;");
            statement.setString(1, playerId.toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("SqlException updating logouttime", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    //<editor-fold defaultstate="collapsed" desc="Other">
    Scoreboard previousScoreboard = null;

    /**
     * Sets the player's scoreboard, preserving their previous one
     * @param scoreboard
     */
    public void setScoreboard(Scoreboard scoreboard) {
        IdpPlayer player = InnPlugin.getPlugin().getPlayer(getUniqueId());
        Player bukkitPlayer = player.getHandle();

        previousScoreboard = bukkitPlayer.getScoreboard();
        bukkitPlayer.setScoreboard(scoreboard);
    }

    /**
     * Resets the player's scoreboard to the previous one
     */
    public void resetScoreboard() {
        IdpPlayer player = InnPlugin.getPlugin().getPlayer(getUniqueId());

        player.getHandle().setScoreboard(previousScoreboard);
        previousScoreboard = null;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Last lot queried">
    private int lastLotIDQueried = 0;

    /**
     * Gets the last lot ID that was looked up (/thislot, /thatlot, etc.)
     * @return
     */
    public int getLastLotIDQueried() {
        return lastLotIDQueried;
    }

    /**
     * Sets the last lot ID that was looked up (/thislot, /thatlot, etc.)
     * @param lastLotID
     */
    public void setLastLotIDQueried(int lastLotID) {
        this.lastLotIDQueried = lastLotID;
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Spoofing">
    /**
     * Gets or sets if the player is spoofing their name
     */
    private SpoofObject spoofObject = null;

    /**
     * Sets the player's spoofed name
     *
     * @param spoofName
     */
    public void setSpoofName(String spoofName, PlayerGroup group) {
        spoofObject = new SpoofObject(spoofName, group);
    }

    /**
     * Gets the spoof object associated with this player
     *
     * @return
     */
    public SpoofObject getSpoofObject() {
        return spoofObject;
    }

    /**
     * Clears the spoof object associated with this player
     */
    public void clearSpoofObject() {
        spoofObject = null;
    }

    /**
     * Checks if the player is spoofing *
     */
    public boolean isSpoofing() {
        return (spoofObject != null);
    }

    /**
     * Checks if the player is a part of staff *
     */
    public boolean isStaff() {
        return group.isStaffGroup();
    }

    /**
     * Gets the display name
     */
    public String getDisplayName() {
        if (spoofObject == null) {
            return playerName;
        }

        return spoofObject.getSpoofName();
    }

    public void setUsername(String username) {
        this.playerName = username;
    }

    /**
     * Gets the ID of this player
     *
     * @return
     */
    public UUID getUniqueId() {
        return playerId;
    }

    /**
     * Gets the real name of the current player.
     */
    public String getRealName() {
        return this.playerName;
    }

    /**
     * Gets the coloured display name of the player
     */
    public String getColoredDisplayName() {
        if (spoofObject == null) {
            return this.nameColor + this.playerName;
        }
        return spoofObject.getSpoofNameColor();
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Playerstatus">
    /**
     * Value to show the status of a player
     */
    private PlayerStatus _playerStatus;

    /**
     * Is the player tagged as alive?
     *
     * @return
     */
    public Boolean isPlayerAlive() {
        return _playerStatus == PlayerStatus.ALIVE_PLAYER;
    }

    /**
     * Returns the status of the player
     *
     * @return the status of the player
     */
    public PlayerStatus getPlayerStatus() {
        return _playerStatus;
    }

    /**
     * *
     * Sets the status of a player
     *
     * @param the status
     */
    public void setPlayerStatus(PlayerStatus status) {
        _playerStatus = status;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Statistics">
    /**
     * The millis when the session started
     */
    private long sessionStart;
    /**
     * The millis when the player logged in.
     */
    private long logintime = Integer.MIN_VALUE;
    /**
     * The millis when the onlinetime was last saved.
     */
    private long _savedlogintime = Integer.MIN_VALUE;
    /**
     * Do not access this directly, its used in a special way. See
     * PlayerSession::getOnlineTime() This value should not be used to store
     * onlinetime as it may be inaccurate
     */
    private long _totalOnlineTime = Integer.MIN_VALUE;
    /**
     * True is the player is new
     */
    private boolean isNewPlayer;
    private static final Date NO_DATE = new Date(1l);
    private Date lastLoginDate = NO_DATE;
    private Date lastLogoutDate = NO_DATE;

    /**
     * Gets the time the player has been online in this session. <br/>
     * That means that this will return how many milli seconds have passed since
     * the player came online.
     *
     * @return
     */
    public long getSessionOnlineTime() {
        return System.currentTimeMillis() - getLoginTime();
    }

    /**
     * This will get the time in millis when the player logged in.
     *
     * @return If the player is not logged in Integer.MIN_VALUE will be
     * returned.
     */
    public long getLoginTime() {
        return this.logintime;
    }

    /**
     * Gets the duration in milliseconds for how long the session is active.
     */
    public long getSessionDuration() {
        return System.currentTimeMillis() - this.sessionStart;
    }

    /**
     * Gets the datetime in millis when the session was created. (This is the
     * total millis, UNIX time)
     */
    public long getSessionCreationTime() {
        return this.sessionStart;
    }

    /**
     * Checks if the player is new (less then a minute onlinetime)
     *
     * @return
     */
    public boolean isNewPlayer() {
        return isNewPlayer;
    }

    /**
     * Gets the total online time of this player. This is the amount of millis
     * in total the player has been online on the server.
     *
     * @return
     *
     * ----
     *
     * Retuns the online time of the player. Or a negative value if none found.
     *
     * This method will store the online time in a variable of the session. The
     * way this is done is simple. When the onlinetime is loaded from the
     * database its subtracted from the currentTimeMillis. That means that when
     * you subtract the stored onlinetime variable from the currentTimeMillis
     * you get the real onlinetime like it is in the database. This allow fast
     * calculation of the total online time without requesting it from the
     * database with every request. Or to constantly adjust the value.
     *
     * If -1 is retuned, it couln't load the time from the database.
     *
     * @return onlinetime
     */
    public long getTotalOnlineTime() {
        // Checks if onlinetime is loaded, if not load it
        if (_totalOnlineTime == Integer.MIN_VALUE) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement("SELECT onlinetime FROM players WHERE player_id = ?;");
                statement.setString(1, playerId.toString());
                result = statement.executeQuery();

                if (result.next()) {
                    _totalOnlineTime = result.getLong("onlinetime");
                }
            } catch (SQLException ex) {
                server.logError("Cannot load onlinetime of player " + getColoredName(), ex);
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }

        if (_savedlogintime == Integer.MIN_VALUE) {
            return _totalOnlineTime;
        }

        // Add the current logged in time
        return _totalOnlineTime + (System.currentTimeMillis() - _savedlogintime);
    }

    /**
     * Saves the online time of the player.
     */
    public void saveOnlineTime() {
        long oldvalue = _totalOnlineTime;

        PreparedStatement statement = null;

        try {
            long newvalue = getTotalOnlineTime();
            long savedLoginTime = System.currentTimeMillis();

            if (isAFK() || getPlayerStatus() == PlayerStatus.DEAD_PLAYER) {
                return; //dont increment played time if they're afk
            }

            statement = DBManager.prepareStatement("UPDATE players SET onlinetime = ? WHERE player_id = ?");
            statement.setFloat(1, newvalue);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();

            _totalOnlineTime = newvalue;
            _savedlogintime = savedLoginTime;
        } catch (SQLException ex) {
            server.logError("SQLException saveOnlineTime() " + getColoredName() + ChatColor.RED + " " + ex.getMessage());
            // Online time not saved, switch back to old value
            _totalOnlineTime = oldvalue;
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Sets the last login date of the player
     *
     * @param player
     */
    public void setLastLogin() {
        lastLoginDate = new Date();
        isNewPlayer = false;

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO players (player_id, name, playergroup) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE lastlogin = CURRENT_TIMESTAMP");
            statement.setString(1, playerId.toString());
            statement.setString(2, playerName);
            statement.setInt(3, PlayerGroup.GUEST.id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            server.logError("SQLException setLastLogin() " + getColoredName() + ChatColor.RED + " " + ex.getMessage());
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Returns the date of the last login
     *
     * @param player
     * @return null when never logged in before
     */
    public final Date getLastLogin() {
        if (lastLoginDate == NO_DATE) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement("SELECT lastlogin FROM players WHERE player_id = ? LIMIT 1;");
                statement.setString(1, playerId.toString());
                result = statement.executeQuery();

                if (result.next()) {
                    lastLoginDate = result.getTimestamp("lastlogin");
                } else {
                    lastLoginDate = null;
                }
            } catch (SQLException ex) {
                server.logError("SQLException getLastLogin() " + getColoredName() + ChatColor.RED + " " + ex.getMessage());
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }

        return lastLoginDate;
    }

    /**
     * Returns the date of the last login
     *
     * @param player
     * @return null when never logged in before
     */
    public final Date getLastLogout() {
        if (lastLogoutDate == NO_DATE) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement(" SELECT logouttime FROM ip_log WHERE player_id = ? AND logouttime IS NOT NULL ORDER BY logtime DESC LIMIT 1;");
                statement.setString(1, playerId.toString());
                result = statement.executeQuery();

                if (result.next()) {
                    lastLogoutDate = result.getTimestamp("logouttime");
                } else {
                    lastLogoutDate = null;
                }
            } catch (SQLException ex) {
                server.logError("SQLException getLastLogout() " + getColoredName() + ChatColor.RED + " " + ex.getMessage());
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }
        return lastLogoutDate;
    }

    /**
     * Returns the playerinfo object of the player
     *
     * @return
     */
    public Playerinfo getPlayerinfo() {
        return Playerinfo.findPlayer(playerId, playerName);
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Session Variables">
    private long expireTime;

    /**
     * This marks the session to be expired in the given number of minutes.
     *
     * @param minutes
     */
    public void expireSession(int minutes) {
        expireTime = System.currentTimeMillis() + (minutes * 60 * 1000);
    }

    /**
     * This demarks the session to expire. It also resets variables that need to
     * be resetted when the player rejoins
     */
    public final void reactivate() {
        expireTime = 0;
        lastBlindnessEffect = 0;
    }

    /**
     * Returns the time when the session should be expired. If the session is
     * not marked to be expired, 0 is returned
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     * What should be done when the session is expired.
     */
    private void expire() {
        // nothing yet
    }

    /**
     * Destroys the session
     */
    public void destroy() {
        synchronized (_synclock) {
            expire();
            sessions.remove(playerId);
        }
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Name & Group">
    private ChatColor nameColor;
    private PlayerGroup group;
    private Prefix[] namePrefix;

    /**
     * Returns the playergroup the given player is in
     *
     * @return playergroup
     */
    public final PlayerGroup getGroup() {
        if (group == null) {
            group = PlayerGroup.getGroupOfPlayerById(playerId);

            // Group not found
            if (group == PlayerGroup.NONE) {
                group = PlayerGroup.GUEST;
            }

            // This will also change the colour of the name
            setGroupWithoutUpdate(group);
        }
        return group;
    }

    /**
     * Checks if the player is a valid player
     *
     * @return
     */
    public boolean isValidPlayer() {
        PlayerGroup tempGroup = PlayerGroup.getGroupOfPlayerById(playerId);
        return (tempGroup != PlayerGroup.NONE);
    }

    /**
     * Sets the new GroupPrefixLocation of the player, this is also saved in the
     * database
     *
     * @param GroupPrefixLocation
     * @retuns if the update was succesfull
     */
    public final boolean setGroup(PlayerGroup newGroup) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players set playergroup = ? WHERE player_id = ?;");
            statement.setInt(1, newGroup.id);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();

            setGroupWithoutUpdate(newGroup);
            namePrefix = null;
            loadPrefix();
            return true;
        } catch (SQLException ex) {
            server.logError("Error setting group of player " + getColoredName(), ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * Sets the GroupPrefixLocation of the player without updating it in the
     * database
     *
     * @param newGroup
     */
    public void setGroupWithoutUpdate(PlayerGroup newGroup) {
        this.group = newGroup;
        this.nameColor = getGroup().color;
    }

    /**
     * Registers the prefix for the given player It will return the old prefix
     * or null if there was none The location must be 1 - 3! If not it will
     * return null
     *
     * @param loc
     * @param prefix
     * @return oldprefix or null
     */
    public Prefix setPrefix(int loc, Prefix prefix) {
        return setPrefix(loc, prefix, true);
    }

    /**
     * Registers the prefix for the given player It will return the old prefix
     * or null if there was none The location must be 1 - 3! If not it will
     * return null
     *
     * @param loc
     * @param prefix
     * @param save states if the prefix will be saved
     * @return oldprefix or null
     */
    public Prefix setPrefix(int loc, Prefix prefix, boolean save) {
        // First load the prefixes!
        loadPrefix();
        loc--;
        if (loc < 0 || loc > 2) {
            return null;
        }

        if (save) {
            savePrefix(loc, prefix.getText(), prefix.getTextColor(), prefix.getSurroundColor());
        }

        // Set and return old prefix
        Prefix oldpre = namePrefix[loc];
        namePrefix[loc] = prefix;
        return oldpre;
    }

    /**
     * This will return the given prefix on the location given
     *
     * @param loc
     * @return
     */
    public Prefix getPrefix(int loc) {
        // First load the prefixes!
        loadPrefix();
        loc--;
        if (loc < 0 || loc > 2) {
            return null;
        }
        return namePrefix[loc];
    }

    /**
     * Saves the prefix in the database
     *
     * @param text
     * @param textcolor
     * @param bracketcolor
     */
    protected final void savePrefix(int id, String text, ChatColor textcolor, ChatColor bracketcolor) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO prefix (player_id, subid, text, color1, color2) values "
                    + "(?,?,?,?,?) on duplicate key update text = ?, color1 = ?, color2 = ?;");
            statement.setString(1, playerId.toString());
            statement.setInt(2, id);
            statement.setString(3, text);
            statement.setString(4, bracketcolor.getCode());
            statement.setString(5, textcolor.getCode());
            statement.setString(6, text);
            statement.setString(7, bracketcolor.getCode());
            statement.setString(8, textcolor.getCode());
            statement.executeUpdate();
        } catch (SQLException ex) {
            server.logError("Failed to store colour for player " + getColoredName(), ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Loads the prefix settings from the database. Note: it will only load if
     * the namePrefix object is null
     */
    protected final void loadPrefix() {
        if (namePrefix == null) {
            namePrefix = new Prefix[3];
            namePrefix[Prefix.GroupPrefixLocation - 1] = getGroup().getPrefix();

            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement("SELECT text, color1, color2, subid FROM prefix WHERE player_id = ?;");
                statement.setString(1, playerId.toString());
                result = statement.executeQuery();

                while (result.next()) {
                    namePrefix[result.getInt("subid")] = new Prefix(result.getString("text"), ChatColor.getByCode(result.getString("color1")), ChatColor.getByCode(result.getString("color2")));
                }
            } catch (SQLException ex) {
                server.logError("Error getting prefix settings of player " + getColoredName(), ex);
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }
    }

    // Reloads the player's prefix from the database
    public final void reloadPrefix() {
        namePrefix = null;
        loadPrefix();
    }

    /**
     * Returns all of the prefixes the player has in the proper format There is
     * no space added at the end
     *
     * @return prefix
     */
    public String getStringPrefix() {
        loadPrefix();
        String completePrefix = "";
        for (Prefix pre : namePrefix) {
            if (pre != null) {
                completePrefix += pre.getFullPrefix();
            }
        }
        return completePrefix;
    }

    /**
     * Gets the name and prefix in 1 formatted string
     */
    public String getStringPrefixAndName() {
        return getStringPrefix() + " " + getColoredName() + ChatColor.WHITE;
    }

    /**
     * Gets the display name and prefix in 1 formatted string
     */
    public String getStringPrefixAndDisplayName() {
        return getStringPrefix() + " " + getColoredDisplayName() + ChatColor.WHITE;
    }

    /**
     * Gets the coloured name of the player
     */
    public String getColoredName() {
        return nameColor + playerName;
    }

    public void setColoredName(ChatColor color, String username) {
        this.nameColor = color;
        this.playerName = username;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private long muteTicks = 0;
    private long signInteract = 0; // Last interacted with a give sign.
    private boolean isJailed = false;
    private boolean canFly = false; // Allow creative gamemode flying?
    private IdpCommandSender<? extends CommandSender> lastWhisperFrom = null;
    private Location lastTeleportLocation = null;
    private long lastTeleportTime = System.currentTimeMillis();
    private long lastBlockMoved = 0; //Holds the time the last block was moved
    private boolean isFrozen = false;
    private boolean isTractorRunning = false;
    private long lastBlindnessEffect = 0;
    private long lastJumpEffect = 0;
    private List<Long> actionsPerMinute = new ArrayList<Long>(30);
    private InformationToolType infoType = InformationToolType.ChestInformation;
    private int spikedTime = 0;
    private double oldHealthLevel = 0;
    private int oldFoodLevel = 0;
    private long chatSoundSettings = 0;
    private Timestamp lastVoteTimestamp = null;

    /**
     * Returns if the player is currently in tractor mode.
     *
     * @return
     */
    public boolean isTractorRunning() {
        return isTractorRunning;
    }

    /**
     * Sets if the player is in tractor mode.
     *
     * @param isTractorRunning
     */
    public void setTractorRunning(boolean isTractorRunning) {
        this.isTractorRunning = isTractorRunning;
    }

    /**
     * Gets the informationToolType for the information tool
     *
     * @return The tooltype, default is ChestInformation
     */
    public InformationToolType getInformationToolType() {
        return infoType;
    }

    /**
     * Sets the informationToolType for the information tool
     */
    public void setInformationToolType(InformationToolType infoType) {
        this.infoType = infoType;
    }

    /**
     * Returns the last time the player correctly interacted with a give sign.
     */
    public long getLastSignInteract() {
        return signInteract;
    }

    /**
     * Sets the last time the player correctly interacted with a give sign.
     *
     * @param newTime
     */
    public void setLastSignInteract(long newTime) {
        this.signInteract = newTime;
    }

    /**
     * Sets the status of the player's bonus messages
     *
     * @param status
     */
    public void setDisplayBonusMessage(boolean status) {
        setSetting(PlayerSettings.BONUS_MESSAGE, !status);
    }

    /**
     * Gets the toggled bonus message status for the player
     *
     * @return the status
     */
    public boolean getDisplayBonusMessage() {
        return hasSetting(PlayerSettings.BONUS_MESSAGE);
    }

    /**
     * Returns whether or not the player will pick up items.
     *
     * @return
     */
    public boolean canPickUpItems() {
        return hasSetting(PlayerSettings.ITEM_PICKUP);
    }

    /**
     * Returns whether or not the player will accept teleports.
     *
     * @return
     */
    public boolean canAcceptTeleport() {
        return hasSetting(PlayerSettings.ALLOW_TP);
    }

    /**
     * Returns whether or not the player requires a teleport request
     *
     * @return
     */
    public boolean allowsInstantTeleporting() {
        return hasSetting(PlayerSettings.INSTANT_TP);
    }

    /**
     * Returns whether or not the player can starve
     */
    public boolean canPlayerStarve() {
        return hasSetting(PlayerSettings.HUNGER);
    }

    /**
     * Returns the milis of the time whern the last effect was set
     *
     * @return
     */
    public long getLastJumpEffect() {
        return lastJumpEffect;
    }

    /**
     * Sets the currentTimeMilis when the last effect was set.
     *
     * @param lastJumpEffect
     */
    public void setLastJumpEffect(long lastJumpEffect) {
        this.lastJumpEffect = lastJumpEffect;
    }

    /**
     * Returns the milis of the time whern the last effect was set
     *
     * @return
     */
    public long getLastBlindnessEffect() {
        return lastBlindnessEffect;
    }

    /**
     * Sets the currentTimeMilis when the last effect was set.
     *
     * @param lastBlindnessEffect
     */
    public void setLastBlindnessEffect(long lastBlindnessEffect) {
        this.lastBlindnessEffect = lastBlindnessEffect;
    }

    /**
     * Indicates if the player is allowed to fly
     *
     * @return
     */
    public boolean isCanFly() {
        return canFly;
    }

    /**
     * Set if the player is allowed to fly or not
     *
     * @param canFly
     */
    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    /**
     * Returns how long the player is spiked for..
     *
     * @return
     */
    public int getSpiked() {
        return spikedTime;
    }

    /**
     * Set how long the player is spiked for..
     *
     * @param spikedTime
     */
    public void setSpiked(int spikedTime) {
        this.spikedTime = spikedTime;
    }

    /**
     * Indicates if the player has godmode
     *
     * @return
     */
    public boolean hasGodmode() {
        return hasSetting(PlayerSettings.GOD);
    }

    /**
     * Sets godmode
     */
    public void setGodmode(boolean hasGodmode) {
        setSetting(PlayerSettings.GOD, hasGodmode);
    }

    /**
     *
     * @return
     */
    public boolean isJailed() {
        return isJailed;
    }

    /**
     * Modify if the player is jailed or not
     *
     * @param isJailed
     */
    public void setJailed(boolean isJailed) {
        this.isJailed = isJailed;
    }

    /**
     * Sets the player muted ticks Anything above 0 = timed mute 0 = Not muted
     * -1 = Indefinitely muted
     *
     * @param isMuted
     */
    public void setMuteTicks(long muteTicks) {
        this.muteTicks = (muteTicks < -1 ? -1 : muteTicks);
    }

    /**
     * Get the player's remainingmute ticks Also unmutes the player
     *
     * @return
     */
    public long getRemainingMuteTicks() {
        if (muteTicks == 0 || muteTicks == -1) {
            return muteTicks;
        }

        long diff = (muteTicks - System.currentTimeMillis());

        if (diff < 0) {
            muteTicks = 0;
            return muteTicks;
        }

        return (muteTicks - System.currentTimeMillis());
    }

    /**
     * Indicates if the player can move around or not
     *
     * @return
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Sets the player's freeze status (if they can move around)
     *
     * @param isMuted
     */
    public void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    /**
     * Returns the name of the last sender of a whisper
     *
     * @return
     */
    public IdpCommandSender<? extends CommandSender> getLastWhisperFrom() {
        return this.lastWhisperFrom;
    }

    /**
     * Sets the name of the last sender of a whisper
     *
     * @param name
     */
    public void setLastWhisperFrom(IdpCommandSender<? extends CommandSender> name) {
        this.lastWhisperFrom = name;
    }

    /**
     * Sets the location the player was at before they teleported
     */
    public void setLastTeleportLocation(Location location) {
        this.lastTeleportLocation = location;
    }

    /**
     * Gets the location the player was at before they teleported
     */
    public Location getLastTeleportLocation() {
        return this.lastTeleportLocation;
    }

    /**
     * Sets the last time the player teleported.
     */
    public void setLastTeleportTime(long lastTeleportTime) {
        this.lastTeleportTime = lastTeleportTime;
    }

    /**
     * Gets the last time the player teleported.
     */
    public long getLastTeleportTime() {
        return this.lastTeleportTime;
    }

    /**
     * Checks if the last block moveing occured longer then <b>100</b>ms ago
     *
     * @return
     */
    public boolean canMoveBlockAgain() {
        return System.currentTimeMillis() - lastBlockMoved > 100;
    }

    /**
     * Indicated that a player has moved a block
     */
    public void blockMoved() {
        lastBlockMoved = System.currentTimeMillis();
    }

    /**
     * Adds the current time to the list of actions per minute
     */
    public void addActionPerMinute() {
        actionsPerMinute.add(System.currentTimeMillis());
    }

    /**
     * The number of actions the player has performed in the last X seconds.
     * This value cannot be over 5 minutes, as that is the threshold for when
     * the list is cleaned up
     */
    public int getActionCount(int withinSeconds) {
        long now = System.currentTimeMillis(), time;
        int ret = 0;
        for (Iterator<Long> it = actionsPerMinute.iterator(); it.hasNext();) {
            time = it.next();
            if (now - time <= withinSeconds * 1000) {
                ret++;
            }
            if (now - time > 300000) { //5 minutes
                it.remove(); //cleanup
            }
        }
        return ret;
    }

    /**
     * The number of actions the player has performed in the last minute
     */
    public int getActionsPerMinute() {
        return getActionCount(60);
    }

    /**
     * Returns true if the action count in last 1 minute is too low
     */
    public boolean isAFK() {
        return getActionsPerMinute() < 40;
    }

    /**
     * Sets the old health level of the player
     *
     * @param oldHealthLevel
     */
    public void setOldHealthLevel(double oldHealthLevel) {
        this.oldHealthLevel = oldHealthLevel;
    }

    /**
     * Gets the old health level of the playe
     *
     * @return
     */
    public double getOldHealthLevel() {
        return oldHealthLevel;
    }

    /**
     * Sets the old food level of the player
     *
     * @param oldFoodLevel
     * @return
     */
    public void setOldFoodLevel(int oldFoodLevel) {
        this.oldFoodLevel = oldFoodLevel;
    }

    /**
     * Gets the old food level of the player
     *
     * @return
     */
    public int getOldFoodLevel() {
        return oldFoodLevel;
    }

    /**
     * Sets the specified chat sound setting
     * @param csg
     * @param enable
     */
    public void setChatSoundSetting(ChatSoundSetting csg, boolean enable) {
        if (enable) {
            chatSoundSettings |= csg.getChatBit();
        } else {
            chatSoundSettings &= ~csg.getChatBit();
        }
    }

    /**
     * Checks if the player has the specified sound setting. Setting
     * check is inverted to allow for new possible sounds to be added
     * in the future and to be set
     * @param csg
     * @return
     */
    public boolean hasChatSoundSetting(ChatSoundSetting csg) {
        boolean enabled = ((chatSoundSettings & csg.getChatBit()) == csg.getChatBit());

        // Double check that they are in the group that can have this setting
        return (enabled && getGroup().equalsOrInherits(csg.getMinGroup()));
    }

    /**
     * Loads the chat sound settings from the database
     */
    public void loadChatSoundSettings() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT chat_sound_settings FROM players WHERE player_id = ?;");
            statement.setString(1, getUniqueId().toString());
            set = statement.executeQuery();

            if (set.next()) {
                chatSoundSettings = set.getLong("chat_sound_settings");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load chat settings for " + getRealName() + ": ", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }
    }

    /**
     * Saves the player's chat sound settings to the database
     */
    public void saveChatSoundSettings() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players SET chat_sound_settings = ? WHERE player_id = ?;");
            statement.setLong(1, chatSoundSettings);
            statement.setString(2, getUniqueId().toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Could not save chat sound settings for " + getRealName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Gets the last vote timestamp for this player
     * @return
     */
    public Timestamp getLastVoteTimestamp() {
        if (lastVoteTimestamp == null) {
            PreparedStatement statement = null;
            ResultSet set = null;

            try {
                statement = DBManager.prepareStatement("SELECT timestamp FROM vote_log WHERE player_id = ? ORDER BY timestamp DESC LIMIT 1;");
                statement.setString(1, playerId.toString());
                set = statement.executeQuery();

                if (set.next()) {
                    lastVoteTimestamp = set.getTimestamp("timestamp");
                }
            } catch (SQLException ex) {
                InnPlugin.logError("Cannot get timestamp for " + getRealName() + "!", ex);
            } finally {
                DBManager.closePreparedStatement(statement);
                DBManager.closeResultSet(set);
            }
        }

        return lastVoteTimestamp;
    }

    /**
     * Sets the last vote timestamp for this player
     * @param lastVoteTimestamp
     * @return
     */
    public void setLastVoteTimestamp(Timestamp lastVoteTimestamp) {
        this.lastVoteTimestamp = lastVoteTimestamp;
    }

    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Damage status and PvP checks">
    private long lastDamageTick = 0L;
    private long lastPvPTick = 0L;
    private long pvpImmuneTime = 0L;
    private Map<UUID, List<Long>> pvpList = new HashMap<UUID, List<Long>>();

    /**
     * Adds a kill to the pvp kill list
     *
     * @param playerId
     */
    public void addPvpKill(UUID playerId) {
        if (!pvpList.containsKey(playerId)) {
            List<Long> pvpTimes = new ArrayList<Long>();
            pvpTimes.add(System.currentTimeMillis());
            pvpList.put(playerId, pvpTimes);
        } else {
            List<Long> pvpTimes = new ArrayList<Long>(pvpList.get(playerId));
            pvpTimes.add(System.currentTimeMillis());
            pvpList.put(playerId, pvpTimes);
        }
    }

    /**
     * Gets the amount of times this player has killed the target player. Kills
     * past the PvP retention time will not be counted.
     *
     * @param playerId ID of the player
     * @return amount of kills
     */
    public int getPvpKillTotalOf(UUID playerId) {
        if (!pvpList.containsKey(playerId)) {
            return 0;
        }

        int count = 0;
        long now = System.currentTimeMillis();
        ArrayList<Long> pvpTimes = new ArrayList<Long>(pvpList.get(playerId));

        for (long time : pvpTimes) {
            if (now - time < Configuration.PLAYER_PVP_KILL_RETENTION) {
                count++;
            }
        }

        return count;
    }

    /**
     * Cleans up expired PvP kills
     */
    public void cleanupPvpKills() {
        long time, now = System.currentTimeMillis();
        for (Iterator<Entry<UUID, List<Long>>> it = pvpList.entrySet().iterator(); it.hasNext();) {
            Entry<UUID, List<Long>> entry = it.next();

            for (Iterator<Long> it2 = entry.getValue().iterator(); it2.hasNext();) {
                time = it2.next();

                if (now - time > Configuration.PLAYER_PVP_KILL_RETENTION) {
                    it2.remove();
                }
            }

            if (entry.getValue().isEmpty()) {
                it.remove();
            }
        }
    }

    /**
     * Sets the PvP immune time of the player
     *
     * @param durationSeconds
     */
    public void setPvPImmuneTime(double durationSeconds) {
        pvpImmuneTime = System.currentTimeMillis() + (long) (durationSeconds * 1000);
    }

    /**
     * Checks if the player is currently PvP immune
     *
     * @return
     */
    public boolean isPvPImmune() {
        return (pvpImmuneTime > System.currentTimeMillis());
    }

    /**
     * Sets the PvP state time of the player
     */
    public void setPvPStateTime() {
        lastPvPTick = System.currentTimeMillis();
    }

    /**
     * Resets the player's PvP state time
     */
    public void resetPvPStateTime() {
        lastPvPTick = 0L;
    }

    /**
     * Checks if the player is in the PvP state
     *
     * @return
     */
    public boolean isInPvPState() {
        boolean pvpStatus = ((System.currentTimeMillis() - lastPvPTick) <= Configuration.PLAYER_DAMAGE_STATE_TIME);

        if (pvpStatus) {
            return !hasPermission(Permission.special_damage_state_bypass);
        } else {
            return false;
        }
    }

    /**
     * Sets the damage state time of the player
     */
    public void setDamageStateTime() {
        this.lastDamageTick = System.currentTimeMillis();
    }

    /**
     * Resets the player's damage state time
     */
    public void resetDamageStateTime() {
        lastDamageTick = 0L;
    }

    /**
     * Checks if the player is in the damage state
     *
     * @return
     */
    public boolean isInDamageState() {
        boolean damageState = ((System.currentTimeMillis() - lastDamageTick) <= Configuration.PLAYER_DAMAGE_STATE_TIME);

        if (damageState) {
            // Since damage state is true, only allow if they don't have the bypass
            // or if they do, their damage state setting is toggled
            return !hasPermission(Permission.special_damage_state_bypass);
        } else {
            return false;
        }
    }

    /**
     * Gets the time left until the player is no longer in the damage state
     *
     * @return
     */
    public String getDamageStatusDuration() {
        long timeLeft = Configuration.PLAYER_DAMAGE_STATE_TIME - (System.currentTimeMillis() - lastDamageTick);

        if (timeLeft >= 1000) {
            return DateUtil.getTimeString(timeLeft, true);
        } else {
            // Less than 1 second has a custom message
            return "less than a second";
        }
    }

    /**
     * Sets and saves the personal PvP mode setting
     */
    public void setPersonalPvpEnabled(boolean enabled) {
        setSetting(PlayerSettings.PVP, enabled);
    }

    /**
     * Checks if the player's personal PvP mode is enabled
     *
     * @return
     */
    public boolean isPersonalPvpEnabled() {
        return hasSetting(PlayerSettings.PVP);
    }

    /**
     * Checks if the lot the player is on has PvP enabled, if not return the
     * personal PvP setting. If you are wanting to see if a player can hit
     * another player, use PvpHandler.playerCanHit() instead!
     *
     * @return
     */
    public boolean isPvpEnabled() {
        if (isPersonalPvpEnabled()) {
            return true;
        }

        InnectisLot testLot = getLastLot();

        if (testLot == null) {
            testLot = LotHandler.getLot(getLastLocation());
        }

        if (testLot != null) {
            return testLot.isFlagSet(LotFlagType.PVP);
        } else {
            return false;
        }
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Portal">
    private long portalCooldownTaskId = -1L;

    /**
     * Gets the ID of the portal cooldown task
     *
     * @return
     */
    public long getPortalCooldownTaskId() {
        return portalCooldownTaskId;
    }

    /**
     * Sets the ID of the portal cooldown task
     *
     * @param portalCooldownTaskId
     */
    public void setPortalCooldownTaskId(long portalCooldownTaskId) {
        this.portalCooldownTaskId = portalCooldownTaskId;
    }

// </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="LastDeath">
    private long _lastDeath = 0L;

    /**
     * Returns the currentTimeMilis of the last time the player died
     *
     * @return
     */
    public long getLastDeath() {
        return _lastDeath;
    }

    /**
     * Sets the millis the player last died
     *
     * @param _lastDeath
     */
    public void setLastDeath(long lastDeath) {
        this._lastDeath = lastDeath;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Last Location">
    // This fixes a bug that would make a user crash when walking out of the map's bound or into a lot where he/she is banned from
    private Location lastLocation = null;

    /**
     * Gets the last known location of the player
     *
     * @return
     */
    public Location getLastLocation() {
        if (this.lastLocation == null) {
            this.lastLocation = WarpHandler.getSpawn();
        }
        return this.lastLocation;
    }

    /**
     * Sets the last known location. This uses the original location, so take
     * care when returning this to not modify it directly
     *
     * @param location
     */
    public void setLastLocation(Location location) {
        if (location == null) {
            lastLocation = null;
        } else {
            lastLocation = location;
        }
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Last Lot Data">
    // This stores the last lot a player was in so it doesn't have to be looked up again
    private InnectisLot lastLot = null;
    private LotEnterLeaveTime lotEnterLeave = new LotEnterLeaveTime();

    /**
     * Gets the last known lot of the player
     *
     * @return
     */
    public InnectisLot getLastLot() {
        return this.lastLot;
    }

    /**
     * Sets the last known lot
     *
     * @param location
     */
    public void setLastLot(InnectisLot lot) {
        this.lastLot = lot;
    }

    /**
     * Gets the data for the lot enter and leave time
     * @return
     */
    public LotEnterLeaveTime getLotEnterLeave() {
        return lotEnterLeave;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Requests">
    /**
     * Map with requests given to the player, when no requests this map will be
     * null
     */
    private Map<Long, Request> requests;

    /**
     * Returns all open requests of the player
     *
     * @return
     */
    public synchronized List<Request> getRequests() {
        if (requests == null) {
            return new ArrayList<Request>();
        } else {
            return new ArrayList<Request>(requests.values());
        }
    }

    /**
     * Remove a request from the player. This will not do anything with the
     * request itself!
     *
     * This will make the request set null if it was the last request.
     *
     * @param requestid
     * @return the Request
     */
    public synchronized Request removeRequest(Long requestid) {
        if (requests == null) {
            return null;
        } else {
            Request req = requests.remove(requestid);
            if (requests.isEmpty()) {
                requests = null;
            }
            return req;
        }
    }

    /**
     * Adds an new request to the requestmap
     *
     * @param request
     * @return false when the player that issued the request already got an
     * active request with the player
     */
    public synchronized boolean addRequest(Request request) {
        if (requests == null) {
            requests = new HashMap<Long, Request>(1);
        }

        UUID currentRequesterId = request.getRequesterId();

        for (Request req : requests.values()) {
            if (req != null) {
                IdpPlayer requester = req.getRequester();

                if (requester != null && currentRequesterId.equals(requester.getUniqueId())) {
                    return false;
                }
            }
        }
        requests.put(request.getRequestid(), request);
        return true;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Chat">
    // Holds the list of ignored players
    private List<PlayerCredentials> ignoredPlayers = new ArrayList<PlayerCredentials>();
    private ChatInjector injector = null;

    /**
     * Adds a new player to the ignored list
     *
     * @param credentials
     * @return false if the player cannot be ignored
     */
    public boolean addIgnoredUser(PlayerCredentials credentials) {
        PlayerSession session = null;
        boolean isTempSession = false;

        IdpPlayer p = server.getPlayer(credentials.getUniqueId());

        if (p != null) {
            session = p.getSession();
        } else {
            session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), server, true);
            isTempSession = true;
        }

        if (session.hasPermission(Permission.chat_ignore_overide)) {
            // If the player isn't online, we can just destroy their session
            if (isTempSession) {
                session.destroy();
            }

            return false;
        }

        ignoredPlayers.add(credentials);
        addIgnoredPlayerToDB(credentials.getUniqueId(), credentials.getName());

        return true;
    }

    /**
     * This removes a player from the ignore list lowercase
     *
     * @param playerName
     */
    public void removeIgnoredUser(String playerName) {
        PlayerCredentials removeCredentials = null;

        for (Iterator<PlayerCredentials> it = ignoredPlayers.iterator(); it.hasNext();) {
            PlayerCredentials credentials = it.next();

            if (credentials.getName().equalsIgnoreCase(playerName)) {
                removeCredentials = credentials;
                it.remove();
                break;
            }
        }

        if (removeCredentials == null) {
            return;
        }

        removeIgnoredPlayerFromDB(removeCredentials);
    }

    /**
     * Clears the ignored players list
     */
    public void clearIgnoredPlayers() {
        ignoredPlayers.clear();
        clearIgnoredPlayersFromDB();
    }

    /**
     * Checks if the player is ignored or not
     *
     * @param playerName
     * @return
     */
    public boolean isIgnored(String playerName) {
        IdpPlayer testPlayer = server.getPlayer(playerName);

        // If a staff member is on the ignore list somehow and they have ignore override
        // then return false because they can't be ignored
        if (testPlayer != null && testPlayer.hasPermission(Permission.chat_ignore_overide)) {
            return false;
        }

        for (PlayerCredentials pc : ignoredPlayers) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a list of ignored players.
     *
     * @return unmodifiable list of ignored players
     */
    public List<String> getIgnoredPlayers() {
        List<String> ignored = new ArrayList<String>();

        for (PlayerCredentials credentials : ignoredPlayers) {
            ignored.add(credentials.getName());
        }

        return ignored;
    }

    /**
     * Loads the ignored players from database
     */
    private void loadIgnoredPlayersFromDB() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM players_ignored where player_id = ?;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            while (set.next()) {
                String ignoredPlayerIdString = set.getString("ignored_player_id");
                UUID ignoredPlayerId = UUID.fromString(ignoredPlayerIdString);

                if (ignoredPlayerId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                    ignoredPlayers.add(Configuration.EVERYONE_CREDENTIALS);
                } else {
                    PlayerCredentials ignoredPlayerCredentials = PlayerCredentialsManager.getByUniqueId(ignoredPlayerId, true);
                    ignoredPlayers.add(ignoredPlayerCredentials);
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load ignore list for " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Adds the ignored player to the database
     */
    private void addIgnoredPlayerToDB(UUID ignoredPlayerId, String ignoredPlayerName) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO players_ignored (player_id, ignored_player_id) VALUES (?, ?);");
            statement.setString(1, playerId.toString());
            statement.setString(2, ignoredPlayerId.toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Failed to add ignored player " + ignoredPlayerName + " for " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * playerId the ignored player from the database
     *
     * @param player
     */
    private void removeIgnoredPlayerFromDB(PlayerCredentials credentials) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM players_ignored WHERE player_id = ? AND ignored_player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.setString(2, credentials.getUniqueId().toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Could not remove ignored player " + credentials.getName() + " from " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Clears all ignored players from this player from the database
     */
    private void clearIgnoredPlayersFromDB() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM players_ignored WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Could not clear all ignored players from " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Checks if the player is muted.
     *
     * @return
     */
    public boolean isMuted() {
        return muteTicks == -1 || getRemainingMuteTicks() > 0;
    }

    /**
     * Checks if the player can see filtered chat
     *
     * @return
     */
    public boolean canSeeFilteredChat() {
        return !hasSetting(PlayerSettings.CHAT_FILTER);
    }

    /**
     * Checks if the player can hear muted players.
     *
     * @return
     */
    public boolean canHearMuted() {
        return hasSetting(PlayerSettings.HEAR_MUTED);
    }

    /**
     * Sets whether the player can hear muted players.
     *
     * @param trigger
     */
    public void setCanHearMuted(boolean trigger) {
        setSetting(PlayerSettings.HEAR_MUTED, trigger);
    }

    /**
     * Sets the chatinjector that overrides the chatevents for this player
     *
     * @param injector
     */
    public void setChatInjector(ChatInjector injector) {
        this.injector = injector;
    }

    /**
     * Returns the chatinjector that is linked to this player
     *
     * @return the injector or null if none.
     */
    public ChatInjector getChatInjector() {
        return this.injector;
    }

    /**
     * Checks if this
     *
     * @return
     */
    public boolean hasInjector() {
        return getChatInjector() != null;
    }

    // </editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Bank Transaction">
    private long transferTaskId = 0;
    private long lastBankTaskTime = 0;

    /**
     * Gets the ID of the bank transfer task ID
     *
     * @return
     */
    public long getBankTaskId() {
        return transferTaskId;
    }

    /**
     * Sets the ID of the bank transfer task ID
     *
     * @param transferTaskId
     */
    public void setBankTaskId(long transferTaskId) {
        this.transferTaskId = transferTaskId;
    }

    /**
     * Gets the last bank task time
     *
     * @return
     */
    public long getLastBankTaskTime() {
        return lastBankTaskTime;
    }

    /**
     * Sets the last bank task time
     *
     * @param lastBankTaskTime
     */
    public void setLastBankTaskTime(long lastBankTaskTime) {
        this.lastBankTaskTime = lastBankTaskTime;
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Chat Channels">
    private Map<Integer, String> channels = new HashMap<Integer, String>();

    /*
     * Returns an unmodifiable map of the player's channels
     * @return
     */
    public Map<Integer, String> getChannels() {
        return Collections.unmodifiableMap(channels);
    }

    /**
     * Assigns a free channel number to the specified channel name
     *
     * @param channelName
     * @return
     */
    public int makeNextChannelAndNumber(String channelName) {
        int freenum = 1;

        while (channels.containsKey(freenum)) {
            freenum++;
        }
        channels.put(freenum, channelName);

        return freenum;
    }

    /**
     * Gets the channel name by personal channel number
     *
     * @param num
     * @return
     */
    public String getChannelNameFromNumber(int num) {
        return channels.get(num);
    }

    /**
     * Gets a player's personal channel number from its channel name if channel
     * not found.
     *
     * @param channelName
     */
    public int getNumberFromChannelName(String channelName) {
        for (int num : channels.keySet()) {
            String name = channels.get(num);

            if (name.equalsIgnoreCase(channelName)) {
                return num;
            }
        }
        return 0;
    }

    /**
     * Removes the specified channel number
     *
     * @param num
     */
    public void removeChannelNumber(int num) {
        channels.remove(num);

        // Don't merge IDs if channel list is empty
        if (channels.isEmpty()) {
            return;
        }

        List<String> channelNames = Lists.newArrayList(channels.values());
        channels.clear();

        PreparedStatement statement = null;
        UUID playerId = getUniqueId();
        int idx = 1;

        try {
            statement = DBManager.prepareStatement("DELETE FROM channel_members WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.execute();
            DBManager.closePreparedStatement(statement);

            for (String channelName : channelNames) {
                ChatChannel channel = ChatChannelHandler.getChannel(channelName);
                MemberDetails details = channel.getMemberDetails(playerName);
                ChatChannelGroup group = details.getGroup();

                details.setPersonalNumber(idx);

                statement = DBManager.prepareStatement("INSERT INTO channel_members VALUES (?, ?, ?, ?);");
                statement.setInt(1, channel.getId());
                statement.setString(2, playerId.toString());
                statement.setInt(3, idx);
                statement.setInt(4, group.getId());
                statement.execute();
                DBManager.closePreparedStatement(statement);

                channels.put(idx, channelName);
                idx++;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot merge channel IDs in database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Renames a channel to the new specified name
     *
     * @param oldChannel
     * @param newChannel
     */
    public void renameChannel(String oldChannel, String newChannel) {
        int personalNum = 0;
        boolean found = false;

        for (Iterator<Integer> it = channels.keySet().iterator(); it.hasNext();) {
            personalNum = it.next();
            String testChannel = channels.get(personalNum);

            if (testChannel.equalsIgnoreCase(oldChannel)) {
                it.remove();
                found = true;
                break;
            }
        }

        if (found) {
            channels.put(personalNum, newChannel);
        }
    }

    /**
     * Loads all the personal channel numbers for this player
     */
    public void loadPersonalChannelNumbers() {
        if (channels.isEmpty()) {
            PreparedStatement statement = null;
            ResultSet set = null;

            try {
                statement = DBManager.prepareStatement("SELECT * FROM channel_members WHERE player_id = ?;");
                statement.setString(1, playerId.toString());
                set = statement.executeQuery();

                while (set.next()) {
                    int id = set.getInt("channelid");
                    String name = ChatChannelHandler.getNameFromId(id);
                    int num = set.getInt("personalnum");
                    channels.put(num, name);
                }
            } catch (SQLException ex) {
                InnPlugin.logError("Unable to load personal channel numbers for " + playerName + "!", ex);
            } finally {
                DBManager.closeResultSet(set);
                DBManager.closePreparedStatement(statement);
            }
        }
    }

    /**
     * Puts the player back in all channels they were in, setting their member
     * status to online
     */
    public void joinAllChatChannels() {
        for (String channelName : channels.values()) {
            ChatChannel channel = ChatChannelHandler.getChannel(channelName);
            channel.setMemberStatus(playerName, true);
            channel.sendGeneralMessage(getColoredName() + ChatColor.AQUA + " has joined the channel.");
        }
    }

    /**
     * Leaves all channels, setting the player to offline in each channel
     */
    public void leaveAllChatChannels() {
        for (String channelName : channels.values()) {
            ChatChannel channel = ChatChannelHandler.getChannel(channelName, false);
            channel.sendGeneralMessage(getColoredName() + ChatColor.AQUA + " has left the channel.");
            channel.setMemberStatus(playerName, false);

            if (channel.isAllOffline()) {
                ChatChannelHandler.unloadChannel(channelName);
            }
        }
        channels.clear();
    }
    // </editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Edit Sign Wand">
    private EditSignWand editSignWand = null;

    /**
     * Gets the current edit sign wand
     *
     * @return
     */
    public EditSignWand getEditSignWand() {
        if (editSignWand == null) {
            editSignWand = new EditSignWand();
        }

        return editSignWand;
    }

    /**
     * Clears the edit sign wand
     */
    public void clearEditSignWand() {
        editSignWand = null;
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Sign Copy/Pasting">
    private String[] signArray;

    /**
     * Sets the lines for a sign that the player has.
     *
     * @param signText
     */
    public void setSignLines(String[] signText) {
        signArray = signText.clone();
    }

    /**
     * Clears the lines of text the player has for signs.
     */
    public void clearSignLines() {
        signArray = null;
    }

    /**
     * Checks if the player has any signlines copied
     *
     * @return
     */
    public boolean hasSignLines() {
        return signArray != null;
    }

    /**
     * Gets the lines of the sign that the player has
     *
     * @return null if none.
     */
    public String[] getSignLines() {
        return signArray;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="TinyWE">
    private IdpEditSession _editsession;
    private RegionClipboard clipboard;

    /**
     * Sets the clipboard this player has copied or cut.
     *
     * @param clipboard T
     */
    public void setClipboard(RegionClipboard clipboard) {
        this.clipboard = clipboard;
    }

    /**
     * The clipboard of the area the player has copied or cut.
     *
     * @return the clipboard or null if none.
     */
    public RegionClipboard getClipboard() {
        return clipboard;
    }

    /**
     * Returns the session if it exists. Otherwise it will create a new session
     *
     * @return
     */
    public IdpEditSession getEditSession() {
        if (_editsession == null) {
            _editsession = new IdpEditSession(playerId);
        }
        return _editsession;
    }

    /**
     * Checks if the user has a session
     *
     * @return
     */
    public boolean hasEditSession() {
        return (_editsession == null);
    }

    /**
     * Clears the session
     */
    public void destroyEditSession() {
        _editsession = null;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Entity Teleporting">
    // Represents all the caught entities from the fishing rod
    private List<EntityTraits> caughtEntityTraits = new ArrayList<EntityTraits>();
    private boolean renameOwners = false;

    public void setCaughtEntities(List<EntityTraits> caughtEntities) {
        this.caughtEntityTraits.clear();
        this.caughtEntityTraits.addAll(caughtEntities);
    }

    /**
     * Adds a caught entity for this player
     *
     * @param ent
     * @return 1 if successfully caught, 0 if too full, 2 if already exists
     */
    public int addCaughtEntityTraits(EntityTraits ent) {
        if (caughtEntityTraits.size() <= 40
                || hasPermission(Permission.entity_catchinfiniteentities)) {
            for (EntityTraits trait : caughtEntityTraits) {
                if (trait.getUniqueId().equals(ent.getUniqueId())) {
                    return 2;
                }
            }

            caughtEntityTraits.add(ent);
            return 1;
        } else {
            return 0;
        }
    }

    public List<EntityTraits> getCaughtEntityTraits() {
        return caughtEntityTraits;
    }

    public void removeCaughtEntityTraits() {
        caughtEntityTraits.clear();
    }

    public int countCaughtEntityTraits() {
        return caughtEntityTraits.size();
    }

    public boolean getRenameOwners() {
        return renameOwners;
    }

    public void setRenameOwners(boolean renameOwners) {
        this.renameOwners = renameOwners;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Jumped">
    private boolean isJumped = false;

    public void setJumped(boolean isJumped) {
        this.isJumped = isJumped;
    }

    public boolean isJumped() {
        return isJumped;
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Allow flint and steel">
    private boolean isUsingFlintSteel = false;

    public void setUsingFintSteel(boolean isUsingFlintSteel) {
        this.isUsingFlintSteel = isUsingFlintSteel;
    }

    public boolean isUsingFlintSteel() {
        return this.isUsingFlintSteel;
    }
    // </editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Player mail">
    private List<MailMessage> mail = new ArrayList<MailMessage>();
    private boolean hasQueriedMail = false;

    /**
     * Indicates if the mail has been queried from db
     *
     * @return
     */
    public boolean hasQueriedMail() {
        return this.hasQueriedMail;
    }

    /**
     * Sets whether the mail has been queried or not
     *
     * @param hasQueriedMail
     */
    public void setQueriedMail(boolean hasQueriedMail) {
        this.hasQueriedMail = hasQueriedMail;
    }

    /**
     * Adds mail for the player
     *
     * @param obj
     */
    public void addMail(MailMessage obj) {
        mail.add(obj);
    }

    /**
     * Gets all the user's mail
     *
     * @return
     */
    public List<MailMessage> getAllMail() {
        return mail;
    }

    /**
     * Gets mail for the player
     *
     * @return
     */
    public MailMessage getMail(int mailNo) {
        if (mailNo > mail.size() || mailNo < 1) {
            return null;
        }

        return mail.get(mailNo - 1);
    }

    /**
     * Removes the specified mail from the player
     *
     * @param mailNo
     * @return
     */
    public MailMessage removeMail(int mailNo) {
        MailMessage obj = null;

        if (mailNo > mail.size() || mailNo < 1) {
            return null;
        }
        obj = mail.remove(mailNo - 1);
        return obj;
    }

    /**
     * Deletes all mail for the user
     */
    public void deleteAllMail() {
        mail = new ArrayList<MailMessage>();
    }

    /**
     * Counts the player's messages
     *
     * @param onlyRead indicates whether to return read messages or not
     * @return
     */
    public int countMail(boolean onlyUnread) {
        int count = 0;

        for (MailMessage obj : mail) {
            if (!obj.hasRead() || !onlyUnread) {
                count++;
            }
        }

        return count;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Ender Chest Editing">
    /**
     * These are necessary to keep track of ender chest edits
     */

    private UUID enderchestOwnerId = null;
    private EnderContentsType enderchestType = null;
    private boolean isViewingEnderChest = false;

    /**
     * Sets if this player is viewing an ender chest
     *
     * @param viewing
     */
    public void setViewingEnderChest(boolean viewing) {
        isViewingEnderChest = viewing;
    }

    /**
     * Gets if this player is viewing an ender chest
     *
     * @return
     */
    public boolean isViewingEnderChest() {
        return isViewingEnderChest;
    }

    /**
     * Sets the ID of the owner of the ender chest being viewed
     *
     * @param enderchestOwnerId
     */
    public void setEnderchestOwnerId(UUID enderchestOwnerId) {
        this.enderchestOwnerId = enderchestOwnerId;
    }

    /**
     * Gets the ID of the ender chest being viewed
     *
     * @return
     */
    public UUID getEnderchestOwnerId() {
        return enderchestOwnerId;
    }

    /**
     * Sets the chest type of the ender chest being viewed
     *
     * @param enderchestType
     */
    public void setEnderchestType(EnderContentsType enderchestType) {
        this.enderchestType = enderchestType;
    }

    /**
     * Gets the chest type of the ender chest being viewed
     *
     * @return
     */
    public EnderContentsType getEnderchestType() {
        return enderchestType;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Entity Mounting">
    private Entity passengerEntity = null;

    /**
     * Sets whether a player may be sat on with a saddle
     *
     * @param canSitSaddle
     */
    public void setCanSitSaddle(boolean canSitSaddle) {
        setSetting(PlayerSettings.SADDLE, canSitSaddle);
    }

    /**
     * Returns whether a player may be sat on with a saddle
     *
     * @return
     */
    public boolean canSitSaddle() {
        return hasSetting(PlayerSettings.SADDLE);
    }

    /**
     * Sets a passenger entity
     *
     * @param entity
     */
    public void setPassengerEntity(Entity entity) {
        passengerEntity = entity;
    }

    /**
     * Gets a passenger entity
     *
     * @return
     */
    public Entity getPassengerEntity() {
        return passengerEntity;
    }

    public void clearPassengerEntity() {
        passengerEntity = null;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Death Messages">
    String nextDeathMessage = null;

    /**
     * Sets the status of the player's death message
     *
     * @param status
     */
    public void setDeathMsgStatus(boolean status) {
        setSetting(PlayerSettings.DEATHMESSAGE, !status);
    }

    /**
     * Gets the toggled death message status for the player
     *
     * @return the status
     */
    public boolean getDeathMsgStatus() {
        return hasSetting(PlayerSettings.DEATHMESSAGE);
    }

    /*
     * Gets the death message set for the player.
     */
    public String getDeathMessage() {
        return nextDeathMessage;
    }

    /*
     * Sets the message displayed on next death
     */
    public void setDeathMessage(String newMessage) {
        nextDeathMessage = newMessage;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Player Backpack">
    private PlayerBackpack backpack = null;

    /**
     * Gets the player's backpack
     */
    public PlayerBackpack getBackpack() {
        if (backpack == null) {
            backpack = PlayerBackpack.loadBackpackFromDB(playerId, playerName);

            if (backpack == null) {
                throw new RuntimeException("Cannot load backpack!");
            }
        }
        return backpack;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Modifiable Permissions">
    private ModifiablePermissions modifiablePermissions = null;

    /**
     * Gets the modified permissions object
     *
     * @return
     */
    public final ModifiablePermissions getModifiablePermissions() {
        if (modifiablePermissions == null) {
            modifiablePermissions = ModifiablePermissionsHandler.loadModifiedPermissions(playerId);
        }

        return modifiablePermissions;
    }

    /**
     * Indicates if the specified permission exists in the group or modifiable
     * group
     *
     * @param perm
     * @return
     */
    public boolean hasPermission(Permission perm) {
        // Don't allow none permission
        if (perm == null || perm == Permission.NONE) {
            return false;
        }

        PermissionType type = getModifiablePermissions().getPermissionType(perm.getId());

        if ((getGroup().getPermissions().contains(perm) && type != PermissionType.DISABLED)
                || type == PermissionType.ADDITIONAL) {
            return true;
        }

        return false;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Lit Block">
    private boolean lightsEnabled = false;

    // Toggles the use of whether or not a player can emit light
    public void setLightsEnabled(boolean lightsEnabled) {
        this.lightsEnabled = lightsEnabled;
    }

    /**
     * Returns whether a player can emit light
     *
     * @return
     */
    public boolean hasLightsEnabled() {
        return lightsEnabled;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Game system">
    private long activeTask = 0;
    private boolean gameGuest = false;

    /**
     * Gets if a user is a guest in a game
     *
     * @return
     */
    public boolean isGameGuest() {
        return gameGuest;
    }
    private boolean isInLobby = false;

    /**
     * Gets if a user is in the game lobby.
     *
     * @return
     */
    public boolean isInLobby() {
        return isInLobby;
    }

    /**
     * Sets if a user is in the game lobby.
     *
     * @return
     */
    public void setIsInLobby(boolean isInLobby) {
        this.isInLobby = isInLobby;
        WorldHandler.reloadHidden();
    }

    /**
     * Returns the ID for a delayed task.
     *
     * @return
     */
    public long getActiveTask() {
        return activeTask;
    }

    /**
     * Stores the ID for a delayed task.
     *
     * @param i
     */
    public void setActiveTask(long i) {
        activeTask = i;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Settings">
    private long referralBonuses, settingfield;
    private int referType;
    private String referId;

    /**
     * Loads settings
     */
    private void loadSettings() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT settings, refer_bonus, refer_type, refer_id FROM players WHERE player_id = ?");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                settingfield = set.getLong("settings");
                referralBonuses = set.getLong("refer_bonus");
                referType = set.getInt("refer_type");
                referId = set.getString("refer_id");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load settings for " + playerName + "1", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Saves the settings value to the database
     */
    private void saveSettings() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players SET settings = ?, refer_bonus = ?, refer_type = ?, refer_id = ? WHERE player_id = ?");
            statement.setLong(1, settingfield);
            statement.setLong(2, referralBonuses);
            statement.setInt(3, referType);
            statement.setString(4, referId);
            statement.setString(5, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Failed to save settings for " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Checks if this setting flag is enabled
     *
     * @param setting
     * @return
     */
    public final boolean hasSetting(PlayerSettings setting) {
        return ((this.settingfield & setting.getSettingBit()) == setting.getSettingBit()) == !setting.isDefaultOn();
    }

    /**
     * Sets the setting on or off
     * <p/>
     * This saves the setting values to the database.
     *
     * @param setting the setting that need to be changed
     * @param enable if the flag should be enabled or not.
     */
    public final void setSetting(PlayerSettings setting, boolean enable) {
        if (enable == !setting.isDefaultOn()) {
            this.settingfield |= setting.getSettingBit();
        } else {
            this.settingfield &= ~setting.getSettingBit();
        }
        saveSettings();
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Login status">
    private boolean isLoggedIn = true;

    /**
     * Sets the login mode of the player
     *
     * @param loggedIn
     */
    public void setPlayerLoggedin(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    /**
     * Checks if the player is loggedin
     *
     * @return
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Visability">
    private boolean isVisible = true, showNametag = true;

    /**
     * Sets if this players nametag should be shown or not.
     * (This will also reload the list of hidden players)
     *
     * @param showNametag
     */
    public void setShowNametag(boolean showNametag) {
        if (showNametag != this.showNametag) {
            this.showNametag = showNametag;
            WorldHandler.reloadHidden();
        }
    }

    /**
     * Can we see this players nametag?
     *
     * @return
     */
    public boolean isShowingNametag() {
        return showNametag;
    }

    /**
     * Sets the visibility of the player.
     *
     * @param loggedIn
     */
    public void setPlayerVisible(boolean visible) {
        if (isVisible != visible) {
            isVisible = visible;
            WorldHandler.reloadHidden();
        }
    }

    /**
     * Checks if the player is visible.
     *
     * @return
     */
    public boolean isVisible() {
        return isVisible;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Bonus">
    private long lastUseBonus = 0;

    /**
     * Checks if a player can use their /bonus again yet.
     *
     * @return
     */
    public boolean canUseBonus() {
        if (lastUseBonus + 120000 > System.currentTimeMillis()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Updates when players bonus was last used.
     */
    public void useBonus() {
        lastUseBonus = System.currentTimeMillis();
    }

    /**
     * Gets when (in seconds) the player can next use their bonus.
     *
     * @return
     */
    public int getNextUseBonus() {
        return (int) (((lastUseBonus + 120000) - System.currentTimeMillis()) / 1000);
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Innectis Anti-Cheat">
    // --------------------------------------------------------------------
    // Anti-Cheat Settings
    // --------------------------------------------------------------------
    // Minimum distance to dig down before possible xray.
    private final int MIN_XRAY_DIG = 5;
    // Maximum distance between digging down and reaching ore
    // before possible xray.
    private final int MAX_XRAY_RANGE = 10;
    // --------------------------------------------------------------------
    // Anti-Cheat Variables
    // --------------------------------------------------------------------
    //private boolean hasJumpedUp = false;
    private int downCount = 0;
    private Location lastBreak = null;
    private Location lastCheat = null;

    // --------------------------------------------------------------------
    // Anti-Cheat Methods
    // --------------------------------------------------------------------
    /**
     * Checks if the block use could be a result of cheating via x-ray
     *
     * @return if the action could be be cheating
     */
    // @IdpDebug
    public boolean checkBlockUse(Block block, boolean isBreak) {
        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());

        if (world.getActingWorldType() != IdpWorldType.RESWORLD) {
            return false;
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (isBreak) {
            switch (mat) {
                case STONE:
                case GRASS:
                case DIRT:
                case OAK_PLANK:
                case GRAVEL:
                case SAND:
                case CLAY:
                    if (lastBreak == null) {
                        lastBreak = block.getLocation();
                    } else {
                        if (block.getX() == lastBreak.getBlockX()
                                && block.getZ() == lastBreak.getBlockZ()
                                && block.getY() < lastBreak.getBlockY()) {
                            downCount++;
                            lastBreak = block.getLocation();
                            if (downCount >= MIN_XRAY_DIG) {
                                lastCheat = lastBreak;
                            }
                        } else {
                            lastBreak = null;
                            downCount = 0;
                        }
                    }
                    return false;
                case REDSTONE_ORE:
                case GLOWING_REDSTONE_ORE:
                case GOLD_ORE:
                case EMERALD_ORE:
                case DIAMOND_ORE:
                case STONE_BRICKS:
                case MOSSY_STONE_BRICKS:
                case CRACKED_STONE_BRICKS:
                case MOSSY_COBBLESTONE:
                    if (lastCheat == null) {
                        return false;
                    }

                    int x = lastCheat.getBlockX();
                    int y = lastCheat.getBlockY();
                    int z = lastCheat.getBlockZ();

                    Vector vec1 = new Vector(x - MAX_XRAY_RANGE, y - MAX_XRAY_RANGE, z - MAX_XRAY_RANGE);
                    Vector vec2 = new Vector(x + MAX_XRAY_RANGE, y + MAX_XRAY_RANGE, z + MAX_XRAY_RANGE);

                    if (block.getLocation().toVector().isInAABB(vec1, vec2)) {

                        // Prevent Spam when Stronghold found.
                        switch (mat) {
                            case STONE_BRICKS:
                            case MOSSY_STONE_BRICKS:
                            case CRACKED_STONE_BRICKS:
                                lastCheat = null;
                            default:
                                lastCheat = block.getLocation();
                        }

                        return true;
                    } else {
                        return false;
                    }
                default:
                    lastBreak = null;
                    downCount = 0;
                    return false;
            }
        } else {
            if (lastCheat == null) {
                return false;
            }

            int x = lastCheat.getBlockX();
            int y = lastCheat.getBlockY();
            int z = lastCheat.getBlockZ();

            Vector vec1 = new Vector(x - MAX_XRAY_RANGE, y - MAX_XRAY_RANGE, z - MAX_XRAY_RANGE);
            Vector vec2 = new Vector(x + MAX_XRAY_RANGE, y + MAX_XRAY_RANGE, z + MAX_XRAY_RANGE);

            if (!block.getLocation().toVector().isInAABB(vec1, vec2)) {
                return false;
            }

            Location loc = block.getLocation();

            if (mat == IdpMaterial.CHEST) {
                if (ChestHandler.getChest(loc) == null
                        && LotHandler.getLot(loc) == null) {
                    lastCheat = loc;
                    return true;
                } else {
                    return false;
                }
            }

            if (mat == IdpMaterial.END_PORTAL_FRAME) {
                lastCheat = loc;
                return true;
            }

            return false;
        }
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Spectating">
    // Previous game mode before spectating
    private GameMode previousGameMode = null;

    // The target being spectated
    private IdpPlayer spectatorTarget = null;

    // The location before the player started spectating
    private Location preSpectateLocation = null;

    /**
     * Gets if this player is spectating
     *
     * @return
     */
    public boolean isSpectating() {
        return (spectatorTarget != null);
    }

    /**
     * Gets the spectating target of this player
     *
     * @return
     */
    public IdpPlayer getSpectatorTarget() {
        return spectatorTarget;
    }

    /**
     * Spectates the specified target, or null to exit spectator
     *
     * @param target
     */
    public void spectateTarget(final IdpPlayer target) {
        final IdpPlayer player = server.getPlayer(getUniqueId());

        if (target == null) {
            spectatorTarget = null;

            if (preSpectateLocation != null) {
                player.teleport(preSpectateLocation);
            }

            player.getHandle().setGameMode(previousGameMode);
        } else {
            previousGameMode = player.getHandle().getGameMode();

            if (previousGameMode != GameMode.SPECTATOR) {
                player.getHandle().setGameMode(GameMode.SPECTATOR);
            }

            // Only get pre-spectate location if the player isn't already
            // spectating someone (spectating one player to the next)
            if (spectatorTarget == null) {
                preSpectateLocation = player.getLocation();
            }

            spectatorTarget = target;
            player.getHandle().setSpectatorTarget(target.getHandle());

            // We need to switch the player's camera again, because for some reason
            // odd behavior will occur if the player is far enough away from
            // their target
            InnPlugin.getPlugin().getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 300, 1) {
                @Override
                public void run() {
                    PacketPlayOutCamera packet = new PacketPlayOutCamera(target.getHandle().getHandle());
                    player.getHandle().getHandle().playerConnection.sendPacket(packet);
                }
            });
        }
    }

    /**
     * Gets all spectators that are spectating this player
     *
     * @return
     */
    public List<IdpPlayer> getSpectators() {
        List<IdpPlayer> playerList = new ArrayList<IdpPlayer>();

        for (IdpPlayer targetPlayer : InnPlugin.getPlugin().getOnlinePlayers()) {
            PlayerSession targetSession = targetPlayer.getSession();
            IdpPlayer spectatingPlayer = targetSession.getSpectatorTarget();

            if (spectatingPlayer != null && spectatingPlayer.getName().equalsIgnoreCase(getRealName())) {
                playerList.add(targetPlayer);
            }
        }

        return playerList;
    }

    /**
     * Sends a message to all players spectating this player with given
     * colour/text.
     *
     * @return
     */
    public void spectatorMessage(ChatColor color, String message) {
        for (IdpPlayer targetPlayer : getSpectators()) {
            targetPlayer.print(color, message);
        }
    }

    /**
     * Removes all players spectating this player sending the given error
     * message. Spectators will be teleported to the location they were last at
     * before spectating and all hidden players will be reloaded.
     *
     * @return
     */
    public boolean kickSpectators(String errorMsg) {
        boolean actionTaken = false;

        for (IdpPlayer targetPlayer : getSpectators()) {
            PlayerSession targetSession = targetPlayer.getSession();
            targetSession.spectateTarget(null);

            targetPlayer.printError("Unable to Spectate: " + errorMsg);
            actionTaken = true;
        }

        return actionTaken;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Inventory Viewing">
    private ViewedPlayerInventoryData vpid = null;

    /**
     * Sets the data for viewing another player's inventory
     *
     * @param vpid
     */
    public void setViewedPlayerInventoryData(ViewedPlayerInventoryData vpid) {
        this.vpid = vpid;
    }

    /**
     * Gets the data for viewing another player's inventory
     *
     * @return
     */
    public ViewedPlayerInventoryData getViewedPlayerInventoryData() {
        return vpid;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Flight">

    /**
     * Checks if the player has flight mode enabled
     *
     * @return
     */
    public boolean hasFlightMode() {
        return hasSetting(PlayerSettings.FLIGHT);
    }

    /**
     * Sets if the player has flight mode enabled
     *
     * @param toggle
     */
    public void setFlightMode(boolean toggle) {
        setSetting(PlayerSettings.FLIGHT, toggle);
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Block Mining tracking">
    private HashMap<IdpMaterial, Integer> materialMined = new HashMap<IdpMaterial, Integer>();

    /**
     * Adds one to the material mined count
     *
     * @param mat
     */
    public void addMaterialMined(IdpMaterial mat) {
        int count = 0;

        if (materialMined.containsKey(mat)) {
            count = materialMined.get(mat);
        }

        count++;
        materialMined.put(mat, count);
    }

    /**
     * Gets the count of the material mined
     *
     * @param mat
     * @return
     */
    public int getMaterialMinedCount(IdpMaterial mat) {
        int count = 0;

        if (materialMined.containsKey(mat)) {
            count = materialMined.get(mat);
        }

        return count;
    }

    /**
     * Clears the count of the material mined
     *
     * @param mat
     */
    public void clearMaterialMined(IdpMaterial mat) {
        materialMined.remove(mat);
    }

    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Respawn Lot">
    private int respawnLotPersonalId = 0;

    /**
     * Gets the personal ID of the player's respawn lot
     *
     * @return
     */
    public int getRespawnLotPersonalId() {
        return respawnLotPersonalId;
    }

    /**
     * Sets the personal ID for the player's respawn lot
     *
     * @param respawnLot
     */
    public void setRespawnLotPersonalId(int respawnLotPersonalId) {
        this.respawnLotPersonalId = respawnLotPersonalId;
        saveRespawnLotPersonalId();
    }

    /**
     * Loads the personal ID for the player's respawn lot
     */
    public void loadRespawnLotPersonalId() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT personalid FROM lot_respawns WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                respawnLotPersonalId = set.getInt("personalid");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load respawn lot ID!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Deletes the personal ID for the player's respawn lot
     */
    public void deleteRespawnLotPersonalId() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM lot_respawns WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete respawn lot personal id from database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        this.respawnLotPersonalId = 0;
    }

    /**
     * Saves the personal ID for the player's respawn lot
     */
    private void saveRespawnLotPersonalId() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM lot_respawns WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.execute();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("INSERT INTO lot_respawns (player_id, personalid) VALUES (?, ?);");
            statement.setString(1, playerId.toString());
            statement.setInt(2, respawnLotPersonalId);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save respawn lot ID!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Special Item Status">
    private boolean specialItemInUse = false;

    /**
     * Gets whether the player is using a special item
     *
     * @return
     */
    public boolean getSpecialItemInUse() {
        return specialItemInUse;
    }

    /**
     * Sets whether the player is using a special item
     *
     * @param specialItemInUse
     */
    public void setSpecialItemInUse(boolean specialItemInUse) {
        this.specialItemInUse = specialItemInUse;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Version Update Notification">

    private String lastPlayedVersion = null;

    /**
     * Has the server updated since the player last logged in?
     *
     * @return
     */
    public boolean hasLastPlayedOnOlderVersion() {
        return !getLatestVersion().equals(Configuration.PLUGIN_VERSION);
    }

    /**
     * Set the players last known server version to the current server version.
     * This also saves into the database.
     */
    public void updateLastPlayedVersion() {
        setLastPlayedVersion(Configuration.PLUGIN_VERSION);
    }

    /**
     * Get the server version the last time the target played logged in.
     *
     * @return
     */
    public String getLatestVersion() {
        if (lastPlayedVersion == null) {
            PreparedStatement statement = null;
            ResultSet set = null;

            try {
                statement = DBManager.prepareStatement("SELECT last_version FROM players WHERE player_id = ?;");
                statement.setString(1, playerId.toString());
                set = statement.executeQuery();

                if (set.next()) {
                    lastPlayedVersion = set.getString("last_version");
                }
            } catch (SQLException ex) {
                InnPlugin.logError("Unable to load last played version!", ex);
            } finally {
                DBManager.closeResultSet(set);
                DBManager.closePreparedStatement(statement);
            }

            if (lastPlayedVersion == null) {
                lastPlayedVersion = "unknown";
            }
        }

        return lastPlayedVersion;
    }

    /**
     * Saves the specified version into the database as the last version played.
     *
     * @param lastPlayedVersion
     */
    public void setLastPlayedVersion(String lastPlayedVersion) {
        this.lastPlayedVersion = lastPlayedVersion;

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players SET last_version = ? WHERE player_id = ?");
            statement.setString(1, lastPlayedVersion);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Failed to save latest version for " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Refferal Points">

    private int totalReferralPoints = Integer.MIN_VALUE;
    private int spentReferralPoints = Integer.MIN_VALUE;
    private List<UUID> referPlayers = new ArrayList<UUID>();

    public String getRefferalList() {
        if (totalReferralPoints == Integer.MIN_VALUE) {
            calculateRefferalPoints();
        }

        if (referPlayers.isEmpty()) {
            return ChatColor.RED + "none";
        }

        List<String> usernames = new ArrayList<String>();

        for (UUID id : referPlayers) {
            PlayerSession session = PlayerSession.getActiveSession(id);

            if (session == null) {
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(id);
                session = PlayerSession.getSession(id, credentials.getName(), InnPlugin.getPlugin());
            }

            usernames.add(session.getColoredDisplayName() + ChatColor.GREEN + " ("
                    + ChatColor.YELLOW + (session.getGroup().getReferralPoints() + (session.getTotalOnlineTime() / (1000 * 60 * 30)))
                    + ChatColor.GREEN + ")");
        }

        return StringUtil.joinString(usernames.toArray(new String[usernames.size()]), ChatColor.GREEN + ", ");
    }

    public int getTotalReferralPoints() {
        if (totalReferralPoints == Integer.MIN_VALUE) {
            calculateRefferalPoints();
        }
        return totalReferralPoints;
    }

    public int getSpentReferralPoints() {
        if (spentReferralPoints == Integer.MIN_VALUE) {
            calculateRefferalPoints();
        }
        return spentReferralPoints;
    }

    public int getReferType() {
        return referType;
    }

    public void setReferType(int referType) {
        this.referType = referType;
        this.saveSettings();
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public String getReferId() {
        return referId;
    }

    public void calculateRefferalPoints() {
        PreparedStatement statement = null;
        ResultSet set = null;

        totalReferralPoints = 0;
        spentReferralPoints = 0;
        referPlayers = new ArrayList<UUID>();

        for (PlayerBonus bonus : PlayerBonus.values()) {
            if (hasBonus(bonus)) {
                spentReferralPoints += bonus.getCost();
            }
        }

        try {
            statement = DBManager.prepareStatement("SELECT onlinetime, playergroup, player_id FROM players WHERE refer_type = ? AND refer_id = ?;");
            statement.setInt(1, 2);
            statement.setString(2, playerId.toString());
            set = statement.executeQuery();

            while (set.next()) {
                long onlineTime = set.getLong("onlinetime");
                PlayerGroup group = PlayerGroup.getGroup(set.getInt("playergroup"));
                UUID playerID = UUID.fromString(set.getString("player_id"));

                referPlayers.add(playerID);
                totalReferralPoints += group.getReferralPoints();
                totalReferralPoints += onlineTime / (1000 * 60 * 30);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to calculate referral points!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    public final boolean hasBonus(PlayerBonus bonus) {
        return (this.referralBonuses & bonus.getBonusBit()) == bonus.getBonusBit();
    }

    public final void setBonus(PlayerBonus bonus, boolean enable) {
        this.referralBonuses |= bonus.getBonusBit();
        saveSettings();
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Mining Stick">
    private MiningStickData miningStick;

    /**
     * Gets the data for this player's mining stick
     * @return
     */
    public MiningStickData getMiningStickData() {
        if (miningStick == null) {
            miningStick = getMiningStickDataFromDatabase();
        }

        return miningStick;
    }

    /**
     * Gets the mining stick data from the database
     * @return
     */
    private MiningStickData getMiningStickDataFromDatabase() {
        PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT ID, settings, size FROM player_mining_stick WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                int id = set.getInt("ID");
                long settings = set.getLong("settings");
                int size = set.getInt("size");

                return new MiningStickData(id, credentials, settings, size);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load mining stick data for " + getRealName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return new MiningStickData(credentials, 0, 1);
    }
    //</editor-fold>
}

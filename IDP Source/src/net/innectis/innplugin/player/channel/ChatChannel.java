package net.innectis.innplugin.player.channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.loggers.ChatLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.player.chat.ChatMessage;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerSession;
import org.bukkit.ChatColor;

/**
 * An object representing a chat channel
 *
 * @author Lynxy
 */
public class ChatChannel {

    private int id;
    private boolean cached = false;
    private long lastActivity;
    private boolean saveLastActivity = false;
    private String name;
    private long settings = 0;
    private String password = null;
    private PlayerCredentials ownerCredentials = null;
    private HashMap<PlayerCredentials, MemberDetails> members = new HashMap<PlayerCredentials, MemberDetails>();
    private List<PlayerCredentials> banned = new ArrayList<PlayerCredentials>();

    public ChatChannel(String name, long settings, String password, long lastActivity) {
        this(0, name, settings, password, lastActivity);
    }

    public ChatChannel(int id, String name, long settings, String password, long lastActivity) {
        this.id = id;
        this.name = name;
        this.settings = settings;
        this.password = password;
        this.lastActivity = lastActivity;
    }

    /**
     * Returns the channel's id.
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Gets if this channel has been cached
     * @return
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * Sets if this channel has been cached. It has been cached when
     * there is at least one user online at the time who is also in
     * this channel
     * @param cached
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    /**
     * Gets the last update tick for this channel
     * @return
     */
    public long getLastActivity() {
        return lastActivity;
    }

    /**
     * Updates the last activity for this channel. This will only
     * update the last activity in the database if there was new
     * activity recently.
     */
    public void saveLastActivity() {
        if (saveLastActivity) {
            saveLastActivity = false;
            saveLastActivityToDatabase();
        }
    }

    /**
     * Gets the name of this channel
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this channel
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        updateChannelSettings();
    }

    /**
     * Checks if the channel has the specified setting
     * @param settomg
     * @return
     */
    public boolean hasSetting(ChannelSettings setting) {
        return (settings & setting.getBit()) == setting.getBit();
    }

    /**
     * Clears the channel setting
     * @param setting
     */
    public void clearSetting(ChannelSettings setting) {
        settings &= ~setting.getBit();
        updateChannelSettings();
    }

    /**
     * Sets the channel setting
     * @param setting
     */
    public void setSetting(ChannelSettings setting) {
        settings |= setting.getBit();
        updateChannelSettings();
    }

    /**
     * Gets the password to this channel
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this channel
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
        updateChannelSettings();
    }

    /**
     * Returns if this channel is requiring a password
     * @return
     */
    public boolean isRequiringPassword() {
        return (password != null && !password.isEmpty());
    }

    /**
     * Gets the credentials of the owner of this channel
     * @return
     */
    public PlayerCredentials getOwnerCredentials() {
        return ownerCredentials;
    }

    /**
     * Gets the owner of this channel
     * @return
     */
    public String getOwner() {
        return ownerCredentials.getName();
    }

    /**
     * Adds the credentials of a member to this channel and returns their member
     * details object. If they are the owner then it sets the owner
     * string to them
     * @param credentials
     * @param group
     * @param personalnum
     * @param save This will save the member table if true
     * @return
     */
    public MemberDetails addMember(PlayerCredentials credentials, ChatChannelGroup group, int personalnum, boolean save) {
        MemberDetails details = new MemberDetails(personalnum, group);
        members.put(credentials, details);

        if (group == ChatChannelGroup.OWNER) {
            ownerCredentials = credentials;
        }

        if (save) {
            updateMembersTable();
        }

        return details;
    }

    /**
     * Removes the member and returns their member group. This will
     * also promote the next user to owner if the person that left
     * was the current owner
     * @param playerName
     * @return
     */
    public MemberDetails removeMember(String playerName) {
        MemberDetails details = null;
        UUID playerId = null;

        for (Iterator<PlayerCredentials> it = members.keySet().iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                details = members.get(pc);
                playerId = pc.getUniqueId();
                it.remove();
                break;
            }
        }

        PlayerSession session = PlayerSession.getActiveSession(playerId);

        // Make sure to remove their personal channel number when they leave
        if (session != null) {
            session.removeChannelNumber(details.getPersonalNumber());
        }

        // If this channel still has members, check if there needs to be a new owner
        if (members.size() > 0) {
            if (details.getGroup() == ChatChannelGroup.OWNER) {
                PlayerCredentials nextOwner = members.keySet().iterator().next();
                MemberDetails nextOwnerDetails = members.get(nextOwner);
                nextOwnerDetails.setGroup(ChatChannelGroup.OWNER);
                ownerCredentials = nextOwner;
            }

            updateMembersTable();
        }

        return details;
    }

    /**
     * Modifies the playerName group the member
     * @param playerName
     * @param group
     */
    public void modifyMemberGroup(String playerName, ChatChannelGroup group) {
        for (PlayerCredentials pc : members.keySet()) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                MemberDetails details = members.get(pc);
                details.setGroup(group);
                updateMembersTable();
                break;
            }
        }
    }

    /**
     * Sets the online/offline status of the member
     * @param playerName
     * @param online
     */
    public void setMemberStatus(String playerName, boolean online) {
        for (PlayerCredentials pc : members.keySet()) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                MemberDetails details = members.get(pc);
                details.setOnline(online);
                break;
            }
        }
    }

    /**
     * Switches the current owner with the specified member
     * @param credentials
     */
    public void switchOwner(PlayerCredentials credentials) {
        MemberDetails previousOwnerDetails = members.get(ownerCredentials);
        MemberDetails newOwnerDetails = members.get(credentials);
        previousOwnerDetails.setGroup(ChatChannelGroup.MEMBER);
        newOwnerDetails.setGroup(ChatChannelGroup.OWNER);
        ownerCredentials = credentials;
        updateMembersTable();
    }

    /**
     * Checks if the specified user is in this channel
     * @param playerName
     * @return
     */
    public boolean containsMember(String playerName) {
        for (PlayerCredentials pc : members.keySet()) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the details of the specified member
     * @param playerName
     * @return
     */
    public MemberDetails getMemberDetails(String playerName) {
        for (PlayerCredentials pc : members.keySet()) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return members.get(pc);
            }
        }

        return null;
    }

    /**
     * Returns a list of all members with the specified group
     * @return
     */
    public List<PlayerCredentials> getMembers() {
        return getMembers(ChatChannelGroup.MEMBER, true);
    }

    /**
     * Returns a list of all members with the specified group
     * @param group
     * @param inherited If true, returns if the member inherits the group
     * @return
     */
    public List<PlayerCredentials> getMembers(ChatChannelGroup group, boolean inherited) {
        List<PlayerCredentials> memberList = new ArrayList<PlayerCredentials>();

        for (PlayerCredentials pc : members.keySet()) {
            MemberDetails details = members.get(pc);
            ChatChannelGroup memberGroup = details.getGroup();
            boolean isRequisite = (inherited ? memberGroup.equalsOrInherits(group) : memberGroup.equals(group));

            if (isRequisite) {
                memberList.add(pc);
            }
        }

        return memberList;
    }

    /**
     * Counts how many offline members are in the channel
     * @return
     */
    public int countOfflineMembers() {
        int offlineCount = 0;

        for (MemberDetails details : members.values()) {
            if (!details.isOnline()) {
                offlineCount++;
            }
        }

        return offlineCount;
    }

    /**
     * Adds the user to the banned list
     * @param credentials
     */
    public void addBanned(PlayerCredentials credentials) {
        banned.add(credentials);
        updateBannedMembersTable();
    }

    /**
     * Removes the user from the banned list
     * @param playerName
     */
    public void removeBanned(String playerName) {
        boolean removed = false;

        for (Iterator<PlayerCredentials> it = banned.iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            updateBannedMembersTable();
        }
    }

    /**
     * Checks if the user is banned from this channel
     * playerName playerName
     * @return
     */
    public boolean isBanned(String playerName) {
        for (PlayerCredentials pc : banned) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of all banned users in this channel
     * @return
     */
    public List<String> getBanned() {
        List<String> bannedMembers = new ArrayList<String>();

        for (PlayerCredentials pc : banned) {
            bannedMembers.add(pc.getName());
        }

        return bannedMembers;
    }

    /**
     * Sends a general channel message to the specified channel
     * @param msg
     */
    public void sendGeneralMessage(String msg) {
        for (IdpPlayer player : getPlayers()) {
            String finalMessage = createChatMessage(player, msg);
            player.printRaw(finalMessage);
        }
   }

    /**
     * Sends a chat message to the channel
     * @param player
     * @param msg
     * @param globalListeners
     */
    public void sendChatMessage(IdpPlayer player, String msg, Set<String> globalListeners) {
        msg = player.getColoredName() + ChatColor.AQUA + ": " + msg;
        ChatSoundSetting setting = ChatSoundSetting.CHANNEL_CHAT;

        for (IdpPlayer tmpPlayer : getPlayers()) {
            if (!globalListeners.contains(tmpPlayer.getName().toLowerCase())) {
                String chatMsg = createChatMessage(tmpPlayer, msg);
                tmpPlayer.printRaw(chatMsg);
            }

            // Don't play chat sound if the sender is receiving the message
            if (tmpPlayer.equals(player)) {
                continue;
            }

            PlayerSession session = tmpPlayer.getSession();

            if (session.hasChatSoundSetting(setting)) {
                tmpPlayer.playChatSoundFromSetting(setting);
            }
        }

        for (String listener : globalListeners) {
            IdpPlayer tmpPlayer = InnPlugin.getPlugin().getPlayer(listener, true);

            if (tmpPlayer != null) {
                String chatMsg = createChatMessage(tmpPlayer, msg);
                tmpPlayer.printRaw(chatMsg);
            }
        }

        // Log in chatlogger
        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
        chatLogger.logChatboxChat(getName(), player.getName(), msg);
        InnPlugin.logCustom(net.innectis.innplugin.player.chat.ChatColor.AQUA,
                "Channel [" + getName() + "] " + msg);

        // Update last activity
        saveLastActivity = true;
        lastActivity = System.currentTimeMillis();
    }

    /**
     * Creates a chat message to send to a channel
     * @param channel
     * @param player
     * @param msg
     * @return
     */
    private String createChatMessage(IdpPlayer player, String msg) {
        int personalNumber = player.getSession().getNumberFromChannelName(getName());
        PlayerSession session = player.getSession();
        String finalMsg = null;

        ChatMessage chatMsg = new ChatMessage(msg, net.innectis.innplugin.player.chat.ChatColor.AQUA);

        if (session.isStaff()) {
            finalMsg = chatMsg.getUncensoredMessage();
        } else {
            if (session.canSeeFilteredChat()) {
                finalMsg = chatMsg.getUncensoredUnmarkedMessage();
            } else {
                finalMsg = chatMsg.getCensoredMessage();
            }
        }

        return ChatColor.WHITE + "[" + ChatColor.AQUA + ChatColor.BOLD + personalNumber + ". " + ChatColor.RESET
            + ChatColor.AQUA + ChatColor.ITALIC + getName() + ChatColor.RESET
            + ChatColor.WHITE + "] " + finalMsg;
    }

    /**
     * Returns a list of IdpPlayers in the channel. If a player can
     * not be resolved to an IdpPlayer, it is not added to the list
     */
    public List<IdpPlayer> getPlayers() {
        List<IdpPlayer> players = new ArrayList<IdpPlayer>();
        IdpPlayer tmpPlayer;
        for (PlayerCredentials pc : members.keySet()) {
            tmpPlayer = InnPlugin.getPlugin().getPlayer(pc.getUniqueId());

            if (tmpPlayer != null) {
                players.add(tmpPlayer);
            }
        }

        return players;
    }

    /**
     * Checks if this channel is empty
     * @return
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Checks if the channel has only offline members. NOTE: This method
     * will ONLY return true if it has members who are all offline, it
     * will not return true otherwise
     * @return
     */
    public boolean isAllOffline() {
        if (members.size() > 0) {
            int total = members.size();
            int offlineCount = 0;

            for (MemberDetails details : members.values()) {
                if (!details.isOnline()) {
                    offlineCount++;
                }
            }

            if (offlineCount == total) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates this channel in the database
     */
    public void createInDB() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("INSERT INTO channel_information (channelname, settings, password, lastactivity) VALUES (?, ?, ?, ?);");
            statement.setString(1, name);
            statement.setLong(2, settings);
            statement.setString(3, password);
            statement.setLong(4, lastActivity);
            statement.execute();
            set = statement.getGeneratedKeys();

            if (set.next()) {
                id = set.getInt(1);
            }

            updateMembersTable();
            updateBannedMembersTable();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to create channel in database!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Deletes this channel from the database.
     */
    public void delete() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM channel_information WHERE channelid = ?;");
            statement.setInt(1, id);
            statement.execute();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM channel_members WHERE channelid = ?;");
            statement.setInt(1, id);
            statement.execute();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM channel_bans WHERE channelid = ?;");
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete channel " + name + " (ID: " + name + ")", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Saves the last actiivty to database
     */
    private void saveLastActivityToDatabase() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE channel_information SET lastactivity = ? WHERE channelid = ?;");
            statement.setLong(1, lastActivity);
            statement.setInt(2, id);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save last activity for channel " + getName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    /**
     * Updates the channel settings in the database
     */
    private void updateChannelSettings() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE channel_information SET channelname = ?, settings = ?, password = ? WHERE channelid = ?;");
            statement.setString(1, name);
            statement.setLong(2, settings);
            statement.setString(3, password);
            statement.setInt(4, id);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to update channel password for " + getName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Populates, and deletes, if necessary, the members table in
     * the database for this channel
     * @throws SQLException
     */
    private void updateMembersTable() {
        PreparedStatement statement = null;

        try {
            // Existing channel, so let's clear the member table
            if (id > 0) {
                statement = DBManager.prepareStatement("DELETE FROM channel_members WHERE channelid = ?;");
                statement.setInt(1, id);
                statement.execute();
                DBManager.closePreparedStatement(statement);
            }

            statement = DBManager.prepareStatement("INSERT INTO channel_members (channelid, player_id, personalnum, membergroup) VALUES (?, ?, ?, ?)");

            for (PlayerCredentials pc : members.keySet()) {
                MemberDetails details = members.get(pc);
                statement.setInt(1, id);
                statement.setString(2, pc.getUniqueId().toString());
                statement.setInt(3, details.getPersonalNumber());
                statement.setInt(4, details.getGroup().getId());
                statement.execute();
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save member's table for " + getName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Populates, and deletes, if necessary, the banned member
     * table in the database for this channel
     * @throws SQLException
     */
    private void updateBannedMembersTable() {
        PreparedStatement statement = null;

        try {
            // Existing channel, so let's clear the banned member table
            if (id > 0) {
                statement = DBManager.prepareStatement("DELETE FROM channel_bans WHERE channelid = ?;");
                statement.setInt(1, id);
                statement.execute();
                DBManager.closePreparedStatement(statement);
            }

            if (banned.size() > 0) {
                statement = DBManager.prepareStatement("INSERT INTO channel_bans (channelid, player_id) VALUES (?, ?);");

                for (PlayerCredentials pc : banned) {
                    statement.setInt(1, id);
                    statement.setString(2, pc.getUniqueId().toString());
                    statement.execute();
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save banned members for " + getName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

}

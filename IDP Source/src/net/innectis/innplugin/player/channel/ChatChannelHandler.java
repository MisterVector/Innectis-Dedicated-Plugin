package net.innectis.innplugin.player.channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerSession;

/**
 * A handler that manages chat channels
 *
 * @author Lynxy
 */

public class ChatChannelHandler {

    private static HashMap<String, ChatChannel> _channels = new HashMap<String, ChatChannel>();
    private static Set<String> globalListeners = new HashSet<String>();
    private static HashMap<Integer, String> idNameMap = new HashMap<Integer, String>();

    /**
     * Returns if the specified channel name is valid or not
     * @param channelName
     * @return
     */
    public static boolean isValidChannel(String channelName) {
        for (String name : idNameMap.values()) {
            if (name.equalsIgnoreCase(channelName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the channel name from its ID
     * @param id
     * @return
     */
    public static String getNameFromId(int id) {
        return idNameMap.get(id);
    }

    /**
     * Gets a channel by its name and caches it
     * @param channelName
     * @return
     */
    public static ChatChannel getChannel(String channelName) {
        return getChannel(channelName, true);
    }

    /**
     * Returns a channel by name, or null if it doesn't exist
     * @param channelName
     * @param cache If true, it will cache the channel if obtained
     * from the database
     */
    public static ChatChannel getChannel(String channelName, boolean cache) {
        ChatChannel channel = _channels.get(channelName.toLowerCase());

        if (channel == null) {
            channel = loadChannelFromDatabase(channelName);

            if (channel == null) {
                return null;
            }

            channel.setCached(cache);

            if (cache) {
                _channels.put(channelName.toLowerCase(), channel);
            }
        }

        return channel;
    }

    /**
     * Creates a new channel, saves it in the database, and returns it
     * @param channelName
     * @param settings
     * @param password
     */
    public static ChatChannel createChannel(String channelName, long settings, String password) {
        ChatChannel channel = new ChatChannel(channelName, settings, password, System.currentTimeMillis());
        channel.createInDB();
        channel.setCached(true);
        _channels.put(channelName.toLowerCase(), channel);
        idNameMap.put(channel.getId(), channelName);
        return channel;
    }

    /**
     * Renames an existing channel to a different name
     * @param channel
     * @param oldChannel
     * @param newChannel
     */
    public static void renameChannel(ChatChannel channel, String newChannel) {
        String oldChannel = channel.getName();
        channel.setName(newChannel);

        // Swap the names of the map
        idNameMap.remove(channel.getId());
        idNameMap.put(channel.getId(), newChannel);

        _channels.remove(oldChannel);
        _channels.put(newChannel.toLowerCase(), channel);


        // Make sure to change the reference of each player's channel
        for (IdpPlayer player : InnPlugin.getPlugin().getOnlinePlayers()) {
            player.getSession().renameChannel(oldChannel, newChannel);
        }
    }

    /**
     * Adds a global listener to the list
     * @param username
     */
    public static void addGlobalListener(String username) {
        globalListeners.add(username.toLowerCase());
    }

    /**
     * Removes a global listener from the list
     * @param username
     */
    public static void removeGlobalListener(String username) {
        globalListeners.remove(username.toLowerCase());
    }

    /**
     * Checks if this person is a global listener
     * @param username
     * @return
     */
    public static boolean isGlobalListener(String username) {
        return globalListeners.contains(username.toLowerCase());
    }

    /**
     * Returns an unmodifiable set of all global listeners
     * @return
     */
    public static Set<String> getGlobalListeners() {
        return Collections.unmodifiableSet(globalListeners);
    }

    /**
     * Unloads the specified channel from the channel cache
     * @param channelName
     */
    public static void unloadChannel(String channelName) {
        _channels.remove(channelName.toLowerCase());
    }

    /**
     * Deletes the channel by its channel object
     * @param channel
     */
    public static void deleteChannel(ChatChannel channel) {
        _channels.remove(channel.getName().toLowerCase());
        idNameMap.remove(channel.getId());

        for (PlayerCredentials pc : channel.getMembers()) {
            MemberDetails details = channel.getMemberDetails(pc.getName());
            PlayerSession session = PlayerSession.getActiveSession(pc.getUniqueId());

            if (session != null) {
                session.removeChannelNumber(details.getPersonalNumber());
            }
        }

        channel.delete();
    }

    /**
     * Returns a list of all channels (loads from cache, and then database
     * if the channel is not in the cache)
     * @return
     */
    public static List<ChatChannel> getAllChannels() {
        List<ChatChannel> channels = new ArrayList<ChatChannel>();

        for (String channelName : idNameMap.values()) {
            // If in cache, add the channel, else create channel from database
            if (_channels.containsKey(channelName.toLowerCase())) {
                channels.add(_channels.get(channelName.toLowerCase()));
            } else {
                ChatChannel channel = loadChannelFromDatabase(channelName);
                channels.add(channel);
            }
        }

        return channels;
    }

    /**
     * Saves last activity for all channels
     */
    public static void saveAllActivity() {
        for (ChatChannel channel : _channels.values()) {
            channel.saveLastActivity();
        }
    }

    /**
     * Loads all associated IDs to channel names
     */
    public static void loadChannelNames() {
        InnPlugin.logInfo("Loading channel names...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT channelid, channelname FROM channel_information;");
            set = statement.executeQuery();
            int count = 0;

            while (set.next()) {
                int id = set.getInt("channelid");
                String name = set.getString("channelname");
                idNameMap.put(id, name);
                count++;
            }

            InnPlugin.logInfo("Loaded " + count + " channel names!");
        } catch (SQLException ex) {
            InnPlugin.logError("Could not load channel names!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Loads the specified channel from the database
     * @param channelName
     * @return
     */
    private static ChatChannel loadChannelFromDatabase(String channelName) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM channel_information WHERE lower(channelname) = ?;");
            statement.setString(1, channelName.toLowerCase());
            set = statement.executeQuery();
            ChatChannel channel = null;
            int id = 0;

            if (set.next()) {
                id = set.getInt("channelid");
                String name = set.getString("channelname");
                long settings = set.getLong("settings");
                String password = set.getString("password");
                long lastActivity = set.getLong("lastactivity");

                channel = new ChatChannel(id, name, settings, password, lastActivity);
            }

            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);

            if (channel != null) {
                statement = DBManager.prepareStatement("SELECT * FROM channel_members WHERE channelid = ?;");
                statement.setInt(1, id);
                set = statement.executeQuery();

                while (set.next()) {
                    String playerIdString = set.getString("player_id");
                    UUID playerId = UUID.fromString(playerIdString);
                    PlayerCredentials memberCredentials = PlayerCredentialsManager.getByUniqueId(playerId, true);

                    int personalNumber = set.getInt("personalnum");
                    ChatChannelGroup group = ChatChannelGroup.fromId(set.getInt("membergroup"));
                    channel.addMember(memberCredentials, group, personalNumber, false);
                }

                DBManager.closeResultSet(set);
                DBManager.closePreparedStatement(statement);

                statement = DBManager.prepareStatement("SELECT * FROM channel_bans WHERE channelid = ?;");
                statement.setInt(1, id);
                set = statement.executeQuery();

                while (set.next()) {
                    String playerIdString = set.getString("player_id");
                    UUID playerId = UUID.fromString(playerIdString);
                    PlayerCredentials playerCredentials = PlayerCredentialsManager.getByUniqueId(playerId, true);

                    channel.addBanned(playerCredentials);
                }
            }

            return channel;
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load channel from database!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }
    
}

package net.innectis.innplugin.system.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * @author Hret (re-written by AlphaBlend)
 *
 */
public class BanHandler {

    // A list of all players banned on the server (includes IPBans as well)
    private static List<Ban> bannedPlayers = new ArrayList<Ban>();
    // Includes all the whitelisted players (Wrongfully IPBanned players)
    private static List<PlayerCredentials> whitelistedPlayers = new ArrayList<PlayerCredentials>();

    private BanHandler() {
    }

    /**
     * Adds a user to the ban whitelist
     * @param credentials
     */
    public static void addWhitelist(PlayerCredentials credentials) {
        whitelistedPlayers.add(credentials);

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO ban_whitelist (player_id) VALUES (?);");
            statement.setString(1, credentials.getUniqueId().toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to add whitelisted player to the database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Removes a player from the ban whitelist
     * @param credentials
     */
    public static void removeWhitelist(PlayerCredentials credentials) {
        whitelistedPlayers.remove(credentials);

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM ban_whitelist WHERE player_id = ?");
            statement.setString(1, credentials.getUniqueId().toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to remove whitelisted player " + credentials.getName() + " from the database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Checks to see if a player is on the ban whitelist by their unique id
     * @param playerId
     * @return
     */
    public static boolean isWhitelisted(UUID playerId) {
        for (PlayerCredentials pc : whitelistedPlayers) {
            if (pc.getUniqueId().equals(playerId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the whitelist as an unmodifiable list of player credentials
     * @return
     */
    public static List<PlayerCredentials> getWhitelist() {
        return Collections.unmodifiableList(whitelistedPlayers);
    }

    /**
     * Loads the ban whitelist from database
     */
    public static void loadWhitelist() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM ban_whitelist");
            set = statement.executeQuery();

            while (set.next()) {
                String playerId = set.getString("player_id");
                UUID uuid = UUID.fromString(playerId);
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(uuid, true);
                whitelistedPlayers.add(credentials);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load ban whitelist!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Links a username and ip to an existing username's ip ban
     * @param creator
     * @param bannedPlayerCredentials
     * @param ip
     * @param linkedPlayerCredentials
     * @return Whether the name was linked to an existing ip ban or not
     */
    public static LinkStatus linkIPBan(IdpCommandSender creator, PlayerCredentials bannedPlayerCredentials, String ip, PlayerCredentials linkedPlayerCredentials) {
        for (Ban ban : bannedPlayers) {
            if (ban instanceof IPBan) {
                IPBan ipban = (IPBan) ban;
                IPBanGroup group = ipban.getGroup();

                if (group.containsPlayerId(linkedPlayerCredentials.getUniqueId())) {
                    if (!ban.canModifyBan(creator)) {
                        return LinkStatus.LINK_NOT_CREATOR;
                    }

                    if (group.containsPlayerId(bannedPlayerCredentials.getUniqueId())) {
                        return LinkStatus.LINK_SAME_USER;
                    }

                    group.addPlayer(bannedPlayerCredentials);
                    group.addIP(ip);
                    ipban.save();
                    return LinkStatus.LINK_SUCCESSFUL;
                }
            }
        }

        return LinkStatus.LINK_NOT_FOUND;
    }

    /**
     * Unlinks a username and ip from an existing IPBan
     * @param creator
     * @param credentials
     * @param ip
     * @return Whether the username was unlinked from an existing ip ban
     */
    public static UnlinkStatus unlinkIPBan(IdpCommandSender creator, PlayerCredentials credentials, String ip) {
        for (Ban ban : bannedPlayers) {
            if (ban instanceof IPBan) {
                IPBan ipban = (IPBan) ban;
                IPBanGroup group = ipban.getGroup();

                if (group.containsPlayerId(credentials.getUniqueId())) {
                    if (!ban.canModifyBan(creator)) {
                        return UnlinkStatus.UNLINK_NOT_CREATOR;
                    }

                    group.removePlayer(credentials);
                    group.removeIP(ip);

                    // No more players or IPs, so just remove the ban entirely
                    if (group.getIPs().isEmpty() && group.getPlayers().isEmpty()) {
                        removeBan(ban);
                        return UnlinkStatus.UNLINK_REMOVED_BAN;
                    } else {
                        ipban.save();
                        return UnlinkStatus.UNLINK_FOUND;
                    }
                }
            }
        }

        return UnlinkStatus.UNLINK_NOT_FOUND;
    }

    /**
     * Gets an IP ban group from the specified IP
     * @param ip
     * @return
     */
    public static IPBanGroup getIPBanGroup(String ip) {
        for (Ban ban : bannedPlayers) {
            if (ban instanceof IPBan) {
                IPBan ipObj = (IPBan) ban;

                if (ipObj.getGroup().containsIP(ip)) {
                    return ipObj.getGroup();
                }
            }
        }

        return null;
    }
    /**
     * Specifies if this IP is banned or not
     * @param ip
     * @return
     */
    public static boolean hasIPBan(String ip) {
        for (Ban ban : bannedPlayers) {
            if (ban instanceof IPBan) {
                IPBan ipban = (IPBan) ban;

                if (ipban.getGroup().containsIP(ip)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the IPBanObject associated with this IP
     * @param ip
     * @return
     */
    public static Ban getIPBanObject(String ip) {
        for (Ban ban : bannedPlayers) {
            if (ban instanceof IPBan) {
                IPBan ipban = (IPBan) ban;

                if (ipban.getGroup().containsIP(ip)) {
                    return ban;
                }
            }
        }

        return null;
    }

    /**
     * Gets the ban object from the specified player ID, if available
     * @param playerId
     * @return
     */
    public static Ban getBan(UUID playerId) {
        for (Ban banobj : bannedPlayers) {
            if (banobj instanceof UserBan) {
                UserBan userban = (UserBan) banobj;
                PlayerCredentials bannedPlayerCredentials = userban.getBannedPlayerCredentials();

                if (bannedPlayerCredentials.getUniqueId().equals(playerId)) {
                    return banobj;
                }
            } else if (banobj instanceof IPBan) {
                IPBan ipban = (IPBan) banobj;

                if (ipban.getGroup().containsPlayerId(playerId)) {
                    return banobj;
                }
            }
        }

        return null;
    }

    /**
     * Adds a new ban (can be either username, or an IPBan)
     * @param ban
     * @return the BanType of the ban made
     *
     * This method also checks whether or not a ban is possible.
     * For instance, if the person who banned the user is a Moderator
     * or a Rainbow Mod, and the ban already exists
     */
    public static BanResult addBan(Ban ban) {
        BanResult type = null;
        boolean found = false;

        if (ban instanceof UserBan) {
            UserBan userban = (UserBan) ban;

            for (Ban b : bannedPlayers) {
                if (b instanceof UserBan) {
                    UserBan ub = (UserBan) b;

                    if (ub.getBannedPlayerCredentials().equals(userban.getBannedPlayerCredentials())) {
                        ub.setAttributesFrom(ban);
                        ub.save();
                        type = BanResult.BAN_EXISTING;
                        found = true;
                        break;
                    }
                }
            }

            // If there is no existing ban, just save this, and add it to the
            // list of existing bans
            if (!found) {
                userban.save();
                bannedPlayers.add(userban);
                type = BanResult.BAN_FRESH;
            }
        } else if (ban instanceof IPBan) {
            IPBan ipban = (IPBan) ban;
            IPBanGroup group = ipban.getGroup();

            // Check to see if this IPBan needs to be part of an existing one
            for (Ban b : bannedPlayers) {
                if (b instanceof IPBan) {
                    IPBan ip = (IPBan) b;

                    if (ip.isPartOfThis(group)) {
                        ip.copyFromGroup(group);
                        ip.setAttributesFrom(ban);
                        ip.save();
                        type = BanResult.BAN_EXISTING_IP;
                        found = true;
                        break;
                    }
                }
            }

            // If there is no existing ban, just save this ban, and add it
            // to the list of IPBans
            if (!found) {
                ban.save();
                bannedPlayers.add(ipban);
                type = BanResult.BAN_FRESH_IP;
            }
        }

        return type;
    }

    /**
     * Removes an existing ban (call getBan(BanObject) before this)
     * @param ban
     */
    public static void removeBan(Ban ban) {
        bannedPlayers.remove(ban);
        ban.delete();
    }

    /**
     * Loads both the banned players and the IPBans
     */
    public static void loadBans() {
        InnPlugin.logInfo("Loading banned players...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM banned_players WHERE expired = 0;");
            set = statement.executeQuery();

            while (set.next()) {
                int id = set.getInt("ID");

                String playerId = set.getString("player_id");
                UUID uuid = UUID.fromString(playerId);
                PlayerCredentials player = PlayerCredentialsManager.getByUniqueId(uuid, true);

                String bannedByPlayerId = set.getString("banned_by_player_id");
                UUID bannedByUUID = UUID.fromString(bannedByPlayerId);
                PlayerCredentials bannedByPlayerCredentials = null;

                if (bannedByUUID.equals(Configuration.SERVER_GENERATED_IDENTIFIER)) {
                    bannedByPlayerCredentials = Configuration.SERVER_GENERATED_CREDENTIALS;
                } else if (bannedByUUID.equals(Configuration.AUTOMATIC_IDENTIFIER)) {
                    bannedByPlayerCredentials = Configuration.AUTOMATIC_CREDENTIALS;
                } else {
                    bannedByPlayerCredentials = PlayerCredentialsManager.getByUniqueId(bannedByUUID, true);
                }

                Timestamp bannedTime = set.getTimestamp("banned_time");
                long durationTicks = set.getLong("duration_ticks");
                boolean joinBan = set.getBoolean("joinban");

                UserBan userban = new UserBan(id, player, bannedByPlayerCredentials, bannedTime, durationTicks, joinBan);
                bannedPlayers.add(userban);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load username ban list!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        InnPlugin.logInfo("Loading ipbanned players...");

        try {
            statement = DBManager.prepareStatement("SELECT * FROM banned_ip_players where expired = 0;");
            set = statement.executeQuery();

            while (set.next()) {
                int id = set.getInt("ID");
                String ipListString = set.getString("iplist");
                String playerIdString = set.getString("player_id_list");

                String bannedByPlayerId = set.getString("banned_by_player_id");
                UUID bannedByPlayerUUID = UUID.fromString(bannedByPlayerId);
                PlayerCredentials bannedByPlayerCredentials = null;

                if (bannedByPlayerUUID.equals(Configuration.SERVER_GENERATED_IDENTIFIER)) {
                    bannedByPlayerCredentials = Configuration.SERVER_GENERATED_CREDENTIALS;
                } else if (bannedByPlayerUUID.equals(Configuration.AUTOMATIC_IDENTIFIER)) {
                    bannedByPlayerCredentials = Configuration.AUTOMATIC_CREDENTIALS;
                } else {
                    bannedByPlayerCredentials = PlayerCredentialsManager.getByUniqueId(bannedByPlayerUUID, true);
                }

                Timestamp bannedTime = set.getTimestamp("banned_time");
                long durationTicks = set.getLong("duration_ticks");
                boolean joinBan = set.getBoolean("joinban");

                List<String> ipList;

                if (ipListString != null && !ipListString.isEmpty()) {
                    ipList = new ArrayList<String>(Arrays.asList(ipListString.split(", ")));
                } else {
                    ipList = new ArrayList<String>(1);
                }

                List<PlayerCredentials> playerList = new ArrayList<PlayerCredentials>();

                if (playerIdString != null && !playerIdString.isEmpty()) {
                    for (String playerId : playerIdString.split(", ")) {
                        UUID playerUUID = UUID.fromString(playerId);
                        PlayerCredentials player = PlayerCredentialsManager.getByUniqueId(playerUUID, true);
                        playerList.add(player);
                    }
                } else {
                    playerList = new ArrayList<PlayerCredentials>(1);
                }

                IPBanGroup group = new IPBanGroup(ipList, playerList);
                IPBan ipban = new IPBan(id, group, bannedByPlayerCredentials, bannedTime, durationTicks, joinBan);
                bannedPlayers.add(ipban);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load ip ban list!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Gets the full username of a partial username
     * @param checkPlayer
     * @return
     */
    public static String getPartialName(String checkPlayer) {
        for (Ban ban : Collections.synchronizedList(bannedPlayers)) {
            if (ban instanceof UserBan) {
                UserBan userban = (UserBan) ban;
                String playerName = userban.getBannedPlayerCredentials().getName();
                int minLen = Math.min(checkPlayer.length(), playerName.length());

                if (playerName.substring(0, minLen).equalsIgnoreCase(checkPlayer)) {
                    return playerName;
                }
            } else if (ban instanceof IPBan) {
                IPBan ipban = (IPBan) ban;
                List<PlayerCredentials> players = ipban.getGroup().getPlayers();

                for (PlayerCredentials player : players) {
                    String name = player.getName();
                    int minLen = Math.min(name.length(), checkPlayer.length());

                    if (name.substring(0, minLen).equalsIgnoreCase(checkPlayer)) {
                        return name;
                    }
                }
            }
        }

        return checkPlayer;
    }

    /**
     * Gets the list of banned players
     * @return
     */
    public static List<Ban> getBanList() {
        return bannedPlayers;
    }

    /**
     * Gets a list of all bans by the specified ban state
     * @param state
     * @return
     */
    public static List<Ban> getBansByState(BanState state) {
        List<Ban> bans = new ArrayList<Ban>();

        for (Ban ban : bannedPlayers) {
            if (ban.getBanState() == state) {
                bans.add(ban);
            }
        }

        return bans;
    }

    /**
     * Enum to specify a certain type of ban
     */
    public enum BanType {
        // Indicates that the user is banned
        BANNED,
        // Indicates the ban has just became active (set to join on ban)
        BANNED_JOINBAN,
        // Indicates that the ban has just become active as an IPBan
        BANNED_JOINBAN_IP,
        // Indicates that the user is IPBanned
        BANNED_IP,
        // Indicates that a user's ban expired upon logging in
        EXPIRED,
    }

    /**
     * Enum on the result of linking an ip ban
     */
    public enum LinkStatus {
        // The user has been successfully linked to an existing nip ban

        LINK_SUCCESSFUL,
        // The user to link the ip ban to is not found
        LINK_NOT_FOUND,
        // When linking an ip ban, unable to link due to not being the original ban creator
        LINK_NOT_CREATOR,
        // This person is already linked to this ip ban
        LINK_SAME_USER;
    }

    /**
     * Enum on the result of unlinking an ip ban
     */
    public enum UnlinkStatus {
        // The username to unlink the ip ban from is found

        UNLINK_FOUND,
        // The username to unlink the ip ban from is not found
        UNLINK_NOT_FOUND,
        // When unlinking the ip ban, the ban contained no other usernames or ips
        UNLINK_REMOVED_BAN,
        // When unlinking the ip ban, permission is not granted
        UNLINK_NOT_CREATOR;
    }

    /**
     * Holds the result of addBan(BanObject)
     */
    public enum BanResult {
        // This was a fresh ban
        BAN_FRESH,
        // This was a fresh IP ban
        BAN_FRESH_IP,
        // This was a modification of an existing ban
        BAN_EXISTING,
        // This was a modification of an existing IP ban
        BAN_EXISTING_IP,
    }
    
}

package net.innectis.innplugin.system.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import net.innectis.innplugin.system.bans.BanHandler.BanType;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.util.PlayerUtil;
import org.bukkit.ChatColor;

/**
 * @author AlphaBlend
 *
 * An object to describe the IPs and usernames associated with an IPBan
 */
public class IPBan extends Ban {

    private IPBanGroup group;

    public IPBan(IPBanGroup group, PlayerCredentials bannedByCredentials, Timestamp bannedTime, long durationTicks, boolean joinBan) {
        this(0, group, bannedByCredentials, bannedTime, durationTicks, joinBan);
    }

    public IPBan(int ID, IPBanGroup group, PlayerCredentials bannedByCredentials, Timestamp bannedTime, long durationTicks, boolean joinBan) {
        super(ID, bannedByCredentials, bannedTime, durationTicks, joinBan);
        this.group = group;
    }

    /**
     * Returns the group associated with this IPBan
     * @return
     */
    public IPBanGroup getGroup() {
        return group;
    }

    /**
     * Copies the IPs and players from an IPBan Group to this IPBan
     * @param group
     */
    public void copyFromGroup(IPBanGroup group) {
        List<String> ips = group.getIPs();
        List<PlayerCredentials> players = group.getPlayers();

        for (String IP : ips) {
            if (!this.group.containsIP(IP)) {
                this.group.addIP(IP);
            }
        }

        for (PlayerCredentials credentials : players) {
            if (!this.group.containsPlayerId(credentials.getUniqueId())) {
                this.group.addPlayer(credentials);
            }
        }
    }

    /**
     * Indicates if the IPBan group needs to be part of this group
     * @param group
     * @return
     */
    public boolean isPartOfThis(IPBanGroup group) {
        List<String> ips = group.getIPs();
        List<PlayerCredentials> players = group.getPlayers();

        for (String ip : ips) {
            if (this.group.containsIP(ip)) {
                return true;
            }
        }

        for (PlayerCredentials credentials : players) {
            if (this.group.containsPlayerId(credentials.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String[] getBanDetails() {
        String[] details = new String[2];
        String coloredPlayerNames = "";

        for (PlayerCredentials pc : getGroup().getPlayers()) {
            if (!coloredPlayerNames.isEmpty()) {
                coloredPlayerNames += ChatColor.WHITE + ", ";
            }

            coloredPlayerNames += PlayerUtil.getColoredName(pc);
        }

        details[0] = "IPBanned players: " + coloredPlayerNames;
        details[1] = "Details of ban: " + super.getBasicDetails();

        return details;
    }

    @Override
    public BanState getBanState() {
        if (super.isExpired()) {
            return BanState.EXPIRED;
        } else {
            if (isIndefiniteBan()) {
                return BanState.IPBAN_INDEFINITE;
            } else {
                return BanState.IPBAN_TIMED;
            }
        }
    }

    @Override
    public BanType getType() {
        if (super.isJoinBan()) {
            return BanType.BANNED_JOINBAN_IP;
        } else if (super.isExpired()) {
            return BanType.EXPIRED;
        } else {
            return BanType.BANNED_IP;
        }
    }

    /**
     * Updates this ban in the database
     */
    @Override
    public void save() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            String ipList = null;

            for (String ip : group.getIPs()) {
                if (ipList == null) {
                    ipList = ip;
                } else {
                    ipList += ", " + ip;
                }
            }

            String playerIdList = null;

            for (PlayerCredentials pc : group.getPlayers()) {
                String playerId = pc.getUniqueId().toString();

                if (playerIdList == null) {
                    playerIdList = playerId;
                } else {
                    playerIdList += ", " + playerId;
                }
            }

            if (getId() > 0) {
                statement = DBManager.prepareStatement("UPDATE banned_ip_players SET iplist = ?, player_id_list = ?, banned_by_player_id = ?, banned_time = ?, duration_ticks = ?, joinban = ? WHERE ID = ?");
                statement.setString(1, ipList);
                statement.setString(2, playerIdList);
                statement.setString(3, super.getBannedByCredentials().getUniqueId().toString());
                statement.setTimestamp(4, getBanStartTime());
                statement.setLong(5, getDurationTicks());
                statement.setBoolean(6, isJoinBan());
                statement.setInt(7, getId());
                statement.executeUpdate();
            } else {
                statement = DBManager.prepareStatementWithAutoGeneratedKeys("INSERT INTO banned_ip_players (iplist, player_id_list, banned_by_player_id, banned_time, duration_ticks, joinban) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, ipList);
                statement.setString(2, playerIdList);
                statement.setString(3, super.getBannedByCredentials().getUniqueId().toString());
                statement.setTimestamp(4, getBanStartTime());
                statement.setLong(5, getDurationTicks());
                statement.setBoolean(6, isJoinBan());
                statement.execute();

                set = statement.getGeneratedKeys();

                if (set.next()) {
                    setId(set.getInt(1));
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save this IPBan!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Deletes this IPBan from the database
     */
    @Override
    public void delete() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE banned_ip_players SET expired = ? WHERE ID = ?");
            statement.setLong(1, System.currentTimeMillis());
            statement.setInt(2, getId());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete this IPBan!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    
}

package net.innectis.innplugin.handlers.iplogging;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.apache.commons.lang.NotImplementedException;

/**
 *
 * @author Hret
 *
 * This class contains some information about the player and its IP address.
 * Also the accounts that are linked to this one (used same IP) can be found here.
 */
public final class Playerinfo {

    private final UUID playerId;
    private final String playerName;
    private HashSet<IPAddress> ipadresses = new HashSet<IPAddress>();

    // <editor-fold defaultstate="collapsed" desc="Contructors">
    private Playerinfo(UUID playerId, String playerName) {
        this(playerId, playerName, null);
    }

    private Playerinfo(UUID playerId, String playerName, HashSet<IPAddress> ipadresses) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.ipadresses = ipadresses;
    }

    // </editor-fold>
    /**
     * Returns the ID of this player
     * @return
     */
    public UUID getUniqueId() {
        return playerId;
    }

    /**
     * Returns a set of IP addresses that are linked to the player.
     * @return UnmodifiableSet of IpAddresses
     */
    public Set<IPAddress> getIpadresses() {
        return Collections.unmodifiableSet(ipadresses);
    }

    /**
     * Adds an IP address to the list
     * @param addr
     */
    @Deprecated
    public void addIpAddress(IPAddress addr) {
        throw new NotImplementedException();
        //TODO: Adds the IP to the hashset and links it to the player in the database
    }

    /**
     * Looks for the IP addresses that have the supplied partial IP in there. <br/>
     * Meaning '127.' would react to '127.0.0.1' but not to '198.127.0.1'<br/>
     * Where '.127.' will react to '198.127.0.1' but not to '127.0.0.1' <br/>
     * The '*' can be used as wildchar
     * @param ip
     * @return null if none found.<br/>
     * Otherwise a list with the IP'addresses that match
     */
    public IPAddress[] lookupPartialIp(String partialIp) {
        List<IPAddress> returnlist = new ArrayList<IPAddress>();
        Set<IPAddress> addresses = getIpadresses();
        for (IPAddress address : addresses) {
            if (address.toString().contains(partialIp)) {
                returnlist.add(address);
            }
        }
        return returnlist.toArray(new IPAddress[returnlist.size()]);
    }

    // <editor-fold defaultstate="collapsed" desc="Find Related">
    /**
     * This method will look in the database for usernames that are linked to this account.
     * An account gets linked when the other username has used the same IP to log in.
     * @return Names of the users that are linked to this account (including own).
     * @throws SQLException
     */
    public List<String> findRelatedUsernames() {
        List<String> playernames = new ArrayList<String>();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT DISTINCT player_id FROM ip_log WHERE ip IN (SELECT distinct ip FROM ip_log WHERE player_id = ?);");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();

            while (result.next()) {
                String playerIdString = result.getString("player_id");
                UUID playerId = UUID.fromString(playerIdString);
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);

                playernames.add(credentials.getName());
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot find related usernames for player " + playerName + "!", ex);
            return null;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return playernames;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Static methods">
    /**
     * Looks for the playerinformation in the database.<br/>
     * If no data is found, null will be returned
     *
     * @param playerId
     * @return The info of the player or null if player not found or no IP-addresses;
     */
    public static Playerinfo findPlayer(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            // Get the addresses of the player.
            statement = DBManager.prepareStatement("SELECT DISTINCT ip FROM `ip_log` where `player_id` = ?;");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();
            HashSet<IPAddress> addresses = new HashSet<IPAddress>();

            while (result.next()) {
                addresses.add(new IPAddress(result.getString("ip")));
            }

            // return the object
            return new Playerinfo(playerId, playerName, addresses);
        } catch (SQLException ex) {
            Logger.getLogger(Playerinfo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }
    //</editor-fold>

}

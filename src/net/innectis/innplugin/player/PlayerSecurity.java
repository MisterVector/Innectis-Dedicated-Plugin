package net.innectis.innplugin.player;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.iplogging.GeoLocation;
import net.innectis.innplugin.handlers.iplogging.IPAddress;
import net.innectis.innplugin.InnPlugin;

/**
 *
 * @author Hret
 *
 * Ugly static handler class for the security of a player.
 * Due to it being optional I put in in a seperate class to
 * avoid having it tied into the PlayerSession.
 *
 */
public class PlayerSecurity {

    /** Amount of times the hash needs to repeat itself (high value not needed) */
    private static final int DEFAULT_MULTI = 4;
    /** The distance 2 IP's need to be from eachother to trigger a warning (in KM) */
    private static final double TRIGGER_DISTANCE = 500;
    /** The amount of days before an IP becomes invalid and isn't checked for distance. */
    public static final int IP_INVALIDIATIONDAYS = 2;
    /** The amount of hours to check for bad logins. */
    public static final int BADLOGIN_HOURSCHECK = 3;
    /** The amount of tries a player is allowed in above time range. */
    public static final int BADLOGIN_MAXTIMES = 3;

    private PlayerSecurity() {
    }

    /**
     * This method will check the last IP the player used and check the distance to it.
     * <p/>
     * If the distance is greater then a pre-configured distance a message will be shown to the player.
     * @param player
     */
    public static void checkPlayer(IdpPlayer player) {
        String currentip = player.getHandle().getAddress().getAddress().getHostAddress();
        String lastIp = getLastIp(player);

        // Check if there is a previous ip
        if (lastIp == null) {
            return;
        }

        // Check IP info
        IPAddress currentAddr = new IPAddress(currentip);
        IPAddress previousAddr = new IPAddress(lastIp);

        GeoLocation currIpGeo = currentAddr.getGeoLocation();
        GeoLocation lastIpGeo = previousAddr.getGeoLocation();

        // Cant check if location not found...
        if (currIpGeo == null || lastIpGeo == null) {
            InnPlugin.logError("Can't lookup IP of " + player.getColoredName());
            return;
        }

        double distanceKM = currIpGeo.distanceFrom(lastIpGeo) / 1000;

        // Check if not 'safe' zone
        if (distanceKM > TRIGGER_DISTANCE) {
            // Print to player
            player.printError("Your account has been accessed from a different IP.");
            player.printError("Used IP: " + lastIp);
            player.printError("The IP was traced back to: " + lastIpGeo.getCity() + " (" + lastIpGeo.getCountryCode() + ")");
            player.printError("If this was not you we suggest changing your password.");

            // Console logs
            InnPlugin.logInfo("Player " + player.getColoredName() + " might be the victim of an hacking attempt");
            InnPlugin.logInfo("Current IP: " + currentip + " - " + currIpGeo.getCity() + " (" + currIpGeo.getCountryCode() + ")");
            InnPlugin.logInfo("Previous IP: " + lastIp + " - " + lastIpGeo.getCity() + " (" + lastIpGeo.getCountryCode() + ")");
            DecimalFormat format = new DecimalFormat("###,###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            InnPlugin.logInfo("Distance: " + format.format(distanceKM).replace(",", ".") + " KM");
        }
    }

    /**
     * This will lookup the IP the player represented by their ID has used <u>before</u> the current one.
     * Meaning that this method will skip the first IP it finds.
     * @param player
     * @return the IP used before the current IP, otherwise null
     */
    private static String getLastIp(IdpPlayer player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT ip, logtime FROM ip_log WHERE player_id = ? ORDER BY logid DESC LIMIT 2;");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -IP_INVALIDIATIONDAYS);

            result.next(); // skip first

            if (result.next()) {
                Date date = result.getTimestamp("logtime");

                // Only if the IP is still needed.
                if (date.after(cal.getTime())) {
                    return result.getString("ip");
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Can't get previous IP of " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * This will check the password of a player
     * @param player
     * @param password
     * @return
     */
    public static boolean checkPlayerPassword(IdpPlayer player, String password) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();

        byte[] inputpass = calculateSecurityHash(playerId, password, DEFAULT_MULTI);
        byte[] pass = getPlayerPassword(playerId, playerName);

        if (pass == null || inputpass == null) {
            return false;
        }

        boolean isvalid = true;
        for (int i = 0; i < inputpass.length; i++) {
            if (inputpass[i] != pass[i]) {
                isvalid = false;
            }
        }

        return isvalid;
    }

    /**
     * This will log a bad password for a player
     * @param playerId
     */
    public static boolean canLogin(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -BADLOGIN_HOURSCHECK);

            statement = DBManager.prepareStatement("SELECT logid FROM player_failedlogin WHERE logdate > ? AND player_id = ?;");
            statement.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
            statement.setString(2, playerId.toString());
            set = statement.executeQuery();

            // No record, player is new, so allow login
            if (!set.next()) {
                return true;
            }

            int counter = 0;

            while (set.next()) {
                counter++;
            }

            return counter < BADLOGIN_MAXTIMES;
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot check if player " + playerName + " can login, disallowing login.", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * This will check if the player has a password setup.
     * @param player
     */
    public static boolean hasPassword(IdpPlayer player) {
        return hasPassword(player.getUniqueId(), player.getName());
    }

    /**
     * This will check if the player has a password setup.
     * @param playerId
     * @param playerName
     * @return
     */
    public static boolean hasPassword(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT player_id FROM player_password WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();

            if (result.next()) {
                return true;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Error checking password for player " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * This will log a bad password for a player
     * @param player
     * @param password
     */
    public static boolean setPassword(IdpPlayer player, String password) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();

        PreparedStatement statement = null;

        try {
            if (password == null) {
                return false;
            }

            byte[] pass = calculateSecurityHash(playerId, password, DEFAULT_MULTI);

            statement = DBManager.prepareStatement(" INSERT INTO player_password (player_id, password, dateset) "
                    + " values (?,?, CURRENT_TIMESTAMP) ON DUPLICATE KEY update password = ?, dateset = CURRENT_TIMESTAMP; ");
            statement.setString(1, playerId.toString());
            statement.setBytes(2, pass);
            statement.setBytes(3, pass);
            statement.executeUpdate();

            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Error setting password of " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * This will delete the password by the specified player ID
     * @param playerId
     * @param playerName
     * @return true if password was removed
     */
    public static boolean removePassword(UUID playerId, String playerName) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM player_password WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            statement.executeUpdate();

            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Error removing password for player " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * This will log a bad password for a player
     * @param player
     * @param ip
     */
    public static void logBadPassword(IdpPlayer player, String ip) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO player_failedlogin (player_id, ip, logdate) VALUES (?,?,?);");
            statement.setString(1, playerId.toString());
            statement.setString(2, ip);
            statement.setTimestamp(3, new Timestamp(new Date().getTime()));
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot log bad password! (Player " + playerName + " trying pass with IP of " + ip + " on " + new Date().getTime() + ")", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Returns a list of all player passwords
     * @return
     */
    public static List<PlayerPassword> getAllPlayerPasswords() {
        List<PlayerPassword> playerPasswords = new ArrayList<PlayerPassword>();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT player_id, password, dateset FROM player_password;");
            set = statement.executeQuery();

            while (set.next()) {
                UUID playerId = UUID.fromString(set.getString("player_id"));
                String password = set.getString("password");
                Timestamp timestamp = set.getTimestamp("dateset");
                PlayerPassword playerPassword = new PlayerPassword(playerId, password, timestamp);
                playerPasswords.add(playerPassword);
            }

        } catch (SQLException ex) {
            InnPlugin.logError("Unable to get all player passwords! ", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return playerPasswords;
    }

    /**
     * Calculates a hash of the given salt and input
     * @param salt
     * @param input
     * @param multiplyer
     * Amount of times the hash must rewrite itself.
     * @return
     */
    private static byte[] calculateSecurityHash(UUID playerId, String input, int multiplyer) {
        try {
            // Add salt using player's name
            String salt = playerId.toString();

            java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt.getBytes());
            byte[] buffer = messageDigest.digest(input.getBytes());
            for (int i = 0; i < multiplyer; i++) {
                messageDigest.reset();
                buffer = messageDigest.digest(buffer);
            }
            return buffer;
        } catch (NoSuchAlgorithmException ex) {
            InnPlugin.logError("Error digesting password for player ID: " + playerId.toString() + "!", ex);
            return null;
        }
    }

    /**
     * Looks up the encrypted password based on the player ID
     * @param playerId
     * @return
     */
    private static byte[] getPlayerPassword(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT password FROM player_password WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            result = statement.executeQuery();

            if (result.next()) {
                return result.getBytes("password");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Error getting password of " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;

    }
    
}
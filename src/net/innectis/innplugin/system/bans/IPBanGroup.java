package net.innectis.innplugin.system.bans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.player.PlayerCredentials;

/**
 * @author AlphaBlend
 *
 * Describes the IPs and usernames associated with an IPBan
 *
 */
public class IPBanGroup {

    private List<String> iplist;
    private List<PlayerCredentials> players;

    public IPBanGroup(String IP, PlayerCredentials credentials) {
        this.iplist = new ArrayList<String>(1);
        this.iplist.add(IP);

        this.players = new ArrayList<PlayerCredentials>(1);
        this.players.add(credentials);
    }

    public IPBanGroup(List<String> iplist, PlayerCredentials credentials) {
        this.iplist = iplist;

        this.players = new ArrayList<PlayerCredentials>(1);
        this.players.add(credentials);
    }

    public IPBanGroup(String ip, List<PlayerCredentials> players) {
        this.iplist = new ArrayList<String>(1);
        this.iplist.add(ip);

        this.players = players;
    }

    public IPBanGroup(List<String> iplist, List<PlayerCredentials> players) {
        this.iplist = iplist;
        this.players = players;
    }

    /**
     * Gets the IPs associated with this IPBan
     * @return
     */
    public List<String> getIPs() {
        return iplist;
    }

    /**
     * Gets the usernames associated with this IPBan
     * @return
     */
    public List<PlayerCredentials> getPlayers() {
        return players;
    }

    /**
     * Adds an IP to the IP list
     * @param ip
     * @return whether the IP was added or not
     */
    public boolean addIP(String ip) {
        if (containsIP(ip)) {
            return false;
        }

        return iplist.add(ip);
    }

    /**
     * Adds a username to this IPBan
     * @param credentials
     * @return whether the username was added or not
     */
    public boolean addPlayer(PlayerCredentials credentials) {
        if (containsPlayerId(credentials.getUniqueId())) {
            return false;
        }

        return players.add(credentials);
    }

    /**
     * Removes the specified IP from the list of IPs
     * @param ip
     * @return
     */
    public boolean removeIP(String ip) {
        if (!containsIP(ip)) {
            return false;
        }

        return iplist.remove(ip);
    }

    /**
     * Removes the specified username from the list of usernames
     * @param credentials
     * @return
     */
    public boolean removePlayer(PlayerCredentials credentials) {
        if (!containsPlayerId(credentials.getUniqueId())) {
            return false;
        }

        return players.remove(credentials);
    }

    /**
     * Returns if the IP is in this group
     * @param IP
     * @return
     */
    public boolean containsIP(String IP) {
        return iplist.contains(IP);
    }

    /**
     * Returns if the player represented by the unique ID
     * is in this group
     * @param playerId
     * @return
     */
    public boolean containsPlayerId(UUID playerId) {
        for (PlayerCredentials credentials : players) {
            if (credentials.getUniqueId().equals(playerId)) {
                return true;
            }

        }

        return false;
    }

    /**
     * Returns whether or not this group contains a player ID or IP
     * @param playerId
     * @param ip
     * @return
     */
    public boolean containsIPOrPlayer(UUID playerId, String ip) {
        return containsIP(ip) || containsPlayerId(playerId);
    }
    
}

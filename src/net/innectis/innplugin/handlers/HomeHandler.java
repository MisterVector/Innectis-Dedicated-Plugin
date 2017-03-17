package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.IdpHome;
import net.innectis.innplugin.objects.IdpHomes;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A handler that keeps track of all players' homes
 *
 * @author AlphaBlend
 */
public class HomeHandler {

    private static Map<UUID, IdpHomes> playerHomes = new HashMap<UUID, IdpHomes>();

    /**
     * Removes the player homes of the player based on ID
     * @param playerId
     */
    public static void removePlayerHomes(UUID playerId) {
        playerHomes.remove(playerId);
    }

    /**
     * Gets a player homes object for the specified player
     * @param player
     * @return
     */
    public static IdpHomes getPlayerHomes(IdpPlayer player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();

        IdpHomes homes = null;

        if (playerHomes.containsKey(playerId)) {
            homes = playerHomes.get(playerId);
        } else {
            homes = loadPlayerHomes(playerId, playerName);
            playerHomes.put(playerId, homes);
        }

        return homes;
    }

    /**
     * Gets how many homes a specific player group can have
     * @param group
     * @return
     */
    public static int getAvailableHomeCount(PlayerGroup group) {
        switch (group) {
            case SADMIN:
            case ADMIN:
                return 20;
            case MODERATOR:
                return 10;
            case DIAMOND:
                return 5;
            case GOLDY:
                return 3;
            case SUPER_VIP:
                return 2;
            case VIP:
            case USER:
            case GUEST:
                return 1;
        }

        return 0;
    }

    /**
     * Load the specified player's homes from the database
     * @return
     */
    private static IdpHomes loadPlayerHomes(UUID playerId, String playerName) {
        IdpHomes homes = new IdpHomes();
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM homes WHERE player_id = ?");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            while (set.next()) {
                String worldName = set.getString("world");
                World world = Bukkit.getWorld(worldName);

                // Don't continue if home is in unloaded world
                if (world == null) {
                    continue;
                }

                int ID = set.getInt("ID");
                int homeId = set.getInt("homeid");
                String homeName = set.getString("homename");

                int x = set.getInt("locx");
                int y = set.getInt("locy");
                int z = set.getInt("locz");
                float yaw = set.getFloat("yaw");

                Location loc = new Location(world, x, y, z, yaw, 0);

                IdpHome home = new IdpHome(ID, playerId, homeId, homeName, loc);
                homes.addHome(homeId, home);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Could not load player homes for " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return homes;
    }

}

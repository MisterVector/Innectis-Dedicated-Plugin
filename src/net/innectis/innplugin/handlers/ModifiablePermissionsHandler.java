package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ModifiablePermissions;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * This handles the modifiable permissions of players.
 *
 * @author AlphaBlend
 */
public final class ModifiablePermissionsHandler {

    public enum PermissionType {
        // Indicates an additional permission
        ADDITIONAL,

        // Indicates a disabled permission
        DISABLED;
    }

    public ModifiablePermissionsHandler() {}

    /**
     * Removes any invalid permissions from the database
     */
    public static void removeInvalidPermissions() {
        List<Integer> invalidPerms = new ArrayList<Integer>();

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT permissionid FROM player_permission");
            set = statement.executeQuery();

            while (set.next()) {
                int id = set.getInt("permissionid");
                Permission testPerm = Permission.getPermission(id);

                if (testPerm == Permission.NONE) {
                    invalidPerms.add(id);
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to check for invalid player permissions!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        if (invalidPerms.size() > 0) {
            for (Integer id : invalidPerms) {
               try {
                   statement = DBManager.prepareStatement("DELETE FROM player_permission WHERE permissionid = ?");
                   statement.setInt(1, id);
                   statement.execute();
               } catch (SQLException ex) {
                   InnPlugin.logError("Unable to remove invalid permission ID " + id + "!", ex);
                   break;
               } finally {
                   DBManager.closePreparedStatement(statement);
               }
            }

            InnPlugin.logInfo("Removed " + invalidPerms.size() + " invalid modifiable permissions!");
        }
    }

    /**
     * Loads the modifiable permissions from database for specified username
     * @param playerId
     * @return
     */
    public static ModifiablePermissions loadModifiedPermissions(UUID playerId) {
        PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId, true);
        ModifiablePermissions perms = new ModifiablePermissions(credentials);

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM player_permission WHERE player_id = ?");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            while (set.next()) {
                int permID = set.getInt("permissionid");
                boolean disabled = set.getBoolean("disabled");
                perms.addPermissionNoSave(permID, disabled ? PermissionType.DISABLED : PermissionType.ADDITIONAL);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot load modifiable permissions for player with ID: " + playerId.toString() + "!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return perms;
    }

    /**
     * Grabs a list of all the players with special permissions
     * @return
     */
    public static List<ModifiablePermissions> getAllModifiedPermsFromDB() {
        HashMap<UUID, ModifiablePermissions> tempMap = new HashMap<UUID, ModifiablePermissions>();
        List<ModifiablePermissions> permList = new ArrayList<ModifiablePermissions>();

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM player_permission");
            set = statement.executeQuery();

            while (set.next()) {
                String playerIdString = set.getString("player_id");
                UUID playerId = UUID.fromString(playerIdString);
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);
                int permID = set.getInt("permissionid");
                boolean disabled = set.getBoolean("disabled");

                if (!tempMap.containsKey(playerId)) {
                    ModifiablePermissions perms = new ModifiablePermissions(credentials);
                    perms.addPermissionNoSave(permID, (disabled ? PermissionType.DISABLED : PermissionType.ADDITIONAL));
                    tempMap.put(playerId, perms);
                } else {
                    ModifiablePermissions perms = tempMap.get(playerId);
                    perms.addPermissionNoSave(permID, (disabled ? PermissionType.DISABLED : PermissionType.ADDITIONAL));
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to retrieve list of special permissions!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        for (ModifiablePermissions perm : tempMap.values()) {
            permList.add(perm);
        }

        return permList;
    }
    
}

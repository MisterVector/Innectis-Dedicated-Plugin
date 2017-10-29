package net.innectis.innplugin.objects;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler.PermissionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerCredentials;

/**
 * Class that manages additional / disabled permissions for a player
 *
 * @author AlphaBlend
 */
public class ModifiablePermissions {

    // The player credentials these permissions apply to
    private PlayerCredentials credentials;

    // A map that associates a permission with its type
    private Map<Integer, PermissionType> permissions = new HashMap<Integer, PermissionType>();

    /*
     * Constructs a new modifiable permissions object with just the username
     * they are associated with
     */
    public ModifiablePermissions(PlayerCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Gets the credentials of the player these modified permissions belong to
     * @return
     */
    public PlayerCredentials getCredentials() {
        return credentials;
    }

    /**
     * Returns the username of these modifiable permissions
     * @return
     */
    public String getUsername() {
        return credentials.getName();
    }

    /**
     * Constructs a modifiable permissions object with existing permissions
     * @param credentials
     * @param permissions
     */
    public ModifiablePermissions(PlayerCredentials credentials, Map<Integer, PermissionType> permissions) {
        this.credentials = credentials;
        this.permissions = permissions;
    }

    /**
     * Gets the permission type of the specified ID
     * @param id
     * @return
     */
    public PermissionType getPermissionType(int id) {
        return permissions.get(id);
    }

    /**
     * Adds the specified permission and its type
     * @param id
     * @param type
     */
    public void addPermission(int id, PermissionType type) {
        permissions.put(id, type);
        savePermissionInDB(id, type);
    }

    /**
     * Adds the specified permission and its type without saving to DB
     * @param id
     * @param type
     */
    public void addPermissionNoSave(int id, PermissionType type) {
        permissions.put(id, type);
    }

    /**
     * Removes the specified permission
     * @param id
     * @return true if successfully removed, false otherwise
     */
    public boolean removePermission(int id, PermissionType type) {
        PermissionType existingType = permissions.get(id);

        if (existingType == type) {
            permissions.remove(id);
            deletePermissionFromDB(id);
            return true;
        }

        return false;
    }

    /**
     * Gets an unmodifiable map of all permissions
     * @return
     */
    public Map<Integer, PermissionType> getAllPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    /**
     * Gets all permission IDs by the specified type
     * @param type
     * @return
     */
    public List<Integer> getPermissionIDByType(PermissionType type) {
        List<Integer> permIDs = new ArrayList<Integer>();

        for (Map.Entry<Integer, PermissionType> set : permissions.entrySet()) {
            if (set.getValue() == type) {
                permIDs.add(set.getKey());
            }
        }

        return permIDs;
    }

    /**
     * Returns if there are any modified permissions
     * @return
     */
    public boolean hasModifiablePermissions() {
        return !permissions.isEmpty();
    }

    /**
     * Saves the permission in the database
     * @param id
     * @param type
     */
    private void savePermissionInDB(int id, PermissionType type) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO player_permission (player_id, permissionid, disabled) VALUES (?, ?, ?)");
            statement.setString(1, credentials.getUniqueId().toString());
            statement.setInt(2, id);
            statement.setBoolean(3, type == PermissionType.DISABLED);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save permission id " + id + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Deletes the specified permission from the database
     * @param id
     */
    private void deletePermissionFromDB(int id) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM player_permission WHERE player_id = ? AND permissionid = ?");
            statement.setString(1, credentials.getUniqueId().toString());
            statement.setInt(2, id);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete permission id " + id + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

}

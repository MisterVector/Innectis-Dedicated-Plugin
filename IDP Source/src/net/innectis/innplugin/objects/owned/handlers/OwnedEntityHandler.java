package net.innectis.innplugin.objects.owned.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.OwnedEntity;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.entity.EntityType;

/**
 * A handler for all player owned entities
 *
 * @author AlphaBlend
 */
public class OwnedEntityHandler {

    // A mmap of all player owned entities
    private static Map<UUID, OwnedEntity> ownedEntities = new HashMap<UUID, OwnedEntity>();

    public OwnedEntityHandler() {}

    /**
     * Loads all owned entities
     * @param owner
     */
    public static boolean loadOwnedEntities() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM owned_entities;");
            set = statement.executeQuery();

            while (set.next()) {
                String playerIdString = set.getString("owner_id");
                UUID playerId = UUID.fromString(playerIdString);
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId, true);
                EntityType type = EntityType.fromId(set.getInt("entityid"));

                long mostSigBits = set.getLong("mostsigbits");
                long leastSigBits = set.getLong("leastsigbits");
                UUID ownedEntityId = new UUID(mostSigBits, leastSigBits);

                OwnedEntity ownedEntity = new OwnedEntity(credentials, type);
                ownedEntities.put(ownedEntityId, ownedEntity);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET OWNED ENTITIES FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * Gets an owned entity by its specified UUID
     * @param uniqueId
     * @return
     */
    public static OwnedEntity getOwnedEntity(UUID uniqueId) {
        return ownedEntities.get(uniqueId);
    }

    /**
     * Adds an owned entity to the list
     * @param entityId
     * @param ownerCredentials
     * @param type
     */
    public static void addOwnedEntity(UUID entityId, PlayerCredentials ownerCredentials, EntityType type) {
        OwnedEntity ownedEntity = new OwnedEntity(ownerCredentials ,type);
        ownedEntities.put(entityId, ownedEntity);

        long mostSigBits = entityId.getMostSignificantBits();
        long leastSigBits = entityId.getLeastSignificantBits();

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO owned_entities (owner_id, entityid, mostsigbits, leastsigbits) VALUES (?, ?, ?, ?);");
            statement.setString(1, ownerCredentials.getUniqueId().toString());
            statement.setInt(2, type.getTypeId());
            statement.setLong(3, mostSigBits);
            statement.setLong(4, leastSigBits);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save owned entity to the database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Removes the specified owned entityId from the database
     * @param entityId
     */
    public static void removeOwnedEntity(UUID entityId) {
        OwnedEntity ownedEntity = ownedEntities.remove(entityId);

        // Doesn't exist, so don't attempt to remove
        if (ownedEntity == null) {
            return;
        }

        long mostSigBits = entityId.getMostSignificantBits();
        long leastSigBits = entityId.getLeastSignificantBits();

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM owned_entities WHERE mostsigbits = ? AND leastsigbits = ?;");
            statement.setLong(1, mostSigBits);
            statement.setLong(2, leastSigBits);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to remove owned entity from the database!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    
}

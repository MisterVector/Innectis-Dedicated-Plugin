package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.StaffMessage;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * A class that handles all staff requests that players have
 *
 * @author AlphaBlend
 */
public final class StaffMessageHandler {

    // Lists all staff requests
    private static List<StaffMessage> staffRequests = new LinkedList<StaffMessage>();

    private StaffMessageHandler() {}

    /**
     * Loads the staff requests
     */
    public static void loadStaffRequests() {
        InnPlugin.logInfo("Loading staff requests...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM staff_requests");
            set = statement.executeQuery();

            while (set.next()) {
                int id = set.getInt("id");
                Date date = new Date(set.getDate("datecreated").getTime());
                boolean read = set.getBoolean("hasread");

                String creatorId = set.getString("creator_id");
                UUID creatorUUID = UUID.fromString(creatorId);
                PlayerCredentials creatorCredentials = PlayerCredentialsManager.getByUniqueId(creatorUUID, true);

                String message = set.getString("message");

                StaffMessage sm = new StaffMessage(id, date, read, creatorCredentials, message);
                staffRequests.add(sm);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load staff requests!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Counts all unread staff messages
     * @param  name the specified name to search for
     * @return
     */
    public static int countUnreadStaffMessages(String name) {
        int count = 0;

        for (StaffMessage sm : staffRequests) {
            if (name != null) {
                if (name.equalsIgnoreCase(sm.getCreator())) {
                    count++;
                }
            } else {
                if (!sm.hasRead()) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Gets the staff request by the ID
     * @param idx
     * @return
     */
    public static StaffMessage getStaffRequestById(int id) {
        for (StaffMessage msg : staffRequests) {
            if (msg.getId() == id) {
                return msg;
            }
        }

        return null;
    }

    /**
     * Adds a new staff request
     * @param sm
     * @return true if no conflicting staff request was in the list
     */
    public static boolean addStaffRequest(StaffMessage sm) {
        for (StaffMessage msg : staffRequests) {
            if (msg.getMessage().equalsIgnoreCase(sm.getMessage())) {
                return false;
            }
        }

        staffRequests.add(sm);
        sm.save();
        return true;
    }

    /**
     * Removes the staff request represented by its ID
     * @param idx
     * @return The staff message that is being deleted
     */
    public static StaffMessage deleteStaffRequeustById(int id) {
        if (staffRequests.isEmpty()) {
            return null;
        }

        for (StaffMessage msg : staffRequests) {
            if (msg.getId() == id) {
                staffRequests.remove(msg);
                return msg;
            }
        }

        return null;
    }

    /**
     * Returns an unmodifiable list of the staff requests
     * @return
     */
    public static List<StaffMessage> getStaffRequests() {
        return Collections.unmodifiableList(staffRequests);
    }
    
}

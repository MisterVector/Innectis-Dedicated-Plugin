package net.innectis.innplugin.system.mail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * Handles the mail for each user
 *
 * @author AlphaBlend
 */
public class MailHandler {

    private MailHandler() {}

    /**
     * Loads player's mail to the session
     *
     * @param playerId
     */
    public static void loadPlayerMailToSession(IdpPlayer player) {
        PlayerCredentials toPlayerCredentials = PlayerCredentialsManager.getByUniqueId(player.getUniqueId());
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM playermail WHERE to_player_id = ?");
            statement.setString(1, player.getUniqueId().toString());
            set = statement.executeQuery();

            while (set.next()) {
                int ID = set.getInt("ID");
                Date date = new Date(set.getDate("datecreated").getTime());
                boolean read = set.getBoolean("readmail");

                String fromPlayerIdString = set.getString("from_player_id");
                UUID fromPlayerId = UUID.fromString(fromPlayerIdString);
                PlayerCredentials fromPlayerCredentials = null;

                if (fromPlayerId.equals(Configuration.SERVER_GENERATED_IDENTIFIER)) {
                    fromPlayerCredentials = Configuration.SERVER_GENERATED_CREDENTIALS;
                } else {
                    fromPlayerCredentials = PlayerCredentialsManager.getByUniqueId(fromPlayerId);
                }

                String title = set.getString("title");
                String content = set.getString("content");

                MailMessage obj = new MailMessage(ID, date, read, fromPlayerCredentials, toPlayerCredentials, title, content);
                player.getSession().addMail(obj);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load mail! " + ex.getMessage());
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }
    }
    
}

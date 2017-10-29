package net.innectis.innplugin.tasks.async;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This method cleans up the block quota log.
 */
public class QuotaCleanup extends RepeatingTask {

    public QuotaCleanup(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.QuotaCleanup);
    }

    @Override
    public void run() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("DELETE FROM block_quota_log WHERE time < DATE_SUB(NOW(), INTERVAL 2 HOUR)");
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot clear block quota log!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}

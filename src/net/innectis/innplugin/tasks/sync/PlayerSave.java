package net.innectis.innplugin.tasks.sync;

import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This the details about players, like the inventory and onlinetime.
 * This also upgrades a guest user to a higher rank when they have been online for a given amount of time.
 */
public class PlayerSave extends RepeatingTask {

    private InnPlugin plugin;

    public PlayerSave(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.PlayerSave);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (IdpPlayer p : plugin.getOnlinePlayers()) {
            try {
                if (p == null) {
                    continue;
                }

                // Save the players inventory
                p.saveInventory();
                // Save the onlinetime of the player
                p.getSession().saveOnlineTime();
            } catch (NullPointerException npe) {
                plugin.logError("NPE in playersave? ", npe);
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}

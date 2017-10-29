package net.innectis.innplugin.tasks.sync;

import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 * A limited task that sends to all online players the
 * current player count, to update their TAB list
 *
 * @author AlphaBlend
 */
public class TabListPlayerCountTask extends LimitedTask {

    private InnPlugin plugin = null;

    public TabListPlayerCountTask(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, 50, 1);

        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<IdpPlayer> players = plugin.getOnlinePlayers();
        int playerCount = players.size();

        // Update player count to each player's TAB list
        for (IdpPlayer player : players) {
            player.sendPlayersOnlineTabList(playerCount);
        }
    }
    
}

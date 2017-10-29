package net.innectis.innplugin.tasks.async;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 * AsyncTask that runs every 5 minutes that tells guests to
 * create a lot if they haven't already done so
 */
public class LotReminder extends RepeatingTask {

    private InnPlugin plugin;

    public LotReminder(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.LotReminder);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (IdpPlayer p : plugin.getOnlinePlayers()) {
            if (p.getGroup() == PlayerGroup.GUEST && LotHandler.getLots(p.getName()).isEmpty()) {
                p.print(ChatColor.WHITE, "Still don't have a lot? Type " + ChatColor.AQUA + "/getlot" + ChatColor.WHITE + " to get one!");
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}

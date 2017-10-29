package net.innectis.innplugin.tasks.async;

import java.util.Iterator;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.LotFlagToggle;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * This handles lotflagtoggles
 */
public class LotflagToggles extends RepeatingTask {

    private InnPlugin plugin;

    public LotflagToggles(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.LotFlags);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        synchronized (plugin.getLotFlagToggles()) {
            for (Iterator<Long> it = plugin.getLotFlagToggles().keySet().iterator(); it.hasNext();) {
                long time = it.next();
                if (getLastExecution() - time > 30000) {
                    LotFlagToggle toggle = plugin.getLotFlagToggles().get(time);
                    InnectisLot toggleLot = toggle.getLot();
                    toggleLot.setFlag(toggle.getFlag(), toggle.getDisable());

                    if (toggleLot.save()) {
                        toggleLot.sendMessageToNearbyPlayers(ChatColor.LIGHT_PURPLE + Configuration.MESSAGE_PREFIX + "PvP is now "
                            + (toggle.getDisable() ? "disabled" : "enabled") + " on " + toggle.getLot().getOwner() + "'s lot!", 30);
                    }

                    it.remove();
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}

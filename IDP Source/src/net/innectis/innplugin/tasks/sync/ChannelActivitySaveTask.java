package net.innectis.innplugin.tasks.sync;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 * Repeating task that saves all channel activity
 *
 * @author AlphaBlend
 */
public class ChannelActivitySaveTask extends RepeatingTask {

    InnPlugin plugin;

    public ChannelActivitySaveTask(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.ChannelActivitySaveTask);
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "channel activity save task";
    }

    @Override
    public void run() {
        ChatChannelHandler.saveAllActivity();
    }
    
}

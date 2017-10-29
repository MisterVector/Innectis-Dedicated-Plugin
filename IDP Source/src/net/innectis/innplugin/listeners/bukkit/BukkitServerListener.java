package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.InnBukkitListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Lynxy
 */
public class BukkitServerListener implements InnBukkitListener {

    private InnPlugin plugin;

    public BukkitServerListener(InnPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin dynplug = event.getPlugin();
        String name = dynplug.getDescription().getName();

        if (name.equalsIgnoreCase("dynmap")) {
            if (plugin.getExternalLibraryManager().setupDynmap(plugin, dynplug)) {
                InnPlugin.logInfo("Interfaced with the dynmap API!");
            } else {
                InnPlugin.logError("COULD NOT LOAD \"Dynmap\" PLUGIN!");
            }
        }
    }

}

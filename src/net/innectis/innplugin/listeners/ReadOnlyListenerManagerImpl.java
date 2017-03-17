package net.innectis.innplugin.listeners;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.bukkit.BukkitPlayerActionListener;
import net.innectis.innplugin.listeners.bukkit.IdpChatListener;
import net.innectis.innplugin.listeners.bukkit.ReadOnlyBlockListener;
import net.innectis.innplugin.listeners.bukkit.ReadOnlyEntityListener;
import net.innectis.innplugin.listeners.bukkit.ReadOnlyPlayerListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * @author Hret
 *
 * The read only version if the listener manager
 */
public final class ReadOnlyListenerManagerImpl implements IListenerManager {

    private Listener readOnlyPlayerListener;
    private Listener readOnlyEntityListener;
    private Listener readOnlyBlockListener;

    @Override
    public void registerMainListeners(InnPlugin plugin) {
        readOnlyPlayerListener = new ReadOnlyPlayerListener(plugin);
        readOnlyEntityListener = new ReadOnlyEntityListener();
        readOnlyBlockListener = new ReadOnlyBlockListener();

        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(this.readOnlyPlayerListener, plugin);
        pm.registerEvents(this.readOnlyEntityListener, plugin);
        pm.registerEvents(this.readOnlyBlockListener, plugin);
    }

    @Override
    public boolean hasListeners(InnEventType eventtype) {
        return false;
    }

    @Override
    public UUID registerSecListener(ISecondairyListener listener) {
        return UUID.randomUUID(); // Just return a random ID >.<
    }

    @Override
    public List<UUID> getListenerList() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void removeSecListener(UUID id) {
        // Do nothing
    }

    @Override
    public ISecondairyListener getListener(UUID id) {
        return null;
    }

    @Override
    public void fireEvent(InnEvent event) {
    }

    @Override
    public IdpChatListener getChatListener() {
        return null;
    }

    @Override
    public BukkitPlayerActionListener getPlayerActionListener() {
        return null;
    }

}

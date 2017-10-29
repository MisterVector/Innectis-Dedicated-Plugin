package net.innectis.innplugin.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.bukkit.IdpBlockListener;
import net.innectis.innplugin.listeners.bukkit.BukkitEnchantmentListener;
import net.innectis.innplugin.listeners.bukkit.BukkitEntityListener;
import net.innectis.innplugin.listeners.bukkit.BukkitHangingListener;
import net.innectis.innplugin.listeners.bukkit.BukkitInventoryListener;
import net.innectis.innplugin.listeners.bukkit.BukkitPlayerActionListener;
import net.innectis.innplugin.listeners.bukkit.BukkitPlayerInteractionListener;
import net.innectis.innplugin.listeners.bukkit.BukkitServerListener;
import net.innectis.innplugin.listeners.bukkit.BukkitVehicleListener;
import net.innectis.innplugin.listeners.bukkit.BukkitWeatherListener;
import net.innectis.innplugin.listeners.bukkit.IdpChatListener;
import org.bukkit.plugin.PluginManager;

/**
 * @author Hret
 *
 * The implementation fo the IListenerManager.
 *
 * This manager will handle the listeners that are used in the IDP.
 * It allows for the registering of additional (secondairy) listeners.
 *
 * TODO: Do we need to make this thread safe for the chat event?
 */
public final class ListenerManagerImpl implements IListenerManager {

    /** Field that gets used to priorize the fireing of events */
    private static final InnEventPriority[] piorityorder = new InnEventPriority[]{InnEventPriority.INTERMEDIATE, InnEventPriority.HIGH, InnEventPriority.NORMAL, InnEventPriority.LOW};
    private boolean bukkitsRegistered = false;
    private BukkitPlayerInteractionListener playerInterActionListener;
    private BukkitPlayerActionListener playerActionListener;
    private IdpBlockListener blockListener;
    private BukkitEntityListener entityListener;
    private BukkitVehicleListener vehicleListener;
    private BukkitWeatherListener weatherListener;
    private BukkitServerListener serverListener;
    private BukkitEnchantmentListener enchantmentListener;
    private BukkitInventoryListener inventoryListener;
    private BukkitHangingListener hangingListener;
    // List of all secondairy listners (in the wrapper objects)
    private List<ListenerWrapper> wrappers;

    public ListenerManagerImpl() {
        wrappers = new CopyOnWriteArrayList<ListenerWrapper>();
    }

    @Override
    public void registerMainListeners(InnPlugin plugin) {
        if (!bukkitsRegistered) {
            playerInterActionListener = new BukkitPlayerInteractionListener(plugin);
            playerActionListener = new BukkitPlayerActionListener(plugin);
            blockListener = new IdpBlockListener(plugin);
            entityListener = new BukkitEntityListener(plugin);
            vehicleListener = new BukkitVehicleListener(plugin);
            weatherListener = new BukkitWeatherListener(plugin);
            serverListener = new BukkitServerListener(plugin);
            enchantmentListener = new BukkitEnchantmentListener(plugin);
            inventoryListener = new BukkitInventoryListener(plugin);
            hangingListener = new BukkitHangingListener(plugin);

            PluginManager pm = plugin.getServer().getPluginManager();

            pm.registerEvents(this.playerInterActionListener, plugin);
            pm.registerEvents(this.playerActionListener, plugin);
            pm.registerEvents(this.entityListener, plugin);
            pm.registerEvents(this.blockListener, plugin);
            pm.registerEvents(this.vehicleListener, plugin);
            pm.registerEvents(this.weatherListener, plugin);
            pm.registerEvents(this.serverListener, plugin);
            pm.registerEvents(this.enchantmentListener, plugin);
            pm.registerEvents(this.inventoryListener, plugin);
            pm.registerEvents(this.hangingListener, plugin);

            bukkitsRegistered = true;
        } else {
            throw new RuntimeException("Registering main listeners twice!");
        }
    }

    /**
     * This will return the IDP chat listener
     * @return
     */
    @Override
    public IdpChatListener getChatListener() {
        return playerActionListener.idpChatListener;
    }

    /**
     * This returns the player action listener
     * @return
     */
    @Override
    public BukkitPlayerActionListener getPlayerActionListener() {
        return playerActionListener;
    }

    /**
     * Checks if the manager has any secondairy listeners.
     * @return
     */
    private boolean hasSecondairyListners() {
        return !wrappers.isEmpty();
    }

    @Override
    public boolean hasListeners(InnEventType eventtype) {
        // First check for the events.
        if (hasSecondairyListners()) {
            // Check the wrappers
            for (ListenerWrapper wrap : wrappers) {
                // If it got a priority, it got the event.
                if (wrap.getEventPriority(eventtype) != InnEventPriority.NONE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This will register the given listener to the listener index and return the ID given.
     * The method will not detect any listners that are added twice!
     * @param listener
     * @return
     */
    @Override
    public UUID registerSecListener(ISecondairyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("You cannot register a null object.");
        }
        ListenerWrapper wrapp = new ListenerWrapper(listener);
        wrappers.add(wrapp);
        return wrapp.getId();
    }

    /**
     * This will return a list of all active listener's ID.
     * @return
     */
    @Override
    public List<UUID> getListenerList() {
        List<UUID> list = new ArrayList<UUID>(wrappers.size());

        for (ListenerWrapper wrapper : wrappers) {
            list.add(wrapper.getId());
        }

        return list;
    }

    /**
     * This method will try to remove the listener with the given ID.
     * @param id
     */
    @Override
    public void removeSecListener(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null!");
        }

        for (int i = 0; i < wrappers.size(); i++) {
            if (wrappers.get(i).getId().equals(id)) {
                wrappers.remove(i);
            }
        }
//        for (Iterator<ListenerWrapper> it = wrappers.iterator(); it.hasNext();) {
//            ListenerWrapper wrap = it.next();
//            if (wrap.getId().equals(id)) {
//                it.remove();
//                break;
//            }
//        }
    }

    /**
     * Send back the listener that belongs to the given ID
     * @param id
     * @return
     */
    @Override
    public ISecondairyListener getListener(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null!");
        }
        for (Iterator<ListenerWrapper> it = wrappers.iterator(); it.hasNext();) {
            ListenerWrapper wrap = it.next();
            if (wrap.getId().equals(id)) {
                return wrap.getListener();
            }
        }
        return null;
    }

    /**
     * This will fire the event to the corresponding listeners.
     * <p/>
     * Note: this event is NOT thread-safe!
     * @param event
     */
    @Override
    public void fireEvent(InnEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be NULL!");
        }

        int callsLeft;
        InnEventPriority wrapPriority;
        for (InnEventPriority priority : piorityorder) {

            callsLeft = 0;
            for (ListenerWrapper wrap : wrappers) {
                wrapPriority = wrap.getEventPriority(event.getType());

                // Check if matching priority
                if (wrapPriority == priority) {
                    try {
                        wrap.fireEvent(event);
                    } catch (Exception ex) {
                        InnPlugin.logError("Exception for event " + event.getType().name() + " in " + wrap.getListener().getName(), ex.getCause());
                    }

                    // Do not continue the events if we need to terminate!
                    if (event.shouldTerminate()) {
                        return;

                        // If lower priority, keep track of it.
                    }                } else if (wrapPriority != InnEventPriority.NONE && wrapPriority.getOrderid() < priority.getOrderid()) {
                    callsLeft++;
                }
            }

            // No need to keep checking if no calls are left.
            if (callsLeft == 0) {
                break;
            }
        }
    }

}

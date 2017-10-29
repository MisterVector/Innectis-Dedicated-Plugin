package net.innectis.innplugin;

import java.util.Date;
import net.innectis.innplugin.player.chat.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Hret
 */
public class ReadOnlyThread extends Thread {

    /** The plugin reference*/
    private final InnPlugin plugin;

    public ReadOnlyThread(InnPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Run the thread
     */
    @Override
    public void run() {
        Date date;
        long now = 0, readOnlyMsg = 0;
        while (true) {
            try {
                date = new Date();
                now = date.getTime();

                if (now - readOnlyMsg > 120000) { //2 min
                    readOnlyMsg = now;
                    synchronized (plugin.getServer().getOnlinePlayers()) {
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            p.sendMessage(ChatColor.RED + "IDP was not able to start correctly!");
                            p.sendMessage(ChatColor.RED + "The server is read-only. Please tell an admin!");
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Starts a new readonly loop thread
     * @param plugin
     */
    public static void start(InnPlugin plugin) {
        final Thread readOnlyMainLoop = new ReadOnlyThread(plugin);
        readOnlyMainLoop.setDaemon(true);
        readOnlyMainLoop.setPriority(Thread.NORM_PRIORITY);
        readOnlyMainLoop.start();
    }
    
}

package net.innectis.innplugin.tasks.async;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 * @author Hret
 *
 * This task will register a synced task that will ping back this async check.
 * If certain conditions are met, the task will mark the server as unresponsive and reboot.
 * <p/>
 * Note that this will try to do a force-close on the server by killing the process!
 */
public class ServerCrashChecker extends RepeatingTask {

    private static final int CRASH_CHECK_SUBDELAY = 1000;
    private static final int CRASH_CHECK_DELAY = 5000;
    private static final int MAX_PING_TIME = 5000;
    private static final int MIN_PING_COUNT = 2;
    private static final int MAX_SKIPS_FORCED_RESTART = 20;
    //
    private final InnPlugin plugin;
    private long lastPing = 0;
    private int pingCount = 0;
    private int missedPings = 0;
    private boolean suspended = false;

    public ServerCrashChecker(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, CRASH_CHECK_DELAY);

        this.plugin = plugin;
        // Add the subtask!
        plugin.getTaskManager().addTask(new ServiceCrashCheck_SubTask());
    }

    /**
     * Sets whether the server crash check is suspended
     * @param suspended
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;

        // Reset last ping and ping count if resuming
        // the crash checker
        if (!suspended) {
            lastPing = 0;
            pingCount = 0;
        }
    }

    @Override
    public String getName() {
        return "Server crash checker - Main worker";
    }

    @Override
    @SuppressWarnings("Deprecation")
    public void run() {
        // Don't run if the checker is suspended
        if (suspended) {
            return;
        }

        if (lastPing > 0) {
            if (lastPing + MAX_PING_TIME < System.currentTimeMillis() || pingCount < MIN_PING_COUNT) {
                missedPings++;

                // Notify console and online-admins
                plugin.broadCastStaffMessage("PING is late!... (" + missedPings + "/" + MAX_SKIPS_FORCED_RESTART + ")", true);
                plugin.logInfo("PING is late!... (" + missedPings + "/" + MAX_SKIPS_FORCED_RESTART + ")");

                // Check if we should force-close
                if (missedPings == MAX_SKIPS_FORCED_RESTART) {
                    plugin.broadCastMessage(ChatColor.LIGHT_PURPLE, "*** Are we stuck? Let's restart! ***");

                    // Kill the server
                    forceKillServer();
                }
            } else {
                missedPings = 0;
            }
            pingCount = 0;
        }
    }

    /**
     * Do a ping.
     */
    private void ping() {
        lastPing = System.currentTimeMillis();
        pingCount++;
    }

    /**
     * This method contains a horrible hacky way to get the current PID of the JVM.
     * However, there is no clean way to do this.
     * @return the PID or 0 if it could not be resolved!
     */
    @Deprecated
    private static int getPID() {
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            Field jvmField = runtimeMXBean.getClass().getDeclaredField("jvm");
            jvmField.setAccessible(true);

//            VMManagement vmManagement = (VMManagement) jvmField.get(runtimeMXBean);
//            Method getProcessIdMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
//            getProcessIdMethod.setAccessible(true);
//            Integer processId = (Integer) getProcessIdMethod.invoke(vmManagement);

            Method getProcessIdMethod = jvmField.get(runtimeMXBean).getClass().getDeclaredMethod("getProcessId");
            getProcessIdMethod.setAccessible(true);
            Integer processId = (Integer) getProcessIdMethod.invoke(jvmField.get(runtimeMXBean));

            return processId;
        } catch (Exception ex) {
            Logger.getLogger(ServerCrashChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Deprecated
    @SuppressWarnings("Deprecation")
    private void forceKillServer() {
        try {
            int pid = getPID();
            // If the PID could not be resolved
            if (pid == 0) {
                System.out.println("Could not resolve PID, kill normal route...");
                System.exit(201105012);
            }

            // Check on which OS the server is running.
            String operatingSystem = System.getProperty("os.name").toLowerCase();
            if (operatingSystem.contains("win")) {
                // Use taskkill on windows
                System.out.println("Force killing PID: " + pid + " using 'taskkill'");
                Runtime.getRuntime().exec("taskkill /pid " + pid + " /f");
            } else {
                // Assume Linux, just kill
                System.out.println("Force killing PID: " + pid + " using 'kill'");
                Runtime.getRuntime().exec("kill -9 " + pid);
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerCrashChecker.class.getName()).log(Level.SEVERE, null, ex);

            // We cannot kill the server...
            // Try normal route.
            System.exit(201105011);
        }
    }

    /**
     * A subthread that runs synchronized with the main thread, that pings back the server checker.
     */
    private class ServiceCrashCheck_SubTask extends RepeatingTask {

        public ServiceCrashCheck_SubTask() {
            super(RunBehaviour.SYNCED, CRASH_CHECK_SUBDELAY);
        }

        @Override
        public String getName() {
            return "Server crash checker - Sub worker";
        }

        @Override
        public void run() {
            //System.out.println("PING!");
            ping();
        }
    }
    
}

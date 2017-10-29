package net.innectis.innplugin.tasks.async;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.innectis.innplugin.system.command.ClassCommand;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.ConfigValueHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.DateUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 *
 * A timer that will restart the server after a preconfigured time.
 */
public class ServerRestartTimer extends RepeatingTask {

    private static final long DEFAULT_RESTART_TIME = 24 * 60 * 60 * 1000; // 24 hours

    /** The times that a notification will be given: 30m 15m 10m 5m 2m 1m 30s 15s 10s 5s 4s 3s 2s 1s 0s */
    private static final long[] ALL_POSSIBLE_DELAYS = {1800, 900, 600, 300, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1, 0};

    // Resworld deletion settings
    private static final String RESOURCEWORLD_DATANAME = "resworld";
    private static final long RESOURCEWORLD_RESET_SECONDS = 3600 * 24;

    private long restartTime = DEFAULT_RESTART_TIME;
    private long ellapsedTime = 0;
    private long tick = 0;
    private int delayIdx = 0;

    // Delay between timer intervals
    // default to 5000 seconds for first notification
    // as we need time for the server to start
    private long delay = 5000;

    private final InnPlugin plugin;
    private boolean done = false;

    public ServerRestartTimer(InnPlugin plugin) {
        super(RunBehaviour.ASYNC, 0);

        this.plugin = plugin;

        // Add the commands
        plugin.commandManager.registerCommand(new RescheduleRestartCommand());
        plugin.commandManager.registerCommand(new RestarttimeCommand());
    }

    @Override
    public String getName() {
        return "Server restart timer";
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public void run() {
        // This may get called after shutdown, so just cancel now
        if (done) {
            return;
        }

        // No more restart time, so let's restart
        if (delayIdx == ALL_POSSIBLE_DELAYS.length) {
            plugin.broadCastMessage(ChatColor.LIGHT_PURPLE, "*** THE SERVER IS RESTARTING NOW ***");

            if (shouldDeleteResourceWorld()) {
                deleteResourceWorld();
            }

            plugin.getServer().shutdown();
            delay = Integer.MAX_VALUE;
            done = true;
        } else {
            // The server is manually shutting down so let's not worry here
            if (InnPlugin.isShuttingDown()) {
                plugin.logInfo("Deleting resource world...");

                // Still delete the resource world if possible
                if (shouldDeleteResourceWorld()) {
                    deleteResourceWorld();
                }

                return;
            }

            long remainMillis = recalculateDelay();
            plugin.broadCastMessage(ChatColor.LIGHT_PURPLE,
                    "*** THE SERVER WILL RESTART IN " + DateUtil.getTimeString(remainMillis, true).toUpperCase() + " ***");
        }
    }

    /**
     * This will mark the given file or directory to be deleted after shutdown.
     * If the given file is a directory, it will recursively go into the given folders.
     * @param curFile
     */
    private static void recursiveFileDeletionMarking(File curFile) {
        curFile.deleteOnExit();
        if (curFile.isDirectory()) {
            for (File f : curFile.listFiles()) {
                recursiveFileDeletionMarking(f);
            }
        }
    }

    /**
     * This will check if resource world should be deleted this restart.
     * If DEBUGMODE is enabled, it will return false.
     * If the world hasn't been reset in over X time, it will return true.
     * @return
     */
    private static boolean shouldDeleteResourceWorld() {
        // Don't reset in debug mode!
        if (Configuration.DEBUGMODE) {
            InnPlugin.logDebug("Skipping deletion of resworld...");
            return false;
        }

        // Get last reset time from database.
        String value = ConfigValueHandler.getValue(RESOURCEWORLD_DATANAME);

        // Hasn't ever been reset? Yes, its due!
        if (value == null) {
            return true;
        }

        try {
            Long lastReset = Long.parseLong(value);
            return (System.currentTimeMillis() > lastReset + RESOURCEWORLD_RESET_SECONDS * 1000);
        } catch (NumberFormatException ex) {
            InnPlugin.logError("Unable to find get last resource zone reset! Resetting anyway!");
            return true;
        }
    }

    /**
     * This will mark the resource world to be deleted on plugin-shutdown.
     * This will then save the last time this world was deleted.
     */
    private static void deleteResourceWorld() {
        // Delete the resworld
        File resworld = new File(RESOURCEWORLD_DATANAME);
        recursiveFileDeletionMarking(resworld);

        InnPlugin.logInfo("Deleted Resource World!");

        PreparedStatement statement = null;

        // Make sure to delete all block logs from reszone
        try {
            statement = DBManager.prepareStatement("DELETE FROM block_log WHERE lower(world) = ?;");
            statement.setString(1, IdpWorldType.RESWORLD.name().toLowerCase());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to delete all block logs from the reszone!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        // Save that resource world was deleted.
        ConfigValueHandler.saveValue(RESOURCEWORLD_DATANAME, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * Recalculates the delay of this restart timer
     * @return the remaining time till server restarts
     */
    private long recalculateDelay() {
        // If this is our first calculation, set the restart time with the
        // current tick so that we can accurately represent the starting time
        // as delay is necessary initially
        if (tick == 0) {
            tick = System.currentTimeMillis();
        }

        long remainMillis = (restartTime - ellapsedTime);
        long curDelayTime = (ALL_POSSIBLE_DELAYS[delayIdx] * 1000);
        long remainMillisToFire = (remainMillis - curDelayTime);
        ellapsedTime += remainMillisToFire;

        delay = remainMillisToFire;
        delayIdx++;

        return remainMillis;
    }

    /**
     * This command allows the user to see when the server is going to restat
     */
    private final class RestarttimeCommand extends ClassCommand {
        private final String[] aliases = new String[]{"restarttime"};

        @Override
        public boolean canUseCommand(IdpCommandSender commandSender) {
            return commandSender.hasPermission(Permission.admin_serverrestart);
        }

        @Override
        public String[] getAliases() {
            return aliases;
        }

        @Override
        public String getDescription() {
            return "Shows the time till the server restarts";
        }

        @Override
        public String getUsage() {
            return "/restarttime";
        }

        @Override
        public void invoke(IdpCommandSender<? extends CommandSender> sender, String commandName, String[] args) {
            long remain = ((restartTime + tick) - System.currentTimeMillis());
            sender.printInfo("Server restarts in " + DateUtil.getTimeString(remain, true).toUpperCase());
        }

        @Override
        public boolean isAlias(String command) {
            return command.equalsIgnoreCase(aliases[0]);
        }
    }

    /**
     * Class that will allow to adjust the restarttime.
     */
    private final class RescheduleRestartCommand extends ClassCommand {
        private final String[] aliases = new String[]{"reschedulerestart"};

        public RescheduleRestartCommand() {
        }

        @Override
        public boolean canUseCommand(IdpCommandSender commandSender) {
            return commandSender.hasPermission(Permission.admin_serverrestart);
        }

        @Override
        public boolean canUseOnWorld(IdpCommandSender commandSender) {
            return true;
        }

        @Override
        public String[] getAliases() {
            return aliases;
        }

        @Override
        public String getDescription() {
            return "Reschedules the server restart";
        }

        @Override
        public String getRankUsage(PlayerGroup group) {
            return null;
        }

        @Override
        public String getUsage() {
            return "/reschedulerestart <time 1d1h1m1s>";
        }

        @Override
        public void invoke(IdpCommandSender sender, String commandName, String[] args) {
            if (args.length == 0) {
                sender.printError("No time given!");
                return;
            }

            // Format the timestring to a long
            long time = DateUtil.getTimeFormula(args[0]);

            // if -1, its invalid
            if (time < 0) {
                sender.printError("Not a valid time: '" + args[0] + "'");
                return;
            }

            // Min restart time of more than 10 seconds
            if (time <= 10500) {
                sender.printError("Give the players some time for the restart!");
                return;
            }

            // Adjust restarttime and set first run
            restartTime = time;
            ellapsedTime = 0;
            tick = 0;

            // Check if the restart time is exact as a time checkpoint
            boolean equalsExact = false;

            // Recalculate the notification index
            for (int i = 0; i < ALL_POSSIBLE_DELAYS.length; i++) {
                long exactMillis = (ALL_POSSIBLE_DELAYS[i] * 1000);

                if (restartTime >= exactMillis) {
                    equalsExact = (restartTime == exactMillis);

                    delayIdx = i;
                    break;
                }
            }

            // Recalc the delay
            long millisRemain = recalculateDelay();

            // Only echo the manual restart message if our delay is not the same as
            // one of the default restart times
            if (!equalsExact) {
                plugin.broadCastMessage(ChatColor.LIGHT_PURPLE,
                        "*** MODIFIED RESTART: THE SERVER WILL RESTART IN " + DateUtil.getTimeString(millisRemain, true).toUpperCase() + " ***");
            }
        }

        @Override
        public boolean isAlias(String command) {
            return command.equalsIgnoreCase(aliases[0]);
        }
    }

}

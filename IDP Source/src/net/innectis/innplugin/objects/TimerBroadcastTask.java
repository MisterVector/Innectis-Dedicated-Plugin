package net.innectis.innplugin.objects;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.StringUtil;

/**
 *
 * @author Hret
 *
 * A task to broadcast a timer or countdown to a player of the server.
 */
public class TimerBroadcastTask extends RepeatingTask {

    private InnPlugin plugin;
    private String messageformat;
    private String endmessage;
    private long targetTime;
    private String targetPlayer;
    private String timertext;
    private long taskId;

    /**
     * Creates a timer that will only target the given player
     * @param timertext
     * The text on the timer (can be null)
     * @param targetTime
     * @param targetplayer
     */
    public TimerBroadcastTask(InnPlugin plugin, String timertext, long targetTime, String targetplayer) {
        super(RunBehaviour.ASYNC, 1000);
        this.targetTime = targetTime;
        this.targetPlayer = targetplayer;
        this.plugin = plugin;
        this.timertext = timertext;

        if (StringUtil.stringIsNullOrEmpty(timertext)) {
            messageformat = "{0} remaining...";
            endmessage = "Time's up!";
        } else {
            messageformat = "{0} remaining till " + timertext;
            endmessage = "It's time for " + timertext;
        }
    }

    /**
     * Creates a timer that will broadcast to all players
     * @param timertext
     * The text on the timer (can be null)
     * @param targetTime
     */
    public TimerBroadcastTask(InnPlugin plugin, String timertext, long targetTime) {
        this(plugin, timertext, targetTime, null);
    }

    /**
     * Gets the target time of this timer
     * @return
     */
    public long getTargetTime() {
        return targetTime;
    }

    /**
     * Returns the title of this timer
     * @return
     */
    public String getTimerText() {
        return timertext;
    }

    /**
     * Sets the task ID of this task
     * @param taskId
     */
    public void setId(long taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the task ID of this task
     * @return
     */
    public long getId() {
        return taskId;
    }

    public void run() {
        long time = targetTime - System.currentTimeMillis();

        // Check if timer has expired.
        if (time < 0) {
            plugin.getTaskManager().removeTask(this);
            plugin.removeTimer(taskId);
            return;
        }

        // Check if it should broadcast something..
        if (!shouldBroadcast(time)) {
            return;
        }

        // Construct the message
        String message;
        if (time < 1000) {
            message = endmessage;
        } else {
            message = StringUtil.format(messageformat, DateUtil.getTimeString(time, true));
        }

        // Check if it needs to be send to a player or broadcasted.
        if (targetPlayer == null) {
            plugin.broadCastMessage(ChatColor.AQUA, message);
        } else {
            IdpPlayer player = plugin.getPlayer(targetPlayer);
            if (player != null) {
                player.print(ChatColor.AQUA, message);
            }
        }
    }

    /**
     * Checks if the timer should broadcast an time update
     * @param time
     * @return
     */
    private boolean shouldBroadcast(long time) {
        time /= 1000;

        // Every halfhour
        if (time % 1800 == 0) {
            return true;
        }

        // Only below 30 min
        if (time < 1800) {

            // Every 10 min
            if (time % 600 == 0) {
                return true;
            }

            // At 15 min.
            if (time == 900) {
                return true;
            }

            // Every min below 5 min mark.
            if (time <= 300 && time % 60 == 0) {
                return true;
            }

            // At 30, 10, 5 and lower seconds.
            if (time <= 5 || time == 30 || time == 10) {
                return true;
            }

        }

        return false;
    }

    public String getName() {
        return getClass().getName();
    }

}

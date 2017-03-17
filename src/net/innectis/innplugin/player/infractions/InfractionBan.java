package net.innectis.innplugin.player.infractions;

import java.util.Date;
import net.innectis.innplugin.system.bans.BanHandler;
import net.innectis.innplugin.system.bans.Ban;
import net.innectis.innplugin.system.bans.UserBan;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.util.DatabaseTools;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.StringUtil;

/**
 *
 * @author Hret
 *
 * Small obvject with info about ban that can result from receiving many infractions
 */
class InfractionBan {

    private static final String BAN_BROAD_MESSAGE = "Player {0} has been banned for {1} for poor behaviour!";
    private static final String BAN_KICK_MESSAGE = "You have been banned for {0} for inappropriate behaviour!";
    private final int intensityLevel;
    private final long bantimeSec;

    public InfractionBan(int intensityLevel, long bantimeSec) {
        this.intensityLevel = intensityLevel;
        this.bantimeSec = bantimeSec;
    }

    /**
     * The intensitylevel on which this ban needs to be triggered.
     * @return
     */
    public int getIntensityLevel() {
        return intensityLevel;
    }

    /**
     * The bantime that results from the ban in seconds.
     * @return
     */
    public long getBantimeSec() {
        return bantimeSec;
    }

    /**
     * This will apply this current ban on the player with the given infraction.
     * @param infraction the infraction that caused the ban
     */
    public void banPlayer(Infraction infraction) {
        Ban obj = BanHandler.getBan(infraction.getPlayerCredentials().getUniqueId());
        // Only ban if not already banned!
        if (obj == null) {
            InnPlugin plugin = InnPlugin.getPlugin();

            // Create the ban
            obj = new UserBan(infraction.getPlayerCredentials(), Configuration.SERVER_GENERATED_CREDENTIALS, DatabaseTools.dateToTimeStamp(new Date()), bantimeSec * 1000, true);
            BanHandler.addBan(obj);

            // Broadcast the ban
            plugin.broadCastMessage(ChatColor.RED, StringUtil.format(BAN_BROAD_MESSAGE, infraction.getPlayerCredentials().getName(), DateUtil.getTimeDifferenceString(bantimeSec, DateUtil.DEFAULT_CONSTANTS)));

            // Find the player and kick if online
            IdpPlayer player = plugin.getPlayer(infraction.getPlayerCredentials().getUniqueId());
            if (player != null && player.isOnline()) {
                player.getHandle().kickPlayer(StringUtil.format(BAN_KICK_MESSAGE, DateUtil.getTimeDifferenceString(bantimeSec, DateUtil.DEFAULT_CONSTANTS)));
            }
        }
    }
    
}

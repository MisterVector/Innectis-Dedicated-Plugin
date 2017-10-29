package net.innectis.innplugin.loggers;

import net.innectis.innplugin.player.IdpPlayer;

/**
 * A logger that logs all death drops from players
 *
 * @author AlphaBlend
 */
public class DeathDropLogger extends IdpFileLogger implements Logger {

    public DeathDropLogger(String logfolder) {
        super(logfolder, "deathdroplogger", "yyyy-MM-dd HH:mm:ss");
    }

    public void log(IdpPlayer player, String deathItems) {
        super.log(player.getName() + " dropped: " + deathItems);
    }

}

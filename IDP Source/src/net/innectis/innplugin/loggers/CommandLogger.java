package net.innectis.innplugin.loggers;

import net.innectis.innplugin.util.StringUtil;

/**
 *
 * @author Hret
 */
public class CommandLogger extends IdpFileLogger implements Logger {

    /**
     * Constructs a new debuglogger
     * @param logfolder
     */
    CommandLogger(String logfolder) {
        super(logfolder, "commandlog", "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * Logs a command usage
     * @param playername
     * @param command
     * @param args
     * @param allowed
     */
    public void logCommand(String playername, String command, String[] args, Boolean allowed){
        log(playername + (allowed ? ", used, " : ", was denied access to, " ) + command + " " + StringUtil.joinString(args, " "));
    }

}

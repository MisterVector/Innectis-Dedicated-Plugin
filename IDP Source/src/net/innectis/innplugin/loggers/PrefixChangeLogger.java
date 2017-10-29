package net.innectis.innplugin.loggers;

/**
 *
 * @author AlphaBlend
 */
public class PrefixChangeLogger extends IdpFileLogger implements Logger {

    /**
     * Constructs a new debuglogger
     * @param logfolder
     */
    public PrefixChangeLogger(String logfolder) {
        super(logfolder, "prefixchangelogger", "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * Logs a command usage
     * @param playername
     * @param newname
     */
    public void logNameChange(String playername, String newname){
        log(playername + ", changed prefix to, " + newname );
    }

}

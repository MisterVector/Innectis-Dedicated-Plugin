package net.innectis.innplugin.loggers;

/**
 * Logger for strange error messages
 *
 * @author AlphaBlend
 */
public class StrangeErrorLogger extends IdpFileLogger implements Logger {

    public StrangeErrorLogger(String logFolder) {
        super(logFolder, "strangeerror", "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void log(String msg) {
        super.log(msg);
    }

}

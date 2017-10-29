package net.innectis.innplugin.loggers;

/**
 * Logs mail messages
 *
 * @author AlphaBlend
 */
public class MailLogger extends IdpFileLogger implements Logger {

    /**
     * Construct the mail logger
     * @param logFile
     */
    MailLogger(String logFile) {
        super(logFile, "MailSendLog", "yyyy-MM-dd HH:mm:ss");
    }

    public void log(String from, String to, String title, String content) {
        super.log("From " + from + " to " + to + " (Title: " + title + "): " + content);
    }

}

package net.innectis.innplugin.loggers;

/**
 * A logger that tracks when the /sendmoney command is used
 *
 * @author AlphaBlend
 */
public class SendMoneyLogger extends IdpFileLogger implements Logger {

    public SendMoneyLogger(String logfolder) {
        super(logfolder, "sendmoneylog", "YYYY-MM HH:mm:ss");
    }

    public void log(String fromPlayer, String toPlayer, int amount) {
        super.log(fromPlayer + " has sent " + amount + " valuta" + (amount != 1 ? "s" : "") + " to " + toPlayer + ".");
    }

}

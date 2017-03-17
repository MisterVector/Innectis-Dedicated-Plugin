package net.innectis.innplugin.loggers;

import net.innectis.innplugin.Configuration;

/**
 * An enum specifying logger types with their log objects
 *
 * @author AlphaBlend
 */
public enum LogType {

    /**
     * A logger for blocks
     */
    BLOCK(new BlockLogger()),

    /**
     * A logger for chat messages
     */
    CHAT(new ChatLogger(Configuration.PATH_DATAFOLDER + "chatlog/")),

    /**
     * A logger for commands
     */
    COMMAND(new CommandLogger(Configuration.PATH_DATAFOLDER + "command/")),

    /**
     * A logger for death drops
     */
    DEATH_DROPS(new DeathDropLogger(Configuration.PATH_DATAFOLDER + "DeathDropLogger/")),

    /**
     * A logger for mail messages
     */
    MAIL(new MailLogger(Configuration.PATH_DATAFOLDER + "mailSendLog/")),

    /**
     * A logger for prefix changes
     */
    PREFIX_CHANGE(new PrefixChangeLogger(Configuration.PATH_DATAFOLDER + "PrefixChangeLog/")),

    /**
     * A logger for sending money
     */
    SEND_MONEY(new SendMoneyLogger(Configuration.PATH_DATAFOLDER + "SendMoneyLogger/")),

    /**
     * A logger for shop transactions
     */
    SHOP_TRANSACTION(new ShopTransactionLogger(Configuration.PATH_DATAFOLDER + "ShopTransactionLog/")),

    /**
     * A logger for strange errors
     */
    STRANGE_ERRORS(new StrangeErrorLogger(Configuration.PATH_DATAFOLDER + "StrangeErrors/"));

    private final Logger logger;

    private LogType(Logger logger) {
        this.logger = logger;
    }

    /**
     * Gets the logger from this type
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets a logger by its type
     * @param type
     * @return
     */
    public static Logger getLoggerFromType(LogType type) {
        for (LogType lt : values()) {
            if (type == lt) {
                return lt.getLogger();
            }
        }

        return null;
    }

}

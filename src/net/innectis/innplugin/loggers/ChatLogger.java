package net.innectis.innplugin.loggers;


/**
 *
 * @author Hret
 *
 * The chatlogs should use a format so it can be exported to excel or a database.
 * The format is now:  Type - Username - Target - Message
 */
public class ChatLogger extends IdpFileLogger implements Logger {

    /**
     * Constructs a new debuglogger
     * @param logfolder
     */
    ChatLogger(String logfolder) {
        super(logfolder, "chatlog", "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Logs a chat message
     * @param username
     * @param group
     * @param message
     */
    public void logChat(String username, String message) {
        log("Global", username, "", message);
    }

    /**
     * Logs a chat message
     * @param username
     * @param group
     * @param message
     */
    public void logLocalChat(String username, String message) {
        log("Local", username, "x", message);
    }

    /**
     * Logs a private chat message
     * @param username
     * @param group
     * @param message
     */
    public void logPrivateChat(String username, String tousername, String message) {
        log("PM", username, tousername, message);
    }

    /**
     * Logs a chat message
     * @param username
     * @param group
     * @param message
     */
    public void logChatboxChat(String chatboxname, String username, String message) {
        log("Chatbox", username, chatboxname, message);
    }

    /**
     * Logs a filtered message
     * @param username
     * @param message
     */
    public void logFilteredMessage(String username, String message) {
        logFilteredMessage(username, "", message);
    }

    public void logFilteredMessage(String username, String toUsername, String message) {
        log("Filtered", username, toUsername, message);
    }

    public void logLotMessage(String username, String message) {
        log("LotChat", username, "", message);
    }

    private static final String delimiter = " - ";

    private void log(String type, String sender, String target, String message) {
        super.log(type + delimiter + sender + delimiter +  target + delimiter + message);
    }

}
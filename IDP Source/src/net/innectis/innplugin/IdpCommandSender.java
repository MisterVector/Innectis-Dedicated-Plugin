package net.innectis.innplugin;

import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 */
public abstract class IdpCommandSender<T extends CommandSender> {

    /** The innplugin class */
    protected InnPlugin plugin;
    protected T handle;

    public IdpCommandSender(InnPlugin plugin, T sender) {
        this.handle = sender;
        this.plugin = plugin;
    }

    /**
     * Returns the plugin instance
     * @return
     */
    public InnPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the bukkit handler of this object
     */
    public T getHandle() {
        return handle;
    }

    /**
     * Checks if the commandsender is a player or not.
     * @return true if it is a player
     */
    public boolean isPlayer() {
        return getType() == CommandSenderType.PLAYER;
    }

    /**
     * The type of the sender of the command.
     * @return
     */
    public abstract CommandSenderType getType();

    /**
     * Returns the name
     * @return name
     */
    public abstract String getName();

    /**
     * Returns the coloured name
     * @return coloured name
     */
    public abstract String getColoredName();

    /**
     * Checks if the player got the given permission
     * @param perm
     * @return true if they got the given permission
     */
    public abstract boolean hasPermission(Permission perm);

    /**
     * Sends a message to the player
     * This method does not add the messageprefix!
     * @param message
     */
    public abstract void printRaw(String message);

    /**
     * Sends a message to the player
     * @param message
     */
    public void print(ChatColor colour, String... message) {
        String full = "";
        for (String str : message) {
            full += colour.toString() + str;
        }
        printRaw(colour + Configuration.MESSAGE_PREFIX + full);
    }

    /**
     * Sends an information message to the player
     * @param message
     */
    public void printInfo(String... message) {
        print(ChatColor.DARK_GREEN, message);
    }

    /**
     * Sends an information message to the player
     * @param message
     * @param objects
     * <p/>
     * The objects that need to be inserted into the string. <br/>
     * You can use the index of them in the array to give the location. <br/>
     * {0} will be replaced by the first object. {1} by the second and so on. <br/>
     */
    public void printInfoFormat(String format, Object... objects) {
        print(ChatColor.DARK_GREEN, StringUtil.format(format, objects));
    }

    /**
     * Sends an error message to the player
     * @param message
     */
    public void printError(String... message) {
        print(ChatColor.RED, message);
    }

    /**
     * Sends an error message to the player
     * @param message
     * @param objects
     * <p/>
     * The objects that need to be inserted into the string. <br/>
     * You can use the index of them in the array to give the location. <br/>
     * {0} will be replaced by the first object. {1} by the second and so on. <br/>
     */
    public void printErrorFormat(String format, Object... objects) {
        print(ChatColor.RED, StringUtil.format(format, objects));
    }

    /**
     * The type of the sender that has send the command.
     */
    public enum CommandSenderType {

        PLAYER,
        CONSOLE,
        BLOCK
    }

}

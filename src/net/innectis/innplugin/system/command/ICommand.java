package net.innectis.innplugin.system.command;

import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.PlayerGroup;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 *
 * Interface for commands in the IDP
 */
public interface ICommand {

    /**
     * Checks if the sender has permission to use the command.
     * @param commandSender
     * @return True if the sender has permission <br/>
     * This also checks if the sender is the console, and if the console can use it
     */
    boolean canUseCommand(IdpCommandSender commandSender);

    /**
     * Checks if the command can be used on the world the sender is on
     * @param The sender of the command
     * @return True: if the sender can use the command on the world its on.<br/>
     * Or if the sender got the override permission for it.
     */
    boolean canUseOnWorld(IdpCommandSender commandSender);

    /**
     * This aliases of the command
     */
    String[] getAliases();

    /**
     * Gives the description of the command
     * @return The command description
     */
    String getDescription();

    /**
     * Gives the ranked usage string if there is any, otherwise it will return null.
     * @return the description or null
     */
    String getRankUsage(PlayerGroup group);

    /**
     * Gets the usage string
     * @return the format to use the command
     */
    String getUsage();

    /**
     * Invoke the command.
     * This method will supply the command with the parameters it needs and then invoke it.
     * If the command fails or the command is not used properly its handled here.
     * @param commandSender - The sender of the argument
     * @param args - The arguments
     */
    @SuppressWarnings(value = "deprecation")
    void invoke(IdpCommandSender<? extends CommandSender> commandSender, String commandName, String[] args);

    /**
     * Checks if this command is triggerd by the given command.
     * @param command
     * @return
     */
    boolean isAlias(String command);

    /**
     * Checks if the command is disabled
     * @return True if disabled
     */
    boolean isDisabled();

    /**
     * Checks if the command is hidden
     * @return True if the command should not given info if sender was not allowed to use itx
     */
    boolean isHiddenCommand();

    /**
     * Checks if the command is allowed to be used before the player is logged in.
     * @return true if the command can be used before the player loggs in
     */
    boolean isPreLoginCommand();

    /**
     * Checks if the arguments of this command should not be logged.
     * @return
     */
    public boolean isObfusticatedLogged();

}

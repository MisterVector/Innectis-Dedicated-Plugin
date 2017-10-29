package net.innectis.innplugin.system.command;

import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.PlayerGroup;

/**
 *
 * @author Hret
 *
 * Simple adapter class for a command class.
 * The command itself contains the basic return values for some commands.
 */
public abstract class ClassCommand implements ICommand {

    protected ClassCommand() {
    }

    /**
     * Checks if this command can be used on the given world.
     * @param commandSender
     * @return
     */
    @Override
    public boolean canUseOnWorld(IdpCommandSender commandSender) {
        return true;
    }

    /**
     * Return a special usage depending on the group of the player requesting it.
     * @param group
     * @return
     */
    @Override
    public String getRankUsage(PlayerGroup group) {
        return getUsage();
    }

    /**
     * Checks if the command is enabled or not.
     * @return
     */
    @Override
    public boolean isDisabled() {
        return false;
    }

    /**
     * Checks if the command should not be shown to players.
     * @return
     */
    @Override
    public boolean isHiddenCommand() {
        return false;
    }

    /**
     * Checks if the login is a login that can be used before the player logs in.
     * @return
     */
    @Override
    public boolean isPreLoginCommand() {
        return false;
    }

    /**
     * Checks if the arguments of this command should not be logged.
     * @return
     */
    @Override
    public boolean isObfusticatedLogged() {
        return false;
    }

}

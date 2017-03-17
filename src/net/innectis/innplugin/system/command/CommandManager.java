package net.innectis.innplugin.system.command;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.loggers.CommandLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 *
 * The manager class with all commands that are supported in the IDP
 */
public final class CommandManager {

    private List<ICommand> commands;
    //
    private List<String> registeredcommands;
    private List<String> disabledCommands;

    public CommandManager() {
    }

    /**
     * Initializes the commandmanager.
     * This will allow classes to register their commands in the manager.
     */
    public void initialize() {
        commands = new ArrayList<ICommand>();
        registeredcommands = new ArrayList<String>();

        // Load the disabled commands.
        try {
            disabledCommands = FileHandler.getData(Configuration.FILE_DISABLEDCOMMANDS);
        } catch (IOException ex) {
            disabledCommands = new ArrayList<String>();
        }
    }

    /**
     * Registers the commands and classes in the commandmanger
     */
    public void registerCommandClass(Class clzz) {
        // Register the methods in this class.
        for (Method method : clzz.getMethods()) {
            try {
                registerICommand(new MethodCommand(method, disabledCommands));
            } catch (NotACommandException nace) {
                // Ignore
            }
        }

        if (InnPlugin.isDebugEnabled()) {
            InnPlugin.logDebug("Methods of class " + clzz.getSimpleName() + " are registered!");
        }
    }

    /**
     * Registers the commands and classes in the commandmanger
     */
    public void registerCommand(ClassCommand command) {
        if (command.getAliases().length > 0) {
            registerICommand(command);

            if (InnPlugin.isDebugEnabled()) {
                InnPlugin.logDebug("Command " + command.getAliases()[0] + " is registered!");
            }
        }
    }

    /**
     * This will register the given command.
     * If the alias is already registered an error will be printed in the log.
     * @param cmd
     */
    private void registerICommand(ICommand cmd) {
        for (String localalias : cmd.getAliases()) {
            // Double command check
            if (registeredcommands.contains(localalias)) {
                InnPlugin.logError("CommandAlias '" + localalias + "' is registered twice!");
                continue;
            }
            registeredcommands.add(localalias);
        }

        commands.add(cmd);
    }

    /**
     * Tries to find the command and invokes it.
     * @param parent
     * @param sender
     * @param args
     * @param commandString
     * @return true if the command is handled
     */
    public boolean invokeCommand(InnPlugin parent, IdpCommandSender sender, String commandString, String[] args) {
        commandString = commandString.toLowerCase(); //all commands are in lowercase

        // Read only check
        if (parent.isReadonly()) {
            sender.printError("All commands are disabled. Server is read-only!");
            return true;
        }

        ICommand command = null;
        for (ICommand cont : commands) {
            if (cont.isAlias(commandString)) {
                command = cont;
                break;
            }
        }

        // Check if the command is found (so index >= 0)
        if (command == null) {
            //logCommandNotFound(sender, commandString, args);
            return checkExternalCommand(parent, sender, commandString, args);
        }

        // Check if player is logged in and can use that command
        if (sender.isPlayer()) {
            if (!((IdpPlayer) sender).getSession().isLoggedIn()) {
                // Check if its a prelogin command
                if (!command.isPreLoginCommand()) {
                    // check if player is logged in
                    sender.printError("You are not logged in!");
                    return true;
                }
            }
        }


        // Check disabled
        if (command.isDisabled()) {
            sender.printError("This command is disabled!");
            logCommandDenied(sender, "disabled", commandString, args);
            return true;
        }

        // Check permissions
        if (!command.canUseCommand(sender)) {
            // Message for the player
            if (sender.isPlayer()) {
                // When command is hidden, act like its not known
                if (command.isHiddenCommand()) {
                    logCommandDenied(sender, "no permission (hidden)", commandString, command.isObfusticatedLogged() ? getObfusticatedArguments(args.length) : args);
                    sender.printRaw("Unknown command. Type \"help\" for help.");
                } else {
                    logCommandDenied(sender, "no permission", commandString, command.isObfusticatedLogged() ? getObfusticatedArguments(args.length) : args);
                    sender.printError("You do not have access to that command.");
                }
            } else {
                sender.printError("This command cannot be used on the console!");
                return true;
            }
            return true;
        }

        // Check for world access
        if (!command.canUseOnWorld(sender)) {
            sender.printError("An unknown force is preventing you from using this command.");
            logCommandDenied(sender, "not allowed on world", commandString, command.isObfusticatedLogged() ? getObfusticatedArguments(args.length) : args);
            return true;
        }

        logCommandAllowed(sender, commandString, command.isObfusticatedLogged() ? getObfusticatedArguments(args.length) : args);
        command.invoke(sender, commandString, args);
        return true;
    }

    /**
     * Checks if the sender got access to the given command if not will return an error msg
     * @param sender
     * @param commandString
     * @param args
     * @return
     */
    private boolean checkExternalCommand(InnPlugin parent, IdpCommandSender sender, String commandString, String[] args) {
        // Check if they can access external commands. The other plugins should handle the reset
        if (sender.hasPermission(Permission.special_external_commands)) {
            logCommandExternal(sender, commandString, args);
            return false;
        } else {
            if (commandString.startsWith("/")) {
                return invokeCommand(parent, sender, commandString.substring(1), args);
            } else {
                // Add override permission? (For SADMIN)
                logCommandNotFound(sender, commandString, args);
                sender.printRaw("Unknown command. Type \"help\" for help.");
                suggestCommand(sender, commandString);
                return true;
            }
        }
    }

    /**
     * Returns a suggestion of completed commands, when the player hits the tab key
     * @param sender
     * @param msg
     * @return
     */
    public List<String> onTabComplete(IdpPlayer player, String msg) {
        List<String> completions = new ArrayList<String>();
        String[] splt = msg.split(" ");

        if (splt.length == 1) {
            for (ICommand cmd : commands) {
                // Only add commands the user can use
                if (cmd.canUseCommand(player) && cmd.canUseOnWorld(player)) {
                    for (String alias : cmd.getAliases()) {
                        // If the message input is less than or equal to the alias, and they
                        // are equal (case insensitive) then add to suggested commands
                        if (alias.startsWith(msg)) {
                            completions.add("/" + alias);
                        }
                    }
                }
            }

            // If no completions found, add an informational message
            if (completions.isEmpty()) {
                player.printRaw("No command suggestions available.");
            }
        }

        return completions;
    }

    /**
     * Looks for a command that is close to the used command and suggests it to the sender.
     * <p/>
     * Commands that are shorter then 2 or longer then 30 characters will be ignored.
     * <p/>
     * This method will check for access to the given command
     * @param sender
     * @param usedcommand
     */
    private void suggestCommand(IdpCommandSender sender, String usedcommand) {
        // Min length to check is between 2 and 30 long.
        if (usedcommand.length() < 2 || usedcommand.length() > 30) {
            return;
        }

        // Distance higher then 10 can be ignored
        int closestmatch = 10;
        String match = null;

        for (ICommand cmd : commands) {
            // Only check those the user can use.
            if (cmd.canUseCommand(sender) && cmd.canUseOnWorld(sender)) {

                // Loop through aliases
                for (String alias : cmd.getAliases()) {
                    // First char must be the same
                    if (alias.charAt(0) == usedcommand.charAt(0)) {

                        // Check distance
                        int currdistance = StringUtil.getLevenshteinDistance(usedcommand, alias);
                        if (currdistance < closestmatch) {
                            closestmatch = currdistance;
                            match = alias;
                        }
                    }
                }
            }
        }

        if (match != null) {
            sender.print(ChatColor.WHITE, "Did you mean to type " + ChatColor.EFFECT_ITALIC + match + ChatColor.EFFECT_CLEAR + "?");
        }
    }

    private void logCommandExternal(IdpCommandSender sender, String command, String[] args) {
        if (sender.isPlayer()) {
            CommandLogger commandLogger = (CommandLogger) LogType.getLoggerFromType(LogType.COMMAND);
            commandLogger.logCommand(sender.getName(), command, args, true);
            InnPlugin.logInfo(sender.getColoredName() + ChatColor.WHITE + " used the external command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
            ((IdpPlayer) sender).getSession().spectatorMessage(ChatColor.DARK_GREEN, sender.getColoredName() + ChatColor.WHITE + " used the external command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
        }
    }

    private void logCommandAllowed(IdpCommandSender sender, String command, String[] args) {
        CommandLogger commandLogger = (CommandLogger) LogType.getLoggerFromType(LogType.COMMAND);
        commandLogger.logCommand(sender.getName(), command, args, true);

        if (sender.isPlayer()) {
            InnPlugin.logInfo(sender.getColoredName() + ChatColor.WHITE + " used the command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
            ((IdpPlayer) sender).getSession().spectatorMessage(ChatColor.DARK_GREEN, sender.getColoredName() + ChatColor.WHITE + " used the command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
        }
    }

    private void logCommandDenied(IdpCommandSender sender, String reason, String command, String[] args) {
        if (sender.isPlayer()) {
            CommandLogger commandLogger = (CommandLogger) LogType.getLoggerFromType(LogType.COMMAND);
            commandLogger.logCommand(sender.getName(), command, args, false);
            InnPlugin.logInfo(sender.getColoredName() + ChatColor.WHITE + " was denied access to command (" + reason + ") " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
            ((IdpPlayer) sender).getSession().spectatorMessage(ChatColor.DARK_GREEN, sender.getColoredName() + ChatColor.RED + " was denied access to command (" + reason + "): " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
        }
    }

    private void logCommandNotFound(IdpCommandSender sender, String command, String[] args) {
        if (sender.isPlayer()) {
            CommandLogger commandLogger = (CommandLogger) LogType.getLoggerFromType(LogType.COMMAND);
            commandLogger.logCommand(sender.getColoredName(), command, args, false);
            InnPlugin.logInfo(sender.getColoredName() + ChatColor.WHITE + " entered the unknown command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
            ((IdpPlayer) sender).getSession().spectatorMessage(ChatColor.DARK_GREEN, sender.getColoredName() + ChatColor.YELLOW + " entered an unknown command: " + ChatColor.GREEN + command + " " + StringUtil.joinString(args, " "));
        }
    }

    /**
     * This will contruct an array of obfusticated strings to replace arguments
     * @param argssize
     * @return
     */
    private String[] getObfusticatedArguments(int argssize) {
        String[] args = new String[argssize];
        for (int i = 0; i < argssize; i++) {
            args[i] = "****";
        }
        return args;
    }

    /**
     * Returns the command method, or null if the command is not found
     * @param command
     * @return the commandcontainer, or null if not found.
     */
    public ICommand getCommand(String command) {
        for (ICommand cont : commands) {
            if (cont.isAlias(command)) {
                return cont;
            }
        }

        return null;
    }

    /**
     * Returns a list of all commands that can be used by the sender.
     *
     * @param sender
     * @return
     */
    public List<ICommand> getCommands(IdpCommandSender<? extends CommandSender> sender) {
        List<ICommand> commandList = new ArrayList<ICommand>(commands);
        for (Iterator<ICommand> iterator = commandList.iterator(); iterator.hasNext();) {
            ICommand cmd = iterator.next();
            if (!cmd.canUseCommand(sender)) {
                iterator.remove();
            }
        }
        return commandList;
    }

}

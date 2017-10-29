package net.innectis.innplugin.system.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.external.MissingDependencyException;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 *
 * Object to hold data for a command.
 */
final class MethodCommand implements ICommand {

    private Method method;
    private ParameterType[] methodParametersTypes;
    private String[] aliases;
    private CommandMethod annotation;
    private boolean isDisabled;
    private boolean printUsageOnFail;

    private MethodCommand() {
    }

    public MethodCommand(Method method, List<String> usedCommands, List<String> disabledCommands) throws NotACommandException {
        this(method, disabledCommands);
    }

    public MethodCommand(Method method, List<String> disabledCommands) throws NotACommandException {
        CommandMethod info = method.getAnnotation(CommandMethod.class);
        if (info == null) {
            throw new NotACommandException("This method is not a command");
        }

        this.method = method;
        this.annotation = info;
        // If the returntype is void then no need to print
        this.printUsageOnFail = !method.getReturnType().equals(Void.TYPE);

        this.aliases = info.aliases();
        this.isDisabled = false;

        // Check the aliasses if its disabled or already used
        for (String commandAlias : info.aliases()) {
            // If an alias is found the command will be disabled
            if (disabledCommands.contains(commandAlias)) {
                this.isDisabled = true;
            }
        }

        // Get parameterTypes
        ArrayList<ParameterType> list = new ArrayList<ParameterType>();
        for (Class clazz : method.getParameterTypes()) {
            list.add(ParameterType.findType(clazz));
        }
        this.methodParametersTypes = list.toArray(new ParameterType[list.size()]);
    }

    /**
     * Checks if the command can be used on the world the sender is on
     * @param The sender of the command
     * @return True: if the sender can use the command on the world its on.<br/>
     * Or if the sender got the override permission for it.
     */
    @Override
    public boolean canUseOnWorld(IdpCommandSender commandSender) {
        // Check for the permission
        if (commandSender.hasPermission(Permission.world_command_override)) {
            return true;
        }

        // Check the world the player is on
        if (commandSender.isPlayer()) {
            IdpWorld world = ((IdpPlayer) commandSender).getWorld();
            IdpWorldType worldType = world.getActingWorldType();
            for (IdpWorldType type : annotation.disabledWorlds()) {
                if (worldType == type) {
                    return false;
                }
            }

            // Check if Dynamic world has commands disabled
            if (world.getWorldType() == IdpWorldType.DYNAMIC) {
                if (!((IdpDynamicWorldSettings) world.getSettings()).hasCommandsAllowed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gives the description of the command
     * @return The command description
     */
    @Override
    public String getDescription() {
        return annotation.description();
    }

    /**
     * Gives the ranked usage string if there is any, otherwise it will return null.
     * @return the description or null
     */
    @Override
    public String getRankUsage(PlayerGroup group) {
        String usage = null;
        switch (group) {
            case SADMIN:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_SAdmin();
        }
            case ADMIN:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_Admin();
        }
            case MODERATOR:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_Mod();
        }
            case GOLDY:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_Goldy();
        }
            case SUPER_VIP:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_Super_VIP();
        }
            case VIP:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_VIP();
        }
            case USER:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_User();
        }
            case GUEST:
                if (StringUtil.stringIsNullOrEmpty(usage)) {
                    usage = annotation.usage_Guest();
        }
        }

        // Make sure it returns null when the string is empty!
        if (StringUtil.stringIsNullOrEmpty(usage)) {
            return null;
        }

        return usage;

    }

    /**
     * This aliases of the command
     */
    @Override
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets the usage string
     * @return the format to use the command
     */
    @Override
    public String getUsage() {
        return annotation.usage();
    }

    /**
     * Checks if the command is disabled
     * @return True if disabled
     */
    @Override
    public boolean isDisabled() {
        return isDisabled;
    }

    /**
     * Checks if the command is hidden
     * @return True if the command should not given info if sender was not allowed to use itx
     */
    @Override
    public boolean isHiddenCommand() {
        return annotation.hiddenCommand();
    }

    /**
     * Checks if the command is allowed to be used before the player is logged in.
     * @return true if the command can be used before the player loggs in
     */
    @Override
    public boolean isPreLoginCommand() {
        return annotation.preLoginCommand();
    }

    /**
     * Checks if the arguments of this command should not be logged.
     * @return
     */
    @Override
    public boolean isObfusticatedLogged() {
        return annotation.obfusticateLogging();
    }

    /**
     * Checks if the sender has permission to use the command.
     * @param commandSender
     * @return True if the sender has permission <br/>
     * This also checks if the sender is the console, and if the console can use it
     */
    @Override
    public boolean canUseCommand(IdpCommandSender commandSender) {
        return commandSender.hasPermission(annotation.permission())
                && (commandSender.isPlayer() || annotation.serverCommand());
    }

    /**
     * Checks if this command is triggerd by the given command.
     * @param command
     * @return
     */
    @Override
    public boolean isAlias(String command) {
        for (String alias : aliases) {
            if (command.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invoke the command.
     * This method will supply the command with the parameters it needs and then invoke it.
     * If the command fails or the command is not used properly its handled here.
     * @param commandSender - The sender of the argument
     * @param args - The arguments
     */
    @SuppressWarnings("deprecation")
    @Override
    public void invoke(IdpCommandSender<? extends CommandSender> commandSender, String commandName, String[] args) {
        try {
            // Fill the parameters
            Object[] params = new Object[methodParametersTypes.length];

            for (int i = 0; i < params.length; i++) {
                switch (methodParametersTypes[i]) {
                    case CommandName:
                        params[i] = new CommandName(commandName);
                        break;
                    case BukkitServerObj:
                        params[i] = InnPlugin.getPlugin().getServer();
                        break;
                    case IdpCommandSenderObj:
                        params[i] = commandSender;
                        break;
                    case IdpPlayerObj:
                        params[i] = (IdpPlayer) commandSender;
                        break;
                    case InnPluginObj:
                        params[i] = InnPlugin.getPlugin();
                        break;
                    case NormalArgs:
                        params[i] = args;
                        break;
                    case SmartArgs:
                        params[i] = new SmartArguments(args);
                        break;
                    case ParameterArgs:
                        params[i] = new ParameterArguments(args);
                        break;
                    case CommandArgs:
                        params[i] = new LynxyArguments(args);
                        break;
                    default:
                        params[i] = null;
                        InnPlugin.logError("CommandMethod '" + method.getName() + "' has an unknown parameter type! (parameter nr. " + i + ")");
                        break;
                }
            }

            // Invoke the command
            if (printUsageOnFail) {
                if (!((Boolean) method.invoke(this, params))) {
                    String usage = null;

                    // If the sender is a player check for a rank usage string
                    if (commandSender.isPlayer()) {
                        usage = getRankUsage(((IdpPlayer) commandSender).getGroup());
                    }

                    // If no player or server, load up the normal usage
                    if (StringUtil.stringIsNullOrEmpty(usage)) {
                        usage = getUsage();
                    }

                    // Send it to the player
                    commandSender.printInfo("Usage: " + usage);
                }
            } else {
                method.invoke(this, params);

                // Catch if a command is missing a dependency
            }
        } catch (MissingDependencyException ex) {
            String name = method.getName().toLowerCase();
            if (name.startsWith("command")) {
                name = name.substring("command".length());
            }

            InnPlugin.logError("Command is missing dependency! (" + commandSender.getName() + "): /"
                    + name + " " + (isObfusticatedLogged() ? "***" : StringUtil.joinString(args, " ")));

            commandSender.printError("Sorry, I can't do that right now... Please notify an admin!");

            // Default handling of (uncaught) errors
        } catch (Exception ex) {
            String name = method.getName().toLowerCase();
            if (name.startsWith("command")) {
                name = name.substring("command".length());
            }

            InnPlugin.logError("Error executing (" + commandSender.getName() + "): /"
                    + name + " " + (isObfusticatedLogged() ? "***" : StringUtil.joinString(args, " ")), ex.getCause());

            //InnPlugin.sendErrorReport(this.getClass(), "Error executing command " + method.getName(), ex);
            // Print error when failed!
            commandSender.printError("Internal server error. Please notify an admin!");
        }
    }

    /**
     * Enum to keep track of the parameters of a method
     */
    @Nullable
    @SuppressWarnings("deprecation")
    private enum ParameterType {

        CommandName(CommandName.class),
        BukkitServerObj(org.bukkit.Server.class),
        InnPluginObj(InnPlugin.class),
        IdpCommandSenderObj(IdpCommandSender.class),
        IdpPlayerObj(IdpPlayer.class),
        NormalArgs(String.class),
        SmartArgs(SmartArguments.class),
        ParameterArgs(ParameterArguments.class),
        CommandArgs(LynxyArguments.class);
        private final Class clazz;

        private ParameterType(Class clazz) {
            this.clazz = clazz;
        }

        /**
         * Find the parameterType
         * @param clazz
         * @return
         */
        public static ParameterType findType(Class clazz) {
            for (ParameterType type : values()) {
                if (type.clazz == clazz) {
                    return type;
                    // Check if its an array of that type
                } else if (clazz.isArray() && clazz.getComponentType() == type.clazz) {
                    return type;
                }
            }
            return null;
        }
    }
}

class NotACommandException extends IdpException {

    /**
     * Creates a new instance of
     * <code>NotACommandException</code> without detail message.
     */
    public NotACommandException() {
    }

    /**
     * Constructs an instance of
     * <code>NotACommandException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NotACommandException(String msg) {
        super(msg);
    }

}

package net.innectis.innplugin;

import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.Permission;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Hret
 *
 * This is the IDP handler for the ColouredConsoleHandler or Console
 */
public class IdpConsole extends IdpCommandSender<ConsoleCommandSender> {

    /**
     * Constructs a new object
     * @param console
     * @param server
     */
    public IdpConsole(InnPlugin server, ConsoleCommandSender console) {
        super(server, console);
    }

    @Override
    public String getName() {
        return "[SERVER]";
    }

    @Override
    public CommandSenderType getType() {
        return CommandSenderType.CONSOLE;
    }

    @Override
    public String getColoredName() {
        return ChatColor.LIGHT_PURPLE + "[SERVER]";
    }

    @Override
    public boolean hasPermission(Permission perm) {
        // Server always permission
        return true;
    }

    @Override
    public void printRaw(String message) {
        getHandle().sendMessage(message);
    }

}

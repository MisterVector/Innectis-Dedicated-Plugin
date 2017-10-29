
package net.innectis.innplugin;

import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.Permission;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;

/**
 *
 * @author Hret
 *
 * Class for the sender of commandblock commands
 */
public class IdpCommandBlockSender extends IdpCommandSender<BlockCommandSender> {

    public IdpCommandBlockSender(InnPlugin plugin, BlockCommandSender sender) {
        super(plugin, sender);
    }

    @Override
    public String getName() {
        return "[COMMANDBLOCK]";
    }

    @Override
    public CommandSenderType getType() {
        return CommandSenderType.BLOCK;
    }

    @Override
    public String getColoredName() {
        return ChatColor.DARK_GRAY + getName();
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return false;
    }

    @Override
    public void printRaw(String message) {
        // Cant..
    }

    /**
     * The block of this commandblock
     * @return
     */
    public Block getBlock() {
        return handle.getBlock();
    }

}

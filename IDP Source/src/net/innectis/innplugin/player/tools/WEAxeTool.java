package net.innectis.innplugin.player.tools;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 *
 * @author Hret
 */
public class WEAxeTool extends Tool {

    public WEAxeTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        super(player, materialInHand, block);
    }

    public static IdpMaterial getItem() {
        return IdpMaterial.WOOD_AXE;
    }

    @Override
    public boolean isAllowed() {
        return materialInHand == getItem() && player.hasPermission(Permission.tinywe_wand);
    }

    @Override
    public boolean onLeftClickBlock() {
        Location loc = block.getLocation();
        if (BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_PLACED, true)
                || player.hasPermission(Permission.tinywe_selection_setpointsanywhere)) {
            player.setRegionLoc1(loc.toVector());

            IdpWorldRegion region = player.getRegion();

            int tempX = region.getWidth();
            int tempY = region.getHeight();
            int tempZ = region.getLength();

            player.print(ChatColor.LIGHT_PURPLE, "1st position: [" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "] (" + player.getRegion().getArea() + ") Lot: " + tempX + "x" + tempY + "x" + tempZ);
        } else {
            player.printError("TinyWE axe cannot be used to set blocks here.");
        }
        return true;
    }

    @Override
    public boolean onRightClickBlock() {
        Location loc = block.getLocation();
        if (BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_PLACED, true)
                || player.hasPermission(Permission.tinywe_selection_setpointsanywhere)) {
            player.setRegionLoc2(loc.toVector());

            IdpWorldRegion region = player.getRegion();

            int tempX = region.getWidth();
            int tempY = region.getHeight();
            int tempZ = region.getLength();

            player.print(ChatColor.LIGHT_PURPLE, "2nd position: [" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "] (" + player.getRegion().getArea() + ") Lot: " + tempX + "x" + tempY + "x" + tempZ);
        } else {
            player.printError("TinyWE axe cannot be used to set blocks here.");
        }
        return true;
    }

    @Override
    public boolean onLeftClickAir() {
        return false;
    }

    @Override
    public boolean onRightClickAir() {
        return false;
    }

}

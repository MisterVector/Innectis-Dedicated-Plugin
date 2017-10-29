package net.innectis.innplugin.player.tools;

import java.util.HashSet;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.Permission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Hret
 */
public class TeleportTool extends Tool {

    public TeleportTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        super(player, materialInHand, block);
    }

    public static IdpMaterial getItem() {
        return IdpMaterial.WATCH;
    }

    @Override
    public boolean isAllowed() {
        if (materialInHand != getItem()) {
            return false;
        }

        if (!player.hasPermission(Permission.items_clock_teleport)) {
            return false;
        }

        return canTeleportToLocation(player.getLocation());
    }

    @Override
    public boolean onLeftClickBlock() {
        return onLeftClickAir();
    }

    @Override
    public boolean onRightClickBlock() {
        return onRightClickAir();
    }

    @Override
    public boolean onLeftClickAir() {

        // Prevent double firing that occurs when breaking block.
        if (System.currentTimeMillis() - player.getSession().getLastTeleportTime() < 50) {
            return true;
        }

        HashSet<Material> losBlocks = new HashSet<Material>();

        for (IdpMaterial mat : InnPlugin.getPlugin().getAllBlocksLoS()) {
            losBlocks.add(mat.getBukkitMaterial());
        }

        for (Block block : player.getHandle().getLineOfSight(losBlocks, 200)) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat.isSolid()) {
                for (int i = block.getY() + 1; i < block.getWorld().getMaxHeight(); i++) {
                    block = block.getRelative(BlockFace.UP);
                    mat = IdpMaterial.fromBlock(block);
                    IdpMaterial materialUp = IdpMaterial.fromBlock(block.getRelative(BlockFace.UP));

                    if (!mat.isSolid() && !materialUp.isSolid()) {
                        World world = block.getWorld();
                        Location loc = block.getLocation();

                        if (canTeleportToLocation(loc)) {
                            int x = loc.getBlockX();
                            int y = loc.getBlockY();
                            int z = loc.getBlockZ();

                            player.teleport(new Location(world, x, y, z, player.getYaw(), player.getPitch()), TeleportType.IGNORE_RESTRICTION);
                        } else {
                            player.printError("Cannot teleport to that location!");
                        }

                        return true;
                    }
                }
            }
        }

        player.printError("No block in sight!");
        return true;
    }

    @Override
    public boolean onRightClickAir() {

        // Prevent double firing that occurs when breaking block.
        if (System.currentTimeMillis() - player.getSession().getLastTeleportTime() < 50) {
            return true;
        }

        boolean hasFoundSolid = false;

        HashSet<Material> losBlocks = new HashSet<Material>();

        for (IdpMaterial mat : InnPlugin.getPlugin().getAllBlocksLoS()) {
            losBlocks.add(mat.getBukkitMaterial());
        }

        for (Block block : player.getHandle().getLineOfSight(losBlocks, 200)) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (!hasFoundSolid && mat.isSolid()) {
                hasFoundSolid = true;
            } else if (hasFoundSolid && !mat.isSolid()
                    && !IdpMaterial.fromBlock(block.getRelative(BlockFace.UP)).isSolid()) {
                World world = block.getWorld();
                Location loc = block.getLocation();

                if (canTeleportToLocation(loc)) {
                    int x = loc.getBlockX();
                    int y = loc.getBlockY();
                    int z = loc.getBlockZ();

                    player.teleport(new Location(world, x, y, z, player.getYaw(), player.getPitch()), TeleportType.IGNORE_RESTRICTION);
                } else {
                    player.printError("Cannot teleport to that location!");
                }

                return true;
            }

            // Don't go any further, this will drop players from world height.
            if (block.getY() == 0) {
                break;
            }
        }

        player.printError("No block to travel through!");
        return true;
    }

    private boolean canTeleportToLocation(Location loc) {
        // Always allow if player has global permissions
        if (player.hasPermission(Permission.world_command_override)) {
            return true;
        }

        InnectisLot lot = LotHandler.getLot(loc);
        IdpWorldType worldType = player.getWorld().getActingWorldType();

        if (lot != null) {
            // Always allow teleport to location if the player has global teleport permissions
            if (player.hasPermission(Permission.items_clock_global_teleport)) {
                return true;
            }

            // Allow use of the teleport clock if the lot has the PixelBuild flag on it
            if (lot.isFlagSet(LotFlagType.PIXELBUILD)) {
                return true;
            }

            // Allow use of the teleport clock on lots that players can manage and
            // have the necessary permission
            if (lot.canPlayerManage(player.getName())
                && player.hasPermission(Permission.items_clock_allow_managed_lots)) {
                return true;
            }

            // Always allow use in the aether and creative world if the player has access to the lot
            if (worldType == IdpWorldType.AETHER || worldType == IdpWorldType.CREATIVEWORLD) {
                return lot.canPlayerAccess(player.getName());
            }

            return false;
        } else {
            switch (player.getWorld().getActingWorldType()) {
                case AETHER:
                case CREATIVEWORLD:
                    return true;
                case NETHER:
                    return player.hasPermission(Permission.world_command_override);
                default:
                    return player.hasPermission(Permission.items_clock_global_teleport);
              }
        }
    }

}
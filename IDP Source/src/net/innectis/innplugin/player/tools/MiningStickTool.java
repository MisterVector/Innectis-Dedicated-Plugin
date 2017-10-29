package net.innectis.innplugin.player.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.player.tools.miningstick.MiningStickSettings;
import net.innectis.innplugin.system.game.IdpGameManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A tool that instantly breaks blocks, optionally yielding drops
 * with the STICK_DROP player setting
 *
 * @author AlphaBlend
 */
public class MiningStickTool extends Tool {

    public MiningStickTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        super(player, materialInHand, block);
    }

    public static IdpMaterial getItem() {
        return IdpMaterial.STICK;
    }

    public boolean isAllowed() {
        // Don't allow players who cannot use the mining stick
        if (!player.hasPermission(Permission.special_builder_miningstick)) {
            return false;
        }

        // Don't allow the miner's stick in The End
        if (player.getWorld().getActingWorldType() == IdpWorldType.THE_END) {
            return false;
        }

        // Can't use mining stick while in a game
        if (IdpGameManager.getInstance().isInGame(player)) {
            return false;
        }

        return true;
    }

    public boolean onLeftClickBlock() {
        MiningStickData miningStickSettings = player.getSession().getMiningStickData();;
        List<Block> mineableBlocks = getMiningStickArea(player, block, miningStickSettings);

        if (mineableBlocks.size() > 0) {
            for (Block b : mineableBlocks) {
                IdpMaterial mat = IdpMaterial.fromFilteredBlock(b);

                if (!miningStickSettings.hasSetting(MiningStickSettings.BLOCK_DROPS) || !mat.canDrop()
                        || BlockHandler.getIdpBlockData(b.getLocation()).isVirtualBlock()) {
                    BlockHandler.setBlock(b, IdpMaterial.AIR);
                } else {
                    b.breakNaturally();
                }

                // Log the insta-break
                BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
                blockLogger.logBlockAction(player.getUniqueId(), mat, b.getLocation(), BlockLogger.BlockAction.BLOCK_BREAK);
            }
            // TODO: This method seems to be called twice in some cases, so we ought to figure that out
        }

        return true;
    }

    public boolean onRightClickBlock() {
        changeStickSize(player.getSession());
        return true;
    }

    public boolean onLeftClickAir() {
        return false;
    }

    public boolean onRightClickAir() {
        changeStickSize(player.getSession());
        return true;
    }

    private void changeStickSize(PlayerSession session) {
        MiningStickData miningStick = session.getMiningStickData();
        int miningStickSize = miningStick.getSize();

        if (miningStickSize == 2) {
            miningStickSize = 1;
        } else {
            miningStickSize++;
        }

        miningStick.setSize(miningStickSize);
        miningStick.update();

        player.printInfo("Mining stick size set to: " + miningStickSize);

    }

    private List<Block> getMiningStickArea(IdpPlayer player, Block block, MiningStickData miningStick) {
        List<Block> affectedBlocks = new ArrayList<Block>();
        World world = block.getWorld();
        int ox = block.getX();
        int oy = block.getY();
        int oz = block.getZ();
        IdpMaterial mainMaterial = null;
        int miningStickSize = miningStick.getSize();

        if (miningStickSize > 1) {
            int range = (miningStickSize - 1);

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Block testBlock = world.getBlockAt(ox + x, oy + y, oz + z);
                        affectedBlocks.add(testBlock);

                        if (x == 0 && y == 0 && z == 0 && miningStick.hasSetting(MiningStickSettings.BREAK_ONLY_SAME_BLOCK)) {
                            mainMaterial = IdpMaterial.fromFilteredBlock(block);
                        }
                    }
                }
            }
        } else {
            affectedBlocks.add(block);
        }

        for (Iterator<Block> it = affectedBlocks.iterator(); it.hasNext();) {
            Block testBlock = it.next();
            Location loc = testBlock.getLocation();
            boolean remove = false;

            if (BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_DESTROY, false)) {
                IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

                if (blockData.isUnbreakable()) {
                    remove = true;
                } else {
                    IdpMaterial mat = IdpMaterial.fromFilteredBlock(testBlock);

                    // Don't include this block if it wasn't part of the
                    // main group of blocks to be broken
                    if (mainMaterial != null && mat != mainMaterial) {
                        remove = true;
                    } else {
                        // These materials cannot be insta-mined
                        switch (mat) {
                            case AIR:
                            case BEDROCK:
                            case WATER:
                            case STATIONARY_WATER:
                            case LAVA:
                            case STATIONARY_LAVA:
                            case BARRIER:
                            case PORTAL:
                            case END_PORTAL:
                            case END_GATEWAY:
                            case END_PORTAL_FRAME:
                                remove = true;
                        }
                    }
                }
            } else {
                remove = true;
            }

            if (remove) {
                it.remove();
            }
        }

        return affectedBlocks;
    }

}

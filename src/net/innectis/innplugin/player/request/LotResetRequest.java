package net.innectis.innplugin.player.request;

import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpVector2D;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.RegionEditTask;
import org.bukkit.block.Block;

/**
 *
 * @author Hret
 *
 * This is a request to remove the lot of a player.
 */
public class LotResetRequest extends Request {

    private InnectisLot lot;
    private int groundHeight;

    public LotResetRequest(InnPlugin plugin, IdpPlayer lotOwner, IdpPlayer staffIssuer, InnectisLot lot, int groundHeight) {
        super(plugin, lotOwner, staffIssuer, System.currentTimeMillis(), Configuration.LOT_REMOVAL_REQUEST_TIMEOUT);
        this.lot = lot;
        this.groundHeight = groundHeight;
    }

    @Override
    public void onReject() {
        IdpPlayer locRequester = getRequester();
        if (locRequester != null) {
            locRequester.printInfo(getPlayer().getColoredName(), " has denied the lot reset!");
        }
    }

    @Override
    public void onTimeout() {
        IdpPlayer locRequester = getRequester();
        if (getPlayer() != null) {
            if (locRequester == null) {
                getPlayer().printInfo("The lot reset request has timed out.");
            } else {
                getPlayer().printInfo("The lot reset request from ", locRequester.getColoredName(), " has timed out.");
            }
        }
        if (locRequester != null) {
            locRequester.printInfo("Your lot reset request has timed out.");
        }
    }

    @Override
    public void onAccept() {
        IdpPlayer staffMember = getRequester();
        IdpPlayer lotOwner = getPlayer();

        if (staffMember == null && lotOwner != null) {
            lotOwner.printError("Player not found, the staff member must be online!");
            return;
        } else if (lotOwner == null && staffMember != null) {
            staffMember.printError("Player not found, lot owner must be online!");
            return;
        } else if (staffMember == null || lotOwner == null) {
            return;
        }

        String[] players;
        if (staffMember == lotOwner) {
            players = new String[]{staffMember.getName()};
        } else {
            players = new String[]{staffMember.getName(), lotOwner.getName()};
        }

        RegionEditTask task = new RegionEditTask(lot, players) {
            int chunkPasses = 0;
            int lastProgress = 0;

            @Override
            public void taskStopped(String reason) {
                this.reportPlayers("The task was stopped for the following reason: " + reason);
            }

            @Override
            public void taskComplete() {
                // Make sure unused chunks are all reclaimed
                ChunkDatamanager.reclaimUnusedChunks();

                this.reportPlayers("Task complete!");
            }

            @Override
            public void taskIncrement(int progress) {
                if (progress != lastProgress) {
                    lastProgress = progress;
                    reportPlayers("Region regeneration " + progress + "% done!");
                }
            }

            final int groundlvl = groundHeight;

            @Override
            public void handleChunk(IdpVector2D chunkLocation) {
                IdpWorldRegion chunkRegion = getChunkRegion(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
                BlockCounter cntr = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);

                IdpRegion bedrock = new IdpRegion(chunkRegion.getMinimumPoint().setY(0), chunkRegion.getMaximumPoint().setY(0));
                IdpRegion belowGround = new IdpRegion(chunkRegion.getMinimumPoint().setY(1), chunkRegion.getMaximumPoint().setY(groundlvl - 1));
                IdpRegion ground = new IdpRegion(chunkRegion.getMinimumPoint().setY(groundlvl), chunkRegion.getMaximumPoint().setY(groundlvl));
                IdpRegion aboveGround = new IdpRegion(chunkRegion.getMinimumPoint().setY(groundlvl + 1), chunkRegion.getMaximumPoint().setY(255));

                for (Block blk : cntr.getBlockList(bedrock, chunkRegion.getWorld(), null)) {
                    IdpMaterial mat = IdpMaterial.fromBlock(blk);

                    if (mat != IdpMaterial.BEDROCK) {
                        BlockHandler.setBlock(blk, IdpMaterial.BEDROCK, false);
                    }

                    IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                    if (blockData.hasData()) {
                        blockData.clear();
                    }
                }

                for (Block blk : cntr.getBlockList(belowGround, chunkRegion.getWorld(), null)) {
                    IdpMaterial mat = IdpMaterial.fromBlock(blk);

                    if (mat != IdpMaterial.STONE) {
                        BlockHandler.setBlock(blk, IdpMaterial.STONE, false);
                    }

                    IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                    if (blockData.hasData()) {
                        blockData.clear();
                    }
                }

                for (Block blk : cntr.getBlockList(ground, chunkRegion.getWorld(), null)) {
                    IdpMaterial mat = IdpMaterial.fromBlock(blk);

                    if (mat != IdpMaterial.GRASS) {
                        BlockHandler.setBlock(blk, IdpMaterial.GRASS, false);
                    }

                    IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                    if (blockData.hasData()) {
                        blockData.clear();
                    }
                }

                for (Block blk : cntr.getBlockList(aboveGround, chunkRegion.getWorld(), null)) {
                    IdpMaterial mat = IdpMaterial.fromBlock(blk);

                    if (mat != IdpMaterial.AIR) {
                        // Only apply physics around the outer wall of the lot
                        boolean applyPhysics = (blk.getX() == lot.getLowestX() || blk.getX() == lot.getHighestX()
                                || blk.getZ() == lot.getLowestZ() || blk.getZ() == lot.getHighestZ());

                        BlockHandler.setBlock(blk, IdpMaterial.AIR, applyPhysics);
                    }

                    IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                    if (blockData.hasData()) {
                        blockData.clear();
                    }
                }

                chunkPasses++;

                // Reclaim chunks after so many passes
                if (chunkPasses % 75 == 0) {
                    // Remove the chunk data recently created by this chunk
                    ChunkDatamanager.reclaimUnusedChunks();
                }
            }
        };
        task.setLastExecution(0l);
        InnPlugin.getPlugin().getTaskManager().addTask(task);

        // Log
        InnPlugin.logInfo(lotOwner.getColoredDisplayName(), " has confirmed to allow ", staffMember.getColoredDisplayName(),
                " to reset lot #" + lot.getId() + "!");

        lotOwner.printInfo("You confirmed the request from " + staffMember.getColoredDisplayName(), " to reset lot #" + lot.getId() + "!");

        // Print password to staffmember
        staffMember.printInfo(lotOwner.getColoredName(), " has confirmed the lot reset request!");
        staffMember.printInfo("Stay close to the lot while it regenerates.");
    }

    @Override
    public String getDescription() {
        // String from /accept "You have accepted the "
        return "Lot reset request for lot #" + lot.getId();
    }

}

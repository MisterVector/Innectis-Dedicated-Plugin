package net.innectis.innplugin.player.tinywe;

import javax.annotation.Nullable;
import net.innectis.innplugin.external.api.interfaces.IWorldEditIDP;
import net.innectis.innplugin.external.LibraryType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpBiome;
import net.innectis.innplugin.location.IdpVector2D;
import net.innectis.innplugin.location.IdpWorldRegion;

/**
 *
 * @author Hret
 *
 * Abstract task to regenerate a region.
 * Currently the biome cannot be changed. Waiting for bukkit support on this..
 */
public abstract class RegionRegenerationTask extends RegionEditTask {

    @Nullable
    protected IdpBiome biome;
    private int lastProgress = -1;

    public RegionRegenerationTask(IdpWorldRegion region, String[] players) {
        super(region, players);
        this.biome = null;
    }

    /**
     * This will regenerate the chunk or part of the chunk.
     * @param chunkLocation
     */
    @Override
    public void handleChunk(IdpVector2D chunkLocation) {
        IdpWorldRegion chunkRegion;
        // Get a Idp region for the chunk only
        chunkRegion = getChunkRegion(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
        // Check if anything needs to be done before it regenerates
        beforeChunkRegen(chunkLocation, chunkRegion);
        // Regen the chunk or a part of it.
        if (chunkInsideRegion(chunkLocation.getBlockX(), chunkLocation.getBlockZ())) {
            // Change the biome before regenerating
            if (biome != null) {
                region.getWorld().setBiome(chunkLocation.getBlockX(), chunkLocation.getBlockZ(), biome.getBukkitBiome());
            }
            // regen whole chunk
            region.getWorld().regenerateChunk(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
        } else {
            IWorldEditIDP worldEdit = (IWorldEditIDP) InnPlugin.getPlugin().getExternalLibraryManager().getAPIObject(LibraryType.WORLDEDIT);
            // regen partial chunk
            worldEdit.regenPartially(getHandlingPlayer(), chunkRegion);
        }
        // Refresh the chunk
        region.getWorld().refreshChunk(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
        // Check if anything needs to be done after it has been regenerated
        afterChunkRegen(chunkLocation, chunkRegion);
    }

    /**
     * Make changes to a chunk before it regenerates
     * @param chunkLocation
     * @param chunkRegion
     */
    protected abstract void beforeChunkRegen(IdpVector2D chunkLocation, IdpWorldRegion chunkRegion);

    /**
     * Make changes to a chunk after it regenerates
     * @param chunkLocation
     * @param chunkRegion
     */
    protected abstract void afterChunkRegen(IdpVector2D chunkLocation, IdpWorldRegion chunkRegion);

    /**
     * Reports the players that task is complete
     */
    @Override
    public void taskComplete() {
        reportPlayers("Region regeneration is complete!");
    }

    /**
     * Reports the players that the task stopped on an error.
     * @param reason
     */
    @Override
    public void taskStopped(String reason) {
        reportPlayers("The task was stopped for the following reason: " + reason);
    }

    /**
     * Reports the player the about the task status
     * @param progress
     */
    @Override
    public void taskIncrement(int progress) {
        if (progress != lastProgress) {
            lastProgress = progress;
            reportPlayers("Region regeneration " + progress + "% done!");
        }
    }
    
}

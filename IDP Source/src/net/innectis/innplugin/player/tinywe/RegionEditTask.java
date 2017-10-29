package net.innectis.innplugin.player.tinywe;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpVector2D;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * A task to edit a region per-chunk.
 * This is effective when editing a large region.
 */
public abstract class RegionEditTask extends RepeatingTask {

    private IdpPlayer player;
    protected final List<IdpVector2D> chunks;
    protected final IdpWorldRegion region;
    protected int lastChunk = 0;
    protected String[] players;

    public RegionEditTask(IdpWorldRegion region, String[] players) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.RegenTimeout);
        this.region = region;
        this.chunks = new ArrayList<IdpVector2D>(region.getChunks());
        this.players = players;
    }

    /**
     * Checks if a full chunk is inside the given region
     * @param x
     * @param z
     * @return
     */
    protected boolean chunkInsideRegion(int x, int z) {
        Vector loc1 = new Vector(x * 16, 0, z * 16);
        Vector loc2 = new Vector((x * 16) + 15, 255, (z * 16) + 15);
        return region.contains(loc1) && region.contains(loc2);
    }

    /**
     * Gets the process of regenerating.
     * @return
     */
    protected int getChunkProgress() {
        return (int) ((lastChunk / (double) chunks.size()) * 100);
    }

    /**
     * Returns the chunk region. <br/>
     * This method also makes the region smaller if its partally outside the lotregion.
     * @param x
     * @param z
     * @return and IDPregion of the chunk.
     */
    protected IdpWorldRegion getChunkRegion(int x, int z) {
        Vector loc1 = new Vector(Math.max(x * 16, region.getLowestX()), Math.max(region.getLowestY(), 0), Math.max(z * 16, region.getLowestZ()));
        Vector loc2 = new Vector(Math.min((x * 16) + 15, region.getHighestX()), Math.min(region.getHighestY(), 255), Math.min((z * 16) + 15, region.getHighestZ()));
        return new IdpWorldRegion(region.getWorld(), loc1, loc2);
    }

    /**
     * Returns the player that is handling the regeneration (often the staff member)
     * @return
     */
    protected final IdpPlayer getHandlingPlayer() {
        if (player == null) {
            player = InnPlugin.getPlugin().getPlayer(players[0], true);
        }
        return player;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * Reports information to the players
     * @param message
     */
    protected void reportPlayers(String message) {
        IdpPlayer player;
        for (String name : players) {
            player = InnPlugin.getPlugin().getPlayer(name, true);
            if (player != null) {
                player.printInfo(message);
            }
        }
    }

    /**
     * Regenerates a series of chunks inside the region.
     * If the last chunk is regenerated it will remove itself from the manager.
     *
     * This uses WE and Bukkit methods where the handling player must stay close to the regen area.
     */
    @Override
    public void run() {
        int startingChunk = lastChunk;
        IdpVector2D chunkLocation;
        while (lastChunk < chunks.size()) {

            if (getHandlingPlayer() == null || !getHandlingPlayer().isOnline()) {
                killTask("Player is not online..");
                return;
            }

            // Get the location of the current chunk
            chunkLocation = chunks.get(lastChunk);
            try {
                // Load the chunk
                region.getWorld().loadChunk(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
                // Handle it
                handleChunk(chunkLocation);
                // Refresh it
                region.getWorld().refreshChunk(chunkLocation.getBlockX(), chunkLocation.getBlockZ());

                // Increase chunk
                lastChunk++;
                // Check if breaking
                if (lastChunk - startingChunk == Configuration.LOT_REGEN_CHUNK_COUNT) {
                    break;
                }
            } catch (NoHandlingPlayerException nhpe) {
                killTask("Player is not online..");
                return;
            } catch (Exception ex) {
                lastChunk++;
                reportPlayers("Unknown exception doing chunk " + lastChunk + "/" + chunks.size() + ". Please notify an Admin!");
                InnPlugin.logError("Exception in regenerating chunk [" + chunkLocation.getBlockX() + "," + chunkLocation.getBlockZ() + "]", ex);
            }
        }

        // Report data to players
        if (lastChunk >= chunks.size()) {
            taskComplete();
            // Remove task from manager
            InnPlugin.getPlugin().getTaskManager().removeTask(this);
        } else {
            // Taking a break;
            taskIncrement(getChunkProgress());
        }
    }

    /**
     * Stops the current task and reports the given reason to the taskStopped methodx
     * @param reason
     */
    private void killTask(String reason) {
        taskStopped(reason);
        // Remove task from manager
        InnPlugin.getPlugin().getTaskManager().removeTask(this);
    }

    /**
     * Method that gets called when the task is complete
     */
    public abstract void taskStopped(String reason);

    /**
     * Method that gets called when the task is complete
     */
    public abstract void taskComplete();

    /**
     * Method that gets called if the task takes a break.
     * @param progress
     */
    public abstract void taskIncrement(int progress);

    /**
     * This method will handle a chunk.
     * @param chunkLocation
     */
    public abstract void handleChunk(IdpVector2D chunkLocation);
}

/**
 * Exception that gets thrown when there is no handling player
 */
class NoHandlingPlayerException extends IdpRuntimeException {

    public NoHandlingPlayerException() {
    }

    public NoHandlingPlayerException(String msg) {
        super(msg);
    }

    public NoHandlingPlayerException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
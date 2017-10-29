package net.innectis.innplugin.tasks.sync;

import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 *
 * This task will unload worlds when they are not used in some time.
 */
public class SyncUnloadWorldTask extends RepeatingTask {

    private static final int WORLD_UNLOAD_TIME = 40; // After 10 minutes
    private InnPlugin plugin;
    private Map<String, Integer> worldcounters;
    private String mainworldname;

    public SyncUnloadWorldTask(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, 15000);
        this.plugin = plugin;
        this.worldcounters = new HashMap<String, Integer>();

        // Find the name of the main world
        this.mainworldname = IdpWorldFactory.getWorld(IdpWorldType.INNECTIS).getName();
    }

    @Override
    public void run() {

        if (!InnPlugin.isShuttingDown()) {

            int counter;
            for (World world : plugin.getServer().getWorlds()) {

                // Skip main world (never unload)
                if (world.getName().equalsIgnoreCase(mainworldname)) {
                    continue;
                }

                // Get the counter
                if (worldcounters.containsKey(world.getName())) {
                    counter = worldcounters.get(world.getName());
                } else {
                    // new world, reset counter
                    counter = 0;
                }

                // Check for players
                if (world.getPlayers().isEmpty()) {

                    if (++counter > WORLD_UNLOAD_TIME) {
                        // Unload chunks
                        for (Chunk chunk : world.getLoadedChunks()) {
                            world.unloadChunk(chunk);
                        }
                    }
                } else {
                    // Players inside, reset counter
                    counter = 0;
                }

                worldcounters.put(world.getName(), counter);

            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}

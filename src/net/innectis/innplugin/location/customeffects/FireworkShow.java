package net.innectis.innplugin.location.customeffects;

import java.util.Random;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory.CountType;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 *
 * @author Nosliw
 */
public class FireworkShow extends CustomEffect {

    private final int range, speed, maxFireworks;
    protected final FireworkShow fireworkShow;
    private final InnPlugin plugin;

    public FireworkShow(InnPlugin plugin, Location location, int range, int speed, int maxFireworks) {
        super(location);

        this.range = range;
        this.speed = speed;
        this.plugin = plugin;
        this.maxFireworks = maxFireworks;
        fireworkShow = this;
    }

    @Override
    public void execute() {
        plugin.getTaskManager().addTask(new FireworkSpawnTask(plugin, this));
    }

    /**
     * @author Nosliw
     *
     * Task that will create the individual fireworks randomly.
     */
    private class FireworkSpawnTask extends LimitedTask {

        private Random random;
        private Block[] blocks;

        public FireworkSpawnTask(InnPlugin plugin, Location loc) {
            super(RunBehaviour.SYNCED, speed, maxFireworks);
            random = new Random();

            World world = loc.getWorld();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            Vector vec1 = new Vector(x + range, y, z + range);
            Vector vec2 = new Vector(x - range, y, z - range);

            IdpRegion region = new IdpRegion(vec1, vec2);
            BlockCounter counter = BlockCounterFactory.getCounter(CountType.YCYL);

            blocks = new Block[range * range];
            blocks = counter.getBlockList(region, world, null).toArray(blocks);
        }

        @Override
        public void run() {
            Block block = blocks[random.nextInt(blocks.length)];

            // Start from the lowest point.
            while (IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) == IdpMaterial.AIR && block.getY() > 0) {
                block = block.getRelative(BlockFace.DOWN);
            }

            // Lets make sure we are starting in an air block.
            while (IdpMaterial.fromBlock(block.getRelative(BlockFace.UP)) != IdpMaterial.AIR && block.getY() < 255) {
                block = block.getRelative(BlockFace.UP);
            }

            // Spawn the meteor at the target location.
            BlockHandler.launchRandomFirework(block.getLocation());
        }
    }

}

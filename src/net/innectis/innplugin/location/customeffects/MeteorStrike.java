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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 *
 * @author Nosliw
 */
public class MeteorStrike extends CustomEffect {

    private final int range, speed, maxStrikes;
    protected final MeteorStrike strike;
    private final InnPlugin plugin;

    public MeteorStrike(InnPlugin plugin, Location location, int range, int speed, int maxStrikes) {
        super(location);

        this.range = range;
        this.speed = speed;
        this.plugin = plugin;
        this.maxStrikes = maxStrikes;
        strike = this;
    }

    @Override
    public void execute() {
        plugin.getTaskManager().addTask(new MeteorSpawnTask(plugin, this));
    }

    /**
     * @author Nosliw
     *
     * Task that will create the individual meteor strikes.
     */
    private class MeteorSpawnTask extends LimitedTask {

        private Random random;
        private Block[] blocks;
        private final InnPlugin plugin;

        public MeteorSpawnTask(InnPlugin plugin, Location loc) {
            super(RunBehaviour.SYNCED, speed, maxStrikes);

            random = new Random();
            this.plugin = plugin;

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

            // Start from the lowest point (so we can track the Y).
            while (IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) == IdpMaterial.AIR && block.getY() > 0) {
                block = block.getRelative(BlockFace.DOWN);
            }

            int y = block.getY();

            // Lets make sure we are starting in an air block.
            while (IdpMaterial.fromBlock(block.getRelative(BlockFace.UP)) != IdpMaterial.AIR && block.getY() < 255) {
                block = block.getRelative(BlockFace.UP);
            }

            // Lets now go up to the highest air block.
            while (IdpMaterial.fromBlock(block.getRelative(BlockFace.UP)) == IdpMaterial.AIR && block.getY() < y + 30) {
                block = block.getRelative(BlockFace.UP);
            }

            // Spawn the meteor at the target location.
            plugin.getTaskManager().addTask(new MeteorStrikeTask(plugin, block, block.getY() - y));
        }
    }

    /**
     * @author Nosliw
     *
     * Task that will create the individual meteor strikes.
     */
    private class MeteorStrikeTask extends LimitedTask {
        private Block block;

        public MeteorStrikeTask(InnPlugin plugin, Block block, int distance) {
            super(RunBehaviour.SYNCED, 50, distance);

            this.block = block;
        }

        @Override
        public void run() {
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 200);
            block = block.getRelative(BlockFace.DOWN);

            if (IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) != IdpMaterial.AIR) {
                BlockHandler.spawnTNT(block.getLocation(), 20, 0);
            }
        }
    }

}

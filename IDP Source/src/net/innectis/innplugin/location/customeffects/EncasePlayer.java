package net.innectis.innplugin.location.customeffects;

import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Nosliw
 */
public class EncasePlayer extends CustomEffect {

    private final IdpPlayer player;
    private final int range, platform;

    public EncasePlayer(IdpPlayer player, int range, int platform) {
        super(player.getLocation());

        this.player = player;
        this.range = range;
        this.platform = platform;
    }

    @Override
    public void execute() {
        World world = super.getWorld();
        int x = super.getBlockX();
        int y = super.getBlockY();
        int z = super.getBlockZ();

        Vector vec1 = new Vector(x + range, y + range, z + range);
        Vector vec2 = new Vector(x - range, y - range, z - range);

        IdpRegion region = new IdpRegion(vec1, vec2);
        BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.SPHERE);

        for (Block block : counter.getBlockList(region, world, null)) {
            player.getHandle().sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
        }

        for (Block block : counter.getHollowBlockList(region, world, null)) {
            player.getHandle().sendBlockChange(block.getLocation(), Material.WOOL, (byte) 0);
        }

        vec1 = new Vector(x + platform, y + -1, z + platform);
        vec2 = new Vector(x - platform, y - 1, z - platform);

        region = new IdpRegion(vec1, vec2);
        counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.YCYL);

        for (Block block : counter.getBlockList(region, world, null)) {
            player.getHandle().sendBlockChange(block.getLocation(), Material.GLOWSTONE, (byte) 0);
        }

    }

}

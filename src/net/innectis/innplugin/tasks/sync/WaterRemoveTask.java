package net.innectis.innplugin.tasks.sync;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.block.Block;

/**
 * A task that removes a water source block by removing it
 * from the world
 *
 * @author AlphaBlend
 */
public class WaterRemoveTask extends LimitedTask {

    private Block block;

    public WaterRemoveTask(Block block, int delay) {
        super(RunBehaviour.SYNCED, delay, 1);
        this.block = block;
    }

    @Override
    public void run() {
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.WATER || mat == IdpMaterial.STATIONARY_WATER) {
            BlockHandler.setBlock(block, IdpMaterial.AIR);
        }
    }
    
}

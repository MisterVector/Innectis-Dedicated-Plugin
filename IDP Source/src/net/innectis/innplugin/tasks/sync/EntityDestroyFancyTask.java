package net.innectis.innplugin.tasks.sync;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * A task that destroys an entity in a fancy way
 *
 * @author AlphaBlend
 */
public class EntityDestroyFancyTask extends LimitedTask {

    private Entity entity = null;

    public EntityDestroyFancyTask(Entity entity) {
        super(RunBehaviour.SYNCED, 500, 1);
        this.entity = entity;
    }

    @Override
    public void run() {
        World world = entity.getWorld();
        Location loc = entity.getLocation();

        world.createExplosion(loc, 0);
        world.playEffect(loc, Effect.MOBSPAWNER_FLAMES, null);
        BlockHandler.launchRandomFirework(loc);
        entity.remove();
    }
    
}

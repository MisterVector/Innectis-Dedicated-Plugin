package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 *
 * Event for handling player damage by projectiles
 */
public class InnCreatureSpawnEvent extends AEntityEvent implements InnEventCancellable {

    private final SpawnReason spawnReason;
    private final Location loc;
    private boolean cancel;

    public InnCreatureSpawnEvent(LivingEntity entity, SpawnReason spawnReason, Location loc) {
        super(entity, InnEventType.ENTITY_SPAWN);
        this.spawnReason = spawnReason;
        this.loc = loc;

        cancel = false;
    }

    public LivingEntity getHandle() {
        return (LivingEntity) getEntity();
    }

    public SpawnReason getSpawnReason() {
        return spawnReason;
    }

    public Location getLoc() {
        return loc;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }
    
}

package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;

/**
 *
 * @author AlphaBlend
 */
public class InnProjectileHitEvent extends AbstractInnEvent {

    private final Projectile projectile;
    private final Entity hitEntity;
    private final Block hitBlock;

    public InnProjectileHitEvent(Projectile projectile, Entity hitEntity, Block hitBlock) {
        super(InnEventType.PROJECTILE_HIT);
        this.projectile = projectile;
        this.hitEntity = hitEntity;
        this.hitBlock = hitBlock;
    }

    /**
     * Gets the projectile that hit an entity
     * @return
     */
    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * Gets the entity that was hit in this event
     * @return
     */
    public Entity getHitEntity() {
        return hitEntity;
    }

    /**
     * Gets the block that was hit in this event
     * @return
     */
    public Block getHitBlock() {
        return hitBlock;
    }

}

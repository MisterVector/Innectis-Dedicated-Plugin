package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Class to handle the different entity damage types
 *
 * @author AlphaBlend
 */
public class InnEntityDamageEvent extends AEntityEvent implements InnEventCancellable {

    private final Entity damager;
    private final DamageCause cause;
    private final DamageType type;
    private double damage;
    private Location location;
    private boolean cancel;

    public InnEntityDamageEvent(EntityDamageEvent event) {
        super(event.getEntity(), InnEventType.ENTITY_ENVIRONMENTAL_DAMAGE);

        cause = event.getCause();
        location = getEntity().getLocation();
        damage = event.getDamage();

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
            damager = event2.getDamager();

            if (getEntity() instanceof Player) {
                if (damager instanceof Player) {
                    type = DamageType.PLAYER_DAMAGE_BY_PLAYER;
                } else if (damager instanceof Projectile) {
                    type = DamageType.PLAYER_DAMAGE_BY_PROJECTILE;
                } else {
                    type = DamageType.PLAYER_DAMAGE_BY_ENTITY;
                }
            } else {
                if (damager instanceof Player) {
                    type = DamageType.ENTITY_DAMAGE_BY_PLAYER;
                } else if (damager instanceof Projectile) {
                    type = DamageType.ENTITY_DAMAGE_BY_PROJECTILE;
                } else {
                    type = DamageType.ENTITY_DAMAGE_BY_ENTITY;
                }
            }
        } else {
            damager = null;

            if (getEntity() instanceof Player) {
                type = DamageType.PLAYER_DAMAGE;
            } else {
                type = DamageType.ENTITY_DAMAGE;
            }
        }
    }

    /**
     * Returns the last damage cause of the entity
     * @return
     */
    public DamageCause getCause() {
        return cause;
    }

    /**
     * Gets the location the entity was damaged at
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets an IdpPlayer from the entity being damaged, if it
     * is a player, or null otherwise
     *
     * @return IdpPlayer
     */
    public IdpPlayer getPlayer() {
        Entity entity = getEntity();

        if (entity instanceof Player) {
            return InnPlugin.getPlugin().getPlayer((Player) entity);
        }

        return null;
    }

    /**
     * Gets the damager entity
     * @return
     */
    public Entity getDamager() {
        return damager;
    }

    /**
     * Gets the damager as an IdpPlayer, if it is a player
     * @return
     */
    public IdpPlayer getDamagerPlayer() {
        return InnPlugin.getPlugin().getPlayer((Player) damager);
    }

    /**
     * Gets the projectile, if the damager is a projectile
     *
     * @return
     */
    public Projectile getProjectile() {
        // Check type just to be sure.
        switch (getDamageType()) {
            case ENTITY_DAMAGE_BY_PROJECTILE:
            case PLAYER_DAMAGE_BY_PROJECTILE:
                return (Projectile) damager;
            default:
                return null;
        }
    }

    /**
     * Gets the damage type of this event
     * @return
     */
    public DamageType getDamageType() {
        return type;
    }

    /**
     * Gets the lot the damaged entity is on
     * @return
     */
    public InnectisLot getLot() {
        return getLot(false);
    }

    /**
     * Gets the lot the damaged entity is on
     * @param hidden specifies if hidden lots should be checked
     * @return
     */
    public InnectisLot getLot(boolean hidden) {
        return LotHandler.getLot(location, hidden);
    }

    /**
     * Sets the damage amount of this event
     * @param damage
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Gets the damage value of this event
     * @return
     */
    public double getDamage() {
        return damage;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    /**
     * Type of damage event that is thrown
     */
    public static enum DamageType {

        ENTITY_DAMAGE,
        ENTITY_DAMAGE_BY_ENTITY,
        ENTITY_DAMAGE_BY_PLAYER,
        ENTITY_DAMAGE_BY_PROJECTILE,
        PLAYER_DAMAGE,
        PLAYER_DAMAGE_BY_ENTITY,
        PLAYER_DAMAGE_BY_PLAYER,
        PLAYER_DAMAGE_BY_PROJECTILE;
    }

}
package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.handlers.PvpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EntityConstants;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Entity damage class whose purpose is to filter individual events If a method
 * returns true, the associated event is canceled
 *
 * @author AlphaBlend
 */
public class IdpEntityDamageListener {

    InnPlugin plugin;

    public IdpEntityDamageListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onEntityDamage(InnEntityDamageEvent event) {
        Entity entity = event.getEntity();
        InnectisLot lot = event.getLot();

        if ((entity instanceof Animals || entity instanceof Snowman
                || entity instanceof IronGolem || entity instanceof Bat
                || entity instanceof Villager)
                && lot != null && lot.isFlagSet(LotFlagType.FARM)) {
            return true;
        }

        return false;
    }

    public boolean onEntityDamageByEntity(InnEntityDamageEvent event) {
        Entity entity = event.getEntity();
        InnectisLot lot = event.getLot();

        if ((entity instanceof Animals || entity instanceof Snowman || entity instanceof IronGolem
                || entity instanceof Squid || entity instanceof Bat || entity instanceof Villager)
                && lot != null && lot.isFlagSet(LotFlagType.FARM)) {
            return true;
        }

        return false;
    }

    public boolean onEntityDamageByProjectile(InnEntityDamageEvent event) {
        Projectile projectile = event.getProjectile();
        Entity entity = event.getEntity();
        InnectisLot lot = event.getLot();

        if (projectile.getShooter() instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) projectile.getShooter());

            if ((entity instanceof Animals || entity instanceof Snowman || entity instanceof IronGolem
                || entity instanceof Squid || entity instanceof Bat || entity instanceof Villager)
                && lot != null && lot.isFlagSet(LotFlagType.FARM)
                && !lot.canPlayerManage(player.getName())) {
                if (entity instanceof Tameable) {
                    Tameable tameable = (Tameable) entity;

                    // Owner of this animal may damagage it
                    if (tameable.getOwner() != null && tameable.getOwner().getName().equalsIgnoreCase(player.getName())) {
                        return false;
                    }
                }

                IdpMaterial targMaterial = null;

                // Don't hand projectile back if the arrow is a special arrow
                boolean returnProjectile = !projectile.hasMetadata(EntityConstants.METAKEY_CUSTOM_ARROW);

                if (returnProjectile) {
                    switch (projectile.getType()) {
                        case EGG:
                            targMaterial = IdpMaterial.EGG;
                            break;
                        case ARROW:
                            targMaterial = IdpMaterial.ARROW;
                            break;
                        case SNOWBALL:
                            targMaterial = IdpMaterial.SNOWBALL;
                            break;
                    }

                    // Hand the projectile back to the player and then destroy it
                    if (targMaterial != null) {
                        EquipmentSlot projectileSlot = player.getHandSlotForMaterial(targMaterial);
                        boolean added = false;

                        if (projectileSlot != null) {
                            IdpItemStack projectileStack = player.getItemInHand(projectileSlot);
                            int amt = projectileStack.getAmount();
                            int maxStack = projectileStack.getMaterial().getMaxStackSize();

                            if (amt < maxStack) {
                                projectileStack.setAmount(amt + 1);
                                player.setItemInHand(projectileSlot, projectileStack);
                                added = true;
                            }
                        }

                        if (!added) {
                            player.addItemToInventory(new IdpItemStack(targMaterial, 1));
                        }
                    }
                }

                projectile.remove();

                player.printError("Hold on there, partner, this be a peaceful farm!");
                return true;
            } else if (entity instanceof Tameable) {
                Tameable tamed = (Tameable) entity;

                if (tamed.isTamed() && tamed.getOwner() != null && !tamed.getOwner().getName().equalsIgnoreCase(player.getName())) {
                    return true;
                }
            // Projectiles fired by players can only damage armor stands if the player
            // has access to the lot or permission
            } else if (entity instanceof ArmorStand) {
                InnectisLot lotIncludeHidden = event.getLot(true);

                return !(lotIncludeHidden == null || lotIncludeHidden.canPlayerAccess(player.getName())
                        || player.hasPermission(Permission.world_build_unrestricted));
            }
        } else {
            if ((entity instanceof Animals || entity instanceof Snowman || entity instanceof IronGolem
                    || entity instanceof Squid || entity instanceof Bat || entity instanceof Villager)
                    && lot != null && lot.isFlagSet(LotFlagType.FARM)) {
                return true;
            }
        }

        return false;
    }

    public boolean onEntityDamageByPlayer(InnEntityDamageEvent event) {
        Entity entity = event.getEntity();
        IdpPlayer player = event.getDamagerPlayer();

        // If you ride a mob and strike them, you are ejected
        if (entity.getPassenger() != null) {
            Entity entity2 = entity.getPassenger();

            if (entity2.getEntityId() == player.getHandle().getEntityId()) {
                entity.eject();
                //return false; // Eject but still do normal damage checks.
            }
        }

        // Owners of tamed animals may damage them, always
        if (entity instanceof Tameable) {
            Tameable tamed = (Tameable) entity;

            // Owner of tamed animal can damage it
            if (tamed.isTamed() && tamed.getOwner() != null && tamed.getOwner().getName().equalsIgnoreCase(player.getName())) {
                return false;
            }
        } else if (entity instanceof ArmorStand) {
            InnectisLot lotIncludeHidden = event.getLot(true);

            return !(lotIncludeHidden == null || lotIncludeHidden.canPlayerAccess(player.getName())
                    || player.hasPermission(Permission.world_build_unrestricted));
        }

        InnectisLot lot = event.getLot();

        // Check lot for farm flag
        if (lot != null && lot.isFlagSet(LotFlagType.FARM)
                && !lot.canPlayerManage(player.getName())
                && (entity instanceof Animals || entity instanceof Snowman || entity instanceof IronGolem
                || entity instanceof Squid || entity instanceof Bat || entity instanceof Villager)) {
            player.printError("Hold on there, partner, this be a peaceful farm!");
            return true;
        }

        IdpItemStack handStack = player.getItemInMainHand();

        if (handStack.getMaterial() != IdpMaterial.AIR) {
            if (handStack.getItemdata().getSpecialItem() != null) {
                switch (handStack.getItemdata().getSpecialItem()) {
                    case FROSTMOURNE: {
                        if (!player.hasPermission(Permission.special_weapons_invuse)) {
                            player.setItemInMainHand(new IdpItemStack(IdpMaterial.AIR, 0));
                        }

                        event.setDamage(20000);
                    }
                }
            }
        }

        return false;
    }

    public boolean onPlayerDamage(InnEntityDamageEvent event) {
        IdpPlayer player = event.getPlayer();

        // Jumped player does not take fall damage
        if (player.getSession().isJumped()) {
            player.getSession().setJumped(false);
            return true;
        }

        InnectisLot lot = event.getLot();

        if (isPlayerImmuneFromDamage(player, lot, event.getCause())) {
            return true;
        }

        DamageCause cause = event.getCause();

        if (cause == DamageCause.DROWNING) {
            // Check if wearing scuba gear
            if (player.getHelmet().getMaterial() == IdpMaterial.GOLD_HELMET
                    && player.getChestplate().getMaterial() == IdpMaterial.GOLD_CHEST
                    && player.getLeggings().getMaterial() == IdpMaterial.GOLD_LEGGINGS
                    && player.getBoots().getMaterial() == IdpMaterial.GOLD_BOOTS) {

                // Set to max
                player.getHandle().getHandle().setAirTicks(player.getHandle().getMaximumAir());

                // Damage the helmet
                IdpItemStack item = player.getHelmet();
                item.setData((short) (item.getData() + (item.toBukkitItemstack().getType().getMaxDurability() * 0.04)));

                // Check if broken
                if (item.getData() > item.toBukkitItemstack().getType().getMaxDurability()) {
                    item = IdpItemStack.EMPTY_ITEM;
                    player.printInfo("Using up the last air in your helmet it started leaking...");
                }

                // Update helmet
                player.setHelmet(item);
                return true;
            }
        } else if (cause == DamageCause.FALL) {
            // Do not take damage on NoFallDamage lots
            if (lot != null && lot.isFlagSet(LotFlagType.NOFALLDAMAGE)) {
                return true;
            }
        }

        return false;
    }

    public boolean onPlayerDamageByEntity(InnEntityDamageEvent event) {
        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getLot();

        if (isPlayerImmuneFromDamage(player, lot, event.getCause())) {
            return true;
        }

        // Check for special (strong) armour
        if (hasAdamantineArmour(player)) {
            event.setDamage(event.getDamage() * 0.20);
        }

        return false;
    }

    public boolean onPlayerDamageByProjectile(InnEntityDamageEvent event) {
        IdpPlayer player = event.getPlayer();
        Projectile projectile = event.getProjectile();

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_DAMAGE_BY_PROJECTILE)) {
            InnPlayerDamageByProjectileEvent idpevent = new InnPlayerDamageByProjectileEvent(player, projectile);
            plugin.getListenerManager().fireEvent(idpevent);

            event.setCancelled(idpevent.isCancelled());

            if (idpevent.shouldTerminate()) {
                return idpevent.isCancelled();
            }
        }

        InnectisLot lot = event.getLot();

        if (isPlayerImmuneFromDamage(player, lot, event.getCause())) {
            return true;
        }

        // If the damage came from the owner throwing an ender pearl, ignore if they have godmode on
        if (projectile.getType() == EntityType.ENDER_PEARL) {
            if (projectile.getShooter() instanceof Player) {
                IdpPlayer throwPlayer = plugin.getPlayer((Player) projectile.getShooter());

                if (player.getName().equalsIgnoreCase(throwPlayer.getName())) {
                    // Only damage when no godmode
                    return player.getSession().hasGodmode();
                }
            }
        }

        // Check for special (strong) armour
        if (hasAdamantineArmour(player)) {
            event.setDamage((event.getDamage() * 0.10));
        }


        // If the entity shooting the projectile is a player
        if (projectile.getShooter() instanceof Player) {
            IdpPlayer attacker = plugin.getPlayer((Player) projectile.getShooter());

            if (!canPlayerPvPPlayer(event, attacker, player)) {
                player.getHandle().setFireTicks(0);
                return true;
            }

            boolean chainArmour = player.getBoots().getMaterial() == IdpMaterial.CHAINMAIL_BOOTS;
            chainArmour = chainArmour && player.getChestplate().getMaterial() == IdpMaterial.CHAINMAIL_CHEST;
            chainArmour = chainArmour && player.getHelmet().getMaterial() == IdpMaterial.CHAINMAIL_HELMET;
            chainArmour = chainArmour && player.getLeggings().getMaterial() == IdpMaterial.CHAINMAIL_LEGGINGS;

            // Deflect projectiles if chainmail
            if (chainArmour) {
                event.getEntity().setFireTicks(0);
                return true;
            }

            // The damage is allowed.
            attacker.getSession().setPvPStateTime();
            player.getSession().setPvPStateTime();

        }

        return false;
    }

    public boolean onPlayerDamageByPlayer(InnEntityDamageEvent event) {
        IdpPlayer attacker = event.getDamagerPlayer();
        IdpPlayer defender = event.getPlayer();

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_DAMAGE_BY_PLAYER)) {
            InnPlayerDamageByPlayerEvent idpevent = new InnPlayerDamageByPlayerEvent(defender, attacker);
            plugin.getListenerManager().fireEvent(idpevent);

            event.setCancelled(idpevent.isCancelled());

            if (idpevent.shouldTerminate()) {
                return idpevent.isCancelled();
            }
        }

        InnectisLot lot = event.getLot();

        if (isPlayerImmuneFromDamage(defender, lot, event.getCause())) {
            return true;
        }

        if (!canPlayerPvPPlayer(event, attacker, defender)) {
            return true;
        }

        // Check for special (strong) armour
        if (hasAdamantineArmour(defender)) {
            event.setDamage((event.getDamage() * 0.20));
        }

        // The damage is allowed.
        attacker.getSession().setPvPStateTime();
        defender.getSession().setPvPStateTime();

        return false;
    }

    /**
     * Checks to see if PvP is possible between players
     *
     * @param atacker
     * @param defender
     * @return
     */
    private boolean canPlayerPvPPlayer(InnEntityDamageEvent e, IdpPlayer attacker, IdpPlayer defender) {
        if (PvpHandler.playerCanHit(attacker, defender, false, false)) {
            return true;
        } else {
            attacker.printRaw(defender.getPrefixAndDisplayName() + ChatColor.WHITE + ": Auch!");
        }

        return false;
    }

    /**
     * Generic check to see if the player will get damaged based on specific
     * world settings or player characteristics
     *
     * @param lot
     * @param player
     * @param cause
     * @return
     */
    private boolean isPlayerImmuneFromDamage(IdpPlayer player, InnectisLot lot, DamageCause cause) {
        // Check god mode
        if (player.getSession().hasGodmode()) {
            player.getHandle().setFireTicks(0);
            return true;
        }

        // are we ignoring fall damage?
        if (cause == EntityDamageEvent.DamageCause.FALL && player.getSession().isJumped()) {
            player.getSession().setJumped(false);
            return true;
        }

        // Check lot for noDamage
        if (lot != null && lot.isFlagSet(LotFlagType.NODAMAGE)) {
            player.getHandle().setFireTicks(0);
            return true;
        }

        return false;
    }

    private boolean hasAdamantineArmour(IdpPlayer defender) {
        IdpItemStack[] armor = new IdpItemStack[] {defender.getHelmet(), defender.getChestplate(), defender.getLeggings(), defender.getBoots()};

        for (IdpItemStack stack : armor) {
            if (stack != null) {
                if (stack.getItemdata().getSpecialItem() == SpecialItemType.ADAMANTINE_ARMOR) {
                    return true;
                }
            }
        }

        return false;
    }

}

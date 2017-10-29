package net.innectis.innplugin.handlers;

import net.innectis.innplugin.system.warps.WarpHandler;
import java.util.HashMap;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Lynxy
 *
 * Handler for PVP
 */
public final class PvpHandler {

    private static HashMap<Integer, Long> _lotPvpToggle = new HashMap<Integer, Long>();

    public synchronized static HashMap<Integer, Long> getLotPvpToggle() {
        return _lotPvpToggle;
    }

    /**
     * Method to check if the attack is allowed to hit the defending player.
     *
     * @param defender
     * @param attacker
     * @param selfOverride
     * @return
     */
    public static boolean playerCanHit(IdpPlayer attacker, IdpPlayer defender, boolean isProjectile, boolean selfOverride) {
        if (defender.getName().equalsIgnoreCase(attacker.getName()) && !selfOverride) {
            //InnPlugin.logDebug("Names are the same");
            return false;
        }

        if (attacker.getSession().hasGodmode() || defender.getSession().hasGodmode()) {
            //InnPlugin.logDebug("One has godmode! " + attacker.getSession().hasGodmode() + " " + defender.getSession().hasGodmode());
            return false;
        }

        if (attacker.getSession().isPvPImmune() || defender.getSession().isPvPImmune()) {
            // InnPlugin.logDebug("Immunity! " + attacker.getSession().isPVPImmune() + " " + defender.getSession().isPVPImmune());
            return false; //neither can attack if either is immune
        }

        if (!attacker.getSession().isVisible() || !defender.getSession().isVisible()
                || attacker.getHandle().hasPotionEffect(PotionEffectType.INVISIBILITY)
                || defender.getHandle().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
//            if (InnPlugin.isDebugEnabled()) {
//                boolean attackerInvisible = (!attacker.getSession().isVisible() || attacker.getHandle().hasPotionEffect(PotionEffectType.INVISIBILITY));
//                boolean defenderInvisible = (!defender.getSession().isVisible() || defender.getHandle().hasPotionEffect(PotionEffectType.INVISIBILITY));
//
//                InnPlugin.logDebug("One is invisible! Attacker: " + attackerInvisible + " Defender: " + defenderInvisible);
//            }
            return false;
        }

        // IdpLocation loc = defender.getLocation();
        // if (!attacker.hasLineOfSight(loc.getBlock(), 5) && !attacker.hasLineOfSight(loc.add(0, 1, 0).getBlock(), 5)) {
        //    if (InnPlugin.isDebugEnabled())
        //        InnPlugin.logDebug("LOS fail! (1)");
        //    return false;
        // }

        // BOTH players must be in PvP area - no standing on border!
        InnectisLot defenderLot = LotHandler.getLot(defender.getLocation());
        InnectisLot attackerLot = LotHandler.getLot(attacker.getLocation());

        // disallow spawn
        if (defenderLot != null && defenderLot == WarpHandler.getSpawnLot()) {
            //InnPlugin.logDebug("Defender is on spawn lot.");
            return false;
        }

        // Check personal PVP settings
        if (attacker.getSession().isPersonalPvpEnabled() && defender.getSession().isPersonalPvpEnabled()) {
            // InnPlugin.logDebug("Pvp enabled for both " + attacker.getSession().isPersonalPvpEnabled() + " " + defender.getSession().isPersonalPvpEnabled());
            return true;
        }

        // Check for lots
        if (attackerLot != null && defenderLot != null) {
            // Check if lotflag is set (overriding personal settings!)
            // BOTH lots must have PvP enabled!
            if (attackerLot.isFlagSet(LotFlagType.PVP) && defenderLot.isFlagSet(LotFlagType.PVP)) {
                //plugin.logDebug("PVP FLAG SET");
                if (isProjectile && attackerLot.isFlagSet(LotFlagType.NOMELEE)) {
                    //InnPlugin.logDebug("Attack stopped, NoMelee flag.");
                    return false;
                }
                if (!isProjectile && attackerLot.isFlagSet(LotFlagType.NORANGED)) {
                    //InnPlugin.logDebug("Attack stopped, NoRanged flag.");
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    public static void playerKilledPlayer(IdpPlayer attacker, IdpPlayer defender) {
        int points = 0; //start points

        // No points for killing yourself
        if (!attacker.getName().equalsIgnoreCase(defender.getName())) {
            int kills = attacker.getSession().getPvpKillTotalOf(defender.getUniqueId());
            if (kills == 0) {
                points = 10;
            } else if (kills == 1) {
                points = 5;
            } else if (kills == 2 || kills == 3) {
                points = 2;
            } else if (kills >= 4 && kills <= 6) {
                points = 1;
            }
        }

        IdpItemStack item = attacker.getItemInHand(EquipmentSlot.HAND);

        if (item != null && item.getMaterial() != IdpMaterial.AIR) {
            SpecialItemType type = item.getItemdata().getSpecialItem();

            if (type != null && type == SpecialItemType.UMBRA) {
                IdpItemStack head = new IdpItemStack(IdpMaterial.PLAYER_SKULL, 1);
                head.getItemdata().setMobheadName(defender.getName());
                attacker.getHandle().getInventory().addItem(head.toBukkitItemstack());
            }
        }


        //defender.dealDamage(100000); //ensure they die
        attacker.getSession().addPvpKill(defender.getUniqueId());

        TransactionObject transaction = TransactionHandler.getTransactionObject(attacker);
        transaction.addValue(points, TransactionHandler.TransactionType.PVP_POINTS);

        attacker.print(ChatColor.LIGHT_PURPLE, "You have been awarded " + points + " points for killing " + defender.getColoredName());
        InnectisLot attackerLot = LotHandler.getLot(attacker.getLocation());
        InnectisLot defenderLot = LotHandler.getLot(defender.getLocation());


        String attackerString = " " + LocationUtil.locationString(attacker.getLocation());
        if (attackerLot != null) {
            attackerString += " (Lot #" + attackerLot.getId() + (attackerLot.getLotName() != null ? " - " + attackerLot.getLotName() : "") + ")";
        }

        String defenderString = " " + LocationUtil.locationString(defender.getLocation());
        if (defenderLot != null) {
            defenderString += " (Lot #" + defenderLot.getId() + (defenderLot.getLotName() != null ? " - " + defenderLot.getLotName() : "") + ")";
        }

        InnPlugin.logInfo("[PvP] " + attacker.getColoredName(), attackerString + " killed " + defender.getColoredName(), defenderString + "!");
    }
    
}

package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;

public abstract class PlayerClass {

    public abstract PlayerClassType getClassType();

    /**
     * Finds the attack score for a specific weapon.
     * @param weapon being used to attack.
     * @return The damage rating the player deals with this weapon.
     */
    public abstract int getAttackScore(IdpMaterial weapon);

    /**
     * Finds the defence score against a specific weapon.
     * @param weapon being attacked with.
     * @return The defence rating the player has against this weapon.
     */
    public abstract int getDefenceScore(IdpMaterial weapon);

    /**
     * Finds the bonus effect that would be used for this specific item.
     * @param weapon The weapon used to hit a player.
     * @param sameTeam Weather the player is on the same team.
     * @return The type of bonus that effect that should occur.
     */
    public abstract PlayerClassBonusType getAttackBonusType(IdpMaterial weapon, boolean sameTeam);

    /**
     * Returns the minimum amount of time between using abilities.
     * @return time in milliseconds.
     */
    public abstract int getBonusCooldown();

    /**
     * Handles what should happen when the player is hit.
     * @param player The player that was hit.
     */
    public abstract void handleHit(IdpPlayer player);

    /**
     * Returns the weapon that sits in the primary slot.
     * @return primary weapon item stack.
     */
    public abstract IdpItemStack getPrimaryWeapon();

    /**
     * Returns the weapon that sits in the secondary slot.
     * @return secondary weapon item stack.
     */
    public abstract IdpItemStack getSecondaryWeapon();

    /**
     * Returns the consumable items that sits on hotbar. (e.g. Arrows, Ender Pearls)
     * @return consumable item stack.
     */
    public abstract IdpItemStack getConsumable();

    /**
     * Returns the maximum amount of consumables that can be held. (e.g. Arrows, Ender Pearls)
     * @return maximum consumable amount.
     */
    public abstract int getConsumableMax();

    /**
     * Returns the reward amount of consumable given. (e.g. Arrows, Ender Pearls)
     * @return consumable increase amount.
     */
    public abstract int getConsumableIncrease();

    /**
     * Returns the bonus items that sits on hotbar. (e.g. Stomp Power, Speed Dust)
     * @return bonus item stack.
     */
    public abstract IdpItemStack getBonus();

    /**
     * Returns the maximum amount of bonus items that can be held. (e.g. Stomp Power, Speed Dust)
     * @return maximum consumable amount.
     */
    public abstract int getBonusMax();

    /**
     * Returns the reward amount of bonus items given. (e.g. Stomp Power, Speed Dust)
     * @return consumable increase amount.
     */
    public abstract double getBonusIncrease();

    /**
     * Returns the effect type for using this classes bonus item.
     * @return Bonus Effect Type for this class
     */
    public abstract PlayerClassBonusType getBonusEffect();

    /**
     * Returns the range of the bonus effect for using this classes bonus item.
     * @return Bonus Effect Range for this class
     */
    public abstract int getBonusRange();

    /**
     * Returns the effect type for using secondary item.
     * @return Secondary Effect Type for this class
     */
    public abstract PlayerClassBonusType getSecondaryEffect();
    
}

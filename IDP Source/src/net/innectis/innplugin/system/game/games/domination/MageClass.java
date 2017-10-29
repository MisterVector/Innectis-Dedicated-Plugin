package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

public class MageClass extends PlayerClass {

    public PlayerClassType getClassType() {
        return PlayerClassType.MAGE;
    }

    @Override
    public int getAttackScore(IdpMaterial weapon) {
        switch (weapon) {
            case SNOWBALL:
                return 0;
            case GOLD_SWORD:
                return 5;
            default:
                return 1;
        }
    }

    @Override
    public int getDefenceScore(IdpMaterial weapon) {
        switch (weapon) {
            case BOW:
                return 4;
            default:
                return 1;
        }
    }

    @Override
    public PlayerClassBonusType getAttackBonusType(IdpMaterial weapon, boolean sameTeam) {
        if (!sameTeam) {
            switch (weapon) {
                case GOLD_SWORD:
                    return PlayerClassBonusType.FIRE;
                case SNOWBALL:
                    return PlayerClassBonusType.POISON;
            }
        }
        return PlayerClassBonusType.NONE;
    }

    @Override
    public int getBonusCooldown() {
        // 0 Seconds.
        return 0;
    }

    @Override
    public void handleHit(IdpPlayer player) {
        // Nothing
    }

    @Override
    public IdpItemStack getPrimaryWeapon() {
        return new IdpItemStack(IdpMaterial.GOLD_SWORD, 1);
    }

    @Override
    public IdpItemStack getSecondaryWeapon() {
        return new IdpItemStack(IdpMaterial.AIR, 0);
    }

    @Override
    public IdpItemStack getConsumable() {
        return new IdpItemStack(IdpMaterial.AIR, 0);
    }

    @Override
    public IdpItemStack getBonus() {
        return new IdpItemStack(IdpMaterial.GUNPOWDER, 8, ChatColor.GOLD + "Poison Spell",
                new String[]{ChatColor.GRAY + "Throws a poisonous snowball."});
    }

    @Override
    public int getConsumableMax() {
        return 0;
    }

    @Override
    public int getConsumableIncrease() {
        return 0;
    }

    @Override
    public int getBonusMax() {
        return 64;
    }

    @Override
    public double getBonusIncrease() {
        return 2;
    }

    @Override
    public PlayerClassBonusType getBonusEffect() {
        return PlayerClassBonusType.POISON;
    }

    @Override
    public int getBonusRange() {
        return 0;
    }

    @Override
    public PlayerClassBonusType getSecondaryEffect() {
        return PlayerClassBonusType.NONE;
    }
    
}

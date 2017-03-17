package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

public class MedicClass extends PlayerClass {

    public PlayerClassType getClassType() {
        return PlayerClassType.MEDIC;
    }

    @Override
    public int getAttackScore(IdpMaterial weapon) {
        switch (weapon) {
            case WOOD_SWORD:
                return 2;
            default:
                return 1;
        }
    }

    @Override
    public int getDefenceScore(IdpMaterial weapon) {
        switch (weapon) {
            case BOW:
                return 3;
            default:
                return 2;
        }
    }

    @Override
    public PlayerClassBonusType getAttackBonusType(IdpMaterial weapon, boolean sameTeam) {
        if (sameTeam) {
            switch (weapon) {
                case GOLD_AXE:
                case EGG:
                    return PlayerClassBonusType.HEAL;
            }
        }
        return PlayerClassBonusType.NONE;
    }

    @Override
    public int getBonusCooldown() {
        // 5 Seconds.
        return 5000;
    }

    @Override
    public void handleHit(IdpPlayer player) {
        // Nothing
    }

    @Override
    public IdpItemStack getPrimaryWeapon() {
        return new IdpItemStack(IdpMaterial.WOOD_SWORD, 1);
    }

    @Override
    public IdpItemStack getSecondaryWeapon() {
        return new IdpItemStack(IdpMaterial.GOLD_AXE, 1);
    }

    @Override
    public IdpItemStack getConsumable() {
        return new IdpItemStack(IdpMaterial.AIR, 0);
    }

    @Override
    public IdpItemStack getBonus() {
        return new IdpItemStack(IdpMaterial.REDSTONE_DUST, 3, ChatColor.GOLD + "Empowering Dust",
                new String[]{ChatColor.GRAY + "Empowers nearby allies."});
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
        return 8;
    }

    @Override
    public double getBonusIncrease() {
        return 1;
    }

    @Override
    public PlayerClassBonusType getBonusEffect() {
        return PlayerClassBonusType.EMPOWER;
    }

    @Override
    public int getBonusRange() {
        return 10;
    }

    @Override
    public PlayerClassBonusType getSecondaryEffect() {
        return PlayerClassBonusType.EGG;
    }
    
}

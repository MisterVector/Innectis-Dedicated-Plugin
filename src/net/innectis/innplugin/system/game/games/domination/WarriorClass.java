package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

public class WarriorClass extends PlayerClass {

    public PlayerClassType getClassType() {
        return PlayerClassType.WARRIOR;
    }

    @Override
    public int getAttackScore(IdpMaterial weapon) {
        switch (weapon) {
            case IRON_SWORD:
                return 6;
            default:
                return 3;
        }
    }

    @Override
    public int getDefenceScore(IdpMaterial weapon) {
        switch (weapon) {
            case BOW:
                return 0;
            default:
                return 2;
        }
    }

    @Override
    public PlayerClassBonusType getAttackBonusType(IdpMaterial weapon, boolean sameTeam) {
        return PlayerClassBonusType.NONE;
    }

    @Override
    public int getBonusCooldown() {
        // 30 Seconds.
        return 30000;
    }

    @Override
    public void handleHit(IdpPlayer player) {
        // Nothing
    }

    @Override
    public IdpItemStack getPrimaryWeapon() {
        return new IdpItemStack(IdpMaterial.IRON_SWORD, 1);
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
        return new IdpItemStack(IdpMaterial.IRON_BOOTS, 1, ChatColor.GOLD + "Stomping Boots",
                new String[]{ChatColor.GRAY + "Pushes away nearby enemies."});
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
        return 1;
    }

    @Override
    public double getBonusIncrease() {
        return 1;
    }

    @Override
    public PlayerClassBonusType getBonusEffect() {
        return PlayerClassBonusType.STOMP;
    }

    @Override
    public int getBonusRange() {
        return 10;
    }

    @Override
    public PlayerClassBonusType getSecondaryEffect() {
        return PlayerClassBonusType.NONE;
    }
    
}

package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

public class ArcherClass extends PlayerClass {

    public PlayerClassType getClassType() {
        return PlayerClassType.ARCHER;
    }

    @Override
    public int getAttackScore(IdpMaterial weapon) {
        switch (weapon) {
            case WOOD_SWORD:
                return 3;
            case BOW:
                return 7;
            default:
                return 2;
        }
    }

    @Override
    public int getDefenceScore(IdpMaterial weapon) {
        switch (weapon) {
            case BOW:
                return 3;
            default:
                return 1;
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
        return new IdpItemStack(IdpMaterial.WOOD_SWORD, 1);
    }

    @Override
    public IdpItemStack getSecondaryWeapon() {
        return new IdpItemStack(IdpMaterial.BOW, 1);
    }

    @Override
    public IdpItemStack getConsumable() {
        return new IdpItemStack(IdpMaterial.ARROW, 32);
    }

    @Override
    public IdpItemStack getBonus() {
        return new IdpItemStack(IdpMaterial.GLOWSTONE_DUST, 3, ChatColor.GOLD + "Speed Powder",
                new String[]{ChatColor.GRAY + "Gives a boost of speed."});
    }

    @Override
    public int getConsumableMax() {
        return 64;
    }

    @Override
    public int getConsumableIncrease() {
        return 8;
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
        return PlayerClassBonusType.SPEED;
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

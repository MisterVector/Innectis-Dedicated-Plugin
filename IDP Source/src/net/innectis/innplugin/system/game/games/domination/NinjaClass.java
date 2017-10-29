package net.innectis.innplugin.system.game.games.domination;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

public class NinjaClass extends PlayerClass {

    public PlayerClassType getClassType() {
        return PlayerClassType.NINJA;
    }

    @Override
    public int getAttackScore(IdpMaterial weapon) {
        switch (weapon) {
            case STONE_SWORD:
                return 6;
            default:
                return 2;
        }
    }

    @Override
    public int getDefenceScore(IdpMaterial weapon) {
        switch (weapon) {
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
        // 17 Seconds.
        return 17000;
    }

    @Override
    public void handleHit(IdpPlayer player) {
        if (player.getSession().getActiveTask() != 0) {
            InnPlugin.getPlugin().getTaskManager().runTask(player.getSession().getActiveTask(), true);
            player.getSession().setActiveTask(0);
        }
    }

    @Override
    public IdpItemStack getPrimaryWeapon() {
        return new IdpItemStack(IdpMaterial.STONE_SWORD, 1);
    }

    @Override
    public IdpItemStack getSecondaryWeapon() {
        return new IdpItemStack(IdpMaterial.AIR, 0);
    }

    @Override
    public IdpItemStack getConsumable() {
        return new IdpItemStack(IdpMaterial.ENDER_PEARL, 6);
    }

    @Override
    public IdpItemStack getBonus() {
        return new IdpItemStack(IdpMaterial.SUGAR, 3, ChatColor.GOLD + "Invisible Powder",
                new String[]{ChatColor.GRAY + "Blend into the background."});
    }

    @Override
    public int getConsumableMax() {
        return 16;
    }

    @Override
    public int getConsumableIncrease() {
        return 2;
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
        return PlayerClassBonusType.INVISIBLE;
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

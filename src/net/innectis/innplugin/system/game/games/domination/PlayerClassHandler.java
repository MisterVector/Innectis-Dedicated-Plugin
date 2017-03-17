package net.innectis.innplugin.system.game.games.domination;

import java.util.List;
import java.util.Random;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.system.game.IdpGameManager;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

public class PlayerClassHandler {

    private static final int defenceMultiplier = 1;
    private static final int blockingDefenceMultiplier = 2;
    private static final int fireDamageTicks = 100;
    private static final int poistonTicks = 100;
    private static final int knockbackDistance = 10;
    private static final float knockbackHeight = 0.15f;
    private static final double healOtherAmount = 2;
    private static final double healSelfAmount = 1;
    private static final int empowerTicks = 100;

    public static int getAttackDamage(PlayerClass attacker, IdpMaterial weapon, PlayerClass defender, boolean isBlocking) {
        return Math.max(0, attacker.getAttackScore(weapon)
                - ((defender.getDefenceScore(weapon) * (isBlocking ? blockingDefenceMultiplier : defenceMultiplier))));
    }

    public static void handleBonusAttackEffect(PlayerClassBonusType type, IdpPlayer weilder, IdpPlayer target) {
        switch (type) {
            case FIRE:
                target.getHandle().setFireTicks(fireDamageTicks);
                break;
            case POISON:
                PlayerEffect.BLINDNESS.applyEffect(target, poistonTicks, 1);
                PlayerEffect.SLOW.applyEffect(target, poistonTicks, 2);
                PlayerEffect.POISION.applyEffect(target, poistonTicks, 2);
                break;
            case HEAL:
                if (target.getHealth() < 20.0D) {
                    target.setHealth(Math.min(20.D, target.getHealth() + healOtherAmount));
                    target.getHandle().setFireTicks(0);
                    PlayerEffect.POISION.removeEffect(target);

                    weilder.setHealth(Math.min(20.0D, weilder.getHealth() + healSelfAmount));
                    PlayerEffect.POISION.removeEffect(weilder);
                    weilder.getHandle().setFireTicks(0);
                }
                break;
        }
    }

    public static boolean handlePlayerAttackPlayer(IdpPlayer attacker, PlayerClass attackerClass, IdpMaterial weapon, IdpPlayer defender, PlayerClass defenderClass, boolean sameTeam) {
        if (attackerClass == null || attackerClass.getClassType() == PlayerClassType.NONE
                || defenderClass == null || defenderClass.getClassType() == PlayerClassType.NONE
                || defender.getSession().isInPvPState()) {
            return false;
        }

        if (sameTeam) {
            handleBonusAttackEffect(attackerClass.getAttackBonusType(weapon, sameTeam), attacker, defender);
            return false;
        } else {
            defenderClass.handleHit(defender);
            double newHealth = Math.max(0.0D, defender.getHealth() - getAttackDamage(attackerClass, weapon, defenderClass, defender.getHandle().isBlocking()));
            defender.getHandle().playEffect(EntityEffect.HURT);
            if (newHealth == 0) {
                defender.getSession().setDeathMessage(defender.getName() + " was murdered by " + attacker.getName());
            } else {
                handleBonusAttackEffect(attackerClass.getAttackBonusType(weapon, sameTeam), attacker, defender);
            }
            defender.setHealth(newHealth);
            defender.getSession().setPvPImmuneTime(0.5);
            return (newHealth == 0);
        }
    }

    public static boolean handleBonusEffect(IdpPlayer player, PlayerClassBonusType effectType, List<IdpPlayer> nearbyTeam, List<IdpPlayer> nearbyEnemy) {
        IdpGameManager gameManager = IdpGameManager.getInstance();

        switch (effectType) {
            case STOMP:
                if (nearbyEnemy.size() > 0) {
                    Vector playerVector = player.getLocation().toVector();
                    Vector addVector = new Vector(0f, knockbackHeight, 0f);

                    for (IdpPlayer target : nearbyEnemy) {
                        Vector targVector = target.getLocation().toVector();
                        Vector targetVelocity = target.getHandle().getVelocity();

                        target.getHandle().setVelocity(targetVelocity.add(targVector.subtract(playerVector).add(addVector).normalize().multiply(knockbackDistance)));
                        target.print(ChatColor.LIGHT_PURPLE, player.getName() + " stomped!");
                    }

                    // Notify the Player
                    player.printInfo("You use your " + ChatColor.AQUA + "Stomping Boots" + ChatColor.GREEN + "!");

                    return true;
                } else {
                    player.printError("There are no enemies nearby!");
                    return false;
                }
            case SPEED:

                // Apply Speed
                PlayerEffect.SPEED.applyEffect(player, 400, 2);

                // Notify the Player
                player.printInfo("You use your " + ChatColor.AQUA + "speed" + ChatColor.GREEN + " power!");

                // Apply Slow afterwards.
                final IdpPlayer finalSpeedPlayer = player;
                player.getSession().setActiveTask(InnPlugin.getPlugin().getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 20 * 1000, 1) {
                    public void run() {
                        // Check player is online and still playing.
                        if (finalSpeedPlayer.isOnline() && gameManager.isInGame(finalSpeedPlayer)) {

                            // Switch to slowness.
                            PlayerEffect.SPEED.removeEffect(finalSpeedPlayer);
                            PlayerEffect.SLOW.applyEffect(finalSpeedPlayer, 200, 2);

                            finalSpeedPlayer.getSession().setActiveTask(0);
                        }
                    }
                }));
                return true;
            case INVISIBLE:

                // Cancel previous armour task.
                if (player.getSession().getActiveTask() != 0) {
                    InnPlugin.getPlugin().getTaskManager().removeTask(player.getSession().getActiveTask());
                    player.getSession().setActiveTask(0);
                }

                // Apply Invisible
                PlayerEffect.INVISIBILITY.applyEffect(player, 400, 1);

                // Save the armour
                final IdpItemStack helmet = player.getHelmet();
                final IdpItemStack chestplate = player.getChestplate();
                final IdpItemStack leggings = player.getLeggings();
                final IdpItemStack boots = player.getBoots();

                // Remove Armour
                player.setHelmet(new IdpItemStack(IdpMaterial.AIR, 1));
                player.setChestplate(new IdpItemStack(IdpMaterial.AIR, 1));
                player.setLeggings(new IdpItemStack(IdpMaterial.AIR, 1));
                player.setBoots(new IdpItemStack(IdpMaterial.AIR, 1));

                // Notify the Player
                player.printInfo("You use your " + ChatColor.AQUA + "invisible" + ChatColor.GREEN + " power!");

                // Re-add armour
                final IdpPlayer finalInvisiblePlayer = player;
                player.getSession().setActiveTask(InnPlugin.getPlugin().getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 20 * 1000, 1) {
                    public void run() {
                        // Check player is online and still playing.
                        if (finalInvisiblePlayer.isOnline() && gameManager.isInGame(finalInvisiblePlayer)) {

                            // Restore their inventory.
                            finalInvisiblePlayer.setHelmet(helmet);
                            finalInvisiblePlayer.setChestplate(chestplate);
                            finalInvisiblePlayer.setLeggings(leggings);
                            finalInvisiblePlayer.setBoots(boots);

                            // Remove effect.
                            PlayerEffect.INVISIBILITY.removeEffect(finalInvisiblePlayer);

                            finalInvisiblePlayer.printInfo("Your " + ChatColor.AQUA
                                    + "invisible" + ChatColor.GREEN + " power has expired!");

                            // Ensure we remove from the task list.
                            finalInvisiblePlayer.getSession().setActiveTask(0);
                        }
                    }
                }));
                return true;
            case POISON:
                player.getHandle().launchProjectile(Snowball.class);
                return true;
            case EGG:
                player.getHandle().launchProjectile(Egg.class);
                return true;
            case EMPOWER:
                if (nearbyTeam.size() > 0) {
                    nearbyTeam.add(player);
                    for (IdpPlayer target : nearbyTeam) {
                        PlayerEffect.RESISTANCE.applyEffect(target, empowerTicks, 3);
                        PlayerEffect.JUMP_BOOST.applyEffect(target, empowerTicks, 3);
                        PlayerEffect.WATER_BREATHING.applyEffect(target, empowerTicks, 3);
                        PlayerEffect.NIGHT_VISION.applyEffect(target, empowerTicks, 3);
                        PlayerEffect.REGENERATION.applyEffect(target, empowerTicks, 3);
                        PlayerEffect.SPEED.applyEffect(target, empowerTicks, 1);
                        target.print(ChatColor.LIGHT_PURPLE, player.getName() + " empowered you!");
                    }

                    // Notify the Player
                    player.printInfo("You use your " + ChatColor.AQUA + "Empowering Dust" + ChatColor.GREEN + "!");

                    return true;
                } else {
                    player.printError("There are no allies nearby!");
                    return false;
                }
        }
        return false;
    }

    public static PlayerClassType getRandomClassType() {
        return PlayerClassType.lookup((new Random()).nextInt(PlayerClassType.values().length - 1) + 1);
    }

}

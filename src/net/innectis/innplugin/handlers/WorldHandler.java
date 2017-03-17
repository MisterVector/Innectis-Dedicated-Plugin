package net.innectis.innplugin.handlers;

import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.InventorySwitchException;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.system.game.IdpGameManager;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Achievement;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Hret
 */
public final class WorldHandler {

    /**
     * This will do action if the player warps between worlds. The transfer can
     * be denied! So if it returns false, the teleport is denied!
     *
     * @param worldFrom
     * @param worldTo
     * @param player
     * @param plugin
     */
    public static void onWorldSwitch(IdpWorld worldFrom, IdpWorld worldTo, IdpPlayer player, InnPlugin plugin) {
        if (!player.getSession().isNewPlayer()) {
            try {
                player.switchInventory(worldTo.getSettings().getInventoryType());
            } catch (InventorySwitchException ex) {
                InnPlugin.logError("Cannot switch inventory for player " + player.getColoredName(), ex);
            }

            onWorldLeave(worldFrom, player);
            onWorldEnter(worldTo, player);

            // Make sure to unload all chunks when the player leaves this world
            if (worldFrom.getPlayerCount() == 0) {
                World bukkitWorld = worldFrom.getHandle();

                for (Chunk c : bukkitWorld.getLoadedChunks()) {
                    bukkitWorld.unloadChunk(c);
                }
            }

            final IdpPlayer delayedPlayer = player;
            plugin.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 1000, 1) {
                public void run() {
                    if (delayedPlayer.isOnline()) {
                        delayedPlayer.getInventory().updateBukkitInventory();
                    }
                }
            });
        }
    }

    /**
     * Check if the player is allowed to switch between the worlds
     *
     * @param worldFrom
     * @param worldTo
     * @param player
     * @return false if world switch is denied!
     */
    public static boolean isSwitchAllowed(IdpWorld worldFrom, IdpWorld worldTo, IdpPlayer player) {
        // Check if world is supported!
        if (worldTo.getWorldType() == IdpWorldType.NONE) {
            // Do not allow teleportation to unknown worlds!
            return false;
        }
        // TODO: Dynamic world hardcore check.

        return true;
    }

    /**
     * Used when a player leaves a world
     *
     * @param world
     * @param player
     */
    public static void onWorldLeave(IdpWorld world, IdpPlayer player) {
        // Handle creative world differently
        if (world.getWorldType() == IdpWorldType.CREATIVEWORLD) {
            // Leave the game mode alone of those able to change it
            if (!player.hasPermission(Permission.special_ignore_creative_world_mode)) {
                Player bukkitPlayer = player.getHandle();

                bukkitPlayer.setGameMode(GameMode.SURVIVAL);
                bukkitPlayer.setFallDistance(0);

                // Remove all potion effects when leaving this world
                for (PotionEffect type : bukkitPlayer.getActivePotionEffects()) {
                    bukkitPlayer.removePotionEffect(type.getType());
                }
            }

            // If they have their flight setting on, toggle flight
            if (player.getSession().hasFlightMode()) {
                player.setAllowFlight(true);
            }
        } else {
            // Leave players alone who can fly on their own
            if (!player.hasPermission(Permission.special_noflight_override)) {
                boolean flyCheck = false;

                // If we left a world with flight disabled and the player has flight mode
                // enabled then re-enable their flight
                if (!world.getSettings().isFlightAllowed()) {
                    if (player.getSession().hasFlightMode()) {
                        player.setAllowFlight(true);
                        flyCheck = true;
                    }
                }

                if (!flyCheck) {
                    // Always disable flight when going out of a world here
                    // if fly check hasn't been done yet and the player
                    // does not have default flight permissions
                    if (player.getAllowFlight() && !player.hasPermission(Permission.special_has_flight)) {
                        player.setAllowFlight(false);
                        player.getHandle().setFallDistance(0);
                        player.printError("Your flying ability was removed!");
                    }
                }
            }
        }
    }

    /**
     * Used when a player enters a world
     *
     * @param world
     * @param player
     */
    public static void onWorldEnter(IdpWorld world, IdpPlayer player) {
        boolean doFly = false;

        switch (world.getWorldType()) {
            case DYNAMIC:
                player.print(ChatColor.AQUA, "You have entered a temporary world. Your inventory here is not "
                    + "the same as it is in the normal world.");

                break;
            case AETHER:
                player.print(ChatColor.AQUA, "Welcome to the Aether, the world in the Sky!");
                doFly = true;

                break;

            case CREATIVEWORLD:
                player.print(ChatColor.AQUA, "Welcome to creative world!");

                // Leave the game mode alone of those able to change it
                if (!player.hasPermission(Permission.special_ignore_creative_world_mode)) {
                    player.getHandle().setGameMode(GameMode.CREATIVE);
                }

                // Nothing else needs to be processed
                return;
            case NETHER:
                Player bukkitPlayer = player.getHandle();

                // This fixes an issue in CraftBukkit where this achievement cannot be given otherwise
                if (!bukkitPlayer.hasAchievement(Achievement.NETHER_PORTAL)
                        && bukkitPlayer.hasAchievement(Achievement.NETHER_PORTAL.getParent())) {
                    bukkitPlayer.awardAchievement(Achievement.NETHER_PORTAL);
                }
        }

        // Modify flight ability for those who cannot fly on their own and the
        // world is forcing flight on the player
        if (!player.hasPermission(Permission.special_noflight_override)) {
            if (doFly) {
                // If this player does not have flight or flight mode, let them fly
                if (!(player.getAllowFlight() || player.getSession().hasFlightMode())) {
                    player.setAllowFlight(true);
                    player.print(ChatColor.AQUA, "You feel like you can fly!");
                }
            // If this world has no flight allowed then disable flight if
            // the player has flight mode enabled
            } else if (!world.getSettings().isFlightAllowed()) {
                if (player.getAllowFlight() || player.getSession().hasFlightMode()) {
                    player.setAllowFlight(false);
                }
            }
        }
    }

    public static void reloadHidden() {
        List<IdpPlayer> onlinePlayers = InnPlugin.getPlugin().getOnlinePlayers();
        InnPlugin plugin = InnPlugin.getPlugin();
        Team hiddenPlayersTeam = plugin.getHiddenNametagPlayersTeam();
        Team vanishedPlayersTeam = plugin.getVannishedPlayersTeam();
        IdpGameManager gameManager = IdpGameManager.getInstance();

        for (IdpPlayer onlinePlayer : onlinePlayers) {
            PlayerSession session = onlinePlayer.getSession();
            Player bukkitOnlinePlayer = onlinePlayer.getHandle();
            String playerName = bukkitOnlinePlayer.getName();

            // Hide nametags if appropriate.
            if (!session.isShowingNametag()) {
                if (!hiddenPlayersTeam.hasEntry(playerName)) {
                    bukkitOnlinePlayer.setScoreboard(hiddenPlayersTeam.getScoreboard());
                    hiddenPlayersTeam.addEntry(playerName);
                }
            } else if (hiddenPlayersTeam.hasEntry(playerName)) {
                hiddenPlayersTeam.removeEntry(playerName);
            }

            // Allow hidden players to see each other. (unless hidden nametag)
            if (onlinePlayer.hasPermission(Permission.spoofing_see_hidden) && onlinePlayer.getSession().isShowingNametag()) {
                if (!vanishedPlayersTeam.hasEntry(playerName)) {
                    bukkitOnlinePlayer.setScoreboard(vanishedPlayersTeam.getScoreboard());
                    vanishedPlayersTeam.addEntry(playerName);
                }
            } else if (vanishedPlayersTeam.hasEntry(playerName)) {
                vanishedPlayersTeam.removeEntry(playerName);
            }

            for (IdpPlayer targetPlayer : onlinePlayers) {
                PlayerSession targetPlayerSession = targetPlayer.getSession();
                Player bukkitTargetPlayer = targetPlayer.getHandle();
                boolean onlinePlayerInGame = gameManager.isInGame(onlinePlayer);
                boolean targetPlayerInGame = gameManager.isInGame(targetPlayer);

                if ((!targetPlayerSession.isVisible() && !onlinePlayer.hasPermission(Permission.spoofing_see_hidden))
                        || (onlinePlayerInGame && targetPlayerSession.isInLobby())
                        || (onlinePlayerInGame && !targetPlayerInGame)) {
                    bukkitOnlinePlayer.hidePlayer(bukkitTargetPlayer);
                } else {
                    InnectisLot lot = LotHandler.getLot(targetPlayer.getLocation());

                    if ((lot != null && lot.isFlagSet(LotFlagType.INVISIBLE))) {
                        bukkitOnlinePlayer.hidePlayer(bukkitTargetPlayer);
                    } else {
                        bukkitOnlinePlayer.showPlayer(bukkitTargetPlayer);
                    }
                }
            }
        }
    }

}

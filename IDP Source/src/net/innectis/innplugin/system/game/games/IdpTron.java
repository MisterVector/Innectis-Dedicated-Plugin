package net.innectis.innplugin.system.game.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventPriority;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDropItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerMoveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPickupItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerQuitEvent;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.system.game.IdpGameType;
import net.innectis.innplugin.system.game.IdpRegionGame;
import net.innectis.innplugin.system.game.IdpStartResult;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * A game representing tron
 *
 * @author Nosliw (converted to normal game system by AlphaBlend)
 */
public class IdpTron extends IdpRegionGame {

    private boolean running, terminating;
    private Map<Block, String> lightBlocks;
    private List<IdpPlayer> players = new ArrayList<IdpPlayer>();
    private List<String> playerNames = new ArrayList<String>();
    private final int MIN_PLAYERS = 2;
    private final IdpMaterial lightMaterial = IdpMaterial.GLASS_PANE;

    public IdpTron(InnectisLot gameLot, String gameHoster) {
        super(gameLot, gameHoster);
        lightBlocks = new HashMap<Block, String>();
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.TRON;
    }

    @Override
    public IdpStartResult canStart() {
        IdpWorldRegion region = super.getRegion();

        if (!(region instanceof InnectisLot)) {
            return new IdpStartResult(false, "This game must be started on a lot.");
        }

        List<IdpPlayer> regionPlayers = region.getPlayersInsideRegion();

        if (regionPlayers.size() < MIN_PLAYERS) {
            return new IdpStartResult(false, "There must be at least " + MIN_PLAYERS + " to start!");
        }

        players.addAll(regionPlayers);

        for (IdpPlayer player : regionPlayers) {
            playerNames.add(player.getName());
        }

        return new IdpStartResult(true);
    }

    @Override
    public void startGame() {
        InnPlugin.getPlugin().getTaskManager().addTask(new CountdownTask());

        for (IdpPlayer player : players) {
            initializePlayer(player);
        }
    }

    @Override
    public void endGame() {
        super.endGame();

        terminating = true;

        // Remove the light blocks
        for (Block block : lightBlocks.keySet()) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat == lightMaterial) {
                BlockHandler.setBlock(block, IdpMaterial.AIR);
            }
        }

        String endMsg = ChatColor.AQUA + "The game of tron has ended!";

        if (players.size() == 1) {
            IdpPlayer winner = players.get(0);
            endMsg += " the winner is " + winner.getColoredDisplayName() + ChatColor.AQUA + "!";
        }

        for (String name : playerNames) {
            IdpPlayer player = InnPlugin.getPlugin().getPlayer(name);

            if (player != null && player.isOnline()) {
                player.printInfo(endMsg);
            }
        }
    }

    @Override
    public void printScore(IdpCommandSender sender) {
        String infoString = "";

        for (IdpPlayer player : players) {
            if (!infoString.isEmpty()) {
                infoString += ChatColor.WHITE + ", ";
            }

            infoString += player.getColoredDisplayName();
        }

        sender.printInfo("Players still in Tron (game #" + super.getId() + "): " + infoString);

        if (!running) {
            sender.printInfo("Game is still counting down!");
        }
    }

    @Override
    public boolean addPlayer(IdpPlayer player) {
        return false;
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @InnEventMarker(type = InnEventType.PLAYER_MOVE, priority = InnEventPriority.HIGH)
    public void handlePlayerMove(InnPlayerMoveEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        if (!running) {
            return;
        }

        Block block = event.getTo().getBlock().getRelative(BlockFace.UP);

        // Check if the player has hit a wall..
        for (int i = 0; i < 3; i++) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat == lightMaterial) {
                lightKillPlayer(player, block);
                removePlayer(player);
                return;
            }

            block = block.getRelative(BlockFace.DOWN);
        }

        InnPlugin.getPlugin().getTaskManager().addTask(new LightBlockTask(event.getTo().getBlock(), player.getName()));
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_LOT_LEAVE, priority = InnEventPriority.HIGH)
    public void handleLeave(InnPlayerLotLeaveEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        removePlayer(event.getPlayer());
    }

    @InnEventMarker(type = InnEventType.ENTITY_ENVIRONMENTAL_DAMAGE, priority = InnEventPriority.HIGH)
    public void onPlayerDamage(InnEntityDamageEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PLAYER, priority = InnEventPriority.HIGH)
    public void handlePlayerDamageByPlayer(InnPlayerDamageByPlayerEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PROJECTILE, priority = InnEventPriority.HIGH)
    public void handlePlayerDamageByProjectile(InnPlayerDamageByProjectileEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_PICKUP_ITEM, priority = InnEventPriority.HIGH)
    public void handlePlayerPickupItem(InnPlayerPickupItemEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DROP_ITEM, priority = InnEventPriority.HIGH)
    public void handlePlayerDropItem(InnPlayerDropItemEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_QUIT, priority = InnEventPriority.HIGH)
    public void handlePlayerLogout(InnPlayerQuitEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        removePlayer(player);
    }

    private void lightKillPlayer(IdpPlayer player, Block block) {
        if (player.getHealth() > 0) {
            if (!lightBlocks.containsKey(block)) {
                sendGameMessage(player.getColoredDisplayName() + ChatColor.GREEN + " smashed into a light wall!");
            } else {
                String wallOwner = lightBlocks.get(block);

                if (wallOwner.equalsIgnoreCase(player.getName())) {
                    sendGameMessage(player.getColoredDisplayName() + ChatColor.GREEN + " smashed into their own light wall!");
                } else {
                    sendGameMessage(player.getColoredDisplayName() + ChatColor.GREEN + " smashed into " + wallOwner + "'s light wall!");

                    if (!isWallOwnerPlaying(wallOwner)) {
                        IdpPlayer wallPlayer = InnPlugin.getPlugin().getPlayer(wallOwner);

                        if (wallPlayer != null) {
                            wallPlayer.printInfo(player.getColoredDisplayName(), " smashed into your light wall!");
                        }
                    }
                }
            }

            player.getLocation().getWorld().createExplosion(player.getLocation(), 0);
        }
    }

    /**
     * Initial start of the game after the countdown
     */
    private void handleStart() {
        running = true;

        sendGameMessage(ChatColor.AQUA + "You start up your light engine!");
    }

    /**
     * Initializes the player for tron
     * @param player
     */
    private void initializePlayer(IdpPlayer player) {
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
        PlayerEffect.JUMP_BOOST.applySpecial(player, 20000, 190);
    }

    /**
     * Removes the player from the game
     * @param player
     */
    private void removePlayer(IdpPlayer player) {
        players.remove(player);
        player.setInventory(player.getWorld().getSettings().getInventoryType());
        PlayerEffect.JUMP_BOOST.removeSpecial(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    /**
     * Checks if the specified wall owner is playing
     * @param wallOwner
     * @return
     */
    private boolean isWallOwnerPlaying(String wallOwner) {
        for (IdpPlayer player : players) {
            if (player.getDisplayName().equalsIgnoreCase(wallOwner)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sends a message to every player in the game
     * @param message
     */
    private void sendGameMessage(String message) {
        for (IdpPlayer player : players) {
            player.printInfo(message);
        }
    }

    /**
     * @author Nosliw
     *
     * Task that will count down and auto-start the game.
     */
    private class CountdownTask extends LimitedTask {

        public CountdownTask() {
            super(RunBehaviour.SYNCED, 1000, 11);
        }

        @Override
        public void run() {
            // Don't do anything if game is terminating
            if (terminating) {
                this.executecount = 0;
                return;
            }

            if (this.executecount == 0) {
                sendGameMessage(ChatColor.AQUA + "Game Starting!");
                handleStart();
            } else if (this.executecount == 10 || this.executecount <= 5) {
                sendGameMessage(ChatColor.AQUA + "Game starting in " + this.executecount + " seconds!");
            }
        }

    }

    private class LightBlockTask extends LimitedTask {

        private Block targetBlock;
        private String playerName;

        public LightBlockTask(Block targetBlock, String playerName) {
            super(RunBehaviour.SYNCED, 750, 1);
            this.targetBlock = targetBlock;
            this.playerName = playerName;
        }

        @Override
        public void run() {
            // Don't do anything if game is terminating
            if (terminating) {
                return;
            }

            // Create the light effect.
            IdpMaterial mat = IdpMaterial.fromBlock(targetBlock);
            List<IdpPlayer> removePlayers = new ArrayList<IdpPlayer>();

            if (mat == IdpMaterial.AIR) {
                BlockHandler.setBlock(targetBlock, lightMaterial);
                lightBlocks.put(targetBlock, playerName);

                for (IdpPlayer player : players) {
                    Location centerLoc = LocationUtil.getCenterLocation(player.getLocation());
                    Location centerTargetLoc = LocationUtil.getCenterLocation(targetBlock.getLocation());

                    if (centerLoc.distance(centerTargetLoc) < 0.5) {
                        lightKillPlayer(player, targetBlock);
                        removePlayers.add(player);
                    }
                }
            }

            targetBlock = targetBlock.getRelative(BlockFace.UP);
            mat = IdpMaterial.fromBlock(targetBlock);

            if (mat == IdpMaterial.AIR) {
                BlockHandler.setBlock(targetBlock, lightMaterial);
                lightBlocks.put(targetBlock, playerName);

                for (IdpPlayer player : players) {
                    Location centerLoc = LocationUtil.getCenterLocation(player.getLocation());
                    Location centerTargetLoc = LocationUtil.getCenterLocation(targetBlock.getLocation());

                    if (centerLoc.distance(centerTargetLoc) < 0.5) {
                        lightKillPlayer(player, targetBlock);
                        removePlayers.add(player);
                    }
                }
            }

            for (IdpPlayer player : removePlayers) {
                removePlayer(player);
            }
        }

    }

}

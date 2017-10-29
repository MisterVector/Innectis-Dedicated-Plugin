package net.innectis.innplugin.system.game.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockBreakEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockPlaceEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDeathEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerFoodLevelChangeEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerMoveEvent;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.system.game.IdpGameType;
import net.innectis.innplugin.system.game.IdpRegionGame;
import net.innectis.innplugin.system.game.IdpStartResult;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * A game representing the Hunger Games movies
 *
 * @author AlphaBlend
 */
public class IdpHungerGames extends IdpRegionGame {

    private static final int MIN_PLAYERS = 4;
    private static final String HUNGER_GAMES_OBJECTIVE = "HungerGames";
    private static final String HUNGER_GAMES_TRIBUTES_LEFT = ChatColor.BLUE + "Tributes Left";

    private Map<Integer, IdpInventory> chestInventories = new HashMap<Integer, IdpInventory>();
    private List<IdpPlayer> players = new ArrayList<IdpPlayer>();
    private List<String> playerNames = new ArrayList<String>();
    private List<Location> startPoints = new ArrayList<Location>();
    private boolean started = false;
    private boolean terminating = false;

    private Scoreboard gameScoreboard;
    private Score tributesLeft;

    public IdpHungerGames(InnectisLot gamelot, String gameHost) {
        super(gamelot, gameHost);
        gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.HUNGERGAMES;
    }

    @Override
    public IdpStartResult canStart() {
        IdpWorldRegion region = super.getRegion();

        if (!(region instanceof InnectisLot)) {
            return new IdpStartResult(false, "This game must be started on a lot.");
        }

        List<IdpPlayer> regionPlayers = region.getPlayersInsideRegion();

        if (regionPlayers.size() < MIN_PLAYERS) {
            return new IdpStartResult(false, "This game needs a minimum of " + MIN_PLAYERS + " players to start.");
        }

        players.addAll(regionPlayers);

        for (IdpPlayer player : players) {
            playerNames.add(player.getName());
        }

        InnectisLot gamelot = (InnectisLot) region;

        for (InnectisLot sublot : gamelot.getSublots()) {
            if (sublot.getLotName().equalsIgnoreCase("startpoint")
                    && sublot.getArea() == 1) {
                startPoints.add(sublot.getSpawn());
            }
        }

        int startPointCount = startPoints.size();
        int playerCount = players.size();

        if (startPointCount < MIN_PLAYERS) {
            return new IdpStartResult(false, "This game must have a minimum of " + MIN_PLAYERS + " start points.");
        }

        if (playerCount > startPointCount) {
            return new IdpStartResult(false, "Players (" + playerCount + ") outnumber start points (" + startPointCount + "). Cannot start!");
        }

        return new IdpStartResult(true);
    }

    @Override
    public void startGame() {
         // Get all original inventories from the chests in the game
        Map<Integer, InnectisChest> chests = ChestHandler.getChests();
        IdpWorldRegion region = super.getRegion();

        for (InnectisChest chest : chests.values()) {
            if (region.contains(chest)) {
                chestInventories.put(chest.getId(), chest.getInventory());
            }
        }

        Objective obj = gameScoreboard.registerNewObjective(HUNGER_GAMES_OBJECTIVE, "dummy");
        obj.setDisplayName(ChatColor.YELLOW + "Hunger Games");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        tributesLeft = obj.getScore(HUNGER_GAMES_TRIBUTES_LEFT);
        tributesLeft.setScore(players.size());

        for (IdpPlayer player : players) {
            initializePlayer(player);
        }

        int idx = 0;

        for (IdpPlayer player : players) {
            player.teleport(startPoints.get(idx++));
        }

        InnPlugin.getPlugin().getTaskManager().addTask(new CountdownTask());
    }

    @Override
    public void endGame() {
        super.endGame();

        terminating = true;

        // Restore the original chest inventories when game ends
        for (Map.Entry<Integer, IdpInventory> entry : chestInventories.entrySet()) {
            int id = entry.getKey();
            IdpInventory inventory = entry.getValue();
            InnectisChest chest = ChestHandler.getChest(id);
            chest.setInventory(inventory);
        }

        for (IdpPlayer player : players) {
            resetPlayer(player);
        }

        String winningPlayer = "";

        if (players.size() == 1) {
            winningPlayer = players.get(0).getColoredDisplayName();
        }

        for (String name : playerNames) {
            IdpPlayer player = InnPlugin.getPlugin().getPlayer(name);

            if (player != null && player.isOnline()) {
                player.print(ChatColor.AQUA, "The game of hunger games has ended!");

                if (!winningPlayer.isEmpty()) {
                    player.print(ChatColor.AQUA, "The winner is " + winningPlayer, "!");
                }
            }
        }
    }

    @Override
    public void printScore(IdpCommandSender sender) {
        String playersLeft = "";

        for (IdpPlayer player : players) {
            if (!playersLeft.isEmpty()) {
                playersLeft += ChatColor.WHITE + ", ";
            }

            playersLeft += player.getColoredDisplayName();
        }

        sender.print(ChatColor.AQUA, "Players left in Hunger Games (game ID #" + super.getId() + "): " + playersLeft);
    }

    @Override
    public boolean addPlayer(IdpPlayer player) {
        return false;
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @InnEventMarker(type = InnEventType.PLAYER_MOVE)
    public void onPlayerMove(InnPlayerMoveEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);

        if (!started) {
            event.setCancelled(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);

        if (!started) {
            event.setCancelled(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_BREAK)
    public void onBlockBreak(InnPlayerBlockBreakEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_PLACE)
    public void onBlockPlace(InnPlayerBlockPlaceEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_FOOD_LEVEL_CHANGE)
    public void onPlayerFoodLevelChange(InnPlayerFoodLevelChangeEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        // End here for everyone, don't apply Goldy hunger rule
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PLAYER)
    public void onPlayerDamageByPlayer(InnPlayerDamageByPlayerEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);

        if (!started) {
            event.setCancelled(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PROJECTILE)
    public void onPlayerDamageByProjectile(InnPlayerDamageByProjectileEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);

        if (!started) {
            event.setCancelled(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DEATH)
    public void onPlayerDeath(InnPlayerDeathEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);

        for (IdpPlayer p : players) {
            p.getHandle().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1.5f, 1.0f);
        }

        resetPlayer(player);
        players.remove(player);
        player.print(ChatColor.AQUA, "You have been killed! Game over!");
        tributesLeft.setScore(tributesLeft.getScore() - 1);

        sendGameMessage(player.getColoredDisplayName(), " has been eliminated!");

        if (players.size() == 1) {
            endGame();
        }
    }

    /**
     * Initializes the player for this game
     * @param player
     */
    private void initializePlayer(IdpPlayer player) {
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
        player.getSession().setScoreboard(gameScoreboard);
    }

    /**
     * Resets the player for this game
     * @param player
     */
    private void resetPlayer(IdpPlayer player) {
        player.getSession().resetScoreboard();
    }

    /**
     * Sends a game message to all players
     * @param messages
     */
    private void sendGameMessage(String... messages) {
        sendGameMessage(ChatColor.AQUA, messages);
    }

    /**
     * Sends a game message to all players
     * @param color
     * @param messages
     */
    private void sendGameMessage(ChatColor color, String... messages) {
        for (IdpPlayer player : players) {
            player.print(color, messages);
        }
    }

    private class CountdownTask extends LimitedTask {

        public CountdownTask() {
            super(RunBehaviour.SYNCED, 1000, 16);
        }

        @Override
        public void run() {
             if (terminating) {
                 this.executecount = 0;
                 return;
             }

            if (this.executecount == 0) {
                sendGameMessage("Let the games begin!");
                started = true;
            } else if (this.executecount == 15 || this.executecount == 10 ||this.executecount <= 5) {
                sendGameMessage("Game will start in " + this.executecount + " seconds!");
            }
        }

    }

}

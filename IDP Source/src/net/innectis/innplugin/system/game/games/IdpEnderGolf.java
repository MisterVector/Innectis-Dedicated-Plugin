package net.innectis.innplugin.system.game.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockBreakEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockPlaceEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerMoveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerQuitEvent;
import net.innectis.innplugin.listeners.idp.InnProjectileHitEvent;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.InnectisLot;
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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * A game where you hit flags with ender pearls
 *
 * @author AlphaBlend
 */
public class IdpEnderGolf extends IdpRegionGame {

    private static final int MIN_PLAYERS = 4;
    private static final int MAX_PLAYERS = 15;
    private static final int MIN_FLAG_COUNT = 3;
    private static final int FLAG_HEIGHT = 3;
    private static final int COOLDOWN_TIME = 650;
    private static final String GAME_OBJECTIVE = "GolfObjective";
    private static final String PROJECTILE_META = "GolfBall";
    private static final String LOT_SPAWNZONE_NAME = "spawnzone";
    private static final String LOT_FLAG_NAME = "flag";
    private static final IdpMaterial FLAG_BASE_MATERIAL = IdpMaterial.NETHER_BRICK_FENCE;
    private static final IdpMaterial FLAG_TOP_MATERIAL = IdpMaterial.WOOL_YELLOW;
    private static final IdpMaterial PLAYER_GOLF_MATERIAL = IdpMaterial.IRON_HOE;

    private int lastFlagIndex = -1;
    private int winLimit;
    private boolean started, terminating;
    private List<IdpPlayer> players = new ArrayList<IdpPlayer>();
    private Map<IdpPlayer, Long> cooldowns = new HashMap<IdpPlayer, Long>();
    private List<Location> flagLocations = new ArrayList<Location>();
    private Location[] flag = new Location[FLAG_HEIGHT];
    private InnectisLot spawnZone = null;
    private Scoreboard gameScoreboard;
    private Random random;

    public IdpEnderGolf(InnectisLot gamelot, String gameHost, int winLimit) {
        super(gamelot, gameHost);
        this.winLimit = winLimit;
        random = new Random(System.currentTimeMillis());
        gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.ENDERGOLF;
    }

    @Override
    public IdpStartResult canStart() {
        IdpWorldRegion region = super.getRegion();

        if (!(region instanceof InnectisLot)) {
            return new IdpStartResult(false, "This game must be started on a lot.");
        }

        List<IdpPlayer> regionPlayers = region.getPlayersInsideRegion();
        int playerSize = regionPlayers.size();

        if (playerSize < MIN_PLAYERS) {
            return new IdpStartResult(false, "This game needs at least " + MIN_PLAYERS + " to play.");
        }

        if (playerSize > MAX_PLAYERS) {
            return new IdpStartResult(false, "This game supports " + MAX_PLAYERS + " players maximum.");
        }

        players.addAll(regionPlayers);

        InnectisLot gamelot = (InnectisLot) region;

        for (InnectisLot sublot : gamelot.getSublots()) {
            if (sublot.getLotName().equalsIgnoreCase(LOT_FLAG_NAME)
                    && sublot.getArea() == 1) {
                flagLocations.add(sublot.getSpawn());
            } else if (sublot.getLotName().equalsIgnoreCase(LOT_SPAWNZONE_NAME)) {
                spawnZone = sublot;
            }
        }

        if (flagLocations.size() < MIN_FLAG_COUNT) {
            return new IdpStartResult(false, "This game needs " + MIN_FLAG_COUNT + " or more flags.");
        }

        if (spawnZone == null) {
            return new IdpStartResult(false, "This game needs a spawn zone.");
        }

        return new IdpStartResult(true);
    }

    @Override
    public void startGame() {
        Objective obj = gameScoreboard.registerNewObjective(GAME_OBJECTIVE, "dummy");
        obj.setDisplayName(ChatColor.YELLOW + "Ender Golf (" + winLimit + " to win)");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (IdpPlayer player : players) {
            Score score = obj.getScore(player.getColoredDisplayName());
            score.setScore(0);

            initializePlayer(player);
            respawnPlayer(player);
        }

        InnPlugin.getPlugin().getTaskManager().addTask(new CountdownTask());
    }

    @Override
    public void endGame() {
        super.endGame();

        destroyFlag();
        sendGameMessage("The game of Ender Golf has ended!");

        terminating = true;

        if (!players.isEmpty()) {
            Objective obj = gameScoreboard.getObjective(GAME_OBJECTIVE);
            IdpPlayer winner = null;
            int winnerScore = -1;

            for (IdpPlayer player : players) {
                Score score = obj.getScore(player.getColoredDisplayName());
                int scoreValue = score.getScore();

                if (scoreValue > winnerScore) {
                    winner = player;
                    winnerScore = scoreValue;
                }
            }

            sendGameMessage(winner.getColoredDisplayName(), " has won the game!");

            players.clear();
        }
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public void printScore(IdpCommandSender sender) {
        Objective obj = gameScoreboard.getObjective(GAME_OBJECTIVE);
        List<String> messages = new ArrayList<String>();
        String tempString = "";

        sender.print(ChatColor.AQUA, "Printing scores for Ender Golf (game #" + super.getId() + ")");
        sender.print(ChatColor.AQUA, "");

        for (IdpPlayer player : players) {
            Score score = obj.getScore(player.getColoredDisplayName());
            boolean clear = false;

            if (!tempString.isEmpty()) {
                tempString += ChatColor.WHITE + ", ";
                clear = true;
            }

            tempString += player.getColoredDisplayName() + ChatColor.WHITE
                    + ": " + ChatColor.RED + score.getScore();

            if (clear) {
                messages.add(tempString);
                tempString = "";

                clear = false;
            }
        }

        if (!tempString.isEmpty()) {
            messages.add(tempString);
        }

        for (String msg : messages) {
            sender.printInfo(msg);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PLAYER)
    public void onPlayerDamageByPlayer(InnPlayerDamageByPlayerEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PROJECTILE)
    public void onPlayerDamageByProjectile(InnPlayerDamageByProjectileEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);

        if (!started) {
            return;
        }

        EquipmentSlot slot = player.getNonEmptyHand();

        if (slot == null) {
            return;
        }

        long diff = 0;

        if (cooldowns.containsKey(player)) {
            diff = (System.currentTimeMillis() - cooldowns.get(player));
        }

        if (diff > 0 && diff < COOLDOWN_TIME) {
            event.setCancelled(true);
            return;
        }

        cooldowns.put(player, System.currentTimeMillis());

        Snowball snowball = player.getHandle().launchProjectile(Snowball.class);
        snowball.setMetadata(PROJECTILE_META, new FixedMetadataValue(InnPlugin.getPlugin(), true));
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_BREAK)
    public void onBlockBreak(InnPlayerBlockBreakEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_BREAK)
    public void onBlockPlace(InnPlayerBlockPlaceEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
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

    @InnEventMarker(type = InnEventType.PROJECTILE_HIT)
    public void onProjectileHit(InnProjectileHitEvent event) {
        Projectile projectile = event.getProjectile();

        if (!projectile.hasMetadata(PROJECTILE_META)) {
            return;
        }

        Block hitBlock = event.getHitBlock();

        if (hitBlock == null) {
            return;
        }

        IdpPlayer shooter = InnPlugin.getPlugin().getPlayer(((Entity) projectile.getShooter()).getName());
        Location hitBlockLocation = hitBlock.getLocation();

        if (isFlagLocation(hitBlockLocation)) {
            String coloredName = shooter.getColoredDisplayName();
            Objective obj = gameScoreboard.getObjective(GAME_OBJECTIVE);
            Score score = obj.getScore(coloredName);
            int scoreValue = score.getScore() + 1;

            score.setScore(scoreValue);
            sendGameMessage(coloredName, " has hit a flag!");

            if (scoreValue == winLimit) {
                endGame();
            } else {
                respawnPlayer(shooter);
                buildFlag();
            }
        } else {
            InnectisLot gameLot = (InnectisLot) super.getRegion();

            if (gameLot.contains(hitBlockLocation)) {
                hitBlockLocation.add(0, 1, 0);

                int shooterY = shooter.getLocation().getBlockY();
                int targetY = hitBlockLocation.getBlockY();
                int diff = Math.abs(shooterY - targetY);

                if (diff == 0) {
                    hitBlockLocation.setYaw(shooter.getYaw());
                    hitBlockLocation.setPitch(shooter.getPitch());

                    shooter.teleport(hitBlockLocation);
                }
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_QUIT)
    public void onPlayerQuit(InnPlayerQuitEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        playerLeave(player);
    }

    @InnEventMarker(type = InnEventType.PLAYER_LOT_LEAVE)
    public void onPlayerLotLeave(InnPlayerLotLeaveEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        InnectisLot gamelot = (InnectisLot) super.getRegion();
        InnectisLot toLot = event.getTo();

        if (toLot != null && toLot.getParentTop() == gamelot) {
            return;
        }

        playerLeave(player);
    }

    /**
     * Initializes the player for this game
     * @param player
     */
    private void initializePlayer(IdpPlayer player) {
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
        player.getSession().setScoreboard(gameScoreboard);
        player.addItemToInventory(PLAYER_GOLF_MATERIAL, 1);
    }

    /**
     * Handles player leaving the game
     * @param player
     */
    private void playerLeave(IdpPlayer player) {
        sendGameMessage(player.getColoredDisplayName(), " has left the game!");
        removePlayer(player);

        if (players.isEmpty()) {
            endGame();
        }
    }

    /**
     * Removes player from the game
     * @param player
     */
    private void removePlayer(IdpPlayer player) {
        player.setInventory(player.getWorld().getSettings().getInventoryType());
        player.getSession().resetScoreboard();
        players.remove(player);
    }

    /**
     * Checks if the specified location is the same as
     * part of the flag
     * @param loc
     * @return
     */
    private boolean isFlagLocation(Location loc) {
        for (int i = 0; i < FLAG_HEIGHT; i++) {
            if (loc.getWorld().equals(flag[i].getWorld()) && loc.getBlockX() == flag[i].getBlockX()
                    && loc.getBlockY() == flag[i].getBlockY() && loc.getBlockZ() == flag[i].getBlockZ()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Builds a flag at a random flag location, after destroying
     * the previous flag if one was generated
     * @param loc
     */
    private void buildFlag() {
        if (lastFlagIndex > -1) {
            destroyFlag();
        }

        int randomIndex = random.nextInt(flagLocations.size());

        // Make sure the same flag location is not returned twice in a row
        if (lastFlagIndex > -1) {
            while (randomIndex == lastFlagIndex) {
                randomIndex = random.nextInt(flagLocations.size());
            }
        }

        lastFlagIndex = randomIndex;
        Location flagLocation = flagLocations.get(randomIndex).clone();

        for (int i = 0; i < FLAG_HEIGHT; i++) {
            if (i > 0) {
                flagLocation.add(0, 1, 0);
            }

            flag[i] = flagLocation.clone();
            IdpMaterial mat = (i == FLAG_HEIGHT - 1 ? FLAG_TOP_MATERIAL : FLAG_BASE_MATERIAL);
            Block block = flagLocation.getBlock();
            BlockHandler.setBlock(block, mat);
        }
    }

    /**
     * Destroys the flag
     */
    private void destroyFlag() {
        for (int i = 0; i < FLAG_HEIGHT; i++) {
            Block block = flag[i].getBlock();
            BlockHandler.setBlock(block, IdpMaterial.AIR);
        }
    }

    /**
     * Respawns the player to a random location on the spawn zone
     * @param player
     */
    private void respawnPlayer(IdpPlayer player) {
        Location spawnLocation = spawnZone.getSpawn().clone();

        spawnLocation.setX(spawnZone.getLowestX() + random.nextInt(spawnZone.getXLength()));
        spawnLocation.setZ(spawnZone.getLowestZ() + random.nextInt(spawnZone.getZLength()));

        player.teleport(spawnLocation);
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
                buildFlag();

                sendGameMessage("The game has started!");
                started = true;
            } else if (this.executecount == 15 || this.executecount == 10 ||this.executecount <= 5) {
                sendGameMessage("Game will start in " + this.executecount + " seconds!");
            }
        }

    }

}

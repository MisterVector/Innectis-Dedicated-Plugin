package net.innectis.innplugin.system.game.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDropItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPickupItemEvent;
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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author AlphaBlend
 */
public class IdpQuakeCraft extends IdpRegionGame {

    private static final String QUAKE_OBJECTIVE = "killedPlayers";
    private static final IdpMaterial RAIL_MATERIAL = IdpMaterial.WOOD_HOE;
    private static final int MIN_PLAYERS = 4;
    private static final int MAX_PLAYERS = 15;
    private static final long RAIL_COOLDOWN = 850;
    private List<IdpPlayer> players = new ArrayList<IdpPlayer>();
    private Map<UUID, Long> lastPlayerShot = new HashMap<UUID, Long>();
    private List<String> entries = new ArrayList<String>();
    private List<Location> spawns = new ArrayList<Location>();
    private InnPlugin plugin;
    private Scoreboard gameScoreboard;
    private Random random;
    private String projectileMetadata;
    private int winLimit;

    public IdpQuakeCraft(InnectisLot region, String gameHost, int winLimit) {
        super(region, gameHost);
        plugin = InnPlugin.getPlugin();
        gameScoreboard = InnPlugin.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        random = new Random(System.currentTimeMillis());
        this.winLimit = winLimit;
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.QUAKECRAFT;
    }

    @Override
    public IdpStartResult canStart() {
        IdpWorldRegion region = super.getRegion();

        if (!(region instanceof InnectisLot)) {
            return new IdpStartResult(false, "This game must be started in a lot.");
        }

        InnectisLot lot = (InnectisLot) region;
        List<IdpPlayer> playersInRegion = lot.getPlayersInsideRegion();
        int playersSize = playersInRegion.size();

        if (playersSize < MIN_PLAYERS) {
            return new IdpStartResult(false, "Not enough players to play the game!");
        }

        if (playersSize > MAX_PLAYERS) {
            return new IdpStartResult(false, "Too many players! Max of " + MAX_PLAYERS + " players allowed.");
        }

        for (InnectisLot sublot : lot.getSublots()) {
            if (sublot.getLotName().equalsIgnoreCase("spawnpoint")
                    && sublot.getArea() == 1) {
                spawns.add(sublot.getSpawn());
            }
        }

        if (spawns.isEmpty()) {
            return new IdpStartResult(false, "No spawn points set!");
        }

        players.addAll(playersInRegion);

        return new IdpStartResult(true);
    }

    @Override
    public void startGame() {
        initializeScoreboard();
        projectileMetadata = "quakeCraft" + super.getId();

        for (IdpPlayer player : players) {
            initializePlayer(player);
            teleportToNextSpawn(player);
        }

        broadcastMessage("Starting a game of QuakeCraft!");
        broadcastMessage("First to " + ChatColor.YELLOW + winLimit, " gibs wins!");
    }

    @Override
    public boolean addPlayer(IdpPlayer player) {
        if (players.size() == MAX_PLAYERS) {
            return false;
        }

        players.add(player);
        initializePlayer(player);

        return true;
    }

    @Override
    public void endGame() {
        super.endGame();

        for (IdpPlayer player : players) {
            resetPlayer(player);
        }

        Objective obj = gameScoreboard.getObjective(QUAKE_OBJECTIVE);
        String winnerColoredName = "";
        int winnerScore = 0;

        for (String entry : entries) {
            Score score = obj.getScore(entry);
            int scoreValue = score.getScore();

            if (scoreValue > winnerScore) {
                winnerColoredName = entry;
                winnerScore = scoreValue;
            }
        }

        broadcastMessage("The winner is ", winnerColoredName, " (" + winnerScore + " gibs)!");
        broadcastMessage("This game of quakecraft has ended!");
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        return players;
    }

    @Override
    public void printScore(IdpCommandSender sender) {
        Objective obj = gameScoreboard.getObjective(QUAKE_OBJECTIVE);
        List<String> messages = new ArrayList<String>();
        String tempMessage = "";

        for (IdpPlayer player : players) {
            String coloredName = player.getColoredDisplayName();
            Score score = obj.getScore(coloredName);
            int points = score.getScore();
            boolean clearTempMessage = false;

            if (!tempMessage.isEmpty()) {
                tempMessage += ChatColor.YELLOW + ", ";
                clearTempMessage = true;
            }

            tempMessage += coloredName + ChatColor.WHITE + ": " + ChatColor.AQUA + points;

            if (clearTempMessage) {
                messages.add(tempMessage);
                tempMessage = "";
            }
        }

        if (!tempMessage.isEmpty()) {
            messages.add(tempMessage);
        }

        sender.print(ChatColor.AQUA, "Displaying scores for QuakeCraft (game #" + super.getId() + "):");
        sender.printInfo("");

        for (String msg : messages) {
            sender.printInfo(msg);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);

        if (player.getSession().isPvPImmune()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long lastShot = (lastPlayerShot.containsKey(playerId) ? lastPlayerShot.get(playerId) : 0);
        long diff = (lastShot > 0 ? System.currentTimeMillis() - lastShot : 0);

        if (diff > 0 && diff < RAIL_COOLDOWN) {
            return;
        }

        EquipmentSlot handSlot = player.getNonEmptyHand();

        if (handSlot != null) {
            IdpItemStack handStack = player.getItemInHand(handSlot);

            if (handStack.getMaterial() == RAIL_MATERIAL) {
                Arrow arrow = player.getHandle().launchProjectile(Arrow.class);
                arrow.setVelocity(arrow.getVelocity().multiply(10));
                arrow.setMetadata(projectileMetadata, new FixedMetadataValue(plugin, true));

                player.getHandle().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.5f);
                lastPlayerShot.put(playerId, System.currentTimeMillis());
            }
        }
    }

    @InnEventMarker(type = InnEventType.PROJECTILE_HIT)
    public void onProjectileHit(InnProjectileHitEvent event) {
        Projectile projectile = event.getProjectile();

        if (!projectile.hasMetadata(projectileMetadata)) {
            return;
        }

        event.setTerminate(true);
        projectile.remove();

        IdpPlayer shooter = plugin.getPlayer(((Player) projectile.getShooter()));
        Entity hitEntity = event.getHitEntity();

        if (hitEntity == null || !(hitEntity instanceof Player)) {
            return;
        }

        IdpPlayer hitPlayer = plugin.getPlayer((Player) hitEntity);

        if (!players.contains(hitPlayer)) {
            return;
        }

        if (shooter.equals(hitPlayer)) {
            return;
        }

        shooterHitPlayer(shooter, hitPlayer);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DROP_ITEM)
    public void onPlayerDropItem(InnPlayerDropItemEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_PICKUP_ITEM)
    public void onPlayerPickupItem(InnPlayerPickupItemEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setTerminate(true);
        event.setCancelled(true);
    }

    @InnEventMarker(type = InnEventType.ENTITY_ENVIRONMENTAL_DAMAGE)
    public void onEntityDamage(InnEntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_LOT_LEAVE)
    public void onPlayerLotLeave(InnPlayerLotLeaveEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        InnectisLot enteringLot = event.getTo();

        if (enteringLot != null && enteringLot.getParentTop().equals(event.getFrom().getParentTop())) {
            return;
        }

        playerLeave(player);
    }

    @InnEventMarker(type = InnEventType.PLAYER_QUIT)
    public void onPlayerQuit(InnPlayerQuitEvent event) {
        IdpPlayer player = event.getPlayer();

        if (!players.contains(player)) {
            return;
        }

        playerLeave(player);
    }

    /**
     * Called when a player leaves the game
     * @param player
     */
    private void playerLeave(IdpPlayer player) {
        players.remove(player);
        resetPlayer(player);

        player.print(ChatColor.AQUA, "You have left the game!");
        broadcastMessage(player.getColoredDisplayName(), " has left the game!");

        if (players.size() < 2) {
            broadcastMessage("Not enough players left. Game ended!");
            player.print(ChatColor.AQUA, "Not enough players. Game ended!");

            endGame();
        }
    }

    /**
     * Initializes the scoreboard
     */
    private void initializeScoreboard() {
        Objective obj = gameScoreboard.registerNewObjective(QUAKE_OBJECTIVE, "playerKillCount");
        obj.setDisplayName(ChatColor.YELLOW + "Player Gibs (Max " + winLimit + ")");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Initializes the player
     * @param player
     */
    private void initializePlayer(IdpPlayer player) {
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
        player.addItemToInventory(new IdpItemStack(RAIL_MATERIAL, 1));

        String coloredName = player.getColoredDisplayName();
        Objective obj = gameScoreboard.getObjective(QUAKE_OBJECTIVE);
        Score score = obj.getScore(coloredName);

        score.setScore(0);
        entries.add(coloredName);

        player.getSession().setScoreboard(gameScoreboard);
    }

    /**
     * Resets the player
     * @param player
     */
    private void resetPlayer(IdpPlayer player) {
        player.setInventory(player.getWorld().getSettings().getInventoryType());
        player.getSession().resetScoreboard();
    }

    /**
     * Called when a shooter hits a player
     * @param shooter
     * @param hitPlayer
     */
    private void shooterHitPlayer(IdpPlayer shooter, IdpPlayer hitPlayer) {
        Objective obj = gameScoreboard.getObjective(QUAKE_OBJECTIVE);
        String coloredName = shooter.getColoredDisplayName();
        Score score = obj.getScore(coloredName);
        int newValue = score.getScore() + 1;

        score.setScore(newValue);

        broadcastMessage(shooter.getColoredDisplayName(), " gibbed ", hitPlayer.getColoredDisplayName(), "!");

        if (newValue == winLimit) {
            endGame();

            return;
        }

        teleportToNextSpawn(hitPlayer);
    }

    /**
     * Broadcasts a message to all players
     * @param messages
     */
    private void broadcastMessage(String... messages) {
        broadcastMessage(ChatColor.AQUA, messages);
    }

    /**
     * Broadcasts a message to all players with a specified color
     * @param messages
     */
    private void broadcastMessage(ChatColor color, String... messages) {
        for (IdpPlayer player : players) {
            player.print(color, messages);
        }
    }

    /**
     * Teleports the player to the next spawn
     */
    private void teleportToNextSpawn(IdpPlayer player) {
        Location loc = spawns.get(random.nextInt(spawns.size()));
        player.teleport(loc);
        player.getSession().setPvPImmuneTime(2);
    }

}

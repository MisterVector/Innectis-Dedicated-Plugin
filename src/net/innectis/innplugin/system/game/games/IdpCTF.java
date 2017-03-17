package net.innectis.innplugin.system.game.games;

import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.listeners.idp.InnPlayerQuitEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDeathEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPostRespawnEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDropItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnInventoryClickEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerRespawnEvent;
import net.innectis.innplugin.system.game.IdpStartResult;
import net.innectis.innplugin.system.game.GameTimer;
import net.innectis.innplugin.system.game.IdpGameType;
import net.innectis.innplugin.system.game.IdpRegionGame;
import net.innectis.innplugin.system.game.GameTimerTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnPlayerPickupItemEvent;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.Prefix;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author AlphaBlend
 */
public class IdpCTF extends IdpRegionGame implements GameTimer {

    public enum TeamType {
        RED(ChatColor.RED, "red"),
        BLUE(ChatColor.BLUE, "blue"),
        NONE;

        private ChatColor color;
        private String name;

        TeamType() {

        }

        TeamType(ChatColor color) {
            this(color, "");
        }

        TeamType(ChatColor color, String name) {
           this.color = color;
           this.name = name;
        }

        public ChatColor getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public String getTeamColorName(ChatColor afterColor) {
            return getColor() + getName() + " team" + afterColor;
        }
    }

    public enum PlaceType {
        NO_CAPTURE,
        CAPTURED,
        FLAG_SAFE,
        FLAG_ON_FIELD,
        ENEMY_FLAG_ON_FIELD,
        NONE;
    }

    public enum DestroyType {
        RECOVERED,
        STOLEN,
        PICK_UP,
        DENIED,
    }

    public enum WinningTouchType {
        FLAG_CAPTURE,
        NO_FLAG_CAPTURE,
        NONE;
    }

    private static final String RED_BASE_NAME = "RedBase";
    private static final String BLUE_BASE_NAME = "BlueBase";
    private static final String RED_FLAG_NAME = "RedFlag";
    private static final String BLUE_FLAG_NAME = "BlueFlag";

    private static final String gamePrefix = "[CTF] ";

    private static final String CTF_OBJECTIVE = "CTF_OBJECTIVE";
    private static final String SCORE_BLUE_TEAM = ChatColor.BLUE + "Team Blue";
    private static final String SCORE_RED_TEAM = ChatColor.RED + "Team Red";
    private static final IdpMaterial RED_FLAG_MATERIAL = IdpMaterial.WOOL_RED;
    private static final IdpMaterial BLUE_FLAG_MATERIAL = IdpMaterial.WOOL_BLUE;
    private static final IdpMaterial FLAG_MATERIAL = IdpMaterial.NETHER_BRICK_FENCE;
    private static final int FLAG_STANDING_LIFETIME = 30000;
    private static final int FLAG_HEIGHT = 3;
    private static final int MIN_TEAM_PLAYERS = 2;

    private List<IdpPlayer> teamRed = new ArrayList<IdpPlayer>();
    private List<IdpPlayer> teamBlue = new ArrayList<IdpPlayer>();

    private Location[] redFlag = new Location[FLAG_HEIGHT];
    private Location[] blueFlag = new Location[FLAG_HEIGHT];

    private HashMap<String, TeamType> heldFlag = new HashMap<String, TeamType>();
    private HashMap<String, Integer> teamRedScore = new HashMap<String, Integer>();
    private HashMap<String, Integer> teamBlueScore = new HashMap<String, Integer>();

    private boolean redFlagAtBase = false;
    private boolean redFlagStanding = false;
    private boolean redFlagStolen = false;

    private boolean blueFlagAtBase = false;
    private boolean blueFlagStanding = false;
    private boolean blueFlagStolen = false;

    private Scoreboard gameScoreboard;
    private Score redScore;
    private Score blueScore;

    private long redflagDropTick = 0;
    private long blueflagDropTick = 0;

    // Game options (these must be set when creating the game
    private int winLimit = 0;
    private InnectisLot gamelot;

    //public IdpCTF(InnectisLot lot, String starter, int limitScore, boolean tpHomeWhenTouched, boolean requireKillCapture) {
    public IdpCTF(InnectisLot lot, String starter, int winLimit) {
        super(lot, starter);

        this.winLimit = winLimit;
        gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.CTF;
    }

    @Override
    public IdpStartResult canStart() {
        IdpPlayer host = InnPlugin.getPlugin().getPlayer(getGameHost());

        if (!(getRegion() instanceof InnectisLot)) {
            return new IdpStartResult(false, "You must start the game on a lot.");
        }

        gamelot = ((InnectisLot) getRegion()).getParentTop();

        if (!(gamelot.canPlayerAccess(host.getName()) || host.hasPermission(Permission.lot_command_override))) {
            return new IdpStartResult(false, "You do not have permission to start a game here.");
        }

        if (getBaseLocation(TeamType.RED, false) == null) {
            return new IdpStartResult(false, "Red base not found!");
        } else if (getBaseLocation(TeamType.RED, true) == null) {
            return new IdpStartResult(false, "Red flag base not found!");
        } else if (getBaseLocation(TeamType.BLUE, false) == null) {
            return new IdpStartResult(false, "Blue base not found!");
        } else if (getBaseLocation(TeamType.BLUE, true) == null) {
            return new IdpStartResult(false, "Blue flag base not found!");
        }

        // Gets a list of team red and blue
        addAllPlaying();

        if (teamRed.size() < MIN_TEAM_PLAYERS || teamBlue.size() < MIN_TEAM_PLAYERS) {
            return new IdpStartResult(false, "Not enough players on either team!");
        }

        return new IdpStartResult(true);
    }

    @Override
    public void startGame() {
        broadcastToAllTeams("A game of Capture the Flag has begun!", ChatColor.GREEN);

        setupFlags();
        setupInventories(true);
        respawnPlayersToBase();
        setPrefixes();

        Objective obj = gameScoreboard.registerNewObjective(CTF_OBJECTIVE, "dummy");
        obj.setDisplayName("Capture the Flag");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = obj.getScore(ChatColor.YELLOW + "Score Limit");
        score.setScore(winLimit);

        redScore = obj.getScore(SCORE_RED_TEAM);
        redScore.setScore(0);

        blueScore = obj.getScore(SCORE_BLUE_TEAM);
        blueScore.setScore(0);

        String teamRedPlayers = "";

        for (IdpPlayer p : teamRed) {
            if (teamRedPlayers.isEmpty()) {
                teamRedPlayers = p.getName();
            } else {
                teamRedPlayers += ", " + p.getName();
            }

            p.getSession().setScoreboard(gameScoreboard);
        }

        String teamBluePlayers = "";

        for (IdpPlayer p : teamBlue) {
            if (teamBluePlayers.isEmpty()) {
                teamBluePlayers = p.getName();
            } else {
                teamBluePlayers += ", " + p.getName();
            }

            p.getSession().setScoreboard(gameScoreboard);
        }

        broadcastToAllTeams("Red Team: " + ChatColor.YELLOW + teamRedPlayers, ChatColor.RED);
        broadcastToAllTeams("Blue Team: " + ChatColor.YELLOW + teamBluePlayers, ChatColor.BLUE);

        InnPlugin.getPlugin().getTaskManager().addTask(new GameTimerTask(1000, this));
    }

    @Override
    public void onInterval() {
        long diff = 0;

        if (redflagDropTick > 0) {
            diff = System.currentTimeMillis() - redflagDropTick;

            if (diff >= FLAG_STANDING_LIFETIME) {
                destroyFlag(TeamType.RED);
                resetFlagLocation(TeamType.RED);
                broadcastToAllTeams(TeamType.RED.getTeamColorName(ChatColor.YELLOW) + "'s flag has returned to base.");
                redflagDropTick = 0;
            }
        }

        if (blueflagDropTick > 0) {
            diff = System.currentTimeMillis() - blueflagDropTick;

            if (diff >= FLAG_STANDING_LIFETIME) {
                destroyFlag(TeamType.BLUE);
                resetFlagLocation(TeamType.BLUE);
                broadcastToAllTeams(TeamType.BLUE.getTeamColorName(ChatColor.YELLOW) + "'s flag has returned to base.");
                blueflagDropTick = 0;
            }
        }

    }

    @Override
    public void endGame() {
        super.endGame();
        resetPlayers();
        clearAllFlags();
        clearPrefixes();

        broadcastToAllTeams("The game of Capture the Flag has ended!", ChatColor.RED);
        broadcastToAllTeams("Final score: " + TeamType.RED.getTeamColorName(ChatColor.YELLOW) + " " + redScore.getScore() + ChatColor.WHITE + ". " + TeamType.BLUE.getTeamColorName(ChatColor.YELLOW) + " " + blueScore.getScore() + ChatColor.WHITE + ".", ChatColor.YELLOW);
    }

    @Override
    public void printScore(IdpCommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.RED).append("Red Team").append(ChatColor.YELLOW).append(": ").append(redScore.getScore()).append(" ");

        if (!teamRedScore.isEmpty()) {
            sb.append("(");
            StringBuilder sb2 = new StringBuilder();

            for (String scorer : teamRedScore.keySet()) {
                int score = teamRedScore.get(scorer);

                if (sb2.length() == 0) {
                    sb2.append(scorer).append(": ").append(score).append(" point").append(score > 1 ? "s" : "");
                } else {
                    sb2.append(", ").append(scorer).append(": ").append(score);
                }
            }

            sb.append(sb2.toString()).append(") ");
        }

        sb.append(ChatColor.BLUE).append("Blue Team").append(ChatColor.YELLOW).append(": ").append(blueScore.getScore()).append(" ");

        if (!teamBlueScore.isEmpty()) {
            sb.append("(");
            StringBuilder sb2 = new StringBuilder();

            for (String scorer : teamBlueScore.keySet()) {
                int score = teamBlueScore.get(scorer);

                if (sb2.length() == 0) {
                    sb2.append(scorer).append(": ").append(score).append(" point").append(score > 1 ? "s" : "");
                } else {
                    sb2.append(", ").append(scorer).append(": ").append(score);
                }
            }

            sb.append(sb2.toString()).append(")");
        }

        sender.print(ChatColor.YELLOW, sb.toString().trim());
    }

    @Override
    public boolean addPlayer(IdpPlayer player) {
        TeamType checkType = getTeamType(player);

        // This player is already in the game
        if (checkType != TeamType.NONE) {
            return false;
        }

        TeamType teamJoined = null;

        if (teamRed.size() <= teamBlue.size()) {
            teamRed.add(player);
            teamJoined = TeamType.RED;
        } else {
            teamBlue.add(player);
            teamJoined = TeamType.BLUE;
        }

        player.getSession().setScoreboard(gameScoreboard);
        Location teamBaseLocation = getBaseLocation(teamJoined, false).getSpawn();
        player.teleport(teamBaseLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        setPrefixPlayer(getTeamPrefix(teamJoined), player);
        setupInventoryPlayer(true, player, teamJoined);
        broadcastToAllTeams(player.getName() + " has joined " + teamJoined.getTeamColorName(ChatColor.YELLOW) + "!");
        return true;
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        List<IdpPlayer> players = new ArrayList<IdpPlayer>();
        players.addAll(teamRed);
        players.addAll(teamBlue);

        return players;
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();
        TeamType playerTeam = getTeamType(player);

        // Not playing, so do nothing
        if (playerTeam == TeamType.NONE) {
            return;
        }

        event.setCancelled(true);

        Block block = event.getBlock();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Location loc = block.getLocation();

        // Interacted outside of game lot, so do nothing
        if (!gamelot.contains(loc)) {
            return;
        }

        TeamType heldFlagTeam = heldFlag.get(player.getName());
        TeamType enemyTeam = (playerTeam == TeamType.RED ? TeamType.BLUE : TeamType.RED);

        if (heldFlagTeam != null) {
            PlaceType placeType = getPlaceType(playerTeam, loc, heldFlagTeam);
            BlockFace face = event.getBlockFace();

            boolean playerScored = false;
            boolean placeFlag = true;
            boolean sendHome = false;

            switch (placeType) {
                case CAPTURED:
                    broadcastTeamMessage(playerTeam, player.getName() + " from your team captured " + enemyTeam.getTeamColorName(playerTeam.getColor()) + "'s flage!");
                    broadcastTeamMessage(enemyTeam, player.getName() + " from " + playerTeam.getTeamColorName(enemyTeam.getColor()) + " has captured your flag!");

                    playerScored = true;
                    placeFlag = false;
                    sendHome = true;

                    break;
                case NO_CAPTURE:
                    placeFlag = false;
                    player.print(ChatColor.YELLOW, gamePrefix + "Your own flag must be at base before capturing the enemy's flag!");
                    break;
                case FLAG_ON_FIELD:
                    if (face != BlockFace.UP) {
                        return;
                    }

                    if (playerTeam == TeamType.RED) {
                        redFlagStanding = true;
                    } else {
                        blueFlagStanding = true;
                    }

                    broadcastTeamMessage(playerTeam, player.getName() + " put down your flag.");
                    broadcastTeamMessage(enemyTeam, player.getName() + " from " + playerTeam.getTeamColorName(enemyTeam.getColor()) + " put down their flag.");
                    break;
                case ENEMY_FLAG_ON_FIELD:
                    if (face != BlockFace.UP) {
                        return;
                    }

                    if (heldFlagTeam == TeamType.RED) {
                        redFlagStanding = true;
                    } else {
                        blueFlagStanding = true;
                    }

                    broadcastTeamMessage(playerTeam, player.getName() + " set " + enemyTeam.getTeamColorName(playerTeam.getColor()) + "'s flag down.");
                    broadcastTeamMessage(enemyTeam, player.getName() + " from " + playerTeam.getTeamColorName(enemyTeam.getColor()) + " set down your flag.");
                    break;
                case FLAG_SAFE:
                    if (face != BlockFace.UP) {
                        return;
                    }

                    placeFlag = false;
                    sendHome = true;

                    broadcastToAllTeams(playerTeam.getTeamColorName(ChatColor.YELLOW) + "'s flag is now safe!");
                    break;
                case NONE:
                    placeFlag = false;
                    break;
            }

            if (placeFlag || sendHome) {
                heldFlag.remove(player.getName());
                removeFlagWearHelmet(player, playerTeam);
            }

            if (placeFlag) {
                placeFlag(loc, heldFlagTeam);
            }

            if (sendHome) {
                resetFlagLocation(heldFlagTeam);
            }

            if (playerScored) {
                if (giveTeamPoint(player, playerTeam)) {
                    broadcastToAllTeams("Winning capture made by " + player.getName() + " (" + playerTeam.getTeamColorName(ChatColor.YELLOW) + ")!");
                    endGame();
                }
            }
        } else {
            TeamType flagTeam = getFlagTouched(loc);

            if (flagTeam != TeamType.NONE) {
                DestroyType destroyType = getDestroyType(playerTeam, flagTeam);

                boolean destroyFlag = true;
                boolean sendHome = false;

                switch (destroyType) {
                    case STOLEN:
                        if (flagTeam == TeamType.RED) {
                            redFlagAtBase = false;
                            redFlagStanding = false;
                            redFlagStolen = true;
                        } else {
                            blueFlagAtBase = false;
                            blueFlagStanding = false;
                            blueFlagStolen = true;
                        }

                        broadcastTeamMessage(playerTeam, player.getName() + " from your team stole " + enemyTeam.getTeamColorName(playerTeam.getColor()) + "'s flag!");
                        broadcastTeamMessage(enemyTeam, player.getName() + " from the " + playerTeam.getTeamColorName(enemyTeam.getColor()) + " stole your flag!");
                        break;
                    case RECOVERED:
                        if (playerTeam == TeamType.RED) {
                            redFlagStanding = false;
                            redFlagStolen = false;
                        } else {
                            blueFlagStolen = false;
                            blueFlagStanding = false;
                        }

                        broadcastTeamMessage(playerTeam, player.getName() + " has recovered your flag!");
                        broadcastTeamMessage(enemyTeam, player.getName() + " from " + playerTeam.getTeamColorName(enemyTeam.getColor()) + " has recovered their flag!");
                        break;
                    case PICK_UP:
                        if (playerTeam == TeamType.RED) {
                            redFlagStanding = false;
                        } else {
                            blueFlagStanding = false;
                        }

                        player.print(ChatColor.YELLOW, gamePrefix + "You picked up your flag.");
                        break;
                    case DENIED:
                        destroyFlag = false;
                        break;
                }

                if (destroyFlag) {
                    wearFlag(player, flagTeam);
                    heldFlag.put(player.getName(), flagTeam);
                    destroyFlag(flagTeam);
                }

                if (sendHome) {
                    resetFlagLocation(flagTeam);
                }
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DROP_ITEM)
    public void onPlayerDropItem(InnPlayerDropItemEvent event) {
        TeamType type = getTeamType(event.getPlayer());

        // Not playing, so do nothing
        if (type == TeamType.NONE) {
            return;
        }

        // Can't drop items in game
        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DROP_ITEM)
    public void onPlayerPickupItem(InnPlayerPickupItemEvent event) {
        TeamType type = getTeamType(event.getPlayer());

        // Not playing, so do nothing
        if (type == TeamType.NONE) {
            return;
        }

        // Can't drop items in game
        event.setCancelled(true);
        event.setTerminate(true);
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PROJECTILE)
    public void onPlayerDamageByProjectile(InnPlayerDamageByProjectileEvent event) {
        IdpPlayer victim = event.getPlayer();
        IdpPlayer damager = event.getDamager();
        boolean terminate = false;

        if (damager != null) {
             terminate = handleDamage(victim, damager, true);
        }

        if (terminate) {
            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PLAYER)
    public void onPlayerDamageByPlayer(InnPlayerDamageByPlayerEvent event) {
        IdpPlayer victim = event.getPlayer();
        IdpPlayer damager = event.getDamager();
        boolean terminate = handleDamage(victim, damager, false);

        if (terminate) {
            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DEATH)
    public void onPlayerDeath(InnPlayerDeathEvent event) {
        IdpPlayer player = event.getPlayer();
        TeamType playerTeam = getTeamType(player);

        // No team, so don't do anything more
        if (playerTeam == TeamType.NONE) {
            return;
        }

        event.clearDrops();
        event.setDeathmessage("");
        event.setTerminate(true);

        TeamType enemyTeam = (playerTeam == TeamType.RED ? TeamType.BLUE : TeamType.RED);
        EntityDamageEvent lastCause = player.getHandle().getLastDamageCause();
        Location loc = player.getLocation();
        IdpPlayer deathPlayer = null;

        if (lastCause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent eevent = (EntityDamageByEntityEvent) lastCause;
            Entity damager = eevent.getDamager();

            if (damager instanceof Player) {
                deathPlayer = InnPlugin.getPlugin().getPlayer((Player) damager);
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;

                if (projectile.getShooter() instanceof Player) {
                    deathPlayer = InnPlugin.getPlugin().getPlayer((Player) projectile.getShooter());
                }
            }
        }

        broadcastToAllTeams(player.getName() + " from " + playerTeam.getTeamColorName(ChatColor.YELLOW) + " died"
                            + (deathPlayer != null ? " by " + deathPlayer.getName() + " from " + enemyTeam.getTeamColorName(ChatColor.YELLOW) + "!" : "!"));

        TeamType flagTeam = heldFlag.get(player.getName());

        if (flagTeam != null) {
            heldFlag.remove(player.getName());
            broadcastToAllTeams(player.getName() + " also dropped " + (playerTeam != flagTeam ? flagTeam.getTeamColorName(playerTeam.getColor()) + "'s" : "their own") + " flag!");

            if (!placeFlag(loc, flagTeam)) {
                resetFlagLocation(flagTeam);
                broadcastToAllTeams("Flag has been teleported to " + flagTeam.getTeamColorName(ChatColor.YELLOW) + "'s base, because it was in an awkward spot.");
            }

            if (flagTeam == TeamType.RED) {
                redFlagStanding = true;
                redFlagStolen = false;
            } else {
                blueFlagStanding = true;
                blueFlagStolen = false;
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_LOT_LEAVE)
    public void onPlayerLotLeave(InnPlayerLotLeaveEvent event) {
        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getTo();

        if (lot == null || !lot.getParentTop().equals(gamelot)) {
            playerLeave(player, player.getLocation());
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_QUIT)
    public void onPlayerQuit(InnPlayerQuitEvent event) {
        IdpPlayer player = event.getPlayer();
        playerLeave(player, player.getLocation());
    }

    @InnEventMarker(type = InnEventType.PLAYER_POST_RESPAWN)
    public void onPostRespawn(InnPlayerPostRespawnEvent event) {
        IdpPlayer player = event.getPlayer();
        TeamType team = getTeamType(player);

        if (team == TeamType.NONE) {
            return;
        }

        setupInventoryPlayer(true, player, team);
    }

    /**
     * Respawns player to base when they respawn
     * @param event
     */
    @InnEventMarker(type = InnEventType.PLAYER_RESPAWN)
    public void onPlayerRespawn(InnPlayerRespawnEvent event) {
        IdpPlayer player = event.getPlayer();
        TeamType team = getTeamType(player);

        // Not playing, so do nothing
        if (team == TeamType.NONE) {
            return;
        }

        Location baseSpawn = getBaseLocation(team, false).getSpawn();
        event.setRespawnLocation(baseSpawn);
    }

    @InnEventMarker(type = InnEventType.PLAYER_INVENTORY_CLICK)
    public void onInventoryClick(InnInventoryClickEvent event) {
        TeamType type = getTeamType(event.getPlayer());

        // Not playing, so do nothing
        if (type == TeamType.NONE) {
            return;
        }

        event.setCancelled(true);
        event.setCancelled(true);
    }

    private boolean handleDamage(IdpPlayer victim, IdpPlayer damager, boolean isProjectile) {
        TeamType teamVictim = getTeamType(victim);
        TeamType teamDamager = getTeamType(damager);

        // If neither player is playing, do nothing
        if (teamVictim == TeamType.NONE && teamDamager == TeamType.NONE) {
            return false;
        }

        // Signal to cancel event, as only one player is playing
        if (teamVictim == TeamType.NONE || teamDamager == TeamType.NONE) {
            return true;
        }

        // Only allow damage if different teams
        if (teamVictim != teamDamager) {
            CTFdealDamage(victim, damager, isProjectile);
        }

        return true;
    }

    /**
     * Handle when a player leaves the game
     * @param player
     * @param type
     */
    private void playerLeave(IdpPlayer player, Location loc) {
        TeamType playerTeam = getTeamType(player);

        // No team, so don't do anything more
        if (playerTeam == TeamType.NONE) {
            return;
        }

        broadcastToAllTeams(player.getName() + " from the " + playerTeam.getTeamColorName(ChatColor.YELLOW) + " has been disqualified for leaving the game!");

        removePlayer(player, playerTeam);

        if (teamRed.isEmpty() || teamBlue.isEmpty()) {
            broadcastToAllTeams("There are not enough players to continue.");
            player.print(ChatColor.YELLOW, gamePrefix + "Since you left, not enough players, so game ended.");
            endGame();
        } else {
            if (heldFlag.containsKey(player.getName())) {
                TeamType flagTeam = heldFlag.get(player.getName());
                heldFlag.remove(player.getName());

                broadcastToAllTeams(player.getName() + " had " + flagTeam.getTeamColorName(ChatColor.YELLOW) + "'s flag.");

                if (!placeFlag(loc, flagTeam)) {
                    resetFlagLocation(flagTeam);
                    broadcastToAllTeams("The flag location was obstructed, so it has teleported to base.");
                } else {
                    if (flagTeam == TeamType.RED) {
                        redFlagStanding = true;
                        redFlagStolen = false;
                    } else {
                        blueFlagStanding = true;
                        blueFlagStolen = false;
                    }
                }
            }
        }

        restoreOriginalInventory(player);
        clearPrefixPlayer(player);
    }

    private void broadcastToAllTeams(String msg) {
        broadcastToAllTeams(msg, ChatColor.YELLOW);
    }

    private void broadcastToAllTeams(String msg, ChatColor color) {
        broadcastTeamMessage(TeamType.RED, msg, color);
        broadcastTeamMessage(TeamType.BLUE, msg, color);
    }

    private void broadcastTeamMessage(TeamType teamType, String msg) {
        broadcastTeamMessage(teamType, msg, teamType.getColor());
    }

    private void broadcastTeamMessage(TeamType teamType, String msg, ChatColor color) {
        List<IdpPlayer> teamPlayers = (teamType == TeamType.RED ? teamRed : teamBlue);

        for (IdpPlayer p : teamPlayers) {
            p.print(color, gamePrefix + msg);
        }
    }

    private TeamType getFlagTouched(Location location) {
        for (int i = 0; i < FLAG_HEIGHT; i++) {
            if (location.equals(redFlag[i])) {
                if (redFlagStanding) {
                    return TeamType.RED;
                }
            }
            else if (location.equals(blueFlag[i])) {
                if (blueFlagStanding) {
                    return TeamType.BLUE;
                }
            }
        }

        return TeamType.NONE;
    }

    private void setupFlags() {
        Location teamRedFlagLocation = getBaseLocation(TeamType.RED, true).getCenter();

        World redWorld = teamRedFlagLocation.getWorld();
        int redX = teamRedFlagLocation.getBlockX();
        int redY = teamRedFlagLocation.getBlockY();
        int redZ = teamRedFlagLocation.getBlockZ();

        for (int i = 0; i < FLAG_HEIGHT; i++) {
            redFlag[i] = new Location(redWorld, redX, redY + 1 + i, redZ);
            IdpMaterial flagMaterial = (i == FLAG_HEIGHT - 1 ? RED_FLAG_MATERIAL : FLAG_MATERIAL);
            BlockHandler.setBlock(redFlag[i].getBlock(), flagMaterial);
        }

        redFlagAtBase = true;
        redFlagStanding = true;
        redFlagStolen = false;

        Location teamBlueFlagLocation = getBaseLocation(TeamType.BLUE, true).getCenter();

        World blueWorld = teamBlueFlagLocation.getWorld();
        int blueX = teamBlueFlagLocation.getBlockX();
        int blueY = teamBlueFlagLocation.getBlockY();
        int blueZ = teamBlueFlagLocation.getBlockZ();

        for (int i = 0; i < FLAG_HEIGHT; i++) {
            blueFlag[i] = new Location(blueWorld, blueX, blueY + 1 + i, blueZ);
            IdpMaterial flagMaterial = (i == FLAG_HEIGHT - 1 ? BLUE_FLAG_MATERIAL : FLAG_MATERIAL);
            BlockHandler.setBlock(blueFlag[i].getBlock(), flagMaterial);
        }

        blueFlagAtBase = true;
        blueFlagStanding = true;
        blueFlagStolen = false;
    }

    /**
     * Places a flag at the specified location
     * @param loc the location just below where the flag is placed
     * @param flagTeam which team this flag belongs to
     * @return the resulting place type
     */
    private PlaceType getPlaceType(TeamType playerTeam, Location loc, TeamType flagTeam) {
        PlaceType placeType = PlaceType.NONE;
        boolean passTest = false;

        Location teamRedFlagLocation = getBaseLocation(TeamType.RED, true).getCenter();
        Location teamBlueFlagLocation = getBaseLocation(TeamType.BLUE, true).getCenter();

        if (playerTeam != flagTeam) {
            WinningTouchType type = getWinningTouchType(flagTeam, loc);

            if (type == WinningTouchType.FLAG_CAPTURE) {
                placeType = PlaceType.CAPTURED;
                passTest = true;
            } else if (type == WinningTouchType.NO_FLAG_CAPTURE) {
                placeType = PlaceType.NO_CAPTURE;
                passTest = true;
            } else {
                if ((playerTeam == TeamType.RED && !loc.equals(teamBlueFlagLocation))
                        || (playerTeam == TeamType.BLUE && !loc.equals(teamRedFlagLocation))) {
                    placeType = PlaceType.ENEMY_FLAG_ON_FIELD;
                }
            }
        } else {
            if ((playerTeam == TeamType.RED && loc.equals(teamRedFlagLocation))
                    || (playerTeam == TeamType.BLUE && loc.equals(teamBlueFlagLocation))) {
                placeType = PlaceType.FLAG_SAFE;
                passTest = true;
            } else if (!(loc.equals(teamRedFlagLocation) || loc.equals(teamBlueFlagLocation))) {
                placeType = PlaceType.FLAG_ON_FIELD;
            }
        }

        if (!passTest) {
            Block block = loc.getBlock();
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat.isNonSolid(true)) {
                return PlaceType.NONE;
            }

            // Check if any obstruction in the way
            for (int i = 0; i < FLAG_HEIGHT; i++) {
                block = block.getRelative(BlockFace.UP);
                mat = IdpMaterial.fromBlock(block);

                if (mat != IdpMaterial.AIR) {
                    return PlaceType.NONE;
                }
            }
        }

        return placeType;
    }

    /**
     * Places a flag at the given location
     * @param loc
     * @param flagTeam
     * @return true if the flag was placed at the location, false if it needs to be reset
     */
    private boolean placeFlag(Location loc, TeamType flagTeam) {
        Block block = loc.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // If placed while the player dies, they may have jumped
        if (!mat.isSolid()) {
            block = block.getRelative(BlockFace.DOWN);
            mat = IdpMaterial.fromBlock(block);
        }

        if (mat.isSolid()) {
            // If the location is not ideal for the flag, don't place it
            for (int i = 0; i < FLAG_HEIGHT; i++) {
                block = block.getRelative(BlockFace.UP);
                mat = IdpMaterial.fromBlock(block);

                if (mat != IdpMaterial.AIR) {
                    return false;
                }
            }

            IdpMaterial teamFlagMaterial = (flagTeam == TeamType.RED ? RED_FLAG_MATERIAL : BLUE_FLAG_MATERIAL);
            Location[] flagLocation = (flagTeam == TeamType.RED ? redFlag : blueFlag);

            World world = loc.getWorld();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            for (int i = 0; i < FLAG_HEIGHT; i++) {
                flagLocation[i] = new Location(world, x, y + 1 + i, z);
                block = flagLocation[i].getBlock();
                IdpMaterial flagMaterial = (i == FLAG_HEIGHT - 1 ? teamFlagMaterial : FLAG_MATERIAL);
                BlockHandler.setBlock(block, flagMaterial);
            }

            if (flagTeam == TeamType.RED) {
                redflagDropTick = System.currentTimeMillis();
            } else {
                blueflagDropTick = System.currentTimeMillis();
            }

            return true;
        }

        return false;
    }

    /**
     * Attempts to destroy the flag at its current location
     * @param player the player destroying the flag
     * @param flagTeam the team the flag belongs to
     * @return
     */
    private DestroyType getDestroyType(TeamType playerTeam, TeamType flagTeam) {
        DestroyType destroyType = DestroyType.DENIED;

        if (flagTeam != playerTeam) {
            destroyType = DestroyType.STOLEN;
        } else {
            if (playerTeam == TeamType.RED) {
                if (!redFlagAtBase) {
                    if (redFlagStolen) {
                        destroyType = DestroyType.RECOVERED;
                    } else {
                        destroyType = DestroyType.PICK_UP;
                    }
                }
            } else {
                if (!blueFlagAtBase) {
                    if (blueFlagStolen) {
                        destroyType = DestroyType.RECOVERED;
                    } else {
                        destroyType = DestroyType.PICK_UP;
                    }
                }
            }
        }

        return destroyType;
    }

    /**
     * Forcibly destroys a flag at its location
     * @param flagTeam
     */
    private void destroyFlag(TeamType flagTeam) {
        Location[] flagLocation = (flagTeam == TeamType.RED ? redFlag : blueFlag);

        for (int i = 0; i < FLAG_HEIGHT; i++) {
            BlockHandler.setBlock(flagLocation[i].getBlock(), IdpMaterial.AIR);
        }

        if (flagTeam == TeamType.RED) {
            redflagDropTick = 0;
        } else {
            blueflagDropTick = 0;
        }
    }

    /**
     * Gives the specified player's team a point
     * @param player
     * @param team
     * @return true if this resulted in the end of the game, false otherwise
     */
    private boolean giveTeamPoint(IdpPlayer player, TeamType team) {
        if (team == TeamType.RED) {
            if (!teamRedScore.containsKey(player.getName())) {
                teamRedScore.put(player.getName(), 1);
            } else {
                int score = teamRedScore.get(player.getName());
                teamRedScore.put(player.getName(), ++score);
            }

            redScore.setScore(redScore.getScore() + 1);
        } else {
            if (!teamBlueScore.containsKey(player.getName())) {
                teamBlueScore.put(player.getName(), 1);
            } else {
                int score = teamBlueScore.get(player.getName());
                teamBlueScore.put(player.getName(), ++score);
            }

            blueScore.setScore(blueScore.getScore() + 1);
        }

        if (redScore.getScore() == winLimit || blueScore.getScore() == winLimit) {
            return true;
        }

        return false;
    }

    private void resetRound() {
        resetAllFlags();
        resetInventories();
        setupInventories(false);
        clearAnyFlagHolders();
        respawnPlayersToBase();
    }

    private void resetAllFlags() {
        resetFlagLocation(TeamType.RED);
        resetFlagLocation(TeamType.BLUE);
    }

    private void clearAllFlags() {
        destroyFlag(TeamType.RED);
        destroyFlag(TeamType.BLUE);
    }

    /**
     * Clears any flags that players may have and replaces with their helmet
     */
    private void clearAnyFlagHolders() {
        for (IdpPlayer p : teamRed) {
            if (heldFlag.containsKey(p.getName())) {
                removeFlagWearHelmet(p, TeamType.RED);
                heldFlag.remove(p.getName());
            }
        }

        for (IdpPlayer p : teamBlue) {
            if (heldFlag.containsKey(p.getName())) {
                removeFlagWearHelmet(p, TeamType.BLUE);
                heldFlag.remove(p.getName());
            }
        }
    }

    /**
     * This method checks to see if you clicked your own flag standing at base or you clicked
     * the base of where the flag is standing, while carrying the enemy flag
     * @param loc
     * @return The touch type of the click
     */
    private WinningTouchType getWinningTouchType(TeamType heldFlagTeam, Location loc) {
        boolean flagAtBase = (heldFlagTeam == TeamType.RED ? blueFlagAtBase : redFlagAtBase);
        Location[] flag = (heldFlagTeam == TeamType.RED ? blueFlag : redFlag);
        TeamType oppositeTeam = (heldFlagTeam == TeamType.RED ? TeamType.BLUE : TeamType.RED);
        Location flagLocation = getBaseLocation(oppositeTeam, true).getCenter();

        if (flagAtBase) {
            for (int i = 0; i < FLAG_HEIGHT; i++) {
                if (loc.equals(flag[i])) {
                    return WinningTouchType.FLAG_CAPTURE;
                }
            }

            // If the flag was not touched, check if the base was touched
            if (loc.equals(flagLocation)) {
                return WinningTouchType.FLAG_CAPTURE;
            }
        } else {
            // Trying to
            if (loc.equals(flagLocation)) {
                return WinningTouchType.NO_FLAG_CAPTURE;
            }
        }

        return WinningTouchType.NONE;
    }

    private void resetFlagLocation(TeamType team) {
        boolean flagAtBase = (team == TeamType.RED ? redFlagAtBase : blueFlagAtBase);

        if (!flagAtBase) {
            boolean flagStanding = (team == TeamType.RED ? redFlagStanding : blueFlagStanding);

            if (flagStanding) {
                destroyFlag(team);
            }

            Location loc;
            IdpMaterial teamFlagMaterial;
            Location[] flagArray;
            loc = getBaseLocation(team, true).getCenter();

            if (team == TeamType.RED) {
                teamFlagMaterial = RED_FLAG_MATERIAL;
                flagArray = redFlag;
                redFlagAtBase = true;
                redFlagStanding = true;
                redFlagStolen = false;
            } else {
                teamFlagMaterial = BLUE_FLAG_MATERIAL;
                flagArray = blueFlag;
                blueFlagAtBase = true;
                blueFlagStanding = true;
                blueFlagStolen = false;
            }

            World world = loc.getWorld();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            for (int i = 0; i < FLAG_HEIGHT; i++) {
                flagArray[i] = new Location(world, x, y + 1 + i, z);
                Block block = flagArray[i].getBlock();
                IdpMaterial flagMaterial = (i == FLAG_HEIGHT - 1 ? teamFlagMaterial : FLAG_MATERIAL);
                BlockHandler.setBlock(block, flagMaterial);
            }
        }
    }

    private void respawnPlayersToBase() {
        Location baseRedLocation = getBaseLocation(TeamType.RED, false).getSpawn();

        for (IdpPlayer p : teamRed) {
            p.teleport(baseRedLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        }

        Location baseBlueLocation = getBaseLocation(TeamType.BLUE, false).getSpawn();

        for (IdpPlayer p : teamBlue) {
            p.teleport(baseBlueLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        }
    }

    private void wearFlag(IdpPlayer player, TeamType flagTeam) {
        IdpMaterial flagMaterial = (flagTeam == TeamType.RED ? RED_FLAG_MATERIAL : BLUE_FLAG_MATERIAL);
        player.setHelmet(new IdpItemStack(flagMaterial, 1));
        player.getInventory().updateBukkitInventory();
    }

    private void removeFlagWearHelmet(IdpPlayer player, TeamType playerTeam) {
        int armorColor = Integer.parseInt(playerTeam.getColor().getHTMLColor(), 16);
        IdpItemStack stack = new IdpItemStack(IdpMaterial.LEATHER_HELMET, 1);
        ItemData data = stack.getItemdata();
        data.setColor(armorColor);
        player.setHelmet(stack);
        player.getInventory().updateBukkitInventory();
    }

    /**
     * Gives players a no save inventory, and equips them with the game inventory
     */
    private void setupInventories(boolean switchInventory) {
        for (IdpPlayer player : teamRed) {
            setupInventoryPlayer(switchInventory, player, TeamType.RED);
        }

        for (IdpPlayer player : teamBlue) {
            setupInventoryPlayer(switchInventory, player, TeamType.BLUE);
        }
    }

    /**
     * Sets up an inventory for the specified player
     * @param switchInventory if this inventory needs to be switched
     * @param player
     * @param teamType
     */
    private void setupInventoryPlayer(boolean switchInventory, IdpPlayer player, TeamType teamType) {
        if (switchInventory) {
            player.saveInventory();
            player.setInventory(InventoryType.NO_SAVE);
        }

        Random random = new Random();
        equipArmor(player, teamType);
        IdpItemStack sword = null;

        if (random.nextInt(32) == 5) {
            sword = new IdpItemStack(IdpMaterial.DIAMOND_SWORD, 1);
        } else if (random.nextInt(16) == 8) {
            sword = new IdpItemStack(IdpMaterial.GOLD_SWORD, 1);
        } else if (random.nextInt(8) == 2) {
            sword = new IdpItemStack(IdpMaterial.IRON_SWORD, 1);
        } else if (random.nextInt(4) == 1) {
            sword = new IdpItemStack(IdpMaterial.STONE_SWORD, 1);
        } else {
            sword = new IdpItemStack(IdpMaterial.WOOD_SWORD, 1);
        }

        player.addItemToInventory(sword);
        player.addItemToInventory(new IdpItemStack(IdpMaterial.BOW, 1));
        player.addItemToInventory(new IdpItemStack(IdpMaterial.ARROW, 6));
    }

    private void resetInventories() {
        for (IdpPlayer p : teamRed) {
            p.clearInventory();
        }

        for (IdpPlayer p : teamBlue) {
            p.clearInventory();
        }
    }

    /**
     * Restores the inventory of the specified world type
     */
    private void resetPlayers() {
        for (IdpPlayer p : teamRed) {
            restoreOriginalInventory(p);
            p.getSession().resetScoreboard();
        }

        for (IdpPlayer p : teamBlue) {
            restoreOriginalInventory(p);
            p.getSession().resetScoreboard();
        }
    }

    private void restoreOriginalInventory(IdpPlayer player) {
        player.setInventory(player.getWorld().getSettings().getInventoryType());
    }

    private void equipArmor(IdpPlayer player, TeamType team) {
        IdpMaterial[] armor = {IdpMaterial.LEATHER_HELMET, IdpMaterial.LEATHER_CHEST,
                               IdpMaterial.LEATHER_LEGGINGS, IdpMaterial.LEATHER_BOOTS};

        IdpItemStack[] armorStack = new IdpItemStack[4];
        int color = Integer.parseInt(team.getColor().getHTMLColor(), 16);

        for (int i = 0; i < 4; i++) {
            IdpItemStack stack = new IdpItemStack(armor[i], 1);
            ItemData data = stack.getItemdata();
            data.setColor(color);
            armorStack[3 - i] = stack;
        }

        IdpPlayerInventory inv = player.getInventory();
        inv.setArmorItems(armorStack);
        inv.updateBukkitInventory();
    }

    /**
     * Sets the prefix of all players playing
     */
    private void setPrefixes() {
        Prefix teamRedPrefix = getTeamPrefix(TeamType.RED);

        for (IdpPlayer p : teamRed) {
            setPrefixPlayer(teamRedPrefix, p);
        }

        Prefix teamBluePrefix = getTeamPrefix(TeamType.BLUE);

        for (IdpPlayer p : teamBlue) {
            setPrefixPlayer(teamBluePrefix, p);
        }
    }

    /**
     * Sets the given prefix to a player
     * @param prefix
     * @param player
     */
    private void setPrefixPlayer(Prefix prefix, IdpPlayer player) {
        player.getSession().setPrefix(1, prefix, false);
    }

    /**
     * Clears the prefixes of all players
     */
    private void clearPrefixes() {
        for (IdpPlayer p : teamRed) {
            clearPrefixPlayer(p);
        }

        for (IdpPlayer p : teamBlue) {
            clearPrefixPlayer(p);
        }
    }

    /**
     * Clears the prefix of the specified player
     * @param p
     */
    private void clearPrefixPlayer(IdpPlayer p) {
        p.getSession().reloadPrefix();
    }

    /**
     * Gets the prefix from the specified team type
     * @param type
     * @return
     */
    private Prefix getTeamPrefix(TeamType type) {
        ChatColor teamColor = (type == TeamType.RED ? ChatColor.RED : ChatColor.BLUE);
        return new Prefix("CTF (lot #" + ((InnectisLot) getRegion()).getId() + ")", ChatColor.WHITE, teamColor);
    }

    private void removePlayer(IdpPlayer player, TeamType teamType) {
        List<IdpPlayer> players = (teamType == TeamType.RED ? teamRed : teamBlue);

        for (IdpPlayer p : players) {
            if (p.getName().equals(player.getName())) {
                players.remove(p);
                break;
            }
        }
    }

    private void addAllPlaying() {
        InnectisLot baseRed = getBaseLocation(TeamType.RED, false);

        for (IdpPlayer p : baseRed.getPlayersInsideRegion(0)) {
            teamRed.add(p);
        }

        InnectisLot baseBlue = getBaseLocation(TeamType.BLUE, false);

        for (IdpPlayer p : baseBlue.getPlayersInsideRegion(0)) {
            teamBlue.add(p);
        }
    }

    /**
     * Checks if the player is on team red
     * @param player
     * @return
     */
    private boolean isTeamRed(IdpPlayer player) {
        for (IdpPlayer p : teamRed) {
            if (p.equals(player)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the player is on team blue
     * @param player
     * @return
     */
    private boolean isTeamBlue(IdpPlayer player) {
        for (IdpPlayer p : teamBlue) {
            if (p.equals(player)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the team type of the specified player, NONE if player not found
     * @param player
     * @return
     */
    private TeamType getTeamType(IdpPlayer player) {
        if (isTeamRed(player)) {
            return TeamType.RED;
        } else if (isTeamBlue(player)) {
            return TeamType.BLUE;
        }

        return TeamType.NONE;
    }

    private InnectisLot getBaseLocation(TeamType type, boolean getFlagBase) {
         if (type == TeamType.RED) {
            for (InnectisLot lot2 : gamelot.getSublots()) {
                if (lot2.getLotName().equalsIgnoreCase(RED_BASE_NAME)) {
                    if (getFlagBase) {
                        for (InnectisLot l : lot2.getSublots()) {
                            if (l.getLotName().equalsIgnoreCase(RED_FLAG_NAME)) {
                                return l;
                            }
                        }
                    } else {
                        return lot2;
                    }
                }
            }
        } else {
            for (InnectisLot lot2 : gamelot.getSublots()) {
                if (lot2.getLotName().equalsIgnoreCase(BLUE_BASE_NAME)) {
                    if (getFlagBase) {
                        for (InnectisLot l : lot2.getSublots()) {
                            if (l.getLotName().equalsIgnoreCase(BLUE_FLAG_NAME)) {
                                return l;
                            }
                        }
                    } else {
                        return lot2;
                    }
                }
            }
        }

        return null;
    }

    // Custom PvP rules for capture the flag games
    private static void CTFdealDamage(IdpPlayer defender, IdpPlayer attacker, boolean isProjectile) {
        Random randomizer = new Random();

        if (defender.getHealth() <= 0.0D) {
            return;
        }

        double damage = 1.0D; // base damage
        double armor = 0.50;

        if (isProjectile) {
            // Check if attacker was hitting with the bow
            if (attacker.getItemInHand(EquipmentSlot.HAND).getMaterial() == IdpMaterial.BOW) {
                damage = +4;
            }

            // Add bonus for blocking
            if (defender.getHandle().isBlocking() && randomizer.nextBoolean()) {
                armor += 1;
            }
        } else {
            switch (attacker.getMaterialInHand(EquipmentSlot.HAND)) {
                case DIAMOND_SWORD:
                    damage += 7;
                    break;
                case GOLD_SWORD:
                    damage += 5;
                    break;
                case IRON_SWORD:
                    damage += 4;
                    break;
                case STONE_SWORD:
                    damage += 3;
                    break;
                case WOOD_SWORD:
                    damage += 1.3;
                    break;
            }

            // Add bonus for blocking
            if (defender.getHandle().isBlocking()) {
                armor += randomizer.nextInt(4) == 1 ? 1 : 0.25;
            }
        }

        damage = Math.round(damage - (damage * armor));

        if (damage <= 0) {
            //damage = 0;
            return;
        }

        double targetLife = Math.max(defender.getHealth() - damage, 0);

        defender.dealDamage(1.0D);
        defender.setHealth(targetLife);
    }

    @Override
    public String getName() {
        return "Capture The Flag Listener";
    }

}

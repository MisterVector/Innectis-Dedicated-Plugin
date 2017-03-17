package net.innectis.innplugin.system.game.games;

import net.innectis.innplugin.listeners.idp.InnInteractEntityEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockPlaceEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDeathEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByProjectileEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPickupItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDamageByPlayerEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerRespawnEvent;
import net.innectis.innplugin.listeners.idp.InnHangingBreakEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerQuitEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockBreakEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPostRespawnEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerTeleportEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerChatEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDropItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnInventoryClickEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.system.game.GameTimer;
import net.innectis.innplugin.system.game.GameTimerTask;
import net.innectis.innplugin.system.game.IdpGameManager;
import net.innectis.innplugin.system.game.IdpGameType;
import net.innectis.innplugin.system.game.IdpRegionGame;
import net.innectis.innplugin.system.game.IdpStartResult;
import net.innectis.innplugin.system.game.games.domination.PlayerClass;
import net.innectis.innplugin.system.game.games.domination.PlayerClassHandler;
import net.innectis.innplugin.system.game.games.domination.PlayerClassType;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnEventMarker;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.Prefix;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession.PlayerStatus;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author Nosliw
 *
 * A game of Domination
 */
public class IdpDomination extends IdpRegionGame implements GameTimer {

    // <editor-fold defaultstate="collapsed" desc="Settings">
    /** The minimum amount of players required on each team to start a game. */
    private static final int MIN_PLAYERS_PER_TEAM = 1;
    /** The minimum amount of capture points required in the arena to start. */
    private static final int MIN_CAPTURE_POINTS = 3;
    /** The amount of milliseconds between each capture point check. */
    private static final int CAPTUREPOINT_INTERVAL_MS = 1000;
    /** The radius of where the capture will trigger. */
    private static final int CAPTUREPOINT_CAPTURERADIUS = 4;
    /** The amount of intervals * player needed to capture a point.
     * For 1 player this is CAPTUREPOINT_INTERVAL_MS * this value as total time.
     * For 2 players this is CAPTUREPOINT_INTERVAL_MS * (this value / 2) as total time. */
    private static final int CAPTUREPOINT_CAPTURE_INVERVALS = 4;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Variables">
    /** The lot that the game takes place in. */
    private InnectisLot masterLot = null;
    /** The lot that the lobby is located in. */
    private InnectisLot lobbyLot = null;
    /** The lot that the red base is in. */
    private InnectisLot redBase = null;
    /** The lot that the blue base is in. */
    private InnectisLot blueBase = null;
    /** If the game has started within the last 30 seconds. */
    private boolean justStarted = true;
    /** The score required to win the game.
     * For no score limit, value 0 is assigned */
    private int winLimit = 0;
    /** The red teams current score. */
    private int redScore = 0;
    /** The blue teams current score. */
    private int blueScore = 0;
    /** List of all red player names. */
    private List<String> redPlayers;
    /** List of all blue player names. */
    private List<String> bluePlayers;
    /** List containing all capture point objects. */
    private List<CapturePoint> capturePoints;
    /** HashMap with Player Names against Class ID. */
    private HashMap<String, PlayerClass> classList = new HashMap<String, PlayerClass>();
    /** HashMap with Player Names against when they last used their boosts. */
    private HashMap<String, Long> powerItemUses = new HashMap<String, Long>();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">

    public IdpDomination(InnectisLot lot, String owner, int winLimit) {
        super(lot, owner);
        if (winLimit == 0) {
            this.winLimit = 0;
        } else if (winLimit < 5) {
            this.winLimit = 5;
        } else if (winLimit > 1000) {
            this.winLimit = 1000;
        } else {
            this.winLimit = winLimit;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Game Handling">

    @Override
    public void printScore(IdpCommandSender player) {
        player.print(ChatColor.BLUE, "Blue Team: " + ChatColor.YELLOW + blueScore);
        player.print(ChatColor.BLUE, "Players: " + ChatColor.YELLOW + StringUtil.joinString(bluePlayers, ", ", 0));
        player.print(ChatColor.RED, "Red Team: " + ChatColor.YELLOW + redScore);
        player.print(ChatColor.RED, "Players: " + ChatColor.YELLOW + StringUtil.joinString(redPlayers, ", ", 0));
    }

    @Override
    public IdpGameType getGameType() {
        return IdpGameType.DOMINATION;
    }

    @Override
    public boolean addPlayer(IdpPlayer player) {

        if (masterLot != null) {
            // Check if master lot contains the player.
            for (IdpPlayer eachPlayer : masterLot.getPlayersInsideRegion(0)) {
                if (eachPlayer.getName().equalsIgnoreCase(player.getName())) {
                    // Check which team (if any) has least members.
                    if (redPlayers.size() <= bluePlayers.size()) {
                        playerAddRedTeam(eachPlayer);
                    } else {
                        playerAddBlueTeam(eachPlayer);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IdpStartResult canStart() {
        IdpStartResult result = new IdpStartResult(true);

        if (getRegion() == null) {
            result.addError(" - You are not on a lot!", false);
            return result;
        }

        InnectisLot lot = LotHandler.getLot(getRegion(), false);

        if (lot == null) {
            result.addError(" - You are not on a lot!", false);
            return result;
        }

        lot = lot.getParentTop();

        List<InnectisLot> lotList = lot.getSublots();

        if (lotList.isEmpty()) {
            result.addError(" - This lot has no sublots!", false);
            return result;
        }

        capturePoints = new ArrayList<CapturePoint>(20);

        for (InnectisLot targetLot : lotList) {
            // If lot is 1x1x1 (capture point), add to Capture Point list.
            if (targetLot.getArea() == 1) {
                capturePoints.add(new CapturePoint(this, targetLot.getLotName().replace("_", " "), targetLot.getCenter(), CAPTUREPOINT_CAPTURERADIUS, CAPTUREPOINT_CAPTURE_INVERVALS));
            } else if (targetLot.getLotName().equalsIgnoreCase("RedBase")) {
                if (redBase != null) {
                    result.addError(" - Multiple red bases found!", false);
                } else {
                    redBase = targetLot;
                }
            } else if (targetLot.getLotName().equalsIgnoreCase("BlueBase")) {
                if (blueBase != null) {
                    result.addError(" - Multiple blue bases found!", false);
                } else {
                    blueBase = targetLot;
                }
            } else if (targetLot.getLotName().equalsIgnoreCase("Lobby")) {
                if (lobbyLot != null) {
                    result.addError(" - Multiple Lobbys found!", false);
                } else {
                    lobbyLot = targetLot;
                }
            } else {
                result.addError(" - Unknown lot found #" + targetLot.getId(), true);
            }
        }

        if (capturePoints.size() < MIN_CAPTURE_POINTS) {
            result.addError(" - Not enough Capture Points!", false);
        }

        if (redBase == null) {
            result.addError(" - No red base found!", false);
            return result;
        }

        if (blueBase == null) {
            result.addError(" - No blue base found!", false);
            return result;
        }

        if (lobbyLot == null) {
            result.addError(" - No lobby found!", false);
            return result;
        }

        if (redBase.getPlayersInsideRegion(0).size() < MIN_PLAYERS_PER_TEAM) {
            result.addError(" - Not enough players in red base!", false);
        }

        if (blueBase.getPlayersInsideRegion(0).size() < MIN_PLAYERS_PER_TEAM) {
            result.addError(" - Not enough players in blue base!", false);
        }

        masterLot = lot;
        return result;
    }

    @Override
    public void startGame() {
        getPlugin().broadCastMessage(ChatColor.GREEN, "A new domination game has started!");
        if (winLimit != 0) {
            gameMessageAll(ChatColor.GREEN, "The winner is the first team to "
                    + ChatColor.RED + winLimit + ChatColor.GREEN + " points!");
        }

        List<IdpPlayer> redBasePlayers = redBase.getPlayersInsideRegion(0);
        redPlayers = new ArrayList<String>(redBasePlayers.size());
        for (IdpPlayer player : redBasePlayers) {
            playerAddRedTeam(player);
        }

        List<IdpPlayer> blueBasePlayers = blueBase.getPlayersInsideRegion(0);
        bluePlayers = new ArrayList<String>(blueBasePlayers.size());
        for (IdpPlayer player : blueBasePlayers) {
            playerAddBlueTeam(player);
        }

        // Add the capture points
        GameTimerTask pointtask = new GameTimerTask(CAPTUREPOINT_INTERVAL_MS);
        for (CapturePoint point : capturePoints) {
            BlockHandler.setBlock(point.getCaptureBlock(), IdpMaterial.WOOL_WHITE);
            pointtask.addTimer(point);
        }
        getPlugin().getTaskManager().addTask(pointtask);

        // Add timer
        getPlugin().getTaskManager().addTask(new GameTimerTask(30000, this));
    }

    @Override
    public void endGame() {
        super.endGame();
        gameMessageAll(ChatColor.GREEN, "Game #" + getId() + " has ended!");

        if (blueScore == redScore) {
            gameMessageAll(ChatColor.GREEN, "The game ended in a tie:");
        } else if (blueScore > redScore) {
            gameMessageAll(ChatColor.GREEN, "The " + ChatColor.BLUE + "blue" + ChatColor.GREEN + " team won:");
        } else {
            gameMessageAll(ChatColor.GREEN, "The " + ChatColor.RED + "red" + ChatColor.GREEN + " team won:");
        }
        gameMessageAll(ChatColor.BLUE, "Blue Team: " + ChatColor.YELLOW + blueScore);
        gameMessageAll(ChatColor.BLUE, "Players: " + ChatColor.YELLOW + StringUtil.joinString(bluePlayers, ", ", 0));
        gameMessageAll(ChatColor.RED, "Red Team: " + ChatColor.YELLOW + redScore);
        gameMessageAll(ChatColor.RED, "Players: " + ChatColor.YELLOW + StringUtil.joinString(redPlayers, ", ", 0));

        // Let all players leave
        IdpPlayer player;
        for (String playername : redPlayers) {
            player = getPlugin().getPlayer(playername, true);
            player.getSession().setIsInLobby(false);
            cancelTasks(player.getName());
            player.setInventory(player.getWorld().getSettings().getInventoryType());
            player.getSession().reloadPrefix();

            if (player.hasPermission(Permission.command_cheat_gamemode)) {
                player.getHandle().setGameMode(GameMode.CREATIVE);
            } else {
                player.getHandle().setGameMode(GameMode.SURVIVAL);
            }
        }

        for (String playername : bluePlayers) {
            player = getPlugin().getPlayer(playername, true);
            player.getSession().setIsInLobby(false);
            cancelTasks(player.getName());
            player.setInventory(player.getWorld().getSettings().getInventoryType());
            player.getSession().reloadPrefix();

            if (player.hasPermission(Permission.command_cheat_gamemode)) {
                player.getHandle().setGameMode(GameMode.CREATIVE);
            } else {
                player.getHandle().setGameMode(GameMode.SURVIVAL);
            }
        }


        // Clean up capture points
        for (CapturePoint point : capturePoints) {
            BlockHandler.setBlock(point.getCaptureBlock(), IdpMaterial.AIR);
        }

        // Remove the game.
        IdpGameManager.getInstance().removeGame(this.getId());
    }

    @Override
    public List<IdpPlayer> getPlayers() {
        List<IdpPlayer> players = new ArrayList<IdpPlayer>();
        InnPlugin plugin = InnPlugin.getPlugin();

        for (String name : bluePlayers) {
            players.add(plugin.getPlayer(name));
        }

        for (String name : redPlayers) {
            players.add(plugin.getPlayer(name));
        }

        return players;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Events">

    @InnEventMarker(type = InnEventType.PLAYER_LOT_LEAVE)
    public void onPlayerLotLeave(InnPlayerLotLeaveEvent event) {
        // If player has left main lot, eject them from the game.
        if (isPlayerInGame(event.getPlayer())) {
            InnectisLot lot = event.getTo();
            if (!(lot != null && lot.getParentTop().equals(masterLot))) {
                playerLeave(event.getPlayer(), true);
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_QUIT)
    public void onPlayerQuit(InnPlayerQuitEvent event) {
        // Eject them from the game.
        if (isPlayerInGame(event.getPlayer())) {
            playerLeave(event.getPlayer(), true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DEATH)
    public void onPlayerDeath(InnPlayerDeathEvent event) {
        // Hide death message, set invisible, cancel delayed tasks, remove drops.
        if (isPlayerInGame(event.getPlayer())) {
            powerItemUses.remove(event.getPlayer().getName());
            event.setShowDeathMessage(false);
            gameMessageAll(ChatColor.GREEN, event.getDeathMessage());
            event.getPlayer().getSession().setIsInLobby(true);
            cancelTasks(event.getPlayer().getName());
            event.clearDrops();
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_POST_RESPAWN)
    public void onPostRespawn(InnPlayerPostRespawnEvent event) {
        IdpPlayer player = event.getPlayer();

        if (isPlayerInGame(player)) {
            player.setInventory(InventoryType.NO_SAVE); // Lets be sure.
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_RESPAWN)
    public void onPlayerRespawn(InnPlayerRespawnEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setRespawnLocation(lobbyLot.getSpawn()); // Re-Choose class.
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_INVENTORY_CLICK)
    public void onInventoryClick(InnInventoryClickEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true); // Lets cancel this event.
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DROP_ITEM)
    public void onPlayerDropItem(InnPlayerDropItemEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true); // Lets cancel this event.
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_PICKUP_ITEM)
    public void onPlayerPickUpItem(InnPlayerPickupItemEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true); // Lets cancel this event.
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT)
    public void onPlayerInteract(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        PlayerClass playerClass = classList.get(player.getName());
        if (player.getMaterialInHand(EquipmentSlot.HAND) != null
                && playerClass != null) {
            if (player.getMaterialInHand(EquipmentSlot.HAND) == playerClass.getBonus().getMaterial()) {
                if (canUseBoost(player.getName())) {
                    List<IdpPlayer> nearbyTeam = new ArrayList<IdpPlayer>();
                    List<IdpPlayer> nearbyEnemy = new ArrayList<IdpPlayer>();
                    int teamId = getTeamId(player);

                    for (IdpPlayer nearbyPlayer : player.getNearByPlayers(playerClass.getBonusRange())) {
                        if (isPlayerInGame(nearbyPlayer)) {
                            if (getTeamId(nearbyPlayer) == teamId) {
                                nearbyTeam.add(nearbyPlayer);
                            } else {
                                nearbyEnemy.add(nearbyPlayer);
                            }
                        }
                    }

                    if (PlayerClassHandler.handleBonusEffect(player, playerClass.getBonusEffect(), nearbyTeam, nearbyEnemy)) {
                        player.removeItemFromInventory(playerClass.getBonus().getMaterial(), 1);
                        powerItemUses.put(player.getName(), System.currentTimeMillis());
                    }

                    event.setCancelled(true);
                    event.setTerminate(true);
                    return;
                } else {
                    player.printError("You cannot use that for another " + getBoostTime(player.getName()) + " seconds!");
                }
            } else if (player.getMaterialInHand(EquipmentSlot.HAND) == playerClass.getSecondaryWeapon().getMaterial()) {
                List<IdpPlayer> nearbyTeam = new ArrayList<IdpPlayer>();
                List<IdpPlayer> nearbyEnemy = new ArrayList<IdpPlayer>();
                int teamId = getTeamId(player);

                for (IdpPlayer nearbyPlayer : player.getNearByPlayers(playerClass.getBonusRange())) {
                    if (isPlayerInGame(nearbyPlayer)) {
                        if (getTeamId(nearbyPlayer) == teamId) {
                            nearbyTeam.add(nearbyPlayer);
                        } else {
                            nearbyEnemy.add(nearbyPlayer);
                        }
                    }
                }

                PlayerClassHandler.handleBonusEffect(player, playerClass.getSecondaryEffect(), nearbyTeam, nearbyEnemy);
            }
        }

        // Is player choosing class?
        if (player.getSession().isInLobby()) {
            switch (event.getBlockMaterial()) {
                case WALL_SIGN:
                case SIGN_POST:
                    Sign sign = (Sign) event.getBlock().getState();
                    if (sign != null) {
                        playerInteractSign(player, sign);
                    }
            }

            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_INTERACT_ENTITY)
    public void onPlayerInteractEntity(InnInteractEntityEvent event) {
        if (!isPlayerInGame(event.getPlayer())) {
            return;
        }

        // Check if player is choosing class.
        if (event.getEntity() instanceof ItemFrame) {
            if (event.getPlayer().getSession().isInLobby()) {

                // Get the Class sign.
                Block block = event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation()).getRelative(BlockFace.UP);

                // Check if class sign is there.
                if ((block.getState().getType() == Material.WALL_SIGN
                        || block.getState().getType() == Material.SIGN_POST)) {

                    Sign sign = (Sign) block.getState();

                    // Deal with choosing class.
                    if (sign != null) {
                        playerInteractSign(event.getPlayer(), sign);
                    }
                }
            }

            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_CHAT)
    public void onPlayerChat(InnPlayerChatEvent event) {
        if (event.getMessage().startsWith("#")) {
            IdpPlayer player = event.getPlayer();
            if (redPlayers.contains(player.getName())) {
                gameMessageRed(ChatColor.GREEN, "[" + ChatColor.DARK_RED
                        + "Red Team Chat" + ChatColor.GREEN + "] " + player.getColoredName()
                        + ChatColor.WHITE + ": " + event.getMessage().substring(1));
            } else {
                gameMessageBlue(ChatColor.GREEN, "[" + ChatColor.DARK_BLUE
                        + "Blue Team Chat" + ChatColor.GREEN + "] " + player.getColoredName()
                        + ChatColor.WHITE + ": " + event.getMessage().substring(1));
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_TELEPORT)
    public void onPlayerTeleport(InnPlayerTeleportEvent event) {
        InnectisLot lot = event.getLotTo();
        IdpPlayer player = event.getPlayer();

        // Check if location is within lot.
        if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
            if (player.hasPermission(Permission.teleport_force)
                    || (lot != null && lot.getParentTop().equals(masterLot))) {
                // If yes, we'll handle it.
                player.teleport(event.getLocTo(), TeleportType.IGNORE_RESTRICTION, TeleportType.USE_SPAWN_FINDER);
            } else {
                // If no, give back item.
                int slot = player.getHandle().getInventory().getHeldItemSlot();
                IdpPlayerInventory inv = player.getInventory();

                if (player.getItemInHand(EquipmentSlot.HAND) == null || player.getItemInHand(EquipmentSlot.HAND).getAmount() == 0) {
                    inv.setItemAt(slot, new IdpItemStack(IdpMaterial.ENDER_PEARL, 1));
                } else {
                    IdpItemStack stack = player.getItemInHand(EquipmentSlot.HAND);
                    stack.setAmount(stack.getAmount() + 1);
                    inv.setItemAt(slot, stack);
                }
                inv.updateBukkitInventory();
            }
            event.setCancelled(true); // Lets cancel this event.
            event.setTerminate(true);
        } else if (!event.getCause().equals(TeleportCause.END_PORTAL)) {
            // Stop players from teleporting around the game.
            if (!player.getSession().isInLobby()) {
                if (!player.hasPermission(Permission.teleport_force)
                        && (lot != null && lot.getParentTop().equals(masterLot))) {
                    event.setCancelled(true);
                    event.setTerminate(true);
                }
            }
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PROJECTILE)
    public void onPlayerDamageByProjectile(InnPlayerDamageByProjectileEvent event) {
        IdpPlayer damager = event.getDamager();
        IdpPlayer player = event.getPlayer();

        // Was it done by another player?
        if (damager == null) {
            return;
        }

        if (!isPlayerInGame(damager) && !isPlayerInGame(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);

        // Prevent PvP while choosing class, double deaths.
        if (player.getSession().isInLobby()
                || damager.getSession().isInLobby()
                || player.getSession().getLastDeath() > System.currentTimeMillis() + 450) {
            return;
        }

        IdpMaterial weapon = IdpMaterial.AIR;
        boolean isEnemy = ((redPlayers.contains(player.getName())
                && bluePlayers.contains(damager.getName()))
                || (bluePlayers.contains(player.getName())
                && redPlayers.contains(damager.getName())));

        switch (event.getProjectile().getType()) {
            case ARROW:
                weapon = IdpMaterial.BOW;
                break;
            case SNOWBALL:
                weapon = IdpMaterial.SNOWBALL;
                break;
            case EGG:
                weapon = IdpMaterial.EGG;
                break;
            case ENDER_PEARL:
                weapon = IdpMaterial.ENDER_PEARL;
                break;
            case FISHING_HOOK:
                weapon = IdpMaterial.FISHING_ROD;
                break;
            case THROWN_EXP_BOTTLE:
                weapon = IdpMaterial.BOTTLE_O_ENCHANTING;
                break;
            case SPLASH_POTION:
                weapon = IdpMaterial.POTIONS;
                break;
        }

        if (PlayerClassHandler.handlePlayerAttackPlayer(damager, classList.get(damager.getName()),
                weapon, player, classList.get(player.getName()), !isEnemy)) {
            handlePvpDeath(player, damager);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_DAMAGE_BY_PLAYER)
    public void onPlayerDamageByPlayer(InnPlayerDamageByPlayerEvent event) {
        IdpPlayer damager = event.getDamager();
        IdpPlayer player = event.getPlayer();

        // Was it done by another player?
        if (damager == null) {
            return;
        }

        if (!isPlayerInGame(damager) && !isPlayerInGame(player)) {
            return;
        }

        event.setCancelled(true);
        event.setTerminate(true);

        // Prevent PvP while choosing class, double deaths.
        if (player.getSession().isInLobby()
                || damager.getSession().isInLobby()
                || player.getSession().getLastDeath() > System.currentTimeMillis() + 450) {
            return;
        }

        boolean isEnemy = ((redPlayers.contains(player.getName())
                && bluePlayers.contains(damager.getName()))
                || (bluePlayers.contains(player.getName())
                && redPlayers.contains(damager.getName())));

        EquipmentSlot handSlot = damager.getNonEmptyHand();
        IdpMaterial attackMaterial = IdpMaterial.AIR;

        if (handSlot != null) {
            attackMaterial = damager.getMaterialInHand(handSlot);
        }

        if (PlayerClassHandler.handlePlayerAttackPlayer(damager, classList.get(damager.getName()),
                attackMaterial, player, classList.get(player.getName()), !isEnemy)) {
            handlePvpDeath(player, damager);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_PLACE)
    public void onBlockPlaced(InnPlayerBlockPlaceEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @Override
    public void onInterval() {

        // If in first 30 seconds, skip check.
        if (justStarted) {
            justStarted = false;
            return;
        }

        // Assign variables to count score.
        int whiteFlags = 0;
        int redFlags = 0;
        int blueFlags = 0;

        // Lets count up scores.
        for (CapturePoint point : capturePoints) {
            switch (point.getOwnerId()) {
                case 1: {
                    redFlags++;
                    break;
                }
                case 2: {
                    blueFlags++;
                    break;
                }
                case -1: {
                    whiteFlags++;
                    break;
                }
            }
        }

        // Update the scores and broadcast messages.
        gameMessageAll(ChatColor.GREEN,
                "Scores Update: " + ChatColor.BLUE + "Blue Team: " + ChatColor.YELLOW
                + "+" + blueFlags + ChatColor.GREEN + ", "
                + ChatColor.RED + "Red Team: " + ChatColor.YELLOW + "+" + redFlags);

        redScore += redFlags;
        blueScore += blueFlags;

        // If game has win limit, check if its been reached.
        if (winLimit != 0 && (redScore >= winLimit || blueScore >= winLimit)) {
            gameMessageAll(ChatColor.GREEN, "The score limit has been reached!");
            endGame();
            return;
        }

        IdpPlayer player;
        int reward;

        // Deal out power item rewards.
        for (String playerName : classList.keySet()) {
            player = getPlugin().getPlayer(playerName, true);
            if (!player.getSession().isInLobby()
                    && classList.get(player.getName()).getClassType() != PlayerClassType.NONE) {
                if (redPlayers.contains(playerName)) {
                    reward = redFlags;
                } else {
                    reward = blueFlags;
                }

                if (reward > 0) {
                    IdpPlayerInventory inventory = player.getInventory();
                    PlayerClass playerClass = classList.get(player.getName());

                    if (inventory.getItemAt(2) == null
                            || inventory.getItemAt(2).getAmount() < 1) {
                        inventory.setItemAt(2, playerClass.getConsumable());
                    } else {
                        inventory.getItemAt(2).setAmount(Math.min(playerClass.getConsumableMax(),
                                inventory.getItemAt(2).getAmount() + (reward * playerClass.getConsumableIncrease())));
                    }

                    if (inventory.getItemAt(3) == null
                            || inventory.getItemAt(3).getAmount() < 1) {
                        inventory.setItemAt(3, playerClass.getBonus());
                    } else {
                        inventory.getItemAt(3).setAmount(Math.min(playerClass.getBonusMax(),
                                inventory.getItemAt(3).getAmount() + (int) (reward * playerClass.getBonusIncrease())));
                    }

                    player.setInventory(inventory);
                }
            }
        }

        // Broadcast the updated scores.
        gameMessageAll(ChatColor.BLUE, "Blue Team: " + ChatColor.YELLOW + blueScore);
        gameMessageAll(ChatColor.RED, "Red Team: " + ChatColor.YELLOW + redScore);
    }

    @InnEventMarker(type = InnEventType.HANGING_BREAK)
    public void onHangingBreak(InnHangingBreakEvent event) {
        IdpPlayer player = event.getPlayer();

        // Check if player is choosing class.
        if (player.getSession().isInLobby()) {
            if (event.getHangingEntity() instanceof ItemFrame) {

                // Get the Class sign.
                Block block = event.getHangingEntity().getWorld().getBlockAt(event.getHangingEntity().getLocation()).getRelative(BlockFace.UP);
                IdpMaterial mat = IdpMaterial.fromBlock(block);

                // Check if class sign is there.
                if (mat == IdpMaterial.WALL_SIGN || mat == IdpMaterial.SIGN_POST) {
                    Sign sign = (Sign) block.getState();

                    // Deal with choosing class.
                    if (sign != null) {
                        playerInteractSign(event.getPlayer(), sign);
                    }
                }
            }
        }

        if (isPlayerInGame(player)) {
            event.setCancelled(true);
            event.setTerminate(true);
        }
    }

    @InnEventMarker(type = InnEventType.PLAYER_BLOCK_BREAK)
    public void onBlockBreak(InnPlayerBlockBreakEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
            event.setTerminate(true);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Methods">

    /**
     * Remove the individual player from the game.
     * @param player
     */
    private void playerLeave(IdpPlayer player, boolean checkPlayers) {

        // Remove from red team list.
        if (redPlayers.contains(player.getName())) {
            redPlayers.remove(player.getName());
        }

        // Remove from blue team list.
        if (bluePlayers.contains(player.getName())) {
            bluePlayers.remove(player.getName());
        }

        // Clean up variables containing game information.
        player.getSession().setIsInLobby(false);
        player.getSession().reloadPrefix();
        classList.remove(player.getName());
        cancelTasks(player.getName());

        // Restore their inventory.
        player.setInventory(player.getWorld().getSettings().getInventoryType());

        // Restore their game mode.
        if (player.hasPermission(Permission.command_cheat_gamemode)) {
            player.getHandle().setGameMode(GameMode.CREATIVE);
        } else {
            player.getHandle().setGameMode(GameMode.SURVIVAL);
        }

        // Broadcast leaving message.
        gameMessageAll(ChatColor.GREEN, player.getColoredName() + " left the game!");

        // Auto balance the teams.
        if (redPlayers.size() - bluePlayers.size() > 1) {
            // Move 1 from red team to blue team.
            IdpPlayer balancePlayer = getPlugin().getPlayer(redPlayers.get(redPlayers.size() - 1));
            playerLeave(balancePlayer, false);
            addPlayer(balancePlayer);

        } else if (bluePlayers.size() - redPlayers.size() > 1) {
            // Move 1 from blue team to red team.
            IdpPlayer balancePlayer = getPlugin().getPlayer(bluePlayers.get(bluePlayers.size() - 1));
            playerLeave(balancePlayer, false);
            addPlayer(balancePlayer);
        }

        // Check if enough players on red team to continue game.
        if (checkPlayers && redPlayers.size() < MIN_PLAYERS_PER_TEAM) {
            gameMessageAll(ChatColor.GREEN, "Not enough players on red team!");
            endGame();
            return;
        }

        // Check if enough players on blue team to continue game.
        if (checkPlayers && bluePlayers.size() < MIN_PLAYERS_PER_TEAM) {
            gameMessageAll(ChatColor.GREEN, "Not enough players on blue team!");
            endGame();
            return;
        }

        // Broadcast players remaining.
        gameMessageAll(ChatColor.BLUE, "Players: " + ChatColor.YELLOW + StringUtil.joinString(bluePlayers, ", ", 0));
        gameMessageAll(ChatColor.RED, "Players: " + ChatColor.YELLOW + StringUtil.joinString(redPlayers, ", ", 0));

    }

    /**
     * Resets the players inventory according to team and class.
     * @param player
     * @param ArmourOnly
     */
    private void resetInventory(IdpPlayer player, boolean ArmourOnly) {

        if (!ArmourOnly) {
            // Lets ensure the inventory doesn't get saved.
            player.setInventory(InventoryType.NO_SAVE);
            player.setHealth(20);
        }


        // Armour Colour Variable.
        Color COLOUR;

        // Get their teams colour.
        if (redPlayers.contains(player.getName())) {
            COLOUR = Color.RED;
        } else {
            COLOUR = Color.BLUE;
        }

        // Define the armour type.
        IdpItemStack HELMET = new IdpItemStack(IdpMaterial.LEATHER_HELMET, 1);
        IdpItemStack CHEST = new IdpItemStack(IdpMaterial.LEATHER_CHEST, 1);
        IdpItemStack PANTS = new IdpItemStack(IdpMaterial.LEATHER_LEGGINGS, 1);
        IdpItemStack BOOTS = new IdpItemStack(IdpMaterial.LEATHER_BOOTS, 1);

        // Dye the armour correct colour.
        HELMET.getItemdata().setColor(COLOUR);
        CHEST.getItemdata().setColor(COLOUR);
        PANTS.getItemdata().setColor(COLOUR);
        BOOTS.getItemdata().setColor(COLOUR);

        // Equip armour to player.
        player.setHelmet(HELMET);
        player.setChestplate(CHEST);
        player.setLeggings(PANTS);
        player.setBoots(BOOTS);

        // Ensure we update.
        player.getInventory().updateBukkitInventory();

        if (!ArmourOnly) {

            // Now lets deal with the equipment (if applicable).
            IdpPlayerInventory inventory = player.getInventory();
            PlayerClass playerClass = classList.get(player.getName());

            inventory.setItemAt(0, playerClass.getPrimaryWeapon());
            inventory.setItemAt(1, playerClass.getSecondaryWeapon());
            inventory.setItemAt(2, playerClass.getConsumable());
            inventory.setItemAt(3, playerClass.getBonus());
            player.setInventory(inventory);
        }
    }

    private InnPlugin getPlugin() {
        return InnPlugin.getPlugin();
    }

    /**
     * Broadcasts a message to all players in the red team.
     * @param colour
     * @param message
     */
    private void gameMessageRed(ChatColor colour, String message) {
        IdpPlayer player;
        for (String playername : redPlayers) {
            player = getPlugin().getPlayer(playername, true);
            player.print(colour, message);
        }
    }

    /**
     * Broadcasts a message to all players in the blue team.
     * @param colour
     * @param message
     */
    private void gameMessageBlue(ChatColor colour, String message) {
        IdpPlayer player;
        for (String playername : bluePlayers) {
            player = getPlugin().getPlayer(playername, true);
            player.print(colour, message);
        }
    }

    /**
     * Broadcasts a message to all players in the game, on the lot and the owner.
     * @param colour
     * @param message
     */
    private void gameMessageAll(ChatColor colour, String message) {

        // Assign owner check boolean.
        boolean containsOwner = false;

        // Print to all players on master lot.
        for (IdpPlayer player : masterLot.getPlayersInsideRegion(0)) {
            player.print(colour, message);

            // Check to see if owner is on master lot.
            if (player.getName().equals(getGameHost())) {
                containsOwner = true;
            }
        }

        // If owner is not on master lot, send message.
        if (!containsOwner) {
            IdpPlayer owner = getPlugin().getPlayer(getGameHost(), true);
            if (owner != null && owner.isOnline()) {
                owner.print(colour, message);
            }
        }
    }

    /**
     * Checks if the given player is either in the red or blue team
     * @param player
     * @return boolean
     */
    private boolean isPlayerInGame(IdpPlayer player) {
        return redPlayers.contains(player.getName()) || bluePlayers.contains(player.getName());
    }

    /**
     * Checks if the player was killed in PvP.
     * @param player
     * @param damager
     */
    private void handlePvpDeath(IdpPlayer player, IdpPlayer damager) {

        if (redPlayers.contains(damager.getName())) {
            redScore++; // Increase red Score.
            // gameMessageAll(ChatColor.GREEN, "Red team scored a point!");
        } else {
            blueScore++; // Increase blue Score.
            // gameMessageAll(ChatColor.GREEN, "Blue team scored a point!");
        }

        // If game has win limit, check if its been reached.
        if (winLimit != 0 && (redScore >= winLimit || blueScore >= winLimit)) {
            gameMessageAll(ChatColor.GREEN, "The score limit has been reached!");
            endGame();
        }
    }

    /**
     * Will look up the team id of the given player.
     * @param player
     * @return -1 if player not in team
     */
    public int getTeamId(IdpPlayer player) {
        if (redPlayers.contains(player.getName())) {
            return 1; // Red Team
        }        if (bluePlayers.contains(player.getName())) {
            return 2; // Blue Team
        }
        return -1; // No Team
    }

    /**
     * Gets the teams flag material.
     * @param teamid
     * @return
     */
    public IdpMaterial getTeamMaterial(int teamid) {
        switch (teamid) {
            case 1: // Red team
                return IdpMaterial.WOOL_RED;
            case 2: // Blue Team
                return IdpMaterial.WOOL_BLUE;
            default: // Neutral
                return IdpMaterial.WOOL_WHITE;
        }
    }

    /**
     * This will capture a point for the given team. <br/>
     * If the point is for the other team, it will first be set to white.
     * @param capturepoint
     * @param teamid
     */
    public void changePoint(CapturePoint capturepoint, int teamid) {

        // Get what the capture point currently is.
        Block flagblock = capturepoint.getCaptureBlock();

        // Get the capture points name.
        String pointname = StringUtil.stringIsNullOrEmpty(capturepoint.getPointName())
                ? "a control point!" : capturepoint.getPointName() + "!";

        IdpMaterial mat = IdpMaterial.fromBlock(flagblock);

        switch (mat) {
            case WOOL_WHITE: // If is neutral.
                switch (teamid) {
                    case 1: // Red
                        BlockHandler.setBlock(flagblock, IdpMaterial.WOOL_RED);
                        gameMessageAll(ChatColor.GREEN, "Red team captured " + pointname);
                        break;
                    case 2: // Blue
                        BlockHandler.setBlock(flagblock, IdpMaterial.WOOL_BLUE);
                        gameMessageAll(ChatColor.GREEN, "Blue team captured " + pointname);
                        break;
                }
                break;
            case WOOL_RED: // If is owned by red team.
                if (teamid != 1) { // ensure blue team is capturing.
                    BlockHandler.setBlock(flagblock, IdpMaterial.WOOL_WHITE);

                    gameMessageAll(ChatColor.GREEN, "Blue team neutralized " + pointname);
                }
                break;
            case WOOL_BLUE: // If is owned by blue team.
                if (teamid != 2) { // Ensure red team is capturing.
                    BlockHandler.setBlock(flagblock, IdpMaterial.WOOL_WHITE);

                    gameMessageAll(ChatColor.GREEN, "Red team neutralized " + pointname);
                }
                break;
        }
    }

    /**
     * Adds the player to the red team.
     * @param IdpPlayer
     */
    private void playerAddRedTeam(IdpPlayer player) {

        // Add player to game.
        redPlayers.add(player.getName());

        // Assign Prefix.
        final Prefix RED_PREFIX = new Prefix("Domination #" + getId(), ChatColor.RED);
        player.getSession().setPrefix(1, RED_PREFIX, false);

        // Send player to Lobby.
        player.teleport(lobbyLot.getSpawn(), TeleportType.IGNORE_RESTRICTION);
        player.getSession().setIsInLobby(true);

        // Send out messages.
        player.printInfo("You have been placed on the Red team!");

        // Make mortal.
        player.getHandle().setGameMode(GameMode.ADVENTURE);
        player.getSession().setGodmode(false);

        // Deal with inventory.
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
    }

    /**
     * Adds the player to the blue team.
     * @param player
     */
    private void playerAddBlueTeam(IdpPlayer player) {


        // Add player to game.
        bluePlayers.add(player.getName());

        // Assign prefix.
        final Prefix BLUE_PREFIX = new Prefix("Domination #" + getId(), ChatColor.BLUE);
        player.getSession().setPrefix(1, BLUE_PREFIX, false);

        // Send player to Lobby.
        player.teleport(lobbyLot.getSpawn(), TeleportType.IGNORE_RESTRICTION);
        player.getSession().setIsInLobby(true);

        // Send out messages.
        player.printInfo("You have been placed on the Blue team!");

        // Make mortal.
        player.getHandle().setGameMode(GameMode.ADVENTURE);
        player.getSession().setGodmode(false);

        // Deal with inventory.
        player.saveInventory();
        player.setInventory(InventoryType.NO_SAVE);
    }

    /**
     * Handles player picking a class.
     * @param player
     * @param sign
     */
    private void playerInteractSign(IdpPlayer player, Sign sign) {

        // Make sure player is alive before setting class.
        if (player.getSession().getPlayerStatus() == PlayerStatus.ALIVE_PLAYER) {

            // Ensure sign is a class sign.
            if (sign.getLine(0).equalsIgnoreCase("[class]")) {
                PlayerClassType classType;
                String classTitle = sign.getLine(1);

                // If player chooses random, pick random class.
                if (classTitle.equalsIgnoreCase("random")) {
                    classType = PlayerClassHandler.getRandomClassType();
                } else {
                    classType = PlayerClassType.lookup(classTitle);
                    if (classType == PlayerClassType.NONE) {
                        player.printError("Class not found!");
                        return;
                    }
                }

                try {
                    classList.put(player.getName(), classType.getHandle().newInstance());
                } catch (InstantiationException ex) {
                    Logger.getLogger(IdpDomination.class.getName()).log(Level.SEVERE, null, ex);
                    player.printError("Error handling class.");
                    return;
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(IdpDomination.class.getName()).log(Level.SEVERE, null, ex);
                    player.printError("Error handling class.");
                    return;
                }

                // Build up inventory, remove from lobby and enter game.
                resetInventory(player, false);
                player.teleport((redPlayers.contains(player.getName()) ? redBase.getSpawn() : blueBase.getSpawn()), TeleportType.IGNORE_RESTRICTION);
                player.getSession().setIsInLobby(false);
                player.getSession().setPvPImmuneTime(5);
            }
        } else {
            player.printError("You cannot use this. Try moving first.");
        }
    }

    /**
     * Cancels Player Effects delayed tasks.
     * @param player
     */
    private void cancelTasks(String player) {
        IdpPlayer idpPlayer = getPlugin().getPlayer(player);
        if (idpPlayer.getSession().getActiveTask() != 0) {

            // Cancel the task.
            getPlugin().getTaskManager().removeTask(idpPlayer.getSession().getActiveTask());

            // Ensure to remove from list.
            idpPlayer.getSession().setActiveTask(0);
        }
    }

    /**
     * Checks if a player can use their boost item yet.
     * @param player
     * @return
     */
    private boolean canUseBoost(String player) {
        if (powerItemUses.containsKey(player) && classList.containsKey(player)
                && (powerItemUses.get(player) + classList.get(player).getBonusCooldown()) > System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    /**
     * Gets the amount of time in seconds until the player can use their boost again.
     * @param player
     * @return
     */
    private int getBoostTime(String player) {
        if (powerItemUses.containsKey(player)
                && classList.containsKey(player)) {
            return (int) (((powerItemUses.get(player) + classList.get(player).getBonusCooldown())
                    - System.currentTimeMillis()) / 1000);
        }
        return 0;
    }
    // </editor-fold>

}

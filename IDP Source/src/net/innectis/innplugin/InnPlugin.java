package net.innectis.innplugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.system.bans.BanHandler;
import net.innectis.innplugin.system.command.CommandManager;
import net.innectis.innplugin.system.command.commands.AdminCommands;
import net.innectis.innplugin.system.command.commands.ChatCommands;
import net.innectis.innplugin.system.command.commands.CheatCommands;
import net.innectis.innplugin.system.command.commands.GameCommands;
import net.innectis.innplugin.system.command.commands.InformationCommands;
import net.innectis.innplugin.system.command.commands.LocationCommands;
import net.innectis.innplugin.system.command.commands.LotCommands;
import net.innectis.innplugin.system.command.commands.MiscCommands;
import net.innectis.innplugin.system.command.commands.ModerationCommands;
import net.innectis.innplugin.system.command.commands.PlayerCommands;
import net.innectis.innplugin.system.command.commands.RequestCommands;
import net.innectis.innplugin.system.command.commands.ShopCommands;
import net.innectis.innplugin.system.command.commands.SpoofCommands;
import net.innectis.innplugin.system.command.commands.TinyWECommands;
import net.innectis.innplugin.external.ExternalLibraryManager;
import net.innectis.innplugin.system.game.IdpGameManager;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.CustomImageHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler;
import net.innectis.innplugin.handlers.StaffMessageHandler;
import net.innectis.innplugin.handlers.TrashHandler;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.holiday.HolidayModuleManager;
import net.innectis.innplugin.objects.LotFlagToggle;
import net.innectis.innplugin.objects.MenuItem;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.IListenerManager;
import net.innectis.innplugin.listeners.ListenerManagerImpl;
import net.innectis.innplugin.listeners.ReadOnlyListenerManagerImpl;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.OwnedEntityHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.renames.PlayerRenameHandler;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.specialitem.SpecialItemManager;
import net.innectis.innplugin.system.shop.ChestShopLotManager;
import net.innectis.innplugin.tasks.async.AsyncMaintenanceTask;
import net.innectis.innplugin.tasks.async.LotReminder;
import net.innectis.innplugin.tasks.async.LotflagToggles;
import net.innectis.innplugin.tasks.async.MessageTask;
import net.innectis.innplugin.tasks.async.PvpCleanup;
import net.innectis.innplugin.tasks.async.QuotaCleanup;
import net.innectis.innplugin.tasks.async.ServerCrashChecker;
import net.innectis.innplugin.tasks.async.ServerRestartTimer;
import net.innectis.innplugin.tasks.async.SessionCleanup;
import net.innectis.innplugin.tasks.sync.MapCleanup;
import net.innectis.innplugin.tasks.sync.PlayerInfoTask;
import net.innectis.innplugin.tasks.sync.PlayerSave;
import net.innectis.innplugin.tasks.sync.SyncUnloadWorldTask;
import net.innectis.innplugin.tasks.TaskManager;
import net.innectis.innplugin.tasks.TaskerStillRunningException;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.ErrorReporter;
import net.innectis.innplugin.util.MagicValueUtil;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 */
public class InnPlugin extends JavaPlugin {

    /** IDP logger */
    private static Logger logger = null;
    /** Instance of the plugin itself */
    protected static InnPlugin innplugin;
    /** Checks if the server is in readonly mode */
    private Boolean readOnly = false;
    /** The Console where we can print messages to. */
    private static IdpConsole console;
    /** The taskmanager, that handles the tasks that are registered */
    private TaskManager taskmanager;
    /** Checks if the server is shutting down. */
    private static boolean shutdown = false;
    /** Manager for the holiday modules */
    private HolidayModuleManager holidaymanager;
    /** Manager for some worldedit functions */
    private ExternalLibraryManager externalLibraryManager;
    private SpecialItemManager effectmanager;
    /** The manager for the listeners */
    private IListenerManager listenerManager;
    // /Listeners
    // SEPERATOR
    private HashSet<IdpMaterial> _ignoreBlocksLoS = new HashSet<IdpMaterial>(65);
    private HashSet<IdpMaterial> _allBlocksLoS = new HashSet<IdpMaterial>(256);
    private Map<String, Long> _adminMsgSpam = Collections.synchronizedMap(new HashMap<String, Long>(200));
    private Map<Long, LotFlagToggle> _lotFlagToggles = Collections.synchronizedMap(new HashMap<Long, LotFlagToggle>(10));
    private Map<UUID, IdpInventory> _backpackViews = new HashMap<UUID, IdpInventory>();
    public CommandManager commandManager;
    public MenuItem helpMenu;
    private List<Long> timers = new LinkedList<Long>();
    private Scoreboard vannishedPlayersScoreboard;
    private Team vannishedPlayersTeam, hiddenNametagPlayersTeam;

    // Reference to the server crash checker
    ServerCrashChecker crashChecker = null;

    // Cache for player lookups
    private HashMap<String, IdpPlayer> cachedPlayers = new HashMap<String, IdpPlayer>();

    // for synchronization
    private final Object _lock = new Object();
    private final Object _playerDisconnectLock = new Object();

    public InnPlugin() {

    }

    @Override
    public void onEnable() {
        logger = super.getLogger();
        console = new IdpConsole(this, getServer().getConsoleSender());
        innplugin = this;
        this.readOnly = false;

        try {
            this.getServer().setDefaultGameMode(GameMode.SURVIVAL);

            logInfo("Connecting to database...");
            if (!DBManager.checkConnection()) {
                this.readOnly = true;
                logError("COULD NOT CONNECT TO DATABASE!");
            }

            logInfo("Loading worlds...");
            IdpWorldFactory.initializeWorlds(this.getServer());

            PlayerGroup.registerPermissions();

            logInfo("Loading external libraries...");
            externalLibraryManager = new ExternalLibraryManager();
            externalLibraryManager.registerExternalLibraries(this);

            loadInnectisOwnedObjects();
            loadManagersAndModules();
            loadScoreboards();

            ChatChannelHandler.loadChannelNames();
            registerCommands();
            registerRecipes();
            registerTasks();
            TrashHandler.loadTrashItems();
            PlayerRenameHandler.loadPlayerRenames();
            setupLineOfSightRestrictions();
            ChestShopLotManager.loadChestShopLots();

            int imagesLoaded = CustomImageHandler.loadCustomMapImages(super.getServer());

            if (imagesLoaded > 0) {
                logInfo("Loaded " + imagesLoaded + " custom map images");
            }

            //generateResworldSpawn();
            ModifiablePermissionsHandler.removeInvalidPermissions();

            logInfo("Loading complete! No errors!");
        } catch (Throwable ex) {
            this.readOnly = true;
            logError("Exception occured while loading IDP!", ex);
        }

        if (this.readOnly == true) {
            for (int i = 0; i < 10; i++) {
                //catch our attention with lots of msgs!
                logError("THE SERVER IS STARTED IN READ-ONLY MODE!");
            }

            listenerManager = new ReadOnlyListenerManagerImpl();
            // Start the readonly message thread
            ReadOnlyThread.start(this);
        } else {
            listenerManager = new ListenerManagerImpl();
        }

        // Register the listeners
        listenerManager.registerMainListeners(this);

        // Check if we have a live server
        java.io.File livefile = new java.io.File(Configuration.PATH_DATAFOLDER + "liveserver.txt");
        Configuration.PRODUCTION_SERVER = livefile.exists();

        // Log some last info
        logInfo(Configuration.PLUGIN_NAME + " version " + Configuration.PLUGIN_VERSION + " is enabled!");
        logInfo("Debug mode is: " + (isDebugEnabled() ? ChatColor.RED + "ON" : ChatColor.GREEN + "OFF"));
        logInfo("Server mode is: " + (Configuration.PRODUCTION_SERVER ? ChatColor.RED + "LIVE" : ChatColor.GREEN + "DEVELOPMENT"));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDisable() {
        shutdown = true;

        taskmanager.stopTasker();
        taskmanager.executeAllTasks();

        //save inventories, remove lights, etc:
        logInfo("Saving all online players...");
        for (IdpPlayer player : getOnlinePlayers()) {
            player.logout();
        }

        logInfo("All online players saved!");

        IdpGameManager.getInstance().endAllGames();

        ChunkDatamanager.reclaimAllChunks();

        LotHandler.saveLots();
        ChestHandler.saveChests();
        DoorHandler.saveDoors();
        DBManager.closeConnection();

        // We need to close the block logger
        BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
        blockLogger.closeLogger();

        long trashStartTime = TrashHandler.getTrashStartTime();

        // Delete all the trash if it is marked for deletion
        if (trashStartTime > 0 && (System.currentTimeMillis() - trashStartTime) > TrashHandler.TRASH_LIFE_TIME) {
            TrashHandler.deleteTrashItems();
            TrashHandler.setTrashStartTime(0L);
        } else {
            TrashHandler.saveTrashItems();
        }

        logger.log(Level.WARNING, new StringBuilder(Configuration.MESSAGE_PREFIX).append("Shutting plugin ").append(Configuration.PLUGIN_NAME).append(" down now!").toString());
    }

    /**
     * This will check if the server is currently shutting down.
     * @return
     */
    public static boolean isShuttingDown() {
        return shutdown;
    }

    // <editor-fold defaultstate="collapsed" desc="Startup methods">
    /**
     * Loads things such as lots, chests, doors, waypoints, etc
     */
    private void loadInnectisOwnedObjects() {
        logCustom(ChatColor.AQUA, "Loading lots...");

        if (!LotHandler.loadLots()) {
            this.readOnly = true;
        }

        logCustom(ChatColor.AQUA, "Loading chests...");

        if (!ChestHandler.loadChests()) {
            this.readOnly = true;
        }

        logCustom(ChatColor.AQUA, "Loading doors...");

        if (!DoorHandler.loadDoors()) {
            this.readOnly = true;
        }

        logCustom(ChatColor.AQUA, "Loading waypoints...");

        if (!WaypointHandler.loadWaypoints()) {
            this.readOnly = true;
        }

        logCustom(ChatColor.AQUA, "Loading trapdoors...");

        if (!TrapdoorHandler.loadTrapdoors()) {
            this.readOnly = true;
        }

        logCustom(ChatColor.AQUA, "Loading owned entities...");

        if (!OwnedEntityHandler.loadOwnedEntities()) {
            this.readOnly = true;
        }
    }

    /**
     * Load IDP commands
     */
    private void registerCommands() {
        logCustom(ChatColor.AQUA, "Registering commands...");
        commandManager.initialize();

        commandManager.registerCommandClass(AdminCommands.class);
        commandManager.registerCommandClass(ChatCommands.class);
        commandManager.registerCommandClass(CheatCommands.class);
        commandManager.registerCommandClass(GameCommands.class);
        commandManager.registerCommandClass(InformationCommands.class);

        commandManager.registerCommandClass(LocationCommands.class);
        commandManager.registerCommandClass(LotCommands.class);
        commandManager.registerCommandClass(MiscCommands.class);
        commandManager.registerCommandClass(ModerationCommands.class);
        commandManager.registerCommandClass(PlayerCommands.class);

        commandManager.registerCommandClass(RequestCommands.class);
        commandManager.registerCommandClass(ShopCommands.class);
        commandManager.registerCommandClass(SpoofCommands.class);
        commandManager.registerCommandClass(TinyWECommands.class);
    }

    /**
     * Unique IDP crafting recipes
     */
    private void registerRecipes() {
        logCustom(ChatColor.AQUA, "Registering crafting recipes...");

        Server server = getServer();

        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 5)).addIngredient(Material.MINECART));
        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 2)).addIngredient(Material.IRON_DOOR));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.OAK_PLANK, 2)).addIngredient(Material.WOOD_DOOR));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.SPRUCE_PLANK, 2)).addIngredient(Material.SPRUCE_DOOR_ITEM));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.BIRCH_PLANK, 2)).addIngredient(Material.BIRCH_DOOR_ITEM));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.JUNGLE_PLANK, 2)).addIngredient(Material.JUNGLE_DOOR_ITEM));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.ACACIA_PLANK, 2)).addIngredient(Material.ACACIA_DOOR_ITEM));
        server.addRecipe(new ShapelessRecipe(MagicValueUtil.materialAmountToItemStack(IdpMaterial.DARK_OAK_PLANK, 2)).addIngredient(Material.DARK_OAK_DOOR_ITEM));
        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.WOOD, 3)).addIngredient(Material.TRAP_DOOR));
        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 4)).addIngredient(Material.IRON_TRAPDOOR));
        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 3)).addIngredient(Material.BUCKET));
        server.addRecipe(new ShapelessRecipe(new ItemStack(Material.QUARTZ, 4)).addIngredient(Material.QUARTZ_BLOCK));
    }

    /**
     * IDP Tasking
     */
    private void registerTasks() {
        logCustom(ChatColor.AQUA, "Registering tasks...");
        //// ASYNC tasks
        // dynmap below
        //taskmanager.addTask(new ChannelActivitySaveTask(innplugin));
        taskmanager.addTask(new LotReminder(innplugin));
        taskmanager.addTask(new LotflagToggles(innplugin));
        taskmanager.addTask(new AsyncMaintenanceTask(innplugin));
        taskmanager.addTask(new MessageTask());
        taskmanager.addTask(new PlayerInfoTask(innplugin));
        taskmanager.addTask(new PlayerSave(innplugin));
        taskmanager.addTask(new PvpCleanup(innplugin));
        taskmanager.addTask(new QuotaCleanup(innplugin));
        taskmanager.addTask(new SessionCleanup());

        // Do not do this in debug mode!
        if (!isDebugEnabled()) {
            taskmanager.addTask(new ServerRestartTimer(innplugin));

            crashChecker = new ServerCrashChecker(this);
            taskmanager.addTask(crashChecker);
        }


        //// SYNC tasks
        //taskmanager.addTask(new SyncMaintenanceTask(innplugin));
        // LotRegen -> 'Spawned Task'
        taskmanager.addTask(new MapCleanup(innplugin));
        taskmanager.addTask(new SyncUnloadWorldTask(innplugin));
        // RegionEdit -> 'Spawned Task'
        // RegionRegeneration -> 'Spawned Task'


        try {
            taskmanager.startTasker();
        } catch (TaskerStillRunningException ex) {
            logError("Tried to start tasker, but it was already running? Lynxy what did you do?", ex);
        }
    }

    private void generateResworldSpawn() {
        logCustom(ChatColor.AQUA, "Generating resworld spawn...");
        World world = Bukkit.getWorld(IdpWorldType.RESWORLD.worldname);
        int height = world.getHighestBlockYAt(0, 0) - 2;
        IdpRegion region = new IdpRegion(new Vector(10, 128, 10), new Vector(-10, 0, -10));
        for (Block block : BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID).getBlockList(region, world, null)) {
            int x = Math.abs(block.getX());
            int y = block.getY();
            int z = Math.abs(block.getZ());
            if (y == 0) {
                BlockHandler.setBlock(block, IdpMaterial.BEDROCK);
            } else if ((y <= height || y == height + 4) && (x == 10 || z == 10)) {
                BlockHandler.setBlock(block, IdpMaterial.BRICK);
            } else if (y <= height) {
                if (x == 2 || z == 2 || x == 6 || z == 6 || x == 9 || z == 9) {
                    BlockHandler.setBlock(block, IdpMaterial.GLOWSTONE);
                } else {
                    BlockHandler.setBlock(block, IdpMaterial.OAK_PLANK);
                }
            } else if (y <= height + 4 && (x == 10 && z == 10)) {
                if (y == height + 2) {
                    if (WaypointHandler.getWaypoint(block.getLocation(), false) == null) {
                        try {
                            PlayerCredentials credentials = PlayerCredentialsManager.getByName("Hret");
                            InnectisWaypoint waypoint = WaypointHandler.createWaypoint(credentials, block, InnectisWaypoint.CostType.NO_COST);
                            waypoint.setDestination(new Location(world, block.getX() * 50, 128, block.getZ() * 50));
                            waypoint.save();
                        } catch (SQLException ex) {
                            InnPlugin.logError("Unable to create reszone spawn waypoint!", ex);
                        }
                    } else {
                        BlockHandler.setBlock(block, IdpMaterial.LAPIS_LAZULI_OREBLOCK);
                    }
                } else {
                    BlockHandler.setBlock(block, IdpMaterial.BRICK);
                }
            } else if (y == height + 1 && (x == 10 || z == 10)) {
                BlockHandler.setBlock(block, IdpMaterial.GLASS_PANE);
            } else {
                BlockHandler.setBlock(block, IdpMaterial.AIR);
            }
        }

        logCustom(ChatColor.AQUA, "... resworld spawn generated!");
    }

    private void loadManagersAndModules() {
        logCustom(ChatColor.AQUA, "--> Loading managers and modules");

        // Setup task Manager
        taskmanager = new TaskManager(this);
        commandManager = new CommandManager();

        BanHandler.loadBans();
        BanHandler.loadWhitelist();

        Configuration.loadBannedWords();
        StaffMessageHandler.loadStaffRequests();
        WarpHandler.initializeWarps();

        this.holidaymanager = new HolidayModuleManager(this);
        this.effectmanager = new SpecialItemManager(this);

        // Check for valid holiday module now
        this.holidaymanager.lookupModule();

        logCustom(ChatColor.AQUA, "<-- Managers and modules loaded!");
    }

    private void loadScoreboards() {

        // Setup team for players hidden via /vanish command.
        vannishedPlayersScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        vannishedPlayersTeam = vannishedPlayersScoreboard.registerNewTeam("vanishedPlayers");
        vannishedPlayersTeam.setCanSeeFriendlyInvisibles(true);

        hiddenNametagPlayersTeam = vannishedPlayersScoreboard.registerNewTeam("hiddenNametag");
        hiddenNametagPlayersTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    /**
     * Sets up the line of sight restrictions
     */
    private void setupLineOfSightRestrictions() {
        // Uncategorized
        _ignoreBlocksLoS.add(IdpMaterial.AIR);
        _ignoreBlocksLoS.add(IdpMaterial.BED_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.FLOWERPOT_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.FIRE);
        _ignoreBlocksLoS.add(IdpMaterial.CAKE);
        _ignoreBlocksLoS.add(IdpMaterial.IRON_BARS);
        _ignoreBlocksLoS.add(IdpMaterial.WEB);
        _ignoreBlocksLoS.add(IdpMaterial.SEEDS);
        _ignoreBlocksLoS.add(IdpMaterial.SKULL_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.TORCH);
        _ignoreBlocksLoS.add(IdpMaterial.WALL_SIGN);
        _ignoreBlocksLoS.add(IdpMaterial.SIGN_POST);
        _ignoreBlocksLoS.add(IdpMaterial.SNOW_LAYER);
        _ignoreBlocksLoS.add(IdpMaterial.GLASS_PANE);
        _ignoreBlocksLoS.add(IdpMaterial.GLASS_PANE_WHITE); // Handles all glass panes with ID
        _ignoreBlocksLoS.add(IdpMaterial.LILY_PAD);
        _ignoreBlocksLoS.add(IdpMaterial.COBBLESTONE_WALL);
        _ignoreBlocksLoS.add(IdpMaterial.COBBLESTONE_MOSSY_WALL);
        _ignoreBlocksLoS.add(IdpMaterial.WALL_MOUNT_BANNER);
        _ignoreBlocksLoS.add(IdpMaterial.FREE_STAND_BANNER);
        _ignoreBlocksLoS.add(IdpMaterial.END_ROD);

        // Portal blocks
        _ignoreBlocksLoS.add(IdpMaterial.PORTAL);
        _ignoreBlocksLoS.add(IdpMaterial.END_PORTAL);

        // Water / lava
        _ignoreBlocksLoS.add(IdpMaterial.WATER);
        _ignoreBlocksLoS.add(IdpMaterial.STATIONARY_WATER);
        _ignoreBlocksLoS.add(IdpMaterial.LAVA);
        _ignoreBlocksLoS.add(IdpMaterial.STATIONARY_LAVA);

        // Fences / gates
        _ignoreBlocksLoS.add(IdpMaterial.OAK_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.BIRCH_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.SPRUCE_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.JUNGLE_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.ACACIA_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.DARK_OAK_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.NETHER_BRICK_FENCE);
        _ignoreBlocksLoS.add(IdpMaterial.OAK_FENCE_GATE);
        _ignoreBlocksLoS.add(IdpMaterial.BIRCH_FENCE_GATE);
        _ignoreBlocksLoS.add(IdpMaterial.SPRUCE_FENCE_GATE);
        _ignoreBlocksLoS.add(IdpMaterial.JUNGLE_FENCE_GATE);
        _ignoreBlocksLoS.add(IdpMaterial.ACACIA_FENCE_GATE);
        _ignoreBlocksLoS.add(IdpMaterial.DARK_OAK_FENCE_GATE);

        // Crops, plants, saplings
        _ignoreBlocksLoS.add(IdpMaterial.OAK_SAPLING); // Saplings with ID 6
        _ignoreBlocksLoS.add(IdpMaterial.CARROT_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.POTATO_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.PUMPKIN_STEM);
        _ignoreBlocksLoS.add(IdpMaterial.MELON_STEM);
        _ignoreBlocksLoS.add(IdpMaterial.DANDELION);
        _ignoreBlocksLoS.add(IdpMaterial.POPPY);
        _ignoreBlocksLoS.add(IdpMaterial.NETHER_WART);
        _ignoreBlocksLoS.add(IdpMaterial.COCOA_PLANT);
        _ignoreBlocksLoS.add(IdpMaterial.TALL_GRASS);
        _ignoreBlocksLoS.add(IdpMaterial.SHRUBS);
        _ignoreBlocksLoS.add(IdpMaterial.SUGAR_CANE);
        _ignoreBlocksLoS.add(IdpMaterial.WHEAT_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.FARMLAND);
        _ignoreBlocksLoS.add(IdpMaterial.BROWN_MUSHROOM);
        _ignoreBlocksLoS.add(IdpMaterial.RED_MUSHROOM);
        _ignoreBlocksLoS.add(IdpMaterial.CHORUS_PLANT);
        _ignoreBlocksLoS.add(IdpMaterial.BEETROOT_BLOCK);

        // Slabs
        _ignoreBlocksLoS.add(IdpMaterial.STONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.SANDSTONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.COBBLE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.STONE_BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.NETHER_BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.QUARTZ_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_STONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_SANDSTONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_COBBLE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_STONE_BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_NETHER_BRICK_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_QUARTZ_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.OAK_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.BIRCH_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.SPRUCE_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.JUNGLE_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.ACACIA_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.DARK_OAK_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_OAK_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_BIRCH_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_SPRUCE_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_JUNGLE_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_ACACIA_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_DARK_OAK_WOOD_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.RED_SANDSTONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_RED_SANDSTONE_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.PURPUR_SLAB);
        _ignoreBlocksLoS.add(IdpMaterial.UP_PURPUR_SLAB);

        // Restone-based
        _ignoreBlocksLoS.add(IdpMaterial.LIGHT_PRESSURE_PLATE);
        _ignoreBlocksLoS.add(IdpMaterial.HEAVY_PRESSURE_PLATE);
        _ignoreBlocksLoS.add(IdpMaterial.STONE_PRESSURE_PLATE);
        _ignoreBlocksLoS.add(IdpMaterial.WOODEN_PRESSURE_PLATE);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_COMPARATOR_OFF);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_COMPARATOR_ON);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_REPEATER_OFF);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_REPEATER_ON);
        _ignoreBlocksLoS.add(IdpMaterial.DAYLIGHT_DETECTOR);
        _ignoreBlocksLoS.add(IdpMaterial.INVERTED_DAY_DETECTOR);
        _ignoreBlocksLoS.add(IdpMaterial.STONE_BUTTON);
        _ignoreBlocksLoS.add(IdpMaterial.WOOD_BUTTON);
        _ignoreBlocksLoS.add(IdpMaterial.TRAP_DOOR);
        _ignoreBlocksLoS.add(IdpMaterial.IRON_TRAP_DOOR);
        _ignoreBlocksLoS.add(IdpMaterial.TRIPWIRE);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_TORCH_OFF);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_TORCH_ON);
        _ignoreBlocksLoS.add(IdpMaterial.LEVER);
        _ignoreBlocksLoS.add(IdpMaterial.REDSTONE_WIRE);
        _ignoreBlocksLoS.add(IdpMaterial.PISTON_EXTENSION);
        _ignoreBlocksLoS.add(IdpMaterial.PISTON_MOVING_PIECE);
        _ignoreBlocksLoS.add(IdpMaterial.TRIPWIRE_HOOK);

        // Rails
        _ignoreBlocksLoS.add(IdpMaterial.RAILS);
        _ignoreBlocksLoS.add(IdpMaterial.ACTIVATOR_RAIL);
        _ignoreBlocksLoS.add(IdpMaterial.POWERED_RAIL);
        _ignoreBlocksLoS.add(IdpMaterial.DETECTOR_RAIL);

        // Stairs
        _ignoreBlocksLoS.add(IdpMaterial.WOODEN_STAIRS); // Includes stairs with ID 53
        _ignoreBlocksLoS.add(IdpMaterial.SPRUCE_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.BIRCH_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.JUNGLE_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.ACACIA_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.DARK_OAK_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.COBBLESTONE_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.BRICK_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.STONE_BRICK_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.NETHER_BRICK_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.SANDSTONE_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.QUARTZ_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.RED_SANDSTONE_STAIRS);
        _ignoreBlocksLoS.add(IdpMaterial.PURPUR_STAIRS);

        // Doors
        _ignoreBlocksLoS.add(IdpMaterial.OAK_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.BIRCH_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.SPRUCE_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.JUNGLE_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.ACACIA_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.DARK_OAK_DOOR_BLOCK);
        _ignoreBlocksLoS.add(IdpMaterial.IRON_DOOR_BLOCK);

        // Carpets
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_WHITE);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_ORANGE);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_MAGENTA);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_LIGHTBLUE);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_YELLOW);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_LIGHTGREEN);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_PINK);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_GRAY);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_LIGHTGRAY);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_CYAN);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_PURPLE);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_BLUE);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_BROWN);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_DARKGREEN);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_RED);
        _ignoreBlocksLoS.add(IdpMaterial.CARPET_BLACK);

        // Containers
        _ignoreBlocksLoS.add(IdpMaterial.CHEST);
        _ignoreBlocksLoS.add(IdpMaterial.TRAPPED_CHEST);
        _ignoreBlocksLoS.add(IdpMaterial.ENDER_CHEST);
        _ignoreBlocksLoS.add(IdpMaterial.BREWING_STAND);

        // Hanging blocks
        _ignoreBlocksLoS.add(IdpMaterial.VINES);
        _ignoreBlocksLoS.add(IdpMaterial.LADDER);

        // Create a list of all blocks..
        for (int i = 0; i < 256; i++) {
            _allBlocksLoS.add(IdpMaterial.fromID(i));
        }
    }

    // </editor-fold>
    /**
     * This will return the manager that contains the listeners.
     */
    public IListenerManager getListenerManager() {
        return listenerManager;
    }

    /**
     * This class handles a command that is done by the server, a player or a block.
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        IdpCommandSender<? extends CommandSender> innSender;
        if (sender instanceof Player) {
            innSender = getPlayer((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            innSender = console;
        } else if (sender instanceof BlockCommandSender) {
            innSender = new IdpCommandBlockSender(this, (BlockCommandSender) sender);
        } else {
            sender.sendMessage("You cannot use this command, notify an admin!");
            logError("Error getting the commandsender!");
            return true;
        }

        return commandManager.invokeCommand(this, innSender, label, args);
    }

    public void sendAdminMessage(String spamIdentifier, String message) {
        if (spamIdentifier != null && !spamIdentifier.equalsIgnoreCase("")) {
            long now = new Date().getTime();
            if (getAdminMsgSpam().containsKey(spamIdentifier)) {
                if (now - getAdminMsgSpam().get(spamIdentifier) < 2000) {
                    return;
                }
            }
            getAdminMsgSpam().put(spamIdentifier, now);
        }

        for (IdpPlayer p : this.getOnlinePlayers()) {
            if (p.hasPermission(Permission.admin_adminmessage)) {
                p.printInfo(message);
            }
        }
    }

    public void sendModeratorMessage(String spamIdentifier, String message) {
        if (spamIdentifier != null && !spamIdentifier.equalsIgnoreCase("")) {
            long now = new Date().getTime();
            if (getAdminMsgSpam().containsKey(spamIdentifier)) {
                if (now - getAdminMsgSpam().get(spamIdentifier) < 2000) {
                    return;
                }
            }
            getAdminMsgSpam().put(spamIdentifier, now);
        }

        for (IdpPlayer p : this.getOnlinePlayers()) {
            if (p.hasPermission(Permission.admin_modmessage)) {
                p.printInfo(message);
            }
        }
    }

    /**
     * Makes an IDPConsole object that links to the console.
     * @return
     */
    public IdpConsole getConsole() {
        return console;
    }

    /**
     * @return the ignoreBlocksLoS
     */
    public final HashSet<IdpMaterial> getIgnoreBlocksLoS() {
        return _ignoreBlocksLoS;
    }

    /**
     *
     * @return the list of all blocks.
     */
    public final HashSet<IdpMaterial> getAllBlocksLoS() {
        return _allBlocksLoS;
    }

    public Map<String, Long> getAdminMsgSpam() {
        synchronized (_lock) {
            return _adminMsgSpam;
        }
    }

    public final synchronized Map<Long, LotFlagToggle> getLotFlagToggles() {
        return _lotFlagToggles;
    }

    public Team getVannishedPlayersTeam() {
        return vannishedPlayersTeam;
    }

    public Team getHiddenNametagPlayersTeam() {
        return hiddenNametagPlayersTeam;
    }

    /**
     * Get the instance of the plugin.
     * @return The pluginInstance
     */
    public static InnPlugin getPlugin() {
        return innplugin;
    }

    /**
     * Checks if the server is in readonly mode
     * @return true if the server is readonly. (so no interactions)
     */
    public boolean isReadonly() {
        return readOnly;
    }

    /**
     * Returns the task manager
     * @return taskmanager
     */
    public TaskManager getTaskManager() {
        return taskmanager;
    }

    /**
     * The manager for the holiday modules
     * @return
     */
    public HolidayModuleManager getHolidaymanager() {
        return holidaymanager;
    }

    /**
     * The manager for item effects.
     * @return
     */
    public SpecialItemManager getSpecialItemManager() {
        return effectmanager;
    }

    /**
     * Returns the external library manager
     * @return
     */
    public ExternalLibraryManager getExternalLibraryManager() {
        return externalLibraryManager;
    }

    /**
     * Gets the server crash checker task
     */
    public ServerCrashChecker getServerCrashChecker() {
        return crashChecker;
    }

    /**
     * Adds a new timer to the list
     * @param taskId
     */
    public void addTimer(long taskId) {
        timers.add(taskId);
    }

    /**
     * Deletes a timer by its task ID
     * @param taskId
     */
    public void removeTimer(long taskId) {
        timers.remove(taskId);
    }

    /**
     * Returns an unmodifiable list of the timers
     * @return
     */
    public List<Long> getTimers() {
        return Collections.unmodifiableList(timers);
    }

    // <editor-fold defaultstate="collapsed" desc="Broadcasting">
    /**
     * Sends a message to an location
     * @param location
     * @param message
     * @param radius
     */
    public void broadCastMessageToLocation(Location location, String message, int radius) {
        List<IdpPlayer> playerList = getOnlinePlayers();
        for (IdpPlayer player : playerList) {
            // Check for same world
            if (location.getWorld().equals(player.getLocation().getWorld())) {
                // Check distance
                if (player.getLocation().distance(location) < radius) {
                    player.printRaw(message);
                }
            }
        }
    }

    /**
     * Broadcasts a message to all players on the server. Colours allowed
     * @param message
     */
    public void broadCastMessage(String message) {
        getServer().broadcastMessage(message);
    }

    /**
     * Broadcasts a message to all players on the server. Colours allowed
     * The messagePrefix is auto added.
     * @param message
     * @param Chatcolor the colour of the text
     */
    public void broadCastMessage(ChatColor color, String... message) {
        String finishMsg = color + Configuration.MESSAGE_PREFIX;
        for (String str : message) {
            finishMsg += color + str;
        }
        getServer().broadcastMessage(finishMsg);
    }

    /**
     * Broadcasts a message to all staff players.
     * The message will be defaulted changed into an infomessage
     * @param message
     */
    public void broadCastStaffMessage(String message, boolean adminOnly) {
        for (IdpPlayer player : getOnlineStaff(adminOnly)) {
            player.printRaw(message);
        }
    }

    /**
     * Broadcasts a message to all staff players except the specified player.
     * The message will be defaulted changed into an infomessage
     * @param message
     */
    public void broadCastStaffMessageExcept(String exception, String message, boolean adminOnly) {
        for (IdpPlayer player : getOnlineStaff(adminOnly)) {
            if (!player.getName().equalsIgnoreCase(exception)) {
                player.printRaw(message);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Player">
    /**
     * TODO: Fix so this works with cache
     *
     * Returns a list with all players that are online
     * @return onlinelist
     */
    public List<IdpPlayer> getOnlinePlayers() {
        List<IdpPlayer> players = new ArrayList<IdpPlayer>();

        for (Player player : getServer().getOnlinePlayers()) {
            players.add(new IdpPlayer(this, player));
        }

        return players;
        //return Collections.unmodifiableList(idpPlayers);
    }

    /**
     * Returns a list of all the online staff, and can be
     * filtered to include all staff, or just admins
     * @param onlyAdmin Whether only admins are returned or not
     * @return
     */
    public List<IdpPlayer> getOnlineStaff(boolean onlyAdmin) {
        List<IdpPlayer> staff = new ArrayList<IdpPlayer>();

        for (IdpPlayer p : getOnlinePlayers()) {
            if (p.getSession().isStaff() && (!onlyAdmin || p.getGroup().equalsOrInherits(PlayerGroup.ADMIN))) {
                staff.add(p);
            }
        }

        return staff;
    }

    /**
     * Gets a player at the location
     * @param loc
     * @return null if the player is not at that location
     */
    public IdpPlayer getPlayer(Location loc) {
        for (IdpPlayer p : getOnlinePlayers()) {
            if (p.getLocation().equals(loc)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Gets an online player from their unique ID
     * @param playerId
     * @return
     */
    public IdpPlayer getPlayer(UUID playerId) {
        for (IdpPlayer player : getOnlinePlayers()) {
            if (player.getUniqueId().equals(playerId)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Returns the given player, or null if he/she is not found (or online)
     *
     * @param player
     * @return
     */
    public IdpPlayer getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    /**
     * Returns the given player, or null if he/she is not found (or online)
     *
     * @param name
     * @return
     */
    public IdpPlayer getPlayer(String name) {
        return getPlayer(name, false);
    }

    /**
     * Gets the specified player. This will first look them up in the cache.
     * If the player exists in the cache, it will be returned.
     * @param name
     * @param exactMatch
     * @return
     */
    public IdpPlayer getPlayer(String name, boolean exactMatch) {
        IdpPlayer player = cachedPlayers.get(name.toLowerCase());

        if (player != null) {
            return player;
        } else {
            return getPlayerNoCache(name, exactMatch);
        }
    }

    /**
     * This method will look in the list of online players and tries to find the
     * player that matches the given name.
     * <p/>
     * This won't return offline players.
     * <p/>
     *
     * @param name
     * The name to look for
     * @param exactMatch
     * True if the match needs to be exact
     * <p/>
     * @return returns the player if its found, otherwise null is returned
     */
    private IdpPlayer getPlayerNoCache(String name, boolean exactMatch) {
        int closestMatch = -1;
        name = name.toLowerCase(); // Case never matters here

        int dist;
        String playername;
        IdpPlayer closestPlayer = null;
        for (IdpPlayer player : getOnlinePlayers()) {
            playername = player.getName().toLowerCase();

            // Exact match, return
            if (playername.equalsIgnoreCase(name)) {
                return player;
            }

            // Check if partial matches are allowed.
            if (exactMatch) {
                continue;
            }

            if (name.length() < playername.length()) {
                dist = 0;
                if (playername.contains(name)) {
                    dist = playername.length() - name.length();
                }
                if (playername.startsWith(name)) {
                    dist += 1000;
                }

                // No match...
                if (dist == 0) {
                    continue;
                }

                // No closest match yet..
                if (closestMatch == -1) {
                    closestPlayer = player;
                    closestMatch = dist;
                    continue;
                }

                // Check if current match has a starting match
                if (dist > 1000) {
                    // Check if the closest match also has a starting match
                    if (closestMatch < 1000) {
                        // Check if the difference is big enough to
                        // ignore the starting match
                        if (Math.abs((dist - 1000) - closestMatch) > 4) {
                            closestPlayer = player;
                            closestMatch = dist;
                        }
                        continue;
                    }
                } else {
                    if (closestMatch > 1000) {
                        // Check if the difference is big enough to
                        // ignore the starting match
                        if (Math.abs((closestMatch - 1000) - dist) > 4) {
                            closestPlayer = player;
                            closestMatch = dist;
                        }
                        continue;
                    }
                }

                // Check normal
                if (dist < closestMatch) {
                    closestPlayer = player;
                    closestMatch = dist;
                }
            }
        }

        // If there is a partial match, return that. Else NULL
        return closestPlayer == null ? null : closestPlayer;
    }

    /**
     * Adds a player to the player cache
     * @param player
     */
    public void addCachedPlayer(IdpPlayer player) {
        cachedPlayers.put(player.getName().toLowerCase(), player);
    }

    /**
     * Removes a player from the player cache
     * @param player
     */
    public void removeCachedPlayer(IdpPlayer player) {
        cachedPlayers.remove(player.getName().toLowerCase());
    }

    /**
     * Saves all players
     */
    public void savePlayers() {
        getServer().savePlayers();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Logging methods">
    /**
     * Log the given message to the console. Colours allowed.
     * @param message
     */
    public static void logMessage(String message) {
        if (console == null) {
            logger.log(Level.INFO, message);
        } else {
            console.printRaw(message);
        }
    }

    /**
     * Lot the given information message to the console. Colours allowed.
     * @param The color that should be used for the message
     * @param message
     */
    public static void logCustom(ChatColor color, String... message) {
        logMessage(color + "[INFO] " + StringUtil.joinString(message, color.toString()));
    }

    /**
     * Lot the given information message to the console. Colours allowed.
     * @param message
     */
    public static void logInfo(String... message) {
        logMessage(ChatColor.GREEN + "[INFO] " + StringUtil.joinString(message, ChatColor.GREEN.toString()));
    }

    /**
     * Log the given errormessage to the console. Colours allowed.
     * @param message
     */
    public static void logError(String... message) {
        logMessage(ChatColor.RED + "[ERROR] " + StringUtil.joinString(message, ChatColor.RED.toString()));
    }

    /**
     * Log the given errormessage to the console. Colours allowed.<br/>
     * This will also print out a stacktrace!
     *
     * @param message
     * The message to use
     * @param th
     * The exception
     * @param objects
     * Any objects that might be handy to also print.
     * (These will only print if a mail is send)
     *
     */
    public static void logError(String message, Throwable th) {
        String errorreport;
        if (th != null) {
            errorreport = message
                    + "\r\nMessage: " + th.getMessage()
                    + "\r\nType:    " + th.getClass().getSimpleName()
                    + "\r\nStacktrace: \r\n" + exceptionToString(th);
        } else {
            errorreport = message + " - no exception found...\r\n";
        }

        // Print it to the console
        logError(errorreport);

        // If live then generate the report
        if (Configuration.PRODUCTION_SERVER) {
            String subject = StringUtil.format("Exception found on {0}", DateUtil.formatString(new Date(), DateUtil.FORMAT_FULL_DATE_TIME));
            ErrorReporter.generateErrorReport(subject, errorreport);
        }
    }

    /**
     * Makes a string from the throwable
     * @param t
     * @return string
     */
    private static String exceptionToString(Throwable t) {
        StringBuilder sb = new StringBuilder(4096);
        StackTraceElement[] elements = t.getStackTrace();

        for (int i = 0; i < Math.min(elements.length, 15); i++) {
            sb.append(StringUtil.format("\t{0})\t{3}\t {1}::{2}() \r\n",
                    (i + 1),
                    elements[i].getClassName(),
                    elements[i].getMethodName(),
                    elements[i].getLineNumber()));
        }

        return sb.toString();
    }

    /**
     * A check to see if the server is in debugMode mode
     * This is used
     * @return
     */
    public static boolean isDebugEnabled() {
        return Configuration.DEBUGMODE;
    }

    /**
     * This will show messages only shown when DEBUG is set to true.
     * @param debugmessage
     */
    public static void logDebug(Object... debugmessage) {
        // Check for debugMode mode
        if (!isDebugEnabled()) {
            return;
        }

        String finalMessage = StringUtil.joinString(debugmessage, " ", 0);
        logMessage(ChatColor.GREEN + finalMessage);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Backpack Viewing">
    /**
     * Adds a new backpack view
     * @param playerId
     * @param inv
     */
    public void addBackpackView(UUID playerId, IdpInventory inv) {
        _backpackViews.put(playerId, inv);
    }

    /**
     * Gets an existing backpack view
     * @param playerId
     * playerId
     */
    public IdpInventory getBackpackView(UUID playerId) {
        return _backpackViews.get(playerId);
    }

    /**
     * Removes a backpack view by the owner
     * @param playerId
     */
    public void removeBackpackView(UUID playerId) {
        _backpackViews.remove(playerId);
    }
    //</editor-fold>

}

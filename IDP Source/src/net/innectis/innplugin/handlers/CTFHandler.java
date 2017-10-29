package net.innectis.innplugin.handlers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.CreateCTFArenaObj;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 *
 * @author AlphaBlend
 *
 * Handler for capture the flag games
 */
public final class CTFHandler {

    private CTFHandler() {
    }
    private static HashMap<String, CreateCTFArenaObj> createGameMap = new HashMap<String, CreateCTFArenaObj>();
    public static final IdpMaterial teamRedFlag = IdpMaterial.WOOL_RED;
    public static final IdpMaterial teamBlueFlag = IdpMaterial.WOOL_BLUE;
    private static final String ctf_BlueBaseString = "BlueBase";
    private static final String ctf_RedBaseString = "RedBase";
    private static final String ctf_BlueFlagLocationString = "BlueFlag";
    private static final String ctf_RedFlagLocationString = "RedFlag";
    private static final IdpMaterial flagBaseMat = IdpMaterial.STONE;

    public enum CreateGameMode {
        MAKE_RED_BASE,
        MAKE_RED_FLAG,
        MAKE_BLUE_BASE,
        MAKE_BLUE_FLAG;
    }

    public static boolean setCreateGameMode(String Player, InnectisLot lot, IdpWorldRegion startRegion) {
        for (CreateCTFArenaObj obj : createGameMap.values()) {
            if (obj.returnLot().equals(lot, lot.isYaxis())) {
                return false;
            }
        }

        createGameMap.put(Player, new CreateCTFArenaObj(lot, startRegion, CreateGameMode.MAKE_RED_BASE));
        return true;
    }

    public static void setCreateGameMode(String player, CreateGameMode mode) {
        CreateCTFArenaObj obj = createGameMap.get(player);
        obj.setGameMode(mode);
    }

    public static CreateCTFArenaObj getCreateGameMode(String player) {
        return createGameMap.get(player);
    }

    public static CreateGameMode getNextGameMode(CreateGameMode mode) {
        CreateGameMode m = null;

        switch (mode) {
            case MAKE_RED_BASE:
                m = CreateGameMode.MAKE_RED_FLAG;
                break;
            case MAKE_RED_FLAG:
                m = CreateGameMode.MAKE_BLUE_BASE;
                break;
            case MAKE_BLUE_BASE:
                m = CreateGameMode.MAKE_BLUE_FLAG;
                break;
        }

        return m;
    }

    public static void endCreateGameMode(String player) {
        createGameMap.remove(player);
    }

    /**
     * Checks if the lot is a CTF lot
     * @param checkLot
     * @return
     */
    public static boolean isCTFArena(InnectisLot checkLot) {
        if (checkLot == null) {
            return false;
        }

        checkLot = checkLot.getParentTop();

        boolean blueArenaCheck = false;
        boolean redArenaCheck = false;

        for (InnectisLot lot : checkLot.getSublots()) {
            if (lot.getLotName().equalsIgnoreCase(ctf_BlueBaseString)) {
                for (InnectisLot sublot : lot.getSublots()) {
                    if (sublot.getLotName().equalsIgnoreCase(ctf_BlueFlagLocationString)) {
                        blueArenaCheck = true;
                        break;
                    }
                }
            }

            if (lot.getLotName().equalsIgnoreCase(ctf_RedBaseString)) {
                for (InnectisLot sublot : lot.getSublots()) {
                    if (sublot.getLotName().equalsIgnoreCase(ctf_RedFlagLocationString)) {
                        redArenaCheck = true;
                        break;
                    }
                }
            }

            if (redArenaCheck && blueArenaCheck) {
                break;
            }
        }

        return (redArenaCheck && blueArenaCheck);
    }

    /**
     * Methods gets called when a player interacts with a block while generating an CTF area
     * @param event
     * @param obj
     */
    public static void onCTFAreaCreation(InnPlayerInteractEvent event, CreateCTFArenaObj obj) {
        IdpPlayer player = event.getPlayer();
        InnectisLot creationLot = obj.returnLot();
        Location loc = event.getBlock().getLocation();

        if (!creationLot.contains(loc)) {
            player.printError("CTF arena is not being created here!");
            return;
        }

        CreateGameMode mode = obj.returnGameMode();
        boolean requireNextMode = false;

        switch (mode) {
            case MAKE_RED_BASE:
            case MAKE_BLUE_BASE:
                IdpWorldRegion region = player.getRegion();
                BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);

                boolean isRedBase = (mode == CreateGameMode.MAKE_RED_BASE);
                boolean createBase = false;
                Vector startVector = obj.getStartRegion().getPos1();

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    region.setPos1(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                    // If both points for the region are set
                    if (!region.getPos1().equals(startVector) && !region.getPos2().equals(startVector)) {
                        if (region.getPos1().getBlockY() != region.getPos2().getBlockY()) {
                            player.printError("The base must be one block high.");
                            return;
                        }

                        requireNextMode = true;
                        createBase = true;
                    } else {
                        player.printInfo("Set region 1 for team " + (isRedBase ? "red" : "blue") + "!");
                    }
                } else {
                    region.setPos2(loc.toVector());

                    // If both points for the region are set
                    if (!region.getPos1().equals(startVector) && !region.getPos2().equals(startVector)) {
                        if (region.getPos1().getBlockY() != region.getPos2().getBlockY()) {
                            player.printError("The base must be one block high.");
                            return;
                        }

                        requireNextMode = true;
                        createBase = true;
                    } else {
                        player.printInfo("Set region 2 for team " + (isRedBase ? "red" : "blue") + "!");
                    }
                }

                player.setRegion(region);

                if (createBase) {
                    String baseName = (isRedBase ? "RedBase" : "BlueBase");

                    List<Block> blocks = counter.getBlockList(player.getRegion(), player.getLocation().getWorld(), null);

                    for (Block block : blocks) {
                        IdpMaterial flagMaterial = (isRedBase ? CTFHandler.teamRedFlag : CTFHandler.teamBlueFlag);
                        BlockHandler.setBlock(block, flagMaterial);
                    }

                    // Don't make this a y-axis lot
                    region.setPos1(region.getPos1().setY(0));
                    region.setPos2(region.getPos2().setY(255));

                    try {
                        PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
                        InnectisLot lot = LotHandler.addLot(player.getLocation().getWorld(), region.getPos1(), region.getPos2(), credentials, credentials);
                        lot.setLotName(baseName);
                        lot.save();
                    } catch (SQLException ex) {
                        InnPlugin.logError("Unable to save lot!", ex);
                    }
                    player.printInfo("Team " + (isRedBase ? "red" : "blue") + "'s base created!");
                }

                break;
            case MAKE_RED_FLAG:
            case MAKE_BLUE_FLAG:
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    InnectisLot baseLot = LotHandler.getLot(loc);
                    Vector vector = loc.toVector();
                    boolean isRedFlag = (mode == CreateGameMode.MAKE_RED_FLAG);

                    if (baseLot == null) {
                        player.printError("You must place the flag location on the " + (isRedFlag ? "red" : "blue") + " team's base.");
                        return;
                    }

                    if (!((isRedFlag && baseLot.getLotName().equalsIgnoreCase("redbase"))
                            || (!isRedFlag && baseLot.getLotName().equalsIgnoreCase("bluebase")))) {
                        player.printError("You must create the flag position in team " + (isRedFlag ? "red" : "blue") + "'s base.");
                        return;
                    }

                    requireNextMode = true;
                    Block block = loc.getWorld().getBlockAt(loc);
                    BlockHandler.setBlock(block, CTFHandler.flagBaseMat);

                    String flagPositionName = (isRedFlag ? "RedFlag" : "BlueFlag");

                    try {
                        PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
                        InnectisLot lot = LotHandler.addLot(player.getLocation().getWorld(), vector, vector, credentials, credentials);
                        lot.setLotName(flagPositionName);
                        lot.save();
                    } catch (SQLException ex) {
                        InnPlugin.logError("Unable to save lot!", ex);
                    }

                    player.printInfo("Created team " + (isRedFlag ? "red" : "blue") + "'s flag!");
                    player.setRegion(obj.getStartRegion());
                }

                break;
        }

        if (requireNextMode) {
            CreateGameMode nextMode = CTFHandler.getNextGameMode(mode);

            if (nextMode == null) {
                player.printInfo("CTF arena created!");
                CTFHandler.endCreateGameMode(player.getName());
                return;
            }

            CTFHandler.setCreateGameMode(player.getName(), nextMode);

            switch (nextMode) {
                case MAKE_RED_FLAG:
                    player.printInfo("Next, mark team red's flag location.");
                    break;
                case MAKE_BLUE_BASE:
                    player.printInfo("Next, mark team blue's base location.");
                    break;
                case MAKE_BLUE_FLAG:
                    player.printInfo("Next, mark team blue's flag location.");
                    break;
            }
        }
    }

}
package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.pojo.BlockLog;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.data.IdpBlockData.SaveStrategy;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.NotchcodeUsage;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerBonus;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.MagicValueUtil;
import net.minecraft.server.v1_11_R1.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Bed;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.material.Stairs;
import org.bukkit.material.Tree;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

/**
 * @author Hret
 */
public final class BlockHandler {

    // Key for falling blocks
    public static final String METADATA_BLOCKDATA_VALUES = "BLOCKDATA";

    public static final int ACTION_BLOCK_DESTROY = 1;
    public static final int ACTION_BLOCK_PLACED = 2;

    private static final List<Material> LOGS = Arrays.asList(new Material[]{Material.LOG, Material.LOG_2});
    private static final List<Material> LEAVES = Arrays.asList(new Material[]{Material.LEAVES, Material.LEAVES_2});

    private static final List<BlockFace> ALL_FACES = new ArrayList<BlockFace>();

    // This hashmap has materials which do not have any direction support in bukkit.
    // IdpMaterial reference to byte array in the order of north, south, east, west in data value format
    private static final HashMap<IdpMaterial, byte[]> unsupportedDirections = new HashMap<IdpMaterial, byte[]>();

    static {
        unsupportedDirections.put(IdpMaterial.ANVIL, new byte[] {0, 2, 1, 3});
        unsupportedDirections.put(IdpMaterial.SLIGHTLY_DAMAGED_ANVIL, new byte[] {4, 6, 5, 7});
        unsupportedDirections.put(IdpMaterial.VERY_DAMAGED_ANVIL, new byte[] {8, 10, 9, 11});

        ALL_FACES.add(BlockFace.NORTH);
        ALL_FACES.add(BlockFace.NORTH_NORTH_EAST);
        ALL_FACES.add(BlockFace.NORTH_EAST);
        ALL_FACES.add(BlockFace.EAST_NORTH_EAST);
        ALL_FACES.add(BlockFace.EAST);
        ALL_FACES.add(BlockFace.EAST_SOUTH_EAST);
        ALL_FACES.add(BlockFace.SOUTH_EAST);
        ALL_FACES.add(BlockFace.SOUTH_SOUTH_EAST);
        ALL_FACES.add(BlockFace.SOUTH);
        ALL_FACES.add(BlockFace.SOUTH_SOUTH_WEST);
        ALL_FACES.add(BlockFace.SOUTH_WEST);
        ALL_FACES.add(BlockFace.WEST_SOUTH_WEST);
        ALL_FACES.add(BlockFace.WEST);
        ALL_FACES.add(BlockFace.WEST_NORTH_WEST);
        ALL_FACES.add(BlockFace.NORTH_WEST);
        ALL_FACES.add(BlockFace.NORTH_NORTH_WEST);
    }

    private BlockHandler() {
    }

    /**
     * Checks if the player can build in the location
     *
     * @param player The player that does an action
     * @param location The location the action takes place.
     * @param action The action that was taken use:
     * <code>ACTION_BLOCK_DESTROY</code> or <code>ACTION_BLOCK_PLACED</code>
     * @param isWorldEdit If the action is from WorldEdit
     * @return True if the player is allowed to build on the location
     */
    public static Boolean canBuildInArea(IdpPlayer player, Location location, int action, boolean isWorldEdit) {
        if (!isWorldEdit && player.hasPermission(Permission.world_build_unrestricted)) {
            return true;
        }

        IdpWorld world = IdpWorldFactory.getWorld(location.getWorld().getName());

        if (world.getWorldType() == IdpWorldType.NONE) {
            return false;
        }

        if (Math.abs(location.getX()) > world.getSettings().getWorldSize() || Math.abs(location.getZ()) > world.getSettings().getWorldSize()) {
            return false;
        }

        InnectisLot lot = LotHandler.getLot(location, true);

        if (lot == null) {
            switch (world.getActingWorldType()) {
                case INNECTIS: {
                    return (!isWorldEdit && player.hasPermission(Permission.world_build_wilderness));
                }

                case NATURALWORLD: {
                    return (!isWorldEdit && player.hasPermission(Permission.world_build_unrestricted));
                }

                case NETHER: {
                    return (!isWorldEdit && player.hasPermission(Permission.world_build_nether));
                }

                case RESWORLD: {
                    return !isWorldEdit
                            || (new Vector(0, 0, 0).distance(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ())) > 300); // Allow everything except WE near spawn
                }

                case AETHER: {
                    return (!isWorldEdit && player.hasPermission(Permission.world_build_aether));
                }

                case THE_END: {
                    return (!isWorldEdit && player.hasPermission(Permission.world_build_theend));
                }

                case DYNAMIC: {
                    IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) world.getSettings();
                    return settings.isBuildable() && ((settings.isWorldeditAllowed() && isWorldEdit) || !isWorldEdit);
                }
            }
        } else {
            if (lot.getDisabled()) { //no one can build on a disabled lot, not even the owner!
                return false;
            }

            if (lot.canPlayerManage(player.getName())) {
                return true;
            }

            // Treat the Pixel Build flag the same as the former pixel world
            if (lot.isFlagSet(LotFlagType.PIXELBUILD)) {
                // Don't let anyone break the pixel world floor
                if (location.getBlockY() <= 1 && !player.hasPermission(Permission.world_build_unrestricted)) {
                    return false;
                }

                return player.hasPermission(Permission.world_build_pixelarea);
            }

            if (isWorldEdit) {
                // Allow WE as member is the given perm & NO_WE flag not set
                if (!lot.isFlagSet(LotFlagType.NOWE)
                        && (lot.containsMember(player.getName())
                        || lot.containsMember("%"))) {
                    return true;
                }

                if (world.getWorldType() == IdpWorldType.NATURALWORLD
                        && !player.hasPermission(Permission.tinywe_natural_world_use)) {
                    return false;
                }
            } else {
                // Check if owner or member of lot
                if (lot.canPlayerAccess(player.getName())) {
                    return true;
                }

                // Check for spleef flag
                if (lot.isFlagSet(LotFlagType.SPLEEF)) {
                    if (action == ACTION_BLOCK_PLACED) {
                        return false;
                    } else if (action == ACTION_BLOCK_DESTROY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the player van break the block at the given location.
     *
     * @param player
     * @param location
     * @param isWorldEdit
     * @return null is player can break, else an error message
     */
    public static String canBreakBlock(IdpPlayer player, Location location, boolean isWorldEdit) {
        Block block = location.getBlock();
        IdpMaterial material = IdpMaterial.fromBlock(block);

        // Dont allow breads of unbreakable blocks
        if (BlockHandler.getIdpBlockData(location).isUnbreakable()) {
            return "That block cannot be broken.";
        }

        if (!material.canPlayerBreakMaterial(player)) {
            if (!isWorldEdit) {
                InnPlugin.logInfo(player.getColoredName(), " tried to break a restricted block (" + material.getName() + ") at " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            }
            return "You can't break that block!";
        }


        if (canBuildInArea(player, location, ACTION_BLOCK_DESTROY, isWorldEdit)) {
            return null;
        } else {
            return "You cannot build here!";
        }
    }

    /**
     * Checks if the player is allowed to place the material at the given
     * location
     *
     * @param player
     * @param location
     * @param material
     * @param isWorldEdit
     * @return
     */
    public static String canPlaceBlock(IdpPlayer player, Location location, IdpMaterial material, boolean isWorldEdit) {
        if (!material.canPlayerPlaceMaterial(player)) {
            if (!isWorldEdit) {
                InnPlugin.logInfo(player.getColoredName(), " tried to use a restricted block (" + material.getName() + ") at " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            }
            return "You can't place that block, how did you get that in the first place?";
        }

        if (canBuildInArea(player, location, ACTION_BLOCK_PLACED, isWorldEdit)) {
            if (BlockHandler.getIdpBlockData(location).isUnbreakable()) {
                return "That block is locked by an admin.";
            }

            return null;
        } else {
            // Return correct error message.
            if (material == IdpMaterial.FIRE) {
                return "No disco inferno for you!";
            } else {
                return "You cannot build here!";
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Quota">
    public static void appendQuota(IdpPlayer player, Block block) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT DELAYED INTO block_quota_log (time, player_id, blockid) VALUES (NOW(),?,?);");
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, getBlockTypeId(block));
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot append to quota", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    public static boolean isExceedingQuota(IdpPlayer player, Block block) {
        //@todo load block quotas into playersession

        if (player.hasPermission(Permission.build_noquota)) {
            return false;
        }

        int maxblocks = getQuotaMax(player, block);
        int timespan = 3600;

        if (maxblocks == -1) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT COUNT(*) FROM block_quota_log WHERE time >= DATE_SUB(NOW(), INTERVAL ? SECOND) AND player_id = ? AND blockid = ?;");
            statement.setInt(1, timespan);
            statement.setString(2, player.getUniqueId().toString());
            statement.setInt(3, getBlockTypeId(block));
            result = statement.executeQuery();

            if (result.next()) {
                if (result.getInt(1) >= maxblocks) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot check quota", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    public static int getQuotaMax(IdpPlayer player, Block block) {

        IdpMaterial mat = IdpMaterial.fromBlock(block);
        if (mat != IdpMaterial.DIAMOND_ORE
                && mat != IdpMaterial.LAPIS_LAZULI_OREBLOCK
                && mat != IdpMaterial.EMERALD_ORE) {
            return -1;
        }

        switch (player.getGroup()) {
            case GUEST:
            case USER:
                return 16;
            case VIP:
                return 20;
            case SUPER_VIP:
                return 25;
            case GOLDY:
                return 30;
            case MODERATOR:
                return 35;
            default:
                return 0;
        }
    }

    //</editor-fold>
    /**
     * Returns an array with the faces NORTH, SOUTH, EAST, WEST, UP & DOWN
     *
     * @return
     */
    public static BlockFace[] getAllFaces() {
        return new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
            BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    }

    /**
     * Returns an array with the faces NORTH, SOUTH, EAST and WEST
     *
     * @return
     */
    public static BlockFace[] getAllSideFaces() {
        return new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    }

    /**
     * Searches a relative location looking for a specific block. If not
     * found, returns null
     * @param location
     * @param mat
     * @param range
     * @return
     */
    public static Block getBlockNearby(Location location, IdpMaterial mat, int range) {
        List<Block> blocks = getBlocksNearby(location, new IdpMaterial[] {mat}, range);

        if (blocks.size() > 0) {
            return blocks.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns a list of all blocks nearby with the given materials
     * @param location
     * @param mats
     * @param range
     * @return
     */
    public static List<Block> getBlocksNearby(Location location, IdpMaterial[] mats, int range) {
        List<Block> blocks = new ArrayList<Block>();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Block block = location.getBlock().getRelative(x, y, z);
                    IdpMaterial blockMat = IdpMaterial.fromBlock(block);

                    for (IdpMaterial mat : mats) {
                        if (blockMat == mat) {
                            blocks.add(block);
                            break;
                        }
                    }
                }
            }
        }

        return blocks;
    }

    //<editor-fold defaultstate="collapsed" desc="Byte directions">
    /**
     * Gets the block face of the specified block. This will search the
     * unsupported list if support is not found. If the block cannot be
     * rotated, SELF will be returned
     * @param state
     * @return
     */
    public static BlockFace getBlockFace(BlockState state) {
        MaterialData mdata = state.getData();

        // default to no face if not found
        BlockFace face = BlockFace.SELF;

        if (mdata instanceof Directional) {
            Directional dir = (Directional) mdata;
            face = dir.getFacing();

            // Extra data is defined
            if (face == BlockFace.SELF && state instanceof Skull) {
                Skull skull = (Skull) state;
                face = skull.getRotation();
            }
        } else if (mdata instanceof Rails) {
            Rails rails = (Rails) mdata;
            face = rails.getDirection();
        } else if (mdata instanceof Tree) {
            Tree tree = (Tree) mdata;
            face = tree.getDirection();
        } else {
            byte data = MagicValueUtil.getDataFromMaterialData(mdata);

            IdpMaterial mat = IdpMaterial.fromFilteredBlock(state.getBlock());
            byte[] faceBytes = unsupportedDirections.get(mat);

            if (faceBytes != null) {
                // Set to -1 to ignore block faces should the block's data value
                // not match the standard block face data values
                int idx = -1;

                // Check if block's data value matches
                for (int i = 0; i < faceBytes.length; i++) {
                    if (faceBytes[i] == data) {
                        idx = i;
                        break;
                    }
                }

                switch (idx) {
                    case 0:
                        face = BlockFace.NORTH;
                        break;
                    case 1:
                        face = BlockFace.SOUTH;
                        break;
                    case 2:
                        face = BlockFace.EAST;
                        break;
                    case 3:
                        face = BlockFace.WEST;
                        break;
                }
            }
        }

        return face;
    }

    /**
     * Rotates the passed in block face and returns the resulting block face.
     * @param face
     * @param times
     * @param extended indicates if the wull block face
     * spectrum should be used
     * @return
     */
    public static BlockFace getRotatedBlockFace(BlockFace face, int times, boolean extended) {
        if (extended) {
            times *= 4;

            int ord = 0;

            for (int i = 0; i < ALL_FACES.size(); i++) {
                BlockFace tempFace = ALL_FACES.get(i);

                if (tempFace == face) {
                    ord = i;
                    break;
                }
            }

            while (times > 0) {
                if (ord == 15) {
                    ord = 0;
                } else {
                    ord++;
                }

                times--;
            }

            return ALL_FACES.get(ord);
        } else {
            int ord = face.ordinal();

            while (times > 0) {
                if (ord == 3) {
                    ord = 0;
                } else {
                    ord++;
                }

                times--;
            }

            return BlockFace.values()[ord];
        }
    }

    /**
     * Rotates the specified block
     *
     * @param block
     * @param direction Rota
     * @return true if block was rotated, false otherwise
     */
    public static boolean rotateBlock(BlockState state, BlockFace direction) {
        MaterialData mdata = state.getData();
        boolean set = false;

        if (mdata instanceof Directional) {
            // Do not allow beds as they are two blocks
            if (mdata instanceof Bed) {
                return false;
            }

            Directional directional = (Directional) mdata;
            BlockFace currentDirection = directional.getFacing();

            if (state instanceof Skull) {
                if (currentDirection == BlockFace.SELF) {
                    Skull skull = (Skull) state;
                    skull.setRotation(direction);
                } else {
                    directional.setFacingDirection(direction.getOppositeFace());
                }

                return true;
            }

            // Stairs are handled differently
            if (mdata instanceof Stairs) {
                Stairs stairs = (Stairs) mdata;
                currentDirection = stairs.getDescendingDirection();
            // Handle ladders differently
            } else if (mdata instanceof Ladder) {
                Ladder ladder = (Ladder) mdata;
                currentDirection = ladder.getAttachedFace().getOppositeFace();
            }

            // Modify only if different
            if (currentDirection != direction) {
                // Doors need to be handled a special way
                if (mdata instanceof Door) {
                    Door door = (Door) mdata;
                    direction = direction.getOppositeFace();

                    if (!door.isTopHalf()) {
                        boolean isOpen = door.isOpen();
                            byte dirData = 0;

                        switch (direction) {
                            case NORTH:
                                dirData = 3;
                                break;
                            case SOUTH:
                                dirData = 1;
                                break;
                            case EAST:
                                dirData = 0;
                                break;
                            case WEST:
                                dirData = 2;
                                break;
                        }

                        door.setData(dirData);
                        door.setOpen(isOpen);
                        state.setData(door);
                    }
                } else {
                    // Reverse direction for stairs and ladders, as setting their direction works a bit differently
                    if (mdata instanceof Stairs || mdata instanceof Ladder) {
                        direction = direction.getOppositeFace();
                    }

                    directional.setFacingDirection(direction);
                }

                set = true;
            }
        } else if (mdata instanceof Rails) {
            Rails rails = (Rails) mdata;
            rails.setDirection(direction, false);
            set = true;
        } else if (mdata instanceof Tree) {
            Tree tree = (Tree) mdata;
            tree.setDirection(direction);
            set = true;
        } else {
            // Check materials bukkit doesn't support
            IdpMaterial mat = IdpMaterial.fromFilteredBlock(state.getBlock());
            byte directionByte = BlockHandler.blockfaceToDirectionByte(mat, direction);

            if (directionByte != -1) {
                byte dat = MagicValueUtil.getDataFromMaterialData(mdata);

                if (dat != directionByte) {
                    mdata.setData(directionByte);
                    set = true;
                }
            }
        }

        return set;
    }

    /**
     * Returns a direction byte for the specified material and blockface
     * @param mat
     * @param face
     * @return
     */
    private static byte blockfaceToDirectionByte(IdpMaterial mat, BlockFace face) {
        byte[] directions = unsupportedDirections.get(mat);
        byte direction = -1;

        if (directions != null) {
            int idx = -1;

            switch (face) {
                case NORTH:
                    idx = 0;
                    break;
                case SOUTH:
                    idx = 1;
                    break;
                case EAST:
                    idx = 2;
                    break;
                case WEST:
                    idx = 3;
                    break;
            }

            // If we have a valid direction
            if (idx != -1) {
                direction = directions[idx];
            }
        }

        return direction;
    }
    //</editor-fold>

    /**
     * Looks up the IDPBlockData object for the specified block
     *
     * @param loc
     * @return The IdpBlockData with the SaveStrategy in Lazy mode.
     */
    public static IdpBlockData getIdpBlockData(Location loc) {
        return getIdpBlockData(loc, SaveStrategy.LAZY);
    }

    /**
     * Looks up the IDPBlockData object for the specified block
     *
     * @param loc
     * @param saveStrategy
     * @return The IdpBlockData with the SaveStrategy in Lazy mode.
     */
    public static IdpBlockData getIdpBlockData(Location loc, SaveStrategy saveStrategy) {
        return getIdpBlockData(loc, saveStrategy, false);
    }

    /**
     * Looks up the IDPBlockData object for the specified block
     *
     * @param loc
     * @param reclaimImmediatelyIfNotCached
     * @return The IdpBlockData with the SaveStrategy in Lazy mode.
     */
    public static IdpBlockData getIdpBlockData(Location loc, boolean reclaimImmediatelyIfNotCached) {
        return getIdpBlockData(loc, SaveStrategy.LAZY, reclaimImmediatelyIfNotCached);
    }

    /**
     * Returns the material data from the block, or creates a new one if needed.
     * Automaticly sets the savestrategy to the given value.
     *
     * @param loc
     * @param saveStrategy
     * @param reclaimImmediatelyIfNotCached
     * @return
     */
    public static IdpBlockData getIdpBlockData(Location loc, SaveStrategy saveStrategy, boolean reclaimImmediatelyIfNotCached) {
        // Convert to chunk coordinates
        Vector chunkVector = LocationUtil.locationToChunkVector(loc);
        Location chunkLocation = new Location(loc.getWorld(), chunkVector.getBlockX(), chunkVector.getBlockY(), chunkVector.getBlockZ());

        IdpBlockData blockData = ChunkDatamanager.getChunkData(chunkLocation, reclaimImmediatelyIfNotCached).getBlockData(LocationUtil.locationToChunkCoordinateVector(loc));
        blockData.setSaveStrategy(saveStrategy);
        return blockData;
    }

    /**
     * Finds a block on any of the sides of the given block
     *
     * @param targetMat
     * @param includeUpDown When true the top and bottom face are also checked
     * @return
     */
    public static Block getBlockAttached(Block block, IdpMaterial targetMat, boolean includeUpDown) {
        for (BlockFace face : includeUpDown ? BlockHandler.getAllFaces() : BlockHandler.getAllSideFaces()) {
            Block relativeBlock = block.getRelative(face);
            IdpMaterial mat = IdpMaterial.fromBlock(relativeBlock);

            if (mat == targetMat) {
                return relativeBlock;
            }
        }

        return null;
    }

    /**
     * Checks if the first block is adjacent to the second one
     * @param block1
     * @param block2
     * @return
     */
    public static boolean isBlockAdjacent(Block block1, Block block2) {
        int x1 = block1.getX();
        int y1 = block1.getY();
        int z1 = block1.getZ();

        int x2 = block2.getX();
        int y2 = block2.getY();
        int z2 = block2.getZ();

        int diffX = Math.abs(x1 - x2);
        int diffY = Math.abs(y1 - y2);
        int diffZ = Math.abs(z1 - z2);

        return (diffX + diffY + diffZ) == 1;
    }

    /**
     * Returns any blocks attached to the block passed in
     *
     * @param targetMat
     * @param includeUpDown When true the top and bottom face are also checked
     * @return List of blocks that are connected
     */
    public static List<Block> getBlocksAttached(Block block, IdpMaterial[] targetMat, boolean includeUpDown) {
        List<Block> blocks = new ArrayList<Block>();

        for (BlockFace face : includeUpDown ? BlockHandler.getAllFaces() : BlockHandler.getAllSideFaces()) {
            for (IdpMaterial mat : targetMat) {
                Block attachedBlock = block.getRelative(face);
                IdpMaterial blockMat = IdpMaterial.fromBlock(attachedBlock);

                if (blockMat == mat) {
                    blocks.add(attachedBlock);
                    break;
                }
            }
        }

        return blocks;
    }

    /**
     * Returns a list of change logs for the specified block
     *
     * @param The amount of logs; if not given it will default to 10;
     * @returns List of block logs. <br/> When the server couldn't get the logs
     * <b>NULL</b> is returned.
     */
    public static List<BlockLog> getBlockChangeLogs(Location location, int... amount) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement(
                    "SELECT logid, player_id, locx, locy, locz, world, Id as id, Data as data, DateTime as datetime, ActionType as actiontype "
                    + "FROM block_log "
                    + "WHERE locx = ? AND locy = ? AND locz = ? and world = ? "
                    + "ORDER BY datetime DESC "
                    + "LIMIT ? ");
            statement.setInt(1, location.getBlockX());
            statement.setInt(2, location.getBlockY());
            statement.setInt(3, location.getBlockZ());
            statement.setString(4, location.getWorld().getName());
            statement.setInt(5, (amount.length == 0 ? 10 : amount[0]));
            set = statement.executeQuery();

            List<BlockLog> blockLogs = new ArrayList<BlockLog>();

            while (set.next()) {
                World world = Bukkit.getWorld(set.getString("world"));

                if (world != null) {
                    int x = set.getInt("locx");
                    int y = set.getInt("locy");
                    int z = set.getInt("locz");

                    int logid = set.getInt("logid");

                    String playerIdString = set.getString("player_id");
                    UUID playerId = UUID.fromString(playerIdString);
                    PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);

                    int id = set.getInt("id");
                    int data = set.getInt("data");

                    Timestamp dateTime = set.getTimestamp("datetime");
                    int action = set.getInt("actiontype");

                    blockLogs.add(new BlockLog(credentials.getName(), x, y, z, world.getName(), id, data, dateTime, action));
                }
            }

            return blockLogs;
        } catch (SQLException ex) {
            InnPlugin.logError("SQLException when getting chestaccess logs!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        // On error return null;
        return null;
    }
    /**
     * This will force the block to apply to physics and fall down.
     * <p/>
     * When the block is spawned this block will be set to air and the material data will be cleared.
     * Any data that was set on this block before calling this method will be transferred to the falling block.
     * <p/>
     * <i>This means that virtual block data will also be transferred</i> <br/>
     * <b>Important:</b> Altough virtual data is transferred, if the item hits a torch, the data is lost..
     * <p/>
     * It will not respect lot boundaries.
     * Furthermore, bedrock will be ignored when calling this method.
     */
    public static void dropBlock(Block block) {
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Bedrock cannot fall
        if (mat == IdpMaterial.BEDROCK) {
            return;
        }

        IdpMaterial matBelow = IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN));

        // Check if it can fall
        if (mat.isNonSolid(true) || !matBelow.isNonSolid(true)) {
            return;
        }

        // Spawn a falling block
        Location centerLocation = LocationUtil.getCenterLocation(block.getLocation());
        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(centerLocation, mat.getBukkitMaterial(), getBlockData(block));

        // Check for blockdata
        IdpBlockData bdata = BlockHandler.getIdpBlockData(block.getLocation());
        Map<String, String> datavals = bdata.getValues();

        if (datavals != null) {
            // Set the data values
            fallingBlock.setMetadata(METADATA_BLOCKDATA_VALUES, new FixedMetadataValue(InnPlugin.getPlugin(), datavals));
            // Clear the data as the block doesn't exist anymore
            bdata.clear();
        }

        // Clear this block
        setBlock(block, IdpMaterial.AIR);
    }

    /**
     * This will check the passed in block with the fallingblock for any metadata values it should copy.
     * Use this when a falling block has fallen all the way down.
     * @param block
     */
    public static void resetBlock(Block block, FallingBlock fallingBlock) {
        List<MetadataValue> values = fallingBlock.getMetadata(METADATA_BLOCKDATA_VALUES);

        if (values != null) {
            for (MetadataValue val : values) {
                // Check if the value is from the IDP
                if (val.getOwningPlugin() == InnPlugin.getPlugin()) {
                    Map<String, String> idpvalues = (Map<String, String>) val.value();

                    IdpBlockData bdata = BlockHandler.getIdpBlockData(block.getLocation());
                    bdata.setValues(idpvalues);
                    bdata.save();
                }
            }
        }
    }

    /**
     * Sets the block with the new material
     * @param block
     * @param mat
     */
    public static void setBlock(Block block, IdpMaterial mat) {
        setBlock(block, mat, true);
    }

    /**
     * Sets the block with the new material
     * @param block
     * @param mat
     * @param applyPhysics
     */
    public static void setBlock(Block block, IdpMaterial mat, boolean applyPhysics) {
        setBlock(block, mat, (byte) mat.getData(), applyPhysics);
    }

    /**
     * Sets the block with the new material and data
     * @param block
     * @param mat
     * @param data
     */
    public static void setBlock(Block block, IdpMaterial mat, byte data) {
        setBlock(block, mat, data, true);
    }

    /**
     * Sets the block with the new material and data
     * @param block
     * @param mat
     * @param data
     * @param applyPhysics
     */
    public static void setBlock(Block block, IdpMaterial mat, byte data, boolean applyPhysics) {
        block.setType(mat.getBukkitMaterial(), applyPhysics);
        block.setData(data, applyPhysics);
    }

    /**
     * Sets the block with the new data
     * @param block
     * @param data
     */
    public static void setBlockData(Block block, byte data) {
        setBlockData(block, data, true);
    }

    /**
     * Sets the block with the new data
     * @param block
     * @param data
     * @param applyPhysics
     */
    public static void setBlockData(Block block, byte data, boolean applyPhysics) {
        block.setData(data, applyPhysics);
    }

    /**
     * Gets the type ID of the block
     * @param block
     * @return
     */
    public static int getBlockTypeId(Block block) {
        return block.getTypeId();
    }

    /**
     * Gets the data of the specified block
     * @param block
     * @return
     */
    public static byte getBlockData(Block block) {
        return block.getData();
    }

    /**
     * Spawns a block of Primed TNT at the target location with the specified properties.
     * This may break as Minecraft updates.
     * @param location
     * @param damage The range of the TNT blast.
     * @param fuse How long (in ticks) before the TNT explodes. (20 ticks per second)
     */
    @NotchcodeUsage(mcversion = "1.3.1")
    public static void spawnTNT(Location location, double damage, int fuse) {
        TNTPrimed primed = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        EntityTNTPrimed nmsPrimed = ((CraftTNTPrimed) primed).getHandle();
        nmsPrimed.P = (float) damage;
        nmsPrimed.setFuseTicks(fuse);
    }

    /**
     * Launches a firework with random characteristics at the target location.
     * The characteristics are random size, colours and height.
     * @param location
     * @return
     */
    public static Firework launchRandomFirework(Location location) {
        Random rand = new Random();
        FireworkEffect.Type fireworkType = null;
        switch (rand.nextInt(5)) {
            case 0:
                fireworkType = FireworkEffect.Type.BALL;
                break;
            case 1:
                fireworkType = FireworkEffect.Type.BALL_LARGE;
                break;
            case 2:
                fireworkType = FireworkEffect.Type.BURST;
                break;
            case 3:
                fireworkType = FireworkEffect.Type.CREEPER;
                break;
            case 4:
                fireworkType = FireworkEffect.Type.STAR;
        }

        return launchFirework(location, fireworkType, rand.nextBoolean(), fireworkColorFromInt(rand.nextInt(16) + 1),
                fireworkColorFromInt(rand.nextInt(16) + 1), rand.nextBoolean());
    }

    /**
     * Launches a firework at the target location with the specified characteristics.
     * @param location
     * @param type Firework Type: Ball, Large Ball, Burst, Creeper or Star (The Shape)
     * @param withFlicker
     * @param colour The starting colour of the firework
     * @param fadeTo The colour the firework changes to.
     * @param withTrail If the firework leaves a tail when it moves.
     * @return
     */
    public static Firework launchFirework(Location location, FireworkEffect.Type type, boolean withFlicker, Color colour, Color fadeTo, boolean withTrail) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(withFlicker)
                .withColor(colour)
                .withFade(fadeTo)
                .trail(withTrail)
                .with(type)
                .trail(withTrail)
                .build();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        return firework;
    }

    private static Color fireworkColorFromInt(int i) {
        switch (i) {
            case 1:
            default:
                return Color.SILVER;
            case 2:
                return Color.AQUA;
            case 3:
                return Color.BLACK;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.FUCHSIA;
            case 6:
                return Color.GRAY;
            case 7:
                return Color.GREEN;
            case 8:
                return Color.LIME;
            case 9:
                return Color.MAROON;
            case 10:
                return Color.NAVY;
            case 11:
                return Color.OLIVE;
            case 12:
                return Color.ORANGE;
            case 13:
                return Color.PURPLE;
            case 14:
                return Color.RED;
            case 15:
                return Color.YELLOW;
            case 16:
        }
        return Color.TEAL;
    }

    public static List<Block> getConnected(Block block, int maxDistance, Material... type) {
        return getConnected(block, maxDistance, Arrays.asList(type));
    }

    public static List<Block> getConnected(Block block, int maxDistance, List<Material> type) {
        List<Block> blocks = new ArrayList<Block>();
        try {
            return getConnected(block.getLocation(), blocks, block, maxDistance * maxDistance, type);
        } catch (StackOverflowError ex) {
            InnPlugin.logError("StackOverflowError: BlockHandler");
            return blocks;
        }
    }

    private static List<Block> getConnected(Location center, List<Block> blocks, Block block, int maxDistanceSqrd, List<Material> type) {

        for (BlockFace face : getAllFaces()) {
            Block relative = block.getRelative(face);
            if (type.contains(relative.getType())
                    && !contains(blocks, relative)
                    && center.distanceSquared(relative.getLocation()) <= maxDistanceSqrd) {
                blocks.add(relative);
                getConnected(center, blocks, relative, maxDistanceSqrd, type);
            }
        }

        return blocks;
    }

    public static boolean contains(List<Block> blocks, Block block) {
        for (Block target : blocks) {
            if (target.getLocation().equals(block.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public static void calcTreeFell(final IdpPlayer player, final Block block) {
        if (player.getSession().hasBonus(PlayerBonus.TREE_FELLING)
                && player.getItemInHand(EquipmentSlot.HAND) != null && player.getItemInHand(EquipmentSlot.HAND).getMaterial().isAxe()
                && LOGS.contains(block.getType())) {

            int i = 0;
            boolean containsLeaves = false;
            while (block.getRelative(BlockFace.UP, i).getType() == block.getType()) {
                for (BlockFace face : BlockHandler.getAllFaces()) {
                    if (LEAVES.contains(block.getRelative(BlockFace.UP, i).getRelative(face).getType())) {
                        containsLeaves = true;
                        break;
                    }
                }
                i++;
            }

            if (containsLeaves) {

                List<Material> LOGS_AND_LEAVES = new ArrayList<Material>();
                LOGS_AND_LEAVES.addAll(LOGS);
                LOGS_AND_LEAVES.addAll(LEAVES);
                int logCount = 0;
                final List<Block> chop = BlockHandler.getConnected(block, 40, LOGS_AND_LEAVES);
                List<Block> stumps = new ArrayList<Block>();
                for (Iterator<Block> it = chop.iterator(); it.hasNext();) {
                    Block stump = it.next();
                    if (LOGS.contains(stump.getType())) {
                        if (stump.getLocation().distanceSquared(block.getLocation()) > 0.01
                                && (stump.getRelative(BlockFace.DOWN).getType() == Material.DIRT
                                || stump.getRelative(BlockFace.DOWN).getType() == Material.GRASS)) {
                            stumps.add(stump);
                            it.remove();
                        }
                    }
                }

                for (Iterator<Block> it = chop.iterator(); it.hasNext();) {
                    Block closer = it.next();
                    boolean remove = false;
                    for (Block stump : stumps) {
                        if (stump.getLocation().distanceSquared(closer.getLocation()) < block.getLocation().distanceSquared(closer.getLocation())) {
                            remove = true;
                            break;
                        }
                    }

                    if (remove) {
                        it.remove();
                    } else if (LOGS.contains(closer.getType())) {
                        logCount++;
                    }
                }

                chop.sort(new Comparator<Block>() {

                    @Override
                    public int compare(Block o1, Block o2) {
                        if (o1.getType() != o2.getType()) {
                            return LOGS.contains(o1.getType()) ? -1 : 1;
                        } else if (o2.getLocation().equals(o1.getLocation())) {
                            return 0;
                        } else if (o1.getLocation().distanceSquared(block.getLocation()) > o2.getLocation().distanceSquared(block.getLocation())) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                if (player.getHandle().getGameMode() != GameMode.CREATIVE) {
                    IdpItemStack stack = player.getItemInHand(EquipmentSlot.HAND);
                    stack.setDataDamage((short) (stack.getData() + logCount));
                    if (stack.getData() >= stack.getMaterial().getMaxDurability()) {
                        stack = IdpItemStack.EMPTY_ITEM;
                    }
                    player.setItemInHand(EquipmentSlot.HAND, stack);
                }

                Bukkit.getScheduler().runTaskLater(InnPlugin.getPlugin(), new Runnable() {

                    @Override
                    public void run() {
                        if (!chop.isEmpty()) {
                            int id = BlockHandler.getBlockTypeId(chop.get(0));

                            if (LOGS.contains(chop.get(0).getType())) {
                                block.getWorld().playEffect(chop.get(0).getLocation(), Effect.STEP_SOUND, id, 32);
                                chop.get(0).breakNaturally();
                            } else if (LEAVES.contains(chop.get(0).getType())) {
                                block.getWorld().playEffect(chop.get(0).getLocation(), Effect.STEP_SOUND, id, 32);
                                chop.get(0).breakNaturally();
                            }

                            chop.remove(0);
                            Bukkit.getScheduler().runTaskLater(InnPlugin.getPlugin(), this, 1);
                        }
//                        else {
//                            if (block.getType() == Material.AIR) {
//                                switch (block.getType()) {
//                                    case LOG:
//                                        block.setTypeIdAndData(Material.SAPLING.getId(), block.getData(), true);
//                                        break;
//                                    case LOG_2:
//                                        block.setTypeIdAndData(Material.SAPLING.getId(), (byte) (block.getData() + 4), true);
//                                        break;
//                                }
//                            }
//                        }
                    }
                }, 1);
            }
        }
    }

}

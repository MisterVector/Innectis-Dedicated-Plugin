package net.innectis.innplugin.objects.owned.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.InnectisDoor;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * @author Lynxy
 */
public class DoorHandler {

    private static HashMap<Integer, InnectisDoor> _doors = new HashMap<Integer, InnectisDoor>();

    public static synchronized HashMap<Integer, InnectisDoor> getDoors() {
        return _doors;
    }

    public static synchronized void setDoors(HashMap<Integer, InnectisDoor> doors) {
        _doors = doors;
    }

    public static boolean loadDoors() {
        getDoors().clear();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM doors;");
            result = statement.executeQuery();

            //load all doors into memory
            while (result.next()) {
                InnectisDoor door = getDoorFromResultSet(result);

                if (door != null) {
                    getDoors().put(result.getInt("doorid"), door);
                }
            }

            saveDoors(); //save any door that may have been modified
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET DOORS FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    public static void saveDoors() {
        InnectisDoor door;
        int exceptionCt = 0;
        Exception exception = null;
        for (Iterator<InnectisDoor> it = getDoors().values().iterator(); it.hasNext();) {
            door = it.next();
            if (door.getUpdated()) {
                try {
                    door.save();
                } catch (Exception ex) {
                    if (exception == null) {
                        exception = ex;

                        InnPlugin.logError("################### ERROR SAVING DOOR #" + ChatColor.GOLD + door.getId() + ChatColor.RED + " ###################", ex);
                    }
                    exceptionCt++;
                }
            }
        }
        if (exceptionCt > 0) {
            InnPlugin.getPlugin().sendAdminMessage("savedoorexception ", "There were " + exceptionCt + " error(s) saving doors! Please check log.");
        }
    }

    public static List<InnectisDoor> getAllDoors() {
        return new ArrayList<InnectisDoor>(_doors.values());
    }

    public static InnectisDoor getDoor(int doorId) {
        return getDoors().get(doorId);
    }

    public static InnectisDoor getDoor(Location location) {
        return getDoor(location, true);
    }

    public static InnectisDoor getDoor(Location location, boolean allowRemove) {
        InnectisDoor door;
        for (Iterator<InnectisDoor> it = _doors.values().iterator(); it.hasNext();) {
            door = it.next();
            if (door.isAtLocation(location)) {
                if (door.isValid()) {
                    return door;
                } else if (allowRemove) {
                    if (removeDoorForcibly(location.getBlock(), door.getId(), false)) {
                        it.remove();
                    }
                }
            }
        }
        return null;
    }

    public static List<InnectisDoor> getDoors(String owner) {
        List<InnectisDoor> doors = new ArrayList<InnectisDoor>();
        InnectisDoor door;
        for (Iterator<InnectisDoor> it = getDoors().values().iterator(); it.hasNext();) {
            door = it.next();
            if (door.getOwner().equalsIgnoreCase(owner)) {
                doors.add(door);
            }
        }
        return doors;
    }

    private static InnectisDoor getDoorFromResultSet(ResultSet result) throws SQLException {
        World world = Bukkit.getWorld(result.getString("world"));

        if (world == null) {
            return null;
        }

        int x1 = result.getInt("locx1"), y1 = result.getInt("locy1"), z1 = result.getInt("locz1");
        int x2 = result.getInt("locx2"), y2 = result.getInt("locy2"), z2 = result.getInt("locz2");

        Block door1 = world.getBlockAt(x1, y1, z1);
        Block door2 = world.getBlockAt(x2, y2, z2);
        Block primaryDoor, optionalDoor;

        IdpMaterial door1Mat = IdpMaterial.fromBlock(door1);
        IdpMaterial door2Mat = IdpMaterial.fromBlock(door2);

        boolean point1IsDoor = door1Mat == IdpMaterial.IRON_DOOR_BLOCK;
        boolean point2IsDoor = door2Mat == IdpMaterial.IRON_DOOR_BLOCK;

        if (point1IsDoor) {
            primaryDoor = door1;
            optionalDoor = getAdjacentDoor(door1, door1Mat);
        } else if (point2IsDoor) {
            primaryDoor = door2;
            optionalDoor = getAdjacentDoor(door2, door2Mat);
        } else {
            return null;
        }

        String ownerIdString = result.getString("owner_id");
        UUID ownerId = UUID.fromString(ownerIdString);
        PlayerCredentials ownerCredentials = null;

        if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
            ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
        } else {
            ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
        }

        int doorid = result.getInt("doorid");
        PreparedStatement statement = DBManager.prepareStatement("SELECT player_id, isop FROM doors_members WHERE doorid = ?;");
        statement.setInt(1, doorid);
        ResultSet result2 = statement.executeQuery();

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();

        while (result2.next()) {
            String memberIdString = result2.getString("player_id");
            UUID memberId = UUID.fromString(memberIdString);

            if (memberId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                members.add(Configuration.EVERYONE_CREDENTIALS);
            } else if (memberId.equals(Configuration.LOT_ACCESS_IDENTIFIER)) {
                members.add(Configuration.LOT_ACCESS_CREDENTIALS);
            } else {
                boolean isOp = result2.getBoolean("isop");

                PlayerCredentials memberCredentials = PlayerCredentialsManager.getByUniqueId(memberId, true);

                if (isOp) {
                    operators.add(memberCredentials);
                } else {
                    members.add(memberCredentials);
                }
            }
        }

        DBManager.closeResultSet(result2);
        DBManager.closePreparedStatement(statement);

        return new InnectisDoor(world, primaryDoor, optionalDoor, result.getInt("doorid"), ownerCredentials, members, operators, result.getLong("flags"));
    }

    public static boolean hasDoor(String playerName) {
        for (Iterator<InnectisDoor> it = getDoors().values().iterator(); it.hasNext();) {
            InnectisDoor door = it.next();

            if (door.getOwner().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOwnDoor(String playerName, Location location) {
        InnectisDoor door = getDoor(location);

        if (door == null) {
            return false;
        }

        if (door.getOwner().equalsIgnoreCase(playerName)) {
            return true;
        }

        return false;
    }

    public static InnectisDoor createDoor(PlayerCredentials ownerCredentials, Block block) throws SQLException {
        Location loc = block.getLocation();
        InnectisDoor door = getDoor(loc);

        if (door != null) { //prevent hijacking a door
            removeDoorForcibly(block, door.getId(), true);
            //player.printError("Illegal door placement. A door is already registered here!");
            //InnPlugin.getPlugin().logError("Player " + player.getName() + " tried placing an iron door over existing door #" + door.getId());
            //return null;
        }

        //check for adjacent doors
        Block attached = getAdjacentDoor(block, IdpMaterial.fromBlock(block));

        if (attached == null) { //no adjacent doors
            //all is good, make the door
            InnectisDoor inndoor = new InnectisDoor(block.getWorld(), block, null, -1, ownerCredentials, null, null, 0);
            inndoor.save();
            DoorHandler.getDoors().put(inndoor.getId(), inndoor);
            return inndoor;
        } else {
            InnectisDoor innAttached = getDoor(attached.getLocation());

            // Other door doesn't exist either
            if (innAttached == null) {
                // Create new double door
                InnectisDoor inndoor = new InnectisDoor(block.getWorld(), block, attached, -1, ownerCredentials, null, null, 0);
                inndoor.save();
                return inndoor;
            }

            if (innAttached.isDoubleDoor()) {
                return null; //already a double door
            }

            String ownerName = ownerCredentials.getName();

            if (!innAttached.getOwner().equalsIgnoreCase(ownerName)) {
                IdpPlayer player = InnPlugin.getPlugin().getPlayer(ownerCredentials.getUniqueId());

                if (player != null && player.isOnline()) {
                    player.printError("You cannot place a door next to a door you do not own.");
                    return null;
                }
            }

            innAttached.setDoor2(block);
            innAttached.save();
            return innAttached;
        }
    }

    public static boolean removeDoor(IdpPlayer player, Block block) throws SQLException {
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat != IdpMaterial.IRON_DOOR_BLOCK) {
            return true;
        }

        Location loc = block.getLocation();
        InnectisDoor door = getDoor(loc);

        if (door == null) {
            return true;
        }

        InnectisLot lot = LotHandler.getLot(loc);
        //you can remove a door if you own it or if its on your lot
        if (door.getOwnerCredentials().getUniqueId().equals(player.getUniqueId())
                || (lot != null && lot.getOwnerCredentials().getUniqueId().equals(player.getUniqueId()))
                || player.hasPermission(Permission.owned_object_override)) {
            removeDoorForcibly(block, door.getId(), true);
            return true;
        } else {
            player.printError("You cannot destroy that door!");
        }
        return false;
    }

    /**
     * Returns true if the block was the last one in the set
     */
    public static boolean removeDoorForcibly(Block block, int id, boolean allowRemovalFromMainVariable) {
        IdpMaterial matBelow = IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN));

        if (matBelow == IdpMaterial.IRON_DOOR_BLOCK) {
            block = block.getRelative(BlockFace.DOWN);
        }

        Location loc = block.getLocation();
        InnectisDoor door = getDoor(block.getLocation(), false);
        PreparedStatement statement = null;

        try {
            if (door != null && door.getDoor2() != null) {
                if (door.getDoor1().getLocation().equals(loc)) {
                    //removed door1
                    door.setDoor1(door.getDoor2());
                    door.setDoor2(null);
                } else {
                    //removed door2
                    door.setDoor2(null);
                }
                door.save();
                return false;
            } else {
                statement = DBManager.prepareStatement("DELETE FROM doors WHERE doorid = ?;");
                statement.setInt(1, id);
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);

                statement = DBManager.prepareStatement("DELETE FROM doors_members WHERE doorid = ?;");
                statement.setInt(1, id);
                statement.executeUpdate();

                DBManager.closePreparedStatement(statement);

                if (allowRemovalFromMainVariable) {
                    _doors.remove(id);
                }

                return true;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to remove door #" + id + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    public static void toggleDoor(Block block) {
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        Block belowBlock = block.getRelative(BlockFace.DOWN);
        IdpMaterial matBelow = IdpMaterial.fromBlock(belowBlock);

        if (isDoorMaterial(matBelow)) {
            mat = matBelow;
            block = belowBlock;
        }

        if (isDoorMaterial(mat)) {
            toggleDoorB(block);

            Block adjBlock = getAdjacentDoor(block, mat);

            if (adjBlock != null) {
                byte dat = BlockHandler.getBlockData(block);
                byte adjDat = BlockHandler.getBlockData(adjBlock);

                //only open adjacent door if they both open/shut together
                if ((adjDat & 4) != (dat & 4)) {
                    toggleDoorB(adjBlock);
                }
            }
        }
    }

    private static void toggleDoorB(Block block) {
        byte dat = BlockHandler.getBlockData(block);
        BlockHandler.setBlockData(block, (byte) (dat ^ 4));
    }

    public static Block getAdjacentDoor(Block block, IdpMaterial mat) {
        byte dat = BlockHandler.getBlockData(block);
        int blockDat = (dat & 1) | (dat & 2);

        for (BlockFace face : BlockHandler.getAllSideFaces()) {
            Block adjBlock = block.getRelative(face);
            IdpMaterial adjMat = IdpMaterial.fromBlock(adjBlock);

            if (isDoorMaterial(adjMat) && adjMat == mat) {
                byte adjDat = BlockHandler.getBlockData(adjBlock);
                int thisDat = (adjDat & 1) | (adjDat & 2);

                if (thisDat == blockDat) {
                    return adjBlock;
                }
            }
        }

        return null;
    }

    private static boolean isDoorMaterial(IdpMaterial mat) {
        switch (mat) {
            case OAK_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case IRON_DOOR_BLOCK:
                return true;
            default:
                return false;
        }
    }
    
}

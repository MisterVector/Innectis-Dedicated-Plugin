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
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.InnectisChest;
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
public class ChestHandler {

    /**
     * An enum of chest material types
     */
    public enum VanillaChestType {
        NORMAL_CHEST(1, IdpMaterial.CHEST),
        TRAPPED_CHEST(2, IdpMaterial.TRAPPED_CHEST); // @todo fill with trap chest state class

        private int id;
        private IdpMaterial mat;

        private VanillaChestType(int id, IdpMaterial mat) {
            this.id = id;
            this.mat = mat;
        }

        /**
         * Gets the ID of this chest type
         * @return
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the material of this chest type
         * @return
         */
        public IdpMaterial getMaterial() {
            return mat;
        }

        /**
         * Gets a chest type from its id
         * @param id
         * @return
         */
        public static VanillaChestType fromID(int id) {
            for (VanillaChestType type : values()) {
                if (type.getId() == id) {
                    return type;
                }
            }

            return null;
        }

        /**
         * Gets a chest type from its material
         * @param mat
         * @return
         */
        public static VanillaChestType fromMaterial(IdpMaterial mat) {
            for (VanillaChestType type : values()) {
                if (type.getMaterial() == mat) {
                    return type;
                }
            }

            return null;
        }

        /**
         * Checks if the specified block is a valid chest block
         * @param block
         * @return
         */
        public static boolean isValidChestBlock(IdpMaterial mat) {
            for (VanillaChestType type : values()) {
                if (type.getMaterial() == mat) {
                    return true;
                }
            }

            return false;
        }
    }
    private static HashMap<Integer, InnectisChest> _chests = new HashMap<Integer, InnectisChest>();

    public static synchronized HashMap<Integer, InnectisChest> getChests() {
        return _chests;
    }

    public static synchronized void setChests(HashMap<Integer, InnectisChest> chests) {
        _chests = chests;
    }

    public static boolean loadChests() {
        getChests().clear();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM chests;");
            result = statement.executeQuery();

            //load all chests into memory
            while (result.next()) {
                InnectisChest chest = getChestFromResultSet(result);
                if (chest != null) {
                    getChests().put(result.getInt("chestid"), chest);
                }
            }

            saveChests(); //save any chest that may have been modified
        } catch (SQLException ex) {
            InnPlugin.logError("COULD NOT GET CHESTS FROM DATABASE!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    public static void saveChests() {
        InnectisChest chest;
        int exceptionCt = 0;
        Exception exception = null;
        for (Iterator<InnectisChest> it = getChests().values().iterator(); it.hasNext();) {
            chest = it.next();
            if (chest.getUpdated()) {
                try {
                    chest.save();
                } catch (Exception ex) {
                    if (exception == null) {
                        exception = ex;

                        InnPlugin.logError("################### ERROR SAVING CHEST #" + ChatColor.GOLD + chest.getId() + ChatColor.RED + " ###################", ex);
                    }
                    exceptionCt++;
                }
            }
        }
        if (exceptionCt > 0) {
            InnPlugin.getPlugin().sendAdminMessage("savechestexception ", "There were " + exceptionCt + " error(s) saving chests! Please check log.");
        }
    }

    public static List<InnectisChest> getAllChests() {
        List<InnectisChest> chests = new ArrayList<InnectisChest>();

        for (InnectisChest chest : _chests.values()) {
            if (chest.isValid()) {
                chests.add(chest);
            }
        }

        return chests;
    }

    public static InnectisChest getChest(int chestId) {
        return getChests().get(chestId);
    }

    public static InnectisChest getChest(Location location) {
        return getChest(location, true);
    }

    public static InnectisChest getChest(Location location, boolean allowRemove) {
        InnectisChest chest;
        for (Iterator<InnectisChest> it = _chests.values().iterator(); it.hasNext();) {
            chest = it.next();
            if (chest.isAtLocation(location)) {
                if (chest.isValid()) {
                    return chest;
                } else if (allowRemove) {
                    if (removeChestForcibly(location.getBlock(), chest.getId(), false)) {
                        it.remove();
                    }
                }
            }
        }
        return null;
    }

    public static List<InnectisChest> getChests(String playerName) {
        List<InnectisChest> chests = new ArrayList<InnectisChest>();
        InnectisChest chest;
        for (Iterator<InnectisChest> it = getChests().values().iterator(); it.hasNext();) {
            chest = it.next();
            if (chest.getOwner().equalsIgnoreCase(playerName) && chest.isValid()) {
                chests.add(chest);
            }
        }
        return chests;
    }

    private static InnectisChest getChestFromResultSet(ResultSet result) throws SQLException {
        World world = Bukkit.getWorld(result.getString("world"));

        if (world == null) {
            return null;
        }

        VanillaChestType type = VanillaChestType.fromID(result.getInt("typeid"));

        int x1 = result.getInt("locx1"), y1 = result.getInt("locy1"), z1 = result.getInt("locz1");
        int x2 = result.getInt("locx2"), y2 = result.getInt("locy2"), z2 = result.getInt("locz2");

        Block chest1 = world.getBlockAt(x1, y1, z1);
        Block chest2 = world.getBlockAt(x2, y2, z2);

        Block primaryChest, optionalChest;

        IdpMaterial chest1Mat = IdpMaterial.fromBlock(chest1);
        IdpMaterial chest2Mat = IdpMaterial.fromBlock(chest2);

        boolean point1IsChest = VanillaChestType.isValidChestBlock(chest1Mat);
        boolean point2IsChest = VanillaChestType.isValidChestBlock(chest2Mat);

        if (point1IsChest) {
            primaryChest = chest1;
            optionalChest = getAttachedChest(chest1);
        } else if (point2IsChest) {
            primaryChest = chest2;
            optionalChest = getAttachedChest(chest2);
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

        int chestid = result.getInt("chestid");
        PreparedStatement statement = DBManager.prepareStatement("SELECT player_id, isop FROM chests_members WHERE chestid = ?;");
        statement.setInt(1, chestid);
        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();
        ResultSet result2 = statement.executeQuery();

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

        return new InnectisChest(type, world, primaryChest, optionalChest, result.getInt("chestid"), ownerCredentials, members, operators, result.getLong("flags"));
    }

    public static boolean hasChest(String playerName) {
        InnectisChest chest;
        for (Iterator<InnectisChest> it = getChests().values().iterator(); it.hasNext();) {
            chest = it.next();

            if (chest.getOwner().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOwnChest(String playerName, Location location) {
        InnectisChest chest = getChest(location);

        if (chest == null) {
            return false;
        }

        if (chest.getOwner().equalsIgnoreCase(playerName)) {
            return true;
        }

        return false;
    }

    public static InnectisChest createChest(PlayerCredentials ownerCredentials, Block block) throws SQLException {
        InnectisChest existingChest = getChest(block.getLocation());

        // Allow placement, original owner is assumed
        if (existingChest != null) {
            return existingChest;
        }

        //check for adjacent chests
        Block attachedBlock = getAttachedChest(block);

        if (attachedBlock != null) { //no adjacent chests
            InnectisChest innAttached = getChest(attachedBlock.getLocation());

            if (innAttached != null) {
                if (innAttached.isDoubleChest()) {
                    return null; //already an double chest
                }

                if (!innAttached.getOwner().equalsIgnoreCase(ownerCredentials.getName())) {
                    IdpPlayer player = InnPlugin.getPlugin().getPlayer(ownerCredentials.getUniqueId());

                    if (player != null) {
                        player.printError("You cannot place a chest next to a chest you do not own.");
                    }

                    return null;
                }

                innAttached.setChest2(block);
                innAttached.save();
                return innAttached;
            }
        }

        //all is good, make the chest
        InnectisChest innchest = new InnectisChest(VanillaChestType.fromMaterial(IdpMaterial.fromBlock(block)), block.getWorld(), block, null, -1, ownerCredentials, null, null, 0);
        innchest.save();
        getChests().put(innchest.getId(), innchest);
        return innchest;
    }

    public static boolean removeChest(IdpPlayer player, Block block) throws SQLException {
        if (!VanillaChestType.isValidChestBlock(IdpMaterial.fromBlock(block))) {
            return true;
        }

        Location loc = block.getLocation();
        InnectisChest chest = getChest(loc);

        if (chest == null || chest.getOwner() == null) {
            return true;
        }

        InnectisLot lot = LotHandler.getLot(loc);

        //you can remove a chest if you own it or if its on your lot
        if (chest.getOwnerCredentials().getUniqueId().equals(player.getUniqueId())
                || (lot != null && lot.getOwnerCredentials().getUniqueId().equals(player.getUniqueId()))
                || player.hasPermission(Permission.owned_object_override)) {
            removeChestForcibly(block, chest.getId(), true);
            return true;
        } else {
            player.printError("You cannot destroy that chest!");
        }
        return false;
    }

    /**
     * Returns true if the block was the last one in the set
     */
    public static boolean removeChestForcibly(Block block, int id, boolean allowRemovalFromMainVariable) {
        Location loc = block.getLocation();
        PreparedStatement statement = null;

        try {
            InnectisChest chest = getChest(block.getLocation(), false);

            if (chest != null && chest.getChest2() != null) {
                if (chest.getChest1().getLocation().equals(loc)) {
                    //removed chest1
                    chest.setChest1(chest.getChest2());
                    chest.setChest2(null);
                } else {
                    //removed chest2
                    chest.setChest2(null);
                }
                chest.save();
                return false;
            } else {
                statement = DBManager.prepareStatement("DELETE FROM chests WHERE chestid = ?;");
                statement.setInt(1, id);
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);

                statement = DBManager.prepareStatement("DELETE FROM chests_members WHERE chestid = ?;");
                statement.setInt(1, id);
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);

                statement = DBManager.prepareStatement("DELETE FROM chestlog WHERE chestid = ?;");
                statement.setInt(1, id);
                statement.executeUpdate();

                DBManager.closePreparedStatement(statement);

                if (allowRemovalFromMainVariable) {
                    _chests.remove(id);
                }

                return true;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to remove chest #" + id + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    public static Block getAttachedChest(Block block) {
        BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        for (BlockFace face : FACES) {
            Block other = block.getRelative(face);
            IdpMaterial otherMaterial = IdpMaterial.fromBlock(other);

            // Make sure other block is the same as this one
            if (otherMaterial == mat && VanillaChestType.isValidChestBlock(otherMaterial)) {
                return other;
            }
        }
        return null;
    }

}

package net.innectis.innplugin.objects.owned;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.pojo.ChestLog;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler.VanillaChestType;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.util.DatabaseTools;
import net.innectis.innplugin.util.ObjectParseException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftChest;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Lynxy
 */
public class InnectisChest extends InnectisOwnedObject {

    // Cached comparator for IdpItemStacks in chests
    private static Comparator<IdpItemStack> chestComparator = null;

    private Block chest1;
    private Block chest2;
    private VanillaChestType type;

    public InnectisChest(VanillaChestType type, World world, Block chest1, Block chest2, int id, PlayerCredentials ownerCredentials, List<PlayerCredentials> members, List<PlayerCredentials> operators, long flags) {
        super(world, chest1.getLocation().toVector(), (chest2 == null ? chest1.getLocation().toVector() : chest2.getLocation().toVector()), id, ownerCredentials, members, operators, flags);
        this.chest1 = chest1;
        this.chest2 = chest2;
        this.type = type;
    }

    @Override
    protected Class<? extends FlagType> getEnumClass() {
        return ChestFlagType.class;
    }

    /**
     * Gets the type of vanilla chest this owned chest is
     * @return
     */
    public VanillaChestType getVanillaChestType() {
        return type;
    }

    public Block getChest1() {
        return this.chest1;
    }

    public Chest getChest1Chest() {
        if (this.chest1.getState() instanceof Chest) {
            return (Chest) this.chest1.getState();
        }

        return null;
    }

    public void setChest1(Block chest) {
        this.chest1 = chest;
    }

    public Block getChest2() {
        return this.chest2;
    }

    public Chest getChest2Chest() {
        if (this.chest2 != null && this.chest2.getState() instanceof Chest) {
            return (Chest) this.chest2.getState();
        }

        return null;
    }

    public void setChest2(Block chest) {
        this.chest2 = chest;
    }

    /**
     * Returns true if location is within the area of this object
     * @param location
     */
    @Override
    public boolean isAtLocation(Location location) {
        if (chest1 != null) {
            Location chest1Loc = chest1.getLocation();

            if (chest1.getWorld().equals(location.getWorld())
                && chest1Loc.getBlockX() == location.getBlockX()
                && chest1Loc.getBlockY() == location.getBlockY()
                && chest1Loc.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }


        if (chest2 != null) {
            Location chest2Loc = chest2.getLocation();

            if (chest2.getWorld().equals(location.getWorld())
                && chest2Loc.getBlockX() == location.getBlockX()
                && chest2Loc.getBlockY() == location.getBlockY()
                && chest2Loc.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether or not the blocks specified for this InnectisChest are actually Chests
     */
    public boolean isValid() {
        // Not valid if chest not found
        if (chest1 == null) {
            return false;
        }

        IdpMaterial chestMaterial = IdpMaterial.fromBlock(chest1);

        // This block is not a valid chest block
        if (!(chestMaterial == IdpMaterial.CHEST
                || chestMaterial == IdpMaterial.TRAPPED_CHEST)) {
            return false;
        }

        // Second chest is not the same type of chest as the first
        if (chest2 == null || chestMaterial != IdpMaterial.fromBlock(chest2)) {
            chest2 = null; //this object is still valid since chest1 exists
        }

        return !(chest1 == null);
    }

    public boolean isDoubleChest() {
        return (chest2 != null);
    }

    public IdpInventory getInventory() {
        return getInventory("container.chest");
    }

    public IdpInventory getInventory(String chestTitle) {
        try {
            Chest chst1 = getChest1Chest();
            Chest chst2 = getChest2Chest();

            // TODO: This can't be the only solution, so find one that works

            // Make sure the tile entity exists
            if (chst1 != null) {
                CraftChest craftChst1 = (CraftChest) chst1;

                if (craftChst1.getTileEntity() == null) {
                    return null;
                }
            }

            // Make sure the tile entity exists
            if (chst2 != null) {
                CraftChest craftChst2 = (CraftChest) chst2;

                if (craftChst2.getTileEntity() == null) {
                    return null;
                }
            }

            if (chst1 != null && chst2 != null) {
                BlockFace relation = chst1.getBlock().getFace(chst2.getBlock());
                // Check for the correct order according to the facing.
                switch (relation) {
                    case NORTH:
                    case WEST:
                        return new IdpInventory(chst2.getBlockInventory(), chst1.getBlockInventory());
                    default:
                        return new IdpInventory(chst1.getBlockInventory(), chst2.getBlockInventory());
                }
            } else if (chst1 != null) {
                return new IdpInventory(chst1.getBlockInventory());
            } else if (chst2 != null) {
                return new IdpInventory(chst2.getBlockInventory());
            }
        } catch (Exception ex) {
            InnPlugin.logError("Exception in getting chest inventory", ex);
        }
        return null;
    }

    /**
     * Sets the inventory of this chest
     * @param inv
     */
    public void setInventory(IdpInventory inv) {
        Chest chest1 = getChest1Chest();
        Chest chest2 = getChest2Chest();
        Chest finalChest = (chest1 != null ? chest1 : chest2);

        if (finalChest != null) {
            Inventory chestInv = finalChest.getInventory();
            chestInv.setContents(inv.getContents());
            finalChest.update();
        }
    }

    /**
     * Logs chest access from the following player represented by ID
     * @param playerId
     */
    public void logChestAccess(UUID playerId) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT DELAYED INTO chestlog (chestid, player_id) VALUES (?,?);");
            statement.setInt(1, super.getId());
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ChestHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Returns a list of access logs for this chest.
     * @param The amount of logs; if not given it will default to 10;
     * @returns List of block logs. <br/>
     * When the server couldn't get the logs <b>NULL</b> is returned.
     */
    public List<ChestLog> getAccessLogs(int... amount) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM chestlog WHERE chestid = ? ORDER BY DATE desc LIMIT ?;");
            statement.setInt(1, super.getId());
            statement.setInt(2, (amount.length == 0 ? 10 : amount[0]));

            // Return the parsed list
            return DatabaseTools.parseToObjects(ChestLog.class, statement.executeQuery());
        } catch (SQLException ex) {
            InnPlugin.logError("SQLException when getting chestaccess logs!", ex);
        } catch (ObjectParseException ex) {
            InnPlugin.logError("ObjectParseException parsing chestlogs into objects", ex.getInnerException());
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    // TODO: Add lot member access checks

    private boolean createChestInDB() {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("REPLACE INTO chests "
                    + "(typeid, owner_id, world, locx1, locy1, locz1, locx2, locy2, locz2, flags)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, getVanillaChestType().getId());
            statement.setString(2, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(3, super.getWorld().getName());
            statement.setInt(4, getChest1().getX());
            statement.setInt(5, getChest1().getY());
            statement.setInt(6, getChest1().getZ());
            statement.setInt(7, getChest2() == null ? 0 : getChest2().getX());
            statement.setInt(8, getChest2() == null ? 0 : getChest2().getY());
            statement.setInt(9, getChest2() == null ? 0 : getChest2().getZ());
            statement.setLong(10, super.getFlags());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                super.setId(result.getInt(1));
                super.setUpdated(false);
            } else {
                InnPlugin.logError("New chest was not found in the database!");
                return false;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to create new chest!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    @Override
    public boolean save() {
        PreparedStatement statement = null;

        try {
            if (getId() == -1) {
                createChestInDB();
                return true;
            }

            statement = DBManager.prepareStatement("UPDATE chests SET "
                    + "typeid = ?, owner_id = ?, world = ?, "
                    + "locx1 = ?, locy1 = ?, locz1  = ?, "
                    + "locx2  = ?, locy2  = ?, locz2  = ?, "
                    + "flags  = ? WHERE chestid = ?;");
            statement.setInt(1, getVanillaChestType().getId());
            statement.setString(2, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(3, super.getWorld().getName());
            statement.setInt(4, getChest1().getX());
            statement.setInt(5, getChest1().getY());
            statement.setInt(6, getChest1().getZ());
            statement.setInt(7, getChest2() == null ? 0 : getChest2().getX());
            statement.setInt(8, getChest2() == null ? 0 : getChest2().getY());
            statement.setInt(9, getChest2() == null ? 0 : getChest2().getZ());
            statement.setLong(10, super.getFlags());
            statement.setInt(11, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM chests_members WHERE chestid = ?;");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            for (PlayerCredentials pc : getMembers()) {
                statement = DBManager.prepareStatement("INSERT INTO chests_members (chestid, player_id, isop) VALUES (?, ?, 0);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }
            for (PlayerCredentials pc : getOperators()) {
                statement = DBManager.prepareStatement("INSERT INTO chests_members (chestid, player_id, isop) VALUES (?, ?, 1);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            super.setUpdated(false);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save chest!", ex);
            return false;
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * This will attempt to automatically order the chest.
     */
    public void sortContents() {
        // Create a blank array to store the new chest content.
        IdpItemStack[] contents = new IdpItemStack[isDoubleChest() ? 54 : 27];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = IdpItemStack.EMPTY_ITEM;
        }

        // Cycle through each item and see if it can be added to each slot.
        // This is done to merge any item stacks (if possible).
        for (IdpItemStack stack : getInventory().getContentsIdp()) {
            for (int i = 0; i < contents.length; i++) {
                IdpItemStack targetStack = contents[i];

                if (targetStack.getMaterial() == IdpMaterial.AIR) {
                    // Next stop is empty, lets slot this stack in here!
                    contents[i] = stack;
                    break;
                } else if (targetStack.getMaterial() == stack.getMaterial() && targetStack.getItemdata().matches(stack.getItemdata())) {

                    // We have found a stack to merge with, lets crunch some numbers!
                    int targetAccept = targetStack.getMaterial().getMaxStackSize() - targetStack.getAmount();
                    if (targetAccept < stack.getAmount()) {
                        stack.setAmount(stack.getAmount() - targetAccept);
                        targetStack.setAmount(targetStack.getMaterial().getMaxStackSize());
                        contents[i] = targetStack;
                    } else {
                        targetStack.setAmount(targetStack.getAmount() + stack.getAmount());
                        contents[i] = targetStack;
                        break;
                    }

                }
            }
        }

        // Now lets use our sorted to order the new item stacks.
        Arrays.sort(contents, getItemStackComparator());

        // Lets now update the inventory (use bukkit for this).
        Chest bukkitChest = (Chest) chest1.getState();
        Inventory chestInv = bukkitChest.getInventory();
        chestInv.setContents(IdpItemStack.toBukkitItemStack(contents));
        bukkitChest.update();
    }

    /**
     * Gets the cached comparator for item stacks in chests
     * @return
     */
    private static Comparator<IdpItemStack> getItemStackComparator() {
        if (chestComparator == null) {
            chestComparator = new Comparator<IdpItemStack>() {
                @Override
                public int compare(IdpItemStack o1, IdpItemStack o2) {
                    IdpMaterial firstMaterial = o1.getMaterial();
                    IdpMaterial secondMaterial = o2.getMaterial();

                    int dmg1 = (firstMaterial.getId() > 255 ? firstMaterial.getData() : 0);
                    int dmg2 = (secondMaterial.getId() > 255 ? secondMaterial.getData() : 0);

                    // If materials match or either material is air, sort by amount, else sort by damage.
                    if (firstMaterial == secondMaterial || firstMaterial == IdpMaterial.AIR || secondMaterial == IdpMaterial.AIR) {
                        if (o1.getAmount() == o2.getAmount()) {
                            return dmg1 - dmg2;
                        } else {
                            return o2.getAmount() - o1.getAmount();
                        }
                    }

                    // Otherwise, sort by material.
                    return firstMaterial.compareTo(secondMaterial);
                }
            };
        }

        return chestComparator;
    }

    public OwnedObjectType getType() {
        return OwnedObjectType.CHEST;
    }

}

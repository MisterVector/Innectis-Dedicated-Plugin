package net.innectis.innplugin.items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 *
 * @author Hret
 *
 * Hanler class to store a bag of items in the database.
 */
public class StackBag {

    private long bagid;
    private int bagsize;
    private IdpItemStack[] contents;

    /**
     * Makes a new bag with the given ID and size
     * @param bagid
     * @param bagsize
     */
    public StackBag(long bagid, int bagsize) {
        this.bagid = bagid;
        this.bagsize = bagsize;
        this.contents = new IdpItemStack[bagsize];
    }

    /**
     * Makes a new bag with the given size
     * @param bagsize
     */
    public StackBag(int bagsize) {
        this(0, bagsize);
    }

    /**
     * Makes a new bag with the given items.
     * The bagsize will be take from the array's size
     * @param contents
     */
    public StackBag(IdpItemStack[] contents) {
        this(0, contents.length);
        this.contents = contents;
    }

    /**
     * Makes a new bag with the ID and contents
     * @param bagid
     * @param contents
     */
    public StackBag(long bagid, IdpItemStack[] contents) {
        this(bagid, contents.length);
        this.contents = contents;
    }

    /**
     * Adds an itemstack to the contents.
     * @param index
     * @param stack
     * @exception ArrayIndexOutOfBoundsException when the index is not pointing to
     * an valid location in the contents (0 to contentsize - 1)
     */
    public void addItemStack(int index, IdpItemStack stack) {
        if (index < 0 || index >= this.contents.length) {
            throw new ArrayIndexOutOfBoundsException("Outside of range");
        }
        this.contents[index] = stack;
    }

    /**
     * Saves the bag in the database.
     * If the ID is known it will update else it will be inserted. <br/>
     * The BagID will be set to the ID of the bag and returned.
     * @return
     */
    public long save() {
        if (getBagid() <= 0) {
            return insert();
        } else {
            return update();
        }

    }

    /**
     * Removes the bag from the database.
     */
    public void delete() {
        Connection conn = null;
        try {
            conn = DBManager.openNewConnection();
            conn.setAutoCommit(false);

            PreparedStatement statement = conn.prepareStatement(" DELETE FROM contentbag WHERE id = ? ");
            statement.setLong(1, getBagid());
            statement.executeUpdate();

            statement = conn.prepareStatement(" DELETE FROM contentbag_items WHERE bagid = ? ");
            statement.setLong(1, getBagid());
            statement.executeUpdate();

            conn.commit();
        } catch (SQLException sqle) {
            InnPlugin.logError("Cant delete stackcontentbag", sqle);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Inserts the current bag into the database.
     * @return the generated ID for the bag
     */
    private long insert() {
        Connection conn = null;
        try {
            conn = DBManager.openNewConnection();
            conn.setAutoCommit(false);

            PreparedStatement statement = conn.prepareStatement(" INSERT INTO contentbag (bagsize) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, getBagsize());
            statement.executeUpdate();

            ResultSet set = statement.getGeneratedKeys();
            if (set.next()) {
                this.setBagid(set.getLong(1));
            } else {
                throw new SQLException(" No generated ID found ");
            }

            IdpItemStack currentStack;
            for (int index = 0; index < contents.length; index++) {
                currentStack = contents[index];


                if (currentStack != null && currentStack.getMaterial() != IdpMaterial.AIR) {
                    statement = conn.prepareStatement(" INSERT INTO contentbag_items (bagid,locindex,id,data,amount,itemdata) VALUES (?,?,?,?,?,?) ");
                    statement.setLong(1, getBagid());
                    statement.setInt(2, index);
                    statement.setInt(3, currentStack.getMaterial().getId());
                    statement.setInt(4, currentStack.getData());
                    statement.setInt(5, currentStack.getAmount());

                    if (!currentStack.getItemdata().isEmpty()) {
                        statement.setBytes(6, currentStack.getItemdata().toByte());
                    } else {
                        statement.setNull(6, Types.BLOB);
                    }

                    statement.executeUpdate();
                }
            }

            conn.commit();
            return this.getBagid();
        } catch (SQLException sqle) {
            InnPlugin.logError("Cant insert stackcontentbag", new Throwable(sqle.getMessage()));
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                }
            }
        }
        return -1;
    }

    /**
     * Does an update on the database.
     * @return the bagid
     */
    private long update() {
        Connection conn = null;
        try {
            conn = DBManager.openNewConnection();
            conn.setAutoCommit(false);

            PreparedStatement statement = conn.prepareStatement(" UPDATE contentbag SET bagsize = ? WHERE id = ? ");
            statement.setInt(1, getBagsize());
            statement.setLong(2, getBagid());
            statement.executeUpdate();

            IdpItemStack currentStack;
            byte[] itemdata;
            for (int index = 0; index < contents.length; index++) {
                currentStack = contents[index];
                if (currentStack != null && currentStack.getMaterial() != IdpMaterial.AIR) {
                    statement = conn.prepareStatement(" INSERT INTO contentbag_items (bagid,locindex,id,data,amount,itemdata) VALUES (?,?,?,?,?,?) "
                            + " ON DUPLICATE KEY UPDATE id = ?, data = ?, amount = ?, itemdata = ? ");
                    itemdata = currentStack.getItemdata().toByte();
                    statement.setLong(1, getBagid());
                    statement.setInt(2, index);
                    statement.setInt(3, currentStack.getMaterial().getId());
                    statement.setInt(4, currentStack.getData());
                    statement.setInt(5, currentStack.getAmount());
                    statement.setBytes(6, itemdata);
                    statement.setInt(7, currentStack.getMaterial().getId());
                    statement.setInt(8, currentStack.getData());
                    statement.setInt(9, currentStack.getAmount());
                    statement.setBytes(10, itemdata);
                    statement.executeUpdate();
                } else {
                    statement = conn.prepareStatement("  DELETE FROM contentbag_items WHERE bagid = ? AND locindex = ? ");
                    statement.setLong(1, getBagid());
                    statement.setInt(2, index);
                    statement.executeUpdate();
                }
            }

            conn.commit();
            return this.getBagid();
        } catch (SQLException sqle) {
            InnPlugin.logError("Cant update stackcontentbag", new Throwable(sqle.getMessage()));
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                }
            }
        }
        return -1;
    }

    /**
     * This will lookup the contentbag and load it.
     * @param bagid
     * @return The contentbag with the given ID or null if the bag was not found.
     */
    public static StackBag getContentbag(long bagid) {
        if (bagid <= 0) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            Connection conn = DBManager.getConnection();

            statement = conn.prepareStatement(" SELECT bagsize FROM contentbag WHERE id = ? ");
            statement.setLong(1, bagid);
            result = statement.executeQuery();

            if (!result.next()) {
                throw new SQLException("Bag not found!");
            }

            int size = result.getInt("bagsize");
            StackBag bag = new StackBag(bagid, size);

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = conn.prepareStatement(" SELECT locindex, id, data, amount, itemdata FROM contentbag_items WHERE bagid = ? ");
            statement.setLong(1, bagid);
            result = statement.executeQuery();

            IdpItemStack cont;
            while (result.next()) {
                IdpMaterial mat = IdpMaterial.fromID(result.getInt("id"));

                cont = new IdpItemStack(
                        mat,
                        result.getInt("amount"),
                        result.getInt("data"),
                        ItemData.fromByte(result.getBytes("itemdata")));
                bag.addItemStack(result.getInt("locindex"), cont);
            }

            return bag;
        } catch (SQLException sqle) {
            InnPlugin.logError("Cant delete stackcontentbag", sqle.getMessage());
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Returns the Itemstack on the given index.
     * @param index
     * @return the index or null if nothing at given location or outside bounds
     */
    public IdpItemStack getItemstack(int index) {
        if (index < 0 || index > contents.length) {
            return null;
        }
        return contents[index];
    }

    /**
     * Returns the contents of the bag.
     * @return
     */
    public IdpItemStack[] getContents() {
        return contents;
    }

    /**
     * Returns the contents of the bag.
     * @return
     */
    public void setContents(IdpItemStack[] newcontents) {
        bagsize = newcontents.length;
        this.contents = newcontents;
    }

    /**
     * @return the bagid
     */
    public long getBagid() {
        return bagid;
    }

    /**
     * @param bagid the bagid to set
     */
    public void setBagid(long bagid) {
        this.bagid = bagid;
    }

    /**
     * @return the bagsize
     */
    public int getBagsize() {
        return bagsize;
    }

    /**
     * Delete the bag with the given ID
     * @param bagid
     */
    public static void delete(long bagid) {
        new StackBag(bagid, 0).delete();
    }
    
}

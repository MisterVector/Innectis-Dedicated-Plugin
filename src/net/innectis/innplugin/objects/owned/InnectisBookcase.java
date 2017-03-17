package net.innectis.innplugin.objects.owned;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.StackBag;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Hret
 *
 */
public class InnectisBookcase extends InnectisOwnedObject {

    private Block bookcase;
    private long bagid;
    private IdpItemStack[] items = null;
    private String caseTitle;

    /**
     * Makes a new InnectisBookcase object
     * @param world
     * @param bookcase
     * @param ownerCredentials
     */
    public InnectisBookcase(World world, Block bookcase, PlayerCredentials ownerCredentials) {
        super(world, bookcase.getLocation().toVector(), bookcase.getLocation().toVector(), -1, ownerCredentials, new ArrayList<PlayerCredentials>(0), new ArrayList<PlayerCredentials>(0), 0);
        this.bookcase = bookcase;
        caseTitle = "Bookcase";
        bagid = -1l;
    }

    private InnectisBookcase(World world, Block bookcase, int id, long bagid, PlayerCredentials ownerCredentials, String caseTitle, List<PlayerCredentials> members, List<PlayerCredentials> operators, long flags) {
        super(world, bookcase.getLocation().toVector(), bookcase.getLocation().toVector(), id, ownerCredentials, members, operators, flags);
        this.bookcase = bookcase;
        this.caseTitle = caseTitle;
        this.bagid = bagid;
    }

    @Override
    protected Class<? extends FlagType> getEnumClass() {
        return null;
    }

    /**
     * @return the caseTitle
     */
    public String getCaseTitle() {
        return caseTitle;
    }

    /**
     * @param caseTitle the caseTitle to set
     */
    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
        super.setUpdated(true);
    }

    public Block getBookcase() {
        return bookcase;
    }

    /**
     * Get the items in this bookcase
     * @return
     */
    public IdpItemStack[] getItems() {
        if (items == null) {
            StackBag bag = StackBag.getContentbag(bagid);
            if (bag == null) {
                items = new IdpItemStack[9];
            } else {
                items = bag.getContents();
            }
        }
        return items;
    }

    /*
     * Get the item at the given index
     */
    public IdpItemStack getItem(int index) {
        return getItems()[index];
    }

    /**
     * Set the contents of the bookcase
     * @param items
     */
    public void setItems(IdpItemStack[] items) {
        this.items = items;
        super.setUpdated(true);
    }

    /**
     * Adds a book at the first free location.
     * @param item
     * @return false if the item is not a book or no room
     */
    public boolean addBook(IdpItemStack item) {
        switch (item.getMaterial()) {
            case WRITTEN_BOOK:
            case BOOK:
            case BOOK_AND_QUILL:
            case ENCHANTED_BOOK: {
                for (int i = 0; i < getItems().length; i++) {
                    if (getItem(i) == null || getItem(i).getMaterial() == IdpMaterial.AIR) {
                        items[i] = item;
                        super.setUpdated(true);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    /**
     * Returns true if location is within the area of this object
     * @param location
     */
    @Override
    public boolean isAtLocation(Location location) {
        Location bookcaseLocation = bookcase.getLocation();

        if (bookcase != null) {
            if (bookcase.getWorld().equals(location.getWorld())
                && bookcaseLocation.getBlockX() == location.getBlockX()
                && bookcaseLocation.getBlockY() == location.getBlockY()
                && bookcaseLocation.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }

        return false;
    }

    private void storeItems() {
        StackBag bag = new StackBag(bagid, getItems());
        bagid = bag.save();
    }

    /**
     * Returns true if the player is the owner or a member, or if this bookcase's owner is % or contains member %, or if this bookcase has member
     * @ and player is a member of the Lot this bookcase is on
     * @param player
     */
    @Override
    public boolean canPlayerAccess(String playerName) {
        if (super.canPlayerAccess(playerName)) {
            return true;
        }

        if (containsMember("@")) { //allow lot members
            InnectisLot lot = LotHandler.getLot(bookcase.getLocation());
            if (lot != null && (lot.containsMember(playerName) || lot.containsOperator(playerName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new bookcase in the database
     */
    private boolean createBookcaseInDB() {
        storeItems();

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatementWithAutoGeneratedKeys("REPLACE INTO bookcase "
                    + "(bagid, owner_id, world, locx, locy, locz, flags, casetitle) VALUES (?,?,?,?,?,?,?,?);");
            statement.setLong(1, bagid);
            statement.setString(2, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(3, super.getWorld().getName());
            statement.setInt(4, getBookcase().getX());
            statement.setInt(5, getBookcase().getY());
            statement.setInt(6, getBookcase().getZ());
            statement.setLong(7, super.getFlags());
            statement.setString(8, getCaseTitle());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                setId(result.getInt(1));
                super.setUpdated(false);
            } else {
                InnPlugin.logError("New bookcase not found in the database!");
                return false;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to create bookcase!", ex);
            return false;
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return true;
    }

    /**
     * Save the bookcase into the base
     */
    @Override
    public boolean save() {
        if (getId() == -1) {
            return createBookcaseInDB();
        }

        storeItems();

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DBManager.openNewConnection();
            conn.setAutoCommit(false);

            statement = conn.prepareStatement(
                    "UPDATE bookcase SET bagid = ?, owner_id = ?, world = ?, locx = ?, "
                    + "locy = ?, locz = ?, flags = ?, casetitle = ? WHERE bookcaseid = ?;");
            statement.setLong(1, bagid);
            statement.setString(2, super.getOwnerCredentials().getUniqueId().toString());
            statement.setString(3, super.getWorld().getName());
            statement.setInt(4, getBookcase().getX());
            statement.setInt(5, getBookcase().getY());
            statement.setInt(6, getBookcase().getZ());
            statement.setLong(7, super.getFlags());
            statement.setString(8, getCaseTitle());
            statement.setInt(9, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = conn.prepareStatement("DELETE FROM bookcase_members WHERE bookcaseid = ?; ");
            statement.setLong(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            for (PlayerCredentials pc : super.getMembers()) {
                statement = conn.prepareStatement("INSERT INTO bookcase_members (bookcaseid, player_id, isop) VALUES (?, ?, 0);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            for (PlayerCredentials pc : super.getOperators()) {
                statement = conn.prepareStatement("INSERT INTO bookcase_members (bookcaseid, player_id, isop) VALUES (?, ?, 1);");
                statement.setInt(1, super.getId());
                statement.setString(2, pc.getUniqueId().toString());
                statement.executeUpdate();
                DBManager.closePreparedStatement(statement);
            }

            conn.commit();
            super.setUpdated(false);
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save bookcase!", ex);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                    return false;
                } finally {
                    DBManager.closePreparedStatement(statement);
                }
            }
        }

        return true;
    }

    /**
     * Returns the bookcase with the given ID
     * @param bookcaseid
     * @return the bookcase or null if not found
     */
    public static InnectisBookcase getBookcase(int bookcaseid) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement(""
                    + "SELECT b.bookcaseid, bagid, owner_id, world, locx, locy, locz, flags, casetitle, "
                    + "  (SELECT group_concat(bmem.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bmem where b.bookcaseid = bmem.bookcaseid and isop = 0 ) as member_id_list, "
                    + "   (SELECT group_concat(bop.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bop where b.bookcaseid = bop.bookcaseid and isop = 1 ) as operator_id_list  "
                    + "FROM bookcase as b WHERE b.bookcaseid = ?;");
            statement.setInt(1, bookcaseid);
            set = statement.executeQuery();

            if (set.next()) {
                return fromResultSet(set);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cant get bookcase! ", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * The bookcase on the given location
     * @param location
     * @return THe bookcate or null if not found.
     */
    public static InnectisBookcase getBookcase(Location location) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement(""
                    + "SELECT b.bookcaseid, bagid, owner_id, world, locx, locy, locz, flags, casetitle, "
                    + "  (SELECT group_concat(bmem.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bmem where b.bookcaseid = bmem.bookcaseid and isop = 0 ) as member_id_list, "
                    + "   (SELECT group_concat(bop.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bop where b.bookcaseid = bop.bookcaseid and isop = 1 ) as operator_id_list  "
                    + "FROM bookcase as b WHERE world = ? AND locx = ? AND locy = ? AND locz = ?;");
            statement.setString(1, location.getWorld().getName());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            set = statement.executeQuery();

            if (set.next()) {
                return fromResultSet(set);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cant get bookcase! ", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Looks up all the bookcases owned by the player represented by their ID
     * @param playerName
     * @return List of the bookcases or null is none.
     */
    public static List<InnectisBookcase> getBookcases(String playerName) {
        // TODO: Load these trapdoors in another way, this method isn't the best
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement(""
                    + "SELECT b.bookcaseid, bagid, owner_id, world, locx, locy, locz, flags, casetitle, "
                    + "  (SELECT group_concat(bmem.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bmem where b.bookcaseid = bmem.bookcaseid and isop = 0 ) as member_id_list, "
                    + "   (SELECT group_concat(bop.player_id SEPARATOR ';') "
                    + "    FROM bookcase_members as bop where b.bookcaseid = bop.bookcaseid and isop = 1 ) as operator_id_list  "
                    + "FROM bookcase as b WHERE owner_id = ?;");
            statement.setString(1, credentials.getUniqueId().toString());
            set = statement.executeQuery();

            List<InnectisBookcase> bookcases = new ArrayList<InnectisBookcase>(set.getFetchSize());

            while (set.next()) {
                bookcases.add(fromResultSet(set));
            }

            return bookcases;
        } catch (SQLException ex) {
            InnPlugin.logError("Cant get bookcase! ", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Makes an InnectisBookcase object from the current row of the resultset.
     * @param set
     * @return
     * @throws SQLException
     */
    private static InnectisBookcase fromResultSet(ResultSet set) throws SQLException {
        World world = Bukkit.getWorld(set.getString("world"));

        if (world == null) {
            return null;
        }

        int bookcaseid = set.getInt("bookcaseid");
        long bagid = set.getLong("bagid");

        String ownerIdString = set.getString("owner_id");
        UUID ownerId = UUID.fromString(ownerIdString);
        PlayerCredentials ownerCredentials = null;

        if (ownerId.equals(Configuration.UNASSIGNED_IDENTIFIER)) {
            ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
        } else {
            ownerCredentials = PlayerCredentialsManager.getByUniqueId(ownerId, true);
        }

        String casetitle = set.getString("casetitle");
        int x = set.getInt("locx");
        int y = set.getInt("locy");
        int z = set.getInt("locz");
        long flags = set.getLong("flags");

        List<PlayerCredentials> members = new ArrayList<PlayerCredentials>();
        String memberIdStringList = set.getString("member_id_list");

        if (memberIdStringList != null) {
            for (String memberIdString : memberIdStringList.split(";")) {
                UUID memberId = UUID.fromString(memberIdString);

                if (memberId.equals(Configuration.EVERYONE_IDENTIFIER)) {
                    members.add(Configuration.EVERYONE_CREDENTIALS);
                } else if (memberId.equals(Configuration.LOT_ACCESS_IDENTIFIER)) {
                    members.add(Configuration.LOT_ACCESS_CREDENTIALS);
                } else {
                    PlayerCredentials memberCredentials = PlayerCredentialsManager.getByUniqueId(memberId, true);
                    members.add(memberCredentials);
                }
            }
        }

        List<PlayerCredentials> operators = new ArrayList<PlayerCredentials>();
        String operatorIdStringList = set.getString("operator_id_list");

        if (operatorIdStringList != null) {
            for (String operatorIdString : operatorIdStringList.split(";")) {
                UUID operatorId = UUID.fromString(operatorIdString);
                PlayerCredentials operatorCredentials = PlayerCredentialsManager.getByUniqueId(operatorId);
                operators.add(operatorCredentials);
            }
        }

        return new InnectisBookcase(world, world.getBlockAt(x, y, z), bookcaseid, bagid, ownerCredentials, casetitle, members, operators, flags);
    }

    /**
     * Delete the bookcase
     */
     public void delete() {
        PreparedStatement statement = null;

        try {
            StackBag.delete(bagid);

            statement = DBManager.prepareStatement("DELETE FROM bookcase WHERE bookcaseid = ? ");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("DELETE FROM bookcase_members WHERE bookcaseid = ? ");
            statement.setInt(1, super.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Cant remove get bookcase! ", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * This will check if the bookcase is empty (ea. has no books in it.)
     * @return
     */
    public boolean isEmpty() {
        for (IdpItemStack item : getItems()) {
            if (item != null && item.getMaterial() != IdpMaterial.AIR) {
                return false;
            }
        }

        return true;
    }

    @Override
    public OwnedObjectType getType() {
        return OwnedObjectType.BOOKCASE;
    }

}

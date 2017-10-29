package net.innectis.innplugin.player;

/**
 *
 * @author Hret
 * <p/>
 * The different types of inventorys the server has.
 * This enum is to keep track of all inventory's to make sure a typo wont happen.
 * <p/>
 * This class currently has a couple of pre configured inventories that should be
 * changed to a better system in the future.
 * In reality the current ENUM system is not sufficient to keep track of the
 * inventory type demands with the dynamic worlds and custom lots.
 */
public enum InventoryType {

    /** Inventory used in the main worlds *//** Inventory used in the main worlds */
    MAIN("main", 0),
    /** Inventory used in pixel world */
    UNUSED_1("unused1", 1),
    /** Inventory type that does not get saved to database and resets when switched * */
    NO_SAVE("nosave", 5),
    /** Inventory used in the creative world */
    CREATIVE("creative", 6),
    /** Inventory used in the event world (1.8.1) */
    EVENTWORLD("eventworld", 7),
    //
    // This is a temp solution to dynamic world inventory problem.
    // Note that this should be changed in the future...
    //
    /** A dynamic inventory that gets cleared on shutdown. * */
    MEMORY1("dynamic1", 900),
    /** A dynamic inventory that gets cleared on shutdown * */
    MEMORY2("dynamic2", 901),
    /** A dynamic inventory that gets cleared on shutdown * */
    MEMORY3("dynamic3", 902),
    /** A dynamic inventory that gets cleared on shutdown * */
    MEMORY4("dynamic4", 903),
    /** A dynamic inventory that gets cleared on shutdown * */
    MEMORY5("dynamic5", 904),
    //
    /** Represents an inventory that is empty and not set to anything */
    NONE("", -1);
//
    /** Name for the inventory, not really used (used in old storage) */
    private final String name;
    /** Id for the inventory (for DB reference) */
    private final int id;

    private InventoryType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Method of getting the inventory through the ID
     * If this method is used a lot, it should be alterd to use caching.
     * @param id
     * @return the inventory, or null if its doesn't exist
     */
    public static InventoryType getInventory(int id) {
        for (InventoryType inv : values()) {
            if (inv.id == id) {
                return inv;
            }
        }
        return null;
    }

    /**
     * Name for the inventory
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * ID of the inventory as stored in the database
     * @return the id
     */
    public int getId() {
        return id;
    }

    public static InventoryType getTypeByName(String name) {
        for (InventoryType type : InventoryType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
}

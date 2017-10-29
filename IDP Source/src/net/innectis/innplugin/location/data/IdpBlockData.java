package net.innectis.innplugin.location.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.location.data.DBValueTracker.TrackerState;

/**
 *
 * @author Hret
 *
 * Blockdata object that keeps records of values related to the block.
 * These values can be saved in the database.
 *
 * The <b>IdpBlockDataFactory</b> class handles the caching of this object, and
 * should be the only one that initializes this object.
 */
public class IdpBlockData extends BlockDataDAO {

    // <editor-fold defaultstate="collapsed" desc="DataKeys">
    private static final String KEY_VIRTUAL_BLOCK = "VIRT_BLK";
    private static final String KEY_UNBREAKABLE = "UNBREAK";
    private static final String KEY_BRIDGE_CONTROL = "BRIDGE_CTRL_BLK";
    // </editor-fold >

    //<editor-fold defaultstate="open" desc="Default value handlers">
    /**
     * Checks if the block is an unbreakable block
     * @return true if the block is unbreakable
     */
    public boolean isUnbreakable() {
        Integer status = getIntValue(KEY_UNBREAKABLE);
        return status != null && status == 1;
    }

    /**
     * Sets the unbreakable block status of the block
     * @param unbeakable - true if block is unbreakable
     */
    public void setUnbreakable(boolean unbeakable) {
        if (!unbeakable) {
            removeValue(KEY_UNBREAKABLE);
        } else {
            setValue(KEY_UNBREAKABLE, 1);
        }
    }

    /**
     * Checks if the block is an virtual block
     * @return true if the block is virtual
     */
    public boolean isVirtualBlock() {
        Integer status = getIntValue(KEY_VIRTUAL_BLOCK);
        return status != null && status == 1;
    }

    /**
     * Sets the virtual block status of the block
     * @param isVirtual - true if block is virtual
     */
    public void setVirtualBlockStatus(boolean isVirtual) {
        if (!isVirtual) {
            removeValue(KEY_VIRTUAL_BLOCK);
        } else {
            setValue(KEY_VIRTUAL_BLOCK, 1);
        }
    }

    /**
     * Checks if the block is a bridge controller
     * @return
     */
    public boolean isBridgeController() {
        Integer status = getIntValue(KEY_BRIDGE_CONTROL);
        return status != null && status == 1;
    }

    /**
     * Set a block's bridge controller status
     * @param isController
     * @return
     */
    public void setBridgeController(boolean isController) {
        if (!isController) {
            removeValue(KEY_BRIDGE_CONTROL);
        } else {
            setValue(KEY_BRIDGE_CONTROL, 1);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CRUD functions">
    /**
     * Returns the value of the given key as Long.
     * @param key
     * @return Null when key does not exist
     */
    public Long getLongValue(String key) {
        try {
            return Long.parseLong(getValue(key));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * Returns the value of the given key as Integer.
     * @param key
     * @return Null when key does not exist
     */
    public Integer getIntValue(String key) {
        try {
            return Integer.parseInt(getValue(key));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     * Returns the value of the given key.
     * @param key
     * @return Null when key does not exist
     */
    public String getValue(String key) {
        if (key == null) {
            return null;
        }

        DBValueTracker<String, String> tracker = getTracker(key);
        return tracker == null ? null : tracker.getValue();
    }

    /**
     * Sets the value under the given key in the materialdata.
     * @param key
     * @param value <br/>
     * The method will call the ::toString() method! <br/>
     * Null values will result in removal.
     */
    public void setValue(String key, Object value) {
        DBValueTracker<String, String> tracker = getTracker(key);

        if (tracker == null) {
            tracker = new DBValueTracker<String, String>(key, (value != null ? value.toString() : null), TrackerState.NEW);
            _values.add(tracker);
        } else {
            tracker.setValue(value != null ? value.toString() : null);
        }

        if (strategy == SaveStrategy.EAGER) {
            if (tracker != null) {

                updateTracker(locationId, chunkId, tracker);

                // If status is deleted, removex
                if (tracker.getTrackerState() == TrackerState.DELETED) {
                    _values.remove(tracker);
                }
            }
        }
    }

    /**
     * Removes the value.
     * @param key
     */
    public void removeValue(String key) {
        setValue(key, null);
    }

    /**
     * Clears the blockdata object.
     * <b>When in lazy mode, ::save() must be called! </b>
     */
    public void clear() {
        if (strategy == SaveStrategy.EAGER) {
            _values.clear();
            removeLocationFromDatabase(locationId, chunkId);
        } else {
            for (DBValueTracker tracker : _values) {
                tracker.setValue(null);
            }
        }
    }

    /**
     * Checks if this block data is actually set
     * @return
     */
    public boolean hasData() {
        return (isUnbreakable() || isVirtualBlock() || isBridgeController());
    }

    /**
     * This will update all the value of this object with the database.
     * It will ignore the SaveStrategy of this object.
     */
    public void save() {
        for (DBValueTracker tracker : _values) {
            updateTracker(locationId, chunkId, tracker);
        }
    }

    /**
     * Returns the tracker for the given value;
     * @param key
     * @return
     */
    private DBValueTracker<String, String> getTracker(String key) {
        DBValueTracker<String, String> tracker = null;
        for (int i = 0; i < _values.size(); i++) {
            tracker = _values.get(i);
            if (tracker.getKey().equals(key)) {
                return tracker;
            }

            if (tracker.getTrackerState() == TrackerState.DELETED) {
                _values.remove(i);
            }
        }

        return null;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Constructors & variables">
    protected IdpBlockData(Long chunkid, Long locationid, List<DBValueTracker<String, String>> values) {
        this.chunkId = chunkid;
        this.locationId = locationid;
        this._values = Collections.synchronizedList(new ArrayList<DBValueTracker<String, String>>(values));
        this.strategy = SaveStrategy.LAZY;
    }

    protected IdpBlockData(Long chunkid, Long locationid) {
        this.chunkId = chunkid;
        this.locationId = locationid;
        this._values = Collections.synchronizedList(new ArrayList<DBValueTracker<String, String>>(10));

        this.strategy = SaveStrategy.LAZY;
    }
    /** The locationid if this object is from the database. */
    protected final Long locationId;
    /** The chunkid if this object is from the database. */
    protected final Long chunkId;
    /** private instance of the values in the data object.
     * <b>Do not call this object directly!</b>
     */
    private final List<DBValueTracker<String, String>> _values;
    /** The stategy that is used to check how the values should be saved. */
    private SaveStrategy strategy;

    /**
     * This will construct a map with all key/value pairs in the object.
     * <p/>
     * Note: values that are deleted won't be copied.
     * @return the map or null if no values.
     */
    public Map<String, String> getValues() {
        if (_values.isEmpty()) {
            return null;
        }

        Map<String, String> contents = new HashMap<String, String>(_values.size());

        DBValueTracker<String, String> tracker;
        for (int i = 0; i < _values.size(); i++) {
            tracker = _values.get(i);
            if (tracker.getTrackerState() != TrackerState.DELETED) {
                contents.put(tracker.getKey(), tracker.getValue());
            }
        }

        return contents;
    }

    /**
     * This method will clear all values and replace them with the given map.
     * @param idpvalues
     */
    public void setValues(Map<String, String> idpvalues) {
        if (hasData()) {
            clear();
        }

        for (Map.Entry<String, String> entry : idpvalues.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="SaveStrategy">
    /**
     * Strategy to saveAllToDatabase the values inside this object
     */
    public enum SaveStrategy {

        /** With this strategy the changes of the values will be directly updated in the database. */
        EAGER,
        /** With this strategy the changes of the values wont be updated till the ::saveAllToDatabase() method is called.
         * <b>This is the default option!</b>
         * */
        LAZY
    }

    /**
     * Returns the strategy that is used to save the values.
     * @return
     */
    public SaveStrategy getSaveStrategy() {
        return strategy;
    }

    /**
     * Sets the save strategy of the object
     * @param strategy
     * @see SaveStrategy
     * <br/><b>Eager<b/>: - With this strategy the changes of the values will be directly updated in the database.
     * <br/><b>Lazy<b/>: - With this strategy the changes of the values wont be updated till the ::saveAllToDatabase() method is called.
     * <u>This is the default option!</u>
     */
    public void setSaveStrategy(SaveStrategy strategy) {
        this.strategy = strategy;
    }
    // </editor-fold>

}

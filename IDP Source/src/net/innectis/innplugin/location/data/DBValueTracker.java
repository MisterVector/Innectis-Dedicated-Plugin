package net.innectis.innplugin.location.data;

/**
 *
 * @author Hret
 *
 * Object that can be used to track values from an database.
 */
public class DBValueTracker<Tkey, TValue> {

    private final Tkey key;
    private TValue value;
    private TrackerState state;

    /**
     * Makes a new tracker with the key and value.
     * This sets teh state to NEW
     * @param key
     * @param value
     */
    public DBValueTracker(Tkey key, TValue value) {
        this.key = key;
        this.value = value;
        this.state = TrackerState.NEW;
    }

    /**
     * Makes a new tracker with a starting state
     * @param key
     * @param value
     * @param state
     */
    public DBValueTracker(Tkey key, TValue value, TrackerState state) {
        this.key = key;
        this.value = value;
        this.state = state;
    }

    /**
     * @return the key
     */
    public Tkey getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public TValue getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(TValue value) {
        // Check if changed
        if ((this.value == null && value != null) || this.value == null || !this.value.equals(value)) {
            this.value = value;

            // Only set if not new
            if (getTrackerState() != TrackerState.NEW) {
                setTrackerState(TrackerState.CHANGED);
            }
        }
    }

    /**
     *
     * The state of the tracker
     * @return the state
     */
    public TrackerState getTrackerState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setTrackerState(TrackerState state) {
        // The NEW tracker state is not allowed to be set!
        if (state != TrackerState.NEW) {
            this.state = state;
        }
    }

    /**
     * The state of the tracker.
     */
    public enum TrackerState {

        /** The tracker's value is deleted */
        DELETED,
        /** The tracker's value didn't change. */
        UNCHANGED,
        /** The tracker is new. */
        NEW,
        /** The value in the tracker has changed. */
        CHANGED
    }

}

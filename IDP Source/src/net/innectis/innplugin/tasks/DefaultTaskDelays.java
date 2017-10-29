package net.innectis.innplugin.tasks;

/**
 * @author Hret
 *
 * Delays for the different types of tasks
 */
public enum DefaultTaskDelays {

    // @todo Have a working use for this (hooked up and saves, but no use right now)
    /** 1 min */
    ChannelActivitySaveTask(60 * 1000),

    /** 5 min */
    LotReminder(5 * 60 * 1000),
    /** 10 min */
    AsyncMaintenance(10 * 60 * 1000),
    /** 5 min */
    SyncMaintenance(5 * 60 * 1000),
    /** 2 min */
    PlayerSave(2 * 60 * 1000),
    /** 7 min */
    Message(7 * 60 * 1000),
    /** 30 sec */
    PlayerInfo(30 * 1000),
    /** 30 sec */
    LotFlags(30 * 1000),
    /** 20 sec */
    MapCleanup(20 * 1000),
    /** 3 min */
    SessionCleanup(3 * 60 * 1000),
    /** 5 min */
    PVPCleanup(5 * 60 * 1000),
    /** 1 hour */
    QuotaCleanup(60 * 60 * 1000),
    /** 2 min */
    DynmapUpdate(2 * 60 * 1000),
    /** 1 sec */
    RegenTimeout(1000);
    /** The delay */
    private final long delay;

    private DefaultTaskDelays(long delay) {
        this.delay = delay;
    }

    /** Returns the delay */
    public long getDelay() {
        return delay;
    }

}

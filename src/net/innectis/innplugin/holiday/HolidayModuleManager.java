package net.innectis.innplugin.holiday;

import java.util.Calendar;
import java.util.TimeZone;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.idp.InnCreatureSpawnEvent;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * Manager to handle special holiday modules and automaticly put through the events to them.
 */
public final class HolidayModuleManager implements HolidayModuleSelector {

    /** The time in which the holiday module must be checked again. (1 hour) */
    private static final long HOLIDAY_MODULE_CHECK = 3600 * 1000;
    /** The module that is currently active */
    private volatile HolidayModule activemodule;

    /**
     * Create a new holiday module manager
     * @param plugin
     */
    public HolidayModuleManager(InnPlugin plugin) {
        activemodule = getModule(HolidayType.NULL);
        lookupModule();
        plugin.getTaskManager().addTask(new HolidayTask(this, HOLIDAY_MODULE_CHECK));
    }

    /**
     * The type of holiday that is currently active.
     * @return The type of holiday
     */
    public HolidayType getCurrentHoliday() {
        return activemodule.getType();
    }

    /**
     * This method will lookup a new module if none is selected.
     */
    @Override
    public final void lookupModule() {
        if (!activemodule.isValid()) {
            checkForNewModule();
        }
    }

    /**
     * Handle the player join event.
     * @param player
     */
    public void onPlayerJoin(IdpPlayer player) {
        if (activemodule.isValid()) {
            activemodule.onPlayerJoin(player);
        }
    }

    /**
     * This method will check for a new module and load it if its different from the current module.
     */
    private void checkForNewModule() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        // Set default type;
        HolidayType neededHoliday = HolidayType.NULL;
        for (HolidayType type : HolidayType.values()) {
            if (cal.after(type.getStartDate()) && cal.before(type.getEndDate())) {
                neededHoliday = type;
                break;
            }
        }

        // Check if new type
        if (!neededHoliday.equals(activemodule.getType())) {
            // Set new module
            activemodule = getModule(neededHoliday);
        }
    }

    /**
     * Factory method that creates the right module with the given holiday
     * @param neededHoliday
     * @return
     */
    private static HolidayModule getModule(HolidayType neededHoliday) {
        switch (neededHoliday) {
            case CHRISTMAS:
                return new ChristmasModule();
            case APRIL_FOOLS:
                return new AprilFoolsModule();
            default:
                return new NullModule();
        }
    }

    @Deprecated
    public void onCreatureSpawn(InnCreatureSpawnEvent event) {
        if (activemodule.isValid()) {
            activemodule.onCreatureSpawn(event);
        }
    }

}
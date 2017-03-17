package net.innectis.innplugin.holiday;

import java.util.Calendar;
import java.util.TimeZone;
import net.innectis.innplugin.listeners.idp.InnCreatureSpawnEvent;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * @author Hret
 *
 * Interface for special modules during specified holidays.
 */
abstract class HolidayModule {

    private final HolidayType type;

    HolidayModule(HolidayType type) {
        this.type = type;
    }

    /**
     * The type of holiday
     * @return
     */
    public final HolidayType getType() {
        return type;
    }

    /**
     * Checks if the holiday is still valid.
     * @return
     */
    public boolean isValid() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return (cal.after(type.getStartDate()) && cal.before(type.getEndDate()));
    }

    /**
     * Event that gets triggerd when a player joins the server
     * @param player
     */
    public void onPlayerJoin(IdpPlayer player) {
    }

    public void onCreatureSpawn(InnCreatureSpawnEvent event) {
    }

}

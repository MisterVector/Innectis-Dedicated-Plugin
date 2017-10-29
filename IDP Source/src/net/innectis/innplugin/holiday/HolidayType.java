package net.innectis.innplugin.holiday;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Hret
 *
 * Enum containing the different types of holidays the IDP supports.
 * Furthermore the dates that the holidays are activated are also controlled in here.
 */
public enum HolidayType {

    CHRISTMAS("Christmas", Calendar.DECEMBER, 25, Calendar.DECEMBER, 26),
    APRIL_FOOLS("April Fools", Calendar.APRIL, 1, Calendar.APRIL, 2),
    NULL("None", Calendar.JANUARY, 1, Calendar.DECEMBER, 31);
    //
    private final String name;
    private final int start_month;
    private final int start_day;
    private final int end_month;
    private final int end_day;

    private HolidayType(String name, int start_month, int start_day, int end_month, int end_day) {
        this.name = name;
        this.start_month = start_month;
        this.start_day = start_day;
        this.end_month = end_month;
        this.end_day = end_day;
    }

    /**
     * The name of the holiday
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * The month the holiday starts
     * @return the start_month
     */
    protected int getStart_month() {
        return start_month;
    }

    /**
     * The day (of month) the holiday starts
     * @return the start_day
     */
    protected int getStart_day() {
        return start_day;
    }

    /**
     * The month the holiday ends
     * @return the end_month
     */
    protected int getEnd_month() {
        return end_month;
    }

    /**
     * The day (of month) the holiday ends
     * @return the end_day
     */
    protected int getEnd_day() {
        return end_day;
    }

    /**
     * The date this holiday starts in the IDP.
     * @return
     */
    public Calendar getStartDate() {
        Calendar cal = new GregorianCalendar(getGMT().get(Calendar.YEAR), start_month, start_day, 0, 0, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.MINUTE, -1);
        return cal;
    }

    /**
     * The date this holiday ends in the IDP.
     * @return
     */
    public Calendar getEndDate() {
        Calendar cal = new GregorianCalendar(getGMT().get(Calendar.YEAR), end_month, end_day, 0, 0, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal;
    }

    private static Calendar getGMT() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }

}
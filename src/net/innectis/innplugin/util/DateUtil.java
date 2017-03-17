package net.innectis.innplugin.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.innectis.innplugin.player.chat.ChatColor;

/**
 *
 * @author Hret
 *
 * This static class contants utility methods for dates.
 * <p/>
 * Also, this class has some default date formats used in the IDP.
 *
 * <p/>
 * This class cannot be inherited or created as an object.x
 */
public final class DateUtil {

    /** GTM timezone */
    public static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");
    /** Formats to MMMM d, yyyy like January 29, 2012 */
    public static final String FORMAT_FULL_DATE = "MMMM d, yyyy";
    /** Formats to MMMM d, yyyy HH:mm' like Januaery 29, 2012 20:15 */
    public static final String FORMAT_FULL_DATE_TIME = "MMMM d, yyyy HH:mm";
    //
    /** Constant value of 31536000 (the amount of seconds in 1 year of 365 days). */
    private static final int YEAR_IN_SECONDS = 31536000;
    /** Constant value of 2628000 (the amount of seconds in 1 month of 30.416667 days). */
    private static final int MONTH_IN_SECONDS = 2628000;
    /** Constant value of 86400 (the amount of seconds in 1 day of 24 hours). */
    private static final int DAY_IN_SECONDS = 86400;
    /** Constant value of 3600 (the amount of seconds in 1 hour). */
    private static final int HOUR_IN_SECONDS = 3600;

    /** Default series of constants to use in getTimeDifferenceString() */
    public static final TimeConstants[] DEFAULT_CONSTANTS = new TimeConstants[] {TimeConstants.DAY, TimeConstants.HOUR, TimeConstants.MINUTE, TimeConstants.SECOND};

    private DateUtil() {
    }

    /**
     * Formats the date to the given patternstring.<br/>
     * Some patterns are predefined in the DateUtil class as static variables.
     * @param date
     * @param pattern
     * @return The formatted datex
     * @see DateUtil
     */
    public static String formatString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Formats the date to the given patternstring. <br/>
     * Some patterns are predefined in the DateUtil class as static variables.
     * @param date
     * @param pattern
     * @param timezone
     * The given date will be formatted to the given timezone.
     * If null is given it will format to the GMT timezone
     * @return The formatted datex
     * @see DateUtil
     */
    public static String formatString(Date date, String pattern, TimeZone timezone) {
        if (date == null) {
            return null;
        } else if (timezone == null) {
            timezone = TIMEZONE_GMT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(timezone);
        return sdf.format(date);
    }

    /**
     * This method will convert the given amount of seconds into a string which displays the time.
     * <p/>
     * For instance if given the value of <b>3725</b> the method will return the following string: <br/>
     * <b>1 hour, 2 minutes</b>.
     *
     * <p/>
     * This method will ignore seconds and if the seconds is lower then 60 (less then 1 minute) the
     * value 'Less then a minute' will be given by default.
     *
     * @param seconds
     * The amount of seconds where the string has to be build upon.
     * @param constants
     * Contains a list of time constants to use with the resulting time string.
     * If only TimeConstants.HOUR and TimeConstants.MINUTE is used, then if you
     * pass in a value that is 24 days, 3 hours and 6 minutes, the result will
     * only be:
     * <b>3 hours, 6 minutes</b>
     *
     * @return
     * The given amount of seconds as string.
     */
    public static String getTimeDifferenceString(long seconds, TimeConstants... constants) {
        return getTimeDifferenceString(seconds, (constants != null ? Arrays.asList(constants) : new ArrayList<TimeConstants>()));
    }

    /**
     * Private method proxied from above converting an array parameter into a list
     */
    private static String getTimeDifferenceString(long seconds, List<TimeConstants> constants) {
        long years = 0, months = 0, days = 0, hours = 0, minutes = 0;
        if (seconds >= YEAR_IN_SECONDS && constants.contains(TimeConstants.YEAR)) {
            years = (long) Math.floor(seconds / YEAR_IN_SECONDS);
            seconds -= years * YEAR_IN_SECONDS;
        }
        if (seconds >= MONTH_IN_SECONDS && constants.contains(TimeConstants.MONTH)) {
            months = (long) Math.floor(seconds / MONTH_IN_SECONDS);
            seconds -= months * MONTH_IN_SECONDS;
        }
        if (seconds >= DAY_IN_SECONDS && constants.contains(TimeConstants.DAY)) {
            days = (long) Math.floor(seconds / DAY_IN_SECONDS);
            seconds -= days * DAY_IN_SECONDS;
        }
        if (seconds >= HOUR_IN_SECONDS && constants.contains(TimeConstants.HOUR)) {
            hours = (long) Math.floor(seconds / HOUR_IN_SECONDS);
            seconds -= hours * HOUR_IN_SECONDS;
        }
        if (seconds >= 60 && constants.contains(TimeConstants.MINUTE)) {
            minutes = (long) Math.floor(seconds / 60);
            seconds -= minutes * 60;
        }

        // Make sure if no time constant is supplied with more than 59 seconds
        // given to this method to skip this section
        if (days == 0 && hours == 0 && minutes == 0 && seconds < 60) {
            return "Less than a minute";
        }

        StringBuilder sb = new StringBuilder(200);
        if (years > 0) {
            sb.append(months).append(" year");
            if (years > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        if (months > 0) {
            sb.append(months).append(" month");
            if (months > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        if (days > 0) {
            sb.append(days).append(" day");
            if (days > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute");
            if (minutes > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second");
            if (seconds > 1) {
                sb.append("s");
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * This method will convert the given time formula string and convert it to miliseconds.
     * <p/>
     * This method will accept the following items: <br/>
     * <ul>
     * <li><b>d</b>: Days</li>
     * <li><b>h</b>: Hours</li>
     * <li><b>m</b>: Minutes</li>
     * <li><b>s</b>: Seconds</li>
     * </ul>
     * <p/>
     * If a value is passed that is not known or the formula is not correctly ended this method
     * will return <b>-1</b> to indicate that the value is incorrect.
     *
     * @param timeFormula
     * The time to parse into miliseconds
     *
     * @return
     * <b>-1</b> on error else the given time in milliseconds that is in the given formula.
     */
    public static long getTimeFormula(String timeFormula) {
        if (StringUtil.stringIsNullOrEmpty(timeFormula)) {
            return -1;
        }

        String timeString = "";
        boolean lastCharIsUnit = false;
        long formula = 0;

        char[] charray = timeFormula.toCharArray();

        for (char ch : charray) {
            // Ignore whitespace
            if (Character.isWhitespace(ch)) {
                continue;
            }

            // Check if number
            if (ch <= '9' && ch >= '0') {
                timeString += ch;
                lastCharIsUnit = false;

            } else {
                if (timeString.isEmpty()) {
                    return -1;
                }

                long timeValue = Long.parseLong(timeString);
                lastCharIsUnit = true;
                timeString = "";

                // Switch based on charvalue
                switch (Character.toLowerCase(ch)) {
                    case 'd':
                        formula += (timeValue * DAY_IN_SECONDS);
                        break;
                    case 'h':
                        formula += (timeValue * HOUR_IN_SECONDS);
                        break;
                    case 'm':
                        formula += (timeValue * 60);
                        break;
                    case 's':
                        formula += timeValue;
                        break;
                    default:
                        return -1;
                }
            }
        }

        // Check for an unfinished value
        if (!lastCharIsUnit) {
            return -1;
        }

        // Convert to milliseconds
        return formula * 1000;
    }

    /**
     * This method will convert the given amount of milliseconds into a string which displays the time.
     * <p/>
     * For instance if given the value of <b>3725</b> the method will return one of the following string: <br/>
     * <b>1 hour, 2 minutes, 5 seconds</b> <br/>
     * or (if the short version is selected) <br/>
     * <b>1h2m5s</b>.<br/>
     * <i>(Do note that the short version will color the numbers white and the rest gray)</i>
     * <p/>
     * This method will ignore seconds and if the seconds is lower then 60 (less then 1 minute) the
     * value 'Less then a minute' will be given by default.
     *
     * @param time
     * The amount of milliseconds where the string has to be build upon.
     * @param useLongVersion
     * Whether to display the long versiom or short version.
     * Long: 1 hour, 3 seconds, short: 1h3s
     *
     * @return
     * The given amount of seconds as string.
     */
    public static String getTimeString(long time, boolean useLongVersion) {
        // Set to seconds.
        time /= 1000;
        StringBuilder timeString = new StringBuilder(40);

        int days = (int) (time / (DAY_IN_SECONDS));
        if (days > 0) {
            if (useLongVersion) {
                timeString.append(days).append(" day").append(days > 1 ? "s" : "");
            } else {
                timeString.append("").append(ChatColor.WHITE).append(days).append(ChatColor.GRAY).append("d");
            }
        }

        int hours = (int) ((time / (HOUR_IN_SECONDS)) % 24);
        if (hours > 0) {
            if (useLongVersion) {
                if (timeString.length() != 0) {
                    timeString.append(", ");
                }
                timeString.append(hours).append(" hour").append(hours > 1 ? "s" : "");
            } else {
                timeString.append("").append(ChatColor.WHITE).append(hours).append(ChatColor.GRAY).append("h");
            }
        }

        int minutes = (int) ((time / 60) % 60);
        if (minutes > 0) {
            if (useLongVersion) {
                if (timeString.length() != 0) {
                    timeString.append(", ");
                }
                timeString.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
            } else {
                timeString.append("").append(ChatColor.WHITE).append(minutes).append(ChatColor.GRAY).append("m");
            }
        }

        int seconds = (int) time % 60;
        if (seconds > 0) {
            if (useLongVersion) {
                if (timeString.length() != 0) {
                    timeString.append(", ");
                }
                timeString.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
            } else {
                timeString.append("").append(ChatColor.WHITE).append(seconds).append(ChatColor.GRAY).append("s");
            }
        }

        return timeString.toString();
    }
    
}

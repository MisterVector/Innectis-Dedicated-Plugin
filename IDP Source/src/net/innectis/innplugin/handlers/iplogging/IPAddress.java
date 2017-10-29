package net.innectis.innplugin.handlers.iplogging;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Hret
 *
 * Object that handles data regarding an IP addres aswell as supplying functions to compare to others and such.
 */
public final class IPAddress {

    /** Pattern that is used to check if an IP is a valid IP. */
    private static Pattern checkPattern;

    static {
        try {
            checkPattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        } catch (PatternSyntaxException pse) {
            pse.printStackTrace();
        }
    }
    private final String ipaddress;
    private GeoLocation geolocation;

    /**
     * Makes a new IPAddress object.
     * @param ipaddress
     * @throws NullPointerException
     * This is thrown if the IPAddress that is given is not valid.
     */
    public IPAddress(String ipaddress) {
        if (!checkIp(ipaddress)) {
            throw new NullPointerException("Not a valid IP!");
        }
        this.ipaddress = ipaddress;
    }

    /**
     * Returns the geolocation of this object.<br/>
     * The first time this method is called it will load it into the memory.<br/>
     * Any requests after that will return the cached item.
     * @return
     */
    public GeoLocation getGeoLocation() {
        if (geolocation == null) {
            geolocation = GeoLocation.findGeoLocation(this);
        }
        return geolocation;
    }

    /**
     * Returns the int value of the IPaddress.
     * @return the same as the IPAddress::ipToInt() method.
     */
    public long longCode() {
        return ipToLong(ipaddress);
    }

    @Override
    public int hashCode() {
        return (int) ipToLong(ipaddress);
    }

    /**
     * Compares if the IP addresses are the same
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPAddress) {
            // Hashcode is the value of the IPaddress so this must work
            return obj.hashCode() == this.longCode();
        }
        // Check for an string input of an IP
        if (obj instanceof String) {
            // Only check the intvalue if its an valid IP
            return checkIp(obj.toString()) && ipToLong(obj.toString()) == this.longCode();
        }

        // Cannot check if its the same
        return false;
    }

    /**
     * Returns the IP address.
     * @return
     */
    @Override
    public String toString() {
        return ipaddress;
    }

    /**
     * Checks if the IP is a valid IP
     * @param input IP
     * @return True if the IP is valid
     */
    public static Boolean checkIp(String input) {
        return checkPattern.matcher(input).find();
    }

    /**
     * Converts an ip to a int value. <br/>
     * <b>Make sure the IP is in the right format "1.1.1.1"!</b>
     * @param ip
     * @return the long value of the IP
     */
    public static long ipToLong(String ip) {
        String[] split = ip.split("\\.");
        long w, x, y, z;
        w = Integer.parseInt(split[0]);
        x = Integer.parseInt(split[1]);
        y = Integer.parseInt(split[2]);
        z = Integer.parseInt(split[3]);

        return 16777216 * w + 65536 * x + 256 * y + z;
    }

    /**
     * Converts an long value to an IP address.
     * @param ipnum
     * @return the IP address.
     */
    public static String longtToIp(long ipnum) {
        long w, x, y, z;
        w = ((ipnum / 16777216) % 256);
        x = ((ipnum / 65536) % 256);
        y = ((ipnum / 256) % 256);
        z = ((ipnum) % 256);
        return w + "." + x + "." + y + "." + z;
    }

}

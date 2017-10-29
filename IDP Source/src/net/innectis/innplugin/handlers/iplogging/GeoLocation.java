package net.innectis.innplugin.handlers.iplogging;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.handlers.datasource.DBManager;

/**
 * @author Hret
 *
 * Class that has geolocation data of an IP Address.
 * The fields in this class should not be allowed to be adjusted.
 */
public final class GeoLocation {

    private static final double EARTH_DIAMETER = 3958.75;
    private static final double METER_CONVERTION = 1609.344;
    private int locid;
    private double longitude;
    private double latitude;
    private String countryCode;
    private String country;
    private String regionCode;
    private String postalCode;
    private String city;

    // Only allow private instances
    private GeoLocation() {
    }

    /**
     * The latitude of the location
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * The longitude of the location
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the countryCode on the given geolocations
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * The name of the city
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * The ISO 3166 country code.<br/>
     * <b>NL</b> - Netherlands<br/>
     * <b>US</n> - United States<br/>
     * <b>GB</b> - United Kingdom<br/>
     *
     * @return
     * @see http://userpage.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Postal code (only works in the US)
     * @return
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Region code. Dunno exactly what this is means..
     * @return
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Calculates the distanceFrom between points in <b>meters</b>.
     * @param loc to measure the distanceFrom with.
     * @return the distanceFrom in meters.
     */
    public double distanceFrom(GeoLocation loc) {
        double lat1 = latitude, lon1 = longitude;
        double lat2 = loc.latitude, lon2 = loc.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_DIAMETER * c;
        return dist * METER_CONVERTION;
    }

    @Override
    public String toString() {
        return locid + "," + countryCode + "," + regionCode + ","
                + postalCode + "," + city + "," + latitude + "," + longitude;
    }

    /**
     * Looks up the name of the countryCode that is on the given geolocation.
     * @param longitude
     * @param latitude
     */
    public static GeoLocation findGeoLocation(IPAddress address) {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT a.*, b.name FROM geolite_location as a LEFT JOIN geolite_country as b "
                    + " ON a.country = b.countrycode "
                    + " WHERE locId = (SELECT locId FROM geolite_blocks "
                    + " WHERE ? >= startIpNum AND ? <= endipnum LIMIT 1)");

            long longValue = address.longCode();
            statement.setLong(1, longValue);
            statement.setLong(2, longValue);

            result = statement.executeQuery();

            if (result.next()) {
                GeoLocation location = new GeoLocation();
                location.locid = result.getInt("locId");
                location.country = result.getString("name");
                location.countryCode = result.getString("country");
                location.regionCode = result.getString("region");
                location.city = result.getString("city");
                location.postalCode = result.getString("postalCode");
                location.latitude = result.getDouble("latitude");
                location.longitude = result.getDouble("longitude");
                return location;
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoLocation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

}

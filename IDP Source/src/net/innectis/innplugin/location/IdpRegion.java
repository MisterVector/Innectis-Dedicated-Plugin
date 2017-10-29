package net.innectis.innplugin.location;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * Handler class between a cuboid region and IdpLocations
 */
public class IdpRegion {

    private Vector pos1, pos2;

    /**
     * Construct a new instance of this cuboid region.
     *
     * @param pos1
     * @param pos2
     */
    public IdpRegion(Vector pos1, Vector pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public IdpRegion(Vector pos) {
        this(pos, pos);
    }

    public IdpRegion(IdpRegion region) {
        this(region.pos1, region.pos2);
    }

    public void setPos1(Vector location) {
        pos1 = location;
    }

    public void setPos2(Vector location) {
        pos2 = location;
    }

    public Vector getPos1() {
        return pos1;
    }

    public Vector getPos2() {
        return pos2;
    }

    public int getHighestX() {
        try {
            return Math.max(getPos1().getBlockX(), getPos2().getBlockX());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getLowestX() {
        try {
            return Math.min(getPos1().getBlockX(), getPos2().getBlockX());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getHighestY() {
        try {
            return Math.max(getPos1().getBlockY(), getPos2().getBlockY());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getLowestY() {
        try {
            return Math.min(getPos1().getBlockY(), getPos2().getBlockY());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getHighestZ() {
        try {
            return Math.max(getPos1().getBlockZ(), getPos2().getBlockZ());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getLowestZ() {
        try {
            return Math.min(getPos1().getBlockZ(), getPos2().getBlockZ());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public int getXLength() {
        return getHighestX() - getLowestX();
    }

    public int getYLength() {
        return getHighestY() - getLowestY();
    }

    public int getZLength() {
        return getHighestZ() - getLowestZ();
    }

    public int getCenterX() {
        return getLowestX() + (getXLength() / 2);
    }

    public int getCenterY() {
        return getLowestY() + (getYLength() / 2);
    }

    public int getCenterZ() {
        return getLowestZ() + (getZLength() / 2);
    }

    public boolean intersects(IdpRegion region, int ignoreMarginDistance) {
        Vector p1 = new Vector(region.getHighestX() - ignoreMarginDistance, region.getHighestY() - ignoreMarginDistance, region.getHighestZ() - ignoreMarginDistance);
        Vector p2 = new Vector(region.getLowestX() + ignoreMarginDistance, region.getHighestY() - ignoreMarginDistance, region.getHighestZ() - ignoreMarginDistance);
        Vector p3 = new Vector(region.getHighestX() - ignoreMarginDistance, region.getHighestY() - ignoreMarginDistance, region.getLowestZ() + ignoreMarginDistance);
        Vector p4 = new Vector(region.getLowestX() + ignoreMarginDistance, region.getHighestY() - ignoreMarginDistance, region.getLowestZ() + ignoreMarginDistance);
        Vector p5 = new Vector(region.getHighestX() - ignoreMarginDistance, region.getLowestY() - ignoreMarginDistance, region.getHighestZ() - ignoreMarginDistance);
        Vector p6 = new Vector(region.getLowestX() + ignoreMarginDistance, region.getLowestY() - ignoreMarginDistance, region.getHighestZ() - ignoreMarginDistance);
        Vector p7 = new Vector(region.getHighestX() - ignoreMarginDistance, region.getLowestY() - ignoreMarginDistance, region.getLowestZ() + ignoreMarginDistance);
        Vector p8 = new Vector(region.getLowestX() + ignoreMarginDistance, region.getLowestY() - ignoreMarginDistance, region.getLowestZ() + ignoreMarginDistance);

        if (contains(p1, 0) || contains(p2, 0) || contains(p3, 0) || contains(p4, 0)
                || contains(p5, 0) || contains(p6, 0) || contains(p7, 0) || contains(p8, 0)) {
            return true;
        }

        p1 = new Vector(getHighestX(), getHighestY(), getHighestZ());
        p2 = new Vector(getLowestX(), getHighestY(), getHighestZ());
        p3 = new Vector(getHighestX(), getHighestY(), getLowestZ());
        p4 = new Vector(getLowestX(), getHighestY(), getLowestZ());
        p5 = new Vector(getHighestX(), getLowestY(), getHighestZ());
        p6 = new Vector(getLowestX(), getLowestY(), getHighestZ());
        p7 = new Vector(getHighestX(), getLowestY(), getLowestZ());
        p8 = new Vector(getLowestX(), getLowestY(), getLowestZ());

        return (region.contains(p1, ignoreMarginDistance) || region.contains(p2, ignoreMarginDistance) || region.contains(p3, ignoreMarginDistance) || region.contains(p4, ignoreMarginDistance)
                || region.contains(p5, ignoreMarginDistance) || region.contains(p6, ignoreMarginDistance) || region.contains(p7, ignoreMarginDistance) || region.contains(p8, ignoreMarginDistance));
    }

    /**
     * Returns true if this region contains the specified region
     * @param region
     */
    public boolean contains(IdpRegion region) {
        if (region.getLowestX() >= getLowestX() && region.getHighestX() <= getHighestX()
                && region.getLowestY() >= getLowestY() && region.getHighestY() <= getHighestY()
                && region.getLowestZ() >= getLowestZ() && region.getHighestZ() <= getHighestZ()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true based on whether the region contains the point,
     * excluding the x-yaxis.
     * @param pt
     */
    public boolean containsExcludeY(Location pt) {
        return pt.getX() >= getLowestX() && pt.getX() < (getHighestX() + 1)
                && pt.getZ() >= getLowestZ() && pt.getZ() < (getHighestZ() + 1);
    }

    /**
     * Returns true based on whether the region contains the point,
     * excluding the x-yaxis.
     * @param pt
     */
    public boolean containsExcludeY(Vector pt) {
        return pt.getX() >= getLowestX() && pt.getX() < (getHighestX() + 1)
                && pt.getZ() >= getLowestZ() && pt.getZ() < (getHighestZ() + 1);
    }

    /**
     * Returns true based on whether the region contains the point,
     * @param pt
     */
    public boolean contains(Vector pt) {
        return contains(pt, 0);
    }

    /**
     * This checks if the given point is inside the region within the given margin.
     * <p/>
     * The point is checked with floored values, that means that point 1.5 is still within
     * the region [0,0] - [1,1].
     *
     * @param pt
     * The point to check
     * @param ignoreMarginDistance
     * The distance from the lot that must be ignored.
     * @return true if the point is inside the region
     */
    public boolean contains(Vector pt, int ignoreMarginDistance) {
        return pt.getX() >= getLowestX() + ignoreMarginDistance && pt.getX() < (getHighestX() + 1) - ignoreMarginDistance
                && pt.getY() >= getLowestY() + ignoreMarginDistance && pt.getY() < (getHighestY() + 1) - ignoreMarginDistance
                && pt.getZ() >= getLowestZ() + ignoreMarginDistance && pt.getZ() < (getHighestZ() + 1) - ignoreMarginDistance;
    }

    /**
     * Returns true based on whether the region contains the point,
     * @param pt
     */
    public boolean contains(Location pt) {
        return contains(pt, 0);
    }

    /**
     * This checks if the given point is inside the region within the given margin.
     * <p/>
     * The point is checked with floored values, that means that point 1.5 is still within
     * the region [0,0] - [1,1].
     *
     * @param pt
     * The point to check
     * @param ignoreMarginDistance
     * The distance from the lot that must be ignored.
     * @return true if the point is inside the region
     */
    public boolean contains(Location pt, int ignoreMarginDistance) {
        return pt.getX() >= getLowestX() + ignoreMarginDistance && pt.getX() < (getHighestX() + 1) - ignoreMarginDistance
                && pt.getY() >= getLowestY() + ignoreMarginDistance && pt.getY() < (getHighestY() + 1) - ignoreMarginDistance
                && pt.getZ() >= getLowestZ() + ignoreMarginDistance && pt.getZ() < (getHighestZ() + 1) - ignoreMarginDistance;
    }

    /**
     * Checks if the area is to large to the max allowed blocks 0 means unlimited.
     * @param maxBlocks
     * @return
     */
    public boolean isAreaToLarge(int maxBlocks, boolean ignoreAirBlocks, int ignoredAirBlockCount) {
        if (maxBlocks == 0) {
            return true;
        }
        return (ignoreAirBlocks ? ignoredAirBlockCount : getArea()) > maxBlocks;
    }

    /**
     * Makes a string of the location
     * @return
     */
    public String getPos1String() {
        return "(" + getPos1().getBlockX() + ", " + getPos1().getBlockY() + ", " + getPos1().getBlockZ() + ") ";
    }

    /**
     * Makes a string of the location
     * @return
     */
    public String getPos2String() {
        return "(" + getPos2().getBlockX() + ", " + getPos2().getBlockY() + ", " + getPos2().getBlockZ() + ") ";
    }

    /**
     * Makes a String based off coords
     * @return
     */
    public final String getHash() {
        try {
            String plaintext = getPos1String() + getPos2String();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            return hashtext.substring(0, 5).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(IdpRegion.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
    }

    /**
     * Makes a password based off coords and the current date
     * @return
     */
    public final String getPassword() {
        try {
            String plaintext = getPos1String() + getPos2String() + DateUtil.formatString(new Date(), "ddMMyyyy");
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            return hashtext.substring(0, 5).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(IdpRegion.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
    }

    /**
     * The lowest point in the region
     * @return lowest point
     */
    public Vector getMinimumPoint() {
        return new Vector(getLowestX(), getLowestY(), getLowestZ());
    }

    /**
     * The highest point in the region
     * @return highest point
     */
    public Vector getMaximumPoint() {
        return new Vector(getHighestX(), getHighestY(), getHighestZ());
    }

    /**
     * Get the number of blocks in the region.
     *
     * @return number of blocks
     */
    public int getArea() {
        return (getHighestX() - getLowestX() + 1)
                * (getHighestY() - getLowestY() + 1)
                * (getHighestZ() - getLowestZ() + 1);
    }

    /**
     * Get X-size.
     *
     * @return width
     */
    public int getWidth() {
        return getHighestX() - getLowestX() + 1;
    }

    /**
     * Get Y-size.
     *
     * @return height
     */
    public int getHeight() {
        return getHighestY() - getLowestY() + 1;
    }

    /**
     * Get Z-size.
     *
     * @return length
     */
    public int getLength() {
        return getHighestZ() - getLowestZ() + 1;
    }

    /**
     * Expands the cuboid in a direction.
     *
     * @param change
     */
    public void expand(Vector change) {
        if (change.getX() > 0) {
            if (Math.max(pos1.getX(), pos2.getX()) == pos1.getX()) {
                pos1 = pos1.add(new Vector(change.getX(), 0, 0));
            } else {
                pos2 = pos2.add(new Vector(change.getX(), 0, 0));
            }
        } else {
            if (Math.min(pos1.getX(), pos2.getX()) == pos1.getX()) {
                pos1 = pos1.add(new Vector(change.getX(), 0, 0));
            } else {
                pos2 = pos2.add(new Vector(change.getX(), 0, 0));
            }
        }

        if (change.getY() > 0) {
            if (Math.max(pos1.getY(), pos2.getY()) == pos1.getY()) {
                pos1 = pos1.add(new Vector(0, change.getY(), 0));
            } else {
                pos2 = pos2.add(new Vector(0, change.getY(), 0));
            }
        } else {
            if (Math.min(pos1.getY(), pos2.getY()) == pos1.getY()) {
                pos1 = pos1.add(new Vector(0, change.getY(), 0));
            } else {
                pos2 = pos2.add(new Vector(0, change.getY(), 0));
            }
        }

        if (change.getZ() > 0) {
            if (Math.max(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                pos1 = pos1.add(new Vector(0, 0, change.getZ()));
            } else {
                pos2 = pos2.add(new Vector(0, 0, change.getZ()));
            }
        } else {
            if (Math.min(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                pos1 = pos1.add(new Vector(0, 0, change.getZ()));
            } else {
                pos2 = pos2.add(new Vector(0, 0, change.getZ()));
            }
        }

        recalculate();
    }

    /**
     * Contracts the cuboid in a direction.
     *
     * @param change
     */
    public void contract(Vector change) {
        if (change.getX() < 0) {
            if (Math.max(pos1.getX(), pos2.getX()) == pos1.getX()) {
                pos1 = pos1.add(new Vector(change.getX(), 0, 0));
            } else {
                pos2 = pos2.add(new Vector(change.getX(), 0, 0));
            }
        } else {
            if (Math.min(pos1.getX(), pos2.getX()) == pos1.getX()) {
                pos1 = pos1.add(new Vector(change.getX(), 0, 0));
            } else {
                pos2 = pos2.add(new Vector(change.getX(), 0, 0));
            }
        }

        if (change.getY() < 0) {
            if (Math.max(pos1.getY(), pos2.getY()) == pos1.getY()) {
                pos1 = pos1.add(new Vector(0, change.getY(), 0));
            } else {
                pos2 = pos2.add(new Vector(0, change.getY(), 0));
            }
        } else {
            if (Math.min(pos1.getY(), pos2.getY()) == pos1.getY()) {
                pos1 = pos1.add(new Vector(0, change.getY(), 0));
            } else {
                pos2 = pos2.add(new Vector(0, change.getY(), 0));
            }
        }

        if (change.getZ() < 0) {
            if (Math.max(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                pos1 = pos1.add(new Vector(0, 0, change.getZ()));
            } else {
                pos2 = pos2.add(new Vector(0, 0, change.getZ()));
            }
        } else {
            if (Math.min(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                pos1 = pos1.add(new Vector(0, 0, change.getZ()));
            } else {
                pos2 = pos2.add(new Vector(0, 0, change.getZ()));
            }
        }

        recalculate();
    }

    protected void recalculate() {
        pos1 = LocationUtil.clampY(pos1, 0, 255);
        pos2 = LocationUtil.clampY(pos2, 0, 255);
    }

    /**
     * Indicates if this region is Y-axis or not
     *
     * @return
     */
    public boolean isYaxis() {
        return (getLowestY() > 0 || getHighestY() < 256);
    }

    /**
     * A set of locations of the chunks that exist in this region.
     * @return
     */
    public Set<IdpVector2D> getChunks() {
        Set<IdpVector2D> chunks = new HashSet<IdpVector2D>();

        Vector min = getMinimumPoint();
        Vector max = getMaximumPoint();

        for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
            //for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                int chunkX = NumberConversions.floor(x / 16.0);
                int chunkZ = NumberConversions.floor(z / 16.0);

                chunks.add(new IdpVector2D(chunkX, chunkZ));
            }
            //}
        }

        return chunks;
    }

    @Override
    public String toString() {
        return getWidth() + "x" + getHeight() + "x" + getLength() + " (area: " + getArea() + ")";
    }

}

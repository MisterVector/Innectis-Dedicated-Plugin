package net.innectis.innplugin.location;

import org.bukkit.util.Vector;

/**
 *
 * @author sk89q
 * Adjusted for the IDP
 *
 * This is a vector for a 2D object such as chunks
 */
public class IdpVector2D {

    protected final double x, z;

    /**
     * Construct the Vector2D object.
     *
     * @param x
     * @param z
     */
    public IdpVector2D(double x, double z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Construct the Vector2D object.
     *
     * @param x
     * @param z
     */
    public IdpVector2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Construct the Vector2D object.
     *
     * @param x
     * @param z
     */
    public IdpVector2D(float x, float z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Construct the Vector2D object.
     *
     * @param pt
     */
    public IdpVector2D(IdpVector2D pt) {
        this.x = pt.x;
        this.z = pt.z;
    }

    /**
     * Construct the Vector2D object.
     */
    public IdpVector2D() {
        this.x = 0;
        this.z = 0;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the x
     */
    public int getBlockX() {
        return (int) Math.round(x);
    }

    /**
     * Set X.
     *
     * @param x
     * @return new vector
     */
    public IdpVector2D setX(double x) {
        return new IdpVector2D(x, z);
    }

    /**
     * Set X.
     *
     * @param x
     * @return new vector
     */
    public IdpVector2D setX(int x) {
        return new IdpVector2D(x, z);
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * @return the z
     */
    public int getBlockZ() {
        return (int) Math.round(z);
    }

    /**
     * Set Z.
     *
     * @param z
     * @return new vector
     */
    public IdpVector2D setZ(double z) {
        return new IdpVector2D(x, z);
    }

    /**
     * Set Z.
     *
     * @param z
     * @return new vector
     */
    public IdpVector2D setZ(int z) {
        return new IdpVector2D(x, z);
    }

    /**
     * Adds two points.
     *
     * @param other
     * @return New point
     */
    public IdpVector2D add(IdpVector2D other) {
        return new IdpVector2D(x + other.x, z + other.z);
    }

    /**
     * Adds two points.
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D add(double x, double z) {
        return new IdpVector2D(this.x + x, this.z + z);
    }

    /**
     * Adds two points.
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D add(int x, int z) {
        return new IdpVector2D(this.x + x, this.z + z);
    }

    /**
     * Adds points.
     *
     * @param others
     * @return New point
     */
    public IdpVector2D add(IdpVector2D... others) {
        double newX = x, newZ = z;

        for (int i = 0; i < others.length; ++i) {
            newX += others[i].x;
            newZ += others[i].z;
        }
        return new IdpVector2D(newX, newZ);
    }

    /**
     * Subtracts two points.
     *
     * @param other
     * @return New point
     */
    public IdpVector2D subtract(IdpVector2D other) {
        return new IdpVector2D(x - other.x, z - other.z);
    }

    /**
     * Subtract two points.
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D subtract(double x, double z) {
        return new IdpVector2D(this.x - x, this.z - z);
    }

    /**
     * Subtract two points.
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D subtract(int x, int z) {
        return new IdpVector2D(this.x - x, this.z - z);
    }

    /**
     * Subtract points.
     *
     * @param others
     * @return New point
     */
    public IdpVector2D subtract(IdpVector2D... others) {
        double newX = x, newZ = z;

        for (int i = 0; i < others.length; ++i) {
            newX -= others[i].x;
            newZ -= others[i].z;
        }
        return new IdpVector2D(newX, newZ);
    }

    /**
     * Component-wise multiplication
     *
     * @param other
     * @return New point
     */
    public IdpVector2D multiply(IdpVector2D other) {
        return new IdpVector2D(x * other.x, z * other.z);
    }

    /**
     * Component-wise multiplication
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D multiply(double x, double z) {
        return new IdpVector2D(this.x * x, this.z * z);
    }

    /**
     * Component-wise multiplication
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D multiply(int x, int z) {
        return new IdpVector2D(this.x * x, this.z * z);
    }

    /**
     * Component-wise multiplication
     *
     * @param others
     * @return New point
     */
    public IdpVector2D multiply(IdpVector2D... others) {
        double newX = x, newZ = z;

        for (int i = 0; i < others.length; ++i) {
            newX *= others[i].x;
            newZ *= others[i].z;
        }
        return new IdpVector2D(newX, newZ);
    }

    /**
     * Scalar multiplication.
     *
     * @param n
     * @return New point
     */
    public IdpVector2D multiply(double n) {
        return new IdpVector2D(this.x * n, this.z * n);
    }

    /**
     * Scalar multiplication.
     *
     * @param n
     * @return New point
     */
    public IdpVector2D multiply(float n) {
        return new IdpVector2D(this.x * n, this.z * n);
    }

    /**
     * Scalar multiplication.
     *
     * @param n
     * @return New point
     */
    public IdpVector2D multiply(int n) {
        return new IdpVector2D(this.x * n, this.z * n);
    }

    /**
     * Component-wise division
     *
     * @param other
     * @return New point
     */
    public IdpVector2D divide(IdpVector2D other) {
        return new IdpVector2D(x / other.x, z / other.z);
    }

    /**
     * Component-wise division
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D divide(double x, double z) {
        return new IdpVector2D(this.x / x, this.z / z);
    }

    /**
     * Component-wise division
     *
     * @param x
     * @param y
     * @param z
     * @return New point
     */
    public IdpVector2D divide(int x, int z) {
        return new IdpVector2D(this.x / x, this.z / z);
    }

    /**
     * Scalar division.
     *
     * @param n
     * @return new point
     */
    public IdpVector2D divide(int n) {
        return new IdpVector2D(x / n, z / n);
    }

    /**
     * Scalar division.
     *
     * @param n
     * @return new point
     */
    public IdpVector2D divide(double n) {
        return new IdpVector2D(x / n, z / n);
    }

    /**
     * Scalar division.
     *
     * @param n
     * @return new point
     */
    public IdpVector2D divide(float n) {
        return new IdpVector2D(x / n, z / n);
    }

    /**
     * Get the length of the vector.
     *
     * @return length
     */
    public double length() {
        return Math.sqrt(x * x + z * z);
    }

    /**
     * Get the length^2 of the vector.
     *
     * @return length^2
     */
    public double lengthSq() {
        return x * x + z * z;
    }

    /**
     * Get the distance away from a point.
     *
     * @param pt
     * @return distance
     */
    public double distance(IdpVector2D pt) {
        return Math.sqrt(Math.pow(pt.x - x, 2)
                + Math.pow(pt.z - z, 2));
    }

    /**
     * Get the distance away from a point, squared.
     *
     * @param pt
     * @return distance
     */
    public double distanceSq(IdpVector2D pt) {
        return Math.pow(pt.x - x, 2)
                + Math.pow(pt.z - z, 2);
    }

    /**
     * Get the normalized vector.
     *
     * @return vector
     */
    public IdpVector2D normalize() {
        return divide(length());
    }

    /**
     * Gets the dot product of this and another vector.
     *
     * @param other
     * @return the dot product of this and the other vector
     */
    public double dot(IdpVector2D other) {
        return x * other.x + z * other.z;
    }

    /**
     * Checks to see if a vector is contained with another.
     *
     * @param min
     * @param max
     * @return
     */
    public boolean containedWithin(IdpVector2D min, IdpVector2D max) {
        return x >= min.x && x <= max.x
                && z >= min.z && z <= max.z;
    }

    /**
     * Checks to see if a vector is contained with another.
     *
     * @param min
     * @param max
     * @return
     */
    public boolean containedWithinBlock(IdpVector2D min, IdpVector2D max) {
        return getBlockX() >= min.getBlockX() && getBlockX() <= max.getBlockX()
                && getBlockZ() >= min.getBlockZ() && getBlockZ() <= max.getBlockZ();
    }

    /**
     * Rounds all components down.
     *
     * @return
     */
    public IdpVector2D floor() {
        return new IdpVector2D(Math.floor(x), Math.floor(z));
    }

    /**
     * Rounds all components up.
     *
     * @return
     */
    public IdpVector2D ceil() {
        return new IdpVector2D(Math.ceil(x), Math.ceil(z));
    }

    /**
     * Rounds all components to the closest integer.<br>
     * <br>
     * Components < 0.5 are rounded down, otherwise up
     *
     *

     * @return
     */
    public IdpVector2D round() {
        return new IdpVector2D(Math.floor(x + 0.5), Math.floor(z + 0.5));
    }

    /**
     * 2D transformation.
     *
     * @param angle in degrees
     * @param aboutX about which x coordinate to rotate
     * @param aboutZ about which z coordinate to rotate
     * @param translateX what to add after rotation
     * @param translateZ what to add after rotation
     * @return
     */
    public IdpVector2D transform2D(double angle,
            double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = this.x - aboutX;
        double z = this.z - aboutZ;
        double x2 = x * Math.cos(angle) - z * Math.sin(angle);
        double z2 = x * Math.sin(angle) + z * Math.cos(angle);
        return new IdpVector2D(
                x2 + aboutX + translateX,
                z2 + aboutZ + translateZ);
    }

    public boolean isCollinearWith(IdpVector2D other) {
        if (x == 0 && z == 0) {
            // this is a zero vector
            return true;
        }

        final double otherX = other.x;
        final double otherZ = other.z;

        if (otherX == 0 && otherZ == 0) {
            // other is a zero vector
            return true;
        }

        if ((x == 0) != (otherX == 0)) {
            return false;
        }
        if ((z == 0) != (otherZ == 0)) {
            return false;
        }

        final double quotientX = otherX / x;
        if (!Double.isNaN(quotientX)) {
            return other.equals(multiply(quotientX));
        }

        final double quotientZ = otherZ / z;
        if (!Double.isNaN(quotientZ)) {
            return other.equals(multiply(quotientZ));
        }

        throw new RuntimeException("This should not happen");
    }

    /**
     * Checks if another object is equivalent.
     *
     * @param obj
     * @return whether the other object is equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdpVector2D)) {
            return false;
        }

        IdpVector2D other = (IdpVector2D) obj;
        return other.x == this.x && other.z == this.z;

    }

    /**
     * Gets the hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return ((new Double(x)).hashCode() >> 13)
                ^ (new Double(z)).hashCode();
    }

    /**
     * Returns string representation "(x, y, z)".
     *
     * @return string
     */
    @Override
    public String toString() {
        return "(" + x + ", " + z + ")";
    }

    /**
     * Creates a 3D vector by adding a zero Y component to this vector.
     *
     * @return Vector
     */
    public Vector toVector() {
        return new Vector(x, 0, z);
    }

    /**
     * Creates a 3D vector by adding the specified Y component to this vector.
     *
     * @return Vector
     */
    public Vector toVector(double y) {
        return new Vector(x, y, z);
    }

    /**
     * Gets the minimum components of two vectors.
     *
     * @param v1
     * @param v2
     * @return minimum
     */
    public static IdpVector2D getMinimum(IdpVector2D v1, IdpVector2D v2) {
        return new IdpVector2D(
                Math.min(v1.x, v2.x),
                Math.min(v1.z, v2.z));
    }

    /**
     * Gets the maximum components of two vectors.
     *
     * @param v1
     * @param v2
     * @return maximum
     */
    public static IdpVector2D getMaximum(IdpVector2D v1, IdpVector2D v2) {
        return new IdpVector2D(
                Math.max(v1.x, v2.x),
                Math.max(v1.z, v2.z));
    }
    
}

package net.innectis.innplugin.player.tinywe.blockcounters;

/**
 *
 * @author Hret
 *
 * Factory object to get blockcounter objects for the given CountType
 */
public final class BlockCounterFactory {

    private BlockCounterFactory() {
    }

    /**
     * This will make a blockcounter object for the given counttype.
     * If the type is not available then it will return null;
     * @param type
     * @return the counter or null;
     */
    public static BlockCounter getCounter(CountType type) {
        switch (type) {
            case CUBOID:
                return new CuboidBlockCounter();
            case SPHERE:
                return new SphereBlockCounter();
            case PYRAMID:
                return new PyramidBlockCounter();
            case XCYL:
                return new XCylinderBlockCounter();
            case YCYL:
                return new YCylinderBlockCounter();
            case ZCYL:
                return new ZCylinderBlockCounter();
        }

        return null;
    }

    /**
     * Enum for the types of counters there are
     */
    public enum CountType {

        /** Cuboid block counter */
        CUBOID,
        /** Sphere block counter */
        SPHERE,
        /** Pyramid block counter */
        PYRAMID,
        /** Cylinder block counter on X axis */
        XCYL,
        /** Cylinder block counter on Y axis */
        YCYL,
        /** Cylinder block counter on Z axis */
        ZCYL
    }

}

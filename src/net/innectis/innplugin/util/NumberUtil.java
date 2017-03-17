package net.innectis.innplugin.util;

/**
 * @author Hret
 *
 * This is an utility class for common actions with integers.
 * Methods in this class are tested and have been proven to work
 * <p /> *
 * <b>Do not edit the way it returns the value without checking where its used. <b/>
 *
 */
public final class NumberUtil {

    private NumberUtil() {
    }

    /**
     * This will calculate the total sum of all given arguments.
     * @param values
     * @return the total sum.
     */
    public static int countValues(int... values) {
        int returnvalue = 0;
        for (int i : values) {
            returnvalue += i;
        }
        return returnvalue;
    }
    
}

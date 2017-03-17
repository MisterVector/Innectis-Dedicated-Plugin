package net.innectis.innplugin.util.comparators;

import java.util.Comparator;

/**
 * A class used to compare strings together
 *
 * @author AlphaBlend
 */
public class StringSorter implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        return o1.toLowerCase().compareTo(o2.toLowerCase());
    }
    
}

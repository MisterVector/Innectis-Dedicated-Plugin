package net.innectis.innplugin.util.comparators;

import java.util.Comparator;
import net.innectis.innplugin.objects.FrequentLotWarp;

/**
 * A comparator that compares two frequently
 * warped to lots for order
 *
 * @author AlphaBlend
 */
public class FrequentLotWarpComparator implements Comparator<FrequentLotWarp> {

    @Override
    public int compare(FrequentLotWarp fw1, FrequentLotWarp fw2) {
        return fw2.getTimesWarpUsed() - fw1.getTimesWarpUsed();
    }
    
}

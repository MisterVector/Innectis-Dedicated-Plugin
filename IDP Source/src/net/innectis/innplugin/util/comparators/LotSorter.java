package net.innectis.innplugin.util.comparators;

import java.util.Comparator;
import net.innectis.innplugin.objects.owned.InnectisLot;

/**
 * A class that implements a comparator to
 * compare two lots for order
 *
 * @author AlphaBlend
 */
public class LotSorter implements Comparator<InnectisLot> {

    @Override
    public int compare(InnectisLot lot1, InnectisLot lot2) {
        return lot1.getLotName().compareTo(lot2.getLotName());
    }
    
}

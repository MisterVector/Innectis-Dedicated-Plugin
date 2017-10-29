package net.innectis.innplugin.objects;

/**
 *
 * @author AlphaBlend
 */
public class FrequentLotWarp {

    private String name = "";
    private int timesWarpUsed;

    public FrequentLotWarp(String name, int timesWarpUsed) {
        this.name = name;
        this.timesWarpUsed = timesWarpUsed;
    }

    public String getName() {
        return name;
    }

    public int getTimesWarpUsed() {
        return timesWarpUsed;
    }

}

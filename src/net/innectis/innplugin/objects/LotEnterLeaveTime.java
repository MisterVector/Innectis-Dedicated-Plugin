package net.innectis.innplugin.objects;

import net.innectis.innplugin.objects.owned.InnectisLot;

/**
 *
 *
 * @author AlphaBlend
 */
public class LotEnterLeaveTime {

    private InnectisLot lot = null;
    private long enterTime = 0;
    private long leaveTime = 0;

    public LotEnterLeaveTime() {

    }

    /**
     * Gets the lot the enter time and leave time is for
     * @return
     */
    public InnectisLot getLot() {
        return lot;
    }

    /**
     * Sets the lot the enter time and leave time is for. Both the
     * enter time and leave time will be reset
     * @param lot
     */
    public void setLot(InnectisLot lot) {
        this.lot = lot;
        enterTime = 0;
        leaveTime = 0;
    }

    /**
     * Gets the enter time for the lot
     * @return
     */
    public long getEnterTime() {
        return enterTime;
    }

    /**
     * Sets the enter time for the lot
     * @param enterTime
     */
    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    /**
     * Gets the leave time for the lot
     * @return
     */
    public long getLeaveTime() {
        return leaveTime;
    }

    /**
     * Sets the leave time for the lot
     * @param leaveTime
     */
    public void setLeaveTime(long leaveTime) {
        this.leaveTime = leaveTime;
    }

}

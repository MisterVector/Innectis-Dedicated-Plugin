package net.innectis.innplugin.objects;

import net.innectis.innplugin.objects.owned.InnectisLot;

/**
 *
 * @author Lynxy
 */
public class LotFlagToggle {

    private InnectisLot lot;
    private long flag;
    private boolean disable;

    public LotFlagToggle(InnectisLot lot, long flag, boolean disable) {
        this.flag = flag;
        this.lot = lot;
        this.disable = disable;
    }

    public InnectisLot getLot() {
        return this.lot;
    }

    public void setLot(InnectisLot lot) {
        this.lot = lot;
    }

    public long getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean getDisable() {
        return this.disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

}

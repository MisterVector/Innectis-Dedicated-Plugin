package net.innectis.innplugin.listeners;

/**
 *
 * @author Hret
 *
 * The priorities for events in the secondairy event listener.
 */
public enum InnEventPriority {

    /** This will be called last * */
    LOW(1),
    /** This will be called before LOW * */
    NORMAL(2),
    /** This will be called before NORMAL * */
    HIGH(3),
    /** This will be called before HIGH and is the first thing called * */
    INTERMEDIATE(4),
    /** This will not be called at all * */
    NONE(0);
    private final int orderid;

    private InnEventPriority(int orderid) {
        this.orderid = orderid;
    }

    /**
     * An int value that shows the ordering.
     * A higher number means a higher priority.
     * @return
     */
    public int getOrderid() {
        return orderid;
    }

}

package net.innectis.innplugin.system.shop;

/**
 * An enum describing the result of a chest shop transaction
 *
 * @author AlphaBlend
 */
public enum PurchaseResult {

    CUSTOMER_FUNDS_INSUFFICIENT("You do not have enough vT for this purchase."),
    OWNER_FUNDS_INSUFFICIENT("The owner of this chest does not have enough vT for this sale."),
    CUSTOMER_NOT_ENOUGH_ITEMS("You do not have enough items to satisfy this buying chest."),
    CUSTOMER_TOO_FULL("You have too many items to accept this amount."),
    OWNER_NOT_ENOUGH_ITEMS("The chest does not have enough items for sale."),
    OWNER_TOO_FULL("This chest has too many items to accept any more."),
    SUCCESSFUL_PURCHASE("{0} {1} items of {2} for {3} vT.");

    private String resultString;

    private PurchaseResult(String resultString) {
        this.resultString = resultString;
    }

    /**
     * Gets the text to display with this purchase result
     * @return
     */
    public String getResultString() {
        return resultString;
    }
    
}

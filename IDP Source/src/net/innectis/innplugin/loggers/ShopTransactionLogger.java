package net.innectis.innplugin.loggers;

import net.innectis.innplugin.items.IdpMaterial;

/**
 *
 * @author AlphaBlend
 *
 * Logs a transaction made from the shop
 */
public class ShopTransactionLogger extends IdpFileLogger implements Logger {

    ShopTransactionLogger(String logfolder) {
        super(logfolder, "shopTransactionLog", "yyyy-MM-dd HH:mm:ss");
    }

    public void logShopTransaction(String player, boolean bought, IdpMaterial mat, int amount, double cost) {
        super.log(player + (bought ? " bought " : " sold ") + amount + " of " + mat.getName().toLowerCase() + " for " + cost + " vT.");
    }

    public void logChestShopTransaction(String player, String seller, boolean bought, IdpMaterial mat, int amount, double cost) {
        super.log(player + " " + (bought ? "bought" : "sold") + " " + amount + " items of " + mat.getName() + " for " + cost + " vT from " + seller);
    }

}

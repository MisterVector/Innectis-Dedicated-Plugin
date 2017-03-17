package net.innectis.innplugin.system.shop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;

/**
 * A manager that handles chest shop lists
 *
 * @author AlphaBlend
 */
public class ChestShopLotManager {

    private static Map<Integer, ChestShopLotDetails> chestShopLots = new HashMap<Integer, ChestShopLotDetails>();

    /**
     * Gets an unmodifiable list of all chest shop lots
     * @return
     */
    public static List<ChestShopLotDetails> getChestShopLots() {
        List<ChestShopLotDetails> tempList = new ArrayList<ChestShopLotDetails>();

        for (ChestShopLotDetails details : Collections.unmodifiableCollection(chestShopLots.values())) {
            tempList.add(details);
        }

        return tempList;
    }

    /**
     * Adds a new chest shop listing to the list
     * @param chestShopLot
     */
    public static void addChestShopLot(ChestShopLotDetails chestShopLot) {
        chestShopLot.save();
        chestShopLots.put(chestShopLot.getId(), chestShopLot);
    }

    /**
     * Removes the chest shop listing
     * @param chestShopLot
     */
    public static void deleteChestShopLot(ChestShopLotDetails chestShopLot) {
        chestShopLot.delete();
        chestShopLots.remove(chestShopLot.getId());
    }

    /**
     * Gets a chest shop lot by the specified lot, if applicable
     * @param lotid
     * @return
     */
    public static ChestShopLotDetails getChestShopLot(int lotid) {
        for (ChestShopLotDetails details : chestShopLots.values()) {
            if (details.getLot().getId() == lotid) {
                return details;
            }
        }

        return null;
    }

    /**
     * Loads all chest shop lots from the database
     */
    public static void loadChestShopLots() {
        InnPlugin.logInfo("Loading all chest shop listings...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM chest_shop_list;");
            set = statement.executeQuery();

            while (set.next()) {
                int id = set.getInt("id");
                InnectisLot lot = LotHandler.getLot(set.getInt("lotid"));
                String name = set.getString("name");

                chestShopLots.put(id, new ChestShopLotDetails(id, lot, name));
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load chest shop lots!", ex);
        }
    }

}

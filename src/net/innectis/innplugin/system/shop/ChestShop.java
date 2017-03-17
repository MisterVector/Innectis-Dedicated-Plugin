package net.innectis.innplugin.system.shop;

import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.loggers.ShopTransactionLogger;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.system.signs.ChestShopSign;
import net.innectis.innplugin.system.signs.ChestShopSign.ChestShopType;
import org.bukkit.block.Chest;

/**
 * A class that handles a chest shop and transactions
 * of an item in that chest shop
 *
 * @author AlphaBlend
 */
public final class ChestShop {

    private IdpPlayer customer;
    private InnectisChest innChest;
    private ChestShopSign chestSign;
    private PurchaseResult result;

    public ChestShop(IdpPlayer customer, InnectisChest innChest, ChestShopSign chestSign) {
        this.customer = customer;
        this.innChest = innChest;
        this.chestSign = chestSign;
    }

    public void processTransaction() {
        Chest bukkitChest = innChest.getChest1Chest();
        boolean buyShop = (chestSign.getShopType() == ChestShopType.BUY);
        PlayerCredentials credentials = innChest.getOwnerCredentials();
        IdpMaterial shopMaterial = chestSign.getMaterial();
        boolean senderUsesBank = false;

        IdpContainer senderContainer = null;
        IdpContainer receiverContainer = null;

        TransactionObject transactionSender = null;
        TransactionObject transactionReceiver = null;

        if (buyShop) {
            senderContainer = new IdpContainer(customer.getInventory());
            transactionSender = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());
            senderUsesBank = true;

            receiverContainer = new IdpContainer(bukkitChest.getInventory());
            transactionReceiver = TransactionHandler.getTransactionObject(customer);
        } else {
            senderContainer = new IdpContainer(bukkitChest.getInventory());
            transactionSender = TransactionHandler.getTransactionObject(customer);

            receiverContainer = new IdpContainer(customer.getInventory());
            transactionReceiver = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());
        }

        int senderValutas = transactionSender.getValue(senderUsesBank ? TransactionType.VALUTAS_IN_BANK : TransactionType.VALUTAS);

        if (senderValutas >= chestSign.getCost()) {
            int count = senderContainer.countMaterial(shopMaterial);
            IdpItem item = new IdpItem(shopMaterial);

            if (count >= chestSign.getAmount()) {
                int maxAccept = receiverContainer.getMaximumAcceptAmount(item, chestSign.getAmount());

                if (maxAccept == chestSign.getAmount()) {
                    result = PurchaseResult.SUCCESSFUL_PURCHASE;
                } else {
                    if (buyShop) {
                        result = PurchaseResult.OWNER_TOO_FULL;
                    } else {
                        result = PurchaseResult.CUSTOMER_TOO_FULL;
                    }
                }
            } else {
                if (buyShop) {
                    result = PurchaseResult.CUSTOMER_NOT_ENOUGH_ITEMS;
                } else {
                    result = PurchaseResult.OWNER_NOT_ENOUGH_ITEMS;
                }
            }
        } else {
            if (buyShop) {
                result = PurchaseResult.OWNER_FUNDS_INSUFFICIENT;
            } else {
                result = PurchaseResult.CUSTOMER_FUNDS_INSUFFICIENT;
            }
        }

        if (result == PurchaseResult.SUCCESSFUL_PURCHASE) {
            int cost = chestSign.getCost();

            transactionSender.subtractValue(cost, senderUsesBank ? TransactionType.VALUTAS_IN_BANK : TransactionType.VALUTAS);
            transactionReceiver.addValue(cost, senderUsesBank ? TransactionType.VALUTAS : TransactionType.VALUTAS_IN_BANK);

            int amt = chestSign.getAmount();

            for (int i = 0; i < senderContainer.size(); i++) {
                IdpItemStack stack = senderContainer.getItemAt(i);

                if (stack != null && stack.getMaterial() == shopMaterial) {
                    IdpMaterial mat = stack.getMaterial();

                    int minValue = Math.min(amt, mat.getMaxStackSize());
                    int remCount = Math.min(minValue, stack.getAmount());
                    boolean removeItem = (remCount == stack.getAmount());

                    IdpItemStack newStack = new IdpItemStack(stack);

                    if (removeItem) {
                        stack = null;
                    } else {
                        stack.setAmount(stack.getAmount() - remCount);
                        newStack.setAmount(remCount);
                    }

                    senderContainer.setItemAt(i, stack);
                    receiverContainer.addMaterialToStack(newStack);
                    amt -= remCount;

                    if (amt == 0) {
                        break;
                    }
                }
            }

            if (buyShop) {
                bukkitChest.getInventory().setContents(receiverContainer.getBukkitItems());
            } else {
                bukkitChest.getInventory().setContents(senderContainer.getBukkitItems());
            }

            bukkitChest.update();

            IdpPlayerInventory inv = customer.getInventory();

            if (buyShop) {
                inv.setItems(senderContainer.getItems());
            } else {
                inv.setItems(receiverContainer.getItems());
            }

            inv.updateBukkitInventory();

            IdpPlayer pOwner = InnPlugin.getPlugin().getPlayer(innChest.getOwner());

            if (pOwner != null && pOwner.isOnline()
                    && pOwner.getSession().hasSetting(PlayerSettings.SHOPNOTIFICATION)) {
                pOwner.printInfo(customer.getName() + " just " + (buyShop ? "sold " : "bought ") + chestSign.getAmount() + " items of " + shopMaterial.getName().toLowerCase() + " for " + chestSign.getCost() + " vT!");
            }

            ShopTransactionLogger shopLogger = (ShopTransactionLogger) LogType.getLoggerFromType(LogType.SHOP_TRANSACTION);
            shopLogger.logChestShopTransaction(customer.getName(), innChest.getOwner(), !buyShop, shopMaterial, chestSign.getAmount(), chestSign.getCost());
        }
    }

    /**
     * Gets the result of this chest shop transaction
     * @return
     */
    public PurchaseResult getResult() {
        return result;
    }

}
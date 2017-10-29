package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hret
 *
 * TODO: Remove depedency on the Bukkit Click Event.
 */
public class InnInventoryClickEvent extends APlayerEvent {

    private final InventoryClickEvent event;

    public InnInventoryClickEvent(IdpPlayer player, InventoryClickEvent event) {
        super(player, InnEventType.PLAYER_INVENTORY_CLICK);

        this.event = event;
    }

    /**
     * The real bukkit event;
     * @return
     */
    public InventoryClickEvent getEvent() {
        return event;
    }

    // ----------------------------------------
    // Bukkit proxied methods
    // ----------------------------------------
    public SlotType getSlotType() {
        return event.getSlotType();
    }

    public ItemStack getCursor() {
        return event.getCursor();
    }

    public ItemStack getCurrentItem() {
        return event.getCurrentItem();
    }

    public boolean isRightClick() {
        return event.isRightClick();
    }

    public boolean isLeftClick() {
        return event.isLeftClick();
    }

    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    public void setResult(Result newResult) {
        event.setResult(newResult);
    }

    public Result getResult() {
        return event.getResult();
    }

    public HumanEntity getWhoClicked() {
        return event.getWhoClicked();
    }

    public void setCurrentItem(ItemStack what) {
        event.setCurrentItem(what);
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean toCancel) {
        event.setCancelled(toCancel);
    }

    public int getSlot() {
        return event.getSlot();
    }

    public int getRawSlot() {
        return event.getRawSlot();
    }
    
}

package net.innectis.innplugin.inventory;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.inventory.payload.InventoryPayload;
import net.innectis.innplugin.NotchcodeUsage;
import net.innectis.innplugin.player.IdpPlayer;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NonNullList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

@NotchcodeUsage()
class MCPlayerInventory implements IInventory, IdpInventoryDataHolder {

    private volatile EntityPlayer player;
    private NonNullList<ItemStack> items;
    private NonNullList<ItemStack> armor;
    private NonNullList<ItemStack> extraSlots = NonNullList.a(5, ItemStack.a);
    private List<HumanEntity> transaction = new ArrayList<HumanEntity>();
    private InventoryPayload payload;

    private MCPlayerInventory(EntityPlayer entityplayer) {
        this.player = entityplayer;
        this.items = entityplayer.inventory.items;
        this.armor = entityplayer.inventory.armor;

        for (int i = 0; i < entityplayer.inventory.extraSlots.size(); i++) {
            extraSlots.set(i, entityplayer.inventory.extraSlots.get(i));
        }
    }

    public MCPlayerInventory(IdpPlayer player) {
        this(player.getHandle().getHandle());
    }

    public NonNullList<ItemStack> getContents() {
        ItemStack[] contents = new ItemStack[getSize()];
        ItemStack[] itemsArray = this.items.toArray(new ItemStack[this.items.size()]);
        ItemStack[] armorArray = this.armor.toArray(new ItemStack[this.armor.size()]);
        ItemStack[] extraSlotsArray = this.extraSlots.toArray(new ItemStack[this.extraSlots.size()]);

        System.arraycopy(itemsArray, 0, contents, 0, itemsArray.length);
        System.arraycopy(armorArray, 0, contents, itemsArray.length, armorArray.length);
        System.arraycopy(extraSlotsArray, 0, contents, itemsArray.length + armorArray.length, extraSlotsArray.length);

        NonNullList<ItemStack> returnContents = NonNullList.a(getSize(), ItemStack.a);

        for (int i = 0; i < contents.length; i++) {
            returnContents.set(i, contents[i]);
        }

        return returnContents;
    }

    public boolean x_() {
        return false;
    }

    public int getSize() {
        return 45;
    }

    public ItemStack getItem(int i) {
        NonNullList<ItemStack> is  = this.items;

        if (i >= is.size()) {
            i -= is.size();
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.size()) {
            i -= is.size();
            is = this.extraSlots;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        return is.get(i);
    }

    public ItemStack splitStack(int i, int j) {
        NonNullList<ItemStack> is = this.items;

        if (i >= is.size()) {
            i -= is.size();
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.size()) {
            i -= is.size();
            is = this.extraSlots;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (!is.get(i).isEmpty()) {
            if (is.get(i).getCount() <= j) {
                ItemStack itemstack = is.get(i);
                is.set(i, ItemStack.a);
                return itemstack;
            }

            ItemStack itemstack = is.get(i).cloneAndSubtract(j);
            if (is.get(i).isEmpty()) {
                is.set(i, ItemStack.a);
            }

            return itemstack;
        }

        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        NonNullList<ItemStack> is = this.items;

        if (i >= is.size()) {
            i -= is.size();
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.size()) {
            i -= is.size();
            is = this.extraSlots;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        is.set(i, itemstack);
    }

    private int getReversedItemSlotNum(int i) {
        if (i >= 27) {
            return i - 27;
        }
        return i + 9;
    }

    private int getReversedArmorSlotNum(int i) {
        if (i == 0) {
            return 3;
        }
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 1;
        }
        if (i == 3) {
            return 0;
        }
        return i;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public boolean b(int i, ItemStack is) {
        return true;
    }

    public void update() {
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (!this.items.get(i).isEmpty()) {
            ItemStack itemstack = this.items.get(i);

            this.items.set(i, ItemStack.a);
            return itemstack;
        } else {
            return null;
        }
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public void closeContainer(EntityHuman human) {

    }

    public void startOpen(EntityHuman human) {
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public InventoryHolder getOwner() {
        return player.inventory.getOwner();
    }

    public void setMaxStackSize(int s) {
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {

    }

    public Location getLocation() {
        return null;
    }

    public int h() {
        return 0;
    }

    public void clear() {

    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(getName());
    }

    public boolean hasCustomName() {
        return true;
    }

    public String getName() {
        if (this.player.getName().length() > 16) {
            return this.player.getName().substring(0, 16);
        }
        return this.player.getName();

    }

    public void setPayload(InventoryPayload payload) {
        this.payload = payload;
    }

    public InventoryPayload getPayload() {
        return payload;
    }
}

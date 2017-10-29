package net.innectis.innplugin.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.StackBag;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Hret
 */
public class IdpPlayerInventory {

    private static final int DEFAULT_ITEM_SIZE = 36;
    private static final int DEFAULT_ARMOR_SIZE = 4;
    /**
     * ID of the owner of this inventory
     */
    private UUID playerId;
    /**
     * Owner of this inventory
     */
    private String playerName;
    /**
     * The type of the inventory
     */
    private InventoryType type;
    /**
     * The items in the inventory
     */
    private IdpItemStack[] items;
    /**
     * The armor items in the inventory
     */
    private IdpItemStack[] armor;
    /**
     * The off-hand item in the inventory
     */
    private IdpItemStack[] offHandItem;
    /**
     * The experience of the player
     */
    private float experience;
    /**
     * The level of the player
     */
    private int level;
    /**
     * The health of the player
     */
    private double health;
    /**
     * The hunger of the player
     */
    private int hunger;
    /**
     * The potion effects the player has
     */
    private List<PotionEffect> potionEffects;

    /**
     * Creates a new empty inventory
     *
     * @param playerId
     * @param playerName
     * @param type
     */
    public IdpPlayerInventory(UUID playerId, String playerName, InventoryType type) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.type = type;
        items = new IdpItemStack[DEFAULT_ITEM_SIZE];
        armor = new IdpItemStack[DEFAULT_ARMOR_SIZE];
        offHandItem = new IdpItemStack[1];
        experience = 0;
        level = 0;
        health = 20;
        hunger = 20;
        potionEffects = new ArrayList<PotionEffect>();
    }

    /**
     * Creates a new empty inventory
     *
     * @param playerId
     * @param playerName
     * @param type
     * @param items
     * @param armor
     * @param offHandItem
     * @param experience
     * @param level
     * @param health
     * @param hunger
     */
    public IdpPlayerInventory(UUID playerId, String playerName, InventoryType type, IdpItemStack[] items, IdpItemStack[] armor, IdpItemStack[] offHandItem, float experience, int level, double health, int hunger) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.type = type;
        this.items = new IdpItemStack[DEFAULT_ITEM_SIZE];
        for (int i = 0; i < this.items.length && i < items.length; i++) {
            this.items[i] = items[i];
        }
        this.armor = new IdpItemStack[DEFAULT_ARMOR_SIZE];
        for (int i = 0; i < this.armor.length && i < armor.length; i++) {
            this.armor[i] = armor[i];
        }
        this.offHandItem = new IdpItemStack[1];
        for (int i = 0; i < this.offHandItem.length && i < offHandItem.length; i++) {
            this.offHandItem[i] = offHandItem[i];
        }
        this.experience = experience;
        this.level = level;
        this.health = health;
        this.hunger = hunger;
        this.potionEffects = new ArrayList<PotionEffect>();
    }

    /**
     * Gets the inventory type
     *
     * @return
     */
    public InventoryType getType() {
        return type;
    }

    /**
     * sets the inventory type
     */
    public void setType(InventoryType type) {
        this.type = type;
    }

    /**
     * gets the experience
     */
    public float getExperience() {
        return experience;
    }

    /**
     * sets the experience
     */
    public void setExperience(float experience) {
        this.experience = experience;
    }

    /**
     * gets the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * sets the level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * gets the health
     */
    public double getHealth() {
        return health;
    }

    /**
     * sets the health
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * gets the hunger
     */
    public int getHunger() {
        return hunger;
    }

    /**
     * sets the hunger
     */
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    /**
     * Gets a list of all the potion effects.
     * @return
     */
    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    /**
     * Sets the inventories potion effect list.
     * @param potionEffects
     */
    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    /**
     * Gets the item at the given position
     * <p/>
     * items = 0 - 35 <br/>
     * armor = 36 - 39
     *
     * @param id
     * @return
     */
    public IdpItemStack getItemAt(int id) {
        if (id >= 0 && id < DEFAULT_ITEM_SIZE) {
            return items[id];
        } else if (id >= DEFAULT_ITEM_SIZE && id < 40) {
            return armor[id - DEFAULT_ITEM_SIZE];
        } else if (id == 40) {
            return offHandItem[id];
        } else {
            return null;
        }
    }

    /**
     * sets the item at the given position
     * <p/>
     * items = 0 - 35 <br/>
     * armor = 36 - 39
     *
     * @param id
     */
    public void setItemAt(int id, IdpItemStack item) {
        if (id >= 0 && id < DEFAULT_ITEM_SIZE) {
            items[id] = item;
        } else if (id >= DEFAULT_ITEM_SIZE && id < 40) {
            armor[id - DEFAULT_ITEM_SIZE] = item;
        } else if (id == 40) {
            offHandItem[id] = item;
        }
    }

    /**
     * Clears the inventory of the player
     */
    public void clear() {
        items = new IdpItemStack[DEFAULT_ITEM_SIZE];
        armor = new IdpItemStack[DEFAULT_ARMOR_SIZE];
        offHandItem = new IdpItemStack[1];
        experience = 0.0f;
        level = 0;
    }

    /**
     * Stores the inventory in the database.
     *
     * @return
     */
    public boolean store() {
        // Dont save the none type inventory
        if (type == InventoryType.NONE) {
            InnPlugin.logError("Trying to store a 'none' inventory for player " + playerName + " - is player new?");
            return true;
        }

        // Do not store a NO_SAVE inventory
        if (type == InventoryType.NO_SAVE) {
            return true;
        }

        PreparedStatement statement = null;
        ResultSet resultset = null;

        try {
            statement = DBManager.prepareStatement(" SELECT bagid FROM player_inventory where player_id = ? and inventorytype = ? ");
            statement.setString(1, playerId.toString());
            statement.setInt(2, type.getId());

            long bagid = 0;
            resultset = statement.executeQuery();
            if (resultset.next()) {
                bagid = resultset.getLong("bagid");
            }

            IdpItemStack[] itemarr = new IdpItemStack[41];
            System.arraycopy(items, 0, itemarr, 0, DEFAULT_ITEM_SIZE);
            System.arraycopy(armor, 0, itemarr, DEFAULT_ITEM_SIZE, DEFAULT_ARMOR_SIZE);
            System.arraycopy(offHandItem, 0, itemarr, DEFAULT_ITEM_SIZE + DEFAULT_ARMOR_SIZE, 1);

            // Save the contents;
            StackBag inventorybag = new StackBag(bagid, itemarr);
            bagid = inventorybag.save();

            // Generate potion string.
            String potionString = "";

            for (PotionEffect effect : potionEffects) {
                if (!potionString.isEmpty()) {
                    potionString += ",";
                }

                potionString += effect.getType().getId() + ":" + effect.getDuration() + ":" + effect.getAmplifier();
            }

            DBManager.closeResultSet(resultset);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("INSERT INTO player_inventory (player_id, inventorytype, level, experience, bagid, health, hunger, potioneffects) VALUES "
                    + "(?,?,?,?,?,?,?, ?) ON DUPLICATE KEY UPDATE level = ?, experience = ?, bagid = ?, health = ?, hunger = ?, potioneffects = ?;");
            statement.setString(1, playerId.toString());
            statement.setInt(2, type.getId());
            statement.setInt(3, level);
            statement.setFloat(DEFAULT_ARMOR_SIZE, experience);
            statement.setLong(5, bagid);
            statement.setDouble(6, health);
            statement.setInt(7, hunger);
            statement.setString(8, potionString);
            statement.setInt(9, level);
            statement.setFloat(10, experience);
            statement.setLong(11, bagid);
            statement.setDouble(12, health);
            statement.setInt(13, hunger);
            statement.setString(14, potionString);
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Failed to store inventory for player " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(resultset);
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * Loads the inventory from the database
     *
     * @param playerCredentials
     * @param type
     * @param plugin
     * @return
     */
    public static IdpPlayerInventory load(UUID playerId, String playerName, InventoryType type, InnPlugin plugin) {
        PreparedStatement statement = null;
        ResultSet resultset = null;

        try {
            int level = 0;
            float exp = 0f;
            double health = 20.0D;
            int hunger = 20;
            long bagid = 0;
            String potionEffects = "";

            statement = DBManager.prepareStatement("SELECT level, experience, bagid, health, hunger, potioneffects FROM player_inventory WHERE player_id = ? and inventorytype = ? LIMIT 1;");
            statement.setString(1, playerId.toString());
            statement.setInt(2, type.getId());
            resultset = statement.executeQuery();

            if (resultset.next()) {
                bagid = resultset.getLong("bagid");
                level = resultset.getInt("level");
                exp = resultset.getFloat("experience");
                health = resultset.getDouble("health");
                hunger = resultset.getInt("hunger");
                potionEffects = resultset.getString("potioneffects");
            }

            if (bagid > 0) {
                StackBag bag = StackBag.getContentbag(bagid);

                IdpPlayerInventory inv = new IdpPlayerInventory(playerId, playerName, type);
                inv.experience = exp;
                inv.level = level;
                inv.health = health;
                inv.hunger = hunger;
                IdpItemStack[] itemarr = bag.getContents();

                try {
                    if (!StringUtil.stringIsNullOrEmpty(potionEffects)) {
                        for (String potionString : potionEffects.split(",")) {
                            String[] potion = potionString.split(":");
                            inv.potionEffects.add(new PotionEffect(PotionEffectType.getById(Integer.parseInt(potion[0])), Integer.parseInt(potion[1]), Integer.parseInt(potion[2])));
                        }
                    }
                } catch (NumberFormatException ex) {
                    InnPlugin.logError("Error loading inventory potion effects of player " + playerName + "!", ex);
                }

                System.arraycopy(itemarr, 0, inv.items, 0, DEFAULT_ITEM_SIZE);
                System.arraycopy(itemarr, DEFAULT_ITEM_SIZE, inv.armor, 0, DEFAULT_ARMOR_SIZE);
                System.arraycopy(itemarr, DEFAULT_ITEM_SIZE + DEFAULT_ARMOR_SIZE, inv.offHandItem, 0, 1);

                return inv;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Error getting group of player " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(resultset);
            DBManager.closePreparedStatement(statement);
        }

        return new IdpPlayerInventory(playerId, playerName, type);
    }

    /**
     * Sets the items of this inventory
     *
     * @param items
     */
    public void setItems(IdpItemStack[] items) {
        if (this.items.length != items.length) {
            throw new IdpRuntimeException("Stack sizes are not the same! Old size: " + this.items.length + " new size: " + items.length);
        }
        this.items = items;
    }

    /**
     * Sets the armor of this inventory
     *
     * @param armor
     */
    public void setArmorItems(IdpItemStack[] armor) {
        this.armor = armor;
    }

    /**
     * Sets the off hand item of this inventory
     * @param offHandItem
     */
    public void setOffHandItem(IdpItemStack offHandItem) {
        this.offHandItem = new IdpItemStack[] {offHandItem};
    }

    /**
     * Sets the content of the inventory from bukkit itemstacks A null value is
     * allowed for both parameters
     *
     * @param items
     * @param armor
     */
    public void setContents(ItemStack[] items, ItemStack[] armor, ItemStack offHandItem) {
        this.items = IdpItemStack.fromBukkitItemStack(items);
        this.armor = IdpItemStack.fromBukkitItemStack(armor);
        IdpItemStack[] stack = new IdpItemStack[1];
        stack[0] = IdpItemStack.fromBukkitItemStack(offHandItem);
        this.offHandItem = stack;
    }

    /**
     * Sets the content of the inventory from given itemstacks.
     *
     * @param items
     * @param armor
     */
    public void setContents(IdpItemStack[] items, IdpItemStack[] armor, IdpItemStack[] offHandItem) {
        this.items = items;
        this.armor = armor;
        this.offHandItem = offHandItem;
    }

    /**
     * Gets the itemstack of all items
     *
     * @return
     */
    public IdpItemStack[] getItems() {
        return items;
    }

    /**
     * Gets the itemstack of all armor items
     *
     * @return
     */
    public IdpItemStack[] getArmorItems() {
        return armor;
    }

    /**
     * Gets the item in the off hand
     * @return
     */
    public IdpItemStack[] getOffHandItem() {
        return offHandItem;
    }

    /**
     * Updates bukkit's inventory with this one
     */
    public void updateBukkitInventory() {
        IdpPlayer idpplayer = InnPlugin.getPlugin().getPlayer(playerId);

        if (idpplayer == null) {
            InnPlugin.logError("Trying to update inventory of offline player.");
            return;
        }

        Player player = idpplayer.getHandle();

        player.getInventory().setContents(getBukkitItems());
        player.getInventory().setArmorContents(getBukkitArmorItems());
        player.getInventory().setItemInOffHand(getBukkitOffHandItem()[0]);

        player.setExp(getExperience());

        // Don't set health to something that is dead...
        if (!player.isDead()) {
            player.setHealth(Math.max(0, health));
            player.setFoodLevel(hunger);
        }

        idpplayer.updateInventory();
    }

    /**
     * Gets the bukkit itemstack of all items
     *
     * @return
     */
    public org.bukkit.inventory.ItemStack[] getBukkitItems() {
        return IdpItemStack.toBukkitItemStack(items);
    }

    /**
     * Gets the bukkit itemstack of the armour components
     *
     * @return
     */
    public org.bukkit.inventory.ItemStack[] getBukkitArmorItems() {
        return IdpItemStack.toBukkitItemStack(armor);
    }

    /**
     * Gets the bukkit itemstack of the off hand item
     */
    public org.bukkit.inventory.ItemStack[] getBukkitOffHandItem() {
        return IdpItemStack.toBukkitItemStack(offHandItem);
    }
    
}

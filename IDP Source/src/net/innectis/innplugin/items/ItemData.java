package net.innectis.innplugin.items;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.util.StringUtil;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

/**
 * @author Hret
 *
 * Wrapper class to handle itemdata.
 * TODO: This needs to be improved, bukkit supports a lot of it
 */
public class ItemData {

    private static final String INNECTIS_COMPOUND_TAGNAME = "__INNECTIS";
    private static final String IDP_ARBITRARY_DATA_STRING = "__SPECIAL_";
    private static final String IDP_SPECIALITEM_KEY = "SPECIALITEM";
    //
    private NBTTagCompound innCompound;
    private Map<EnchantmentType, Integer> _enchantments;

    /**
     * Makes a new ItemData object with the compound as the source
     * @param source
     */
    ItemData(NBTTagCompound source) {
        this.innCompound = source;
        _enchantments = null;
    }

    /**
     * Makes a new empty ItemData object
     */
    ItemData() {
        this.innCompound = null;
        _enchantments = null;
    }

    /**
     * The NBTTagCompound object
     * @return
     */
    NBTTagCompound getTag() {
        return innCompound;
    }

    /**
     * Converts the itemdata to an bytearray
     * @return
     */
    public byte[] toByte() {
        if (innCompound == null) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(stream);

        try {
            NBTCompressedStreamTools.a(innCompound, output);
        } catch (IOException ex) {
            return null;
        }

        return stream.toByteArray();
    }

    /**
     * Makes a new itemdata object from the byte aray
     * @param bytes
     * @return
     */
    public static ItemData fromByte(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream input = new DataInputStream(stream);
            NBTTagCompound source = null;

            try {
                source = NBTCompressedStreamTools.a(input);
            } catch (IOException ex) {
                return null;
            }

            return new ItemData(source);
        }
        return null;
    }
    // END Package Only

    /**
     * Gets the information of the book.
     * @return Bookinfo or Null if no book info
     */
    public Bookinfo getBookinfo() {
        if (innCompound == null || !innCompound.hasKey("author")) {
            return null;
        }

        Bookinfo info = new Bookinfo();
        info.setAuthor(innCompound.getString("author"));
        info.setTitle(innCompound.getString("title"));

        NBTTagList taglist = innCompound.getList("pages", 8);
        List<String> pages = new ArrayList<String>(taglist.size());
        for (int i = 0; i < taglist.size(); i++) {
            String str = taglist.getString(i);
            pages.add(str);
        }
        info.setPages(pages);

        return info;
    }

    /**
     * Sets the bookinfo
     * @param info
     * The info to be added
     *
     */
    public void setBookinfo(Bookinfo info) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (info == null) {
            return;
        }

        innCompound.setString("author", info.getAuthor());
        innCompound.setString("title", info.getTitle());

        NBTTagList taglist = new NBTTagList();

        List<String> pages = info.getPages();
        for (int i = 0; i < pages.size(); i++) {
            String page = pages.get(i);
            NBTTagString string = new NBTTagString(page);
            taglist.add(string);
        }
        innCompound.set("pages", taglist);
    }

    /**
     * Method stolen from Bukkit
     * Bukkit: <b>private void rebuildEnchantments(Map<EnchantmentType, Integer> enchantments)</b>
     * @param enchantments
     */
    private void setEnchantments() {
        if (getEnchantments() == null || getEnchantments().isEmpty()) {
            return;
        }

        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        NBTTagList list = new NBTTagList();

        for (Map.Entry<EnchantmentType, Integer> entry : getEnchantments().entrySet()) {
            NBTTagCompound subtag = new NBTTagCompound();

            subtag.setShort("id", (short) entry.getKey().getId());
            subtag.setShort("lvl", (short) (int) entry.getValue());

            list.add(subtag);
        }

        if (getEnchantments().isEmpty()) {
            innCompound.remove("ench");
        } else {
            innCompound.set("ench", list);
        }
    }

    /**
     * The map with the enchantments on this object.
     *
     * @return the enchantments
     */
    public Map<EnchantmentType, Integer> getEnchantments() {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (_enchantments == null) {
            _enchantments = new HashMap<EnchantmentType, Integer>(4);

            NBTTagList list = innCompound.getList("ench", 10);
            // Check if there is any enchantments
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null) {
                        NBTTagCompound subtag = list.get(i);
                        int id = subtag.getShort("id");
                        int lvl = subtag.getShort("lvl");

                        _enchantments.put(EnchantmentType.fromId(id), lvl);
                    }
                }
            }
        }
        return _enchantments;
    }

    public void addEnchantment(EnchantmentType enchantmentType, int level) {
        getEnchantments().put(enchantmentType, level);
        setEnchantments();
    }

    /**
     * Removes the specified enchantment. Returns true if successfully removed
     * or false otherwise
     * @param enchantmentType
     * @return
     */
    public boolean removeEnchantment(EnchantmentType enchantmentType) {
        Map<EnchantmentType, Integer> enchs = getEnchantments();

        if (enchs.containsKey(enchantmentType)) {
            enchs.remove(enchantmentType);
            setEnchantments();
            return true;
        }

        return false;
    }

    /**
     * Returns the level for the given enchantment.
     * @param enchantmentType
     * @return the level or 0 is enchantment not present
     */
    public int getEnchantmentlevel(EnchantmentType enchantmentType) {
        Integer level = getEnchantments().get(enchantmentType);
        return (level != null ? level : 0);
    }

    /**
     * Checks if the given enchantment is in the data.
     * @param enchantmentType
     * @return
     */
    public boolean hasEnchantment(EnchantmentType enchantmentType) {
        return getEnchantmentlevel(enchantmentType) > 0;
    }

    /**
     * Will lookup the name of the item (or null if none)
     * @return
     */
    public String getItemname() {
        if (innCompound == null || !innCompound.hasKey("display")) {
            return null;
        }
        return innCompound.getCompound("display").getString("Name");
    }

    /**
     * Sets the name of the item
     * @param name
     */
    public void setItemName(String name) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (name == null) {
            return;
        }

        if (!innCompound.hasKey("display")) {
            innCompound.set("display", new NBTTagCompound());
        }
        innCompound.getCompound("display").setString("Name", name);
    }

    public void clearItemname() {
        if (innCompound == null || !innCompound.hasKey("display")) {
            return;
        }

        innCompound.getCompound("display").remove("Name");
    }

    /**
     * Will lookup the list of Lores (or null if none)
     * @return
     */
    public String[] getLore() {
        if (innCompound == null || !innCompound.hasKey("display")) {
            return null;
        }

        NBTTagList nbtlist = innCompound.getCompound("display").getList("Lore", 8);

        ArrayList<String> strings = new ArrayList<String>(nbtlist.size());
        for (int i = 0; i < nbtlist.size(); i++) {
            strings.add(nbtlist.getString(i));
        }

        return strings.toArray(new String[strings.size()]);
    }

    /**
     * Sets the lores on this item.
     * <p/>
     * To clear the lures an empty array or a null value can be given.
     * @param name
     */
    public void setLore(String[] lores) {
        // If lore is null (or empty), clear it.
        if (lores == null || lores.length == 0) {
            if (innCompound.hasKey("display")) {
                innCompound.getCompound("display").remove("Lore");
            }
            return;
        }

        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        // Create new display compound if needed
        if (!innCompound.hasKey("display")) {
            innCompound.set("display", new NBTTagCompound());
        }

        // Set new lores
        NBTTagList list = new NBTTagList();
        for (String lore : lores) {
            list.add(new NBTTagString(lore));
        }

        innCompound.getCompound("display").set("Lore", list);
    }

    /**
     * This will get the lores currently on this item and add the new one to this list.
     * Adds a new lore to this item
     * @param lore
     */
    public void addLore(String lore) {
        // Check if there is a lore
        if (StringUtil.stringIsNullOrEmpty(lore)) {
            return;
        }

        String[] oldLores = getLore();
        String[] newLores;

        if (oldLores == null) {
            newLores = new String[]{lore};
        } else {
            newLores = new String[oldLores.length + 1];

            // Copy old lores
            System.arraycopy(oldLores, 0, newLores, 0, oldLores.length);
            // Add new lore
            newLores[oldLores.length] = lore;
        }

        // Set lores
        setLore(newLores);
    }

    /**
     * Will lookup the owner of this mobhead.
     * <p/>
     *
     * <b>Note: This is only for MOB_HEADS!</b>
     * @return
     */
    public String getMobheadName() {
        if (innCompound == null || !innCompound.hasKey("SkullOwner")) {
            return null;
        }

        NBTTagCompound compound = innCompound.getCompound("SkullOwner");

        return compound.getString("Name");
    }

    /**
     * Sets the name of the mobhead.
     * The skin of this player is used to get the skin.
     * <p/>
     * <b>Note: This is only for MOB_HEADS!</b>
     *
     * @param playername
     */
    public void setMobheadName(String playername) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (playername == null) {
            return;
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", playername);

        innCompound.set("SkullOwner", compound);
    }

    /**
     * Used to set the color of leather armor. Cannot be
     * used for anything else.
     * @return
     */
    public void setColor(int value) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (value < 0) {
            return;
        }

        if (!innCompound.hasKey("display")) {
            innCompound.set("display", new NBTTagCompound());
        }

        innCompound.getCompound("display").setInt("color", value);
    }

    /**
     * Sets the colour to an
     * @param col
     */
    public void setColor(Color col) {
        if (col == null) {
            return;
        }
        setColor(Integer.toHexString(col.getRGB()).substring(2));
    }

    /**
     * Sets the colour to an hex value.
     * @param col
     * @throws NumberFormatException
     * This exception is thrown when the hexcode is not valid
     */
    public void setColor(String hexcode) throws NumberFormatException {
        if (hexcode == null || hexcode.length() != 6) {
            return;
        }
        setColor(Integer.parseInt(hexcode, 16));
    }

    /**
     * Gets the color of this item. Only works for leather
     * armor at the moment!
     * @return
     */
    public int getColor() {
        if (innCompound == null || !innCompound.hasKey("display")) {
            return -1;
        }

        return innCompound.getCompound("display").getInt("color");
    }

    /**
     * Gets the base color of the banner
     * @return
     */
    public DyeColor getBannerBaseColor() {
        if (innCompound == null || !innCompound.hasKey("BlockEntityTag")) {
            return null;
        }

        NBTTagCompound blockEntityCompound = innCompound.getCompound("BlockEntityTag");
        return DyeColor.getByDyeData((byte) blockEntityCompound.getInt("Base"));
    }

    /**
     * Sets the base color of the banner
     * @param color
     */
    public void setBannerBaseColor(DyeColor color) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (!innCompound.hasKey("BlockEntityTag")) {
            innCompound.set("BlockEntityTag", new NBTTagCompound());
        }

        NBTTagCompound blockEntityCompound = innCompound.getCompound("BlockEntityTag");
        blockEntityCompound.setInt("Base", color.getDyeData());
    }

    /**
     * Gets all the patterns of the banner
     * @return
     */
    public List<Pattern> getBannerPatterns() {
        if (innCompound == null || !innCompound.hasKey("BlockEntityTag")) {
            return null;
        }

        NBTTagCompound blockEntityCompound = innCompound.getCompound("BlockEntityTag");
        NBTTagList patternList = blockEntityCompound.getList("Patterns", 10);
        List<Pattern> bannerPatterns = new ArrayList<Pattern>();

        for (int i = 0; i < patternList.size(); i++) {
            NBTTagCompound compound = patternList.get(i);
            DyeColor color = DyeColor.getByDyeData((byte) compound.getInt("Color"));
            PatternType patternType = PatternType.getByIdentifier(compound.getString("Pattern"));
            bannerPatterns.add(new Pattern(color, patternType));
        }

        return bannerPatterns;
    }

    /**
     * Sets all the patterns of the banner
     * @param patterns
     */
    public void setBannerPatterns(List<Pattern> patterns) {
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (!innCompound.hasKey("BlockEntityTag")) {
            innCompound.set("BlockEntityTag", new NBTTagCompound());
        }

        NBTTagCompound blockEntityCompound = innCompound.getCompound("BlockEntityTag");
        NBTTagList patternList = new NBTTagList();

        for (Pattern pattern : patterns) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Pattern", pattern.getPattern().getIdentifier());
            compound.setInt("Color", pattern.getColor().getDyeData());
            patternList.add(compound);
        }

        blockEntityCompound.set("Patterns", patternList);
    }

    /**
     * Checks if there is any data.
     * @return
     */
    public boolean isEmpty() {
        return innCompound == null || innCompound.c().isEmpty();
    }

    /**
     * Checks if this source and the target source are the same.
     * @param data
     * @return
     */
    public boolean matches(ItemData data) {
        return innCompound == data.getTag();
    }

    /**
     * Sets either a special value, or a value from an NBT
     * path, with the given value
     * @param key
     * @param value
     */
    public void setValue(String key, String value) {
        if (StringUtil.stringIsNullOrEmpty(key)) {
            return;
        }

        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (key.contains("/")) {
            String[] keys = key.split("/");
            String actualKey = keys[keys.length - 1];
            NBTTagCompound compound = getInnermostCompound(keys);

            if (compound == null) {
                compound = createCompoundPath(keys);
            }

            if (value == null) {
                compound.remove(actualKey);
            } else {
                compound.setString(actualKey, value);
            }
        } else {
            if (!innCompound.hasKey(INNECTIS_COMPOUND_TAGNAME)) {
                innCompound.set(INNECTIS_COMPOUND_TAGNAME, new NBTTagCompound());
            }

            if (StringUtil.stringIsNullOrEmpty(value)) {
                innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).remove(IDP_ARBITRARY_DATA_STRING + key);
            } else {
                innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).setString(IDP_ARBITRARY_DATA_STRING + key, value);
            }
        }
    }

    /**
     * Looks up the special IDP value or a value from an NBT path
     * that might have been set on this item
     * @param key
     * @return the value
     */
    public String getValue(String key) {
        if (innCompound == null) {
            return null;
        }

        if (key.contains("/")) {
            String[] keys = key.split("/");
            String actualKey = keys[keys.length - 1];
            NBTTagCompound compound = getInnermostCompound(keys);

            if (compound != null) {
                return compound.getString(actualKey);
            } else {
                return null;
            }
        } else {
            if (!innCompound.hasKey(INNECTIS_COMPOUND_TAGNAME)) {
                return null;
            }

            return innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).getString(IDP_ARBITRARY_DATA_STRING + key);
        }
    }

    /**
     * Checks if the specified value exists in the item data. Can also
     * specify an NBT path such as compound1/compound2/value
     * @param key
     * @return
     */
    public boolean hasValue(String key) {
        if (innCompound == null) {
            return false;
        }

        if (key.contains("/")) {
            String[] keys = key.split("/");
            String actualKey = keys[keys.length - 1];
            NBTTagCompound compound = getInnermostCompound(keys);

            if (compound != null) {

                return compound.hasKey(actualKey);
            } else {
                return false;
            }

        } else {
            if (!innCompound.hasKey(INNECTIS_COMPOUND_TAGNAME)) {
                return false;
            }

            return (innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).getString(IDP_ARBITRARY_DATA_STRING + key) != null);
        }
    }

    /**
     * This will check if the only data value set on this item is the itemname.
     * @return
     */
    public boolean onlyContainsName() {
        return innCompound != null && innCompound.c().size() == 1 && !StringUtil.stringIsNullOrEmpty(getItemname());
    }

    /**
     * Adds an itemeffect to this item.
     * @param type
     */
    public void setSpecialItem(SpecialItemType type) {
        if (type == null) {
            return;
        }
        if (innCompound == null) {
            innCompound = new NBTTagCompound();
        }

        if (!innCompound.hasKey(INNECTIS_COMPOUND_TAGNAME)) {
            innCompound.set(INNECTIS_COMPOUND_TAGNAME, new NBTTagCompound());
        }

        innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).setInt(IDP_SPECIALITEM_KEY, type.getId());
    }

    /**
     * Clears the special item from this item
     */
    public void clearSpecialItem() {
        if (!hasSpecialItem()) {
            return;
        }

        clearItemname();
        setLore(null);
        innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).remove(IDP_SPECIALITEM_KEY);
    }

    /**
     * Gets the effect that are on this item.
     * @return
     */
    public SpecialItemType getSpecialItem() {
        if (!hasSpecialItem()) {
            return null;
        }

        // Get the id
        return SpecialItemType.fromId(innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).getInt(IDP_SPECIALITEM_KEY));
    }

    /**
     * This checks if the item has any item effects.
     * @return
     */
    public boolean hasSpecialItem() {
        if (innCompound == null || !innCompound.hasKey(INNECTIS_COMPOUND_TAGNAME)) {
            return false;
        }

        return innCompound.getCompound(INNECTIS_COMPOUND_TAGNAME).hasKey(IDP_SPECIALITEM_KEY);
    }

    /**
     * Gets the innermost compound from the key path
     * @param keys the key path
     * @return
     */
    private NBTTagCompound getInnermostCompound(String[] keys) {
        NBTTagCompound compound = innCompound;

        for (int i = 0; i < keys.length - 1; i++) {
            if (compound.hasKey(keys[i])) {
                compound = compound.getCompound(keys[i]);
            } else {
                return null;
            }
        }

        return compound;
    }

    /**
     * Creates a compound path from the specified key path
     * @param keyPath The key path
     * @return The innermost compound from the path
     */
    private NBTTagCompound createCompoundPath(String[] keyPath) {
        NBTTagCompound compound = innCompound;

        for (int i = 0; i < keyPath.length - 1; i++) {
            if (!compound.hasKey(keyPath[i])) {
                NBTTagCompound newCompound = new NBTTagCompound();
                compound.set(keyPath[i], newCompound);
                compound = newCompound;
            } else {
                compound = compound.getCompound(keyPath[i]);
            }
        }

        return compound;
    }

}

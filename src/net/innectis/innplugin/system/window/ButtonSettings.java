package net.innectis.innplugin.system.window;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;

/**
 * A class that controls a number of button settings
 * for a window
 *
 * @author AlphaBlend
 */
public class ButtonSettings {

    private int slot;
    private ButtonType type;
    private IdpMaterial material;
    private PlayerGroup minGroup;
    private int windowId;
    private String title;
    private String[] description;

    public ButtonSettings(int slot, ButtonType type, String title, String... description) {
        this(slot, PlayerGroup.GUEST, type, type.getMaterial(), title, description);
    }

    public ButtonSettings(int slot, PlayerGroup minGroup, ButtonType type, String title, String... description) {
        this(slot, minGroup, type, type.getMaterial(), title, description);
    }

    public ButtonSettings(int slot, ButtonType type, IdpMaterial material, String title, String... description) {
        this(slot, type, material, -1, title, description);
    }

    public ButtonSettings(int slot, PlayerGroup minGroup, ButtonType type, IdpMaterial material, String title, String... description) {
        this(slot, minGroup, type, material, -1, title, description);
    }

    public ButtonSettings(int slot, ButtonType type, IdpMaterial material, int windowId, String title, String... description) {
        this(slot, PlayerGroup.GUEST, type, material, windowId, title, description);
    }

    public ButtonSettings(int slot, PlayerGroup minGroup, ButtonType type, IdpMaterial material, int windowId, String title, String... description) {
        this.slot = slot;
        this.minGroup = minGroup;
        this.type = type;
        this.material = material;
        this.windowId = windowId;
        this.title = title;
        this.description = description;
    }

    /**
     * Gets the inventory slot number of this button
     * @return
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Checks if the player can see this button
     * @param player
     * @return
     */
    public boolean canSeeButton(IdpPlayer player) {
        return player.getGroup().equalsOrInherits(minGroup);
    }

    /**
     * Gets the minimum player group that can see this button
     * @return
     */
    public PlayerGroup getMinGroup() {
        return minGroup;
    }

    /**
     * Gets the type of this button
     * @return
     */
    public ButtonType getButtonType() {
        return type;
    }

    /**
     * Gets the material represented by this button
     * @return
     */
    public IdpMaterial getMaterial() {
        return material;
    }

    /**
     * Gets the window ID represented by this button
     * @return
     */
    public int getWindowId() {
        return windowId;
    }

    /**
     * Gets the title of this button
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the array of description of this button
     * @return
     */
    public String[] getDescription() {
        return description;
    }

}

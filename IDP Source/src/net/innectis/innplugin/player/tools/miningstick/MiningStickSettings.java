package net.innectis.innplugin.player.tools.miningstick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.innectis.innplugin.system.window.PagedInventory;

/**
 * The settings for the mining stick
 *
 * @author AlphaBlend
 */
public enum MiningStickSettings {

    BLOCK_DROPS(1, "Block Drops", "Toggles whether the blocks broken will drop as items"),
    BREAK_ONLY_SAME_BLOCK(2, "Break Same Block Only", "Toggles whether to only break all similar blocks to the first one");

    private long flagBit;
    private String name;
    private String description;

    private MiningStickSettings(long flagBit, String name, String description) {
        this.flagBit = (long) Math.pow(2L, flagBit - 1);
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the flag bit of this setting
     * @return
     */
    public long getFlagBit() {
        return flagBit;
    }

    /**
     * Gets the name of this setting
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this setting
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the mining stick settings, sorted by name
     * @return
     */
    public static MiningStickSettings[] getSortedSettings() {
        List<MiningStickSettings> settings = new ArrayList<MiningStickSettings>();

        for (MiningStickSettings setting : values()) {
            settings.add(setting);
        }

        Collections.sort(settings, new Comparator<MiningStickSettings>() {
            @Override
            public int compare(MiningStickSettings mss1, MiningStickSettings mss2) {
                return mss1.getName().compareTo(mss2.getName());
            }
        });

        return settings.toArray(new MiningStickSettings[settings.size()]);
    }

}

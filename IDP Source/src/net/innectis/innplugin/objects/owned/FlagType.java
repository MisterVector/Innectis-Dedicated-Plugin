package net.innectis.innplugin.objects.owned;

import net.innectis.innplugin.player.PlayerGroup;

/**
 * @author Hret
 * 
 * Interface for the flags that can be set on InnectisOwnerObjects
 */
public interface FlagType {

    /**
     * Gets the BIT that is used for this flag
     * @return 
     */
    public long getFlagBit();

    /**
     * Returns the name of the flag
     * @return 
     */
    public String getFlagName();

    /**
     * Returns the group that is required for this flag
     */
    public PlayerGroup getRequiredGroup();
    
}

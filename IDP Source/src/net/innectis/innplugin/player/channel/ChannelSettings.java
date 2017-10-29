package net.innectis.innplugin.player.channel;

/**
 * An enum describing the various settings of a channel
 *
 * @author AlphaBlend
 */
public enum ChannelSettings {

    HIDDEN(1);

    private long flags;

    private ChannelSettings(long flags) {
        this.flags = (long) Math.pow(2, flags - 1);
    }

    /**
     * Gets the channel setting bit
     * @return
     */
    public long getBit() {
        return flags;
    }

}

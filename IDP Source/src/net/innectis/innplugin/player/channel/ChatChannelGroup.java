package net.innectis.innplugin.player.channel;

/**
 * An enum specifying user groups of a channel.
 *
 * @author AlphaBlend
 */
public enum ChatChannelGroup {

    OWNER(1),
    OPERATOR(2),
    MEMBER(3),
    NONE(-1);
    //
    private final int id;

    private ChatChannelGroup(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of this user group
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Checks if the group passed in equals or inherits this group
     * @param group
     * @return
     */
    public boolean equalsOrInherits(ChatChannelGroup group) {
        return (this.getId() <= group.getId());
    }

    /**
     * Returns if the specified group equals this group
     * @param group
     * @return
     */
    public boolean equals(ChatChannelGroup group) {
        return (this.getId() == group.getId());
    }

    /**
     * Gets the user group from its id
     * @param id
     * @return
     */
    public static ChatChannelGroup fromId(int id) {
        for (ChatChannelGroup userGroup : values()) {
            if (userGroup.getId() == id) {
                return userGroup;
            }
        }

        return NONE;
    }
    
}

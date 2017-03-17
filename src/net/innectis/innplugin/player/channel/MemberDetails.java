package net.innectis.innplugin.player.channel;

/**
 * A class that represents the details of a member in a channel
 *
 * @author AlphaBlend
 */
public class MemberDetails {

    private int personalNumber;
    private ChatChannelGroup group;
    private boolean online = false;

    public MemberDetails(int personalNumber, ChatChannelGroup group) {
        this.personalNumber = personalNumber;
        this.group = group;
    }

    /**
     * Gets the personal id representing the channel member in
     * this channel
     * @return
     */
    public int getPersonalNumber() {
        return personalNumber;
    }

    /**
     * Sets the personal id representing the channel member in
     * this channel
     * @param personalNumber
     */
    public void setPersonalNumber(int personalNumber) {
        this.personalNumber = personalNumber;
    }

    /**
     * Sets the channel group to the username this object represents
     * @param group
     */
    public void setGroup(ChatChannelGroup group) {
        this.group = group;
    }

    /**
     * Gets the channel group representing the channel member in
     * this channel
     * @return
     */
    public ChatChannelGroup getGroup() {
        return group;
    }

    /**
     * Sets if this user is online or not
     * @param online
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Gets if this user is online or not
     * @return
     */
    public boolean isOnline() {
        return online;
    }

}

package net.innectis.innplugin.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.util.DateUtil;

/**
 * An abstract class that defines a basic message
 *
 * @author AlphaBlend
 */
public abstract class Message {

    protected int id;
    protected Date date;
    protected boolean read;
    protected PlayerCredentials creatorCredentials;
    protected String message;

    public Message(int id, Date date, boolean read, PlayerCredentials creatorCredentials, String message) {
        this.id = id;
        this.date = date;
        this.read = read;
        this.creatorCredentials = creatorCredentials;
        this.message = message;
    }

    /**
     * Gets the ID of this message
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Gets this message
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the date this message was created
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the date in the format MMMM dd, yyyy
     * @return
     */
    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);
        return sdf.format(date);
    }

    /**
     * Sets whether or not this message has been read
     * @param read
     */
    public void setRead(Boolean read) {
        this.read = read;
    }

    /**
     * Gets whether or not this message has been read
     * @return
     */
    public boolean hasRead() {
        return read;
    }

    public PlayerCredentials getCreatorCredentials() {
        return creatorCredentials;
    }

    /**
     * Gets the creator of the message
     * @return
     */
    public String getCreator() {
        return creatorCredentials.getName();
    }

    /**
     * Saves this message
     */
    public abstract void save();

    /**
     * Deletes this message
     */
    public abstract void delete();

}

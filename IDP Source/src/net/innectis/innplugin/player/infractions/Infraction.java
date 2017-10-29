package net.innectis.innplugin.player.infractions;

import java.util.Date;
import net.innectis.innplugin.player.PlayerCredentials;

/**
 *
 * @author Hret
 *
 * A POJO containing the information about an infraction.
 */
public final class Infraction {

    protected static final int DEFAULT_ID = -1;
    private int id;
    private final PlayerCredentials playerCredentials;
    private final InfractionIntensity intensity;
    private final String summary;
    private final String details;
    private final Date dateGMT;
    private final PlayerCredentials creatorCredentials;
    private final boolean revoked;
    private final PlayerCredentials revokerCredentials;
    private final Date revokeDate;

    /**
     * Creates a new infraction with the given details.
     * @param credentials
     * The credentials of the player this infraction is given to
     * @param intensity
     * The intensity of the infraction
     * @param summary
     * A small one-line summary of the infraction
     * @param details
     * Optional list of details about the infraction and further information about the context.
     * @param dateGMT
     * The date in GMT when the infraction was created
     * @param creatorCredentials
     * The creatpr of the infraction or <b>[SERVER]</b> for server generated infractions.
     */
    public Infraction(PlayerCredentials credentials, InfractionIntensity intensity, String summary, String details, Date dateGMT, PlayerCredentials creatorCredentials) {
        this.id = DEFAULT_ID;
        this.playerCredentials = credentials;
        this.intensity = intensity;
        this.summary = summary;
        this.details = details;
        this.dateGMT = dateGMT;
        this.creatorCredentials = creatorCredentials;
        this.revoked = false;
        this.revokerCredentials = null;
        this.revokeDate = null;
    }

    /**
     * Created a new infraction with all details possible to be given.
     * @param id
     * @param credentials
     * @param intensity
     * @param summary
     * @param details
     * @param dateGMT
     * @param creatorCredentials
     * @param revoked
     * @param revokerCredentials
     * @param revokeDate
     */
    protected Infraction(int id, PlayerCredentials credentials, InfractionIntensity intensity, String summary, String details, Date dateGMT, PlayerCredentials creatorCredentials, boolean revoked, PlayerCredentials revokerCredentials, Date revokeDate) {
        this.id = id;
        this.playerCredentials = credentials;
        this.intensity = intensity;
        this.summary = summary;
        this.details = details;
        this.dateGMT = dateGMT;
        this.creatorCredentials = creatorCredentials;
        this.revoked = revoked;
        this.revokerCredentials = revokerCredentials;
        this.revokeDate = revokeDate;
    }

    /**
     * The ID of an infraction.
     * If the infraction is not listed in the database the <b>DEFAULT_ID</b> will be used.
     * @return id of infraction
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the infraction
     * @param id
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * The credentials of the player to whom the infraction is listed to.
     * @return
     */
    public PlayerCredentials getPlayerCredentials() {
        return playerCredentials;
    }

    /**
     * Optionally extra details about the infraction, this can be empty.
     * @return
     */
    public String getDetails() {
        return details;
    }

    /**
     * The intesity of the infraction.
     * @return
     */
    public InfractionIntensity getIntensity() {
        return intensity;
    }

    /**
     * The credentials of the creator that did the infraction.
     * @return
     */
    public PlayerCredentials getCreatorCredentials() {
        return creatorCredentials;
    }

    /**
     * A summary of the infraction
     * @return
     */
    public String getSummary() {
        return summary;
    }

    /**
     * The date the infraction was created.
     * @return
     */
    public Date getDateGMT() {
        return dateGMT;
    }

    /**
     * The date that the infraction has been revoked.
     * @return
     */
    public Date getRevokeDate() {
        return revokeDate;
    }

    /**
     * The credentials of the player that has revoked the infraction
     * @return
     */
    public PlayerCredentials getRevokerCredentials() {
        return revokerCredentials;
    }

    /**
     * Boolean value that shows if the infraction has been revoked.
     * @return
     */
    public boolean isRevoked() {
        return revoked;
    }
    
}

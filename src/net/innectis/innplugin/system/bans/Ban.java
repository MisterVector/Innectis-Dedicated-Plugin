package net.innectis.innplugin.system.bans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import net.innectis.innplugin.system.bans.BanHandler.BanType;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.PlayerUtil;

/**
 *
 * @author AlphaBlend
 *
 * Abstract class that manages ban times for a ban
 */
public abstract class Ban {

    private int id;
    private PlayerCredentials bannedByCredentials;
    private Timestamp banStartTime;
    private long durationTicks;
    private boolean joinBan;

    public Ban(int id, PlayerCredentials bannedByCredentials, Timestamp banStartTime, long durationTicks, boolean joinBan) {
        this.id = id;
        this.bannedByCredentials = bannedByCredentials;
        this.banStartTime = banStartTime;
        this.durationTicks = durationTicks;
        this.joinBan = joinBan;
    }

    /**
     * Modifies this ban object from an existing one
     * @param ban
     */
    public void setAttributesFrom(Ban ban) {
        setBannedBy(ban.getBannedByCredentials());
        setBanStartTime(ban.getBanStartTime(), ban.getDurationTicks());
        setJoinBan(ban.isJoinBan());
    }

    /**
     * Sets the ID of this ban
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the ID associated with this ban
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the credentials of the player that made this ban
     * @return
     */
    public PlayerCredentials getBannedByCredentials() {
        return bannedByCredentials;
    }

    /**
     * Specifies whether the specified sender may modify this ban. It can
     * either be a player or the console
     * @param sender
     * @return
     */
    public boolean canModifyBan(IdpCommandSender sender) {
        return (bannedByCredentials.getName().equalsIgnoreCase(sender.getName())
                || sender.hasPermission(Permission.special_modifyban_otherowner));
    }

    /**
     * Sets the credentials of the player that made this ban
     * @param bannedBy
     */
    public void setBannedBy(PlayerCredentials bannedBy) {
        this.bannedByCredentials = bannedBy;
    }

    /**
     * Gets the time the ban was created
     * @return
     */
    public Timestamp getBanStartTime() {
        return banStartTime;
    }

    /**
     * Sets the new start time and duration of this ban
     * @param banStartTime
     * @param durationTicks
     */
    public void setBanStartTime(Timestamp banStartTime, long durationTicks) {
        this.banStartTime = banStartTime;
        this.durationTicks = durationTicks;
    }

    /**
     * Sets the ban start time as the current time and adds the
     * specified duration to it
     * @param durationTicks
     */
    public void setNewBanStartTime(long durationTicks) {
        this.banStartTime = new Timestamp(System.currentTimeMillis());
        this.durationTicks = durationTicks;
    }

    /**
     * Gets the timestamp of when this ban will expire
     * @return
     */
    public Timestamp getExpireTime() {
        if (joinBan) {
            return (new Timestamp(System.currentTimeMillis() + durationTicks));
        }

        return new Timestamp(banStartTime.getTime() + durationTicks);
    }

    /**
     * Gets the remaining expire time as a time string
     * @param longversion
     * @return
     */
    public String getUnbanTimeString(boolean longversion) {
        return (DateUtil.getTimeString((joinBan ? getDurationTicks() : getRemainingDurationTicks()), longversion));
    }

    /**
     * Gets if this ban is an indefinite ban
     * @return
     */
    public boolean isIndefiniteBan() {
        return (durationTicks == 0);
    }

    /**
     * Gets if this ban will take effect on the player's next join
     * @return
     */
    public boolean isJoinBan() {
        return joinBan;
    }

    /**
     * Sets whether or not this ban will expire when a user of this ban joins
     * @param joinBan
     */
    public void setJoinBan(boolean joinBan) {
        this.joinBan = joinBan;
    }

    /**
     * Gets the length in ticks this ban is set for
     * @return
     */
    public long getDurationTicks() {
        return durationTicks;
    }

    public long getRemainingDurationTicks() {
        return (banStartTime.getTime() + durationTicks) - System.currentTimeMillis();
    }

    /**
     * Checks to see if this ban expired
     * @return
     */
    public boolean isExpired() {
        return (!isIndefiniteBan() && System.currentTimeMillis() > (banStartTime.getTime() + durationTicks));
    }

    /**
     * Checks if this ban was made automatically
     * @return
     */
    public boolean isAutomatic() {
        return (bannedByCredentials == Configuration.AUTOMATIC_CREDENTIALS);
    }

    /**
     * Provides basic info about this ban, such as ban creator
     * start date, duration, and whether or not it is a join ban
     * @return
     */
    protected String getBasicDetails() {
        String coloredBannedBy = null;

        if (isAutomatic()) {
            coloredBannedBy = ChatColor.YELLOW + "Automatic Ban";
        } else {
            coloredBannedBy = PlayerUtil.getColoredName(getBannedByCredentials());
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);
        String startDate = sdf.format(getBanStartTime());
        String durationString = null;

        if (isExpired()) {
            durationString = ChatColor.GRAY + "expired";
        } else if (isIndefiniteBan()) {
            durationString = ChatColor.RED + "indefinite";
        } else {
            durationString = DateUtil.getTimeString(getDurationTicks(), false);
        }

        String details = coloredBannedBy + ChatColor.WHITE + ", " + ChatColor.YELLOW
                + startDate + ChatColor.WHITE + ", " + durationString;

        if (isJoinBan()) {
            details += ChatColor.WHITE + " (" + ChatColor.YELLOW + "JB"
                    + ChatColor.WHITE + ")";
        }

        return details;
    }

    /**
     * Gets the state of this ban
     * @return
     */
    public abstract BanState getBanState();

    /**
     * Indicates what type of ban this is
     */
    public abstract BanType getType();

    /**
     * Returns an array of strings indicating the
     * details of this ban
     */
    public abstract String[] getBanDetails();

    /**
     * Saves this ban object (implementation is object specific)
     */
    public abstract void save();

    /**
     * Deletes this ban object from the database (implementation is object specific)
     */
    public abstract void delete();

}
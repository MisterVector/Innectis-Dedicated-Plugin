package net.innectis.innplugin.objects.pojo;

import java.util.Date;

/**
 *
 * @author Hret
 *
 * Chestlog POJO object.
 * This corresponds to the chestlog table
 */
public class ChestLog {

    private Integer logid;
    private int chestid;
    private String username;
    private Date date;

    public ChestLog() {
    }

    public ChestLog(int chestid, String username, Date date) {
        this.chestid = chestid;
        this.username = username;
        this.date = date;
    }

    public Integer getLogid() {
        return logid;
    }

    public int getChestid() {
        return chestid;
    }

    public void setChestid(int chestid) {
        this.chestid = chestid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}

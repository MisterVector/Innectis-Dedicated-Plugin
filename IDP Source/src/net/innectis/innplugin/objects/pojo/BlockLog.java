package net.innectis.innplugin.objects.pojo;

import java.util.Date;
import net.innectis.innplugin.loggers.BlockLogger.BlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 *
 * Chestlog POJO object.
 * This corresponds to the block_log table
 *
 * Note: Id, Data, DateTime & ActionType fields have incorrect case!
 * You need to use 'as' keywords when parsing!
 */
public class BlockLog {

    private Long logid;
    private String name;
    private int locx;
    private int locy;
    private int locz;
    private String world;
    private int id;
    private int data;
    private Date datetime;
    private int actiontype;

    public BlockLog() {
    }

    public BlockLog(String name, int locx, int locy, int locz, String world, int id, int data, Date datetime, int actiontype) {
        this.name = name;
        this.locx = locx;
        this.locy = locy;
        this.locz = locz;
        this.world = world;
        this.id = id;
        this.data = data;
        this.datetime = datetime;
        this.actiontype = actiontype;
    }

    public int getActiontype() {
        return actiontype;
    }

    public void setActiontype(int actiontype) {
        this.actiontype = actiontype;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocX() {
        return locx;
    }

    public void setLocX(int locx) {
        this.locx = locx;
    }

    public int getLocY() {
        return locy;
    }

    public void setLocY(int locy) {
        this.locy = locy;
    }

    public int getLocZ() {
        return locz;
    }

    public void setLocZ(int locz) {
        this.locz = locz;
    }

    public Long getLogid() {
        return logid;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public String getWorldName() {
        return world;
    }

    public void setWorldName(String world) {
        this.world = world;
    }

    public Vector getVector() {
        return new Vector(getLocX(), getLocY(), getLocZ());
    }

    public void setLocation(Vector vector) {
        setLocX(vector.getBlockX());
        setLocY(vector.getBlockY());
        setLocZ(vector.getBlockZ());
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public void setWorld(World world) {
        setWorldName(world.getName());
    }

    public Location getLocation() {
        Vector vec = getVector();
        return new Location(getWorld(), vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    public BlockAction getAction() {
        return BlockAction.getAction(getActiontype());
    }
    
}

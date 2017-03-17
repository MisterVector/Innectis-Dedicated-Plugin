package net.innectis.innplugin.system.game.games;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.system.game.GameTimer;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * A point that can be captured in a domination match
 */
public class CapturePoint implements GameTimer {

    private String pointname;
    private IdpDomination game;
    private IdpWorldRegion region;
    private Location capturepoint;
    private int capturedistance;
    private int captureintervals;
    private int interval = 0;
    private int ownerteam = -1;
    private int captureingteam = -1;
    private boolean isFlashing = false;
    private boolean bool = true;

    /**
     * Creates a new capture point
     * @param game
     * The game that belongs to this point
     * @param capturepoint
     * The location of the point
     * @param capturedistance
     * The distance (for X and Z) in each direction that should activate the capturing
     * @param captureintervals
     * The amount of intervals needed to capture it.
     * <p/>
     * Note: the amount of intervals given is (1 * amount of players). <br/>
     * So 4 players of the same team, get 4 'intervals' each real interval.
     */
    public CapturePoint(IdpDomination game, String pointname, Location capturepoint, int capturedistance, int captureintervals) {
        this.game = game;
        this.pointname = pointname;
        this.capturepoint = capturepoint;
        this.captureintervals = captureintervals;
        this.capturedistance = capturedistance;

        World world = capturepoint.getWorld();
        int captureX = capturepoint.getBlockX();
        int captureY = capturepoint.getBlockY();
        int captureZ = capturepoint.getBlockZ();

        Vector vec1 = new Vector(captureX - capturedistance, captureY - capturedistance, captureZ - capturedistance);
        Vector vec2 = new Vector(captureX + capturedistance, captureY + capturedistance, captureZ + capturedistance);

        region = new IdpWorldRegion(world, vec1, vec2);
    }

    /**
     * The location of the capture point
     * @return
     */
    public Location getCapturepoint() {
        return capturepoint;
    }

    /**
     * The block of the capture point
     * @return
     */
    public Block getCaptureBlock() {
        return capturepoint.getBlock();
    }

    /**
     * The ID of the team that captured/owns the point
     * @return
     */
    public int getOwnerId() {
        return ownerteam;
    }

    /**
     * The ID of the team that captured/owns the point
     * @return
     */
    public void setOwnerId(int newOwnerTeam) {
        ownerteam = newOwnerTeam;
    }

    /**
     * The team that is currently capturing this point
     * @return
     */
    public int getCaptureingteam() {
        return captureingteam;
    }

    /**
     * The name of the point
     * @return
     */
    public String getPointName() {
        return pointname;
    }

    /**
     * Checks if the point is captured
     * @return
     */
    public boolean isCaptured() {
        return ownerteam != -1;
    }

    /**
     * This will handle an interval for capturing the locations
     */
    public void onInterval() {
        bool = !bool;
        flash();
        if (bool) {
            return;
        }

        List<Integer> teamsNearPoint = new ArrayList<Integer>(2);
        int playersNearPoint = 0;

        for (IdpPlayer player : region.getPlayersInsideRegion(0)) {
            int teamid = game.getTeamId(player);
            if (teamid != -1) {
                // Made the capturedistance a circle not a square.
                if (player.getLocation().distance(capturepoint) <= capturedistance) {
                    if (!teamsNearPoint.contains(teamid)) {
                        teamsNearPoint.add(teamid);
                    }
                    playersNearPoint++;
                }
            }
        }

        // Reset when no teams
        if (teamsNearPoint.isEmpty()) {
            interval = 0;
            captureingteam = -1;
            return;
        }

        // Only lock if multiple teams
        if (teamsNearPoint.size() > 1) {
            startFlash();
            return;
        }

        // Get the first (and only) team in the list
        int teamid = teamsNearPoint.get(0);

        // Check if its a different team then the owner of the point
        if (teamid != ownerteam) {
            // Check if its the same team as the capturing team
            if (teamid == captureingteam) {
                interval += playersNearPoint;

                if (interval >= captureintervals) {
                    if (isCaptured()) {

                        interval = 0;
                        ownerteam = -1;

                        game.changePoint(this, teamid);
                    } else {
                        captureingteam = -1;

                        interval = 0;
                        ownerteam = teamid;

                        game.changePoint(this, teamid);
                    }
                } else {
                    startFlash();
                }
            } else {
                // Different team started capturing it, reset interval
                interval = 0;
                captureingteam = teamid;
                startFlash();
                return;
            }
        } else {
            // Only team inside is the poitn owner, so reset
            interval = 0;
            captureingteam = -1;
        }
    }

    /**
     * The ID of the game this point belongs to.
     * @return
     */
    @Override
    public int getId() {
        return game.getId();
    }

    /**
     * Turns on the flash for 1 turn
     */
    public void startFlash() {
        isFlashing = true;
    }

    /**
     * Flashes to the material of the capturing team if captured otherwise to the no team material.
     * When set back, it will disable the flashing
     */
    public void flash() {
        if (isFlashing) {
            //Lets make it flash!
            Block captureblock = getCaptureBlock();
            IdpMaterial mat = IdpMaterial.fromBlock(captureblock);
            IdpMaterial ownermaterial = game.getTeamMaterial(ownerteam);

            if (mat == ownermaterial) {
                IdpMaterial newMaterial = game.getTeamMaterial(isCaptured() ? -1 : captureingteam);
                BlockHandler.setBlock(captureblock, newMaterial);
            } else {
                BlockHandler.setBlock(captureblock, ownermaterial);
                isFlashing = false;
            }
        }
    }

}

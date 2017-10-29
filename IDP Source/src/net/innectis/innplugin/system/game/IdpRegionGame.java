package net.innectis.innplugin.system.game;

import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.ISecondairyListener;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.RegionSnapshot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class IdpRegionGame implements IdpGame, ISecondairyListener {

    private int id;
    private UUID listenerId;
    private final IdpWorldRegion region;
    private RegionSnapshot snapshot;
    private String gamehost;

    public IdpRegionGame(IdpWorldRegion region, String owner) {
        this.id = -1;
        this.gamehost = owner;
        this.region = region;
    }

    @Override
    public void initializeGame() {
        listenerId = InnPlugin.getPlugin().getListenerManager().registerSecListener(this);
    }

    @Override
    public String getName() {
        return this.getGameType().getName() + " #" + this.getId();
    }

    @Override
    public void endGame() {
        InnPlugin.getPlugin().getListenerManager().removeSecListener(listenerId);
        IdpGameManager.getInstance().removeGame(id);
    }

    /**
     * This will make a snapshot of the gameregion that can be restored later.
     */
    protected void saveRegion() {
        snapshot = region.getSnapshot();
    }

    /**
     * Restore the region of the game back to the last snapshot.
     */
    protected void restoreRegion() {
        if (snapshot != null) {
            snapshot.restore();
        }
    }

    /**
     * The region where this game is located in.
     * @return
     */
    public IdpWorldRegion getRegion() {
        return region;
    }

    /**
     * Parses information to the game, and prints error/true to sender.
     * @param sender
     * @param parse
     */
    @Override
    public void parse(IdpCommandSender sender, String parse) {
        sender.printError("Unknown Parse!");
    }

    /**
     * Prints all possible parse information to the sender.
     * @param sender
     */
    @Override
    public void printParse(IdpCommandSender sender) {
        sender.printError("This game has no parses!");
    }

    /**
     * Attempts to add the player to the game.
     * @return
     */
    @Override
    public boolean addPlayer(IdpPlayer player) {
        return false;
    }

    /**
     * Will return a list of all players inside the region of the game.
     * @return
     */
    protected List<IdpPlayer> getPlayersInsideRegion() {
        return getRegion().getPlayersInsideRegion(0);
    }

    /**
     * Sets the ID of this game.
     * @param id
     * The new ID of the game.
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Will return the ID of this game.
     * @return the ID, or -1 if there is no ID
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     * The name of the player that acts as the game host.
     * @return
     */
    @Override
    public String getGameHost() {
        return gamehost;
    }

    /**
     * Sets the name of the game host
     * @param gamehost
     */
    protected void setGamehost(String gamehost) {
        this.gamehost = gamehost;
    }

    /**
     * The IdpGameType of this game.
     * @return
     */
    @Override
    public abstract IdpGameType getGameType();

    /**
     * Checks if the player is in the game
     * @param player
     * @return
     */
    public boolean isInGame(IdpPlayer player) {
        for (IdpPlayer p : getPlayers()) {
            if (p.equals(player)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This will check if the given location is within the boundaries of the IdpRegionGame.
     * If the given game is not a region game, false will be returned
     * @param location
     * @return
     */
    @Override
    public boolean isInGame(Location location) {
        return getRegion().contains(location);
    }

    /**
     * Checks if the entity has the IdpGame metakey.
     * @see IdpGameManager.ENTITY_METADATAKEY
     * @param entity
     * @return
     */
    @Override
    public boolean isInGame(Entity entity) {
        if (entity.hasMetadata(IdpGameManager.ENTITY_METADATAKEY)) {
            try {
                return entity.getMetadata(IdpGameManager.ENTITY_METADATAKEY).get(0).asInt() == getId();
            } catch (NullPointerException npe) {
                // Ignore it...
            }
        }
        return false;
    }

}

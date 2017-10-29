package net.innectis.innplugin.system.game;

import java.util.List;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 *
 * @author Hret
 *
 * This is the default interface for a game that can be started with the IDP.
 */
public interface IdpGame {

    /**
     * The ID of the game.
     * @return
     */
    int getId();

    /**
     * Sets the ID of the game
     * @param id
     */
    void setId(int id);

    /**
     * Initializes the game
     */
    void initializeGame();

    /**
     * The name of the player that acts as the host.
     * @return
     */
    String getGameHost();

    /**
     * The gametype of this game.
     * @return
     */
    IdpGameType getGameType();

    /**
     * Checks if the game can be started.
     * @return
     */
    IdpStartResult canStart();

    /**
     * Prints the game score to the given player
     * @param player
     */
    void printScore(IdpCommandSender player);

    /**
     * Parses information to the game, and prints error/true to sender.
     * @param sender
     * @param parse
     */
    public void parse(IdpCommandSender sender, String parse);

    /**
     * Prints all possible parse information to the sender.
     * @param sender
     */
    public void printParse(IdpCommandSender sender);

    /**
     * Attempts to add the player to the game.
     * @return
     */
    boolean addPlayer(IdpPlayer player);

    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Ends the game.
     */
    void endGame();

    /**
     * Checks if the entity has the IdpGame metakey.
     * @see IdpGameManager.ENTITY_METADATAKEY
     * @param entity
     * @return
     */
    boolean isInGame(Entity entity);

    /**
     * This checks if the given player is in the game
     * @param player
     * @return
     */
    boolean isInGame(IdpPlayer player);

    /**
     * This will check if the given location is within the boundaries of the IdpRegionGame.
     * If the given game is not a region game, false will be returned
     * @param location
     * @return
     */
    boolean isInGame(Location location);

    /**
     * Gets a list of all players in the game
     * @return
     */
    public List<IdpPlayer> getPlayers();

}

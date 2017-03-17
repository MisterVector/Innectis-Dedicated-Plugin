package net.innectis.innplugin.system.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;

/**
 *
 * @author Hret
 *
 * Handler for different games that can be started.
 */
public final class IdpGameManager {

    /**
     * Constant used to get the metadata key where idpgames are stored
     * that need to be tracked by entities
     */
    public static String ENTITY_METADATAKEY = "IDPGAME";
    /**
     * Synclock for changing the _instance variable.
     */
    private static final Object _synclock = new Object();
    private static IdpGameManager _instance;

    /**
     * This will return the active instance of the game manager.
     *
     * @return
     */
    public static IdpGameManager getInstance() {
        synchronized (_synclock) {
            if (_instance == null) {
                _instance = new IdpGameManager();
            }

            return _instance;
        }
    }
    /**
     * Synclock for games.
     */
    private static final Object _gameslock = new Object();
    /**
     * Map with the active games mapped to their ID's
     */
    private Map<Integer, IdpGame> games;
    /**
     * Counter that will count the games, and supply new games with a valid id.
     */
    private int gameCounter;

    private IdpGameManager() {
        games = new HashMap<Integer, IdpGame>(5);
        gameCounter = 0;
    }

    /**
     * Ends all games that are currently in progress
     */
    public void endAllGames() {
        Set<IdpGame> gameobjects;

        // Get the current games and reset the games map
        synchronized (_gameslock) {
            gameobjects = new HashSet<IdpGame>(this.games.values());
            games = new HashMap<Integer, IdpGame>(0);
        }

        // Call endgame for all (still active) games
        for (IdpGame game : gameobjects) {
            game.endGame();
        }
    }

    /**
     * Adds a new game to the manager.
     *
     * @param game
     * @return
     */
    public int addGame(IdpGame game) {
        synchronized (_gameslock) {
            game.setId(++gameCounter);
            games.put(game.getId(), game);
            return game.getId();
        }
    }

    /**
     * Removes the game with the given ID.
     *
     * @param id
     * @return
     */
    public IdpGame removeGame(int id) {
        synchronized (_gameslock) {
            return games.remove(id);
        }
    }

    /**
     * Will look for the game the player is in. If the player is not in a game,
     * null will be returned.
     *
     * @param player
     * @return
     */
    public IdpGame getGame(IdpPlayer player) {
        for (IdpGame game : games.values()) {
            if (game.isInGame(player)) {
                return game;
            }
        }

        return null;
    }

    /**
     * Checks if the player is in a game
     * @param player
     * @return
     */
    public boolean isInGame(IdpPlayer player) {
        return getGame(player) != null;
    }

    /**
     * Will return the game with the given id. If the ID is not found, null will
     * be returned.
     *
     * @param id
     * @return
     */
    public IdpGame getGame(int id) {
        return games.get(id);
    }

    /**
     * Gets a game by the location (only applies to region games)
     * @param loc
     * @return
     */
    public IdpGame getGame(Location loc) {
        for (IdpGame game : games.values()) {
            if (game.isInGame(loc)) {
                return game;
            }
        }

        return null;
    }

    /**
     * Returns an unmodifiable map of all the games
     * @return
     */
    public Map<Integer, IdpGame> getGames() {
        return Collections.unmodifiableMap(games);
    }

}

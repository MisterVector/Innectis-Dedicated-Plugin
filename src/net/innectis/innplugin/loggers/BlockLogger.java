package net.innectis.innplugin.loggers;

import java.util.Date;
import java.util.UUID;
import net.innectis.innplugin.items.IdpMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 *
 * @author Hret
 */
public class BlockLogger extends IdpDBLogger implements Logger {

    BlockLogger() {
        super("block_log", "player_id", "locx", "locy", "locz", "world", "Id", "Data", "DateTime", "ActionType");
    }

    /**
     * Possible actions that can be logged into the database.
     */
    public enum BlockAction {

        BLOCK_BREAK(0),
        BLOCK_PLACE(1),
        WE_ACTION(2),
        ENTITY_PLACE(3),
        ENTITY_BREAK(4),
        /** Special actions (or system actions, typeid > 100) */
        /** Special/system action! */
        REVERTION(100),
        /** Only use when the action is unknown */
        UNKNOWN(-1);
        private final int typeid;

        private BlockAction(int typeid) {
            this.typeid = typeid;
        }

        /**
         * Returns the actiontypeid
         * @return
         */
        public int getTypeid() {
            return typeid;
        }

        /**
         * Looks for the blocksaction with the given id
         */
        public static BlockAction getAction(int typeid){
            for (BlockAction action : values()) {
                if (action.getTypeid() == typeid) {
                    return action;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Log an blockaction.
     * @param username - The name of the user that done the action.
     * @param block <br/>
     * For the <b>BLOCK_BREAK</b> action the block is the block before it got broken.
     * For the <b>BLOCK_PLACE</b> action the block is the block after it got placed.
     * For the <b>WE_ACTION</b> action its the block before the command.
     * For the <b>REVERTION</b> action the block is the block after it got reverted.
     * @param action - The action that was done
     */
    public void logBlockAction(UUID playerId, IdpMaterial mat, Location loc, BlockAction action) {
        int id = mat.getId();
        byte dat = (byte) mat.getData();

        addToCache(
                playerId.toString(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ(),
                loc.getWorld().getName(),
                id,
                dat,
                new Date(),
                action.getTypeid());
    }

    /**
     * Log an blockaction.
     * @param playerId - The ID of the player that did the action
     * @param entity - the entity interacted with
     * @param action - The action that was done
     */
    public void logEntityAction(UUID playerId, Entity entity, BlockAction action) {
        IdpMaterial mat = null;

        switch (entity.getType()) {
            case ITEM_FRAME:
                mat = IdpMaterial.ITEM_FRAME;
                break;
            case PAINTING:
                mat = IdpMaterial.PAINTING;
                break;
        }

        Location loc = entity.getLocation();

        addToCache(
                playerId.toString(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ(),
                loc.getWorld().getName(),
                (mat != null ? mat.getId() : 999),
                0,
                new Date(),
                action.getTypeid());
    }

}
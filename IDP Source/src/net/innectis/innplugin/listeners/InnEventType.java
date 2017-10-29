package net.innectis.innplugin.listeners;

/**
 *
 * @author Nosliw
 *
 * The event types that are supported by the secondairy listener.
 */
public enum InnEventType {

    /** <b>NOTE: This event is ignored in the secondairy listener<b/> */
    NONE,                         // Default, this should never be used.
    //
    PLAYER_BLOCK_BREAK,           // Called when a player breaks a block.
    PLAYER_BLOCK_PLACE,           // Called when a player places a block.
    PLAYER_DEATH,                 // Called when a player dies
    PLAYER_DAMAGE_BY_PLAYER,      // Called when a player is hit by a different player
    PLAYER_DAMAGE_BY_PROJECTILE,  // Called when a player is hit by a projectile
    PLAYER_FOOD_LEVEL_CHANGE,     // Called when a player's food level changes
    PLAYER_INTERACT,              // Called when a player interacts with a block
    PLAYER_INTERACT_ENTITY,       // Called when a player interacts with another entity
    PLAYER_INVENTORY_CLICK,       // Called when a player clicks in its inventory
    PLAYER_MOVE,                  // Called when a player moves
    PLAYER_QUIT,                  // Called when a player leaves the server. (they disconnect)
    PLAYER_TELEPORT,              // Called when a player teleports.
    PLAYER_LOT_LEAVE,             // Called when a player leaves a lot.
    PLAYER_RESPAWN,               // Called when a player that is dead hits the respawn button.
    PLAYER_POST_RESPAWN,          // Called when a player has just respawned.
    PLAYER_DROP_ITEM,             // Called when a player drops an item.
    PLAYER_PICKUP_ITEM,           // Called when a player picks up an item.
    PLAYER_CHAT,                  // Called when a player types a message.
    PROJECTILE_HIT,               // Called when a projectile hits something
    HANGING_BREAK,                // Called if a hanging item (itemfram, painting) breaks
    ENTITY_SPAWN,                 // Called when an entity spawns
    ENTITY_ENVIRONMENTAL_DAMAGE;  // Called when an entity undergoes environmental damage

}

package net.innectis.innplugin.system.game;

import net.innectis.innplugin.player.Permission;

/**
 *
 * @author Hret
 */
public enum IdpGameType {

    CTF(1, "Capture the Flag", Permission.game_start_ctf, "ctf"),
    DOMINATION(2, "Domination", Permission.game_start_dom, "domination"),
    QUAKECRAFT(3, "QuakeCraft", Permission.game_start_quake, "quakecraft"),
    TRON(4, "Tron", Permission.game_start_tron, "tron"),
    HUNGERGAMES(5, "Hunger Games", Permission.game_start_hungergames, "hungergames"),
    ENDERGOLF(6, "Ender Golf", Permission.game_start_endergolf, "endergolf");
    //
    private final int id;
    private final Permission permission;
    private final String name;
    private final String[] triggers;

    private IdpGameType(int id, String name, Permission permission, String... triggers) {
        this.id = id;
        this.permission = permission;
        this.name = name;
        this.triggers = triggers;
    }

    /**
     * An lookup ID for this game type
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the permission required to start this game.
     * @return
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * The user friendly name for this game type
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * A list of triggers that will return this value when the getGameType method is used.
     * @return
     */
    public String[] getTriggers() {
        return triggers;
    }

    /**
     * Checks if the given trigger matches one of the type's triggers.
     * <p/>
     * This check ignores casing.
     * @param trigger
     * @return
     */
    public boolean isMatch(String trigger) {
        for (String str : getTriggers()) {
            if (str.equalsIgnoreCase(trigger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks for the given gametype with the given trigger.
     * @param trigger
     * @return
     */
    public static IdpGameType fromTrigger(String trigger) {
        for (IdpGameType type : values()) {
            if (type.isMatch(trigger)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Looks for the given gametype with the given id.
     * @param trigger
     * @return
     */
    public static IdpGameType fromId(int id) {
        for (IdpGameType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

}

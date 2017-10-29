package net.innectis.innplugin.system.signs;

/**
 *
 * @author Hret
 */
public enum WallSignType {

    ELEVATOR_UP("Elevator up", "up"),
    ELEVATOR_DOWN("Elevator down", "down"),
    CHEST_SHOP("Chest shop", "buy", "sell"),
    STASH_SIGN("Stash sign", "stash"),
    BOUNCE("Bounce Sign", "bounce"),
    KICKER("Kick Sign", "kick"),
    GIVE("Give Sign", "give"),
    BANK("Bank Sign", "bank"),
    NONE("NONE");
    //
    private final String name;
    private final String[] triggers;

    private WallSignType(String name, String... triggers) {
        this.name = name;
        this.triggers = triggers;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the triggers
     */
    public String[] getTriggers() {
        return triggers;
    }

    /**
     * Uses the text from a sign to find the type of wall sign.
     * @param text
     * @return
     */
    public static WallSignType fromSignText(String[] text) {
        if (text != null && text.length != 0) {
            String triggertext = text[0];

            for (WallSignType type : values()) {
                if (type.isMatch(triggertext)) {
                    return type;
                }
            }
        }
        return NONE;
    }

    /**
     * Checks if the text is a match.
     * @param triggertext
     * @return
     */
    private boolean isMatch(String triggertext) {
        if (triggers.length > 0) {
            triggertext = triggertext.toLowerCase();

            for (String trigger : triggers) {
                if (triggertext.equalsIgnoreCase("[" + trigger + "]")
                        || triggertext.equalsIgnoreCase(trigger)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if this sign requires converting
     * @param text
     * @return
     */
    public boolean requiresConvert(String[] text) {
        String triggertext = text[0];

        for (String trigger : triggers) {
            if (trigger.equalsIgnoreCase(triggertext)) {
                return true;
            }
        }

        return false;
    }
    
}

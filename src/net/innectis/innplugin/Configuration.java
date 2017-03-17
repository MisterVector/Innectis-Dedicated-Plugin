package net.innectis.innplugin;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.ConfigValueHandler;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.infractions.InfractionIntensity;
import net.innectis.innplugin.player.PlayerCredentials;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author Hret
 *
 * Configuration class for values that are globally set and can be adjusted
 * often
 *
 */
public final class Configuration {

    // <editor-fold defaultstate="open" desc="Plugin settings (db, name, version)">
    /** Tells the plugin if it's in logDebug mode */
    public static final boolean DEBUGMODE = true;
    /** The full name of the plugin */
    public static final String PLUGIN_NAME = "Innectis Dedicated Plugin";
    /** The plugin Version */
    public static final String PLUGIN_VERSION = "11.1.0";
    /** Plugin messagePrefix '[IDP]' */
    public static final String MESSAGE_PREFIX = "[IDP] ";
    /** Mysql database */
    public static final String MYSQL_DATABASE = "innectis_db";
    /** Mysql password */
    public static final String MYSQL_PASSWORD = "gR3ns9xs";
    /** Mysql username */
    public static final String MYSQL_USERNAME = "craft";
    /** Database version this plugin uses (live: 181) */
    public static final int DATABASE_VERSION = 181;
    /** Tells the plugin if the server is live or test */
    public static boolean PRODUCTION_SERVER = false;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Other">
    /** Sponge radius */
    public static final int SPONGE_RADIUS = 2;
    /** Amount of chunks to regen for each tick */
    public static final int LOT_REGEN_CHUNK_COUNT = 2;
    /** The timeout for bridges in milliseconds */
    public static final int BRIDGE_TIMEOUT = 50;
    public static final int MAX_BRIDGE_LENTH = 16;
    public static final int MAX_BIGBRIDGE_LENTH = 25;
    public static final int BRIDGE_POWER = 10;
    public static final int BIG_BRIGE_POWER = 25;
    /** Return the time the end is alive. */
    public static final long ENDWORLD_LIFETIME = 60 * 60 * 1000;
    /** The amount of infractions to show per page, used by /showinfraction command. */
    public static final int INFRACTIONS_PER_PAGE = 10;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Files ">
    /** Path to the PATH_Datafolder */
    public static final String PATH_DATAFOLDER = "plugins/IDP/";
    /** Path to debugMode logfile directory */
    public static final String PATH_DEBUGLOG = PATH_DATAFOLDER + "debug/";

    /** Path the the error reports of the IDP */
    public static final String PATH_ERROR_REPORTS = PATH_DATAFOLDER + "error_reports/";
    /** Filename of the help file, with the PATH_Datafolder attached */
    public static final String FILE_HELPFILE = PATH_DATAFOLDER + "help.yml";
    /** Filename of the Banned words file, with the PATH_Datafolder attached */
    public static final String FILE_BANNEDWORDS = PATH_DATAFOLDER + "bannedwords.txt";
    /** Filename of the disabled commands file, with the PATH_Datafolder attached */
    public static final String FILE_DISABLEDCOMMANDS = PATH_DATAFOLDER + "disabledcommands.txt";
    /** Filename for all valid votifier service names */
    public static final String FILE_VOTIFIERSERVICES = PATH_DATAFOLDER + "VotifierServices.txt";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Player ">
    /** The wand item for WE, its set to an Wooden Axe (271) */
    public static final int TWE_WANDID = 271;
    /** The multiplyer is done on the maxBlockChange to get the max size of a
     * region where an edit can be done with */
    public static final double TWE_MAX_CHANGESELECTION_MULTIPLYER = 10.0;
    /** Max region tinywe may edit */
    public static final int TWE_MAX_BLOCKCHANGES_SVIP = 1000;
    public static final int TWE_MAX_BLOCKCHANGES_GOLDY = 3000;
    public static final int TWE_MAX_BLOCKCHANGES_DIAMOND = 5000;
    public static final int TWE_MAX_BLOCKCHANGES_MODERATOR = 10000;
    /** The time (in minutes) it how long a session stays in the memory after the player logged out */
    public static final int PLAYERSESSION_EXPIRETIME = 10;
    /** The time in milliseconds a player is in the damaged state */
    public static final int PLAYER_DAMAGE_STATE_TIME = 10000; // 10 seconds
    /** The time to keep total pvp kills in memory (in seconds) */
    public static final int PLAYER_PVP_KILL_RETENTION = 1000 * 60 * 30; // 30 mins
    /** The amount of milliseconds a transfer caught entity request lasts */
    public static final long PLAYER_TRANSFER_CAUGHT_ENTITIES_TIMEOUT = 300001; // 30 sec
    /** The amount of milliseconds a teleport request stays active before timing out. */
    public static final long PLAYER_TELEPORT_REQUEST_TIMEOUT = 30000l; // 30 sec
    /** The amount of milliseconds a lotremoval request stays active */
    public static final long LOT_REMOVAL_REQUEST_TIMEOUT = 15000l; // 15 sec
    /** The time a player must wait in a portal before being teleported */
    public static final long PORTAL_TELEPORT_TIME = 2000;
    /** The delay (in millis) between a player using a give sign. (2 Seconds) */
    public static final long SIGN_INTERACT_DELAY = 2 * 1000;
    /** Max allowable owned animals for any player */
    public static final int MAX_PETS = 50;
    /** The infraction level that gets used if a player gets infraction by the IDP**/
    public static final InfractionIntensity AUTO_INFRACT_INTENSITY = InfractionIntensity.MIDDLE;
    /** Time to transfer funds to/from the bank **/
    public static final long BANK_TRANSACTION_TIME = 60 * 1000;
    /** Cost to warp out of the nether **/
    public static final int WARP_OUT_NETHER_COST = 400;
    /** Value of The End pot needed to open The End */
    public static final int END_POT_OPEN_VALUE = 5000;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Chat">
    /** The distance for local chat to travel. On the Y axis it will be 140% */
    public static final int CHAT_LOCALRADIUS = 50;
    /** Localprefix */
    public static final String CHAT_PREFIX_LOCAL = ChatColor.WHITE + "(" + ChatColor.LIGHT_PURPLE + "Local" + ChatColor.WHITE + ") ";
    /** LotChat */
    public static final String LOT_CHAT_PREFIX_LOCAL = ChatColor.WHITE + "(" + ChatColor.AQUA + "Lot" + ChatColor.WHITE + ") ";
    /** Jailed chat prefix */
    public static final String CHAT_PREFIX_JAILED = ChatColor.WHITE + "(" + ChatColor.RED + "Jailed" + ChatColor.WHITE + ") ";
    /** Muted chat prefix */
    public static final String CHAT_PREFIX_MUTED = ChatColor.WHITE + "(" + ChatColor.RED + "Muted" + ChatColor.WHITE + ") ";
    /** Censorship replacement word */
    public static final String CHAT_CENSOR_REPLACEMENT = "*****";
    /** The max amount of hits the player can have before getting banned */
    public static final int CHAT_FILTER_BANHITS = 6;
    /** The max amount of hits the player can have before getting kicked */
    public static final int CHAT_FILTER_KICKHITS = 4;
    /** The time (in MS) that the player can get banned for hitting the filter (6 hours) */
    public static final int CHAT_FILTER_BANTIME = 6 * 60 * 60 * 1000;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Configuration Values">
    private static HashMap<Integer, HashSet<String>> bannedWords = new HashMap<Integer, HashSet<String>>();

    public static void loadBannedWords() {
        // Allow reloading banned words, so clear if this already has words
        if (!bannedWords.isEmpty()) {
            bannedWords.clear();
        }

        InnPlugin.logCustom(ChatColor.AQUA, "Loading banned words...");

        try {
            List<String> strings = FileHandler.getData(Configuration.FILE_BANNEDWORDS);

            if (strings != null) {
                for (String str : strings) {
                    int len = str.length();
                    HashSet<String> words = new HashSet<String>();

                    if (!bannedWords.containsKey(len)) {
                        bannedWords.put(len, words);
                    } else {
                        words = bannedWords.get(len);
                    }

                    words.add(str);
                }
            }
        } catch (FileNotFoundException ex) {
            InnPlugin.logError("Cannot find banned words file!", ex);
        }
    }

    /**
     * Returns all banned words that are on the filter
     * @return
     */
    public static HashSet<String> getBannedWords() {
        HashSet<String> allBannedWords = new HashSet<String>();

        for (HashSet<String> words : bannedWords.values()) {
            allBannedWords.addAll(words);
        }

        return allBannedWords;
    }

    /**
     * Gets a list of all banned words by the specified length
     * @param len
     * @return
     */
    public static HashSet<String> getBannedWordsByLength(int len) {
        return bannedWords.get(len);
    }

    /**
     * @return the haltBannedWordFilter
     */
    public static boolean isBanFilterEmpty() {
        return bannedWords.isEmpty();
    }

    /**
     * Checks if the server is in maintenance mode
     * @return
     */
    public static boolean isInMaintenanceMode() {
        String maintenanceModeString = ConfigValueHandler.getValue("maintenance_mode");

        if (maintenanceModeString == null) {
            return false;
        }

        return Boolean.valueOf(maintenanceModeString);
    }

    /**
     * Sets the server maintenance mode
     * @param mode
     */
    public static void setMaintenanceMode(boolean mode) {
        ConfigValueHandler.saveValue("maintenance_mode", Boolean.toString(mode));
    }

    /**
     * Gets the server MOTD
     * @return
     */
    public static String getMotd() {
        return ConfigValueHandler.getValue("motd");
    }

    /**
     * Saves the server's MOTD
     * @param motd
     * @return
     */
    public static void setMotd(String motd) {
        ConfigValueHandler.saveValue("motd", motd);
    }

    /**
     * Gets the region where guests are automatically promoted to users
     * @return
     */
    public static IdpWorldRegion getGuestPromoteRegion() {
        String posString = ConfigValueHandler.getValue("guest_promote_region");

        if (posString != null) {
            try {
                String values[] = posString.split(", ");

                int x1 = Integer.parseInt(values[0]);
                int y1 = Integer.parseInt(values[1]);
                int z1 = Integer.parseInt(values[2]);
                Vector vec1 = new Vector(x1, y1, z1);

                int x2 = Integer.parseInt(values[3]);
                int y2 = Integer.parseInt(values[4]);
                int z2 = Integer.parseInt(values[5]);
                Vector vec2 = new Vector(x2, y2, z2);

                World world = Bukkit.getWorld(values[6]);

                return new IdpWorldRegion(world, vec1, vec2);
            } catch (Throwable throwable) {
                return null;
            }
        }

        return null;
    }

    /**
     * Sets the region where guests are promoted to users
     * @param region
     */
    public static void setGuestPromotionRegion(IdpWorldRegion region) {
        World world = region.getWorld();
        Vector vec1 = region.getPos1();
        Vector vec2 = region.getPos2();

        int x1 = vec1.getBlockX();
        int y1 = vec1.getBlockY();
        int z1 = vec1.getBlockZ();

        int x2 = vec2.getBlockX();
        int y2 = vec2.getBlockY();
        int z2 = vec2.getBlockZ();

        String worldName = world.getName();

        String regionString = x1 + ", " + y1 + ", " + z1 + ", " + x2 + ", " + y2 + ", " + z2 + ", " + worldName;
        ConfigValueHandler.saveValue("guest_promote_region", regionString);
    }

    // </editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Fake Player IDs">
    // These represent UUIDs of special names that do not represent actual players
    public static final UUID EVERYONE_IDENTIFIER = UUID.nameUUIDFromBytes("%".getBytes());
    public static final UUID LOT_ASSIGNABLE_IDENTIFIER = UUID.nameUUIDFromBytes("#".getBytes());
    public static final UUID LOT_ACCESS_IDENTIFIER = UUID.nameUUIDFromBytes("@".getBytes());
    public static final UUID SERVER_GENERATED_IDENTIFIER = UUID.nameUUIDFromBytes("[SERVER]".getBytes());
    public static final UUID OTHER_IDENTIFIER = UUID.nameUUIDFromBytes("~".getBytes());
    public static final UUID UNASSIGNED_IDENTIFIER = UUID.nameUUIDFromBytes("[UNASSIGNED]".getBytes());
    public static final UUID AUTOMATIC_IDENTIFIER = UUID.nameUUIDFromBytes("[AUTOMATIC]".getBytes());
    public static final UUID SYSTEM_IDENTIFIER = UUID.nameUUIDFromBytes("[SYSTEM]".getBytes());
    public static final UUID GAME_IDENTIFIER = UUID.nameUUIDFromBytes("[GAME]".getBytes());

    // These represent credentials of special names that do not represent actual players
    public static final PlayerCredentials EVERYONE_CREDENTIALS = new PlayerCredentials(EVERYONE_IDENTIFIER, "%", false);
    public static final PlayerCredentials LOT_ASSIGNABLE_CREDENTIALS = new PlayerCredentials(LOT_ASSIGNABLE_IDENTIFIER, "#", false);
    public static final PlayerCredentials LOT_ACCESS_CREDENTIALS = new PlayerCredentials(LOT_ACCESS_IDENTIFIER, "@", false);
    public static final PlayerCredentials SERVER_GENERATED_CREDENTIALS = new PlayerCredentials(SERVER_GENERATED_IDENTIFIER, "[SERVER]", false);
    public static final PlayerCredentials OTHER_CREDENTIALS = new PlayerCredentials(OTHER_IDENTIFIER, "~", false);
    public static final PlayerCredentials UNASSIGNED_CREDENTIALS = new PlayerCredentials(UNASSIGNED_IDENTIFIER, "[UNASSIGNED]", false);
    public static final PlayerCredentials AUTOMATIC_CREDENTIALS = new PlayerCredentials(AUTOMATIC_IDENTIFIER, "[AUTOMATIC]", false);
    public static final PlayerCredentials SYSTEM_CREDENTIALS = new PlayerCredentials(SYSTEM_IDENTIFIER, "[SYSTEM]", false);
    public static final PlayerCredentials GAME_CREDENTIALS = new PlayerCredentials(GAME_IDENTIFIER, "[GAME]");
    //</editor-fold>

    private Configuration() {
    }

}

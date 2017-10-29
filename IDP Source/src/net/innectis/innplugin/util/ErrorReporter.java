package net.innectis.innplugin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerSession;
import org.bukkit.World;

/**
 *
 * @author Hret
 * Class to handle error reports
 */
public final class ErrorReporter {

    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Sends an errormessage to the player
     * @param header
     * @param body
     */
    public static void generateErrorReport(String header, String body) {
        try {

            String folderlocation = "Error_reports" + File.separator;
            try {
                folderlocation = Configuration.PATH_ERROR_REPORTS;
            } catch (NullPointerException npe) {
                // Nothing
            }

            // Format the date to GMT
            final Date currentTime = new Date();
            final SimpleDateFormat date = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
            final SimpleDateFormat datetime = new SimpleDateFormat("yyyy_MM_dd_HHmm_ss.SSS");
            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            datetime.setTimeZone(TimeZone.getTimeZone("GMT"));

            String reportname = "IDP_ERROR_" + datetime.format(currentTime) + ".report";
            String foldername = date.format(currentTime);

            log.log(Level.SEVERE, "reportname: " + reportname);

            File file = new File(folderlocation + foldername + File.separator + reportname);
            if (file.exists()) {
                log.log(Level.SEVERE, "Double loginreports!");

                // Try to remake it
                reportname = "IDP_ERROR_" + datetime.format(currentTime) + "_DOUBLE.report";
                file = new File(folderlocation + reportname);
                if (file.exists()) {
                    log.log(Level.SEVERE, "Can't recover from making report.");
                    return;
                }
            }

            // Make sure the directories/files are made
            file.getParentFile().mkdirs();
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            try {
                // Write it all
                writer.write("Subject: " + header);
                writer.newLine();
                writer.flush();

                writer.write("-- -- -- -- System data -- -- -- --\r\n");
                writer.write(getSystemVariables());
                writer.newLine();
                writer.newLine();
                writer.flush();

                writer.write("-- -- -- -- Message data -- -- -- --\r\n");
                writer.write(body);
                writer.newLine();
                writer.flush();
            } catch (IOException ioex) {
                throw ioex;
            } finally {
                // Always close it off!
                writer.close();
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Something when wrong generating the error report! (" + ex.getMessage() + ")");
            ex.printStackTrace();
        }
    }

    /**
     * Returns some system variables that might come in handy
     * @return
     */
    private static String getSystemVariables() {
        StringBuilder builder = new StringBuilder(8000);

        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        // Default memory data
        builder.append("System time: ").append(DateUtil.formatString(new Date(), DateUtil.FORMAT_FULL_DATE_TIME));
        builder.append("\r\nMemory usage: ").append((runtime.totalMemory() - runtime.freeMemory()) / mb).append("mb/").append(runtime.totalMemory() / mb).append("mb");
        builder.append("\r\nFree Memory: ").append(runtime.freeMemory() / mb).append("mb");

        // Try to get MC server data
        try {
            // Worldinformations
            builder.append("\r\n\r\nWorldinfo: ");
            for (World w : InnPlugin.getPlugin().getServer().getWorlds()) {
                builder.append("\r\n\t").append(w.getName()).append(" holds: ").append(w.getEntities().size()).append(" entitie(s), ").append(w.getPlayers().size()).append(" player(s), ").append(w.getLoadedChunks().length).append(" chunk(s)");
            }

            // The sessions
            int active = 0;
            for (PlayerSession session : PlayerSession.getSessions()) {
                if (session.getExpireTime() == 0) {
                    active++;
                }
            }
            builder.append("\r\n\r\nSessions: ").append(PlayerSession.getSessions().size()).append(" (").append(active).append(" active)");

            // The players onlfine
            builder.append("\r\n\r\nPlayers: ");
            List<IdpPlayer> players = InnPlugin.getPlugin().getOnlinePlayers();
            if (players.isEmpty()) {
                builder.append("none");
            } else {
                for (IdpPlayer player : players) {
                    builder.append("['").append(player.getName()).append("',").append(player.getLocation()).append("]");
                }
            }
        } catch (NullPointerException npe) {
        }
        return builder.toString();
    }
    
}

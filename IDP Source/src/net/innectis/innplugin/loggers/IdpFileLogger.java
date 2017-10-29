package net.innectis.innplugin.loggers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;

/**
 *
 * @author Hret
 */
abstract class IdpFileLogger {

    /** * SyncLock object */
    private static final Object _syncLock = new Object();
    /** The path to the directory */
    protected String directoryPath;
    /** A Long value of the next midnight */
    protected Long nextMidnight;
    /** Output stream */
    protected BufferedWriter output;
    /** The dateformat */
    protected SimpleDateFormat format;
    /** The prefix to the filenames, files are saved as 'filename'-'datestring' */
    protected String filenamePrefix;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public IdpFileLogger(String location, String filenamePrefix, String dateformat) {
        this.format = new SimpleDateFormat(dateformat.concat(" "));
        this.filenamePrefix = filenamePrefix;
        this.directoryPath = location;
        File file = new File(location);
        file.mkdirs();

        this.updateNextMidnight();
        this.updateWriter();
    }

    /**
     * Updates the writer to switch to a new output file
     */
    protected final void updateWriter() {
        // The new file
        File logfile = new File(directoryPath + File.separator + getDateFolderPath() + File.separator + getFilename());
        // Make sure the folders are created
        logfile.getParentFile().mkdirs();
        try {
            // Closes the output if its not set already
            if (output != null) {
                synchronized (_syncLock) {
                    output.close();
                    output = new BufferedWriter(new FileWriter(logfile, true));
                }
            } else {
                output = new BufferedWriter(new FileWriter(logfile, true));
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The filename the log should be saved in
     */
    protected String getFilename() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return filenamePrefix + "-" + dateFormat.format(new Date()) + ".log";
    }

    /**
     * This creates a folder path constructed as: yyyy/MM
     * Note that this will not create any folders, it will just return the path!
     * @return yyyy/MM formatted with current dates
     */
    protected String getDateFolderPath() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy" + File.separator + "MM");
        return dateFormat.format(new Date());
    }

    /**
     * Updates the next midnight long value
     */
    protected final void updateNextMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 24, 00);
        nextMidnight = cal.getTimeInMillis();
    }

    /**
     * Logs the given message
     * @param message
     */
    protected void log(String message) {
        Date d = new Date();

        if (nextMidnight < d.getTime()) {
            updateWriter();
            updateNextMidnight();
        }

        try {
            output.write(format.format(d) + " | " + message);
            output.newLine();
            try {
                output.flush(); // always flush
            } catch (IOException ex) {
                InnPlugin.logError(ChatColor.RED + "Cannot flush writer! ", ex);
            }
        } catch (IOException ex) {
            InnPlugin.logError("Cannot log message! ", ex);
        }
    }

}

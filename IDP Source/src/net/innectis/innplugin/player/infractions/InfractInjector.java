package net.innectis.innplugin.player.infractions;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatInjector;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.util.StringUtil;

/**
 *
 * @author Hret
 *
 * A chatinjector for dealing infractions
 */
public final class InfractInjector extends ChatInjector {

    public static final String ENTER_CHAR = "\n";
    private final PlayerCredentials playerCredentials;
    private final InfractionIntensity intensity;
    private final String summary;
    private StringBuilder details;

    /**
     * Creates a new infractioninjector with the given details
     * @param playerCredentials
     * @param intensity
     * @param summary
     */
    public InfractInjector(PlayerCredentials playerCredentials, InfractionIntensity intensity, String summary) {
        this.playerCredentials = playerCredentials;
        this.intensity = intensity;
        this.summary = summary;
    }

    @Override
    public void onChat(IdpCommandSender sender, String message) {
        // Close the injector and save the infraction
        if (StringUtil.matches(message, "q", "quit", "exit", "save")) {
            Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();

            PlayerCredentials credentials = null;

            // Determine if we should use server generated credentials or player's
            if (sender instanceof IdpConsole) {
                credentials = Configuration.SERVER_GENERATED_CREDENTIALS;
            } else {
                credentials = PlayerCredentialsManager.getByName(sender.getName());
            }

            Infraction inf = new Infraction(playerCredentials, intensity, summary, details == null ? null : details.toString(), date, credentials);

            // Save the infraction and report
            int level = InfractionManager.getManager().saveInfraction(inf);
            if (inf.getId() != Infraction.DEFAULT_ID) {
                sender.printInfo("Infraction #" + inf.getId() + " saved!");
                sender.printInfo("Player '" + playerCredentials.getName() + "' infraction level: " + level);
            } else {
                sender.printError("Could not save infraction!");
            }

            // If the sender is a player, remove the injector
            if (sender.isPlayer()) {
                ((IdpPlayer) sender).getSession().setChatInjector(null);
            }
        } else {
            // Append to details
            if (details == null) {
                details = new StringBuilder(message);
            } else {
                details.append(ENTER_CHAR).append(message);
            }

            sender.print(ChatColor.GRAY, message);
        }
    }
    
}

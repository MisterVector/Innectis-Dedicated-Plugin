package net.innectis.innplugin.tasks.sync;

import java.sql.Timestamp;
import net.innectis.innplugin.system.bans.BanHandler;
import net.innectis.innplugin.system.bans.UserBan;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.external.api.interfaces.INoCheatPlusIDP;
import net.innectis.innplugin.external.LibraryType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 *
 * Alters the player online list (tab window) with custom values for Innectis.
 * This displays amount of players as well as the world the player is in.
 */
public class PlayerInfoTask extends RepeatingTask {

    private InnPlugin plugin;

    public PlayerInfoTask(InnPlugin plugin) {
        super(RunBehaviour.SYNCED, DefaultTaskDelays.PlayerInfo);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getExternalLibraryManager().isLoaded(LibraryType.NOCHEATPLUS)) {
            INoCheatPlusIDP noCheatPlus = (INoCheatPlusIDP) plugin.getExternalLibraryManager().getAPIObject(LibraryType.NOCHEATPLUS);

            String[] noCheatBanned = noCheatPlus.getBannedPlayers();
            if (noCheatBanned != null) {
                for (String banned : noCheatBanned) {
                    PlayerCredentials credentials = PlayerCredentialsManager.getByName(banned);

                    BanHandler.addBan(new UserBan(credentials, Configuration.AUTOMATIC_CREDENTIALS, new Timestamp(System.currentTimeMillis()), 21600000, false));
                    InnPlugin.getPlugin().broadCastMessage(ChatColor.RED, String.format("Player %s has been banned for 6 hours by the console!", banned));
                }
                noCheatPlus.clearBannedPlayers();
            }
        }


        for (IdpPlayer player : plugin.getOnlinePlayers()) {
            if (!player.getSession().isVisible()) {
                player.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 1), true);
            }

            if (player.getSession().getSpiked() != 0) {
                player.getSession().setSpiked(player.getSession().getSpiked() - 1);
                if (player.getSession().getSpiked() == 0) {
                    player.print(ChatColor.AQUA, "You feel your senses returning to normal..");
                } else {
                    player.print(ChatColor.AQUA, "You feel like you're going to vommit..");
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean mustRunAgain(long currentTime) {
        return super.mustRunAgain(currentTime);
    }
    
}

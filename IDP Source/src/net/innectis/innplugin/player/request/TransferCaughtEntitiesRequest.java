package net.innectis.innplugin.player.request;

import java.util.List;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author AlphaBlend
 *
 * Request to transfer caught entities to another player
 */
public class TransferCaughtEntitiesRequest extends Request {

    private IdpPlayer innReceiver;
    private IdpPlayer innSender;

    /**
     * This will take
     * @param plugin
     * @param caughtEntities
     * @param player
     * @param requester
     */
    public TransferCaughtEntitiesRequest(InnPlugin plugin, IdpPlayer player, IdpPlayer requester) {
        super(plugin, player, requester, System.currentTimeMillis(), Configuration.PLAYER_TRANSFER_CAUGHT_ENTITIES_TIMEOUT);

        this.innReceiver = player;
        this.innSender = requester;
    }

    @Override
    public void onAccept() {
        List<EntityTraits> caughtEntities = innSender.getSession().getCaughtEntityTraits();
        if (caughtEntities.isEmpty()) {
            innReceiver.printError(innSender.getName() + " has placed the caught entities!");
            innSender.printError("You have no caught entities to transfer to " + innReceiver.getName() + "!");
            return;
        }

        innReceiver.getSession().setCaughtEntities(caughtEntities);
        innReceiver.getSession().setRenameOwners(true);

        innSender.getSession().removeCaughtEntityTraits();
        innSender.printInfo(innReceiver.getColoredDisplayName() + ChatColor.AQUA + " has accepted the transfer entities request!");
    }

    @Override
    public void onReject() {
        if (innReceiver.isOnline()) {
            innReceiver.printInfo(innReceiver.getColoredDisplayName() + ChatColor.RED + " has rejected the entities transfer request!");
        }
    }

    @Override
    public void onTimeout() {
        if (innReceiver.isOnline()) {
            innReceiver.printError("The request to transfer entities has timed out.");
        }
    }

    @Override
    public String getDescription() {
        return "request to transfer caught entities to another player.";
    }
    
}

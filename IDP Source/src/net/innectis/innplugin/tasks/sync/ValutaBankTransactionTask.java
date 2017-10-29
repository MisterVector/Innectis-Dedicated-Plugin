package net.innectis.innplugin.tasks.sync;

import java.util.UUID;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 * A task to handle money going in or out of a player's bank
 *
 * @author AlphaBlend
 */
public class ValutaBankTransactionTask extends LimitedTask {

    private UUID playerId;
    private String playerName;

    public ValutaBankTransactionTask(IdpPlayer player, long time) {
        super(RunBehaviour.SYNCED, time, 1);
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
    }

    @Override
    public void run() {
        PlayerSession session = PlayerSession.getActiveSession(playerId);

        if (session != null) {
            session.setBankTaskId(0);
            session.setLastBankTaskTime(0);
        }

        IdpPlayer player = InnPlugin.getPlugin().getPlayer(playerId);

        TransactionObject transaction = TransactionHandler.getTransactionObject(playerId, playerName);
        int valutasToPlayer = transaction.getValue(TransactionType.VALUTAS_TO_PLAYER);

        if (valutasToPlayer > 0) {
            transaction.setValue(0, TransactionType.VALUTAS_TO_PLAYER);
            transaction.addValue(valutasToPlayer, TransactionType.VALUTAS);

            if (player != null) {
                player.printInfo("You have received " + ChatColor.YELLOW + valutasToPlayer, " valuta" + (valutasToPlayer != 1 ? "s" : "") + " from the bank!");
            }
        }

        int valutasToBank = transaction.getValue(TransactionType.VALUTAS_TO_BANK);

        if (valutasToBank > 0) {
            transaction.setValue(0, TransactionType.VALUTAS_TO_BANK);
            transaction.addValue(valutasToBank, TransactionType.VALUTAS_IN_BANK);

            if (player != null) {
                player.printInfo("You have sent " + ChatColor.YELLOW + valutasToBank, " valuta" + (valutasToBank != 1 ? "s" : "") + " to the bank!");
            }
        }
    }
    
}

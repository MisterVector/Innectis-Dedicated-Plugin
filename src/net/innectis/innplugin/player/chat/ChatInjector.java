package net.innectis.innplugin.player.chat;

import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * @author Hret
 *
 * This class can be extended to keep track of the sender's chat.
 * <p/>
 * When set to a player this class will capure the chat given by the player
 * and not send the chatevent to the default listeners.
 * <p/>
 * If set to the console it will capture chat from the named console text (/hret, /lynx).
 */
public abstract class ChatInjector {

    /**
     * Create a new injector
     */
    public ChatInjector() {
    }

    /**
     * This will remove the injector from the player's session.
     */
    protected void removeInjector(IdpCommandSender sender) {
        if (sender.isPlayer()) {
            ((IdpPlayer) sender).getSession().setChatInjector(null);
        }
    }

    /**
     * Event that gets called when the player chats.
     * @param sender
     * @param message
     */
    public abstract void onChat(IdpCommandSender sender, String message);
    
}

package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatMessage;
import net.innectis.innplugin.player.IdpPlayer;

public class InnPlayerChatEvent extends APlayerEvent implements InnEventCancellable {

    private String message;
    private ChatColor msgColor;
    private ChatMessage chatmessage;
    private boolean cancel;

    public InnPlayerChatEvent(final IdpPlayer player, String message, ChatColor msgColor) {
        super(player, InnEventType.PLAYER_CHAT);

        this.message = message;
        this.msgColor = msgColor;
        this.cancel = false;
    }

    /**
     * The message itself.
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * The message itself.
     * @return
     */
    public void setMessage(String message) {
        this.message = message;
        this.chatmessage = null;
    }

    /**
     * The colour of the message.
     * @return
     */
    public ChatColor getMsgColor() {
        return msgColor;
    }

    /**
     * The message itself.
     * @return
     */
    public void setMsgColor(ChatColor color) {
        msgColor = color;
        this.chatmessage = null;
    }

    /**
     * This will construct a chatmessage object and return that.
     * <p/>
     * The ChatMessage object will be cached in the event.
     *
     * @return
     */
    public ChatMessage getChatMessage() {
        if (chatmessage == null) {
            chatmessage = new ChatMessage(message, msgColor);
        }
        return chatmessage;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}

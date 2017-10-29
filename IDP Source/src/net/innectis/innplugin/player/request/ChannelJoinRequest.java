package net.innectis.innplugin.player.request;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.channel.ChatChannel;
import net.innectis.innplugin.player.channel.ChatChannelGroup;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * A request for a user to join a channel.
 *
 * @author AlphaBlend
 */
public class ChannelJoinRequest extends Request {

    private String channelName;

    public ChannelJoinRequest(InnPlugin parent, IdpPlayer player, IdpPlayer requester, String channelName) {
        super(parent, player, requester, System.currentTimeMillis(), 30000L);
        this.channelName = channelName;
    }

    @Override
    public void onAccept() {
        IdpPlayer player = getPlayer();
        IdpPlayer requester = getRequester();

        if (player == null) {
            if (requester != null) {
                requester.printError("Could not complete the channel join request.");
            }

            return;
        }

        ChatChannel channel = ChatChannelHandler.getChannel(channelName);

        if (channel != null) {
            if (requester != null) {
                requester.print(ChatColor.AQUA, player.getColoredDisplayName(), " has accepted the request to join the channel " + channelName + ".");
            }

            player.print(ChatColor.AQUA, "You have accepted the request to join channel " + channelName + ".");

            if (channel.containsMember(player.getName())) {
                player.printError("You are already a member of this channel!");

                if (requester != null) {
                    requester.printError(player.getColoredDisplayName(), " is already a member of this channel!");
                }

                return;
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(player.getUniqueId());

            int personalNum = player.getSession().makeNextChannelAndNumber(channelName);
            channel.addMember(credentials, ChatChannelGroup.MEMBER, personalNum, true);
            channel.sendGeneralMessage(player.getColoredDisplayName() + ChatColor.AQUA + " has joined the channel.");
        } else {
            requester.printError("The channel join request could not be completed.");
            requester.printError("The channel " + channelName + " doesn't exist anymore.");

            player.printError("The channel join request could not be completed.");
            player.printError("The channel " + channelName + " doesn't exist anymore.");
        }
    }

    @Override
    public void onReject() {
        IdpPlayer player = getPlayer();
        IdpPlayer requester = getRequester();

        if (player != null) {
            player.printError("You have rejected the request to join channel " + channelName + ".");
        }

        if (requester != null) {
            requester.printError("The request to join the channel " + channelName + " has been rejected.");
        }
    }

    @Override
    public void onTimeout() {
        IdpPlayer player = getPlayer();
        IdpPlayer requester = getRequester();

        if (player != null) {
            player.printError("The request to join channel " + channelName + " has timed out.");
        }

        if (requester != null) {
            requester.printError("The request to join the channel " + channelName + " has timed out.");
        }
    }

    @Override
    public String getDescription() {
        return "request to join a channel.";
    }
    
}

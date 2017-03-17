package net.innectis.innplugin.listeners.bukkit;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.loggers.ChatLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatMessage;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.util.DateUtil;
import org.bukkit.Location;

/**
 * @author Hret
 */
public class IdpChatListener {

    private final InnPlugin plugin;

    public IdpChatListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handler for chat when the player is muted
     *
     * @param player
     * @param message
     * @param msgColor
     */
    public void playerMutedChat(IdpPlayer player, String message, ChatColor msgColor) {
        String premsg = player.getPrefixAndDisplayName() + ": ";
        long muteTicks = player.getSession().getRemainingMuteTicks();

        premsg += ChatColor.WHITE + "(" + ChatColor.RED + "Muted";
        if (muteTicks > 0) {
            String muteTime = DateUtil.getTimeString(player.getSession().getRemainingMuteTicks(), false);
            premsg += " for " + muteTime;
        }
        premsg += ChatColor.WHITE + ") ";

        ChatMessage msg = new ChatMessage(message, msgColor);
        boolean isDrunk = false;

        for (IdpPlayer tarplayer : plugin.getOnlinePlayers()) {
            if (tarplayer.hasPermission(Permission.chat_hearmuted) && tarplayer.getSession().canHearMuted()) {
                // If the color is RED (admin msg) then skip checks
                if (msgColor != ChatColor.RED) {
                    // If the player has the sending player ignored, skip.
                    if (tarplayer.getSession().isIgnored(player.getName())) {
                        continue;
                    }

                    // If the player has a global mute.
                    if (tarplayer.getSession().isIgnored("%")) {
                        continue;
                    }
                }

                if (tarplayer.getSession().getSpiked() != 0 && !tarplayer.getName().equals(player.getName())) {
                    Random random = new Random();
                    if (random.nextInt(3) == 1) {
                        isDrunk = true;
                    }
                }

                String printMsg = premsg + msgColor;
                PlayerSession session = tarplayer.getSession();

                if (session.isStaff()) {
                    printMsg += msg.getUncensoredMessage(isDrunk);
                } else {
                    if (session.canSeeFilteredChat()) {
                        printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                    } else {
                        printMsg += msg.getCensoredMessage(isDrunk);
                    }
                }

                tarplayer.printRaw(printMsg);

                isDrunk = false;
            }
        }

        player.printError("Your words are spoken to deaf man's ears.");

        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
        chatLogger.logChat(player.getName(), "(MUTED) " + msg.getUncensoredMessage());
        InnPlugin.logMessage(premsg + msgColor + msg.getUncensoredMessage());
    }

    /**
     * Handler for the global chat
     *
     * @param player
     * @param message
     * @param msgColor
     */
    public void playerGlobalChat(IdpPlayer player, String message, ChatColor msgColor) {
        if (player.getSession().isIgnored("%")) {
            player.printError("Unable to send a message, everyone is ignored.");
            return;
        }

        String premsg = player.getPrefixAndDisplayName() + ": ";
        Boolean isDrunk = false;

        if (player.getSession().isJailed()) {
            premsg = ChatColor.WHITE + "(" + ChatColor.RED + "Jailed" + ChatColor.WHITE + ") " + premsg;
        }

        ChatMessage msg = new ChatMessage(message, msgColor);
        ChatSoundSetting setting = ChatSoundSetting.GLOBAL_AND_EMOTE;

        for (IdpPlayer tarplayer : plugin.getOnlinePlayers()) {
            // If the color is RED (admin msg) then skip checks
            if (msgColor != ChatColor.RED) {
                // If the player has the sending player ignored, skip.
                if (tarplayer.getSession().isIgnored(player.getName())) {
                    continue;
                }

                // If the player has a global mute.
                if (tarplayer.getSession().isIgnored("%")) {
                    continue;
                }
            }

            if (tarplayer.getSession().getSpiked() != 0 && !tarplayer.getName().equals(player.getName())) {
                Random random = new Random();
                if (random.nextInt(3) == 1) {
                    isDrunk = true;
                }
            }

            String printMsg = premsg + msgColor;
            PlayerSession session = tarplayer.getSession();

            if (session.isStaff()) {
                printMsg += msg.getUncensoredMessage(isDrunk);
            } else {
                if (session.canSeeFilteredChat()) {
                    printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                } else {
                    printMsg += msg.getCensoredMessage(isDrunk);
                }
            }

            tarplayer.printRaw(printMsg);
            isDrunk = false;

            // Don't play chat sound if receiver sent this message
            if (tarplayer.equals(player)) {
                continue;
            }

            if (session.hasChatSoundSetting(setting)) {
                tarplayer.playChatSoundFromSetting(setting);
            }
        }

        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
        chatLogger.logChat(player.getName(), msg.getUncensoredMessage());
        InnPlugin.logMessage(premsg + msgColor + msg.getUncensoredMessage());
    }

    /**
     * Handler for lot chat
     *
     * @param player
     * @param lot
     * @param message
     * @param msgColor
     */
    public void playerLotChat(IdpPlayer player, InnectisLot lot, String message, ChatColor msgColor) {
        if (player.getSession().isIgnored("%")) {
            player.printError("Unable to send a message, everyone is ignored.");
            return;
        }

        if (player.getSession().isJailed()) {
            player.printError("You are jailed. Cannot send lot chat!");
            return;
        }

        String premsg = Configuration.LOT_CHAT_PREFIX_LOCAL + player.getPrefixAndDisplayName() + ": ";
        Boolean isDrunk = false;
        ChatMessage msg = new ChatMessage(message, msgColor);
        InnectisLot topLot = lot.getParentTop();
        ChatSoundSetting setting = ChatSoundSetting.LOT_AND_LOCAL_CHAT;
        boolean gotOne = false;

        for (IdpPlayer tarplayer : topLot.getPlayersInsideRegion()) {
            // Don't process the sending player
            if (tarplayer.getName().equalsIgnoreCase(player.getName())) {
                continue;
            }

            // If the color is RED (admin msg) then skip checks
            if (msgColor != ChatColor.RED) {
                // If the player has the sending player ignored, skip.
                if (tarplayer.getSession().isIgnored(player.getName())) {
                    continue;
                }

                // If the player has a global mute.
                if (tarplayer.getSession().isIgnored("%")) {
                    continue;
                }
            }

            if (tarplayer.getSession().getSpiked() != 0 && !tarplayer.getName().equals(player.getName())) {
                Random random = new Random();
                if (random.nextInt(3) == 1) {
                    isDrunk = true;
                }
            }

            String printMsg = premsg + msgColor;
            PlayerSession session = tarplayer.getSession();

            if (session.isStaff()) {
                printMsg += msg.getCensoredMessage(isDrunk);
            } else {
                if (session.canSeeFilteredChat()) {
                    printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                } else {
                    printMsg += msg.getCensoredMessage(isDrunk);
                }
            }

            tarplayer.printRaw(printMsg);

            isDrunk = false;

            if (!gotOne) {
                gotOne = true;
            }

            if (session.hasChatSoundSetting(setting)) {
                tarplayer.playChatSoundFromSetting(setting);
            }
        }

        if (!gotOne) {
            player.printError("No one was on the lot to hear you!");
        } else {
            String printMsg = premsg + msgColor;
            PlayerSession session = player.getSession();

            if (session.isStaff()) {
                printMsg += msg.getUncensoredMessage(isDrunk);
            } else {
                if (session.canSeeFilteredChat()) {
                    printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                } else {
                    printMsg += msg.getCensoredMessage(isDrunk);
                }
            }

            player.printRaw(printMsg);

            ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
            chatLogger.logLotMessage(player.getName(), msg.getUncensoredMessage());
            InnPlugin.logMessage(premsg + msgColor + msg.getUncensoredMessage());
        }
    }

    /**
     * Handler for local chat
     *
     * @param player
     * @param message
     * @param msgColor
     */
    public void playerLocalChat(IdpPlayer player, String message, ChatColor msgColor, boolean global) {
        if (message.startsWith("!!") && player.hasPermission(Permission.chat_exclaim)) {
            message = message.substring(1);
            msgColor = ChatColor.RED;
        }

        String premsg = Configuration.CHAT_PREFIX_LOCAL + player.getPrefixAndDisplayName() + ": ";
        boolean isDrunk = false;

        if (player.getSession().isJailed()) {
            premsg = ChatColor.WHITE + "(" + ChatColor.RED + "Jailed" + ChatColor.WHITE + ") " + premsg;
        }

        try {
            // This can throw an exception as this method is not threadsafe (the bukkit one)
            // List<IdpPlayer> nearby = player.getNearbyPlayers(localChatRadius, (localChatRadius * 1.4), localChatRadius);
            List<IdpPlayer> nearby = new ArrayList<IdpPlayer>(6);
            Location playerloc = player.getLocation();
            for (IdpPlayer onlineplayer : plugin.getOnlinePlayers()) {
                Location onlinePlayerLoc = onlineplayer.getLocation();

                if (global || (onlinePlayerLoc.getWorld().equals(playerloc.getWorld())
                        && onlineplayer.getLocation().distance(playerloc) <= Configuration.CHAT_LOCALRADIUS
                        && onlineplayer.getSession().isVisible()
                        && onlineplayer.getWorld().getWorldType() == player.getWorld().getWorldType())) {
                    nearby.add(onlineplayer);
                }
            }

            boolean gotOne = false;
            ChatMessage msg = new ChatMessage(message, msgColor);
            ChatSoundSetting setting = ChatSoundSetting.LOT_AND_LOCAL_CHAT;

            for (IdpPlayer tarplayer : nearby) {
                // Skip self.
                if (tarplayer.getName().equalsIgnoreCase(player.getName())) {
                    continue;
                }

                // If the color is RED (admin msg) then skip checks
                if (msgColor != ChatColor.RED) {
                    // If the player has the sending player ignored, skip.
                    if (tarplayer.getSession().isIgnored(player.getName())) {
                        continue;
                    }

                    // If the player has a global mute.
                    if (tarplayer.getSession().isIgnored("%")) {
                        continue;
                    }
                }

                if (tarplayer.getSession().getSpiked() != 0 && !tarplayer.getName().equals(player.getName())) {
                    Random random = new Random();
                    if (random.nextInt(3) == 1) {
                        isDrunk = true;
                    }
                }

                String printMsg = premsg + msgColor;
                PlayerSession session = tarplayer.getSession();

                if (session.isStaff()) {
                    printMsg += msg.getUncensoredMessage(isDrunk);
                } else {
                    if (session.canSeeFilteredChat()) {
                        printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                    } else {
                        printMsg += msg.getCensoredMessage(isDrunk);
                    }
                }

                tarplayer.printRaw(printMsg);

                if (session.hasChatSoundSetting(setting)) {
                    tarplayer.playChatSoundFromSetting(setting);
                }

                isDrunk = false;

                if (!gotOne) {
                    gotOne = true;
                }
            }

            if (!gotOne) {
                player.printError("No one was around to hear your message.");
            } else {
                String printMsg = premsg + msgColor;
                PlayerSession session = player.getSession();

                if (session.isStaff()) {
                    printMsg += msg.getUncensoredMessage(isDrunk);
                } else {
                    if (session.canSeeFilteredChat()) {
                        printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                    } else {
                        printMsg += msg.getCensoredMessage(isDrunk);
                    }
                }

                player.printRaw(printMsg);

                ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
                chatLogger.logChat(player.getName(), msg.getUncensoredMessage());
                InnPlugin.logMessage(premsg + msgColor + msg.getUncensoredMessage());
            }
        } catch (ConcurrentModificationException cme) {
            InnPlugin.logError("ConcurrentModificationException getting nearby players!", cme);
            player.printError("Internal server error!");
        }
    }

    public void playerEmote(IdpPlayer player, String message) {
        if (player.getSession().isIgnored("%")) {
            player.printError("Unable to send a message, everyone is ignored.");
            return;
        }

        String premsg = ChatColor.LIGHT_PURPLE + "*" + player.getColoredDisplayName() + " ";
        boolean isDrunk = false;

        ChatColor msgColor = ChatColor.YELLOW;
        ChatMessage msg = new ChatMessage(message, msgColor);
        ChatSoundSetting setting = ChatSoundSetting.GLOBAL_AND_EMOTE;

        for (IdpPlayer tarplayer : plugin.getOnlinePlayers()) {
            // If the player has the sending player ignored, skip.
            if (tarplayer.getSession().isIgnored(player.getName())) {
                continue;
            }

            // If the player has a global mute.
            if (tarplayer.getSession().isIgnored("%")) {
                continue;
            }

            if (tarplayer.getSession().getSpiked() != 0 && !tarplayer.getName().equals(player.getName())) {
                Random random = new Random();
                if (random.nextInt(3) == 1) {
                    isDrunk = true;

                }
            }

            String printMsg = premsg + msgColor;
            PlayerSession session = tarplayer.getSession();

            if (session.isStaff()) {
                printMsg += msg.getUncensoredMessage(isDrunk);
            } else {
                if (session.canSeeFilteredChat()) {
                    printMsg += msg.getUncensoredUnmarkedMessage(isDrunk);
                } else {
                    printMsg += msg.getCensoredMessage(isDrunk);
                }
            }

            tarplayer.printRaw(printMsg + ChatColor.LIGHT_PURPLE + "*");

            // Don't play chat sound if receiver sent this message
            if (tarplayer.equals(player)) {
                continue;
            }

            if (session.hasChatSoundSetting(setting)) {
                tarplayer.playChatSoundFromSetting(setting);
            }

            isDrunk = false;
        }

        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
        chatLogger.logChat(player.getName(), ChatColor.LIGHT_PURPLE + "(Emote) " + msgColor + msg.getUncensoredMessage());
        InnPlugin.logMessage(premsg + ChatColor.LIGHT_PURPLE + "(Emote) " + msgColor + msg.getUncensoredMessage());
    }

}

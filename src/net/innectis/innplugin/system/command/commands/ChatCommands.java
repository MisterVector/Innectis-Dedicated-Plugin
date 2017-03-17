package net.innectis.innplugin.system.command.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.system.command.CommandName;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.loggers.ChatLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.loggers.PrefixChangeLogger;
import net.innectis.innplugin.player.channel.ChannelSettings;
import net.innectis.innplugin.player.channel.ChatChannel;
import net.innectis.innplugin.player.channel.ChatChannelGroup;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.channel.MemberDetails;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatMessage;
import net.innectis.innplugin.player.chat.Prefix;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.request.ChannelJoinRequest;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public final class ChatCommands {

    @CommandMethod(aliases = {"emote", "em", "me"},
    description = "Sends a message as an emote.",
    permission = Permission.command_misc_emote,
    usage = "/emote <text>",
    serverCommand = false)
    public static boolean commandEmote(InnPlugin parent, IdpPlayer player, SmartArguments args) {
        if (player.getSession().getRemainingMuteTicks() != 0) {
            player.printError("You cannot use this command when muted.");
            return true;
        }

        if (args.size() == 0) {
            return false;
        }

        parent.getListenerManager().getChatListener().playerEmote(player, args.getJoinedStrings(0));
        return true;
    }

    @CommandMethod(aliases = {"filter", "chatfilter"},
    description = "Adds/removes words from the word filter.",
    permission = Permission.command_moderation_filter,
    usage = "/filter <add/remove> <-word <word>> [-reason <reason>]",
    serverCommand = true)
    public static boolean commandFilter(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, ParameterArguments args) {

        String inputWord = args.getString("word", "w");
        if (inputWord == null) {
            sender.printError("No word was given... (use -word <word>)");
            return true;
        } else {
            inputWord = inputWord.toLowerCase();
        }

        String reason = args.getString("reason", "r");
        if (reason == null) {
            reason = "none";
        }

        Set<String> bannedWords = Configuration.getBannedWords();
        boolean alreadyExists = bannedWords.contains(inputWord);

        if (args.actionMatches(0, "add", "a")) {
            if (alreadyExists) {
                sender.printError("The word \"" + inputWord + "\" is already being filtered!");
                return true;
            }

            File bannedWordsFile = new File(Configuration.FILE_BANNEDWORDS);
            try {
                FileHandler.addData(bannedWordsFile, new String[]{inputWord}, true);
            } catch (IOException ex) {
                sender.printError("Cannot update banned words!");
                InnPlugin.logError("Cannot save banned words file! (1)", ex);
                return true;
            }

            // Reload the file!
            Configuration.loadBannedWords();
            sender.printInfo("The word \"" + inputWord + "\" was added to the filter!");
            InnPlugin.logInfo(sender.getColoredName(), " has added \"" + inputWord + "\" to the filter with reason: " + reason);
            return true;

        } else if (args.actionMatches(0, "remove", "rem", "del", "delete")) {
            if (!alreadyExists) {
                sender.printError("The word \"" + inputWord + "\" is not being filtered!");
                return true;
            }

            if (bannedWords.remove(inputWord)) {

                File bannedWordsFile = new File(Configuration.FILE_BANNEDWORDS);
                try {
                    FileHandler.addData(bannedWordsFile, bannedWords.toArray(new String[]{}), false);
                } catch (IOException ex) {
                    sender.printError("Cannot update banned words!");
                    InnPlugin.logError("Cannot save banned words file! (2)", ex);
                    return true;
                }

                // Reload the file!
                Configuration.loadBannedWords();
                sender.printInfo("The word \"" + inputWord + "\"was removed from the filter!");
                InnPlugin.logInfo(sender.getColoredName(), " has removed \"" + inputWord + "\" from the filter with reason: " + reason);
                return true;
            } else {
                sender.printError("Cannot update banned words!");
                return true;
            }
        }

        return false;
    }

    @CommandMethod(aliases = {"msg", "m", "w", "whisper", "tell"},
    description = "Sends a whisper message to the target player.",
    permission = Permission.command_misc_privatemessage,
    usage = "/msg <username> <message>",
    serverCommand = true)
    public static boolean commandMsg(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, SmartArguments args) {
        if (args.size() >= 2) {
            // Stop muted players!
            if (sender.isPlayer() && ((IdpPlayer) sender).getSession().isMuted()) {
                sender.printError("You can't whisper when you're muted!");
                return true;
            }

            String whisperArg = args.getString(0);

            List<IdpCommandSender> receivers = new ArrayList<IdpCommandSender>();

            String[] receiverArray = whisperArg.split(",");

            for (String receiver : receiverArray) {
                if (receiver.equalsIgnoreCase("#console")) {
                    IdpConsole console = parent.getConsole();

                    if (!receivers.contains(console)) {
                        receivers.add(console);
                    }
                } else {
                    IdpPlayer testReceiver = parent.getPlayer(receiver);

                    if (testReceiver != null && testReceiver.isOnline()) {
                        if (testReceiver.getName().equalsIgnoreCase(sender.getName())) {
                            sender.printError("You cannot whisper yourself!");
                            return true;
                        }

                        // Make sure not to add the same player multiple times
                        if (!receivers.contains(testReceiver)) {
                            receivers.add(testReceiver);
                        }
                    }
                }
            }

            if (receivers.isEmpty()) {
                sender.printError("Failed to send whisper, as none of the recipients are online!");
                return true;
            }

            String receiveNames = "";

            for (IdpCommandSender receiver : receivers) {
                if (!receiveNames.isEmpty()) {
                    receiveNames += ChatColor.GRAY + ", ";
                }

                receiveNames += receiver.getColoredName();
            }

            // Reference to the global chatlogger
            ChatLogger chatlog = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);

            // Setup the message for the player itself.
            String senderMsg = ChatColor.WHITE + "(" + ChatColor.GRAY + "whisper" + ChatColor.WHITE + ") "
                    + ChatColor.GRAY + "You tell " + receiveNames + ChatColor.GRAY + ": ";

            String message = ChatColor.parseChatColor(args.getJoinedStrings(1));

            // Allow colored text if player can use it
            if (sender.hasPermission(Permission.chat_usecolours)) {
                message = ChatColor.parseChatColor(message);
            }

            ChatMessage msg = new ChatMessage(message, ChatColor.GRAY);

            String whisperMsg = null;

            if (!sender.isPlayer() || ((IdpPlayer) sender).getSession().isStaff()) {
                whisperMsg = msg.getUncensoredMessage();
            } else {
                IdpPlayer player = (IdpPlayer) sender;

                if (player.getSession().canSeeFilteredChat()) {
                    whisperMsg = msg.getUncensoredUnmarkedMessage();
                } else {
                    whisperMsg = msg.getCensoredMessage();
                }
            }

            if (sender.isPlayer()) {
                senderMsg += ChatColor.GRAY.toString() + ChatColor.EFFECT_ITALIC.toString() + whisperMsg;
            } else {
                // Console doesn't display italics very well
                senderMsg += ChatColor.GRAY.toString() + whisperMsg;
            }

            sender.printRaw(senderMsg);

            // Check if the sender is the console or a staff member
            boolean isConsoleOrStaff = !sender.isPlayer() || ((IdpPlayer) sender).getSession().isStaff();
            String receiverMsg = ChatColor.WHITE + "(" + ChatColor.GRAY + "whisper" + ChatColor.WHITE + ") "
                    + sender.getColoredName() + ChatColor.GRAY + " tells " + (receivers.size() == 1 ? "you" : receiveNames) + ": ";
            ChatSoundSetting setting = ChatSoundSetting.WHISPER_CHAT;

            for (IdpCommandSender receiver : receivers) {
                if (msg.hasCussing()) {
                    // Log cussing
                    chatlog.logFilteredMessage(sender.getName(), receiver.getName());
                }

                // Check that the receiver can receive the message
                if (isConsoleOrStaff || receiver instanceof IdpConsole
                        || (receiver instanceof IdpPlayer && !((IdpPlayer) receiver).getSession().isIgnored(sender.getName()))) {
                    String finalReceiverMsg = receiverMsg;

                    // If the receiver is the console make sure not to add the italic effect
                    // as the console cannot display it properly
                    if (receiver instanceof IdpConsole) {
                        finalReceiverMsg += ChatColor.GRAY.toString();
                    } else {
                        finalReceiverMsg += ChatColor.GRAY.toString() + ChatColor.EFFECT_ITALIC.toString();
                    }

                    if (!receiver.isPlayer() || ((IdpPlayer) receiver).getSession().isStaff()) {
                        finalReceiverMsg += msg.getUncensoredMessage();
                    } else {
                        IdpPlayer player = (IdpPlayer) receiver;

                        if (player.getSession().canSeeFilteredChat()) {
                            finalReceiverMsg += msg.getUncensoredUnmarkedMessage();
                        } else {
                            finalReceiverMsg += msg.getCensoredMessage();
                        }
                    }

                    // The message to send to the receiver
                    receiver.printRaw(finalReceiverMsg);

                    if (receiver instanceof IdpPlayer) {
                        IdpPlayer receiverPlayer = (IdpPlayer) receiver;
                        PlayerSession session = receiverPlayer.getSession();

                        if (session.hasChatSoundSetting(setting)) {
                           receiverPlayer.playChatSoundFromSetting(setting);
                        }

                        receiverPlayer.getSession().setLastWhisperFrom(sender);
                    }
                } else {
                    sender.printError(receiver.getColoredName(), " could not receive your message!");
                }

                // log the message
                chatlog.logPrivateChat(sender.getName(), receiver.getName(), message);
            }
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"reply", "r"},
    description = "Replies to the last person that sent you a message.",
    permission = Permission.command_misc_reply,
    usage = "/reply <message>",
    serverCommand = false)
    public static boolean commandReply(Server server, InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length >= 1) {
            if (sender.isPlayer()) {
                IdpPlayer player = (IdpPlayer) sender;
                IdpCommandSender receiver = player.getSession().getLastWhisperFrom();

                if (receiver == null) {
                    sender.printError("No one has whispered you yet!");
                    return true;
                }

                String message = StringUtil.joinString(args, " ", 0);

                // Allow colored text if player can use it
                if (player.hasPermission(Permission.chat_usecolours)) {
                    message = ChatColor.parseChatColor(message);
                }

                ChatMessage msg = new ChatMessage(message, ChatColor.GRAY);
                String senderMsg = ChatColor.WHITE + "(" + ChatColor.GRAY + "reply" + ChatColor.WHITE + ") "
                        + ChatColor.GRAY + "you reply to " + receiver.getColoredName() + ChatColor.GRAY + ": " + ChatColor.EFFECT_ITALIC;
                ChatSoundSetting setting = ChatSoundSetting.WHISPER_CHAT;
                PlayerSession session = player.getSession();

                if (session.isStaff()) {
                    senderMsg += msg.getUncensoredMessage();
                } else {
                    if (session.canSeeFilteredChat()) {
                        senderMsg += msg.getUncensoredUnmarkedMessage();
                    } else {
                        senderMsg += msg.getCensoredMessage();
                    }
                }

                String receiverMsg = ChatColor.WHITE + "(" + ChatColor.GRAY + "reply" + ChatColor.WHITE + ") "
                        + player.getColoredDisplayName() + ChatColor.GRAY + " replies: ";

                if (receiver.isPlayer()) {
                    receiverMsg += ChatColor.EFFECT_ITALIC;
                }

                if (!receiver.isPlayer() || ((IdpPlayer) receiver).getSession().isStaff()) {
                    receiverMsg += msg.getUncensoredMessage();
                } else {
                    IdpPlayer receiverPlayer = (IdpPlayer) receiver;

                    if (receiverPlayer.getSession().canSeeFilteredChat()) {
                        receiverMsg += msg.getUncensoredUnmarkedMessage();
                    } else {
                        receiverMsg += msg.getCensoredMessage();
                    }
                }

                if (receiver.isPlayer()) {
                    IdpPlayer tempplayer = (IdpPlayer) receiver;
                    tempplayer.getSession().setLastWhisperFrom(sender);

                    if (!tempplayer.isOnline()) {
                        sender.printError("That user is no longer online.");
                        return true;
                    }
                }

                // The message to send back to the sender
                sender.printRaw(senderMsg);

                boolean canSend = true;

                // If the target has the player ignored
                if (receiver.isPlayer() && ((IdpPlayer) receiver).getSession().isIgnored(sender.getName())) {
                    canSend = false;
                }

                // If staff, overrule previous.
                if (sender.isPlayer() && ((IdpPlayer) sender).getSession().isStaff()) {
                    canSend = true;
                }

                // If console overrule previous
                if (!sender.isPlayer()) {
                    canSend = true;
                }

                if (canSend) {
                    // The message to send to the receiver
                    receiver.printRaw(receiverMsg);

                    if (receiver instanceof IdpPlayer) {
                        IdpPlayer receiverPlayer = (IdpPlayer) receiver;
                        PlayerSession receiverSession = receiverPlayer.getSession();

                        if (receiverSession.hasChatSoundSetting(setting)) {
                            receiverPlayer.playChatSoundFromSetting(setting);
                        }
                    }

                    // log the message
                    ChatLogger chatlog = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
                    chatlog.logPrivateChat(sender.getName(), receiver.getName(), message);
                }
            } else {
                sender.printError("Console can't use /reply.");
                return true;
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"setprefix", "changeprefix"},
    description = "Sets a prefix for the player.",
    permission = Permission.command_misc_setprefix,
    usage = "/setprefix <prefix text> [-username, -user, -u, -player, -p <username>] [-color]",
    serverCommand = false)
    public static boolean commandSetPrefix(IdpPlayer player, ParameterArguments args) {
        String prefixText;
        IdpPlayer tarPlayer;

        if (args.size() >= 1) {
            prefixText = args.getString(0).trim();

            // Check if empty or only spaces
            if (prefixText == null || prefixText.isEmpty() || Pattern.matches("^[ ]+$", prefixText)) {
                player.printError("You may not set your prefix to that.");
                return true;
            }

            ChatMessage msg = new ChatMessage(prefixText, ChatColor.WHITE);

            if (msg.hasCussing()) {
                player.printError("You cannot set this prefix.");
                return true;
            }

            // ------------------------------------------------------
            // Get the targetplayer
            if (args.getString("username", "user", "u", "player", "p") == null || player.getGroup() == PlayerGroup.GOLDY) {
                tarPlayer = player;
            } else {
                tarPlayer = args.getPlayer("username", "user", "u", "player", "p");
                if (tarPlayer == null) {
                    player.printError("That user is not online.");
                    return true;
                }
            }

            // Get the groupcolor of the target
            ChatColor groupColor = tarPlayer.getGroup().color;

            String argColor = args.getString("color", "c", "colour");
            // ------------------------------------------------------
            // Get the colour if supplied and not skipped
            if (argColor != null && !argColor.equalsIgnoreCase("x")) {
                groupColor = ChatColor.getByCodeOrString(argColor.toLowerCase());

                if (groupColor == null) {
                    player.printError("That color doesn't exist.");
                    return true;
                }

                if (player.getGroup() == PlayerGroup.GOLDY) {
                    switch (groupColor) {
                        case BLACK:        // Unreadable
                        case RED:          // For admins
                        case DARK_RED:     // For admins
                        case GREEN:        // For moderators
                        case DARK_GREEN:   // For moderators
//                        case LIGHT_PURPLE: // For server
                            player.printError("That color can't be used for your rank! ");
                            return true;

                        // Effects
                        case EFFECT_MAGIC:          // For hax
                        case EFFECT_BOLD:           // For hax
                        case EFFECT_ITALIC:         // For hax
                        case EFFECT_STRIKE_THROUGH: // For hax
                        case EFFECT_UNDERLINED:     // For hax
                        case EFFECT_CLEAR:          // For hax
                            player.printError("That color doesn't exist.");
                            return true;
                    }
                }

                // Dont allow these for mods...
                if (player.getGroup() == PlayerGroup.MODERATOR) {
                    switch (groupColor) {
//                        case BLACK:        // Unreadable
//                        case RED:          // For admins
//                        case DARK_RED:     // For admins
//                        case LIGHT_PURPLE: // For server

                        // Effects
                        case EFFECT_MAGIC:          // For hax
                        case EFFECT_BOLD:           // For hax
                        case EFFECT_ITALIC:         // For hax
                        case EFFECT_STRIKE_THROUGH: // For hax
                        case EFFECT_UNDERLINED:     // For hax
                        case EFFECT_CLEAR:          // For hax
                            player.printError("You can't set effects as colours.");
                            return true;
                    }
                }
            }

            // ------------------------------------------------------
            // Check lenght
            if (prefixText.length() > 14) {
                player.printError("The prefix is too long.");
                return true;
            }

            // Check content
            if (!Pattern.matches("^[ 0-9A-Za-z_-]{0,14}$", prefixText) && !player.hasPermission(Permission.command_misc_setprefix_allcharacters)) {
                player.printError("The prefix is not valid!");
                return true;
            }

            // Convert the prefix text
            String arrPrefix[] = prefixText.split(" ");
            prefixText = "";
            for (int i = 0; i < arrPrefix.length; i++) {
                prefixText += arrPrefix[i].substring(0, 1).toUpperCase()
                        + arrPrefix[i].substring(1).toLowerCase()
                        + (i < arrPrefix.length - 1 ? " " : "");
            }

            // Check for banned words
            HashSet<String> bannedWords = Configuration.getBannedWords();
            if (bannedWords.contains(prefixText.toLowerCase())) {
                player.printError("You may not set your prefix to that.");
                return true;
            }

            // Check some prefixes that are not allowed
            if (player.getGroup() == PlayerGroup.GOLDY) {
                if (prefixText.equalsIgnoreCase("admin")
                        || prefixText.equalsIgnoreCase("global admin")
                        || prefixText.equalsIgnoreCase("local admin")
                        || prefixText.equalsIgnoreCase("administrator")
                        || prefixText.equalsIgnoreCase("owner")
                        || prefixText.equalsIgnoreCase("webmin")
                        || prefixText.equalsIgnoreCase("webadmin")
                        || prefixText.equalsIgnoreCase("web admin")
                        || prefixText.equalsIgnoreCase("mod")
                        || prefixText.equalsIgnoreCase("rainbow mod")
                        || prefixText.equalsIgnoreCase("global mod")
                        || prefixText.equalsIgnoreCase("local mod")
                        || prefixText.equalsIgnoreCase("moderator")
                        || prefixText.equalsIgnoreCase("server")) {
                    player.printError("You are not allowed to change your prefix to that.");
                    return true;
                }
            }

            PrefixChangeLogger prefixChangeLogger = (PrefixChangeLogger) LogType.getLoggerFromType(LogType.PREFIX_CHANGE);
            prefixChangeLogger.logNameChange(player.getName(), prefixText);

            Prefix prefix = new Prefix(prefixText, groupColor);
            tarPlayer.getSession().setPrefix(2, prefix);
            player.print(ChatColor.DARK_GREEN, "Set new prefix for " + tarPlayer.getName() + ".");

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"resetprefix"},
    description = "Resets a player's prefix.",
    permission = Permission.command_misc_resetprefix,
    usage = "/resetprefix [username]",
    serverCommand = true)
    public static void commandResetPrefix(IdpCommandSender<? extends CommandSender> sender, SmartArguments args) {
        if (!sender.isPlayer()) {
            if (args.size() == 1) {
                IdpPlayer tarPlayer = args.getPlayer(0);

                if (tarPlayer == null) {
                    sender.printError("That user is not online.");
                    return;
                }
                Prefix prefix = new Prefix(tarPlayer.getGroup().name, tarPlayer.getGroup().color);
                tarPlayer.getSession().setPrefix(2, prefix);
            } else {
                sender.printError("Can't change server prefix.");
            }
            return;
        }
        IdpPlayer player = (IdpPlayer) sender;
        IdpPlayer tarPlayer = player;

        if (args.size() == 1) {
            tarPlayer = args.getPlayer(0);

            if (tarPlayer == null) {
                player.printError("That user is not online.");
                return;
            }

            if (player != tarPlayer && player.getGroup() == PlayerGroup.GOLDY) {
                player.printError("You cannot reset the prefix of other players.");
                return;
            }
        }

        Prefix prefix = new Prefix(tarPlayer.getGroup().name, tarPlayer.getGroup().color);
        tarPlayer.getSession().setPrefix(2, prefix);

        player.print(ChatColor.DARK_GREEN, "Reset " + tarPlayer.getName() + "'s prefix.");

        return;
    }

    @CommandMethod(aliases = {"spk", "hret", "lynx", "alpha", "schret", "scalpha", "sclynx"},
    description = "Speak command for console.",
    permission = Permission.command_misc_speak,
    usage = "/spk <message>",
    serverCommand = true,
    hiddenCommand = true,
    hideinlists = true)
    public static boolean commandSpkConsoleHret(InnPlugin parent, IdpCommandSender sender, CommandName cmd, SmartArguments args) {
        if (sender.isPlayer()) {
            // Command blocked in players
            sender.printError("This command can only be used by the server.");
            return true;
        }

        String name = cmd.getName();
        String argsstr = args.getJoinedStrings(0);
        boolean staffChat = name.startsWith("sc");

        if (staffChat) {
            name = name.substring(2);
        }

        if (name.equalsIgnoreCase("spk") || name.equalsIgnoreCase("hret")) {
            name = "Hret";
        } else if (name.equalsIgnoreCase("lynx")) {
            name = "The_Lynxy";
        } else if (name.equalsIgnoreCase("alpha")) {
            name = "AlphaBlend";
        } else {
            // Inpossible, but just to keep it nice and clean.
            name = "Console";
        }

        if (staffChat) {
            String staffMsg = ChatColor.WHITE + "[" + ChatColor.GREEN + "STAFF" + ChatColor.WHITE + "] "
                    + ChatColor.DARK_PURPLE + "[" + name + "]" + ChatColor.WHITE + ": " + ChatColor.GREEN + argsstr;
            ChatSoundSetting setting = ChatSoundSetting.STAFF_CHAT;

            for (IdpPlayer player : parent.getOnlineStaff(false)) {
                player.printInfo(staffMsg);

                PlayerSession session = player.getSession();

                if (session.hasChatSoundSetting(setting)) {
                    player.playChatSoundFromSetting(setting);
                }
            }

            InnPlugin.logInfo(ChatColor.WHITE + "[" + ChatColor.GREEN + "STAFF" + ChatColor.WHITE + "] "
                    + ChatColor.DARK_PURPLE + "[" + name + "]" + ChatColor.WHITE + ": " + ChatColor.GREEN + argsstr);
        } else {
            parent.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + "SERVER" + "] "
                    + ChatColor.DARK_RED + name + ChatColor.WHITE + ": " + argsstr);
            InnPlugin.logCustom(ChatColor.RED, "[SERVER] " + name + ": " + argsstr);
        }
        return true;
    }

    @CommandMethod(aliases = {"join", "j"},
    description = "Joins a chat channel by its name.",
    permission = Permission.command_misc_chatchannel_join,
    usage = "/join <channel> [-setpass, -sp <password>] [-pass, -password, -p <password>] [-hide, -h]",
    serverCommand = false)
    public static boolean commandJoin(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }
        String channelName = args.getString(0);

        if (channelName.equalsIgnoreCase("all") && player.hasPermission(Permission.command_misc_chatchannel_joinall)) {
            ChatChannelHandler.addGlobalListener(player.getName());
            player.printInfo("You have joined all channels.");
            return true;
        }

        if (channelName.length() < 3 || channelName.length() > 20) {
            player.printError("Chat channel names must be between 3 and 20 characters!");
            return true;
        }

        if (ChatChannelHandler.isValidChannel(channelName)) {
            if (player.getSession().getChannels().size() == 20) {
                player.printError("You cannot be in more than 20 channels at once.");
                return true;
            }

            ChatChannel channel = ChatChannelHandler.getChannel(channelName);

            if (channel.containsMember(player.getName())) {
                player.printError("You are already in that channel!");
                return true;
            }

            if (channel.isBanned(player.getName())) {
                player.printError("You are banned from that channel!");
                return true;
            }

            // This channel is password protected, check unless they
            // have a permission that bypasses it
            if (channel.isRequiringPassword() && !player.hasPermission(Permission.special_chatroom_override)) {
                String password = args.getString("password", "pass", "p");

                if (password == null || !password.equals(channel.getPassword())) {
                    if (password == null) {
                        player.printError("This channel requires a password to enter.");
                    } else {
                        player.printError("Wrong password entered.");
                    }

                    return true;
                }
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
            MemberDetails details = channel.addMember(credentials, ChatChannelGroup.MEMBER, player.getSession().makeNextChannelAndNumber(channelName), true);
            details.setOnline(true);
            channel.sendGeneralMessage(player.getColoredName() + ChatColor.AQUA + " has joined the channel.");
        } else {
            ChatMessage msg = new ChatMessage(channelName);

            if (msg.hasCussing()) {
                player.printError("You may not use profanity in channel names.");
                return true;
            }

            long settings = 0;

            String password = args.getString("setpass", "sp");
            boolean hidden = args.hasOption("hide", "h");

            if (password != null) {
                player.printInfo("This channel will be password protected. Password: " + password);
            }

            if (hidden) {
                settings |= ChannelSettings.HIDDEN.getBit();
                player.printInfo("This channel will be hidden on the channel list.");
            }

            ChatChannel channel = ChatChannelHandler.createChannel(channelName, settings, password);
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
            MemberDetails details = channel.addMember(credentials, ChatChannelGroup.OWNER, player.getSession().makeNextChannelAndNumber(channelName), true);
            details.setOnline(true);
            channel.sendGeneralMessage(player.getColoredName() + ChatColor.AQUA + " has joined the channel.");
        }

        return true;
    }

    @CommandMethod(aliases = {"leave", "l"},
    description = "Leaves a chat channel specified by its ID or name.",
    permission = Permission.command_misc_chatchannel_leave,
    usage = "/leave <channel number/name>",
    serverCommand = false)
    public static boolean commandLeave(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        if (args.length == 0) {
            return false;
        }
        String channelName = args[0];

        if (channelName.equalsIgnoreCase("all") && player.hasPermission(Permission.command_misc_chatchannel_joinall)) {
            ChatChannelHandler.removeGlobalListener(player.getName());
            player.printInfo("You have left all channels.");
            return true;
        }

        try {
            int num = Integer.parseInt(channelName);
            String tmp = player.getSession().getChannelNameFromNumber(num);
            if (tmp != null) {
                channelName = tmp;
            }
        } catch (NumberFormatException ex) {
        }

        if (ChatChannelHandler.isValidChannel(channelName)) {
            ChatChannel channel = ChatChannelHandler.getChannel(channelName);

            if (!channel.containsMember(player.getName())) {
                player.printError("You are not a part of this channel.");
                return true;
            }

            channel.sendGeneralMessage(player.getColoredName() + ChatColor.AQUA + " has left the channel.");
            channel.removeMember(player.getName());

            if (channel.isEmpty()) {
                ChatChannelHandler.deleteChannel(channel);
            }
        } else {
            player.printError("That is not a valid channel.");
        }

        return true;
    }

    @CommandMethod(aliases = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"},
    description = "A command that lets you talk in a channel.",
    permission = Permission.command_misc_chatchannel_speak,
    usage = "/channelnumber <message>",
    serverCommand = false,
    hideinlists = true)
    public static boolean commandChatChannel(InnPlugin parent, IdpPlayer player, CommandName cmdName, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String message = StringUtil.joinString(args, " ").trim();
        ChatChannel chan;

        if (message.isEmpty()) {
            player.printError("What do you want to say?");
            return true;
        }

        // Check for mute
        if (player.getSession().isMuted()) {
            player.printError("Unable to chat while muted.");
            return true;
        }

        try {
            int num = Integer.parseInt(cmdName.getName());
            String tmp = player.getSession().getChannelNameFromNumber(num);
            if (tmp == null) {
                player.printError("Unknown channel number!");
                return true;
            }
            chan = ChatChannelHandler.getChannel(tmp);
        } catch (NumberFormatException ex) {
            player.printError("Invalid channel number!");
            return true;
        }

        chan.sendChatMessage(player, message, ChatChannelHandler.getGlobalListeners());
        return true;
    }

    @CommandMethod(aliases = {"channel", "ch"},
    description = "A command to manage a chat channel by its ID or name.",
    permission = Permission.command_player_channel,
    usage = "/channel <channel/ID> [-banned] [-op/-deop <username>] [-disband] [-kick, -k/-ban, -b/-unban, -ub <username>] [-setpass, -sp <password>] [-giveup, -setowner, -gu, -so <new owner>] [-clearpass] [-hide, -h] [-unhide, -uh] [-information, -info] [-invite <username>] [-rename <new name>]",
    serverCommand = true)
    public static boolean commandChannel(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() > 0) {
            String channelName = args.getString(0);

            if (sender instanceof IdpPlayer) {
                IdpPlayer player = (IdpPlayer) sender;

                try {
                    int num = Integer.parseInt(args.getString(0));
                    String tempChannel = player.getSession().getChannelNameFromNumber(num);

                    if (tempChannel != null) {
                        channelName = tempChannel;
                    }
                } catch (NumberFormatException nfe) {
                }
            }

            if (!ChatChannelHandler.isValidChannel(channelName)) {
                sender.printError("That channel doesn't exist!");
                return true;
            }

            ChatChannel channel = ChatChannelHandler.getChannel(channelName, false);

            // Console chat!
            if (sender instanceof IdpConsole && args.getActionSize() > 1) {
                String consoleMsg = ChatColor.AQUA + args.combineActions(1);

                sender.printRaw("[" + ChatColor.AQUA + channel.getName() + ChatColor.WHITE + "] " + sender.getColoredName()
                              + ChatColor.WHITE + ": " + consoleMsg);
                channel.sendGeneralMessage(sender.getColoredName() + ChatColor.WHITE + ": " + consoleMsg);

                return true;
            }

            // If the user does not have global permissions, check for channel member rank
            if (!sender.hasPermission(Permission.special_chatroom_override)) {
                boolean hasPermission = false;
                MemberDetails details = channel.getMemberDetails(sender.getName());

                if (details != null) {
                    ChatChannelGroup requiredGroup = null;

                    if (args.hasArgument("setpass", "sp", "op", "deop", "giveup", "gu", "setowner", "so", "invite", "rename")
                            || args.hasOption("clearpass", "disband", "hide", "h", "unhide", "uh")) {
                        requiredGroup = ChatChannelGroup.OWNER;
                    } else if (args.hasArgument("kick", "ban", "k", "b", "unban", "ub")
                            || args.hasOption("banned")) {
                        requiredGroup = ChatChannelGroup.OPERATOR;
                    } else if (args.hasOption("information", "info")) {
                        requiredGroup = ChatChannelGroup.MEMBER;
                    }

                    if (requiredGroup == null) {
                        return false;
                    }

                    hasPermission = details.getGroup().equalsOrInherits(requiredGroup);
                }

                if (!hasPermission) {
                    sender.printError("You do not have permission to do this.");
                    return true;
                }
            }

            if (args.hasArgument("giveup", "gu")) {
                String playerName = args.getString("giveup", "gu");
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player does not exist.");
                    return true;
                } else {
                    // Get the proper casing of the name
                    playerName = credentials.getName();
                }

                if (!channel.containsMember(playerName)) {
                    sender.printError("That player isn't part of this channel!");
                    return true;
                }

                MemberDetails details = channel.getMemberDetails(playerName);

                if (details.getGroup() == ChatChannelGroup.OWNER) {
                    sender.printError("This player is already the owner!");
                    return true;
                }

                String coloredName = PlayerUtil.getColoredName(playerName);
                channel.switchOwner(credentials);
                channel.sendGeneralMessage("The owner has been changed to " + coloredName + ChatColor.AQUA + "!");

                if (!channel.containsMember(sender.getName())) {
                    sender.print(ChatColor.AQUA, "Set the owner of " + channel.getName() + " to " + coloredName + ChatColor.AQUA + "!");
                }

                return true;
            } else if (args.hasArgument("op", "deop")) {
                String playerName = args.getString("op", "deop");
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player doesn't exist!");
                    return true;
                } else {
                    // Get the proper casing of the name
                    playerName = credentials.getName();
                }

                String coloredName = PlayerUtil.getColoredName(playerName);
                boolean doOp = args.hasArgument("op");

                if (channel.containsMember(playerName)) {
                    MemberDetails memberDetails = channel.getMemberDetails(playerName);

                    if (memberDetails.getGroup().equals(ChatChannelGroup.OWNER)) {
                        sender.printError("Cannot op or deop the channel owner.");
                        return true;
                    }

                    ChatChannelGroup checkGroup = (doOp ? ChatChannelGroup.OPERATOR : ChatChannelGroup.MEMBER);

                    if (memberDetails.getGroup().equals(checkGroup)) {
                        sender.printError("This player is " + (doOp ? "already" : "not") + " an operator!");
                        return true;
                    }

                    channel.modifyMemberGroup(playerName, checkGroup);

                    if (channel.isCached()) {
                        channel.sendGeneralMessage(coloredName + ChatColor.AQUA + " is " + (doOp ? "now" : "no longer") + " an operator!");
                    }

                    if (!channel.containsMember(sender.getName())) {
                        sender.printInfo(coloredName + ChatColor.AQUA + " is " + (doOp ? "now" : "no longer") + " an operator in the channel " + channel.getName() + "!");
                    }
                } else {
                    sender.printError(coloredName + ChatColor.RED + " is not in the channel " + channel.getName() + "!");
                }

                return true;
            } else if (args.hasArgument("kick", "k")) {
                String playerName = args.getString("kick", "k");
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player doesn't exist!");
                    return true;
                } else {
                    // Get the proper casing of the name
                    playerName = credentials.getName();
                }

                if (playerName.equalsIgnoreCase(sender.getName())) {
                    sender.printError("You cannot kick yourself!");
                    return true;
                }

                if (!canModerate(playerName, parent) && !(sender.hasPermission(Permission.special_chatroom_override))) {
                    sender.printError("That player cannot be kicked.");
                    return true;
                }

                String coloredName = PlayerUtil.getColoredName(playerName);

                if (channel.containsMember(playerName)) {
                    MemberDetails senderDetails = channel.getMemberDetails(sender.getName());
                    MemberDetails memberDetails = channel.getMemberDetails(playerName);

                    if (!memberDetails.getGroup().equalsOrInherits(ChatChannelGroup.OPERATOR)
                            || (senderDetails != null && senderDetails.getGroup().equals(ChatChannelGroup.OWNER))
                            || sender.hasPermission(Permission.special_chatroom_override)) {
                        if (channel.isCached()) {
                            channel.sendGeneralMessage(coloredName + ChatColor.AQUA + " has been kicked from the channel!");
                            channel.sendGeneralMessage(coloredName + ChatColor.AQUA + " has left the channel!");
                        }

                        channel.removeMember(playerName);

                        if (!channel.containsMember(sender.getName())) {
                            sender.printInfo(coloredName + ChatColor.AQUA + " has been kicked from channel " + channel.getName() + "!");
                        }

                        // Delete channel if this resulted in the channel being empty
                        if (channel.isEmpty()) {
                            sender.printInfo(ChatColor.AQUA + "The channel " + channel.getName() + " was disbanded due to being empty.");
                            ChatChannelHandler.deleteChannel(channel);
                        }
                    } else {
                        sender.printError("You cannot kick " + coloredName + ChatColor.RED + " from the channel.");
                    }
                } else {
                    sender.printError(coloredName + ChatColor.RED + " is not in the channel " + channel.getName() + "!");
                }

                return true;
            } else if (args.hasArgument("ban", "b")) {
                String playerName = args.getString("ban", "b");
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player does not exist.");
                    return true;
                } else {
                    // Get the proper casing of the name
                    playerName = credentials.getName();
                }

                if (playerName.equalsIgnoreCase(sender.getName())) {
                    sender.printError("You cannot ban yourself!");
                    return true;
                }

                if (!canModerate(playerName, parent) && !(sender.hasPermission(Permission.special_chatroom_override))) {
                    sender.printError("That user cannot be banned.");
                    return true;
                }

                if (channel.isBanned(playerName)) {
                    sender.printError(playerName + " is already banned from that channel!");
                    return true;
                }

                String coloredName = PlayerUtil.getColoredName(playerName);
                MemberDetails memberDetails = channel.getMemberDetails(playerName);
                MemberDetails senderDetails = channel.getMemberDetails(sender.getName());

                if ((memberDetails == null || !memberDetails.getGroup().equalsOrInherits(ChatChannelGroup.OPERATOR)
                        || (senderDetails != null && senderDetails.getGroup().equals(ChatChannelGroup.OWNER))
                        || sender.hasPermission(Permission.special_chatroom_override))) {
                    if (channel.isCached()) {
                        channel.sendGeneralMessage(coloredName + ChatColor.RED + " has been banned from the channel!");
                    }

                    if (channel.containsMember(playerName)) {
                        channel.removeMember(playerName);
                        channel.sendGeneralMessage(coloredName + ChatColor.AQUA + " has left the channel!");
                    }

                    // Delete channel if this resulted in the channel being empty
                    if (channel.isEmpty()) {
                        ChatChannelHandler.deleteChannel(channel);
                    } else {
                        channel.addBanned(credentials);

                        if (!channel.containsMember(sender.getName())) {
                            sender.printInfo(coloredName + ChatColor.RED + " has been banned from the channel " + channel.getName() + "!");
                        }
                    }
                } else {
                    sender.printError(coloredName + ChatColor.RED + " cannot be banned from channel " + channel.getName() + ".");
                }

                return true;
            } else if (args.hasArgument("unban", "ub")) {
                String playerName = args.getString("unban", "ub");
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player does not exist.");
                    return true;
                } else {
                    // Get the proper casing of the name
                    playerName = credentials.getName();
                }

                String coloredName = PlayerUtil.getColoredName(playerName);

                if (!channel.isBanned(playerName)) {
                    sender.printError(coloredName + ChatColor.RED + " is not banned from this channel.");
                    return true;
                }

                channel.removeBanned(playerName);

                if (channel.isCached()) {
                    channel.sendGeneralMessage(coloredName + ChatColor.RED + " has been unbanned from the channel!");
                }

                if (!channel.containsMember(sender.getName())) {
                    sender.printInfo(coloredName + ChatColor.RED + " has been unbanned from the channel " + channel.getName() + "!");
                }

                return true;
            } else if (args.hasArgument("invite")) {
                if (!sender.isPlayer()) {
                    sender.printError("Console cannot invite users to channels!");
                    return true;
                }

                IdpPlayer player = (IdpPlayer) sender;

                String playerName = args.getString("invite");
                IdpPlayer testPlayer = parent.getPlayer(playerName);

                if (testPlayer == null || !testPlayer.isOnline()) {
                    sender.printError("That player is not online.");
                    return true;
                } else {
                    playerName = testPlayer.getName();
                }

                if (channel.containsMember(playerName)) {
                    sender.printError(playerName + " is already in that channel.");
                    return true;
                }

                ChannelJoinRequest cjr = new ChannelJoinRequest(parent, testPlayer, player, channelName);
                testPlayer.getSession().addRequest(cjr);

                player.print(ChatColor.AQUA, "You sent a request to " + testPlayer.getColoredDisplayName(), " to join the channel " + channelName + ".");

                testPlayer.print(ChatColor.AQUA, player.getColoredDisplayName(), " has invited you to join the channel " + channelName + ".");
                testPlayer.print(ChatColor.AQUA, "Please type /accept or /reject within 30 seconds.");

                TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
                text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
                text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
                testPlayer.print(text);

                return true;
            } else if (args.hasArgument("rename")) {
                String newChannel = args.getString("rename");
                ChatMessage msg = new ChatMessage(newChannel);

                if (msg.hasCussing()) {
                    sender.printError("You cannot use profanity in the channel name.");
                    return true;
                }

                if (ChatChannelHandler.isValidChannel(newChannel)) {
                    sender.printError("That channel already exists.");
                    return true;
                }

                channel.sendGeneralMessage(sender.getColoredName() + ChatColor.AQUA + " has renamed the channel " + newChannel + ".");
                ChatChannelHandler.renameChannel(channel, newChannel);

                return true;
            } else if (args.hasOption("banned")) {
                List<String> bannedUsers = channel.getBanned();

                if (bannedUsers.size() > 0) {
                    String bannedString = "";

                    for (String bannedMember : channel.getBanned()) {
                        String coloredName = PlayerUtil.getColoredName(bannedMember);

                        if (bannedString.isEmpty()) {
                            bannedString = coloredName;
                        } else {
                            bannedString += ChatColor.WHITE + ", " + coloredName;
                        }
                    }

                    sender.printInfo("Banned users in channel " + channel.getName() + ": " + bannedString);
                } else {
                    sender.printInfo("No users are banned in channel " + channel.getName() + ".");
                }

                return true;
            } else if (args.hasArgument("setpass", "sp")) {
                String password = args.getString("setpass", "sp");
                channel.setPassword(password);
                sender.printInfo("New password set for channel " + channel.getName() + " set to: " + password);
                return true;
            } else if (args.hasOption("clearpass")) {
                channel.setPassword(null);
                sender.printInfo("The channel " + channel.getName() + " no longer requires a password to enter.");
                return true;
            } else if (args.hasOption("disband")) {
                if (channel.isCached()) {
                    channel.sendGeneralMessage(sender.getColoredName() + ChatColor.AQUA + " has disbanded this channel!");
                }

                ChatChannelHandler.deleteChannel(channel);

                if (!channel.containsMember(sender.getName())) {
                    sender.printError("The channel " + channel.getName() + " has been disbanded!");
                }

                return true;
            } else if (args.hasOption("hide", "unhide", "h", "uh")) {
                boolean hide = args.hasOption("hide", "h");
                boolean isHidden = channel.hasSetting(ChannelSettings.HIDDEN);
                if (hide && isHidden) {
                    sender.printError("The channel is already hidden!");
                    return true;
                } else if (!hide && !isHidden) {
                    sender.printError("The channel is already unhidden!");
                    return true;
                }

                if (hide) {
                    channel.setSetting(ChannelSettings.HIDDEN);
                } else {
                    channel.clearSetting(ChannelSettings.HIDDEN);
                }

                sender.printInfo("The channel has been made " + (hide ? "hidden" : "unhidden") + ".");
                return true;
            } else if (args.hasOption("information", "info")) {
                String owner = channel.getOwner();
                String coloredOwner = PlayerUtil.getColoredName(owner);
                boolean isHidden = channel.hasSetting(ChannelSettings.HIDDEN);
                boolean requiresPassword = channel.isRequiringPassword();
                String channelPassword = channel.getPassword();

                sender.printInfo("Channel information for channel: " + ChatColor.YELLOW + channel.getName());
                sender.printInfo("Owner: " + coloredOwner);
                sender.printInfo("Channel is hidden: " + ChatColor.YELLOW + isHidden);
                sender.printInfo("Channel password: " + (requiresPassword ? ChatColor.YELLOW + channelPassword : ChatColor.RED + "none set"));

                List<PlayerCredentials> ops = channel.getMembers(ChatChannelGroup.OPERATOR, false);
                String coloredOpsString = "";

                for (PlayerCredentials pc : ops) {
                    String coloredOp = PlayerUtil.getColoredName(pc.getName());

                    if (coloredOpsString.isEmpty()) {
                        coloredOpsString = coloredOp;
                    } else {
                        coloredOpsString += ChatColor.YELLOW + ", " + coloredOp;
                    }
                }

                String operatorString = "Operators: ";

                if (!coloredOpsString.isEmpty()) {
                    operatorString += coloredOpsString;
                } else {
                    operatorString += ChatColor.YELLOW + "none";
                }

                sender.printInfo(operatorString);

                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @CommandMethod(aliases = {"channellist", "chanlist", "chlist"},
    description = "Lists all channels, or users in a channel.",
    permission = Permission.command_misc_chatchannel_list,
    usage = "/chanlist [channel number/name [-showoffline, -so]] [-page, -p <page number>] [-mychans]",
    serverCommand = true)
    public static boolean commandChannelList(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.hasOption("mychans")) {
            if (sender instanceof IdpPlayer) {
                Map<Integer, String> playerChannels = ((IdpPlayer) sender).getSession().getChannels();

                if (playerChannels.isEmpty()) {
                    sender.printError("You have no channels!");
                    return true;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Your channels (").append(playerChannels.size()).append(" total): ");
                for (Map.Entry<Integer, String> set : playerChannels.entrySet()) {
                    int num = set.getKey();
                    String chan = set.getValue();
                    sb.append(ChatColor.AQUA).append(chan).append(ChatColor.DARK_GREEN).append(" (ID: ").append(num).append(")").append(", ");
                }

                sender.printInfo(sb.substring(0, sb.length() - 2));
            } else {
                sender.printError("Console doesn't have channels!");
            }
        } else if (args.getActionSize() > 0) {
            String channelName = args.getString(0);

            if (sender instanceof IdpPlayer) {
                IdpPlayer player = (IdpPlayer) sender;

                try {
                    int num = Integer.parseInt(channelName);
                    String tmp = player.getSession().getChannelNameFromNumber(num);
                    if (tmp != null) {
                        channelName = tmp;
                    }
                } catch (NumberFormatException ex) {
                }
            }

            if (!ChatChannelHandler.isValidChannel(channelName)) {
                sender.printError("That channel does not exist!");
                return true;
            }

            boolean showOffline = args.hasOption("showoffline", "so");

            ChatChannel chan = ChatChannelHandler.getChannel(channelName, false);

            StringBuilder sb = new StringBuilder();
            int totalOffline = 0, totalOnline = 0;

            for (PlayerCredentials pc : chan.getMembers()) {
                boolean addMember = false;
                String member = pc.getName();
                IdpPlayer player = parent.getPlayer(member);
                String addedTag = "";

                if (player != null) {
                    totalOnline++;
                    addMember = true;
                } else {
                    totalOffline++;

                    if (showOffline) {
                        addMember = true;
                        addedTag = " (offline)";
                    }
                }

                if (addMember) {
                    String coloredName = PlayerUtil.getColoredName(member);
                    sb.append(chan.getOwner().equalsIgnoreCase(member) ? ChatColor.LIGHT_PURPLE + "*" : "").append(coloredName).append(ChatColor.YELLOW).append(addedTag).append(", ");
                }
            }

            int totalMembers = (totalOffline + totalOnline);

            if (totalMembers == 0) {
                sender.printError("There are no players in channel " + chan.getName() + "!");
            } else {
                MemberDetails details = chan.getMemberDetails(sender.getName());
                int personalnum = 0;

                if (details != null) {
                    personalnum = details.getPersonalNumber();
                }

                String chanName = ChatColor.YELLOW + chan.getName() + ChatColor.DARK_GREEN;
                sender.printInfo("Channel listing for channel: " + chanName + (personalnum > 0 ? " (personal number: " + personalnum + ")" : ""));

                if (!showOffline) {
                    sender.printInfo((totalOnline > 0 ? totalOnline + " online member" + (totalOnline != 1 ? "s" : "") + ": " + sb.substring(0, sb.length() - 2) : ChatColor.RED + "No online members!"));
                } else {
                    sender.printInfo(totalMembers + " total member" + (totalMembers != 1 ? "s" : "") + ": " + sb.substring(0, sb.length() - 2));
                }

                if (totalOffline > 0 && !showOffline) {
                    sender.print(ChatColor.YELLOW, totalOffline + " offline member" + (totalOffline != 1 ? "s were not " : " is not ") + "shown. Use -showoffline to display them.");
                }
            }
        } else {
            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        sender.printError("Page must be greater than 0.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    sender.printError("Page is not a number!");
                    return true;
                }
            }

            List<ChatChannel> channels = ChatChannelHandler.getAllChannels();
            List<String> contents = new ArrayList<String>();
            int channelCount = 0;

            for (ChatChannel channel : channels) {
                boolean isHidden = channel.hasSetting(ChannelSettings.HIDDEN);
                boolean isPassworded = (channel.getPassword() != null && !channel.getPassword().isEmpty());
                boolean canSeeChannel = (!isHidden || channel.containsMember(sender.getName())
                        || sender.hasPermission(Permission.special_chatroom_override));

                if (canSeeChannel) {
                    channelCount++;

                    ChatColor displayColor = ChatColor.WHITE;

                    if (channel.containsMember(sender.getName())) {
                        displayColor = ChatColor.YELLOW;
                    } else if (isPassworded) {
                        displayColor = ChatColor.RED;
                    } else if (isHidden) {
                        displayColor = ChatColor.DARK_RED;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(displayColor).append(channel.getName()).append(" (").append(channel.getMembers().size()).append(" members");
                    if (channel.countOfflineMembers() > 0) {
                        sb.append(", ").append(channel.countOfflineMembers()).append(" offline");
                    }
                    sb.append(")");

                    contents.add(sb.toString());
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, contents);

            if (ph.isValidPage()) {
                sender.printInfo(ChatColor.AQUA + "Innectis Channel Listing (" + channelCount + " channel" + (channelCount != 1 ? "s" : "") + ")");

                String legendText = ChatColor.AQUA + "Legend: " + ChatColor.YELLOW + "Member " + ChatColor.WHITE + "Open " + ChatColor.RED + "Password Protected";

                if (sender.hasPermission(Permission.special_chatroom_override)) {
                    legendText += ChatColor.DARK_RED + " Hidden";
                }

                sender.printInfo(legendText);
                sender.printInfo(ChatColor.AQUA + "page " + pageNo + " of " + ph.getMaxPage());
                sender.printInfo("");

                for (String str : ph.getParsedInfo()) {
                    sender.printInfo(str);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }
        }

        return true;
    }

    /**
     * Determines if the specified user can be moderated against with
     * channel kick/ban commands
     * @param playerName
     * @return
     */
    private static boolean canModerate(String playerName, InnPlugin parent) {
        UUID playerId = getPlayerId(playerName);
        PlayerSession session = PlayerSession.getActiveSession(playerId);
        boolean tempSession = false;

        if (session == null) {
            session = PlayerSession.getSession(playerId, playerName, parent, true);
            tempSession = true;
        }

        boolean isModeratable = !session.hasPermission(Permission.special_chatroom_override);

        if (tempSession) {
            session.destroy();
        }

        return isModeratable;
    }

    /**
     * Gets the unique ID of the specified player
     * @param playerName
     * @return
     */
    private static  UUID getPlayerId(String playerName) {
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);
        return credentials.getUniqueId();
    }

}

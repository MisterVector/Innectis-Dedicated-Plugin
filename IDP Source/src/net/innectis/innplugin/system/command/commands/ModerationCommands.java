package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.system.bans.BanHandler;
import net.innectis.innplugin.system.bans.Ban;
import net.innectis.innplugin.system.bans.IPBan;
import net.innectis.innplugin.system.bans.IPBanGroup;
import net.innectis.innplugin.system.bans.UserBan;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.innectis.innplugin.system.bans.BanState;
import net.innectis.innplugin.system.bans.BanHandler.BanResult;
import net.innectis.innplugin.system.bans.BanHandler.LinkStatus;
import net.innectis.innplugin.system.bans.BanHandler.UnlinkStatus;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.objects.IPLogger;
import net.innectis.innplugin.items.Bookinfo;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.system.warps.IdpWarp;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.loggers.ChatLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatInjector;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.infractions.InfractInjector;
import net.innectis.innplugin.player.infractions.Infraction;
import net.innectis.innplugin.player.infractions.InfractionIntensity;
import net.innectis.innplugin.player.infractions.InfractionManager;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;

public final class ModerationCommands {

    @CommandMethod(aliases = {"infract", "warn"},
    description = "Gives the player an infraction.",
    permission = Permission.command_moderation_infract,
    usage = "/infract <username> <intensity> <reason>",
    serverCommand = true)
    public static boolean commandInfract(InnPlugin parent, IdpCommandSender sender, SmartArguments args) {
        if (args.size() < 3) {
            return false;
        }

        String playerName = args.getString(0);
        IdpPlayer target = parent.getPlayer(playerName);

        if (target != null) {
            playerName = target.getName();
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        if (credentials == null) {
            sender.printError("That player does not exist.");
            return true;
        }

        String intensityString = args.getString(1);

        // Subcommand to revoke infractions
        if (StringUtil.matches(playerName, "r", "revoke")) {
            int id;
            try {
                id = args.getInt(2);
            } catch (NumberFormatException nfe) {
                sender.printError("No valid ID given!");
                return true;
            }

            Infraction inf = InfractionManager.getManager().getInfraction(id);
            if (inf == null) {
                sender.printError("Infraction #" + id + " not found!");
                return true;
            } else if (inf.isRevoked()) {
                sender.printError("Infraction #" + id + " already revoked by " + inf.getRevokerCredentials().getName());
                return true;
            } else {
                Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
                InfractionManager.getManager().revokeInfraction(inf, sender.getName(), date);
                sender.printInfo("Infraction #" + id + " was revoked!");
            }
            // Dont continue when revoke is given!
            return true;
        }

        // Get the infraction intensity
        InfractionIntensity infIntensity;
        if (StringUtil.matches(intensityString, "high", "h")) {
            infIntensity = InfractionIntensity.HIGH;
        } else if (StringUtil.matches(intensityString, "med", "mid", "middle", "m", "normal", "n")) {
            infIntensity = InfractionIntensity.MIDDLE;
        } else if (StringUtil.matches(intensityString, "low", "l")) {
            infIntensity = InfractionIntensity.LOW;
        } else if (StringUtil.matches(intensityString, "notice", "n")) {
            infIntensity = InfractionIntensity.NOTICE;
        } else {
            sender.printError("Unknown intensity! try: (high, mid, low, notice)");
            return true;
        }

        // The summary
        String summary = args.getJoinedStrings(2);

        // Make an injector for the details message
        ChatInjector injector = new InfractInjector(credentials, infIntensity, summary);

        if (sender.isPlayer()) {
            PlayerSession currPlayer = ((IdpPlayer) sender).getSession();
            currPlayer.setChatInjector(injector);

            sender.printInfo("Write chat to show infraction details.");
            sender.printInfo("Type 'quit' to save and exit.");

        } else {
            // If server, close the injector right away.
            // Currently no way for the injector to be hooked into the console.
            injector.onChat(sender, "quit");
        }

        return true;
    }

    @CommandMethod(aliases = {"showinfractions", "showinfraction", "showinf", "playerinfs"},
    description = "Shows the infractions of a player.",
    permission = Permission.command_moderation_infract,
    usage = "/showinfractions [username [-all] [-page <page>]] [-id <id> [-book]]",
    serverCommand = true)
    public static boolean commandShowInfraction(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        if (args.size() == 1) {
            String playerName = args.getString(0);

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

            List<Infraction> infractions = InfractionManager.getManager().getInfractions(credentials, args.hasOption("all"));

            if (infractions.isEmpty()) {
                sender.printInfo("That player has no infractions!");
            } else {
                List<String> lines = new ArrayList<String>();

                // Add the infractions
                for (Infraction in : infractions) {
                    lines.add(StringUtil.format("{0}{1} - {2} {3} ({4})",
                            in.isRevoked() ? ChatColor.RED : ChatColor.GREEN,
                            in.getId(),
                            DateUtil.formatString(in.getDateGMT(), DateUtil.FORMAT_FULL_DATE_TIME),
                            "GMT",
                            in.getIntensity().name()));
                }

                int page = 0;

                try {
                    page = Integer.parseInt(args.getString("page", "p"));
                } catch (NumberFormatException nfe) {
                    sender.printError("Page is not a number.");
                    return true;
                }

                PagedCommandHandler ph = new PagedCommandHandler(page, lines);
                ph.setNewLinesPerPage(Configuration.INFRACTIONS_PER_PAGE);

                if (ph.isValidPage()) {
                    sender.printInfo("Printing infractions for " + playerName + " page: " + page + "/" + ph.getMaxPage());

                    for (String line : lines) {
                        sender.printInfo(line);
                    }
                } else {
                    sender.printError(ph.getInvalidPageNumberString());
                    return true;
                }
            }
        } else if (args.hasOption("id")) {
            int id;
            try {
                id = args.getInt("id");
            } catch (NumberFormatException nfe) {
                sender.printError("No valid ID given!");
                return true;
            }

            Infraction inf = InfractionManager.getManager().getInfraction(id);
            if (inf == null) {
                sender.printError("Infraction not found!");
            } else {
                // Check if the player wants it in book form
                if (sender.isPlayer() && args.hasOption("book")) {
                    IdpItemStack infractionBook = getInfractionBook(inf);
                    ((IdpPlayer) sender).addItemToInventory(infractionBook);
                    sender.printInfo("Book added with info of infraction #" + inf.getId());
                } else {
                    // Print it out as chat
                    TimeZone tz = DateUtil.TIMEZONE_GMT;

                    // Make a new section in chat (makes it easier to seperate multiple infractios)
                    sender.printInfo("----------------------------------");
                    sender.printInfo("Infraction #" + inf.getId());
                    sender.printInfo("Player " + inf.getPlayerCredentials().getName());
                    sender.printInfo("Intensity " + inf.getIntensity().name());
                    sender.printInfo("Created on " + DateUtil.formatString(inf.getDateGMT(), DateUtil.FORMAT_FULL_DATE_TIME, tz) + " (" + tz.getDisplayName() + ").");
                    sender.printInfo("Infracted by " + inf.getCreatorCredentials().getName());
                    sender.printInfo("Summary: " + inf.getSummary());

                    // Print revoke details.
                    if (inf.isRevoked()) {
                        String dateString = DateUtil.formatString(inf.getDateGMT(), DateUtil.FORMAT_FULL_DATE_TIME, tz) + " (" + tz.getDisplayName() + ")";
                        sender.printError("Revoked on " + dateString + " by " + inf.getRevokerCredentials().getName() + ".");
                    }

                    if (inf.getDetails() != null) {
                        sender.printInfo("Details:");
                        String[] detailsSplit = inf.getDetails().split(InfractInjector.ENTER_CHAR);
                        for (String string : detailsSplit) {
                            sender.printInfo(string);
                        }
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * This will create a WRITTEN_BOOK with information about the given infraciton.
     * @param inf
     * @return IdpItemStack with a written book containing the information of the infraction.
     */
    private static IdpItemStack getInfractionBook(Infraction inf) {
        IdpItemStack item = new IdpItemStack(IdpMaterial.WRITTEN_BOOK, 1);
        Bookinfo info = new Bookinfo();
        info.setAuthor("[SERVER]");
        info.setTitle("Infraction #" + inf.getId());

        // Init objects
        List<String> pages = new ArrayList<String>(50);
        Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();

        // Create the basic info
        StringBuilder basicInfo = new StringBuilder();

        basicInfo.append("Infraction #").append(inf.getId()).append(InfractInjector.ENTER_CHAR);
        basicInfo.append("Created on: ").append(DateUtil.formatString(date, DateUtil.FORMAT_FULL_DATE_TIME)).append(InfractInjector.ENTER_CHAR);
        basicInfo.append(InfractInjector.ENTER_CHAR);

        basicInfo.append(ChatColor.BLACK).append("Username ").append(ChatColor.DARK_AQUA).append(inf.getPlayerCredentials().getName()).append(InfractInjector.ENTER_CHAR);
        basicInfo.append(ChatColor.BLACK).append("Intensity ").append(ChatColor.DARK_AQUA).append(inf.getIntensity().name()).append(InfractInjector.ENTER_CHAR);

        basicInfo.append(ChatColor.BLACK).append("Date ").append(ChatColor.DARK_AQUA).append(DateUtil.formatString(inf.getDateGMT(), DateUtil.FORMAT_FULL_DATE_TIME)).append(" GMT").append(InfractInjector.ENTER_CHAR);
        basicInfo.append(ChatColor.BLACK).append("Staff ").append(ChatColor.DARK_AQUA).append(inf.getCreatorCredentials().getName()).append(InfractInjector.ENTER_CHAR);

        // Revoke details
        if (inf.isRevoked()) {
            basicInfo.append(ChatColor.BLACK).append("Revoked by: ").append(ChatColor.RED).append(inf.getRevokerCredentials().getName()).append(InfractInjector.ENTER_CHAR);
            basicInfo.append(ChatColor.BLACK).append("Revoked on: ").append(ChatColor.RED).append(DateUtil.formatString(inf.getRevokeDate(), DateUtil.FORMAT_FULL_DATE_TIME)).append(InfractInjector.ENTER_CHAR);
        }

        pages.add(basicInfo.toString());

        // Put summary on new page so we have enough space
        pages.add("Summary:" + InfractInjector.ENTER_CHAR + inf.getSummary());

        if (inf.getDetails() != null) {
            pages.add("Details on following pages");
            String[] detailsSplit = inf.getDetails().split(InfractInjector.ENTER_CHAR);
            for (String str : detailsSplit) {
                pages.add(str);
            }
        }

        info.setPages(pages);
        item.getItemdata().setBookinfo(info);

        return item;
    }

    @CommandMethod(aliases = {"ban"},
    description = "Bans the given player.",
    permission = Permission.command_moderation_ban,
    usage = "/ban <username> [-time (-t) <ban time(m, h, d, or s)>] [-ip] [-joinban (-jb)] [-link <username>]",
    serverCommand = true)
    public static boolean commandBan(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() > 0) {
            long banFormula = 0;

            if (args.getActionSize() > 1 && !args.hasArgument("time", "t")) {
                sender.printError("You need to specify time with the -t or -time switch.");
                return true;
            }

            IdpPlayer targetPlayer = parent.getPlayer(args.getString(0), false);
            String playerName = (targetPlayer != null ? targetPlayer.getName() : args.getString(0));

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player doesn't exist! Cannot ban.");
                return true;
            }

            if (args.hasArgument("time", "t")) {
                banFormula = DateUtil.getTimeFormula(args.getString("time", "t"));

                if (banFormula < 0) {
                    sender.printError("Invalid time unit specified. Use s, m, h, or d (Example: 20d or 20h5m)");
                    return true;
                }
            }

            boolean isIPBan = args.hasOption("ip");
            boolean isJoinBan = args.hasOption("joinban", "jb");
            boolean isLinkedBan = args.hasArgument("link");

            if (isJoinBan && targetPlayer != null && targetPlayer.isOnline()) {
                sender.printError("Cannot set join ban on a player already online!");
                return true;
            }

            if (isJoinBan && banFormula == 0) {
                sender.printError("Can't set join ban without a ban time.");
                return true;
            }

            Ban ban = BanHandler.getBan(credentials.getUniqueId());

            if (ban != null && (sender instanceof IdpPlayer) && !ban.canModifyBan(sender)) {
                sender.printError("You did not ban this player! Unable to modify.");
                return true;
            }

            if (isLinkedBan) {
                if (ban != null) {
                    boolean ipbanned = (ban instanceof IPBan);

                    sender.printError("This user is already " + (ipbanned ? "ip" : "") + "banned. Unable to link!");
                    return true;
                }

                String linkedUsername = args.getString("link");
                PlayerCredentials linkedCredentials = PlayerCredentialsManager.getByName(linkedUsername);

                if (linkedCredentials == null) {
                    sender.printError("The username to link this ban to does not exist!");
                    return true;
                }

                String coloredLinkedPlayerName = PlayerUtil.getColoredName(linkedCredentials);

                String ip;

                if (targetPlayer != null) {
                    ip = targetPlayer.getHandle().getAddress().getAddress().getHostAddress();
                } else {
                    ip = IPLogger.getLastUsedIP(credentials.getUniqueId(), credentials.getName());
                }

                String coloredPlayerName = PlayerUtil.getColoredName(credentials);
                LinkStatus status = BanHandler.linkIPBan(sender, credentials, ip, linkedCredentials);

                switch (status) {
                    case LINK_SUCCESSFUL:
                        sender.printInfo(coloredPlayerName + ChatColor.YELLOW + " was linked to " + coloredLinkedPlayerName + ChatColor.YELLOW + "'s ip ban.");
                        String resultMsg = playerName + " was linked to " + linkedUsername + "'s ip ban.";
                        parent.broadCastMessage(ChatColor.RED + Configuration.MESSAGE_PREFIX + resultMsg);
                        break;
                    case LINK_NOT_FOUND:
                        sender.printError(coloredLinkedPlayerName + ChatColor.YELLOW + " is not ipbanned. Unable to link.");
                        return true;
                    case LINK_NOT_CREATOR:
                        sender.printError("The ip ban to link this user to was not created by you. Unable to link.");
                        return true;
                    case LINK_SAME_USER:
                        sender.printError("This user is already linked to this ip ban.");
                        return true;
                }
            } else {
                if ((ban instanceof IPBan) && !isIPBan) {
                    IPBan ipban = (IPBan) ban;
                    String ipBannedPlayerString = "";
                    List<PlayerCredentials> ipBannedPlayers = ipban.getGroup().getPlayers();

                    for (PlayerCredentials pc : ipBannedPlayers) {
                        String coloredPlayerName = PlayerUtil.getColoredName(pc);

                        if (!ipBannedPlayerString.isEmpty()) {
                            ipBannedPlayerString += ChatColor.WHITE + ", ";
                        }

                        ipBannedPlayerString += coloredPlayerName;
                    }

                    if (ipBannedPlayers.size() > 1) {
                        sender.print(ChatColor.YELLOW, "You switched an ipban with multiple accounts to a ban.");
                        sender.print(ChatColor.YELLOW, "IP banned users: " + ipBannedPlayerString);
                    } else {
                        sender.print(ChatColor.RED, playerName + " was previously ipbanned. Changing to ban. ");
                    }

                    BanHandler.removeBan(ban);
                } else if ((ban instanceof UserBan) && isIPBan) {
                    sender.print(ChatColor.RED, playerName + " was previously banned. Changing to ipban.");
                    BanHandler.removeBan(ban);
                }

                PlayerCredentials bannedByPlayerCredentials = null;

                // Since console can ban players, our system won't work that way
                // so replace the console with the name of a stored player we have
                if (sender instanceof IdpConsole) {
                    bannedByPlayerCredentials = Configuration.SERVER_GENERATED_CREDENTIALS;
                } else {
                    bannedByPlayerCredentials = PlayerCredentialsManager.getByName(sender.getName());
                }

                if (isIPBan) {
                    List<String> IPs = new ArrayList<String>();
                    String ip = IPLogger.getLastUsedIP(credentials.getUniqueId(), credentials.getName());

                    if (ip != null) {
                        IPs.add(ip);
                    }

                    List<PlayerCredentials> players = new ArrayList<PlayerCredentials>();
                    players.add(credentials);
                    IPBanGroup group = new IPBanGroup(IPs, players);

                    ban = new IPBan(group, bannedByPlayerCredentials, new Timestamp(System.currentTimeMillis()), banFormula, isJoinBan);
                } else {
                    ban = new UserBan(credentials, bannedByPlayerCredentials, new Timestamp(System.currentTimeMillis()), banFormula, isJoinBan);
                }

                BanResult result = BanHandler.addBan(ban);
                String resultMsg = "";

                switch (result) {
                    case BAN_FRESH:
                    case BAN_FRESH_IP:
                        resultMsg = playerName + " was " + (result == BanResult.BAN_FRESH ? "banned " : "ipbanned ") + (banFormula == 0 ? "indefinitely" : "for " + DateUtil.getTimeString(banFormula, true)) + (isJoinBan ? " (effective on next join)" : "") + " by " + sender.getName() + ".";
                        break;
                    case BAN_EXISTING:
                    case BAN_EXISTING_IP:
                        resultMsg = playerName + "'s " + (result == BanResult.BAN_EXISTING ? "ban" : "ipban") + " was modified to " + (banFormula == 0 ? "indefinite" : DateUtil.getTimeString(banFormula, true)) + (isJoinBan ? " (effective on next join)" : "") + " by " + sender.getName() + ".";
                        break;
                }

                parent.broadCastMessage(ChatColor.RED + Configuration.MESSAGE_PREFIX + resultMsg);
            }

            if (targetPlayer != null) {
                targetPlayer.getHandle().kickPlayer("You have been " + (isIPBan ? "ip" : "") + "banned from the server. Check Innectis.net!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"unban"},
    description = "Unbans the given player.",
    permission = Permission.command_moderation_unban,
    usage = "/unban <playername> [-unlink]",
    serverCommand = true)
    public static boolean commandUnBan(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() > 0) {
            String playerName = BanHandler.getPartialName(args.getString(0));
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player does not exist.");
                return true;
            } else {
                // Get the proper casing of the name
                playerName = credentials.getName();
            }

            Ban ban = BanHandler.getBan(credentials.getUniqueId());

            if (ban == null) {
                sender.printError("That user is not banned.");
                return true;
            }

            if ((sender instanceof IdpPlayer) && !ban.canModifyBan(sender)) {
                sender.printError("You are unable to unban this player.");
                return true;
            }

            boolean isIPBan = (ban instanceof IPBan);
            boolean isUnlinkedBan = (args.hasOption("unlink"));

            if (isUnlinkedBan) {
                String ip = IPLogger.getLastUsedIP(credentials.getUniqueId(), credentials.getName());

                String coloredPlayerName = PlayerUtil.getColoredName(credentials);
                UnlinkStatus status = BanHandler.unlinkIPBan(sender, credentials, ip);

                switch (status) {
                    case UNLINK_FOUND:
                        sender.printInfo(coloredPlayerName + ChatColor.YELLOW + " was unlinked from a previous ip ban.");
                        break;
                    case UNLINK_NOT_FOUND:
                        sender.printError(playerName + " is not linked to any ip ban.");
                        return true;
                    case UNLINK_REMOVED_BAN:
                        sender.printInfo(coloredPlayerName + ChatColor.DARK_GREEN + " was unlinked from a previous ip ban");
                        sender.printInfo("As this was the only remaining user, this ipban has been removed.");
                        break;
                    case UNLINK_NOT_CREATOR:
                        sender.printError("Unable to unlink. The ip ban this links to was not created by you.");
                        return true;
                }

                parent.broadCastMessage(ChatColor.RED + Configuration.MESSAGE_PREFIX + playerName + " was unipbanned");
            } else {
                if (isIPBan) {
                    IPBan ipban = (IPBan) ban;

                    List<PlayerCredentials> players = ipban.getGroup().getPlayers();
                    String playerList = "";

                    for (PlayerCredentials pc : players) {
                        String coloredPlayerName = PlayerUtil.getColoredName(pc);

                        if (!playerList.isEmpty()) {
                            playerList += ChatColor.WHITE + ", ";
                        }

                        playerList += coloredPlayerName;
                    }

                    if (players.size() > 1) {
                        sender.printInfo("This was an ipban associated with more than one player.");
                        sender.printInfo("Players: " + playerList);
                    }
                }

                BanHandler.removeBan(ban);
                parent.broadCastMessage(ChatColor.RED + Configuration.MESSAGE_PREFIX + playerName + " was un" + (isIPBan ? "ip" : "") + "banned by " + sender.getName() + "!");
            }

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"banned"},
    description = "Returns a list of all banned users by a filter.",
    permission = Permission.command_moderation_banned,
    usage = "/banned [ban type number [-page, -p <page>]]",
    serverCommand = true)
    public static boolean commandBanned(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() > 0) {
            BanState state = null;

            try {
                int stateNumber = Integer.parseInt(args.getString(0));
                state = BanState.fromStateNumber(stateNumber);
            } catch (NumberFormatException nfe) {
                sender.printError("Ban type is not a number!");
                return true;
            }

            if (state == null) {
                sender.printError("Invalid ban state number.");
                return true;
            }

            List<Ban> bans = BanHandler.getBansByState(state);

            if (bans.isEmpty()) {
                sender.printError("There are no bans with the specified type.");
                return true;
            }

            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        sender.printError("Page cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    sender.printError("Page is not a number.");
                    return true;
                }
            }

            List<String> info = new ArrayList<String>();
            int idx = 1;

            for (Ban ban : bans) {
                StringBuilder sb = new StringBuilder();
                sb.append(idx++).append(". ");

                String[] details = ban.getBanDetails();
                sb.append(details[0]);
                info.add(sb.toString());

                if (details.length > 1) {
                    sb = new StringBuilder();
                    sb.append(details[1]);
                    info.add(sb.toString());
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, info);

            if (ph.isValidPage()) {
                sender.print(ChatColor.YELLOW, "Showing bans with the given type: " + ChatColor.GRAY + state.getTitle());
                sender.print(ChatColor.YELLOW, "Legend: player" + ChatColor.WHITE + ": " + ChatColor.YELLOW
                        + "ban creator" + ChatColor.WHITE + ", " + ChatColor.YELLOW + "start date"
                        + ChatColor.WHITE + ", " + ChatColor.YELLOW + "duration");
                sender.print(ChatColor.YELLOW, "Bans with a " + ChatColor.WHITE + "(" + ChatColor.YELLOW + "JB"
                        + ChatColor.WHITE + ")" + ChatColor.YELLOW + " are in joinban status");
                sender.print(ChatColor.YELLOW, "");

                sender.printInfo("There are " + bans.size() + " bans (page " + pageNo + " of " + ph.getMaxPage() + ")");
                sender.printInfo("");

                for (String line : ph.getParsedInfo()) {
                    sender.print(ChatColor.WHITE, line);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }

            return true;
        } else {
            sender.print(ChatColor.YELLOW, "Listing all ban counts by type");
            sender.print(ChatColor.YELLOW, "");

            for (BanState state : BanState.values()) {
                StringBuilder sb = new StringBuilder();
                List<Ban> bans = BanHandler.getBansByState(state);
                int automatic = 0;
                int joinban = 0;

                sb.append(ChatColor.WHITE).append(state.getStateNumber()).append(". ")
                        .append(ChatColor.GRAY).append(state.getTitle()).append(ChatColor.WHITE)
                        .append(" (").append(ChatColor.DARK_AQUA).append(bans.size())
                        .append(ChatColor.YELLOW).append(" bans").append(ChatColor.WHITE)
                        .append(", ");

                for (Ban ban : bans) {
                    if (ban.isAutomatic()) {
                        automatic++;
                    }

                    if (ban.isJoinBan()) {
                        joinban++;
                    }
                }

                sb.append(ChatColor.DARK_AQUA).append(joinban).append(ChatColor.YELLOW)
                        .append(" join bans").append(ChatColor.WHITE).append(", ")
                        .append(ChatColor.DARK_AQUA).append(automatic).append(ChatColor.YELLOW)
                        .append(" automatic").append(ChatColor.WHITE).append(")");

                sender.printRaw(sb.toString());
            }

            sender.print(ChatColor.YELLOW, "");
            sender.print(ChatColor.YELLOW, "Type /banned <ban type number> to list"
                + " bans with the specified ban number.");
        }

        return true;
    }

    @CommandMethod(aliases = {"whitelist"},
    description = "Manages the ban whitelist.",
    permission = Permission.command_moderation_whitelist,
    usage = "/whitelist [-add <username>] [-remove <username>] [-list [-page <number>]]",
    serverCommand = true)
    public static boolean commandWhitelist(IdpCommandSender sender, LynxyArguments args) {
        if (args.getArgumentSize() == 0 && args.getOptionSize() == 0) {
            return false;
        }

        if (args.hasOption("list", "l")) {
            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        sender.printError("Page number cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    sender.printError("Page number is not a number.");
                    return true;
                }
            }

            List<PlayerCredentials> whitelistPlayers = BanHandler.getWhitelist();

            if (whitelistPlayers.isEmpty()) {
                sender.printError("There are no whitelisted users.");
                return true;
            }

            List<String> whitelistNames = new ArrayList<String>();

            for (PlayerCredentials credentials : whitelistPlayers) {
                whitelistNames.add(credentials.getName());
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, whitelistNames);

            if (ph.isValidPage()) {
                ph.adjustEntriesPerLine(3);
                sender.printInfo("Listing " + ph.getStartLine() + "-" + ph.getEndLine() + " whitelisted players. Showing page: " + pageNo + " of " + ph.getMaxPage());

                for (String line : ph.getParsedInfo()) {
                    sender.printInfo(line);
                }
            } else {
                sender.printError("The page number is invalid.");
            }

            return true;
        }

        if (args.hasArgument("add", "a")) {
            String username = args.getString("add", "a");
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(username);

            if (credentials == null) {
                sender.printError("That username doesn't exist.");
                return true;
            }

            if (BanHandler.isWhitelisted(credentials.getUniqueId())) {
                sender.printError("That username is already whitelisted.");
                return true;
            }

            BanHandler.addWhitelist(credentials);

            sender.printInfo("Added " + username + " to the ban whitelist.");

            return true;
        }

        if (args.hasArgument("remove", "r")) {
            String username = args.getString("remove", "r");
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(username);

            if (credentials == null) {
                sender.printError("That username doesn't exist.");
                return true;
            }

            if (BanHandler.isWhitelisted(credentials.getUniqueId())) {
                BanHandler.removeWhitelist(credentials);
                sender.printInfo("Removed " + username + " from the ban whitelist.");
            } else {
                sender.printError(username + " is not on the ban whitelist.");
            }


            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"clearinv"},
    description = "Clears the inventory of the given player.",
    permission = Permission.command_moderation_clearinventory,
    usage = "/clearinv <playername>",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandClearinv(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            IdpPlayer player = parent.getPlayer(args[0], false);
            if (player != null) {
                player.clearInventory();
                sender.printInfo(player.getColoredName(), "'s inventory cleared");
            } else {
                sender.printError("Player ", args[0], " not found!");
            }
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"jail"},
    description = "Puts a player in jail.",
    permission = Permission.command_moderation_jail,
    usage = "/jail <username> OR <-list>",
    serverCommand = true)
    public static boolean commandJail(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.hasOption("list", "l")) {
            StringBuilder sb = new StringBuilder();
            for (PlayerSession session : PlayerSession.getSessions()) {
                if (session.isJailed()) {
                    sb.append(session.getColoredName()).append(" ");
                }
            }
            sender.printInfo("The following players are jailed:");
            sender.printInfo(sb.toString());
            return true;
        }

        if (args.getString(0) != null) {
            String playerName = args.getString(0);
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null && target.isOnline()) {
                if (target.getSession().isJailed()) {
                    sender.printError(target.getName() + " is already jailed!");
                    return true;
                }

                if (target.isOnline()) {
                    IdpWarp jail = WarpHandler.getJail();
                    if (jail.getLocation() == WarpHandler.getSpawn()) {
                        sender.printError("Jail warp was not found! User not jailed.");
                        return true;
                    }
                    if (target.teleport(jail)) {
                        target.getSession().setJailed(true);
                        sender.printInfo(target.getName() + " has been jailed!");
                        target.printError("You have been jailed.");
                    } else {
                        sender.printError("Failed to teleport user to jail!");
                    }
                }
            } else {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player doesn't exist!");
                    return false;
                }

                if (PlayerSession.hasSession(playerName)) {
                    PlayerSession session = PlayerSession.getSession_(playerName);
                    session.setJailed(true);
                    sender.printInfo(playerName + " has been jailed!");
                } else {
                    sender.printError("That player doesn't have an active session. Unable to jail!");
                }
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"kick"},
    description = "Kicks a player from the server.",
    permission = Permission.command_moderation_kick,
    usage = "/kick <playername> [reason]",
    serverCommand = true)
    public static boolean commandKick(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        IdpPlayer tarplayer = parent.getPlayer(args[0], false);

        if (tarplayer == null) {
            sender.printError("Player not found!");
            return true;
        }

        String reason = null;

        if (args.length >= 2) {
            String tempReasonString = "";

            for (int i = 1; i < args.length; i++) {
                if (!tempReasonString.isEmpty()) {
                    tempReasonString += " ";
                }

                tempReasonString += args[i];
            }

            reason = tempReasonString;
        }

        tarplayer.getHandle().kickPlayer("You were kicked from the server by " + sender.getName()
            + "!" + (reason != null ? " (" + reason + ")" : ""));
        parent.broadCastMessage(ChatColor.RED + Configuration.MESSAGE_PREFIX + "Player " + tarplayer.getName() + " was kicked by " + sender.getName()
                + "!" + (reason != null ? " (" + reason + ")" : ""));

        return true;
    }

    @CommandMethod(aliases = {"modmsg", "sc"},
    description = "Sends a message to the staff chat.",
    permission = Permission.command_moderation_modmsg,
    usage = "/modmsg <message>",
    serverCommand = true)
    public static boolean commandModMsg(InnPlugin parent, IdpCommandSender sender, SmartArguments args) {
        String msg = args.getJoinedStrings(0);
        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
        chatLogger.logChatboxChat("ModMsg", sender.getName(), msg);
        String staffMsg = ChatColor.WHITE + "[" + ChatColor.GREEN + "STAFF" + ChatColor.WHITE + "] "
                + sender.getColoredName() + ChatColor.WHITE + ": " + ChatColor.GREEN + msg;
        ChatSoundSetting setting = ChatSoundSetting.STAFF_CHAT;

        for (IdpPlayer player : parent.getOnlineStaff(false)) {
            player.printRaw(staffMsg);

            // Play chat sound only when the player that sent the message is not the one receiving it
            if (player.equals(sender)) {
                continue;
            }

            PlayerSession session = player.getSession();

            if (session.hasChatSoundSetting(setting)) {
                player.playChatSoundFromSetting(setting);
            }
        }

        // Log to console if console...
        if (!sender.isPlayer()) {
            InnPlugin.logInfo(ChatColor.WHITE + "[" + ChatColor.GREEN + "STAFF" + ChatColor.WHITE + "] "
                    + sender.getColoredName() + ChatColor.WHITE + ": " + ChatColor.GREEN + msg);
        }
        return true;
    }

    @CommandMethod(aliases = {"mute"},
    description = "Mutes a player.",
    permission = Permission.command_moderation_mute,
    usage = "/mute <username> [-list] [-time (-t) <time>]",
    serverCommand = true)
    public static boolean commandMute(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.hasOption("list", "l")) {
            StringBuilder sb = new StringBuilder(50);
            for (PlayerSession session : PlayerSession.getSessions()) {
                long muteTicks = session.getRemainingMuteTicks();

                if (muteTicks != 0) {
                    String muteString = session.getColoredName() + " ";

                    if (muteTicks > 0) {
                        muteString += ChatColor.WHITE + "(" + DateUtil.getTimeString(muteTicks, false) + " left" + ChatColor.WHITE + ")";
                    }

                    sb.append(muteString);
                }
            }

            sender.printInfo("The following players are muted:");
            sender.printInfo(sb.toString());
            return true;
        }

        if (args.getString(0) != null) {
            String playerName = args.getString(0);
            PlayerSession session = null;

            if (PlayerSession.hasSession(playerName)) {
                session = PlayerSession.getSession_(playerName);
            } else {
                sender.printError(playerName + " doesn't have an active session!");
                return true;
            }

            if (args.hasArgument("time", "t")) {
                long formula = DateUtil.getTimeFormula(args.getString("time", "t"));

                if (formula == -1) {
                    sender.printError("Incorrect mute formula. (Ex. 5m3s)");
                    return true;
                }

                session.setMuteTicks(System.currentTimeMillis() + formula);
                sender.printInfo(playerName + " is silenced for " + DateUtil.getTimeString(formula, true) + ".");
            } else {
                session.setMuteTicks(-1);
                sender.printInfo(playerName + " is silenced.");
            }

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"reminv"},
    description = "Removes an item from the player's inventory.",
    permission = Permission.command_moderation_removefrominventory,
    usage = "/reminv <player> <itemid[:data[:damage]]>",
    serverCommand = true)
    public static boolean commandRemoveFromInventory(InnPlugin parent, IdpCommandSender sender, String[] args) {
        try {
            if (args.length == 2) {
                IdpPlayer targetPlayer = parent.getPlayer(args[0], false);
                IdpMaterial mat = IdpMaterial.fromString(args[1]);

                if (targetPlayer.isOnline()) {
                    int amount = targetPlayer.getInventoryItemCount(mat);
                    targetPlayer.removeItemFromInventory(mat, amount);
                    sender.printInfo("Removed " + amount + " items of " + mat.getName() + " from " + targetPlayer.getName() + "'s inventory!");
                } else {
                    sender.printError("Player " + targetPlayer.getName() + " not found!");
                }
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.printError("Invalid itemid.");
        }
        return false;
    }

    @CommandMethod(aliases = {"unjail"},
    description = "Unjails a player.",
    permission = Permission.command_moderation_unjail,
    usage = "/unjail <username>",
    serverCommand = true)
    public static boolean commandUnJail(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];
            PlayerSession session = null;

            if (PlayerSession.hasSession(playerName)) {
                session = PlayerSession.getSession_(playerName);
            } else {
                sender.printError(playerName + " doesn't have an active session. Cannot unjail!");
                return true;
            }

            if (!session.isJailed()) {
                sender.printError(playerName + " is not jailed!");
                return true;
            }

            session.setJailed(false);
            sender.printInfo(playerName + " has been unjailed!");

            IdpPlayer tarplayer = parent.getPlayer(playerName);

            if (tarplayer != null && tarplayer.isOnline()) {
                tarplayer.teleport(WarpHandler.getSpawn(tarplayer.getGroup()), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);

                tarplayer.printInfo("You have been unjailed.");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"unmute"},
    description = "Unmutes a player.",
    permission = Permission.command_moderation_unmute,
    usage = "/unmute <username>",
    serverCommand = true)
    public static boolean commandUnMute(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];
            PlayerSession session = null;

            if (PlayerSession.hasSession(playerName)) {
                session = PlayerSession.getSession_(playerName);
            } else {
                sender.printError("That player doesn't have an active session. Cannot unmute!");
                return true;
            }

            long muteTicks = session.getRemainingMuteTicks();

            if (muteTicks == 0) {
                sender.printError(playerName + " is not muted!");
            } else {
                session.setMuteTicks(0);
                sender.printInfo(playerName + " is not longer muted.");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"freeze"},
    description = "Freezes a player.",
    permission = Permission.command_moderation_freeze,
    usage = "/freeze <username>",
    serverCommand = true)
    public static boolean commandFreeze(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];
            PlayerSession session = null;

            if (PlayerSession.hasSession(playerName)) {
                session = PlayerSession.getSession_(playerName);
            } else {
                sender.printError("That player doesn't have an active session. Unable to freeze!");
                return true;
            }

            if (session.isFrozen()) {
                sender.printError(playerName + " is already frozen!");
            } else {
                session.setFrozen(true);
                sender.printInfo(playerName + " was frozen.");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"unfreeze"},
    description = "Unfreezes a player.",
    permission = Permission.command_moderation_unfreeze,
    usage = "/unfreeze <username>",
    serverCommand = true)
    public static boolean commandUnFreeze(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];
            PlayerSession session = null;

            if (PlayerSession.hasSession(playerName)) {
                session = PlayerSession.getSession_(playerName);
            } else {
                sender.printError(playerName + " does not have an active session. Unable to unfreeze!");
                return true;
            }

            if (!session.isFrozen()) {
                sender.printError(playerName + " is not frozen!");
            } else {
                session.setFrozen(false);
                sender.printInfo(playerName + " is not longer frozen.");
            }

            return true;
        }
        return false;
    }

}

package net.innectis.innplugin.system.command.commands;

import com.mysql.jdbc.StringUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.external.api.VotifierIDP;
import net.innectis.innplugin.external.ExternalLibraryManager;
import net.innectis.innplugin.external.LibraryType;
import net.innectis.innplugin.external.VotifierService;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.handlers.StaffMessageHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.objects.StaffMessage;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.inventory.payload.BackpackInventoryPayload;
import net.innectis.innplugin.inventory.payload.MiningStickSettingsPayload;
import net.innectis.innplugin.inventory.payload.RedeemInventoryPayload;
import net.innectis.innplugin.inventory.payload.ToggleInventoryPayload;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.items.RewardItem;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.loggers.MailLogger;
import net.innectis.innplugin.system.mail.MailMessage;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerBackpack;
import net.innectis.innplugin.player.PlayerBonus;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSecurity;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.renames.PlayerRename;
import net.innectis.innplugin.player.renames.PlayerRenameHandler;
import net.innectis.innplugin.player.request.Request;
import net.innectis.innplugin.player.request.TransferCaughtEntitiesRequest;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowSystemUtil;
import net.innectis.innplugin.system.window.windows.MiningStickSettingsWindow;
import net.innectis.innplugin.system.window.windows.ToggleWindow;
import net.innectis.innplugin.util.ColorUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author AlphaBlend
 */
public final class PlayerCommands {

    @CommandMethod(aliases = {"die"},
    description = "Kills yourself at the expense of losing your experience.",
    permission = Permission.command_player_die,
    usage = "/die",
    serverCommand = false)
    public static boolean commandSuicide(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        player.getHandle().setLastDamageCause(new EntityDamageEvent(player.getHandle(), DamageCause.SUICIDE, 1000.0D));
        player.getHandle().setLevel(0);
        player.getHandle().setExp(0);
        player.getHandle().setHealth(0.0D);
        player.printInfo("You have killed yourself and lost all your exp!");

        return true;
    }

    @CommandMethod(aliases = {"findrenamedplayer", "frp"},
    description = "A command to find the most current name of a player.",
    permission = Permission.command_player_findrenamedplayer,
    usage = "/findrenamedplayer <any player name>",
    usage_Mod = "/findrenamedplayer [<any player name>] OR [-history, -h <any player name> [-page, -p <page>]]",
    serverCommand = true)
    public static boolean commandFindRenamedPlayer(IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() == 0 && args.getArgumentSize() == 0) {
            return false;
        }

        if (args.hasArgument("history", "h") && sender.hasPermission(Permission.command_player_findrenamedplayer_all)) {
            String playerName = args.getString("history", "h");

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player doesn't exist.");
                return true;
            }

            List<PlayerRename> renameHistory = PlayerRenameHandler.getRenameHistory(credentials.getUniqueId());

            if (renameHistory.isEmpty()) {
                sender.printError("That username does not have a rename history.");
                return true;
            }

            Collections.sort(renameHistory, new Comparator<PlayerRename>() {
                @Override
                public int compare(PlayerRename pr1, PlayerRename pr2) {
                    Timestamp ts1 = pr1.getTimestamp();
                    Timestamp ts2 = pr2.getTimestamp();

                    if (ts1.before(ts2)) {
                        return -1;
                    } else if (pr1.getTimestamp().equals(pr2)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });

            List<String> messages = new ArrayList<String>();
            SimpleDateFormat sdf  = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);
            int idx = 1;

            for (PlayerRename rename : renameHistory) {
                String renameDateString = sdf.format(rename.getTimestamp());
                messages.add(idx++ + ". " + ChatColor.GRAY + rename.getNewName() + ChatColor.WHITE + " (" + ChatColor.YELLOW
                        + "first joined on " + renameDateString + ChatColor.WHITE + ")");
            }

            int page = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    page = Integer.parseInt(args.getString("page", "p"));
                } catch (NumberFormatException nfe) {
                    sender.printError("Page number is not a number.");
                    return true;
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(page, messages);

            if (ph.isValidPage()) {
                String coloredPlayerName = PlayerUtil.getColoredName(playerName);

                sender.printInfo("Looking up rename history for " + coloredPlayerName);                sender.print(ChatColor.AQUA, "Showing page " + page + " of " + ph.getMaxPage());
                sender.printInfo("");

                for (String msg : messages) {
                    sender.printInfo(msg);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }

            return true;
        } else if (args.getActionSize() > 0) {
            String findName = args.getString(0);
            List<String> playerNames = PlayerRenameHandler.getPlayerRenames(findName);

            if (playerNames.size() > 0) {
                String coloredNames = "";

                for (String playerName : playerNames) {
                    if (!coloredNames.isEmpty()) {
                        coloredNames += ChatColor.WHITE + ", ";
                    }

                    coloredNames += PlayerUtil.getColoredName(playerName);
                }

                sender.print(ChatColor.YELLOW, "Searching all players that have used \"" + findName + "\"");
                sender.printInfo("Found " + playerNames.size() + " " + (playerNames.size() > 1 ? "players" : "player")
                        + ": " + coloredNames);

                return true;
            } else {
                PlayerCredentials testCredentials = PlayerCredentialsManager.getByName(findName);

                if (testCredentials != null) {
                    sender.printError("\"" + findName + "\" is the most current username.");
                } else {
                    sender.printError("That username does not exist.");
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @CommandMethod(aliases = {"chatsounds"},
    description = "A command to toggle chat sounds.",
    permission = Permission.command_player_chatsounds,
    usage = "/chatsounds <sound type OR all> <on/off> OR [list]",
    serverCommand = false)
    public static boolean commandChatSounds(InnPlugin plugin, IdpPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.print(ChatColor.YELLOW, "The following chat sounds exist:");
            player.print(ChatColor.YELLOW, "Legend: " + ChatColor.GREEN + "enabled " + ChatColor.RED + "disabled");
            player.printInfo("");

            int idx = 1;

            for (ChatSoundSetting csg : ChatSoundSetting.values()) {
                if (player.getGroup().equalsOrInherits(csg.getMinGroup())) {
                    ChatColor lineColor = (player.getSession().hasChatSoundSetting(csg) ? ChatColor.GREEN : ChatColor.RED);
                    String nameList = "";

                    for (String name : csg.getNames()) {
                        if (!nameList.isEmpty()) {
                            nameList += ", ";
                        }

                        nameList += name;
                    }

                    player.printInfo(idx++ + ": " + lineColor + csg.getDescription()
                            + ChatColor.YELLOW + " (" + nameList + ")");
                }
            }

            player.printInfo("Type /chatsounds <sound type OR all> [on/off] to enable/disable these sound types.");

            return true;
        }

        if (args.length < 2) {
            player.printError("You must use a sound type or \"all\" as well as \"on\" or \"off\".");
            return true;
        }

        boolean status = false;

        if (args[1].equalsIgnoreCase("on")) {
            status = true;
        } else if (args[1].equalsIgnoreCase("off")) {
            status = false;
        } else {
            player.printError("You must use either \"on\" or \"off\".");
            return true;
        }

        List<ChatSoundSetting> settings = new ArrayList<ChatSoundSetting>();

        if (args[0].equalsIgnoreCase("all")) {
            for (ChatSoundSetting csg : ChatSoundSetting.values()) {
                if (player.getGroup().equalsOrInherits(csg.getMinGroup())) {
                    settings.add(csg);
                }
            }
        } else {
            ChatSoundSetting setting = ChatSoundSetting.byName(args[0]);

            if (setting == null || !player.getGroup().equalsOrInherits(setting.getMinGroup())) {
                player.printError("Invalid chat setting specified.");
                return true;
            }

            settings.add(setting);
        }

        PlayerSession session = player.getSession();

        for (ChatSoundSetting setting : settings) {
            boolean existingStatus = session.hasChatSoundSetting(setting);

            if (existingStatus == status) {
                player.printError("The setting " + setting.getDescription() + " was already " + (existingStatus ? "on" : "off")+ ".");
            } else {
                session.setChatSoundSetting(setting, status);
                player.printInfo("The setting " + setting.getDescription() + " has been turned " + (status ? "on" : "off") + ".");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"miningstick", "ms"},
    description = "A command to modify the behavior of the mining stick.",
    permission = Permission.command_player_miningstick,
    usage = "/miningstick",
    serverCommand = false)
    public static void commandMiningStick(IdpPlayer player) {
        PagedInventory miningStickInventory = WindowSystemUtil.getInventoryFromMiningStickSettings(player);
        MiningStickSettingsWindow miningStickWindow = new MiningStickSettingsWindow(miningStickInventory);
        MiningStickSettingsPayload payload = new MiningStickSettingsPayload(miningStickWindow);

        IdpInventory playerInv = miningStickWindow.createInventory(player, player.getName() + "'s mining stick settings");
        playerInv.setPayload(payload);
        player.openInventory(playerInv);
    }

    @CommandMethod(aliases = {"referral", "refer"},
    description = "All referral related commands.",
    permission = Permission.command_player_referral,
    usage = "/referral [-points] [-refer [reason] [-u player] [-list] [-unlock <bonus>]",
    usage_SAdmin = "/referral [-points] [-refer [reason] [-u player] [-list] [-unlock <bonus>] [-p <username>]",
    serverCommand = true)
    public static boolean commandReferral(InnPlugin plugin, IdpCommandSender sender, ParameterArguments args) {
        IdpPlayer target = sender.isPlayer() ? (IdpPlayer) sender : null;
        boolean self = true;
        if (sender.hasPermission(Permission.command_player_refferal_others) && args.hasOption("p", "player")) {
            target = args.getPlayer("p", "player");
            self = false;
        }

        if (target == null) {
            sender.printError("You have specified an unknown player.");
            return true;
        }

        if (args.hasOption("unlock")) {
            PlayerBonus bonus = PlayerBonus.getBonus(args.getString("unlock"));
            if (bonus == null) {
                sender.printInfo(" ---- [REFER BONUSES] ----");
                for (PlayerBonus list : PlayerBonus.values()) {
                    sender.print(target.getSession().hasBonus(list) ? ChatColor.GREEN : ChatColor.RED, list.getName() + ": " + list.getDescription());
                }
            } else {
                int spent = target.getSession().getSpentReferralPoints() + bonus.getCost();
                if (target.getSession().hasBonus(bonus)) {
                    sender.printError("You already have this bonus!");
                } else if (!target.getGroup().equalsOrInherits(bonus.getRequiredGroup())) {
                    sender.printError("Your rank cannot use this bonus! (" + bonus.getRequiredGroup().name, ")");
                } else if (spent > target.getSession().getTotalReferralPoints()) {
                    sender.printError("You don't have enough referral points for this bonus!");
                } else {
                    target.getSession().setBonus(bonus, true);
                    target.getSession().calculateRefferalPoints();
                    sender.printInfo("You have unlocked " + ChatColor.YELLOW + bonus.getName(), "!");
                }
            }
            return true;
        }

        if (args.hasOption("refer")) {
            String arg = args.getString("refer");

            switch (target.getSession().getReferType()) {
                case 1:
                    sender.printInfo((self ? "You were" : target.getColoredName() + ChatColor.DARK_GREEN + " was") + " referred by: " + ChatColor.YELLOW + "SERVERLIST");
                    return true;
                case 2:
                    UUID referId = UUID.fromString(target.getSession().getReferId());
                    IdpPlayer referPlayer = plugin.getPlayer(referId);
                    PlayerSession session = null;

                    if (referPlayer != null) {
                        session = referPlayer.getSession();
                    } else {
                        PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(referId);
                        session = PlayerSession.getSession(referId, credentials.getName(), plugin);
                    }

                    sender.printInfo((self ? "You were" : session.getColoredDisplayName() + ChatColor.DARK_GREEN + " was") + " referred by: " + ChatColor.YELLOW + "PLAYER (" + session.getColoredDisplayName() + ChatColor.YELLOW + ")");
                    return true;
                case 3:
                    sender.printInfo((self ? "You were" : target.getColoredName() + ChatColor.DARK_GREEN + " was") + " referred by: " + ChatColor.YELLOW + "FORUMS");
                    return true;
            }

            if (StringUtils.isNullOrEmpty(arg)) {
                arg = "lol, you will never see this!";
            }

            if (arg.equalsIgnoreCase("serverlist")) {
                target.getSession().setReferType(1);
                sender.printInfo("You have confirmed that " + (self ? "you were" : target.getColoredDisplayName() + ChatColor.DARK_GREEN + "was")
                        + " reffered by the " + ChatColor.YELLOW + "server list", ".");
            } else if (arg.equalsIgnoreCase("player")) {
                String referName = args.getString("u", "username");
                if (referName == null) {
                    sender.printError("You are missing the argument " + ChatColor.YELLOW + "-u <username>", " for the player that referred you.");
                    sender.printError(" e.g. /refer -refer player -username " + ChatColor.YELLOW + "[PLAYER]");
                    return true;
                }

                PlayerCredentials refer = PlayerCredentialsManager.getByName(referName);
                if (refer == null || !refer.isValidPlayer()) {
                    sender.printError("Unknown player! Please use their full name.");
                } else if (refer.getUniqueId().equals(target.getUniqueId())) {
                    sender.printError("You cannot refer yourself!");
                } else {
                    UUID referId = refer.getUniqueId();

                    target.getSession().setReferId(referId.toString());
                    target.getSession().setReferType(2);

                    IdpPlayer testPlayer = plugin.getPlayer(referId);
                    PlayerSession testSession = null;

                    if (testPlayer != null) {
                        testSession = testPlayer.getSession();
                    } else {
                        testSession = PlayerSession.getSession(referId, refer.getName(), plugin);
                    }

                    testSession.calculateRefferalPoints();

                    sender.printInfo("You have confirmed that " + (self ? "you were" : target.getColoredDisplayName() + ChatColor.DARK_GREEN + "was")
                            + " reffered by " + testSession.getColoredDisplayName(), ".");
                }
            } else if (arg.equalsIgnoreCase("forums")) {
                target.getSession().setReferType(3);
                sender.printInfo("You have confirmed that " + (self ? "you were" : target.getColoredDisplayName() + ChatColor.DARK_GREEN + "was")
                        + " reffered by the " + ChatColor.YELLOW + "forums", ".");
            } else {
                sender.printError("Refer Types: "
                        + ChatColor.YELLOW + "SERVERLIST", ", "
                        + ChatColor.YELLOW + "PLAYER", ", "
                        + ChatColor.YELLOW + "FORUMS");
                sender.printError(" e.g. /refer -refer " + ChatColor.YELLOW + "[TYPE]");
            }

            return true;
        }

        if (args.hasOption("points")) {
            int totalPoints = target.getSession().getTotalReferralPoints();
            sender.printInfo((self ? "Your" : target.getColoredName() + ChatColor.DARK_GREEN + "'s") + " points: " + ChatColor.YELLOW + (totalPoints - target.getSession().getSpentReferralPoints()), "/" + ChatColor.YELLOW + totalPoints);
            return true;
        }

        if (args.hasOption("list")) {
            sender.printInfo((self ? "Your" : target.getColoredName() + ChatColor.DARK_GREEN + "'s") + " refferals: " + target.getSession().getRefferalList());
            return true;
        }

        if (args.hasOption("u", "username")) {
            String referName = args.getString("u", "username");
            if (referName == null) {
                referName = "[PLAYER]";
            }
            sender.printError("Did you mean to type " + ChatColor.YELLOW + "/refer -refer player -username " + referName, " ?");
            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"setting", "settings", "playersetting", "toggle"},
    description = "Changes the setting for a player.",
    permission = Permission.command_player_settings,
    usage = "/setting",
    usage_Admin = "/setting [username]",
    serverCommand = true)
    public static boolean commandSetting(InnPlugin plugin, IdpCommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            IdpPlayer player = (IdpPlayer) sender;
            PlayerSession targetSession = null;

            if (args.length > 0) {
                String playerName = args[0];

                if (playerName.equalsIgnoreCase(player.getName())) {
                    targetSession = player.getSession();
                } else if (player.hasPermission(Permission.command_player_settings_other)) {
                    PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                    if (credentials == null) {
                        player.printError("Player not found.");
                        return true;
                    }

                    playerName = credentials.getName();
                    UUID uniqueId = credentials.getUniqueId();

                    targetSession = PlayerSession.getActiveSession(credentials.getUniqueId());

                    if (targetSession == null) {
                        targetSession = PlayerSession.getSession(uniqueId, playerName, plugin);
                    }
                } else {
                    player.printError("You cannot do that!");
                    return true;
                }
            }

            if (targetSession == null) {
                targetSession = player.getSession();
            }

            PagedInventory settingsInventory = WindowSystemUtil.getInventoryFromPlayerSettings(player, targetSession);
            ToggleWindow toggleWindow = new ToggleWindow(settingsInventory, targetSession);
            IdpInventory inv = toggleWindow.createInventory(player, targetSession.getRealName() + "'s settings");

            inv.setPayload(new ToggleInventoryPayload(toggleWindow));
            player.openInventory(inv);
        } else {
            if (args.length == 0) {
                sender.printError("Usage: /setting <username> [ID]");
                return true;
            }

            String playerName = args[0];
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("Player not found.");
                return true;
            }

            playerName = credentials.getName();
            UUID uniqueId = credentials.getUniqueId();

            PlayerSession session = PlayerSession.getActiveSession(uniqueId);

            if (session == null) {
                session = PlayerSession.getSession(uniqueId, playerName, plugin);
            }

            if (args.length > 1) {
                int id = 0;

                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    sender.printError("Id is not a number.");
                    return true;
                }

                PlayerSettings setting = PlayerSettings.getSetting(id);

                if (setting == null) {
                    sender.printError("That setting doesn't exist.");
                    return true;
                }

                boolean status = session.hasSetting(setting);
                boolean newStatus = !status;

                session.setSetting(setting, newStatus);

                ChatColor statusColor = (newStatus ? ChatColor.GREEN : ChatColor.RED);

                sender.printInfo(statusColor + (newStatus ? "Enabled " : "Disabled ") + "the \"" + setting.getName() + "\" setting for " + playerName + ".");
            } else {
                String settingsStatus = "";

                for (PlayerSettings setting : PlayerSettings.getSortedSettings()) {
                    if (!settingsStatus.isEmpty()) {
                        settingsStatus += ChatColor.WHITE + ", ";
                    }

                    ChatColor statusColor = (session.hasSetting(setting) ? ChatColor.GREEN : ChatColor.RED);
                    settingsStatus += statusColor + setting.getName() + " (" + setting.getId() + ")";
                }

                sender.printInfo("Settings of " + playerName + ": " + settingsStatus);
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"setchatname"},
    description = "Change the case of your username in chat.",
    permission = Permission.command_player_set_chatname,
    usage = "/setchatname <newname>",
    serverCommand = false)
    public static boolean commandSetChatusername(IdpPlayer player, SmartArguments args) {
        if (args.size() != 1) {
            return false;
        }

        String newName = args.getString(0);

        if (!player.getName().equalsIgnoreCase(newName)) {
            player.printError("You can only change the case of your username!");
            return true;
        }

        if (player.getSession().setFixedPlayerName(newName)) {
            player.printInfo("Name updated!");
        } else {
            player.printError("Cannot update name!");
            throw new RuntimeException(" Cannot update name for player: " + player.getName());
        }

        return true;
    }

    @CommandMethod(aliases = {"sethelmet", "sethelm", "helmet", "helm"},
    description = "Puts the item in your hand into your helmet slot.",
    permission = Permission.command_misc_set_helmet,
    usage = "/sethelmet",
    serverCommand = false)
    public static boolean commandSetHelmet(IdpPlayer player) {
        IdpMaterial helmMaterial = player.getHelmet().getMaterial();

        if (helmMaterial != IdpMaterial.AIR) {
            player.printError("You already have a helmet!");
            return true;
        }

        EquipmentSlot handSlot = player.getNonEmptyHand();

        if (handSlot == null) {
            player.printError("You must place an item in your hand to equip it!");
            return true;
        }

        IdpItemStack handStack = player.getItemInHand(handSlot);
        IdpMaterial mat = handStack.getMaterial();

        if (!canWearMaterial(mat)) {
            // If the player cannot equip this, then only staff can
            if (!player.getSession().isStaff()) {
                player.printError("You cannot equip this " + mat.getName().toLowerCase() + "!");
                return true;
            }
        }

        int amt = handStack.getAmount();
        ItemData oldItemData = handStack.getItemdata();

        // If they have more than one of the item in their hand, decrement
        // and hand it back, otherwise clear it
        if (amt > 1) {
            amt--;
            handStack.setAmount(amt);
        } else {
            handStack = IdpItemStack.EMPTY_ITEM;
        }

        player.setItemInHand(handSlot, handStack);
        player.setHelmet(new IdpItemStack(mat, 1, oldItemData));
        player.getInventory().updateBukkitInventory();

        player.printInfo("You have equipped " + mat.getName().toLowerCase() + ".");

        return true;
    }

    private static boolean canWearMaterial(IdpMaterial material) {

        // Do not allow most items except for these exceptions
        if (!material.isBlock()) {
            switch (material) {
                case BANNER_WHITE:
                case BANNER_ORANGE:
                case BANNER_MAGENTA:
                case BANNER_LIGHT_BLUE:
                case BANNER_YELLOW:
                case BANNER_LIME:
                case BANNER_PINK:
                case BANNER_GRAY:
                case BANNER_LIGHT_GRAY:
                case BANNER_CYAN:
                case BANNER_PURPLE:
                case BANNER_BLUE:
                case BANNER_BROWN:
                case BANNER_GREEN:
                case BANNER_RED:
                case BANNER_BLACK:
                    return true;
                default:
                    return false;
            }
        }

        switch (material) {
            case GLASS:
            case TNT:
            case DIAMOND_BLOCK:
            case GLOWSTONE:
            case GRASS:
            case STONE:
            case SAND:
            case BOOKCASE:
            case CHEST:
            case NOTE_BLOCK:
            case DIRT:
            case GRAVEL:
            case DISPENSER:
            case GOLD_BLOCK:
            case GLASS_STAINED_WHITE:
            case GLASS_STAINED_ORANGE:
            case GLASS_STAINED_MAGENTA:
            case GLASS_STAINED_LIGHT_BLUE:
            case GLASS_STAINED_YELLOW:
            case GLASS_STAINED_LIME:
            case GLASS_STAINED_PINK:
            case GLASS_STAINED_GRAY:
            case GLASS_STAINED_LIGHT_GRAY:
            case GLASS_STAINED_CYAN:
            case GLASS_STAINED_PURPLE:
            case GLASS_STAINED_BLUE:
            case GLASS_STAINED_BROWN:
            case GLASS_STAINED_GREEN:
            case GLASS_STAINED_RED:
            case GLASS_STAINED_BLACK:
            case BRICK:
            case MOSSY_COBBLESTONE:
            case OAK_PLANK:
            case SPONGE:
            case WET_SPONGE:
            case FURNACE:
            case PISTON:
            case STICKY_PISTON:
            case JACK_O_LANTERN:
            case PUMPKIN:
            case SNOW_BLOCK:
            case JUKEBOX:
            case WOOL_ORANGE:
            case WOOL_WHITE:
            case WOOL_MAGENTA:
            case WOOL_LIGHTBLUE:
            case WOOL_YELLOW:
            case WOOL_LIGHTGREEN:
            case WOOL_PINK:
            case WOOL_GRAY:
            case WOOL_LIGHTGRAY:
            case WOOL_CYAN:
            case WOOL_PURPLE:
            case WOOL_BLUE:
            case WOOL_BROWN:
            case WOOL_DARKGREEN:
            case WOOL_RED:
            case WOOL_BLACK:
                return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"ignore", "ign"},
    description = "Allows you to ignore a user.",
    permission = Permission.chat_ignoreuser,
    usage = "/ignore <player>",
    serverCommand = false)
    public static boolean commandIgnore(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        PlayerSession session = player.getSession();

        if (player.getSession().isStaff()) {
            player.printError("Staff cannot ignore players!");
            return true;
        }

        if (args.getActionSize() != 1) {
            return false;
        }

        boolean isMutingGlobal = args.getString(0).equalsIgnoreCase("%");

        if (isMutingGlobal) {
            if (session.isIgnored("%")) {
                player.printError("You are already ignoring everyone!");
                return true;
            }

            session.addIgnoredUser(Configuration.EVERYONE_CREDENTIALS);
            player.print(ChatColor.AQUA, "Added everyone to the ignore list.");
        } else {
            String playerName = args.getString(0);
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                playerName = target.getName();
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                player.printError("That player doesn't exist!");
                return true;
            }

            if (playerName.equalsIgnoreCase(player.getName())) {
                player.printError("You cannot ignore yourself!");
                return true;
            }

            if (session.isIgnored(playerName)) {
                player.printError("This player is already ignored!");
                return true;
            }

            if (session.addIgnoredUser(credentials)) {
                player.print(ChatColor.AQUA, "Added " + playerName + " to the ignore list.");
            } else {
                player.printError("That player cannot be ignored.");
            }
        }

        return true;
    }

    @CommandMethod(aliases = ("ignored"),
    description = "Allows you to see who is ignored on your list.",
    permission = Permission.chat_ignoredusers,
    usage = "/ignored",
    serverCommand = false)
    public static void commandIgnored(IdpPlayer player) {
        List<String> ignoredPlayers = player.getSession().getIgnoredPlayers();

        if (!ignoredPlayers.isEmpty()) {
            String ignoredPlayersString = "";

            for (String ignoredPlayer : ignoredPlayers) {
                if (ignoredPlayersString.isEmpty()) {
                    ignoredPlayersString = ignoredPlayer;
                } else {
                    ignoredPlayersString += ", " + ignoredPlayer;
                }
            }

            player.print(ChatColor.AQUA, "Ignored users: " + ignoredPlayersString);
        } else {
            player.print(ChatColor.RED, "Your ignore list is empty.");
        }
    }

    @CommandMethod(aliases = {"kicksit", "ks"},
    description = "Kicks a passenger off of you.",
    permission = Permission.command_misc_kicksit,
    usage = "/kicksit",
    serverCommand = false)
    public static boolean commandKickSit(IdpPlayer player) {
        if (player.getHandle().getPassenger() == null) {
            player.printError("You do not have a passenger!");
            return true;
        }
        player.getHandle().eject();
        player.printInfo("Ejected your passenger!");

        return true;
    }

    @CommandMethod(aliases = {"pvp"},
    description = "This changes the pvp settings for the player.",
    permission = Permission.command_misc_pvp,
    usage = "/pvp <on/off>",
    serverCommand = false)
    public static boolean commandPVP(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        if (args.length == 0 || args.length == 1) {
            boolean enabled = (args.length == 0 ? !player.getSession().isPersonalPvpEnabled() : args[0].equalsIgnoreCase("on"));

            if (enabled == false) {
                if (player.getSession().isInDamageState()) {
                    player.printError("You cannot turn PvP off yet!");
                    return true;
                }
            }

            player.getSession().setPersonalPvpEnabled(enabled);
            player.printInfo("You have toggled PvP " + (enabled ? "on" : "off") + ".");

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"unignore", "unign"},
    description = "Allows you to unignore a user.",
    permission = Permission.chat_unignoreuser,
    usage = "/unignore <player or -all>",
    serverCommand = false)
    public static boolean commandUnignore(Server server, InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        PlayerSession session = player.getSession();

        if (args.hasOption("all")) {
            if (session.getIgnoredPlayers().size() > 0) {
                session.clearIgnoredPlayers();

                player.printInfo("Cleared your ignored player list!");
            } else {
                player.printError("You are not ignoring anyone!");
            }

            return true;
        } else {
            if (args.getActionSize() != 1) {
                return false;
            }

            String unignorePlayer = args.getString(0);
            boolean isEveryone = unignorePlayer.equals("%");

            if (!session.isIgnored(unignorePlayer)) {
                player.printError((isEveryone ? "Everyone " : "That player ") + "is not being ignored.");
                return true;
            }

            session.removeIgnoredUser(unignorePlayer);
            player.print(ChatColor.AQUA, "Removed " + (isEveryone ? "everyone" : unignorePlayer) + " from the ignore list.");

            return true;
        }
    }

    @CommandMethod(aliases = {"transfercaughtentities", "tce"},
    description = "Requests transfer of caught entities to a player.",
    permission = Permission.entity_transfercaughtentities,
    usage = "/transfercaughtentities <player>",
    serverCommand = false)
    public static boolean commandTransferCaughtEntities(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length > 0) {
            IdpPlayer player = (IdpPlayer) sender;
            IdpPlayer tarplayer = parent.getPlayer(args[0]);

            if (tarplayer == null || !tarplayer.isOnline()) {
                sender.printError("That player is not online.");
                return true;
            }

            if (player.getSession().getCaughtEntityTraits().isEmpty()) {
                sender.printError("You have no caught entities to transfer!");
                return true;
            }

            Request rec = new TransferCaughtEntitiesRequest(parent, tarplayer, player);

            if (tarplayer.getSession().addRequest(rec)) {
                player.print(ChatColor.AQUA, "Teleport entity request sent to " + tarplayer.getColoredName() + ChatColor.AQUA + "!");
                tarplayer.print(ChatColor.AQUA, player.getColoredDisplayName() + ChatColor.AQUA + " wants to transfer their caught entities to you.");
                tarplayer.print(ChatColor.AQUA, "This will overwrite your list, if you caught any entities.");
                tarplayer.print(ChatColor.AQUA, "Please type /accept or /reject in 30 seconds.");

                TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
                text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
                text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
                tarplayer.print(text);
            } else {
                player.printError("You already have a pending request with this player!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"mail"},
    description = "Sends mail to another player.",
    permission = Permission.command_misc_mail,
    usage = "/mail [-list, -l [-page, -p <number>]] [-read, -r <number>] "
    + "[-remove, -delete <number>] [<username> <title> <message>] [-clear, -c]",
    serverCommand = false)
    public static boolean commandMail(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        ParameterArguments arguments = new ParameterArguments(args);

        if (arguments.getString("list", "l") != null
                || arguments.hasOption("list", "l")) {
            int countUnreadMail = player.getSession().countMail(true);
            int countAllMail = player.getSession().countMail(false);

            List<MailMessage> mail = player.getSession().getAllMail();

            int pageNo = 1;

            if (arguments.getString("page", "p") != null) {
                try {
                    pageNo = arguments.getInt("page", "p");
                } catch (NumberFormatException nfe) {
                    player.printError("Page is not a number.");
                    return true;
                }
            }

            List<String> output = new ArrayList<String>();

            if (mail.size() > 0) {
                for (int i = 0; i < mail.size(); i++) {
                    MailMessage m = mail.get(i);
                    String coloredName = PlayerUtil.getColoredName(m.getCreatorCredentials());

                    String outputString = ChatColor.DARK_GREEN.toString() + (i + 1) + ". From " + coloredName
                            + (m.getDate() != null ? ChatColor.DARK_GREEN + " on " + ChatColor.YELLOW + m.getDateString() : "")
                            + ChatColor.DARK_GREEN + " (" + ChatColor.WHITE + m.getTitle() + ChatColor.DARK_GREEN + ")";

                    if (!m.hasRead()) {
                        outputString += " " + ChatColor.WHITE + "(" + ChatColor.DARK_RED + "unread" + ChatColor.WHITE + ")";
                    }

                    output.add(outputString);
                }

                PagedCommandHandler ph = new PagedCommandHandler(pageNo, output);

                if (ph.isValidPage()) {
                    player.printInfo(ChatColor.AQUA + "All mail for " + player.getColoredDisplayName() + ChatColor.AQUA + " (" + countAllMail + " total, " + countUnreadMail + " unread)");
                    player.printInfo(ChatColor.AQUA + "Viewing page " + pageNo + " of " + ph.getMaxPage());
                    player.printInfo("");

                    for (String s : ph.getParsedInfo()) {
                        player.printInfo(s);
                    }

                    player.printInfo("");
                    player.printInfo(ChatColor.YELLOW + "Type /mail -read <number> to read a message.");
                    player.printInfo(ChatColor.YELLOW + "Type /mail -remove <number> to remove a message.");
                } else {
                    player.printError(ph.getInvalidPageNumberString());
                }
            } else {
                player.printError("You have no mail in your inbox.");
            }

            return true;
        } else if (arguments.getString("read", "r") != null) {
            int mailNo = 0;

            try {
                mailNo = arguments.getInt("read", "r");
            } catch (NumberFormatException nfe) {
                player.printError("Mail number not specified as a number.");
                return true;
            }

            MailMessage m = player.getSession().getMail(mailNo);

            if (m == null) {
                player.printError("That mail number doesn't exist.");
                return true;
            }

            if (!m.hasRead()) {
                m.setRead(true);
                m.save();
            }

            String coloredPlayerName = PlayerUtil.getColoredName(m.getCreatorCredentials());

            player.printInfo("From: " + coloredPlayerName + ChatColor.DARK_GREEN + " (" + m.getTitle() + ") " + m.getMessage());
            return true;
        } else if (arguments.getString("remove", "delete") != null) {
            int mailNo = 1;

            try {
                mailNo = arguments.getInt("remove", "delete");
            } catch (NumberFormatException nfe) {
                player.printError("Mail number not specified as a number.");
                return true;
            }

            MailMessage m = player.getSession().removeMail(mailNo);

            if (m == null) {
                player.printError("That mail number doesn't exist.");
                return true;
            }

            m.delete();

            player.printInfo("Deleted message from " + m.getCreator() + "!");

            return true;
        } else if (arguments.hasOption("clear", "c")) {
            List<MailMessage> list = player.getSession().getAllMail();

            if (list.size() < 1) {
                player.printError("Your mailbox is already empty.");
                return true;
            }

            player.getSession().deleteAllMail();

            for (MailMessage m : list) {
                m.delete();
                player.printInfo("Deleted message from " + m.getCreator() + "!");
            }

            return true;
        } else if (arguments.size() > 2) {
            SmartArguments sargs = new SmartArguments(args);

            if (sargs.size() < 3) {
                player.printError("You have incorrectly specified a message.");
                return true;
            }

            String toPlayer = sargs.getString(0);
            IdpPlayer target = parent.getPlayer(toPlayer);

            if (target != null) {
                toPlayer = target.getName();
            }

            PlayerCredentials toPlayerCredentials = PlayerCredentialsManager.getByName(toPlayer);

            if (toPlayerCredentials == null) {
                player.printError("That player does not exist.");
                return true;
            } else {
                // Get the proper casing of the name
                toPlayer = toPlayerCredentials.getName();
            }

            String fromPlayer = player.getName();
            PlayerCredentials fromPlayerCredentials = PlayerCredentialsManager.getByName(fromPlayer);

            String title = sargs.getString(1);
            String content = sargs.getJoinedStrings(2);
            int lengthToCheck = 100;

            if (fromPlayer.equalsIgnoreCase(toPlayer)) {
                player.printError("You can't send mail to yourself.");
                return true;
            }

            if (title.length() > 30) {
                player.printError("Your title is too long. Must be 30 characters or less.");
                return true;
            }
            if ((lengthToCheck - (title.length() + content.length())) < 0) {
                player.printError("Your message is too long.");
                return true;
            }

            PlayerCredentialsManager.addCredentialsToCache(fromPlayerCredentials);
            PlayerCredentialsManager.addCredentialsToCache(toPlayerCredentials);

            MailLogger mailLogger = (MailLogger) LogType.getLoggerFromType(LogType.MAIL);
            mailLogger.log(fromPlayer, toPlayer, title, content);

            MailMessage obj = new MailMessage(new Date(), fromPlayerCredentials, toPlayerCredentials, title, content);
            obj.save();

            if (target != null) {
                target.getSession().addMail(obj);

                if (target.isOnline()) {
                    target.printInfo(player.getColoredDisplayName() + ChatColor.DARK_GREEN + " has just sent you mail!");
                }
            }

            player.printInfo("Sent a mail message to " + toPlayer + "!");

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"staffrequest", "sr"},
    description = "Creates a request the staff can see.",
    permission = Permission.command_misc_staffrequest,
    usage = "/staffrequest <message> [-list, -l [-page, -p <page>]] [-read, -view, -r, -v <ID>]",
    usage_Mod = "/staffrequest [-list, -l [-page, -p <page>]] [-read, -view, -r, -v <ID>] [-delete, -d <ID>]",
    serverCommand = true)
    public static boolean commandStaffRequest(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.hasOption("list", "l")) {
            List<StaffMessage> staffRequests = StaffMessageHandler.getStaffRequests();

            if (staffRequests.size() > 0) {
                List<String> lines = new ArrayList<String>();
                int pageNo = 1;

                if (args.hasArgument("page", "p")) {
                    try {
                        pageNo = Integer.parseInt(args.getString("page", "p"));

                        if (pageNo < 1) {
                            sender.printError("Page number cannot be less than 1.");
                            return true;
                        }
                    } catch (NumberFormatException nfe) {
                        sender.printError("Page number not formatted correctly.");
                        return true;
                    }
                }

                boolean restricted = !sender.hasPermission(Permission.command_staffrequest_special);

                for (StaffMessage sm : staffRequests) {
                    String coloredPlayerName = PlayerUtil.getColoredName(sm.getCreatorCredentials());

                    Date date = sm.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);
                    String dateString = sdf.format(date);

                    // If restricted, only add staff requests the player has
                    if (restricted) {
                        if (sm.getCreator().equalsIgnoreCase(sender.getName())) {
                            lines.add("ID: " + sm.getId() + ". " + ChatColor.GREEN + "Requested by you on " + ChatColor.YELLOW + dateString + ChatColor.GREEN + ".");
                        }
                    } else {
                        lines.add("ID: " + sm.getId() + ". " + ChatColor.GREEN + "Request by " + coloredPlayerName + ChatColor.GREEN + " on " + ChatColor.YELLOW + dateString + (!sm.hasRead() ? ChatColor.WHITE + " (" + ChatColor.RED + "UNREAD" + ChatColor.WHITE + ")" : ""));
                    }
                }

                PagedCommandHandler ph = new PagedCommandHandler(pageNo, lines);

                if (ph.isValidPage()) {
                    sender.printInfo((restricted ? "Your " : "Player ") + "requests to staff - page " + pageNo + " of " + ph.getMaxPage());

                    for (String str : ph.getParsedInfo()) {
                        sender.printInfo(str);
                    }

                    sender.printInfo("");
                    sender.printInfo("To view a staff request, type /staffrequest -view <ID>");
                } else {
                    sender.printError(ph.getInvalidPageNumberString());
                }
            } else {
                sender.printError("There are no staff requests available.");
            }

            return true;
        } else if (args.hasArgument("read", "view", "r", "v")) {
            int requestId = 0;

            try {
                requestId = Integer.parseInt(args.getString("read", "view", "r", "v"));

                if (requestId < 1) {
                    sender.printError("Request IDs cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                sender.printError("Request ID not formatted properly.");
                return true;
            }

            StaffMessage sm = StaffMessageHandler.getStaffRequestById(requestId);
            boolean restricted = !sender.hasPermission(Permission.command_staffrequest_special);

            if (sm != null) {
                if (restricted && !sm.getCreator().equalsIgnoreCase(sender.getName())) {
                    sender.printError("Unknown request ID.");
                    return true;
                }

                String coloredPlayerName = PlayerUtil.getColoredName(sm.getCreatorCredentials());
                Date date = sm.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);
                String dateString = sdf.format(date);

                if (restricted) {
                    sender.print(ChatColor.GREEN, "Request (ID: " + sm.getId() + ") by you on " + ChatColor.YELLOW + dateString + ChatColor.WHITE + ": " + ChatColor.YELLOW + sm.getMessage());
                } else {
                    sender.print(ChatColor.GREEN, "Request (ID: " + requestId + ") by " + coloredPlayerName + ChatColor.GREEN + " on " + ChatColor.YELLOW + dateString + ChatColor.WHITE + ": " + ChatColor.YELLOW + sm.getMessage());

                    if (!sm.hasRead()) {
                        sm.setRead(true);
                        sm.save();
                    }
                }
            } else {
                sender.printError("Unknown request ID.");
            }

            return true;
        } else if (args.hasArgument("delete", "d") && sender.hasPermission(Permission.command_staffrequest_special)) {
            int requestId = 0;

            try {
                requestId = Integer.parseInt(args.getString("delete", "d"));

                if (requestId < 1) {
                    sender.printError("Request ID cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                sender.printError("Request ID not formatted properly.");
                return true;
            }

            StaffMessage sm = StaffMessageHandler.deleteStaffRequeustById(requestId);

            if (sm != null) {
                String coloredPlayerName = PlayerUtil.getColoredName(sm.getCreatorCredentials());
                String staffMsg = sender.getColoredName() + ChatColor.DARK_GREEN + " deleted staff request ID " + requestId + " by " + coloredPlayerName + ChatColor.DARK_GREEN + ": " + ChatColor.YELLOW + sm.getMessage();

                for (IdpPlayer p : parent.getOnlineStaff(false)) {
                    p.printInfo(staffMsg);
                }

                IdpConsole console = parent.getConsole();
                console.printInfo(staffMsg);

                IdpPlayer pCreator = parent.getPlayer(sm.getCreator());

                // Notify player of their request being deleted, if online
                if (pCreator != null && pCreator.isOnline()) {
                    pCreator.printInfo(sender.getColoredName() + ChatColor.DARK_GREEN + " deleted your staff request: " + ChatColor.YELLOW + sm.getMessage());
                }

                sm.delete();
            } else {
                sender.printError("Unknown request ID.");
            }

            return true;
        } else {
            if (args.getActionSize() > 0) {
                if (!(sender instanceof IdpPlayer)) {
                    sender.printError("Console cannot create staff requests!");
                    return true;
                }

                IdpPlayer player = (IdpPlayer) sender;

                if (player.getSession().isMuted()) {
                    player.printError("Can't make a staff request while muted!");
                    return true;
                }

                String request = "";

                for (int i = 0; i < args.getActionSize(); i++) {
                    if (request.isEmpty()) {
                        request = args.getString(i);
                    } else {
                        request += " " + args.getString(i);
                    }
                }

                if (request.length() > 200) {
                    player.printError("Request must be 200 characters or less.");
                    return true;
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());
                StaffMessage sm = new StaffMessage(new Date(), credentials, request);
                boolean result = StaffMessageHandler.addStaffRequest(sm);

                if (result) {
                    String sendMsg = player.getColoredDisplayName() + ChatColor.DARK_GREEN + " just created a staff request! (ID " + sm.getId() + ")";

                    for (IdpPlayer p : parent.getOnlineStaff(false)) {
                        p.printInfo(sendMsg);
                    }

                    IdpConsole console = parent.getConsole();
                    console.printInfo(sendMsg);

                    player.printInfo("New staff request created!");
                } else {
                    player.printError("A similar staff request was already created.");
                }

                return true;
            }
        }

        return false;
    }

    @CommandMethod(aliases = {"backpack", "bp"},
    description = "Opens up the backpack of a player.",
    permission = Permission.command_misc_backpack,
    usage = "/backpack",
    usage_Admin = "/backpack [player]",
    disabledWorlds = {IdpWorldType.CREATIVEWORLD},
    serverCommand = false)
    public static boolean commandBackpack(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        boolean override = player.hasPermission(Permission.special_backpack_override);
        boolean self = true;
        String playerName = player.getName();

        if (args.getActionSize() > 0) {
            if (override) {
                playerName = args.getString(0);
                IdpPlayer target = parent.getPlayer(playerName);

                if (target != null) {
                    playerName = target.getName();
                }

                self = playerName.equalsIgnoreCase(player.getName());
            }
        }

        if (self) {
            if (!override) {
                if (player.getInventory().getType() != InventoryType.MAIN) {
                    player.printError("You cannot open your backpack here!");
                    return true;
                }

                if (player.getWorld().getSettings().getMaptype() == MapType.THE_END) {
                    player.printError("Your bag seems to be missing!");
                    return true;
                }
            }
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        if (credentials == null) {
            player.printError("That player doesn't exist!");
            return true;
        }

        String backpackOwner = credentials.getName();
        UUID backpackOwnerId = credentials.getUniqueId();
        IdpInventory inv = parent.getBackpackView(backpackOwnerId);
        int backpackSize = 0;
        boolean fresh = false;

        if (inv == null) {
            PlayerGroup group = PlayerGroup.getGroupOfPlayerById(backpackOwnerId);
            backpackSize = PlayerBackpack.getBackpackSize(group);

            if (backpackSize == 0) {
                player.printError("This player cannot use a backpack!");
                return true;
            }

            inv = new IdpInventory(playerName + "'s backpack", backpackSize);
            inv.setPayload(new BackpackInventoryPayload(parent, backpackOwner, backpackOwnerId));

            parent.addBackpackView(backpackOwnerId, inv);
            fresh = true;
        } else {
            backpackSize = inv.getSize();
        }

        // Fill inventory with contents if fresh
        if (fresh) {
            PlayerBackpack backpack = null;

            if (self) {
                backpack = player.getSession().getBackpack();
            } else {
                IdpPlayer testPlayer = parent.getPlayer(playerName);

                if (testPlayer != null) {
                    backpack = testPlayer.getSession().getBackpack();
                } else {
                    backpack = PlayerBackpack.loadBackpackFromDB(backpackOwnerId, playerName);
                }
            }

            // Make sure to populate the correct item amount for the viewing
            // size for the backpack
            IdpItemStack[] backpackItems = new IdpItemStack[backpackSize];

            for (int i = 0; i < backpackSize; i++) {
                backpackItems[i] = backpack.getItemAt(i);
            }

            inv.setContents(backpackItems);
        }

        player.printInfo("You open " + (self ? "your " : playerName + "'s ") + "backpack!");
        player.openInventory(inv);

        return true;
    }

    @CommandMethod(aliases = {"login"},
    description = "A command that logs a player with a password set into the server.",
    permission = Permission.command_player_login,
    usage = "/login <password> [-update <newpassword>] [-delete <current password>]",
    preLoginCommand = true,
    obfusticateLogging = true,
    serverCommand = false)
    public static boolean commandLogin(IdpPlayer player, ParameterArguments args) {
        // Check if password was given.
        if (args.size() != 0 && args.size() != 1) {
            return false;
        }

        // Check if logged in already
        if (!player.getSession().isLoggedIn()) {

            if (args.size() == 0) {
                return false;
            }

            // Check password
            if (PlayerSecurity.checkPlayerPassword(player, args.getString(0))) {
                player.printInfo("Loggin in succesfully.");
                player.getSession().setPlayerLoggedin(true);
                return true;

            } else {
                PlayerSecurity.logBadPassword(player, player.getHandle().getAddress().getAddress().getHostAddress());

                // Check if they can still try, if not kick
                if (!PlayerSecurity.canLogin(player.getUniqueId(), player.getName())) {
                    player.getHandle().kickPlayer("Your password was incorrect.");
                } else {
                    player.printError("Password incorrect!");
                }

                return true;
            }
        } else {
            // Check if player wants to update password
            if (args.hasOption("update", "new", "u")) {

                // Boolean to check if the password can be changed.
                boolean canChange;

                if (args.size() == 0) {
                    // Check if player has a password;
                    canChange = !PlayerSecurity.hasPassword(player);
                } else {
                    // Check password
                    canChange = PlayerSecurity.checkPlayerPassword(player, args.getString(0));
                }

                // Only do rest if the password can be changed.
                if (canChange) {
                    String newpass = args.getString("update", "new", "u");

                    if (newpass == null || newpass.length() < 3) {
                        player.printError("Password too short.");
                        return true;
                    }

                    // Update the password.
                    if (PlayerSecurity.setPassword(player, newpass)) {
                        player.printInfo("Password was updated!");
                    } else {
                        player.printError("Could not update password.");
                    }
                } else {
                    player.printError("Password incorrect.");
                }

                return true;
            }

            // Check if player wants to delete their password
            if (args.hasOption("delete")) {
                // Needs a password
                if (args.getString("delete") == null) {
                    return false;
                }

                // Only do rest if the password can be changed.
                if (PlayerSecurity.checkPlayerPassword(player, args.getString("delete"))) {
                    // Remove the password
                    if (PlayerSecurity.removePassword(player.getUniqueId(), player.getName())) {
                        player.printInfo("Password was removed!!");
                    } else {
                        player.printError("Could not remove password.");
                    }
                } else {
                    player.printError("Password incorrect.");
                }

                return true;
            }
        }
        return false;

    }

    @CommandMethod(aliases = {"vote"},
    description = "Shows you all vote sites and when you can vote again.",
    permission = Permission.command_player_vote,
    usage = "/vote",
    serverCommand = false)
    public static boolean commandVote(InnPlugin plugin, IdpPlayer player, LynxyArguments args) {
        ExternalLibraryManager elm = plugin.getExternalLibraryManager();

        if (elm.isLoaded(LibraryType.VOTIFIER)) {
            VotifierIDP votifier = (VotifierIDP) elm.getAPIObject(LibraryType.VOTIFIER);
            List<TextComponent> output = new ArrayList<TextComponent>();

            TextComponent prefixComponent = ChatUtil.createTextComponent(Configuration.MESSAGE_PREFIX);

            TextComponent text = new TextComponent(prefixComponent);
            text.addExtra(ChatUtil.createTextComponent("Listing all Innectis vote sites:"));

            output.add(text);
            output.add(prefixComponent);

            for (VotifierService service : votifier.getVotifierServices()) {
                String serviceName = service.getName();
                String serviceURL = service.getURL();
                String serviceTitle = service.getTitle();

                text = new TextComponent(prefixComponent);
                text.addExtra(ChatUtil.createHTMLLink(serviceTitle, serviceURL));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.WHITE, " ("));

                long secondsSinceVote = votifier.getLastVoteTimeFromService(player, serviceName);

                if (secondsSinceVote > 0) {
                    long maxTime = VotifierIDP.VOTE_COOLDOWN_TIME;

                    if  (secondsSinceVote >= maxTime) {
                        text.addExtra(ChatUtil.createTextComponent(ChatColor.GREEN, "You may vote now!"));
                    } else {
                        text.addExtra(ChatUtil.createTextComponent(ChatColor.YELLOW, "Can vote in "));

                        long remain = (maxTime - secondsSinceVote);
                        String remainString = DateUtil.getTimeString(remain, false);

                        BaseComponent[] components = TextComponent.fromLegacyText(remainString);

                        for (BaseComponent component : components) {
                            text.addExtra(component);
                        }
                    }
                } else {
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.YELLOW, "You haven't voted yet!"));
                }

                text.addExtra(ChatUtil.createTextComponent(ChatColor.WHITE, ")"));
                output.add(text);
            }

            output.add(new TextComponent(prefixComponent));

            text = new TextComponent(prefixComponent);
            text.addExtra(ChatUtil.createTextComponent(ChatColor.DARK_GREEN, "Click any of the vote site titles above to go to that site."));

            output.add(text);

            TextComponent extraText = new TextComponent(prefixComponent);
            extraText.addExtra(ChatUtil.createTextComponent("Then click "));
            extraText.addExtra(ChatUtil.createCommandLink("here", "/redeem"));
            extraText.addExtra(ChatUtil.createTextComponent(" or type /redeem to collect your reward."));
            output.add(extraText);

            for (TextComponent t : output) {
                player.print(false, t);
            }
        } else {
            player.printError("Votifier not loaded! Voting will not give vote points.");

            TextComponent prefixComponent = new TextComponent(Configuration.MESSAGE_PREFIX);
            TextComponent text = new TextComponent(prefixComponent);

            text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, "Click "));
            text.addExtra(ChatUtil.createHTMLLink("here", "http://tinyurl.com/InnectisVotes"));
            text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, " to view the voting sites on our forum."));

            player.print(false, text);
        }

        return true;
    }

    @CommandMethod(aliases = {"redeem", "reward", "votepoint", "vp", "rd", "re"},
    description = "Allows you to redeem vote points.",
    permission = Permission.command_player_redeem,
    usage = "/redeem",
    serverCommand = false)
    public static void commandRedeem(IdpPlayer player, ParameterArguments args) {
        if (player.getInventory().getType() != InventoryType.MAIN) {
            player.printError("You cannot use that here!");
            return;
        }

        int votePoints = TransactionHandler.getTransactionObject(player).getValue(TransactionType.VOTE_POINTS);
        IdpInventory chest = new IdpInventory("Redeem an Item ("
                + votePoints + " remaining)", 36);
        chest.setPayload(new RedeemInventoryPayload());

        for (RewardItem item : RewardItem.values()) {
            IdpItemStack itemStack = new IdpItemStack(item.getItemStack());
            ItemData itemData = itemStack.getItemdata();
            itemData.addLore((votePoints < item.getCost() ? ChatColor.DARK_RED : ChatColor.GRAY) + "" + item.getCost() + " vote points.");
            chest.setItem(item.getSlot(), itemStack);
        }

        player.openInventory(chest);
    }

    @CommandMethod(aliases = {"massremove", "mremove", "mr"},
    description = "Mass removes all items of a player based on type.",
    permission = Permission.command_player_massdrop,
    usage = "/massremove <material> [amount] [-drop]",
    serverCommand = false)
    public static boolean commandMassRemove(IdpPlayer player, LynxyArguments args) {
        if (args.getActionSize() < 1) {
            return true;
        }

        IdpMaterial mat = IdpMaterial.fromString(args.getString(0));

        if (mat == null) {
            player.printError("That material doesn't exist.");
            return true;
        }

        if (mat == IdpMaterial.AIR) {
            player.printError("Cannot remove air!");
            return true;
        }

        int amount = -1;

        if (args.getActionSize() >= 2) {
            try {
                amount = Integer.parseInt(args.getString(1));

                if (amount < 1) {
                    player.printError("Amount cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Amount is not formatted correctly");
                return true;
            }
        }

        boolean doDrop = args.hasOption("drop");

        if (doDrop) {
            // Check if the player can drop items..
            if (player.getInventory().getType() == InventoryType.NONE) { //prevent item dupe
                return true;
            }

            // Don't allow dropping items in a spleef lot
            InnectisLot lot = LotHandler.getLot(player.getLocation(), true);
            if (lot != null && lot.isFlagSet(LotFlagType.SPLEEF)) {
                player.printError("You may not drop items in a spleef lot!");
                return true;
            }
        }

        IdpPlayerInventory inv = player.getInventory();
        IdpContainer container = new IdpContainer(inv.getItems());
        container.addMaterial(inv.getArmorItems());
        container.addMaterial(inv.getOffHandItem());
        int itemCount = container.countMaterial(mat);

        if (itemCount == 0) {
            player.printError("You do not have any of " + mat.getName().toLowerCase() + " to drop!");
            return true;
        }

        // If the player entered an explicit amount, check for item count
        if (amount > 0 && !container.hasMaterialCount(mat, amount)) {
            player.printError("You do not have enough of " + mat.getName().toLowerCase() + " to drop!");
            return true;
        }

        int totalRemoved = 0;

        for (int i = 0; i < container.size(); i++) {
            IdpItemStack stack = container.getItemAt(i);

            if (stack != null && stack.getMaterial() == mat) {
                int removeCount = stack.getAmount();

                // If we have an amount to remove, determine how much to remove
                if (amount > 0) {
                    removeCount = Math.min(amount, stack.getAmount());
                    amount -= removeCount;
                }

                stack.setAmount(stack.getAmount() - removeCount);
                totalRemoved += removeCount;

                if (doDrop) {
                    BlockFace facing = player.getFacingDirection();
                    Block block = player.getLocation().getBlock().getRelative(facing);
                    Location dropLocation = block.getLocation();

                    // Item will get stuck, so use player's location
                    if (IdpMaterial.fromBlock(block).isSolid()) {
                        dropLocation = player.getLocation();
                    }

                    player.getLocation().getWorld().dropItem(dropLocation, new IdpItemStack(mat, removeCount).toBukkitItemstack());
                }

                container.setItemAt(i, (stack.getAmount() > 0 ? stack : null));

                if (amount == 0) {
                    break;
                }
            }
        }

        inv.setItems(container.getNonArmorItems());
        inv.setOffHandItem(container.getOffHandItem());
        inv.updateBukkitInventory();

        player.printInfo((doDrop ? "Dropped " : "Removed ") + totalRemoved + " items of " + mat.getName().toLowerCase() + ".");

        return true;
    }

}

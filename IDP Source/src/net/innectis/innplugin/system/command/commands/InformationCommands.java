package net.innectis.innplugin.system.command.commands;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.system.command.ICommand;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.ValutaSinkManager;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.handlers.iplogging.GeoLocation;
import net.innectis.innplugin.handlers.iplogging.IPAddress;
import net.innectis.innplugin.handlers.iplogging.Playerinfo;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler.PermissionType;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.MenuItem;
import net.innectis.innplugin.objects.ModifiablePermissions;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.FlagType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler.VanillaChestType;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisOwnedObject;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.objects.owned.WaypointFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.infractions.InfractionManager;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.renames.PlayerRenameHandler;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.renames.PlayerRename;
import net.innectis.innplugin.player.request.Request;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.ColorUtil;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.StringUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

public final class InformationCommands {

    public static final int HELP_PAGE_LENGTH = 6;
    public static final int COMMANDS_PAGE_LENGTH = 10;

    @CommandMethod(aliases = {"valutasink", "vtsink", "vsink"},
    description = "Shows how many valutas were taken from the economy.",
    permission = Permission.command_information_vtsink,
    usage = "/vtsink",
    serverCommand = true)
    public static boolean commandVtSink(IdpCommandSender sender) {
        sender.printInfo("The current valuta sink is: " + ValutaSinkManager.getSink() + ".");
        return true;
    }

    @CommandMethod(aliases = {"commandlist", "commands", "cmdlist", "cmd"},
    description = "Shows all accessible commands.",
    permission = Permission.command_information_commandlist,
    usage = "/commandlist [page]",
    serverCommand = true)
    public static void commandCommandList(InnPlugin plugin, IdpCommandSender<? extends CommandSender> sender, String[] args) {

        int page = 1;

        // Try to get a page? Don't fail if we give invalid number.
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
            }
        }

        // Order alphabetically. :)
        List<ICommand> commands = plugin.commandManager.getCommands(sender);

        Collections.sort(commands, new Comparator<ICommand>() {

            @Override
            public int compare(ICommand o1, ICommand o2) {
                return o1.getAliases()[0].compareTo(o2.getAliases()[0]);
            }
        });

        // Calculate page numbers & positions.
        int maxPage = ((commands.size() + COMMANDS_PAGE_LENGTH - 1) / COMMANDS_PAGE_LENGTH * COMMANDS_PAGE_LENGTH) / COMMANDS_PAGE_LENGTH;
        int pagePos = Math.max(1, Math.min(page, maxPage)); //Don't fail on exceed. (Be Friendly)

        sender.printRaw("");
        sender.printInfo(" --- Commands [" + ChatColor.YELLOW + pagePos, " of " + ChatColor.YELLOW + maxPage, "] ---");
        for (int i = (pagePos - 1) * COMMANDS_PAGE_LENGTH; i < pagePos * COMMANDS_PAGE_LENGTH; i++) {
            if (i >= commands.size()) {
                break;
            }

            // We'll trim usages to keep down the spam.
            sender.printInfo(ChatColor.YELLOW.toString() + (i + 1) + " " + (i % 2 == 0 ? ChatColor.GRAY : ChatColor.WHITE) + StringUtil.trimLengthFancy(commands.get(i).getUsage(), 30, "..."));
        }
    }

    @CommandMethod(aliases = {"cmdinfo"},
    description = "Gives information about a command.",
    permission = Permission.command_information_cmdinfo,
    usage = "/cmdinfo <command>",
    serverCommand = true)
    public static boolean commandCmdInfo(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        ICommand command = parent.commandManager.getCommand(args[0]);
        if (command != null && command.canUseCommand(sender)) {

            String usage;
            if (sender.isPlayer()) {
                usage = command.getRankUsage(((IdpPlayer) sender).getGroup());
            } else {
                usage = command.getRankUsage(PlayerGroup.SADMIN); // SADMIN for server
            }

            // If no usage, return the normal one
            if (usage == null) {
                usage = command.getUsage();
            }

            sender.printInfo(ChatColor.AQUA + "[" + StringUtil.joinString(command.getAliases(), ",") + "]");
            sender.printInfo(ChatColor.AQUA + "Desc: ", command.getDescription());
            sender.printInfo(ChatColor.AQUA + "Usage: " + usage);

            return true;
        }

        // Make this if the command is not found or if the player has no permission
        // This to make sure that admin commands are not shown by this.
        sender.printError("Command not found!");
        return true;
    }

    @CommandMethod(aliases = {"countitem"},
    description = "Counts the number of items in a container the player is looking at.",
    permission = Permission.command_information_countitem,
    usage = "/countitem <material/ID[:data]>",
    serverCommand = false)
    public static boolean commandCountItem(IdpPlayer player, String[] args) {
        if (args.length > 0) {
            Block block = player.getTargetOwnedBlock();

            if (block == null || !VanillaChestType.isValidChestBlock(IdpMaterial.fromBlock(block))) {
                player.printError("You must be looking at a chest.");
                return true;
            }

            InnectisChest innchest = ChestHandler.getChest(block.getLocation());

            if (innchest == null) {
                player.printError("Chest not found! Cannot count.");
                return true;
            }

            if (!innchest.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.owned_object_override)) {
                player.printError("You cannot access this chest. Unable to count contents!");
                return true;
            }

            IdpMaterial mat = IdpMaterial.fromString(args[0]);

            if (mat == null) {
                player.printError("That material doesn't exist.");
                return true;
            }

            Chest chest = (Chest) block.getState();
            IdpContainer chestContainer = new IdpContainer(chest.getInventory());
            int count = chestContainer.countMaterial(mat);

            player.printInfo("There are " + count + " items of " + mat.getName() + " in this chest!");
            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"statistics", "stats"},
    description = "Shows statistics for a player.",
    permission = Permission.command_information_statistics,
    usage = "/statistics [player]",
    serverCommand = true)
    public static boolean commandStatistics(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        PlayerSession session = null;

        if (args.length > 0) {
            String playerName = args[0];
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                session = target.getSession();
            } else {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("That player does not exist.");
                    return true;
                }

                session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), parent, true);
            }
        } else {
            if (sender instanceof IdpConsole) {
                sender.printError("Cannot lookup statistics for console!");
                return true;
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(sender.getName());

            if (credentials == null) {
                sender.printError("Could not look up statistics for you!");
                return true;
            }

            session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), parent, true);
        }

        String name = session.getDisplayName();
        TransactionObject transaction = TransactionHandler.getTransactionObject(session.getUniqueId(), session.getRealName());

        List<InnectisLot> lots = LotHandler.getLots(name);
        String lotInfo;
        if (lots.isEmpty()) {
            lotInfo = "None";
        } else {
            int sublotCt = 0;
            int lotCt = 0;
            for (InnectisLot lot : lots) {
                if (lot.getParent() != null && lot.getParent().getId() > 0) {
                    sublotCt++;
                } else {
                    lotCt++;
                }
            }

            lotInfo = lotCt + " lots, " + sublotCt + " sublots.";
        }

        String lastSeen;
        if (server.getPlayerExact(name) != null) {
            lastSeen = "Now! (" + ChatColor.stripColor(DateUtil.getTimeString(session.getSessionOnlineTime(), false)) + ")";
        } else {
            Date logoutDate = session.getLastLogout();
            if (logoutDate == null) {
                logoutDate = session.getLastLogin();
            }
            if (logoutDate != null) {
                long logoutTime = (Calendar.getInstance().getTime().getTime() - logoutDate.getTime()) / 1000;
                lastSeen = DateUtil.getTimeDifferenceString(logoutTime, DateUtil.DEFAULT_CONSTANTS) + " ago.";
            } else {
                lastSeen = "Never!";
            }
        }

        String playedTime;
        long totalPlayedTime = (long) Math.floor(session.getTotalOnlineTime() / 1000);
        playedTime = DateUtil.getTimeDifferenceString(totalPlayedTime, DateUtil.DEFAULT_CONSTANTS);

        String firstJoined = "unknown";
        if (Bukkit.getOfflinePlayer(session.getUniqueId()) != null) {
            long firstJoinedTime = (long) Math.floor((System.currentTimeMillis() - Bukkit.getOfflinePlayer(session.getUniqueId()).getFirstPlayed()) / 1000);
            firstJoined = DateUtil.getTimeDifferenceString(firstJoinedTime, DateUtil.DEFAULT_CONSTANTS);
        }

        sender.printInfo("Printing information for " + name + ":");
        sender.print(ChatColor.YELLOW, "Full Username: " + session.getStringPrefixAndDisplayName());
        if (session.isStaff()) {
            sender.print(ChatColor.YELLOW, "Staff: Yes");
        }
        sender.print(ChatColor.YELLOW, "Last Seen: " + lastSeen);
        sender.print(ChatColor.YELLOW, "Played: " + playedTime);
        // @TODO: Either fix this up or remove entirely?
        //sender.print(ChatColor.YELLOW, "First Joined: " + firstJoined);

        List<PlayerRename> renames = PlayerRenameHandler.getPlayerRenames(session.getUniqueId());

        if (renames.size() > 0) {
            List<String> uniqueNames = getUniquePreviousNames(renames);
            String previousNameString = "";

            for (String uniqueName : uniqueNames) {
                if (!previousNameString.isEmpty()) {
                    previousNameString += ChatColor.WHITE + ", ";
                }

                previousNameString += ChatColor.YELLOW + uniqueName;
            }

            sender.print(ChatColor.YELLOW, "Previous names: " + previousNameString);
        }

        if (transaction != null) {
            sender.print(ChatColor.YELLOW, "Points: " + transaction.getValue(TransactionType.PVP_POINTS)
                    + " PVP, " + transaction.getValue(TransactionType.VOTE_POINTS)
                    + " VOTE POINTS.");
            sender.print(ChatColor.YELLOW, "Lots: " + lotInfo);
        }

        boolean showAlways = sender.getName().equalsIgnoreCase(session.getDisplayName());
        ChatColor extendedInfoColor = (showAlways ? ChatColor.YELLOW : ChatColor.GOLD);

        if (showAlways || sender.hasPermission(Permission.command_information_statistics_extended)) {
            if (transaction != null) {
                int valutas = transaction.getValue(TransactionType.VALUTAS);
                int valutasInBank = transaction.getValue(TransactionType.VALUTAS_IN_BANK);
                int valutasToPlayer = transaction.getValue(TransactionType.VALUTAS_TO_PLAYER);
                int valutasToBank = transaction.getValue(TransactionType.VALUTAS_TO_BANK);

                String extendedInfoString = "Balance: " + valutas;
                boolean showOtherBalances = (valutasInBank > 0 || valutasToPlayer > 0 || valutasToBank > 0);

                if (showOtherBalances) {
                    extendedInfoString += " (";
                    boolean previous = false;

                    if (valutasInBank > 0) {
                        extendedInfoString += "bank: " + valutasInBank;
                        previous = true;
                    }

                    if (valutasToBank > 0) {
                        if (previous) {
                            extendedInfoString += " ";
                        }

                        extendedInfoString += "to bank: " + valutasToBank;
                    }

                    if (valutasToPlayer > 0) {
                        if (previous) {
                            extendedInfoString += " ";
                        }

                        extendedInfoString += "to player: " + valutasToPlayer;
                    }

                    extendedInfoString += ")";
                }

                sender.print(extendedInfoColor, extendedInfoString);
            }

            sender.print(extendedInfoColor, "Ignored Users: " + session.getIgnoredPlayers());
        }

        if (sender.hasPermission(Permission.command_information_statistics_extended)) {
            sender.print(ChatColor.GOLD, "Infraction level: " + InfractionManager.getManager().getInfractionLevel(session.getUniqueId(), session.getRealName()));
            sender.print(ChatColor.GOLD, "Status: " + session.getPlayerStatus());
        }

        return true;
    }

    @CommandMethod(aliases = {"help"},
    description = "Displays a help menu.",
    permission = Permission.command_information_help,
    usage = "/help [page]",
    serverCommand = true)
    public static boolean commandHelp(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (parent.helpMenu == null) {
            try {
                parent.helpMenu = MenuItem.loadFile(FileHandler.getYmlFile(Configuration.FILE_HELPFILE));
            } catch (FileNotFoundException ex) {
                sender.printError("Cannot find help! Notify an admin!");
                InnPlugin.logError("Help not found!", ex);
                return true;
            }
        }
        if (parent.helpMenu == null) {
            sender.printError("Cannot find help file...");
            return true;
        }
        if (args.length == 0) {
            parent.helpMenu.findItem("help").printContent(sender);
        } else if (args.length == 1) {
            try {
                MenuItem item = parent.helpMenu.findItem(Integer.parseInt(args[0]));
                if (item == null) {
                    sender.printError("That help item was not found!");
                    return true;
                }
                item.printContent(sender);
            } catch (NumberFormatException nfe) {
                MenuItem item = parent.helpMenu.findItem(args[0]);
                if (item == null) {
                    sender.printError("That help item was not found!");
                    return true;
                }
                item.printContent(sender);
            }
        } else {
            return false;
        }
        return true;
    }

    @CommandMethod(aliases = {"id"},
    description = "Shows the material of a block looked at (additional info if owned or a player head).)",
    permission = Permission.command_information_id,
    usage = "/id",
    serverCommand = false)
    public static boolean commandID(IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        Block block = player.getTargetBlock(5);

        if (block != null) {
            InnectisOwnedObject ownedObj = null;
            String ownedBlockType = null;
            IdpMaterial mat = IdpMaterial.fromBlock(block);
            Location loc = block.getLocation();

            switch (mat) {
                case CHEST:
                case TRAPPED_CHEST:
                    ownedObj = ChestHandler.getChest(loc);
                    ownedBlockType = "Chest";
                    break;
                case BOOKCASE:
                    ownedObj = InnectisBookcase.getBookcase(loc);
                    ownedBlockType = "Bookcase";
                    break;
                case IRON_DOOR_BLOCK:
                    ownedObj = DoorHandler.getDoor(loc);
                    ownedBlockType = "Door";
                    break;
                case LAPIS_LAZULI_OREBLOCK:
                    ownedObj = WaypointHandler.getWaypoint(loc);
                    ownedBlockType = "Waypoint";
                    break;
                case IRON_TRAP_DOOR:
                    ownedObj = TrapdoorHandler.getTrapdoor(loc);
                    ownedBlockType = "Trapdoor";
                    break;
                case LEVER:
                    ownedObj = InnectisSwitch.getSwitch(loc);
                    ownedBlockType = "Switch";
                    break;
            }

            mat = IdpMaterial.fromFilteredBlock(block);
            player.printInfo("Block type: " + mat.getName() + " (" + mat.getId() + ":" + mat.getData() + ")");

            boolean locked = BlockHandler.getIdpBlockData(loc).isUnbreakable();

            if (locked) {
                player.print(ChatColor.LIGHT_PURPLE, "This block is locked.");
            }

            if (mat == IdpMaterial.SKULL_BLOCK) {
                Skull skull = (Skull) block.getState();
                SkullType type = skull.getSkullType();
                String headInfo = "Head type: " + type.name().toLowerCase();

                if (type == SkullType.PLAYER) {
                    OfflinePlayer owner = skull.getOwningPlayer();

                    if (owner != null) {
                        headInfo += " (name: " + owner.getName() + ")";
                    }
                }

                player.printInfo(headInfo);
            }

            if (mat == IdpMaterial.NOTE_BLOCK) {
                NoteBlock noteBlock = (NoteBlock) block.getState();
                Note note = noteBlock.getNote();

                player.print(ChatColor.LIGHT_PURPLE, "Note: " + note.getTone() + (note.isSharped() ? "#" : ""));
            }

            if (mat == IdpMaterial.JUKEBOX) {
                Jukebox jukebox = (Jukebox) block.getState();

                if (jukebox.isPlaying()) {
                    IdpMaterial playingRecord = IdpMaterial.fromBukkitMaterial(jukebox.getPlaying());

                    player.print(ChatColor.LIGHT_PURPLE, "Currently playing: " + playingRecord);
                } else {
                    player.print(ChatColor.LIGHT_PURPLE, "This jukebox is not playing music.");
                }
            }

            // If this is also an owned object, print out extra information
            if (ownedObj != null) {
                String membersString = ownedObj.getMembersString(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, ChatColor.GRAY);

                player.print(ChatColor.LIGHT_PURPLE, ownedBlockType + " ID #" + ownedObj.getId() + " owned by " + ownedObj.getOwner());

                player.print(ChatColor.LIGHT_PURPLE, "Members: " + (membersString.isEmpty() ? "none" : membersString));

                // If this owned object can be set with flags, display the list even if it is empty
                FlagType[] types = ownedObj.getFlagTypes();
                if (types.length > 0) {
                    player.print(ChatColor.LIGHT_PURPLE, "Flags: " + ownedObj.getFlagsString(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE));
                }

                // Print extra information if this is a waypoint
                if (ownedObj instanceof InnectisWaypoint) {
                    InnectisWaypoint wp = (InnectisWaypoint) ownedObj;
                    Location waypointLocation = wp.getDestination();
                    InnectisLot lot = LotHandler.getLot(waypointLocation);
                    boolean canSee = (!wp.isFlagSet(WaypointFlagType.HIDDEN) || wp.canPlayerAccess(player.getName())
                            || player.hasPermission(Permission.owned_object_override));

                    if (canSee) {
                        player.print(ChatColor.LIGHT_PURPLE, "Target location: " + LocationUtil.locationString(loc) + (lot == null ? "" : " (" + lot.getOwner() + "'s lot)"));
                    }
                }
            }
        } else {
            player.printError("You must look at a block to identify it!");
        }
        return true;
    }

    @CommandMethod(aliases = {"version", "ver", "icanhasbukkit", "idpv", "omversion"},
    description = "Displays version information of the IDP.",
    permission = Permission.command_information_version,
    usage = "/version",
    serverCommand = true)
    public static void commandVersion(IdpCommandSender<? extends CommandSender> sender) {
        String pluginVersion = Configuration.PLUGIN_VERSION;

        sender.printInfo(Configuration.PLUGIN_NAME + " " + ChatColor.AQUA + pluginVersion);
        sender.print(ChatColor.YELLOW, "Authors: Hret, The_Lynxy, AlphaBlend & Nosliw");
        sender.print(ChatColor.YELLOW, "Custom made for Innectis.net!");

        // Only players can see this
        if (sender instanceof IdpPlayer) {
            IdpPlayer player = (IdpPlayer) sender;

            TextComponent prefixComponent = new TextComponent(Configuration.MESSAGE_PREFIX);
            prefixComponent.setColor(ColorUtil.idpColorToBungee(ChatColor.YELLOW));

            TextComponent text = ChatUtil.createTextComponent(ChatColor.YELLOW, "Read the changelog ");
            text.addExtra(ChatUtil.createHTMLLink("here", "https://archives.codespeak.org/innectis/information/changelog/index.htm#" + pluginVersion));
            text.addExtra(ChatUtil.createTextComponent(ChatColor.YELLOW, "!"));

            player.print(text);
        }

        // Extended info
        if (!sender.isPlayer() || ((IdpPlayer) sender).hasPermission(Permission.command_information_version_extended)) {
            sender.print(ChatColor.AQUA, "Bukkit: " + InnPlugin.getPlugin().getServer().getBukkitVersion());
            sender.print(ChatColor.AQUA, "MC version: " + InnPlugin.getPlugin().getServer().getVersion());
        }
    }

    @CommandMethod(aliases = {"playerinfo"},
    description = "Shows information about a player that can be either online or offline.",
    permission = Permission.command_admin_playerinfo,
    usage = "/playerinfo <username> [-help] [-page <number>]] [-status, -st] [-world, -w] [-location, -loc] [-group, -g] "
    + "[-balance, -bal] [-lastlogin, -ll] [-pvppoints, -pvpp] [-votepoints, -votep] [-referralpoints, -refp] [-mode] [-exp]"
    + "[-level] [-specialperms, -sp] [-requests] [-geolocation, -geo] [-linkedaccounts, -la] ",
    serverCommand = true)
    public static void commandPlayerInfo(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, ParameterArguments args) {

        // <editor-fold defaultstate="collapsed" desc="Help">
        // Display help
        if (args.hasOption("help", "h")) {
            int page = 0;

            try {
                page = Integer.parseInt(args.getString("page", "p"));
            } catch (NumberFormatException nfe) {
                sender.printError("Page is not a number.");
                return;
            }

            List<String> helpLines = new ArrayList<String>();

            helpLines.add("-help (-h): This help page.");
            helpLines.add("-page (-p): The number of the page.");
            helpLines.add("-username (-u) <username>: The username that needs to be queried.");
            helpLines.add("Partial names are accepted if the user is online. ");
            helpLines.add("-status (-st): The status of the player (alive/dead/respawned) ");
            helpLines.add("-world (-w): The world the player is in.");
            helpLines.add("-location (-loc): The x,y,z location. ");
            helpLines.add("-group (-g): The group of the player. ");
            helpLines.add("-ip: The IP the player uses to connect with.");
            helpLines.add("-balance (-b): Balance in Volters.");
            helpLines.add("-lastlogin (-ll): The date of the last login.");
            helpLines.add("-pvppoints (-pp): The # of pvp points.");
            helpLines.add("-votepoints (-vp): The # of vote points.");
            helpLines.add("-mode (-m): Shows several settings. Like: Jailed, Muted, Godmode, ");
            helpLines.add("Frozen, PVP enabled & PVP immune.");
            helpLines.add("-exp (-xp): The amount of Expierence.");
            helpLines.add("-level (-lvl): The amount of Expierence.");
            helpLines.add("-damagereduction (-dgr): The amount of damage reduction in PVP.");
            helpLines.add("-specialperms (-sp): Special permissions for the player.");
            helpLines.add("-requests (-rs): The open requests of a player.");

            PagedCommandHandler ph = new PagedCommandHandler(page, helpLines);
            ph.setNewLinesPerPage(6);

            if (ph.isValidPage()) {
                sender.print(ChatColor.DARK_AQUA, "------------------------");
                sender.printInfo("Playerinfo Help");
                sender.printInfo(ChatColor.AQUA + "Viewing page " + page + " of " + ph.getMaxPage());

                for (String str : ph.getParsedInfo()) {
                    sender.printInfo(str);
                }

                sender.print(ChatColor.DARK_AQUA, "------------------------");
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }

            return;
        }
        //</editor-fold>

        String playerName = args.getString(0);
        IdpPlayer target = parent.getPlayer(playerName);
        PlayerSession session = null;
        boolean playerOnline = false;

        if (target != null) {
            playerName = target.getName();
            session = target.getSession();
            playerOnline = true;
        } else {
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player does not exist.");
                return;
            }

            session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), parent, true);
        }

        TransactionObject transaction = TransactionHandler.getTransactionObject(session.getUniqueId(), session.getRealName());

        sender.printInfo("Player: " + session.getColoredDisplayName());

        if (args.hasOption("group", "g")) {
            sender.printInfo("Group: " + ChatColor.AQUA + session.getGroup());
        }

        if (args.hasOption("lastlogin", "ll")) {
            sender.printInfo("Last Login: " + ChatColor.AQUA + DateUtil.formatString(session.getLastLogin(), DateUtil.FORMAT_FULL_DATE));
        }

        if (args.hasOption("balance", "b")) {
            sender.printInfo("valuta balance: " + ChatColor.AQUA + transaction.getValue(TransactionType.VALUTAS));
        }

        if (playerOnline && args.hasOption("status", "st")) {
            sender.printInfo("Status: " + ChatColor.AQUA + session.getPlayerStatus());
        }

        if (playerOnline && args.hasOption("world", "w")) {
            sender.printInfo("World: " + ChatColor.AQUA + target.getWorld().getName());
        }

        if (playerOnline && args.hasOption("location", "loc")) {
            sender.printInfo("Location: " + ChatColor.AQUA + LocationUtil.locationString(target.getLocation()));
        }

        Player bukkitPlayer = (playerOnline ? target.getHandle() : null);

        if (playerOnline && args.hasOption("ip")) {
            sender.printInfo("IP: " + ChatColor.AQUA + bukkitPlayer.getAddress().getAddress().toString());
        }

        if (playerOnline && args.hasOption("exp")) {
            sender.printInfo("EXP: " + ChatColor.AQUA + bukkitPlayer.getExp());
        }

        if (playerOnline && args.hasOption("level")) {
            sender.printInfo("Level: " + ChatColor.AQUA + bukkitPlayer.getLevel());
        }

        if (playerOnline && args.hasOption("op")) {
            sender.printInfo("OP: " + ChatColor.AQUA + bukkitPlayer.isOp());
        }

        if (args.hasOption("specialperms", "sp")) {
            ModifiablePermissions perms = session.getModifiablePermissions();
            boolean hasModifiedPerms = false;

            List<Integer> addedPerms = perms.getPermissionIDByType(PermissionType.ADDITIONAL);

            if (!addedPerms.isEmpty()) {
                hasModifiedPerms = true;
                sender.print(ChatColor.GREEN, "Showing additional permissions:");

                for (int permID : addedPerms) {
                    Permission perm = Permission.getPermission(permID);
                    sender.print(ChatColor.GREEN, perm.getId() + " (" + perm.name() + ")");
                }
            }

            List<Integer> disabledPerms = perms.getPermissionIDByType(PermissionType.DISABLED);

            if (!disabledPerms.isEmpty()) {
                hasModifiedPerms = true;
                sender.print(ChatColor.RED, "Showing disabled permissions:");

                for (int permID : disabledPerms) {
                    Permission perm = Permission.getPermission(permID);
                    sender.print(ChatColor.RED, perm.getId() + " (" + perm.name() + ")");
                }
            }
        }

        // Destroys the session!
        if (args.hasOption("destroy")) {
            if (playerOnline) {
                target.getHandle().kickPlayer("Sorry for that, please rejoin.");
            }
            session.destroy();
            sender.printInfo("Session destroyed!");
        }

        if (args.hasOption("requests")) {
            List<Request> requests = session.getRequests();
            sender.printInfo("Player has " + ChatColor.AQUA + requests.size(), " pending request" + (requests.size() != 1 ? "s" : ""));
            int i = 0;
            for (Request req : requests) {
                sender.print(ChatColor.DARK_AQUA, (i < 10 ? i + " " : i) + ChatColor.AQUA.toString() + " - " + req.getDescription());
                i++;
            }
        }

        // Modes
        if (args.hasOption("mode")) {
            sender.printInfo("Jailed: " + ChatColor.AQUA + (session.isJailed() ? "True" : "False"));
            sender.printInfo("Muted: " + ChatColor.AQUA + (session.getRemainingMuteTicks() != 0 ? "True" : "False"));
            sender.printInfo("Frozen: " + ChatColor.AQUA + (session.isFrozen() ? "True" : "False"));
            sender.printInfo("Staff: " + ChatColor.AQUA + (session.isStaff() ? "True" : "False"));
            sender.printInfo("Godmode: " + ChatColor.AQUA + (session.hasGodmode() ? "True" : "False"));
            sender.printInfo("PVP enabled: " + ChatColor.AQUA + (session.isPvpEnabled() ? "True" : "False"));
            sender.printInfo("PVP immune: " + ChatColor.AQUA + (session.isPvPImmune() ? "True" : "False"));
        }

        if (args.hasOption("pvppoints", "pvpp") || args.hasOption("referralpoints", "refp")
                || args.hasOption("votepoints", "votep")) {
            if (args.hasOption("pvppoints", "pvpp")) {
                sender.printInfo("PVP points: " + ChatColor.AQUA + transaction.getValue(TransactionType.PVP_POINTS));
            }
            if (args.hasOption("votepoints", "votep")) {
                sender.printInfo("Vote points: " + ChatColor.AQUA + transaction.getValue(TransactionType.VOTE_POINTS));
            }
        }

        Playerinfo info = null;
        if (args.hasOption("geolocation", "geo")) {
            if (info == null) {
                info = session.getPlayerinfo();
            }

            if (playerOnline) {
                if (target.getGroup() == PlayerGroup.SADMIN && !sender.hasPermission(Permission.command_admin_perm)) {
                    sender.printInfo("No geo info");
                } else {
                    IPAddress[] addresses = info.lookupPartialIp(target.getHandle().getAddress().getAddress().toString().substring(1));
                    if (addresses.length > 0) {
                        GeoLocation geoloc = addresses[0].getGeoLocation();
                        if (geoloc != null) {
                            sender.printInfo("Geo information for: " + ChatColor.AQUA + addresses[0]);
                            sender.printInfo("Country: " + ChatColor.AQUA + geoloc.getCountry() + " (" + geoloc.getCountryCode() + ")");
                            sender.printInfo("Region code: " + ChatColor.AQUA + geoloc.getRegionCode());
                            sender.printInfo("Postal code: " + ChatColor.AQUA + geoloc.getPostalCode());
                            sender.printInfo("City: " + ChatColor.AQUA + geoloc.getCity());
                            sender.printInfo("Lat/Long: " + ChatColor.AQUA + geoloc.getLatitude(), " / " + ChatColor.AQUA + geoloc.getLongitude());
                        } else {
                            sender.printInfo("No geo info");
                        }
                    } else {
                        sender.printInfo("IP address not found");
                    }
                }
            } else {
                sender.printInfo("Player not online!");
            }
        }

        if (args.hasOption("linkedaccounts", "la")) {
            if (session.getGroup() == PlayerGroup.SADMIN && !sender.hasPermission(Permission.command_admin_perm)) {
                sender.printInfo("Linked accounts: " + ChatColor.AQUA + "Unknown");
            } else {
                if (info == null) {
                    info = session.getPlayerinfo();
                }

                List<String> players = info.findRelatedUsernames();

                if (players != null) {
                    String playerstring = "";

                    for (String name : players) {
                        playerstring += name + ", ";
                    }

                    if (playerstring.length() > 2) {
                        sender.printInfo("Linked accounts: " + ChatColor.AQUA + playerstring.substring(0, playerstring.length() - 2));
                    } else {
                        sender.printInfo("Linked accounts: " + ChatColor.AQUA + "None");
                    }
                } else {
                    sender.printError("Unable to get linked usernames for " + playerName + "!");
                }
            }
        }
    }

    @CommandMethod(aliases = {"serverinfo"},
    description = "Displays information about the server.",
    permission = Permission.command_information_serverinfo,
    usage = "/serverinfo [-help]",
    serverCommand = true)
    public static void commandServerInfo(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, ParameterArguments args) {
        if (sender.hasPermission(Permission.command_information_serverinfo_extended) && !args.hasOption("pmode")) {
            // Display help
            if (args.hasOption("help", "h")) {
                sender.printInfo("/serverinfo [-help] [-World] [-Memory] [-Sessions] [-pmode]");
                return;
            }

            sender.print(ChatColor.BLUE, "----------------------------------");
            sender.print(ChatColor.AQUA, "Server information: " + InnPlugin.getPlugin().getServer().getVersion());

            // Memory
            if (args.hasOption("memory", "mem", "m")) {
                int mb = 1024 * 1024;
                Runtime runtime = Runtime.getRuntime();

                sender.print(ChatColor.AQUA, "Memory usage: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + "mb/" + runtime.totalMemory() / mb + "mb");
                sender.print(ChatColor.AQUA, "Free Memory: " + runtime.freeMemory() / mb + "mb");
                sender.print(ChatColor.BLUE, "----------------------------------");
            }

            // Worldinformation
            if (args.hasOption("worlds", "w")) {
                for (World w : InnPlugin.getPlugin().getServer().getWorlds()) {
                    ChatColor color = ChatColor.GREEN;
                    if (new IdpWorld(w).getWorldType() == IdpWorldType.NONE) {
                        color = ChatColor.DARK_PURPLE;
                    } else if (w.getPlayers().isEmpty() && w.getLoadedChunks().length == 0) {
                        color = ChatColor.GRAY;
                    } else if (w.getPlayers().isEmpty()) {
                        color = ChatColor.YELLOW;
                    }

                    if (w.getEntities().size() > 10000 || w.getLoadedChunks().length > 5000) {
                        color = ChatColor.RED;
                    }

                    sender.print(ChatColor.AQUA, color + w.getName(),
                            ": " + color + w.getEntities().size(),
                            " entities, " + color + w.getPlayers().size(),
                            " players, " + color + w.getLoadedChunks().length, " chunks");
                }
                sender.print(ChatColor.BLUE, "----------------------------------");
            }

            // Chunkdata info
            if (args.hasOption("chunks", "c")) {
                sender.print(ChatColor.AQUA, "Cached chunks: " + ChunkDatamanager.cacheSize());
                sender.print(ChatColor.BLUE, "----------------------------------");
            }

            // Session information
            if (args.hasOption("sessions", "ses", "s")) {
                int active = 0;
                for (PlayerSession session : PlayerSession.getSessions()) {
                    if (session.getExpireTime() == 0) {
                        active++;
                    }
                }
                sender.print(ChatColor.AQUA, "There are a total of " + PlayerSession.getSessions().size() + " session(s) of which (" + active + ") are active.");
                sender.print(ChatColor.BLUE, "----------------------------------");
            }
        } else {
            int mb = 1024 * 1024;
            Runtime runtime = Runtime.getRuntime();

            int chunkCount = 0, entCount = 0;
            for (World w : InnPlugin.getPlugin().getServer().getWorlds()) {
                chunkCount += w.getLoadedChunks().length;
                entCount += w.getEntities().size();
            }

            sender.printInfo(ChatColor.AQUA + "Innectis", " minecraft community");
            sender.printInfo("Previously owned by " + ChatColor.DARK_RED + "Hret", " since 05-14-2011.");
            sender.printInfo("Now owned by " + ChatColor.DARK_RED + "AlphaBlend", " since 08-15-2013");
            sender.printInfo("Powered by: Nuclear Fallout, Lynxy and the players!");
            sender.printInfo("----------------------------------");
            sender.printInfo("Running " + Configuration.PLUGIN_NAME + " " + Configuration.PLUGIN_VERSION);
            sender.printInfo("Currently using " + (runtime.totalMemory() - runtime.freeMemory()) / mb + "mb memory.");
            sender.printInfo("Loaded " + chunkCount + " chunks and " + entCount + " entities.");
        }
    }

    @CommandMethod(aliases = {"ping"},
    description = "Displays your ping, another player's, or all players on the server.",
    permission = Permission.command_information_ping,
    usage = "/ping",
    usage_Mod = "/ping [player/all]",
    serverCommand = true)
    public static void commandPing(InnPlugin plugin, IdpCommandSender sender, ParameterArguments args) {
        if (args.size() == 1 && sender.hasPermission(Permission.command_information_ping_other)) {
            IdpPlayer player = args.getPlayer(0);

            if (player != null) {
                boolean self = (player.getName().equalsIgnoreCase(sender.getName()));
                int ping = player.getHandle().getHandle().ping;
                sender.printInfo((self ? "Your " : player.getColoredDisplayName()), " ping is currently " + ping + "ms.");
            } else {
                if (args.actionMatches(0, "all")) {
                    // List all players
                    for (IdpPlayer subplayer : plugin.getOnlinePlayers()) {
                        int ping = subplayer.getHandle().getHandle().ping;
                        sender.printInfo(subplayer.getColoredDisplayName(), "'s ping is currently " + ping + "ms.");
                    }

                    return;
                }

                sender.printError("Player not found!");
            }

        } else if (sender.isPlayer()) {
            int ping = ((IdpPlayer) sender).getHandle().getHandle().ping;
            sender.printInfo("Your ping is currently " + ping + "ms.");
        } else {
            sender.printError("Console can't ping itself!");
        }
    }

    @CommandMethod(aliases = {"plugins", "pl"},
    description = "Shows the plugins running on the server.",
    permission = Permission.command_information_plugins,
    usage = "/plugins",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandPlugins(Server server, IdpCommandSender<? extends CommandSender> sender) {
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = server.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            pluginList.append(plugin.getDescription().getName());
        }

        sender.printInfo("Available plugins: " + pluginList.toString());
        return true;
    }

    @CommandMethod(aliases = {"itemid", "iteminfo", "ii"},
    description = "Returns a description of the item in your hand.",
    permission = Permission.command_information_itemid,
    usage = "/itemid",
    serverCommand = false)
    public static void commandItemId(IdpPlayer player, String[] args) {
        EquipmentSlot handSlot = player.getNonEmptyHand();

        if (handSlot == null) {
            player.printError("You are not holding an item in either hand!");
            return;
        }

        IdpItemStack item = player.getItemInHand(EquipmentSlot.HAND);

        if (item != null && item.getMaterial() != IdpMaterial.AIR) {
            IdpMaterial mat = item.getMaterial();
            player.printInfo("You are holding " + item.getMaterial().getName() + " (" + mat.getIdData() + ") in your main hand!");
        }

        item = player.getItemInHand(EquipmentSlot.OFF_HAND);

        if (item != null && item.getMaterial() != IdpMaterial.AIR) {
            IdpMaterial mat = item.getMaterial();
            player.printInfo("You are holding " + item.getMaterial().getName() + " (" + mat.getIdData() + ") in your off hand!");
        }
    }

    @CommandMethod(aliases = {"who", "list"},
    description = "Displays a list of all users online, or in a specific world.",
    permission = Permission.command_information_who,
    usage = "/who [world]",
    serverCommand = true)
    public static boolean commandWho(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        boolean canSeeSpoofed = sender.hasPermission(Permission.spoofing_listspoofed);
        boolean canSeeInvisible = sender.hasPermission(Permission.command_admin_vanish);
        IdpWorldType limitWorld = null;

        if (args.length >= 1) {
            String inputname = args[0];
            int closestMatch = Integer.MAX_VALUE, currmatch = 0;

            // Find the world with the closest match to the input.
            for (IdpWorldType wset : IdpWorldType.values()) {
                if (wset.worldname == null) {
                    continue;
                }

                currmatch = StringUtil.getLevenshteinDistance(wset.worldname, inputname);
                if (currmatch <= 2 && currmatch < closestMatch) {
                    closestMatch = currmatch;
                    limitWorld = wset;
                }
            }

            if (limitWorld == null) {
                sender.printErrorFormat("World '{0}' not found!", inputname);
                return true;
            }
        }

        List<IdpPlayer> players = parent.getOnlinePlayers();
        List<String> playerNames = new ArrayList<String>();

        for (IdpPlayer player : players) {
            // Don't continue if player is not in target world
            if (limitWorld != null && player.getWorld().getWorldType() != limitWorld) {
                continue;
            }

            boolean isSpoofing = player.getSession().isSpoofing();
            boolean isVisible = player.getSession().isVisible();
            boolean hidden = (isSpoofing ? player.getSession().getSpoofObject().isHidden() : false);
            String name = "";

            if (isVisible || args.length == 0) {
                if (!hidden) {
                    name = player.getColoredDisplayName() + (isSpoofing && canSeeSpoofed ? ChatColor.WHITE + " (" + player.getName() + ")" : "");
                } else if (canSeeSpoofed) {
                    name = player.getColoredName() + ChatColor.WHITE + " (hidden)";
                }
            } else {
                if (canSeeInvisible) {
                    name = ChatColor.GRAY + player.getName();
                }
            }

            if (!name.isEmpty()) {
                playerNames.add(name);
            }
        }

        int count = playerNames.size();
        String playersString = "";

        for (String player : playerNames) {
            if (!playersString.isEmpty()) {
                playersString += ChatColor.WHITE + ", ";
            }

            playersString += player;
        }

        sender.printInfo((count != 1 ? "There are " : "There is ") + count + " player" + (count != 1 ? "s " : " ")
                + (limitWorld == null ? "online" : "in world " + limitWorld.worldname));

        sender.printInfo(playersString);

        return true;
    }

    @CommandMethod(aliases = {"world"},
    description = "Displays the world the player is in.",
    permission = Permission.command_information_world,
    usage = "/world",
    serverCommand = false)
    public static void commandWorld(IdpPlayer player) {
        IdpWorld world = player.getWorld();

        player.printInfo("You are on world: " + ChatColor.LIGHT_PURPLE + player.getWorld().getSettings().getWorldName());

        if (world.getWorldType() == IdpWorldType.DYNAMIC) {
            player.print(ChatColor.AQUA, "This is a dynamic world.");
        }
    }

    /**
     * Gets a unique list of all renames of the renamed player
     *
     * @param renames
     * @return
     */
    private static List<String> getUniquePreviousNames(List<PlayerRename> renames) {
        List<String> uniqueNames = new ArrayList<String>();

        for (PlayerRename rp : renames) {
            String oldName = rp.getOldName();

            if (!uniqueNames.contains(oldName)) {
                uniqueNames.add(oldName);
            }
        }

        return uniqueNames;
    }

}

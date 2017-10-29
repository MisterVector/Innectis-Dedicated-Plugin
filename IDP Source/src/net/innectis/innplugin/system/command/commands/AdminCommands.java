package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.objects.EnderChestContents;
import net.innectis.innplugin.objects.DynWorldBuilder;
import net.innectis.innplugin.objects.ViewedPlayerInventoryData;
import net.innectis.innplugin.objects.IdpHomes;
import net.innectis.innplugin.objects.ModifiablePermissions;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSecurity;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerPassword;
import net.innectis.innplugin.player.PlayerBackpack;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.HomeHandler;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler;
import net.innectis.innplugin.handlers.ModifiablePermissionsHandler.PermissionType;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.handlers.PictureCreator;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.inventory.payload.PlayerInventoryPayload;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.externalpermissions.ExternalPermissionHandler;
import net.innectis.innplugin.player.tools.InformationTool.InformationToolType;
import net.innectis.innplugin.tasks.async.ServerCrashChecker;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

public final class AdminCommands {

    @CommandMethod(aliases = {"playerpasswords", "ppw"},
    description = "A command that lists all players with passwords as well as the ability to delete them.",
    permission = Permission.command_admin_playerpasswords,
    usage = "/playerpasswords [-list, -l] OR [-delete, -d <player>]",
    serverCommand = true)
    public static boolean commandPlayerPasswords(InnPlugin plugin, IdpCommandSender sender, LynxyArguments args) {
        if (args.getArgumentSize() == 0 && args.getOptionSize() == 0) {
            return false;
        }

        if (args.hasOption("list", "l")) {
            List<PlayerPassword> playerPasswords = PlayerSecurity.getAllPlayerPasswords();
            List<String> messages = new ArrayList<String>();
            int count = 1;

            for (PlayerPassword playerPassword : playerPasswords) {
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerPassword.getPlayerId());
                String coloredPlayerName = PlayerUtil.getColoredName(credentials);
                Timestamp timestamp = playerPassword.getTimestamp();

                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT_FULL_DATE);

                messages.add(count++ + ". " + coloredPlayerName + ChatColor.WHITE + " (" + ChatColor.GREEN
                        + "password set on " + ChatColor.YELLOW + sdf.format(timestamp) + ChatColor.WHITE + ")");
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
                sender.printInfo("Displaying " + playerPasswords.size() + " player passwords.");
                sender.print(ChatColor.AQUA, "Page " + page + " of " + ph.getMaxPage());
                sender.printInfo("");

                for (String msg : messages) {
                    sender.printInfo(msg);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }
        } else if (args.hasArgument("delete", "d")) {
            String playerName = args.getString("delete", "d");
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player doesn't exist.");
                return true;
            }

            UUID playerId = credentials.getUniqueId();

            if (!PlayerSecurity.hasPassword(playerId, playerName)) {
                sender.printError("That player does not have a password!");
                return true;
            }

            PlayerSecurity.removePassword(playerId, playerName);

            String coloredPlayerName = PlayerUtil.getColoredName(credentials);

            sender.printInfo("Cleared the password of " + coloredPlayerName, "!");
        } else {
            return false;
        }

        return true;
    }

    @CommandMethod(aliases = {"maintenance"},
    description = "Turns maintenance mode on or off.",
    permission = Permission.command_admin_maintenance,
    usage = "/maintenance <on/off>",
    serverCommand = true)
    public static boolean commandMaintenance(InnPlugin plugin, IdpCommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        boolean enable = false;

        if (args[0].equalsIgnoreCase("on")) {
            enable = true;
        } else if (args[0].equalsIgnoreCase("off")) {
            enable = false;
        } else {
            sender.printError("You must use \"on\" or \"off\".");
            return true;
        }

        boolean status = Configuration.isInMaintenanceMode();

        // Don't try to toggle same status
        if (status == enable) {
            sender.printError("Maintenance mode is already " + (status ? "enabled" : "disabled") + ".");
            return true;
        }

        Configuration.setMaintenanceMode(enable);
        Server server = plugin.getServer();

        if (enable) {
            int kicked = 0;

            // Kick any non-staff still online
            for (IdpPlayer player : plugin.getOnlinePlayers()) {
                if (!player.getSession().isStaff()) {
                    Player bukkitPlayer = player.getHandle();
                    bukkitPlayer.kickPlayer("Maintenance mode has been enabled!");
                    kicked++;
                }
            }

            if (kicked > 0) {
                sender.printInfo("Kicked " + kicked + " players due to maintenance mode.");
            }

            // Get current MOTD so we can replace when maintenance mode is turned off
            Configuration.setMotd(server.getMotd());
            ((CraftServer) server).getHandle().getServer().setMotd("Innectis: Maintenance Mode");
        } else {
            ((CraftServer) server).getHandle().getServer().setMotd(Configuration.getMotd());
        }

        ChatColor msgColor = (enable ? ChatColor.GREEN : ChatColor.RED);
        plugin.broadCastMessage(msgColor + "Maintenance mode has been " + (enable ? "enabled" : "disabled") + ".");

        return true;
    }

    @CommandMethod(aliases = {"utility", "util"},
    description = "Used to manage various parts of IDP.",
    permission = Permission.command_admin_utility,
    usage = "/utility <function>",
    serverCommand = true)
    public static void commandUtility(InnPlugin plugin, IdpCommandSender sender, ParameterArguments args) {

        String function = args.getString(0);

        if (!StringUtil.stringIsNullOrEmpty(function)) {
            if (function.equalsIgnoreCase("reloadHelp")) {
                plugin.helpMenu = null;
                sender.printInfo("Help file reloaded!");
                return;
            } else if (function.equalsIgnoreCase("reloadFilter")) {
                Configuration.loadBannedWords();
                sender.printInfo("Filter file reloaded!");
                return;
            } else if (function.equalsIgnoreCase("reloadPrefixes")) {
                for (IdpPlayer player : plugin.getOnlinePlayers()) {
                    player.getSession().reloadPrefix();
                }
                sender.printInfo("Prefixes reloaded for all online players.");
                return;
            }

            return;
        }

        sender.print(ChatColor.AQUA, " ---- Possible Utilities ----");
        sender.printInfo("reloadHelp - " + ChatColor.RED + "Reloads /help from file.");
        sender.printInfo("reloadFilter - " + ChatColor.RED + "Reloads the swear filter from file.");
        sender.printInfo("reloadPrefixes - " + ChatColor.RED + "Reloads prefixes for all online players.");
    }

    @CommandMethod(aliases = {"setguestpromotearea"},
    description = "Sets an area where guests are automatically promoted.",
    permission = Permission.command_admin_setguestpromotearea,
    usage = "/setguestpromotearea",
    serverCommand = false)
    public static boolean commandSetGuestPromoteArea(IdpPlayer player) {
        IdpWorldRegion region = player.getRegion();

        if (region == null) {
            player.printError("You need to have a selection first.");
            return true;
        }

        Configuration.setGuestPromotionRegion(region);
        player.printInfo("Set guest promotion area!");

        return true;
    }

    @CommandMethod(aliases = {"unloadworldchunks"},
    description = "Unloads all chunks from a world.",
    permission = Permission.command_admin_unloadworldchunks,
    usage = "/unloadworld [worldname]",
    serverCommand = true)
    public static void commandUnloadWorld(InnPlugin plugin, IdpCommandSender sender, ParameterArguments args) {
        // Check if a world was selected
        if (args.size() != 0) {
            // Get the world
            String worldname = args.getString(0);
            World world = Bukkit.getWorld(worldname);

            // Check for valid work
            if (world == null) {
                sender.printError("World '" + worldname + "' not found!");
                return;
            }

            // Check if there are player there
            if (world.getPlayers().size() > 0) {
                sender.printError("Cannot unload world, players are inside...");
                return;
            }

            // Unload the chunks
            for (Chunk chunk : world.getLoadedChunks()) {
                world.unloadChunk(chunk);
            }

            sender.printInfo("All chunks unloaded.");
            return;
        }

        // If not specified world, unload all empty worlds..

        // Loop through worlds
        for (World world : plugin.getServer().getWorlds()) {
            // Check for players
            if (world.getPlayers().isEmpty()) {

                // Unload chunks
                for (Chunk chunk : world.getLoadedChunks()) {
                    world.unloadChunk(chunk);
                }
            }
        }

        sender.printInfo("Chunk unloaded in all empty worlds");
    }

    @CommandMethod(aliases = {"spectate", "spy"},
    description = "Allows you to spectate another player.",
    permission = Permission.command_moderation_spectate,
    usage = "/spectate [-view, -v <username>] [-end, -e] [-who] [-kickall]",
    serverCommand = false)
    public static boolean commandSpectate(IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("end", "e")) {
            PlayerSession session = player.getSession();

            if (session.isSpectating()) {
                IdpPlayer spectating = session.getSpectatorTarget();
                player.printInfo("You are no longer spectating " + spectating.getColoredDisplayName(), "!");

                player.getSession().spectateTarget(null);
            } else {
                player.printError("You are not spectating anyone.");
            }

            return true;
        }

        if (args.hasArgument("view", "v")) {
            IdpPlayer targetPlayer = InnPlugin.getPlugin().getPlayer(args.getString("view", "v"));

            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.printError("Unable to Spectate: Player '" + args.getString("view") + "' not found.");
                return true;
            }

            if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                player.printError("Unable to Spectate: You cannot spectate yourself!");
                player.printError("To leave spectate mode, type: /spectate -end");
                return true;
            }

            IdpPlayer targetSpectatingPlayer = targetPlayer.getSession().getSpectatorTarget();

            if (targetSpectatingPlayer != null) {
                player.printError("Unable to Spectate: " + targetPlayer.getName() + " is spectating " + targetSpectatingPlayer.getName() + "!");
                return true;
            }

            PlayerSession session = player.getSession();
            session.spectateTarget(targetPlayer);

            player.printInfo("You are now spectating " + targetPlayer.getColoredDisplayName(), "!");
            return true;
        }

        if (args.hasOption("who")) {
            int count = 0;

            player.print(ChatColor.AQUA, " ---- Printing Spectating Players ----");
            for (IdpPlayer targetPlayer : InnPlugin.getPlugin().getOnlinePlayers()) {
                IdpPlayer targetSpectatingPlayer = targetPlayer.getSession().getSpectatorTarget();

                if (targetSpectatingPlayer != null) {
                    player.printInfo(count + ") " + targetPlayer.getColoredDisplayName(), " is spectating " + targetSpectatingPlayer.getColoredDisplayName(), ".");
                    count++;
                }
            }

            if (count == 0) {
                player.printError("No players are currently spectating.");
            }

            return true;
        }

        if (args.hasOption("kickall")) {
            if (player.hasPermission(Permission.command_admin_spectate_kickall)) {
                if (player.getSession().kickSpectators(player.getName() + " kicked you out!")) {
                    player.printInfo("You kicked all spectating players.");
                } else {
                    player.printError("You have no spectating players.");
                }
            } else {
                player.printError("You cannot kick all players!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"fillcontainer", "fc"},
    description = "A command that allows you to quickly fill inventories with items. Can target players or containers.",
    permission = Permission.command_admin_fillcontainer,
    usage = "/fillcontainer <itemid[:data]> [amount] [-inventory, -i] [-replace, -r] [-includeself, -is]",
    serverCommand = false)
    public static boolean commandFillContainer(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        IdpPlayer player = (IdpPlayer) sender;
        IdpMaterial mat;
        int amt = 0;

        if (args.getActionSize() > 0) {
            mat = args.getMaterial(0);

            if (mat == null) {
                player.printError("Specified material isn't found.");
                return true;
            }

            amt = mat.getMaxStackSize();

            if (args.getActionSize() > 1) {
                try {
                    amt = Integer.parseInt(args.getString(1));

                    if (amt > 5000) {
                        player.printError("Amount is too high.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("Amount is specified incorrectly.");
                    return true;
                }
            }

            boolean fillInventory = args.hasOption("inventory", "inv", "i");
            boolean replace = args.hasOption("replace", "r");

            if (!fillInventory) {
                Block block = player.getTargetBlock(5);

                if (!(block.getState() instanceof InventoryHolder)) {
                    player.printError("You must be looking at a container to use this command.");
                    return true;
                }

                InventoryHolder holder = (InventoryHolder) block.getState();
                IdpContainer container = new IdpContainer(holder.getInventory());

                if (replace) {
                    container.clearContainer(holder.getInventory().getSize());
                }

                int remaining = container.addMaterialToStack(new IdpItem(mat), amt);
                holder.getInventory().setContents(container.getBukkitItems());
                ((BlockState) holder).update();

                player.printInfo((replace ? "Replaced " : "Added to ") + "the " + holder.getInventory().getType().getDefaultTitle().toLowerCase() + "'s contents with " + (remaining > 0 ? (amt - remaining) + " of " + amt : amt) + " of " + mat.getName().toLowerCase() + ".");
            } else {
                InnectisLot lot = LotHandler.getLot(player.getLocation());

                if (lot == null) {
                    player.printError("You must be on a lot in order to use this command.");
                    return true;
                }

                boolean includeSelf = args.hasOption("includeself", "is");

                for (IdpPlayer p : parent.getOnlinePlayers()) {
                    if (!p.getName().equalsIgnoreCase(player.getName()) || includeSelf) {
                        InnectisLot lot2 = LotHandler.getLot(p.getLocation());

                        if (lot2 != null && lot2 == lot) {
                            IdpPlayerInventory inv = p.getInventory();
                            IdpContainer container = new IdpContainer(inv.getItems(), 36);

                            if (replace) {
                                inv.setArmorItems(new IdpItemStack[4]);
                                container.clearContainer(36);
                            }

                            int remaining = container.addMaterialToStack(new IdpItem(mat), amt);

                            inv.setItems(container.getItems());
                            inv.updateBukkitInventory();

                            player.printInfo((replace ? "Replaced " : "Added to ") + p.getDisplayName() + ChatColor.DARK_GREEN + "'s inventory with " + (remaining > 0 ? (amt - remaining) + " of " + amt : amt) + " of " + mat.getName().toLowerCase() + ".");
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"perm", "permlist"},
    description = "Manages additional / disabled permissions for players.",
    permission = Permission.command_admin_perm,
    usage = "/perm [-reset, -resetdp, -resetap [player]] [-list [player] [-all] [<username> <perm ID> <-add/-remove> <-additionalperms, -ap/-disabledperms, -dp>]]",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandPerm(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() > 1) {
            String playerName = args.getString(0);
            IdpPlayer target = parent.getPlayer(playerName);
            PlayerSession session = null;

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

            int permID = 0;
            try {
                permID = Integer.parseInt(args.getString((1)));
                Permission testPerm = Permission.getPermission(permID);

                if (testPerm == Permission.NONE) {
                    sender.printError("That is an invalid permission!");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                sender.printError("Permission ID not formatted properly.");
                return true;
            }

            boolean add = args.hasOption("add");

            if (!add && !args.hasOption("remove")) {
                sender.printError("You must use either the -add or -remove switch.");
                return true;
            }

            boolean disable = args.hasOption("disabledperms", "dp");

            if (!disable && !args.hasOption("additionalperms", "ap")) {
                sender.printError("You must use the -additionalperms or -disabledperms switch.");
                return true;
            }

            PermissionType type = (!disable ? PermissionType.ADDITIONAL : PermissionType.DISABLED);
            ModifiablePermissions perms = session.getModifiablePermissions();

            // If adding, check if this permission is already added
            if (add) {
                PermissionType existingType = perms.getPermissionType(permID);

                if (existingType != null) {
                    sender.printError("That permission already exists in the " + existingType.name().toLowerCase() + " permission list for " + session.getColoredName(), ".");
                    return true;
                }

                perms.addPermission(permID, type);
                sender.printInfo("Added permission id " + permID + " (" + Permission.getPermission(permID).name() + ") to the " + type.name().toLowerCase() + " permission list for " + session.getColoredName(), ".");
            } else {
                boolean remove = perms.removePermission(permID, type);

                if (remove) {
                    sender.printInfo("Removed permission id " + permID + " (" + Permission.getPermission(permID).name() + ") from the " + type.name().toLowerCase() + " permission list for " + session.getColoredName(), ".");
                } else {
                    sender.printError("That permission did not exist in the " + type.name().toLowerCase() + " permission list for " + session.getColoredName(), ".");
                }
            }

            return true;
        } else if (args.hasOption("list") || args.hasArgument("list")) {
            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        sender.printError("Page number cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    sender.printError("Page number not formatted properly.");
                    return true;
                }
            }

            List<String> lines = new ArrayList<String>();

            if (args.hasOption("list", "l")) {
                List<ModifiablePermissions> permList = ModifiablePermissionsHandler.getAllModifiedPermsFromDB();

                if (permList.isEmpty()) {
                    sender.printInfo("No one has any modified permissions!");
                    return true;
                }

                for (int i = 0; i < permList.size(); i++) {
                    ModifiablePermissions perms = permList.get(i);
                    String coloredPlayerName = PlayerUtil.getColoredName(perms.getCredentials());

                    lines.add(ChatColor.YELLOW + "Showing " + ChatColor.GREEN + "additional "
                            + ChatColor.YELLOW + "and " + ChatColor.RED + "disabled "
                            + ChatColor.YELLOW + "permissions for " + coloredPlayerName);
                    lines.add("");

                    Map<Integer, PermissionType> allPerms = perms.getAllPermissions();

                    if (!allPerms.isEmpty()) {
                        for (Map.Entry<Integer, PermissionType> entry : allPerms.entrySet()) {
                            Permission perm = Permission.getPermission(entry.getKey());
                            PermissionType type = entry.getValue();
                            ChatColor permColor = (type == PermissionType.ADDITIONAL ? ChatColor.GREEN : ChatColor.RED);

                            lines.add(permColor.toString() + perm.getId() + " (" + perm.name() + ")");
                        }
                    }

                    if (i < permList.size() - 1) {
                        lines.add("");
                    }
                }
            } else {
                String playerName = args.getString("list", "l");
                IdpPlayer target = InnPlugin.getPlugin().getPlayer(playerName);
                PlayerSession session = null;

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

                ModifiablePermissions perms = session.getModifiablePermissions();

                if (!perms.hasModifiablePermissions()) {
                    sender.printError(session.getColoredName(), " has no modified permissions.");
                    return true;
                }

                String coloredUsername = session.getGroup().getPrefix().getTextColor() + session.getRealName();
                Map<Integer, PermissionType> allPerms = perms.getAllPermissions();

                if (!allPerms.isEmpty()) {
                    lines.add(ChatColor.YELLOW + "Showing " + ChatColor.GREEN + "additional "
                            + ChatColor.YELLOW + "and " + ChatColor.RED + "disabled "
                            + ChatColor.YELLOW + "permissions for " + coloredUsername);
                    lines.add("");

                    for (Map.Entry<Integer, PermissionType> entry : allPerms.entrySet()) {
                        Permission perm = Permission.getPermission(entry.getKey());
                        PermissionType type = entry.getValue();
                        ChatColor permColor = (type == PermissionType.ADDITIONAL ? ChatColor.GREEN : ChatColor.RED);

                        lines.add(permColor.toString() + "" + perm.id + " (" + perm.name() + ")");
                    }
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, lines);

            if (ph.isValidPage()) {
                sender.printInfo("Modified permission list - page " + pageNo + " to " + ph.getMaxPage() + "\n");

                for (String str : ph.getParsedInfo()) {
                    sender.printInfo(str);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }

            return true;
        } else if (args.hasArgument("reset", "resetdp", "resetap")) {
            boolean resetDisabledPerms = args.hasArgument("reset", "resetdp");
            boolean resetAdditionalPerms = args.hasArgument("reset", "resetap");

            String playerName = args.getString("reset", "resetdp", "resetap");
            IdpPlayer target = parent.getPlayer(playerName);
            PlayerSession session = null;

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

            ModifiablePermissions perms = session.getModifiablePermissions();

            if (!perms.hasModifiablePermissions()) {
                sender.printError(session.getColoredName(), " has no modified permissions.");
                return true;
            }

            String coloredName = PlayerUtil.getColoredName(session.getRealName());
            int count = 0;

            if (resetDisabledPerms) {
                List<Integer> disabledPerms = perms.getPermissionIDByType(PermissionType.DISABLED);

                for (int permID : disabledPerms) {
                    Permission perm = Permission.getPermission(permID);
                    sender.printInfo("Removed " + ChatColor.RED + "disabled", " permission " + permID + " - " + perm.name() + " from " + session.getColoredName(), ".");
                    perms.removePermission(permID, PermissionType.DISABLED);
                    count++;
                }
            }

            if (resetAdditionalPerms) {
                List<Integer> addedPerms = perms.getPermissionIDByType(PermissionType.ADDITIONAL);

                for (int permID : addedPerms) {
                    Permission perm = Permission.getPermission(permID);
                    sender.printInfo("Removed " + ChatColor.GREEN + "additional", " permission " + permID + " - " + perm.name() + " from " + session.getColoredName(), ".");
                    perms.removePermission(permID, PermissionType.ADDITIONAL);
                    count++;
                }
            }

            if (count > 0) {
                sender.printInfo("Removed " + ChatColor.AQUA + count, " permission modifications from " + coloredName);
            } else {
                sender.printError(coloredName, " does not have any modified permissions to remove!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"listinv"},
    description = "Allows you to view and manipulate the inventory of a player in real-time.",
    permission = Permission.command_admin_listinventory,
    usage = "/listinv <player> [-type, -t <inventory type>] ",
    serverCommand = true)
    public static boolean commandListInventory(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() == 1) {
            // Default to main inventory if no special conditions
            InventoryType type = InventoryType.MAIN;
            boolean forceView = false;

            if (args.hasArgument("type", "t")) {
                type = InventoryType.getTypeByName(args.getString("type", "t"));

                if (type == null || type == InventoryType.NONE) {
                    sender.printError("Unable to view inventory, it is unknown!");
                    return true;
                }

                forceView = true;
            }

            String playerName = args.getString(0);
            IdpPlayer target = parent.getPlayer(playerName);
            PlayerCredentials credentials = null;

            if (target != null) {
                playerName = target.getName();
            }

            credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That is not a valid player!");
                return true;
            }

            boolean useLiveInventory = false;

            if (target != null && target.isOnline()) {
                if (target.getSession().getPlayerStatus() != PlayerSession.PlayerStatus.ALIVE_PLAYER) {
                    sender.printError("That player is not alive!");
                    return true;
                }

                InventoryType testType = target.getInventory().getType();

                // If we're not forcing a particular inventory type, use the type of this player
                // also force live view if we force the same view as their type
                if (!forceView || type == testType) {
                    if (type != testType) {
                        type = testType;
                    }

                    useLiveInventory = true;
                }
            }

            sender.printInfo("Opening inventory of " + playerName + "! (Type: " + type.getName() + ")");

            if (sender.isPlayer()) {
                IdpPlayer player = (IdpPlayer) sender;
                IdpInventory inv = null;

                if (useLiveInventory) {
                    inv = new IdpInventory(target);
                } else {
                    // If we're not forcing a view, use the sending player's inventory type
                    if (!forceView) {
                        type = player.getInventory().getType();
                    }

                    IdpPlayerInventory playerInventory = IdpPlayerInventory.load(credentials.getUniqueId(), playerName, type, parent);

                    inv = new IdpInventory(playerName + " (" + type.getName() + ")", 45);
                    inv.setContents(playerInventory.getItems(), 0);
                    inv.setContents(playerInventory.getArmorItems(), 36);
                    inv.setContents(playerInventory.getOffHandItem(), 40);
                }

                ViewedPlayerInventoryData vpid = new ViewedPlayerInventoryData(credentials.getUniqueId(), credentials.getName(), type, useLiveInventory);
                player.getSession().setViewedPlayerInventoryData(vpid);

                inv.setPayload(new PlayerInventoryPayload(parent));
                player.openInventory(inv);
            } else {
                List<String> strings = listInventoryAsString(parent, type, credentials.getUniqueId(), playerName);

                for (int i = 0; i < strings.size(); i++) {
                    sender.printInfo(ChatColor.WHITE + strings.get(i));
                }
            }

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"cleanup"},
    description = "Cleans up various things (orphanned doors, non-existant chests, etc.).",
    permission = Permission.command_admin_cleanup,
    usage = "/cleanup",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandCleanup(Server server, InnPlugin parent, IdpCommandSender sender, String[] args) {
        ServerCrashChecker crashChecker = parent.getServerCrashChecker();

        // Suspend the crash checker, this process can take
        // a long time to complete
        if (crashChecker != null) {
            crashChecker.setSuspended(true);
        }

        int chestCt = 0, doorCt = 0, waypointCt = 0, homeCt = 0, lotCt = 0, incorrectLotMemberCt = 0;
        int bookcaseCt = 0, trapdoorCt = 0;
        int invalidChestCt = 0, invalidDoorCt = 0, invalidWaypointCt = 0, invalidHomeCt = 0;
        int invalidBookcaseCt = 0, invalidTrapdoorCt = 0;
        String unusedLots = "";

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT chestid, world, locx1, locy1, locz1, locx2, locy2, locz2 FROM chests;");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x1 = result.getInt("locx1");
                    int y1 = result.getInt("locy1");
                    int z1 = result.getInt("locz1");

                    Block block = world.getBlockAt(x1, y1, z1);
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    int id = result.getInt("chestid");

                    if (mat != IdpMaterial.CHEST && mat != IdpMaterial.TRAPPED_CHEST) {
                        ChestHandler.removeChestForcibly(block, id, true);
                        chestCt++;
                    }

                    int x2 = result.getInt("locx2");
                    int y2 = result.getInt("locy2");
                    int z2 = result.getInt("locz2");

                    if (x2 != 0 && y2 != 0 && z2 != 0) {
                        block = world.getBlockAt(x2, y2, z2);
                        mat = IdpMaterial.fromBlock(block);

                        if (mat != IdpMaterial.CHEST && mat != IdpMaterial.TRAPPED_CHEST) {
                            ChestHandler.removeChestForcibly(block, id, true);
                            chestCt++;
                        }
                    }
                } else {
                    invalidChestCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("SELECT doorid, world, locx1, locy1, locz1, locx2, locy2, locz2 FROM doors;");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x1 = result.getInt("locx1");
                    int y1 = result.getInt("locy1");
                    int z1 = result.getInt("locz1");

                    Block block = world.getBlockAt(x1, y1, z1);
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    int id = result.getInt("doorid");

                    if (mat != IdpMaterial.IRON_DOOR_BLOCK) {
                        DoorHandler.removeDoorForcibly(block, id, true);
                        doorCt++;
                    }

                    int x2 = result.getInt("locx2");
                    int y2 = result.getInt("locy2");
                    int z2 = result.getInt("locz2");

                    if (x2 != 0 && y2 != 0 && z2 != 0) {
                        block = world.getBlockAt(x2, y2, z2);
                        mat = IdpMaterial.fromBlock(block);

                        if (mat != IdpMaterial.IRON_DOOR_BLOCK) {
                            DoorHandler.removeDoorForcibly(block, id, true);
                            doorCt++;
                        }
                    }
                } else {
                    invalidDoorCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("SELECT waypointid, world, locx, locy, locz FROM waypoints;");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x = result.getInt("locx");
                    int y = result.getInt("locy");
                    int z = result.getInt("locz");

                    Block block = world.getBlockAt(x, y, z);
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    if (mat != IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
                        int id = result.getInt("waypointid");

                        WaypointHandler.removeWaypointForcibly(block, id, true);
                        waypointCt++;
                    }
                } else {
                    invalidWaypointCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("SELECT ID, player_id, homeid, world, locx, locy, locz FROM homes;");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x = result.getInt("locx");
                    int y = result.getInt("locy");
                    int z = result.getInt("locz");
                    Location loc = new Location(world, x, y, z);

                    InnectisLot lot = LotHandler.getLot(loc);

                    String playerIdString = result.getString("player_id");
                    UUID playerId = UUID.fromString(playerIdString);
                    PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);
                    String name = credentials.getName();

                    if (lot != null && !lot.getOwner().equalsIgnoreCase(name) && !lot.containsMember(name) && !lot.containsOperator(name)) {
                        // If player is online make sure to delete the reference to their home
                        IdpPlayer testPlayer = parent.getPlayer(credentials.getUniqueId());

                        if (testPlayer != null) {
                            int homeId = result.getInt("homeid");
                            IdpHomes homes = HomeHandler.getPlayerHomes(testPlayer);
                            homes.deleteHome(homeId);
                        }

                        int id = result.getInt("ID");
                        homeCt++;

                        PreparedStatement statement2 = DBManager.prepareStatement("DELETE FROM homes WHERE ID = ?;");
                        statement2.setInt(1, id);
                        statement2.executeUpdate();

                        DBManager.closePreparedStatement(statement2);
                    }
                } else {
                    invalidHomeCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("SELECT bookcaseid, world, locx, locy, locz FROM bookcase");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x = result.getInt("locx");
                    int y = result.getInt("locy");
                    int z = result.getInt("locz");

                    Block block = world.getBlockAt(x, y, z);
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    if (mat != IdpMaterial.BOOKCASE) {
                        int id = result.getInt("bookcaseid");

                        InnectisBookcase bookcase = InnectisBookcase.getBookcase(id);
                        bookcase.delete();
                        bookcaseCt++;
                    }
                } else {
                    invalidBookcaseCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            statement = DBManager.prepareStatement("SELECT trapdoorid, world, locx, locy, locz FROM trapdoors");
            result = statement.executeQuery();

            while (result.next()) {
                World world = server.getWorld(result.getString("world"));

                if (world != null) {
                    int x = result.getInt("locx");
                    int y = result.getInt("locy");
                    int z = result.getInt("locz");
                    Location loc = new Location(world, x, y, z);

                    Block block = loc.getBlock();
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    if (mat != IdpMaterial.IRON_TRAP_DOOR) {
                        int id = result.getInt("trapdoorid");

                        TrapdoorHandler.removeTrapdoor(id);
                        trapdoorCt++;
                    }
                } else {
                    invalidTrapdoorCt++;
                }
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);

            for (Iterator<Entry<Integer, InnectisLot>> it = LotHandler.getLots().entrySet().iterator(); it.hasNext();) {
                Entry<Integer, InnectisLot> entry = it.next();
                InnectisLot lot = entry.getValue();
                if (System.currentTimeMillis() - lot.getLastOwnerEdit() < 172800000) { //48 hours
                    break;
                } else if (!lot.getOwnerCredentials().isValidPlayer()) {
                    break;
                } else {
                    statement = DBManager.prepareStatement("SELECT onlinetime FROM players WHERE player_id = ?;");
                    statement.setString(1, lot.getOwnerCredentials().getUniqueId().toString());
                    result = statement.executeQuery();

                    if (!(result.next() && result.getFloat("onlinetime") > 10800000)) { //3 hours
                        Player owner = server.getPlayerExact(lot.getOwner());
                        if (owner == null || !owner.isOnline()) {
                            unusedLots += lot.getId() + " ";
                            lotCt++;
                        }
                    }

                    DBManager.closeResultSet(result);
                    DBManager.closePreparedStatement(statement);
                }
            }

            // Catch all
        } catch (Exception ex) {
            Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
            sender.printError("Exception caused cleanup to halt...");
            // Always print
        } finally {
            sender.printInfo("There are " + lotCt + " unused lots: " + unusedLots);
            sender.printInfo("Removed " + chestCt + " chests, " + doorCt + " doors, " + waypointCt + " waypoints, " + homeCt + " homes, " + bookcaseCt + " bookcases, " + trapdoorCt + " trapdoors, and " + incorrectLotMemberCt + " incorrect lot members.");

            // Only print invalid statistics if any objects were not cleaned up due to not
            // being in an existing world
            if (invalidChestCt > 0 || invalidDoorCt > 0 || invalidWaypointCt > 0
                    || invalidHomeCt > 0 || invalidBookcaseCt > 0 || invalidTrapdoorCt > 0) {
                sender.printInfo("Preserved " + invalidChestCt + " chests, " + invalidDoorCt + " doors, " + invalidWaypointCt + " waypoints, "
                        + invalidBookcaseCt + " bookshelves and " + invalidTrapdoorCt + " trapdoors as they "
                        + "are in worlds that aren't loaded.");
            }

            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        // Done! Now resume the crash checker
        if (crashChecker != null) {
            crashChecker.setSuspended(false);
        }

        return true;
    }

    @CommandMethod(aliases = {"finditem"},
    description = "Searches player inventories, ender chests, and chests for items.",
    permission = Permission.command_moderation_finditem,
    usage = "/finditem <id[:data]> [amount [-max]] [-player, -p <player>]",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandFindItem(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, ParameterArguments args) {
        if (args.size() > 0) {
            final IdpMaterial mat = IdpMaterial.fromString(args.getString(0));

            if (mat == null) {
                sender.printError("Material not found.");
                return true;
            }

            int amount = 1;

            if (args.size() > 1) {
                try {
                    amount = Integer.parseInt(args.getString(1));
                } catch (NumberFormatException nfe) {
                    sender.printError("Amount not formatted properly.");
                    return true;
                }
            }

            String playerName = null;
            PlayerCredentials credentials = null;
            IdpPlayer targ = null;

            if (args.getString("player", "p") != null) {
                playerName = args.getString("player", "p");
                targ = parent.getPlayer(playerName);

                if (targ != null) {
                    playerName = targ.getName();
                }

                credentials = PlayerCredentialsManager.getByName(playerName);

                if (credentials == null) {
                    sender.printError("Player not found.");
                    return true;
                }
            }

            List<IdpPlayer> players = new ArrayList<IdpPlayer>(targ != null ? Arrays.asList(targ) : parent.getOnlinePlayers());
            IdpContainer container = null;
            final String matString = (mat == IdpMaterial.AIR ? "infinite item" : mat.getName().toLowerCase());
            int count = 0;
            final boolean useAsMax = args.hasOption("max");

            sender.print(ChatColor.LIGHT_PURPLE, "Finding " + amount + " " + (useAsMax ? "maximum " : "minimum ") + "items of type " + matString);

            if (players.size() > 0) {
                boolean hasCount = false;

                for (IdpPlayer player : players) {
                    IdpPlayerInventory inv = player.getInventory();
                    container = new IdpContainer(inv.getItems(), inv.getItems().length);
                    count = container.countMaterial(mat);

                    if (count > 0 && ((count <= amount && useAsMax) || count >= amount && !useAsMax)) {
                        hasCount = true;
                        sender.print(ChatColor.LIGHT_PURPLE, matString + " count in " + player.getName() + "'s inventory matching filter: " + count);
                    }
                }

                if (hasCount) {
                    sender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                    hasCount = false;
                }

                for (IdpPlayer player : players) {
                    if (player.hasPermission(Permission.special_usebackpack)) {
                        PlayerBackpack backpack = player.getSession().getBackpack();
                        if (backpack != null) {
                            container = new IdpContainer(backpack.getItems(), backpack.getItems().length);
                            count = container.countMaterial(mat);
                        }
                    }

                    if (count > 0 && ((count <= amount && useAsMax) || count >= amount && !useAsMax)) {
                        hasCount = true;
                        sender.print(ChatColor.LIGHT_PURPLE, "Count of items in " + playerName + "'s backpack matching filter: " + amount);
                    }
                }

                if (hasCount) {
                    sender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                }
            } else {
                sender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
            }

            List<InnectisChest> chests = null;

            if (credentials != null) {
                chests = ChestHandler.getChests(playerName);
                sender.print(ChatColor.LIGHT_PURPLE, "Searching all " + chests.size() + " of " + playerName + "'s chests for " + matString + ":");
            } else {
                chests = ChestHandler.getAllChests();
                sender.print(ChatColor.LIGHT_PURPLE, "Searching all " + chests.size() + " chests for " + matString + ":");
            }

            List<ChestDetails> chestDetailsList = new ArrayList<ChestDetails>();

            for (InnectisChest chest : chests) {
                IdpInventory inv = chest.getInventory();

                if (inv != null) {
                    ChestDetails details = new ChestDetails(chest.getOwner(), chest.getId(), new IdpContainer(inv), chest.getChest1().getLocation());
                    chestDetailsList.add(details);
                }
            }

            List<EnderChestContents> enderChestContents = new ArrayList<EnderChestContents>();

            if (playerName != null) {
                List<EnderContentsType> types = EnderChestContents.getAllEnderContentTypes();

                for (EnderContentsType ect : types) {
                    EnderChestContents contents = EnderChestContents.getContents(credentials, ect);

                    if (contents != null) {
                        enderChestContents.add(contents);
                    }
                }

                sender.print(ChatColor.LIGHT_PURPLE, "Searching " + enderChestContents.size() + " of " + playerName + "'s ender chests for " + matString + ":");
            } else {
                enderChestContents = EnderChestContents.getAllChestContents();
                sender.print(ChatColor.LIGHT_PURPLE, "Searching " + enderChestContents.size() + " ender chests for " + matString + ":");
            }

            final IdpCommandSender<? extends CommandSender> finalSender = sender;
            final List<ChestDetails> finalChestDetailsList = chestDetailsList;
            final List<EnderChestContents> finalEnderChestContents = enderChestContents;
            final String finalUsername = playerName;
            final int finalAmount = amount;

            final Thread thread = new Thread(new Runnable() {
                public void run() {
                    if (finalChestDetailsList != null && finalChestDetailsList.size() > 0) {
                        boolean hasCount = false;

                        for (int i = 0; i < finalChestDetailsList.size(); i++) {
                            ChestDetails details = finalChestDetailsList.get(i);
                            IdpContainer container = details.getContainer();
                            int count = container.countMaterial(mat);

                            if (count > 0 && ((count <= finalAmount && useAsMax) || count >= finalAmount && !useAsMax)) {
                                hasCount = true;
                                String owner = details.getOwner();
                                Location loc = details.getLocation();
                                finalSender.print(ChatColor.LIGHT_PURPLE, "#" + details.getId() + " (" + owner + ") " + LocationUtil.locationString(loc) + ": " + count);
                            }
                        }

                        if (hasCount) {
                            finalSender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                        } else {
                            finalSender.print(ChatColor.LIGHT_PURPLE, "No chest contains " + matString + " with an amount of " + finalAmount + "!");
                        }
                    } else {
                        if (finalUsername != null) {
                            finalSender.print(ChatColor.LIGHT_PURPLE, finalUsername + " has no regular chests to search!");
                        } else {
                            finalSender.print(ChatColor.LIGHT_PURPLE, "There are no regular chests to search!");
                        }

                        finalSender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                    }

                    if (finalEnderChestContents != null && finalEnderChestContents.size() > 0) {
                        boolean hasCount = false;

                        for (EnderChestContents enderChest : finalEnderChestContents) {
                            String username = enderChest.getPlayername();
                            int id = enderChest.getChestId();
                            EnderContentsType type = EnderContentsType.getTypeById(enderChest.getTypeId());
                            IdpContainer container = new IdpContainer(enderChest.getItems());
                            int count = container.countMaterial(mat);

                            if (count > 0 && ((count <= finalAmount && useAsMax) || count >= finalAmount && !useAsMax)) {
                                hasCount = true;
                                finalSender.print(ChatColor.LIGHT_PURPLE, "#" + id + " (" + username + "'s " + type.name() + " ender chest): x" + count);
                            }
                        }

                        if (hasCount) {
                            finalSender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                        } else {
                            finalSender.print(ChatColor.LIGHT_PURPLE, "No ender chest contains " + matString + " with an amount of " + finalAmount + "!");
                        }
                    } else {
                        finalSender.print(ChatColor.LIGHT_PURPLE, "There are no ender chests to search!");
                        finalSender.print(ChatColor.LIGHT_PURPLE, "--------------------------");
                    }
                }
            });
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"gc"},
    description = "Garbage collection, clears unused objects in memory.",
    permission = Permission.command_admin_garbagecollection,
    usage = "/gc",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandGc(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        sender.printInfo("Garbage collection!");
        return true;
    }

    @CommandMethod(aliases = {"lock"},
    description = "Locks the block that you're looking at.",
    permission = Permission.command_admin_lock,
    usage = "/lock",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandLock(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        Block block = player.getTargetBlock(5);

        if (block == null) {
            player.printError("You must look at a block to lock it.");
        } else if (player.hasPermission(Permission.admin_blocklock)) {
            Location loc = block.getLocation();
            IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

            if (blockData.isUnbreakable()) {
                player.printError("That block is already locked!");
                return true;
            }

            blockData.setUnbreakable(true);
            player.printInfo("Block (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") is now locked!");
        }

        return true;
    }

    @CommandMethod(aliases = {"openchest"},
    description = "Opens the chest with the given id.",
    permission = Permission.command_admin_openchest,
    usage = "/openchest <id>",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandOpenChest(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        if (args.length != 1) {
            return false;
        }

        int chestId;
        try {
            chestId = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            player.printError("Invalid chest ID!");
            return true;
        }

        InnectisChest chest = ChestHandler.getChest(chestId);
        if (chest == null) {
            player.printError("Could not find any chests with that ID!");
        } else {
            player.openInventory(chest.getInventory());
        }
        return true;
    }

    @CommandMethod(aliases = {"openenderchest", "openechest"},
    description = "Opens a player's enderchest, optionally by world type.",
    permission = Permission.command_admin_openenderchest,
    usage = "/openenderchest <player> [world]",
    serverCommand = false)
    public static boolean commandOpenEnderChest(InnPlugin parent, IdpPlayer player, ParameterArguments args) {
        if (args.size() > 0) {
            String playerName = args.getString(0);
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                playerName = target.getName();
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                player.printError("That player does not exist.");
                return true;
            }

            IdpWorld world = player.getWorld();
            EnderContentsType type = world.getSettings().getEnderchestType();

            if (args.size() > 1) {
                world = IdpWorldFactory.getWorld(args.getString(1));

                if (world == null || world.getWorldType() == IdpWorldType.NONE) {
                    player.printError("Invalid world given.");
                    return true;
                }

                type = world.getSettings().getEnderchestType();
            }

            if (type == EnderContentsType.NONE) {
                player.printError("This world does not have an ender chest type.");
                return true;
            }

            Inventory inventory = Bukkit.createInventory(null, IdpInventory.DEFAULT_CHEST_SIZE, playerName + " (" + type.name().toLowerCase() + ")");

            PlayerSession session = player.getSession();
            session.setEnderchestOwnerId(credentials.getUniqueId());
            session.setEnderchestType(type);
            session.setViewingEnderChest(true);

            player.printInfo("You opened the ender chest contents of " + playerName + " in " + world.getName() + " (Type: " + type.name() + ")");
            player.getHandle().openInventory(inventory);

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"unlock"},
    description = "Unlocks the block you're looking at.",
    permission = Permission.command_admin_unlock,
    usage = "/unlock",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandUnlock(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        Block block = player.getTargetBlock(5);

        if (block == null) {
            player.printError("You must look at a block to unlock it.");
        } else if (player.hasPermission(Permission.admin_blockunlock)) {
            Location loc = block.getLocation();
            IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

            if (!blockData.isUnbreakable()) {
                player.printError("That block is not locked!");
                return true;
            }

            blockData.setUnbreakable(false);
            player.printInfo("Block (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") has been unlocked!");
        }

        return true;
    }

    @CommandMethod(aliases = {"invswitch", "switchinv", "changeinv", "invchange", "swapinv", "invswap", "setinv", "invset", "setinventory", "inventoryset"},
    description = "Switches the inventory of a player with a specified type.",
    permission = Permission.command_admin_invswitch,
    usage = "/invswitch <name> <type>",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandInvSwitch(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 2) {
            return false;
        }
        IdpPlayer player = (IdpPlayer) sender;
        IdpPlayer target = parent.getPlayer(args[0], false);
        if (target == null) {
            player.printError("Player not found!");
            return true;
        }

        InventoryType invType = InventoryType.getTypeByName(args[1]);
        if (invType == null || invType == InventoryType.NONE) {
            player.printError("Unknown inventory type!");
            return true;
        }

        target.saveInventory();
        target.setInventory(invType);
        player.printInfo(target.getName() + "'s inventory switched to " + invType.getName());
        return true;
    }

    @CommandMethod(aliases = {"knockback", "knock", "kb"},
    description = "Knocks back all entities near you.",
    permission = Permission.command_admin_knockback,
    usage = "/knockback [distance] [y-multiplier(float, 0 to 1)]",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandKnockback(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        int distance = 10;
        float yMul = 0.15f;
        if (args.length >= 1) {
            try {
                distance = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                sender.printError("Invalid distance!");
                return true;
            }
        }
        if (args.length >= 2) {
            try {
                yMul = Float.parseFloat(args[1]);
            } catch (NumberFormatException ex) {
                sender.printError("Invalid y-multiplier!");
                return true;
            }
            if (yMul < 0 || yMul > 1) {
                sender.printError("Invalid y-multiplier (out of range)!");
                return true;
            }
        }

        Vector playerVector = player.getLocation().toVector();
        Vector addVector = new Vector(0f, yMul, 0f);
        List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);

        for (Entity entity : nearbyEntities) {
            Vector entityVector = entity.getLocation().toVector();
            Vector vel = entity.getVelocity();

            entity.setVelocity(vel.add(entityVector.subtract(playerVector).add(addVector).normalize().multiply(distance)));
        }

        player.printInfo("Knocked back " + nearbyEntities.size() + " entit" + (nearbyEntities.size() != 1 ? "ies" : "y") + "!");
        return true;
    }

    @CommandMethod(aliases = {"setinfotool", "setinfotooltype", "setinformationtooltype"},
    description = "Sets various modes for the info tool.",
    permission = Permission.command_admin_infotool,
    usage = "/setinfotool chest/block/blockvalue",
    serverCommand = false)
    public static void commandSetToolType(IdpPlayer sender, SmartArguments smartargs) {
        String value = smartargs.getString(0);

        if (StringUtil.matches(value, "chest", "chestlog", "clog")) {
            sender.getSession().setInformationToolType(InformationToolType.ChestInformation);
            sender.printInfo("Chest logs will be shown with the information tool.");
        } else if (StringUtil.matches(value, "block", "blocklog", "blog")) {
            sender.getSession().setInformationToolType(InformationToolType.BlockInformation);
            sender.printInfo("Block logs will be shown with the information tool.");
        } else if (StringUtil.matches(value, "multiblock", "multiblocklog", "mlog")) {
            sender.getSession().setInformationToolType(InformationToolType.MultiBlockInformation);
            sender.printInfo("Multiple block logs will be shown with the information tool.");
        } else if (StringUtil.matches(value, "groupblock", "groupblocklog", "glog")) {
            sender.getSession().setInformationToolType(InformationToolType.GroupBlockInformation);
            sender.printInfo("Group block logs will be shown with the information tool.");
        } else if (StringUtil.matches(value, "datavalue", "datavalues", "blockvalue", "blockvalues")) {
            sender.getSession().setInformationToolType(InformationToolType.BlockValues);
            sender.printInfo("Block values will be shown with the information tool.");
        } else {
            sender.printError("Information tool type not found! (" + value + ")");
        }
    }

    @CommandMethod(aliases = {"setfloatingtext", "sft"},
    description = "Sets a floating text message at the player or their TinyWE selection.",
    permission = Permission.command_admin_setfloatingmessage,
    usage = "/setfloatingtext <message> [-sel] [-remove, -r [-sel]]",
    serverCommand = true)
    public static boolean commandSetFloatingMessage(IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("remove", "r")) {
            List<Entity> entities = null;
            boolean selectionUsed = false;

            if (args.hasOption("sel")) {
                IdpWorldRegion region = player.getRegion();

                if (region == null) {
                    player.printError("You have no TinyWE selection!");
                    return true;
                }

                entities = region.getEntities(ArmorStand.class);
                selectionUsed = true;
            } else {
                entities = LocationUtil.getEntitiesNearLocation(player.getLocation(), EntityType.ARMOR_STAND, 1, 4, 1);
            }

            if (entities.size() > 0) {
                for (Entity entity : entities) {
                    ArmorStand stand = (ArmorStand) entity;

                    // Only remove armor stands that are invisible
                    // signifying floating text stands
                    if (!stand.isVisible()) {
                        stand.remove();
                    }
                }

                player.printInfo("Removed floating text " + (selectionUsed ? "in your selection" : "nearby") + ".");
            } else {
                player.printError("There is no floating text to remove!");
            }

            return true;
        }

        Location standLocation = null;

        if (args.hasOption("sel")) {
            IdpWorldRegion region = player.getRegion();

            if (region == null) {
                player.printError("You have no TinyWE selection!");
                return true;
            }

            if (region.getArea() > 1) {
                player.printError("Your selection is too big. Must be 1x1x1.");
                return true;
            }

            // The position doesn't matter as it is the same
            standLocation = LocationUtil.getCenterLocation(region.getPos1Location());
        } else {
            Location playerLocation = player.getLocation();
            playerLocation.setY(playerLocation.getY() + 1);
            standLocation = playerLocation;
        }

        if (args.getActionSize() == 0) {
            player.printError("No message input!");
            return true;
        }

        String message = ChatColor.parseChatColor(args.combineActions());
        List<String> messages = StringUtil.wrapText(message, 35);
        World world = player.getWorld().getHandle();

        for (int i = 0; i < messages.size(); i++) {
            if (i > 0) {
                standLocation.setY(standLocation.getY() - 0.30D);
            }

            String part = messages.get(i);
            ArmorStand stand = (ArmorStand) world.spawnEntity(standLocation, EntityType.ARMOR_STAND);

            stand.setCustomName(part);
            stand.setCustomNameVisible(true);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setMarker(true);
        }

        player.printInfo("Created floating text with the message: " + message, "!");

        return true;
    }

    @CommandMethod(aliases = {"setgroup"},
    description = "Modifies the group of the specified player.",
    permission = Permission.command_admin_setgroup,
    usage = "/setgroup <username> <group> [-disableannounce, -da]",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandSetGroup(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() < 2) {
            return false;
        }

        String playerName = args.getString(0);
        IdpPlayer targetPlayer = parent.getPlayer(playerName);
        PlayerSession targetSession = null;

        if (targetPlayer != null) {
            targetSession = targetPlayer.getSession();
        } else {
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                sender.printError("That player does not exist.");
                return true;
            }

            targetSession = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), parent, true);
        }

        PlayerGroup group;
        try {
            group = PlayerGroup.getGroup(Integer.parseInt(args.getString(1)));
        } catch (NumberFormatException nfe) {
            group = PlayerGroup.getGroup(args.getString(1));
        }

        if (group == null || group == PlayerGroup.NONE) {
            sender.printError("Group not found!");
            return true;
        }

        String oldColoredName = targetSession.getColoredDisplayName();

        if (!targetSession.setGroup(group)) {
            sender.printInfo("Group could not be saved to the database!");
        }

        List<IdpCommandSender> recipients = new ArrayList<IdpCommandSender>();
        boolean disableAnnouncement = args.hasOption("disableannounce", "da");

        // Make sure console is added
        recipients.add(parent.getConsole());

        if (disableAnnouncement) {
            // Make sure sender is not console before adding to recipients
            if (!recipients.contains(sender)) {
                recipients.add(sender);
            }
        } else {
            for (IdpPlayer onlinePlayer : parent.getOnlinePlayers()) {
                // Make sure the promoted player is not listed, as they are messaged later
                if (targetPlayer == null || !targetPlayer.equals(onlinePlayer)) {
                    recipients.add(onlinePlayer);
                }
            }
        }

        String groupName = group.getPrefix().getTextColor() + group.toString();

        for (IdpCommandSender commandSender : recipients) {
            commandSender.printInfo(oldColoredName, " is now part of the group " + groupName, "!");
        }

        if (disableAnnouncement) {
            sender.print(ChatColor.YELLOW, "Message was not broadcast to server.");
        }

        if (targetPlayer != null) {
            // Set the player's tab list name to include their rank color
            // if their name is less than 16 characters
            if (targetPlayer.getName().length() < 16) {
                targetPlayer.getHandle().setPlayerListName(targetPlayer.getPlayerListName());
            }

            targetPlayer.printInfo("Your group has changed!");
            targetPlayer.printInfo("You are now part of the " + groupName, " group!");

            // reset the player's external permissions
            ExternalPermissionHandler.resetPlayerPermissions(targetPlayer);
        }

        return true;
    }

    @CommandMethod(aliases = {"delworld"},
    description = "Unloads a dynamic world.",
    permission = Permission.command_admin_delworld,
    usage = "/delworld <worlddataname>",
    serverCommand = true)
    public static boolean commanDelWorld(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        if (args.size() != 1) {
            return false;
        }

        String dataname = args.getString(0);

        IdpWorld world = IdpWorldFactory.getWorld(dataname);
        if (world == null) {
            sender.printError("World '" + dataname + "' not found!");
            return true;
        }

        try {
            sender.printInfo("Removing world....");
            IdpWorldFactory.removeDynamicWorld(world, false);
            sender.printInfo("World unloaded.");
        } catch (IdpRuntimeException ex) {
            sender.printError("Could not remove world: " + ex.getMessage());
        }
        return true;
    }

    @CommandMethod(aliases = {"addworld"},
    description = "Loads a dynamic world.",
    permission = Permission.command_admin_addworld,
    usage = "/addworld <worlddataname> [name <name>]",
    serverCommand = true)
    public static boolean commandAddWorld(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        if (args.size() != 1) {
            return false;
        }

        String dataname = args.getString(0);
        String name = args.hasOption("name") ? args.getString("name") : dataname;

        // Use the injector for players
        if (sender.isPlayer()) {
            IdpPlayer player = (IdpPlayer) sender;
            DynWorldBuilder builder = new DynWorldBuilder(name, dataname);

            player.getSession().setChatInjector(builder);
            builder.printInfo(sender);
            return true;
        }


        MapType maptype;
        String typename = args.getString("maptype");
        if (typename == null) {
            maptype = MapType.DEFAULT;
        } else {
            try {
                maptype = MapType.valueOf(typename.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Maptype invalid!");
                return true;
            }
        }

        EnderContentsType endertype;
        String endertypename = args.getString("endertype");
        if (endertypename == null) {
            endertype = EnderContentsType.NONE;
        } else {
            try {
                endertype = EnderContentsType.valueOf(endertypename.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Invalid endercontenttype!");
                return true;
            }
        }

        InventoryType invtype;
        String invtypename = args.getString("invtype");
        if (invtypename == null) {
            invtype = InventoryType.MEMORY1;
        } else {
            try {
                invtype = InventoryType.valueOf(invtypename.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Invalid inventory type!");
                return true;
            }
        }
        // Clear the inventory if requested
        if (args.hasOption("clearinv")) {
            // Only MEMORY/DYNAMIC inventories!
            switch (invtype) {
                case MEMORY1:
                case MEMORY2:
                case MEMORY3:
                case MEMORY4:
                case MEMORY5:
                    PreparedStatement statement = null;

                    try {
                        statement = DBManager.prepareStatement(" DELETE FROM player_inventory WHERE inventorytype = ? ");

                        statement.setInt(1, invtype.getId());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        InnPlugin.logError("Error on clearing inventories!", ex);
                        sender.printError("Cannot clear inventories!");
                        return true;
                    } finally {
                        DBManager.closePreparedStatement(statement);
                    }
            }

        }

        IdpWorldType worldtype;
        String worldtypename = args.getString("worldtype");
        if (worldtypename == null) {
            // Dont load other's
            worldtype = IdpWorldType.DYNAMIC;
        } else {
            try {
                worldtype = IdpWorldType.valueOf(worldtypename.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Worldtype invalid!");
                return true;
            }
        }

        // Default no limit
        int worldsize = args.getIntDefaultTo(Integer.MAX_VALUE, "size");
        // Default to 60 min
        int unloadTime = args.getIntDefaultTo(60, "unload");

        boolean noCommands = args.hasOption("nocmd");
        boolean noWe = args.hasOption("nowe");
        boolean hardcoremode = args.hasOption("hardcore");
        boolean noBuild = args.hasOption("nobuild");
        boolean noHunger = args.hasOption("nohunger");
        boolean tntAllowed = args.hasOption("tntdetonate", "tntdet");

        IdpDynamicWorldSettings settings = new IdpDynamicWorldSettings(
                name, maptype, invtype, endertype, worldsize, unloadTime, worldtype,
                !noCommands, !noWe, hardcoremode, !noBuild, !noHunger, tntAllowed);

        try {
            sender.printInfo("Loading world....");
            IdpWorldFactory.registerDynamicWorld(dataname, settings);

            // Print settings to player
            sender.printInfo("World '" + ChatColor.AQUA + dataname, "' loaded!");
            sender.printInfo("Map type: " + ChatColor.AQUA + maptype);
            sender.printInfo("Inventory type: " + ChatColor.AQUA + invtype);
            sender.printInfo("Ender type: " + ChatColor.AQUA + endertype);
            sender.printInfo("Worldsize: " + ChatColor.AQUA + worldsize);
            sender.printInfo("Unload time: " + ChatColor.AQUA + unloadTime);
            sender.printInfo(" ---< SETTINGS >--- ");
            sender.printInfo("Commands: " + getOnOffmsg(!noCommands));
            sender.printInfo("TinyWE: " + getOnOffmsg(!noWe));
            sender.printInfo("Harcore: " + getOnOffmsg(hardcoremode));
            sender.printInfo("Building: " + getOnOffmsg(!noBuild));
            sender.printInfo("Hunger: " + getOnOffmsg(!noHunger));
            sender.printInfo("TNT autodetonate: " + getOnOffmsg(tntAllowed));

            if (hardcoremode) {
                sender.printError("Hardcore mode not yet supported");
            }

        } catch (IdpRuntimeException ex) {
            sender.printError("Could not register world: " + ex.getMessage());
        }
        return true;
    }

    private static String getOnOffmsg(boolean ison) {
        return ison ? ChatColor.AQUA + "ON" : ChatColor.RED + "OFF";
    }

    @CommandMethod(aliases = {"createpicture"},
    description = "Creates wool art from a picture in the data folder.",
    permission = Permission.command_admin_addworld,
    usage = "/createpicture <filename>",
    serverCommand = true)
    public static boolean commandCreatePicture(InnPlugin parent, IdpPlayer player, ParameterArguments args) {
        if (args.size() != 1) {
            return false;
        }

        InnectisLot lot = LotHandler.getLot(player.getLocation());

        if (lot == null || !lot.isFlagSet(LotFlagType.PIXELBUILD)) {
            player.printError("This is not suggested to use outside of the pixel building area!");
            player.printError("Use the -override option to override this.");
            return true;
        }

        String name = args.getString(0);

        Vector direction;

        switch (player.getFacingDirection()) {
            case NORTH:
            default:
                direction = new Vector(0, 0, -1);
                break;
            case EAST:
                direction = new Vector(1, 0, 0);
                break;
            case SOUTH:
                direction = new Vector(0, 0, 1);
                break;
            case WEST:
                direction = new Vector(-1, 0, 0);
                break;
        }

        PictureCreator creator = new PictureCreator();
        try {
            creator.start(name, player.getLocation(), direction);
        } catch (IOException ex) {
            parent.logError("Error creating picture!", ex);
            player.printError("Internal server error!");
        }
        return true;
    }

    /**
     * Lists the specified player's inventory as a series of strings
     * @param parent
     * @param playerName
     * @return
     */
    private static List<String> listInventoryAsString(InnPlugin parent, InventoryType type, UUID playerId, String playerName) {
        IdpPlayer player = parent.getPlayer(playerId);
        IdpPlayerInventory inv = null;

        if (player != null) {
            inv = player.getInventory();
            InventoryType playerInventoryType = inv.getType();

            // Not viewing the same type, so load the specified type
            if (type != playerInventoryType) {
                inv = IdpPlayerInventory.load(playerId, playerName, type, parent);
            }
        } else {
            inv = IdpPlayerInventory.load(playerId, playerName, type, parent);
        }

        IdpItemStack[] items1 = inv.getItems();
        IdpItemStack[] items2 = inv.getArmorItems();
        IdpItemStack[] items3 = inv.getOffHandItem();

        String amount, id;
        IdpItemStack item;
        int itemcounter = 0;

        List<String> strings = new ArrayList<String>();
        strings.add("Inventory of player: " + playerName);
        StringBuilder currentbuilder = new StringBuilder();

        for (int i = 0; i < 41; i++) {
            itemcounter++;

            if (i < 36) {
                item = items1[i];
            } else if (i < 40) {
                item = items2[i - 36];
            } else {
                item = items3[i - 40];
            }

            if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                currentbuilder.append("---x--, ");
            } else {
                int itemId = item.getMaterial().getId();

                id = (itemId < 100 ? (itemId < 10 ? "00" + itemId : "0" + itemId) : "" + itemId);
                amount = (item.getAmount() < 10 ? "0" + item.getAmount() : "" + item.getAmount());
                currentbuilder.append(id).append("x").append(amount).append(", ");
            }

            if (itemcounter == 5 || itemcounter == 9) {
                strings.add(currentbuilder.toString());
                currentbuilder = new StringBuilder();

                if (itemcounter == 9) {
                    itemcounter = 0;
                }
            }
        }

        return strings;
    }

    /**
     * A private class to handle chest lookup with /finditem
     */
    private static class ChestDetails {

        private String owner;
        private int id;
        private IdpContainer container;
        private Location location;

        public ChestDetails(String owner, int id, IdpContainer container, Location location) {
            this.owner = owner;
            this.id = id;
            this.container = container;
            this.location = location;
        }

        public String getOwner() {
            return owner;
        }

        public int getId() {
            return id;
        }

        public IdpContainer getContainer() {
            return container;
        }

        public Location getLocation() {
            return location;
        }
    }

}

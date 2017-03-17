package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.objects.FrequentLotWarp;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.objects.LotFlagToggle;
import net.innectis.innplugin.objects.IdpHomes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.HomeHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.handlers.PvpHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpConsole;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.objects.IdpHome;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpSpawnFinder;
import net.innectis.innplugin.system.warps.IdpWarp;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.objects.owned.FlagType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisDoor;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisOwnedObject;
import net.innectis.innplugin.objects.owned.LotTag;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.objects.owned.InnectisTrapdoor;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.request.Request;
import net.innectis.innplugin.player.request.TeleportRequest;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory.CountType;
import net.innectis.innplugin.player.tinywe.IdpEditSession;
import net.innectis.innplugin.system.economy.ValutaSinkManager;
import net.innectis.innplugin.util.comparators.FrequentLotWarpComparator;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.comparators.LotSorter;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.comparators.StringSorter;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.StringUtil;
import net.innectis.innplugin.system.warps.WarpSettings;
import net.innectis.innplugin.util.ChatUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public final class LocationCommands {

    @CommandMethod(aliases = {"top"},
    description = "Takes you to the next highest avalible block.",
    permission = Permission.command_location_top,
    usage = "/top",
    serverCommand = false)
    public static boolean commandTop(InnPlugin parent, IdpPlayer player) {
        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        IdpSpawnFinder finder = new IdpSpawnFinder(player.getLocation().add(0, 2, 0));
        Location location = finder.findClosestSpawn(true);

        if (location != null) {
            player.teleport(location, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
            player.printInfo("You have been sent to the next top block!");
        } else {
            player.printError("Unable to find the next top block!");
        }
        return true;
    }

    @CommandMethod(aliases = {"addwarp"},
    description = "Creates a warp at the player's location.",
    permission = Permission.command_location_addwarp,
    usage = "/addwarp <warpname> [-hidden, -h]  [-staff, -s] [-force, -f]",
    serverCommand = false)
    @SuppressWarnings("fallthrough")
    public static boolean commandAddWarp(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;
        String warpName = args.getString(0);
        boolean force = args.hasOption("force", "f");
        IdpWarp warp = WarpHandler.getWarp(args.getString(0));
        boolean exists = (warp != null);
        boolean invalid = (exists && !warp.isValid());

        // Don't add a new warp if it already exists and we're not forcing it
        if (exists && !force) {
            // Only reject if the warp is valid
            if (!invalid) {
                player.printError("The warp with the name " + args.getString(0) + " already exists! ");
                return true;
            }
        }

        boolean hidden = args.hasOption("hidden", "h");
        boolean staffOnly = args.hasOption("staff", "s");

        // Delete existing one if it already exists
        if (exists) {
            if (invalid) {
                player.print(ChatColor.YELLOW, "Invalid warp found. Replacing with new location.");
            } else {
                player.printInfo("Existing warp found. Replacing with new location.");
            }

            WarpHandler.deleteWarp(warp);
        }

        warp = new IdpWarp(warpName, player.getLocation(), "", 0);

        if (hidden) {
            warp.setSetting(WarpSettings.HIDDEN, true);
        }

        if (staffOnly) {
            warp.setSetting(WarpSettings.STAFF_ONLY, true);
        }

        if (WarpHandler.addWarp(warp)) {
            player.printInfo("Warp added!");
        } else {
            player.printError("Could not create warp! Notify an admin!");
        }
        return true;
    }

    @CommandMethod(aliases = {"clearcaughtentities", "cce"},
    description = "Removes all caught entities with the fishing rod.",
    permission = Permission.command_location_removecaughtanimals,
    usage = "/clearcaughtentities",
    serverCommand = false)
    public static boolean commandClearCaughtEntities(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        if (player.getSession().getCaughtEntityTraits().isEmpty()) {
            player.printError("You have no caught entities.");
            return true;
        }

        player.getSession().removeCaughtEntityTraits();
        player.getSession().setRenameOwners(false);
        player.printInfo("All caught entities removed!");

        return true;
    }

    @CommandMethod(aliases = {"compass"},
    description = "Shows the direction the player is looking at.",
    permission = Permission.command_location_compass,
    usage = "/compass",
    serverCommand = false)
    public static void commandCompass(InnPlugin parent, IdpPlayer player, String[] args) {
        if (player.getWorld().getWorldType() == IdpWorldType.NETHER) {
            player.printInfo("Your compass is turning around in circles..");
        } else {
            player.print(ChatColor.LIGHT_PURPLE, "Compass: " + StringUtil.blockFaceToString(player.getFacingDirection()));
        }
    }

    @CommandMethod(aliases = {"delwarp"},
    description = "Deletes a warp by the specified name.",
    permission = Permission.command_location_delwarp,
    usage = "/delwarp <warpname>",
    serverCommand = true)
    public static boolean commandDelWarp(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length == 1) {
            IdpWarp warp = WarpHandler.getWarp(args[0]);

            if (warp == null) {
                sender.printError("That warp doesn't exist. Cannot delete.");
                return true;
            }

            if (WarpHandler.deleteWarp(warp)) {
                sender.printInfo("Warp removed.");
            } else {
                sender.printError("Could not remove warp.");
            }
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"home"},
    description = "Teleports the player to their home.",
    permission = Permission.command_location_home,
    usage = "/home [number, or home name]",
    serverCommand = false)
    public static boolean commandHome(InnPlugin parent, IdpPlayer player, String[] args) {
        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        IdpWorld world = player.getWorld();

        // Check if not in end
        if (world.getSettings().getMaptype() == MapType.THE_END
                && !player.hasPermission(Permission.world_command_override)) {
            player.printError("A strange force is keeping you from doing that.");
            return true;
        }

        IdpHomes homes = HomeHandler.getPlayerHomes(player);

        if (homes.getHomeCount() == 0) {
            player.printError("You have no homes!");
            return true;
        }

        int homeId = 1;
        String homeName = null;
        IdpHome home = null;

        if (args.length >= 1) {
            try {
                homeId = Integer.parseInt(args[0]);

                if (homeId < 1) {
                    player.printError("You cannot use a home number less than 1.");
                    return true;
                }

                home = homes.getHome(homeId);
            } catch (NumberFormatException nfe) {
                homeName = args[0];
                home = homes.getHome(homeName);
            }
        } else {
            home = homes.getHome(homeId);
        }

        if (home == null) {
            if (homeName != null) {
                player.printError("The home with name \"" + homeName + "\" does not exist!");
            } else {
                player.printError("Home number " + homeId + " was not found!");
            }

            return true;
        }

        Location warploc = home.getLocation();

        if (player.teleport(warploc, TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE)) {
            player.print(ChatColor.AQUA, "Warped to home " + home.getId() + (!home.getName().isEmpty() ? " (" + home.getName() + ")" : "") + "!");
        }

        return true;
    }

    @CommandMethod(aliases = {"locateplayer", "lp"},
    description = "Sets compass target to given player or spawn if no username.",
    permission = Permission.command_location_locate,
    usage = "/locateplayer [username]",
    serverCommand = false)
    public static boolean commandLocatePlayer(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length == 1) {
            IdpPlayer locplayer = parent.getPlayer(args[0], false);
            if (locplayer != null && locplayer.isOnline()) {
                player.getHandle().setCompassTarget(locplayer.getLocation());
                player.printInfo("Compass set to " + locplayer.getName());
            } else {
                player.printError("Player not found!");
            }
            return true;
        } else {
            player.getHandle().setCompassTarget(WarpHandler.getSpawn());
            player.printInfo("Compass set to spawn!");

        }
        return true;
    }

    @CommandMethod(aliases = {"sethome", "sh"},
    description = "Sets the home location of the player.",
    permission = Permission.command_location_sethome,
    usage = "/sethome <[home number] or [-name <name>]>",
    disabledWorlds = {IdpWorldType.THE_END, IdpWorldType.RESWORLD, IdpWorldType.DYNAMIC},
    serverCommand = false)
    public static boolean commandSetHome(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        IdpPlayer player = (IdpPlayer) sender;
        int homeId = 1;
        String homeName = "";
        Location loc = player.getLocation();
        InnectisLot lot = LotHandler.getLot(loc);

        if (lot != null && !lot.canPlayerAccess(player.getName()) && !lot.getHidden()
                && !player.hasPermission(Permission.lot_sethome_override)) {
            player.printError("You cannot set your home here!");
            return true;
        }

        if (player.getWorld().getActingWorldType() == IdpWorldType.NETHER
                && !player.hasPermission(Permission.special_nether_tp_override)) {
            player.printError("An unknown force is preventing you from using that command!");
            return true;
        }

        if (args.getActionSize() > 0) {
            try {
                homeId = Integer.parseInt(args.getString(0));

                if (homeId < 1) {
                    player.printError("You cannot use a home number less than 1.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Home argument is not a number.");
                return true;
            }
        }

        if (args.hasArgument("name")) {
            homeName = args.getString("name");
        }

        int maxHomes = HomeHandler.getAvailableHomeCount(player.getGroup());

        if (homeId > maxHomes) {
            player.printError("That home number would exceed your maximum home count.");
            return true;
        }

        IdpHomes homes = HomeHandler.getPlayerHomes(player);
        int nextNumber = (homes.getHomeCount() + 1);

        if (homeId > nextNumber) {
            player.printError("Your next home isn't set! Use /sethome " + nextNumber + " instead.");
            return true;
        }

        boolean updated = homes.addHome(player.getUniqueId(), homeId, homeName, player.getLocation());
        player.printInfo("Home location " + homeId + (!homeName.isEmpty() ? " (name: " + homeName + ")" : "") + " " + (updated ? "updated" : "set") + "!");

        return true;
    }

    @CommandMethod(aliases = {"deletehome", "removehome", "dh", "rh"},
    description = "Deletes a home by its number or home name.",
    permission = Permission.command_location_deletehome,
    usage = "/deletehome <number, or home name>",
    serverCommand = false)
    public static boolean commandDeleteHome(IdpPlayer player, String[] args) {
        if (args.length > 0) {
            int homeId = 0;
            String homeName = null;

            IdpHomes homes = HomeHandler.getPlayerHomes(player);

            if (homes.getHomeCount() == 0) {
                player.printError("You have no homes to delete!");
                return true;
            }

            try {
                homeId = Integer.parseInt(args[0]);

                if (homeId < 1) {
                    player.printError("Home number cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException e) {
                homeName = args[0];
            }

            IdpHome home = null;

            if (homeName != null) {
                home = homes.deleteHome(homeName);
            } else {
                home = homes.deleteHome(homeId);
            }

            if (home != null) {
                player.printInfo("Removed home " + home.getId() + (!home.getName().isEmpty() ? " (" + home.getName() + ")" : "") + ".");
            } else {
                player.printError("You don't have a home by that " + (homeName != null ? "name" : "number") + ".");
            }

            if (homes.getHomeCount() == 0) {
                HomeHandler.removePlayerHomes(player.getUniqueId());
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"listhomes", "homelist"},
    description = "Lists all of the player's homes.",
    permission = Permission.command_location_listhomes,
    usage = "/listhomes",
    serverCommand = false)
    public static boolean commandHomeList(IdpPlayer player) {
        IdpHomes homes = HomeHandler.getPlayerHomes(player);

        if (homes.getHomeCount() == 0) {
            player.printError("You do not have any homes!");
            return true;
        }

        for (IdpHome h : homes.getHomes()) {
            Location loc = h.getLocation();
            String worldName = loc.getWorld().getName();

            player.printInfo("Home " + h.getId() + (!StringUtil.stringIsNullOrEmpty(h.getName()) ? " (" + h.getName() + ")" : "") + ": [" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "] "
                    + "World: " + worldName);
        }

        return true;
    }

    @CommandMethod(aliases = {"setspawn"},
    description = "Sets the spawn location.",
    permission = Permission.command_location_setspawn,
    usage = "/setspawn",
    serverCommand = false)
    public static boolean commandSetSpawn(InnPlugin parent, IdpPlayer player, String[] args) {
        IdpWorldType type = player.getWorld().getActingWorldType();

        World world = player.getWorld().getHandle();
        Location loc = player.getLocation();
        world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        player.printInfo("Spawn location set in world " + world.getName().toLowerCase() + "!");

        // Only change the main spawn warp in the main world
        if (type == IdpWorldType.INNECTIS) {
            IdpWarp warp = WarpHandler.getWarp("spawn");
            if (warp != null) {
                WarpHandler.deleteWarp(warp);
            }

            long settings = WarpSettings.HIDDEN.getSettingsBit();
            warp = new IdpWarp("spawn", player.getLocation(), "Welcome to the spawn!", settings);

            WarpHandler.addWarp(warp);
            WarpHandler.resetSpawnLocations();
            player.printInfo("Main spawnpoint set.");
        }

        return true;
    }

    @CommandMethod(aliases = {"spawn"},
    description = "Sends te player to the spawn.",
    permission = Permission.command_location_spawn,
    usage = "/spawn",
    serverCommand = false)
    public static boolean commandSpawn(InnPlugin parent, IdpPlayer player) {
        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        Location spawn = WarpHandler.getSpawn(player.getGroup());

        if (spawn != null) {
            player.teleport(spawn, TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE);
        } else {
            player.printError("Unable to find spawn warp! Please alert an Admin!");
        }

        return true;
    }

    @CommandMethod(aliases = {"tpp", "tphere"},
    description = "Teleports a player to another player.",
    permission = Permission.command_location_tpp,
    usage = "/tpp <username> [target]",
    usage_Admin = "/tpp <username> [target] [-force, -f]",
    serverCommand = false)
    public static boolean commandTpp(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        try {
            if (args.getActionSize() == 2) {
                String playerString = args.getString(0);

                IdpPlayer tarplayer = parent.getPlayer(playerString, false);

                if (tarplayer == null) {
                    player.printError("Player " + playerString + " not found!");
                    return true;
                }

                IdpWorldType worldType = tarplayer.getWorld().getActingWorldType();

                if (worldType == IdpWorldType.NETHER || worldType == IdpWorldType.THE_END) {
                    // Allow teleporting the target player if forced and the right permission exists
                    boolean force = (args.hasOption("force", "f")
                            && player.hasPermission(Permission.teleport_others_force));

                    if (!force) {
                        String msg = tarplayer.getColoredName() + ChatColor.RED + " is in world: " + worldType.name().toLowerCase() + ".";

                        if (player.hasPermission(Permission.teleport_others_force)) {
                            msg += " Use the -force argument to teleport them.";
                        } else {
                            msg += " Cannot teleport!";
                        }

                        player.printError(msg);
                        return true;
                    }
                }

                Location deslocation;
                String deslocstring = "";

                if (args.getString(1).equalsIgnoreCase("spawn")) {
                    deslocation = WarpHandler.getSpawn(tarplayer.getGroup());
                    deslocstring = "spawn";
                } else {
                    IdpPlayer desplayer = parent.getPlayer(args.getString(1), false);

                    if (desplayer != null) {
                        if (!desplayer.getSession().isVisible()) {
                            if (tarplayer.hasPermission(Permission.teleport_invisible)) {
                                player.getSession().setPlayerVisible(false);
                                player.printInfo("You have teleported to " + tarplayer.getColoredDisplayName() + ChatColor.AQUA, " and are now invisible!");
                                InnPlugin.getPlugin().broadCastStaffMessageExcept(player.getName(), "The player '" + player.getColoredDisplayName() + ChatColor.AQUA + "' is now invisible.", true);
                                tarplayer.teleport(desplayer, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT);
                                return true;
                            } else {
                                player.printError("You cannot teleport " + tarplayer.getColoredDisplayName() + ChatColor.AQUA + " to " + player.getColoredDisplayName() + ChatColor.AQUA + "!");
                                return true;
                            }
                        } else {
                            deslocation = desplayer.getLocation();
                            deslocstring = desplayer.getName();
                        }
                    } else {
                        player.printError("Player " + desplayer.getName() + " not found!");
                        return true;
                    }
                }

                if (tarplayer.teleport(deslocation, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT)) {
                    player.printInfo("Teleporting " + tarplayer.getName() + " to " + deslocstring + ".");
                } else {
                    player.printError("Failed to teleport player!");
                }
                return true;
            } else if (args.getActionSize() == 1) {
                String playerString = args.getString(0);

                IdpPlayer desplayer = parent.getPlayer(playerString, false);
                if (desplayer == null) {
                    player.printError("Player " + playerString + " not found!");
                    return true;
                }

                if (desplayer.getWorld().getWorldType() == IdpWorldType.NETHER) {
                    boolean force = args.hasOption("force", "f")
                            && player.hasPermission(Permission.teleport_others_force);

                    if (!force) {
                        String msg = desplayer.getDisplayName() + ChatColor.RED + " is in the nether.";

                        if (player.hasPermission(Permission.teleport_others_force)) {
                            msg += " Use the -force argument to teleport them.";
                        } else {
                            msg += " Cannot teleport!";
                        }

                        player.printError(msg);
                        return true;
                    }
                }

                if (desplayer.teleport(player, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT)) {
                    player.printInfo("Teleporting player...");
                } else {
                    player.printError("Failed to teleport player!");
                }
                return true;
            }
        } catch (NullPointerException npe) {
            player.printError("Player " + args.getString(0) + " not found!");
        }
        return false;
    }

    @CommandMethod(aliases = {"warp"},
    description = "Teleports the player to a warp location.",
    permission = Permission.command_location_warp,
    usage = "/warp [list] OR [-warps (-w), -lots (-l) [-page, -p <page number>]] OR [warp name]",
    usage_Mod = "/warp [list] OR [-warps (-w), -lots (-l) [-page, -p <page number>]] OR [warp name [-force (-f)]]",
    serverCommand = true)
    public static boolean commandWarp(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() > 0 && args.getString(0).equalsIgnoreCase("list")) {
            int warpCountPublic = 0;
            int warpCountPrivate = 0;
            int warpCountFail = 0;

            for (IdpWarp warp : WarpHandler.getWarps()) {
                if (warp.isValid()) {
                    if (warp.hasSetting(WarpSettings.HIDDEN) || warp.hasSetting(WarpSettings.STAFF_ONLY)) {
                        warpCountPrivate++;
                    } else {
                        warpCountPublic++;
                    }
                } else {
                    warpCountFail++;
                }
            }

            int privateLotTags = 0;
            int publicLotTags = 0;
            int lotNameCount = 0;

            Collection<InnectisLot> lots = LotHandler.getLots().values();

            for (InnectisLot lot : lots) {
                LotTag tag = lot.getTag();

                if (tag != null) {
                    if (tag.isPublic()) {
                        publicLotTags++;
                    } else {
                        privateLotTags++;
                    }
                } else {
                    String lotName = lot.getLotName();

                    if (lotName != null && !lotName.equalsIgnoreCase("")
                            && WarpHandler.getWarp(lotName) == null) {
                        lotNameCount++;
                    }
                }
            }

            boolean canSeePrivate = (sender.hasPermission(Permission.special_view_private_tags)
                                || sender.hasPermission(Permission.warp_show_private));

            sender.printInfo("Showing all Innectis warp types:");
            sender.printInfo("");

            int warpsTotal = 0;
            int lotsTotal = 0;

            if (canSeePrivate) {
                warpsTotal = (warpCountPrivate + warpCountPublic);
                lotsTotal = (privateLotTags + publicLotTags + lotNameCount);
            } else {
                warpsTotal = warpCountPublic;
                lotsTotal = (publicLotTags + lotNameCount);
            }

            sender.printInfo("1. ", ChatColor.AQUA + "" + warpsTotal + ChatColor.DARK_GREEN + " total warps"
                    + (canSeePrivate ? " (" + ChatColor.AQUA + warpCountPrivate + ChatColor.DARK_GREEN + " private warps)" : ""));
            sender.printInfo("2. ", ChatColor.AQUA + "" + lotsTotal + ChatColor.DARK_GREEN + " total lot warps"
                    + (canSeePrivate ? " (" + ChatColor.AQUA + privateLotTags + ChatColor.DARK_GREEN + " private tagged lots)" : ""));

            if (warpCountFail > 0 && canSeePrivate) {
                sender.printError(warpCountFail + " warps could not be loaded.");
            }

            sender.printInfo("");
            sender.printInfo(ChatColor.YELLOW + "Type /warp -warps (-w) to view the warps.");
            sender.printInfo(ChatColor.YELLOW + "Type /warp -lots (-l) to view the lot warps.");
            sender.printInfo(ChatColor.YELLOW + "Type /warp <name> to warp to the specified warp.");

            return true;
        } else if (args.hasOption("warps", "w", "lots", "l")) {
            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = args.getInt("page", "p");
                } catch (NumberFormatException nfe) {
                    sender.printError("Page argument is not a number.");
                    return true;
                }
            }

            List<String> lines = new ArrayList<String>();
            String title = "";

            if (args.hasOption("warps", "w")) {
                List<IdpWarp> warps = WarpHandler.getWarps();

                if (warps.isEmpty()) {
                    sender.printError("There are no warps!");
                    return true;
                }

                List<String> warpLines = new ArrayList<String>();
                int warpsPrivate = 0;
                int warpsPublic = 0;
                int warpsInvalid = 0;

                for (IdpWarp warp : warps) {
                    ChatColor warpColor = null;
                    boolean hidden = warp.hasSetting(WarpSettings.HIDDEN);
                    boolean staffOnly = warp.hasSetting(WarpSettings.STAFF_ONLY);
                    boolean isValid = warp.isValid();
                    boolean privateWarp = (hidden || staffOnly || !isValid);

                    if (!privateWarp || sender.hasPermission(Permission.warp_show_private)) {
                        if (staffOnly) {
                            warpColor = ChatColor.DARK_RED;
                        } else if (hidden) {
                            warpColor = ChatColor.DARK_GRAY;
                        } else if (!isValid) {
                            warpColor = ChatColor.WHITE;
                        } else {
                            warpColor = ChatColor.DARK_GREEN;
                        }

                        if (privateWarp) {
                            warpsPrivate++;
                        } else if (!isValid) {
                            warpsInvalid++;
                        } else {
                            warpsPublic++;
                        }

                        warpLines.add(warpColor + warp.getName());
                    }
                }

                if (sender.hasPermission(Permission.warp_show_private)) {
                    int total = (warpsPrivate + warpsPublic);

                    title = ChatColor.YELLOW + "Listing all " + total + " warps (" + warpsPrivate + " private";

                    if (warpsInvalid > 0) {
                        title += ", " + warpsInvalid + "invalid";
                    }

                    title += "):";
                } else {
                    title = ChatColor.YELLOW + "Listing all " + warpsPublic + " warps:";
                }

                Collections.sort(warpLines, new StringSorter());

                int count = 1;
                String tempLine = "";

                for (String line : warpLines) {
                    if (!tempLine.isEmpty()) {
                        tempLine += ChatColor.WHITE + ", ";
                    }

                    tempLine += line;

                    if (count == 4) {
                        lines.add(tempLine);
                        tempLine = "";
                        count = 1;
                    } else {
                        count++;
                    }
                }

                if (count > 1) {
                    lines.add(tempLine);
                }
            } else if (args.hasOption("lots", "l")){
                Map<LotTag, List<InnectisLot>> taggedLots = new HashMap<LotTag, List<InnectisLot>>();
                List<InnectisLot> otherLots = new ArrayList<InnectisLot>();
                int lotTagsPrivate = 0;
                int lotTagsPublic = 0;

                for (Iterator<InnectisLot> it = LotHandler.getLots().values().iterator(); it.hasNext();) {
                    InnectisLot lot = it.next();
                    LotTag tag = lot.getTag();
                    String lotName = lot.getLotName();

                    if (tag != null) {
                        if (tag.isPublic() || sender.hasPermission(Permission.special_view_private_tags)) {
                            List<InnectisLot> lots = new ArrayList<InnectisLot>();

                            if (taggedLots.containsKey(tag)) {
                                lots = taggedLots.get(tag);
                            } else {
                                taggedLots.put(tag, lots);
                            }

                            lots.add(lot);

                            if (tag.isPublic()) {
                                lotTagsPublic++;
                            } else {
                                lotTagsPrivate++;
                            }
                        }
                    } else if (lotName != null && !lotName.equalsIgnoreCase("")
                            && WarpHandler.getWarp(lot.getLotName()) == null) {
                        otherLots.add(lot);
                    }
                }

                if (taggedLots.isEmpty() && otherLots.isEmpty()) {
                    sender.printError("There are no lots to show!");
                    return true;
                }

                boolean canSeePrivate = (sender.hasPermission(Permission.special_view_private_tags)
                                    || sender.hasPermission(Permission.warp_show_private));
                int totalLotWarps = 0;

                if (canSeePrivate) {
                    totalLotWarps = (lotTagsPrivate + lotTagsPublic + otherLots.size());
                } else {
                    totalLotWarps = (lotTagsPublic + otherLots.size());
                }

                title = ChatColor.YELLOW + "Listing all " + totalLotWarps + " lot warps";

                if (canSeePrivate) {
                    title += " (" + lotTagsPrivate + " private)";
                }

                lines.add("Displaying all categorized lot warps");

                // Add all the tagged lot info to the resulting lines
                for (Map.Entry<LotTag, List<InnectisLot>> entry : taggedLots.entrySet()) {
                    LotTag tag = entry.getKey();
                    List<InnectisLot> lots = entry.getValue();
                    String tempLine = "";
                    int count = 1;

                    lines.add((tag.isPublic() ? ChatColor.AQUA : ChatColor.GRAY) + tag.getTag());

                    Collections.sort(lots, new LotSorter());

                    for (InnectisLot lot : lots) {
                        if (!tempLine.isEmpty()) {
                            tempLine += ChatColor.WHITE + ", ";
                        }

                        tempLine += ChatColor.YELLOW;
                        String tempLotName = lot.getLotName();

                        if (tempLotName != null && !tempLotName.equalsIgnoreCase("")) {
                            tempLine += lot.getLotName();
                        } else {
                            tempLine += "#" + lot.getId();
                        }

                        if (count == 4) {
                            lines.add("  " + tempLine);
                            tempLine = "";
                            count = 1;
                        } else {
                            count++;
                        }
                    }

                    if (count > 1) {
                        lines.add("  " + tempLine);
                    }
                }

                lines.add("Showing non-categorized lot warps: " + ChatColor.RED + "frequent " + ChatColor.YELLOW + "normal");

                // Frequent lot warps (5 or more times warped to)
                List<FrequentLotWarp> frequentLotWarps = new ArrayList<FrequentLotWarp>(120);

                // List of the rest of the lot warps, sorted by name
                List<String> lotWarps = new ArrayList<String>(120);

                // Makes sure lot name isn't added more than once
                List<String> cache = new ArrayList<String>(20);

                for (InnectisLot lot : otherLots) {
                    String lowerName = lot.getLotName().toLowerCase();

                    if (!cache.contains(lowerName)) {
                        cache.add(lowerName);

                        int timesWarped = lot.getTimesWarpUsed();

                        if (timesWarped > 4) {
                            frequentLotWarps.add(new FrequentLotWarp(lot.getLotName(), lot.getTimesWarpUsed()));
                        } else {
                            lotWarps.add(lot.getLotName());
                        }
                    }
                }

                Collections.sort(lotWarps, new StringSorter());

                // Sort the frequent lot warps by how often they were used
                Collections.sort(frequentLotWarps, new FrequentLotWarpComparator());

                String tempLine = "";
                int count = 1;

                for (FrequentLotWarp fwp : frequentLotWarps) {
                    if (!tempLine.isEmpty()) {
                        tempLine += ChatColor.WHITE + ", ";
                    }

                    tempLine += ChatColor.RED + fwp.getName();

                    if (count == 4) {
                        lines.add(tempLine);
                        tempLine = "";
                        count = 1;
                    } else {
                        count++;
                    }
                }

                if (count > 1) {
                    lines.add(tempLine);
                    count = 1;
                }

                for (String line : lotWarps) {
                    if (!tempLine.isEmpty()) {
                        tempLine += ChatColor.WHITE + ", ";
                    }

                    tempLine += ChatColor.YELLOW + line;

                    if (count == 4) {
                        lines.add(tempLine);
                        tempLine = "";
                        count = 1;
                    } else {
                        count++;
                    }
                }

                if (count > 1) {
                    lines.add(tempLine);
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, lines);

            if (ph.isValidPage()) {
                sender.printInfo(title);
                sender.printInfo(ChatColor.AQUA + "Viewing page " + pageNo + " of " + ph.getMaxPage());

                for (String str : ph.getParsedInfo()) {
                    sender.printInfo(str);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }

            return true;
        }

        if (args.getActionSize() == 0) {
            return false;
        }

        //let console use /warp list, but not other stuff
        if (sender instanceof IdpConsole) {
            sender.printError("This command cannot be used on the console!");
            return true;
        }

        // DO NOT post this higher, before this statement the sender CAN be the console!
        IdpPlayer player = (IdpPlayer) sender;

        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        String warpName = args.getString(0);
        IdpWarp warploc = WarpHandler.getWarp(warpName);
        boolean force = args.hasOption("force", "f") && player.hasPermission(Permission.teleport_force);

        if (warploc != null) {
            if (!warploc.isValid()) {
                player.printError("This warp is not valid. Notify an admin!");
                return true;
            }

            if (warploc.hasSetting(WarpSettings.STAFF_ONLY) && !player.getSession().isStaff()) {
                player.printError("You cannot warp here.");
                return true;
            }

            List<TeleportType> teleportTypes = new ArrayList<TeleportType>();
            teleportTypes.add(TeleportType.RESTRICT_IF_NETHER);
            teleportTypes.add(TeleportType.PVP_IMMUNITY);
            teleportTypes.add(TeleportType.RESTRICT_IF_NOESCAPE);

            if (force) {
                teleportTypes.add(TeleportType.IGNORE_RESTRICTION);
            }

            if (player.teleport(warploc.getLocation(), teleportTypes.toArray(new TeleportType[teleportTypes.size()]))) {
                player.printInfo("Warping to " + ChatColor.AQUA + warpName, ".");
            }
        } else {
            InnectisLot lot = args.getLot(0);

            if (lot != null) {
                if (player.teleport(lot.getSpawn(), TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE)) {
                    lot.addWarpTimeUsed();
                    String warpMsg = "Warping to lot #" + lot.getId() + ".";

                    if (lot.getLotName() != null) {
                        warpMsg += " (" + ChatColor.AQUA + lot.getLotName() + ChatColor.DARK_GREEN + ")";
                    }

                    player.printInfo(warpMsg);
                }
            } else {
                player.printError("Warp not found!");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"wpadd", "addwaypoint"},
    description = "Adds a waypoint at the player's location.",
    permission = Permission.command_location_addwaypoint,
    usage = "/wpadd",
    serverCommand = false)
    public static boolean commandWaypointAdd(IdpPlayer player) {
        try {
            Location location = player.getLocation();
            InnectisLot lot = LotHandler.getLot(location, true);
            Block block = location.getBlock();
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat != IdpMaterial.AIR && mat != IdpMaterial.SNOW_LAYER
                    && mat != IdpMaterial.STATIONARY_WATER && mat != IdpMaterial.WATER
                    && mat != IdpMaterial.STATIONARY_LAVA && mat != IdpMaterial.LAVA) {
                player.printError("Unable to create a waypoint here!");
                return true;
            }

            if (lot == null || lot.canPlayerAccess(player.getName())
                    || player.hasPermission(Permission.owned_object_override)) {
                InnectisWaypoint.CostType costType = null;
                TransactionObject to = TransactionHandler.getTransactionObject(player);

                if (!player.hasPermission(Permission.special_waypoint_nodeduct)) {
                    if (!player.hasItemInInventory(IdpMaterial.LAPIS_LAZULI_BLOCK, 3)) {
                        if (to.getValue(TransactionType.VALUTAS) < 30) {
                            player.printError("You do not have the materials to make a waypoint!");
                            player.printError("You need 3 lapis lazuli blocks or 30 vT.");

                            return true;
                        } else {
                            costType = InnectisWaypoint.CostType.VALUTA_COST;
                        }
                    } else {
                        costType = InnectisWaypoint.CostType.LAPIS_COST;
                    }
                } else {
                    costType = InnectisWaypoint.CostType.NO_COST;
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
                InnectisWaypoint waypoint = WaypointHandler.createWaypoint(credentials, block, costType);

                if (waypoint != null) {
                    // Take 30 vT from player if successful and not forced
                    if (costType == InnectisWaypoint.CostType.LAPIS_COST) {
                        player.removeItemFromInventory(IdpMaterial.LAPIS_LAZULI_BLOCK, 3);
                    } else if (costType == InnectisWaypoint.CostType.VALUTA_COST) {
                        to.subtractValue(30, TransactionType.VALUTAS);
                        ValutaSinkManager.addToSink(30);
                    }

                    player.printInfo("Waypoint created! Use \"/wpset " + waypoint.getId() + "\" to set target location.");
                } else {
                    player.printError("Waypoint could not be created, notify an admin!");
                }
            } else {
                player.printError("You cannot add a waypoint here.");
            }
        } catch (SQLException ex) {
            player.printError("An error has occured, notify an admin!");
            InnPlugin.logError("SQLException wpadd " + player.getColoredName(), ex);
        }
        return true;
    }

    @CommandMethod(aliases = {"wpset", "waypointset", "setwaypoint"},
    description = "Sets the destination of a waypoint.",
    permission = Permission.command_location_setwaypoint,
    usage = "/wpset <id>",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD, IdpWorldType.THE_END, IdpWorldType.NETHER})
    public static boolean commandWaypointSet(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;

        InnectisWaypoint waypoint = null;
        int waypointid = 0;

        try {
            waypointid = Integer.parseInt(args[0]);
            waypoint = WaypointHandler.getWaypoint(waypointid);
        } catch (NumberFormatException nfe) {
            player.printError("Waypoint ID is not a number.");
            return true;
        }

        if (waypoint == null) {
            player.printError("Waypoint not found!");
        } else if (waypoint.canPlayerManage(player.getName())
                || player.hasPermission(Permission.owned_waypoint_setall)) {
            InnectisLot lot = LotHandler.getLot(player.getLocation());

            if (lot == null || lot.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.owned_waypoint_setanywhere)) {
                IdpWorld world = IdpWorldFactory.getWorld(waypoint.getWorld().getName());

                if (world.getActingWorldType() == IdpWorldType.CREATIVEWORLD
                        && player.getWorld().getActingWorldType() != IdpWorldType.CREATIVEWORLD
                        && !player.hasPermission(Permission.owned_waypoint_setanywhere)) {
                    player.printError("You cannot set that waypoint here!");
                    return true;
                }

                waypoint.setDestination(player.getLocation());

                if (waypoint.save()) {
                    player.printInfo("Location for waypoint " + waypointid + " has been changed!");
                } else {
                    player.printError("Waypoint not saved properly. Notify an admin!");
                }
            } else {
                player.printError("You cant set the waypoint here!");
            }
        } else {
            player.printError("You are not the owner of that waypoint!");
        }

        return true;
    }

    @CommandMethod(aliases = {"tp", "teleport"},
    description = "Teleports to a player.",
    permission = Permission.command_location_teleport,
    usage = "/tp <username>",
    usage_Mod = "/tp <username> OR [world] <x> [y] <z> [-relative, -r]",
    serverCommand = false)
    public static boolean commandTp(InnPlugin plugin, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;

        try {
            PlayerSession session = player.getSession();

            if (session.isInDamageState()) {
                player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
                return true;
            }

            boolean relative = args.hasOption("relative", "r");

            if (args.getActionSize() == 1) {
                String tarplayerArgument = args.getString(0);
                IdpPlayer tarplayer = plugin.getPlayer(tarplayerArgument, false);

                if (tarplayer == null) {
                    player.printError("Player " + tarplayerArgument + " not found!");
                } else {
                    if (tarplayer.getName().equalsIgnoreCase(player.getName())) {
                        player.printError("You cannot teleport to yourself!");
                        return true;
                    }

                    PlayerSession targetSession = tarplayer.getSession();

                    if (player.hasPermission(Permission.teleport_instant)
                            || targetSession.allowsInstantTeleporting()) {
                        if (targetSession.isVisible()) {
                            List<TeleportType> types = new ArrayList<TeleportType>();
                            types.add(TeleportType.PVP_IMMUNITY);

                            if (player.hasPermission(Permission.teleport_instant)) {
                                types.add(TeleportType.IGNORE_RESTRICTION);
                            }

                            if (player.teleport(tarplayer, types.toArray(new TeleportType[types.size()]))) {
                                player.printInfo("You have teleported to " + tarplayer.getColoredName(), "!");

                                if (targetSession.allowsInstantTeleporting() && session.isVisible()) {
                                    tarplayer.printInfo(player.getColoredDisplayName(), " has teleported to you!");
                                }
                            }
                        } else {
                            if (player.hasPermission(Permission.teleport_invisible)) {
                                player.getSession().setPlayerVisible(false);
                                player.printInfo("You have teleported to " + tarplayer.getColoredName(), " and were made invisible!");
                                InnPlugin.getPlugin().broadCastStaffMessageExcept(player.getName(), "The player '" + player.getColoredDisplayName() + ChatColor.AQUA + "' is now invisible.", true);
                                player.teleport(tarplayer, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY);
                            } else {
                                player.printError("You cannot teleport to this player!");
                            }
                        }
                    } else if (tarplayer.getSession().isIgnored(player.getName())) {
                        player.printError("That player is ignoring you!");
                    } else if (!tarplayer.getSession().canAcceptTeleport()
                            || !tarplayer.getSession().isVisible()) {
                        player.printError("That player is not accepting teleport requests!");
                    } else {
                        Request req = new TeleportRequest(plugin, tarplayer, player, 0, false);

                        if (tarplayer.getSession().addRequest(req)) {
                            tarplayer.print(ChatColor.AQUA, player.getColoredName(), " would like to teleport to you.");
                            tarplayer.print(ChatColor.AQUA, "Please type /accept or /reject within 30 seconds.");

                            TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
                            text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                            text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
                            text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
                            text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
                            tarplayer.print(text);

                            player.print(ChatColor.AQUA, "You have requested to teleport to ", tarplayer.getColoredName(), ".");
                        } else {
                            player.printError("You already have a pending request with that player!");
                        }
                    }
                }
            } else if (args.getActionSize() == 2 && player.hasPermission(Permission.teleport_coords)) {
                int x = args.getInt(0);
                int z = args.getInt(1);

                Location location = null;

                if (relative) {
                    location = player.getLocation();
                    location.add(x, 0, z);
                } else {
                    location = new Location(player.getLocation().getWorld(), x, 60, z);
                }

                location.getWorld().getChunkAt(location); //load chunk to ensure next call works

                IdpSpawnFinder finder = new IdpSpawnFinder(location);
                location = finder.findClosestSpawn(false);

                if (location == null) {
                    player.printError("No safe location found!");
                } else {
                    player.printRaw(ChatColor.AQUA + "Teleporting...");
                    player.teleport(location, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY);
                }
            } else if (args.getActionSize() == 3 && player.hasPermission(Permission.teleport_coords)) {
                int x = args.getInt(0);
                int y = args.getInt(1);
                int z = args.getInt(2);

                Location location = null;

                if (relative) {
                    location = player.getLocation();
                    location.add(x, y, z);
                } else {
                    location = new Location(player.getLocation().getWorld(), x, y, z);
                }

                player.printRaw(ChatColor.AQUA + "Teleporting...");
                player.teleport(location, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY);
            } else if (args.getActionSize() == 4 && player.hasPermission(Permission.teleport_coords)) {
                World world = Bukkit.getWorld(args.getString(0));

                if (world == null) {
                    player.printError("Could not find target world!");
                    return true;
                }

                int x = args.getInt(1);
                int y = args.getInt(2);
                int z = args.getInt(3);

                Location location = null;

                if (relative) {
                    location = player.getLocation();
                    location.setWorld(world);
                    location.add(x, y, z);
                } else {
                    location = new Location(world, x, y, z);
                }

                player.teleport(location, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY);
            } else {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            player.printError("Invalid coordinates!");
        } catch (NullPointerException npe) {
            player.printError("Player not found!");
            InnPlugin.logError("NPE at commandTp", npe);
        }
        return false;
    }

    @CommandMethod(aliases = {"setowner"},
    description = "Sets the owner of an owned object or all lots with a tag.",
    permission = Permission.command_location_setowner,
    usage = "/setowner <[id] <owner> [-inherited, -i for lots]> OR <owner -lottag, -lt <tag>>",
    serverCommand = false)
    public static boolean commandSetOwner(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.hasArgument("lottag", "lt")) {
            String tag = args.getString("lottag", "lt");

            if (args.getActionSize() == 0) {
                return false;
            }

            String owner = args.getString(0);
            PlayerCredentials credentials = null;

            // Check for special owners for the lot
            if (owner.equals("#")) {
                credentials = Configuration.LOT_ASSIGNABLE_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[SYSTEM]")) {
                credentials = Configuration.SYSTEM_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[UNASSIGNED]")) {
                credentials = Configuration.UNASSIGNED_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[GAME]")) {
                credentials = Configuration.GAME_CREDENTIALS;
            } else {
                credentials = PlayerCredentialsManager.getByName(owner, true);

                if (credentials == null) {
                    player.printError("That player doesn't exist!");
                    return true;
                }
            }

            List<InnectisLot> lots = LotHandler.getByTag(tag);

            if (lots.isEmpty()) {
                player.printError("There are no existing lots with tag \"" + tag + "\".");
                return true;
            }

            String idNumbersString = "";

            for (InnectisLot lot : lots) {
                lot.setOwner(credentials);

                if (!lot.save()) {
                    player.printError("There was an error saving lot #" + lot.getId() + ". Notify an admin!");
                    return true;
                }

                if (!idNumbersString.isEmpty()) {
                    idNumbersString += ", ";
                }

                idNumbersString += "#" + lot.getId();
            }

            player.printInfo("Added owner " + credentials.getName() + " to all lots with tag \"" + tag + "\": " + idNumbersString);
        } else {
            if (args.getActionSize() != 1 && args.getActionSize() != 2
                    && !args.hasOption("inherited", "i")) {
                return false;
            }

            int id = 0;
            int argIdx = 0;

            if (args.getActionSize() == 2) {
                try {
                    id = Integer.parseInt(args.getString(0));

                    if (id < 1) {
                        player.printError("ID cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("ID is not a number.");
                    return true;
                }

                argIdx = 1;
            }

            String owner = args.getString(argIdx);
            InnectisOwnedObject obj = null;
            Block block = player.getTargetOwnedBlock();

            if (block != null) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                Location loc = block.getLocation();

                switch (mat) {
                    case CHEST:
                    case TRAPPED_CHEST:
                        if (id > 0) {
                            obj = ChestHandler.getChest(id);
                        } else {
                            obj = ChestHandler.getChest(loc);
                        }

                        break;
                    case LAPIS_LAZULI_OREBLOCK:
                        if (id > 0) {
                            obj = WaypointHandler.getWaypoint(id);
                        } else {
                            obj = WaypointHandler.getWaypoint(loc);
                        }

                        break;
                    case IRON_TRAP_DOOR:
                        if (id > 0) {
                            obj = TrapdoorHandler.getTrapdoor(id);
                        } else {
                            obj = TrapdoorHandler.getTrapdoor(loc);
                        }

                        break;
                    case IRON_DOOR_BLOCK:
                        if (id > 0) {
                            obj = DoorHandler.getDoor(id);
                        } else {
                            obj = DoorHandler.getDoor(loc);
                        }

                        break;
                    case BOOKCASE:
                        if (id > 0) {
                            obj = InnectisBookcase.getBookcase(id);
                        } else {
                            obj = InnectisBookcase.getBookcase(loc);
                        }

                        break;
                }
            }

            if (obj == null) {
                if (id > 0) {
                    obj = LotHandler.getLot(id);
                } else {
                    obj = LotHandler.getLot(player.getLocation());
                }
            }

            if (obj == null) {
                player.printError("You cannot use /setowner here.");
                return true;
            }

            PlayerCredentials previousOwnerCredentials = obj.getOwnerCredentials();
            PlayerCredentials ownerCredentials = null;

            // Only allow assignable, game, and system owners on lots
            if (owner.equals("#") && obj instanceof InnectisLot) {
                ownerCredentials = Configuration.LOT_ASSIGNABLE_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[GAME]") && obj instanceof InnectisLot) {
                ownerCredentials = Configuration.GAME_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[SYSTEM]") && obj instanceof InnectisLot) {
                ownerCredentials = Configuration.SYSTEM_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[UNASSIGNED]")) {
                ownerCredentials = Configuration.UNASSIGNED_CREDENTIALS;
            } else {
                ownerCredentials = PlayerCredentialsManager.getByName(owner, true);

                if (ownerCredentials == null) {
                    player.printError("That player doesn't exist!");
                    return true;
                }
            }

            boolean inherited = false;

            if (obj instanceof InnectisLot) {
                if (args.hasOption("inherited", "i")) {
                    inherited = true;
                }

                InnectisLot lot = (InnectisLot) obj;

                if (inherited) {
                    lot.setOwnerWithSublots(ownerCredentials);
                } else {
                    lot.setOwner(ownerCredentials);
                }

                // Special cleanup for lots
                lot.setLotNumber(0);
                LotHandler.cleanLots(owner);
            } else {
                obj.setOwner(ownerCredentials);
            }

            if (obj.save()) {
                String coloredPreviousOwner = PlayerUtil.getColoredName(previousOwnerCredentials);
                String coloredNewOwner = PlayerUtil.getColoredName(ownerCredentials);

                player.print(ChatColor.AQUA, obj.getType().getName() + " #" + obj.getId() + " owner changed from " + coloredPreviousOwner, " to " + coloredNewOwner, "!");

                if (inherited) {
                    player.printInfo("Owner has been set on all child lots.");
                }
            } else {
                player.printError("An error has occured, notify an admin!");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"tprall"},
    description = "Requests all players to teleport to you.",
    permission = Permission.command_location_teleportall,
    usage = "/tprall [-force] [-override]",
    serverCommand = false)
    public static boolean commandTprAll(InnPlugin parent, IdpPlayer player, ParameterArguments args) {
        List<IdpPlayer> onlinePlayers = parent.getOnlinePlayers();
        boolean force = args.hasOption("force");
        boolean override = args.hasOption("override");

        if (onlinePlayers.size() == 1) {
            player.printError("There are no other players online.");
            return true;
        }

        for (IdpPlayer target : onlinePlayers) {
            if (target.getName().equalsIgnoreCase(player.getName())) {
                continue;
            }

            if (target.getWorld().getActingWorldType() == IdpWorldType.NETHER) {
                player.printError(target.getName() + " is in the nether, teleport failed!");
                continue;
            } else if (target.getSession().isInDamageState()) {
                player.printError(target.getName() + " was recently damaged. Cannot teleport!");
                continue;
            }

            if (force) {
                List<TeleportType> teleportTypes = new ArrayList<TeleportType>();
                teleportTypes.add(TeleportType.PVP_IMMUNITY);

                if (override) {
                    teleportTypes.add(TeleportType.IGNORE_RESTRICTION);
                }

                if (target.teleport(player.getLocation(), teleportTypes.toArray(new TeleportType[teleportTypes.size()]))) {
                    player.printInfo(target.getName() + " successfully teleported to you!");
                } else {
                    player.printError(target.getName() + " failed to teleport to you!");
                }
            } else {
                if (target.getSession().isIgnored(player.getName())) {
                    player.printError(target.getName() + " is ignoring you!");
                    continue;
                } else if (!target.getSession().canAcceptTeleport()) {
                    player.printError(target.getName() + " is not accepting teleport requests!");
                    continue;
                }

                Request req = new TeleportRequest(parent, target, player, 0, true, true);

                if (target.getSession().addRequest(req)) {
                    target.print(ChatColor.AQUA, player.getColoredName(), " would like to teleport ", ChatColor.RED + "you ", "to them.");
                    target.print(ChatColor.AQUA, "Please type /accept or /reject within 30 seconds.");

                    TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
                    text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
                    text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
                    target.print(text);

                    player.print(ChatColor.AQUA, "You have requested to teleport ", target.getColoredName(), " to you.");
                } else {
                    player.printError("You already have a pending request with that player!");
                }
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"tpr"},
    description = "Teleports a player to you, at the cost of 50 or 35 valutas.",
    permission = Permission.command_location_teleportrequest,
    usage = "/tpr <username>",
    usage_Admin = "/tpr <username> [-force, -f]",
    serverCommand = false)
    public static boolean commandTpr(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        IdpPlayer player = (IdpPlayer) sender;
        try {
            if (args.getActionSize() == 1) {
                String playerName = args.getString(0);

                IdpPlayer tarplayer = parent.getPlayer(playerName, false);

                if (tarplayer == null) {
                    player.printError("Player " + playerName + " not found!");
                } else {
                    if (tarplayer.getName().equalsIgnoreCase(player.getName())) {
                        player.printError("You cannot teleport yourself!");
                        return true;
                    }

                    boolean force = args.hasOption("force", "f")
                                && player.hasPermission(Permission.teleport_others_force);

                    // Check restrictions if not force teleporting player
                    if (!force) {
                        PlayerSession session = tarplayer.getSession();

                        if (session.isInDamageState()) {
                            player.printError(tarplayer.getName() + " recently took damage. Cannot teleport!");
                            return true;
                        }

                        IdpWorldType worldType = tarplayer.getWorld().getActingWorldType();

                        if (worldType == IdpWorldType.NETHER || worldType == IdpWorldType.THE_END) {
                            String msg = tarplayer.getColoredDisplayName() + ChatColor.RED + " is in world: " + worldType.name().toLowerCase() + ".";

                            if (player.hasPermission(Permission.teleport_others_force)) {
                                msg += " Use -force to teleport them.";
                            } else {
                                msg += " Cannot teleport.";
                            }

                            player.printError(msg);
                            return true;
                        }
                    }

                    if (player.hasPermission(Permission.teleport_instant)) {
                        player.printInfo("You have teleported " + tarplayer.getColoredName(), " to you.");
                        tarplayer.printInfo(player.getColoredName(), " has teleported you to them.");
                        tarplayer.teleport(player, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT);
                    } else if (tarplayer.getSession().isIgnored(player.getName())) {
                        player.printError("That player is ignoring you!");
                    } else if (!tarplayer.getSession().canAcceptTeleport()) {
                        player.printError("That player is not accepting teleport requests!");
                    } else {
                        int tpCost = 50;

                        if (player.hasPermission(Permission.teleport_tpr_free)) {
                            tpCost = 0;
                        } else if (player.hasPermission(Permission.teleport_tpr_cheaper)) {
                            tpCost = 35;
                        }

                        TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                        int balance = transaction.getValue(TransactionType.VALUTAS);

                        if (balance >= tpCost) {
                            Request req = new TeleportRequest(parent, tarplayer, player, tpCost, true);
                            if (tarplayer.getSession().addRequest(req)) {
                                tarplayer.print(ChatColor.AQUA, player.getColoredName(), " would like to teleport ", ChatColor.RED + "you ", "to them.");
                                tarplayer.print(ChatColor.AQUA, "Please type /accept or /reject within 30 seconds.");

                                TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
                                text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
                                text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
                                text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
                                tarplayer.print(text);

                                player.print(ChatColor.AQUA, "You have requested to teleport ", tarplayer.getColoredName(), " to you.");
                            } else {
                                player.printError("You already have a pending request with that player!");
                            }
                        } else {
                            player.printError("You need at least " + tpCost + " valutas to use this!");
                        }
                    }
                }

                return true;
            }
        } catch (NullPointerException npe) {
            player.printError("Player " + args.getString(0) + " not found!");
        }
        return false;
    }

    @CommandMethod(aliases = {"tpback", "tpfrom"},
    description = "Teleports you to the location you were at before your last teleport.",
    permission = Permission.command_location_teleportback,
    usage = "/tpback",
    serverCommand = false)
    public static boolean commandTpBack(IdpPlayer player) {
        if (player.getSession().getLastTeleportLocation() == null) {
            player.printError("Your last location is unknown!");
            return true;
        }

        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        Location previousLocation = player.getLocation();

        if (player.teleport(session.getLastTeleportLocation(), TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER,
                TeleportType.RAW_COORDINATES, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE)) {
            player.printInfo("You have been teleported to your last location.");

            // Switch their present location with their last teleported location
            session.setLastTeleportLocation(previousLocation);
        }

        return true;
    }

    @CommandMethod(aliases = {"resync"},
    description = "Resyncs the region with owned objects and transfers them to the owner.",
    permission = Permission.command_misc_resync,
    usage = "/resync <lotid> [-override, -o]",
    serverCommand = true)
    public static boolean commandResync(IdpCommandSender sender, ParameterArguments args) {
        int lotid = args.getIntDefaultTo(-1, 0);

        if (lotid < 0) {
            sender.printError("Invalid lot ID! (" + args.getString(0) + ")");
            return false;
        }

        InnectisLot lot = LotHandler.getLot(lotid);

        if (lot == null) {
            sender.printError("Lot was not found!");
            return true;
        }

        PlayerCredentials credentials = lot.getOwnerCredentials();

        if (!credentials.isValidPlayer()) {
            sender.printError("The lot owner is not a valid player. Cannot resync!");
            return true;
        }

        boolean override = args.hasOption("override", "o");
        World world = lot.getWorld();
        String coloredName = PlayerUtil.getColoredName(credentials);

        for (int x = lot.getLowestX(); x <= lot.getHighestX(); x++) {
            for (int y = lot.getLowestY(); y <= lot.getHighestY(); y++) {
                for (int z = lot.getLowestZ(); z <= lot.getHighestZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Location loc = block.getLocation();
                    IdpMaterial mat = IdpMaterial.fromBlock(block);

                    switch (mat) {
                        case LAPIS_LAZULI_OREBLOCK: {
                            InnectisWaypoint waypoint = WaypointHandler.getWaypoint(loc);

                            if (waypoint == null || override) {
                                if (waypoint == null) {
                                    try {
                                        waypoint = WaypointHandler.createWaypoint(credentials, block, InnectisWaypoint.CostType.NO_COST);

                                        if (waypoint != null) {
                                            sender.printInfo(StringUtil.format("Unlinked waypoint at {0} -> #{1}", LocationUtil.locationString(loc), waypoint.getId()));
                                            break;
                                        }
                                    } catch (SQLException ex) {
                                        InnPlugin.logError("Error creating waypoint!", ex);
                                    }

                                    sender.printError(StringUtil.format("Can't link waypoint at {0}", loc));
                                } else {
                                    waypoint.setOwner(credentials);
                                    sender.printInfo("Replaced the owner of waypoint #" + waypoint.getId() + " with " + coloredName, "!");
                                }
                            }
                            break;
                        }
                        case IRON_DOOR_BLOCK: {
                            InnectisDoor door = DoorHandler.getDoor(loc);

                            if (door == null || override) {
                                if (door == null) {
                                    try {
                                        door = DoorHandler.createDoor(credentials, block);

                                        if (door != null) {
                                            sender.printInfo(StringUtil.format("Unlinked door at {0} -> #{1}", LocationUtil.locationString(loc), door.getId()));
                                            break;
                                        }
                                    } catch (SQLException ex) {
                                        InnPlugin.logError("Error creating door!", ex);
                                    }

                                    sender.printError(StringUtil.format("Can't link door at {0}", loc));
                                } else {
                                    door.setOwner(credentials);
                                    sender.printInfo("Replaced the owner of door #" + door.getId() + " with " + coloredName, "!");
                                }
                            }
                            break;
                        }
                        case CHEST:
                        case TRAPPED_CHEST: {
                            InnectisChest chest = ChestHandler.getChest(loc);

                            if (chest == null || override) {
                                if (chest == null) {
                                    try {
                                        chest = ChestHandler.createChest(credentials, block);

                                        if (chest != null) {
                                            sender.printInfo(StringUtil.format("Unlinked chest at {0} -> #{1}", LocationUtil.locationString(loc), chest.getId()));
                                            break;
                                        }
                                    } catch (SQLException ex) {
                                        InnPlugin.logError("Error creating chest!", ex);
                                    }

                                    sender.printError(StringUtil.format("Can't link chest at {0}", loc));
                                } else {
                                    chest.setOwner(credentials);
                                    sender.printInfo("Replaced the owner of chest #" + chest.getId() + " with " + coloredName, "!");
                                }
                            }
                            break;
                        }
                        case IRON_TRAP_DOOR: {
                            InnectisTrapdoor trapdoor = TrapdoorHandler.getTrapdoor(loc);

                            if (trapdoor == null || override) {
                                if (trapdoor == null) {
                                    try {
                                        trapdoor = TrapdoorHandler.createTrapdoor(block.getWorld(), loc, credentials);

                                        if (trapdoor != null) {
                                            sender.printInfo(StringUtil.format("Unlinked trapdoor at {0} -> #{1}", LocationUtil.locationString(loc), trapdoor.getId()));
                                            break;
                                        }
                                    } catch (SQLException ex) {
                                        InnPlugin.logError("Error creating trapdoor!", ex);
                                    }

                                    sender.printError(StringUtil.format("Can't link trapdoor at {0}", loc));
                                } else {
                                    trapdoor.setOwner(credentials);
                                    sender.printInfo("Replaced the owner of trapdoor #" + trapdoor.getId() + " with " + coloredName);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

        sender.printInfo("Lot " + lotid + " resynced!");
        return true;
    }

    @CommandMethod(aliases = {"setlottag"},
    description = "Sets a tag on the specified lot.",
    permission = Permission.command_location_settag,
    usage = "/setlottag <[id] <tag name OR -clear> [-owner, -o <owner>] [-hide, -h]>",
    serverCommand = false)
    public static boolean commandSetLotTag(IdpPlayer player, LynxyArguments args) {
        if (args.getActionSize() == 0 && !args.hasOption("clear", "c")) {
            return false;
        }

        if (args.hasArgument("owner", "o") && player.hasPermission(Permission.command_location_settag_setowner)) {
            String owner = args.getString("owner", "o");
            PlayerCredentials credentials = null;

            // Only allow assignable owner on lots
            if (owner.equals("#")) {
                credentials = Configuration.LOT_ASSIGNABLE_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[SYSTEM]")) {
                credentials = Configuration.SYSTEM_CREDENTIALS;
            } else if (owner.equalsIgnoreCase("[UNASSIGNED]")) {
                credentials = Configuration.UNASSIGNED_CREDENTIALS;
            } else {
                credentials = PlayerCredentialsManager.getByName(owner, true);

                if (credentials == null) {
                    player.printError("That player doesn't exist!");
                    return true;
                }
            }

            String tag = "";
            boolean hidden = false;

            if (!args.hasOption("clear", "c")) {
                tag = args.getString(0);
                hidden = args.hasOption("hide", "h");
            }

            List<InnectisLot> lots = LotHandler.getLots(credentials.getName());

            if (lots.isEmpty()) {
                player.printError("There are no lots with owner " + credentials.getName() + ".");
                return true;
            }

            LotTag lotTag = null;

            if (!tag.isEmpty()) {
                lotTag = new LotTag(tag, !hidden);
            }

            for (InnectisLot lot : lots) {
                lot.setTag(lotTag);

                if (!lot.save()) {
                    player.printError("Unable to save lot #" + lot.getId() + ". Notify an admin!");
                    return true;
                }
            }

            if (tag.isEmpty()) {
                player.printInfo("Cleared existing tag from all lots for " + credentials.getName() + ".");
            } else {
                player.printInfo("Added tag \"" + tag + "\" to all lots for " + credentials.getName() + ".");

                if (hidden) {
                    player.printInfo("The tag has been made hidden.");
                }
            }
        } else {
            if (args.getActionSize() == 0 && !args.hasOption("clear", "c")) {
                return false;
            }

            int id = 0;
            int tagIdx = 0;

            if (args.getActionSize() > 1) {
                try {
                    id = Integer.parseInt(args.getString(0));

                    if (id < 1) {
                        player.printError("ID cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("ID is not a number.");
                    return true;
                }

                tagIdx = 1;
            }

            String tag = "";
            boolean hidden = false;

            if (!args.hasOption("clear", "c")) {
                tag = args.getString(tagIdx);
                hidden = args.hasOption("hide", "h");
            }

            InnectisLot lot = null;

            if (id > 0) {
                lot = LotHandler.getLot(id);
            } else {
                lot = LotHandler.getLot(player.getLocation());
            }

            if (lot == null) {
                player.printError("Cannot use /settag here!");
                return true;
            }

            if (!lot.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("You do not have access to this lot.");
                return true;
            }

            LotTag objTag = null;

            if (!tag.isEmpty()) {
                objTag = new LotTag(tag, !hidden);
            }

            lot.setTag(objTag);

            if (!lot.save()) {
                player.printError("There was an error saving tag to lot #" + lot.getId() + "!");
                return true;
            }

            if (tag.isEmpty()) {
                player.printInfo("Cleared tag from lot #" + lot.getId() + ".");
            } else {
                player.printInfo("Added tag \"" + tag + "\" to lot #" + lot.getId() + ".");

                if (hidden) {
                    player.printInfo("The tag has been made hidden.");
                }
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"setflag"},
    description = "Sets an object's flags.",
    permission = Permission.command_location_setflag,
    usage = "/setflag [id] <flag1[,flag2,...]> [on/off]",
    serverCommand = false)
    public static boolean commandSetFlag(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length < 1 || args.length > 3) {
            return false;
        }

        String flagNameString = null;
        byte flagSwitchType = 2; // 1 = enabled, 0 = disabled, 2 = toggle
        int id = 0;

        if (args.length >= 2) {
            // Possible index to check "on" and "off" string for flag setting
            int idxCheck = 0;

            try {
                id = Integer.parseInt(args[0]);

                if (id < 1) {
                    player.printError("ID cannot be less than 1.");
                    return true;
                }

                flagNameString = args[1];

                if (args.length == 3) {
                    idxCheck = 2;
                }
            } catch (NumberFormatException nfe) {
                // Args length is 3, so ID was not entered properly
                if (args.length == 3) {
                    player.printError("Lot ID is not a number.");
                    return true;
                }

                flagNameString = args[0];
                idxCheck = 1;
            }

            if (idxCheck > 0) {
                if (args[idxCheck].equalsIgnoreCase("on")) {
                    flagSwitchType = 1;
                } else if (args[idxCheck].equalsIgnoreCase("off")) {
                    flagSwitchType = 0;
                } else {
                    player.printError((idxCheck == 1 ? "Second " : "Third ") + "argument must be either \"on\" or \"off\".");
                    return true;
                }
            }
        } else {
            flagNameString = args[0];
        }

        Block block = player.getTargetOwnedBlock();
        InnectisOwnedObject innOwnedObj = null;

        if (block != null) {
            String foundObjectName = null;
            IdpMaterial mat = IdpMaterial.fromBlock(block);
            Location loc = block.getLocation();

            switch (mat) {
                case CHEST:
                case TRAPPED_CHEST:
                    foundObjectName = "chest";

                    if (id > 0) {
                        innOwnedObj = ChestHandler.getChest(id);
                    } else {
                        innOwnedObj = ChestHandler.getChest(loc);
                    }

                    break;
                case BOOKCASE:
                    foundObjectName = "bookcase";

                    if (id > 0) {
                        innOwnedObj = InnectisBookcase.getBookcase(id);
                    } else {
                        innOwnedObj = InnectisBookcase.getBookcase(loc);
                    }

                    break;
                case IRON_DOOR_BLOCK:
                    foundObjectName = "door";

                    if (id > 0) {
                        innOwnedObj = DoorHandler.getDoor(id);
                    } else {
                        innOwnedObj = DoorHandler.getDoor(loc);
                    }

                    break;
                case IRON_TRAP_DOOR:
                    foundObjectName = "trapdoor";

                    if (id > 0) {
                        innOwnedObj = TrapdoorHandler.getTrapdoor(id);
                    } else {
                        innOwnedObj = TrapdoorHandler.getTrapdoor(loc);
                    }

                    break;
                case LAPIS_LAZULI_OREBLOCK:
                    foundObjectName = "waypoint";

                    if (id > 0) {
                        innOwnedObj = WaypointHandler.getWaypoint(id);
                    } else {
                        innOwnedObj = WaypointHandler.getWaypoint(loc);
                    }

                    break;
                case LEVER:
                    foundObjectName = "switch";

                    if (id > 0) {
                        innOwnedObj = InnectisSwitch.getSwitch(id);
                    } else {
                        innOwnedObj = InnectisSwitch.getSwitch(loc);
                    }

                    break;
            }

            // A block representing an owned object found, but not linked to an actual owned object
            if (innOwnedObj == null && foundObjectName != null) {
                player.printError("Unable to find a " + foundObjectName + " with that ID!");
                return true;
            }
        }

        // If no owned object found, attempt to get lot
        if (innOwnedObj == null) {
            if (id > 0) {
                innOwnedObj = LotHandler.getLot(id);
            } else {
                innOwnedObj = LotHandler.getLot(player.getLocation());
            }
        }

        // No owned object found, cannot use this command
        if (innOwnedObj == null) {
            player.printError("Owned object not found! Cannot set flags here.");
            return true;
        }

        String typeName = innOwnedObj.getType().getName();

        if (!innOwnedObj.hasFlags()) {
            player.printError("This " + typeName + " does not have flags you can set.");
            return true;
        }

        if (flagNameString.equalsIgnoreCase("list") || flagNameString.equalsIgnoreCase("?")) {
            List<String> allowedFlagNames = new ArrayList<String>();
            List<String> deniedFlagNames = new ArrayList<String>();

            for (FlagType type : innOwnedObj.getFlagTypes()) {
                if (type.getRequiredGroup() != PlayerGroup.NONE) {
                    if (player.hasFlagPermissions(type)) {
                        allowedFlagNames.add(type.getFlagName());
                    } else {
                        deniedFlagNames.add(type.getFlagName());
                    }
                }
            }

            Collections.sort(allowedFlagNames);
            Collections.sort(deniedFlagNames);

            StringBuilder sb = new StringBuilder(1024);

            for (String flag : allowedFlagNames) {
                sb.append(ChatColor.GREEN).append(flag).append(ChatColor.YELLOW).append(", ");
            }

            for (String flag : deniedFlagNames) {
                sb.append(ChatColor.RED).append(flag).append(ChatColor.YELLOW).append(", ");
            }

            if (sb.length() == 0) {
                player.printError("No flags are available for this " + typeName + "!");
            } else {
                player.print(ChatColor.YELLOW, "Available flags for this " + typeName + ": " + sb.toString().substring(0, sb.length() - 2));
            }

            return true;
        } else {
            String[] flagNames = flagNameString.split(",");

            for (String flagName : flagNames) {
                FlagType flag = innOwnedObj.getFlagsFromString(flagName);

                if (flag == null) {
                    player.printError("The flag \"" + flagNameString + "\" does not exist for this " + typeName + ".");
                    continue;
                }

                if (!player.hasFlagPermissions(flag)) {
                    player.printError("You do not have permission to set that flag!");
                    continue;
                }

                boolean canSet = (innOwnedObj.canPlayerManage(player.getName()) || player.hasPermission(Permission.owned_object_override));

                // Treat lots differently than other owned objects
                if (innOwnedObj instanceof InnectisLot) {
                    InnectisLot lot = (InnectisLot) innOwnedObj;

                    if (!canSet) {
                        canSet = (lot.canPlayerAccess(player.getName()) && player.hasPermission(Permission.lot_memberlot_flagset));
                    }

                    if (!canSet) {
                        player.printError("You cannot set flags on this lot!");
                        return true;
                    }

                    boolean disable = (flagSwitchType == 0 ? true : (flagSwitchType == 1 ? false : innOwnedObj.isFlagSetNoInheritance(flag)));

                    if (flag == LotFlagType.PVP) {
                        long now = System.currentTimeMillis();
                        if (PvpHandler.getLotPvpToggle().containsKey(lot.getId())) {
                            if (now - PvpHandler.getLotPvpToggle().get(lot.getId()) < 60000) {
                                player.printError("PvP flag can only be toggled once every 60 seconds!");
                                return true;
                            }
                        }

                        PvpHandler.getLotPvpToggle().put(lot.getId(), now);
                        parent.getLotFlagToggles().put(now, new LotFlagToggle(lot, flag.getFlagBit(), disable));
                        lot.sendMessageToNearbyPlayers(ChatColor.LIGHT_PURPLE + Configuration.MESSAGE_PREFIX + "PvP will be " + (disable ? "disabled" : "enabled") + " on " + lot.getOwner() + "'s lot (ID: " + lot.getId() + ") in 30 seconds!", 0);

                        return true;
                    }

                    // If HARDCORE flag, clear all players banned from the lot
                    if (flag == LotFlagType.HARDCORE) {
                        lot.clearBanned();
                    }

                    lot.setLotFlag(flag, disable);

                    if (lot.save()) {
                        player.printInfo("Flag \"" + flag.getFlagName() + "\" " + (disable ? "cleared!" : "set") + " on lot #" + lot.getId() + "!");
                    } else {
                        player.printError("An error has occured, notify an admin!");
                    }
                } else {
                    if (!canSet) {
                        player.printError("You do not have permission to set flags on this " + typeName + ".");
                        return true;
                    }

                    boolean disable = (flagSwitchType == 0 ? true : (flagSwitchType == 1 ? false : innOwnedObj.isFlagSetNoInheritance(flag)));

                    innOwnedObj.setFlag(flag.getFlagBit(), disable);

                    if (innOwnedObj.save()) {
                        player.printInfo("Flag \"" + flag.getFlagName() + "\" " + (disable ? "cleared!" : "set") + " on " + typeName + " #" + innOwnedObj.getId() + "!");
                    } else {
                        player.printError("Internal server error! Contact an admin!");
                    }
                }
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"massaccess", "ma"},
    description = "Allows or denies a user to all owned objects in the player's selection.",
    permission = Permission.command_location_massaccess,
    usage = "/massaccess <allow/add/deny/remove> <username>",
    serverCommand = false)
    public static boolean commandMassAccess(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length > 1) {
            IdpRegion region = player.getRegion();

            if (region == null) {
                player.printError("No region found!");
                return true;
            }

            IdpEditSession editSession = player.getSession().getEditSession();

            if (region.getArea() > editSession.getMaxSelectionSize()) {
                player.printError("The total size of your selection is to big, max size: " + editSession.getMaxSelectionSize() + "!");
                return true;
            }

            byte action = 0; // 1 = allow, 2 = deny

            if (args[0].equalsIgnoreCase("allow")
                    || args[0].equalsIgnoreCase("add")) {
                action = 1;
            } else if (args[0].equalsIgnoreCase("deny")
                    || args[0].equalsIgnoreCase("remove")) {
                action = 2;
            }

            // No valid action
            if (action == 0) {
                player.printError("Specify \"allow\" or \"deny\" for player.");
                return true;
            }

            String playerName = args[1];
            boolean modifyOperator = playerName.startsWith("!");

            if (modifyOperator) {
                playerName = playerName.substring(1);
            }

            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                playerName = target.getName();
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

            if (credentials == null) {
                player.printError("That player does not exist.");
                return true;
            } else {
                // Get the proper casing of the name
                playerName = credentials.getName();
            }

            // Lists all checked objects in the selection. This is because multiple
            // locations may go to the same owned object, so we don't want those to
            // get checked
            List<InnectisOwnedObject> queried = new ArrayList<InnectisOwnedObject>();

            for (Block block : BlockCounterFactory.getCounter(CountType.CUBOID).getBlockList(region, player.getLocation().getWorld(), null)) {
                InnectisOwnedObject obj = null;
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                Location loc = block.getLocation();

                switch (mat) {
                    case CHEST:
                    case TRAPPED_CHEST:
                        obj = ChestHandler.getChest(loc);
                        break;
                    case BOOKCASE:
                        obj = InnectisBookcase.getBookcase(loc);
                        break;
                    case LAPIS_LAZULI_OREBLOCK:
                        obj = WaypointHandler.getWaypoint(loc);
                        break;
                    case IRON_TRAP_DOOR:
                        obj = TrapdoorHandler.getTrapdoor(loc);
                        break;
                    case IRON_DOOR_BLOCK:
                        obj = DoorHandler.getDoor(loc);
                        break;
                }

                String saveMsg = null;
                boolean save = false;

                // If an owned object is found
                if (obj != null) {
                    String typeName = obj.getType().getName();

                    // Make sure the object isn't modified more than once
                    if (!queried.contains(obj)) {
                        if (obj.canPlayerManage(player.getName()) || player.hasPermission(Permission.owned_object_override)) {
                            if (obj.getOwner().equalsIgnoreCase(playerName)) {
                                player.printError(playerName + " is owner of " + typeName + " #" + obj.getId() + ". Cannot add!");
                                continue;
                            }

                            if (action == 1) {
                                if (obj.containsMember(playerName) || obj.containsOperator(playerName)) {
                                    if (modifyOperator && obj.containsMember(playerName)) {
                                        obj.addOperator(credentials);
                                        save = true;
                                        saveMsg = "Added " + playerName + " as operator to " + typeName + " #" + obj.getId() + ".";
                                    } else {
                                        player.printError(playerName + " is already added to " + typeName + " #" + obj.getId() + ".");
                                        continue;
                                    }
                                } else {
                                    if (modifyOperator) {
                                        obj.addOperator(credentials);
                                    } else {
                                        obj.addMember(credentials);
                                    }

                                    save = true;
                                    saveMsg = "Added " + playerName + (modifyOperator ? " as operator" : "") + " to " + typeName + " #" + obj.getId() + ".";
                                }
                            } else if (action == 2) {
                                if (obj.containsMember(playerName) || obj.containsOperator(playerName)) {
                                    if (modifyOperator) {
                                        if (obj.containsOperator(playerName)) {
                                            obj.removeOperator(playerName);
                                            save = true;
                                            saveMsg = "Removed " + playerName + " as operator of " + typeName + " #" + obj.getId() + ".";
                                        } else {
                                            player.printError(playerName + " is not an operator of " + typeName + " #" + obj.getId() + ".");
                                            continue;
                                        }
                                    } else {
                                        boolean isOperator = obj.containsOperator(playerName);

                                        if (isOperator) {
                                            obj.removeOperator(playerName);
                                        } else {
                                            obj.removeMember(playerName);
                                        }

                                        save = true;
                                        saveMsg = "Removed " + playerName + (isOperator ? " as operator" : "") + " from " + typeName + " #" + obj.getId() + ".";
                                    }
                                } else {
                                    player.printError(playerName + " is not added to " + typeName + " #" + obj.getId() + ".");
                                    continue;
                                }
                            }

                            if (save) {
                                PlayerCredentialsManager.addCredentialsToCache(credentials);

                                if (obj.save()) {
                                    player.printInfo(saveMsg);
                                } else {
                                    player.printError("Error while saving " + typeName + " #" + obj.getId() + "!");
                                }
                            }
                        } else {
                            player.printError("You do not have permission to" + (action == 1 ? " add " : "remove ") + playerName + (action == 1 ? " to " : " from ") + typeName + " #" + obj.getId() + ".");
                        }

                        queried.add(obj);
                    }
                }
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"massowner", "mo"},
    description = "Sets the owner of all owned objects in the player's selection.",
    permission = Permission.command_location_massowner,
    usage = "/massowner <owner>",
    serverCommand = false)
    public static boolean commandMassOwner(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length > 0) {
            IdpRegion region = player.getRegion();

            if (region == null) {
                player.printError("No region found!");
                return true;
            }

            String targPlayer = args[0];
            IdpPlayer target = parent.getPlayer(targPlayer);

            if (target != null) {
                targPlayer = target.getName();
            }

            PlayerCredentials credentials = null;

            if (targPlayer.equalsIgnoreCase("[UNASSIGNED]")) {
                credentials = Configuration.UNASSIGNED_CREDENTIALS;
            } else {
                credentials = PlayerCredentialsManager.getByName(targPlayer);

                if (credentials == null) {
                    player.printError("That player does not exist.");
                    return true;
                }
            }

            // Lists all checked objects in the selection. This is because multiple
            // locations may go to the same owned object, so we don't want those to
            // get checked
            List<InnectisOwnedObject> queried = new ArrayList<InnectisOwnedObject>();

            // Look for any owned objects, and attempt to remove the specified player from them
            for (Block block : BlockCounterFactory.getCounter(CountType.CUBOID).getBlockList(region, player.getLocation().getWorld(), null)) {
                InnectisOwnedObject obj = null;
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                Location loc = block.getLocation();

                switch (mat) {
                    case CHEST:
                    case TRAPPED_CHEST:
                        obj = ChestHandler.getChest(loc);
                        break;
                    case BOOKCASE:
                        obj = InnectisBookcase.getBookcase(loc);
                        break;
                    case LAPIS_LAZULI_OREBLOCK:
                        obj = WaypointHandler.getWaypoint(loc);
                        break;
                    case IRON_TRAP_DOOR:
                        obj = TrapdoorHandler.getTrapdoor(loc);
                        break;
                    case IRON_DOOR_BLOCK:
                        obj = DoorHandler.getDoor(loc);
                        break;
                }

                // If an owned object is found
                if (obj != null) {
                    String typeName = obj.getType().getName();

                    // Make sure this owned object wasn't previously checked
                    if (!queried.contains(obj)) {
                        if (!obj.getOwner().equalsIgnoreCase(targPlayer)) {
                            String previousOwner = obj.getOwner();
                            obj.setOwner(credentials);
                            PlayerCredentialsManager.addCredentialsToCache(credentials);

                            if (obj.save()) {
                                player.print(ChatColor.AQUA, "Set the owner of " + typeName + " #" + obj.getId() + " from " + previousOwner + " to " + targPlayer + ".");
                            } else {
                                player.printError("There was an error while saving " + typeName + " #" + obj.getId() + ".");
                            }
                        } else {
                            player.printError(targPlayer + " already owns " + typeName + " #" + obj.getId() + ".");
                        }

                        queried.add(obj);
                    }
                }
            }

            return true;
        }

        return false;
    }

}

package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.objects.owned.InnectisLot;
import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.external.api.interfaces.IWorldEditIDP;
import net.innectis.innplugin.external.LibraryType;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpBiome;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpVector2D;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.tinywe.BlockBag;
import net.innectis.innplugin.player.tinywe.BlockBag.TinyWEBlockChanges;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory.CountType;
import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;
import net.innectis.innplugin.player.tinywe.IdpEditSession;
import net.innectis.innplugin.player.tinywe.RegionClipboard;
import net.innectis.innplugin.player.tinywe.RegionClipboard.RegionClipboardResult;
import net.innectis.innplugin.player.tinywe.RegionRegenerationTask;
import net.innectis.innplugin.player.tinywe.RegionSizeException;
import net.innectis.innplugin.player.tinywe.TWEActionNotFinishedException;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.SmartArguments;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public final class TinyWECommands {

    @CommandMethod(aliases = {"twoutline"},
    description = "This will place blocks around the outline of the selection.",
    permission = Permission.command_tinywe_outline,
    usage = "/twoutline <blockid[:data]> [type] [-force, -f]",
    usage_Admin = "/twoutline <blockid[:data]> [type] [-force, -f] [-virtual, -virt, -v]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static boolean commandTWOutline(IdpPlayer player, LynxyArguments args) {
        try {
            if (args.getActionSize() >= 1) {
                IdpEditSession editSession = player.getSession().getEditSession();
                IdpMaterial material = args.getMaterial(0);

                if (material == null) {
                    player.printError("No block found!");
                    return true;
                }

                if (!editSession.canPlaceMaterial(material)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                if (player.getRegion() == null) {
                    player.printError("No region found!");
                    return true;
                }

                boolean virtual = args.hasOption("virtual", "virt", "v");

                if (virtual && !player.hasPermission(Permission.tinywe_override_useanywhere)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                String type = "cuboid";

                if (args.getActionSize() == 2) {
                    type = args.getString(1);
                }

                BlockCounter counter;

                try {
                    counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException iae) {
                    player.printError("Type '", type, "' not found!");
                    return true;
                }

                if (counter == null) {
                    player.printError("The given counter is not supported.");
                    return true;
                }

                BlockBag bag = new BlockBag(player.getRegion());

                try {
                    bag.checkRegion(editSession);
                } catch (RegionSizeException rse) {
                    player.printError(rse.getMessage());
                    return true;
                }

                boolean force = args.hasOption("force", "f");
                List<Block> blocks = counter.getHollowBlockList(player.getRegion(), player.getLocation().getWorld(), null);

                try {
                    TinyWEBlockChanges changes = bag.setBlocks(editSession, blocks, material, force, virtual);
                    int processed = changes.getBlocksProcessed();
                    int amount = changes.getBlocksChanged();
                    int explicitlyIgnored = changes.getExplicitlyIgnored();

                    player.print(ChatColor.LIGHT_PURPLE, "The outline of your selection has been set to " + material.getName().toLowerCase() + ".");
                    player.print(ChatColor.LIGHT_PURPLE, "Total blocks: " + blocks.size() + " (processed: " + processed + ", changed: " + amount + ")");

                    if (!force) {
                        int rejectedCount = changes.getIgnoredBlocks();

                        if (rejectedCount > 0) {
                            player.print(ChatColor.LIGHT_PURPLE, rejectedCount + " blocks were rejected and not removed. Use -force"
                                    + " to remove them without acquiring items.");
                        }
                    }

                    if (explicitlyIgnored > 0) {
                        player.print(ChatColor.LIGHT_PURPLE, explicitlyIgnored + " blocks were explicitly ignored. (cannot be removed by -force)");
                    }

                    if (virtual) {
                        player.print(ChatColor.LIGHT_PURPLE, "The blocks placed are now virtual.");
                    }
                } catch (TWEActionNotFinishedException ex) {
                    player.printError(ex.getMessage());
                }

                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + args.getString(0) + "!");
        }

        return false;
    }

    @CommandMethod(aliases = {"twpos"},
    description = "Sets a point in the selection for TinyWE.",
    permission = Permission.command_tinywe_position,
    usage = "/twpos <nr>",
    serverCommand = false)
    public static boolean commandTWPos(IdpPlayer player, String[] args) {
        if (args.length == 1) {
            if (BlockHandler.canBuildInArea(player, player.getLocation(), BlockHandler.ACTION_BLOCK_PLACED, true)
                    || player.hasPermission(Permission.tinywe_selection_setpointsanywhere)) {
                int locationnr;
                try {
                    locationnr = Integer.parseInt(args[0]);
                } catch (NumberFormatException nfe) {
                    player.printError("Unknown position: " + args[0] + "!");
                    return true;
                }
                if (locationnr != 1 && locationnr != 2) {
                    player.printError("Unknown position: " + args[0] + "!");
                    return true;
                }

                Vector loc = player.getLocation().toVector();
                if (locationnr == 1) {
                    player.setRegionLoc1(loc);
                } else {
                    player.setRegionLoc2(loc);
                }

                IdpWorldRegion region = player.getRegion();

                int tempX = region.getWidth();
                int tempY = region.getHeight();
                int tempZ = region.getLength();

                player.print(ChatColor.LIGHT_PURPLE, (locationnr == 1 ? "1st" : "2nd") + " position set! " + LocationUtil.vectorString(player.getLocation().toVector()) + " " + player.getRegion().getArea() + ") Lot: " + tempX + "x" + tempY + "x" + tempZ);
            } else {
                player.printError("You're not allowed to do that here!");
            }
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"twreplace"},
    description = "Replaces the blocks in the selection with the given block.",
    permission = Permission.command_tinywe_replace,
    usage = "/twreplace <fromblockid[:data];..> <toblockid[:data]> [type] [-force, -f]",
    usage_Admin = "/twreplace <fromblockid[:data];..> <toblockid[:data]> [type] [-force, -f] [-virtual, -virt, -v]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static boolean commandTWReplace(IdpPlayer player, LynxyArguments args) {
        try {
            if (args.getActionSize() >= 2) {
                IdpEditSession editSession = player.getSession().getEditSession();
                MaterialSelector materialSelection = MaterialSelector.fromString(args.getString(0));
                IdpMaterial targetMaterial = args.getMaterial(1);

                if (materialSelection == null || targetMaterial == null) {
                    player.printError("No block found!");
                    return true;
                }

                if (!editSession.canPlaceMaterial(targetMaterial) || !editSession.checkMaterials(materialSelection)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                boolean virtual = args.hasOption("virtual", "virt", "v");

                if (virtual && !player.hasPermission(Permission.tinywe_override_useanywhere)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                if (player.getRegion() == null) {
                    player.printError("No region found!");
                    return true;
                }

                String type = "cuboid"; // Default is cuboid

                if (args.getActionSize() == 3) {
                    type = args.getString(2);
                }

                BlockCounter counter;

                try {
                    counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException iae) {
                    player.printError("Type '", type, "' not found!");
                    return true;
                }

                if (counter == null) {
                    player.printError("The given counter is not supported.");
                    return true;
                }

                BlockBag bag = new BlockBag(player.getRegion());

                try {
                    bag.checkRegion(editSession);
                } catch (RegionSizeException rse) {
                    player.printError(rse.getMessage());
                    return true;
                }

                boolean force = args.hasOption("force", "f");

                // Always blocklist!
                List<Block> blocks = counter.getBlockList(player.getRegion(), player.getLocation().getWorld(), materialSelection);

                try {
                    TinyWEBlockChanges changes = bag.setBlocks(editSession, blocks, targetMaterial, force, virtual);
                    int processed = changes.getBlocksProcessed();
                    int amount = changes.getBlocksChanged();
                    int explicitlyIgnored = changes.getExplicitlyIgnored();

                    player.print(ChatColor.LIGHT_PURPLE, "Replaced the blocks in your selection with " + targetMaterial.getName().toLowerCase() + ".");
                    player.print(ChatColor.LIGHT_PURPLE, "Total blocks: " + blocks.size() + " (processed: " + processed + ", changed: " + amount + ")");

                    if (!force) {
                        int rejectedCount = changes.getIgnoredBlocks();

                        if (rejectedCount > 0) {
                            player.print(ChatColor.LIGHT_PURPLE, rejectedCount + " blocks were rejected and not removed. Use -force"
                                    + " to remove them without acquiring items.");
                        }
                    }

                    if (explicitlyIgnored > 0) {
                        player.print(ChatColor.LIGHT_PURPLE, explicitlyIgnored + " blocks were explicitly ignored. (cannot be removed by -force)");
                    }

                    if (virtual) {
                        player.print(ChatColor.LIGHT_PURPLE, "The replaced blocks are now virtual.");
                    }
                } catch (TWEActionNotFinishedException ex) {
                    player.printError(ex.getMessage());
                }

                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + args.getString(0) + "!");
        }
        return false;
    }

    @CommandMethod(aliases = {"twresize", "twr"},
    description = "Allows the player to expand or contract the selected region.",
    permission = Permission.command_tinywe_resize,
    usage = "/twresize [help [number]] OR <amount> <north, south, east, west, up, down, face, all, n, s, e, w, u, d, f, a> ... [vertical, vert, v]",
    serverCommand = false)
    public static boolean commandTWResize(IdpPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        // Display help
        if (args[0].equalsIgnoreCase("help")
                || args[0].equalsIgnoreCase("h")) {
            int page = 1;

            if (args.length >= 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    player.printError("Page is not a number.");
                    return true;
                }
            }

            List<String> lines = new ArrayList<String>();

            lines.add("help or h [number] : This help page. Page of help screen if number is present.");
            lines.add("<amount> <north/n>: expand/contract the region north");
            lines.add("<amount> <south/s> : expand/contract the region south");
            lines.add("<amount> <east/e> : expand/contract the region east");
            lines.add("<amount> <west/w> : expand/contract the region west");
            lines.add("<amount> <up/u> : expand/contract the region up");
            lines.add("<amount> <down/d> : expand/contract the region down");
            lines.add("<amount> <face/a> : expand/contract the player's facing direction");
            lines.add("<amount> <all/a> : expand/contract the region in all directions");
            lines.add("vertical, vert, v : Expand vertical (so top to down block)");
            lines.add("");
            lines.add("");

            lines.add("It's possible to supply multiple parameters.");
            lines.add("These will add or subtract the others.");
            lines.add("For instance: \"/twresize all 5 west -3\"");
            lines.add("Will extend the region 5 in each direction.");
            lines.add("But only 2 (5-3) in the west Direction!");
            lines.add("");

            PagedCommandHandler ph = new PagedCommandHandler(page, lines);
            ph.setNewLinesPerPage(6);

            if (ph.isValidPage()) {
                player.print(ChatColor.DARK_AQUA, "------------------------");
                player.printInfo("twresize Help - page " + page + " of " + ph.getMaxPage());

                for (String str : ph.getParsedInfo()) {
                    player.printInfo(str);
                }

                player.print(ChatColor.DARK_AQUA, "------------------------");
            } else {
                player.printError(ph.getInvalidPageNumberString());
            }

            return true;
        }

        IdpWorldRegion region = player.getRegion();

        if (region == null) {
            player.printError("No region selected!");
            return true;
        }

        int north = 0;
        int south = 0;
        int east = 0;
        int west = 0;
        int up = 0;
        int down = 0;

        // Checks for odd command length (not divisble by 2)
        boolean oddLength = (args.length % 2 != 0);
        int max = (oddLength ? args.length - 1 : args.length);

        for (int i = 0; i < max; i += 2) {
            int amount = 0;

            try {
                amount = Integer.parseInt(args[i]);
            } catch (NumberFormatException nfe) {
                player.printError("Amount is not a number.");
                return true;
            }

            if (args[i + 1].equalsIgnoreCase("all")
                    || args[i + 1].equalsIgnoreCase("a")) {
                north += amount;
                south += amount;
                east += amount;
                west += amount;
                up += amount;
                down += amount;
            } else if (args[i + 1].equalsIgnoreCase("north")
                    || args[i + 1].equalsIgnoreCase("n")) {
                north += amount;
            } else if (args[i + 1].equalsIgnoreCase("south")
                    || args[i + 1].equalsIgnoreCase("s")) {
                south += amount;
            } else if (args[i + 1].equalsIgnoreCase("east")
                    || args[i + 1].equalsIgnoreCase("e")) {
                east += amount;
            } else if (args[i + 1].equalsIgnoreCase("west")
                    || args[i + 1].equalsIgnoreCase("w")) {
                west += amount;
            } else if (args[i + 1].equalsIgnoreCase("up")
                    || args[i + 1].equalsIgnoreCase("u")) {
                up += amount;
            } else if (args[i + 1].equalsIgnoreCase("down")
                    || args[i + 1].equalsIgnoreCase("d")) {
                down += amount;
            } else if (args[i + 1].equalsIgnoreCase("face")
                    || args[i + 1].equalsIgnoreCase("f")) {
                BlockFace facing = player.getFacingDirection();

                if (facing == BlockFace.NORTH) {
                    north += amount;
                } else if (facing == BlockFace.SOUTH) {
                    south += amount;
                } else if (facing == BlockFace.EAST) {
                    east += amount;
                } else if (facing == BlockFace.WEST) {
                    west += amount;
                }
            } else {
                player.printError("Invalid direction specified.");
                return true;
            }
        }

        // Check for last argument if not divible by 2
        if (oddLength) {
            if (args[max].equalsIgnoreCase("vertical")
                    || args[max].equalsIgnoreCase("vert")
                    || args[max].equalsIgnoreCase("v")) {
                down = region.getLowestY();
                up = region.getWorld().getMaxHeight() - region.getHighestY();
            } else {
                player.printError("Last argument should be \"vertical\", \"vert\", or \"v\".");
                return true;
            }
        }

        region.expand(new Vector(west > 0 ? 0 - west : 0, down > 0 ? 0 - down : 0, north > 0 ? 0 - north : 0));
        region.expand(new Vector(east > 0 ? east : 0, up > 0 ? up : 0, south > 0 ? south : 0));

        region.contract(new Vector(west < 0 ? 0 - west : 0, down < 0 ? 0 - down : 0, north < 0 ? 0 - north : 0));
        region.contract(new Vector(east < 0 ? east : 0, up < 0 ? up : 0, south < 0 ? south : 0));

        player.setRegion(region);
        player.printInfo("Region resized!");

        List<String> info = new ArrayList<String>();

        if (north != 0) {
            info.add(north + " north");
        }

        if (east != 0) {
            info.add(east + " east");
        }

        if (south != 0) {
            info.add(south + " south");
        }

        if (west != 0) {
            info.add(west + " west");
        }

        if (up != 0) {
            info.add(up + " up");
        }

        if (down != 0) {
            info.add(down + " down");
        }

        // Print directions
        for (int i = 0; i < info.size(); i++) {
            String str = info.get(i);
            i++;
            if (i < info.size()) {
                str += ", " + info.get(i);
            }

            player.printInfo(str);
        }

        return true;
    }

    @CommandMethod(aliases = {"twshift"},
    description = "Shifts the TinyWE region in the given direction and amount.",
    permission = Permission.command_tinywe_shift,
    usage = "/twshift <amount> <north, south, east, west, up, down, n, s, e, w, u, d>",
    serverCommand = false)
    public static boolean commandTWShift(Server server, IdpPlayer player, String[] args) {
        if (args.length > 1) {
            String marker = "";
            int coordpos1X = 0;
            int coordpos1Y = 0;
            int coordpos1Z = 0;
            int coordpos2X = 0;
            int coordpos2Y = 0;
            int coordpos2Z = 0;
            int increase = 0;

            if (player.getRegion() == null) {
                player.printError("You have no region set.");
                return true;
            }

            IdpWorldRegion region = player.getRegion();

            try {
                increase = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                player.printError("Value to increase is not a number.");
                return true;
            }

            if (increase < 0) {
                player.printError("Cannot use negative numbers.");
                return true;
            }

            coordpos1X = region.getPos1().getBlockX();
            coordpos2X = region.getPos2().getBlockX();

            coordpos1Y = region.getPos1().getBlockY();
            coordpos2Y = region.getPos2().getBlockY();

            coordpos1Z = region.getPos1().getBlockZ();
            coordpos2Z = region.getPos2().getBlockZ();

            if (args[0].equalsIgnoreCase("north")
                    || args[1].equalsIgnoreCase("n")) {
                int final1Z = coordpos1Z - increase;
                int final2Z = coordpos2Z - increase;
                marker = "north";

                region.setPos1(region.getPos1().setZ(final1Z));
                region.setPos2(region.getPos2().setZ(final2Z));
            } else if (args[0].equalsIgnoreCase("south")
                    || args[1].equalsIgnoreCase("s")) {
                int final1Z = coordpos1Z + increase;
                int final2Z = coordpos2Z + increase;
                marker = "south";

                region.setPos1(region.getPos1().setZ(final1Z));
                region.setPos2(region.getPos2().setZ(final2Z));
            } else if (args[0].equalsIgnoreCase("east")
                    || args[1].equalsIgnoreCase("e")) {
                int final1X = coordpos1X + increase;
                int final2X = coordpos2X + increase;
                marker = "east";

                region.setPos1(region.getPos1().setX(final1X));
                region.setPos2(region.getPos2().setX(final2X));
            } else if (args[0].equalsIgnoreCase("west")
                    || args[1].equalsIgnoreCase("w")) {
                int final1X = coordpos1X - increase;
                int final2X = coordpos2X - increase;
                marker = "west";

                region.setPos1(region.getPos1().setX(final1X));
                region.setPos2(region.getPos2().setX(final2X));
            } else if (args[0].equalsIgnoreCase("up")
                    || args[1].equalsIgnoreCase("u")) {
                int final1Y = coordpos1Y + increase;
                int final2Y = coordpos2Y + increase;
                marker = "up";

                region.setPos1(region.getPos1().setY(final1Y));
                region.setPos2(region.getPos2().setY(final2Y));
            } else if (args[0].equalsIgnoreCase("down")
                    || args[1].equalsIgnoreCase("d")) {
                int final1Y = coordpos1Y - increase;
                int final2Y = coordpos2Y - increase;
                marker = "down";

                region.setPos1(region.getPos1().setY(final1Y));
                region.setPos2(region.getPos2().setY(final2Y));
            } else {
                player.printError("Invalid direction specified.");
                return true;
            }

            player.setRegion(region);
            player.printInfo("Shifted your TinyWE selection " + increase + " block" + (increase == 1 ? "" : "s") + " " + marker + ".");
            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"twset"},
    description = "Sets the blocks in an area to the given block.",
    permission = Permission.command_tinywe_set,
    usage = "/twset <blockid[:data]]> [type] [-force, -f]",
    usage_Admin = "/twset <blockid[:data]]> [type] [-force, -f] [-virtual, -virt, -v]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static boolean commandTWSet(IdpPlayer player, LynxyArguments args) {
        try {
            if (args.getActionSize() >= 1) {
                IdpEditSession editSession = player.getSession().getEditSession();
                IdpMaterial material = args.getMaterial(0);

                if (material == null) {
                    player.printError("No block found");
                    return true;
                }

                if (!editSession.canPlaceMaterial(material)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                boolean virtual = args.hasOption("virtual", "virt", "v");

                if (virtual && !player.hasPermission(Permission.tinywe_override_useanywhere)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                if (player.getRegion() == null) {
                    player.printError("No region found!");
                    return true;
                }

                String type = "cuboid"; // Default is cuboid
                if (args.getActionSize() == 2) {
                    type = args.getString(1);
                }

                BlockCounter counter;

                try {
                    counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException iae) {
                    player.printError("Type '", type, "' not found!");
                    return true;
                }
                if (counter == null) {
                    player.printError("The given counter is not supported.");
                    return true;
                }

                BlockBag bag = new BlockBag(player.getRegion());

                try {
                    bag.checkRegion(editSession);
                } catch (RegionSizeException rse) {
                    player.printError(rse.getMessage());
                    return true;
                }

                boolean force = args.hasOption("force", "f");
                List<Block> blocks = counter.getBlockList(player.getRegion(), player.getLocation().getWorld(), null);

                try {
                    TinyWEBlockChanges changes = bag.setBlocks(editSession, blocks, material, force, virtual);
                    int processed = changes.getBlocksProcessed();
                    int amount = changes.getBlocksChanged();
                    int explicitlyIgnored = changes.getExplicitlyIgnored();

                    player.print(ChatColor.LIGHT_PURPLE, "Set the blocks in your selection to " + material.getName().toLowerCase() + ".");
                    player.print(ChatColor.LIGHT_PURPLE, "Total blocks: " + blocks.size() + " (processed: " + processed + ", changed: " + amount + ")");

                    if (!force) {
                        int rejectedCount = changes.getIgnoredBlocks();

                        if (rejectedCount > 0) {
                            player.print(ChatColor.LIGHT_PURPLE, rejectedCount + " blocks were rejected and not removed. Use -force"
                                    + " to remove them without acquiring items.");
                        }
                    }

                    if (explicitlyIgnored > 0) {
                        player.print(ChatColor.LIGHT_PURPLE, explicitlyIgnored + " blocks were explicitly ignored. (cannot be removed by -force)");
                    }

                    if (virtual) {
                        player.print(ChatColor.LIGHT_PURPLE, "The blocks placed are now virtual.");
                    }
                } catch (TWEActionNotFinishedException ex) {
                    player.printError(ex.getMessage());
                }

                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + args.getString(0) + "!");
        } catch (Exception ex) {
            player.printError("Unknown exception #TWC_TWS_1");
            InnPlugin.logError("Error in TWSet!", ex);
        }

        return false;
    }

    @CommandMethod(aliases = {"twsize"},
    description = "Gives information about the selected region.",
    permission = Permission.command_tinywe_size,
    usage = "/twsize [shape]",
    serverCommand = false)
    public static boolean commandTWSize(IdpPlayer player, SmartArguments smartargs) {
        try {
            IdpEditSession editSession = player.getSession().getEditSession();
            IdpRegion region = player.getRegion();

            if (region == null) {
                player.printError("No region found!");
                return true;
            }

            String type = "cuboid"; // Default is cuboid
            boolean typeselected = false;
            if (smartargs.size() >= 1) {
                typeselected = true;
                type = smartargs.getString(0);
            }

            BlockCounter counter;
            try {
                counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException iae) {
                player.printError("Type '", type, "' not found!");
                return true;
            }
            if (counter == null) {
                player.printError("The given counter is not supported.");
                return true;
            }

            BlockBag bag = new BlockBag(region);

            boolean isTooLarge = false;
            MaterialSelector selector = smartargs.getMaterialSelector(typeselected ? 1 : 0);
            try {
                bag.checkRegion(editSession);
                //amount = bag.countBlocks(editSession, player.getWorld(), selector);
            } catch (RegionSizeException rse) {
                isTooLarge = true;
            }

            int amountHollow = 0, amountFull = 0, amountWall = 0;
            if (!isTooLarge && region.getArea() < 10000) { // Max do this with 10.000 blocks)
                amountFull = counter.getBlockList(region, player.getLocation().getWorld(), selector).size();
                amountHollow = counter.getHollowBlockList(region, player.getLocation().getWorld(), selector).size();
                amountWall = counter.getWallBlockList(region, player.getLocation().getWorld(), selector).size();
            }

            player.print(ChatColor.LIGHT_PURPLE, "--- TinyWE Selection Size ---");
            player.print(ChatColor.LIGHT_PURPLE, "Location 1: " + region.getPos1String());
            player.print(ChatColor.LIGHT_PURPLE, "Location 2: " + region.getPos2String());

            player.print(ChatColor.LIGHT_PURPLE, "Size: (" + region.getWidth() + ", " + region.getHeight() + ", " + region.getLength() + ")");
            player.print(ChatColor.LIGHT_PURPLE, "Total: " + region.getArea());

            player.print(ChatColor.LIGHT_PURPLE, "Area of blocks (cuboid): " + region.getArea() + (isTooLarge ? " (area too large)" : ""));

            if (!isTooLarge && region.getArea() < 10000) {
                player.print(ChatColor.LIGHT_PURPLE, "Amount for Full: " + amountFull);
                player.print(ChatColor.LIGHT_PURPLE, "Amount for Outline: " + amountHollow);
                player.print(ChatColor.LIGHT_PURPLE, "Amount for Walls: " + amountWall);
            }

            return true;

        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + smartargs.getString(0) + "!");
        }
        return false;
    }

    @CommandMethod(aliases = {"twtakeover", "twto"},
    description = "Takes over the selection from somebody else.",
    permission = Permission.command_tinywe_takeover,
    usage = "/twtakeover <username>",
    serverCommand = false)
    public static boolean commandTakeover(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length == 1) {
            IdpPlayer tarplayer = parent.getPlayer(args[0], false);

            if (tarplayer == null) {
                player.printError("Player not found!");
                return true;
            }

            if (tarplayer.equals(player)) {
                player.printError("You cannot take over your own selection!");
                return true;
            }

            IdpWorldRegion region = tarplayer.getRegion();
            if (region == null) {
                player.printError("That player doesn't have a region set!");
                return true;
            }

            player.setRegion(region);

            player.print(ChatColor.LIGHT_PURPLE, "Took over " + tarplayer.getName() + "'s selection!");
            player.print(ChatColor.LIGHT_PURPLE, "Location 1: " + region.getPos1String() + ", " + " Location 2: " + region.getPos2String());
            player.print(ChatColor.LIGHT_PURPLE, "Lot: " + region.getWidth() + "x" + region.getHeight() + "x" + region.getLength());
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"twsendselection", "twss"},
    description = "Copies your selection to the specified player.",
    permission = Permission.command_tinywe_takeover,
    usage = "/twsendselection <username>",
    serverCommand = false)
    public static boolean commandTWSendSelection(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length == 1) {
            IdpPlayer tarplayer = parent.getPlayer(args[0], false);

            if (tarplayer == null) {
                player.printError("Player not found!");
                return true;
            }

            if (tarplayer.equals(player)) {
                player.printError("You cannot send your selection to yourself!");
                return true;
            }

            IdpWorldRegion region = player.getRegion();

            if (region == null) {
                player.printError("You do not have a region set!");
                return true;
            }

            tarplayer.setRegion(region);

            tarplayer.print(ChatColor.LIGHT_PURPLE, player.getName() + " gave you their selection!");
            tarplayer.print(ChatColor.LIGHT_PURPLE, "Location 1: " + region.getPos1String() + ", " + " Location 2: " + region.getPos2String());
            tarplayer.print(ChatColor.LIGHT_PURPLE, "Lot: " + region.getWidth() + "x" + region.getHeight() + "x" + region.getLength());
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"twwalls"},
    description = "Sets the walls in the selection to the target block.",
    permission = Permission.command_tinywe_walls,
    usage = "/twwalls <blockid[:data]> [type] [-force, -f]",
    usage_Admin = "/twwalls <blockid[:data]> [type] [-force, -f] [-virtual, -virt, -v]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static boolean commandTWWalls(IdpPlayer player, LynxyArguments args) {
        try {
            if (args.getActionSize() >= 1) {
                IdpEditSession editSession = player.getSession().getEditSession();
                IdpMaterial material = args.getMaterial(0);

                if (material == null) {
                    player.printError("No block found!");
                    return true;
                }

                if (!editSession.canPlaceMaterial(material)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                boolean virtual = args.hasOption("virtual", "virt", "v");

                if (virtual && !player.hasPermission(Permission.tinywe_override_useanywhere)) {
                    player.printError("This block is not allowed!");
                    return true;
                }

                if (player.getRegion() == null) {
                    player.printError("No region found!");
                    return true;
                }

                String type = "cuboid"; // Default is cuboid
                if (args.getActionSize() == 2) {
                    type = args.getString(1);
                }

                BlockCounter counter;
                try {
                    counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException iae) {
                    player.printError("Type '", type, "' not found!");
                    return true;
                }
                if (counter == null) {
                    player.printError("The given counter is not supported.");
                    return true;
                }
                BlockBag bag = new BlockBag(player.getRegion());

                try {
                    bag.checkRegion(editSession);
                } catch (RegionSizeException rse) {
                    player.printError(rse.getMessage());
                    return true;
                }

                boolean force = args.hasOption("force", "f");

                List<Block> blocks = counter.getWallBlockList(player.getRegion(), player.getLocation().getWorld(), null);

                try {
                    TinyWEBlockChanges changes = bag.setBlocks(editSession, blocks, material, force, virtual);
                    int processed = changes.getBlocksProcessed();
                    int amount = changes.getBlocksChanged();
                    int explicitlyIgnored = changes.getExplicitlyIgnored();

                    player.print(ChatColor.LIGHT_PURPLE, "Set the walls in your selection to " + material.getName().toLowerCase() + ".");
                    player.print(ChatColor.LIGHT_PURPLE, "Total blocks: " + blocks.size() + " (processed: " + processed + ", changed: " + amount + ")");

                    if (!force) {
                        int rejectedCount = changes.getIgnoredBlocks();

                        if (rejectedCount > 0) {
                            player.print(ChatColor.LIGHT_PURPLE, rejectedCount + " blocks were rejected and not removed. Use -force"
                                    + " to remove them without acquiring items.");
                        }
                    }

                    if (explicitlyIgnored > 0) {
                        player.print(ChatColor.LIGHT_PURPLE, explicitlyIgnored + " blocks were explicitly ignored. (cannot be removed by -force)");
                    }

                    if (virtual) {
                        player.print(ChatColor.LIGHT_PURPLE, "The blocks placed are now virtual.");
                    }
                } catch (TWEActionNotFinishedException ex) {
                    player.printError(ex.getMessage());
                }
                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + args.getString(0) + "!");
        }
        return false;
    }

    @CommandMethod(aliases = {"twregen"},
    description = "Regenerates an area.",
    permission = Permission.command_tinywe_regen,
    usage = "/twregen",
    serverCommand = false)
    public static boolean commandTWRegen(InnPlugin parent, IdpPlayer player) {
        IdpWorldRegion region = player.getRegion();
        if (region == null) {
            player.printError("No region found!");
            return true;
        }

        List<InnectisLot> lots = LotHandler.getLotsOverlapping(region, false);
        if (lots != null && player.getWorld().getActingWorldType() != IdpWorldType.NETHER && !player.hasPermission(Permission.tinywe_regen_anywhere)) {
            player.printError("You can't regen an area with lots.");
            return true;
        }

        IWorldEditIDP worldEdit = (IWorldEditIDP) parent.getExternalLibraryManager().getAPIObject(LibraryType.WORLDEDIT);

        if (worldEdit.isAlternative()) {
            player.printError("Cannot regen right now...");
            return true;
        }

        // Add new task
        RegionRegenerationTask task = new RegionRegenerationTask(region, new String[]{player.getName()}) {
            @Override
            protected void beforeChunkRegen(IdpVector2D chunkLocation, IdpWorldRegion chunkRegion) {
                // do nothing
            }

            @Override
            protected void afterChunkRegen(IdpVector2D chunkLocation, IdpWorldRegion chunkRegion) {
                // do nothing
            }
        };
        task.setLastExecution(0l);
        InnPlugin.getPlugin().getTaskManager().addTask(task);

        player.printError("Region is regenerating. Stay close to the area.");

        return true;
    }

    @CommandMethod(aliases = {"twsetbiome", "twbiome"},
    description = "Sets the biome in a region.",
    permission = Permission.command_tinywe_biome,
    usage = "/twsetbiome <biomename>",
    serverCommand = false)
    public static boolean commandTWBiome(InnPlugin parent, IdpPlayer player, ParameterArguments paramargs) {
        IdpWorldRegion region = player.getRegion();
        if (region == null) {
            player.printError("No region found!");
            return true;
        }

        IdpBiome biome = IdpBiome.lookup(paramargs.getString(0));

        if (biome == null) {
            player.printError("Biome not found!");
        } else {
            if (biome.isWorldAllowed(IdpWorldFactory.getWorld(region.getWorld().getName())) || (paramargs.hasOption("override") && player.hasPermission(Permission.tinywe_override_biomeworld))) {
                // Set the biome
                region.setBiome(biome);
                player.printInfo("The biome of this area has been changed to " + biome.name() + "!");
            } else {
                player.printError("The biome " + biome.name() + " is not allowed on this world!");

            }
        }
        return true;
    }

    @CommandMethod(aliases = {"twcount"},
    description = "Counts the amount of blocks within your region.",
    permission = Permission.command_tinywe_count,
    usage = "/twcount <block>",
    serverCommand = false)
    public static boolean commandTWCount(IdpPlayer player, SmartArguments smartargs) {
        try {
            IdpEditSession editSession = player.getSession().getEditSession();
            IdpRegion region = player.getRegion();
            IdpMaterial material = smartargs.getMaterial(0);

            if (material == null) {
                player.printError("No block found");
                return true;
            }

            if (!editSession.canPlaceMaterial(material) || material == IdpMaterial.AIR) {
                player.printError("You cannot count this block");
                return true;
            }

            if (region == null) {
                player.printError("No region found!");
                return true;
            }

            PlayerGroup group = player.getGroup();
            if (!group.equalsOrInherits(PlayerGroup.ADMIN)) {
                if (region.getArea() > (editSession.getMaxSelectionSize() * 20)) {
                    player.printError("Your region is too large!");
                    return true;
                }
            }

            BlockCounter counter;
            try {
                counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);
            } catch (IllegalArgumentException iae) {
                player.printError("An error occured with this command. Please contact an admin!");
                return true;
            }

            MaterialSelector selector = smartargs.getMaterialSelector(0);
            int amount = counter.getBlockList(region, player.getLocation().getWorld(), selector).size();

            player.print(ChatColor.LIGHT_PURPLE, amount + "/" + region.getArea() + " " + material.getName() + " found within your region.");
            return true;

        } catch (NumberFormatException nfe) {
            player.printError("Unknown block: " + smartargs.getString(0) + "!");
        }

        return false;
    }

    @CommandMethod(aliases = {"twcopy"},
    description = "Copies the selected region to your clipboard.",
    permission = Permission.command_tinywe_copy,
    usage = "/twcopy",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static void commandTWCopy(IdpPlayer player) {
        IdpEditSession editSession = player.getSession().getEditSession();

        if (player.getRegion() == null) {
            player.printError("No region found!");
            return;
        }

        if (player.getRegion().getArea() > editSession.getMaxBlockChanges()) {
            player.printError("The total size of your selection is too big, max size: " + editSession.getMaxBlockChanges() + "!");
            return;
        }

        RegionClipboard clipboard = RegionClipboard.getClipboard(player.getName(), player.getRegion(), player.getLocation());

        if (clipboard == null) {
            player.printError("No blocks loaded into your clipboard.");
        } else {
            player.getSession().setClipboard(clipboard);
            player.print(ChatColor.LIGHT_PURPLE, "Loaded " + clipboard.getBlockAmount() + " blocks into your clipboard.");
        }
    }

    @CommandMethod(aliases = {"twpaste"},
    description = "Pastes the region from your clipboard.",
    permission = Permission.command_tinywe_copy,
    usage = "/twpaste [-air]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.RESWORLD})
    public static void commandTWPaste(IdpPlayer player, ParameterArguments args) {
        RegionClipboard clipboard = player.getSession().getClipboard();
        Location newPosition = player.getLocation();

        if (clipboard == null) {
            player.printError("Your clipboard is empty.");
            return;
        }

        IdpWorld world = clipboard.getWorld();

        // Do not allow clipboards taken from creative world to be pasted elsewhere
        if (world.getActingWorldType() == IdpWorldType.CREATIVEWORLD
                && player.getWorld().getActingWorldType() != IdpWorldType.CREATIVEWORLD
                && !player.hasPermission(Permission.tinywe_clipboard_creative_override)) {
            player.printError("Cannot paste the clipboard here as it was taken from creative world.");
            return;
        }

        MaterialSelector selector = new MaterialSelector();
        selector.setMode(true);

        // Exclude air if option is set.
        if (args.hasOption("air")) {
            selector.addMaterials(IdpMaterial.AIR);
        }

        RegionClipboardResult result = clipboard.setBlocks(newPosition, selector);
        int count = result.getChanged();

        if (count == 0) {
            player.printError("No blocks were pasted from your clipboard.");
        } else {
            player.print(ChatColor.LIGHT_PURPLE, "Pasted " + count + " blocks from your clipboard.");

            int ignored = result.getIgnored();

            if (ignored > 0) {
                player.print(ChatColor.LIGHT_PURPLE, ignored + " blocks were not pasted because you "
                        + "could not acquire them due to a full inventory.");
            }
        }
    }

    @CommandMethod(aliases = {"twclipboard", "twclip", "twc"},
    description = "Manages your TinyWE clipboard.",
    permission = Permission.command_tinywe_clipboard,
    usage = "/twclipboard [-clear] [-rotate [degrees]]",
    serverCommand = false)
    public static boolean commandTWClipboard(IdpPlayer player, ParameterArguments args) {
        if (args.hasOption("clear")) {
            player.getSession().setClipboard(null);
            player.print(ChatColor.LIGHT_PURPLE, "You have cleared your clipboard.");
            return true;
        }

        if (args.hasOption("rotate")) {
            RegionClipboard clipboard = player.getSession().getClipboard();

            if (clipboard == null) {
                player.printError("Your clipboard is empty.");
                return true;
            }

            int degrees = 0;

            try {
                degrees = Integer.parseInt(args.getString("rotate"));

                if (degrees <= 0) {
                    player.printError("Degrees cannot be 0 or less.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Degrees is not a number.");
                return true;
            }

            if (clipboard.rotate(degrees)) {
                player.getSession().setClipboard(clipboard);
                player.print(ChatColor.LIGHT_PURPLE, "You have rotated your clipboard " + degrees + " degrees.");
            } else {
                player.printError("Cannot rotate with degrees of " + degrees + ".");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"twrotate"},
    description = "Rotates the direction of various blocks.",
    permission = Permission.command_tinywe_rotate,
    usage = "/twrotate <north/south/east/west/up/down>",
    serverCommand = false)
    public static boolean commandTWRotate(IdpPlayer player, String[] args) {
        if (args.length != 1) {
            return false;
        }

        IdpRegion region = player.getRegion();

        if (region == null) {
            player.printError("No region found!");
            return true;
        }

        IdpEditSession session = player.getSession().getEditSession();

        if (region.getArea() > session.getMaxSelectionSize()) {
            player.printError("Your block selection exceeds your rank's max!");
            return true;
        }

        BlockFace direction = null;
        String directionString = args[0];

        if (directionString.equalsIgnoreCase("north")) {
            direction = BlockFace.NORTH;
        } else if (directionString.equalsIgnoreCase("south")) {
            direction = BlockFace.SOUTH;
        } else if (directionString.equalsIgnoreCase("east")) {
            direction = BlockFace.EAST;
        } else if (directionString.equalsIgnoreCase("west")) {
            direction = BlockFace.WEST;
        } else {
            player.printError("Invalid direction. Use \"north\", \"south\", \"east\", or \"west\".");
            return true;
        }

        List<Block> blocks = BlockCounterFactory.getCounter(CountType.CUBOID).getBlockList(region, player.getHandle().getWorld(), null);
        int maxArea = session.getMaxSelectionSize();
        int maxChanges = session.getMaxBlockChanges();
        int countArea = 0;
        int countChanges = 0;

        for (Block block : blocks) {
            if (countArea < maxArea) {
                if (countChanges < maxChanges) {
                    if (BlockHandler.canBuildInArea(player, block.getLocation(), BlockHandler.ACTION_BLOCK_PLACED, true)) {
                        BlockState state = block.getState();
                        boolean dataSet = BlockHandler.rotateBlock(state, direction);

                        if (dataSet) {
                            state.update();
                            countChanges++;
                        }
                    }

                    countArea++;
                } else {
                    player.printError("Max rotations exceeded max block changes. Aborting...");
                    break;
                }
            } else {
                player.printError("Max selection size exceeded. Aborting...");
                break;
            }
        }

        player.printInfo("Rotated " + countChanges + " blocks " + directionString + ".");

        return true;
    }

}

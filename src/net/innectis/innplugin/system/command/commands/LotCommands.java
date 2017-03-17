package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.location.IdpVector2D;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.NotANumberException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.ChunkDatamanager;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.objects.owned.LotTag;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.request.LotRemovalRequest;
import net.innectis.innplugin.player.request.LotResetRequest;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;
import net.innectis.innplugin.player.tinywe.RegionEditTask;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.PlayerUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.util.Vector;

public final class LotCommands {

    @CommandMethod(aliases = {"checkmemberbuild", "cmb"},
    description = "Checks all non-staff lots where members built last, for a certain time range.",
    permission = Permission.command_lot_checkmemberbuild,
    usage = "/checkmemberbuild <member build (Ex. 5h3m)>",
    serverCommand = true)
    public static boolean commandMemberBuild(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String buildFormula = args[0];
        long lastBuildTime = DateUtil.getTimeFormula(buildFormula);

        if (lastBuildTime == -1) {
            sender.printError("Invalid time formula entered. (Ex. 5h3m)");
            return true;
        }

        List<InnectisLot> foundLots = new ArrayList<InnectisLot>();

        for (InnectisLot lot : LotHandler.getLots().values()) {
            PlayerGroup group = PlayerGroup.getGroupOfPlayerById(lot.getOwnerCredentials().getUniqueId());

            // Don't check special lots or staff lots
            if (group == PlayerGroup.NONE || group.equalsOrInherits(PlayerGroup.MODERATOR)) {
                continue;
            }

            long ownerBuildTime = lot.getLastOwnerEdit();
            long memberBuildTime = lot.getLastMemberEdit();

            if (memberBuildTime > ownerBuildTime) {
                long diff = (memberBuildTime - ownerBuildTime);

                if (diff >= lastBuildTime) {
                    foundLots.add(lot);
                }
            }
        }

        if (foundLots.isEmpty()) {
            sender.printError("No lots found with specified build time.");
            return true;
        }

        Map<String, List<Integer>> ownerLots = new HashMap<String, List<Integer>>();
        int lotCount = foundLots.size();

        for (InnectisLot lot : foundLots) {
            String coloredOwnerName = PlayerUtil.getColoredName(lot.getOwnerCredentials());
            List<Integer> lotIds = null;

            if (!ownerLots.containsKey(coloredOwnerName)) {
                lotIds = new ArrayList<Integer>();
                ownerLots.put(coloredOwnerName, lotIds);
            } else {
                lotIds = ownerLots.get(coloredOwnerName);
            }

            lotIds.add(lot.getId());
        }

        sender.printInfo("Searching for lots where members built last.");
        sender.printInfo(ChatColor.AQUA.toString() + lotCount, " lots found with the formula" + ChatColor.WHITE + ": " + ChatColor.YELLOW + buildFormula);
        sender.printInfo("");

        for (Map.Entry<String, List<Integer>> entry : ownerLots.entrySet()) {
            String coloredOwner = entry.getKey();
            String coloredIdString = "";

            for (int id : entry.getValue()) {
                if (!coloredIdString.isEmpty()) {
                    coloredIdString += ChatColor.WHITE + ", ";
                }

                coloredIdString += ChatColor.AQUA.toString() + id;
            }

            sender.printInfo(coloredOwner + ChatColor.WHITE + ": " + coloredIdString);
        }

        return true;
    }

    @CommandMethod(aliases = {"checklotflag", "clf"},
    description = "Checks all lots for a flag or multiple flags (not inherited).",
    permission = Permission.command_lot_checkflag,
    usage = "/checkflag <flag[,flag2,etc.]>",
    serverCommand = true)
    public static boolean commandCheckFlag(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        List<LotFlagType> checkFlags = new ArrayList<LotFlagType>();
        String flagString = args[0];
        String[] potentialFlags = flagString.split(",");

        for (String potentialFlag : potentialFlags) {
            LotFlagType flag = LotFlagType.fromName(potentialFlag);

            if (flag != null && !checkFlags.contains(flag)) {
                checkFlags.add(flag);
            }
        }

        if (checkFlags.isEmpty()) {
            sender.printError("No valid flags used for searching!");
            return true;
        }

        List<InnectisLot> foundLots = LotHandler.getLots(checkFlags);
        int lotCount = foundLots.size();

        if (!foundLots.isEmpty()) {
            String searchedFlagString = "";

            for (LotFlagType flag : checkFlags) {
                if (!searchedFlagString.isEmpty()) {
                    searchedFlagString += ChatColor.WHITE + ", ";
                }

                searchedFlagString += ChatColor.YELLOW + flag.getFlagName().toLowerCase();
            }

            Map<String, List<Integer>> playerLotIds = new HashMap<String, List<Integer>>();

            for (InnectisLot lot : foundLots) {
                List<Integer> lotIds = null;
                String coloredLotOwner = PlayerUtil.getColoredName(lot.getOwnerCredentials());

                if (!playerLotIds.containsKey(coloredLotOwner)) {
                    lotIds = new ArrayList<Integer>();
                    playerLotIds.put(coloredLotOwner, lotIds);
                } else {
                    lotIds = playerLotIds.get(coloredLotOwner);
                }

                lotIds.add(lot.getId());
            }

            sender.printInfo(ChatColor.AQUA.toString() + lotCount, " lot" + (lotCount > 1 ? "s" : "") + " found with flag" + (checkFlags.size() > 1 ? "s" : "") + ": " + searchedFlagString);
            sender.printInfo("");

            for (Map.Entry<String, List<Integer>> entry : playerLotIds.entrySet()) {
                String owner = entry.getKey();
                String lotIdString = "";

                for (int lotId : entry.getValue()) {
                    if (!lotIdString.isEmpty()) {
                        lotIdString += ChatColor.WHITE + ", ";
                    }

                    lotIdString += ChatColor.AQUA.toString() + lotId;
                }

                sender.printInfo(owner + ChatColor.WHITE + ": " + lotIdString);
            }
        } else {
            sender.printError("No lots found with the specified flag" + (checkFlags.size() > 1 ? "s" : "") + ".");
        }

        return true;
    }

    @CommandMethod(aliases = {"inspectmembers", "inspectlot", "inspect"},
    description = "Checks all target lots for members of a lower user group.",
    permission = Permission.command_lot_inspectmembers,
    usage = "/inspectmembers <username>",
    serverCommand = true)
    public static boolean commandInspectMembers(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        String playerName = args[0];
        IdpPlayer target = parent.getPlayer(playerName);

        if (target != null) {
            playerName = target.getName();
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        if (credentials == null) {
            sender.printError("That player does not exist.");
            return true;
        } else {
            // Get the proper casing of the player
            playerName = credentials.getName();
        }

        PlayerGroup group = PlayerGroup.getGroupOfPlayerById(credentials.getUniqueId());

        List<InnectisLot> lots = LotHandler.getLots(playerName);

        if (lots.isEmpty()) {
            sender.printError("Target does not have any lots: " + playerName);
        } else {
            sender.print(ChatColor.AQUA, "Checking lots for " + playerName + " (" + group.name + ")..");
            List<String> violationsFound = new ArrayList<String>();
            for (InnectisLot lot : lots) {
                if (lot.containsMember("%")) {
                    violationsFound.add(ChatColor.DARK_PURPLE + "[IDP] Lot #" + lot.getId() + ": Wildcard (%) is lot allowed!");
                }

                for (PlayerCredentials pc : lot.getOperators()) {
                    PlayerGroup targetGroup = PlayerGroup.getGroupOfPlayerById(pc.getUniqueId());
                    if (group != PlayerGroup.NONE && group.inherits(targetGroup)) {
                        violationsFound.add(ChatColor.DARK_RED + "[IDP] Lot #" + lot.getId() + ": " + pc.getName() + " is a lot operator!");
                    }
                }

                for (PlayerCredentials pc : lot.getMembers()) {
                    PlayerGroup targetGroup = PlayerGroup.getGroupOfPlayerById(pc.getUniqueId());
                    if (group != PlayerGroup.NONE && group.inherits(targetGroup)) {
                        violationsFound.add(ChatColor.RED + "[IDP] Lot #" + lot.getId() + ": " + pc.getName() + " is a lot member.");
                    }
                }
            }

            if (violationsFound.isEmpty()) {
                sender.printInfo("No violations found on targets lots!");
            } else {
                for (String error : violationsFound) {
                    sender.printRaw(error);
                }
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"allowedlots", "al"},
    description = "Lists all lots that a player is allowed to.",
    permission = Permission.command_lot_allowedlots,
    usage = "/allowedlots",
    usage_Mod = "/allowedlots [username] [-page, -p <page>]",
    serverCommand = false)
    public static void commandAllowedLots(IdpPlayer player, LynxyArguments args) {
        String lookupName = null;

        if (args.getActionSize() > 0) {
            lookupName = args.getString(0);

            if (!lookupName.equalsIgnoreCase(player.getName())
                    && !player.hasPermission(Permission.command_lot_allowedlotsall)) {
                player.printError("You cannot look up allowed lots of other players.");
                return;
            }
        } else {
            lookupName = player.getName();
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(lookupName);

        if (credentials == null) {
            player.printError("That player doesn't exist.");
            return;
        }

        int pageNo = 1;

        if (args.hasArgument("page", "p")) {
            try {
                pageNo = Integer.parseInt(args.getString("page", "p"));

                if (pageNo < 1) {
                    player.printError("Page number cannot be less than 1.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Page is not a number.");
                return;
            }
        }

        List<InnectisLot> lots = new ArrayList<InnectisLot>(LotHandler.getLots().values());
        HashMap<PlayerGroup, List<InnectisLot>> foundLots = new HashMap<PlayerGroup, List<InnectisLot>>();

        for (InnectisLot lot : lots) {
            if (lot.canPlayerAccess(lookupName)) {
                UUID ownerId = lot.getOwnerCredentials().getUniqueId();
                PlayerGroup ownerGroup = PlayerGroup.getGroupOfPlayerById(ownerId);

                List<InnectisLot> tempLots = new ArrayList<InnectisLot>();

                if (!foundLots.containsKey(ownerGroup)) {
                    foundLots.put(ownerGroup, tempLots);
                } else {
                    tempLots = foundLots.get(ownerGroup);
                }

                tempLots.add(lot);
            }
        }

        boolean self = lookupName.equalsIgnoreCase(player.getName());

        if (foundLots.isEmpty()) {
            player.printError((self ? "You do not " : lookupName + " does not ")
                + "have access to any lots.");
            return;
        }

        List<PlayerGroup> groups = new ArrayList<PlayerGroup>(foundLots.keySet());

        Collections.sort(groups, new Comparator<PlayerGroup>() {
            @Override
            public int compare(PlayerGroup pg1, PlayerGroup pg2) {
                return pg1.id - pg2.id;
            }
        });

        player.printInfo((self ? "You have " : lookupName + " has ") + "access to "
        + "the following lots:");

        List<String> lotList = new ArrayList<String>();

        for (PlayerGroup group : groups) {
            List<InnectisLot> tempLots = foundLots.get(group);

            for (InnectisLot lot : tempLots) {
                ChatColor color = group.getPrefix().getTextColor();
                String lotName = (lot.getLotName() != null ? ChatColor.LIGHT_PURPLE + " " + lot.getLotName() : "");

                lotList.add(color.toString() + lot.getId() + lotName);
            }
        }

        PagedCommandHandler ph = new PagedCommandHandler(pageNo, lotList);

        if (ph.isValidPage()) {
            ph.adjustEntriesPerLine(8, ChatColor.YELLOW);

            player.printInfo("Showing page " + pageNo + " of " + ph.getMaxPage());

            for (String str : ph.getParsedInfo()) {
                player.printInfo(str);
            }
        } else {
            player.printError("Page number is too high.");
        }
    }

    @CommandMethod(aliases = {"listentities", "le", "countentities", "ce"},
    description = "Counts the entities in a range, in your TinyWE selection, or on a lot.",
    permission = Permission.command_lot_lotentities,
    usage = "/listentities [-range, -r <amount>] [-selection, -sel] [-lot, -l [ID]]",
    serverCommand = false)
    public static boolean commandListEntities(IdpPlayer player, LynxyArguments args) {
        List<Entity> entities = null;

        if (args.hasArgument("lot", "l") || args.hasOption("lot", "l")
                || args.hasOption("selection", "sel")) {
            IdpWorldRegion region = null;

            if (args.hasArgument("lot", "l") || args.hasOption("lot", "l")) {
                InnectisLot lot = LotHandler.getLot(player.getLocation());

                if (args.hasArgument("lot", "l")) {
                    try {
                        int id = Integer.parseInt(args.getString("lot", "l"));
                        lot = LotHandler.getLot(id);
                    } catch (NumberFormatException nfe) {
                        player.printError("Lot ID is not a number.");
                        return true;
                    }
                }

                if (lot == null) {
                    player.printError("Lot not found! Cannot count entities!");
                    return true;
                }

                region = lot;
                player.printInfo("Scanning for entities on lot #" + lot.getId() + ".");
            } else {
                region = player.getRegion();

                if (region == null) {
                    player.printError("No region found!");
                    return true;
                }

                player.printInfo("Scanning for entities in your selection: " + region.toString());
            }

            entities = region.getEntities(Entity.class, true, 0);
        } else {
            int range = 50;

            if (args.hasArgument("range", "r")) {
                try {
                    range = Integer.parseInt(args.getString("range", "r"));

                    if (range < 1) {
                        player.printError("Range cannot be below 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("Range is not a number.");
                    return true;
                }
            }

            player.printInfo("Scanning for all entities in a " + ChatColor.AQUA + range + ChatColor.DARK_GREEN + " block radius.");
            entities = player.getNearbyEntities(range, Entity.class, false, true, 0);
        }

        if (entities.isEmpty()) {
            player.printError("No entities returned from search!");
            return true;
        }


        player.printInfo("Legend: " + ChatColor.GREEN + "moderate " + ChatColor.YELLOW + "caution "
                + ChatColor.RED + "high " + ChatColor.DARK_RED + "extremely high");

        HashMap<EntityType, Integer> entityCount = new HashMap<EntityType, Integer>();

        int total = 0;
        int totalLiving = 0;
        int totalNonLiving = 0;
        int totalTamed = 0;

        for (Entity entity : entities) {
            if (!entityCount.containsKey(entity.getType())) {
                entityCount.put(entity.getType(), 1);
            } else {
                int previousCount = entityCount.get(entity.getType());
                entityCount.put(entity.getType(), previousCount + 1);
            }

            // TODO: Remove armor stand exception when they are no longer living
            if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
                totalLiving++;

                if (entity instanceof Tameable) {
                    Tameable tameable = (Tameable) entity;

                    if (tameable.isTamed()) {
                        totalTamed++;
                    }
                }
            } else {
                totalNonLiving++;
            }

            total++;
        }

        String entityString = "";

        for (EntityType type : entityCount.keySet()) {
            int count = entityCount.get(type);
            String name = type.name().toLowerCase();
            ChatColor resultColor = ChatColor.GREEN;

            if (count >= 250) {
                resultColor = ChatColor.DARK_RED;
            } else if (count >= 150) {
                resultColor = ChatColor.RED;
            } else if (count >= 100) {
                resultColor = ChatColor.YELLOW;
            }

            if (!entityString.isEmpty()) {
                entityString += ChatColor.YELLOW + ", ";
            }

            entityString += ChatColor.AQUA + name + ChatColor.DARK_GREEN + ": " + resultColor + count;
        }

        player.printInfo(entityString);
        player.printInfo(ChatColor.AQUA.toString() + total + ChatColor.DARK_GREEN + " total entities (" +
                ChatColor.AQUA + totalLiving + ChatColor.DARK_GREEN + " living" + ChatColor.YELLOW + ", " + ChatColor.AQUA + totalTamed
                + ChatColor.DARK_GREEN + " tamed" + ChatColor.YELLOW + ", " + ChatColor.AQUA + totalNonLiving + ChatColor.DARK_GREEN + " non-living)");

        return true;
    }

    @CommandMethod(aliases = {"createlotarea"},
    description = "Creates a lot area.",
    permission = Permission.command_lot_addlotarea,
    usage = "/createlotarea <lotsWide> <lotsDeep> <mainMaterial> <borderMaterial> <pathMaterial>",
    serverCommand = false)
    @SuppressWarnings("deprecation")
    public static boolean commandCreateLotArea(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        if (args.length != 5) {
            return false;
        }

        final int minLotareaSize = 1;
        final int maxLotareaSize = 20;
        final int lotWidth = 50;
        final int lotDepth = 50;
        final int lotMargin = 3; //includes the "border"
        final int pathWidth = 3;

        // assign arguments
        int lotsWide, lotsDeep, material, border, path;
        try {
            lotsWide = Integer.parseInt(args[0]);
            lotsDeep = Integer.parseInt(args[1]);
            material = Integer.parseInt(args[2]);
            border = Integer.parseInt(args[3]);
            path = Integer.parseInt(args[4]);
        } catch (NumberFormatException ex) {
            player.printError("There was an error while creating the lot area!");
            return true;
        }

        // Check sizes
        if (lotsWide < minLotareaSize || lotsDeep < minLotareaSize) {
            player.printError("Minimum lotarea size is " + minLotareaSize + "x" + minLotareaSize);
            return true;
        }
        if (lotsWide > maxLotareaSize || lotsDeep > maxLotareaSize) {
            player.printError("Maximum lotarea size is " + maxLotareaSize + "x" + maxLotareaSize);
            return true;
        }

        int blocksWide = (lotsWide * (lotWidth + lotMargin * 2 + pathWidth)) + pathWidth - 1;
        int blocksDeep = (lotsDeep * (lotDepth + lotMargin * 2 + pathWidth)) + pathWidth - 1;

        int startX = player.getLocation().getBlockX(),
                startY = player.getLocation().getBlockY() - 1,
                startZ = player.getLocation().getBlockZ(),
                endX, endZ;

        // Get direction
        float yaw = player.getLocation().getYaw() % 360; //0 = west
        if (yaw < 0) {
            yaw = 360 + yaw;
        }

        if (yaw >= 22.5 && yaw <= 67.5) { //NW
            endX = startX + blocksWide;
            endZ = startZ - blocksDeep;
        } else if (yaw >= 112.5 && yaw <= 157.5) { //NE
            endX = startX - blocksWide;
            endZ = startZ - blocksDeep;
        } else if (yaw >= 202.5 && yaw <= 247.5) { //SE
            endX = startX - blocksWide;
            endZ = startZ + blocksDeep;
        } else if (yaw >= 292.5 && yaw <= 337.5) { //SW
            endX = startX + blocksWide;
            endZ = startZ + blocksDeep;
        } else {
            player.printError("Invalid direction specified!");
            return true;
        }

        // Set starting points
        Vector start = new Vector(startX, startY, startZ);
        Vector end = new Vector(endX, startY, endZ);
        IdpWorldRegion idpRegion = new IdpWorldRegion(player.getLocation().getWorld(), start, end);


        // Check for lots inside the region
        List<InnectisLot> lots = LotHandler.getLotsOverlapping(idpRegion, false);
        if (lots != null && !lots.isEmpty()) {
            player.printError("Found " + lots.size() + " lot(s) in way of lot area. Aborting!");
            return true;
        }

        // Reserve/init variables
        int blocksPerLayer, layersPerOperation, operations;
        IdpRegion region;

        World world = player.getLocation().getWorld();

        BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);
        region = new IdpRegion(start, end);

        blocksPerLayer = region.getArea();
        layersPerOperation = (int) Math.floor(900000 / blocksPerLayer);
        if (layersPerOperation < 1) {
            player.printError("Area too large!");
            return true;
        }

        region.expand(new Vector(0, 200, 0));
        region.contract(new Vector(0, -1, 0));
        operations = ((region.getArea() / blocksPerLayer) / layersPerOperation) + 1;

        player.printRaw("blocksPerLayer=" + blocksPerLayer + ", layersPerOperation=" + layersPerOperation + ", operations=" + operations);

        MaterialSelector nonairselector = new MaterialSelector();
        nonairselector.setMode(true);
        nonairselector.addMaterials(IdpMaterial.AIR);

        // clear all blocks above lotarea:
        int curY = startY + 1;
        for (int i = 1; i <= operations; i++) {
            region = new IdpRegion(new Vector(startX, curY, startZ), new Vector(endX, curY + layersPerOperation, endZ));
            curY += layersPerOperation;

            player.printRaw(region.getMinimumPoint() + " - " + region.getMaximumPoint());
            for (Block blk : counter.getBlockList(region, world, nonairselector)) {
                BlockHandler.setBlock(blk, IdpMaterial.AIR);
            }

        }

        player.printInfo("Overhead cleared");

        // set path material (overwritten later)
        region = new IdpRegion(start, end);

        IdpMaterial pathmaterial = IdpMaterial.fromID(path);
        for (Block blk : counter.getBlockList(region, world, null)) {
            BlockHandler.setBlock(blk, pathmaterial);
        }

        player.printInfo("Paths set");

        //fill & border lots
        IdpMaterial groundmat = IdpMaterial.fromID(material);
        IdpMaterial bordermat = IdpMaterial.fromID(border);

        int x = region.getMinimumPoint().getBlockX() + (pathWidth - 1) + lotMargin;
        int start_z = region.getMinimumPoint().getBlockZ() + (pathWidth - 1) + lotMargin;

        int max_x = region.getMaximumPoint().getBlockX();
        int max_z = region.getMaximumPoint().getBlockZ();

        int increment = lotWidth + lotMargin * 2 + pathWidth;

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);

        Vector loc1, loc2;
        for (; x < max_x; x += increment) {
            for (int z = start_z; z < max_z; z += increment) {
                loc1 = new Vector(x, startY, z);
                loc2 = new Vector(x + lotWidth + 1, startY, z + lotDepth + 1);

                loc1 = loc1.subtract(new Vector(lotMargin - 1, 0, lotMargin - 1));
                loc2 = loc2.add(new Vector(lotMargin - 1, 0, lotMargin - 1));

                // Set ground
                region = new IdpRegion(loc1, loc2);
                for (Block blk : counter.getBlockList(region, world, null)) {
                    BlockHandler.setBlock(blk, groundmat);
                }

                loc1 = loc1.add(new Vector(lotMargin - 1, 0, lotMargin - 1));
                loc2 = loc2.subtract(new Vector(lotMargin - 1, 0, lotMargin - 1));

                loc1 = loc1.add(new Vector(1, 0, 1));
                loc2 = loc2.subtract(new Vector(1, 0, 1));

                // Set border
                for (Block blk : counter.getWallBlockList(region, world, null)) {
                    BlockHandler.setBlock(blk, bordermat);
                }

                int x1 = loc1.getBlockX();
                int y1 = 0;
                int z1 = loc1.getBlockZ();
                Vector vec1 = new Vector(x1, y1, z1);

                int x2 = loc2.getBlockX();
                int y2 = 255;
                int z2 = loc2.getBlockZ();
                Vector vec2 = new Vector(x2, y2, z2);

                // Create the lot
                try {
                    InnectisLot lot = LotHandler.addLot(world, vec1, vec2, Configuration.LOT_ASSIGNABLE_CREDENTIALS, credentials);
                    lot.setSpawn(new Location(world, loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()));
                    lot.save();
                    player.printInfo("Lot: " + ChatColor.AQUA + lot.getId() + ChatColor.DARK_PURPLE, " " + lot.getMinimumPoint() + " " + lot.getMaximumPoint());
                } catch (SQLException ex) {
                    player.printError("SQL Error adding lot!");
                    InnPlugin.logError("SQL exception adding lot!", ex);
                }
            }
        }

        try {
            InnectisLot lot = LotHandler.addLot(world, new Vector(start.getBlockX(), 0, start.getBlockZ()), new Vector(end.getBlockX(), 255, end.getBlockZ()), Configuration.OTHER_CREDENTIALS, credentials);
            lot.setSpawn(new Location(player.getLocation().getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()));
            lot.setHidden(true);
            lot.save();
            player.printInfo("Area lot: " + ChatColor.AQUA + lot.getId() + ChatColor.DARK_PURPLE, " " + lot.getMinimumPoint() + " " + lot.getMaximumPoint());
        } catch (SQLException ex) {
            player.printError("SQL Error adding lot!");
            InnPlugin.logError("SQL exception adding lot!", ex);
        }

        player.printError("done. please RELOG!");

        return true;
    }

    @CommandMethod(aliases = {"lotlastedit", "lle"},
    description = "Finds all lots not built before a certain time.",
    permission = Permission.command_moderation_lotlastedit,
    usage = "/lotlastedit <time> [-p <page>] [-size, -s <size (Ex. 50x50, 50-100x50-100, or any)>]",
    serverCommand = true)
    public static boolean commandLotLastEdit(IdpCommandSender sender, LynxyArguments args) {
        if (args.getActionSize() > 0) {
            long timeLastEdited = DateUtil.getTimeFormula(args.getString(0));

            if (timeLastEdited == -1) {
                sender.printError("Invalid time formula. (Ex. 5m2s");
                return true;
            }

            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        sender.printError("Page must be greater than 0.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    sender.printError("Page is not a number.");
                    return true;
                }
            }

            int minLength = 50;
            int maxLength = 50;

            int minWidth = 50;
            int maxWidth = 50;

            boolean anySize = false;

            if (args.hasArgument("size", "s")) {
                try {
                    String sizeArg = args.getString("size", "s");

                    if (!sizeArg.equalsIgnoreCase("any")) {
                        if (sizeArg.contains("x")) {
                            String[] lw = sizeArg.split("x");

                            if (lw[0].contains("-")) {
                                String[] minMax = lw[0].split("-");

                                minLength = Integer.parseInt(minMax[0]);
                                maxLength = Integer.parseInt(minMax[1]);
                            } else {
                                minLength = Integer.parseInt(lw[0]);
                                maxLength = Integer.parseInt(lw[0]);
                            }

                            if (lw[1].contains("-")) {
                                String[] minMax = lw[1].split("-");

                                minWidth = Integer.parseInt(minMax[0]);
                                maxWidth = Integer.parseInt(minMax[1]);
                            } else {
                                minWidth = Integer.parseInt(lw[1]);
                                maxWidth = Integer.parseInt(lw[1]);
                            }
                        } else {
                            sender.printError("Size must be length x width (I.E. 50x50)");
                            return true;
                        }
                    } else {
                        anySize = true;
                    }
                } catch (NumberFormatException nfe) {
                    sender.printError("Size must be length x width (I.E. 50x50, or 50-100x50-100)");
                    return true;
                }
            }

            HashMap<PlayerGroup, List<String>> foundLots = new HashMap<PlayerGroup, List<String>>();

            for (InnectisLot lot : LotHandler.getLots().values()) {
                boolean canTrack = !(lot.isAssignable() || lot.getOwner().equals("~"));

                if (canTrack) {
                    int lotLength = lot.getLength();
                    int lotWidth = lot.getWidth();

                    if (anySize || (lotLength >= minLength && lotLength <= maxLength
                            && lotWidth >= minWidth && lotWidth <= maxWidth)) {
                        long lastEdit = lot.getLastOwnerEdit();

                        if (lastEdit > 0 && (System.currentTimeMillis() - lastEdit) >= timeLastEdited) {
                            int id = lot.getId();
                            PlayerGroup group = PlayerGroup.getGroupOfPlayerById(lot.getOwnerCredentials().getUniqueId());

                            if (foundLots.containsKey(group)) {
                                foundLots.get(group).add(group.getPrefix().getTextColor().toString() + id);
                            } else {
                                List<String> IDs = new ArrayList<String>();
                                IDs.add(group.getPrefix().getTextColor().toString() + id);
                                foundLots.put(group, IDs);
                            }
                        }
                    }
                }
            }

            String lenString = (minLength == maxLength ? minLength + "": "(" + minLength + " to " + maxLength + ")");
            String widthString = (minWidth == maxWidth ? minWidth + "": "(" + minWidth + " to " + maxWidth + ")");

            if (foundLots.isEmpty()) {
                sender.printError("There are no lots last edited on or after this time for lot size: " + ChatColor.YELLOW + lenString + "x" + widthString);
                return true;
            }

            List<PlayerGroup> groupList = new ArrayList<PlayerGroup>(foundLots.keySet());

            // The output requires that we sort by group
            Collections.sort(groupList, new Comparator<PlayerGroup>() {
                @Override
                public int compare(PlayerGroup g1, PlayerGroup g2) {
                    return g1.id - g2.id;
                }
            });

            List<String> finalIDs = new ArrayList<String>();
            for (PlayerGroup group : groupList) {
                List<String> idList = foundLots.get(group);
                finalIDs.addAll(idList);
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, finalIDs);
            ph.adjustEntriesPerLine(8, ChatColor.YELLOW);

            String showSizeString = "Showing lots last edited of size: " + ChatColor.YELLOW;

            if (anySize) {
                showSizeString += "any";
            } else {
                showSizeString += lenString + "x" + widthString;
            }

            sender.printInfo(ChatColor.YELLOW + "Showing " + finalIDs.size() + " lots last edited by owner " + DateUtil.getTimeString(timeLastEdited, true) + " or more ago");
            sender.printInfo("Page " + pageNo + " of " + ph.getMaxPage());
            sender.printInfo(showSizeString);
            sender.printInfo("");

            for (String st : ph.getParsedInfo()) {
                sender.printInfo(st);
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"putlothere", "lothere", "plh", "lh"},
    description = "Moves a lot in your selection, or given lot ID, to your region.",
    permission = Permission.command_lot_putlothere,
    usage = "/putlothere [lot ID] [-ignorelots, -il] [-spawnhere, -sh] [-yaxis, -y]",
    serverCommand = false)
    public static boolean commandPutLotHere(IdpPlayer player, LynxyArguments args) {
        IdpWorldRegion region = player.getRegion();

        if (region == null) {
            player.printError("No region found.");
            return true;
        }

        InnectisLot lot = null;
        boolean ignoreLots = args.hasOption("ignorelots", "il");

        if (args.getActionSize() > 0) {
            int id = 0;
            try {
                id = Integer.parseInt(args.getString(0));
                lot = LotHandler.getLot(id);
            } catch (NumberFormatException nfe) {
                player.printError("Lot ID not formatted properly.");
                return true;
            }

            if (lot == null) {
                player.printError("No lot found with ID " + id + "!");
                return true;
            }
        }

        List<InnectisLot> lots = LotHandler.getLotsOverlapping(region, false);

        if (lots != null && !ignoreLots) {
            if (lots.size() > 1) {
                String lotstring = "";

                for (InnectisLot l : lots) {
                    lotstring += "#" + l.getId() + " ";
                }

                player.printError("There were multiple lots in your selection: " + lotstring);
                return true;
            } else {
                if (lot != null) {
                    if (lots.get(0).getId() != lot.getId()) {
                        player.printError("A conflicting lot was found in your selection: " + lots.get(0).getId());
                        return true;
                    }
                } else {
                    lot = lots.get(0);
                }
            }
        } else {
            if (lot == null) {
                player.printError("No lots found in selection!");
                return true;
            }
        }

        String lotSizeString = lot.getWidth() + "x" + lot.getLength();
        String regionSizeString = region.getWidth() + "x" + region.getLength();
        String playerWorld = player.getWorld().getName();
        String lotWorld = lot.getWorld().getName();

        // Don't set lowest/highest Y if using y-axis placement
        if (!args.hasOption("yaxis", "y")) {
            // Make sure to preserve both Y coordinates
            int lotLowY = lot.getLowestY();
            int lotHighY = lot.getHighestY();

            region.getPos1().setY(lotLowY);
            region.getPos2().setY(lotHighY);
        }

        // lot.getLowestY() is in here to preserve Y, since it is not modified
        String regionCoordStringOne = "[" + region.getLowestX() + ", " + region.getLowestY() + ", " + region.getLowestZ() + "]";
        String regionCoordStringTwo = "[" + region.getHighestX() + ", " + region.getHighestY() + ", " + region.getHighestZ() + "]";

        String lotCoordStringOne = "[" + lot.getLowestX() + ", " + lot.getLowestY() + ", " + lot.getLowestZ() + "]";
        String lotCoordStringTwo = "[" + lot.getHighestX() + ", " + lot.getHighestY() + ", " + lot.getHighestZ() + "]";

        lot.setPos1(region.getPos1());
        lot.setPos2(region.getPos2());
        lot.setWorld(player.getLocation().getWorld());

        if (args.hasOption("spawnhere", "sh")) {
            lot.setSpawn(player.getLocation());
            player.printInfo("Spawn location set to your location.");
        }

        if (lot.save()) {
            player.printInfo("Lot #" + lot.getId() + "'s location has been moved!");
            player.printInfo("Old location: " + lotWorld + " " + lotCoordStringOne + " " + lotCoordStringTwo);
            player.printInfo("New location: " + playerWorld + " " + regionCoordStringOne + " " + regionCoordStringTwo);
            player.printInfo("Old size: " + lotSizeString + " New size: " + regionSizeString);
        } else {
            player.printError("Unable to save lot!");
        }

        return true;
    }

    @CommandMethod(aliases = {"changelotheight", "clh"},
    description = "Changes the height of the lot, full or not.",
    permission = Permission.command_lot_chagelotheight,
    usage = "/changelotheight <ID> [<low-Y> <high-Y>] OR [-selection, -sel] OR [-full]",
    serverCommand = false)
    public static boolean commandChangeLotHeight(IdpPlayer player, ParameterArguments args) {
        if (args.size() == 0) {
            return false;
        }

        InnectisLot lot = null;

        try {
            int id = args.getInt(0);
            lot = LotHandler.getLot(id);
        } catch (NotANumberException NaN) {
            player.printError("ID is not a number.");
            return true;
        }

        if (lot == null) {
            player.printError("Lot not found!");
            return true;
        }

        Vector pos1 = lot.getPos1();
        Vector pos2 = lot.getPos2();

        int minY = 0;
        int maxY = 0;

        if (args.hasOption("full")) {
            minY = 0;
            maxY = 255;
        } else if (args.hasOption("selection", "sel")) {
            IdpRegion region = player.getRegion();

            if (region == null) {
                player.printError("You do not have a region set!");
                return true;
            }

            minY = region.getLowestY();
            maxY = region.getHighestY();
        } else {
            if (args.size() < 3) {
                player.printError("You need to provide a minimum Y and maximum Y.");
                return true;
            }

            try {
                minY = args.getInt(1);
                maxY = args.getInt(2);
            } catch (NotANumberException NaN) {
                player.printError("Mininum Y or Maximum Y is not a number.");
                return true;
            }

            if (minY < 0 || maxY < 0 || minY > 255 || maxY > 255 || maxY < minY) {
                player.printError("Either point cannot be less than 1 or greater than 255, and max must be greater or equal to min.");
                return true;
            }
        }

        lot.setPos1(pos1.setY(minY));
        lot.setPos2(pos2.setY(maxY));

        if (lot.save()) {
            player.printInfo("Set minimum Y to " + minY + " and maximum Y to " + maxY + " for lot #" + lot.getId() + ".");
        } else {
            InnPlugin.logError("Unable to save lot!");
        }

        return true;
    }

    @CommandMethod(aliases = {"addlot"},
    description = "Creates a new lot for a player.",
    permission = Permission.command_lot_addlot,
    usage = "/addlot <username> [-p password] [-yaxis]",
    serverCommand = false)
    public static boolean commandAddLot(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }

        IdpWorldRegion region = player.getRegion();
        if (region == null) {
            player.printError("No region found!");
            return true;
        }

        String playerName = args.getString(0);
        IdpPlayer target = parent.getPlayer(playerName);
        PlayerCredentials ownerCredentials = null;

        if (target != null) {
            playerName = target.getName();
            ownerCredentials = PlayerCredentialsManager.getByName(playerName);
        } else {
            if (playerName.equals("#")) {
                ownerCredentials = Configuration.LOT_ASSIGNABLE_CREDENTIALS;
            } else if (playerName.equals("~")) {
                ownerCredentials = Configuration.OTHER_CREDENTIALS;
            } else if (playerName.equalsIgnoreCase("[SYSTEM]")) {
                ownerCredentials = Configuration.SYSTEM_CREDENTIALS;
            } else {
                ownerCredentials = PlayerCredentialsManager.getByName(playerName);

                if (ownerCredentials == null) {
                    player.printError("That player doesn't exist.");
                    return true;
                }
            }
        }

        boolean useYAxis = args.hasOption("yaxis", "y");
        String password = args.getString("password", "pass", "p");

        InnectisLot checkLot = LotHandler.getLot(region, useYAxis);

        if (checkLot != null) {
            player.printError("A lot already exists at this X/Z coordinate pair!");
            return true;
        }

        List<InnectisLot> lots = LotHandler.getLotsOverlapping(region, false);
        Location spawn = player.getLocation();

        try {
            if (lots != null) {
                if (password == null || password.isEmpty()) {
                    StringBuilder sb = new StringBuilder(1024);
                    for (InnectisLot lot : lots) {
                        sb.append(lot.getId()).append(", ");
                    }
                    player.printError("Uh oh! " + lots.size() + " lot(s) were found in your selection: " + sb.substring(0, sb.length() - 2));
                    player.printError("To add this lot anyway use password " + ChatColor.LIGHT_PURPLE + region.getPassword());
                    return true;
                } else {
                    if (!password.equalsIgnoreCase(region.getPassword())) {
                        player.printError("Invalid password supplied to override lot add!");
                        return true;
                    }
                }
            }

            Vector pos1 = region.getMinimumPoint();
            Vector pos2 = region.getMaximumPoint();
            if (!useYAxis) { //not using Y-axis, so use entire Y range
                pos1 = pos1.setY(0);
                pos2 = pos2.setY(player.getWorld().getMaxHeight() - 1);
            }

            PlayerCredentialsManager.addCredentialsToCache(ownerCredentials);
            PlayerCredentials creatorCredentials = PlayerCredentialsManager.getByName(player.getName(), true);
            InnectisLot lot = LotHandler.addLot(player.getLocation().getWorld(), pos1, pos2, ownerCredentials, creatorCredentials);
            lot.setSpawn(spawn);
            lot.save();
            LotHandler.getLots().put(lot.getId(), lot);

            player.printInfo((useYAxis ? "Y-" : "") + "Lot #" + lot.getId() + (lot.getParentNotHidden() != null ? " (sublot of " + lot.getParentNotHidden().getId() + ")" : "") + " created for " + playerName + "!");
            InnPlugin.logInfo(ChatColor.GREEN + (useYAxis ? "Y-" : "") + "Lot #" + lot.getId() + (lot.getParentNotHidden() != null ? " (sublot of " + lot.getParentNotHidden().getId() + ")" : "") + " created for " + playerName + "!");
        } catch (SQLException ex) {
            player.printError("Failed to save lot, notify an Admin!");
            InnPlugin.logError("SQLException addlot " + player.getColoredName(), ex);
        }
        return true;
    }

    @CommandMethod(aliases = {"lot"},
    description = "Command to manage a lot's traits.",
    permission = Permission.command_lot_lot,
    usage = "/lot [lotID] [-clearall, -ca] [-clearlotmembers, -clm] [-clearbanned, -cb] "
    + "[-clearflags, -cf] [-clearsafelist, -cs] [-clearmessages, -cm) [-makeassignable, -ma] "
    + "[-makehidden, -mh] [-makeunhidden, -mu] [-enable] [-disable]",
    serverCommand = false)
    public static boolean commandLot(IdpPlayer player, LynxyArguments args) {
        InnectisLot lot = null;
        boolean isValidCommand = false;

        if (args.getActionSize() > 0) {
            try {
                int id = Integer.parseInt(args.getString(0));
                lot = LotHandler.getLot(id);
            } catch (NumberFormatException nfe) {
                player.printError("Lot ID is not an integer!");
                return true;
            }
        } else {
            lot = LotHandler.getLot(player.getLocation(), true);
        }

        if (lot == null) {
            player.printError("This command must be used on a lot.");
            return true;
        }

        if (args.hasOption("clearall", "ca", "makeassignable", "ma")) {
            lot.clearTraits();
            boolean makeAssignable = args.hasOption("makeassignable", "ma");

            if (makeAssignable) {
                lot.setOwner(Configuration.LOT_ASSIGNABLE_CREDENTIALS);
                lot.setLastOwnerEdit(0);
                lot.setLastMemberEdit(0);
            }

            if (lot.save()) {
                player.printInfo(makeAssignable ? "Lot #" + lot.getId() + " has been made assignable!" : "All lot traits cleared!");
            } else {
                player.printError("Error saving lot!");
            }

            return true;
        }

        if (args.hasOption("enable")) {
            isValidCommand = true;
            lot.setDisabled(false);
        }

        if (args.hasOption("disable")) {
            isValidCommand = true;
            lot.setDisabled(true);
        }

        if (args.hasOption("clearlotmembers", "clm")) {
            isValidCommand = true;
            lot.clearMembersAndOperators();
        }

        if (args.hasOption("clearbanned", "cb")) {
            isValidCommand = true;
            lot.clearBanned();
        }

        if (args.hasOption("clearsafelist", "cs")) {
            isValidCommand = true;
            lot.clearSafelist();
        }

        if (args.hasOption("makehidden", "mh")) {
            isValidCommand = true;
            lot.setHidden(true);
        }

        if (args.hasOption("makeunhidden", "mu")) {
            isValidCommand = true;
            lot.setHidden(false);
        }

        if (args.hasOption("clearmessages", "cm")) {
            isValidCommand = true;
            lot.setEnterMsg("");
            lot.setExitMsg("");
        }

        if (args.hasOption("clearflags", "cf")) {
            isValidCommand = true;
            lot.simulateLeave();
            lot.clearFlags();
            lot.simulateJoin();
        }

        if (isValidCommand) {
            if (lot.save()) {
                player.printInfo("Lot traits modified for lot #" + lot.getId() + ".");
            } else {
                player.printError("Unable to save lot!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"wholot", "wl"},
    description = "Displays all users on the lot you're standing in.",
    permission = Permission.command_lot_wholot,
    usage = "/wholot",
    serverCommand = false)
    public static boolean commandWhoLot(InnPlugin parent, IdpPlayer player) {
        InnectisLot lot = LotHandler.getLot(player.getLocation());

        if (lot == null) {
            player.printError("You must use this command on a lot!");
            return true;
        }

        String lotUsers = "";
        int numUsers = 0;
        for (IdpPlayer p : lot.getPlayersInsideRegion(0)) {
            if (p.getSession().isVisible()) {
                numUsers++;
                if (lotUsers.isEmpty()) {
                    lotUsers = p.getColoredName();
                } else {
                    lotUsers += ChatColor.YELLOW + ", " + p.getColoredName();
                }
            } else {
                if (player.hasPermission(Permission.command_admin_vanish)) {
                    numUsers++;
                    if (lotUsers.isEmpty()) {
                        lotUsers = ChatColor.GRAY + p.getName();
                    } else {
                        lotUsers += ChatColor.YELLOW + ", " + ChatColor.GRAY + p.getName();
                    }
                }
            }
        }
        player.printInfo("There are " + numUsers + " players on this lot (lot #" + lot.getId() + "):");
        player.printInfo(lotUsers);

        return true;
    }

    @CommandMethod(aliases = {"getlot"},
    description = "Gives a pre-made lot to the player.",
    permission = Permission.command_lot_getlot,
    usage = "/getlot",
    serverCommand = false)
    public static boolean commandGetLot(IdpPlayer player, String[] args) {
        if (LotHandler.getLotCount(player.getName()) > 0) {
            player.printError("You already have a lot!");
            player.printError("To get another, please talk to a Moderator.");
        } else {
            try {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());

                if (LotHandler.assignLot(credentials) == null) {
                    player.printError("There are no lots available.");
                    player.printError("Please notify an Admin so they can get you one.");
                } else {
                    PlayerCredentialsManager.addCredentialsToCache(credentials);
                }
            } catch (SQLException ex) {
                InnPlugin.logError("Error when assigning a lot! ", ex);
                player.printError("An error occured, notify an Admin!");
            }
        }
        return true;
    }

    @CommandMethod(aliases = {"assignlot"},
    description = "Assigns a pre-made lot to a player.",
    permission = Permission.command_lot_assignlot,
    usage = "/assignlot <username> [lotid]",
    serverCommand = true)
    public static boolean commandAssignlot(InnPlugin parent, IdpCommandSender sender, String[] args) {
        try {
            InnectisLot lot = null;
            String targetName = "";
            if (args.length >= 1) {
                targetName = args[0];

                if (args.length == 2) {
                    try {
                        int lotId = Integer.parseInt(args[1]);
                        InnectisLot targetLot = LotHandler.getLot(lotId);

                        if (targetLot == null) {
                            sender.printError("Lot #" + lotId + " not found!");
                        } else if (targetLot.isAssignable()) {
                            PlayerCredentials credentials = PlayerCredentialsManager.getByName(targetName, true);
                            lot = LotHandler.assignLot(credentials, targetLot);
                        } else {
                            sender.printError("Lot #" + lotId + " is not an assignable lot!");
                        }
                    } catch (NumberFormatException ex) {
                        sender.printError("Invalid lot ID!");
                        return true;
                    }
                } else {
                    PlayerCredentials credentials = PlayerCredentialsManager.getByName(targetName);
                    lot = LotHandler.assignLot(credentials);
                }

                if (lot == null) {
                    sender.printError("No lots in stock!");
                }

                return true;
            }
        } catch (SQLException ex) {
            sender.printError("Failed to assign lot, notify an Admin!");
            InnPlugin.logError("SQLException assignlot ", ex);
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"changespawn", "changelotspawn", "setlotspawn"},
    description = "Sets the spawn of a lot.",
    permission = Permission.command_lot_changelotspawn,
    usage = "/changespawn [lotid]",
    serverCommand = false)
    public static boolean commandChangeLotSpawn(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        InnectisLot lot = null;

        if (args.length > 0) {
            try {
                int lotid = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(lotid);
            } catch (NumberFormatException nfe) {
                player.printError("Invalid lot id!");
                return true;
            }
        } else {
            lot = LotHandler.getLot(player.getLocation());
        }

        if (lot == null) {
            player.printError("No lot found!");
            return true;
        }

        if (player.hasPermission(Permission.lot_changeanyspawn)) {
            lot.setSpawn(player.getLocation());

            if (lot.save()) {
                player.printInfo("Spawn changed for lot " + lot.getId() + "!");
            } else {
                player.printError("An internal server occured saving lot #" + lot.getId() + ". Contact an admin!");
            }
        } else {
            if (!lot.canPlayerManage(player.getName())) {
                player.printError("Cannot change spawn on this lot!");
                return true;
            }

            InnectisLot playerLot = LotHandler.getLot(player.getLocation());

            if (lot != playerLot) {
                player.printError("You cannot set lot spawn outside of this lot!");
                return true;
            }

            lot.setSpawn(player.getLocation());

            if (lot.save()) {
                player.printInfo("Spawn changed for lot #" + lot.getId() + "!");
            } else {
                player.printError("An internal server occured saving lot #" + lot.getId() + ". Contact an admin!");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"gotolot", "gtl"},
    description = "Teleports to the spawn of the given lot.",
    permission = Permission.command_lot_gotolot,
    usage = "/gotolot [lot id] [<username> [lotnumber]]",
    serverCommand = false)
    public static boolean commandGoToLot(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        if (args.length > 0) {
            IdpPlayer lotPlayer = parent.getPlayer(args[0]);
            String playerName = (lotPlayer != null ? lotPlayer.getName() : args[0]);
            InnectisLot lot = null;

            if (args.length > 1) {
                try {
                    int lotNr = Integer.parseInt(args[1]);
                    lot = LotHandler.getLot(playerName, lotNr); // by player name and lot number
                } catch (NumberFormatException nfe) {
                    lot = LotHandler.getLot(playerName, args[1]); // by player name and lot name
                }
            } else {
                try {
                    int id = Integer.parseInt(args[0]);
                    lot = LotHandler.getLot(id); // by lot ID
                } catch (NumberFormatException nfe) {
                    lot = LotHandler.getLot(playerName, 1); // by player's first lot
                }
            }

            if (lot == null) {
                player.printError("That lot doesn't exist!");
                return true;
            }

            if (player.teleport(lot.getSpawn(), TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE)) {
                player.printInfo("You have warped to lot #" + lot.getId() + ".");
            }

            return true;
        }

        return false;
    }

    public static void printLotList(IdpCommandSender sender, String owner, int pageNo, World world) {
        List<InnectisLot> lots = LotHandler.getLots(owner);
        if (lots.isEmpty()) {
            sender.printError(owner + " does not have any lots!");
        } else {
            List<String> strLots = new ArrayList<String>(), strSublots = new ArrayList<String>();
            StringBuilder sublotBuilder = new StringBuilder(1024);
            int lotCt = 0, sublotCt = 0;

            for (InnectisLot lot : lots) {
                if (world != null && !lot.getWorld().equals(world)) {
                    continue;
                }

                LotInfo info = getLotInfo(lot);

                if (info.isNormalLot()) {
                    strLots.add("  " + info.getLotString());
                    lotCt++;
                }

                String sublotString = info.getSublotString();

                if (!sublotString.isEmpty()) {
                    sublotBuilder.append(sublotString).append(", ");
                    sublotCt++;

                    if (sublotCt % 3 == 0) {
                        strSublots.add("  " + sublotBuilder.substring(0, sublotBuilder.length() - 2));
                        sublotBuilder = new StringBuilder(1024);
                    }
                }
            }

            if (lotCt == 0 && sublotCt == 0) {
                sender.printError(owner + " does not have any lots in the specified world!");
                return;
            }

            if (sublotBuilder.length() > 2) {
                strSublots.add("  " + sublotBuilder.substring(0, sublotBuilder.length() - 2));
            }

            List<String> content = new ArrayList<String>();
            content.add(ChatColor.AQUA + "Lot information:");

            for (String s : strLots) {
                content.add(ChatColor.AQUA + s);
            }

            if (!strSublots.isEmpty()) {
                content.add(" ");
                content.add(ChatColor.AQUA + "Sublot information:");

                for (String s : strSublots) {
                    content.add(ChatColor.AQUA + s);
                }
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, content);

            if (ph.isValidPage()) {
                sender.print(ChatColor.AQUA, owner + " has " + lotCt + " lots" + " and " + sublotCt + " sublots" + (world != null ? " in world " + world.getName() : ""));
                sender.print(ChatColor.AQUA, "Viewing page " + pageNo + " of " + ph.getMaxPage());
                sender.print(ChatColor.AQUA, "");

                for (String s : ph.getParsedInfo()) {
                    sender.print(ChatColor.AQUA, s);
                }
            } else {
                sender.printError(ph.getInvalidPageNumberString());
            }
        }
    }

    private static LotInfo getLotInfo(InnectisLot lot) {
        StringBuilder lotBuilder = new StringBuilder(1024);
        StringBuilder sublotBuilder = new StringBuilder(1024);

        ChatColor lotNumberColor = (lot.isDynamicLot() ? ChatColor.GREEN : ChatColor.AQUA);
        ChatColor sublotNumberColor = (lot.isDynamicLot() ? ChatColor.WHITE : ChatColor.GRAY);
        int sizeX = lot.getWidth();
        int sizeY = lot.getHeight();
        int sizeZ = lot.getLength();

        lotBuilder.append(ChatColor.DARK_RED).append(lot.getLotNumber()).append(lotNumberColor).append(" #").append(lot.getId()).append(ChatColor.AQUA).append(" ").append(lot.getLotName().equalsIgnoreCase("") ? "" : ChatColor.LIGHT_PURPLE + lot.getLotName()).append(": ").append(ChatColor.DARK_AQUA).append(lot.getWorld().getName()).append(" ").append(sizeX).append("x").append(sizeZ).append(" [").append(lot.getSpawn().getBlockX()).append(", ").append(lot.getSpawn().getBlockY()).append(", ").append(lot.getSpawn().getBlockZ()).append("]").append(lot.getHidden() ? ChatColor.YELLOW + " H" : "").append(lot.getDisabled() ? ChatColor.DARK_RED + " D" : "");

        if (lot.getParent() != null && lot.getParent().getId() > 0) {
            sublotBuilder.append(ChatColor.DARK_RED).append(lot.getLotNumber()).append(sublotNumberColor).append(" #").append(lot.getId()).append(lot.getLotName().equalsIgnoreCase("") ? "" : " " + ChatColor.LIGHT_PURPLE + lot.getLotName()).append(ChatColor.DARK_AQUA).append("->").append(lot.getParent().getId());

            if (lot.getParent().getParent() != null && lot.getParent().getParent().getId() > 0) {
                sublotBuilder.append(ChatColor.GRAY).append("->").append(lot.getParent().getParent().getId());
            }

            sublotBuilder.append(lot.getHidden() ? ChatColor.YELLOW + " H" : "").append(ChatColor.AQUA);
        }

        String lotString = lotBuilder.toString();
        String sublotString = "";
        boolean normalLot = true;

        if (sublotBuilder.length() > 0) {
            sublotString = sublotBuilder.toString();
            normalLot = false;
        }

        return new LotInfo(lotString, sublotString, normalLot);
    }

    static class LotInfo {
        private String lotString = null;
        private String sublotString = null;
        private boolean normalLot = false;

        public LotInfo(String lotString, String sublotString, boolean normalLot) {
            this.lotString = lotString;
            this.sublotString = sublotString;
            this.normalLot = normalLot;
        }

        public String getLotString() {
            return lotString;
        }

        public String getSublotString() {
            return sublotString;
        }

        public boolean isNormalLot() {
            return normalLot;
        }
    }

    @CommandMethod(aliases = {"listlots"},
    description = "Lists all lots of a player, filtered by world, or all tagged lots.",
    permission = Permission.command_lot_listlots,
    usage = "/listlots <[username [-page, -p <number>]] [-hidden] [-world, -w <world>]> OR <-tagged, -t [-page, -p <page>]>",
    serverCommand = true)
    public static boolean commandListLots(InnPlugin parent, IdpCommandSender sender, LynxyArguments args) {
        if (args.hasArgument("tag")) {
            String tag = args.getString("tag");

            if (tag == null) {
                sender.printError("You must provide a tag.");
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

            List<InnectisLot> lots = LotHandler.getByTag(tag);

            if (lots.isEmpty()) {
                sender.printError("There are no lots with that tag!");
                return true;
            }

            List<String> content = new ArrayList<String>();
            List<String> sublotContent = new ArrayList<String>();
            StringBuilder sublotBuilder = new StringBuilder(1024);
            int lotCt = 0;
            int sublotCt = 0;
            int idx = 0;

            content.add(ChatColor.AQUA + "Lot information:");

            for (InnectisLot lot : lots) {
                LotInfo info = getLotInfo(lot);

                if (info.isNormalLot()) {
                    content.add("  " + info.getLotString());
                    lotCt++;
                } else {
                    sublotCt++;

                    if (sublotBuilder.length() > 0) {
                        sublotBuilder.append(" ");
                    }

                    sublotBuilder.append(info.getSublotString());
                    idx++;

                    if (idx % 3 == 0) {
                        sublotContent.add("  " + sublotBuilder.toString());
                        sublotBuilder = new StringBuilder(1024);
                    }
                }
            }

            if (sublotBuilder.length() > 0) {
                sublotContent.add("  " + sublotBuilder.toString());
            }

            if (sublotContent.size() > 0) {
                content.add(ChatColor.AQUA + "");
                content.add(ChatColor.AQUA + "Sublot information:");
                content.addAll(sublotContent);
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, content);

            if (ph.isValidPage()) {
                sender.print(ChatColor.AQUA, "Listing " + lotCt + " lots and " + sublotCt + " sublots with tag \"" + tag + "\"");
                sender.print(ChatColor.AQUA, "Showing page " + pageNo + " of " + ph.getMaxPage());
                sender.print(ChatColor.AQUA, "");

                for (String str : ph.getParsedInfo()) {
                    sender.print(ChatColor.AQUA, str);
                }
            } else {
                sender.printError("Invalid page number.");
            }

            return true;
        } else if (args.hasOption("tagged", "t")) {
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

            HashMap<String, List<InnectisLot>> taggedLots = LotHandler.getAllTaggedLots();

            if (taggedLots.isEmpty()) {
                sender.printError("There are no tagged lots!");
                return true;
            }

            List<String> content = new ArrayList<String>();

            for (Map.Entry<String, List<InnectisLot>> entry : taggedLots.entrySet()) {
                String tag = entry.getKey();
                List<InnectisLot> lots = entry.getValue();
                int lotCt = 0;
                int sublotCt = 0;

                for (InnectisLot lot : lots) {
                    LotInfo info = getLotInfo(lot);

                    if (info.isNormalLot()) {
                        lotCt++;
                    } else {
                        sublotCt++;
                    }
                }

                content.add(ChatColor.AQUA + tag + " (" + ChatColor.WHITE + lotCt + ChatColor.GREEN + " lots" + ChatColor.AQUA + ", " + ChatColor.WHITE + sublotCt + ChatColor.YELLOW + " sublots" + ChatColor.AQUA + ")");
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, content);

            if (ph.isValidPage()) {
                sender.print(ChatColor.AQUA, "Listing all lots by tag");
                sender.print(ChatColor.AQUA, "Showing page " + pageNo + " of " + ph.getMaxPage());
                sender.print(ChatColor.AQUA, "");

                for (String str : ph.getParsedInfo()) {
                    sender.print(ChatColor.AQUA, str);
                }
            } else {
                sender.printError("Invalid page number.");
            }

            return true;
        } else if (args.getActionSize() == 0) {
            if (sender.isPlayer()) {
                IdpPlayer player = (IdpPlayer) sender;
                IdpWorldRegion region = player.getRegion();
                boolean includeHidden = false;

                if (args.hasOption("hidden")) {
                    includeHidden = true;
                }

                if (region == null) {
                    player.printInfo("No region found.");
                    return true;
                }

                List<InnectisLot> lots = LotHandler.getLotsOverlapping(region, includeHidden);
                if (lots == null) {
                    player.printInfo("There are no lots in your selection.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (InnectisLot lot : lots) {
                        sb.append(lot.getId()).append(", ");
                    }
                    player.printInfo(lots.size() + " lot(s) found in your selection: " + sb.substring(0, sb.length() - 2));
                }
                return true;
            }
        } else if (args.getActionSize() >= 1) {
            int pageNo = 1;
            String targ = args.getString(0);

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));
                } catch (NumberFormatException nfe) {
                    sender.printError("Page number is not expressed as a number.");
                    return true;
                }
            }

            World world = null;

            if (args.hasArgument("world", "w")) {
                world = parent.getServer().getWorld(args.getString("world", "w"));

                if (world == null) {
                    sender.printError("Invalid world specified.");
                    return true;
                }
            }

            IdpPlayer target = parent.getPlayer(targ, false);

            if (target != null) {
                targ = target.getName();
            }
            printLotList(sender, targ, pageNo, world);

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"lotsafeadd", "lsa"},
    description = "Adds a username to the lot safelist.",
    permission = Permission.command_lot_lotsafeadd,
    usage = "/lotsafeadd [lot id] <player>",
    serverCommand = false)
    public static boolean CommandLotSafeAdd(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        InnectisLot lot = null;
        String playerName = "";

        if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(id);
            } catch (NumberFormatException nfe) {
                player.printError("ID is not a number.");
                return true;
            }

            playerName = args[1];
        } else {
            lot = LotHandler.getLot(player.getLocation());
            playerName = args[0];
        }

        if (lot == null) {
            player.printError("No lot found!");
            return true;
        }

        if (!(lot.canPlayerManage(player.getName()) || player.hasPermission(Permission.lot_command_override))) {
            player.printError("You do not manage this lot. (ID: " + lot.getId() + ")");
            return true;
        }

        IdpPlayer target = parent.getPlayer(playerName);

        if (target != null) {
            playerName = target.getName();
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName, true);

        if (credentials == null) {
            player.printError("That user doesn't exist!");
            return true;
        }

        if (lot.addSafelist(credentials)) {
            player.printInfo("Added " + playerName + " to the safelist on lot #" + lot.getId() + ".");
        } else {
            player.printError(playerName + " is already safelisted on lot #" + lot.getId() + ".");
        }

        return true;
    }

    @CommandMethod(aliases = {"lotsafedel", "lsd"},
    description = "Removes a player from the lot safelist.",
    permission = Permission.command_lot_lotsafedel,
    usage = "/lotsafedel [lot id] <player> OR <-clear>",
    serverCommand = false)
    public static boolean CommandLotSafeDel(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        InnectisLot lot = null;
        String safeUser = "";

        if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(id);
            } catch (NumberFormatException nfe) {
                player.printError("Lot ID is not a number.");
                return true;
            }

            safeUser = args[1];
        } else {
            lot = LotHandler.getLot(player.getLocation());
            safeUser = args[0];
        }

        if (lot == null) {
            player.printError("No lot found!");
            return true;
        }

        if (!(lot.canPlayerManage(player.getName()) || player.hasPermission(Permission.lot_command_override))) {
            player.printError("You do not manage this lot. (ID: " + lot.getId() + ")");
            return true;
        }

        boolean clearAll = safeUser.equalsIgnoreCase("-clear");

        if (clearAll) {
            List<PlayerCredentials> safelist = lot.getSafelist();

            if (safelist.size() > 0) {
                lot.clearSafelist();
                player.printInfo("Cleared the safelist on lot #" + lot.getId() + ".");
            } else {
                player.printError("Lot #" + lot.getId() + " does not have a safelist.");
            }
        } else {
            IdpPlayer target = parent.getPlayer(safeUser);

            if (target != null) {
                safeUser = target.getName();
            } else {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(safeUser);

                if (credentials == null) {
                    player.printError("That player doesn't exist!");
                    return true;
                }
            }

            if (lot.removeSafelist(safeUser)) {
                player.printInfo("Removed " + safeUser + " from the safelist on lot #" + lot.getId() + ".");
            } else {
                player.printError(safeUser + " is not safelisted on lot #" + lot.getId() + ".");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"mylots"},
    description = "Lists all of your lots.",
    permission = Permission.command_lot_mylots,
    usage = "/mylots [-page, -p <number>]",
    serverCommand = false)
    public static void commandMyLots(IdpPlayer player, ParameterArguments args) {
        int pageNo = 1;

        if (args.getString("page", "p") != null) {
            try {
                pageNo = Integer.parseInt(args.getString("page", "p"));
            } catch (NumberFormatException nfe) {
                player.printError("Page number is not expressed as a number.");
                return;
            }
        }

        printLotList(player, player.getName(), pageNo, null);
    }

    @CommandMethod(aliases = {"lotban"},
    description = "Bans one or multiple players from a lot.",
    permission = Permission.command_lot_lotban,
    usage = "/lotban [id] <username[,username2,...]> [timeout]",
    serverCommand = false)
    public static boolean commandLotBan(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length < 1 || args.length > 3) {
            return false;
        }

        InnectisLot lot = LotHandler.getLot(player.getLocation());
        String playerString = null;
        String timeString = "";
        long timeout = 0;

        if (args.length > 1) {
            int timeArg = 0;

            try {
                int id = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(id);
                playerString = args[1];

                if (args.length == 3) {
                    timeArg = 2;
                }
            } catch (NumberFormatException nfe) {
                playerString = args[0];
                timeArg = 1;
            }

            if (timeArg > 0) {
                String timeStr = args[timeArg];

                timeout = DateUtil.getTimeFormula(timeStr);

                if (timeout == -1) {
                    player.printError("Invalid time specified. (Ex. 5h3m)");
                    return true;
                }

                timeString = DateUtil.getTimeString(timeout, true);
                timeout += System.currentTimeMillis();
            }
        } else {
            playerString = args[0];
        }

        if (playerString == null) {
            return false;
        }

        if (lot == null) {
            lot = LotHandler.getLot(player.getLocation());
        }

        // Can't ban if not on a lot!
        if (lot == null) {
            player.printError("You must use this command on a lot!");
            return true;
        }

        // Check for rights to the lot
        if (!(lot.canPlayerManage(player.getName()) || player.hasPermission(Permission.lot_command_override))) {
            player.printError("You do not own this lot. Unable to lot ban!");
            return true;
        }

        if (!lot.isValidPlayer(playerString)) {
            player.printError("That player does not exist!");
            return true;
        }

        if (playerString.toLowerCase().contains(player.getName().toLowerCase())) {
            player.printError("You cannot ban yourself from this lot!");
            return true;
        }

        if (playerString.contains("@")) {
            player.printError("You cannot lotban @!");
            return true;
        }

        if (playerString.equalsIgnoreCase("%")) {
            for (IdpPlayer p : lot.getPlayersInsideRegion(0)) {
                // If not self or staff
                if (!p.getSession().isStaff()) {
                    // No members, operators or the owner
                    if (!(lot.containsMember(p.getName()) || lot.canPlayerManage(p.getName()))) {
                        Location kickLocation = lot.getClosestCorner(p.getLocation());
                        p.teleport(kickLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                        p.printInfo("You were banned from lot #" + lot.getId() + " by " + player.getName() + ".");
                    }
                }
            }

            lot.banUser(Configuration.EVERYONE_CREDENTIALS, timeout);
            String personalBanMsg = "Banned everyone on lot #" + lot.getId();

            if (timeout > 0) {
                personalBanMsg += " for " + timeString;
            }

            personalBanMsg += ".";

            player.printError(personalBanMsg);
        } else {
            if (playerString.contains("%")) {
                player.printError("You cannot ban % with a group of names.");
                return true;
            }

            String[] targetNames = playerString.split(",");
            List<String> nameCache = new ArrayList<String>();
            List<String> cannotBan = new ArrayList<String>();

            for (String name : targetNames) {
                IdpPlayer testPlayer = parent.getPlayer(name);
                String actualName = name;
                boolean tempSession = false;
                boolean valid = false;
                PlayerSession session = null;
                PlayerCredentials credentials = null;

                if (testPlayer != null) {
                    actualName = testPlayer.getName();
                    session = testPlayer.getSession();
                    credentials = PlayerCredentialsManager.getByUniqueId(testPlayer.getUniqueId());
                    valid = true;
                } else {
                    credentials = PlayerCredentialsManager.getByName(actualName);

                    if (credentials != null) {
                        session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), parent, true);
                        valid = true;
                        tempSession = true;
                    }
                }

                if (valid) {
                    if (!nameCache.contains(actualName.toLowerCase())) {
                        nameCache.add(actualName.toLowerCase());

                        boolean canBan = (!actualName.equalsIgnoreCase(player.getName())
                                && !session.hasPermission(Permission.lot_ban_override));

                        if (tempSession) {
                            session.destroy();
                        }

                        if (canBan) {
                            if (lot.banUser(credentials, timeout)) {
                                PlayerCredentialsManager.addCredentialsToCache(credentials);

                                if (timeout > 0) {
                                    player.printInfo("Banned " + actualName + " from lot #" + lot.getId() + " for " + timeString + ".");
                                } else {
                                    player.printInfo("Banned " + actualName + " from lot #" + lot.getId() + " indefinitely.");
                                }

                                if (lot.removeMember(actualName) || lot.removeOperator(actualName)) {
                                    player.printInfo(actualName + " was also denied access to this lot.");
                                }

                                if (testPlayer != null) {
                                    InnectisLot banPlayerLot = LotHandler.getLot(testPlayer.getLocation());

                                    if (banPlayerLot == lot) {
                                        Location kickLocation = lot.getClosestCorner(testPlayer.getLocation());
                                        testPlayer.teleport(kickLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                                        testPlayer.printInfo("You were banned from lot #" + lot.getId() + " by " + player.getName() + ".");
                                    }
                                }
                            } else {
                                player.printError(actualName + " is already banned from this lot.");
                            }
                        } else {
                            cannotBan.add(actualName);
                        }
                    } else {
                        cannotBan.add(actualName);
                    }
                } else {
                    cannotBan.add(actualName);
                }
            }

            String notBannedList = "";

            for (String name : cannotBan) {
                if (notBannedList.isEmpty()) {
                    notBannedList = name;
                } else {
                    notBannedList += ", " + name;
                }
            }

            if (!notBannedList.isEmpty()) {
                player.printError("The following players were not lot banned: " + notBannedList);
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"lotkick"},
    description = "Kicks one or multiple players from a lot.",
    permission = Permission.command_lot_lotkick,
    usage = "/lotkick [id] <username[,username2,...]>",
    serverCommand = false)
    public static boolean commandLotKick(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length > 0) {
            InnectisLot lot = null;

            if (args.length > 1) {
                try {
                    int id = Integer.parseInt(args[0]);
                    lot = LotHandler.getLot(id);
                } catch (NumberFormatException nfe) {
                    player.printError("Lot id is not a number.");
                    return true;
                }
            } else {
                lot = LotHandler.getLot(player.getLocation());
            }

            // Can't kick if not on a lot!
            if (lot == null) {
                player.printError("This command must be used on a lot!");
                return true;
            }

            // Check for rights to the lot
            if (!(lot.canPlayerManage(player.getName()) || player.hasPermission(Permission.lot_command_override))) {
                player.printError("You do not own or operate this lot. Unable to kick!");
                return true;
            }

            String playerString = args[args.length - 1];

            if (playerString.toLowerCase().contains(player.getName().toLowerCase())) {
                player.printError("You cannot kick yourself from this lot!");
                return true;
            }

            if (playerString.equals("%")) {
                int kickCount = 0;

                for (IdpPlayer p : lot.getPlayersInsideRegion(0)) {
                    if (!p.getSession().isStaff()) {
                        Location kickLocation = lot.getClosestCorner(p.getLocation());
                        p.teleport(kickLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                        p.printInfo("You were kicked from lot #" + lot.getId() + " by " + player.getName() + ".");
                        player.printInfo("You kicked " + p.getName() + " from lot #" + lot.getId() + ".");
                        kickCount++;
                    }
                }

                if (kickCount > 0) {
                    player.printInfo("Kicked " + kickCount + " users off lot #" + lot.getId() + ".");
                } else {
                    player.printError("No one to kick on lot #" + lot.getId() + ".");
                }
            } else {
                if (playerString.contains("%")) {
                    player.printError("Cannot kick % with a group of players.");
                    return true;
                }

                String[] targetNames = playerString.split(",");
                List<IdpPlayer> targetPlayers = new ArrayList<IdpPlayer>();
                List<String> nameCache = new ArrayList<String>();
                List<String> notKickedList = new ArrayList<String>();

                for (String name : targetNames) {
                    IdpPlayer testPlayer = parent.getPlayer(name);

                    if (testPlayer != null) {
                        if (!nameCache.contains(testPlayer.getName())) {
                            targetPlayers.add(testPlayer);
                            nameCache.add(testPlayer.getName());
                        }
                    } else if (!notKickedList.contains(name)) {
                        notKickedList.add(name);
                    }
                }

                if (targetPlayers.isEmpty()) {
                    player.printError("No one to kick on lot #" + lot.getId() + ".");
                    return true;
                }

                for (IdpPlayer kickPlayer : targetPlayers) {
                    InnectisLot kickerLot = LotHandler.getLot(kickPlayer.getLocation());

                    if (kickerLot == lot) {
                        if (!kickPlayer.hasPermission(Permission.lot_ban_override)) {
                            Location kickLocation = lot.getClosestCorner(kickPlayer.getLocation());
                            kickPlayer.teleport(kickLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                            player.printInfo("Kicked " + kickPlayer.getName() + " off lot #" + lot.getId() + ".");
                            kickPlayer.printInfo("You were kicked from lot #" + lot.getId() + " by " + player.getName() + ".");
                        } else {
                            player.printError(kickPlayer.getName() + " cannot be kicked.");
                        }
                    } else {
                        player.printError(kickPlayer.getName() + " is not on this lot.");
                    }
                }

                if (!notKickedList.isEmpty()) {
                    String cannotKickString = "";

                    for (String name : notKickedList) {
                        if (cannotKickString.isEmpty()) {
                            cannotKickString = name;
                        } else {
                            cannotKickString += ", " + name;
                        }
                    }

                    player.printError("The following players were not lot kicked: " + cannotKickString);
                }
            }

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"lotcenter"},
    description = "Teleports you to the center of a lot.",
    permission = Permission.command_lot_lotcenter,
    usage = "/lotcenter [id]",
    serverCommand = false)
    public static boolean commandLotCenter(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length > 1) {
            return false;
        }

        InnectisLot lot = LotHandler.getLot(player.getLocation());

        if (args.length > 0) {
            try {
                lot = LotHandler.getLot(Integer.parseInt(args[0]));
            } catch (NumberFormatException ex) {
                player.printError("Invalid lot ID!");
                return true;
            }
        }

        if (lot == null) {
            player.printError("Lot not found!");
            return true;
        }

        if (!lot.canPlayerAccess(player.getName()) && !player.hasPermission(Permission.command_lot_lotcenter_override)) {
            player.printError("You do not have access to this lot!");
            return true;
        }

        PlayerSession session = player.getSession();

        if (session.isInDamageState()) {
            player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
            return true;
        }

        player.teleport(lot.getCenterSafe(), TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE);

        return true;
    }

    @CommandMethod(aliases = {"lotinfo"},
    description = "Prints out info about a lot.",
    permission = Permission.command_lot_lotinfo,
    usage = "/lotinfo <lotid>",
    serverCommand = true)
    public static boolean commandLotInfo(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 1) {
            try {
                int id = Integer.parseInt(args[0]);
                InnectisLot thislot = LotHandler.getLot(id);
                if (thislot != null) {
                    ArrayList<InnectisLot> lots = new ArrayList<InnectisLot>();
                    lots.add(thislot);

                    if (sender instanceof IdpPlayer) {
                        IdpPlayer player = (IdpPlayer) sender;
                        player.getSession().setLastLotIDQueried(id);
                    }

                    commandLotInfo_printLotDetails(sender, lots);
                } else {
                    sender.printError("That lot ID is unknown!");
                }
                return true;
            } catch (NumberFormatException nfe) {
                sender.printError("That is not a valid ID!");
            }
        }
        return false;
    }

    private static void commandLotInfo_printLotDetails(IdpCommandSender sender, List<InnectisLot> lots) {
        InnectisLot thislot = lots.get(0);
        thislot.reloadBanned();

        sender.print(ChatColor.LIGHT_PURPLE, "Lot id: " + thislot.getId() + (thislot.getLotName().equalsIgnoreCase("") ? "" : " \"" + thislot.getLotName() + "\"") + (thislot.getHidden() ? " HIDDEN" : "") + (thislot.getDisabled() ? ChatColor.DARK_RED + " DISABLED" + ChatColor.LIGHT_PURPLE : "") + (thislot.getParent() == null ? "" : " (parent: " + thislot.getParent().getId() + ")"));

        LotTag tag = thislot.getTag();

        if (tag != null) {
            ChatColor tagColor = (tag.isPublic() ? ChatColor.LIGHT_PURPLE : ChatColor.DARK_PURPLE);

            if (tag.isPublic() || sender.hasPermission(Permission.special_view_private_tags)) {
                sender.print(tagColor, "Tagged as \"" + tag.getTag() + "\"");
            }
        }

        if (lots.size() > 1) {
            StringBuilder sb = new StringBuilder(500);
            sb.append(lots.size() - 1).append(" others lot").append(lots.size() == 2 ? "" : "s").append(" found: ");
            for (int i = 1; i < lots.size(); i++) {
                sb.append(lots.get(i).getId()).append(", ");
            }
            sender.print(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE + sb.substring(0, sb.length() - 2));
        }

        if (thislot.isAssignable()) {
            sender.print(ChatColor.LIGHT_PURPLE, "Assignable Lot");
        } else {
            String ownerName = null;

            if (thislot.getOwnerCredentials().equals(Configuration.SYSTEM_CREDENTIALS)) {
                ownerName = "System Account";
            } else if (thislot.getOwnerCredentials().equals(Configuration.GAME_CREDENTIALS)) {
                ownerName = "Game Account";
            } else {
                ownerName = thislot.getOwner();
            }

            sender.print(ChatColor.LIGHT_PURPLE, "Owner: " + ownerName);
            sender.print(ChatColor.LIGHT_PURPLE, "Members: " + thislot.getMembersString(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, ChatColor.GRAY));

            if (thislot.getBannedList().size() > 0) {
                sender.print(ChatColor.LIGHT_PURPLE, "Banned: " + thislot.getBannedString(ChatColor.LIGHT_PURPLE));
            }

            if (thislot.getSafelist().size() > 0) {
                sender.print(ChatColor.LIGHT_PURPLE, "Safelist: " + thislot.getSafelistString());
            }
        }
        sender.print(ChatColor.LIGHT_PURPLE, "Flags: " + thislot.getFlagsString(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE));
        if (!thislot.getSublots().isEmpty()) {
            StringBuilder sb = new StringBuilder(20);
            for (InnectisLot lot : thislot.getSublots()) {
                sb.append(lot.getId()).append(", ");
            }
            sender.print(ChatColor.LIGHT_PURPLE, "Sublots: " + sb.substring(0, sb.length() - 2));
        }
        int sizeX = thislot.getWidth();
        int sizeY = thislot.getHeight();
        int sizeZ = thislot.getLength();
        sender.print(ChatColor.LIGHT_PURPLE, "Size: " + sizeX + ChatColor.DARK_PURPLE + "x" + sizeY + ChatColor.LIGHT_PURPLE + "x" + sizeZ + " Area2D: " + (sizeX * sizeZ) + " Area3D: " + (sizeX * sizeY * sizeZ));
        if (sender.hasPermission(Permission.lot_extendedinfo)) {
            sender.print(ChatColor.LIGHT_PURPLE, "Location: [" + thislot.getPos1().getBlockX() + ", " + thislot.getPos1().getBlockY() + ", " + thislot.getPos1().getBlockZ() + "]-[" + thislot.getPos2().getBlockX() + ", " + thislot.getPos2().getBlockY() + ", " + thislot.getPos2().getBlockZ() + "]");
            sender.print(ChatColor.LIGHT_PURPLE, "Creator: " + thislot.getCreator());
            sender.print(ChatColor.LIGHT_PURPLE, "Last owner edit: " + thislot.getLastOwnerEditString());
            sender.print(ChatColor.LIGHT_PURPLE, "Last member edit: " + thislot.getLastMemberEditString());
            sender.print(ChatColor.LIGHT_PURPLE, "-----------------------");
        }
    }

    @CommandMethod(aliases = {"lotstack", "lotcount"},
    description = "Lists how many lots are pre-made.",
    permission = Permission.command_lot_lotstack,
    usage = "/lotstack",
    serverCommand = true)
    public static boolean commandLotStack(InnPlugin parent, IdpCommandSender sender, String[] args) {
        sender.printInfo("There are " + LotHandler.getLotCount(LotHandler.ASSIGNABLE_LOT_OWNER) + " available lots to assign!");
        return true;
    }

    @CommandMethod(aliases = {"lotunban"},
    description = "Unbans a player from a lot.",
    permission = Permission.command_lot_lotunban,
    usage = "/lotunban [id] <username>",
    serverCommand = false)
    @SuppressWarnings("fallthrough")
    public static boolean commandLotUnban(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        InnectisLot lot = null;
        switch (args.length) {
            case 1:
                lot = LotHandler.getLot(player.getLocation());
            case 2:
                if (lot == null) {
                    try {
                        int id = Integer.parseInt(args[0]);
                        lot = LotHandler.getLot(id);
                    } catch (NumberFormatException nfe) {
                        player.printError("Lot ID is not a number.");
                        return true;
                    }

                    if (lot == null) {
                        player.printError("No lot with that ID was found!");
                        return true;
                    }
                }

                if (lot.canPlayerManage(player.getName())
                        || player.hasPermission(Permission.lot_command_override)) {
                    String unbanName = args[args.length - 1];

                    if (unbanName.equalsIgnoreCase("-all")) {
                        if (!lot.getBannedList().isEmpty()) {
                            lot.clearBanned();
                            player.printInfo("Cleared all banned users from lot #" + lot.getId() + ".");
                        } else {
                            player.printError("The banned user list is empty!");
                            return true;
                        }
                    } else {
                        IdpPlayer target = parent.getPlayer(unbanName, false);
                        if (target != null) {
                            unbanName = target.getName();
                        }

                        if (lot.unbanUser(unbanName)) {
                            player.printInfo("You unbanned " + unbanName + " from lot #" + lot.getId());
                        } else {
                            player.printError(unbanName + " is not banned from lot #" + lot.getId() + ".");
                            return true;
                        }
                    }
                } else {
                    player.printError("You cannot unban users on this lot!");
                }

                return true;

        }
        return false;
    }

    @CommandMethod(aliases = {"mylot", "myplot"},
    description = "Teleports the player to their lot.",
    permission = Permission.command_lot_mylot,
    usage = "/mylot [lotnumber]",
    serverCommand = false)
    public static boolean commandMyLot(InnPlugin parent, IdpPlayer player, String[] args) {
        PlayerSession session = player.getSession();

        try {
            if (session.isInDamageState()) {
                player.printError("You recently took damage. Wait " + session.getDamageStatusDuration() + " before teleporting!");
                return true;
            }

            InnectisLot lot = null;
            if (args.length == 1) {
                lot = LotHandler.getLot(player.getName(), Integer.parseInt(args[0]));
            } else {
                lot = LotHandler.getLot(player.getName(), 1);
            }

            if (lot != null) {
                if (player.teleport(lot.getSpawn(), TeleportType.RESTRICT_IF_NETHER, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.RESTRICT_IF_NOESCAPE)) {
                    player.printInfo("You have warped to lot #" + lot.getId() + ". (personal lot " + lot.getLotNumber() + ")");
                }
            } else {
                player.printError("That lot does not exist!");
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @CommandMethod(aliases = {"respawnlot", "rl"},
    description = "Sets the lot you will respawn at.",
    permission = Permission.command_lot_respawnlot,
    usage = "/respawnlot <peronal lot number>",
    serverCommand = false)
    public static boolean commandRespawnLot(IdpPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        InnectisLot testLot = null;
        int id = 0;

        try {
            id = Integer.parseInt(args[0]);
            testLot = LotHandler.getLot(player.getName(), id);

            if (id < 0) {
                player.printError("Lot ID cannot be below 0.");
                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Lot number is not a number.");
            return true;
        }

        PlayerSession session = player.getSession();

        // If ID is not 0, save it, otherwise delete from player
        if (id > 0) {
            if (testLot == null) {
                player.printError("The lot with that personal ID is not found.");
                return true;
            }

            session.setRespawnLotPersonalId(id);
            player.printInfo("Your respawn lot has been set to personal lot " + id + ".");
        } else {
            // Make sure they can delete an existing respawn lot
            int existingId = session.getRespawnLotPersonalId();

            if (existingId == 0) {
                player.printError("You have no respawn lot to remove!");
                return true;
            }

            session.deleteRespawnLotPersonalId();
            player.printInfo("Your respawn lot has been removed.");
        }

        return true;
    }

    @CommandMethod(aliases = {"randomlot"},
    description = "Teleports you to a random lot.",
    permission = Permission.command_lot_randomlot,
    usage = "/randomlot",
    serverCommand = false)
    public static boolean commandRandomLot(InnPlugin parent, IdpPlayer player, String[] args) {
        List<InnectisLot> lots = new ArrayList<InnectisLot>(LotHandler.getLots().values());
        InnectisLot targetLot = lots.get(new Random().nextInt(lots.size()));

        if (player.teleport(targetLot.getSpawn(), TeleportType.IGNORE_RESTRICTION, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY)) {
            player.printInfo("You have been teleported to a random lot (#" + targetLot.getId() + ", " + targetLot.getOwner() + ")");
        } else {
            player.printError("Unable to teleport you to random lot (#" + targetLot.getId() + ", " + targetLot.getOwner() + ")");
        }

        return true;
    }

    @CommandMethod(aliases = {"reloadlots"},
    description = "Reloads the lots from the database into memory.",
    permission = Permission.command_lot_reloadlots,
    usage = "/reloadlots",
    serverCommand = true)
    public static boolean commandReloadLots(InnPlugin parent, IdpCommandSender sender, String[] args) {
        HashMap<Integer, InnectisLot> backupLots = LotHandler.getLots();
        InnectisLot backupMainLot = LotHandler.getMainLot();

        if (LotHandler.loadLots()) {
            sender.printInfo("All lots reloaded from database.");
        } else {
            sender.printError("SQL error has occured!");
            sender.printError("Restoring old lots...");
            LotHandler.setLots(backupLots);
            LotHandler.setMainLot(backupMainLot);
            sender.printError("Old lots loaded back into memory.");
        }

        return true;
    }

    @CommandMethod(aliases = {"remlot", "removelot"},
    description = "Removes a lot with the given ID.",
    permission = Permission.command_lot_remlot,
    usage = "/remlot <id> [-pass <password>] [-request]",
    serverCommand = true)
    public static boolean commandRemoveLot(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        int lotid = args.getIntDefaultTo(-1, 0);
        boolean makeRequest = args.hasOption("makerequest", "request", "r");

        // Check the lotid
        if (lotid < 0) {
            sender.printInfo("Invalid lot ID!");
            return false;
        }

        // Check if the command is always a request (for mods) or is if its never a request (console)
        if (sender.isPlayer()) {
            InnectisLot checkLot = LotHandler.getLot(lotid);
            if (checkLot == null) {
                sender.printError("That lot was not found!");
                return true;
            }
            long timeDiff = (System.currentTimeMillis() - checkLot.getLastOwnerEdit()) / (24 * 60 * 60 * 1000);

            if (timeDiff <= 120 && PlayerGroup.MODERATOR.equalsOrInherits(((IdpPlayer) sender).getGroup())
                    && !checkLot.getOwner().equalsIgnoreCase(sender.getName())) {
                // Mods always request unless the lot is theirs and it was last editted over 120 days ago!
                makeRequest = true;
            }
        } else {
            if (makeRequest) {
                sender.printError("The console can't make a removal request.");
                return true;
            }
        }
        InnectisLot lot = LotHandler.getLot(lotid);
        if (lot == null) {
            sender.printError("That lot was not found!");
            return true;
        }

        int lastLotID = -1;

        if (sender instanceof IdpPlayer) {
            IdpPlayer player = (IdpPlayer) sender;
            lastLotID = player.getSession().getLastLotIDQueried();
        }

        String password = args.getString("password", "pass", "p");

        // Check if there is a password
        if (lastLotID == lotid || password != null) {
            try {
                if (lastLotID != lotid && !password.equalsIgnoreCase(lot.getPassword())) {
                    sender.printError("Invalid password supplied to remove lot!");
                    return true;
                }

                InnPlugin.logInfo("Lot #" + lotid + " was removed by " + sender.getColoredName() + "!");

                int remCount = 0;
                if ((remCount = LotHandler.removeLot(lotid)) > 0) {
                    sender.printInfo("Lot #" + lotid + " was succesfully removed!");
                    if (remCount > 1) {
                        sender.printInfo((remCount - 1) + " sublots were also removed.");
                    }
                } else {
                    sender.printError("Lot #" + lotid + " not found!");
                }
                return true;
            } catch (SQLException ex) {
                InnPlugin.logError("SQLException remlot ", ex);
                sender.printError("Cannot remove lot, notify an Admin!");
                return true;
            }
        } else {
            // Check if it must be confirmed by lotowner or do it right away
            if (!makeRequest) {
                List<InnectisLot> lots = new ArrayList<InnectisLot>();
                lots.add(lot);
                sender.printError("Please review the information about lot " + lot.getId() + ".");
                sender.printError("To remove this lot, use password " + ChatColor.AQUA + lot.getPassword() + ".");
                commandLotInfo_printLotDetails(sender, lots);

            } else {
                // Get the lotowner
                IdpPlayer lotOwner = parent.getPlayer(lot.getOwner(), true);

                if (lotOwner == null) {
                    sender.printError("The lot owner must be online to confirm!");
                } else {
                    // The console can't make requests to its safe to cast to player
                    LotRemovalRequest request = new LotRemovalRequest(parent, lotOwner, ((IdpPlayer) sender), lotid);
                    // Send info to player
                    lotOwner.printError(sender.getColoredName(), " has requested to remove your lot #" + lotid + "! ");
                    lotOwner.printError("All sublots (if any) will be removed.");
                    lotOwner.printError("This process cannot be reversed!");

                    TextComponent text = ChatUtil.createTextComponent(ChatColor.RED, "To confirm type /accept or click ");
                    text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, "."));
                    lotOwner.print(text);

                    lotOwner.getSession().addRequest(request);

                    List<InnectisLot> lots = new ArrayList<InnectisLot>();
                    lots.add(lot);
                    sender.printError("A confirmation is sent to the lot owner.");
                    sender.printError("The password will be sent on confirmation!");
                    sender.printError("Please review the information about lot #" + lot.getId() + ".");
                    commandLotInfo_printLotDetails(sender, lots);
                }
            }
            return true;
        }
    }

    @CommandMethod(aliases = {"resetlot"},
    description = "Resets a lot with the given ID.",
    permission = Permission.command_lot_resetlot,
    usage = "/resetlot <id> [-request]",
    serverCommand = false)
    public static boolean commandResetLot(InnPlugin parent, IdpPlayer sender, ParameterArguments args) {
        int lotid = args.getIntDefaultTo(-1, 0);
        boolean makeRequest = args.hasOption("makerequest", "request");

        // Check the lotid
        if (lotid < 0) {
            sender.printInfo("Invalid lot ID!");
            return false;
        }

        final InnectisLot lot = LotHandler.getLot(lotid);

        if (lot == null) {
            sender.printError("That lot was not found!");
            return true;
        }

        // Check if the command is always a request (for mods)
        if (PlayerGroup.MODERATOR.equalsOrInherits(sender.getGroup())) {
            // Mods always request if the lot is not assignable and the owner isn't the sender!
            makeRequest = !(lot.isAssignable() || lot.getOwner().equalsIgnoreCase(sender.getName()));
        }

        // Check if it must be confirmed by lotowner or do it right away
        if (!makeRequest) {
            String[] players;
            if (sender.getName().equalsIgnoreCase(lot.getOwner())) {
                players = new String[]{sender.getName()};
            } else {
                players = new String[]{sender.getName(), lot.getOwner()};
            }

            final int groundlvl = sender.getLocation().getBlockY() - 1;
            RegionEditTask task = new RegionEditTask(lot, players) {
                int chunkPasses = 0;
                int lastProgress = 0;

                @Override
                public void taskStopped(String reason) {
                    this.reportPlayers("The task was stopped for the following reason: " + reason);
                }

                @Override
                public void taskComplete() {
                    // Make sure unused chunks are all reclaimed
                    ChunkDatamanager.reclaimUnusedChunks();

                    this.reportPlayers("Task complete!");
            }

                @Override
                public void taskIncrement(int progress) {
                    if (progress != lastProgress) {
                        lastProgress = progress;
                        reportPlayers("Region regeneration " + progress + "% done!");
                    }
                }

                @Override
                public void handleChunk(IdpVector2D chunkLocation) {
                    IdpWorldRegion chunkRegion = getChunkRegion(chunkLocation.getBlockX(), chunkLocation.getBlockZ());
                    BlockCounter cntr = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);

                    IdpRegion bedrock = new IdpRegion(chunkRegion.getMinimumPoint().setY(0), chunkRegion.getMaximumPoint().setY(0));
                    IdpRegion belowGround = new IdpRegion(chunkRegion.getMinimumPoint().setY(1), chunkRegion.getMaximumPoint().setY(groundlvl - 1));
                    IdpRegion ground = new IdpRegion(chunkRegion.getMinimumPoint().setY(groundlvl), chunkRegion.getMaximumPoint().setY(groundlvl));
                    IdpRegion aboveGround = new IdpRegion(chunkRegion.getMinimumPoint().setY(groundlvl + 1), chunkRegion.getMaximumPoint().setY(255));

                    for (Block blk : cntr.getBlockList(bedrock, chunkRegion.getWorld(), null)) {
                        IdpMaterial mat = IdpMaterial.fromBlock(blk);

                        if (mat != IdpMaterial.BEDROCK) {
                            BlockHandler.setBlock(blk, IdpMaterial.BEDROCK, false);
                        }

                        IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                        if (blockData.hasData()) {
                            blockData.clear();
                        }
                    }

                    for (Block blk : cntr.getBlockList(belowGround, chunkRegion.getWorld(), null)) {
                        IdpMaterial mat = IdpMaterial.fromBlock(blk);

                        if (mat != IdpMaterial.STONE) {
                            BlockHandler.setBlock(blk, IdpMaterial.STONE, false);
                        }

                        IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                        if (blockData.hasData()) {
                            blockData.clear();
                        }
                    }

                    for (Block blk : cntr.getBlockList(ground, chunkRegion.getWorld(), null)) {
                        IdpMaterial mat = IdpMaterial.fromBlock(blk);

                        if (mat != IdpMaterial.GRASS) {
                            BlockHandler.setBlock(blk, IdpMaterial.GRASS, false);
                        }

                        IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                        if (blockData.hasData()) {
                            blockData.clear();
                        }
                    }

                    for (Block blk : cntr.getBlockList(aboveGround, chunkRegion.getWorld(), null)) {
                        IdpMaterial mat = IdpMaterial.fromBlock(blk);

                        if (mat != IdpMaterial.AIR) {
                            // Only apply physics around the outer wall of the lot
                            boolean applyPhysics = (blk.getX() == lot.getLowestX() || blk.getX() == lot.getHighestX()
                                    || blk.getZ() == lot.getLowestZ() || blk.getZ() == lot.getHighestZ());

                            BlockHandler.setBlock(blk, IdpMaterial.AIR, applyPhysics);
                        }

                        IdpBlockData blockData = BlockHandler.getIdpBlockData(blk.getLocation(), true);

                        if (blockData.hasData()) {
                            blockData.clear();
                        }
                    }

                    chunkPasses++;

                    // Reclaim chunks after so many passes
                    if (chunkPasses % 75 == 0) {
                        // Remove the chunk data recently created by this chunk
                        ChunkDatamanager.reclaimUnusedChunks();
                    }
                }
            };
            task.setLastExecution(0l);

            InnPlugin.getPlugin().getTaskManager().addTask(task);
        } else {
            // Get the lotowner
            IdpPlayer lotOwner = parent.getPlayer(lot.getOwner(), true);

            if (lotOwner == null) {
                sender.printError("The lot owner must be online to confirm!");
            } else {
                // The console can't make requests to its safe to cast to player
                LotResetRequest request = new LotResetRequest(parent, lotOwner, sender, lot, sender.getLocation().getBlockY() - 1);
                // Send info to player
                lotOwner.printError(sender.getColoredName(), " has requested to reset your lot #" + lotid + "! ");
                lotOwner.printError("This process cannot be reversed!");

                TextComponent text = ChatUtil.createTextComponent(ChatColor.RED, "To confirm type /accept or click ");
                text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
                text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, "."));
                lotOwner.print(text);

                lotOwner.getSession().addRequest(request);

                sender.printError("A confirmation is send to the lotowner.");
                sender.printError("Stay close to the lot untill its done with the regeneration!");
            }
        }
        return true;
    }

    @CommandMethod(aliases = {"savelots"},
    description = "Saves the lots in memory to the database.",
    permission = Permission.command_lot_savelots,
    usage = "/savelots",
    serverCommand = true)
    public static boolean commandSaveLots(InnPlugin parent, IdpCommandSender sender, String[] args) {
        sender.printInfo("Saving lots...");
        LotHandler.saveLots();
        sender.printInfo("Save complete.");
        return true;
    }

    @CommandMethod(aliases = {"selectlot", "sellot"},
    description = "Loads a lot into your WE selection.",
    permission = Permission.command_lot_select,
    usage = "/selectlot [id] [-full, -f] [-usefeet, -uf]",
    serverCommand = false)
    public static boolean commandSelectLot(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        Location playerLocation = player.getLocation();
        InnectisLot lot = LotHandler.getLot(playerLocation);
        boolean full = args.hasOption("full", "f");
        boolean useFeet = args.hasOption("usefeet", "uf");

        if (args.getActionSize() > 0) {
            try {
                lot = LotHandler.getLot(Integer.parseInt(args.getString(0)));
            } catch (NumberFormatException nfe) {
                player.printError("Lot number is not a number.");
                return true;
            }
        }

        if (lot == null) {
            player.printError("No lot found!");
            return false;
        }

        Vector minPt = lot.getMinimumPoint();
        Vector maxPt = lot.getMaximumPoint();

        if (full) {
            player.setRegionLoc1(minPt);
            player.setRegionLoc2(maxPt);
        } else {
            int y = playerLocation.getBlockY();
            int modY = (useFeet ? y : y - 1);

            player.setRegionLoc1(minPt.setY(modY));
            player.setRegionLoc2(maxPt.setY(modY));
        }

        player.printInfo("Loaded selection for lot #" + lot.getId());

        return true;
    }

    @CommandMethod(aliases = {"borderlot"},
    description = "Makes a border on the edge of a lot.",
    permission = Permission.command_lot_borderlot,
    usage = "/borderlot [lot ID] [block] [-y <value>]",
    serverCommand = false)
    public static boolean commandBorderLot(IdpPlayer player, LynxyArguments args) {
        Location loc = player.getLocation();
        InnectisLot lot = LotHandler.getLot(loc);
        IdpMaterial mat = IdpMaterial.GLASS;

        if (args.getActionSize() > 0) {
            if (args.getActionSize() > 1) {
                try {
                    lot = LotHandler.getLot(Integer.parseInt(args.getString(0)));
                } catch (NumberFormatException nfe) {
                    player.printError("Lot ID is not a number!");
                    return true;
                }

                mat = IdpMaterial.fromString(args.getString(1));
            } else {
                mat = IdpMaterial.fromString(args.getString(0));
            }

            if (mat == null) {
                player.printError("Material not found!");
                return true;
            }
        }

        if (lot == null) {
            player.printError("Lot not found!");
            return true;
        }

        // Can only set border material if you have access to the lot
        if (!lot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.lot_command_override)) {
            player.printError("You cannot set a lot border here!");
            return true;
        }

        if (!(mat.isBlock() && mat.canPlayerPlaceMaterial(player))) {
            player.printError("You cannot use this border material!");
            return true;
        }

        int y = loc.getBlockY() - 1;

        if (args.hasArgument("y")) {
            try {
                y = Integer.parseInt(args.getString("y"));

                if (y < 1) {
                    player.printError("Y cannot be less than 1!");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Y-coordinate is not a number!");
                return true;
            }
        }

        Vector minVec = lot.getMinimumPoint();
        int minX = minVec.getBlockX();
        int minZ = minVec.getBlockZ();

        Vector maxVec = lot.getMaximumPoint();
        int maxX = maxVec.getBlockX();
        int maxZ = maxVec.getBlockZ();

        Vector vec1 = new Vector(minX, y, minZ);
        Vector vec2 = new Vector(maxX, y, maxZ);

        player.setRegionLoc1(vec1);
        player.setRegionLoc1(vec2);

        IdpRegion borderregion = new IdpRegion(vec1, vec2);

        // Set the border
        BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);
        for (Block blk : counter.getWallBlockList(borderregion, lot.getWorld(), null)) {
            BlockHandler.setBlock(blk, mat);

            IdpBlockData data = BlockHandler.getIdpBlockData(blk.getLocation());

            // If material is set to air, remove all block data, else set
            // this to a virtual block
            if (mat == IdpMaterial.AIR) {
                data.clear();
            } else {
                data.setVirtualBlockStatus(true);
            }

            data.save();
        }

        if (mat == IdpMaterial.AIR) {
            player.printInfo("Cleared the border of lot #" + lot.getId() + ".");
        } else {
            player.printInfo("Lot #" + lot.getId() + " was given a border of " + mat.getName().toLowerCase() + ".");
        }

        return true;
    }

    @CommandMethod(aliases = {"setleavemsg", "lmsg", "setexitmsg"},
    description = "Sets the exit message of a lot.",
    permission = Permission.command_lot_setleavemsg,
    usage = "/setleavemsg [id] <message>",
    serverCommand = false)
    public static boolean commandSetLeaveMsg(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        InnectisLot lot = null;
        String message = null;
        int startarg = 0;
        StringBuilder sb;

        if (args.length == 0) {
            lot = LotHandler.getLot(player.getLocation());
            if (lot == null) {
                player.printError("You are not standing on a lot!");
                return true;
            }
            message = "";
        } else if (args.length == 1) {
            try {
                int lotnr = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(lotnr);
                message = "";
                if (lot == null) {
                    lot = LotHandler.getLot(player.getLocation());
                    message = args[0];
                }
            } catch (NumberFormatException ex) {
                lot = LotHandler.getLot(player.getLocation());
                message = args[0];
            }
            if (lot == null) {
                player.printError("You are not standing on a lot!");
                return true;
            }
        } else if (args.length > 1) {
            try {
                int lotnr = Integer.parseInt(args[0]);
                startarg = 1;
                lot = LotHandler.getLot(lotnr);
            } catch (NumberFormatException ex) {
                lot = LotHandler.getLot(player.getLocation());
            }
            if (lot == null) {
                player.printError("No lot found!");
                return true;
            }
            sb = new StringBuilder(args[startarg]);
            for (int i = startarg + 1; i < args.length; i++) {
                sb.append(" ").append(args[i]);
            }
            message = sb.toString();
        }

        message = message.trim(); // don't allow blank or whitespaced message
        boolean makeBlank = true;

        if (!message.isEmpty()) {
            makeBlank = false;
            if (message.length() >= 5 && message.substring(0, 5).equalsIgnoreCase("[idp]")) { //trim [IDP] from message
                message = message.substring(5);
            }
        }

        if (message.length() > 200) {
            player.printError("Message size is too big!");
            return true;
        }

        if (lot.canPlayerManage(player.getName())
                || player.hasPermission(Permission.lot_command_override)) {
            lot.setExitMsg(message);
            player.printInfo("Exit message " + (makeBlank ? "cleared" : "updated") + " for lot #" + lot.getId());
        } else {
            player.printError("This is not your lot!");
        }
        return true;
    }

    @CommandMethod(aliases = {"setlotnr"},
    description = "Sets the lot number of the lot.",
    permission = Permission.command_lot_setlotnumber,
    usage = "/setlotnr [lotid] <lotnr>",
    serverCommand = true)
    public static boolean commandSetLotNr(InnPlugin parent, IdpCommandSender sender, String[] args) {
        InnectisLot lot = null;
        int lotnr;

        try {
            if (args.length == 1 && sender.isPlayer()) {
                IdpPlayer player = (IdpPlayer) sender;
                lot = LotHandler.getLot(player.getLocation());
                lotnr = Integer.parseInt(args[0]);

            } else if (args.length == 2) {
                int lotid = Integer.parseInt(args[0]);
                lotnr = Integer.parseInt(args[1]);
                if (lotnr > 0) {
                    lot = LotHandler.getLot(lotid);
                }
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }

        if (lot == null) {
            sender.printError("A lot with that ID was not found!");
        } else {
            if (sender.isPlayer()) {
                IdpPlayer player = (IdpPlayer) sender;

                if (!lot.getOwner().equalsIgnoreCase(player.getName()) && !player.hasPermission(Permission.command_lot_setlotnumber_any)) {
                    sender.printError("You may not set the lot number of this lot!");
                    return true;
                }
            }

            if (lot.getLotNumber() == lotnr) {
                sender.printError("Lot number is the same as current lot number!");
            } else if (LotHandler.editLotNr(lot.getId(), lotnr)) { //dont need to call lot.saveLot()
                sender.printInfo("Succesfully changed the lot number for #" + lot.getId() + " to " + lot.getLotNumber());
            } else {
                sender.printError("That lot number already taken!");
            }
        }
        return true;
    }

    @CommandMethod(aliases = {"setentermsg", "emsg", "setentrymsg", "setwelcomemsg"},
    description = "Sets the enter message for the lot.",
    permission = Permission.command_lot_setentermsg,
    usage = "/setentermsg [id] <message>",
    serverCommand = false)
    public static boolean commandSetEnterMsg(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        InnectisLot lot = null;
        String message = null;
        int startarg = 0;
        StringBuilder sb;

        if (args.length == 0) {
            lot = LotHandler.getLot(player.getLocation());
            if (lot == null) {
                player.printError("You are not standing on a lot!");
                return true;
            }
            message = "";
        } else if (args.length == 1) {
            try {
                int lotnr = Integer.parseInt(args[0]);
                lot = LotHandler.getLot(lotnr);
                message = "";
                if (lot == null) {
                    lot = LotHandler.getLot(player.getLocation());
                    message = args[0];
                }
            } catch (NumberFormatException ex) {
                lot = LotHandler.getLot(player.getLocation());
                message = args[0];
            }
            if (lot == null) {
                player.printError("You are not standing on a lot!");
                return true;
            }
        } else if (args.length > 1) {
            try {
                int lotnr = Integer.parseInt(args[0]);
                startarg = 1;
                lot = LotHandler.getLot(lotnr);
            } catch (NumberFormatException ex) {
                lot = LotHandler.getLot(player.getLocation());
            }
            if (lot == null) {
                return false;
            }
            sb = new StringBuilder(args[startarg]);
            for (int i = startarg + 1; i < args.length; i++) {
                sb.append(" ").append(args[i]);
            }
            message = sb.toString();
        }

        boolean makeBlank = true;
        message = message.trim(); // don't allow blank or whitespaced message

        if (!message.isEmpty()) {
            makeBlank = false;
            if (message.length() >= 5 && message.substring(0, 5).equalsIgnoreCase("[idp]")) { //trim [IDP] from message
                message = message.substring(5);
            }
        }

        if (message.length() > 200) {
            player.printError("Message size is too big!");
            return true;
        }

        if (lot.canPlayerManage(player.getName())
                || player.hasPermission(Permission.lot_command_override)) {
            lot.setEnterMsg(message);
            player.printInfo("Welcome message " + (makeBlank ? "cleared" : "updated") + " for lot #" + lot.getId());
        } else {
            player.printError("This is not your lot!");
        }

        return true;
    }

    @CommandMethod(aliases = {"thislot"},
    description = "Displays information about the lot you are in.",
    permission = Permission.command_lot_thislot,
    usage = "/thislot",
    usage_Mod = "/thislot [-showhidden, -sh]",
    serverCommand = false)
    public static void commandThisLot(IdpPlayer player, LynxyArguments args) {
        boolean showHidden = (args.hasOption("showhidden", "sh") && player.hasPermission(Permission.command_thislot_showhidden));

        List<InnectisLot> lots = LotHandler.getLots(player.getLocation(), showHidden);

        if (lots == null) {
            player.printError("This is not a lot!");
            return;
        }

        player.getSession().setLastLotIDQueried(lots.get(0).getId());
        commandLotInfo_printLotDetails(player, lots);
    }

    @CommandMethod(aliases = {"thatlot"},
    description = "Displays information about the lot you are looking at.",
    permission = Permission.command_lot_thatlot,
    usage = "/thatlot",
    usage_Mod = "/thatlot [-showhidden, -sh]",
    serverCommand = false)
    public static void commandThatLot(IdpPlayer player, LynxyArguments args) {
        boolean showHidden = (args.hasOption("showhidden", "sh") && player.hasPermission(Permission.command_thatlot_showhidden));

        Block block = player.getTargetBlock(5);
        List<InnectisLot> lots = LotHandler.getLots(block.getLocation(), showHidden);

        if (lots == null) {
            player.printError("That is not a lot!");
            return;
        }

        player.getSession().setLastLotIDQueried(lots.get(0).getId());

        commandLotInfo_printLotDetails(player, lots);
    }

    @CommandMethod(aliases = {"setlotname"},
    description = "Sets a lot's name.",
    permission = Permission.command_lot_setlotname,
    usage = "/setlotname [id] [name]",
    serverCommand = false)
    @SuppressWarnings("fallthrough")
    public static boolean commandSetLotName(InnPlugin parent, IdpCommandSender sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        InnectisLot lot = null;
        String targetName = "";
        switch (args.length) {
            case 0:
                lot = LotHandler.getLot(player.getLocation());
                break;
            case 1:
                lot = LotHandler.getLot(player.getLocation());
            case 2:
                try {
                    if (lot == null) {
                        lot = LotHandler.getLot(Integer.parseInt(args[0]));
                        if (lot == null) {
                            player.printError("No lot with that ID was found!");
                            return true;
                        }
                    }
                    targetName = args[args.length - 1];
                } catch (NumberFormatException nfe) {
                    player.printError("Invalid lot ID!");
                }
                break;
            default:
                return false;
        }

        if (lot == null) {
            player.printError("This command must be used on a lot.");
            return true;
        }

        if (lot.canPlayerManage(player.getName())
                || player.hasPermission(Permission.lot_command_override)) {
            if (targetName.length() > 20 || targetName.contains(" ")) {
                player.printError("The lot name specified is invalid!");
                return true;
            }

            if (lot.setLotName(targetName)) {
                lot.resetWarpTimesUsed();
                lot.save();
                player.printInfo("Lot #" + lot.getId() + "'s name set to: " + targetName);
            } else {
                player.printError("An error has occured, notify an admin!");
            }
        } else {
            player.printError("This is not your lot!");
        }

        return true;
    }

}

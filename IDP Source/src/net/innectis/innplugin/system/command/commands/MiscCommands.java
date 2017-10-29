package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.handlers.OwnedPetHandler;
import net.innectis.innplugin.handlers.TrashHandler;
import net.innectis.innplugin.handlers.CTFHandler;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.MapPictureBuilder;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.EditSignWand;
import net.innectis.innplugin.objects.PresentContent;
import net.innectis.innplugin.objects.IdpEntityType;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.objects.OwnedPets;
import net.innectis.innplugin.objects.CreateCTFArenaObj;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.IdpWorldType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.ValutaSinkManager;
import net.innectis.innplugin.system.game.IdpGame;
import net.innectis.innplugin.system.game.IdpGameManager;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.inventory.payload.PresentInventoryPayload;
import net.innectis.innplugin.inventory.payload.TrashAddItemsPayload;
import net.innectis.innplugin.inventory.payload.TrashViewItemsPayload;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.customeffects.EncasePlayer;
import net.innectis.innplugin.location.customeffects.FireworkShow;
import net.innectis.innplugin.location.customeffects.MeteorStrike;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler.VanillaChestType;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisOwnedObject;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.NotANumberException;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.windows.TrashWindow;
import org.bukkit.Art;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftTameableAnimal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MinecraftFont;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MiscCommands {

    private MiscCommands() {
    }

    @CommandMethod(aliases = {"findObject", "find"},
    description = "Lists all owned objects of a certain type you own.",
    permission = Permission.command_misc_findobject,
    usage = "/findObject <-chests> <-waypoints> <-doors> <-trapdoors> <-bookcases> <-switches>",
    usage_Admin = "/findObject <-username (-u) [username]> <-chests> <-waypoints> <-doors> <-trapdoors> <-bookcases> <-switches>",
    serverCommand = false)
    public static void commandFindObject(InnPlugin plugin, IdpPlayer player, LynxyArguments args) {
        List<InnectisOwnedObject> objectList = new ArrayList<InnectisOwnedObject>();
        String playerName = player.getName();

        boolean extendedInfo = player.hasPermission(Permission.command_misc_findobject_extended);

        if (extendedInfo && args.hasArgument("username", "u")) {
            playerName = (args.getString("username", "u"));
        }

        if (args.hasOption("chests", "chest")) {
            objectList.addAll(ChestHandler.getChests(playerName));
        }
        if (args.hasOption("waypoints", "waypoint")) {
            objectList.addAll(WaypointHandler.getWaypoints(playerName));
        }
        if (args.hasOption("doors", "door")) {
            objectList.addAll(DoorHandler.getDoors(playerName));
        }
        if (args.hasOption("trapdoors", "trapdoor")) {
            objectList.addAll(TrapdoorHandler.getTrapdoors(playerName));
        }
        if (args.hasOption("bookcases", "bookcase", "book")) {
            objectList.addAll(InnectisBookcase.getBookcases(playerName));
        }
        if (args.hasOption("switches", "switch", "levers", "lever")) {
            objectList.addAll(InnectisSwitch.getSwitches(playerName));
        }

        if (objectList.isEmpty()) {
            player.printError("No objects found!");
        } else {
            for (InnectisOwnedObject object : objectList) {
                player.printInfo(object.getType().getName() + " #" + object.getId() +
                        (extendedInfo ? " (" + playerName + ") " : " ")
                        + object.getPos1Location().toString());
            }
        }
    }

    @CommandMethod(aliases = {"autoharvest", "harvest", "tractor"},
    description = "This will automatically plant and harvest crops as you walk for 30 seconds.",
    permission = Permission.command_misc_autoharvest,
    usage = "/autoharvest",
    serverCommand = false)
    public static void commandAutoHarvest(InnPlugin plugin, final IdpPlayer player) {
        if (player.getSession().isTractorRunning()) {
            player.printError("Your tractor is already running!");
        } else {
            TransactionObject transaction = TransactionHandler.getTransactionObject(player);
            int valutas = transaction.getValue(TransactionType.VALUTAS);

            if (valutas >= 100) {
                transaction.subtractValue(100, TransactionType.VALUTAS);
                player.getSession().setTractorRunning(true);
                player.printInfo("You start up your tractor! (100 valutas)");

                plugin.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 30000, 1) {
                    @Override
                    public void run() {
                        player.getSession().setTractorRunning(false);
                        player.printError("Your tractor has run out of fuel!");
                    }
                });

                ValutaSinkManager.addToSink(100);
            } else {
                player.printError("You cannot afford to fuel your tractor! (100 vT)");
            }
        }
    }

    @CommandMethod(aliases = {"sortchest", "sort", "orderchest", "order"},
    description = "This will automatically sort the chest you are looking at according to ID and Amount.",
    permission = Permission.command_misc_sortchest,
    usage = "/sortchest",
    serverCommand = false)
    public static void commandSortChest(IdpPlayer player) {
        Block block = player.getTargetOwnedBlock();

        if (block != null && IdpMaterial.fromBlock(block).isChest()) {
            InnectisChest chest = ChestHandler.getChest(block.getLocation());

            if (chest == null) {
                player.printError("This is not a valid chest. Contact an admin!");
                return;
            }

            if (chest.canPlayerAccess(player.getName())) {
                chest.sortContents();

                player.printInfo("You have sorted chest #" + chest.getId());
            } else {
                player.printError("You cannot access this chest to sort it!");
            }
        } else {
            player.printError("You are not looking at a chest!");
        }
    }

    @CommandMethod(aliases = {"bleach"},
    description = "This turns the wool, carpet, or clay in your hand to white (or hard clay), or your entire inventory if you specify -all.",
    permission = Permission.command_misc_bleach,
    usage = "/bleach [-wool, -w] [-carpet, -c] [-clay] [-all]",
    serverCommand = false)
    public static boolean commandBleach(IdpPlayer player, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        boolean bleachWool = args.hasOption("wool", "w", "all");
        boolean bleachCarpet = args.hasOption("carpet", "c", "all");
        boolean bleachClay = args.hasOption("clay", "all");

        List<IdpMaterial> bleachMaterial = new ArrayList<IdpMaterial>();

        if (bleachWool) {
            bleachMaterial.add(IdpMaterial.WOOL_ALL);
        }

        if (bleachCarpet) {
            bleachMaterial.add(IdpMaterial.CARPET_ALL);
        }

        if (bleachClay) {
            bleachMaterial.add(IdpMaterial.CLAY_ALL);
        }

        if (bleachMaterial.size() > 0) {
            for (IdpMaterial mat : bleachMaterial) {
                int count = player.getInventoryItemCount(mat);

                if (count > 0) {
                    player.removeItemFromInventory(mat, count);

                    IdpItemStack addStack = null;

                    if (mat == IdpMaterial.CLAY_ALL) {
                        addStack = new IdpItemStack(IdpMaterial.CLAY_HARD, count);
                    } else {
                        addStack = new IdpItemStack(mat, count, 0);
                    }

                    player.addItemToInventory(addStack);

                    player.printInfo("You have bleached all your " + mat.getName() + "!");
                } else {
                    player.printError("You don't have any " + mat.getName() + " on you!");
                }
            }
        } else {
            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You don't have any items in hand!");
                return true;
            }

            IdpItemStack handStack = player.getItemInHand(handSlot);
            IdpMaterial mat = handStack.getMaterial();

            if (mat == IdpMaterial.WOOL_WHITE) {
                player.printError("This wool is already bleached!");
                return true;
            }

            if (mat == IdpMaterial.CARPET_WHITE) {
                player.printError("This carpet is already bleached!");
                return true;
            }

            if (mat == IdpMaterial.CLAY_HARD) {
                player.printError("This clay is already bleached!");
                return true;
            }

            if (!mat.isWool() && !mat.isCarpet() && !mat.isClay()) {
                player.printError("Item in hand is not wool, carpet, or clay. Cannot bleach!");
                return true;
            }

            if (mat.isClay()) {
                int previousCount = handStack.getAmount();
                handStack = new IdpItemStack(IdpMaterial.CLAY_HARD, previousCount);
            } else {
                handStack.setData(0);
            }

            player.setItemInHand(handSlot, handStack);

            player.printInfo("Successfully bleached the " + mat.getName() + " in your hand!");
        }

        return true;
    }

    @CommandMethod(aliases = {"present", "createpresent"},
    description = "Creates a present from the item in your hand.",
    permission = Permission.command_misc_present,
    usage = "/present [title]",
    disabledWorlds = {IdpWorldType.NETHER},
    serverCommand = false)
    public static boolean commandPresent(IdpPlayer player, ParameterArguments args) {

        if (args.checkArgumentAmount(0, 1)) {
            String title = args.getString(0);

            // Check if there is a name
            if (StringUtil.stringIsNullOrEmpty(title)) {
                title = "" + ChatColor.AQUA + ChatColor.EFFECT_ITALIC + "Present";
            }

            // Parse colours
            title = ChatColor.parseChatColor(title);

            // Check length (after colour parsing)
            if (title.length() > 30) {
                player.printError("Title too long.");
                return true;
            }

            // Take the needed item from the inventory
            if (!player.removeItemFromInventory(new IdpItemStack(IdpMaterial.REDSTONE_LAMP_OFF, 1))) {
                player.printError("You need a package to wrap your items.");
                return true;
            }

            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(player.getUniqueId());

            // Make a present
            PresentContent present = new PresentContent(credentials, title);

            // Make the inventory, replace spaces with underscores
            IdpInventory inventory = new IdpInventory(title.replace(" ", "_"), 9);
            inventory.setPayload(new PresentInventoryPayload(present));

            // Open
            player.openInventory(inventory);

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"craft"},
    description = "Pulls up a mobile crafting grid.",
    permission = Permission.command_misc_craft,
    usage = "/craft",
    serverCommand = false)
    public static boolean commandCraft(IdpPlayer player) {
        player.getHandle().openWorkbench(player.getLocation(), true);
        player.printInfo("You open your portable workbench!");
        return true;
    }

    @CommandMethod(aliases = {"emptybucket", "empty", "eb"},
    description = "Empties the bucket in your hand.",
    permission = Permission.command_misc_empty_bucket,
    usage = "/emptybucket",
    serverCommand = false)
    public static boolean commandEmptyBucket(IdpPlayer player) {
        EquipmentSlot handSlot = player.getNonEmptyHand();

        if (handSlot == null) {
            player.printError("You must be holding an item.");
            return true;
        }

        IdpMaterial handItem = player.getMaterialInHand(handSlot);

        if (handItem == IdpMaterial.LAVA_BUCKET
                || handItem == IdpMaterial.WATER_BUCKET
                || handItem == IdpMaterial.MILK_BUCKET) {
            player.setItemInHand(handSlot, new IdpItemStack(IdpMaterial.BUCKET, 1));
            player.printInfo("You empty your bucket.");
        } else if (handItem == IdpMaterial.BUCKET) {
            player.printError("This Bucket is already empty!");
        } else {
            player.printError("You need to be holding a full bucket!");
        }

        return true;
    }

    @CommandMethod(aliases = {"createctfarena", "cca"},
    description = "Creates a CTF arena.",
    permission = Permission.command_lot_createctfarena,
    usage = "/createctfarena [-stop]",
    serverCommand = false)
    public static boolean commandCtf(IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("stop")) {
            CreateCTFArenaObj obj = CTFHandler.getCreateGameMode(player.getName());

            if (obj != null) {
                CTFHandler.endCreateGameMode(player.getName());
                player.printInfo("CTF creation halted!");

                try {
                    LotHandler.removeLot(obj.returnLot(), true);
                } catch (SQLException ex) {
                    InnPlugin.logError("Could not remove lot!", ex);
                }
                player.printInfo("Partial CTF lot removed!");
            } else {
                player.printError("CTF creation isn't taking place on this lot!");
            }
        } else {
            InnectisLot lot = LotHandler.getLot(player.getLocation());
            Location loc = player.getLocation();

            if (lot == null) {
                player.printError("This command must be used on a lot!");
                return true;
            }

            IdpGame game = IdpGameManager.getInstance().getGame(loc);
            lot = lot.getParentTop();

            if (game != null) {
                player.printError("A game is already in progress on this lot!");
                return true;
            }

            if (!lot.getOwner().equalsIgnoreCase(player.getName())
                    && !player.hasPermission(Permission.special_ctf_createarenaanywhere)) {
                player.printError("You may not create a CTF arena on this lot!");
                return true;
            }

            if (CTFHandler.isCTFArena(lot)) {
                player.printError("There is a CTF arena already here.");
                return true;
            }

            loc.setY(loc.getBlockY() - 1);
            IdpWorldRegion startRegion = new IdpWorldRegion(player.getWorld().getHandle(), loc);
            player.setRegion(startRegion);

            boolean result = CTFHandler.setCreateGameMode(player.getName(), lot, startRegion);

            if (result) {
                player.printInfo("First, make team red's base.");
            } else {
                player.printError("You or someone else is trying to create CTF here!");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"unsitpets"},
    description = "Unsits any pets you may have.",
    permission = Permission.command_misc_unsittamed,
    usage = "/unsitpets",
    serverCommand = false)
    public static boolean commandUnsitPets(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        OwnedPets pets = OwnedPetHandler.getPets(sender.getName());

        if (pets.petCount() == 0) {
            boolean hasTraits = (pets.traitSize() > 0);

            if (hasTraits) {
                sender.printError("Your pets won't obey!");
            } else {
                sender.printError("You have no pets!");
            }

            return true;
        }

        IdpPlayer player = (IdpPlayer) sender;

        for (LivingEntity pet : pets.getPets()) {
            if (pet instanceof CraftTameableAnimal) {
                CraftTameableAnimal tameable = (CraftTameableAnimal) pet;
                tameable.setSitting(false);
            }
        }

        player.printInfo("Your pets have been unsat!");

        return true;
    }

    @CommandMethod(aliases = {"editsign", "es"},
    description = "Edits the sign you're looking at.",
    permission = Permission.command_misc_signedit,
    usage = "/editsign [-shift, -s <amount> <up/down>] OR <line number> <new text> OR <-clear, -c>",
    serverCommand = false)
    public static boolean commandEditSign(IdpPlayer player, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }

        Block block = player.getTargetBlock(5);

        if (block == null || !(block.getState() instanceof Sign)) {
            player.printError("You must be looking at a sign to use this command.");
            return true;
        }

        InnectisLot lot = LotHandler.getLot(block.getLocation());

        if (lot != null && !lot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.special_signedit)) {
            player.printError("You cannot edit this sign.");
            return true;
        }

        int lineNo = 0;

        try {
            lineNo = Integer.parseInt(args.getString(0));
        } catch (NumberFormatException ex) {
            player.printError("Line number is not expressed as a number.");
            return true;
        }

        if (lineNo > 4 || lineNo < 1) {
            player.printError("Line number must be between 1-4.");
            return true;
        }

        String signText = "";

        if (!args.hasOption("clear", "c")) {
            if (args.getActionSize() < 2) {
                return false;
            }

            for (int i = 1; i < args.getActionSize(); i++) {
                signText += args.getString(i) + " ";
            }

            signText = ChatColor.convertLongColors(signText).trim();
            MinecraftFont font = MinecraftFont.Font;
            int pixels = font.getWidth(signText);

            if (pixels > 89) {
                player.printError("Line cannot be longer than 89 pixels. (You have input " + pixels + " pixels)");
                return true;
            }

            signText = ChatColor.parseSignColor(signText, player);
        }

        Sign sign = (Sign) block.getState();
        Location loc = sign.getLocation();
        loc.subtract(0, 1, 0); // Get location under
        Block chestBlock = loc.getBlock();

        if (VanillaChestType.isValidChestBlock(IdpMaterial.fromBlock(chestBlock))) {
            InnectisChest ichest = ChestHandler.getChest(loc);

            if (ichest != null && !(ichest.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.special_chestshop_override))) {
                player.printError("Cannot edit this sign. You do not own or operate the chest below.");
                return true;
            }
        }

        sign.setLine(lineNo - 1, signText);
        sign.update();

        player.printInfo((!signText.isEmpty() ? "Updated " : "Cleared ") + "line " + lineNo + " of this sign!");

        return true;
    }

    @CommandMethod(aliases = {"clearchat"},
    description = "This sends a lot of empty messages to the sender, clearing the chat screen.",
    permission = Permission.command_misc_clearchat,
    usage = "/clearchat",
    serverCommand = true)
    public static boolean commandClearChat(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        for (int i = 0; i < 24; i++) {
            sender.printRaw(" ");
        }
        return true;
    }

    @CommandMethod(aliases = {"allow", "grant"},
    description = "Allows one or multiple players to the object you're looking at.",
    permission = Permission.command_misc_allow,
    usage = "/allow [lotid] <username[,username2,...]>",
    serverCommand = false)
    public static boolean commandAllow(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;
        Block block = player.getTargetOwnedBlock();
        InnectisOwnedObject innObj = null;

        if (args.length == 1) {
            if (block != null) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                Location loc = block.getLocation();

                switch (mat) {
                    case IRON_DOOR_BLOCK:
                        innObj = DoorHandler.getDoor(loc);
                        break;
                    case CHEST:
                    case TRAPPED_CHEST:
                        innObj = ChestHandler.getChest(loc);
                        break;
                    case BOOKCASE:
                        innObj = InnectisBookcase.getBookcase(loc);
                        break;
                    case LAPIS_LAZULI_OREBLOCK:
                        innObj = WaypointHandler.getWaypoint(loc);
                        break;
                    case IRON_TRAP_DOOR:
                        innObj = TrapdoorHandler.getTrapdoor(loc);
                        break;
                }
            }

            if (innObj == null) {
                innObj = LotHandler.getLot(player.getLocation());
            }

            if (innObj == null) {
                player.printError("You cannot use /allow here!");
                return true;
            }
        } else {
            try {
                innObj = LotHandler.getLot(Integer.parseInt(args[0]));
            } catch (NumberFormatException ex) {
                player.printError("Invalid lot ID!");
                return true;
            }
            if (innObj == null) {
                player.printError("No lots with that ID were found!");
                return true;
            }
        }

        String[] targetNames = args[args.length - 1].split(",");
        String typeName = innObj.getType().getName();
        String saveMsg = null;
        PlayerCredentials credentials = null;
        boolean saveCacheCredentials = true;
        boolean save = false;

        if (innObj.canPlayerManage(player.getName()) || player.hasPermission(Permission.owned_object_override)) {
            for (String name : targetNames) {
                if (!innObj.isValidPlayer(name)) {
                    player.printError("Unknown player: \"" + name + "\"");
                    player.printError("Please check your spelling.");
                } else if (name.startsWith("!")) {
                    name = name.substring(1);

                    if (innObj.getOwner().equalsIgnoreCase(player.getName())
                            || player.hasPermission(Permission.lot_command_override)) {
                        credentials = PlayerCredentialsManager.getByName(name);

                        if (innObj.addOperator(credentials)) {
                            save = true;
                            saveMsg = "You gave " + name + " operating access to " + typeName + " #" + innObj.getId() + ".";
                        } else {
                            player.printError(name + " is already added to this " + typeName + "!");
                        }
                    } else {
                        player.printError("You cannot add operators to this " + typeName + "!");
                    }
                } else {
                    if (name.equals("%")) {
                        credentials = Configuration.EVERYONE_CREDENTIALS;
                        saveCacheCredentials = false;
                    } else if (name.equals("@")) {
                        if (innObj instanceof InnectisLot) {
                            player.printError("You cannot allow @ on lots!");
                            return true;
                        }

                        credentials = Configuration.LOT_ACCESS_CREDENTIALS;
                        saveCacheCredentials = false;
                    } else {
                        credentials = PlayerCredentialsManager.getByName(name);
                    }

                    if (innObj.addMember(credentials)) {
                        save = true;
                        saveMsg = "You gave " + name + " access to " + typeName + " #" + innObj.getId() + ".";
                    } else {
                        player.printError(name + " is already added to this " + typeName + "!");
                    }
                }
            }

            if (save) {
                if (saveCacheCredentials) {
                    PlayerCredentialsManager.addCredentialsToCache(credentials);
                }

                if (innObj.save()) {
                    player.printInfo(saveMsg);
                } else {
                    player.printError("Unable to save lot! Notify an admin!");
                }
            }
        } else {
            player.printError("You are not the owner of that " + typeName + "!");
        }

        return true;
    }

    @CommandMethod(aliases = {"editsignwand", "esw"},
    description = "Manages a player's edit sign wand.",
    permission = Permission.command_misc_editsignwand,
    usage = "/editsignwand [-blank, -b [line]] [-clear, -c [line]] [-view, -v] <text>",
    serverCommand = false)
    public static boolean commandEditSignWand(Server server, InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        EditSignWand wand = player.getSession().getEditSignWand();

        if (args.hasOption("blank", "b") || args.hasArgument("blank", "b")) {
            if (args.hasArgument("blank", "b")) {
                int lineNo = 0;

                try {
                    lineNo = Integer.parseInt(args.getString("blank", "b"));
                } catch (NumberFormatException nfe) {
                    player.printError("Line number is not a number.");
                    return true;
                }

                if (lineNo < 1 || lineNo > 4) {
                    player.printError("Line must be 1 through 4.");
                    return true;
                }

                String line = wand.getLine(lineNo - 1);

                if (line != null && line.equals("")) {
                    player.printError("Line " + lineNo + " is already blank!");
                    return true;
                }

                wand.setLine(lineNo - 1, "");
                player.printInfo("Line " + lineNo + " has been made blank!");
            } else {
                for (int i = 0; i < 4; i++) {
                    wand.setLine(i, "");
                }

                player.printInfo("All lines of the edit sign wand have been made blank!");
            }

            return true;
        } else if (args.hasOption("clear", "c") || args.hasArgument("clear", "c")) {
            if (wand.isEmpty()) {
                player.printError("Your edit sign wand is empty. Cannot clear!");
                return true;
            }

            if (args.hasArgument("clear", "c")) {
                int lineNo = 0;

                try {
                    lineNo = Integer.parseInt(args.getString("clear", "c"));
                } catch (NumberFormatException nfe) {
                    player.printError("Line argument is not a number.");
                    return true;
                }

                if (lineNo < 1 || lineNo > 4) {
                    player.printError("Line must be 1 through 4.");
                    return true;
                }

                String line = wand.getLine(lineNo);

                if (line == null) {
                    player.printError("This line is already cleared.");
                    return true;
                }

                wand.setLine(lineNo, null);
                player.printInfo("Cleared line " + lineNo + " of your edit sign wand.");
            } else {
                player.getSession().clearEditSignWand();
                player.printInfo("Your edit sign wand has been cleared!");
            }

            return true;
        } else if (args.hasOption("view", "v")) {
            if (wand.size() > 0) {
                player.printInfo("Current edit wand settings:");
                player.printInfo("");

                for (int i = 0; i < 4; i++) {
                    String status = "";
                    String line = wand.getLine(i);

                    if (line == null) {
                        status = ChatColor.RED + "empty";
                    } else if (line.equals("")) {
                        status = ChatColor.YELLOW + "blank";
                    } else {
                        status = line;
                    }

                    player.printInfo("Line " + ChatColor.AQUA + (i + 1), ": " + status);
                }
            } else {
                player.printError("Your wand is not set yet.");
            }

            return true;
        } else if (args.getActionSize() > 1) {
            int lineNo = 0;
            String line = "";

            try {
                lineNo = Integer.parseInt(args.getString(0));
            } catch (NumberFormatException ex) {
                player.printError("Line argument is not a number.");
                return true;
            }

            if (lineNo < 1 || lineNo > 4) {
                player.printError("Line number must be between 1 and 4.");
                return true;
            }

            for (int i = 1; i < args.getActionSize(); i++) {
                line += args.getString(i) + " ";
            }
            line = ChatColor.convertLongColors(line).trim();

            MinecraftFont font = MinecraftFont.Font;
            int pixels = font.getWidth(line);

            if (pixels > 89) {
                player.printError("Line cannot be longer than 89 pixels. (You have input " + pixels + " pixels)");
                return true;
            }

            wand.setLine(lineNo - 1, line);

            player.printInfo("Set line " + lineNo + " of the edit sign wand.");
            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"deny", "unallow", "disallow", "revoke"},
    description = "Denies one or multiple players to the object you're looking at.",
    permission = Permission.command_misc_deny,
    usage = "/deny [lotid] <-all> OR <username[,username2,...]>",
    serverCommand = false)
    public static boolean commandDeny(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;
        Location loc = player.getLocation();
        Block block = player.getTargetOwnedBlock();
        InnectisOwnedObject innObj = null;

        if (args.length == 1) {
            if (block != null) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                loc = block.getLocation();

                switch (mat) {
                    case IRON_DOOR_BLOCK:
                        innObj = DoorHandler.getDoor(loc);
                        break;
                    case CHEST:
                    case TRAPPED_CHEST:
                        innObj = ChestHandler.getChest(loc);
                        break;
                    case BOOKCASE:
                        innObj = InnectisBookcase.getBookcase(loc);
                        break;
                    case LAPIS_LAZULI_OREBLOCK:
                        innObj = WaypointHandler.getWaypoint(loc);
                        break;
                    case IRON_TRAP_DOOR:
                        innObj = TrapdoorHandler.getTrapdoor(loc);
                        break;
                }
            }

            if (innObj == null) {
                innObj = LotHandler.getLot(loc);
            }

            if (innObj == null) {
                player.printError("Cannot use /deny here!");
                return true;
            }
        } else {
            try {
                innObj = LotHandler.getLot(Integer.parseInt(args[0]));
            } catch (NumberFormatException ex) {
                player.printError("Invalid lot ID!");
                return true;
            }
            if (innObj == null) {
                player.printError("No lots with that ID were found!");
                return true;
            }
        }

        String typeName = innObj.getType().getName();
        String saveMsg = null;
        boolean save = false;

        if (innObj.canPlayerManage(player.getName()) || player.hasPermission(Permission.owned_object_override)) {
            if (args[args.length - 1].equalsIgnoreCase("-all")) {
                if (!(innObj.getMembers().isEmpty() && innObj.getOperators().isEmpty())) {
                    innObj.clearMembersAndOperators();
                    save = true;
                    saveMsg = "Cleared all members and operators from this " + typeName + ".";
                } else {
                    player.printError("This " + typeName + " has no members or operators!");
                    return true;
                }
            } else {
                String[] targetNames = args[args.length - 1].split(",");

                for (String name : targetNames) {
                    // If starting with "!" remove operating access.
                    if (name.startsWith("!")) {
                        name = name.substring(1);

                        if (innObj.getOwner().equalsIgnoreCase(player.getName())
                                || player.hasPermission(Permission.lot_command_override)) {
                            if (innObj.removeOperator(name)) {
                                PlayerCredentials credentials = PlayerCredentialsManager.getByName(name, true);
                                // Add the name back as player
                                innObj.addMember(credentials);
                                save = true;
                                saveMsg = "You removed " + name + " operating access to " + typeName + " #" + innObj.getId() + ".";
                            } else {
                                player.printError(name + " has no operating access to this " + typeName + "!");
                            }
                        } else {
                            player.printError("You cannot remove operators from this " + typeName + "!");
                        }
                    } else {
                        // If normal name, remove all access
                        if (innObj.removeMember(name)) {
                            save = true;
                            saveMsg = "You removed " + name + " access to " + typeName + " #" + innObj.getId() + ".";
                        } else {
                            if (innObj.containsOperator(name)) {
                                if (innObj.getOwner().equalsIgnoreCase(player.getName()) && innObj.removeOperator(name)) {
                                    save = true;
                                    saveMsg = "You removed " + name + " access to " + typeName + " #" + innObj.getId() + ".";
                                } else {
                                    player.printError("You cannot remove operators from this " + typeName + "!");
                                }
                            } else {
                                player.printError(name + " is not added to this " + typeName + "!");
                            }
                        }
                    }
                }
            }

            if (save) {
                if (innObj.save()) {
                    player.printInfo(saveMsg);
                } else {
                    player.printError("Unable to save lot. Notify an admin!");
                }
            }
        } else {
            player.printError("You are not the owner of that " + typeName + "!");
        }

        return true;
    }

    @CommandMethod(aliases = {"jump"},
    description = "This shoots the player into the air.",
    permission = Permission.command_misc_jump,
    usage = "/jump [username]",
    serverCommand = false)
    public static boolean commandJump(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        int height = 10;
        Player jumpplayer = (Player) sender.getHandle();
        if (args.length == 1) {
            IdpPlayer tmpPlayer = parent.getPlayer(args[0], false);
            if (tmpPlayer != null) {
                jumpplayer = tmpPlayer.getHandle();
            }
            tmpPlayer = null;
        }
        if (jumpplayer == null) {
            sender.printError("Player not found!");
            return true;
        }
        IdpPlayer player = parent.getPlayer(jumpplayer);
        if (jumpplayer.isInsideVehicle()) {
            Vector speed = jumpplayer.getVehicle().getVelocity();
            speed.setY(height);
            jumpplayer.getVehicle().setVelocity(speed);
        } else {
            Vector speed = jumpplayer.getVelocity();
            speed.setY(height);
            jumpplayer.setVelocity(speed);
        }
        player.getSession().setJumped(true);

        return true;
    }

    @CommandMethod(aliases = {"kill"},
    description = "Kills any entity in the specified range. Protect lots with -protectlots.",
    permission = Permission.command_misc_kill,
    usage = "/kill [entity type] [-player, -p <player>] [-leave, -l <amount>] [-protectlots, -pl] [-allowtamed, -at] [-range, -r <range>] [-selection, -sel] [-l, -lot [ID]]",
    serverCommand = false)
    public static boolean commandKill(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        Class<? extends Entity> filter = Monster.class;
        String entityName = IdpEntityType.MONSTER.getName();

        if (args.getActionSize() > 0) {
            String nameArg = args.getString(0);
            EntityType bukkitEntityType = EntityType.fromName(nameArg);

            if (bukkitEntityType == null) {
                // Entity doesn't exist by that name in bukkit's enum, so lets try IDP's
                IdpEntityType idpEntityType = IdpEntityType.lookup(nameArg);

                if (idpEntityType == IdpEntityType.NONE) {
                    player.printError("Invalid entity specified.");
                    return true;
                } else {
                    filter = idpEntityType.getBukkitClass();
                    entityName = idpEntityType.getName();
                }
            } else {
                filter = bukkitEntityType.getEntityClass();
                entityName = bukkitEntityType.getName();
            }
        }

        boolean allowTamed = args.hasOption("allowtamed", "at");
        boolean protectLots = args.hasOption("protectlots", "pl");

        int leaveAmount = 0;

        if (args.hasArgument("leave", "l")) {
            try {
                leaveAmount = Integer.parseInt(args.getString("leave", "l"));

                if (leaveAmount < 1) {
                    player.printError("Leave amount must be greater than 0.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Leave amount is not a number.");
                return true;
            }
        }

        if (args.hasArgument("player", "p")) {
            IdpPlayer testPlayer = parent.getPlayer(args.getString("player", "p"));

            if (testPlayer != null) {
                testPlayer.setHealth(0.0D);
                player.printInfo("You have killed " + testPlayer.getColoredDisplayName(), "!");
            } else {
                player.printError("That player is not online.");
            }

            return true;
        } else if (args.hasArgument("lot", "l") || args.hasOption("lot", "l")
                || args.hasOption("selection", "sel")) {
            IdpWorldRegion region = null;
            boolean useLot = false;

            if (args.hasArgument("lot", "l") || args.hasOption("lot", "l")) {
                InnectisLot lot = LotHandler.getLot(player.getLocation());

                if (args.hasArgument("lot", "l")) {
                    try {
                        int id = Integer.parseInt(args.getString("l", "lot"));
                        lot = LotHandler.getLot(id);
                    } catch (NumberFormatException nfe) {
                        player.printError("Lot ID is not a number.");
                        return true;
                    }
                }

                if (lot == null) {
                    player.printError("Lot not found!");
                    return true;
                }

                region = lot;
                useLot = true;
            } else {
                region = player.getRegion();

                if (region == null) {
                    player.printError("Region not found!");
                    return true;
                }
            }

            List<Entity> entities = region.getEntities(filter, allowTamed, leaveAmount);

            if (entities.size() > 0) {
                for (Entity entity : entities) {
                    entity.remove();
                }

                if (useLot) {
                    InnectisLot lot = (InnectisLot) region;
                    player.printInfo("Removed " + entities.size() + " entities of type " + entityName + " from lot #" + lot.getId() + ".");
                } else {
                    player.printInfo("Removed " + entities.size() + " entities of type " + entityName + " from your selection.");
                }
            } else {
                if (useLot) {
                    InnectisLot lot = (InnectisLot) region;
                    player.printError("Could not remove any entities of type " + entityName + " from lot #" + lot.getId() + ".");
                } else {
                    player.printError("Could not remove any entities of type " + entityName + " from your selection.");
                }
            }

            return true;
        }

        int range = 50;

        if (args.hasArgument("range", "r")) {
            try {
                range = Integer.parseInt(args.getString("range", "r"));

                if (range < 1) {
                    player.printError("Range cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Range is not a number.");
                return true;
            }
        }

        List<Entity> entities = player.getNearbyEntities(range, filter, protectLots, allowTamed, leaveAmount);

        if (entities.size() > 0) {
            for (Entity ent : entities) {
                ent.remove();
            }

            player.print(ChatColor.DARK_GREEN, "Removed " + entities.size() + " entities of type "  + entityName + " in a " + range + " block radius.");
        } else {
            player.printError("Could not remove any entities of type " + entityName + " in a " + range + " block radius.");
        }

        return true;
    }

    @CommandMethod(aliases = {"setdata"},
    description = "Sets the data of the block you are looking at.",
    permission = Permission.command_misc_setdata,
    usage = "/setdata <byte>",
    serverCommand = false)
    public static boolean commandSetData(IdpPlayer player, String[] args) {
        if (args.length != 1) {
            return false;
        }

        byte data = 0;

        try {
            data = Byte.parseByte(args[0]);
        } catch (NumberFormatException nfe) {
            player.printError("Invalid data!");
            return true;
        }

        Block block = player.getTargetBlock(5);

        if (block == null) {
            player.printError("You must look at a block to change its data!");
            return true;
        }

        boolean invalid = false;

        try {
            BlockHandler.setBlockData(block, data);

            // Double check the data was set properly
            if (BlockHandler.getBlockData(block) != data) {
                invalid = true;
            }
        } catch (IllegalArgumentException iae) {
            invalid = true;
        }

        if (invalid) {
            player.printError("Data value specified is invalid for this block.");
        } else {
            Location loc = block.getLocation();
            player.printInfo("Block (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") data changed to " + data + "!");
        }

        return true;
    }

    @CommandMethod(aliases = {"sit"},
    description = "This let the player sit on another player.",
    permission = Permission.command_misc_sit,
    usage = "/sit <playername>",
    serverCommand = false)
    public static boolean commandSit(Server server, InnPlugin parent, IdpPlayer player, String[] args) {
        try {
            if (args.length == 0) {
                try {
                    player.getHandle().leaveVehicle();
                    player.printInfo("You jumped off!");
                } catch (Exception ex) {
                    InnPlugin.logError("Exception sit " + player.getColoredName(), ex);
                }
            } else if (args.length == 1) {
                IdpPlayer tarplayer = parent.getPlayer(args[0], false);
                if (tarplayer != null) {
                    if (player.getName().equalsIgnoreCase(tarplayer.getName())) {
                        player.printError("You cannot sit on yourself!");
                        return true;
                    }

                    player.teleport(tarplayer.getLocation(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                    final IdpPlayer finalPlayer = player;
                    final IdpPlayer finalTarplayer = tarplayer;

                    parent.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 25, 1) {
                        @Override
                        public void run() {
                            if (finalTarplayer != null && finalTarplayer.isOnline()) {
                                finalTarplayer.getHandle().setPassenger(finalPlayer.getHandle());
                            }
                        }
                    });

                    player.printInfo("You jumped on " + tarplayer.getName() + "'s back!");
                } else {
                    player.printInfo("Player not found!");
                }
            } else {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            player.printError("Error");
        }
        return false;
    }

    @CommandMethod(aliases = {"test"},
    description = "Test command for... testing?",
    permission = Permission.command_misc_test,
    usage = "/test",
    serverCommand = true)
    public static void commandTest(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        sender.printInfo("Hello world!");

        // IdpPlayer player = (IdpPlayer) sender;
    }

    @CommandMethod(aliases = {"customEffect", "custom", "cast"},
    description = "Creates custom effects.",
    permission = Permission.command_misc_custom,
    usage = "/customEffect [type] [player]",
    serverCommand = false)
    public static void commandCustomEffect(InnPlugin parent, IdpPlayer player, ParameterArguments args) {

        if (args.actionMatches(0, "meteorStrike", "meteor", "strike", "explode", "1")) {
            IdpPlayer target = args.getPlayer(1);
            if (target == null) {
                target = player;
            }

            MeteorStrike effect = new MeteorStrike(parent, target.getLocation(), 20, 250, 30);
            effect.execute();

            player.print(ChatColor.AQUA, "You launch a meteor strike on " + target.getDisplayName(), "!");
            return;
        }

        if (args.actionMatches(0, "encase", "case", "trap", "encasement", "2")) {
            IdpPlayer target = args.getPlayer(1);
            if (target == null) {
                target = player;
            }

            EncasePlayer effect = new EncasePlayer(target, 8, 3);
            effect.execute();

            player.print(ChatColor.AQUA, "You encase " + target.getDisplayName(), "!");
            return;
        }

        if (args.actionMatches(0, "fireworkshow", "firework", "fireworks", "show", "3")) {
            IdpPlayer target = args.getPlayer(1);
            if (target == null) {
                target = player;
            }

            FireworkShow effect = new FireworkShow(parent, target.getLocation(), 30, 350, 50);
            effect.execute();

            player.print(ChatColor.AQUA, "You launch fireworks for " + target.getDisplayName(), "!");
            return;
        }

        player.print(ChatColor.AQUA, " --- Printing Custom Effects ---");
        player.print(ChatColor.AQUA, "");
        player.printInfo("1) " + ChatColor.AQUA + "(strike) Meteor Strike: ", "Launches an explosive attack!");
        player.printInfo("2) " + ChatColor.AQUA + "(encase) Encasement: ", "Temporarily trap a player!");
        player.printInfo("3) " + ChatColor.AQUA + "(firework) Firework Show: ", "Spawns fireworks at the player!");
    }

    @CommandMethod(aliases = {"link"},
    description = "Allows two switches to be linked to each other. ",
    permission = Permission.command_misc_link,
    usage = "/link <switchid> [switchid]",
    serverCommand = true)
    public static boolean commandLink(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        try {
            // Check for a deletion
            if (args.hasOption("del", "d", "delete")) {
                InnectisSwitch target = null;
                switch (args.size()) {
                    case 1:
                        target = InnectisSwitch.getSwitch(args.getInt(0));
                        break;
                    case 0:
                        if (sender.isPlayer()) {
                            Block block = ((IdpPlayer) sender).getTargetOwnedBlock();

                            if (IdpMaterial.fromBlock(block) == IdpMaterial.LEVER) {
                                target = InnectisSwitch.getSwitch(block.getLocation());
                            }
                            break;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }

                if (target == null) {
                    sender.printError("404 - Switch not found..");
                    return true;
                }

                // Check if allowed
                if (sender.isPlayer() && !sender.hasPermission(Permission.admin_linkanyswitch)) {
                    if (!target.canPlayerManage(sender.getName())) {
                        sender.printError("You do not own or operate this switch!");
                        return true;
                    }
                }

                // Unlink
                target.unlinkAll();

                sender.printInfo("Links removed of #" + target.getId() + "!");
                return true;
            } else {

                if (args.size() == 2) {
                    int idA = args.getInt(0);
                    int idB = args.getInt(1);

                    InnectisSwitch switchA = InnectisSwitch.getSwitch(idA);
                    InnectisSwitch switchB = InnectisSwitch.getSwitch(idB);

                    if (switchA == null) {
                        sender.printError("Switch " + idA + " does not exist!");
                        return true;
                    }
                    if (switchB == null) {
                        sender.printError("Switch " + idB + " does not exist!");
                        return true;
                    }

                    // Check if allowed
                    if (sender.isPlayer() && !sender.hasPermission(Permission.admin_linkanyswitch)) {
                        if (!switchA.canPlayerManage(sender.getName()) || !switchB.canPlayerManage(sender.getName())) {
                            sender.printError("You do not own or operate this switch!");
                            return true;
                        }
                    }

                    // Link (only needs to be done once, method will handle rest)
                    switchA.addLink(switchB.getId());
                    sender.printInfo("Link created between #" + +switchA.getId() + " and #" + switchB.getId() + "!");

                    return true;
                } else if (args.size() == 1 && sender.isPlayer()) {
                    IdpPlayer player = (IdpPlayer) sender;
                    Block block = player.getTargetOwnedBlock();

                    if (IdpMaterial.fromBlock(block) == IdpMaterial.LEVER) {
                        InnectisSwitch targetSwitch = InnectisSwitch.getSwitch(block.getLocation());

                        if (targetSwitch != null) {
                            int linkid = args.getInt(0);

                            InnectisSwitch linkSwitch = InnectisSwitch.getSwitch(linkid);
                            if (linkSwitch == null) {
                                sender.printError("Switch " + linkid + " does not exist!");
                                return true;
                            }

                            // Check if allowed
                            if (sender.isPlayer() && !sender.hasPermission(Permission.admin_linkanyswitch)) {
                                if (!linkSwitch.canPlayerManage(sender.getName())) {
                                    sender.printError("You do not own or operate this switch!");
                                    return true;
                                }
                            }

                            // Link (only needs to be done once, method will handle rest
                            targetSwitch.addLink(linkSwitch.getId());
                            sender.printInfo("Switch #" + targetSwitch.getId() + " now linked with #" + +linkSwitch.getId() + "!");

                        } else {
                            player.printError("404 - Switch not found.");
                        }
                    } else {
                        player.printError("This can only be done on levers!");
                    }
                    return true;
                }
            }
        } catch (NotANumberException nan) {
            sender.printError("No valid switchid given!");
        }
        return false;
    }

    @CommandMethod(aliases = {"fall"},
    description = "This command will force all blocks in the selected region to fall like sand.",
    permission = Permission.command_misc_fall,
    usage = "/fall",
    serverCommand = false)
    public static void commandFall(IdpPlayer player) {
        IdpWorldRegion region = player.getRegion();

        if (region == null) {
            player.printError("No region selected");
            return;
        }

        BlockCounter counter = BlockCounterFactory.getCounter(BlockCounterFactory.CountType.CUBOID);
        for (Block block : counter.getBlockList(region, region.getWorld(), null)) {
            BlockHandler.dropBlock(block);
        }

        player.printInfo("Watch your head!");
    }

    @CommandMethod(aliases = {"trash"},
    description = "Opens a trash bin.",
    permission = Permission.command_misc_trash,
    usage = "/trash [-view, -v] [-timeleft, -tl]",
    usage_Admin = "/trash [-view, -v] [-timeleft, -tl] [-clear, -c]",
    serverCommand = false)
    public static void commandTrash(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("timeleft", "tl")) {
          long trashStartTime = TrashHandler.getTrashStartTime();

          if (trashStartTime > 0) {
              long ellapsed = (System.currentTimeMillis() - trashStartTime);
              long remain = (TrashHandler.TRASH_LIFE_TIME - ellapsed);

              player.printInfo("The trash will be wiped in " + DateUtil.getTimeString(remain, true));
          } else {
              player.printError("The trash is not scheduled to be wiped.");
          }
        } else if (args.hasOption("clear", "c") && player.hasPermission(Permission.command_misc_trash_clear)) {
            TrashHandler.clear();

            parent.broadCastMessage(ChatColor.GREEN, player.getColoredDisplayName(), " has just cleared the trash!");
        } else if (args.hasOption("view", "v")) {
            if (TrashHandler.getTrashContents().size() == 0) {
                player.printError("There is no trash to view!");
                return;
            }

            boolean customConditions = (player.getInventory().getType() != InventoryType.MAIN);

            if (customConditions) {
                player.printError("You may not open the trash here!");
                return;
            }

            PagedInventory trashInventory = TrashHandler.getTrashContents();
            TrashWindow trashWindow = new TrashWindow(trashInventory);

            IdpInventory inv = trashWindow.createInventory(player, "Trash Contents");
            inv.setPayload(new TrashViewItemsPayload(trashWindow));

            player.openInventory(inv);
        } else {
            IdpInventory inv = new IdpInventory("Trash", 54);
            boolean customConditions = (player.getInventory().getType() != InventoryType.MAIN);

            // Only flag items that will go into the global trash from members who
            // cannot spawn items themselves, or player inventory is not main
            if (!customConditions && !player.getGroup().equalsOrInherits(PlayerGroup.ADMIN)) {
                inv.setPayload(new TrashAddItemsPayload());
            }

            player.openInventory(inv);
        }
    }

    @CommandMethod(aliases = {"spike"},
    description = "Spikes the drink of the target.",
    permission = Permission.command_misc_spike,
    usage = "/spike <player> [on/enable/off/disable]",
    serverCommand = false)
    public static boolean commandSpike(final Server server, InnPlugin parent, IdpPlayer sender, String[] args) {
        IdpPlayer player = sender;
        Boolean enabled = null;

        if (args.length > 0) {
            player = parent.getPlayer(args[0], false);

            if (player == null) {
                sender.printError("Target player not found.");
                return true;
            }

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("on")
                        || args[1].equalsIgnoreCase("enable")) {
                    enabled = false;
                } else if (args[1].equalsIgnoreCase("off")
                        || args[1].equalsIgnoreCase("disable")) {
                    enabled = true;
                } else {
                    player.printError("You must specify either \"on\", \"enable\", \"off\" or \"disable\".");
                    return true;
                }
            }
        }

        if (enabled == null) {
            enabled = (player.getSession().getSpiked() > 0);
        }

        if (!enabled) {
            PlayerEffect.NAUSIA.applyEffect(player, 6000, 9);
            player.getSession().setSpiked(10);
            parent.broadCastMessage(ChatColor.AQUA + "Someone spiked " + player.getColoredDisplayName() + ChatColor.AQUA + "'s drink!");
            final IdpPlayer affectPlayer = player;

            new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        for (IdpPlayer tarplayer : InnPlugin.getPlugin().getOnlinePlayers()) {
                            if (!tarplayer.getName().equals(affectPlayer.getName())) {
                                int random = new Random().nextInt(7);
                                String premsg = tarplayer.getPrefixAndDisplayName() + ": ";

                                if (random == 0) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "LOL");
                                } else if (random == 1) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "Whats wrong dude?");
                                } else if (random == 2) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "You don't look so good, man!");
                                } else if (random == 3) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "Sup, better get this party going down!");
                                } else if (random == 4) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "Boogly Woogly Woo, HaHa!");
                                } else if (random == 5) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "You best lay down, buddy!");
                                } else if (random == 6) {
                                    affectPlayer.printRaw(premsg + ChatColor.WHITE + "Can I help ya? HoHoHo");
                                }

                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }).start();
        } else {
            PlayerEffect.NAUSIA.removeEffect(player);
            player.getSession().setSpiked(0);
            parent.broadCastMessage(ChatColor.AQUA + "Someone handed " + player.getColoredDisplayName() + ChatColor.AQUA + " some coffee!");
        }

        return true;
    }

    @CommandMethod(aliases = {"boom"},
    description = "Creates a special jump,",
    permission = Permission.command_misc_boom,
    usage = "/boom [-player <name>]",
    serverCommand = false)
    public static boolean commandBoom(final Server server, InnPlugin parent, IdpPlayer sender, ParameterArguments args) {
        IdpPlayer player = sender;

        if (args.getString("player", "p") != null) {
            player = args.getPlayer("player", "p");
            if (player == null) {
                sender.printError("Player not found!");
                return false;
            }
        }
        final int ox = player.getLocation().getBlockX();
        final int oy = player.getLocation().getBlockY();
        final int oz = player.getLocation().getBlockZ();

        final Vector playerLoc = player.getLocation().toVector();

        parent.broadCastMessage(player.getColoredDisplayName() + ChatColor.AQUA + " just did a Sonic Rainboom!");

        final List<Integer> colourlist = new ArrayList<Integer>();
        colourlist.add(14); // Red
        // colourlist.add(1); // Orange
        colourlist.add(4); // Yellow
        //colourlist.add(5); // Green
        colourlist.add(11); // Blue
        //colourlist.add(10); // purple

        // Set the player's initial velocity
        final double velocity = 2.226;
        Vector speed = player.getHandle().getVelocity();
        speed.setY(velocity);
        player.getHandle().setVelocity(speed);

        final World world = player.getLocation().getWorld();
        Location originalLocation = player.getLocation();
        final IdpPlayer p = player;

        // Determine how many more jumps to give the player, depending on where they
        // are from the max world height
        int heightDifference = world.getMaxHeight() - player.getLocation().getBlockY();
        int timesToJump = Math.round(heightDifference / ((int) (velocity * 11)));

        if (timesToJump > 0) {
            parent.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 1000, timesToJump) {
                @Override
                public void run() {
                    Vector speed = p.getHandle().getVelocity();
                    speed.setY(velocity);
                    p.getHandle().setVelocity(speed);
                }
            });
        }

        List<IdpPlayer> playersInRange = player.getNearByPlayers(100);

        for (IdpPlayer rangePlayer : playersInRange) {
            rangePlayer.getHandle().playSound(originalLocation, Sound.ENTITY_LIGHTNING_THUNDER, 2, 2.0f);
        }

        player.getHandle().playSound(originalLocation, Sound.ENTITY_LIGHTNING_THUNDER, 2, 2.0f);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    Location newLocation = new Location(world, originalLocation.getBlockX() + x, originalLocation.getBlockY() + y, originalLocation.getBlockZ() + z);
                    world.playEffect(newLocation, Effect.MOBSPAWNER_FLAMES, 0);
                }
            }
        }

        // The base
        new Thread(new Runnable() {
            public void run() {
                List<Location> blocks = new ArrayList<Location>();
                int i = 0;

                Vector temploc;
                for (int range = 1; range < 10; range++) {
                    blocks = new ArrayList<Location>();

                    for (double cx = -range; cx <= range;) {
                        for (double cz = -range; cz <= range;) {
                            temploc = new Vector(ox + cx, oy, oz + cz);
                            double d = temploc.distance(playerLoc);

                            if (d < range + 0.5 && d > range - 0.5) {
                                Block block = world.getBlockAt(temploc.getBlockX(), temploc.getBlockY(), temploc.getBlockZ());
                                IdpMaterial mat = IdpMaterial.fromBlock(block);

                                if (mat == IdpMaterial.AIR) {
                                    BlockHandler.setBlock(block, IdpMaterial.GLASS_STAINED_WHITE, new Byte(colourlist.get(i % 3) + ""));
                                    BlockHandler.getIdpBlockData(block.getLocation()).setVirtualBlockStatus(true);
                                    blocks.add(block.getLocation());
                                    i++;
                                }
                            }
                            cz += 0.25;
                        }
                        cx += 0.25;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (Location loc : blocks) {
                        Block block = world.getBlockAt(loc);
                        BlockHandler.setBlock(block, IdpMaterial.AIR);

                        IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

                        if (blockData.hasData()) {
                            blockData.clear();
                        }
                    }
                }
            }
        }).start();

        // The Stream
        new Thread(new Runnable() {
            public void run() {
                List<Location> blocklist = new ArrayList<Location>();

                for (int i = oy; i < world.getMaxHeight(); i++) {
                    for (int j = 0; j < 3; j++) {
                        Block block = world.getBlockAt(ox - j + 1, i, oz);
                        IdpMaterial mat = IdpMaterial.fromBlock(block);

                        if (mat == IdpMaterial.AIR) {
                            BlockHandler.setBlock(block, IdpMaterial.GLASS_STAINED_WHITE, new Byte(colourlist.get(j).toString()));
                            BlockHandler.getIdpBlockData(block.getLocation()).setVirtualBlockStatus(true);
                            blocklist.add(block.getLocation());
                            try {
                                Thread.sleep(15);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }

                Random rand = new Random();
                Location loc;
                int q;
                while (!blocklist.isEmpty()) {
                    q = (blocklist.size() / 3);
                    if (q <= 0) {
                        loc = blocklist.remove(0);
                    } else {
                        loc = blocklist.remove(rand.nextInt(q));
                    }

                    Block block = world.getBlockAt(loc);
                    BlockHandler.setBlock(block, IdpMaterial.AIR);
                    IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

                    if (blockData.hasData()) {
                        blockData.clear();
                    }

                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

        return true;
    }

    @CommandMethod(aliases = {"setimage"},
    description = "Creates an image at the location the player is looking.",
    permission = Permission.command_admin_setimage,
    usage = "/setimage <image>",
    serverCommand = false)
    public static boolean commandSetImage(Server server, InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        Block targetBlock = player.getTargetBlock(5);

        if (targetBlock == null) {
            player.printError("You must be looking at a block!");
            return true;
        }

        String imageFile = "plugins/IDP/images/" + args[0];
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(imageFile));
        } catch (IOException ex) {
            player.printError("Unable to find the specified picture!");
            return true;
        }

        String ext = imageFile.substring(imageFile.length() - 4);
        BlockFace frameFacingFace = player.getFacingDirection().getOppositeFace();
        World world = player.getWorld().getHandle();
        MapPictureBuilder builder = new MapPictureBuilder(server, world, image, targetBlock, frameFacingFace);

        String errorMessage = builder.createMapImages(ext);

        if (errorMessage == null) {
            player.printInfo("Image created!");
        } else {
            player.printError(errorMessage);
        }

        return true;
    }

    @CommandMethod(aliases = {"setart", "setpicture", "setpic"},
    description = "Sets a painting's picture.",
    permission = Permission.command_misc_setart,
    usage = "/setart <picture> OR <list>",
    serverCommand = false)
    public static boolean commandSetArt(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;
        Block block = player.getTargetBlock(5);
        Block oppositeBlock = block.getRelative(player.getFacingDirection().getOppositeFace());
        Chunk chunk = oppositeBlock.getChunk();
        Painting painting = null;

        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.PAINTING) {
                Location entLocation = entity.getLocation();
                Location blockLocation = oppositeBlock.getLocation();

                if (entLocation.getBlockX() == blockLocation.getBlockX()
                        && entLocation.getBlockY() == blockLocation.getBlockY()
                        && entLocation.getBlockZ() == blockLocation.getBlockZ()) {
                    painting = (Painting) entity;
                    break;
                }
            }
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<Art> toAdd = new ArrayList<Art>(Arrays.asList(Art.values()));
            StringBuilder sb = new StringBuilder();

            if (painting != null) {
                Art paintingArt = painting.getArt();
                int width = paintingArt.getBlockWidth();
                int height = paintingArt.getBlockHeight();

                player.printInfo("You are looking at: " + ChatColor.YELLOW + paintingArt.name().toLowerCase() + " (size: " + width + "x" + height + ")");
                sb.append("Paintings available in this size: ").append(ChatColor.YELLOW);

                for (Iterator<Art> it = toAdd.iterator(); it.hasNext();) {
                    Art art = it.next();
                    int artWidth = art.getBlockWidth();
                    int artHeight = art.getBlockHeight();

                    if (artWidth == width && artHeight == height) {
                        sb.append(art.name().toLowerCase()).append(", ");
                        it.remove();
                    }
                }

                player.printInfo(sb.substring(0, sb.length() - 2));
                sb = new StringBuilder();
                sb.append("Other paintings: ").append(ChatColor.YELLOW);
            } else {
                sb.append("The available paintings are: ").append(ChatColor.YELLOW);
            }

            for (Art art : toAdd) {
                sb.append(art.name().toLowerCase()).append(", ");
            }

            player.printInfo(sb.substring(0, sb.length() - 2));
        } else {
            if (painting == null) {
                player.printError("You must be looking at a painting!");
                return true;
            }

            Art art = Art.getByName(args[0]);

            if (art == null) {
                player.printError("Invalid art specified! Type /setart for a list.");
                return true;
            }

            painting.setArt(art, true);
            player.printInfo("Set target painting to " + art.toString().toLowerCase() + ".");
        }

        return true;
    }

    @CommandMethod(aliases = {"save"},
    description = "Saves your player profile.",
    permission = Permission.command_misc_save,
    usage = "/save",
    usage_Admin = "/save [all]",
    serverCommand = true)
    public static boolean commandSave(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            if (!sender.hasPermission(Permission.command_misc_saveall)) {
                sender.printError("You cannot do that!");
                return true;
            }
            for (IdpPlayer idpplayer : parent.getOnlinePlayers()) {
                idpplayer.saveInventory();
                idpplayer.getHandle().saveData();
            }
            sender.printInfo("All online users saved!");
        } else {
            if (sender.getHandle() instanceof Player) {
                Player player = (Player) sender.getHandle();
                IdpPlayer idpplayer = parent.getPlayer(player);
                idpplayer.saveInventory();
                player.saveData();
                idpplayer.printInfo("Your profile was successfully saved!");
            } else {
                sender.printError("Your profile can never be saved...");
            }
        }
        return true;
    }

    @CommandMethod(aliases = {"lights"},
    description = "Toggles the use of your portable light.",
    permission = Permission.command_misc_lights,
    usage = "/lights [on/off]",
    serverCommand = false)
    public static boolean commandLight(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;
        if (args.length == 0 || args.length == 1) {
            boolean enabled = player.getSession().hasLightsEnabled();

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (enabled) {
                        player.printError("Lights are already enabled for you.");
                        return true;
                    }

                    enabled = true;
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (!enabled) {
                        player.printError("Lights are already disabled for you.");
                        return true;
                    }

                    enabled = false;
                } else {
                    player.printError("Use either \"on\" or \"off\"");
                    return true;
                }
            } else {
                enabled = !enabled;
            }

            // Don't allow lights if the player is using a night vision potion
            if (enabled && player.getHandle().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.printError("Cannot enable lights with night vision in effect!");
                return true;
            }

            player.getSession().setLightsEnabled(enabled);

            if (enabled) {
                if (player.canUsePortableLight()) {
                    PlayerEffect.NIGHT_VISION.applySpecial(player, 9000000, 1);
                }
            } else {
                PlayerEffect.NIGHT_VISION.removeSpecial(player);
            }

            player.printInfo("Lights are now " + (enabled ? "enabled" : "disabled") + " for you.");

            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"mcboost"},
    description = "Boosts the vehicle of the player.",
    permission = Permission.command_cheat_mcboost,
    usage = "/mcboost",
    serverCommand = false)
    public static boolean commandMcBoost(IdpPlayer player) {
        Player bukkitPlayer = player.getHandle();
        if (bukkitPlayer.isInsideVehicle()) {
            Vector speed = bukkitPlayer.getVehicle().getVelocity();
            if (speed.getX() > 0.00) {
                speed.setX(5000);
            } else if (speed.getX() < 0.00) {
                speed.setX(-5000);
            } else if (speed.getZ() > 0.00) {
                speed.setZ(5000);
            } else if (speed.getZ() < 0.00) {
                speed.setZ(-5000);
            }
            bukkitPlayer.getVehicle().setVelocity(speed);
            player.printInfo("You have just boosted this vehicle!");
        } else {
            player.printError("You are not riding a minecart. Cannot boost!");
        }

        return true;
    }

    @CommandMethod(aliases = {"setbookcasetitle", "bookcase", "setcasename", "casetitle"},
    description = "Sets the name of a bookcase.",
    permission = Permission.command_misc_setbookcasetitle,
    usage = "/setbookcasetitle <name>",
    serverCommand = false)
    public static boolean commandSetBookaseTitle(Server server, InnPlugin parent, IdpPlayer player, SmartArguments args) {
        if (args.size() != 1) {
            return false;
        }

        Block block = player.getTargetOwnedBlock();
        InnectisBookcase bookcase = InnectisBookcase.getBookcase(block.getLocation());

        if (bookcase == null) {
            player.printError("This object is unknown or not owned by anyone!");
            return true;
        }

        if (!bookcase.canPlayerManage(player.getName())) {
            player.printError("You do not own or operate this bookcase!");
            return true;
        }

        // Check length
        if (args.getString(0).length() > 16) {
            player.printError("The name is too long! (16 max limit)");
            return true;
        }

        // Set name
        bookcase.setCaseTitle(args.getString(0));

        if (bookcase.save()) {
            player.printInfo("Name updated");
        } else {
            player.printError("Internal server error!");
        }

        return true;
    }

}

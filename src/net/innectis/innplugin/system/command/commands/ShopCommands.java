package net.innectis.innplugin.system.command.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.loggers.SendMoneyLogger;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.system.shop.ChestShopLotDetails;
import net.innectis.innplugin.system.shop.ChestShopLotManager;
import net.innectis.innplugin.tasks.sync.ValutaBankTransactionTask;
import net.innectis.innplugin.tasks.TaskManager;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.PlayerUtil;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EquipmentSlot;

public final class ShopCommands {

    @CommandMethod(aliases = {"modifybalance", "modifybal", "modbal", "mb"},
    description = "Modifies the balance of the specified player.",
    permission = Permission.command_admin_modifybalance,
    usage = "/modifybalance <player> <add/deposit/remove/withdraw/set> <amount> [vT/bvT/pvp/vp/rp]",
    serverCommand = true)
    public static boolean commandModifyBalance(InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        if (args.length > 2) {
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
                // Get the proper casing of the name
                playerName = credentials.getName();
            }

            int action = 0; // 1 = add, 2 = subtract, 3 = set

            if (args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("deposit")) {
                action = 1;
            } else if (args[1].equalsIgnoreCase("subtract")
                    || args[1].equalsIgnoreCase("withdraw")) {
                action = 2;
            } else if (args[1].equalsIgnoreCase("set")) {
                action = 3;
            }

            if (action == 0) {
                sender.printError("Invalid action. Must be \"add\", \"deposit\", \"subtract\", \"withdraw\", or \"set\".");
                return true;
            }

            int amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                sender.printError("Amount is not a number.");
                return true;
            }

            if (amount < 1) {
                sender.printError("Amount cannot be less than 1.");
                return true;
            }

            TransactionType type = TransactionType.VALUTAS;

            if (args.length == 4) {
                type = TransactionType.fromString(args[3]);

                if (type == null) {
                    sender.printError("Transaction type not found!");
                    return true;
                }
            }

            TransactionObject transaction = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());
            String amtTypeName = (amount == 1 ? type.getName().toLowerCase().substring(0, type.getName().length() - 1) : type.getName().toLowerCase());

            if (action == 1) {
                transaction.addValue(amount, type);
                sender.printInfo("Deposited " + amount + " " + amtTypeName + " to " + playerName + "!");

                if (target != null && target.isOnline()) {
                    target.printInfo(amount + " " + amtTypeName + " was deposited to you!");
                }
            } else if (action == 2) {
                int balance = transaction.getValue(type);
                String balTypeName = (balance == 1 ? type.getName().toLowerCase().substring(0, type.getName().length() - 1) : type.getName().toLowerCase());

                if (balance < amount) {
                    sender.printError("Unable to withdraw " + amount + " " + amtTypeName + " from a balance of " + balance + " " + balTypeName + " for " + playerName + "!");
                    return true;
                }

                transaction.subtractValue(amount, type);
                sender.printInfo("Withdrew " + amount + " " + amtTypeName + " from " + playerName + "!");

                if (target != null && target.isOnline()) {
                    target.printInfo(amount + " " + amtTypeName + " was withdrawn from you!");
                }
            } else if (action == 3) {
                transaction.setValue(amount, type);
                sender.printInfo("Set " + playerName + "'s " + type.getName().toLowerCase() + " to " + amount + "!");

                if (target != null && target.isOnline()) {
                    target.printInfo("Your " + type.getName().toLowerCase() + " was set to " + amount + "!");
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @CommandMethod(aliases = {"sendmoney", "sm", "send"},
    description = "Sends valutas to the specified player.",
    permission = Permission.command_shop_sendmoney,
    usage = "/sendmoney <player> <amount>",
    serverCommand = false)
    public static boolean commandSendMoney(Server server, InnPlugin parent, IdpPlayer player, String args[]) {
        if (args.length > 1) {
            String playerName = args[0];
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                playerName = target.getName();
            }

            PlayerCredentials receiverCredentials = PlayerCredentialsManager.getByName(playerName);

            if (receiverCredentials == null) {
                player.printError("That player does not exist.");
                return true;
            } else {
                // Get the proper casing of the name
                playerName = receiverCredentials.getName();
            }

            int amount = 0;

            try {
                amount = Integer.parseInt(args[1]);

                if (amount < 1) {
                    player.printError("Amount cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException ex) {
                player.printError("Amount is not a number.");
                return true;
            }

            TransactionObject transactionSender = TransactionHandler.getTransactionObject(player);
            int balance = transactionSender.getValue(TransactionType.VALUTAS);

            if (amount > balance) {
                player.printError("You cannot send more valutas than you have.");
                return true;
            }

            transactionSender.subtractValue(amount, TransactionType.VALUTAS);

            TransactionObject transactionReceiver = TransactionHandler.getTransactionObject(receiverCredentials.getUniqueId(), receiverCredentials.getName());
            transactionReceiver.addValue(amount, TransactionType.VALUTAS);

            player.printInfo("You sent " + amount + " valuta" + (amount != 1 ? "s" : "") + " to " + playerName + "!");

            if (target != null && target.isOnline()) {
                target.printInfo(player.getName() + " sent you " + amount + " valuta" + (amount != 1 ? "s" : "") + ".");
            }

            SendMoneyLogger sml = (SendMoneyLogger) LogType.getLoggerFromType(LogType.SEND_MONEY);
            sml.log(player.getName(), playerName, amount);

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"bank"},
    description = "Command to access a player's bank.",
    permission = Permission.command_shop_bank,
    usage = "/bank [-deposit, -d <amount>] [-withdraw, -w <amount>] [-note, -n <valutas> [-amount, -a <amount>]]",
    serverCommand = false)
    public static boolean commandBank(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.hasArgument("note", "n")) {
            Integer valutasPerNote = 0;

            try {
                valutasPerNote = Integer.parseInt(args.getString("note", "n"));
            } catch (NumberFormatException nfe) {
                player.printError("Valutas amount is not a number.");
                return true;
            }

            ChatColor amountColor = null;

            if (valutasPerNote == 10000) {
                amountColor = ChatColor.AQUA;
            } else if (valutasPerNote == 1000) {
                amountColor = ChatColor.GOLD;
            } else if (valutasPerNote == 500) {
                amountColor = ChatColor.DARK_PURPLE;
            } else if (valutasPerNote == 100) {
                amountColor = ChatColor.DARK_AQUA;
            } else if (valutasPerNote == 50) {
                amountColor = ChatColor.YELLOW;
            } else {
                player.printError("Amount must be 10000, 1000, 500, 100, or 50.");
                return true;
            }

            int noteAmount = 1;

            if (args.hasArgument("amount", "a")) {
                try {
                    noteAmount = Integer.parseInt(args.getString("amount", "a"));
                } catch (NumberFormatException nfe) {
                    player.printError("Amount is not a number.");
                    return true;
                }
            }

            TransactionObject transaction = TransactionHandler.getTransactionObject(player);
            int valutasInBank = transaction.getValue(TransactionType.VALUTAS_IN_BANK);
            int notesToCreate = noteAmount;
            int createdNotes = 0;

            while (notesToCreate > 0) {
                if (valutasPerNote > valutasInBank) {
                    if (createdNotes > 0) {
                        player.printInfo("Created " + createdNotes + " bank note" + (createdNotes != 1 ? "s" : "") + " worth " + amountColor + (valutasPerNote * createdNotes), " valutas.");
                        player.printError("You ran out of valutas while creating bank notes.");
                        player.printError("Unable to create " + notesToCreate + " remaining bank note" + (notesToCreate != 1 ? "s" : "") + ".");
                    } else {
                        player.printError("You do not have enough valutas in the bank for " + noteAmount + " bank note" + (noteAmount != 1 ? "s" : "") + ".");
                    }

                    return true;
                }

                IdpItemStack stack = new IdpItemStack(IdpMaterial.PAPER, 1);
                ItemData data = stack.getItemdata();

                data.setSpecialItem(SpecialItemType.BANK_NOTE);
                data.setItemName(ChatColor.AQUA + "Bank Note (" + amountColor
                    + valutasPerNote + ChatColor.YELLOW + " vT" + ChatColor.AQUA + ")");
                data.setValue("ValutaAmount", valutasPerNote.toString());

                int remain = player.addItemToInventory(stack, true);

                if (remain > 0) {
                    if (createdNotes > 0) {
                        player.printInfo("Created " + createdNotes + " bank note" + (createdNotes != 1 ? "s" : "") + " worth " + amountColor + (valutasPerNote * createdNotes), " valutas.");
                        player.printError("You ran out of space while creating bank notes.");
                        player.printError("Unable to create " + notesToCreate + " remaining bank note" + (notesToCreate != 1 ? "s" : "") + ".");
                    } else {
                        player.printError("You don't have enough space for " + noteAmount + " bank note" + (noteAmount != 1 ? "s" : "") + "!");
                    }

                    return true;
                }

                valutasInBank -= valutasPerNote;
                transaction.setValue(valutasInBank, TransactionType.VALUTAS_IN_BANK);

                notesToCreate--;
                createdNotes++;
            }

            player.printInfo("Created " + createdNotes + " bank note" + (createdNotes != 1 ? "s" : "") + " worth " + amountColor + (valutasPerNote * createdNotes), " valutas.");

            return true;
        }

        if (args.hasArgument("deposit", "d", "withdraw", "w")) {
            TransactionObject transaction = TransactionHandler.getTransactionObject(player);
            boolean isDeposit = args.hasArgument("deposit", "d");
            int amount = 0;

            try {
                amount = Integer.parseInt(args.getString("deposit", "d", "withdraw", "w"));
            } catch (NumberFormatException ex) {
                player.printError("Amount is not an integer.");
                return true;
            }

            if (amount < 1) {
                player.printError("Amount cannot be less than 1.");
                return true;
            }

            if (isDeposit) {
                int balance = transaction.getValue(TransactionType.VALUTAS);

                if (amount > balance) {
                    player.printError("Cannot deposit more than your valuta balance!");
                    return true;
                }

                int currentValutasToBank = transaction.getValue(TransactionType.VALUTAS_TO_BANK);
                int newValutasToBank = (currentValutasToBank + amount);

                transaction.subtractValue(amount, TransactionType.VALUTAS);
                transaction.addValue(amount, TransactionType.VALUTAS_TO_BANK);

                player.print(ChatColor.YELLOW, amount + " valuta" + (amount != 1 ? "s" : "") + " will be sent to the bank."
                        + " (" + newValutasToBank + " valuta" + (newValutasToBank != 1 ? "s" : "") + " pending)");
            } else {
                int balance = transaction.getValue(TransactionType.VALUTAS_IN_BANK);

                if (amount > balance) {
                    player.printError("Cannot withdraw more valutas than you have in the bank.");
                    return true;
                }

                int currentValutasToPlayer = transaction.getValue(TransactionType.VALUTAS_TO_PLAYER);
                int newValutasToPlayer = (currentValutasToPlayer + amount);

                transaction.subtractValue(amount, TransactionType.VALUTAS_IN_BANK);
                transaction.addValue(amount, TransactionType.VALUTAS_TO_PLAYER);

                player.print(ChatColor.YELLOW, amount + " valuta" + (amount != 1 ? "s" : "") + " will be sent to you."
                        + " (" + newValutasToPlayer + " valuta" + (newValutasToPlayer != 1 ? "s" : "") + " pending)");
            }

            PlayerSession session = player.getSession();
            TaskManager manager = parent.getTaskManager();
            long existingTaskId = session.getBankTaskId();
            long bankTaskTime = 0;

            // If there's an existing task, get rid of it and make a new one
            if (existingTaskId > 0) {
                manager.removeTask(existingTaskId);
                bankTaskTime = session.getLastBankTaskTime();
                session.setLastBankTaskTime(0);
            }

            // Determine a new time if we don't have an existing one
            if (bankTaskTime == 0) {
                bankTaskTime = Configuration.BANK_TRANSACTION_TIME;
            }

            ValutaBankTransactionTask vbtt = new ValutaBankTransactionTask(player, bankTaskTime);
            long taskId = parent.getTaskManager().addTask(vbtt);

            session.setBankTaskId(taskId);
            session.setLastBankTaskTime(bankTaskTime);

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"balance", "getbalance", "getbal", "bal"},
    description = "Gets a player's balance.",
    permission = Permission.command_shop_getbalance,
    usage = "/balance",
    usage_Mod = "/balance [player]",
    serverCommand = true)
    public static boolean commandBalance(Server server, InnPlugin parent, IdpCommandSender sender, String[] args) {
        String playerName = sender.getName();

        if (args.length > 0) {
            playerName = args[0];
            IdpPlayer target = parent.getPlayer(playerName);

            if (target != null) {
                playerName = target.getName();
            }

            if (!playerName.equalsIgnoreCase(sender.getName().toLowerCase())
                    && sender.isPlayer() && !sender.hasPermission(Permission.command_shop_getallbalances)) {
                sender.printError("You are unable to view the balance of other players.");
                return true;
            }
        }

        if (!sender.isPlayer() && playerName.equals(sender.getName())) {
            sender.printError("Console can't lookup itself!");
            return true;
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        if (credentials == null) {
            sender.printError("That player does not exist.");
            return true;
        } else {
            // Get the proper casing of the name
            playerName = credentials.getName();
        }

        TransactionObject transaction = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());
        int valutas = transaction.getValue(TransactionType.VALUTAS);
        sender.print(ChatColor.YELLOW, "Showing balance information for " + playerName);
        sender.print(ChatColor.YELLOW, "");
        sender.print(ChatColor.YELLOW, "Current balance: " + valutas + " valuta" + (valutas != 1 ? "s" : ""));

        if (playerName.equalsIgnoreCase(sender.getName())
                || sender.hasPermission(Permission.command_shop_getallbalances)) {
            int valutasInBank = transaction.getValue(TransactionType.VALUTAS_IN_BANK);
            int valutasToBank = transaction.getValue(TransactionType.VALUTAS_TO_BANK);
            int valutasToPlayer = transaction.getValue(TransactionType.VALUTAS_TO_PLAYER);

            if (valutasInBank > 0) {
                sender.print(ChatColor.YELLOW, "Bank balance: " + valutasInBank + " valuta" + (valutasInBank != 1 ? "s" : ""));
            }

            if (valutasToBank > 0) {
                sender.print(ChatColor.YELLOW, "Transfering to bank: " + valutasToBank + " valuta" + (valutasToBank != 1 ? "s" : ""));
            }

            if (valutasToPlayer > 0) {
                sender.print(ChatColor.YELLOW, "Transferring to player: " + valutasToPlayer + " valuta" + (valutasToPlayer != 1 ? "s" : ""));
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"setshopitem", "ssi"},
    description = "Creates a listing of an item on a chest sign shop.",
    permission = Permission.command_shop_setshopitem,
    usage = "/setshopitem [item name] <buy/sell> <amount> <price>",
    serverCommand = false)
    public static boolean commandSetShopItem(InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length < 3 || args.length > 4) {
            return false;
        }

        Block block = player.getTargetOwnedBlock();

        if (block == null || !(block.getState() instanceof Chest)) {
            player.printError("You must be looking at a chest.");
            return true;
        }

        InnectisChest ownedChest = ChestHandler.getChest(block.getLocation());

        if (ownedChest != null && !ownedChest.getOwner().equalsIgnoreCase(player.getName())) {
            player.printError("You do not own this chest.");
            return true;
        }

        String buySell = (args.length == 4 ? args[1] : args[0]);

        if (!buySell.equalsIgnoreCase("buy") && !buySell.equalsIgnoreCase("sell")) {
            player.printError("You must indicate either \"buy\" or \"sell\".");
            return true;
        }

        int amount = 0;

        try {
            amount = Integer.parseInt(args.length == 4 ? args[2] : args[1]);
        } catch (NumberFormatException nfe) {
            player.printError("Amount is not a number.");
            return true;
        }

        int price = 0;

        try {
            price = Integer.parseInt((args.length == 4 ? args[3] : args[2]));
        } catch (NumberFormatException nfe) {
            player.printError("Price is not a number.");
        }

        IdpMaterial mat = null;

        if (args.length < 4) {
            IdpItemStack handStack = player.getItemInHand(EquipmentSlot.HAND);

            if (handStack == null || handStack.getMaterial() == IdpMaterial.AIR) {
                handStack = player.getItemInHand(EquipmentSlot.OFF_HAND);
            }

            if (handStack == null || handStack.getMaterial() == IdpMaterial.AIR) {
                player.printError("You must have an item in hand!");
                return true;
            }

            mat = handStack.getMaterial();
        } else {
            mat = IdpMaterial.fromString(args[0]);

            if (mat == null) {
                player.printError("You have entered an invalid material name.");
                return true;
            }
        }

        Chest chest = (Chest) block.getState();
        org.bukkit.material.Chest chestMaterial = (org.bukkit.material.Chest) chest.getData();
        Block checkBlock = block.getRelative(BlockFace.UP);
        IdpMaterial checkMaterial = IdpMaterial.fromBlock(checkBlock);

        // This space is taken, check in front of the chest
        if (checkMaterial != IdpMaterial.AIR && checkMaterial != IdpMaterial.WALL_SIGN) {
            BlockFace facing = chestMaterial.getFacing();
            checkBlock = block.getRelative(facing);
            checkMaterial = IdpMaterial.fromBlock(checkBlock);

            if (checkMaterial != IdpMaterial.AIR && checkMaterial != IdpMaterial.WALL_SIGN) {
                player.printError("Cannot make chest shop listing. Sign location is blocked.");
                return true;
            }
        }

        InnectisLot checkLot = LotHandler.getLot(checkBlock.getLocation());

        if (checkLot != null && !checkLot.canPlayerAccess(player.getName())) {
            player.printError("You do not have access to create a chest shop listing.");
            return true;
        }

        Sign sign = null;

        if (checkMaterial == IdpMaterial.WALL_SIGN) {
            sign = (Sign) checkBlock.getState();
        } else {
            BlockFace facing = chestMaterial.getFacing();

            // Must have a sign unless player has permission otherwise
            if (!player.hasPermission(Permission.special_setshopitem_noconsume)) {
                if (!player.removeItemFromInventory(IdpMaterial.SIGN, 1)) {
                    player.printError("You must have a sign in your inventory to create a listing.");
                    return true;
                }
            }

            BlockHandler.setBlock(checkBlock, IdpMaterial.WALL_SIGN, true);

            BlockState state = checkBlock.getState();
            BlockHandler.rotateBlock(state, facing);
            state.update();
            sign = (Sign) state;
        }

        sign.setLine(0, "[" + buySell + "]");
        sign.setLine(1, mat.getName());
        sign.setLine(2, amount + " @ " + price + " vT");

        IdpInventory inv = new IdpInventory(chest.getInventory());
        int itemCount = 0;

        for (IdpItemStack stack : inv.getContentsIdp()) {
            if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            if (stack.getMaterial() == mat) {
                itemCount += stack.getAmount();
            }
        }

        sign.setLine(3, "Count: " + itemCount);
        sign.update();

        player.printInfo("Created chest shop listing! " + ChatColor.YELLOW + buySell + "ing "
                + ChatColor.AQUA + amount, " items of " + ChatColor.AQUA + mat.getName(), " for "
                + ChatColor.AQUA + price, " vT.");

        return true;
    }

    @CommandMethod(aliases = {"chestshop", "cs"},
    description = "A command that is used to list all chest shop lots.",
    permission = Permission.command_shop_chestshop,
    usage = "/chestshop [-list [-page, -p <page>]]",
    usage_Mod = "/chestshop [-list [-page, -p <page>]] [-add, -a <lot id> <name>] [-delete, -d <id>]",
    serverCommand = false)
    public static boolean commandChestShop(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("list", "l")) {
            List<ChestShopLotDetails> chestShopLots = ChestShopLotManager.getChestShopLots();

            if (chestShopLots.isEmpty()) {
                player.printError("There are no chest shop listings!");
                return true;
            }

            Collections.sort(chestShopLots, new Comparator<ChestShopLotDetails>() {
                @Override
                public int compare(ChestShopLotDetails o1, ChestShopLotDetails o2) {
                    int id1 = o1.getLot().getId();
                    int id2 = o2.getLot().getId();

                    if (id1 > id2) {
                        return 1;
                    } else if (id1 == id2){
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });

            int pageNo = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    pageNo = Integer.parseInt(args.getString("page", "p"));

                    if (pageNo < 1) {
                        player.printError("Page number cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("Page number is not a number.");
                    return true;
                }
            }

            List<String> messages = new ArrayList<String>();

            for (int i = 0; i < chestShopLots.size(); i++) {
                ChestShopLotDetails details = chestShopLots.get(i);
                InnectisLot lot = details.getLot();
                String coloredName = PlayerUtil.getColoredName(lot.getOwnerCredentials());
                String msg = ChatColor.AQUA + "Lot #" + lot.getId() + ChatColor.WHITE + " (" + ChatColor.YELLOW
                        + details.getName() + ChatColor.WHITE + ")" + ChatColor.AQUA + " owned by " + coloredName;
                messages.add(msg);
            }

            PagedCommandHandler ph = new PagedCommandHandler(pageNo, messages);

            if (ph.isValidPage()) {
                player.printInfo("Listing all registered player chest shop lots!");
                player.printInfo("");

                player.print(ChatColor.AQUA, "Showing page " + pageNo + " of " + ph.getMaxPage());
                player.print(ChatColor.AQUA, "");

                for (String msg : ph.getParsedInfo()) {
                    player.print(ChatColor.AQUA, msg);
                }
            }

            return true;
        } else if (args.hasArgument("add", "a") && player.hasPermission(Permission.command_shop_chestshop_special)) {
            if (args.getActionSize() < 1) {
                player.printError("Must supply the name of the chest shop lot.");
                return true;
            }

            int lotid = 0;
            InnectisLot lot = null;

            try {
                lotid = args.getInt("add", "a");
                lot = LotHandler.getLot(lotid);

                if (lot == null) {
                    player.printError("The specified lot doesn't exist.");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("-add needs a lot ID.");
                return true;
            }

            String shopName = args.getString(0);

            if (shopName.length() > 100) {
                player.printError("Shop name is too long. 50 characters max.");
                return true;
            }

            ChestShopLotDetails details = ChestShopLotManager.getChestShopLot(lotid);

            if (details != null) {
                details.setName(shopName);
                details.save();

                player.printInfo("Updated lot " + ChatColor.AQUA + "#" + lot.getId(), "'s chest shop lot name!");
            } else {
                ChestShopLotManager.addChestShopLot(new ChestShopLotDetails(lot, shopName));

                player.printInfo("Added lot " + ChatColor.AQUA + "#" + lot.getId(), " to the chest shop lot list!");
            }



            return true;
        } else if (args.hasArgument("delete", "d") && player.hasPermission(Permission.command_shop_chestshop_special)) {
            int lotid = 0;

            try {
                lotid = args.getInt("delete", "d");
            } catch (NumberFormatException nfe) {
                player.printError("-delete requires a lot ID.");
                return true;
            }

            ChestShopLotDetails details = ChestShopLotManager.getChestShopLot(lotid);

            if (details == null) {
                player.printError("Chest shop lot not found!");
                return true;
            }

            ChestShopLotManager.deleteChestShopLot(details);
            player.printInfo("Deleted chest shop lot " + ChatColor.AQUA + "#" + lotid, "!");
            return true;
        }

        return false;
    }

}

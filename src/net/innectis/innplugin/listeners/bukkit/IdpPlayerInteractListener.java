package net.innectis.innplugin.listeners.bukkit;

import java.util.List;
import java.util.Map;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.BridgeHandler;
import net.innectis.innplugin.handlers.GateHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.DirectionType;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.objects.IdpContainerTransfer;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.inventory.payload.AutoRefillInventoryPayload;
import net.innectis.innplugin.inventory.payload.BookcaseInventoryPayload;
import net.innectis.innplugin.inventory.payload.ShowcaseInventoryPayload;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.objects.owned.ChestFlagType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler.VanillaChestType;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.OwnedEntityHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisDoor;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.objects.owned.InnectisTrapdoor;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerBackpack;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.system.shop.ChestShop;
import net.innectis.innplugin.system.shop.PurchaseResult;
import net.innectis.innplugin.system.signs.ChestShopSign;
import net.innectis.innplugin.system.signs.ChestShopSign.ChestShopType;
import net.innectis.innplugin.system.signs.SignValidator;
import net.innectis.innplugin.system.signs.WallSignType;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.tasks.TaskManager;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Door;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * When adding a method here. Make sure it returns a boolean. Returning true
 * means that the event is handled. And should be stopped.
 */
public class IdpPlayerInteractListener {

    private final InnPlugin plugin;

    public IdpPlayerInteractListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean playerInteractWithSign(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // copying sign text
        if (mat == IdpMaterial.WALL_SIGN || mat == IdpMaterial.SIGN_POST) {
            InnectisLot lot = event.getBlockLot();

            if (lot == null || lot.canPlayerAccess(player.getName())
                    || player.hasPermission(Permission.special_edit_any_sign)) {
                Sign sign = (Sign) block.getState();
                boolean canCopySign = false;

                for (String line : sign.getLines()) {
                    if (!StringUtil.stringIsNullOrEmpty(line)) {
                        canCopySign = true;
                        break;
                    }
                }

                if (canCopySign) {
                    player.getSession().setSignLines(sign.getLines());
                    player.printInfo(ChatColor.DARK_GREEN + "Copied sign text! Paste by left-clicking with a sign.");

                    return true;
                }
            }
        }

        // Pasting copied sign text
        if (event.getBlockFace() != BlockFace.DOWN && player.getSession().hasSignLines()) {
            Block checkBlock = null;
            boolean existingSign = false;

            if (mat == IdpMaterial.WALL_SIGN || mat == IdpMaterial.SIGN_POST) {
                Sign sign = (Sign) block.getState();

                // Do not allow paste if sign has text already
                for (String line : sign.getLines()) {
                    if (!StringUtil.stringIsNullOrEmpty(line)) {
                        return true;
                    }
                }

                checkBlock = block;
                existingSign = true;
            } else {
                checkBlock = block.getRelative(event.getBlockFace());
            }

            InnectisLot lot = LotHandler.getLot(checkBlock.getLocation(), true);

            // Do not allow sign pasting where not allowed
            if (lot != null && !lot.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.special_signcopy)) {
                return true;
            }

            IdpMaterial checkMaterial = IdpMaterial.fromBlock(checkBlock);

            if (checkMaterial != IdpMaterial.AIR && !existingSign) {
                player.printError("There is a " + checkMaterial.getName() + " at the destination!");
                return true;
            }

            Block blockBelow = checkBlock.getRelative(BlockFace.DOWN);
            InnectisChest innChest = ChestHandler.getChest(blockBelow.getLocation());

            // Only chest owner can place a sign above the chest
            if (innChest != null && !innChest.getOwner().equalsIgnoreCase(player.getName())
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("You cannot paste a sign above a chest you do not own.");
                return true;
            }

            Sign sign = null;

            if (existingSign) {
                sign = (Sign) checkBlock.getState();
            } else {
                DirectionType type = null;
                IdpMaterial signMaterial = null;

                if (event.getBlockFace() == BlockFace.UP) {
                    type = DirectionType.FULL;
                    signMaterial = IdpMaterial.SIGN_POST;
                } else {
                    type = DirectionType.CARDINAL;
                    signMaterial = IdpMaterial.WALL_SIGN;
                }

                BlockHandler.setBlock(checkBlock, signMaterial);
                BlockFace face = null;

                if (signMaterial == IdpMaterial.WALL_SIGN) {
                    face = event.getBlockFace();
                } else {
                    face = player.getFacingDirection(type).getOppositeFace();
                }

                sign = (Sign) checkBlock.getState();
                org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
                signData.setFacingDirection(face);
                sign.setData(signData);
            }

            String[] signText = player.getSession().getSignLines();
            player.getSession().clearSignLines();

            for (int i = 0; i < 4; i++) {
                if (signText[i] != null) {
                    sign.setLine(i, signText[i]);
                }
            }

            sign.update();

            // Only take a sign the player has in their inventory if they
            // are not in creative and have not pasted onto an
            // existing sign
            if (player.getHandle().getGameMode() != GameMode.CREATIVE && !existingSign) {
                player.removeItemFromInventory(IdpMaterial.SIGN, 1);
            }

            player.printInfo("Pasted sign text!");

            return true;
        }

        return false;
    }

    public boolean playerPotionUse(InnPlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getPlayerLot();
        Block block = event.getBlock();
        IdpItemStack stack = event.getItem();

        if (lot != null && lot.isFlagSet(LotFlagType.NOPOTION)) {
            player.printError("Potions are disabled on this lot!");
            player.getInventory().updateBukkitInventory();
            return true;
        }

        // Do not check normal potions, only splash and lingering potions
        if (stack.getMaterial() != IdpMaterial.POTIONS) {
            PotionMeta meta = (PotionMeta) stack.toBukkitItemstack().getItemMeta();
            PotionType type = meta.getBasePotionData().getType();

            switch (type) {
                case POISON:
                case WEAKNESS:
                case SLOWNESS:
                case INSTANT_DAMAGE: {
                    boolean skip = (block != null && IdpMaterial.fromBlock(block).isInteractable() && !player.getHandle().isSneaking());

                    if (!skip && !player.getSession().isPvpEnabled()) {
                        event.getPlayer().printError("Unable to throw potion, PvP is off!");
                        event.getPlayer().getInventory().updateBukkitInventory();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // @todo: use an event for this when one exists
    public boolean playerFlowerPotUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        Block block = event.getBlock();
        InnectisLot lot = LotHandler.getLot(block.getLocation(), true);

        if (lot != null && !lot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.world_build_unrestricted)) {
            player.sendBlockChange(block);
            player.printError("You cannot manipulate this flower pot!");

            return true;
        }

        return false;
    }

    public boolean playerContainerUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        IdpMaterial mat = IdpMaterial.fromBlock(event.getBlock());
        InnectisLot lot = event.getBlockLot();

        //Do not allow non-lot members to open Containers
        //(excluding Chests as they have their own protection system [chestallow])
        //The interaction check above SHOULD catch these - this is a failsafe!
        if (lot != null && !lot.canPlayerAccess(player.getName())
                && mat != IdpMaterial.CHEST && mat != IdpMaterial.TRAPPED_CHEST
                && !player.hasPermission(Permission.command_admin_open_any_container)) {
            event.getPlayer().printError("Try as you might, you are unable to open this " + mat.getName() + "!");
            return true;
        }

        return false;
    }

    public boolean playerFlintAndSteelUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getBlockLot();

        if (lot != null && lot.canPlayerAccess(player.getName())
                && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.getSession().setUsingFintSteel(true);

            return true;
        }
        return false;
    }

    public boolean playerFishrodUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getBlockLot();

        // Teleporting caught entities to destination
        if (event.getBlockFace() == BlockFace.UP
                && player.getSession().getCaughtEntityTraits().size() > 0
                && player.getWorld().getSettings().getInventoryType() == InventoryType.MAIN) {

            if ((lot != null && lot.canPlayerManage(player.getName()))
                    || player.hasPermission(Permission.entity_catchentitiesoverride)) {
                if (lot != null && lot.isFlagSet(LotFlagType.NOMOBS)) {
                    player.printError("You cannot drop mobs off on this lot!");
                    return true;
                }

                List<EntityTraits> caughtEntityTraits = player.getSession().getCaughtEntityTraits();
                boolean renameOwners = player.getSession().getRenameOwners();

                Location blockLoc = LocationUtil.getCenterLocation(event.getBlock().getLocation());
                blockLoc.setY(blockLoc.getY() + 1); // Living entities would suffocate otherwise

                World world = blockLoc.getWorld();

                for (EntityTraits traits : caughtEntityTraits) {
                    EntityType type = traits.getType();
                    String typeName = "thing";

                    if (type != null) {
                        typeName = type.toString().toLowerCase();
                    }

                    Entity ent = world.spawnEntity(blockLoc, type);
                    traits.applyTraits(ent);

                    if (ent instanceof Tameable) {
                        Tameable tameable = (Tameable) ent;

                        if (tameable.isTamed() && renameOwners) {
                            tameable.setOwner(player.getHandle());
                            player.printInfo("This tamed " + typeName + " is now yours!");
                        }
                    }
                }

                player.getSession().removeCaughtEntityTraits();
                player.getSession().setRenameOwners(false);
                player.printInfo("All entities transported to their new location!");
                return true;
            }
        }

        return false;
    }

    public boolean playerFireworkUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        Block block = event.getBlock();
        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());

        // Used to restrict fireworks to only be
        // activated in lots allowed on.
        if (world.getWorldType() != IdpWorldType.RESWORLD
                && !event.getPlayer().hasPermission(Permission.world_build_unrestricted)) {

            Block placeBlock = block.getRelative(event.getBlockFace());
            IdpPlayer player = event.getPlayer();
            InnectisLot lot = LotHandler.getLot(placeBlock.getLocation(), true);

            if ((lot != null && !lot.canPlayerAccess(player.getName()))
                    && !lot.isFlagSet(LotFlagType.ALLOW_FIREWORKS)) {
                player.printError("You may not use a firework here!");
                return true;
            }
        }
        return false;
    }

    public boolean playerFireChargeUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();
        if (lot != null && !lot.canPlayerAccess(player.getName()) && !player.hasPermission(Permission.world_build_unrestricted)) {
            player.printError("You cannot place a fire charge here!");
            return true;
        }
        return false;
    }

    public boolean playerDiodeUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();
        IdpMaterial mat = IdpMaterial.fromBlock(event.getBlock());
        boolean isRepeater = (mat == IdpMaterial.REDSTONE_REPEATER_OFF
                || mat == IdpMaterial.REDSTONE_REPEATER_ON);

        //prevent redstone repeater/comparator tampering
        if (lot != null && !lot.canPlayerAccess(player.getName()) && !player.hasPermission(Permission.world_build_unrestricted)) {
            player.printError("You cannot change the state of someone elses " + (isRepeater ? "repeater" : "comparator") + "!");
            return true;
        }
        return false;
    }

    public boolean playerWallSignUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        EquipmentSlot handSlot = event.getHandSlot();
        IdpItemStack handStack = player.getItemInHand(handSlot);

        // Do not continue if the player is holding a sign
        if (handStack.getMaterial() == IdpMaterial.SIGN) {
            return false;
        }

        Block block = event.getBlock();
        Sign sign = (Sign) block.getState();
        WallSignType signType = WallSignType.fromSignText(sign.getLines());
        InnectisLot lot;

        if (signType != WallSignType.NONE) {
            if (signType.requiresConvert(sign.getLines())) {
                String firstLine = "[" + sign.getLine(0) + "]";
                sign.setLine(0, firstLine);
                sign.update();

                player.printInfo("Converted the sign to a proper sign.");
            }
        }

        switch (signType) {
            case ELEVATOR_DOWN:
            case ELEVATOR_UP:
                //<editor-fold defaultstate="collapsed" desc="Elevator code">
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block platformBlock = block.getRelative(BlockFace.DOWN);
                    IdpMaterial mat = IdpMaterial.fromBlock(platformBlock);

                    // Check for a valid elevator platform
                    if (!mat.isSolid()) {
                        platformBlock = platformBlock.getRelative(BlockFace.DOWN);
                        mat = IdpMaterial.fromBlock(platformBlock);

                        if (!mat.isSolid()) {
                            player.printError("Elevator platform not found!");
                            return true;
                        }
                    }

                    block = platformBlock;
                    BlockFace scanFace = (signType == WallSignType.ELEVATOR_UP ? BlockFace.UP : BlockFace.DOWN);

                    for (int i = 0; i < 50; i++) {
                        block = block.getRelative(scanFace);
                        mat = IdpMaterial.fromBlock(block);

                        if (mat.isSolid()) {
                            Block destBlock = block.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
                            IdpMaterial destMaterial = IdpMaterial.fromBlock(destBlock);

                            // Make sure the destination block to teleport to is valid and safe
                            if (!destMaterial.isSolid() && !destMaterial.isHazard()) {
                                destBlock = destBlock.getRelative(BlockFace.DOWN);
                                destMaterial = IdpMaterial.fromBlock(destBlock);

                                // Make sure the destination block to teleport to is valid and safe
                                if (!destMaterial.isSolid() && !destMaterial.isHazard()) {
                                    Location playerLoc = player.getLocation();

                                    float yaw = playerLoc.getYaw();
                                    float pitch = playerLoc.getPitch();

                                    Location destloc = destBlock.getLocation();
                                    destloc.setYaw(yaw);
                                    destloc.setPitch(pitch);

                                    player.teleport(destloc, TeleportType.IGNORE_RESTRICTION, TeleportType.PVP_IMMUNITY);
                                    player.print(ChatColor.AQUA, "Going " + scanFace.name().toLowerCase() + "!");

                                    return true;
                                }
                            }
                        }
                    }

                    player.printError("No suitable elevator destination found!");
                    return true;
                }

                break;
            //</editor-fold>
            case CHEST_SHOP:
                //<editor-fold defaultstate="collapsed" desc="Chest Shop code">
                ChestShopSign chestSign = SignValidator.getChestShopSign(sign.getLines());

                if (chestSign != null) {
                    Block chestBlock = sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
                    if (!(chestBlock.getState() instanceof Chest)) {
                        chestBlock = sign.getBlock().getRelative(BlockFace.DOWN);
                    }

                    if (chestBlock.getState() instanceof Chest) {
                        InnectisChest chest = ChestHandler.getChest(chestBlock.getLocation());

                        // Make sure the chest is a valid chest
                        if (chest != null) {
                            if (!chest.getOwnerCredentials().isValidPlayer()) {
                                player.printError("This chest shop is invalid as the owner is not valid!");
                                return true;
                            }

                            if (player.getWorld().getWorldType() == IdpWorldType.CREATIVEWORLD) {
                                player.printError("You cannot use this chest shop here!");
                                return true;
                            }

                            ChestShop chestshop = new ChestShop(player, chest, chestSign);
                            chestshop.processTransaction();

                            PurchaseResult result = chestshop.getResult();

                            // The successful purchase result is special, as it requires
                            // four pieces of information to display to the person interacting
                            // with the chest shop
                            if (result == PurchaseResult.SUCCESSFUL_PURCHASE) {
                                String resultString = result.getResultString();
                                String typeShop = (chestSign.getShopType() == ChestShopType.BUY ? "Sold" : "Bought");
                                player.printInfo(StringUtil.format(resultString, typeShop, chestSign.getAmount(), chestSign.getMaterial().getName().toLowerCase(), chestSign.getCost()));

                                IdpContainer container = new IdpContainer(chest.getInventory());
                                int count = container.countMaterial(chestSign.getMaterial());
                                sign.setLine(3, "Count: " + count);
                                sign.update();
                            } else {
                                player.printError(result.getResultString());
                            }
                        } else {
                            player.printError("This is not a valid chest shop.");
                        }
                    }
                } else {
                    player.printError("This is not a valid chest shop.");
                }

                return true;
            //</editor-fold>
            case STASH_SIGN:
                //<editor-fold defaultstate="collapsed" desc="Stash sign code">
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block chestBlock = sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
                    if (!(chestBlock.getState() instanceof Chest)) {
                        chestBlock = sign.getBlock().getRelative(BlockFace.DOWN);
                    }
                    IdpMaterial mat = IdpMaterial.fromBlock(chestBlock);

                    if (!VanillaChestType.isValidChestBlock(mat)) {
                        player.printError("There is no chest associated with this stash sign!");
                        return true;
                    }

                    Chest chest = (Chest) chestBlock.getState();
                    InnectisChest innChest = ChestHandler.getChest(chestBlock.getLocation());

                    if (innChest == null) {
                        player.printError("This chest isn't owned by anyone!");
                        return true;
                    }

                    if (!(innChest.canPlayerAccess(player.getName())
                            || player.hasPermission(Permission.special_chest_stashall))) {
                        player.printError("You do not own this chest.");
                        return true;
                    }

                    mat = IdpMaterial.fromString(sign.getLine(1));
                    boolean useArmor = false;
                    boolean useBackpack = false;
                    boolean useInventory = false;
                    boolean useHand = false;
                    boolean useHotbar = false;

                    // Check for extra information if not using a single material
                    if (mat == null) {
                        boolean stashPlayer = sign.getLine(1).equalsIgnoreCase("player");

                        if (stashPlayer) {
                            IdpPlayerInventory inv = player.getInventory();
                            IdpContainer playerContainer = new IdpContainer(inv.getItems());
                            playerContainer.addMaterial(inv.getOffHandItem()[0]);

                            IdpContainer chestContainer = new IdpContainer(chest.getInventory());

                            IdpContainerTransfer transfer = new IdpContainerTransfer(chestContainer, playerContainer);
                            int added = transfer.process();

                            if (added > 0) {
                                Map<IdpMaterial, Integer> addedMaterial = transfer.getMaterialAdded();
                                String addedString = "";

                                for (Map.Entry<IdpMaterial, Integer> entry : addedMaterial.entrySet()) {
                                    IdpMaterial material = entry.getKey();
                                    int count = entry.getValue();

                                    if (!addedString.isEmpty()) {
                                        addedString += ", ";
                                    }

                                    addedString += count + " " + material;
                                }

                                player.printInfo("Stashed " + added + " items into your inventory!");
                                player.printInfo("Items stashed: " + addedString);

                                inv.setItems(playerContainer.getNonArmorItems());
                                inv.setOffHandItem(playerContainer.getItemAt(36));
                                inv.updateBukkitInventory();

                                Inventory chestInv = chest.getInventory();
                                chestInv.setContents(chestContainer.getBukkitItems());
                                chest.update();
                            } else {
                                player.printError("No items were stashed to your inventory.");
                            }

                            return true;
                        } else {
                            useArmor = (sign.getLine(1).equalsIgnoreCase("armor")
                                    || sign.getLine(1).equalsIgnoreCase("all"));
                            useBackpack = sign.getLine(1).equalsIgnoreCase("backpack");
                            useInventory = (sign.getLine(1).equalsIgnoreCase("inventory")
                                    || sign.getLine(1).equalsIgnoreCase("all"));
                            useHand = sign.getLine(1).equalsIgnoreCase("hand");
                            useHotbar = sign.getLine(2).equalsIgnoreCase("allowhotbar");

                            // Using hand will always use hotbar
                            if (useHand) {
                                useHotbar = true;
                            }

                            if (!(useArmor || useInventory || useHand || useBackpack)) {
                                player.printError("This stash sign is not formatted correctly.");
                                return true;
                            }
                        }
                    } else {
                        // Allow hotbar if material is used
                        useHotbar = true;
                    }

                    if (useHand) {
                        mat = handStack.getMaterial();

                        if (mat == null || mat == IdpMaterial.AIR) {
                            handStack = player.getItemInHand(EquipmentSlot.OFF_HAND);
                            mat = (handStack != null ? handStack.getMaterial() : null);
                        }

                        if (mat == null || mat == IdpMaterial.AIR) {
                            player.printError("You are not holding an item!");
                            return true;
                        }
                    }

                    IdpContainer chestContainer = new IdpContainer(chest.getInventory());
                    IdpContainer playerContainer = null;
                    IdpPlayerInventory inv = player.getInventory();
                    IdpContainerTransfer transfer = new IdpContainerTransfer();
                    int added = 0;

                    if (useArmor) {
                        playerContainer = new IdpContainer(inv.getArmorItems());

                        transfer.setSourceContainer(playerContainer);
                        transfer.setDestinationContainer(chestContainer);
                        int addedFromArmor = transfer.process();

                        if (addedFromArmor > 0) {
                            added += addedFromArmor;
                            inv.setArmorItems(playerContainer.getItems());
                            inv.updateBukkitInventory();
                        }
                    }

                    // If single item, or inventory is used
                    if (mat != null || useInventory) {
                        playerContainer = new IdpContainer(inv.getItems());
                        playerContainer.addMaterial(inv.getOffHandItem()[0]);

                        transfer.setSourceContainer(playerContainer);
                        transfer.setDestinationContainer(chestContainer);

                        int addedFromInventory = 0;

                        if (!useHotbar) {
                            addedFromInventory = transfer.process(mat, 0, 8);
                        } else {
                            addedFromInventory = transfer.process(mat);
                        }

                        if (addedFromInventory > 0) {
                            added += addedFromInventory;
                            inv.setItems(playerContainer.getNonArmorItems());
                            inv.setOffHandItem(playerContainer.getItemAt(36));
                            inv.updateBukkitInventory();
                        }
                    }

                    if (useBackpack) {
                        if (!player.hasPermission(Permission.special_usebackpack)) {
                            player.printError("You cannot use a backpack!");
                            return true;
                        }

                        PlayerBackpack backpack = player.getSession().getBackpack();

                        if (backpack.isEmpty()) {
                            player.printError("Your backpack is empty!");
                            return true;
                        }

                        IdpContainer backpackContainer = new IdpContainer(backpack.getItems());

                        transfer.setSourceContainer(backpackContainer);
                        transfer.setDestinationContainer(chestContainer);

                        int addedFromBackpack = transfer.process();

                        if (addedFromBackpack > 0) {
                            added += addedFromBackpack;
                            backpack.setItems(backpackContainer.getItems());
                            backpack.save();
                        }
                    }

                    if (added > 0) {
                        player.printInfo("Stashed " + added + " items into the chest!");

                        String addedString = "";

                        for (Map.Entry<IdpMaterial, Integer> entry : transfer.getMaterialAdded().entrySet()) {
                            IdpMaterial material = entry.getKey();
                            int count = entry.getValue();

                            if (!addedString.isEmpty()) {
                                addedString += ", ";
                            }

                            addedString += count + " " + material;
                        }

                        player.printInfo("Items stashed: " + addedString);

                        Inventory chestInv = chest.getInventory();
                        chestInv.setContents(chestContainer.getBukkitItems());
                        chest.update();
                    } else {
                        player.printError("No items were stashed to the chest.");
                    }

                    return true;
                }

                break;
            //</editor-fold>
            case BOUNCE:
                //<editor-fold defaultstate="collapsed" desc="Bounce code">

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    lot = event.getBlockLot();

                    if (lot != null && lot.isFlagSet(LotFlagType.BOUNCE)) {
                        int height = 3;
                        String error = null;

                        if (!sign.getLine(1).isEmpty()) {
                            try {
                                height = Integer.parseInt(sign.getLine(1));
                            } catch (NumberFormatException ex) {
                                error = "Jump height not properly stated. Using 3.";
                            }
                            if (error == null) {
                                if (height < 1) {
                                    error = "Jump height cannot be below 1, using 1 instead.";
                                    height = 1;
                                } else if (height > 10) {
                                    error = "Max jump height exceeded. Using a value of 10.";
                                    height = 10;
                                }
                            }
                        }

                        if (player.getHandle().isInsideVehicle()) {
                            Vector speed = player.getHandle().getVehicle().getVelocity();
                            speed.setY(height);
                            player.getHandle().getVehicle().setVelocity(speed);
                        } else {
                            Vector speed = player.getHandle().getVelocity();
                            speed.setY(height);
                            player.getHandle().setVelocity(speed);
                        }
                        if (error == null) {
                            player.print(ChatColor.AQUA, "You have been propelled in the air!");
                        } else {
                            player.printError(error);
                        }
                        player.getSession().setJumped(true);
                        return true;
                    } else {
                        player.printError("This sign is not on a Bounce lot.");
                    }
                }
                break;
            //</editor-fold>
            case KICKER:
                //<editor-fold defaultstate="collapsed" desc="Kicker code">

                String[] lines = sign.getLines();

                // Check if there are any lines
                if (StringUtil.stringIsNullOrEmpty(lines[1])
                        || StringUtil.stringIsNullOrEmpty(lines[2])
                        || StringUtil.stringIsNullOrEmpty(lines[3])) {
                    return false;
                }

                String targetline1 = "" + ChatColor.EFFECT_CLEAR + ChatColor.RED + ChatColor.WHITE + ChatColor.BLUE + ChatColor.EFFECT_CLEAR;
                String targetline2 = "" + ChatColor.EFFECT_ITALIC + ChatColor.RED + ChatColor.BLUE + ChatColor.YELLOW + ChatColor.EFFECT_CLEAR;
                String targetline3 = "" + ChatColor.GOLD + ChatColor.GREEN + ChatColor.DARK_GRAY + ChatColor.DARK_PURPLE + ChatColor.DARK_RED;

                // Check if lines are the correct passphrase
                if (!lines[1].equalsIgnoreCase(targetline1)
                        || !lines[2].equalsIgnoreCase(targetline2)
                        || !lines[3].equalsIgnoreCase(targetline3)) {
                    return false;
                }

                // Well... Sign's alright, so Derpy go on!
                player.getHandle().kickPlayer("Derpy: Oops. My bad.");
                return true;
            //</editor-fold>
            case GIVE:
                //<editor-fold defaultstate="collapsed" desc="Give code">
                lot = event.getBlockLot();

                if (player.getSession().getLastSignInteract() + Configuration.SIGN_INTERACT_DELAY > System.currentTimeMillis()) {
                    player.printError("You cannot use this sign again just yet!");
                    return true;
                }

                if (lot == null) {
                    player.printError("Give sign is not on a lot. Cannot use!");
                    return true;
                }

                int amount = 0;

                try {
                    String line = sign.getLine(1).toLowerCase().replace("vt", "");
                    amount = Integer.parseInt(line.trim());

                    if (amount < 1) {
                        player.printError("This give sign cannot give below 1 valuta.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("The amount is not formatted correctly.");
                    return true;
                }

                TransactionObject transactionSender = TransactionHandler.getTransactionObject(player);
                int balance = transactionSender.getValue(TransactionType.VALUTAS);

                if (balance < amount) {
                    player.printError("You do not have enough valutas to give.");
                    return true;
                }

                PlayerCredentials credentials = lot.getOwnerCredentials();
                TransactionObject transactionReceiver = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());

                // Lot owner did not represent an actual player
                if (transactionReceiver == null) {
                    player.printError("Could not give the specified money. Owner is not valid!");
                    return true;
                }

                transactionSender.subtractValue(amount, TransactionType.VALUTAS);
                transactionReceiver.addValue(amount, TransactionType.VALUTAS);

                player.printInfo("You gave " + lot.getOwner() + " " + amount + " valuta" + (amount != 1 ? "s" : "") + " using a give sign!");
                player.getSession().setLastSignInteract(System.currentTimeMillis());

                World bukkitWorld = player.getHandle().getWorld();
                bukkitWorld.playEffect(sign.getLocation(), Effect.SMOKE, 0);

                IdpPlayer playerReceiver = plugin.getPlayer(lot.getOwner());

                // Notify if player is online
                if (playerReceiver != null) {
                    playerReceiver.printInfo(player.getName() + " just gave you " + amount + " valuta" + (amount != 1 ? "s" : "") + " using a give sign!");
                }

                // Trigger a repeater attached to the block under the block the sign is attached to
                Block attachedBlock = block.getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
                Block repeaterBlock = BlockHandler.getBlockAttached(attachedBlock.getRelative(BlockFace.DOWN), IdpMaterial.REDSTONE_REPEATER_OFF, false);

                if (repeaterBlock != null) {
                    byte oldData = BlockHandler.getBlockData(repeaterBlock);
                    BlockHandler.setBlock(repeaterBlock, IdpMaterial.REDSTONE_REPEATER_ON, oldData);
                }

                return true;
            //</editor-fold>
            case BANK:
                //<editor-fold defaultstate="collapsed" desc="Bank Sign">
                lot = event.getBlockLot();

                if (lot == null || !lot.isFlagSet(LotFlagType.BANKLOT)) {
                    player.printError("You may not use this bank sign here.");
                    return true;
                }

                if (handStack.getMaterial() == IdpMaterial.PAPER) {
                    ItemData data = handStack.getItemdata();

                    if (data.getSpecialItem() == SpecialItemType.BANK_NOTE) {
                        int noteAmount = 0;

                        try {
                            noteAmount = Integer.parseInt(data.getValue("ValutaAmount"));
                        } catch (NumberFormatException nfe) {
                            player.printError("There was a problem handling this bank note!");
                            return true;
                        }

                        int itemAmount = handStack.getAmount();
                        ChatColor amountColor = null;

                        if (noteAmount == 10000) {
                            amountColor = ChatColor.AQUA;
                        } else if (noteAmount == 1000) {
                            amountColor = ChatColor.GOLD;
                        } else if (noteAmount == 500) {
                            amountColor = ChatColor.DARK_PURPLE;
                        } else if (noteAmount == 100) {
                            amountColor = ChatColor.DARK_AQUA;
                        } else {
                            amountColor = ChatColor.YELLOW;
                        }

                        TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                        transaction.addValue((noteAmount * itemAmount), TransactionType.VALUTAS);

                        player.printInfo("You have just received " + amountColor + (itemAmount * noteAmount),
                                " valutas from " + itemAmount + " bank note" + (itemAmount != 1 ? "s" : "") + "!");

                        player.setItemInHand(handSlot, IdpItemStack.EMPTY_ITEM);

                        return true;
                    }
                }

                PlayerSession session = player.getSession();
                long bankTaskId = session.getBankTaskId();

                if (bankTaskId == 0) {
                    player.printError("You have no pending transactions at this time.");
                    return true;
                }

                TransactionObject transaction = TransactionHandler.getTransactionObject(player);

                int vTToBank = transaction.getValue(TransactionType.VALUTAS_TO_BANK);

                if (vTToBank > 0) {
                    transaction.setValue(0, TransactionType.VALUTAS_TO_BANK);
                    transaction.addValue(vTToBank, TransactionType.VALUTAS_IN_BANK);

                    player.printInfo("Successfully deposited " + vTToBank + " valuta"
                            + (vTToBank != 1 ? "s" : "") + " into the bank!");
                }

                int vTToPlayer = transaction.getValue(TransactionType.VALUTAS_TO_PLAYER);

                if (vTToPlayer > 0) {
                    transaction.setValue(0, TransactionType.VALUTAS_TO_PLAYER);
                    transaction.addValue(vTToPlayer, TransactionType.VALUTAS);

                    player.printInfo("Successfully withdrew " + vTToPlayer + " valuta"
                            + (vTToPlayer != 1 ? "s" : "") + " from the bank!");
                }

                TaskManager manager = plugin.getTaskManager();
                manager.removeTask(bankTaskId);

                session.setLastBankTaskTime(0);
                session.setBankTaskId(0);

                return true;
            //</editor-fold>
        }

        return false;
    }

    public boolean playerJukeboxUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();

        //prevent ejecting discs to jukeboxes on lots you aren't a member of
        if (!player.hasPermission(Permission.owned_object_override)) {
            if (lot != null && !lot.canPlayerAccess(player.getName())) {
                player.printError("You may not interact with this jukebox!");
                return true;
            }
        }
        return false;
    }

    public boolean playerNoteBlockUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();

        //prevent note block tweaking
        if (lot != null && !lot.canPlayerAccess(player.getName()) && !player.hasPermission(Permission.world_build_unrestricted)) {
            player.printError("Tisk tisk, don't tamper with someone else's tunes!");
            return true;
        }

        return false;
    }

    public boolean playerBonemealUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Check if bonemeal can be used on the target material
        if (mat.isGrowableFromBonemeal() || mat.isPaintableWithBonemeal()) {
            IdpPlayer player = event.getPlayer();
            InnectisLot lot = event.getBlockLot();
            Location loc = block.getLocation();

            // Is this a hidden lot?
            if (lot == null) {
                lot = LotHandler.getLot(loc, true);
            }

            // Check access
            if (lot != null && !lot.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("You may not use bonemeal here!");
                return true;
            }

            // Using bonemeal on a color wool block will turn it into its base material
            if (mat.isPaintableWithBonemeal()) {
                IdpMaterial baseMaterial = mat.getBaseMaterial();
                BlockHandler.setBlock(block, baseMaterial);

                player.printInfo("You bleached this " + mat.getName() + ".");
                return true;
            }
        }

        return false;
    }

    public boolean playerVehiclePlace(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpMaterial mat = event.getItem().getMaterial();
        Block block = event.getBlock();
        IdpMaterial blockMaterial = IdpMaterial.fromBlock(block);

        switch (mat) {
            case MINECART:
            case POWERED_MINECART:
            case STORAGE_MINECART:
            case TNT_MINECART:
            case HOPPER_MINECART:
                // Don't process if trying to place a minecart where there are no rails
                if (!(blockMaterial == IdpMaterial.RAILS
                        || blockMaterial == IdpMaterial.ACTIVATOR_RAIL
                        || blockMaterial == IdpMaterial.POWERED_RAIL
                        || blockMaterial == IdpMaterial.DETECTOR_RAIL)) {
                    return true;
                }
        }

        IdpPlayer player = event.getPlayer();
        InnectisLot lot = event.getBlockLot();

        if (lot != null && lot.isFlagSet(LotFlagType.RESTRICTVEHICLES)
                && !lot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.world_build_unrestricted)) {
            player.printError("You may not place a vehicle here.");
            player.updateInventory(); // Client will think there is no item in hand otherwise

            return true;
        }

        EquipmentSlot handSlot = event.getHandSlot();

        // if minecart hopper, let's spawn it so we can get the UUID and player
        // (this is not available in VehicleCreateEvent, so we do it here instead)
        if (mat == IdpMaterial.HOPPER_MINECART) {
            HopperMinecart hopperMinecart = (HopperMinecart) player.getLocation().getWorld().spawnEntity(block.getLocation(), EntityType.MINECART_HOPPER);
            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(player.getUniqueId());
            OwnedEntityHandler.addOwnedEntity(hopperMinecart.getUniqueId(), credentials, EntityType.MINECART_HOPPER);
            player.setItemInHand(handSlot, IdpItemStack.EMPTY_ITEM);

            return true;
        }

        return false;
    }

    public boolean playerSoilTramp(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();

        //prevent farmland trampling
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getBlock();
            player.sendBlockChange(block);
            return true;
        }
        return false;
    }

    public boolean playerChestUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        InnectisChest chest = ChestHandler.getChest(event.getBlock().getLocation());

        InnectisLot chestlot = event.getBlockLot();

        if (chest == null) {
            if (chestlot == null) {
                // chest wasn't in database, and not on a lot
                // let them open it (eg it may be a dungeon chest)
                return true;
            } else {
                // chest wasn't in database, but ON a lot
                // dont let them open it, even if they are the lot owner
                // they need to destroy and remake it to repair database sync
                player.printError("Internal error, chest not found!");
                player.printError("Destroy the chest and remake it if possible.");

                event.setCancelled(true);
                return true;
            }
        }

        // --------------------------------------------------
        // Now we are sure there is a chest and a player owns it
        // --------------------------------------------------
        if (chest.canPlayerAccess(player.getName())
                || (chest.isFlagSet(ChestFlagType.LOT_MEMBER_CHEST) && chestlot != null
                && (chestlot.containsMember(player.getName()) || chestlot.containsOperator(player.getName())))) {
            // Auto-refill chests
            if (chest.isFlagSet(ChestFlagType.AUTO_REFILL)) {
                chest.logChestAccess(player.getUniqueId());

                IdpInventory checkInv = chest.getInventory("Auto-refill");
                checkInv.setPayload(new AutoRefillInventoryPayload(checkInv.getSize()));
                player.openInventory(checkInv);

                event.setCancelled(true);
                return true;
            }

        } else if (player.hasPermission(Permission.owned_object_override)) {
            player.printInfo("You forced open chest #" + chest.getId() + ".");

            // Auto-refill chests
            if (chest.isFlagSet(ChestFlagType.AUTO_REFILL)) {
                chest.logChestAccess(player.getUniqueId());

                IdpInventory checkInv = chest.getInventory("Auto-refill");
                checkInv.setPayload(new AutoRefillInventoryPayload(checkInv.getSize()));
                player.openInventory(checkInv);

                event.setCancelled(true);
                return true;
            }

        } else if (chestlot != null && chestlot.canPlayerManage(player.getName())) {
            // Lets open up a read-only chest.
            IdpInventory showcase = chest.getInventory("Read-Only Chest");
            showcase.setPayload(new ShowcaseInventoryPayload());
            player.openInventory(showcase);
            player.printInfo("You open " + chest.getOwner() + "'s chest in read-only!");
            event.setCancelled(true);
            return true;

        } else if (chest.isFlagSet(ChestFlagType.SHOWCASE)) {
            // Lets open up a read-only chest.
            IdpInventory showcase = chest.getInventory("Showcase Chest");
            showcase.setPayload(new ShowcaseInventoryPayload());
            player.openInventory(showcase);
            player.printInfo("You open " + chest.getOwner() + "'s showcase!");
            event.setCancelled(true);
            return true;
        } else {
            player.printError("This chest is locked!");
            event.setCancelled(true);
            return true;
        }

        // --------------------------------------------------
        // At this point, we are sure the player has access
        // --------------------------------------------------
        // Log the access
        chest.logChestAccess(player.getUniqueId());

        if (!player.getSession().isVisible()) {
            player.openInventory(chest.getInventory());
            event.setCancelled(true);
            return true;
        }

        // Trigger if flag set
        if (chest.isFlagSet(ChestFlagType.INTERACT_CURRENT)) {
            triggerChestRepeater(event.getBlock());
        }

        return true;
    }

    private void triggerChestRepeater(Block chest) {
        Block repeater = BlockHandler.getBlockAttached(chest.getRelative(BlockFace.DOWN), IdpMaterial.REDSTONE_REPEATER_OFF, false);

        if (repeater != null) {
            BlockHandler.setBlock(repeater, IdpMaterial.REDSTONE_REPEATER_ON);
        }
    }

    public boolean spawnEggUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        // Check if player has global access as potential access if the
        // egg is a special egg
        boolean globalPermission = player.hasPermission(Permission.world_build_unrestricted);
        ItemData itemData = event.getItem().getItemdata();
        boolean special = itemData.hasValue("specialEgg");

        if (!globalPermission && !special) {
            player.printError("Unable to use mob egg!");
            return true;
        }

        // Only check special egg if player does not have global permission
        if (special && !globalPermission) {
            Block block = event.getBlock();
            InnectisLot checkLot = LotHandler.getLot(block.getLocation());

            if (checkLot == null || !checkLot.canPlayerAccess(player.getName())) {
                player.printError("You cannot use this mob egg here!");
                return true;
            }
        }

        Location blockLocation = event.getBlock().getLocation();
        BlockFace clickedFace = event.getBlockFace();
        Location finalLocation = LocationUtil.getCenterLocation(blockLocation.add(clickedFace.getModX(), clickedFace.getModY(), clickedFace.getModZ()));

        String name = itemData.getValue("EntityTag/id");
        EntityType entityType = EntityType.fromName(name.replace("minecraft:", ""));
        player.getWorld().getHandle().spawnEntity(finalLocation, entityType);

        if (player.getHandle().getGameMode() != GameMode.CREATIVE) {
            EquipmentSlot handSlot = event.getHandSlot();
            IdpItemStack handStack = player.getItemInHand(handSlot);

            if (handStack.getAmount() > 1) {
                handStack.setAmount(handStack.getAmount() - 1);
            } else {
                handStack = IdpItemStack.EMPTY_ITEM;
            }

            player.setItemInHand(handSlot, handStack);
        }

        return true;
    }

    public boolean playerEnderChestUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        EnderContentsType type = player.getWorld().getSettings().getEnderchestType();
        IdpMaterial mat = IdpMaterial.fromBlock(event.getBlock());

        if (type == EnderContentsType.NONE) {
            event.getPlayer().printError("This " + mat.getName() + " doesn't seem to be working.");
            return true;
        }

        PlayerSession session = player.getSession();
        session.setEnderchestOwnerId(player.getUniqueId());
        session.setEnderchestType(type);
        session.setViewingEnderChest(true);

        return false;
    }

    public boolean playerDoorUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();

        // For some reason, levers only call player interact event with
        // the off-hand and not main hand, so make an exception for
        // them here
        if (event.getHandSlot() == EquipmentSlot.OFF_HAND) {
            EquipmentSlot nonEmptySlot = player.getNonEmptyHand();

            if (nonEmptySlot != null) {
                IdpItemStack handStack = player.getItemInHand(nonEmptySlot);

                if (handStack.getMaterial() != IdpMaterial.LEVER) {
                    return true;
                }
            } else {
                return true;
            }
        }

        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.IRON_DOOR_BLOCK) {
            InnectisDoor door = DoorHandler.getDoor(event.getBlock().getLocation());
            if ((door == null || !door.canPlayerAccess(player.getName()))
                    && !player.hasPermission(Permission.owned_object_override)) {
                player.printError("This door is locked!");
            } else {
                DoorHandler.toggleDoor(block);
                Door doorState = (Door) block.getState().getData();
                Sound doorSound = (doorState.isOpen() ? Sound.BLOCK_IRON_DOOR_CLOSE : Sound.BLOCK_IRON_DOOR_OPEN);
                Location doorLocation = block.getLocation();

                player.getHandle().playSound(doorLocation, doorSound, 1, 1);
                List<IdpPlayer> players = player.getNearByPlayers(16);

                for (IdpPlayer p : players) {
                    p.getHandle().playSound(doorLocation, doorSound, 1, 1);
                }
            }
        } else {
            DoorHandler.toggleDoor(event.getBlock());
        }

        return true;
    }

    public boolean playerWaypointUse(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        IdpPlayer player = event.getPlayer();
        Block block = event.getBlock();

        InnectisWaypoint waypoint = WaypointHandler.getWaypoint(block.getLocation());

        if (waypoint != null) {
            // Not in end
            if (player.getWorld().getSettings().getMaptype() == MapType.THE_END) {
                player.printError("A strange power is preventing you from doing that.");
                return true;
            }

            if (!waypoint.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.owned_object_override)) {
                player.printError("You cannot use that waypoint!");
            } else {
                Location target = waypoint.getDestination();
                if (target != null) {
                    if (!target.getBlock().equals(block)) {
                        if (player.teleport(target, TeleportType.IGNORE_RESTRICTION, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY)) {
                            player.printInfo("Warping to " + ChatColor.AQUA + LocationUtil.locationString(target), ".");

                            // Force an update of their inventory. This will make it so
                            // the player's client doesn't remove a block from their
                            // inventory incorrectly
                            player.updateInventory();
                        }
                    } else {
                        player.printError("This waypoint does not go anywhere!");

                        if (waypoint.canPlayerManage(player.getName())
                                || player.hasPermission(Permission.owned_object_override)) {
                            player.printInfo("Set a location with /wpset " + waypoint.getId() + ".");
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    public boolean playerLeverUse(InnPlayerInteractEvent event) {
        Block block = event.getBlock();
        byte dat = BlockHandler.getBlockData(block);

        // Bridges:
        if (dat > 8) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return false;
            }

            leverOnAction(block);
        } else {
            leverOffAction(block);
        }

        Location loc = block.getLocation();

        // Switches
        if (event.getItem().getMaterial() == IdpMaterial.REDSTONE_BLOCK) {
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(event.getPlayer().getName());
            InnectisSwitch.createOrGetSwitch(loc, credentials);
        } else {
            InnectisSwitch st = InnectisSwitch.getSwitch(loc);
            if (st != null) {
                st.toggleLinkedSwitches();
            }
        }

        return false;
    }

    private BlockFace getLeverDirection(int data) {
        BlockFace bf = null;
        switch (data) {
            case 3:
                bf = BlockFace.SOUTH;
                break;
            case 1:
                bf = BlockFace.EAST;
                break;
            case 4:
                bf = BlockFace.NORTH;
                break;
            case 2:
                bf = BlockFace.WEST;
                break;
        }
        return bf;
    }

    private void leverOffAction(Block lever) {
        byte dat = BlockHandler.getBlockData(lever);
        BlockFace bf = getLeverDirection(dat);
        if (bf == null) {
            return;
        }

        // Get the block attached to the lever
        Block attachedBlock = lever.getRelative(bf.getOppositeFace());

        // Blockfaces
        BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

        Block block, block2;
        // Check blocks close to the lever from top-down
        for (int i = 5; i > -6; i--) {
            block = attachedBlock.getRelative(0, i, 0);

            // No block, continue
            if (block == null) {
                continue;
            }
            // around the block
            for (BlockFace face : blockFaces) {
                // Get the surrounding block
                block2 = block.getRelative(face);
                IdpMaterial blockMat = IdpMaterial.fromBlock(block2);
                boolean valid = GateHandler.isValidGateMaterial(blockMat);

                // Check if it is a gate
                if (valid) {
                    // Close gate
                    GateHandler.openGate(block2, face);
                }
                // Check for bridge
                if (blockMat == BridgeHandler.CONTROLBLOCK_MATERIAL
                        && (BlockHandler.getIdpBlockData(block2.getLocation()).isBridgeController())) {
                    try {
                        // Lever must be attached to the bridge block
                        if (BlockHandler.isBlockAdjacent(attachedBlock, block2)) {
                            BridgeHandler.openBridge(block2);
                        }
                    } catch (Exception ex) {
                        InnPlugin.logError("A bridge has crashed!", ex);
                    }
                }
            }
        }
    }

    private void leverOnAction(Block lever) {
        byte dat = BlockHandler.getBlockData(lever);
        BlockFace bf = getLeverDirection(dat - 8);
        if (bf == null) {
            return;
        }

        // Get the block attached to the lever
        Block attachedBlock = lever.getRelative(bf.getOppositeFace());

        // Blockfaces
        BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

        Block block, block2;
        // Check blocks close to the lever from top-down
        for (int i = 5; i > -6; i--) {
            block = attachedBlock.getRelative(0, i, 0);

            // around the block
            for (BlockFace face : blockFaces) {
                // Get the surrounding block
                block2 = block.getRelative(face);
                IdpMaterial mat = IdpMaterial.fromBlock(block2);
                boolean valid = GateHandler.isValidGateMaterial(mat);
                Location loc = block2.getLocation();

                // Check if it is a gate && the fence is not virtual
                if (valid && !BlockHandler.getIdpBlockData(loc).isVirtualBlock()) {
                    // Close gate
                    InnectisLot lot = LotHandler.getLot(loc, true);
                    boolean useBig = false;

                    if (lot != null) {
                        // Do not form a gate if NoStructure flag is set
                        if (lot.isFlagSet(LotFlagType.NOSTRUCTURE)) {
                            return;
                        }

                        useBig = lot.isFlagSet(LotFlagType.BIGSTRUCTURE);
                    }

                    GateHandler.closeGate(block2, face, useBig);
                }

                // Check for bridge
                if (mat == BridgeHandler.CONTROLBLOCK_MATERIAL
                        && !BlockHandler.getIdpBlockData(block.getLocation()).isBridgeController()) {
                    try {
                        for (int j = 0; j < 10 + i; j++) {
                            block2 = block2.getRelative(BlockFace.DOWN);
                            mat = IdpMaterial.fromBlock(block2);
                            IdpBlockData blockData = BlockHandler.getIdpBlockData(block2.getLocation());

                            if (!(mat == BridgeHandler.CONTROLBLOCK_MATERIAL && !blockData.isBridgeController())) {
                                block2 = block2.getRelative(BlockFace.UP);

                                break;
                            }
                        }

                        // Lever must be attached to the bridge block
                        if (BlockHandler.isBlockAdjacent(attachedBlock, block2)) {
                            BridgeHandler.closeBridge(block2);
                        }
                    } catch (Exception ex) {
                        InnPlugin.logError("A bridge as crashed.", ex);
                    }
                }
            }
        }
    }

    public boolean playerFireExtinguish(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();

        //Prevents a player from putting a fire out on a lot
        //they do not have build rights on
        if (lot != null) {
            if (!lot.canPlayerAccess(player.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean playerFootballPush(InnPlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }

        InnectisLot lot = event.getBlockLot();
        IdpPlayer player = event.getPlayer();

        if (lot != null && lot.isFlagSet(LotFlagType.FOOTBALLBLOCKS)) {
            pushBlock(lot, player, event.getBlock(), event.getBlockFace());
            return true;
        }

        return false;
    }

    private void pushBlock(InnectisLot lot, IdpPlayer player, Block block, BlockFace face) {
        Location loc = block.getLocation();

        // No virtual Material
        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

        if (blockData.isVirtualBlock()) {
            return;
        }
        // Dont move locked blocks
        if (blockData.isUnbreakable()) {
            return;
        }
        // Checks if the playerFootballPush can move blocks again
        if (!player.getSession().canMoveBlockAgain()) {
            return;
        }

        // Invert
        face = face.getOppositeFace();
        // Check face allowedx
        switch (face) {
            case EAST:
            case WEST:
            case NORTH:
            case SOUTH:
                break;
            case DOWN:
                // because its inverted
                Location blockloc = block.getLocation();
                Location playerLocation = player.getLocation();

                int changeX = blockloc.getBlockX() - playerLocation.getBlockX();
                int changeZ = blockloc.getBlockZ() - playerLocation.getBlockZ();

                // Too close, ignore
                if (changeX == 0 && changeZ == 0) {
                    return;
                }

                if (Math.abs(changeX) > Math.abs(changeZ)) {
                    if (changeX >= 0) {
                        face = BlockFace.SOUTH;
                    } else {
                        face = BlockFace.NORTH;
                    }
                } else {
                    if (changeZ >= 0) {
                        face = BlockFace.WEST;
                    } else {
                        face = BlockFace.EAST;
                    }
                }

                break;
            default: // Disallow Others
                return;
        }
        Block target = block.getRelative(face);

        // Get lot other block
        InnectisLot tolot = LotHandler.getLot(target.getLocation());
        if (tolot != null && tolot.getId() == lot.getId()) {
            IdpMaterial mat = IdpMaterial.fromBlock(target);

            // Only move blocks up if space
            if (!canPushBlockTo(mat)) {
                // check if block should be pushed up!
                target = target.getRelative(BlockFace.UP);
                mat = IdpMaterial.fromBlock(target);
                IdpMaterial aboveMaterial = IdpMaterial.fromBlock(block.getRelative(BlockFace.UP));

                if (!canPushBlockTo(mat) || !canPushBlockTo(aboveMaterial)) {
                    // No room or blocked on top
                    return;
                }
            } else {
                // Push block down if empty space
                while (canPushBlockTo(IdpMaterial.fromBlock(target.getRelative(BlockFace.DOWN)))) {
                    // protection just to be sure
                    if (target.getLocation().getBlockY() > 1) {
                        target = target.getRelative(BlockFace.DOWN);
                    } else {
                        return;
                    }
                }
            }
        } else {
            // Not same lot
            return;
        }
        // Tell that the playerFootballPush has moved a block!
        player.getSession().blockMoved();

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Update Blocks (first update old block to prevent any dupes)
        BlockHandler.setBlock(block, IdpMaterial.AIR);
        BlockHandler.setBlock(target, mat);

        IdpMaterial targetMaterial = IdpMaterial.fromBlock(target);

        // Drop blocks without support
        target = block;
        Block target2;
        while (IdpMaterial.fromBlock((target2 = target.getRelative(BlockFace.UP))) == targetMaterial
                && !BlockHandler.getIdpBlockData(target2.getLocation()).isVirtualBlock()) {
            // First update new block to prevent any duplication
            BlockHandler.setBlock(target2, IdpMaterial.AIR);
            BlockHandler.setBlock(target, mat);

            // Update block
            target = target2;
        }
    }

    public boolean canPushBlockTo(IdpMaterial mat) {
        switch (mat) {
            case AIR:
            case SNOW_LAYER:
                return true;
            default:
                return false;
        }
    }

    public boolean playerTrapdoorUse(InnPlayerInteractEvent idpEvent) {
        IdpPlayer player = idpEvent.getPlayer();
        Block block = idpEvent.getBlock();

        // For some reason, levers only call player interact event with
        // the off-hand and not main hand, so make an exception for
        // them here
        if (idpEvent.getHandSlot() == EquipmentSlot.OFF_HAND) {
            EquipmentSlot nonEmptySlot = player.getNonEmptyHand();

            if (nonEmptySlot != null) {
                IdpItemStack handStack = player.getItemInHand(nonEmptySlot);

                if (handStack.getMaterial() != IdpMaterial.LEVER) {
                    return true;
                }
            } else {
                return true;
            }
        }

        if (idpEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
            InnectisTrapdoor trapdoor = TrapdoorHandler.getTrapdoor(block.getLocation());

            // Only check access for trapdoors that are owned
            if (trapdoor != null) {
                if (!(trapdoor.canPlayerAccess(player.getName()) || player.hasPermission(Permission.owned_object_override))) {
                    player.printError("You may not access this trapdoor.");
                    return true;
                }
            }

            BlockState state = block.getState();
            TrapDoor trapdoorBlock = (TrapDoor) state.getData();
            trapdoorBlock.setOpen(!trapdoorBlock.isOpen());

            state.setData(trapdoorBlock);
            state.update();

            player.getHandle().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
            List<IdpPlayer> players = player.getNearByPlayers(16);

            for (IdpPlayer p : players) {
                p.getHandle().playEffect(player.getLocation(), Effect.DOOR_TOGGLE, 0);
            }
        }

        return true;
    }

    public boolean playerBookcaseUse(InnPlayerInteractEvent event) {
        IdpPlayer player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        EquipmentSlot handSlot = event.getHandSlot();
        IdpItemStack handStack = event.getItem();
        IdpMaterial mat = handStack.getMaterial();

        // Only on rightclick, and not with bookcase in hand or sneaking
        if (action == Action.RIGHT_CLICK_BLOCK
                && !player.getHandle().isSneaking()
                && (!mat.isBlock() || mat == IdpMaterial.AIR)) { // No blocks
            InnectisBookcase bookcase = InnectisBookcase.getBookcase(loc);

            if (bookcase != null) {
                if (bookcase.canPlayerAccess(player.getName()) || player.hasPermission(Permission.owned_object_override)) {
                    // if holding an written book, just store it.
                    if (mat == IdpMaterial.WRITTEN_BOOK) {
                        bookcase.addBook(handStack);

                        if (!bookcase.save()) {
                            player.printError("Unable to save bookcase. Notify an admin!");
                            return true;
                        }

                        // Set it here, so that on an SQLException the book isn't removed
                        player.setItemInHand(handSlot, IdpItemStack.EMPTY_ITEM);
                        return true;
                    } else {
                        IdpInventory caseinv = new IdpInventory(bookcase.getCaseTitle(), bookcase.getItems());
                        caseinv.setPayload(new BookcaseInventoryPayload(bookcase));

                        player.openInventory(caseinv);
                        return true;
                    }
                } else {
                    player.printError("You do not have access to this bookcase!");
                    return true;
                }
            }
        }

        // Give an inventory when left clicked with a chest
        if (action == Action.LEFT_CLICK_BLOCK && (mat == IdpMaterial.CHEST || mat == IdpMaterial.TRAPPED_CHEST)) {
            InnectisLot lot = LotHandler.getLot(loc);

            if (lot == null || lot.canPlayerAccess(player.getName()) || player.hasPermission(Permission.owned_object_override)) {
                InnectisBookcase bookcase = InnectisBookcase.getBookcase(loc);

                if (bookcase == null) {
                    PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());
                    bookcase = new InnectisBookcase(block.getWorld(), block, credentials);

                    if (bookcase.save()) {
                        PlayerCredentialsManager.addCredentialsToCache(credentials);
                        player.printInfo("This bookcase has been given an inventory.");
                    } else {
                        player.printError("Unable to save bookcase. Notify an admin!");
                    }

                    return true;
                } else {
                    player.printError("This bookcase already has an inventory!");
                    return true;
                }
            } else {
                player.printError("Unable to do that here!");
                return true;
            }
        }

        return false;
    }

}
